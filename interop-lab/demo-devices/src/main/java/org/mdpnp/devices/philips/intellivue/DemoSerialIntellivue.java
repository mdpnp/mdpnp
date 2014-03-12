/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

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
        for (int i = 0; i < cnt; i++) {
            ServerSocket ss = new ServerSocket(0);
            twoports[i] = ss.getLocalPort();
            ss.close();
        }
        return twoports;
    }

    @Override
    public void shutdown() {
        adapter.shutdown();
        super.shutdown();
    }

    protected RS232Adapter adapter;

    @Override
    public void connect(String str) {
        if (null != adapter) {
            throw new IllegalStateException("Multiple calls to connect are not currently supported");
        }
        try {
            int[] ports = getAvailablePorts(2);
            InetSocketAddress serialSide = new InetSocketAddress(InetAddress.getLoopbackAddress(), ports[0]);
            InetSocketAddress networkSide = new InetSocketAddress(InetAddress.getLoopbackAddress(), ports[1]);
            adapter = new RS232Adapter(str, serialSide, networkSide, threadGroup, networkLoop);
            connect(serialSide, networkSide);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
