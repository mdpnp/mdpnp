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
public class MdibObjectSupport implements Value {
    private final List<MdibObjectSupportEntry> list = new ArrayList<MdibObjectSupportEntry>();

    public void addClass(ObjectClass objClass) {
        list.add(new MdibObjectSupportEntry(objClass));
    }

    public void addClass(ObjectClass objClass, long maxInstances) {
        list.add(new MdibObjectSupportEntry(objClass, maxInstances));
    }

    public static class MdibObjectSupportEntry implements Value {
        private final Type type;
        private long maxInstances = MAX_INSTANCES_UNDEFINED;
        public static final long MAX_INSTANCES_UNDEFINED = 0xFFFFFFFFL;

        public MdibObjectSupportEntry() {
            this.type = new Type();
        }

        public MdibObjectSupportEntry(ObjectClass c) {
            this(c, MAX_INSTANCES_UNDEFINED);
        }

        public MdibObjectSupportEntry(ObjectClass c, long maxInstances) {
            this.type = new Type(c);
            this.maxInstances = maxInstances;
        }

        @Override
        public void format(ByteBuffer bb) {
            type.format(bb);
            Bits.putUnsignedInt(bb, maxInstances);
        }

        @Override
        public void parse(ByteBuffer bb) {
            type.parse(bb);
            maxInstances = Bits.getUnsignedInt(bb);
        }

        @Override
        public java.lang.String toString() {
            return "[type=" + type + ",maxInstances=" + maxInstances + "]";
        }
    }

    @Override
    public void parse(ByteBuffer bb) {
        Util.PrefixLengthShort.read(bb, list, true, MdibObjectSupportEntry.class);
    }

    @Override
    public void format(ByteBuffer bb) {
        Util.PrefixLengthShort.write(bb, list);
    }

    @Override
    public java.lang.String toString() {
        return list.toString();
    }

}
