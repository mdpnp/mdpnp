package org.mdpnp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class PumpControlUDP {
	public static void main(String[] args) throws NumberFormatException, IOException {
		if(args.length == 0) {
			args = new String[] {"recv", "224.0.0.15", "10250", "C:\\pump_control_1.txt"};
		}
		if("recv".equals(args[0])) {
			recv(true, args[1], Integer.parseInt(args[2]), args[3]);
		} else if("send".equals(args[0])) {
			System.out.println("acknowledged:"+send(true, args[1], Integer.parseInt(args[2]), args[3]));
		}
	}
	protected static boolean acknowledged = false;
	
	public static synchronized boolean send(String content) throws UnknownHostException, IOException {
		return send(false, "224.0.0.15", 10250, content);
	}
	
	public static boolean send(boolean cmdline, final String ip, final int port, String content) throws UnknownHostException, IOException {
		acknowledged = false;
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
			    MulticastSocket insock = null;
				try {
					InetAddress group = InetAddress.getByName(ip);
					insock = new MulticastSocket(port+1);
					insock.joinGroup(group);
					byte[] buffer = new byte[10*1024];
			        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			        insock.receive(data);
			        acknowledged = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				    insock.close();
				}
				
			}
			
		});
		t.setDaemon(true);
		t.start();
		InetAddress group = InetAddress.getByName(ip);
		MulticastSocket s = new MulticastSocket(port);
		s.joinGroup(group);

		if(cmdline) {
		    System.out.println("Sending to " + group + ":"+port + "  " + content.replaceAll("\\n", "\\\\n"));
		}
		
	    DatagramPacket data = new DatagramPacket(
	            content.getBytes(), content.length(), group, port);
	    s.send(data);
	    long giveup = System.currentTimeMillis() + 5000L;
	    while(!acknowledged && System.currentTimeMillis() < giveup) {
	    	try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	s.send(data);
	    }
	    s.close();
	    return acknowledged;
	}
	private static boolean running = true;
	
	public static void recv(boolean cmdline, String ip, int port, String filename) throws IOException {
		InetAddress group = InetAddress.getByName(ip);
		MulticastSocket insock = new MulticastSocket(port);
		MulticastSocket outsock = new MulticastSocket(port+1);
		
		insock.joinGroup(group);
		outsock.joinGroup(group);
		if(cmdline) {
		    System.out.println("Listening on " + group + ":"+port + ", writing to " + filename);
		}
		
		byte[] buffer = new byte[10*1024];
        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
        byte[] outbuffer = "ACKNOWLEDGED".getBytes("ASCII");
        DatagramPacket outdata = new DatagramPacket(outbuffer, outbuffer.length, group, port+1);
        
		while(running) {
			insock.receive(data);
			if(port == data.getPort()) {
			    if(cmdline) {
			        System.out.println("Received from " + data.getSocketAddress() + ", writing...");
			    }
				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data.getData(), data.getOffset(), data.getLength());
				fos.flush();
				fos.close();
				if(cmdline) {
    				System.out.write(data.getData(), data.getOffset(), data.getLength());
    				System.out.println();
    				System.out.println("Done");
				}
				outsock.send(outdata);
				if(cmdline) {
				    System.out.println("Sent acknowledgement");
				}
			}
			
		}
		insock.close();
		outsock.close();

	}

}
