package org.mdpnp.devices.nihon.koden;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringEscapeUtils;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

import ice.ConnectionState;
import ice.ConnectionType;
import ice.Numeric;
import ice.SampleArray;

/**
 * An OpenICE device interface for communicating with a Nihon Koden NKV-550<br/>
 * 
 * The implementation is based on a document titled <code>TC-RC1 Rev P1 NKV550 Remote Control
 * Communication Protocol.docx</code>
 * @author simon
 *
 */
public class NKV550 extends AbstractConnectedDevice {
	
	/**
	 * The default port to connect to to receive data
	 */
	private static int defaultPort=15376;
	
	/**
	 * Logger instance.  For logging...
	 */
	private static final Logger log = LoggerFactory.getLogger(NKV550.class);
	
	/**
	 * A socket connected to the device.
	 */
	private Socket deviceSocket;
	
	/**
	 * The input stream to read from the device
	 */
	private BufferedInputStream fromDevice;
	
	/**
	 * The output stream to write to the device
	 */
	private BufferedOutputStream toDevice;
	
	/**
	 * A variable to indicate to the processing thread that it should keep running.
	 */
	private boolean keepGoing;
	
	/**
	 * A variable to indicate that the read loop should pause while a command is executed
	 * and the response processed - but not that the read loop should quit.
	 */
	private boolean pauseForCommand;
	
	/**
	 * The XML node name indicating device data
	 */
	private static final String DEVICE="device";
	
	/**
	 * The XML node name indicating waveforms.
	 */
	private static final String WAVEFORMS="waveforms";
	
	/**
	 * The XML node name indicating monitors
	 */
	private static final String MONITORS="monitors";
	
	/**
	 * The XML node name indicating monitorsindex
	 */
	private static final String MONITORS_INDEX="monitorsindex";
	
	/**
	 * The XML node name indicating an individual waveform.
	 */
	private static final String WAVEFORM="w";
	
	/**
	 * The XML node name indicating settingsindex
	 */
	private static final String SETTINGS_INDEX="settingsindex";
	
	/**
	 * The XML node name indicating settings
	 */
	private static final String SETTINGS="settings";
	
	/**
	 * The XML node name indicating an individual setting
	 */
	private static final String SETTING="s";
	
	
	private static final String[] WAVEFORM_TYPES=new String[] {
		"Patient Pressure",
		"Patient Flow",
		"Patient Tidal Volume",
		"Tracheal Pressure",
		"Auxillary Pressure",
		"Transpulmonary Pressure",
		"Measured SPO\u2082 concentration",
		"Measured CO\u2082 concentration"
	};
	
	private InetAddress address;

	private int port;
	
	/**
	 * We need to have different values for each one of these, but the vent panel can
	 * display MDC_FLOW_AWAY, so we can concentrate on that for now.
	 */
	private static final String[] WAVEFORM_METRICS=new String[] {
		rosetta.MDC_PRESS_AWAY.VALUE,
		rosetta.MDC_FLOW_AWAY.VALUE,
		rosetta.MDC_VOL_AWAY_TIDAL.VALUE,
		rosetta.MDC_PRESS_AWAY.VALUE,
		rosetta.MDC_PRESS_AWAY.VALUE,
		rosetta.MDC_PRESS_AWAY.VALUE,
		"",	//Measured SPO2
		""	//Measures CO2
	};
	
	private static final String[] WAVEFORM_UNITS=new String[] {
			rosetta.MDC_DIM_CM_H2O.VALUE,
			rosetta.MDC_DIM_L_PER_MIN.VALUE,
			rosetta.MDC_DIM_MILLI_L.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			"",	//Measured SPO2
			""	//Measures CO2
		};
	
	private InstanceHolder<SampleArray>[] waveformInstances=new InstanceHolder[WAVEFORM_TYPES.length];
	
	/**
	 * 
	 */
	private HashMap<String, InstanceHolder<Numeric>> numericInstances;
	
	private Number[][] waveformBuffers=new Number[WAVEFORM_TYPES.length][520];
	
	private int[] waveformBufferLength=new int[WAVEFORM_TYPES.length];
	
	/**
	 * The thread that reads from the device and publishes.
	 */
	private Thread readLoop;
	
	private final DeviceClock.WallClock ourClock;
	
	private int publishCount=0;

	/**
	 * A map between the id index values in monitors from the device
	 * and the names of the metrics that they represent.
	 */
	private HashMap<String,String> monitorNamesMap;
	
	/**
	 * A map between the setting name in settings from the device
	 * and the id of the setting.  Note this is opposite to some
	 * of the other mappings, as we really want to look up some
	 * sort of human readable setting name to get the corresponding
	 * id so that we can put that in a command to set the setting.
	 */
	private HashMap<String, String> settingsNameMap;
	
	/**
	 * A map between the <em>names</em> of the elements in the device
	 * monitors and the metrics we want to publish them as in OpenICE.
	 */
	private HashMap<String,String[]> monitorsMetricsMap;
	
	/**
	 * If this is true, we transform received XML docs to String
	 * and then dump them.
	 */
	private static final boolean DEBUG_INCOMING_XML=true;
	
	/**
	 * A document builder instance.  Since we use one all the way through the
	 * code, it makes sense to have a global variable. WATCH OUT FOR THREAD SAFETY THOUGH?!?!
	 */
	private DocumentBuilder db;
	
	/**
	 * A (possibly) temporary variable indicating the current operating mode.  We need
	 * to be able to display the current mode, but this is more just a thing so we can
	 * test changing it from one mode to another.
	 * 
	 * 0 = ACMV_VC<br/>
	 * 1 = ACMV_PC<br/>
	 * 2 = ACMV_PRVC<br/>
	 * 
	 * etc.  See the NKV docs for all modes.
	 * 
	 */
	private int currentOperatingMode;

	public NKV550(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		super(subscriber, publisher, eventLoop);
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();
		ourClock=new DeviceClock.WallClock();
		fillMonitorsMetricsMap();
		numericInstances=new HashMap<>();
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try {
			db=dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fillMonitorsMetricsMap() {
		//TODO: Some devices read these sort of maps from a file.
		monitorsMetricsMap=new HashMap<>();
		monitorsMetricsMap.put("RR<sub>TOT</sub>", new String[] {rosetta.MDC_RESP_RATE.VALUE, "MDC_DIM_RESP_PER_MIN"});
		monitorsMetricsMap.put("EtCO<sub>2</sub>", new String[] {rosetta.MDC_AWAY_CO2_ET.VALUE, rosetta.MDC_DIM_MMHG.VALUE});
		monitorsMetricsMap.put("P<sub>PEAK</sub>", new String[] {rosetta.MDC_PRESS_AWAY_INSP_PEAK.VALUE, rosetta.MDC_DIM_CM_H2O.VALUE});
		monitorsMetricsMap.put("P<sub>PLAT</sub>", new String[] {rosetta.MDC_PRESS_RESP_PLAT.VALUE, rosetta.MDC_DIM_CM_H2O.VALUE});
		monitorsMetricsMap.put("PEEP", new String[] {"ICE_PEEP", rosetta.MDC_DIM_CM_H2O.VALUE});	//TODO: Confirm there is no MDC_ for PEEP
		monitorsMetricsMap.put("FiO<sub>2</sub>%", new String[] {"ICE_FIO2", rosetta.MDC_DIM_PERCENT.VALUE});	//TODO: Confirm there is no MDC_ for FiO2
		monitorsMetricsMap.put("Leak %", new String[] {rosetta.MDC_VENT_VOL_LEAK.VALUE, rosetta.MDC_DIM_PERCENT.VALUE});	//TODO: Confirm there is no MDC_ for FiO2
		
		//Leak %
	}

	@Override
	public boolean connect(String address) {
		if(address==null || address.length()==0) {
			//TODO: Listen for the broadcast UDP packet from the 550.
			log.error("connect called with empty address");
			return false;
		}
//		Thread.dumpStack();
		stateMachine.transitionIfLegal(ConnectionState.Connecting, "Connecting to NKV 550 with address "+address);
		
		int port = defaultPort;

        int colon = address.lastIndexOf(':');
        if (colon >= 0) {
            port = Integer.parseInt(address.substring(colon + 1, address.length()));
            address = address.substring(0, colon);
        }

        try {
        	InetAddress addr = InetAddress.getByName(address);
        	connect(addr, port);
        } catch (IOException ioe) {
        	log.error("Could not connect to NKV-550", ioe);
        	return false;
        }
		
		return true;
	}

	@Override
	public void disconnect() {
		keepGoing=false;

	}

	@Override
	protected ConnectionType getConnectionType() {
		// TODO Auto-generated method stub
		return ConnectionType.Network;
	}
	
	public void connect(InetAddress address, int port) throws IOException {
		this.address=address;
		this.port=port;
		_connect();
		//askForWaveforms();
		dummyAskForWaveforms();
		askForMonitorValues();
		//Try a sleep after monitorValues
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		askForCurrentSettings();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		keepGoing=true;
		startReadLoop();
		//Temp test...
		new Thread() {
			public void run() {
				try {
					sleep(10000);
					Hashtable<String,String> params=new Hashtable();
					if(currentOperatingMode==0) {
						params.put("2","1");
						System.err.println("Current operating mode is 0, setting to 1");
					} else {
						params.put("2","0");
						System.err.println("Current operating mode is 1, setting to 0");
					}
					String setVentOpMode=createCommandWithParams(1, params);
					pauseForCommand=true;
					System.err.println("Set pauseForCommand to true...");
					synchronized (toDevice) {
						String cmdLength=String.format("%08d", setVentOpMode.length());
						String finalCmdToSend=cmdLength+setVentOpMode;
						System.err.println("Sending command "+finalCmdToSend+" to set mode");
						toDevice.write(finalCmdToSend.getBytes());
						toDevice.flush();
					}
					
					int response=getStatusFromResponse();
					//Don't think there is any more response?
					switch (response) {
					case 0:
						System.err.println("Got response 0 from set vent mode");
						break;
					default:
						System.err.println("Got response "+response+" from set vent mode");
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					pauseForCommand=false;
					System.err.println("Set pauseForCommand to false...");
				}
				
			}
		}.start();
	}
	
	private void _connect() throws IOException {
		deviceSocket=new Socket(address, port);
		fromDevice=new BufferedInputStream(deviceSocket.getInputStream());
		toDevice=new BufferedOutputStream(deviceSocket.getOutputStream());
	}
	
	private void dummyAskForWaveforms() {
		stateMachine.transitionIfLegal(ConnectionState.Negotiating, "Requesting waveforms from NKV 550 with serial number "+deviceIdentity.serial_number);
	}
	
	/**
	 * Request waveform data from the device.
	 * 
	 * It seems that if we do this, it stops after some period of time, like 60 seconds.
	 * However, if we don't request waveforms, they seem to get supplied anyway, without
	 * stopping.  So until we clarify, this method isn't used.
	 */
	private void askForWaveforms() {
		String waveformsPlease="<xml version=\"1.0\" encoding=\"UTF8\"?>\n" + 
				"<device>\n" + 
				"<command>2</command>\n" + 
				"<waveforms>\n" + 
				"<capturetime>0</capturetime>\n" + 
				"</waveforms>\n" + 
				"<crc>8F11603D </crc>\n" + 
				"</device>";
		try {
			toDevice.write(waveformsPlease.getBytes());
			toDevice.flush();
			log.info("Wrote the waveforms command XML");
//			Thread.dumpStack();
			/*
			 * Does this really count as "negotiating"?  We have to pass through this state to get to connected
			 * If we send this successfully then we are connected, so negotiating works OK here and then we
			 * transition to connected once we get a Device node in XML
			 */
			stateMachine.transitionIfLegal(ConnectionState.Negotiating, "Requesting waveforms from NKV 550 with serial number "+deviceIdentity.serial_number);
			//Doing a write seems to reset the input stream.  Reset it here.
			//fromDevice=new BufferedInputStream(deviceSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Request monitor values from device.  
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private void askForMonitorValues() throws IOException {
		String monitorCommand=createCommand(0);
		System.err.println("monitorCommand is "+monitorCommand);
		
		
		
		//TODO: - how long should we stay synchronized on output?
		/*
		 * There shouldn't be anything else trying to use output stream at this point
		 */
		synchronized (toDevice) {
			String cmdLength=String.format("%08d", monitorCommand.length());
			String finalCmdToSend=cmdLength+monitorCommand;
			toDevice.write(finalCmdToSend.getBytes());
			toDevice.flush();
		}
		
		int response=getStatusFromResponse();
		switch (response) {
		case 0:
			try {
				Document d=getNextBlock();
				if(d.hasChildNodes()) {
					processChildren(d);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

		//We haven't started the read loop yet, so we can read on the input without mangling other reads yet.
			
	}
	
	private Document getNextBlock() throws IOException, SAXException {
		byte[] eightBytes=new byte[8];
		fetch(eightBytes);
		
		String _blockSize=new String(eightBytes);
		int blockSize=0;
		try {
			blockSize=Integer.parseInt(_blockSize);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			keepGoing=false;
			System.err.println("Read error - setting next block size to 256");
			blockSize=256;	//So we can try and see what else we get.
		}
		byte blockBytes[]=new byte[blockSize];
		fetch(blockBytes);
		String xmlBlock=new String(blockBytes);
		System.err.println("xmlBlock is "+xmlBlock);
		
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder localDb=null;
		try {
			localDb = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document xmlDoc=localDb.parse(new InputSource(new StringReader(xmlBlock)));
		
		if(DEBUG_INCOMING_XML) {
			TransformerFactory tf=TransformerFactory.newInstance();
			Transformer t;
			try {
				t = tf.newTransformer();
				DOMSource source=new DOMSource(xmlDoc);
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				StreamResult result=new StreamResult(baos);
				t.transform(source, result);
				String dumpThis=new String(baos.toByteArray());
				System.err.println("<<<"+dumpThis);
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return xmlDoc;
			
		
	}
	
	/**
	 * Request monitor values from device.  
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private void askForCurrentSettings() throws IOException {
		String monitorCommand=createCommand(4);
		System.err.println("monitorCommand is "+monitorCommand);
		//TODO: - how long should we stay synchronized on output?
		/*
		 * There shouldn't be anything else trying to use output stream at this point
		 */
		synchronized (toDevice) {
			String cmdLength=String.format("%08d", monitorCommand.length());
			String finalCmdToSend=cmdLength+monitorCommand;
			toDevice.write(finalCmdToSend.getBytes());
			toDevice.flush();
		}
		int response=getStatusFromResponse();
		switch (response) {
		case 0:
			try {
				Document d=getNextBlock();
				if(d.hasChildNodes()) {
					processChildren(d);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	
	private int getStatusFromResponse() throws IOException {
		NodeList statusList=null;
		Document d;
		while(statusList==null || statusList.getLength()==0) {
			/*
			 * We can receive other output from the device that does not yet include
			 * the status element, because the device will already be sending other
			 * data before we send our command.  So we need to parse repeated device
			 * responses until we find the one with a status field in it.
			 */
			try {
				d=getNextBlock();
				statusList=d.getElementsByTagName("status");
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		//We only get here once statusList is not null, and according to the spec, there should only be one...
		Node statusNode=statusList.item(0);
		Text textChild=(Text)statusNode.getFirstChild();
		String statusText=textChild.getTextContent();
		int status=Integer.parseInt(statusText);
		return status;
	}
	
	/**
	 * This is where we expect to be connected and so we start reading data from the device.
	 */
	private void startReadLoop() {
		
		readLoop=new Thread() {
			public void run() {
				/*
				 * According to the spec, we should be able to read an 8 character string,
				 * which we turn into a number, and that number is the size of the next data
				 * block.  In all likelihood our buffer will be much bigger, but the whole point
				 * of using the buffer is we don't have to care about the underlying sync between
				 * what we have read, what the device has written etc. etc. 
				 */
				while(keepGoing) {
					if(pauseForCommand) {
						try {
							Thread.sleep(50);	//Give a bit of time for something else...
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.err.println("readLoop is paused");
						continue;
					}
					Document xmlDoc;
					try {
						xmlDoc = getNextBlock();
						if(xmlDoc.hasChildNodes()) {
							processChildren(xmlDoc);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				log.info("Read loop finishing as keepGoing now false");
			}
		};
		readLoop.setName("NKV550Reader");
		readLoop.start();
	}
	
	private void processChildren(Node node) {
		NodeList nodes=node.getChildNodes();
		//System.err.println("nodes length is "+nodes.getLength());
		for(int i=0;i<nodes.getLength();i++) {
			Node n=nodes.item(i);
			//System.err.println("Got node "+i+" with name "+n.getNodeName());
			switch (n.getNodeType()) {
			case Node.ELEMENT_NODE:
				Element elem=(Element)n;
				String nodeName=elem.getNodeName();
				if(nodeName.equals(DEVICE)) {
					System.err.println("Got a device node");
					processDeviceNode(elem);
				}
				if(nodeName.equals(WAVEFORMS)) {
					System.err.println("Got a waveforms node");
					processWaveformsNode(elem);
				}
				if(nodeName.equals(MONITORS)) {
					System.err.println("Got a monitors node");
					processMonitors(elem);
				}
				if(nodeName.equals(MONITORS_INDEX)) {
					System.err.println("Got a monitors index node");
					processMonitorsIndex(elem);
				}
				if(nodeName.equals(SETTINGS_INDEX) || nodeName.equals(SETTINGS)) {
					System.err.println("Got a settings index node");
					processSettings(elem);
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	private void processDeviceNode(Element deviceElement) {
		String serialNumber=deviceElement.getAttribute("sn");
		//Simple check for first time
		if(! deviceIdentity.serial_number.equals(serialNumber)) {
			log.info("Device serial number for device identity is "+serialNumber);
			deviceIdentity.manufacturer="Nihon Koden";
			deviceIdentity.model="NKV550";
			deviceIdentity.serial_number=serialNumber;
			stateMachine.transitionIfLegal(ConnectionState.Connected, "Receiving date from NKV 550 with serial number "+serialNumber);
			writeDeviceIdentity();
		}
		processChildren(deviceElement);
		
	}
	
	
	
	private void processWaveformsNode(Element waveformElement) {
		String _numdata=waveformElement.getAttribute("numdata");
		int numdata=Integer.parseInt(_numdata);
		//TODO: MAke this a class member and check against individual waveforms
		log.debug("numdata in waveforms element is "+numdata);
		
		NodeList waveforms=waveformElement.getChildNodes();
		for(int i=0;i<waveforms.getLength();i++) {
			Node n=waveforms.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE) {
				processWaveform((Element)n, numdata);
			}
		}
		
	}
	
	private void processWaveform(Element waveformElement, int numdata) {
	
		String _datasize=waveformElement.getAttribute("datasize");
		int datasize=Integer.parseInt(_datasize);
		
		String _datagain=waveformElement.getAttribute("datagain");
		int datagain=Integer.parseInt(_datagain);
		
		String _zeroffset=waveformElement.getAttribute("zeroffset");
		int zeroffset=Integer.parseInt(_zeroffset);
		
		String _id=waveformElement.getAttribute("id");
		int id=Integer.parseInt(_id);
		
		/*
		 * For now, we only want 0, 1 and 2
		 */
		if(id>2) {
			return;
		}
		
		log.debug("waveform "+id+ " datasize "+datasize+" datagain "+datagain+" zeroffset "+zeroffset);
		
		String base64Encoded=waveformElement.getTextContent();
		log.debug(base64Encoded);
		Decoder d=Base64.getDecoder();
		byte bytes[]=d.decode(base64Encoded);
		log.debug("decoded bytes of length "+bytes.length);
		
		//All waveforms so far seem to be datasize=2...
		if(datasize!=2) {
			log.warn("datasize for waveform was not 2 but "+datasize);
			return;
		}
		
		int expectedNumOfPoints=bytes.length/datasize;
		log.debug("Expected num of points is "+expectedNumOfPoints);
		
		/*
		 * Presumably we will encounter the customary annoyance of a short
		 * not being the appropriate holder for short, because of not being
		 * able to do an unsigned in Java. 
		 */
		Number wave[]=new Number[expectedNumOfPoints];
		int j=0;
		
		ByteBuffer bb=ByteBuffer.wrap(bytes);
				
		for(int i=0;i<bytes.length;i+=datasize) {
			int fromTwoBytes = Short.toUnsignedInt(bb.getShort());
			float finalVal = (float)fromTwoBytes/datagain - zeroffset;
			wave[j++]=finalVal;
		}
		
		/*
		 * wave is now a float array, perfect for publishing...
		 */
		//if(publishCount++<5) {
		//We need something like the emitFastData thing in the Intellivue code..
		if(waveformBufferLength[id]>500) {
			//Closest we can get for now...
			waveformInstances[id]=sampleArraySample(waveformInstances[id],waveformBuffers[id],WAVEFORM_METRICS[id],"",0, WAVEFORM_UNITS[id], waveformBufferLength[id], ourClock.instant());
			
			//System.arraycopy(waveformBuffers[id], 480, waveformBuffers[id], 0, 20);
			waveformBufferLength[id]=0;
		} else {
			System.arraycopy(wave, 0, waveformBuffers[id], waveformBufferLength[id], wave.length);
			waveformBufferLength[id]+=wave.length;
		}
	}
	
	private void processMonitors(Element monitorsElement) {
		NodeList waveforms=monitorsElement.getChildNodes();
		for(int i=0;i<waveforms.getLength();i++) {
			Node n=waveforms.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE) {
				processMonitor((Element)n);
			}
		}
	}
	
	private void processMonitor(Element monitorElement) {
		String id=monitorElement.getAttribute("id");
		String name=(monitorNamesMap!=null && monitorNamesMap.containsKey(id)) ? monitorNamesMap.get(id) : id;
		float newValue=Float.parseFloat(monitorElement.getFirstChild().getTextContent());

//		System.err.println("Monitor element "+name+" has value "+newValue);
		/*
		 * We have a slightly annoying issue here - it would be nice to map directly from the integer 'id'
		 * values to the metrics we want to publish.  But we probably can't guarantee that they never change,
		 * so we are left with mapping from the names.
		 */
		if(monitorsMetricsMap.containsKey(name)) {
			//monitorsMetricsMap.put("RR<sub>TOT</sub>", new String[] {rosetta.MDC_RESP_RATE.VALUE, "MDC_DIM_RESP_PER_MIN"});
			String metricId=monitorsMetricsMap.get(name)[0];
			String unitId=monitorsMetricsMap.get(name)[1];
			if(numericInstances.containsKey(metricId)) {
				InstanceHolder<Numeric> holder=numericInstances.get(metricId);
				  holder=numericSample(holder, newValue,metricId,"", 0,unitId,ourClock.instant());
			} else {
				InstanceHolder<Numeric> holder=numericSample(null, newValue, unitId, "", 0, ourClock.instant());
				numericInstances.put(metricId, holder);
				
			}
			
		}
	}
	
	private void processMonitorsIndex(Element monitorsElement) {
		if(monitorNamesMap==null) {
			monitorNamesMap=new HashMap<String,String>();
			NodeList waveforms=monitorsElement.getChildNodes();
			for(int i=0;i<waveforms.getLength();i++) {
				Node n=waveforms.item(i);
				if(n.getNodeType()==Node.ELEMENT_NODE) {
					processMonitorIndex((Element)n);
				}
			}
		}
		/*
		 * We'll make an assumption here that the monitor names map doesn't change, and so we
		 * don't need to process it more than once.  Maybe it only gets sent once anyway...
		 */
	}
	
	private void processMonitorIndex(Element monitorElement) {
		String id=monitorElement.getAttribute("id");
		String name=monitorElement.getFirstChild().getTextContent();
		id=StringEscapeUtils.unescapeHtml4(id);
		System.err.println(id+" "+name);
		monitorNamesMap.put(id, name);
	}
	
	/**
	 * Process the settings or settingsindex element.  These have the same
	 * child nodes - a single setting (&lt;s id="...") but the format of the
	 * child nodes varies according to whether it's settingindex (which just
	 * includes the names) or settings, which has the current values for the
	 * settings.  processSetting handles that according to the true/false
	 * value we pass in from here.
	 * @param settingsElement
	 */
	private void processSettings(Element settingsElement) {
		if(settingsNameMap==null) {
			settingsNameMap=new HashMap<String,String>();
		}
		NodeList settings=settingsElement.getChildNodes();
		for(int i=0;i<settings.getLength();i++) {
			Node n=settings.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE) {
				Element settingElement=(Element)n;
				String isOff=settingElement.getAttribute("isOff");
				if(isOff!=null && isOff.length()>0) {
					processSetting(settingElement, false);
				} else {
					processSetting(settingElement, true);
				}
			}
		}
	}
	
	private void processSetting(Element settingElement, boolean names) {
		int id=Integer.parseInt(settingElement.getAttribute("id"));
		if(names) {
			System.err.print("Setting name "+id);
			String value=settingElement.getFirstChild().getTextContent();
			System.err.println(" "+value);
			settingsNameMap.put(settingElement.getAttribute("id"), value);
		} else {
			System.err.print("Setting "+settingElement.getAttribute("id")+ " is off "+settingElement.getAttribute("isOff"));
			String value=settingElement.getFirstChild().getTextContent();
			System.err.println(" "+value);
			if(id==2) {
				//Operating mode...
				currentOperatingMode=Integer.parseInt(value);
				System.err.println("Current operating mode is "+currentOperatingMode);
			}
		}
	}
	
	@Override
	protected String iconResourceName() {
		// TODO Auto-generated method stub
		return "nkv550.png";
	}

	@Override
	public void shutdown() {
		try {
			if(fromDevice!=null) {
				toDevice.close();
			}
			if(toDevice!=null) {
				fromDevice.close();
			}
			if(deviceSocket!=null) {
				deviceSocket.close();
			}
		} catch (Exception e) {
			log.error("Exception closing resources during shutdown of NKV550",e);
		}
		log.info("Finished closing streams and sockets in NKV550");
		super.shutdown();
	}
	
	private String createCommand(int command) {
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.newDocument();
			
			doc.setXmlStandalone(true);
			Element rootElement=doc.createElement("device");
			rootElement.setAttribute("version", "1.0");
			doc.appendChild(rootElement);
			
			Element cmdElement=doc.createElement("command");
			Text txt=doc.createTextNode(String.valueOf(command));
			cmdElement.appendChild(txt);
			rootElement.appendChild(cmdElement);
			
			Element crcElement=doc.createElement("crc");
			Text ff=doc.createTextNode("FFFFFFFF");
			crcElement.appendChild(ff);
			rootElement.appendChild(crcElement);
			
			//Transform the doc.
			TransformerFactory tf=TransformerFactory.newInstance();
			Transformer t=tf.newTransformer();
			DOMSource source=new DOMSource(doc);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			StreamResult result=new StreamResult(baos);
			t.transform(source, result);
			
			int actualCRC=NKVCRC.calculate(baos.toByteArray());
			String hexCRC=Integer.toHexString(actualCRC).toUpperCase();
			if(hexCRC.length()<8) {
				hexCRC=String.format("%8s", hexCRC).replaceAll(" ", "0");
			}
			
			ff.setNodeValue(hexCRC);
			source=new DOMSource(doc);
			baos.reset();
			result=new StreamResult(baos);
			t.transform(source, result);
			
			return new String(baos.toByteArray());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Create a command, and use the hashtable as additional data.
	 * 
	 * THIS PROBABLY IS NOT REALLY GENERIC AND ONLY USABLE FOR command=1 FOR SETTINGS.
	 * @param command
	 * @param params
	 * @return
	 */
	private String createCommandWithParams(int command, Hashtable<String,String> params) {
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.newDocument();
			
			doc.setXmlStandalone(true);
			Element rootElement=doc.createElement("device");
			rootElement.setAttribute("version", "1.0");
			doc.appendChild(rootElement);
			
			Element cmdElement=doc.createElement("command");
			Text txt=doc.createTextNode(String.valueOf(command));
			cmdElement.appendChild(txt);
			
			Element settingsElement=doc.createElement("settings");
			params.forEach( (key, value) -> {
				Element settingElement=doc.createElement("s");
				settingElement.setAttribute("id", key);
				Text valueElement=doc.createTextNode(value);
				settingElement.appendChild(valueElement);
				settingsElement.appendChild(settingElement);
			});
			
			rootElement.appendChild(cmdElement);
			rootElement.appendChild(settingsElement);
			
			Element crcElement=doc.createElement("crc");
			Text ff=doc.createTextNode("FFFFFFFF");
			crcElement.appendChild(ff);
			rootElement.appendChild(crcElement);
			
			//Transform the doc.
			TransformerFactory tf=TransformerFactory.newInstance();
			Transformer t=tf.newTransformer();
			DOMSource source=new DOMSource(doc);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			StreamResult result=new StreamResult(baos);
			t.transform(source, result);
			
			int actualCRC=NKVCRC.calculate(baos.toByteArray());
			String hexCRC=Integer.toHexString(actualCRC).toUpperCase();
			if(hexCRC.length()<8) {
				hexCRC=String.format("%8s", hexCRC).replaceAll(" ", "0");
			}
			
			ff.setNodeValue(hexCRC);
			source=new DOMSource(doc);
			baos.reset();
			result=new StreamResult(baos);
			t.transform(source, result);
			
			return new String(baos.toByteArray());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Fill the specified buffer with data from the device.
	 * @param target
	 * @throws IOException
	 */
	private void fetch(byte[] target) throws IOException {
		int actuallyRead=0;
		int required=target.length;
		log.debug("Need to read "+required+" to fill buffer");
		while(actuallyRead<required) {
			actuallyRead+=fromDevice.read(target,actuallyRead,required-actuallyRead);
			log.debug("Now read "+actuallyRead);
		}
	}
	
	
	
}
