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

import org.mdpnp.devices.io.util.Bits;

/**
 * @author Jeff Plourde
 *
 */
public class DevAlarmEntry implements Value {

    private OIDType alSource;
    private OIDType alCode;
    private final AlertType alType = new AlertType();
    private final AlertState alState = new AlertState();
    private final ManagedObjectIdentifier object = new ManagedObjectIdentifier();
    private final PrivateOID alert_info_id = new PrivateOID();
    private int length;
    private AlMonInfo alMonInfo;

    private final static int GEN_ALMON_INFO = 513;
    private final static int STR_ALMON_INFO = 516;

    public OIDType getAlSource() {
        return alSource;
    }

    public OIDType getAlCode() {
        return alCode;
    }

    public AlertType getAlType() {
        return alType;
    }

    public AlertState getAlState() {
        return alState;
    }

    public ManagedObjectIdentifier getObject() {
        return object;
    }

    public PrivateOID getAlertInfoId() {
        return alert_info_id;
    }

    public int getLength() {
        return length;
    }

    public AlMonInfo getAlMonInfo() {
        return alMonInfo;
    }

    @Override
    public java.lang.String toString() {
        return "[al_source=" + alSource + ",al_code=" + alCode + ",al_type=" + alType + ",al_state=" + alState + ",object=" + object
                + ",alert_info_id=" + alert_info_id + ",length=" + length + ",al_mon_info=" + alMonInfo + "]";
    }

    @Override
    public void format(ByteBuffer bb) {
        alSource.format(bb);
        alCode.format(bb);
        alType.format(bb);
        alState.format(bb);
        object.format(bb);
        alert_info_id.format(bb);
        Bits.putUnsignedShort(bb, length);
        alMonInfo.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        alSource = OIDType.parse(bb);
        alCode = OIDType.parse(bb);
        alType.parse(bb);
        alState.parse(bb);
        object.parse(bb);
        alert_info_id.parse(bb);
        length = Bits.getUnsignedShort(bb);
        switch (alert_info_id.getOid()) {
        case GEN_ALMON_INFO:
            if (alMonInfo == null || !(alMonInfo instanceof AlMonGenInfo)) {
                this.alMonInfo = new AlMonGenInfo();
            }
            alMonInfo.parse(bb);
            break;
        case STR_ALMON_INFO:
            if (alMonInfo == null || !(alMonInfo instanceof StrAlMonInfo)) {
                this.alMonInfo = new StrAlMonInfo();
            }
            alMonInfo.parse(bb);
            break;
        default:
            bb.position(bb.position() + length);
            break;
        }
    }

}
