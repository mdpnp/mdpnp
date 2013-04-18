package org.mdpnp.transport.jgroups;

import java.io.InputStream;
import java.io.OutputStream;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST2;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.transport.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGroupsWrapper implements GatewayListener, Receiver, Wrapper {
	private final Gateway gateway;
//	private final Role role;
	private final JChannel outChannel, inChannel;
	
	
	public void tearDown() {
		outChannel.disconnect();
		inChannel.disconnect();
		
		outChannel.close();
		inChannel.close();
	}
	
	private static final ProtocolStack stack(JChannel channel) throws Exception {
		ProtocolStack stack=new ProtocolStack();

        channel.setProtocolStack(stack);

        stack.addProtocol(new UDP()/*.setValue("bind_addr",

                                              InetAddress.getByName("192.168.1.5"))*/)

                .addProtocol(new PING())

                .addProtocol(new MERGE2())

                .addProtocol(new FD_SOCK())

                .addProtocol(new FD_ALL().setValue("timeout", 12000)

                                         .setValue("interval", 3000))

                .addProtocol(new VERIFY_SUSPECT())

                .addProtocol(new BARRIER())

                .addProtocol(new NAKACK2())

                .addProtocol(new UNICAST2())

                .addProtocol(new STABLE())

                .addProtocol(new GMS())

                .addProtocol(new UFC())

                .addProtocol(new MFC())

                .addProtocol(new FRAG2().setValue("frag_size", 1024)); 

        stack.init();    
        return stack;
	}
	
	public JGroupsWrapper(Role role, Gateway gateway) throws Exception {
		this.gateway = gateway;
		outChannel = new JChannel(false);
		
		inChannel = new JChannel(false);
		
		stack(inChannel);
		stack(outChannel);
		
		outChannel.setDiscardOwnMessages(true);
		
		inChannel.setDiscardOwnMessages(true);
		
		inChannel.receiver(this);
		
		for(String out : role.getSendPartitions()) {
		    outChannel.connect(out);
		}
		for(String in : role.getReceivePartitions()) {
		    inChannel.connect(in);
		}

		gateway.addListener(this);
	}
	
	@Override
	public void update(IdentifiableUpdate<?> update) {
		try {
			outChannel.send(null, update);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void receive(Message msg) {
		IdentifiableUpdate<?> update = (IdentifiableUpdate<?>) msg.getObject();
		gateway.update(this, update);
	}
	@Override
	public void getState(OutputStream output) throws Exception {
		
	}
	@Override
	public void setState(InputStream input) throws Exception {
		
	}
	@Override
	public void viewAccepted(View new_view) {
		
	}
	@Override
	public void suspect(Address suspected_mbr) {
		
	}
	@Override
	public void block() {
		
	}
	@Override
	public void unblock() {
		
	}
	
}
