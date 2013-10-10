package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

import org.mdpnp.devices.EventLoop;

public class DemoSerialIntellivue extends AbstractDemoIntellivue {

    public DemoSerialIntellivue(int domainId, EventLoop eventLoop) throws IOException {
        super(domainId, eventLoop);
    }

    @Override
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Serial;
    }

    private static int[] getAvailablePorts(int cnt) throws IOException {
        int[] twoports = new int[cnt];
        for(int i = 0; i < cnt; i++) {
            ServerSocket ss = new ServerSocket(0);
            twoports[i] = ss.getLocalPort();
            ss.close();
        }
        return twoports;
    }

    @Override
    public void connect(String str) {




        try {
            int [] ports = getAvailablePorts(2);
            InetSocketAddress serialSide = new InetSocketAddress(InetAddress.getLoopbackAddress(), ports[0]);
            InetSocketAddress networkSide = new InetSocketAddress(InetAddress.getLoopbackAddress(), ports[1]);
            RS232Adapter adapter = new RS232Adapter(str, serialSide, networkSide);
            connect(serialSide, networkSide);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
