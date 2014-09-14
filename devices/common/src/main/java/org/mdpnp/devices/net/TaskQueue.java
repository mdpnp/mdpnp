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
package org.mdpnp.devices.net;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class TaskQueue {
    private final ThreadLocal<List<Task<?>>> tasksToDo = new ThreadLocal<List<Task<?>>>() {
        protected java.util.List<TaskQueue.Task<?>> initialValue() {
            return new ArrayList<Task<?>>();
        }
    };

    public long doExpiredTasks(long now) {
        List<Task<?>> tasksToDo = this.tasksToDo.get();

        tasksToDo.clear();

        synchronized (this) {
            for (Task<?> t : queue) {
                if (t.getScheduledTime() <= now) {
                    tasksToDo.add(t);
                }
            }
            queue.removeAll(tasksToDo);
            this.notifyAll();
        }
        if (!tasksToDo.isEmpty()) {
            for (Task<?> t : tasksToDo) {
                t.execute(this);
            }
        }
        return timeToNextTask(now);
    }

    public final synchronized long timeToNextTask(long now) {
        if (queue.isEmpty()) {
            return 0L;
        } else {
            return queue.first().getScheduledTime() - now;
        }
    }

    public interface Task<T> extends Comparable<Task<T>> {
        long getScheduledTime();

        void setScheduledTime(long l);

        T execute(TaskQueue queue);

        T waitForResult();

        boolean isRecurrent();

        long getInterval();

        void setInterval(long l);
    }

    public abstract static class TaskImpl<T> implements Task<T> {
        private long scheduledTime;
        private T t;
        private Throwable e;
        private long interval;
        
        private static final Logger log = LoggerFactory.getLogger(TaskImpl.class);

        public abstract T doExecute(TaskQueue queue);

        @Override
        public synchronized T waitForResult() {
            while (null == t && null == e) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null != e) {
                throw new RuntimeException(e);
            } else {
                return this.t;
            }
        }

        @Override
        public final T execute(TaskQueue queue) {
            try {
                T t = doExecute(queue);
                synchronized (this) {
                    if (isRecurrent()) {
                        setScheduledTime(System.currentTimeMillis() + getInterval());
                        queue.add(this);
                    }
                    this.t = t;
                    this.notifyAll();
                }
                return t;
            } catch (Throwable e) {
                synchronized (this) {
                    log.error("Caught Throwable in TaskImpl.execute; may not be reported if no call to waitForResult", e);
                    this.e = e;
                    this.notifyAll();
                }
            }
            return null;
        }

        @Override
        public void setScheduledTime(long l) {
            this.scheduledTime = l;
        }

        @Override
        public long getScheduledTime() {
            return scheduledTime;
        }

        @Override
        public int compareTo(Task<T> o) {
            if (scheduledTime < o.getScheduledTime()) {
                return -1;
            } else if (scheduledTime > o.getScheduledTime()) {
                return 1;
            } else {
                if (this.equals(o)) {
                    return 0;
                } else {
                    // TODO terribly arbitrary
                    return -1;
                }
            }

        }

        public boolean isRecurrent() {
            return interval > 0L;
        }

        @Override
        public long getInterval() {
            return interval;
        }

        @Override
        public void setInterval(long l) {
            this.interval = l;
        }

    }

    private final SortedSet<Task<?>> queue = new TreeSet<Task<?>>();

    public synchronized void add(Task<?> task) {
        queue.add(task);
    }

    public synchronized void clear() {
        queue.clear();
    }
}
