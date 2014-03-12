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
package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Get;

/**
 * @author Jeff Plourde
 *
 */
public class GetImpl implements Get {
    private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    private long scope;
    private final List<OIDType> list = new ArrayList<OIDType>();

    private DataExportMessage message;

    @Override
    public DataExportMessage getMessage() {
        return message;
    }

    @Override
    public void setMessage(DataExportMessage message) {
        this.message = message;
    }

    @Override
    public void parse(ByteBuffer bb) {
        parse(bb, true);
    }

    @Override
    public void parseMore(ByteBuffer bb) {
        parse(bb, false);
    }

    @SuppressWarnings("unused")
    private void parse(ByteBuffer bb, boolean clear) {
        managedObject.parse(bb);
        scope = Bits.getUnsignedInt(bb);
        int count = Bits.getUnsignedShort(bb);
        int length = Bits.getUnsignedShort(bb);
        list.clear();
        for (int i = 0; i < count; i++) {
            list.add(OIDType.parse(bb));
        }
    }

    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        Bits.putUnsignedInt(bb, scope);
        Bits.putUnsignedShort(bb, list.size());
        bb.mark();
        Bits.putUnsignedShort(bb, 0);
        int pos = bb.position();
        for (OIDType t : list) {
            t.format(bb);
        }
        int length = bb.position() - pos;
        bb.reset();
        Bits.putUnsignedShort(bb, length);
        bb.position(bb.position() + length);
    }

    @Override
    public List<OIDType> getAttributeId() {
        return list;
    }

    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }

    @Override
    public String toString() {
        return "[managedObject=" + managedObject + ",scope=" + scope + ",list=" + list + "]";
    }

}
