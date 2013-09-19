/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nonin.pulseox;

import ice.SampleArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoPulseOx extends AbstractDelegatingSerialDevice<NoninPulseOx> {

    private static final Logger log = LoggerFactory.getLogger(DemoPulseOx.class);
    
    public enum Perfusion {
        Red,
        Green,
        Yellow
    }
    
    public enum Bool {
        True,
        False
    }
    
//    public static final Enumeration PERFUSION = new EnumerationImpl(DemoPulseOx.class, "PERFUSION");
    
    
    
    
//    public static final Enumeration ARTIFACT = new EnumerationImpl(DemoPulseOx.class, "ARTIFACT");
//    public static final Enumeration SENSOR_ALARM = new EnumerationImpl(DemoPulseOx.class, "SENSOR_ALARM");
//    public static final Enumeration SMART_POINT = new EnumerationImpl(DemoPulseOx.class, "SMART_POINT");
//    public static final Enumeration OUT_OF_TRACK = new EnumerationImpl(DemoPulseOx.class, "OUT_OF_TRACK");
    
//    public static final Enumeration LOW_BATTERY = new EnumerationImpl(DemoPulseOx.class, "LOW_BATTERY");
//    public static final Numeric FIRMWARE_REVISION = new NumericImpl(DemoPulseOx.class, "FIRMWARE_REVISION");
//    
    
	public DemoPulseOx(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);
		
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Nonin";
	    
	    deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
	    
	    deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
	    deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
	    
	    deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
	    deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
	    
//	    add(firmwareRevisionUpdate);
//		add(pulseUpdate, spo2Update, plethUpdate);
//		add(perfusionUpdate, artifactUpdate, smartPointUpdate, lowBatteryUpdate, outOfTrackUpdate);
	}
	
	protected enum Format {
	    Onyx, WristOx
	};
	protected Format formatRequested;
	
	private class MyNoninPulseOx extends NoninPulseOx {

		public MyNoninPulseOx(InputStream in, OutputStream out) {
			super(in, out);
		}
		
		@Override
		protected synchronized void receiveSerialNumber(String serial) {
		    super.receiveSerialNumber(serial);
		    
		    formatRequested = Format.Onyx;
		    try {
                getDelegate().sendSetOnyxFormat();
            } catch (IOException e) {
                log.error("Error sending set onyx format", e);
            }
		}
		
		@Override
		protected synchronized void recvAcknowledged(boolean success) {
		    super.recvAcknowledged(success);
		    if(Format.Onyx.equals(formatRequested)) {
		        formatRequested = null;
		        if(success) {
		            deviceIdentity.model = "Onyx II";
		            
                    try {
                        iconFromResource(deviceIdentity, "9650.png");
                    } catch (IOException e) {
                        log.error("Error loading icon resource", e);
                        deviceIdentity.icon.raster.clear();
                        deviceIdentity.icon.height = 0;
                        deviceIdentity.icon.width = 0;
                    }
                    deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
                    getDelegate().readyFlag = true;
		        } else {
		            formatRequested = Format.WristOx;
		            try {
                        sendSetWristOxFormat();
                    } catch (IOException e) {
                        log.error("Error sending set wristox format", e);
                    }
		        }
		    } else if(Format.WristOx.equals(formatRequested)) {
		        formatRequested = null;
		        if(success) {
		            deviceIdentity.model = "WristOx2";
                    try {
                        iconFromResource(deviceIdentity, "3150.png");
                    } catch (IOException e) {
                        log.error("Error loading icon resource", e);
                        deviceIdentity.icon.raster.clear();
                        deviceIdentity.icon.height = 0;
                        deviceIdentity.icon.width = 0;
                    }
                    deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
                    getDelegate().readyFlag = true;
		        } else {
		            log.warn("Onyx and WristOx formats rejected; nothing left to try");
		        }
		    }
		}
		
		private int[] plethBuffer = new int[Packet.FRAMES];
		
		@Override
		public void receivePacket(Packet currentPacket) {
		    reportConnected();

			for(int i = 0; i < Packet.FRAMES; i++) {
			    plethBuffer[i] = currentPacket.getPleth(i);
			}
			pleth = sampleArraySample(pleth, plethBuffer, plethBuffer.length, (int)NoninPulseOx.MILLISECONDS_PER_SAMPLE, ice.Physio._MDC_PULS_OXIM_PLETH);

			
			if(currentPacket.getCurrentStatus().isArtifact()||currentPacket.getCurrentStatus().isSensorAlarm()||currentPacket.getCurrentStatus().isOutOfTrack()) {
			    pulse = numericSample(pulse, (Integer)null, ice.Physio._MDC_PULS_OXIM_PULS_RATE);
			    SpO2 = numericSample(SpO2, (Integer)null, ice.Physio._MDC_PULS_OXIM_SAT_O2);
			} else {
			    Integer heartRate = getHeartRate();
			    Integer spo2 = getSpO2();
			    pulse = numericSample(pulse, heartRate != null ? (heartRate < 895 ? heartRate : null) : null, ice.Physio._MDC_PULS_OXIM_PULS_RATE);
			    SpO2 = numericSample(SpO2, spo2 != null ? (spo2 <= 100 ? spo2 : null) : null, ice.Physio._MDC_PULS_OXIM_SAT_O2);
			}

			
//			Perfusion perfusion = perfusion(currentPacket.getCurrentStatus());
//			if(perfusion == null) {
//				if(null != perfusionUpdate.getValue()) {
//					perfusionUpdate.setValue(null);
//					gateway.update(DemoPulseOx.this, perfusionUpdate);
//				}
//			} else if(perfusionUpdate.getValue() == null) {
//				perfusionUpdate.setValue(perfusion);
//				gateway.update(DemoPulseOx.this, perfusionUpdate);
//			} else if(!perfusion.equals(perfusionUpdate.getValue())) {
//				perfusionUpdate.setValue(perfusion);
//				gateway.update(DemoPulseOx.this, perfusionUpdate);
//			}
//			
//			if(null == firmwareRevisionUpdate.getValue() || !firmwareRevisionUpdate.getValue().equals(currentPacket.getFirmwareRevision())) {
//				firmwareRevisionUpdate.setValue(currentPacket.getFirmwareRevision());
//				gateway.update(DemoPulseOx.this, firmwareRevisionUpdate);
//			}
//			
//			Bool artifact = currentPacket.getCurrentStatus().isArtifact() ? Bool.True : Bool.False;
//			if(null == artifactUpdate.getValue() || !artifactUpdate.getValue().equals(artifact)) {
//				artifactUpdate.setValue(artifact);
//				gateway.update(DemoPulseOx.this, artifactUpdate);
//			}
//			
//			Bool smartPoint = currentPacket.isSmartPoint() ? Bool.True : Bool.False;
//			if(null == smartPointUpdate.getValue() || !smartPointUpdate.getValue().equals(smartPoint)) {
//				smartPointUpdate.setValue(smartPoint);
//				gateway.update(DemoPulseOx.this, smartPointUpdate);
//			}
//			
//			Bool lowBattery = currentPacket.isLowBattery() ? Bool.True : Bool.False;
//			if(null == lowBatteryUpdate.getValue() || !lowBatteryUpdate.getValue().equals(lowBattery)) {
//				lowBatteryUpdate.setValue(lowBattery);
//				gateway.update(DemoPulseOx.this, lowBatteryUpdate);
//			}
//			
//			Bool outOfTrack = currentPacket.getCurrentStatus().isOutOfTrack() ? Bool.True : Bool.False;
//			if(null == outOfTrackUpdate.getValue() || !outOfTrackUpdate.getValue().equals(outOfTrack)) {
//				outOfTrackUpdate.setValue(outOfTrack);
//				gateway.update(DemoPulseOx.this, outOfTrackUpdate);
//			}
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
    protected InstanceHolder<ice.Numeric> pulse;
    protected InstanceHolder<ice.Numeric> SpO2;
    protected InstanceHolder<SampleArray> pleth;
    
//	private final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
//	private final MutableNumericUpdate spo2Update = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
//	private final MutableWaveformUpdate plethUpdate = new MutableWaveformUpdateImpl(PulseOximeter.PLETH);
//	private final MutableEnumerationUpdate perfusionUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.PERFUSION);
//	private final MutableEnumerationUpdate artifactUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.ARTIFACT);
//	private final MutableEnumerationUpdate smartPointUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.SMART_POINT);
//	private final MutableEnumerationUpdate lowBatteryUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.LOW_BATTERY);
//	private final MutableNumericUpdate firmwareRevisionUpdate = new MutableNumericUpdateImpl(DemoPulseOx.FIRMWARE_REVISION);
//	private final MutableEnumerationUpdate outOfTrackUpdate = new MutableEnumerationUpdateImpl(DemoPulseOx.OUT_OF_TRACK);

	

	
	public void doInitCommands() throws IOException {
	    super.doInitCommands();
	    if(null == formatRequested) {
	        getDelegate().sendGetSerial();
	    }
	}


	@Override
	protected boolean delegateReceive(NoninPulseOx delegate) throws IOException {
		return delegate.receive();
	}
	
	@Override
	protected long getConnectInterval() {
	    return 1000L;
	}

	
	@Override
	// 3 packets per second mean the theoretical max quiet time is 333ms
	protected long getMaximumQuietTime() {
		return 750L;
	}
	
	@Override
	protected String iconResourceName() {
		return "3150.png";
	}
}
