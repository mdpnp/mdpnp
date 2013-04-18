package org.mdpnp.devices.draeger.medibus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.PureJavaSerialPort;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class SerialRTMedibus  {

	private class MyRTMedibus extends RTMedibus {

		public MyRTMedibus(InputStream in, OutputStream out) {
			super(in, out);
		}
		@Override
		public void receiveDataValue(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, int data) {
			cache.put(realtimeData, data);
		}
		@Override
		protected void receiveDeviceSetting(Data[] data, int n) {
			for(Data d : data) {
				cache.put(d.code, d.data);
			}
		}
		@Override
		protected void receiveMeasuredData(Data[] data, int n) {
			for(Data d : data) {
				cache.put(d.code, d.data);
			}
		}
		
		@Override
		protected void receiveTextMessage(Data[] data, int n) {
			for(Data d : data) {
				cache.put(d.code, d.data);
			}
		}
		@Override
		public void startInspiratoryCycle() {
			lastStartOfBreath = System.currentTimeMillis();
		}
		
		@Override
		protected void receiveDateTime(Date date) {
			offset = date.getTime() - System.currentTimeMillis();
		}
		
	}
	
	public SerialRTMedibus() {
		
	}
	private MyRTMedibus medibus;
	private Thread slowThread, fastThread, pollThread;
	
	private final Map<Object, Object> cache = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
	
	private final NumberFormat numberFormat = NumberFormat.getInstance();
	
	protected long lastStartOfBreath;
	protected long offset;
	
	public long getLastStartOfBreath() {
		return lastStartOfBreath;
	}
	
	public long getTime() {
		return System.currentTimeMillis() + offset;
	}
	
	public Number getNumber(Object key) {
		Object o = get(key);
		try {
			return null == o ? null : numberFormat.parse(o.toString());
		} catch(ParseException pe) {
			throw new RuntimeException(pe);
		}
	}
	
	public Object get(Object key) {
		return cache.get(key);
	}
	
	
	private static final Command[] slowRequests = new Command[] {Command.ReqDeviceSetting, Command.ReqMeasuredDataCP1};
	
	protected boolean connected = false;
	private final Logger log = LoggerFactory.getLogger(SerialRTMedibus.class);
	public void connect(String port) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
		CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(port);
		PureJavaSerialPort serialPort = (PureJavaSerialPort) portid.open("ICE", 10000);
		// Apollo
		serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
		// EvitaXL
//		serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		medibus = new MyRTMedibus(serialPort.getInputStream(), serialPort.getOutputStream());
		connected = true;
		slowThread = new Thread(new Runnable() {
			public void run() {
				boolean keepGoing = true;
				while(connected && keepGoing) {
					try {
						keepGoing = medibus.receive();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Medibus slow processing completed");
			}
		});
		slowThread.setDaemon(true);
		slowThread.start();
		
		fastThread = new Thread(new Runnable() {
			public void run() {
				boolean keepGoing = true;
				while(connected && keepGoing) {
					try {
						keepGoing = medibus.receiveFast();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Medibus fast processing completed");
			}
		});
		fastThread.setDaemon(true);
		fastThread.start();
		
		medibus.sendCommand(Command.InitializeComm);
		medibus.sendCommand(Command.ReqDateTime);
		medibus.enableRealtime(5000L, RealtimeData.AirwayPressure, RealtimeData.FlowInspExp);
		
		pollThread = new Thread(new Runnable() {
			public void run() {
				boolean success = true;
				while(connected&& success) {
					for(Command c : slowRequests) {
						try {
							success = medibus.sendCommand(c, 5000L);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				log.info("Medibus slow polling completed");
			}
		});
		pollThread.setDaemon(true);
		pollThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}));
	}
	
	public void disconnect() throws IOException, InterruptedException {
		medibus.sendCommand(Command.StopComm);
		connected = false;
		slowThread.join();
		fastThread.join();
	}
	public static final void main(String[] args) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, InterruptedException {
		SerialRTMedibus rtm = new SerialRTMedibus();
		rtm.connect("cu.PL2303-00001014");
		Thread.sleep(10000L);
		rtm.disconnect();
	}

}
