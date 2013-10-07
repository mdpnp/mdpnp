/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import org.mdpnp.devices.math.Distribution;
import org.mdpnp.devices.math.DistributionImpl;

public class EvenTempoWaveformSource extends AbstractNestedWaveformSource implements Runnable {
    private boolean running = true;
    private Thread thread;

    public EvenTempoWaveformSource(WaveformSource target) {
        super(target);
    }

    Distribution timeIntervals = new DistributionImpl();
    Distribution bigTimeIntervals = new DistributionImpl();
    Distribution countBigTime = new DistributionImpl();


    private int lastCount = -1;
    private long lastTime = -1L;

    private int countSinceLastGap = 0;

    private int reportingCount;
    private int realCount;

    private long sleepInterval = 0L;
    @Override
    public void reset(WaveformSource source) {
        timeIntervals.reset();
        bigTimeIntervals.reset();
        countBigTime.reset();

        lastCount = -1;
        lastTime = -1L;
        countSinceLastGap = 0;
        reportingCount = 0;
        realCount = 0;
        sleepInterval = 0L;
        fireReset();
    }

    private void catchup() {
        boolean fire = false;
        synchronized(this) {
            if(reportingCount != realCount) {
                reportingCount = realCount;
                fire = true;
            }
        }
        if(fire) {
            fireWaveform();
        }
    }

    @Override
    public void waveform(WaveformSource source) {
        this.realCount = source.getCount();
        long now = System.currentTimeMillis();

        if(lastTime > 0 && lastCount >= 0) {
            int max = source.getMax();
            int n = lastCount <= realCount ? (realCount-lastCount) : (max-lastCount+realCount);

            long interval = now - lastTime;

            timeIntervals.newPoint(interval);


            // do we have data to start evaluating timeliness?
            // if not catchup
            if(timeIntervals.getRealSamples() > 10) {
                // Is this an extraordinarily large interval?
                if(interval > 3.0 * timeIntervals.getStdDev()) {

                    bigTimeIntervals.newPoint(interval);
                    countBigTime.newPoint(countSinceLastGap);
                    countSinceLastGap = 0;
                    sleepInterval = (long) (bigTimeIntervals.getAverage() / countBigTime.getAverage());
                    catchup();
                    synchronized(this) {
                        this.notify();
                    }
                } else {
                    if(sleepInterval <= 0L) {
                        catchup();
                    }
                    countSinceLastGap+=n;
                    // regular sized interval between points
                }
            } else {
                catchup();
            }

        } else {
            catchup();
        }

        lastTime = now;
        lastCount = realCount<0?0:realCount;
    }



    @Override
    public void run() {
        while(running) {
            boolean fire = false;

            synchronized(this) {
                try {
                    this.wait(sleepInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(reportingCount != realCount) {
                    reportingCount = ++reportingCount==getTarget().getMax()?0:reportingCount;
                    fire = true;
                }
            }

            if(fire) {
                fireWaveform();
            }
        }
    }

    @Override
    public synchronized int getCount() {
        return reportingCount;
    }

    @Override
    public synchronized void addListener(WaveformSourceListener listener) {
        if(getListeners().isEmpty()) {
            running = true;
            thread = new Thread(this, "EvenTempoWaveformSource");
            thread.setDaemon(true);
            thread.start();
        }
        super.addListener(listener);
    }

    @Override
    public void removeListener(WaveformSourceListener listener) {
        Thread joinThread = null;
        synchronized(this) {
            super.removeListener(listener);
            if(getListeners().isEmpty()) {
                running = false;
                notifyAll();
                joinThread = thread;
                thread = null;
            }
        }
        if(null != joinThread) {
            try {
                joinThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
