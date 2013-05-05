/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.oridion.capnostream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.enumeration.Enumeration;
import org.mdpnp.data.enumeration.EnumerationImpl;
import org.mdpnp.data.enumeration.MutableEnumerationUpdate;
import org.mdpnp.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.data.numeric.MutableNumericUpdate;
import org.mdpnp.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.numeric.NumericImpl;
import org.mdpnp.data.waveform.MutableWaveformUpdate;
import org.mdpnp.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.devices.oridion.capnostream.Capnostream.Command;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.Capnograph;
import org.mdpnp.nomenclature.Ventilator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoCapnostream20 extends AbstractDelegatingSerialDevice<Capnostream> {

    public static final Numeric FAST_STATUS = new NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    public static final Numeric SLOW_STATUS = new NumericImpl(DemoCapnostream20.class, "SLOW_STATUS");
    public static final Numeric CO2_ACTIVE_ALARMS = new NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    public static final Numeric SPO2_ACTIVE_ALARMS = new NumericImpl(DemoCapnostream20.class, "FAST_STATUS");
    public static final Enumeration CAPNOSTREAM_UNITS = new EnumerationImpl(DemoCapnostream20.class, "CAPNOSTREAM_UNITS");
    public static final Numeric EXTENDED_CO2_STATUS = new NumericImpl(DemoCapnostream20.class, "EXTENDED_CO2_STATUS");
    
	@Override
	protected long getMaximumQuietTime() {
		return 6000L;
	}
	
	@Override
	protected String iconResourceName() {
		return "capnostream.png";
	}
	
	protected final MutableWaveformUpdate co2 = new MutableWaveformUpdateImpl(Capnograph.CAPNOGRAPH);
//	protected final MutableNumericUpdate spo2 = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
//	protected final MutableNumericUpdate pulserate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);

	protected final MutableNumericUpdate rr = new MutableNumericUpdateImpl(Ventilator.RESPIRATORY_RATE);
	protected final MutableNumericUpdate etco2 = new MutableNumericUpdateImpl(Ventilator.END_TIDAL_CO2_MMHG);
	protected final MutableNumericUpdate fastStatus = new MutableNumericUpdateImpl(DemoCapnostream20.FAST_STATUS);
	protected final MutableNumericUpdate slowStatus = new MutableNumericUpdateImpl(DemoCapnostream20.SLOW_STATUS);
	protected final MutableNumericUpdate co2ActiveAlarms = new MutableNumericUpdateImpl(DemoCapnostream20.CO2_ACTIVE_ALARMS);
	protected final MutableNumericUpdate spo2ActiveAlarms = new MutableNumericUpdateImpl(DemoCapnostream20.SPO2_ACTIVE_ALARMS);
	protected final MutableNumericUpdate extendedCO2Status = new MutableNumericUpdateImpl(DemoCapnostream20.EXTENDED_CO2_STATUS);
	protected final MutableEnumerationUpdate capnostreamUnits = new MutableEnumerationUpdateImpl(DemoCapnostream20.CAPNOSTREAM_UNITS);
	

	
	public DemoCapnostream20(Gateway gateway) {
		super(gateway);
		nameUpdate.setValue("Capnostream20");
		add(co2, rr, etco2, fastStatus, slowStatus, co2ActiveAlarms, spo2ActiveAlarms, extendedCO2Status, capnostreamUnits);
	}
	
	public DemoCapnostream20(Gateway gateway, SerialSocket serialSocket) {
        super(gateway, serialSocket);
        nameUpdate.setValue("Capnostream20");
        add(co2, rr, etco2, fastStatus, slowStatus, co2ActiveAlarms, spo2ActiveAlarms, extendedCO2Status, capnostreamUnits);
    }
	
	private static final int BUFFER_SAMPLES = 10;
	private final Number[] realtimeBuffer = new Number[BUFFER_SAMPLES];
	private int realtimeBufferCount = 0;
	private final Date date = new Date();
	
	protected volatile boolean connected = false;
	
	public class MyCapnostream extends Capnostream {
		public MyCapnostream(InputStream in, OutputStream out) {
			super(in, out);
		}
		
		private List<IdentifiableUpdate<?>> updates = new ArrayList<IdentifiableUpdate<?>>();
		
		@Override
		public boolean receiveNumerics(long date, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate,
		        int slowStatus, int CO2ActiveAlarms, int SpO2ActiveAlarms, int noBreathPeriodSeconds,
		        int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh, int rrAlarmLow, int fico2AlarmHigh,
		        int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units,
		        int extendedCO2Status) {
		    
		    updates.clear();
//		    if(DemoCapnostream20.this.spo2.setValue(spo2==0xFF?null:spo2)) {
//		        updates.add(DemoCapnostream20.this.spo2);
//            }
		    
		    if(DemoCapnostream20.this.extendedCO2Status.setValue(extendedCO2Status)) {
                updates.add(DemoCapnostream20.this.extendedCO2Status);
            }
		    
            if(DemoCapnostream20.this.rr.setValue(respiratoryRate==0xFF?null:respiratoryRate)) {
                updates.add(DemoCapnostream20.this.rr);
            }
//            if(DemoCapnostream20.this.pulserate.setValue(pulserate==0xFF?null:pulserate)) {
//                updates.add(DemoCapnostream20.this.pulserate);
//            }
            if(DemoCapnostream20.this.etco2.setValue(etCO2==0xFF?null:etCO2)) {
                updates.add(DemoCapnostream20.this.etco2);
            }
            if(DemoCapnostream20.this.slowStatus.setValue(slowStatus)) {
                updates.add(DemoCapnostream20.this.slowStatus);
            }
            if(DemoCapnostream20.this.co2ActiveAlarms.setValue(CO2ActiveAlarms)) {
                updates.add(DemoCapnostream20.this.co2ActiveAlarms);
            }
            if(DemoCapnostream20.this.spo2ActiveAlarms.setValue(SpO2ActiveAlarms)) {
                updates.add(DemoCapnostream20.this.spo2ActiveAlarms);
            }
            
            if(DemoCapnostream20.this.capnostreamUnits.setValue(units)) {
                updates.add(DemoCapnostream20.this.capnostreamUnits);
            }
            
            gateway.update(DemoCapnostream20.this, updates);
            return true;
		}
		
		@Override
		public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
			realtimeBuffer[realtimeBufferCount++] = co2;
			
			if(DemoCapnostream20.this.fastStatus.setValue(status)) {
	            gateway.update(DemoCapnostream20.this, DemoCapnostream20.this.fastStatus);    
			}
			
			
			if(realtimeBufferCount==realtimeBuffer.length) {
				realtimeBufferCount = 0;
				date.setTime(System.currentTimeMillis());
				DemoCapnostream20.this.co2.setValues(realtimeBuffer);
				DemoCapnostream20.this.co2.setTimestamp(date);
				DemoCapnostream20.this.co2.setMillisecondsPerSample(50.0);
//				log.trace("CO2:"+Arrays.toString(realtimeBuffer));
				gateway.update(DemoCapnostream20.this, DemoCapnostream20.this.co2);
			}
			return true;
		}
		
		@Override
		public boolean receiveDeviceIdSoftwareVersion(String s) {
			log.debug(s);
			connected = true;
			return true;
		}
	}

	private final Logger log = LoggerFactory.getLogger(DemoCapnostream20.class);
	@Override
	protected Capnostream buildDelegate(InputStream in, OutputStream out) {
		return new MyCapnostream(in, out);
	}
	@Override
	protected boolean doInitCommands(OutputStream outputStream)
			throws IOException {
	    super.doInitCommands(outputStream);

		long giveup = System.currentTimeMillis() + 10000L;
		
		while(System.currentTimeMillis() < giveup && !connected) {
			getDelegate().sendCommand(Command.EnableComm);
			try {
				Thread.sleep(400L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		getDelegate().sendHostMonitoringId("ICE");
		
		getDelegate().sendCommand(Command.StartRTComm);
		return connected;
	}

	@Override
	public void disconnect() {
		Capnostream capnostream = getDelegate(false);

		if(null != capnostream) {
			try {
				capnostream.sendCommand(Command.DisableComm);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.trace(" was already null in disconnect");
		}
		super.disconnect();
	}
	
	@Override
	public SerialProvider getSerialProvider() {
		SerialProvider serialProvider =  super.getSerialProvider();
		serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
		return serialProvider;
	}
	
	@Override
	protected boolean delegateReceive(Capnostream delegate) throws IOException {
		return delegate.receive();
	}


}
