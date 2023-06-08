package org.mdpnp.devices.vitalsbridge;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModel;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModelImpl;
import org.mdpnp.rtiapi.data.GlobalSimulationObjectiveInstanceModelListener;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataReader;

public class VitalsBridgeDevice extends AbstractSerialDevice implements GlobalSimulationObjectiveInstanceModelListener {
	
	private static final byte SOT=0x02;
	private static final byte EOT=0x03;
	
	private static String genericVitalSignChange="{\"MessageType\":\"VitalsData\",\"Message\":{\"hr\":-1,\"sbp\":-1,\"dbp\":-1"
			+ ",\"mbp\":-1,\"sPAP\":-1,\"dPAP\":-1"
			+ ",\"mPAP\":-1,\"CVP\":-1,\"AUXP\":-1"
			+ ",\"t1\":-1,\"t2\":-1,\"rr\":-1,\"etCO2\":-1"
			+ ",\"co2FlowFactor\":-1,\"spO2\":-1,\"spO2Ratio\":-1"
			+ ",\"spo2Override\":\"None\",\"abpZeroState\":\"Unknown\",\"papZeroState\":\"Unknown\""
			+ ",\"cvpZeroState\":\"Unknown\",\"auxZeroState\":\"Unknown\""
			+ "}}";
	
	private BufferedInputStream is;
	private OutputStream os;
	
	private boolean pleaseStop;
	
	Thread deviceStatusThread;
	
	private static final HashMap<String, String> metricsMap=new HashMap<>();
	
	/**
	 * An indicator of whether we have received and parsed a status message from the device.
	 * We do this in doInitCommands, and once we've done that, we set this to true.  This means
	 * that the code in {@link #process(int, InputStream, OutputStream)} can do whatever it wants,
	 * because we consider ourselves connected.
	 */
	private boolean gotStatus;
	
	/**
     * A list of ECG rhythm values that are known to VitalsBridge.  We keep these in an ArrayList, so that when one
     * is specified in the script, we can look up the index for it, and then publish the index as a numeric metric.
     * The VitalsBridge receiving device can reverse the process to revert back to the String value that is required
     * in order to publish the proprietary command to the VitalsBridge.<br/><br/>
     * 
     * Needless to say, that requires that the VitalsBridge has exactly the same order of elements.  Needless to say,
     * that means that these should be stored in a common class.  Needless to say, they are not.  Yet.
     */
    private ArrayList<String> vitalsBridgeECG=new ArrayList<>();
    
    /**
     * The special metric for ecg rhythm commands.
     */
    private String VB_ECG_METRIC="VB_ECG_RHYTHM";
	
	protected final GlobalSimulationObjectiveInstanceModel monitor;

	public VitalsBridgeDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		this(subscriber, publisher, eventLoop, 1);
	}

	public VitalsBridgeDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop, int countSerialPorts) {
		super(subscriber, publisher, eventLoop, countSerialPorts);
		deviceIdentity.manufacturer="Vitals Bridge";
		deviceIdentity.model="VitalsBridge";
		deviceIdentity.operating_system="3.4.3.3";
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		writeDeviceIdentity();
		
		monitor = new GlobalSimulationObjectiveInstanceModelImpl(ice.GlobalSimulationObjectiveTopic.VALUE);
        monitor.addListener(this);
        monitor.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        
        populateMetrics();
        populateVitalsBridgeMetrics();
	}
	
	private void populateMetrics() {
		metricsMap.put(rosetta.MDC_PULS_RATE.VALUE, "\"hr\"");
		metricsMap.put(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "\"spO2\"");
		metricsMap.put(rosetta.MDC_RESP_RATE.VALUE, "\"rr\"");
		metricsMap.put("VB_PULSE_RATE", "\"hr\"");
		metricsMap.put("VB_SBP", "\"sbp\"");
		metricsMap.put("VB_DBP", "\"dbp\"");
		metricsMap.put("VB_MBP", "\"mbp\"");
		
		//ETCO2, TEMPERATURE, ECG RHYTHM
	}
	
    private final void populateVitalsBridgeMetrics() {
    	String[] vitals=new String[] {
    			"Ignore",
    			"AFib",
    			"Aflutter4to1",
    			"Aflutter3to1",
    			"Aflutter2to1",
    			"Asystole",
    			"AVBlock1stDegree",
    			"AVBlock2ndDegreeType1_3to2",
    			"AVBlock2ndDegreeType1_4to3",
    			"AVBlock2ndDegreeType1_5to4",
    			"AVBlock2ndDegreeType2_3to2",
    			"AVBlock2ndDegreeType2_4to3",
    			"AVBlock2ndDegreeType2_5to4",
    			"AVBlock3rdDegree",
    			"BundleBranchBlockRight",
    			"BundleBranchBlockLeft",
    			"EctopicAtrial",
    			"HyperkalemiaBase",
    			"HyperkalemiaMild",
    			"HyperkalemiaModerate",
    			"HyperkalemiaSevere",
    			"Idioventricular",
    			"LVH_1",
    			"LVH_2",
    			"LVHStressed",
    			"NormalSinus",
    			"Paced1",
    			"Paced2",
    			"STElevationInferiorAMIBaseline",
    			"STElevationInferiorAMIMild",
    			"STElevationInferiorAMIModerate",
    			"STElevationInferiorAMISevere",
    			"STElevationAnteriorAMIBaseline",
    			"STElevationAnteriorAMIMild",
    			"STElevationAnteriorAMIMModerate",
    			"STElevationAnteriorAMISevere",
    			"STElevationAnteriorAMILate",
    			"STDepressionIschemia",
    			"STDepressionPostIschemia",
    			"TorsadeDePointes",
    			"VFib",
    			"VentricularStandstill"
    	};
    	vitalsBridgeECG.addAll(Arrays.asList(vitals));
    }

	@Override
	protected void doInitCommands(int idx) throws IOException {
		if(deviceStatusThread==null) {
			deviceStatusThread=new Thread() {
				@Override
				public void run() {
					/*
					 * All responses have a 5 byte header - the SOT byte, then the four byte length indicator.
					 * So we use a separate 5 byte array for those, meaning we can just call a blocking read
					 * for the whole array without needing offset or length arguments. 
					 */
					byte recvHeaderBytes[]=new byte[5];
					/*
					 * Per the docs, the length indicator is 4 bytes long, so the max response length
					 * is 9999. We allow one additional byte for the EOT byte, and that means that 10000
					 * bytes is perfect. (even though it's not 2^something)
					 */
					byte recvJsonBytes[]=new byte[10000];
					while(!pleaseStop) {
						synchronized (os) {
							String statusRequestString=getStatusRequestString();
							byte sendBytes[]=wrapRequestString(statusRequestString);
							
							try {
								os.write(sendBytes);
								os.flush();
								//A status request should produce 3 replies, that we read separately.
								for(int i=0;i<3;i++) {
									/*
									 * All response messages start with an SOT then a four byte length indicator.
									 * So we read 5 bytes, decode that and then check how much more to read using
									 * that length. 
									 */
									
									int bytesRead=is.read(recvHeaderBytes);
									int fullResponseLength=-1;
									int recvdSoFar=-1;
									if(bytesRead==5) {
										System.err.println("Received "+bytesRead+" from device");
										System.err.println(ArrayUtils.toString(recvHeaderBytes));
										if(recvHeaderBytes[0]!=SOT) {
											String whatIsThis=new String(recvHeaderBytes);
											System.err.println("Unexpected bytes as string are "+whatIsThis);
											//throw new IOException("Unexpected start byte in response from VitalsBridge");
											/*
											 * What seems to happen here is that we miss the 0x02 SOT byte.  Instead, try and make
											 * the responseLength from the first N numeric bytes, and push the remaining chars onto the start
											 * of the JSON array
											 */
											int numCheck=0;
											for(;numCheck<5;numCheck++) {
												/*
												 * We know we have read 5 bytes here.  Scan them for anything that is a number.  A number is a byte between 48 (0) and 57 (9) inclusive.
												 * Use all bytes that are numbers to make the fullResponseLength.  Then push all the rest of them into the recvJsonBytes array, and set
												 * recvdSoFar to match the number of bytes pushed. 
												 */
												if(recvHeaderBytes[numCheck]<48 || recvHeaderBytes[numCheck]>57) {
													System.err.println("Breaking on "+numCheck+" as byte value is "+recvHeaderBytes[numCheck]);
													break;
												}
												
											}
											if(numCheck!=0) {
												String testString=new String(recvHeaderBytes,0,numCheck);
												fullResponseLength=Integer.parseInt(testString);
												System.err.println("Hacked fullResponseLength is "+fullResponseLength);
												System.arraycopy(recvHeaderBytes, numCheck, recvJsonBytes, 0, 5-numCheck);
												System.err.println("initial JSON is "+new String(recvJsonBytes,0,5-numCheck));
												recvdSoFar=5-numCheck;	
											} else {
												System.err.println("VitalsBridge Protocol out of sync - did not get any number to try and recover");
											}
											
											//System.err.printf("Unexpected start byte in response from VitalsBridge %x\n",recvHeaderBytes[0]);
										} else {
											fullResponseLength=Integer.parseInt(new String(recvHeaderBytes,1,4));
											recvdSoFar=0;
										}
										
										fullResponseLength++;	//Add one to ensure we read the closing EOT byte as well.
										System.err.println("Response length is "+fullResponseLength);
										//We read fullResponseLength+1 to get the closing SOT byte
										
										while(recvdSoFar<fullResponseLength) {
											recvdSoFar+=is.read(recvJsonBytes, recvdSoFar, fullResponseLength-recvdSoFar);
											System.err.println("recvdSoFar is "+recvdSoFar);
										}
										//System.err.println(ArrayUtils.toString(recvJsonBytes));
										
										if(recvJsonBytes[fullResponseLength-1]!=EOT) {
											//System.err.printf("Closing byte is %x\n",recvJsonBytes[fullResponseLength+1]);
											throw new IOException("Unexpected end byte in response from VitalsBridge");
										}
										//We trim off the EOT byte whilst making the string.
										String jsonResponse=new String(recvJsonBytes,0,fullResponseLength-1);
										System.err.println("response "+i+" is "+jsonResponse);
										gotStatus=true;
										if(!deviceConnectivity.state.equals(ice.ConnectionState.Connected)) {
											reportConnected("Connected to VitalsBridge - received status messages");
										}
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try {
							sleep(5000);//For now...
						} catch (InterruptedException ie) {
							if(pleaseStop) {
								String disconnectString=getDisconnectString();
								byte sendBytes[]=wrapRequestString(disconnectString);
								
								try {
									os.write(sendBytes);
									os.flush();
									System.err.println("Sent the disconnect message to VitalsBridge");
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}
								return;
							}
						}
					}
				}
			};
			deviceStatusThread.start();
		}

	}
	
	/**
	 * Format the request string as a byte array, according to the required specs from VitalsBridge,
	 * i.e. SOT byte, 4 byte length field for the length of request, then EOT.
	 * @param request
	 * @return
	 */
	private byte[] wrapRequestString(String request) {
		int strlen=request.length();		//Get length of request
		byte ret[]=new byte[strlen+6];		//final byte array is 6 bytes longer
		ret[0]=SOT;							//first byte is SOT
		System.arraycopy(String.format("%04d", strlen).getBytes(), 0, ret, 1, 4);	//Next four bytes are the int length
		System.arraycopy(request.getBytes(), 0, ret, 5, strlen);					//Copy in the actual string bytes
		ret[ret.length-1]=EOT;				//Terminate with EOR
		//System.err.println("wrapRequestString returning "+ArrayUtils.toString(ret));
		return ret;
	}
	
	private static String getStatusRequestString() {
		return "{\"MessageType\":\"StatusRequest\",\"Message\":{\"requestNetworkStatus\":\"true\"}}";
	}
	
	private static String getDisconnectString() {
		return "{\"MessageType\":\"Disconnect\",\"Message\":{\"disconnect\":true}}";	//NOTE: no double quotes round the true
	}
	

	@Override
	protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
		/*
		 * We don't do anything in here, becasue there aren't really any data sent back from the device that
		 * we want to make use of, apart from the ongoing status packets, and those are dealt with by the deviceStatusThread.
		 */
		this.is=new BufferedInputStream(inputStream);
		this.os=outputStream;
		while(!pleaseStop) {
			if(gotStatus) {
				readDevicePackets();
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	@Override
	public SerialProvider getSerialProvider(int idx) {
		// TODO Auto-generated method stub
		SerialProvider serialProvider=super.getSerialProvider(idx);
		serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
		
		return serialProvider;
	}
	
	
	
	@Override
	protected long getMaximumQuietTime(int idx) {
		// TODO Auto-generated method stub
		return 15000L;
	}

//	@Override
//	public void shutdown() {
//		// TODO Auto-generated method stub
//		pleaseStop=true;
//		deviceStatusThread.interrupt();
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException ie) {
//			ie.printStackTrace();
//		}
//		
//	}
	
	
	
	@Override
	public void disconnect() {
		monitor.stopReader();		//Stop responding to changes.
		pleaseStop=true;
		if(deviceStatusThread!=null) {
			deviceStatusThread.interrupt();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		super.disconnect();
	}
	
	@Override
	protected String iconResourceName() {
		// TODO Auto-generated method stub
		return "VitalsBridge-Logo.png";
	}

	private void readDevicePackets() {
		while(true) {
			
		}
	}

	/*
	 * Next three methods are GlobalSimulationObjectiveInstanceModelListener
	 */
	@Override
	public void instanceAlive(ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
			GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective data, SampleInfo sampleInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void instanceNotAlive(
			ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
			GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective keyHolder, SampleInfo sampleInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void instanceSample(
			ReaderInstanceModel<GlobalSimulationObjective, GlobalSimulationObjectiveDataReader> model,
			GlobalSimulationObjectiveDataReader reader, GlobalSimulationObjective data, SampleInfo sampleInfo) {
		System.err.println("VitalsBridgeDevice - instanceSample called with metric "+data.metric_id);
		if(sampleInfo.valid_data) {
//			if (rosetta.MDC_PULS_RATE.VALUE.equals(data.metric_id)) {
//				setPulseRate( GlobalSimulationObjectiveListener.toDoubleNumber(data) );
//            }
			processMetricIfKnown(data);
		}
		
	}
	
	/**
	 * This method looks up the metric in the lookup table we have for metrics that we know how to set on the device.
	 * If the metric is found, we retrieve the corresponding entry in the generic "set a parameter" string, and replace it.
	 * We then send the configured parameter setting string to the device.
	 *  
	 * @param data - the GlobalSimulationObjective that contains both the metric_id and the value (data)
	 */
	private void processMetricIfKnown(GlobalSimulationObjective data) {
		if(metricsMap.containsKey(data.metric_id)) {
			//Get the String we want to replace.
			String vitalsBridgeKey=metricsMap.get(data.metric_id);
			String replaceThis=vitalsBridgeKey+":-1";
			String replaceWith=vitalsBridgeKey+":"+GlobalSimulationObjectiveListener.toDoubleNumber(data).intValue();
			String finalToSend=genericVitalSignChange.replace(replaceThis, replaceWith);
			System.err.println("setter string is "+finalToSend);
			byte setHRBytes[]=wrapRequestString(finalToSend);
			synchronized (os) {
				try {
					os.write(setHRBytes);
					os.flush();
					System.err.println("sent the setter string");
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		if(VB_ECG_METRIC.equals(data.metric_id)) {
			try {
				String rhythm=vitalsBridgeECG.get( (int) data.value );
				//TODO: Check that extraSystoleType:None and extraSystoleFreq:0 are suitable.
				String rhythmCommand="{\"MessageType\":\"ECGAutoRhythm\",\"Message\":{\"ecgAutoRhythm\":\"" + rhythm + "\",\"extraSystoleType\":\"None\",\"extraSystoleFreq\":0}}";
				byte rhythmBytes[]=wrapRequestString(rhythmCommand);
				synchronized (os) {
					try {
						os.write(rhythmBytes);
						os.flush();
						System.err.println("sent the setter string for rhythm "+rhythmCommand);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			} catch(ArrayIndexOutOfBoundsException bounds) {
				System.err.println("Unknown rhythm requested for VitalsBridge with index "+(int)data.value);
			}
		}
	}
	
	/*
	private void setPulseRate(Number newPulseRate) {
		String pulseRate="{\"MessageType\":\"VitalsData\",\"Message\":{\"hr\":"+newPulseRate.intValue()+",\"sbp\":-1,\"dbp\":-1"
				+ ",\"mbp\":-1,\"sPAP\":-1,\"dPAP\":-1"
				+ ",\"mPAP\":-1,\"CVP\":-1,\"AUXP\":-1"
				+ ",\"t1\":-1,\"t2\":-1,\"rr\":-1,\"etCO2\":-1"
				+ ",\"co2FlowFactor\":-1,\"spO2\":-1,\"spO2Ratio\":-1"
				+ ",\"spo2Override\":\"None\",\"abpZeroState\":\"Unknown\",\"papZeroState\":\"Unknown\""
				+ ",\"cvpZeroState\":\"Unknown\",\"auxZeroState\":\"Unknown\""
				+ "}}";
		System.err.println("setPulseRate string is "+pulseRate);
		byte setHRBytes[]=wrapRequestString(pulseRate);
		synchronized (os) {
			try {
				os.write(setHRBytes);
				os.flush();
				System.err.println("sent the pulseRate string");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	*/
	
}	
	
	/*
{"MessageTy
pe":"VitalsData"
,"Message":{"hr"
:-1,"sbp":-1,"db
p":-1,"mbp":-1,"
sPAP":-1,"dPAP":
-1,"mPAP":-1,"CV
 50 22 3A 2D 31 2E 30 2C 22 41 55 58 50 22 3A 2D   P":-1.0,"AUXP":-
 31 2C 22 74 31 22 3A 2D 31 2E 30 2C 22 74 32 22   1,"t1":-1.0,"t2"
 3A 2D 31 2E 30 2C 22 72 72 22 3A 2D 31 2C 22 65   :-1.0,"rr":-1,"e
 74 43 4F 32 22 3A 33 35 2C 22 63 6F 32 46 6C 6F   tCO2":35,"co2Flo
 77 46 61 63 74 6F 72 22 3A 2D 31 2C 22 73 70 4F   wFactor":-1,"spO
 32 22 3A 2D 31 2C 22 73 70 4F 32 52 61 74 69 6F   2":-1,"spO2Ratio
 22 3A 2D 31 2C 22 73 70 6F 32 4F 76 65 72 72 69   ":-1,"spo2Overri
 64 65 22 3A 22 49 67 6E 6F 72 65 22 2C 22 61 62   de":"Ignore","ab
 70 5A 65 72 6F 53 74 61 74 65 22 3A 22 55 6E 6B   pZeroState":"Unk
 6E 6F 77 6E 22 2C 22 70 61 70 5A 65 72 6F 53 74   nown","papZeroSt
 61 74 65 22 3A 22 55 6E 6B 6E 6F 77 6E 22 2C 22   ate":"Unknown","
 63 76 70 5A 65 72 6F 53 74 61 74 65 22 3A 22 55   cvpZeroState":"U
 6E 6B 6E 6F 77 6E 22 2C 22 61 75 78 5A 65 72 6F   nknown","auxZero
 53 74 61 74 65 22 3A 22 55 6E 6B 6E 6F 77 6E 22   State":"Unknown"
 7D 7D 03 02 30 33 33 33 7B 22 4D 65 73 73 61 67   }}..0333{"Messag
 65 54 79 70 65 22 3A 22 56 69 74 61 6C 73 44 61   eType":"VitalsDa
 74 61 22 2C 22 4D 65 73 73 61 67 65 22 3A 7B 22   ta","Message":{"
 68 72 22 3A 2D 31 2C 22 73 62 70 22 3A 2D 31 2C   hr":-1,"sbp":-1,
 22 64 62 70 22 3A 2D 31 2C 22 6D 62 70 22 3A 2D   "dbp":-1,"mbp":-
 31 2C 22 73 50 41 50 22 3A 2D 31 2C 22 64 50 41   1,"sPAP":-1,"dPA
 50 22 3A 2D 31 2C 22 6D 50 41 50 22 3A 2D 31 2C   P":-1,"mPAP":-1,
 22 43 56 50 22 3A 2D 31 2E 30 2C 22 41 55 58 50   "CVP":-1.0,"AUXP
 22 3A 2D 31 2C 22 74 31 22 3A 2D 31 2E 30 2C 22   ":-1,"t1":-1.0,"
 74 32 22 3A 2D 31 2E 30 2C 22 72 72 22 3A 2D 31   t2":-1.0,"rr":-1
 2C 22 65 74 43 4F 32 22 3A 33 35 2C 22 63 6F 32   ,"etCO2":35,"co2
 46 6C 6F 77 46 61 63 74 6F 72 22 3A 2D 31 2C 22   FlowFactor":-1,"
 73 70 4F 32 22 3A 2D 31 2C 22 73 70 4F 32 52 61   spO2":-1,"spO2Ra
 74 69 6F 22 3A 2D 31 2C 22 73 70 6F 32 4F 76 65   tio":-1,"spo2Ove
 72 72 69 64 65 22 3A 22 49 67 6E 6F 72 65 22 2C   rride":"Ignore",
 22 61 62 70 5A 65 72 6F 53 74 61 74 65 22 3A 22   "abpZeroState":"
 55 6E 6B 6E 6F 77 6E 22 2C 22 70 61 70 5A 65 72   Unknown","papZer
 6F 53 74 61 74 65 22 3A 22 55 6E 6B 6E 6F 77 6E   oState":"Unknown
 22 2C 22 63 76 70 5A 65 72 6F 53 74 61 74 65 22   ","cvpZeroState"
 3A 22 55 6E 6B 6E 6F 77 6E 22 2C 22 61 75 78 5A   :"Unknown","auxZ
 65 72 6F 53 74 61 74 65 22 3A 22 55 6E 6B 6E 6F   eroState":"Unkno
 77 6E 22 7D 7D                                    wn"}}
*/

