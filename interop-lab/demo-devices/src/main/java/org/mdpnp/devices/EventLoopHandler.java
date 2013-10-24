package org.mdpnp.devices;

import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.omg.dds.core.Condition;
import org.omg.dds.core.Duration;
import org.omg.dds.core.GuardCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoopHandler implements Runnable, ConditionHandler {

    private final EventLoop eventLoop;
//    private final GuardCondition exitCondition;
    private boolean keepGoing = true;
    private final Thread thread;

    private static final Logger log = LoggerFactory.getLogger(EventLoopHandler.class);

    public EventLoopHandler(EventLoop eventLoop) {
        this(eventLoop, Thread.currentThread().getThreadGroup());
    }

    public EventLoopHandler(EventLoop eventLoop, ThreadGroup group) {
        this.eventLoop = eventLoop;
//        exitCondition = GuardCondition.newGuardCondition(eventLoop.getServiceEnvironment());
        thread = new Thread(this, "EventLoopHandler");
        thread.setDaemon(true);
        thread.start();
        // Add this only after service thread is in place
//        eventLoop.addHandler(exitCondition, this);
    }

    @Override
    public void run() {
//        Duration dur = Duration.infiniteDuration(eventLoop.getServiceEnvironment());
        Duration dur = Duration.newDuration(1, TimeUnit.SECONDS, eventLoop.getServiceEnvironment());

        try {
            log.debug("EventLoopHandler begins");
            while(keepGoing) {
                try {
                    eventLoop.waitAndHandle(dur);
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
//        exitCondition.setTriggerValue(true);
        thread.join();
        log.debug("shutdown complete");
    }

    @Override
    public void conditionChanged(Condition condition) {

    }
}
