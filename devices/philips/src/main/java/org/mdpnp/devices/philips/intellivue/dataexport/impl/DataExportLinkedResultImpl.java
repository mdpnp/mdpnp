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

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportLinkedResult;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperationLinkedState;
import org.mdpnp.devices.philips.intellivue.dataexport.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DataExportLinkedResultImpl extends DataExportResultImpl implements DataExportLinkedResult {
    private RemoteOperationLinkedState state;
    private short count;

    @Override
    public RemoteOperationLinkedState getLinkedState() {
        return state;
    }

    @Override
    public short getLinkedCount() {
        return count;
    }

    public static final int peekInvokeId(ByteBuffer bb) {
        return 0xFFFF & bb.getShort(bb.position() + 2);
    }

    private static final Logger log = LoggerFactory.getLogger(DataExportLinkedResultImpl.class);

    private static final void advanceOrEnd(ByteBuffer bb, int advancement) {
        int newPosition = bb.position() + advancement;
        if (newPosition > bb.limit()) {
            log.warn("Tried to advance " + advancement + " bytes where only " + bb.remaining() + " remain");
            bb.position(bb.limit());
        } else {
            bb.position(newPosition);
        }
    }

    @Override
    public void parse(ByteBuffer bb) {
        state = RemoteOperationLinkedState.valueOf(Bits.getUnsignedByte(bb));
        count = Bits.getUnsignedByte(bb);
        invokeId = Bits.getUnsignedShort(bb);
        int cmdType;
        commandType = CommandType.valueOf(cmdType = Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        if (commandType == null) {
            // TODO error ish
            log.warn("Unrecognized command type " + cmdType);
            advanceOrEnd(bb, length);
        } else {
            switch (state) {
            case First:
                command = CommandFactory.buildCommand(commandType, true);
                if (null == command) {
                    log.warn("Unable to build command for CommandType=" + commandType);
                    advanceOrEnd(bb, length);
                } else {
                    command.setMessage(this);
                    command.parse(bb);
                }
                break;
            case NotFirstNotLast:
                if (null == command) {
                    log.warn("Received a command of type " + commandType + " with NotFirstNotLastState but no previous First");
                    advanceOrEnd(bb, length);
                } else {
                    command.parseMore(bb);
                }
                break;
            case Last:
                if (null == command) {
                    log.warn("Received a command of type " + commandType + " with Last but no previous First");
                    advanceOrEnd(bb, length);
                } else {
                    command.parseMore(bb);
                }
                break;
            }
        }

    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedByte(bb, state.asShort());
        Bits.putUnsignedByte(bb, count);
        super.format(bb);
    }
}
