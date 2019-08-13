package org.mdpnp.devices.denver.mseries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceClock.Reading;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

import ice.Numeric;

public class MSeriesScale extends AbstractSerialDevice {
	
	private static final Logger log = LoggerFactory.getLogger(MSeriesScale.class);
	private InstanceHolder<Numeric> holder;
	private DeviceClock defaultClock;	//Will map to DDS time via default implementation
	
	/**
	 * These patterns match the formats given in the documentation for the balance,
	 * and/or formats in Dave Arney's kindly supplied sample file (that in fact, didn't
	 * match any of the formats given in the docs).  The documented formats are
	 * 
	 * Output can be in one of the following forms:
		<table>
		<tr><th>Stable</th><th>Unstable</th></tr>
		<tr><td>1 + 0000.0002</td><td>U + 0000.0002</td></tr>
		<tr><td>S + 0000.0003g</td><td>SD + 0000.0003g</td></tr>
		<tr><td>ST + 0000.0003</td><td>US + 000.0003</td></tr>
		<tr><td>+ 0000.0003</td><td>+ 000.0003</td></tr>
		<tr><td>+ 0000.0003 grams</td><td>+ 0000.0003 unstable</td></tr>
		<tr><td>12.4 g</td><td>&lt;--This came from Dave's sample</td></tr>
		</table>
		The output string is terminated with a &lt;cr&gt;&lt;If&gt;.
	 */
	Pattern[] patterns=new Pattern[] {
			/**
			 * a number, zero to four times, followed by a decimal point, followed by zero or one spaces, followed by zero or one letter g
			 * This pattern can actually handle all possible Stable forms.  See the Javadoc for getFloatFromLine to understand how.
			 */
			Pattern.compile("[0-9]{0,4}\\.[0-9]{0,4} ?g?")
	};
	//TODO - array of Matcher as well?
	

	public MSeriesScale(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		this(subscriber, publisher, eventLoop,1);
	}

	public MSeriesScale(Subscriber subscriber, Publisher publisher, EventLoop eventLoop, int countSerialPorts) {
		super(subscriber, publisher, eventLoop, countSerialPorts);
		deviceIdentity.manufacturer="Denver";
		deviceIdentity.model="M-Series";
		deviceIdentity.operating_system="OS";
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		writeDeviceIdentity();
		defaultClock=new MSeriesScaleClock();
	}
	
	//Do we not have a better built in ticking clock somewhere?
	private class MSeriesScaleClock implements DeviceClock {

		@Override
		public Reading instant() {
			return new Reading() {
				
				@Override
				public Reading refineResolutionForFrequency(int hertz, int size) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean hasDeviceTime() {
					// TODO Auto-generated method stub
					return true;
				}
				
				@Override
				public Instant getTime() {
					// TODO Auto-generated method stub
					Instant i=Instant.ofEpochMilli(System.currentTimeMillis());
					System.out.println("MSeriesScaleClock returning "+i.toString());
					return i;
				}
				
				@Override
				public Instant getDeviceTime() {
					Instant i=Instant.ofEpochMilli(System.currentTimeMillis());
					return i;
				}
			};
		}
		
	}
	
	

	@Override
	protected long getMaximumQuietTime(int idx) {
		return Long.MAX_VALUE;	//Obviously this is a VERY large number, but since we have no negotiation etc...
	}

	@Override
	protected void doInitCommands(int idx) throws IOException {
		log.info("MSeriesScale does not do any init commands at the moment");

	}

	@Override
	protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
		//We don't need to do any output - we just read the input stream as CR/LF terminated lines.
		BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
		boolean connected=false;
		String lineWithValue=null;
		while( (lineWithValue=br.readLine()) != null ) {
			float f=getFloatFromLine(lineWithValue);
			//Publish a metric...
			Reading r=defaultClock.instant();
			numericSample(holder, f, rosetta.MDC_ATTR_PT_WEIGHT.VALUE , rosetta.MDC_ATTR_PT_WEIGHT.VALUE, 0, rosetta.MDC_DIM_G.VALUE, new DeviceClock.CombinedReading(r, r));
			if(!connected) {
				reportConnected("data was received");
			}

		}

	}
	
	/**
	 * 
	 * The one regexp we have can be used to handle all known cases of stable input.
	 * This works because the start() and end() methods on the matcher allow us to
	 * trim the input value down the matched segment, and then do replacements of any
	 * space characters, plus characters and the g character  So, for<br/>
	 * 
	 * 1 + 0000.0002 the trimmed match is 0000.0002 , from which we can make a float<br/>
	 * S + 0000.0003g the trimmed match is 0000.0003g, from which we can replace the g and make a float<br/>
	 * ST + 0000.0003 the trimmed match is 0000.0003, from which we can make a float<br/>
	 * + 0000.0003 the trimmed match is 0000.0003, from which we can make a float<br/>
	 * + 0000.0003 grams the trimmed match is 0000.0003 g, from which we can replace the space and the g and make a float<br/>
	 * 12.9 g the trimmed match is 12.9 g, from which we can replace the space and the g and make a float</br>
	 *
	 * @param line
	 * @return float value from line
	 */
	private float getFloatFromLine(String line) {
		for(int i=0;i<patterns.length;i++) {
			Matcher m=patterns[i].matcher(line);
			while(m.find()) {
				//System.out.println("It's a match...");
				if( m.groupCount() > 0 ) {
					//More than one match...
					for(int j=0;j<m.groupCount();j++) {
						System.out.println("match group "+j+" is "+m.group(j));
					}
				} else {
					int start=m.start();
					int end=m.end();
					System.out.println("Matched string is "+line.substring(start, end));
					String stripped=line.substring(start,end).replaceAll("[ +g]", "")/*.replaceAll("[A-z]", "")*/;
					System.out.println("Stripped "+line+" is "+stripped);
					Float f=Float.valueOf(stripped);
					return f;
				}
			}
		}
		log.error("getFloatFromLine could not match input line "+line);
		return -1f;
	}

	@Override
	public SerialProvider getSerialProvider(int idx) {
		SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(300, DataBits.Eight, Parity.None, StopBits.Two);
        return serialProvider;
	}

	@Override
	protected String iconResourceName() {
		return "balance.png";
	}
	
	

}
