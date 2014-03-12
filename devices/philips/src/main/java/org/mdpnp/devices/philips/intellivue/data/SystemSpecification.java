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

import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;

/**
 * @author Jeff Plourde
 *
 */
public class SystemSpecification implements Value {
    private final AttributeValueList attrs = new AttributeValueList();

    private final Attribute<MdibObjectSupport> objectSupport = AttributeFactory.getMdibObjectSupport();

    @Override
    public void format(ByteBuffer bb) {
        attrs.add(objectSupport);
        attrs.format(bb);
    }

    public MdibObjectSupport getMdibObjectSupport() {
        return objectSupport.getValue();
    }

    public AttributeValueList getAttributes() {
        return attrs;
    }

    @Override
    public void parse(ByteBuffer bb) {
        attrs.reset();
        attrs.parse(bb);
        attrs.get(objectSupport);

    }

    @Override
    public java.lang.String toString() {

        if (attrs.getList().isEmpty()) {
            return objectSupport.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[attrs=");
            sb.append(attrs);
            sb.append(",objectSupport=");
            sb.append(objectSupport);
            sb.append("]");
            return sb.toString();
        }
    }
}
