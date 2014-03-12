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
package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Message;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportLinkedResult;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportProtocol;
import org.mdpnp.devices.philips.intellivue.dataexport.Header;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class DataExportProtocolImpl implements DataExportProtocol {

    private final Header header = new Header();

    @Override
    public Header getHeader() {
        return header;
    }

    private final Map<Integer, DataExportLinkedResult> linked = new HashMap<Integer, DataExportLinkedResult>();

    @SuppressWarnings("unused")
    @Override
    public DataExportMessage parse(ByteBuffer bb) {
        header.parse(bb);
        RemoteOperation remoteOperation = RemoteOperation.valueOf(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);

        // Peek .. this is awful
        // int invokeId = 0xFFFF & bb.getShort(bb.position());
        int invokeId;

        DataExportMessage message = null;
        switch (remoteOperation) {
        case Error:
            message = new DataExportErrorImpl();
            message.parse(bb);
            return message;
        case Invoke:
            message = new DataExportInvokeImpl();
            message.parse(bb);
            return message;
        case Result:
            invokeId = DataExportResultImpl.peekInvokeId(bb);
            if (linked.containsKey(invokeId)) {
                DataExportLinkedResult r = linked.remove(invokeId);
                r.parseMore(bb);
                return r;
            } else {
                message = new DataExportResultImpl();
                message.parse(bb);
                return message;
            }
        case LinkedResult:
            invokeId = DataExportLinkedResultImpl.peekInvokeId(bb);

            if (linked.containsKey(invokeId)) {
                linked.get(invokeId).parse(bb);
                return null;
            } else {
                message = new DataExportLinkedResultImpl();
                message.parse(bb);
                linked.put(message.getInvoke(), (DataExportLinkedResult) message);

                return null;
            }
        }

        return null;
    }

    @Override
    public void format(Message message, ByteBuffer bb) {
        if (message instanceof DataExportMessage) {
            format((DataExportMessage) message, bb);
        }
    }

    @Override
    public void format(DataExportMessage message, ByteBuffer bb) {
        header.format(bb);
        Bits.putUnsignedShort(bb, message.getRemoteOperation().asInt());
        Util.PrefixLengthShort.write(bb, message);
    }

}
