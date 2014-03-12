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
package org.mdpnp.guis.waveform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvenTempoWaveformSource extends AbstractNestedWaveformSource implements Runnable {
    public EvenTempoWaveformSource(WaveformSource target) {
        super(target);
    }

    private int reportingCount;

    private long microsecondsPerSample = 50000L;

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public synchronized void reset(WaveformSource source) {
        reportingCount = 0;
        fireReset();
    }

    @Override
    public void waveform(WaveformSource source) {
    }

    private long priorInvocation;
    private static final Logger log = LoggerFactory.getLogger(EvenTempoWaveformSource.class);

    @Override
    public void run() {
        synchronized (this) {
            long now = System.currentTimeMillis();
            if (0L != priorInvocation) {
                // Number of samples we should play since the last invocation
                int samples = (int) Math.round((now - priorInvocation) / getTarget().getMillisecondsPerSample());

                // putative updated reportingCount
                int max = getTarget().getMax();
                int count = getTarget().getCount();

                int oldReportingCount = this.reportingCount;
                int reportingCount = oldReportingCount + samples;
                // wrap around
                reportingCount = reportingCount >= max ? (reportingCount - max) : reportingCount;

                if (count >= 0) {
                    boolean crossedTheCount = (oldReportingCount < count && count < reportingCount)
                            || (count < reportingCount && reportingCount < oldReportingCount);
                    if (crossedTheCount) {
                        log.info("Reported count got ahead of real count");
                        reportingCount = count;
                    }
                }
                this.reportingCount = reportingCount;

            }
            priorInvocation = now;
        }

        fireWaveform();
    }

    @Override
    public synchronized int getCount() {
        return reportingCount;
    }

    private static ScheduledExecutorService executorService;
    private static int executorServiceReferences = 0;

    private synchronized static ScheduledExecutorService reference() {
        if (0 == executorServiceReferences) {
            executorService = Executors.newScheduledThreadPool(1);
        }
        executorServiceReferences++;
        return executorService;
    }

    private synchronized static void release() {
        executorServiceReferences--;
        if (0 == executorServiceReferences) {
            executorService.shutdown();
            executorService = null;
        }
    }

    @Override
    public synchronized void addListener(WaveformSourceListener listener) {
        boolean wasEmpty = getListeners().isEmpty();

        super.addListener(listener);
        if (wasEmpty) {
            reference();
            scheduledFuture = executorService.scheduleAtFixedRate(this, microsecondsPerSample, microsecondsPerSample, TimeUnit.MICROSECONDS);
        }
    }

    @Override
    public synchronized void removeListener(WaveformSourceListener listener) {
        super.removeListener(listener);
        if (getListeners().isEmpty()) {
            if (null != scheduledFuture) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
            release();
        }
    }
}
