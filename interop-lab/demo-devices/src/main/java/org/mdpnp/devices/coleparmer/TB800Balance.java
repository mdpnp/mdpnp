package org.mdpnp.devices.coleparmer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceClock.Reading;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

import ice.Numeric;

public class TB800Balance extends AbstractSerialDevice {
	
	BufferedWriter writer;
	BufferedReader reader;
	boolean connected;
	
	private static final String MODE="C1";
	private static final String CONT_OFF="C0\r\n";
	private static final String CONT_ON="C1\r\n";
	private static final String MODE_COMMAND=MODE+"\r\n";
	
	private InstanceHolder<Numeric> holder;
	private DeviceClock defaultClock;

	public TB800Balance(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		super(subscriber, publisher, eventLoop);
		deviceIdentity.manufacturer="Cole-Parmer";
		deviceIdentity.model="TB-800";
		deviceIdentity.operating_system="";
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		writeDeviceIdentity();
		defaultClock=new DeviceClock.WallClock();
	}
	
	@Override
	protected long getMaximumQuietTime(int idx) {
		return Long.MAX_VALUE;	//Obviously this is a VERY large number, but since we have no negotiation etc...
	}

	@Override
	protected void doInitCommands(int idx) throws IOException {
		
		// Before connecting scale to OpenICE, please ensure it is in the "Continuous Transmission" Mode
		reportConnected("Scale connected to OpenICE");
		connected=true;
		
		
	}

	@Override
	protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
		writer=new BufferedWriter(new OutputStreamWriter(outputStream));
		reader=new BufferedReader(new InputStreamReader(inputStream));
		while(!connected) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.err.println("Now connected in process loop.");
		//We should now be able to keep reading response lines.
		String massFrame;
		float f;
		while(true) {
			massFrame=reader.readLine();
			if(massFrame.startsWith("SI ")) {
				//Expected format
				String mass=massFrame.substring(6, 15);
				String unit=massFrame.substring(17,20);
				f=Float.parseFloat(mass);
				System.err.println("mass "+mass+" "+unit);
				Reading r=defaultClock.instant();
				numericSample(holder, f, rosetta.MDC_ATTR_PT_WEIGHT.VALUE , rosetta.MDC_ATTR_PT_WEIGHT.VALUE, 0, rosetta.MDC_DIM_G.VALUE, new DeviceClock.CombinedReading(r, r));
			} else {
				System.err.println("Mass line was actually "+massFrame);
				if(massFrame.trim().endsWith(" g")) {
					String tweaked=massFrame.replaceAll("[^\\d\\.]","");
					f=Float.parseFloat(tweaked);
					Reading r=defaultClock.instant();
					numericSample(holder, f, rosetta.MDC_ATTR_PT_WEIGHT.VALUE , rosetta.MDC_ATTR_PT_WEIGHT.VALUE, 0, rosetta.MDC_DIM_G.VALUE, new DeviceClock.CombinedReading(r, r));
				}
			}
		}
		
	}

	@Override
	public SerialProvider getSerialProvider(int idx) {
		SerialProvider serialProvider = super.getSerialProvider(idx);
		//No detail in the docs about DataBits or StopBits.  Parity may be any of None, Even, Odd!
        serialProvider.setDefaultSerialSettings(57600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
	}
	
	@Override
	protected String iconResourceName() {
		return "balance.png";
	}

}
