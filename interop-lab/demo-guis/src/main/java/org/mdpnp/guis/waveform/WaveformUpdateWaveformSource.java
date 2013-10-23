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
        return lastUpdate.values.getFloat(x);
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
        return lastUpdate.values.size();
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
