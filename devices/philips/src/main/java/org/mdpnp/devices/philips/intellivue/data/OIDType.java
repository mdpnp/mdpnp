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
import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

/**
 * @author Jeff Plourde
 *
 */
public class OIDType implements Formatable {
    private final int type;

    private static final Map<Integer, OIDType> values = new HashMap<Integer, OIDType>();

    public static OIDType lookup(int type) {
        if (values.containsKey(type)) {
            return values.get(type);
        } else {
            OIDType t = new OIDType(type);
            values.put(type, t);
            return t;
        }
    }

    private OIDType(int type) {
        this.type = type;
    }

    @Override
    public java.lang.String toString() {
        ObjectClass oc = ObjectClass.valueOf(type);
        if (oc == null) {
            AttributeId id = AttributeId.valueOf(type);
            if (null == id) {
                ObservedValue ov = ObservedValue.valueOf(type);
                if (null == ov) {
                    return Integer.toString(type);
                } else {
                    return ov.toString();
                }
            } else {
                return id.toString();
            }
        } else {
            return oc.toString();
        }

    }

    @Override
    public int hashCode() {
        return type;
    }

    public static OIDType parse(ByteBuffer bb) {
        return OIDType.lookup(Bits.getUnsignedShort(bb));
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OIDType) {
            return type == ((OIDType) obj).type;
        } else {
            return false;
        }
    }

    public int getType() {
        return type;
    }

    public static void main(java.lang.String[] args) {
        System.out.println(OIDType.lookup(0XF23A));
    }

}
