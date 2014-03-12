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

import java.util.Set;

public class TestWaveformSource implements WaveformSource, Runnable {

    public TestWaveformSource() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    private int offset = 0;
    private int MAX = 800;
    private double MAX_X = 8.0 * Math.PI;
    private long TIME = 5000L;
    private int count = 0;
    private long startTime;

    private double scale = 25.0;

    @Override
    public float getValue(int x) {
        double d = (1.0 * x / MAX) * MAX_X;
        return (float) (scale * Math.cos(OFFSETS[offset] + d));
    }

    @Override
    public int getMax() {
        return MAX;
    }

    @Override
    public int getCount() {
        return count;
    }

    private static final double[] OFFSETS = new double[] {0, Math.PI / 2.0};

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void run() {
        long INTERVAL = TIME / MAX;
        while(true) {
            for(int i = 0; i < MAX; i++) {
                startTime = System.currentTimeMillis();
                for(WaveformSourceListener listener : listeners) {
                    listener.waveform(this);
                }
                try {

                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            count = 0;
            offset=++offset==OFFSETS.length?0:offset;
            scale = 1.0 + Math.random() * 100.0;
            System.out.println("New offset:" + OFFSETS[offset] + " New scale:"+ scale);
        }
    }

    private final Set<WaveformSourceListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<WaveformSourceListener>();

    @Override
    public void addListener(WaveformSourceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(WaveformSourceListener listener) {
        listeners.remove(listener);
    }
    @Override
    public double getMillisecondsPerSample() {
        return 1.0 * TIME / MAX;
    }
}
