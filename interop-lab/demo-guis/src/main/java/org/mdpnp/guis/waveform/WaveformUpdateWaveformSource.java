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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;

public class WaveformUpdateWaveformSource extends AbstractWaveformSource {
    private final ice.SampleArray lastUpdate = new ice.SampleArray();
    private final SampleInfo lastSampleInfo = new SampleInfo();

    private static final Logger log = LoggerFactory.getLogger(WaveformUpdateWaveformSource.class);

    public void applyUpdate(ice.SampleArray update, SampleInfo sampleInfo) {

        if(0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & sampleInfo.instance_state)) {
            if(sampleInfo.valid_data) {
                this.lastUpdate.copy_from(update);
                this.lastSampleInfo.copy_from(sampleInfo);
                fireWaveform();
            }
        } else {
            reset();
        }
    }


    @Override
    public long getStartTime() {
        return 1000L * lastSampleInfo.source_timestamp.sec + lastSampleInfo.source_timestamp.nanosec / 1000000L;
    }

    public void reset() {
        fireReset();
    }

    @Override
    public float getValue(int x) {
        return lastUpdate.values.userData.getFloat(x);
//		if(null == lastUpdate) {
//			return 0;
//		} else {
//			float[] values = lastUpdate.values.toArrayFloat(arg0)
//			if(null == values) {
//				return 0;
//			} else {
//				Number value = values[x];
//				if(null == value) {
//					return 0;
//				} else {
//					return value.intValue();
//				}
//			}
//		}
    }

    @Override
    public int getMax() {
        return lastUpdate.values.userData.size();
//		return null == lastUpdate.getValues() ? 0 : lastUpdate.getValues().length;
    }

    @Override
    public int getCount() {
        return -1;
    }

    @Override
    public double getMillisecondsPerSample() {
        return lastUpdate.millisecondsPerSample;
//		return lastUpdate.getMillisecondsPerSample();
    }
}
