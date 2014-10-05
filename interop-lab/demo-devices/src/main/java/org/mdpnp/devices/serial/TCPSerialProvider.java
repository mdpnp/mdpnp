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
package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;

/**
 * @author Jeff Plourde
 *
 */
public class TCPSerialProvider implements SerialProvider {

    private static class TCPSerialSocket implements SerialSocket {

        private final Socket tcpSocket;

        public TCPSerialSocket(Socket tcpSocket) {
            this.tcpSocket = tcpSocket;
        }

        @Override
        public String getPortIdentifier() {
            return tcpSocket.getRemoteSocketAddress().toString();
        }

        @Override
        public void close() throws IOException {
            tcpSocket.close();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return tcpSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return tcpSocket.getOutputStream();
        }

        @Override
        public void setSerialParams(int baud, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl) {
            // all this needs a refactoring for another day
        }

    }

    @Override
    public List<String> getPortNames() {
        return new ArrayList<String>();
    }

    @Override
    public SerialSocket connect(String portIdentifier, long timeout) {
        String[] parts = portIdentifier.split("\\:");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        try {
            return new TCPSerialSocket(new Socket(host, port));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelConnect() {
        // we'll see if there is something to do about his maybe but TCP
        // timeouts are pretty short
    }

    @Override
    public void setDefaultSerialSettings(int baudrate, DataBits dataBits, Parity parity, StopBits stopBits) {

    }

    @Override
    public void setDefaultSerialSettings(int baudrate, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl) {

    }
    
    @Override
    public SerialProvider duplicate() {
        return new TCPSerialProvider();
    }
}
