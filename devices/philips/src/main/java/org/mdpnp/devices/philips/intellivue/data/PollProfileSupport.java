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
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;

/**
 * @author Jeff Plourde
 *
 */
public class PollProfileSupport implements Value {
    private long pollProfileRevision = POLL_PROFILE_REV_0;
    public static final long POLL_PROFILE_REV_0 = 0x80000000L;

    private final RelativeTime minPollPeriod = new RelativeTime();

    private long maxMtuRx = 1400L, maxMtuTx = 1400L, maxBwTx = 0xFFFFFFFFL;
    private long pollProfileOptions = P_OPT_DYN_CREATE_OBJECTS | P_OPT_DYN_DELETE_OBJECTS;
    public static final long P_OPT_DYN_CREATE_OBJECTS = 0x40000000L;
    public static final long P_OPT_DYN_DELETE_OBJECTS = 0x20000000L;

    private final AttributeValueList optionalPackages = new AttributeValueList();

    private final Attribute<PollProfileExtensions> pollProfileExtensions = AttributeFactory.getPollProfileExtensions();

    @Override
    public java.lang.String toString() {
        return "[pollProfileRevision=" + pollProfileRevision + ",minPollPeriod=" + minPollPeriod + ",maxMtuRx=" + maxMtuRx + ",maxMtuTx=" + maxMtuTx
                + ",maxBwTx=" + maxBwTx + ",pollProfileOptions=" + Long.toHexString(pollProfileOptions) + ",optionaPackages=" + optionalPackages
                + ",pollProfileExtensions=" + pollProfileExtensions + "]";
    }

    @Override
    public void parse(ByteBuffer bb) {
        pollProfileRevision = Bits.getUnsignedInt(bb);
        minPollPeriod.parse(bb);
        maxMtuRx = Bits.getUnsignedInt(bb);
        maxMtuTx = Bits.getUnsignedInt(bb);
        maxBwTx = Bits.getUnsignedInt(bb);
        pollProfileOptions = Bits.getUnsignedInt(bb);
        optionalPackages.parse(bb);

        optionalPackages.get(pollProfileExtensions);
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedInt(bb, pollProfileRevision);
        minPollPeriod.format(bb);
        Bits.putUnsignedInt(bb, maxMtuRx);
        Bits.putUnsignedInt(bb, maxMtuTx);
        Bits.putUnsignedInt(bb, maxBwTx);
        Bits.putUnsignedInt(bb, pollProfileOptions);

        optionalPackages.reset();
        optionalPackages.add(pollProfileExtensions);
        optionalPackages.format(bb);
    }

    public RelativeTime getMinPollPeriod() {
        return minPollPeriod;
    }

    public long getMaxBwTx() {
        return maxBwTx;
    }

    public long getMaxMtuRx() {
        return maxMtuRx;
    }

    public long getMaxMtuTx() {
        return maxMtuTx;
    }

    public void setMaxBwTx(long maxBwTx) {
        this.maxBwTx = maxBwTx;
    }

    public void setMaxMtuRx(long maxMtuRx) {
        this.maxMtuRx = maxMtuRx;
    }

    public void setMaxMtuTx(long maxMtuTx) {
        this.maxMtuTx = maxMtuTx;
    }

    public PollProfileExtensions getPollProfileExtensions() {
        return pollProfileExtensions.getValue();
    }

}
