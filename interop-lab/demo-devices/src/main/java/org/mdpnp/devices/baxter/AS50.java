package org.mdpnp.devices.baxter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.DeviceClock.Reading;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
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

import ice.InfusionProgram;
import ice.InfusionProgramDataReader;
import ice.Numeric;

public class AS50 extends AbstractSerialDevice {
	
	private BufferedInputStream fromDevice;
	private BufferedOutputStream toDevice;
	private boolean initDone;
	
	/**
	 * Device query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] DEV_CMD_BYTES= new byte[]{'[', 'D', 'E', 'V', '?', ']', -117 , 28 };
	/**
	 * Rate query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] PRIRATE_QRY_BYTES=new byte[]{'[','P', 'R', 'I', 'R', 'A', 'T', 'E', '?', ']', 52 , 68 };
	
	/**
	 * Bolus delivery query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] BOLRATE_QRY_BYTES=new byte[]{'[', 'B', 'O', 'L', 'R', 'A', 'T', '?', ']', 18 , 120 };
	
	/**
	 * Bolus size query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] BOLSIZE_QRY_BYTES=new byte[]{'[', 'B', 'O', 'L', 'S', 'I', 'Z', '?', ']', 38 , -21 };
	
	/**
	 * Exit command to end control, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] EXIT_CMD_BYTES=new byte[]{'[', 'E', 'X', 'I', 'T', ']', -69 , -18 };
	
	/**
	 * Bolus size query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] LBOLUS_QRY_BYTES=new byte[]{'[', 'L', 'B', 'O', 'L', 'U', 'S', '?', ']', 55 , -73 };
	
	/**
	 * Status query command, including hard coded checksum to avoid needing to calculate the checksum each time
	 */
	private static final byte[] STATUS_QRY_BYTES=new byte[]{'[', 'S', 'T', 'A', 'T', 'U', 'S', '?', ']', -61 , 75 };
	
	private static final String ACK="[ACK]";
	
	private InstanceHolder<Numeric> flowRateHolder;
	
	private DeviceClock defaultClock;
	
	private InfusionProgramDataReader programReader;
	private Topic programTopic;
	private QueryCondition programQueryCondition;
	
	private static final Logger log = LoggerFactory.getLogger(AS50.class);

	public AS50(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		this(subscriber, publisher, eventLoop,1);
		// TODO Auto-generated constructor stub
	}
	
	public AS50(Subscriber subscriber, Publisher publisher, EventLoop eventLoop, int countSerialPorts) {
		super(subscriber, publisher, eventLoop, countSerialPorts);
		deviceIdentity.manufacturer = "Baxter";
		deviceIdentity.model = "AS50";
		deviceIdentity.operating_system="";
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		super.writeDeviceIdentity();
		addProgramListener();
		defaultClock=new DeviceClock.WallClock();
	}
	
	/**
	 * We don't seem to be able to use readAllBytes on the input stream
	 * as the device just hangs when that happens.  Instead we have to read
	 * byte by byte until we see the ']' character that represents the end
	 * of all the different responses that can happen.  So we do that in this
	 * one method.
	 * @return
	 */
	private String getResponse() throws IOException {
		ByteBuffer bb=ByteBuffer.allocate(64);
		
		int i=0;
		while( (i=fromDevice.read()) != 93) {
			log.info("got byte "+i);
			bb.put((byte)i);
		}
		bb.put((byte)i);
		byte[] crc=new byte[2];
		fromDevice.read(crc);
		//Am I missing the method in ByteBuffer that just returns the bytes actually put rather than the full allocated but unused array?
		String responseToCheck=new String(bb.array()).substring(0,bb.position());
		System.err.println("getResponse responseToCheck is "+responseToCheck);

		byte[] compareCRC=crc(responseToCheck);
		if(compareCRC[0]!=crc[0] || compareCRC[1]!=crc[1]) {
			log.error("Checksum failed {} {} {} {}", crc[0], crc[1], compareCRC[0], compareCRC[1]);
			throw new IOException("Checksum failure for response "+responseToCheck);
		}
		return responseToCheck;
	}

	@Override
	protected void doInitCommands(int idx) throws IOException {
		initDone=false;
		log.info("AS50 doInitCommands");
		/*
		 * There's no specific initialisation required.  We just do this to establish that the pump
		 * is at the other end of the wire and talking to us.
		 */
		String response=doStatusQry();
		
		reportConnected("Got STATUS_QRY reply from pump - "+response);
		log.info("reportConnected after STATUS_QRY");
		initDone=true;

	}
	
	private synchronized String doStatusQry() throws IOException {
		toDevice.write(STATUS_QRY_BYTES);
		toDevice.flush();
		
		log.info("Flushed STATUS_QRY");
		
		String response=getResponse();
		System.err.println("STATUS_QRY response is "+response);
		log.info("DEV_CMD response is "+response);
		
		return response;
	}

	@Override
	protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
		fromDevice=new BufferedInputStream(inputStream);
		toDevice=new BufferedOutputStream(outputStream);
		while( ! initDone ) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//int zzz=0;
		while(true) {
			getPrimaryRate();
			//Test to set speed.
//			if(zzz++==5) {
//				try {
//					Thread.sleep(1000);
//					System.err.println("Calling programPump2...");
//					programPump2(5f);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException ie) {
				//blah
			}
		}
	}
	
	private synchronized void getPrimaryRate() throws IOException {
		toDevice.write(PRIRATE_QRY_BYTES);
		toDevice.flush();
		log.info("Flushed PRIRATE_QRY_BYTES");
		System.err.println("Send PRIRATE_QRY_BYTES");
		
		String priRateResponse=getResponse();
		System.err.println("PRIRATE response is "+priRateResponse);
		log.info("PRIRATE response is "+priRateResponse);
		processPriRateResponse(priRateResponse);
	}
	
	private synchronized void processPriRateResponse(String priRateResponse) {
		String trimmed=priRateResponse.substring(priRateResponse.indexOf('[')+1,priRateResponse.lastIndexOf(']'));
		if(trimmed.startsWith("REJECT")) {
			log.warn("PRIRATE QUERY WAS REJECTED WITH "+trimmed);
			//TODO: Throw an exception or anything?
			return;	//Can't publish a flow rate.
		}
		String flowRateStr=trimmed.substring(trimmed.indexOf('=')+1);
		float flowRate=Float.parseFloat(flowRateStr);
		Reading r=defaultClock.instant();
		numericSample(flowRateHolder, flowRate, rosetta.MDC_FLOW_FLUID_PUMP.VALUE, "", rosetta.MDC_FLOW_FLUID_PUMP.VALUE, new DeviceClock.CombinedReading(r, r));
	}
	
	private final void addProgramListener() {
		/**
		 * Following block of code is for receiving objectives to program the pump.
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
	
	private void programPump(InfusionProgram data) throws IOException {
		if(data.infusionRate!=-1) {
			//Set the PRIRATE...
			setPriRate(data);
		}
		if(data.VTBI!=-1) {
			setVTBI(data);
		}
		if(data.bolusVolume!=-1) {
			setBolusSize(data);
		}
		if(data.bolusRate!=-1) {
			//setBolusRate(data);
			log.info("Boluse rate cannot be set");
			System.err.println("Bolus rate cannot be set");
		}
	}
	
	/**
	 * Set the primary flow rate
	 * 
	 * @param data The infusion programm
	 * @throws IOException
	 */
	private synchronized void setPriRate(InfusionProgram data) throws IOException {
		String cmd="[PRIRATE="+data.infusionRate+"]";
		String priRateResponse=sendWithRetry(cmd);
		System.err.println("Got response "+priRateResponse);
	}
	
	private synchronized void setVTBI(InfusionProgram data) throws IOException {
		String cmd="[PRIVTBI="+data.VTBI+"]";
		String priVTBIResponse=sendWithRetry(cmd);
		System.err.println("Got response "+priVTBIResponse);
	}
	
	private synchronized void setBolusSize(InfusionProgram data) throws IOException {
		String cmd="[BOLSIZ="+data.bolusVolume+"]";
		String bolSIZResponse=sendWithRetry(cmd);
		System.err.println("Got response "+bolSIZResponse);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			//
		}
		
		cmd="[BOLST]";
		String bolStartResponse=sendWithRetry(cmd);
		System.err.println("Got response "+bolStartResponse);
	}
	
	/*
	private synchronized void setBolusRate(InfusionProgram data) throws IOException {
		String cmd="[BOLRAT="+data.bolusRate+"]";
		String bolRATResponse=sendWithRetry(cmd);
		System.err.println("Got response "+bolRATResponse);
	}
	*/
	
	private String sendWithRetry(String cmd) throws IOException {
		byte[] crc=crc(cmd);
		toDevice.write(cmd.getBytes());
		String response="";
		int count=0;
		//Arbitrary count of 5 retries.
		while( ! response.equals(ACK) && count++<5) {
			toDevice.write(crc);
			toDevice.flush();
			System.err.println("Sent "+cmd+" and waiting for response");
			
			response=getResponse();
			System.err.println("Got response "+response);
			if(response.startsWith("[REJECT")) {
				break;	//TODO: throw new IOException("Rejected");
			}
		}
		return response;
	}
	
	
	private void programPump2(float f) throws IOException {
		String cmd="[PRIRATE="+f+"]";
		byte[] crc=crc(cmd);
		toDevice.write(cmd.getBytes());
		toDevice.write(crc);
		toDevice.flush();
		System.err.println("Sent "+cmd+" and waiting for response");
		
		System.err.println("Got response "+getResponse());
	}
	
	
	/**
	 * Calculate a CRC for the input string according to the example provided in the Baxter documentation.
	 * 
	 * @param str the string to make the CRC for
	 * 
	 * @return a pair of bytes representing the CRC
	 */
	private static byte[] crc(String str) {
		
		/*
		 * [RESEND]
		 * should have the CRC values 219 107
		 * 
		 * 219 - 1101 1011
		 * 107 - 0110 1011
		 * 
		 */
		
		int CRC=0;
		
		int generatingPolynomial=0x1021;
		int msb=0x8000;
		
		int len=str.length();
		
		byte[] bytes=str.getBytes();
		
		int b;
		
		for(int off=0;off<len;off++) {
			b=Byte.toUnsignedInt(bytes[off]);
			b=b ^ (CRC>>8);
			b=b << 8;
			
			int generator=0;
			for(int j=0;j<8;j++) {
				if( ((b ^ generator) & msb) == msb) {
					generator=(generator << 1) ^ generatingPolynomial;
				} else {
					generator=(generator << 1);
				}
				b=b<<1;
			}
			
			CRC= (CRC<<8) ^ generator;
			
			
		}
		System.err.println(Integer.toBinaryString(CRC));
		byte first=(byte)(CRC&0xff);
		CRC=CRC>>8;
		byte second=(byte)(CRC&0xff);
		System.err.println("CRC for "+str+" is "+first+" "+second);
		return new byte[] { second, first };
	}

	@Override
	protected String iconResourceName() {
		return "baxter-as-50.jpg";
	}

	@Override
	protected long getMaximumQuietTime(int idx) {
		// TODO Auto-generated method stub
		return 30_000L;
	}

	@Override
	public SerialProvider getSerialProvider(int idx) {
		// TODO Auto-generated method stub
		SerialProvider provider=super.getSerialProvider(idx);
		provider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
		return provider;
	}
	
	
	
	
	
	public static void main(String args[]) {
		String dev="[STATUS?]";
		byte[] crc=crc(dev);
		System.out.print("new byte[]{");
		for(int i=0;i<dev.length();i++) {
			System.out.print("'"+dev.charAt(i)+"', ");
		}
		System.out.print(crc[0]+" , "+crc[1]+" }");
//		System.out.println("crc is "+crc);
//		crc=crc(dev);
//		System.out.println("crc is "+crc);
		
	}

}
