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


/**
 * @author Jeff Plourde
 *
 */
public class TestWaveformSource implements WaveformSource, Runnable {

    
    private static final float[] values = new float[] {
        1,1,1,1,1,1,1,1,1,1,
        1,1,1,1,1,1,1,1,1,1,
        0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,
    };
    
    public TestWaveformSource() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
    
    @Override
    public boolean loadingHistoricalData() {
        return false;
    }

    private int MAX = 800;
    private double MAX_X = 8.0 * Math.PI;
    private long TIME = 5000L;

    private double scale = 25.0;

//    @Override
//    public float getValue(int x) {
//        double d = (1.0 * x / MAX) * MAX_X;
//        return (float) (scale * Math.cos(OFFSETS[offset] + d));
//    }

    private static final double[] OFFSETS = new double[] { 0, Math.PI / 2.0 };

    
    @Override
    public String getIdentifier() {
        return TestWaveformSource.class.getName();
    }

    @Override
    public void iterate(WaveformIterator itr) {
        long now = System.currentTimeMillis();
        itr.begin();
        for(int i = 0; i < values.length; i++) {
//            int x = (offset + i) % values.length;
            itr.sample(now-(values.length-i)*200L, values[i]);
        }
        
        itr.end();
        // TODO Auto-generated method stub
        
    }
//    private int offset = 0;
    @Override
    public void run() {
//        offset = ++offset == values.length ? 0 : offset;
    }
}
