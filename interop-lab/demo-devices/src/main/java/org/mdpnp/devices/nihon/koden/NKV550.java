package org.mdpnp.devices.nihon.koden;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceIdentityBuilder;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

import ice.ConnectionState;
import ice.ConnectionType;
import ice.DeviceIdentity;
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
	 * The XML node name indicating device data
	 */
	private static final String DEVICE="device";
	
	/**
	 * The XML node name indicating waveforms.
	 */
	private static final String WAVEFORMS="waveforms";
	
	/**
	 * The XML node name indicating an individual waveform.
	 */
	private static final String WAVEFORM="w";
	
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
			rosetta.MDC_PRESS_AWAY.VALUE,
			rosetta.MDC_DIM_CM_H2O.VALUE,
			rosetta.MDC_VOL_AWAY_TIDAL.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			rosetta.MDC_PRESS_AWAY.VALUE,
			"",	//Measured SPO2
			""	//Measures CO2
		};
	
	private InstanceHolder<SampleArray>[] waveformInstances=new InstanceHolder[WAVEFORM_TYPES.length];
	
	private Number[][] waveformBuffers=new Number[WAVEFORM_TYPES.length][520];
	
	private int[] waveformBufferLength=new int[WAVEFORM_TYPES.length];
	
	/**
	 * The thread that reads from the device and publishes.
	 */
	private Thread readLoop;
	
	private final DeviceClock.WallClock ourClock;
	
	private int publishCount=0;

	public NKV550(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		super(subscriber, publisher, eventLoop);
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();
		ourClock=new DeviceClock.WallClock();
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
		askForWaveforms();
		keepGoing=true;
		startReadLoop();
	}
	
	private void _connect() throws IOException {
		deviceSocket=new Socket(address, port);
		fromDevice=new BufferedInputStream(deviceSocket.getInputStream());
		toDevice=new BufferedOutputStream(deviceSocket.getOutputStream());
	}
	
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
			System.err.println("Wrote the waveformsPlease XML");
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
				byte eightBytes[]=new byte[8];
				while(keepGoing) {
					try {
						int eightByteCount=0;
						while(eightByteCount<8) {
							try {
								eightByteCount=fromDevice.read(eightBytes);
							} catch (SocketException se) {
								_connect();
							}
						}
						String _blockSize=new String(eightBytes);
						int blockSize=Integer.parseInt(_blockSize);
						byte blockBytes[]=new byte[blockSize];
						int actuallyRead=0;
						while(actuallyRead<blockSize) {
							actuallyRead+=fromDevice.read(blockBytes,actuallyRead,blockSize-actuallyRead);
						}
						//System.err.println("Read "+actuallyRead+" bytes from device to get XML block");
						String xmlBlock=new String(blockBytes);
						
						DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
						DocumentBuilder db=dbf.newDocumentBuilder();
						Document xmlDoc=db.parse(new InputSource(new StringReader(xmlBlock)));
						if(xmlDoc.hasChildNodes()) {
							processChildren(xmlDoc);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.err.println("Read loop finishing as keepGoing now false");
			}
		};
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
					processDeviceNode(elem);
				}
				if(nodeName.equals(WAVEFORMS)) {
					processWaveformsNode(elem);
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	private void processDeviceNode(Element deviceElement) {
		String serialNumber=deviceElement.getAttribute("sn");
		//System.err.println("Device serial number is "+serialNumber);
		//Simple check for first time
		if(! deviceIdentity.serial_number.equals(serialNumber)) {
			deviceIdentity.manufacturer="Nihon Koden";
			deviceIdentity.model="NKV550";
			deviceIdentity.serial_number=serialNumber;
			//deviceIdentity.unique_device_identifier=DeviceIdentityBuilder.randomUDI();
			//Thread.dumpStack();
			stateMachine.transitionIfLegal(ConnectionState.Connected, "Receiving date from NKV 550 with serial number "+serialNumber);
			writeDeviceIdentity();
		}
		processChildren(deviceElement);
		
	}
	
	
	
	private void processWaveformsNode(Element waveformElement) {
		String _numdata=waveformElement.getAttribute("numdata");
		int numdata=Integer.parseInt(_numdata);
		//TODO: MAke this a class member and check against individual waveforms
//		System.err.println("numdata in waveforms element is "+numdata);
		
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
		
		if(id!=1 && id!=2) {
			//System.err.println("Temp not doing waveform "+id);
			return;
		}
		
//		System.err.println("waveform "+id+ " datasize "+datasize+" datagain "+datagain+" zeroffset "+zeroffset);
		
		String base64Encoded=waveformElement.getTextContent();
//		System.err.println(base64Encoded);
		Decoder d=Base64.getDecoder();
		byte bytes[]=d.decode(base64Encoded);
//		System.err.println("decoded bytes of length "+bytes.length);
		
		//All waveforms so far seem to be datasize=2...
		if(datasize!=2) {
			log.warn("datasize for waveform was not 2 but "+datasize);
			return;
		}
		
		int expectedNumOfPoints=bytes.length/datasize;
//		System.err.println("Expected num of points is "+expectedNumOfPoints);
		
		/*
		 * Presumably we will encounter the customary annoyance of a short
		 * not being the appropriate holder for short, because of not being
		 * able to do an unsigned in Java. 
		 */
		Number wave[]=new Number[expectedNumOfPoints];
		int j=0;
		
		ByteBuffer bb=ByteBuffer.wrap(bytes);
				
		for(int i=0;i<bytes.length;i+=datasize) {
			//System.err.println("Using bytes "+i+" and "+ (i+1));
			int fromTwoBytes = Short.toUnsignedInt(bb.getShort());
			float finalVal = (float)fromTwoBytes/datagain - zeroffset;
			wave[j++]=finalVal;
		}
		//System.err.println();
		
		//System.err.println("wave is "+ArrayUtils.toString(wave));
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
			
		//}
		
		/*
		protected InstanceHolder<ice.SampleArray> sampleArraySample(InstanceHolder<ice.SampleArray> holder,
                Number[] newValues,
                String metric_id, String vendor_metric_id, int instance_id, String unit_id, int frequency,
                DeviceClock.Reading timestamp)
		*/
		
		
		
//		sampleArraySample(waveformInstances[id], wave, ourClock.instant());
		/*
		 * wave = sampleArraySample(wave, waveValues, rosetta.MDC_PRESS_BLD.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
		 */
		
	}

	@Override
	protected String iconResourceName() {
		// TODO Auto-generated method stub
		return "nkv550.png";
	}
	
	
	
}
