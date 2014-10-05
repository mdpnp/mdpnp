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
package org.mdpnp.data.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

/**
 * @author Jeff Plourde
 *
 */
public class PureJavaCommSerialProvider implements SerialProvider {

    static {
        // nudgepipe allows us to use polling / selecting with an indefinite
        // timeout
        // by adding an extra file descriptor that gets written to in the
        // "close" case
        System.setProperty("purejavacomm.usenudgepipe", "true");
    }

    private static final int flowControl(SerialSocket.FlowControl flowControl) {
        switch (flowControl) {
        case Hardware:
            return SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT;
        case Software:
            return SerialPort.FLOWCONTROL_XONXOFF_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT;
        default:
        case None:
            return SerialPort.FLOWCONTROL_NONE;
        }
    }

    private static class DefaultSerialSettings {
        private final int baud;
        private final SerialSocket.DataBits dataBits;
        private final SerialSocket.Parity parity;
        private final SerialSocket.StopBits stopBits;
        private final SerialSocket.FlowControl flowControl;

        public DefaultSerialSettings(int baud, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits,
                SerialSocket.FlowControl flowControl) {
            this.baud = baud;
            this.dataBits = dataBits;
            this.parity = parity;
            this.stopBits = stopBits;
            this.flowControl = flowControl;
        }

        public void configurePort(SerialSocket socket) {
            socket.setSerialParams(baud, dataBits, parity, stopBits, flowControl);
        }
    }

    private static class SocketImpl implements SerialSocket {
        private final SerialPort serialPort;
        private final String portIdentifier;

        public SocketImpl(SerialPort serialPort, String portIdentifier) {
            this.portIdentifier = portIdentifier;
            this.serialPort = serialPort;

        }

        @Override
        public InputStream getInputStream() throws IOException {
            return serialPort.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return serialPort.getOutputStream();
        }

        @Override
        public void close() throws IOException {
            serialPort.close();
        }

        @Override
        public String getPortIdentifier() {
            return portIdentifier;
        }

        @Override
        public void setSerialParams(int baud, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits,
                SerialSocket.FlowControl flowControl) {
            int db = 0;
            switch (dataBits) {
            case Eight:
                db = SerialPort.DATABITS_8;
                break;
            case Seven:
                db = SerialPort.DATABITS_7;
                break;
            }
            int p = 0;
            switch (parity) {
            case None:
                p = SerialPort.PARITY_NONE;
                break;
            case Even:
                p = SerialPort.PARITY_EVEN;
                break;
            case Odd:
                p = SerialPort.PARITY_ODD;
                break;
            }
            int sb = 0;
            switch (stopBits) {
            case One:
                sb = SerialPort.STOPBITS_1;
                break;
            case OneAndOneHalf:
                sb = SerialPort.STOPBITS_1_5;
                break;
            case Two:
                sb = SerialPort.STOPBITS_2;
                break;
            }
            int fc = flowControl(flowControl);

            try {
                serialPort.setSerialPortParams(baud, db, sb, p);
                serialPort.setFlowControlMode(fc);
            } catch (UnsupportedCommOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<String> getPortNames() {
        List<String> list = new ArrayList<String>();
        Enumeration<?> e = purejavacomm.CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof purejavacomm.CommPortIdentifier) {
                list.add(((purejavacomm.CommPortIdentifier) o).getName());
            }
        }
        Collections.sort(list);
        return list;
    }

    private DefaultSerialSettings defaultSettings = new DefaultSerialSettings(9600, SerialSocket.DataBits.Eight, SerialSocket.Parity.None,
            SerialSocket.StopBits.One, SerialSocket.FlowControl.None);

    public void setDefaultSerialSettings(int baud, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits,
            SerialSocket.FlowControl flowControl) {
        defaultSettings = new DefaultSerialSettings(baud, dataBits, parity, stopBits, flowControl);
    }

    @Override
    public void setDefaultSerialSettings(int baudrate, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits) {
        setDefaultSerialSettings(baudrate, dataBits, parity, stopBits, SerialSocket.FlowControl.None);
    }

    protected void doConfigurePort(SerialSocket serialPort) throws UnsupportedCommOperationException {
        defaultSettings.configurePort(serialPort);
    }

    @Override
    public void cancelConnect() {
        // Not so terribly much we can do here
        // added for the sake of the android impl which
        // is trying several connect methods
    }

    public SerialSocket connect(String portIdentifier, long timeout) throws java.io.IOException {
        try {
            // getPortIdentifiers does some initialization that we desire
            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
            while (e.hasMoreElements()) {
                CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
                if (cpi.getName().equals(portIdentifier)) {
                    SerialPort serialPort = (SerialPort) cpi.open("", Long.MAX_VALUE == timeout ? Integer.MAX_VALUE : (int) timeout);
                    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                    SerialSocket socket = new SocketImpl(serialPort, portIdentifier);
                    doConfigurePort(socket);

                    return socket;
                }
            }
            throw new IllegalArgumentException("Unknown portIdentifier:" + portIdentifier);
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }
    
    @Override
    public SerialProvider duplicate() {
        return new PureJavaCommSerialProvider();
    }
}
