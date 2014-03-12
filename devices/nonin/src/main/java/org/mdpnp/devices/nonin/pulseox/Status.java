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
package org.mdpnp.devices.nonin.pulseox;

/**
 * @author Jeff Plourde
 *
 */
public class Status {
    public boolean isArtifact() {
        return artifact;
    }

    public boolean isOutOfTrack() {
        return outOfTrack;
    }

    public boolean isSensorAlarm() {
        return sensorAlarm;
    }

    public boolean isRedPerfusion() {
        return redPerfusion;
    }

    public boolean isGreenPerfusion() {
        return greenPerfusion;
    }

    public boolean isYellowPerfusion() {
        return yellowPerfusion;
    }

    public boolean isSync() {
        return sync;
    }

    private boolean artifact, outOfTrack, sensorAlarm;
    private boolean redPerfusion, greenPerfusion, yellowPerfusion;
    private boolean sync, highBitSet;

    public boolean isHighBitSet() {
        return highBitSet;
    }

    public Status set(byte b) {
        sync = 0 != (0x01 & b);
        greenPerfusion = 0 != (0x02 & b);
        redPerfusion = 0 != (0x04 & b);
        yellowPerfusion = greenPerfusion && redPerfusion;
        if (yellowPerfusion) {
            redPerfusion = false;
            greenPerfusion = false;
        }
        sensorAlarm = 0 != (0x08 & b);
        outOfTrack = 0 != (0x10 & b);
        artifact = 0 != (0x20 & b);
        highBitSet = b < 0;
        return this;
    }
}
