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
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
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

import ice.ConnectionType;

/**
 * An OpenICE device interface for communicating with a Nihon Koden NKV-550<br/>
 * 
 * The implementation is based on a document titled <code>TC-RC1 Rev P1 NKV550 Remote Control
 * Communication Protocol.docx</code>
 * @author simon
 *
 */
public class WithMain /*extends AbstractConnectedDevice*/ {
	
	/**
	 * The default port to connect to to receive data
	 */
	private static int defaultPort=15376;
	
	/**
	 * Logger instance.  For logging...
	 */
	private static final Logger log = LoggerFactory.getLogger(WithMain.class);
	
	/**
	 * A socket connected to the device.
	 */
	private Socket deviceSocket;
	
	/**
	 * The input stream to read from the device
	 */
	private BufferedInputStream fromDevice;
	
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
	 * The XML node name indicating waveforms.
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
	
	private final FileWriter[] waveformWriters=new FileWriter[WAVEFORM_TYPES.length];

	private InetAddress address;

	private int port;
	

	public WithMain(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		//super(subscriber, publisher, eventLoop);
		// TODO Auto-generated constructor stub
	}

	//@Override
	public boolean connect(String address) {
		if(address==null || address.length()==0) {
			//TODO: Listen for the broadcast UDP packet from the 550.
			log.error("connect called with empty address");
			return false;
		}
		
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

	//@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	//@Override
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
					try {
						actuallyRead+=fromDevice.read(blockBytes,actuallyRead,blockSize-actuallyRead);
					} catch (SocketException se) {
						
					}
				}
				System.err.println("Read "+actuallyRead+" bytes from device to get XML block");
				String xmlBlock=new String(blockBytes);
				System.err.println("XML is "+xmlBlock);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void processChildren(Node node) {
		NodeList nodes=node.getChildNodes();
		System.err.println("nodes length is "+nodes.getLength());
		for(int i=0;i<nodes.getLength();i++) {
			Node n=nodes.item(i);
			System.err.println("Got node "+i+" with name "+n.getNodeName());
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
		System.err.println("Device serial number is "+serialNumber);
		processChildren(deviceElement);
		
	}
	
	private void processWaveformsNode(Element waveformElement) {
		String _numdata=waveformElement.getAttribute("numdata");
		int numdata=Integer.parseInt(_numdata);
		
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
		
		System.err.println("waveform "+id+ " datasize "+datasize+" datagain "+datagain+" zeroffset "+zeroffset);
		
		String base64Encoded=waveformElement.getTextContent();
		System.err.println(base64Encoded);
		Decoder d=Base64.getDecoder();
		byte bytes[]=d.decode(base64Encoded);
		//System.err.println("decoded bytes of length "+bytes.length+" is "+ArrayUtils.toString(bytes));
		
		//All waveforms so far seem to be datasize=2...
		if(datasize!=2) {
			log.warn("datasize for waveform was not 2 but "+datasize);
			return;
		}
		
		/*
		 * Presumably we will encounter the customary annoyance of a short
		 * not being the appropriate holder for short, because of not being
		 * able to do an unsigned in Java. 
		 */
		float wave[]=new float[bytes.length/datasize];
		int j=0;
		for(int i=0;i<bytes.length;i+=datasize) {
			//System.err.println("Using bytes "+i+" and "+ (i+1));
			int fromTwoBytes = 0;
			//fromTwoBytes |= (bytes[i]<<8) | (bytes[i+1]);
			fromTwoBytes |= (bytes[i+1]<<8) | (bytes[i]);	//Looks better in some cases?
			//System.err.print(fromTwoBytes+" ");
			float finalVal = (float)fromTwoBytes/datagain - zeroffset;
			wave[j++]=finalVal;
		}
		//System.err.println();
		
		//System.err.println("wave is "+ArrayUtils.toString(wave));
		/*
		 * wave is now a float array, perfect for publishing...
		 */
		FileWriter thisWriter=null;
		if(waveformWriters[id]==null) {
			try {
				thisWriter=new FileWriter("/tmp/"+id+"_waveform.csv",true);
				waveformWriters[id]=thisWriter;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else {
			thisWriter=waveformWriters[id];
		}
		PrintWriter pw=new PrintWriter(thisWriter,false);
		for(j=0;j<wave.length;j++) {
			pw.println(wave[j]);
		}
		pw.flush();
		
	}
	
	/*
	 * Just for testing.
	 */
	private WithMain() {
		
	}
	
	private void closeWriters() {
		for(int i=0;i<waveformWriters.length;i++) {
			try {
				waveformWriters[i].close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		WithMain device=new WithMain();
		try {
			InetAddress addr=InetAddress.getByName("192.168.90.146");
			device.connect(addr, WithMain.defaultPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			device.closeWriters();
		}
		
	}

}
