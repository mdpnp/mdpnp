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

import java.util.Collection;

/**
 * @author Jeff Plourde
 *
 */
public class SimpleWaveformSource implements WaveformSource, Runnable {

    public SimpleWaveformSource() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    private final Collection<WaveformSourceListener> listeners = new java.util.concurrent.CopyOnWriteArrayList<WaveformSourceListener>();

    @Override
    public float getValue(int x) {
        return x % 2;
    }

    @Override
    public int getMax() {
        return 2;
    }

    @Override
    public int getCount() {
        return -1;
    }

    @Override
    public double getMillisecondsPerSample() {
        return 500.0;
    }

    @Override
    public void addListener(WaveformSourceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(WaveformSourceListener listener) {
        listeners.remove(listener);

    }

    private int count = 0;
    private long startTime = 0L;

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startTime = System.currentTimeMillis();
            count = ++count % 2;
            for (WaveformSourceListener listener : listeners) {
                listener.waveform(this);
            }
        }
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    public static final void main(String[] args) {
        // JFrame frame = new JFrame("TEST");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // WaveformSource source = new SimpleWaveformSource();
        //
        // final WaveformPanel panel = new WaveformPanel(new
        // CachingWaveformSource(source, 4000L));
        // // final WaveformPanel panel = new WaveformPanel(source);
        //
        // frame.getContentPane().add(panel);
        // frame.setSize(640, 480);
        // frame.setVisible(true);

    }

}
