/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nonin.pulseox;


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
		if(yellowPerfusion) {
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
