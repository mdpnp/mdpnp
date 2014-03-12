/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CachingWaveformSource extends AbstractNestedWaveformSource {



    private float[] sampleCache = new float[100];

    private int nextCacheCount = 0;
    private int lastSourceCount = -1;
    private long startTime = 0L;

    private Long fixedTimeDomain;

    @Override
    public void reset(WaveformSource source) {
        for(int i = 0; i < sampleCache.length; i++) {
            sampleCache[i] = 0;
        }
        nextCacheCount = 0;
        lastSourceCount = -1;
        fireReset();
    }

    public void setCacheSize(int samples) {
        this.fixedTimeDomain = null;
        sampleCache = new float[samples];
    }

    public void setFixedTimeDomain(long fixedTimeDomain) {
        this.fixedTimeDomain = fixedTimeDomain;
    }

    public Long getFixedTimeDomain() {
        return fixedTimeDomain;
    }

    public CachingWaveformSource(WaveformSource source) {
        this(source, null);
    }
    public CachingWaveformSource(WaveformSource source, Long fixedTimeDomain) {
        super(source);
        this.fixedTimeDomain = fixedTimeDomain;
    }


    @Override
    public float getValue(int x) {
        float[] sampleCache = this.sampleCache;
        // I don't want to waste a lot of cycles on synchronization
        if(x<sampleCache.length) {
            return sampleCache[x];
        } else {
            return 0;
        }
    }

    @Override
    public int getMax() {
        return sampleCache.length;
    }

    @Override
    public int getCount() {
        return nextCacheCount;
    }



    private int postIncrCacheCount(long startTime) {
        int nextCacheCount = this.nextCacheCount;

        this.nextCacheCount = incr(nextCacheCount, sampleCache.length);

        if(0 == nextCacheCount) {
            this.startTime = startTime;
        }

        return nextCacheCount;
    }

//    private int postDecrCacheCount() {
//        int nextCacheCount = this.nextCacheCount;
//
//        this.nextCacheCount = decr(nextCacheCount, sampleCache.length);
//        return nextCacheCount;
//    }

    private int postIncrLastSourceCount(int sourceMax) {
        int lastSourceCount = this.lastSourceCount;
        this.lastSourceCount = incr(lastSourceCount, sourceMax);
        return lastSourceCount;
    }

    private static final int incr(int x, int max) {
        return ++x>=max?0:x;
    }
    private static final int decr(int x, int max) {
        return --x<0?(max-1):x;
    }
    private final Logger log = LoggerFactory.getLogger(CachingWaveformSource.class);
    @Override
    public void waveform(WaveformSource source) {
        int sourceCount = source.getCount();
        int sourceMax = source.getMax();

        if(sourceMax == 0) {
            return;
        }

        if(null != fixedTimeDomain) {
            double resolution = source.getMillisecondsPerSample();
            int samples = (int) (fixedTimeDomain / resolution);
            if(samples != sampleCache.length) {
                float[] oldSampleCache = this.sampleCache;
                this.sampleCache = new float[samples];
//				Log.d(CachingWaveformSource.class.getName(), "NEW sampleCache");
                int n = Math.min(oldSampleCache.length, sampleCache.length);
                System.arraycopy(oldSampleCache, oldSampleCache.length - n, sampleCache, sampleCache.length - n, n);

                this.nextCacheCount = 0;
//				// smaller
//				if(sampleCache.length < oldSampleCache.length) {
//					this.nextCacheCount -= (oldSampleCache.length - sampleCache.length);
//				}
//
//				// Forces any rendering logic to review all the new data
//				this.nextCacheCount--;
//				if(this.nextCacheCount >= this.sampleCache.length || this.nextCacheCount < 0) {
//					this.nextCacheCount = 0;
//				}
                log.info("Adjusted sample array to " + this.sampleCache.length + " to accomodate " + fixedTimeDomain + "ms domain at resolution " + resolution);
            }
        }
        long msPerSample = (long) source.getMillisecondsPerSample();
        // Indicating there is no cursor, just a bunch of new data
        if(sourceCount < 0) {
            long startTime = source.getStartTime();
            for(int i = 0; i < sourceMax; i++) {
                sampleCache[postIncrCacheCount(startTime + i * msPerSample)] = source.getValue(i);
            }
            fireWaveform();

        } else {
            if(lastSourceCount < 0) {
                // base case
                int i = decr(sourceCount, sourceMax);
                sampleCache[postIncrCacheCount(startTime + i * msPerSample)] = source.getValue(i);
                this.lastSourceCount = sourceCount;
                fireWaveform();
            } else {
                while(lastSourceCount != sourceCount) {
                    int i = postIncrLastSourceCount(sourceMax);
                    sampleCache[postIncrCacheCount(startTime + i * msPerSample)] = source.getValue(i);
                }
                fireWaveform();
            }
        }
    }

    @Override
    public double getMillisecondsPerSample() {
        return getTarget().getMillisecondsPerSample();
    }



}
