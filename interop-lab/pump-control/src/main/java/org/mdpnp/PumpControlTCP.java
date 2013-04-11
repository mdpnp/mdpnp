package org.mdpnp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PumpControlTCP {
	public static void send(String host, int port, String content) throws UnknownHostException, IOException {
		Socket client = new Socket(host, port);
		client.getOutputStream().write(content.getBytes("ASCII"));
		client.getOutputStream().flush();
		client.close();
	}
	
	private static boolean running = true;
	
	public static void main(String[] args) throws IOException {
		final int port = Integer.parseInt(args[0]);
		final String filename = args[1];
		
		ServerSocket serverSocket = new ServerSocket(port);
		
		System.out.println("Listening on " + serverSocket.getLocalSocketAddress() + ", writing to " + filename);
		
		byte[] buffer = new byte[8192];
			
		while(running) {
			Socket client = serverSocket.accept();
			System.out.println("Connection from " + client.getRemoteSocketAddress() + ", writing...");
			int r;
			InputStream is = client.getInputStream();
			FileOutputStream fos = new FileOutputStream(filename);
			while( (r = is.read(buffer, 0, buffer.length)) > 0) {
				fos.write(buffer, 0, r);
				System.out.write(buffer, 0, r);
			}
			fos.flush();
			fos.close();
			System.out.println();
			System.out.println("Connection Closed");
			client.close();
		}
		
		serverSocket.close();
	}
}
