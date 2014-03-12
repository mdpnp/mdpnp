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

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;

/**
 * @author Jeff Plourde
 *
 */
public class AttributeValueAssertion implements Value, Attribute<ByteArray> {
    private OIDType oidType;
    private ByteArray value;

    public AttributeValueAssertion() {

    }

    // public AttributeValueAssertion(OIDType type, byte[] value) {
    // this.oidType = type;
    // this.value = new ByteArray(value);
    // }

    @Override
    public void parse(ByteBuffer bb) {
        // Keep the OID and the length in the buffer so other attributes can
        // parse
        // with the same logic
        int pos = bb.position();
        oidType = OIDType.lookup(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        bb.position(pos);
        // TODO this could be a memory issue
        value = new ByteArray(new byte[length + 4]);
        value.parse(bb);
    }

    public ByteArray getValue() {
        return value;
    }

    public void setValue(byte[] b) {
        this.value = new ByteArray(b);
    }

    @Override
    public java.lang.String toString() {
        return "[oid=" + oidType + ",value=" + HexUtil.dump(ByteBuffer.wrap(value.getArray())) + "]";
    }

    @Override
    public void format(ByteBuffer bb) {
        // OID and length are stored in the buffer
        // oidType.format(bb);
        // Bits.putUnsignedShort(bb, value.getArray().length-4);
        value.format(bb);
    }

    @Override
    public OIDType getOid() {
        return this.oidType;
    }

    public void setOid(OIDType oid) {
        this.oidType = oid;
    }

}
