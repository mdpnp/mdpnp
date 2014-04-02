/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;

/**
 * @author Jeff Plourde
 *
 */
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
            while (keepGoing) {
                try {
                    eventLoop.waitAndHandle(condSeq, dur);
                } catch (Throwable t) {
                    log.error("Unexpected in ConditionHandler", t);
                }
            }
        } finally {
            if (keepGoing) {
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
