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
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DemoEthernetIntellivue extends AbstractDemoIntellivue {

    public DemoEthernetIntellivue(int domainId, EventLoop eventLoop) throws IOException {
        super(domainId, eventLoop);
    }

    @Override
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Network;
    }

    private static final Logger log = LoggerFactory.getLogger(DemoEthernetIntellivue.class);

    @Override
    public boolean connect(String address) {
        if (null == address || "".equals(address)) {
            try {
                String[] hosts = listenForConnectIndication();

                if (null == hosts) {
                    state(ice.ConnectionState.Disconnected, "no broadcast addresses");
                } else {
                    state(ice.ConnectionState.Connecting, "listening  on " + Arrays.toString(hosts));
                }
            } catch (IOException e) {
                log.error("Awaiting beacon", e);
            }

        } else {
            try {
                int port = Intellivue.DEFAULT_UNICAST_PORT;

                int colon = address.lastIndexOf(':');
                if (colon >= 0) {
                    port = Integer.parseInt(address.substring(colon + 1, address.length()));
                    address = address.substring(0, colon);
                }

                InetAddress addr = InetAddress.getByName(address);

                connect(addr, -1, port);

            } catch (UnknownHostException e) {
                log.error("Trying to connect to address", e);
            } catch (IOException e) {
                log.error("Trying to connect to address", e);
            }
        }
        return true;
    }

    public String[] listenForConnectIndication() throws IOException {
        unregisterAll();

        List<Network.AddressSubnet> broadcastAddresses = Network.getBroadcastAddresses();
        if (broadcastAddresses.isEmpty()) {
            return null;
        } else {
            List<String> hosts = new ArrayList<String>();
            for (Network.AddressSubnet address : broadcastAddresses) {
                final DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.socket().setReuseAddress(true);
                channel.socket().bind(new InetSocketAddress(address.getInetAddress(), Intellivue.BROADCAST_PORT));
                registrationKeys.add(networkLoop.register(myIntellivue, channel));

                hosts.add(address.getInetAddress().getHostAddress());
            }
            return hosts.toArray(new String[0]);
        }
    }
}
