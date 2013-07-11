package org.mdpnp.devices;

import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;

public class EventLoopHandler implements Runnable, ConditionHandler {
    
    private final EventLoop eventLoop;
    private final GuardCondition exitCondition = new GuardCondition();
    private boolean keepGoing = true;
    private final Thread thread;
    
    private static final Logger log = LoggerFactory.getLogger(EventLoopHandler.class);
    
    public EventLoopHandler(EventLoop eventLoop) {
        this(eventLoop, Thread.currentThread().getThreadGroup());
    }
    
    public EventLoopHandler(EventLoop eventLoop, ThreadGroup group) {
        this.eventLoop = eventLoop;
        
        thread = new Thread(this, "EventLoopHandler");
        thread.setDaemon(true);
        thread.start();
        // Add this only after service thread is in place
        eventLoop.addHandler(exitCondition, this);
    }
    
    @Override
    public void run() {
        ConditionSeq condSeq = new ConditionSeq();
        Duration_t dur = new Duration_t(Duration_t.DURATION_INFINITY_SEC, Duration_t.DURATION_INFINITY_NSEC);
        
        try {
            log.debug("EventLoopHandler begins");
            while(keepGoing) {
                try {
                    eventLoop.waitAndHandle(condSeq, dur);
                } catch (Throwable t) {
                    log.error("Unexpected in ConditionHandler", t);
                }
            }
        } finally {
            if(keepGoing) {
                log.error("EventLoopHandler ends prematurely");
            } else {
                log.debug("EventLoopHandler ends");
            }
        }
    }
    
    public void shutdown() throws InterruptedException {
        log.debug("shutdown invoked");
        keepGoing = false;
        exitCondition.set_trigger_value(true);
        thread.join();
        log.debug("shutdown complete");
    }

    @Override
    public void conditionChanged(Condition condition) {
        
    }
}
