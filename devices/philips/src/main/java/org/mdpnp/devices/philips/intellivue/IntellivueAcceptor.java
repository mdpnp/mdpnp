package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.List;

import org.mdpnp.devices.net.NetworkLoop;
import org.mdpnp.devices.net.TaskQueue;
import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationAcceptImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationImpl;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecification;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecificationType;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry.ApplicationProtocol;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry.TransportProtocol;
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportInvoke;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Set;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.EventReportImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.event.impl.MdsCreateEventImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportInvokeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntellivueAcceptor extends  Intellivue {

    private static final Logger log = LoggerFactory.getLogger(IntellivueAcceptor.class);

    protected final TaskQueue.Task<Void> beacon = new TaskQueue.TaskImpl<Void>() {
        @Override
        public Void doExecute(TaskQueue queue) {

            try {
                final List<Network.AddressSubnet> address = Network.getBroadcastAddresses();




                for(Network.AddressSubnet as : address) {
                    ConnectIndicationImpl ci = new ConnectIndicationImpl();

                    ProtocolSupportEntry e = new ProtocolSupportEntry();
                    e.setAppProtocol(ApplicationProtocol.DataOut);
                    e.setTransProtocol(TransportProtocol.UDP);
                    e.setPortNumber(port);
                    e.setOptions(0);
                    ci.getProtocolSupport().getList().add(e);

                    ci.getIpAddressInformation().setInetAddress(as.getLocalAddress());
                    Network.prefix(ci.getIpAddressInformation().getSubnetMask(), as.getPrefixLength());

                    ByteBuffer bb = ByteBuffer.allocate(5000);
                    bb.order(ByteOrder.BIG_ENDIAN);
                    ci.format(bb);
                    byte[] bytes = new byte[bb.position()];
                    bb.position(0);
                    bb.get(bytes);

                    DatagramSocket ds = new DatagramSocket();
                    DatagramPacket dp = new DatagramPacket(bytes, bytes.length, null, 24005);


                    log.info("Transmit to " + as.getInetAddress());

                    dp.setAddress(as.getInetAddress());

                    ds.send(dp);
                    ds.close();
                }

            } catch (SocketException e1) {
                log.error("failed to emit beacon", e1);
            } catch (IOException e1) {
                log.error("failed to emit beacon", e1);
            }
            return null;
        }
    };
    @Override
    protected void handle(Set set, boolean confirmed) throws IOException {
        super.handle(set, confirmed);
        if(confirmed) {
        }
    }
    @Override

    protected synchronized void handle(SocketAddress sockaddr, org.mdpnp.devices.philips.intellivue.association.AssociationConnect message) {
        super.handle(sockaddr, message);

        AssociationAccept acc = new AssociationAcceptImpl();
        log.debug("Sending accept:"+acc);
        try {
            final DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);

            channel.connect(new InetSocketAddress( ((InetSocketAddress) sockaddr).getAddress(), port));

            networkLoop.register(this, channel);
            send(acc);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        MdsCreateEventImpl m = new MdsCreateEventImpl();
        Attribute<SystemModel> asm = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_MODEL, SystemModel.class);
        Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
        Attribute<ProductionSpecification> ps = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);

        ProductionSpecification.Entry e = new ProductionSpecification.Entry();
        e.getProdSpec().setString("1234567");
        e.setComponentId(ComponentId.ID_COMP_PRODUCT);
        e.setSpecType(ProductionSpecificationType.SERIAL_NUMBER);
        ps.getValue().getList().add(e);
        asm.getValue().setManufacturer("MD PNP");
        asm.getValue().setModelNumber("ICE TEST ONE");
        m.getAttributes().add(asm);
        m.getAttributes().add(as);
        m.getAttributes().add(ps);

        EventReport er = new EventReportImpl();
        er.setEvent(m);
        er.setEventType(OIDType.lookup(ObjectClass.NOM_NOTI_MDS_CREAT.asInt()));

        DataExportInvoke der = new DataExportInvokeImpl();
        der.setCommandType(CommandType.EventReport);
        der.setCommand(er);

        try {
            send(der);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    };
    public IntellivueAcceptor() throws IOException {
        this(DEFAULT_UNICAST_PORT);
    }
    public IntellivueAcceptor(int port) throws IOException {
        super();
        beacon.setInterval(10000L);
        this.port = port;

    }

    private NetworkLoop networkLoop;

    public void accept(final NetworkLoop networkLoop) throws IOException {
        this.networkLoop = networkLoop;
        networkLoop.add(new TaskQueue.TaskImpl<Void>() {
            @Override
            public Void doExecute(TaskQueue queue) {
                try {
                    DatagramChannel channel = DatagramChannel.open();
                    channel.configureBlocking(false);
                    channel.socket().setReuseAddress(true);
                    channel.socket().bind(new InetSocketAddress(port));
                    networkLoop.register(IntellivueAcceptor.this, channel);
                    networkLoop.add(beacon);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }

        });
        networkLoop.add(new TaskQueue.TaskImpl<Void>() {
            @Override
            public Void doExecute(TaskQueue queue) {
                try {
                    final DatagramChannel channel = DatagramChannel.open();
                    channel.configureBlocking(false);
                    channel.socket().setReuseAddress(true);
                    channel.socket().bind(new InetSocketAddress(DEFAULT_UNICAST_PORT));

                    networkLoop.register(IntellivueAcceptor.this, channel);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        });


    }
    protected final int port;

    public static void main(String[] args) throws IOException {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : Intellivue.DEFAULT_UNICAST_PORT;
        final NetworkLoop networkLoop = new NetworkLoop();
        final IntellivueAcceptor ia = new IntellivueAcceptor( port);
        ia.accept(networkLoop);
        networkLoop.runLoop();

    }
}
