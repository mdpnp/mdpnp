package org.mdpnp.devices.philips.intellivue.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class IPAddressInformation implements Value {

	private final byte[] macAddress = new byte[6];
	private final byte[] ipAddress = new byte[4];
	private final byte[] subnetMask = new byte[4];

	public IPAddressInformation() {
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		bb.get(macAddress);
		bb.get(ipAddress);
		bb.get(subnetMask);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		bb.put(macAddress);
		bb.put(ipAddress);
		bb.put(subnetMask);
	}
	
	@Override
	public java.lang.String toString() {
		InetAddress inetAddress = null;
		try {
			inetAddress = getInetAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException();
		}
		return "[macAddress="+Arrays.toString(macAddress)+",ipAddress="+Arrays.toString(ipAddress)+"inetAddress="+inetAddress+",subnetMask="+Arrays.toString(subnetMask)+"]";
	}
	
	public void setInetAddress(InetAddress addr) {
		System.arraycopy(addr.getAddress(), 0, ipAddress, 0, 4);
	}
	
	public InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getByAddress(ipAddress);
	}

	public byte[] getSubnetMask() {
		return subnetMask;
	}
	public byte[] getMacAddress() {
		return macAddress;
	}
	public byte[] getIpAddress() {
		return ipAddress;
	}
}
