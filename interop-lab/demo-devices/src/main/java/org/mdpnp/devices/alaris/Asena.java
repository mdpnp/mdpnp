package org.mdpnp.devices.alaris;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

import org.apache.commons.lang3.ArrayUtils;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.alaris.AlarisMITM.TimeAndCommand;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.devices.simulation.pump.SimControllablePump;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

import ice.FlowRateObjectiveDataReader;
import ice.InfusionObjectiveDataReader;
import ice.InfusionProgram;
import ice.InfusionProgramDataReader;
import ice.Numeric;

public class Asena extends AbstractSerialDevice {
	
private boolean initDone;
	
//	private BufferedInputStream fromDevice;
	private BufferedReader fromDevice;
	private BufferedOutputStream toDevice;
	
	private DeviceClock.WallClock wallClock;
	
	private String InstSerial;
	private String ConnectionStatus;
	private String AlarmNotification;
	private String InfMode;
	private String InfRate;
	private String InfRateUnit;
	private String VolumeInf;
	private String VolumeInfUnit;
	private String InfTimeRemaining;
	private String Pressure; 
	private String PressureUnit;
	private String LogType;
	private String LogEntryID;
	
	private String serialNumber;
	
	private float currentVTBI;
	private static final long MAX_WAIT=500L;
	
	private boolean pendingWriteSpeed = false;
	private boolean pendingWriteBolus = false;
	float setPumpSpeed;
	float setVTBIvol;
	float setBolusSpeed;
	float setBolusVol;
	int masterValue = (int)1;
	
	private final InstanceHolder<Numeric> flowRateHolderHead, volumeInfused, vtbiRemaining;
	
	private FlowRateObjectiveDataReader flowRateReader;
	private InfusionObjectiveDataReader pauseResumeReader;
	private InfusionProgramDataReader programReader;
	private Topic flowRateTopic,pauseResumeTopic, programTopic;
	private QueryCondition flowRateQueryCondition, pauseResumeQueryCondition, programQueryCondition;

	
	private static final Logger log = LoggerFactory.getLogger(Asena.class);
	
	private static final String INFSTATUS = "!INF|4961\r";
	private static final String COMMSPROTOCOL = "!COMMS_PROTOCOL|E8DA\r";
	private static final String INSTSERIAL = "!INST_SERIALNO|457D\r";
	//private static final String REMOTEENABLE = "!REMOTE_CTRL^ENABLED^3CC1|B1F1\r";

	private static final String BOLUSENABLE = "!INF_BOLUS^ENABLED|E288\r";
	private static final String VTBI = "!INF_VTBI|8456\r";
	private static final String VTBISET = "!INF_VTBI^ACTIV^11.000^ml^STOP|4433\r";
	private static final String VTBIDEACTIVATE = "!INF_VTBI^DEACT|D8C5\r";
	
	private static final String REMQUERYDEACTIVATE = "!REMQUERY^DEACT|F295\r";
	private static final String REMOTEDISABLE = "!REMOTE_CTRL^DISABLED|EF95\r";
	
	private static final String INFSTART = "!INF_START|38F3\r";
	private static final String INFSTOP = "!INF_STOP|CD57\r";
	
	private static final int MAX_RESPONSE_LEN = 15000;
	
	public Asena(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		this(subscriber, publisher, eventLoop,1);
	}
	
	public Asena(Subscriber subscriber, Publisher publisher, EventLoop eventLoop, int countSerialPorts) {
		super(subscriber, publisher, eventLoop, countSerialPorts);
		deviceIdentity.manufacturer = "Alaris";
		deviceIdentity.model = "Asena";
		deviceIdentity.operating_system="OS";
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		super.writeDeviceIdentity();
		flowRateHolderHead = createNumericInstance("MDC_FLOW_FLUID_PUMP", "DEV_STATUS_INFRATE_ACTUAL");
		volumeInfused = createNumericInstance("VOLUME_INFUSED", "DEV_STATUS_TVI");
		vtbiRemaining = createNumericInstance("VTBI", "DEV_PROG_CONT_VTBI");
		wallClock = new DeviceClock.WallClock();
//		addListener();
		addObjectiveListeners();
	}
	
	
	@Override
	protected long getMaximumQuietTime(int idx) {
		// TODO Auto-generated method stub
		return 10000L;
	}
	
	public SerialProvider getSerialProvider(int idx) {
		SerialProvider provider = super.getSerialProvider(idx);
		provider.setDefaultSerialSettings(38400, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
		return provider;
	}

	@Override
	protected void doInitCommands(int idx) throws IOException{
		
		initDone = false;


		
		instituteSerialNumber();
		
		getCommsResponseMax();

		commsProtocol();
	
		remoteEnable();
		
//		String serialno=Long.toString(getPumpSerialNumber());
		writeTechnicalAlert("serialNumber", serialNumber);
		writeTechnicalAlert("UDI", deviceIdentity.unique_device_identifier);
		writeTechnicalAlert("Model", "Asena");
				
		reportConnected("Connection Protocol complete and Remote Control is Enabled");
		initDone = true;
//		System.err.println("^^^^^^^^^^^^^^^^^^^^COM " +portIdentPass);
		
	}
	
	
	@Override
	protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException{
//		this.fromDevice = new BufferedInputStream(inputStream);
		fromDevice=new BufferedReader(new InputStreamReader(inputStream));
		this.toDevice = new BufferedOutputStream(outputStream);
		try {
			while( !initDone) {
				Thread.sleep(1000L);
			}
			while (true) {
				doStatReq();
				Thread.sleep(2500);
				
				if(pendingWriteBolus != false) {
					System.err.println("Pending Bolus of " + String.valueOf(setBolusSpeed));
					pumpStatus();
					queryVTBI();
					setVTBI(50);
					setBolus(setBolusSpeed);
					long waitTime = (long) ((setBolusVol/setBolusSpeed)*60*60*1000);
//					System.err.print(waitTime + " SECONDS TO GO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					System.err.print("WAIT TIME DONE ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
					
					if(currentVTBI==0) {
						deactivateVTBI();
					}else {
						setVTBI(currentVTBI);
					}
					
					actualSetSpeed(Float.parseFloat(InfRate));
					
					
					pendingWriteBolus = false;
					System.err.println(setBolusVol +" ml of Bolus at " + setBolusSpeed + " ml/h attempted. Pending commands - " + pendingWriteBolus + "\n");
				}
				
				if (pendingWriteSpeed != false) {
					System.err.println("Pending write command of " + String.valueOf(setPumpSpeed));
					actualSetSpeed(setPumpSpeed);
					if(setVTBIvol >0) {
						setVTBI(setVTBIvol);
					}
//					else {
//						deactivateVTBI();
//					}
					pendingWriteSpeed = false;
					System.err.println(setPumpSpeed + " ml/h speed attemped. Pending commands - " + pendingWriteSpeed + "\n");
					setPumpSpeed =(float) 0.1;
//					setVTBIvol = 50;
				}
				
			}
		}catch (InterruptedException e) {
			e.printStackTrace();
			}catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	private synchronized void doStatReq() throws IOException{
		
		remoteEnable();
		
		pumpStatus();
		queryVTBI();
		
//		checkAlarm();
		
		System.err.println("Flow Rate is "+ InfRate);
		numericSample(flowRateHolderHead, Float.parseFloat(InfRate), wallClock.instant());
		numericSample(vtbiRemaining, currentVTBI, wallClock.instant());
		numericSample(volumeInfused, Float.parseFloat(VolumeInf), wallClock.instant());
		
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
		
	
	private void setSpeed(float newFlowRate) throws IOException {
		
		setPumpSpeed = newFlowRate;
		pendingWriteSpeed = true;
	}
	
	private void actualSetSpeed(float newFlowRate) throws IOException {
		
		remoteEnable();
		
//		check if value is within limits
		
		if (newFlowRate > 1200 || newFlowRate < 0.1 ) {
			System.err.println("Value of pump flow rate is out of limits. Rate must be 0.1 - 1200 ml/h \n");
			return;
		}
		
//		format value to be set
		DecimalFormat df = new DecimalFormat("0.00");
		System.err.println("Asena setSpeed to " + df.format(newFlowRate));
		
		String command = "INF_RATE^" + df.format(newFlowRate) + "^ml/h";
		System.err.println(command);
		String sendCommand = crc(command);
		System.err.println(sendCommand);
		
		byte[] initBytes = sendCommand.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("INF RATE SET command written in setSpeed");

		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		int bytesRead = 0;
//		while(bytesRead <24 || responseBytes[bytesRead-6] !='|') {
////			System.err.println("Read with bytesRead " + bytesRead);
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
////			System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//		}
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("INF SET command response is " + response);

	}

	private void setBolus(float newFlowRate) throws IOException {
		
		remoteEnable();
		
//		check if value is within limits
		
		if (newFlowRate > 600 || newFlowRate < 1200 ) {
			
//			format value to be set
			DecimalFormat df = new DecimalFormat("0.00");
//			System.err.println("Asena setSpeed to " + df.format(newFlowRate));
			
			String command = "INF_RATE^" + df.format(newFlowRate) + "^ml/h";
//			System.err.println(command);
			String sendCommand = crc(command);
			System.err.println(sendCommand);
			
			byte[] initBytes = sendCommand.getBytes();
			toDevice.write(initBytes);
			toDevice.flush();
			System.err.println("SET BOLUS command written");
			
			String response = fromDevice.readLine();
//			byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//			int bytesRead = 0;
//			
//			while(bytesRead <24 || responseBytes[bytesRead-6] !='|') {
////				System.err.println("Read with bytesRead " + bytesRead);
//				bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
////				System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//			}
//			String response = new String (responseBytes,0,bytesRead);
			System.err.println("SET BOLUS command response is " + response);
			
		}
		
		else {
			System.err.println("Value of Bolus is out of limits.");
			return;
		}
		


	}
	
	private synchronized void pauseInfusion() throws IOException {
		
		byte[] initBytes = INFSTOP.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Attempting to pause infusion");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		System.err.println("Reading for response");
//		int bytesRead = 0;
//		
//		while(bytesRead <15 || responseBytes[bytesRead-6] !='|') {
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);			
//		}		
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("INF STOP command response is " + response);
	}
	
	private synchronized void instituteSerialNumber() throws IOException {
		
		byte[] initBytes = INSTSERIAL.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("INSTSERIAL command written");
		
		String response = fromDevice.readLine();
		
		System.err.println(response);
		String parts[] = response.split("\\^");
//		System.err.println(parts[1]);
		String lastvals [] = parts[1].split("\\|");
//		System.err.println(lastvals[0]);
		serialNumber = lastvals[0];
		
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		System.err.println("Reading for response");
//		int bytesRead = 0;
//		
//		while(bytesRead <31 || responseBytes[bytesRead-6] !='|') {
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
//		}
//				
//		String response = new String(responseBytes,0,bytesRead);
		System.err.println("INST SERIAL response is " + response);
	}
	
	private synchronized void getCommsResponseMax() throws IOException {
		String cmd="COMMS_RESPONSE_MAX";
		String sendThis=crc(cmd);
		System.err.println("comms cmd is "+sendThis);
		toDevice.write(sendThis.getBytes());
		toDevice.flush();
		
		String response=fromDevice.readLine();
		System.err.println("timeout is "+response);
	}
	
	private synchronized void commsProtocol() throws IOException {
		
		byte[] initBytes = COMMSPROTOCOL.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Comms Protocol command written");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		System.err.println("Reading for response");
//		int bytesRead = 0;
//		
//		while(bytesRead <37 || responseBytes[bytesRead-6] !='|') {
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
//		}
//				
//		String response = new String(responseBytes,0,bytesRead);
		System.err.println("COMMS PROTOCOL response is " + response);
	}
	
	private synchronized void startInfusion() throws IOException {
		
		byte[] initBytes = INFSTART.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Attempting to start infusion");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		int bytesRead = 0;
//		
//		while(bytesRead <15 || responseBytes[bytesRead-6] !='|') {
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);			
//		}		
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("INF START command response is " + response);
	}
	
	private synchronized void remoteEnable() throws IOException {
		
		String commsCode = crc(serialNumber);
		String parts[] = commsCode.split("\\|");
		commsCode = parts[1];
//		System.err.println(serialNumber);
//		System.err.println(commsCode);
		
//		String REMOTEENABLE = "REMOTE_CTRL^ENABLED^" + commsCode;
//		System.err.println(REMOTEENABLE);
//		REMOTEENABLE = crc(REMOTEENABLE);
		String REMOTEENABLE=getRemoteControlCmd(serialNumber);
		System.err.println(REMOTEENABLE);
		byte[] initBytes = REMOTEENABLE.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Attempting to write REMOTE ENABLE command");
		
		
		final StringBuilder sb=new StringBuilder();
		String PUMPresponse = null;
		
		// Timeout tracking Thread
		IOException[] ioeDuringRead=new IOException[1];
		Thread t = new Thread() {

			@Override
			public void run() {
				long l1=System.currentTimeMillis();
				try {
					
					sleep(MAX_WAIT);
					if (sb.length() == 0){
						System.err.println("!!!!! Pump did not send response to EasyTIVA within "+MAX_WAIT);
						System.err.println("String read when looking for line is "+sb.toString());
						retrySendCommand(REMOTEENABLE);
						if(ioeDuringRead!=null) {
//							easyTivaLog.trace("exception during read", ioeDuringRead[0]);
						}
					}
					else {
						System.err.println( "Partial read from pump is : "+ sb.toString());
					}
				} catch (InterruptedException e) {
					System.err.println(" Interrupted after "+(System.currentTimeMillis()-l1));
				}
			}
			
		};
		t.setName("Pump Timeout Thread");
		t.start();
		
		// Read Char by char
		try {
			char c;
			//response[0] = fromDevice.readLine();
			while( (c=(char)fromDevice.read())!=-1 && c!='\r') {
				sb.append(c);
//				System.err.println(sb);
			}
			PUMPresponse=sb.toString();	
		} catch (IOException ioe) {
			ioeDuringRead[0] = ioe;
			ioe.printStackTrace();
		}
		t.interrupt();
		System.err.println("Response from Alaris is " + PUMPresponse);
		
//		String response = fromDevice.readLine();
//		System.err.println("REMOTE ENABLE command response is " + response);
	}
	
	private synchronized void pumpStatus() throws IOException {
		
		byte[] initBytes = INFSTATUS.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Attempting to write INF command");
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
		System.err.println("Reading for response - INF");
//		int bytesRead = 0;
//		
//		while(bytesRead <75 || responseBytes[bytesRead-6] !='|') {
//			System.err.println("Read with bytesRead " + bytesRead);
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
//			System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//			
//		}
//		
//		String response = new String (responseBytes,0,bytesRead);
		
		final StringBuilder sb=new StringBuilder();
		String PUMPresponse = null;
		
		// Timeout tracking Thread
		IOException[] ioeDuringRead=new IOException[1];
		Thread t = new Thread() {

			@Override
			public void run() {
				long l1=System.currentTimeMillis();
				try {
					
					sleep(MAX_WAIT);
					if (sb.length() == 0){
						System.err.println("!!!!! Pump did not send response to EasyTIVA within "+MAX_WAIT);
						System.err.println("String read when looking for line is "+sb.toString());
						retrySendCommand(INFSTATUS);
						if(ioeDuringRead!=null) {
//							easyTivaLog.trace("exception during read", ioeDuringRead[0]);
						}
					}
					else {
						System.err.println( "Partial read from pump is : "+ sb.toString());
					}
				} catch (InterruptedException e) {
					System.err.println(" Interrupted after "+(System.currentTimeMillis()-l1));
				}
			}
			
		};
		t.setName("Pump Timeout Thread");
		t.start();
		
		// Read Char by char
		try {
			char c;
			//response[0] = fromDevice.readLine();
			while( (c=(char)fromDevice.read())!=-1 && c!='\r') {
				sb.append(c);
//				System.err.println(sb);
			}
			PUMPresponse=sb.toString();	
		} catch (IOException ioe) {
			ioeDuringRead[0] = ioe;
			ioe.printStackTrace();
		}
		t.interrupt();
//		System.err.println("Response from Alaris is " + PUMPresponse);
		
		
//		String response = fromDevice.readLine();
		
		System.err.println("INF command response is " + PUMPresponse);
		String parts[] = PUMPresponse.split("\\^");
		
		InstSerial = parts [1];
		AlarmNotification = parts [2];
		InfMode = parts [3];
		InfRate = parts [4];
		InfRateUnit = parts [5];
		VolumeInf = parts [7];
		VolumeInfUnit = parts[8];
		Pressure = parts[9];
		PressureUnit = parts[10];
		InfTimeRemaining = parts[11];
		LogType = parts[12];
		LogEntryID = parts[13];
		
		String lastvals [] = LogEntryID.split("|");
		LogEntryID = lastvals[0];
		
	}
	
	private void queryVTBI() throws IOException{
		byte[] initBytes = VTBI.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("Attempting to query VTBI status");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[64];
//		int bytesRead = 0;
//
//		while(bytesRead <24 || responseBytes[bytesRead-6] !='|') {
////		while(bytesRead <35 || responseBytes[bytesRead-6] !='|') {
////			System.err.println("Read with bytesRead " + bytesRead);
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
////			System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//		}
//	
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("VTBI command response is " + response);
		String parts[] = response.split("\\^");
		
		float vtbi;
				
		if(parts[2].isEmpty()) {
			vtbi = 0;
		}
		else {
			vtbi =  Float.parseFloat(parts[2]);
		}
		
		currentVTBI = vtbi;
	}
	
	private void setVTBI(float VTBIvol) throws IOException {
		
		remoteEnable();
		
//		check if value is within limits
		
		if (VTBIvol > 50 || VTBIvol < 0 ) {
			System.err.println("Value of VTBI is out of limits. Rate must be 0 - 50 ml \n");
			return;
		}
		
//		format value to be set
		DecimalFormat df = new DecimalFormat("0.000");
		System.err.println("Asena setVTBI to " + df.format(VTBIvol));
		
		String command = "INF_VTBI^ACTIV^" + df.format(VTBIvol) + "^ml^STOP";
		System.err.println(command);
		String sendCommand = crc(command);
		System.err.println(sendCommand);
		
		byte[] initBytes = sendCommand.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("VTBI SET command written in setVTBI");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		int bytesRead = 0;
//		
//		while(bytesRead <35 || responseBytes[bytesRead-6] !='|') {
////			System.err.println("Read with bytesRead " + bytesRead);
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
////			System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//		}
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("VTBI SET command response is " + response);

	}
	
	private void checkAlarm() throws IOException {
	
	String sendCom = "ALARM";
	sendCom = crc(sendCom);
	
	byte[] initBytes = sendCom.getBytes();
	toDevice.write(initBytes);
	toDevice.flush();
	System.err.println("Attempting to query Alarm status");
	
	String response = fromDevice.readLine();

	System.err.println("ALARM command response is " + response);
	
	}
	
	private void deactivateVTBI() throws IOException {
		
		remoteEnable();
		
		byte[] initBytes = VTBIDEACTIVATE.getBytes();
		toDevice.write(initBytes);
		toDevice.flush();
		System.err.println("VTBI DEACTIVATE command written in deactivateVTBI");
		
		String response = fromDevice.readLine();
//		byte[] responseBytes = new byte[MAX_RESPONSE_LEN];
//		int bytesRead = 0;
//		
//		while(bytesRead <24 || responseBytes[bytesRead-6] !='|') {
//			System.err.println("Read with bytesRead " + bytesRead);
//			bytesRead += fromDevice.read(responseBytes, bytesRead, responseBytes.length - bytesRead);
//			System.err.println("Response is now " + new String(responseBytes,0,bytesRead));
//		}
//		String response = new String (responseBytes,0,bytesRead);
		System.err.println("VTBI DEACTIVATE command response is " + response);

	}
	
	private void retrySendCommand(String saveCommand) {
		
		try {
			byte[] initBytes = saveCommand.getBytes();
			toDevice.write(initBytes);
			toDevice.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println("Command rewrite attempt to AlarisGH - " + saveCommand);
		
	}
	
	public static String crc(String input) {
		final short initRegister = (short)0xffff;
		String message = input;
        byte[] messageBytes = message.getBytes();

        java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(messageBytes);
        short bitMask = (short)(1 << 15);

        // Process each message byte.
        int value = stream.read();
        short register = initRegister;
        while (value != -1) {
            byte element = (byte)value;

            register ^= ((short)element << 8);
            for (int i = 0; i < 8; i++) {
                if ((register & bitMask) != 0) {
                    register = (short)((register << 1) ^ 0x1021);
                }
                else {
                register <<= 1;
                }
            }
            value = stream.read();
        }

        // XOR the final register value.
        register ^= 0x0000;
        String hexValue = valueOf(register);
        hexValue = hexValue.toUpperCase();
        while(hexValue.length()!=4){
            hexValue = "0"+hexValue;
        }
        String fullCommand = "!" + message +"|" + hexValue + "\r";
        
        return fullCommand;
		
	}
	
	public static String valueOf(short number) {
        // Create a mask to isolate only the correct width of bits.
        long fullMask = (((1L << 15) - 1L) << 1) | 1L;
        return Long.toHexString(number & fullMask);
      }
	
	public void addListener() {
		
		/**
		 * Following block of code is for receiving objectives for the flow rate
		 */
		ice.FlowRateObjectiveTypeSupport.register_type(getParticipant(), ice.FlowRateObjectiveTypeSupport.get_type_name());
		flowRateTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.FlowRateObjectiveTopic.VALUE, ice.FlowRateObjectiveTypeSupport.class);
		flowRateReader = (ice.FlowRateObjectiveDataReader) subscriber.create_datareader_with_profile(flowRateTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);
		StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
        flowRateQueryCondition = flowRateReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(flowRateQueryCondition, new ConditionHandler() {
            private ice.FlowRateObjectiveSeq data_seq = new ice.FlowRateObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                        flowRateReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.FlowRateObjective data = (ice.FlowRateObjective) data_seq.get(i);
                            if (si.valid_data) {
                            	try { 
                            		setSpeed(data.newFlowRate);
                            	} catch (IOException ioe) {
                            		log.error("Failed to set pump speed", ioe);
                            		ioe.printStackTrace();
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        flowRateReader.return_loan(data_seq, info_seq);
                    }
                }
            }

        });
		
	}
	
	
	
	@Override
	public void disconnect() {
		
		byte[] initBytes = REMOTEDISABLE.getBytes();
		try {
			toDevice.write(initBytes);
			toDevice.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("REMOTE DISABLE command written in disconnect");
		
		
		super.disconnect();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		super.shutdown();
	}

	
	private final void addObjectiveListeners() {
//		addFlowRateListener();
		addPauseResumeListener();
		addProgramListener();
	}
	
	@SuppressWarnings("unused")
	private final void addFlowRateListener() {
		/**
		 * Following block of code is for receiving objectives for the flow rate
		 */
		ice.FlowRateObjectiveTypeSupport.register_type(getParticipant(), ice.FlowRateObjectiveTypeSupport.get_type_name());
		flowRateTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.FlowRateObjectiveTopic.VALUE, ice.FlowRateObjectiveTypeSupport.class);
		flowRateReader = (ice.FlowRateObjectiveDataReader) subscriber.create_datareader_with_profile(flowRateTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);
		StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
        flowRateQueryCondition = flowRateReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(flowRateQueryCondition, new ConditionHandler() {
            private ice.FlowRateObjectiveSeq data_seq = new ice.FlowRateObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                        flowRateReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.FlowRateObjective data = (ice.FlowRateObjective) data_seq.get(i);
                            if (si.valid_data) {
                            	try { 
                            		setSpeed(data.newFlowRate);
                            	} catch (IOException ioe) {
                            		log.error("Failed to set pump speed", ioe);
                            		ioe.printStackTrace();
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        flowRateReader.return_loan(data_seq, info_seq);
                    }
                }
            }
        });
	}
	
	private final void addPauseResumeListener() {
		/**
		 * Following block of code is for receiving objectives to pause resume.
		 */
		ice.InfusionObjectiveTypeSupport.register_type(getParticipant(), ice.InfusionObjectiveTypeSupport.get_type_name());
		pauseResumeTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.InfusionObjectiveTopic.VALUE, ice.InfusionObjectiveTypeSupport.class);
		pauseResumeReader = (ice.InfusionObjectiveDataReader) subscriber.create_datareader_with_profile(pauseResumeTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);
		StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
        pauseResumeQueryCondition = pauseResumeReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(pauseResumeQueryCondition, new ConditionHandler() {
            private ice.InfusionObjectiveSeq data_seq = new ice.InfusionObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                    	pauseResumeReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.InfusionObjective data = (ice.InfusionObjective) data_seq.get(i);
                            if (si.valid_data) {
                            	try { 
                            		if(data.stopInfusion) {
                            			pauseInfusion(); // removed argument

                            		} else {
                            			startInfusion(); // removed argument, changed to "startInfusion" to reflect Asena method
                            		}
                            	} catch (IOException ioe) {
                            		log.error("Failed to pause/resume pump", ioe);
                            		ioe.printStackTrace();
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        pauseResumeReader.return_loan(data_seq, info_seq);
                    }
                }
            }
        });
	}
	
	private final void addProgramListener() {
		/**
		 * Following block of code is for receiving objectives to pause resume.
		 */
		ice.InfusionProgramTypeSupport.register_type(getParticipant(), ice.InfusionProgramTypeSupport.get_type_name());
		programTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.InfusionProgramTopic.VALUE, ice.InfusionProgramTypeSupport.class);
		programReader = (ice.InfusionProgramDataReader) subscriber.create_datareader_with_profile(programTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);
		StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
        programQueryCondition = programReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(programQueryCondition, new ConditionHandler() {
            private ice.InfusionProgramSeq data_seq = new ice.InfusionProgramSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                    	programReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.InfusionProgram data = (ice.InfusionProgram) data_seq.get(i);
                            if (si.valid_data) {
                            	try { 
                            		programPump(data);
                            	} catch (IOException ioe) {
                            		log.error("Failed to program pump", ioe);
                            		ioe.printStackTrace();
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        programReader.return_loan(data_seq, info_seq);
                    }
                }
            }
        });
	}
	
	
	private synchronized void programPump(InfusionProgram program) throws IOException {
		
		if(program.infusionRate > 0) {
			setPumpSpeed = program.infusionRate;
			setVTBIvol = program.VTBI;
			pendingWriteSpeed = true;
		}
		
		if( program.bolusRate > 0 ) {
			setBolusSpeed = program.bolusRate;
			setBolusVol = program.bolusVolume;
			pendingWriteBolus = true;
		}
		
	}
	
	@Override
    protected String iconResourceName() {
        return "alaris_asena_pump.png";
    }
	
	private String getRemoteControlCmd(String serial) {
		switch (serial) {
		case "8002-51740":
			return "!REMOTE_CTRL^ENABLED^3CC1|B1F1\r";
		case "8002-51733":
			return "!REMOTE_CTRL^ENABLED^9535|FB2F\r";

		default:
			break;
		}
		
		//8002-51740 = !REMOTE_CTRL^ENABLED^3CC1|B1F1
		//8002-51733 = !REMOTE_CTRL^ENABLED^9535|FB2F
		return "";
	}
	
}
        

