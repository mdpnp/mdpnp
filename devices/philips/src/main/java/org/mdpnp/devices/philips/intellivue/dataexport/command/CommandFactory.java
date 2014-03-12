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
package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.ActionImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.ActionResultImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.EventReportImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.GetImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.GetResultImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.SetImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.SetResultImpl;

/**
 * @author Jeff Plourde
 *
 */
public class CommandFactory {
    public static final DataExportCommand buildCommand(CommandType commandType, boolean result) {
        switch (commandType) {
        case EventReport:
        case ConfirmedEventReport:
            return new EventReportImpl();
        case ConfirmedAction:
            return result ? new ActionResultImpl() : new ActionImpl();
        case Get:
            return result ? new GetResultImpl() : new GetImpl();
        case Set:
        case ConfirmedSet:
            return result ? new SetResultImpl() : new SetImpl();
        default:
            throw new IllegalArgumentException("Unknown command type:" + commandType);
        }
    }
}
