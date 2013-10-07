/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import java.util.Collection;

public class SimpleWaveformSource implements WaveformSource, Runnable {

    public SimpleWaveformSource() {
        Thread t= new Thread(this);
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
        while(true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startTime = System.currentTimeMillis();
            count = ++count%2;
            for(WaveformSourceListener listener : listeners) {
                listener.waveform(this);
            }
        }
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    public static final void main(String[] args) {
//		JFrame frame = new JFrame("TEST");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		WaveformSource source = new SimpleWaveformSource();
//
//		final WaveformPanel panel = new WaveformPanel(new CachingWaveformSource(source, 4000L));
////		final WaveformPanel panel = new WaveformPanel(source);
//
//		frame.getContentPane().add(panel);
//		frame.setSize(640, 480);
//		frame.setVisible(true);

    }

}
