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
package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class ProtocolSupport implements Value {
    private final List<ProtocolSupportEntry> list = new ArrayList<ProtocolSupportEntry>();

    public static class ProtocolSupportEntry implements Value {
        public enum ApplicationProtocol {
            ACSE, DataOut, Unknown;

            public static ApplicationProtocol valueOf(int x) {
                switch (x) {
                case 1:
                    return ApplicationProtocol.ACSE;
                case 5:
                    return ApplicationProtocol.DataOut;
                default:
                    return ApplicationProtocol.Unknown;
                }
            }

            public int asShort() {
                switch (this) {
                case ACSE:
                    return 0x01;
                case DataOut:
                    return 0x05;
                default:
                    throw new IllegalArgumentException("Unknown:" + this);
                }
            }

        }

        public enum TransportProtocol {
            UDP, Unknown;

            public static TransportProtocol valueOf(int x) {
                switch (x) {
                case 1:
                    return TransportProtocol.UDP;
                default:
                    return TransportProtocol.Unknown;
                }
            }

            public int asShort() {
                switch (this) {
                case UDP:
                    return 0x01;
                default:
                    throw new IllegalArgumentException("Unknown:" + this);
                }
            }
        }

        private ApplicationProtocol appProtocol;
        private TransportProtocol transProtocol;
        private int portNumber;
        private int options;

        public static final int OPT_WIRELESS = 0x8000;

        @Override
        public java.lang.String toString() {
            return "[appProto=" + appProtocol + ",transProto=" + transProtocol + ",portNumber=" + portNumber + "]";
        }

        @Override
        public void parse(ByteBuffer bb) {
            this.appProtocol = ApplicationProtocol.valueOf(Bits.getUnsignedShort(bb));
            this.transProtocol = TransportProtocol.valueOf(Bits.getUnsignedShort(bb));

            this.portNumber = Bits.getUnsignedShort(bb);
            this.options = Bits.getUnsignedShort(bb);
        }

        public ApplicationProtocol getAppProtocol() {
            return appProtocol;
        }

        public void setAppProtocol(ApplicationProtocol appProtocol) {
            this.appProtocol = appProtocol;
        }

        public TransportProtocol getTransProtocol() {
            return transProtocol;

        }

        public void setTransProtocol(TransportProtocol transProtocol) {
            this.transProtocol = transProtocol;
        }

        public int getPortNumber() {
            return portNumber;
        }

        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber;
        }

        public int getOptions() {
            return options;
        }

        public void setOptions(int options) {
            this.options = options;
        }

        @Override
        public void format(ByteBuffer bb) {
            Bits.putUnsignedShort(bb, appProtocol.asShort());
            Bits.putUnsignedShort(bb, transProtocol.asShort());
            Bits.putUnsignedShort(bb, portNumber);
            Bits.putUnsignedShort(bb, options);
        }
    }

    public List<ProtocolSupportEntry> getList() {
        return list;
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (ProtocolSupportEntry e : list) {
            sb.append(e).append(",");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void parse(ByteBuffer bb) {
        Util.PrefixLengthShort.read(bb, list, true, ProtocolSupportEntry.class);
    }

    @Override
    public void format(ByteBuffer bb) {
        Util.PrefixLengthShort.write(bb, list);
    }

}
