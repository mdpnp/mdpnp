package org.mdpnp.transport.mcast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
//import java.net.StandardProtocolFamily;
//import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.mdpnp.transport.Wrapper;
import org.mdpnp.transport.dds.rti.DDSInterface;
//import org.mdpnp.devices.impl.intellivue.NetworkConnection;
//import org.mdpnp.devices.impl.intellivue.NetworkLoop;

public class MulticastWrapper implements Wrapper, GatewayListener /*, NetworkConnection*/ {
//	private final Gateway gateway;
//	private final static int PORT = 5000;
//	private final InetAddress address;
//	private final ByteBuffer outBuffer = ByteBuffer.allocate(65536);
//	private final ByteBuffer inBuffer = ByteBuffer.allocate(65536);
//	private final SelectionKey selectionKey;
	private final DatagramChannel datagramChannel;
	private final List<IdentifiableUpdate<?>> outQueue = new ArrayList<IdentifiableUpdate<?>>();
	
//	private final ByteArrayOutputStream out = new ByteArrayOutputStream(65536);
//	private final byte[] inBytes = new byte[65536];
//	private final ByteArrayInputStream in = new ByteArrayInputStream(inBytes);
//	
//	private final ObjectInputStream ois = new ObjectInputStream(in);
//	private final ObjectOutputStream oos = new ObjectOutputStream(out);

	
	protected static NetworkInterface defaultNetworkInterface() throws SocketException {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements()) {
			NetworkInterface ni = e.nextElement();
			if(!ni.isLoopback() && ni.isUp()) {
				return ni;
			}
		}
		throw new IllegalStateException("No non-local network interface is up");
	}
	
	public MulticastWrapper(InetAddress address, Gateway gateway/*, NetworkLoop networkLoop*/) throws IOException {
//		this.gateway = gateway;
//		this.address = address;

		NetworkInterface iface = defaultNetworkInterface();
		System.out.println(iface);
		datagramChannel = DatagramChannel.open();
//		datagramChannel.
		// JRE 1.7 .. not sure we're ready to upgrade
//		StandardProtocolFamily.INET)
//		.setOption(StandardSocketOptions.SO_REUSEADDR, true)
//		datagramChannel.bind(new InetSocketAddress(5000));
//		.setOption(StandardSocketOptions.IP_MULTICAST_IF, iface);
//		datagramChannel.configureBlocking(false);
//		datagramChannel.connect(new InetSocketAddress(address, 5000));
//		MembershipKey key = datagramChannel.join(address, iface);
		/*selectionKey = networkLoop.register(this, datagramChannel);*/
		
//		gateway.addListener(this);
	}

	@Override
	public void update(IdentifiableUpdate<?> update) {
		synchronized(outQueue) {
			outQueue.add(update);
//			selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
		}
		
	}

//	@Override
	public void read(SelectionKey sk) throws IOException {
//		in.reset();
//		
//		datagramChannel.receive(ByteBuffer.wrap(inBytes));
//		Object o;
//		try {
//			o = ois.readObject();
//			System.out.println(o);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		System.out.println(new String(inBuffer.array(), 0, inBuffer.position()));
//		System.out.println("IN:"+inBuffer);
		

	}

//	@Override
	public void write(SelectionKey sk) throws IOException {
		synchronized(outQueue) {
			if(!outQueue.isEmpty()) {
				IdentifiableUpdate<?> update = outQueue.remove(0);
				System.out.println(DDSInterface.findPersistent(update.getClass()).getClazz());
//				out.reset();
//				oos.writeObject(update);
				
//				ByteBuffer.wrap(out.toByteArray());
//				outBuffer.clear();
//				outBuffer.put("HELLO WORLD".getBytes());
//				outBuffer.flip();
//				System.out.println("OUT:"+outBuffer);
//				datagramChannel.send(ByteBuffer.wrap(out.toByteArray()), new InetSocketAddress(address, PORT));
			}
			if(outQueue.isEmpty()) {
//				selectionKey.interestOps(SelectionKey.OP_READ);
			} else {
//				selectionKey.interestOps(SelectionKey.OP_WRITE);
			}
		}

	}
	public static void main(String[] args) throws IOException, InterruptedException {
		Gateway gateway = new Gateway();
//		final NetworkLoop nl = new NetworkLoop();
//		Thread t = new Thread(new Runnable() {
//			public void run() {
//				nl.runLoop();
//			}
//		});
//		t.setDaemon(true);
//		t.start();
//		MulticastWrapper wrapper = new MulticastWrapper(InetAddress.getByName("225.0.0.1"),gateway,nl);
		gateway.update(new MutableTextUpdateImpl(PulseOximeter.NAME, "THIS IS MY NAME"));
		
		Thread.sleep(5000L);
//		nl.cancelThreadAndWait();
		
	}

    @Override
    public void tearDown() {
        // TODO Auto-generated method stub
        
    }
}
