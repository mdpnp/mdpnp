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
import org.mdpnp.devices.io.util.HexUtil;

/**
 * @author Jeff Plourde
 *
 */
public class MDSGeneralSystemInfo implements Value {
    public static class Entry implements Value {
        protected int choice;
        protected byte[] value;

        @Override
        public void format(ByteBuffer bb) {
            Bits.putUnsignedShort(bb, choice);
            Bits.putUnsignedShort(bb, value.length);
            bb.put(value);

        }

        @Override
        public void parse(ByteBuffer bb) {
            choice = Bits.getUnsignedShort(bb);
            int length = Bits.getUnsignedShort(bb);
            value = new byte[length];
            bb.get(value);
        }

        @Override
        public java.lang.String toString() {
            return "[choice=" + choice + ",value=" + HexUtil.dump(ByteBuffer.wrap(value)) + "]";
        }
    }

    private final List<Entry> list = new ArrayList<Entry>();

    public static class PulseEntry extends Entry {
        private final ManagedObjectIdentifier systemPulse = new ManagedObjectIdentifier();
        private final ManagedObjectIdentifier alarmPulse = new ManagedObjectIdentifier();

        @Override
        public void format(ByteBuffer bb) {

            Bits.putUnsignedShort(bb, choice);
            bb.mark();
            Bits.putUnsignedShort(bb, 0);
            int pos = bb.position();
            systemPulse.format(bb);
            alarmPulse.format(bb);
            int length = bb.position() - pos;
            bb.reset();
            Bits.putUnsignedShort(bb, length);
            bb.position(bb.position() + length);
        }

        @Override
        public void parse(ByteBuffer bb) {
            super.parse(bb);
            ByteBuffer wrapped = ByteBuffer.wrap(value);
            systemPulse.parse(wrapped);
            alarmPulse.parse(wrapped);
        }

        @Override
        public java.lang.String toString() {
            return "[choice=" + choice + ",systemPulse=" + systemPulse + ",alarmPulse=" + alarmPulse + "]";
        }
    }

    @Override
    public void format(ByteBuffer bb) {
        // TODO ??? getting so tired
        throw new UnsupportedOperationException();
    }

    @Override
    public void parse(ByteBuffer bb) {
        int count = Bits.getUnsignedShort(bb);
        @SuppressWarnings("unused")
        int length = Bits.getUnsignedShort(bb);
        list.clear();
        for (int i = 0; i < count; i++) {
            int choice = 0xFFFF & bb.getShort(bb.position());
            Entry e;
            switch (choice) {
            case 1:
                e = new PulseEntry();
                break;
            default:
                e = new Entry();
                break;
            }
            e.parse(bb);
            list.add(e);
        }
    }

    @Override
    public java.lang.String toString() {
        return list.toString();
    }
}
