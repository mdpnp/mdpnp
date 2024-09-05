package org.mdpnp.apps.testapp.alaris;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceIdentityBuilder;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

import ice.MDSConnectivity;
import ice.Numeric;
import ice.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SingleSimAlaris {
	
	@FXML
	ComboBox<String> serialPortsCombo;
	@FXML
	ComboBox<String> serialNumCombo;
	@FXML
	TextField responseDelayField;
	@FXML
	TextField rampingSpeedField;
	@FXML
	CheckBox infAccEnabled;
	@FXML
	TextField infAccAmplitude;
	@FXML
	TextField infAccStdDev;
	
	TextArea loggingArea;
	
	private DeviceDriverProvider.DeviceAdapter simAlarisDeviceAdapter;
	
	private SimulatedAlarisDevice simAlarisDevice;
	
	private EventLoop eventLoop;
	
	private Subscriber subscriber;
	
	private ApplicationContext parentContext;
	
	private Patient currentPatient;
	
	private MDSHandler mdsHandler;
	
	/**
	 * A reader to read lines from the requesting device, e.g. EasyTIVA
	 */
	private BufferedReader fromRequestor;
	
	/**
	 * An output stream to send bytes to the requesting device, e.g. EasyTIVA
	 */
	private BufferedOutputStream toRequestor;
	
	/**
	 * Last flow rate requested by the client.
	 */
	private float lastRequestedRate=0.1f;
	
	/**
	 * The volume infused.  Can be reset by VI_CLEAR command;
	 */
	private float volumeInfused=0;
	
	/**
	 * Instance holder to allow the flow rate to be published.
	 */
	private InstanceHolder<Numeric> flowRateHolder;
	
	private static final Logger easyTivaLog = LoggerFactory.getLogger("easy.tiva");
	
	private String drug;
	
	public void set(ApplicationContext parentContext, MDSHandler mdsHandler, Subscriber subscriber, TextArea loggingArea) {
		this.parentContext=parentContext;
		this.subscriber=subscriber;
		this.mdsHandler=mdsHandler;
		this.loggingArea=loggingArea;
		
	}
	
	private void createSimAlarisDevice() {
		if(simAlarisDeviceAdapter==null) {
			DeviceDriverProvider.SpringLoadedDriver df = new DeviceDriverProvider.SpringLoadedDriver() {
	            @Override
	            public DeviceType getDeviceType() {
	                return new DeviceType(ice.ConnectionType.Serial, "ICE", "SimAlaris", "SimAlaris", 1);
	            }
	
	            @Override
	            public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
//	                EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
//	                Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
	                Publisher publisher = context.getBean("publisher", Publisher.class);
	                return new SimulatedAlarisDevice(subscriber, publisher, eventLoop,1);
	            }
	            
	            
	        };
	        
	        try {
	        	simAlarisDeviceAdapter = df.create((AbstractApplicationContext) parentContext);

                // TODO Make this more elegant
                List<String> strings = new ArrayList<String>();
                SubscriberQos qos = new SubscriberQos();
//                System.err.println("assignedSubscriber is "+assignedSubscriber);
                subscriber.get_qos(qos);

                for (int i = 0; i < qos.partition.name.size(); i++) {
                    strings.add((String) qos.partition.name.get(i));
                }

                simAlarisDeviceAdapter.setPartition(strings.toArray(new String[0]));

            }
            catch(Exception ex) {
                throw new RuntimeException("Failed to create a driver", ex);
            }
		}
		simAlarisDevice=(SimulatedAlarisDevice)simAlarisDeviceAdapter.getDevice();
		if(currentPatient!=null) {
			simAlarisDeviceAdapter.setPartition(new String[] {PartitionAssignmentController.toPartition(currentPatient.mrn)});
			String partitionToAssociate=PartitionAssignmentController.toPartition(currentPatient.mrn);
	        MDSConnectivity connectivity=new MDSConnectivity();
	        connectivity.partition=partitionToAssociate;
	        connectivity.unique_device_identifier=simAlarisDevice.getUniqueDeviceIdentifier();
	        mdsHandler.publish(connectivity);
		}
		String selectedPort=serialPortsCombo.getSelectionModel().getSelectedItem();
		simAlarisDevice.connect(selectedPort);
		
	}
	
	public void runAlaris() {
		createSimAlarisDevice();
	}
	
	public void stopAlaris() {
		destroyAlarisDevice();
	}
	
	private void destroyAlarisDevice() {
		if(simAlarisDevice!=null) {
			simAlarisDevice.shutdown();
			simAlarisDevice=null;
			simAlarisDeviceAdapter.stop();
			simAlarisDeviceAdapter=null;
		}
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		List<String> l = SerialProviderFactory.getDefaultProvider().getPortNames();
		ObservableList<String> ports=FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(l));
		serialPortsCombo.setItems(ports);
		
		List<String> serials=new ArrayList<String>();
		serials.add("8002-51740 (Remifentanil)");
		serials.add("8002-51733 (Propofol)");
		ObservableList<String> sers=FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(serials));
		serialNumCombo.setItems(sers);
		
		this.eventLoop=eventLoop;
	}
	
	/**
	 * A simulated Alaris device.
	 * @author simon
	 *
	 */
	class SimulatedAlarisDevice extends AbstractSerialDevice {

		public SimulatedAlarisDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop,
				int countSerialPorts) {
			super(subscriber, publisher, eventLoop, countSerialPorts);
			deviceIdentity.unique_device_identifier=DeviceIdentityBuilder.randomUDI();
			deviceIdentity.manufacturer="ICE";
			String currentSelection=serialNumCombo.getSelectionModel().getSelectedItem();
        	drug=currentSelection.substring(currentSelection.indexOf(' '));
			deviceIdentity.model="Simulated Alaris "+drug;
			writeDeviceIdentity();
			flowRateHolder=createNumericInstance(rosetta.MDC_FLOW_FLUID_PUMP.VALUE, "", 0, rosetta.MDC_DIM_MILLI_L_PER_HR.VALUE);
		}

		@Override
		protected void doInitCommands(int idx) throws IOException {
			
			
		}
		
		public SerialProvider getSerialProvider(int idx) {
			SerialProvider provider = super.getSerialProvider(idx);
			provider.setDefaultSerialSettings(38400, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
			return provider;
		}
		
		private void writeAndLog(String str, OutputStream out) throws IOException {
			out.write( str.getBytes() );
			out.flush();
			System.err.println("Sim Pump Wrote "+str);
			loggingArea.appendText(">>> "+drug+" "+str);
			easyTivaLog.trace(">>> "+drug+" "+str);
		}

		@Override
		protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
			DeviceClock clock=new DeviceClock.WallClock();
			reportConnected(idx, "Sim pump is always connected");
			System.err.println("Sim Pump process starts for idx "+idx);
			fromRequestor=new BufferedReader(new InputStreamReader(inputStream)); //inputStream;
			toRequestor=new BufferedOutputStream(outputStream);
			System.err.println("Receiving from requestor idx "+idx);
			String clientRequest;
			/*
			 * We try to be flexible with the incoming requests here by only trying to match the start
			 * of the lines - in case the end of the line varies with device serial number or other variable
			 * data that we can't control.
			 */
			/*!INF_STOP|CD57
Request from client is !REMQUERY^DEACT|F295
Request from client is !REMOTE_CTRL^DISABLED|EF95
*/
			float delay=0;
			try {
				delay=Float.parseFloat(responseDelayField.getText());
			} catch (NumberFormatException nfe) {
				
			}
			float ramp=1000;
			try {
				ramp=Float.parseFloat(rampingSpeedField.getText());
			} catch (NumberFormatException nfe) {
				//Invalid, just use a massive rate;
			}
			//TODO: Allow initial rate to be set from somewhere? 
			FlowRateProducer producer=new FlowRateProducer(ramp, 0.1f);
			while( (clientRequest=fromRequestor.readLine())!=null)  {
				short handled=0;
				System.err.println("Request from client is "+clientRequest);
				loggingArea.appendText("<<<"+drug+" "+clientRequest);
				easyTivaLog.trace("<<<"+drug+" "+clientRequest);
				/*
				 * Command written to ALarisGH - !INST_SERIALNO|457D
				 * Response from Alaris is !INST_SERIALNO^8002-51740|050B
				 */
				if(clientRequest.startsWith("!INST_SERIALNO|")) {
					//We need a response like this
					//!INST_SERIALNO^8002-51740|050B
					//!INST_SERIALNO^8002-51740|050B
					String selectedSerNumWithDrug=serialNumCombo.getSelectionModel().getSelectedItem();
					String serNum=selectedSerNumWithDrug.substring(0,selectedSerNumWithDrug.indexOf(' '));
					String cmd="INST_SERIALNO^"+serNum;
					String crcForReturn=crc(cmd);
					String finalReturn="!"+cmd+"|"+crcForReturn+"\r";
					writeAndLog(finalReturn, toRequestor);
					handled++;
				}
				/*
				 * Command written to ALarisGH - !COMMS_PROTOCOL|E8DA
				 * Response from Alaris is !COMMS_PROTOCOL^Asena Rev 2.1.5|661A
				 */
				if(clientRequest.startsWith("!COMMS_PROTOCOL|")) {
					writeAndLog(new String("!COMMS_PROTOCOL^Asena Rev 2.1.5|661A\r"), toRequestor);
					handled++;
				}
				//                           !REMOTE_CTRL^ENABLED^3CC1|B1F1 0 times!!!!!
				if(clientRequest.startsWith("!REMOTE_CTRL^ENABLED^")) {
					/*
					 * Command written to ALarisGH - !REMOTE_CTRL^ENABLED^3CC1|B1F1
					 * Response from Alaris is !REMOTE_CTRL^ENABLED^****^PERMIT^0^ms|7BB0
					 * 
					 * Should this one one use **** or 3CC1?
					 */
					writeAndLog( new String("!REMOTE_CTRL^ENABLED^****^PERMIT^0^ms|7BB0\r"), toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!REMOTE_CTRL^DISABLED|")) {
					writeAndLog( new String("!REMOTE_CTRL^DISABLED|EF95\r"), toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!COMMS_RESPONSE_MAX|")) {
					writeAndLog( new String("!COMMS_RESPONSE_MAX^4000^ms|DFDF\r"), toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!SYRINGE_STATUS|")) {
					//toRequestor.write( new String("!SYRINGE_STATUS^BD PERFUSOR^ 50^CONF|4567\r").getBytes() );
					writeAndLog( new String("!SYRINGE_STATUS^BD PLASTIPAK^ 50^CONF|1CF8\r"), toRequestor);
					handled++;
				}
				if(clientRequest.startsWith("!INF_RATE_MAX_SYRINGE|")) {
					writeAndLog( new String("!INF_RATE_MAX_SYRINGE^1200.00^ml/h|8575\r"), toRequestor);
					handled++;
				}
				if(clientRequest.startsWith("!INF_RATE^")) {
					//!INF_RATE^0.1^ml/h|B775
					String split[]=clientRequest.split("\\^");
					String newRequestedRate=split[1];
					lastRequestedRate=Float.parseFloat(newRequestedRate);
					//!INF_RATE^0.10^ml/h|D407
					//TODO: Should the reply be requested rate, to confirm we have set it, or the new current rate?
					String reply="INF_RATE^"+String.format("%.2f", lastRequestedRate)+"^ml/h";
					String crcForReturn=crc(reply);
					String finalReturn="!"+reply+"|"+crcForReturn+"\r";
					writeAndLog(finalReturn, toRequestor);
					handled++;
				}
				if(clientRequest.startsWith("!INF|")) {
					//TODO: Surely this must need to become variable as it's the key high frequency request/response
					String selectedSerNumWithDrug=serialNumCombo.getSelectionModel().getSelectedItem();
					String serNum=selectedSerNumWithDrug.substring(0,selectedSerNumWithDrug.indexOf(' '));
					String cmd="INF^"+serNum+"^-^SET^"+String.format("%.2f", producer.getCurrentRate())+"^ml/h^^"+String.format("%.3f",volumeInfused)+"^ml^17.91^mmHg^24+^EVENT^127904";
					numericSample(flowRateHolder, lastRequestedRate, clock.instant());
					String crcForReturn=crc(cmd);
					String finalReturn="!"+cmd+"|"+crcForReturn+"\r";
					writeAndLog( finalReturn, toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!INF_VI_CLEAR|")) {
					writeAndLog( new String("!INF_VI_CLEAR|A706\r"), toRequestor );
					volumeInfused=0;
					handled++;
				}
				if(clientRequest.startsWith("!INF_STOP|")) {
					writeAndLog( new String("!INF_STOP|CD57\r"), toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!REMQUERY^DEACT|")) {
					writeAndLog( new String("!REMQUERY^DEACT^^^|E5C9\r"), toRequestor );
					handled++;
				}
				if(clientRequest.startsWith("!INF_START|")) {
					writeAndLog( new String("!INF_START|38F3\r"), toRequestor);
					handled++;
				}
				if(clientRequest.startsWith("!ALARM|")) {
					writeAndLog("!ALARM^AL_NOALM^ ^ |C73B", toRequestor);
					handled++;
				}
				
				if(handled!=1) {
					//Either unhandled or handled more than once
					throw new RuntimeException("Handled request "+clientRequest+" "+handled+" times!!!!!");
				}
				
			}
			//System.err.println("First request from client is  " + clientRequest);
			
		}
		
		private String crc(String input) {
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
	        return hexValue;
		}
		
		private String valueOf(short number) {
	        // Create a mask to isolate only the correct width of bits.
	        long fullMask = (((1L << 15) - 1L) << 1) | 1L;
	        return Long.toHexString(number & fullMask);
      }

		@Override
		public boolean connect(String address) {
			System.err.println("connect called with address "+address+" - need to replace with something");
			return super.connect(address);
		}

		@Override
		protected void connect(int idx) {
			// TODO Auto-generated method stub
			super.connect(idx);
		}

		@Override
		protected long getMaximumQuietTime(int idx) {
			return Integer.MAX_VALUE;
		}

		@Override
		protected String iconResourceName() {
			return "sim_alaris_asena_pump.png";
		}
		
	}
	
	/**
	 * A class that allows us to control the response to flow rate requests.<br/>
	 * 
	 * The two inputs to the calculation are the delay, and the ramping speed.<br/>
	 * 
	 * There is also an initial speed parameter.<br/>
	 * 
	 * The delay determines how long we wait before making any change at all.<br/>
	 * 
	 * The ramping speed in ml/hr/s determines how fast the rate can change.  For instance
	 * if the ramping speed is 10ml/hr/s, then to go from 1ml/hr to 101ml/hr would take
	 * 10 seconds. 
	 * @author SimonKelly
	 *
	 */
	public class FlowRateProducer extends Thread {
		
		private Thread calculator;
		/**
		 * The ramping rate, in ml/hr/s
		 */
		private float rampingSpeed;
		
		/**
		 * The initial flow rate.
		 */
		private float initialRate;
		
		/**
		 * The current flow rate, derived according to the elapsed time, and ramping speed.
		 */
		private float currentRate;
		
		/**
		 * The desired flow rate.
		 */
		private float targetRate;
		
		/**
		 * The last requested target rate;
		 */
		private float lastTargetRate;
		
		/**
		 * Create an instance with the specified initial values.  The initial flow rate will
		 * be 0.1ml/hr.
		 * 
		 * @param delay the initial delay.
		 * @param rampingSpeed the initial ramping speed.
		 */
		public FlowRateProducer(float rampingSpeed) {
			this(rampingSpeed, 0.1f);
		}
		
		/**
		 * Create an instance with the specified initial values.
		 * 
		 * @param delay the initial delay.
		 * @param rampingSpeed the initial ramping speed.
		 * @param initialRate the initial flow rate.
		 */
		public FlowRateProducer(float rampingSpeed, float initialRate) {
			this.rampingSpeed=rampingSpeed;
			this.initialRate=initialRate;
			currentRate=initialRate;
		}
		
		public float getRampingSpeed() {
			return rampingSpeed;
		}

		public void setRampingSpeed(float rampingSpeed) {
			this.rampingSpeed = rampingSpeed;
		}
		
		public float getTargetRate() {
			return targetRate;
		}

		public void setTargetRate(float targetRate) {
			this.targetRate = targetRate;
		}
		
		public float getCurrentRate() {
			System.err.println("getCurrentRate returning "+currentRate);
			return currentRate;
		}
		
		@Override
		public void run() {
			System.err.println("run starts, currentRate="+currentRate+" target is "+targetRate);
			while(true) {
				if(currentRate>targetRate) {
					/*
					 * We assume we are going down.  Let's also assume we can settle on exactly the desired flow rate,
					 * and check if a step would take us below that.  
					 */
					if( currentRate-rampingSpeed < targetRate) {
						currentRate=targetRate;
						continue;
					}
					//If we get here, we can take at least one whole rampingSpeed step towards the target.
					currentRate-=rampingSpeed;
				}
				if(currentRate<targetRate) {
					/*
					 * We assume we are going up.  Let's also assume we can settle on exactly the desired flow rate,
					 * and check if a step would take us above that.  
					 */
					if( currentRate+rampingSpeed > targetRate) {
						currentRate=targetRate;
						System.err.println("set currentRate to exactly "+currentRate);
						continue;
					}
					//If we get here, we can take at least one whole rampingSpeed step towards the target.
					currentRate+=rampingSpeed;
					System.err.println("set currentRate to "+currentRate);
				}
				/*
				 * Having arrived here, we now do a sleep.  The ramping speed is in ml/hr/s, so we just sleep for 1s.  
				 */
				try {
					System.err.println("Sleeping 1s at bottom of loop");
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public String toString() {
			return "FlowRateProducer [rampingSpeed=" + rampingSpeed + ", initialRate="
					+ initialRate + ", currentRate=" + currentRate + ", targetRate=" + targetRate + "]";
		}

	}



}
