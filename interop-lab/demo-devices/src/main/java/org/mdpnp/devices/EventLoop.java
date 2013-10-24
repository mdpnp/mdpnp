package org.mdpnp.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.omg.CORBA.ServiceInformation;
import org.omg.dds.core.Condition;
import org.omg.dds.core.Duration;
import org.omg.dds.core.GuardCondition;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.WaitSet;
import org.omg.dds.domain.DomainParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop  {

    private static final Logger log = LoggerFactory.getLogger(EventLoop.class);

    public interface ConditionHandler {
        void conditionChanged(Condition condition);
    }

    private final Map<Condition, ConditionHandler> conditionHandlers = new HashMap<Condition, ConditionHandler>();
    private final List<Mutation> queuedMutations = new ArrayList<Mutation>();
    private final List<Runnable> queuedRunnables = new ArrayList<Runnable>();
    private final WaitSet waitSet;
//    private final GuardCondition mutate, runnable;

    protected void handleMutation(Mutation m) {
        if(m.isAdd()) {
//          log.debug("Handling an add mutation for " + m.getCondition());
          conditionHandlers.put(m.getCondition(), m.getConditionHandler());
          waitSet.attachCondition(m.getCondition());
      } else {
//          log.debug("Handling a remove mutation for " + m.getCondition());
          if(null == conditionHandlers.remove(m.getCondition())) {
              log.warn("Attempt to detach unknown condition:"+m.getCondition());
              for(int i = 0; i < m.getTrace().length; i++) {
                  log.warn("\tat "+m.getTrace()[i]);
              }
          } else {
              waitSet.detachCondition(m.getCondition());
          }
      }
      m.done();
    }

    private final ConditionHandler mutateHandler = new ConditionHandler() {
        @Override
        public void conditionChanged(Condition condition) {
            Mutation[] mutations = new Mutation[0];
            synchronized(queuedMutations) {
                mutations = queuedMutations.toArray(mutations);
                queuedMutations.clear();
                if(null != condition) {
                    ((GuardCondition)condition).setTriggerValue(false);
                }
            }
            for(Mutation m : mutations) {
                handleMutation(m);
            }
        }
    };


    private Thread currentServiceThread;

    private final ConditionHandler runnableHandler = new ConditionHandler() {
        public void conditionChanged(Condition condition) {
            Runnable[] runnables = new Runnable[0];
            synchronized(queuedRunnables) {
                runnables = queuedRunnables.toArray(runnables);
                queuedRunnables.clear();
                if(null != condition) {
                    ((GuardCondition)condition).setTriggerValue(false);
                }
            }
            for(Runnable r : runnables) {
                r.run();
            }
        }
    };

    private static class Mutation {
        private final boolean add;
        private final Condition condition;
        private final ConditionHandler conditionHandler;
        private final StackTraceElement[] trace;

        private boolean done = false;

        public Mutation(boolean add, Condition condition, ConditionHandler conditionHandler) {
            this.add = add;
            this.condition = condition;
            this.conditionHandler = conditionHandler;
            this.trace = Thread.currentThread().getStackTrace();
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

        public StackTraceElement[] getTrace() {
            return trace;
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

    private final ServiceEnvironment env;

    public final ServiceEnvironment getServiceEnvironment() {
        return env;
    }

    public EventLoop(ServiceEnvironment env) {
        this.env = env;
        waitSet = WaitSet.newWaitSet(env);
//        mutate = GuardCondition.newGuardCondition(env);
//        runnable = GuardCondition.newGuardCondition(env);
//        waitSet.attachCondition(mutate);
//        waitSet.attachCondition(runnable);
//        conditionHandlers.put(mutate, mutateHandler);
//        conditionHandlers.put(runnable, runnableHandler);
    }

    public boolean waitAndHandle(Duration dur) throws TimeoutException {
        // Only one thread at a time can currently service the event loop
        long giveup = dur.isInfinite() ? Long.MAX_VALUE : (System.currentTimeMillis() + dur.getDuration(TimeUnit.MILLISECONDS));

        long now = System.currentTimeMillis();
        synchronized(this) {
            while(currentServiceThread != null && now < giveup) {
                if(dur.isZero()) {
                    log.debug("Timed out waiting to become service thread");
                    return false;
                }
                try {
                    this.wait(giveup-now);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
                now = System.currentTimeMillis();
            }
            currentServiceThread = Thread.currentThread();
        }

        if(!dur.isZero() && now >= giveup) {
            log.debug("Timed out waiting to become service thread");
            return false;
        }

        try {
            Collection<Condition> activeConditions = waitSet.waitForConditions(dur);
            Iterator<Condition> itr = activeConditions.iterator();
            while(itr.hasNext()) {
                Condition c = itr.next();
                ConditionHandler ch = conditionHandlers.get(c);
                if(null != ch) {
                    ch.conditionChanged(c);
                } else {
                    log.warn("No ConditionHandler for Condition " + c);
                }
            }
            mutateHandler.conditionChanged(null);
            runnableHandler.conditionChanged(null);
            return true;
        } catch(TimeoutException timeout) {
            return false;
        } finally {
            synchronized(this) {
                currentServiceThread = null;
                this.notifyAll();
            }
        }
    }

    public synchronized boolean isCurrentServiceThread() {
        return Thread.currentThread().equals(currentServiceThread);
    }


    public void addHandler(Condition condition, ConditionHandler conditionHandler) {
        Mutation m = new Mutation(true, condition, conditionHandler);
        if(isCurrentServiceThread()) {
            handleMutation(m);
        } else {
            synchronized(queuedMutations) {
    //            log.debug("Queue add condition:"+condition);
                queuedMutations.add(m);
//                mutate.setTriggerValue(true);
            }
            m.await();
        }
//        log.debug("addHandler complete for " + condition);
    }

    public void removeHandler(Condition condition) {

        Mutation m = new Mutation(false, condition, null);
        if(isCurrentServiceThread()) {
            handleMutation(m);
        } else {
            synchronized(queuedMutations) {
    //            log.debug("Queue remove condition:"+condition);
                queuedMutations.add(m);
//                mutate.setTriggerValue(true);
            }
            m.await();
        }
//        log.debug("removeHandler complete for " + condition);
    }

    public void doLater(Runnable r) {
        synchronized(queuedRunnables) {
            queuedRunnables.add(r);
//            runnable.setTriggerValue(true);
        }
    }

}
