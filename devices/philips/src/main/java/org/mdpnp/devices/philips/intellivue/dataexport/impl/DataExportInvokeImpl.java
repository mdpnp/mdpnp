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
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportInvoke;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.dataexport.command.CommandFactory;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class DataExportInvokeImpl implements DataExportInvoke {

    private int invokeId;
    private CommandType commandType;

    private DataExportCommand command;

    @Override
    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public int getInvoke() {
        return invokeId;
    }

    @Override
    public void setInvoke(int i) {
        this.invokeId = i;
    }

    @SuppressWarnings("unused")
    @Override
    public void parse(ByteBuffer bb) {
        invokeId = Bits.getUnsignedShort(bb);
        commandType = CommandType.valueOf(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        command = CommandFactory.buildCommand(commandType, false);
        command.setMessage(this);
        command.parse(bb);

    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, invokeId);
        Bits.putUnsignedShort(bb, commandType.asInt());

        Util.PrefixLengthShort.write(bb, command);

    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public void setCommand(DataExportCommand dec) {
        this.command = dec;
    }

    @Override
    public DataExportCommand getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "[invokeId=" + invokeId + ",commandType=" + commandType + ",command=" + command + "]";
    }

    @Override
    public RemoteOperation getRemoteOperation() {
        return RemoteOperation.Invoke;
    }
}
