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
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueAssertion;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Set;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class SetImpl implements Set {

    private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
    private long scope;
    private DataExportMessage message;

    public static class AttributeModEntryImpl implements AttributeModEntry {

        public AttributeModEntryImpl() {
            this(null, new AttributeValueAssertion());

        }

        AttributeModEntryImpl(ModifyOperator modifyOperator, Attribute<?> attributeValueAssertion) {
            this.modifyOperator = modifyOperator;
            this.attributeValueAssertion = attributeValueAssertion;
        }

        private ModifyOperator modifyOperator;
        private final Attribute<?> attributeValueAssertion;

        @Override
        public ModifyOperator getModifyOperator() {
            return modifyOperator;
        }

        @Override
        public Attribute<?> getAttributeValueAssertion() {
            return attributeValueAssertion;
        }

        @Override
        public void parse(ByteBuffer bb) {
            modifyOperator = ModifyOperator.valueOf(Bits.getUnsignedShort(bb));
            attributeValueAssertion.parse(bb);
        }

        @Override
        public void format(ByteBuffer bb) {
            Bits.putUnsignedShort(bb, modifyOperator.asInt());
            attributeValueAssertion.format(bb);
        }

        @Override
        public String toString() {
            return "[modifyOperator=" + modifyOperator + ",ava=" + attributeValueAssertion + "]";
        }

    }

    private final List<AttributeModEntry> list = new ArrayList<AttributeModEntry>();

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
        scope = Bits.getUnsignedInt(bb);
        Util.PrefixLengthShort.read(bb, list, clear, AttributeModEntryImpl.class);
    }

    @Override
    public void format(ByteBuffer bb) {
        managedObject.format(bb);
        Bits.putUnsignedInt(bb, scope);

        Util.PrefixLengthShort.write(bb, list);

    }

    @Override
    public List<AttributeModEntry> getList() {
        return list;
    }

    @Override
    public void add(ModifyOperator modifyOperator, Attribute<?> ava) {
        list.add(new AttributeModEntryImpl(modifyOperator, ava));
    }

    // @Override
    // public void add(ModifyOperator modifyOperator, Attribute attribute) {
    // ByteBuffer bb1 = ByteBuffer.allocate(5000);
    // bb1.order(ByteOrder.BIG_ENDIAN);
    // attribute.format(bb1);
    // bb1.flip();
    // byte[] b = new byte[bb1.remaining()];
    // bb1.get(b);
    //
    // AttributeValueAssertion ava = new
    // AttributeValueAssertion(attribute.getOid(), b);
    // add(modifyOperator, ava);
    // }

    @Override
    public void setMessage(DataExportMessage message) {
        this.message = message;
    }

    @Override
    public DataExportMessage getMessage() {
        return message;
    }

    @Override
    public ManagedObjectIdentifier getManagedObject() {
        return managedObject;
    }

    @Override
    public SetResult createResult() {
        SetResultImpl eri = new SetResultImpl();
        eri.getManagedObject().setOidType(managedObject.getOidType());
        eri.getManagedObject().getGlobalHandle().setMdsContext(managedObject.getGlobalHandle().getMdsContext());
        eri.getManagedObject().getGlobalHandle().setHandle(managedObject.getGlobalHandle().getHandle());
        for (AttributeModEntry e : getList()) {
            eri.getAttributes().add(e.getAttributeValueAssertion());
        }
        return eri;
    }

    @Override
    public String toString() {
        return "[managedObject=" + managedObject + ",scope=" + scope + ",list=" + list + "]";
    }

}
