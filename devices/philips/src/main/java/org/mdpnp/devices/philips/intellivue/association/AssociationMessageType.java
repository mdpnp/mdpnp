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
package org.mdpnp.devices.philips.intellivue.association;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

/**
 * @author Jeff Plourde
 *
 */
public enum AssociationMessageType implements OrdinalEnum.ShortType {
    /**
     * Association Request
     */
    Connect(0x0D),
    /**
     * Association Response
     */
    Accept(0x0E),
    /**
     * Refused Response
     */
    Refuse(0x0C),
    /**
     * Disconnect Request
     */
    Finish(0x09),
    /**
     * Disconnect Response
     */
    Disconnect(0x0A),
    /**
     * Abort Request
     */
    Abort(0x19);

    private final short x;

    // for convenience
    private AssociationMessageType(int x) {
        this((short) x);
    }

    private AssociationMessageType(short x) {
        this.x = x;
    }

    private static final Map<Short, AssociationMessageType> map = OrdinalEnum.buildShort(AssociationMessageType.class);

    public final short asShort() {
        return x;
    }

    public static final AssociationMessageType valueOf(short x) {
        return map.get(x);
    }
}
