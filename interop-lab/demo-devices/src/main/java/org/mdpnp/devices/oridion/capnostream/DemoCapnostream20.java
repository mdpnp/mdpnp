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
import java.util.Date;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.data.numeric.MutableNumericUpdate;
import org.mdpnp.comms.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdate;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.comms.nomenclature.Capnograph;
import org.mdpnp.comms.nomenclature.Ventilator;
import org.mdpnp.comms.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.comms.serial.SerialProvider;
import org.mdpnp.comms.serial.SerialSocket.DataBits;
import org.mdpnp.comms.serial.SerialSocket.Parity;
import org.mdpnp.comms.serial.SerialSocket.StopBits;
import org.mdpnp.devices.oridion.capnostream.Capnostream.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoCapnostream20 extends AbstractDelegatingSerialDevice<Capnostream> {

	@Override
	protected long getMaximumQuietTime() {
		return 5000L;
	}
	
	@Override
	protected String iconResourceName() {
		return "capnostream.png";
	}
	
	protected final MutableWaveformUpdate co2 = new MutableWaveformUpdateImpl(Capnograph.CAPNOGRAPH);
//	protected final MutableNumericUpdate spo2 = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
//	protected final MutableNumericUpdate pulserate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
//	protected final MutableNumericUpdate rr = new MutableNumericUpdateImpl(Capnograph.AIRWAY_RESPIRATORY_RATE);
	protected final MutableNumericUpdate rr = new MutableNumericUpdateImpl(Ventilator.RESPIRATORY_RATE);
	protected final MutableNumericUpdate etco2 = new MutableNumericUpdateImpl(Ventilator.END_TIDAL_CO2_MMHG);
	
	public DemoCapnostream20(Gateway gateway) {
		super(gateway);
		nameUpdate.setValue("Capnostream20");
		add(co2, rr);
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
		
		@Override
		public boolean receiveNumerics(long date, int etCO2, int FiCO2,
				int respiratoryRate, int spo2, int pulserate) {
//			Capnostream20Impl.this.spo2.setValue(spo2==0xFF?null:spo2);
			DemoCapnostream20.this.rr.setValue(respiratoryRate==0xFF?null:respiratoryRate);
//			Capnostream20Impl.this.pulserate.setValue(pulserate==0xFF?null:pulserate);
			DemoCapnostream20.this.etco2.setValue(etCO2==0xFF?null:etCO2);
			gateway.update(DemoCapnostream20.this, DemoCapnostream20.this.rr, DemoCapnostream20.this.etco2);
			return true;
		}
		
		@Override
		public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
			realtimeBuffer[realtimeBufferCount++] = co2;
			if(realtimeBufferCount==realtimeBuffer.length) {
				realtimeBufferCount = 0;
				date.setTime(System.currentTimeMillis());
				DemoCapnostream20.this.co2.setValues(realtimeBuffer);
				DemoCapnostream20.this.co2.setTimestamp(date);
					// interval is in microseconds
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
		setOutputStream(outputStream);
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
		Capnostream capnostream = getDelegate();

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
