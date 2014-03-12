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

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.GetResult;

/**
 * @author Jeff Plourde
 *
 */
public class GetResultImpl implements GetResult {

    private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    private final AttributeValueList attr = new AttributeValueList();

    private DataExportMessage message;

    @Override
    public void parse(ByteBuffer bb) {
        parse(bb, true);
    }

    @Override
    public void parseMore(ByteBuffer bb) {
        parse(bb, false);
    }

    private void parse(ByteBuffer bb, boolean clear) {
        managedObject.parse(bb);
        if (clear) {
            attr.reset();
        }
        attr.parse(bb);
    }

    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        attr.format(bb);
    }

    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }

    @Override
    public AttributeValueList getAttributeList() {
        return attr;
    }

    @Override
    public String toString() {
        return "[managedObject=" + managedObject + ",attrs=" + attr + "]";
    }

    @Override
    public void setMessage(DataExportMessage message) {
        this.message = message;
    }

    @Override
    public DataExportMessage getMessage() {
        return message;
    }

}
