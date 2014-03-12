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
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportError;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetail;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailAccessDeniedImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailGetListImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidArgumentValueImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidObjectInstanceImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidScopeImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchActionImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchObjectClassImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchObjectInstanceImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailProcessingFailureImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailSetListImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.RemoteError;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class DataExportErrorImpl implements DataExportError {

    private int invoke;
    private RemoteError error;
    private ErrorDetail detail;

    private static final ErrorDetail buildErrorDetail(RemoteError error) {
        switch (error) {
        case GetListError:
            return new ErrorDetailGetListImpl();
        case SetListError:
            return new ErrorDetailSetListImpl();
        case NoSuchAction:
            return new ErrorDetailNoSuchActionImpl();
        case NoSuchObjectClass:
            return new ErrorDetailNoSuchObjectClassImpl();
        case NoSuchObjectInstance:
            return new ErrorDetailNoSuchObjectInstanceImpl();
        case AccessDenied:
            return new ErrorDetailAccessDeniedImpl();
        case ProcessingFailure:
            return new ErrorDetailProcessingFailureImpl();
        case InvalidArgumentValue:
            return new ErrorDetailInvalidArgumentValueImpl();
        case InvalidScope:
            return new ErrorDetailInvalidScopeImpl();
        case InvalidObjectInstance:
            return new ErrorDetailInvalidObjectInstanceImpl();
        default:
            throw new IllegalArgumentException("Unknown error type:" + error);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void parse(ByteBuffer bb) {
        invoke = Bits.getUnsignedShort(bb);
        error = RemoteError.valueOf(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        detail = buildErrorDetail(error);

        detail.parse(bb);
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, invoke);
        Bits.putUnsignedShort(bb, error.asInt());
        Util.PrefixLengthShort.write(bb, detail);
    }

    @Override
    public RemoteError getError() {
        return error;
    }

    @Override
    public int getInvoke() {
        return invoke;
    }

    @Override
    public ErrorDetail getErrorDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "[error=" + error + ",invoke=" + invoke + ",detail=" + detail + "]";
    }

    @Override
    public RemoteOperation getRemoteOperation() {
        return RemoteOperation.Error;
    }

    @Override
    public void setInvoke(int i) {
        this.invoke = i;
    }

}
