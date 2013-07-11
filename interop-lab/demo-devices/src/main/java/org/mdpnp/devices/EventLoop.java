package org.mdpnp.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.infrastructure.WaitSetProperty_t;

public class EventLoop  {
    
    private static final Logger log = LoggerFactory.getLogger(EventLoop.class); 
    
    public interface ConditionHandler {
        void conditionChanged(Condition condition);
    }

    private final Map<Condition, ConditionHandler> conditionHandlers = new HashMap<Condition, ConditionHandler>();
    private final WaitSet waitSet;
    private final GuardCondition mutate = new GuardCondition();

    private final ConditionHandler mutateHandler = new ConditionHandler() {

        @Override
        public void conditionChanged(Condition condition) {
            Mutation[] mutations = new Mutation[0];
            synchronized(queuedMutations) {
                mutations = queuedMutations.toArray(mutations);
                queuedMutations.clear();
                ((GuardCondition)condition).set_trigger_value(false);
            }
            for(Mutation m : mutations) {
                
                if(m.isAdd()) {
//                    log.debug("Handling an add mutation for " + m.getCondition());
                    conditionHandlers.put(m.getCondition(), m.getConditionHandler());
                    waitSet.attach_condition(m.getCondition());
                } else {
//                    log.debug("Handling a remove mutation for " + m.getCondition());
                    conditionHandlers.remove(m.getCondition());
                    waitSet.detach_condition(m.getCondition());
                }
                m.done();
            }
        }
        
    };
    
    private static class Mutation {
        private final boolean add;
        private final Condition condition;
        private final ConditionHandler conditionHandler;
        
        private boolean done = false;
        
        public Mutation(boolean add, Condition condition, ConditionHandler conditionHandler) {
            this.add = add;
            this.condition = condition;
            this.conditionHandler = conditionHandler;
        }

        public boolean isAdd() {
            return add;
        }

        public Condition getCondition() {
            return condition;
        }

        public ConditionHandler getConditionHandler() {
            return conditionHandler;
        }
        public synchronized void done() {
            this.done = true;
            this.notifyAll();
        }
        public synchronized void await()  {
            while(!done) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
    
    private final List<Mutation> queuedMutations = new ArrayList<Mutation>();
    
    public EventLoop() {
        waitSet = new WaitSet();
        waitSet.attach_condition(mutate);
        conditionHandlers.put(mutate, mutateHandler);
    }
    
    public EventLoop(WaitSetProperty_t properties) {
        waitSet = new WaitSet(properties);
        conditionHandlers.put(mutate, mutateHandler);
    }
    
    public boolean waitAndHandle(ConditionSeq condSeq, Duration_t dur) {
        condSeq.clear();
        try {
            waitSet.wait(condSeq, dur);
            for(int i = 0; i < condSeq.size(); i++) {
                Condition c = (Condition) condSeq.get(i);
                ConditionHandler ch = conditionHandlers.get(c);
                if(null != ch) {
                    ch.conditionChanged(c);
                } else {
                    log.warn("No ConditionHandler for Condition " + c);
                }
            }
            return true;
        } catch(RETCODE_TIMEOUT timeout) {
            return false;
        }
    }
   
    
    public void addHandler(Condition condition, ConditionHandler conditionHandler) {
        Mutation m = new Mutation(true, condition, conditionHandler);
        synchronized(queuedMutations) {
//            log.debug("Queue add condition:"+condition);
            queuedMutations.add(m);
            mutate.set_trigger_value(true);
        }
        m.await();
//        log.debug("addHandler complete for " + condition);
    }
    
    public void removeHandler(Condition condition) {
        Mutation m = new Mutation(false, condition, null);
        synchronized(queuedMutations) {
//            log.debug("Queue remove condition:"+condition);
            queuedMutations.add(m);
            mutate.set_trigger_value(true);
        }
        m.await();
//        log.debug("removeHandler complete for " + condition);
    }
    
}
