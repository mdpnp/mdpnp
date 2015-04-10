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
public class WaveformRenderer implements WaveformSource.WaveformIterator {

    public WaveformRenderer() {
    }

    private WaveformCanvas.Extent extent;
    
    private double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
    private double last_x = -1, last_y = -1;
    private long t0, t1, t2;
    private WaveformCanvas canvas;
    private boolean continuousRescale = false;
    private boolean overwrite = true;
    private double gapSize = 0.02;
    boolean aged_segment = true;
    boolean rendering = false;

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    public void setGapSize(double gapSize) {
        this.gapSize = gapSize;
    }
    
    public double getGapSize() {
        return gapSize;
    }
    
    public boolean getOverwrite() {
        return overwrite;
    }
    
    public void setContinuousRescale(boolean continuousRescale) {
        this.continuousRescale = continuousRescale;
    }
    
    

    public void rescaleValue() {
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;
    }
    
    
    
    @Override
    public void sample(long time, float value) {
        
        if(time>=t1&&time<t2) {
            minY = Math.min(value, minY);
            maxY = Math.max(value, maxY);

            if(0==Double.compare(minY, maxY)) {
                maxY = minY + 0.01;
            }
        }
        double x_prop = -1.0;
        if(overwrite) {
            double split_prop = 1.0 * (t2 - t0) / (t2 - t1);
            if(time >= t0 && time < t2) {
                // the newer data (left)
                if(aged_segment) {
                    last_x = -1;
                    last_y = -1;
                    aged_segment = false;
                }
                x_prop = 1.0 * (time - t0) / (t2-t0);
                x_prop *= split_prop;
            } else if(time >= t1 && time < t0) {
                // the older data (right)
                x_prop = 1.0 * (time - t1) / (t0-t1);
                x_prop *= (1.0-split_prop);
                if(x_prop < gapSize) {
                    x_prop = -1.0;
                } else {
                    x_prop += split_prop;
                }
            } else {
                x_prop = -1.0;
            }
        } else {
            x_prop = 1.0 * (time - t1) / (t2-t1);
        }
        
        double y_prop = 1.0 * (value - minY) / (maxY-minY);
        
        double x = extent.getMinX() + x_prop * (extent.getMaxX()-extent.getMinX());
        double y = extent.getMinY() + y_prop * (extent.getMaxY()-extent.getMinY());
        
        if(x_prop>=0.0&&x_prop<1.0&&y_prop>=0.0&&y_prop<1.0) {
            if(last_x>=0.0||last_y>=0.0&&x>last_x) {
                count++;
                canvas.drawLine(last_x, last_y, x, y);
//                System.err.println(last_x+","+last_y+" "+x+","+y);
            }
            last_x = x;
            last_y = y;
//            System.err.println("in " + x_prop + ", " + y_prop + " " + new Date(time));
        } else {
//            System.err.println("out of " + x_prop + ", " + y_prop);
        }
        
    }
    
    public void render(WaveformSource source, WaveformCanvas canvas, long t1, long t2) {
        synchronized(this) {
            this.rendering = true;
        }
        try {
            this.canvas = canvas;
    
            if (null == canvas || null == source) {
                return;
            }
            
            if(source.loadingHistoricalData()) {
                canvas.drawString("Receiving Recent Data...", 0, 0);
                return;
            }
    
            extent = canvas.getExtent();
            
            canvas.clearRect(extent.getMinX(), extent.getMinY(), extent.getMaxX(), extent.getMaxY());
    
            
            if (continuousRescale) {
                minY = Double.MAX_VALUE;
                maxY = Double.MIN_VALUE;
            }
            
            if(overwrite) {
                long domain = t2 - t1;
                // what is the nearest start increment for a domain of this size?
                this.t0 = t2 - t2 % domain;
            }
            
            this.t1 = t1;
            this.t2 = t2;
    
            this.last_x = -1;
            this.last_y = -1;
            
            source.iterate(this);
        } finally {
            synchronized(this) {
                rendering = false;
                this.notifyAll();
            }
        }
    }
    
    public synchronized void awaitLastRender(long timeout) throws InterruptedException {
        long nowNs = System.nanoTime();
        long giveUpNs = nowNs + timeout * 1000000L;
        while(rendering) {
            long remainingNs = giveUpNs - nowNs;
            this.wait(remainingNs/1000000L, (int)(remainingNs%1000000L));
            nowNs = System.nanoTime();
        }
    }
    
    private int count = 0;
    @Override
    public void begin() {
        count = 0;
        aged_segment = true;
        
    }

    @Override
    public void end() {
//        System.err.println(count + " points");
//        System.err.println(count + " points rendered most recent " + new Date(mostRecent));
    }
}
