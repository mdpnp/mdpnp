/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.data.waveform;

import java.util.Arrays;
import java.util.Date;

import org.mdpnp.comms.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableWaveformUpdateImpl extends MutableIdentifiableUpdateImpl<Waveform> implements MutableWaveformUpdate {
//	private Number[] value = new Number[0];
//	private Integer count;
	private Date timestamp;
	private Double millisecondsPerSample;
	private Number[] values;
	
	public MutableWaveformUpdateImpl() {
	}
	
	public MutableWaveformUpdateImpl(Waveform waveform) {
	    super(waveform);
	}
	
	@Override
	public Number[] getValues() {
		return values;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date date) {
		this.timestamp = date;
	}
	
	@Override
	public void setValues(Number[] values) {
		this.values = values;
	}

	@Override
	public Double getMillisecondsPerSample() {
		return this.millisecondsPerSample;
	}

	@Override
	public void setMillisecondsPerSample(Double d) {
		this.millisecondsPerSample = d;
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+"timestamp="+getTimestamp()+",msPerSample="+getMillisecondsPerSample()+",values="+Arrays.toString(this.values)+"]";
	}
}
