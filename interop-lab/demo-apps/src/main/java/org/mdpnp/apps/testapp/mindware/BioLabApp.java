package org.mdpnp.apps.testapp.mindware;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceClock.WallClock;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

import ice.Patient;
import ice.SampleArray;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BioLabApp {
	
	/**
	 * Out logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(BioLabApp.class);
	
	/**
	 * First byte of data packet representing "General Info"
	 */
	private byte GENERAL_INFO=0x01;
	/**
	 * First byte of data packet representing "Channel Info"
	 */
	private byte CHANNEL_INFO=0x02;
	/**
	 * First byte of data packet representing "Channel Data"
	 */
	private byte CHANNEL_DATA=0x03;
	
	/**
	 * The IP address that we will listen on
	 */
	private InetAddress addr;
	
	/**
	 * The port number that we will listen on
	 */
	private int port;
	
	@FXML
	private GridPane rootPane;
	
	@FXML
	private Button startButton;
	
	/**
	 * The number of channels specified in a "General Info" packet
	 */
	private int channelCount;
	
	/**
	 * Timebase of data (cycles per second) specified in a "General Info" packet
	 */
	private int timebase;
	
	/**
	 * Array of ChannelInfo objects.  We need this so we can refer to them by channel index after they are received.
	 */
	private ChannelInfo[] channelInfos;
	
	private MDSHandler mdsHandler;
	
	private Subscriber subscriber;
	
	private Patient currentPatient;
	
	private ApplicationContext parentContext;
	
	private Parent[] parents;
	
	//We REQUIRE that BioLab Channel names are unique. That will be true in any genuine playback.
	
	/**
	 * List of channels by name that are currently publishing.  We store these independently of the list
	 * of instance ids etc., because the channels could theoretically be stopped and started from publishing
	 * during the lifespan of the app, and there is no need to delete the instance ids for that particular
	 * channel.  So channel names can just be added/removed from here to indicate if they are active.  The
	 * keys in this map are the BioLab ones.  The values are the MDC codes.
	 */
	private HashMap<String, String> activeChannels;
	
	/**
	 * A map of channels and the clinician that is using that channel.  This is necessary because we will need to
	 * support more than one clinician publishing the same channel data - e.g. more than one clinician could be 
	 * producing MDC_ECG_LEAD_I.  So there needs to be a different BioLabDevice for each clinician.  Hence the channel
	 * needs to be mapped to clinician for later look up to check if there is a device for that clinician.  
	 */
	private HashMap<String, String> channelClinician;
	
	/**
	 * A map of clinicians and there associated BioLabDevice instance.  Because we expect more than one clinician
	 * to be producing the same metric, e.g. MDC_ECG_LEAD_I, we need one device per clinician.  This map takes care
	 * of that.  {@link #createBioLabDeviceForClinician(String)} creates the devices and also adds them to this map.
	 */
	private HashMap<String, BioLabDevice> bioLabDeviceByClinician;
	
	/**
	 * A collection of all the adapters we create, so we can stop them easily.
	 */
	private HashSet<DeviceDriverProvider.DeviceAdapter> adapters;
	
	@FXML
	TextField ipAddress;
	
	@FXML
	TextField portNumber;
	
	private ReceivingThread receivingThread;
	
	private boolean pleaseStop;
	
	static class GeneralInfo {
		
		int length;
		short version;
		int _timebase;
		short _channelCount;
		short checksum;
		
		GeneralInfo(ByteBuffer byteBuffer) {
			byte type=byteBuffer.get();	//Discard the type...
			length=byteBuffer.getInt();
			version=byteBuffer.getShort();
			_timebase=byteBuffer.getInt();
			_channelCount=byteBuffer.getShort();
			checksum=byteBuffer.getShort();
			log.info(toString());
		}

		@Override
		public String toString() {
			return "GeneralInfo [length=" + length + ", version=" + version + ", timebase=" + _timebase
					+ ", channelCount=" + _channelCount + ", checksum=" + checksum + "]";
		}
		
	}
	
	static class ChannelInfo {
		
		int length;
		short channelNumber;
		int divisor;
		byte labelLength;
		String label;
		byte unitsLength;
		String units;
		short checksum;
		
		ChannelInfo(ByteBuffer byteBuffer) {
			byte type=byteBuffer.get();
			length=byteBuffer.getInt();
			channelNumber=byteBuffer.getShort();
			divisor=byteBuffer.getInt();
			labelLength=byteBuffer.get();
			byte[] dst=new byte[labelLength];
			byteBuffer.get(dst);
			label=new String(dst);
			unitsLength=byteBuffer.get();
			dst=new byte[unitsLength];
			byteBuffer.get(dst);
			units=new String(dst);
			checksum=byteBuffer.getShort();
			log.info(toString());
		}

		@Override
		public String toString() {
			return "ChannelInfo [length=" + length + ", channelNumber=" + channelNumber + ", divisor=" + divisor
					+ ", labelLength=" + labelLength + ", label=" + label + ", unitsLength=" + unitsLength + ", units="
					+ units + ", checksum=" + checksum + "]";
		}
	}
	
	static class ChannelData {
		int length;
		short channelNumber;
		long startTime;
		double data;	//This is the big one...
		short checksum;
		ArrayList<Double> dataPoints=new ArrayList<>();
		
		ChannelData(ByteBuffer byteBuffer) {
			byte type=byteBuffer.get();
			length=byteBuffer.getInt();
			channelNumber=byteBuffer.getShort();
			startTime=byteBuffer.getLong();
			//TODO: Don't assume all packets are the same length, and instead deduce the correct length.
			for(int i=0;i<50;i++) {
				dataPoints.add(byteBuffer.getDouble()/*100000*/);
			}
			checksum=byteBuffer.getShort();
		}

		@Override
		public String toString() {
			return "ChannelData [length=" + length + ", channelNumber=" + channelNumber + ", startTime=" + startTime
					+ ", data=" + data + ", checksum=" + checksum + "]";
		}
	}
	
	private GeneralInfo getGeneralInfo(byte[] bytes) {
		ByteBuffer bb=ByteBuffer.wrap(bytes);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
		GeneralInfo gi=new GeneralInfo(bb);
		if(validGeneralInfo(gi)) {
			return gi;
		} else {
			return null;
		}
	}
	
	private boolean validGeneralInfo(GeneralInfo gi) {
		/*
		 * Size of a GeneralInfo should always be the same
		 * 1 byte packet type
		 * 4 bytes length
		 * 2 bytes version
		 * 4 bytes timebase
		 * 2 bytes channel count
		 * 2 bytes checksum
		 */
		log.info("gi packet size is "+gi.length+" with checksum "+gi.checksum);
		if(gi.length<=15) {
			return true;
		}
		return false;
	}
	
	private ChannelInfo getChannelInfo(byte[] bytes) {
		ByteBuffer bb=ByteBuffer.wrap(bytes);
		ChannelInfo ci=new ChannelInfo(bb);
		//TODO: Validity Check.
		return ci;
	}
	
	private ChannelData getChannelData(byte[] bytes) {
		ByteBuffer bb=ByteBuffer.wrap(bytes);
		ChannelData cd=new ChannelData(bb);
		//TODO: Validity Check.
		return cd;
	}

	public BioLabApp() {
		// TODO Auto-generated constructor stub
	}
	
	public void set(ApplicationContext context, MDSHandler mdsHandler, Subscriber subscriber) {
		this.parentContext=context;
		this.mdsHandler=mdsHandler;
		this.subscriber=subscriber;
		
		activeChannels=new HashMap<>();
		channelClinician=new HashMap<>();
		bioLabDeviceByClinician=new HashMap<>();
		adapters=new HashSet<>();
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		try {
			String addr=InetAddress.getLocalHost().getHostAddress();
			ipAddress.setText(addr);
		} catch (UnknownHostException e) {
			//Just do nothing and let the user sort it out
		}
		
		
	}
	
	class ReceivingThread extends Thread {
		public void run() {
			try(DatagramSocket listenerSocket=new DatagramSocket(port, addr)) {
				byte receiveBuffer[]=new byte[512];
				DatagramPacket nextPacket=new DatagramPacket(receiveBuffer, receiveBuffer.length);
				int totalChannels=0;
				while(true && !pleaseStop) {
					listenerSocket.receive(nextPacket);
					int len=nextPacket.getLength();
	//				System.err.println("nextPacket length is "+len);
					byte receivedData[]=nextPacket.getData();
					byte trimmed[]=ArrayUtils.subarray(receivedData, 0, len);
					//System.err.println("bytes are "+ArrayUtils.toString(trimmed));
					int columnIndex[]=new int[1];
					int rowIndex[]=new int[1];
					columnIndex[0]=0;
					rowIndex[0]=0;
					if(trimmed[0]==GENERAL_INFO) {
						log.info("Got possible GeneralInfo");
						//System.err.println("bytes are "+ArrayUtils.toString(trimmed));
						GeneralInfo gi=getGeneralInfo(trimmed);
						if(gi!=null) {
							//Let's assume it was valid
							//i+=gi.length;	//Jump forward by the size of the packet as specificed by MindWare
							channelCount=gi._channelCount;
							timebase=gi._timebase;
							log.info("Channel Count from General Info is "+channelCount);
							channelInfos=new ChannelInfo[channelCount];
							parents=new Parent[channelCount];
						}
					} 
					else if(trimmed[0]==CHANNEL_INFO) {
						log.info("Got possible ChannelInfo");
						ChannelInfo ci=getChannelInfo(trimmed);
						if(ci!=null) {
							log.info("Channel name and number are "+ci.label+" "+ci.channelNumber);
						}
						
						channelInfos[ci.channelNumber-1]=ci;
						
						FXMLLoader loader = new FXMLLoader(BioLabApp.class.getResource("BioLabChannel.fxml"));

				        final Parent ui = loader.load();
				       
				        final BioLabChannelController controller = ((BioLabChannelController) loader.getController());
				        controller.setChannelName(ci.label);
				        controller.getPublishing().addListener(new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if(newValue) {
								  //We want to start publishing.  ci.label , the BioLab channel will be unique.
								  if(!activeChannels.containsKey(ci.label)) {
									log.info("New value for channel "+ci.label);
									log.info("No key for that");
									activeChannels.put(ci.label, controller.getMetricID().get());
									log.info("BioLabApp added active channel "+ci.label+" with "+controller.getMetricID().get());
									channelClinician.put(ci.label, controller.getClinicianName());
									log.info("BioLabApp added channel clinician "+ci.label+" with "+controller.getClinicianName());
									
								  } else {
									//We can collapse all this into one block?
								  }
								  
									
								} else {
									//We want to stop publishing.
									activeChannels.remove(ci.label);
									log.info("BioLabApp removed channel "+ci.label);
									channelClinician.remove(ci.label);	//Just to be thorough.
								}
								controller.toggleStopStart();
							}
				        	
				        });
				        
				        parents[totalChannels++]=ui;
				        
				        if(totalChannels==channelInfos.length) {
				        	//All channels done
				        	javafx.application.Platform.runLater(new Runnable() {
								public void run() {
									int row=1;
									int col=0;
									for(int i=0;i<parents.length;i++) {
										rootPane.add(parents[i],col%2,row);
										if( (col++ %2) ==1 ) {
											row++;
										}	
									}
							        

								}
							});
				        }
					} else if(trimmed[0]==CHANNEL_DATA) {
						ChannelData cd=getChannelData(receivedData);
						if(cd!=null) {
							//These channel numbers appear to be 0 based, as opposed to 1 based when ChannelInfo is produced.
							//TODO - confirm this with vendor
							ChannelInfo ciForThisData=channelInfos[cd.channelNumber];
//							if(pw!=null) {
							//System.err.println("Channel label is "+ciForThisData.label);
							if(activeChannels.containsKey(ciForThisData.label)) {
								String clinicianName=channelClinician.get(ciForThisData.label);
								BioLabDevice __bioLabDevice=bioLabDeviceByClinician.get(clinicianName);
								if(__bioLabDevice==null) {
									__bioLabDevice=createBioLabDeviceForClinician(clinicianName);	//create method populates the HashMap
								}
								__bioLabDevice.writeSamplesFromPacket(ciForThisData, cd);
							}
						}
						
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final class BioLabDevice extends AbstractDevice {
		
		/**
		 * Mapping of metric IDs to instance holders.
		 */
		private HashMap<String, InstanceHolder<ice.SampleArray>> instanceHolders;

		private WallClock biolabClock=new WallClock();

		public BioLabDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop, String clinicianName) {
			super(subscriber, publisher, eventLoop);
            deviceIdentity.manufacturer = "OpenICE";
            deviceIdentity.model = clinicianName;
            deviceIdentity.serial_number = "5678";
            AbstractSimulatedDevice.randomUDI(deviceIdentity);		//TODO: clone the device id, or does that mess everything up?
            log.info("New BioLabDevice had UDI "+deviceIdentity.unique_device_identifier);
            writeDeviceIdentity();
//            System.err.println("NumericBPDeviceConstructor subscriber is "+subscriber);
            instanceHolders=new HashMap<>();
		}

//		public void writeNumerics() {
//            DeviceClock.Reading sampleTime = clock.instant();
//            // TODO clearly a synchronization issue here.
//            // enforce a singular calling thread or synchronize accesses
//            systolic = numericSample(systolic, (int) Math.round(systolicProperty.floatValue()), rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, 
//                    rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, 0, rosetta.MDC_DIM_MMHG.VALUE, sampleTime);
//            diastolic = numericSample(diastolic, (int) Math.round(diastolicProperty.floatValue()), rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, 
//                    rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, 0, rosetta.MDC_DIM_MMHG.VALUE, sampleTime);
//		}
		
		public void writeSamplesFromPacket(ChannelInfo ci, ChannelData cd) {
			String metricID=activeChannels.get(ci.label);
			if(metricID==null) {
				//This channel isn't being published.
				return;
			}
			if(!instanceHolders.containsKey(metricID)) {
				int frequency=timebase/ci.divisor;
				log.info("Frequency is "+frequency);
				InstanceHolder<SampleArray> holder=createSampleArrayInstance(metricID, "", 0, ci.units, frequency);
				instanceHolders.put(ci.label, holder);
				sampleArraySample(holder, (Double[])cd.dataPoints.toArray(new Double[0]), biolabClock.instant());
				log.info("Published sampleArraySample for "+metricID);
			} else {
				InstanceHolder<SampleArray> holder=(InstanceHolder<SampleArray>)instanceHolders.get(ci.label);
				sampleArraySample(holder, (Double[])cd.dataPoints.toArray(new Double[0]), biolabClock.instant());
			}
		}
		
		@Override
		protected String iconResourceName() {
			return "mindware.png";
		}
	}

    protected final DeviceClock clock = new DeviceClock.WallClock();
    //private DeviceDriverProvider.DeviceAdapter bioLabDeviceAdapter;

    /**
     * This method creates a BioLabDevice object for the given clinician.  It also adds it to
     * the map of instances by clinician name.
     * @param clinicianName
     * @return
     */
    private BioLabDevice createBioLabDeviceForClinician(String clinicianName) {
    	
    	DeviceDriverProvider.DeviceAdapter bioLabDeviceAdapter=null; 

            DeviceDriverProvider.SpringLoadedDriver df = new DeviceDriverProvider.SpringLoadedDriver() {
                @Override
                public DeviceType getDeviceType() {
                    return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "BioLab", "BioLab", 4);
                }

                @Override
                public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
                    EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
                    Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
                    Publisher publisher = context.getBean("publisher", Publisher.class);
                    return new BioLabDevice(subscriber, publisher, eventLoop, clinicianName);
                }
            };

            try {
            	bioLabDeviceAdapter = df.create((AbstractApplicationContext) parentContext);
            	adapters.add(bioLabDeviceAdapter);

                // TODO Make this more elegant
                List<String> strings = new ArrayList<String>();
                SubscriberQos qos = new SubscriberQos();
//                System.err.println("assignedSubscriber is "+assignedSubscriber);
                subscriber.get_qos(qos);

                for (int i = 0; i < qos.partition.name.size(); i++) {
                    strings.add((String) qos.partition.name.get(i));
                }

                //numericBPDeviceAdapter.setPartition(strings.toArray(new String[0]));
                

            }
            catch(Exception ex) {
                throw new RuntimeException("Failed to create a driver", ex);
            }

        //bioLabDeviceAdapter.setPartition(new String[] {PartitionAssignmentController.toPartition(currentPatient.mrn)});
        bioLabDeviceAdapter.setPartition(new String[] {});
        BioLabDevice _device=(BioLabDevice)bioLabDeviceAdapter.getDevice();

        bioLabDeviceByClinician.put(clinicianName, _device);
        log.info("createBioLabDeviceForClinician added device for "+clinicianName);
        return _device;
        //We have the device - we must associate it with the correct partition...
        //mdsHandler.
        //mdsHandler.publish(new MDSConnectivity());
        //String partitionToAssociate=PartitionAssignmentController.toPartition(currentPatient.mrn);
        //MDSConnectivity connectivity=new MDSConnectivity();
        //connectivity.partition=partitionToAssociate;
        //connectivity.unique_device_identifier=bioLabDevice.getUniqueDeviceIdentifier();
        //mdsHandler.publish(connectivity);
//        System.err.println(connectivity.toString("Published device connectivity ",2));
    }
    
    /**
     * Uses the IP and port number fields on the UI to start the listening process.
     */
    public void startListening() {
    	if(receivingThread==null) {
        	String addrText=ipAddress.getText();
    		String portNumText=portNumber.getText();
    		if(addrText.length()==0 || portNumText.length()==0) {
    			Alert bad=new Alert(AlertType.ERROR,"You must specify an ip address and port number");
    			bad.show();
    			return;
    		}
    		try {
    			addr = InetAddress.getByName(addrText);
    		} catch (UnknownHostException uhe) {
    			Alert bad=new Alert(AlertType.ERROR,"You must specify a valid ip address");
    			bad.show();
    			return;
    		}
    		try {
    			port=Integer.parseInt(portNumText);
    		} catch (NumberFormatException nfe) {
    			Alert bad=new Alert(AlertType.ERROR,"Port must be numeric");
    			bad.show();
    			return;
    		}
    		startButton.setText("Stop");
    		receivingThread=new ReceivingThread();
    		receivingThread.start();
    	} else {
    		pleaseStop=true;
    		receivingThread.interrupt();
    		receivingThread=null;
    		startButton.setText("Start");
    		bioLabDeviceByClinician.values().forEach( d-> {
    			d.shutdown();
    			d=null;
    		});
    		adapters.forEach( a -> {
    			a.stop();
    			a=null;
    		});
    		activeChannels.clear();
    		bioLabDeviceByClinician.clear();
    		adapters.clear();
    	}
    	
		
    }


}
