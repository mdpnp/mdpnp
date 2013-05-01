package org.mdpnp.devices.cpc.bernoulli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mdpnp.devices.io.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class Bernoulli implements ContentHandler, ErrorHandler {
	public Bernoulli() {
		
	}
	
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public final static String CMD_REQUEST_NIBP = "*NIBP";
	
	/**
	 * Send command a command
	 * @param out to write command
	 * @param in to read response
	 * @param bid bed identifier
	 * @param cmd command identifier
	 * @return true if success
	 * @throws IOException
	 */
	public static boolean sendCommand(OutputStream out, InputStream in, String bid, String cmd) throws IOException {
	    byte[] bytes = ("<?xml version=\"1.0\" encoding=\"utf-8\"?><cpc><device bid=\""+bid+"\"><cmd>"+cmd+"</cmd></device></cpc>").getBytes("UTF-8");
	    out.write(bytes);
	    out.flush();
        int ret = in.read();
        return 80 == ret;
	}
	
	public static boolean sendCommand(Socket sock, String bid, String cmd) throws IOException {
        return sendCommand(sock.getOutputStream(), sock.getInputStream(), bid, cmd);
    }
	
	public static boolean sendCommand(String host, int port, String bid, String cmd) throws IOException {
	    Socket sock = new Socket(host, port);
	    boolean returnValue = sendCommand(sock, bid, cmd);
	    sock.close();
	    return returnValue;
	}
	
	
	public static void sendSubscription(OutputStream out) throws IOException {
		byte[] bytes = ("<cpc seq=\"1\" datetime=\""+dateFormat.format(new Date())+"\" version=\"Not for patient use!\" type=\"GUI\" bid=\"UniqueIdForGui\" conntype=\"subscriber\"><device bid=\"*\"/></cpc>").getBytes("UTF-8"); 
		out.write(bytes);
		out.flush();
		 

//		CPCSubscription cpc = new CPCSubscription();
//		JAXBContext jc = JAXBContext.newInstance(CPCSubscription.class, CPCSubscription.Device.class);
//		Marshaller m = jc.createMarshaller();
//		m.marshal(cpc, out);
//		out.flush();
	}

	
	
	
	
	private SAXParser saxParser;
	
	@SuppressWarnings("unused")
	private static final boolean consumeProcessingDirective(InputStream is) throws IOException {
		if(is.read() == '<') {
			if(is.read() == '?') {
				while(is.read() != '>') {
					
				}
				return true;
			}
		} 
		return false;
	}
	
	private static final class FilterDirectiveInputStream extends java.io.FilterInputStream {

		private final byte[] initialBytes;
		private int i;
		
		protected FilterDirectiveInputStream(InputStream in, byte[] initialBytes) {
			super(in);
			this.initialBytes = initialBytes;
		}
		
		private final int[] buffer = new int[1];
		private int n = 0;
		
		@Override
		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}
		
		@Override
		public int read(byte[] buf, int off, int len) throws IOException {
			for(int i = 0; i < len; i++) {
				int b = read();
				if(b < 0) {
					if(i > 0) {
						return i;
					} else {
						return b;
					}
				} else {
					buf[off+i] = (byte)b;
				}
			}
			return len;
		}
		@Override
		public int read() throws IOException {
			if(i < initialBytes.length) {
				return initialBytes[i++];
			}
			if(n > 0) {
				return buffer[--n];
			}
			
			int b = super.read();
			if('<' == b) {
				b = super.read();
				if('?'==b) {
					while('>' != (b = super.read())) {
						
					}
					return super.read();
				} else {
					buffer[n++] = b;
					return '<';
				}
			} else {
				return b;
			}
		}
	}
	
	public void process(InputStream is) {
		
		try {
			// We're going to make this look like a regular XML document by injecting a root element
			
//			// First eliminate the processing directive
//			if(!consumeProcessingDirective(is)) {
//				throw new IOException("No xml processing directive found");
//			}
			// Then create a stream that will first deliver our root element; then the rest of the stream
			byte[] initialBytes = "<cpcs>".getBytes("UTF-8");
			is = new FilterDirectiveInputStream(is, initialBytes);
			InputSource inputSource = new InputSource(is);

			SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(false);
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(this);
			xmlReader.parse(inputSource);
			
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage(), e);
		} catch (SAXException e) {
			log.error(e.getMessage(), e);
		}
		
	}

	public void status(String status) {
	    
	}
	
	public void location(String location) {
	    
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
//		log.trace("setDocumentLocator("+locator+")");
	}

	@Override
	public void startDocument() throws SAXException {
//		log.trace("startDocument");
	}

	@Override
	public void endDocument() throws SAXException {
//		log.trace("endDocument");
//		saxParser.reset();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
//		log.trace("startPrefixMapping("+prefix+","+uri+")");
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
//		log.trace("endPrefixMapping("+prefix+")");
	}

	private String currentSetting = null;
	private String currentMeasurement = null;
	private String currentMeasurementGroup = null;
	@SuppressWarnings("unused")
	private String currentDeviceSequence = null;
	@SuppressWarnings("unused")
	private String currentCpcUpdateSequence = null;
	private Map<String, String> measurementGroup = new HashMap<String, String>();
	private boolean statusOn, locationOn;
	
	protected void setting(String name, String value) {
		measurement(name, value);
	}
	
	protected void measurement(String name, String value) {
		
	}
	
	protected void measurementGroup(String name, Number[] n, double msPerSample) {
		
	}
	
	protected void measurementGroup(String name, Map<String,String> values) {

		double hertz = Double.parseDouble(values.get("Hz"));
		byte[] wave = Base64.decodeFast(values.get("Wave"));
		int points = Integer.parseInt(values.get("Points"));
		int pointBytes = Integer.parseInt(values.get("PointBytes"));
		int offset = Integer.parseInt(values.get("Offset"));
		int gain = Integer.parseInt(values.get("Gain"));
		Number[] n = new Number[points];
		ByteBuffer bb = ByteBuffer.wrap(wave).order(ByteOrder.BIG_ENDIAN);

		switch(pointBytes) {
		case 1:
			for(int i = 0; i < points; i++) {
				n[i] = gain * (0xFF & bb.get()) + offset;
			}
			break;
		case 2:
			ShortBuffer sb = bb.asShortBuffer();
			for(int i = 0; i < points; i++) {
				n[i] = gain * (0xFFFF & sb.get()) + offset;
			}
			break;
		case 4:
			IntBuffer ib = bb.asIntBuffer();
			for(int i = 0; i < points; i++) {
				n[i] = gain * (0xFFFFFFFFL & ib.get()) + offset;
			}
			break;
		}
		measurementGroup(name, n, 1000.0 * (1.0 / hertz));
	}
	
	protected void device(String bid, String make, String model) {
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
	    if("measurements".equals(qName)) {
	        
	    } else if("cpcs".equals(qName)) {
	    } else if("settings".equals(qName)) {
	    } else if("cpc".equals(qName)) {
			currentCpcUpdateSequence = atts.getValue("seq");
		} else if("device".equals(qName)) {
			currentDeviceSequence = atts.getValue("seq");
			
			String make = atts.getValue("make");
			String model = atts.getValue("model");

			device(atts.getValue("bid"), make, model);
		} else if("s".equals(qName)) {
			currentSetting = atts.getValue("name");
		} else if("m".equals(qName)) {
			currentMeasurement = atts.getValue("name");
		} else if("mg".equals(qName)) {
			currentMeasurementGroup = atts.getValue("name");
		} else if("alarms".equals(qName)) {
			// TODO alarms
		} else if("status".equals(qName)) {
		    statusOn = true;
		} else if("location".equals(qName)) {
		    locationOn = true;
		}else {
			log.debug("Unknown startElement("+uri+","+localName+","+qName+","+attributes(atts)+")");
		}
//		log.trace("startElement("+uri+","+localName+","+qName+","+atts+")");
	}

	private static final String attributes(Attributes atts) {
	    StringBuilder sb = new StringBuilder("{");
	    for(int i = 0; i < atts.getLength(); i++) {
	        sb.append(atts.getQName(i)).append("=").append(atts.getValue(i));
	    }
	    sb.append("}");
	    return sb.toString();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("cpc".equals(qName)) {
			currentCpcUpdateSequence = null;
		} else if("device".equals(qName)) {
			currentDeviceSequence = null;
		} else if("s".equals(qName)) {
			currentSetting = null;
		} else if("m".equals(qName)) {
			currentMeasurement = null;
		} else if("mg".equals(qName)) {
			measurementGroup(currentMeasurementGroup, measurementGroup);
			measurementGroup.clear();
			currentMeasurementGroup = null;
		} else if("alarms".equals(qName)) {
			// TODO alarms
	    } else if("status".equals(qName)) {
	        statusOn = false;
	    } else if("location".equals(qName)) {
	        locationOn = false;
	    } else if("settings".equals(qName)) {
	    } else if("measurements".equals(qName)) {
		} else {
			log.debug("No context for endElement("+uri+","+localName+","+qName+")");
		}
//		log.trace("endElement("+uri+","+localName+","+qName+")");
	}
	private static final Logger log = LoggerFactory.getLogger(Bernoulli.class);
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(null != currentSetting) {
			setting(currentSetting, new String(ch, start, length));
		} else if(null != currentMeasurement) {
			if(currentMeasurementGroup != null) {
				measurementGroup.put(currentMeasurement, new String(ch, start, length));
			} else {
				measurement(currentMeasurement, new String(ch, start, length));
			}
		} else if(statusOn) {
		    status(new String(ch, start, length).intern());
		} else if(locationOn) {
		    location(new String(ch, start, length).intern());
		} else {
			log.debug("No context for characters:"+new String(ch, start, length));
		}
//		log.trace("characters("+Arrays.toString(ch)+","+start+","+length+")");
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
//		log.trace("ignorableWhitespace("+Arrays.toString(ch)+","+start+","+length+")");
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		log.trace("processingInstruction("+target+","+data+")");
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		log.trace("skippedEntity("+name+")");
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		log.info("warning("+exception+")");
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		log.warn("error("+exception+")");
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		log.error("fatalError("+exception+")");
		
	}
}
