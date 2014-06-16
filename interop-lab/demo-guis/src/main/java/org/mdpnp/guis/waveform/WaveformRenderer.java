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
    
    private float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
    private int last_x = -1, last_y = -1;
    private long t0, t1, t2;
    private WaveformCanvas canvas;
    private boolean continuousRescale = false;
    private boolean overwrite = true;
    private float gapSize = 0.02f;
    boolean aged_segment = true;

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    public void setGapSize(float gapSize) {
        this.gapSize = gapSize;
    }
    
    public float getGapSize() {
        return gapSize;
    }
    
    public boolean getOverwrite() {
        return overwrite;
    }
    
    public void setContinuousRescale(boolean continuousRescale) {
        this.continuousRescale = continuousRescale;
    }
    
    

    public void rescaleValue() {
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
    }
    
    
    
    @Override
    public void sample(long time, float value) {
        if(time>=t1&&time<t2) {
            minY = Math.min(value, minY);
            maxY = Math.max(value, maxY);

            if(0==Float.compare(minY, maxY)) {
                maxY = minY + 0.01f;
            }
        }
        float x_prop = -1f;
        if(overwrite) {
            float split_prop = 1f * (t2 - t0) / (t2 - t1);
            if(time >= t0 && time < t2) {
                // the newer data (left)
                if(aged_segment) {
                    last_x = -1;
                    last_y = -1;
                    aged_segment = false;
                }
                x_prop = 1f * (time - t0) / (t2-t0);
                x_prop *= split_prop;
            } else if(time >= t1 && time < t0) {
                // the older data (right)
                x_prop = 1f * (time - t1) / (t0-t1);
                x_prop *= (1f-split_prop);
                if(x_prop < gapSize) {
                    x_prop = -1f;
                } else {
                    x_prop += split_prop;
                }
            } else {
                x_prop = -1f;
            }
        } else {
            x_prop = 1f * (time - t1) / (t2-t1);
        }
        
        float y_prop = 1f * (value - minY) / (maxY-minY);
        
        int x = extent.getMinX() + (int) (x_prop * (extent.getMaxX()-extent.getMinX()));
        int y = extent.getMinY() + (int) (y_prop * (extent.getMaxY()-extent.getMinY())) + 1;
        
        if(x_prop>=0f&&x_prop<1f&&y_prop>=0f&&y_prop<1f) {
            if(last_x>=0||last_y>=0&&x>last_x) {
                canvas.drawLine(last_x, last_y, x, y);
            }
            last_x = x;
            last_y = y;
//            System.err.println("in " + x_prop + ", " + y_prop + " " + new Date(time));
        } else {
//            System.err.println("out of " + x_prop + ", " + y_prop);
        }

    }
    
    public void render(WaveformSource source, WaveformCanvas canvas, long t1, long t2) {
        this.canvas = canvas;

        if (null == canvas || null == source) {
            return;
        }
        
        if(source.loadingHistoricalData()) {
            canvas.drawString("Receiving Recent Data...", 0, 0);
            return;
        }

        extent = canvas.getExtent();
        
        if (continuousRescale) {
            minY = Float.MAX_VALUE;
            maxY = Float.MIN_VALUE;
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
    }

    @Override
    public void begin() {
        aged_segment = true;
    }

    @Override
    public void end() {
//        System.err.println(count + " points rendered most recent " + new Date(mostRecent));
    }
}
