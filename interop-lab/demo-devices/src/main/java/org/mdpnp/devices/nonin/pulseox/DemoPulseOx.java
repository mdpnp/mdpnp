/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nonin.pulseox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.data.enumeration.Enumeration;
import org.mdpnp.comms.data.enumeration.EnumerationImpl;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdate;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.comms.data.numeric.MutableNumericUpdate;
import org.mdpnp.comms.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdate;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.comms.serial.AbstractDelegatingSerialDevice;

public class DemoPulseOx extends AbstractDelegatingSerialDevice<NoninPulseOx> {

    public enum Perfusion {
        Red,
        Green,
        Yellow
    }
    
    public enum Bool {
        True,
        False
    }
    
    public static final Enumeration PERFUSION = new EnumerationImpl(DemoPulseOx.class, "PERFUSION");
    public static final Enumeration ARTIFACT = new EnumerationImpl(DemoPulseOx.class, "ARTIFACT");
    public static final Enumeration SENSOR_ALARM = new EnumerationImpl(DemoPulseOx.class, "SENSOR_ALARM");
    public static final Enumeration SMART_POINT = new EnumerationImpl(DemoPulseOx.class, "SMART_POINT");
    public static final Enumeration LOW_BATTERY = new EnumerationImpl(DemoPulseOx.class, "LOW_BATTERY");
    public static final Numeric FIRMWARE_REVISION = new NumericImpl(DemoPulseOx.class, "FIRMWARE_REVISION");
    public static final Enumeration OUT_OF_TRACK = new EnumerationImpl(DemoPulseOx.class, "OUT_OF_TRACK");
    
	public DemoPulseOx(Gateway gateway) {
		super(gateway);
		add(firmwareRevisionUpdate);
		add(pulseUpdate, spo2Update, plethUpdate);
		add(perfusionUpdate, artifactUpdate, smartPointUpdate, lowBatteryUpdate, outOfTrackUpdate);
		
		this.plethUpdate.setMillisecondsPerSample(NoninPulseOx.MILLISECONDS_PER_SAMPLE);
		this.plethUpdate.setValues(new Number[Packet.FRAMES]);

		
	
	}
	private class MyNoninPulseOx extends NoninPulseOx {

		public MyNoninPulseOx(InputStream in, OutputStream out) {
			super(in, out);
		}
		@Override
		public void receivePacket(Packet currentPacket) {
			Number[] values = plethUpdate.getValues();
			for(int i = 0; i < Packet.FRAMES; i++) {
				values[i] = currentPacket.getPleth(i);
			}
			
			if(currentPacket.getCurrentStatus().isArtifact()||currentPacket.getCurrentStatus().isSensorAlarm()||currentPacket.getCurrentStatus().isOutOfTrack()) {
				pulseUpdate.set(null, getTimestamp());
				spo2Update.set(null, getTimestamp());
			} else {
				pulseUpdate.set(getHeartRate(), getTimestamp());
				spo2Update.set(getSpO2(), getTimestamp());
			}
			
			gateway.update(DemoPulseOx.this, pulseUpdate, spo2Update, plethUpdate);
			
			Perfusion perfusion = perfusion(currentPacket.getCurrentStatus());
			if(perfusion == null) {
				if(null != perfusionUpdate.getValue()) {
					perfusionUpdate.setValue(null);
					gateway.update(DemoPulseOx.this, perfusionUpdate);
				}
			} else if(perfusionUpdate.getValue() == null) {
				perfusionUpdate.setValue(perfusion);
				gateway.update(DemoPulseOx.this, perfusionUpdate);
			} else if(!perfusion.equals(perfusionUpdate.getValue())) {
				perfusionUpdate.setValue(perfusion);
				gateway.update(DemoPulseOx.this, perfusionUpdate);
			}
			
			if(null == firmwareRevisionUpdate.getValue() || !firmwareRevisionUpdate.getValue().equals(currentPacket.getFirmwareRevision())) {
				firmwareRevisionUpdate.setValue(currentPacket.getFirmwareRevision());
				gateway.update(DemoPulseOx.this, firmwareRevisionUpdate);
			}
			
			Bool artifact = currentPacket.getCurrentStatus().isArtifact() ? Bool.True : Bool.False;
			if(null == artifactUpdate.getValue() || !artifactUpdate.getValue().equals(artifact)) {
				artifactUpdate.setValue(artifact);
				gateway.update(DemoPulseOx.this, artifactUpdate);
			}
			
			Bool smartPoint = currentPacket.isSmartPoint() ? Bool.True : Bool.False;
			if(null == smartPointUpdate.getValue() || !smartPointUpdate.getValue().equals(smartPoint)) {
				smartPointUpdate.setValue(smartPoint);
				gateway.update(DemoPulseOx.this, smartPointUpdate);
			}
			
			Bool lowBattery = currentPacket.isLowBattery() ? Bool.True : Bool.False;
			if(null == lowBatteryUpdate.getValue() || !lowBatteryUpdate.getValue().equals(lowBattery)) {
				lowBatteryUpdate.setValue(lowBattery);
				gateway.update(DemoPulseOx.this, lowBatteryUpdate);
			}
			
			Bool outOfTrack = currentPacket.getCurrentStatus().isOutOfTrack() ? Bool.True : Bool.False;
			if(null == outOfTrackUpdate.getValue() || !outOfTrackUpdate.getValue().equals(outOfTrack)) {
				outOfTrackUpdate.setValue(outOfTrack);
				gateway.update(DemoPulseOx.this, outOfTrackUpdate);
			}
		}
	}
	public static final Perfusion perfusion(Status status) {
		if(status.isGreenPerfusion()) {
			return Perfusion.Green;
		} else if(status.isYellowPerfusion()) {
			return Perfusion.Yellow;
		} else if(status.isRedPerfusion()) {
			return Perfusion.Red;
		} else {
			return null;
		}
	}
	@Override
	protected NoninPulseOx buildDelegate(InputStream in, OutputStream out) {
		return new MyNoninPulseOx(in, out);
	}
	private final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
	private final MutableNumericUpdate spo2Update = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
	private final MutableWaveformUpdate plethUpdate = new MutableWaveformUpdateImpl(PulseOximeter.PLETH);
	private final MutableEnumerationUpdate perfusionUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.PERFUSION);
	private final MutableEnumerationUpdate artifactUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.ARTIFACT);
	private final MutableEnumerationUpdate smartPointUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.SMART_POINT);
	private final MutableEnumerationUpdate lowBatteryUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.LOW_BATTERY);
	private final MutableNumericUpdate firmwareRevisionUpdate = new MutableNumericUpdateImpl(DemoPulseOx.FIRMWARE_REVISION);
	private final MutableEnumerationUpdate outOfTrackUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.OUT_OF_TRACK);

	

	
	public boolean doInitCommands(OutputStream outputStream) throws IOException {
		setOutputStream(outputStream);
		String guid = getDelegate().fetchSerial();
		
		if(null == guid) {
			return false;
		}
		boolean ack;
		
	 	if(ack = getDelegate().setDataFormat(getDelegate().onyxFormat)) {
	 		nameUpdate.setValue("Nonin Onyx II");
	 		if(iconUpdateFromResource(iconUpdate, "9650.png")) {
	 			gateway.update(this, iconUpdate);
	 		}
	 	} else if(ack = getDelegate().setDataFormat(getDelegate().wristOxFormat)) {
	 		nameUpdate.setValue("Nonin WristOx2");
	 		if(iconUpdateFromResource(iconUpdate, "3150.png")) {
	 			gateway.update(this, iconUpdate);
	 		}
		}
	 	guidUpdate.setValue(guid);
	 	gateway.update(this, nameUpdate, guidUpdate);
	 	
		getDelegate().readyFlag = ack;
		return ack;
		
	}


	@Override
	protected boolean delegateReceive(NoninPulseOx delegate) throws IOException {
		return delegate.receive();
	}



	
	@Override
	protected long getMaximumQuietTime() {
		return 1000L;
	}
	
	@Override
	protected String iconResourceName() {
		return "3150.png";
	}
}
