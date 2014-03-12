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

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.ActionFactory;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.devices.philips.intellivue.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class ActionResultImpl implements ActionResult {
    protected final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    protected OIDType actionType;

    protected DataExportMessage message;
    protected DataExportAction action;

    private static final Logger log = LoggerFactory.getLogger(ActionResultImpl.class);

    @Override
    public void parse(ByteBuffer bb) {
        managedObject.parse(bb);
        actionType = OIDType.parse(bb);
        int length = Bits.getUnsignedShort(bb);
        action = ActionFactory.buildAction(actionType, false);
        if (null == action) {
            log.warn("Unknown action type:" + actionType);

            bb.position(bb.position() + length);
        } else {
            action.setAction(this);
            action.parse(bb);

        }
    }

    @Override
    public void parseMore(ByteBuffer bb) {
        managedObject.parse(bb);
        actionType = OIDType.parse(bb);
        int length = Bits.getUnsignedShort(bb);
        if (null == action) {
            log.warn("Unknown action type:" + actionType);
            bb.position(bb.position() + length);
        } else {
            action.parseMore(bb);

        }
    }

    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        actionType.format(bb);

        Util.PrefixLengthShort.write(bb, action);
    }

    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }

    @Override
    public OIDType getActionType() {
        return actionType;
    }

    @Override
    public String toString() {
        java.lang.String at = ObjectClass.valueOf(actionType.getType()) == null ? actionType.toString() : ObjectClass.valueOf(actionType.getType())
                .toString();
        return "[managedObject=" + managedObject + ",actionType=" + at + ",action=" + action + "]";
    }

    @Override
    public DataExportMessage getMessage() {
        return message;
    }

    @Override
    public void setMessage(DataExportMessage message) {
        this.message = message;
    }

    @Override
    public void setActionType(OIDType type) {
        this.actionType = type;
    }

    @Override
    public DataExportAction getAction() {
        return action;
    }

    @Override
    public void setAction(DataExportAction action) {
        this.action = action;
    }
}
