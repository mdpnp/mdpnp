package org.mdpnp.apps.testapp.mindware;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BioLabPlayer {

	public BioLabPlayer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String filename=args[0];
		String ipAddr=args[1];
		String port=args[2];
		int targetPort=Integer.parseInt(port);
		
		//OK, this might suck for really big files but we'll just go for it.
		try {
			List<String> allLines=Files.readAllLines(new File(filename).toPath());
			String sampleRateLine=allLines.get(0);
			String[] parts=sampleRateLine.split("\t");
			int sampleRate=500;	//Default.
			if(!parts[0].equals("Sample Rate:")) {
				System.err.println("Missing sample rate - assuming "+sampleRate+" but file may be unreliable");
			} else {
				sampleRate=Integer.parseInt(parts[1].substring(0,parts[1].indexOf('.')));
			}
			
			String channelNamesLine=allLines.get(1);
			String channelNames[]=channelNamesLine.split("\t");
			short channelCount=(short)(channelNames.length-1 &Short.MAX_VALUE);	//First field is the time marker
			
			short version=1;
			
			InetAddress addr=InetAddress.getByName(ipAddr);
			DatagramSocket sender=new DatagramSocket(null);
			sender.connect(addr, targetPort);
			
			sendGeneralInfo(sender, version,sampleRate,channelCount);
			
			sendChannelInfo(sender, channelNames, 1);
			
			ArrayList<ByteBuffer> byteBuffers=new ArrayList<ByteBuffer>();
			for(short i=0;i<channelCount;i++) {
				ByteBuffer bb=ByteBuffer.allocate(1024);
				bb.put( (byte) 3 );
				bb.putInt(0);	//TODO: Work out proper length
				bb.putShort(i);	//Channel number
				bb.mark();
				byteBuffers.add(bb);
			}
			
			//Rest of the contents of each packet comes from reading the rest of the lines in the file
			int totalLines=2;
			while(totalLines<allLines.size()) {
				String nextLine=allLines.get(totalLines);
				String fields[]=nextLine.split("\t");
				if( ( (totalLines-2) % 50 ) == 0 ) {
					/*
					 * The fields for time offsets look like this
					 * 0.000000
					 * 0.002000
					 * 0.004000
					 * 0.006000
					 * 0.008000
					 * 0.010000
					 * 
					 * and so one.  We will handle one timestamp every 50 lines, so our timestamps will be something like
					 * 
					 * 0.100000
					 * 
					 * That is a tenth of a second, or a 100 milliseconds.  We have to parse as a float because of all the decimal places,
					 * then we multiply by 1000 to get a milliseconds offset value.
					 * 
					 */
					Float f=Float.parseFloat(fields[0]);
					Float f2=new Float(f*1000);
					long startTime=System.currentTimeMillis()+ f2.longValue() ;
					for(int i=0;i<byteBuffers.size();i++) {
						byteBuffers.get(i).putLong(startTime);	//Each channel has the same start time.
					}
					//System.err.println("set startTime to "+startTime);
				}
				for(int i=0;i<byteBuffers.size();i++) {
					byteBuffers.get(i).putDouble(Double.parseDouble(fields[i+1]));
				}
				if( ( (totalLines-2) % 50 ) == 49 ) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						//
					}
					sendAllChannelData(sender, byteBuffers);
				}
				totalLines++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	static void sendGeneralInfo(DatagramSocket sender, short version, int sampleRate, short channelCount) throws IOException {
		ByteBuffer bb=ByteBuffer.allocate(15);
		bb.put( (byte)1 );
		bb.putInt( 0 );		//Later on, check a real length from a real packet - but it's not used by the BioLab device at the moment.
		bb.putShort(version);
		bb.putInt(sampleRate);
		bb.putShort(channelCount);
		bb.putShort( (short)0);	//BioLab device also doesn't care about checksums yet.
		DatagramPacket generalPacket=new DatagramPacket(bb.array(), 15);
		sender.send(generalPacket);
	}
	
	static void sendChannelInfo(DatagramSocket sender, String[] channelNames, int divisor) throws IOException {
		//We use 1 as the index number here to skip the time field...
		for(short i=1;i<channelNames.length;i++) {
			ByteBuffer bb=ByteBuffer.allocate(64);
			bb.put((byte)2);
			bb.putInt( 0 );		//Later on, check a real length from a real packet - but it's not used by the BioLab device at the moment.
			bb.putShort(i);
			bb.putInt(divisor);
			bb.put( (byte) (channelNames[i].length() & Byte.MAX_VALUE) );
			bb.put(channelNames[i].getBytes());
			//TODO Find a way of getting the real units.
			bb.put( (byte)1 );
			bb.put("N".getBytes());
			bb.putShort((short)0);
			DatagramPacket channelPacket=new DatagramPacket(bb.array(), bb.position());
			sender.send(channelPacket);
		}
	}
	
	static void sendAllChannelData(DatagramSocket sender, ArrayList<ByteBuffer> byteBuffers) {
		for(int i=0;i<byteBuffers.size();i++) {
			ByteBuffer bb=byteBuffers.get(i);
			bb.putShort( (short) 0 );	//Checksum unused by OpenICE device for now.
			DatagramPacket channelDataPacket=new DatagramPacket(bb.array(), bb.position());
			try {
				sender.send(channelDataPacket);
				bb.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
