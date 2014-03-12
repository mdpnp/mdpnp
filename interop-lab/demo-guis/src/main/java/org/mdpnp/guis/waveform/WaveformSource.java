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
 * A generic interface for a component that has access to waveform data.
 * The data are expected to form a circular buffer from domain x=0 to x=(getMax()-1)
 * getCount() is indicative of the most recently updated point
 * For two invocations of getCount(), x0 and x1 it is implied that all domain
 * values between x0 and x1 have been updated between the invocations
 *
 *
 */
public interface WaveformSource {
    /**
     * The waveform at position x
     * @param x
     * @return
     */
    float getValue(int x);

    /**
     * The maximum extent of the waveform domain
     * @return
     */
    int getMax();

    /**
     * The most recently updated point
     * @return
     */
    int getCount();


    /**
     * Resolution of the sample array
     * @return
     */
    double getMillisecondsPerSample();

    long getStartTime();

    void addListener(WaveformSourceListener listener);
    void removeListener(WaveformSourceListener listener);
}
