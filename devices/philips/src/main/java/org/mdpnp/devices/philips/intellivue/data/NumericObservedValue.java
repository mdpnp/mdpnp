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

/**
 * @author Jeff Plourde
 *
 */
public class NumericObservedValue implements Value {

    private OIDType physioId;
    private final MeasurementState msmtState = new MeasurementState();
    private OIDType unitCode;
    private final Float value = new Float();

    @Override
    public void format(ByteBuffer bb) {
        physioId.format(bb);
        msmtState.format(bb);
        unitCode.format(bb);
        value.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        physioId = OIDType.parse(bb);
        msmtState.parse(bb);
        unitCode = OIDType.parse(bb);
        value.parse(bb);
    }

    @Override
    public java.lang.String toString() {
        int physioIdType = physioId.getType();
        java.lang.String physioIdStr = ObservedValue.valueOf(physioIdType) == null ? physioId.toString() : ObservedValue.valueOf(physioIdType)
                .toString();
        // int unitCodeIdType = unitCode.getType();
        // java.lang.String dimension =
        // Dimension.valueOf(unitCodeIdType)==null?unitCode.toString():Dimension.valueOf(unitCodeIdType).toString();
        return "[physioId=" + physioIdStr + ",msmtState=" + msmtState + ",unitCode=" + unitCode + ",value=" + value + "]";
    }

    public Float getValue() {
        return value;
    }

    public MeasurementState getMsmtState() {
        return msmtState;
    }

    public OIDType getPhysioId() {
        return physioId;
    }

    public OIDType getUnitCode() {
        return unitCode;
    }

    public void setPhysioId(OIDType physioId) {
        this.physioId = physioId;
    }

    public void setUnitCode(OIDType unitCode) {
        this.unitCode = unitCode;
    }

}
