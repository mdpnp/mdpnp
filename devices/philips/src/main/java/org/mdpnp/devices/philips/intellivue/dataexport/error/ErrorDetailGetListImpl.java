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
package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class ErrorDetailGetListImpl implements ErrorDetailGetList {
    private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    private final List<GetError> list = new ArrayList<GetError>();

    public static class GetErrorImpl implements GetError {
        private ErrorStatus errorStatus;
        private OIDType oid;

        @Override
        public void format(ByteBuffer bb) {
            Bits.putUnsignedShort(bb, errorStatus.asInt());
            oid.format(bb);
        }

        @Override
        public void parse(ByteBuffer bb) {
            errorStatus = ErrorStatus.valueOf(Bits.getUnsignedShort(bb));
            oid = OIDType.lookup(Bits.getUnsignedShort(bb));
        }

        public ErrorStatus getErrorStatus() {
            return errorStatus;
        }

        public OIDType getOid() {
            return oid;
        }

        @Override
        public String toString() {
            return "[oid=" + oid + ",errorStatus=" + errorStatus + "]";
        }
    }

    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }

    @SuppressWarnings("unused")
    @Override
    public void parse(ByteBuffer bb) {
        managedObject.parse(bb);
        int count = Bits.getUnsignedShort(bb);
        int length = Bits.getUnsignedShort(bb);
        list.clear();
        for (int i = 0; i < count; i++) {
            GetError err = new GetErrorImpl();
            err.parse(bb);
            list.add(err);
        }
    }

    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        Util.PrefixLengthShort.write(bb, list);
    }

    @Override
    public List<GetError> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "[object=" + managedObject + ",list=" + list + "]";
    }
}
