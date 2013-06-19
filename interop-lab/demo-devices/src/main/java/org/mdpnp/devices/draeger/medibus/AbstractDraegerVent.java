package org.mdpnp.devices.draeger.medibus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.data.Identifier;
import org.mdpnp.data.MutableIdentifiableUpdate;
import org.mdpnp.data.enumeration.Enumeration;
import org.mdpnp.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.data.identifierarray.IdentifierArray;
import org.mdpnp.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.data.numeric.MutableNumericUpdate;
import org.mdpnp.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.text.MutableTextUpdate;
import org.mdpnp.data.text.MutableTextUpdateImpl;
import org.mdpnp.data.text.Text;
import org.mdpnp.data.textarray.MutableTextArrayUpdateImpl;
import org.mdpnp.data.textarray.TextArray;
import org.mdpnp.data.waveform.MutableWaveformUpdate;
import org.mdpnp.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.data.waveform.Waveform;
import org.mdpnp.devices.draeger.medibus.Medibus.Alarm;
import org.mdpnp.devices.draeger.medibus.Medibus.Data;
import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.MeasuredDataCP1;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.Device;
import org.mdpnp.nomenclature.PulseOximeter;
import org.mdpnp.nomenclature.Ventilator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDraegerVent extends AbstractDelegatingSerialDevice<RTMedibus> {

	private static final Logger log = LoggerFactory.getLogger(AbstractDraegerVent.class);
	
	protected  Map<Enum<?>, InstanceHolder<ice.Numeric>> numericUpdates = new HashMap<Enum<?>, InstanceHolder<ice.Numeric>>();
	protected  Map<Enum<?>, InstanceHolder<ice.SampleArray>> sampleArrayUpdates = new HashMap<Enum<?>, InstanceHolder<ice.SampleArray>>();
	
	protected final InstanceHolder<ice.Numeric> startInspiratoryCycleUpdate;// = new MutableTextUpdateImpl(Ventilator.START_INSPIRATORY_CYCLE);
	protected final InstanceHolder<ice.Numeric> timeUpdate; // = new MutableNumericUpdateImpl(Device.TIME_MSEC_SINCE_EPOCH);
	
	protected MutableIdentifiableUpdate<?> getUpdate(Object code) {
		if(code instanceof Enum<?>) {
			MutableIdentifiableUpdate<?> miu = updates.get(code);
			if(null == miu) {
//				log.trace("No update for enum code="+code+" class="+code.getClass().getName());
			}
			return miu;
		} else {
			log.trace("No update for code="+code+" class="+code.getClass().getName());
			return null;
		}
	}
	
	protected void populateUpdate(MutableIdentifiableUpdate<?> update, Object value) {
		if(update instanceof MutableTextUpdate) {
			((MutableTextUpdate)update).setValue(null==value?null:value.toString());
		} else if(update instanceof MutableNumericUpdate) {
			try {
				// TODO There are weird number formats in medibus .. this will need enhancement
				if(value instanceof Number) {
					((MutableNumericUpdate)update).setValue((Number) value);	
				} else {
					String s = null == value ? null : value.toString().trim();
					((MutableNumericUpdate)update).setValue(null==s?(Number)null:(Number)Double.parseDouble(s));
				}
				
			} catch(NumberFormatException nfe) {
				((MutableNumericUpdate)update).setValue(null);
			}
		}
	}
	
	protected void processStartInspCycle() {
		gateway.update(this, startInspiratoryCycleUpdate);
	}
	
	private static final int BUFFER_SAMPLES = 10;
	
	// Theoretical maximum 16 streams, practical limit seems to be 3 
	// Buffering ten points is for testing, size of this buffer might be 
	// a function of the sampling rate
	private final Number[][] realtimeBuffer = new Number[16][BUFFER_SAMPLES];
	private final int[] realtimeBufferCount = new int[16];
	private long lastRealtime;
	
	private final Date date = new Date();
	protected void processRealtime(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object code, int value) {
		lastRealtime = System.currentTimeMillis();
		realtimeBuffer[streamIndex][realtimeBufferCount[streamIndex]++] = value;
		if(realtimeBufferCount[streamIndex]==realtimeBuffer[streamIndex].length) {
			realtimeBufferCount[streamIndex] = 0;
			// flush
			MutableIdentifiableUpdate<?> miu = getUpdate(code);
			if(null != miu && miu instanceof MutableWaveformUpdate) {
				date.setTime(System.currentTimeMillis());
				MutableWaveformUpdate mwu = (MutableWaveformUpdate) miu;
				mwu.setValues(realtimeBuffer[streamIndex]);
				mwu.setTimestamp(date);
				// interval is in microseconds
				mwu.setMillisecondsPerSample(1.0 * config.interval * multiplier / 1000.0);
				gateway.update(this, mwu);
			} else {
				log.warn("for "+ code + " did not get expected WaveformUpdate type, identifier=" + (null == miu ? "null":miu.getIdentifier()));
			}
			
			
		}
	}
	
	protected void process(Object code, Object data) {
		MutableIdentifiableUpdate<?> miu = getUpdate(code);
		if(null != miu) {
			populateUpdate(miu, data);
			gateway.update(this, miu);
		}
	}
	
	protected void process(Data d) {
		process(d.code, d.data);
	}
	
	protected void process(Alarm a) {
		process(a.alarmCode, a.alarmPhrase);
	}
	
	protected void process(Alarm[] alarms) {
		for(Alarm a : alarms) {
			process(a);
		}
	}
	
	protected void process(Date date) {
		timeUpdate.setValue(date.getTime());
		gateway.update(this, timeUpdate);
	}
	
	protected void processCorrupt(Object cmd) {
		if(Command.ReqDeviceId.equals(cmd)) {
			// Repeat ourselves
			try {
				getDelegate().sendCommand(Command.ReqDeviceId, REQUEST_TIMEOUT);
			} catch (IOException e) {
				log.error("", e);
			}
		}
	}
	
	protected void process(Data[] data, int n) {
		for(int i = 0; i < n; i++) {
			process(data[i]);
		}
	}
	
	private class MyRTMedibus extends RTMedibus {
		public MyRTMedibus(InputStream in, OutputStream out) {
			super(in, out);
		}
		@Override
		protected void receiveDeviceIdentification(String idNumber,
				String name, String revision) {
			receiveDeviceId(idNumber, name);
		}
		@Override
		protected void receiveTextMessage(Data[] data, int n) {
			process(data, n);
		}
		@Override
		protected void receiveDeviceSetting(Data[] data, int n) {
			process(data, n);
		}
		@Override
		protected void receiveMeasuredData(Data[] data, int n) {
			process(data, n);
		}
		@Override
		protected void receiveCorruptResponse(Object priorCommand) {
			processCorrupt(priorCommand);
		}
		@Override
		public void receiveDataValue(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, int data) {
			processRealtime(config, multiplier, streamIndex, realtimeData, data);
		}
		@Override
		protected void receiveAlarms(Alarm[] alarms) {
			process(alarms);
		}
		@Override
		protected void receiveDateTime(Date date) {
			process(date);
		}
		@Override
		public void startInspiratoryCycle() {
			processStartInspCycle();
		}
		
	}
	
//	private static final long POLITE_REQUEST_INTERVAL = 500L;
	private static final long REQUEST_TIMEOUT = 7000L;
	private static final Command[] REQUEST_COMMANDS = {
//		Command.ReqDateTime,
		Command.ReqDeviceSetting,
//		Command.ReqAlarmsCP1,
		Command.ReqMeasuredDataCP1,
//		Command.ReqAlarmsCP2,
//		Command.ReqMeasuredDataCP2,
//		Command.ReqTextMessages
	};
	private class RequestSlowData implements Runnable {
		public void run() {
			if(State.Connected.equals(getState())) {
				try {
					if( (System.currentTimeMillis()-lastRealtime) >= getMaximumQuietTime() ) {
						log.warn(""+(System.currentTimeMillis()-lastRealtime) +"ms since realtime data, requesting anew");
						
						if(!getDelegate().enableRealtime(REQUEST_TIMEOUT, RealtimeData.AirwayPressure, RealtimeData.FlowInspExp, RealtimeData.ExpiratoryCO2mmHg, RealtimeData.O2InspExp)) {
							log.debug("timed out waiting to issue enableRealtime");
						}
					}
					
					RTMedibus medibus = AbstractDraegerVent.this.getDelegate();
					for(Command c : REQUEST_COMMANDS) {
						if(!medibus.sendCommand(c, REQUEST_TIMEOUT)) {
							log.debug("polling thread timed out sending request " + c);
							return;
						}
						Thread.sleep(200L);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
				
		}
	}
	
	@Override
	public void disconnect() {
		stopRequestSlowData();
		RTMedibus medibus = null;
		synchronized(this) {
			medibus = getDelegate(false);
		}
		if(null != medibus) {
			try {
				if(!medibus.sendCommand(Command.StopComm, 1000L)) {
					log.trace("timed out waiting to send StopComm");
				} else {
				    log.trace("sent StopComm");
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.debug("rtMedibus was already null in disconnect");
		}
		super.disconnect();
	}
	
	private void loadMap() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(AbstractDraegerVent.class.getResourceAsStream("draeger.map")));
			String line = null;
			// TODO this is a kluge until nomenclature ideas are more mature
			String draegerPrefix = MeasuredDataCP1.class.getPackage().getName()+".";
			String prefix = ice.Numeric.class.getPackage().getName()+".";

			
			while(null != (line = br.readLine())) {
				line = line.trim();
				if('#'!=line.charAt(0)) {
					String v[] = line.split("\t");
					String c[] = v[0].split("\\.");
					@SuppressWarnings({ "unchecked", "rawtypes" })
                    Enum<?> draeger = (Enum<?>) Enum.valueOf( (Class<? extends Enum>)Class.forName(draegerPrefix+c[0]), c[1]);
					int tag = Class.forName(prefix+v[1]).getField("VALUE").getInt(null);
					log.trace("Adding " + draeger + " mapped to " + i);
					add(draeger, i);
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private ScheduledFuture<?> requestSlowData;
	
	private synchronized void stopRequestSlowData() {
		if(null != requestSlowData) {
			requestSlowData.cancel(false);
			requestSlowData = null;
			log.trace("Canceled slow data request task");
		} else {
		    log.trace("Slow data request already canceled");
		}
	}
	
	private synchronized void startRequestSlowData() {
		if(null == requestSlowData) {
			requestSlowData = executor.scheduleWithFixedDelay(new RequestSlowData(), 2000L, 500L, TimeUnit.MILLISECONDS);
			log.trace("Scheduled slow data request task");
		} else {
		    log.trace("Slow data request already scheduled");
		}
	}
	
	public AbstractDraegerVent(int domainId) {
		super(domainId);
		loadMap();
	}
	
	public AbstractDraegerVent(int domainId, SerialSocket serialSocket) {
        super(domainId, serialSocket);
        loadMap();
    }

	@Override
	protected RTMedibus buildDelegate(InputStream in, OutputStream out) {
	    log.trace("Creating an RTMedibus");
	    return new MyRTMedibus(in, out);
	}
	
	@Override
	protected boolean delegateReceive(RTMedibus delegate) throws IOException {
	    return delegate.receive();
	}

	private boolean gotDeviceId = false;
	protected synchronized void receiveDeviceId(String guid, String name) {
		log.trace("receiveDeviceId:guid="+guid+", name="+name);
		
		boolean writeIt = false;
		if(null!=guid) {
		    deviceIdentity.serial_number = guid;
		    writeIt = true;
		    
		}
		if(null!=name) {
		    deviceIdentity.model = "Draeger " + name;
		    writeIt = true;
		}
		if(writeIt) {
		    deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
		}
		gotDeviceId = true;
		notifyAll();
	}
	
	
	
	@Override
	protected boolean doInitCommands(OutputStream outputStream) throws IOException {
		super.doInitCommands(outputStream);
		RTMedibus rtMedibus = getDelegate();
		
		long now = System.currentTimeMillis();
		long giveup = now + getConnectInterval();
		
//		if(Medibus.State.Uninitialized.equals(rtMedibus.getState())) {
			if(!rtMedibus.sendCommand(Command.InitializeComm, giveup - System.currentTimeMillis())) {
				log.debug("timed out waiting to issue InitializeComm");
				return false;
			}
//		}

		synchronized(this) {
			gotDeviceId = false;
			while(true) {
				if(gotDeviceId) {
					startRequestSlowData();
					return true;
				} else if(System.currentTimeMillis()>giveup) {
					log.debug("timed out waiting for deviceId");
					return false;
				}
				
				if(!rtMedibus.sendCommand(Command.ReqDeviceId, giveup - System.currentTimeMillis())) {
					log.debug("timed out waiting to issue ReqDeviceId");
					return false;
				}
				
				try {
					wait(giveup - System.currentTimeMillis());
				} catch (InterruptedException e) {
					log.error("", e);
				}
			}
		}
	}
	@Override
	protected long getMaximumQuietTime() {
		return 6000L;
	}
	@Override
	protected void process(InputStream inputStream) throws IOException {
	   
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    // Will block until the delegate is available
                    final RTMedibus rtMedibus = getDelegate();
                    rtMedibus.receiveFast();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Medibus FAST data");
        t.setDaemon(true);
        t.start();
        log.trace("spawned a fast data processor");

        // really the RTMedibus thread will block until
        // the super.process populates an InputStream to allow
        // building of the delegate
        super.process(inputStream);
	    
	}

}
