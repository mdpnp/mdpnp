/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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
        synchronized(this) {
            long now = System.currentTimeMillis();
            if(0L!=priorInvocation) {
                // Number of samples we should play since the last invocation
                int samples = (int) Math.round((now-priorInvocation)/getTarget().getMillisecondsPerSample());
                
                // putative updated reportingCount
                int max = getTarget().getMax();
                int count = getTarget().getCount();
                
                int oldReportingCount = this.reportingCount;
                int reportingCount = oldReportingCount + samples;
                // wrap around
                reportingCount = reportingCount >= max?(reportingCount-max):reportingCount;
                
                if(count >= 0) {
                    boolean crossedTheCount = (oldReportingCount<count&&count<reportingCount) || (count<reportingCount&&reportingCount<oldReportingCount);
                    if(crossedTheCount) {
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
        if(0 == executorServiceReferences) {
            executorService = Executors.newScheduledThreadPool(1);
        }
        executorServiceReferences++;
        return executorService;
    }
    
    private synchronized static void release() {
        executorServiceReferences--;
        if(0 == executorServiceReferences) {
            executorService.shutdown();
            executorService = null;
        }
    }
    
    @Override
    public synchronized void addListener(WaveformSourceListener listener) {
        boolean wasEmpty = getListeners().isEmpty();

        super.addListener(listener);
        if(wasEmpty) {
            reference();
            scheduledFuture = executorService.scheduleAtFixedRate(this, microsecondsPerSample, microsecondsPerSample, TimeUnit.MICROSECONDS);
        }
    }

    @Override
    public synchronized void removeListener(WaveformSourceListener listener) {
        super.removeListener(listener);
        if(getListeners().isEmpty()) {
            if(null != scheduledFuture) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
            release();
        }
    }
}
