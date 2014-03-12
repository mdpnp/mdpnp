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
package org.mdpnp.devices.philips.intellivue.connectindication;

import java.net.SocketException;
import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Network;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport;
import org.mdpnp.devices.philips.intellivue.data.SystemLocalization;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.Nomenclature;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class ConnectIndicationImpl implements ConnectIndication {
    private final Nomenclature nomenclature = new Nomenclature();
    private RemoteOperation remoteOperation = RemoteOperation.Invoke;

    private int invokeId;
    private CommandType commandType = CommandType.Get;

    private final EventReport report = new EventReport();
    private final AttributeValueList attrs = new AttributeValueList();
    private final Attribute<Type> systemType = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_SYS_TYPE, Type.class);
    private final Attribute<ProtocolSupport> protocolSupport = AttributeFactory
            .getAttribute(AttributeId.NOM_ATTR_PCOL_SUPPORT, ProtocolSupport.class);
    private final Attribute<SystemLocalization> systemLocalization = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_LOCALIZN,
            SystemLocalization.class);
    private final Attribute<IPAddressInformation> ipAddressInformation = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_NET_ADDR_INFO,
            IPAddressInformation.class);

    @Override
    public Nomenclature getNomenclature() {
        return nomenclature;
    }

    @Override
    public IPAddressInformation getIpAddressInformation() {
        return ipAddressInformation.getValue();
    }

    @Override
    public SystemLocalization getSystemLocalization() {
        return systemLocalization.getValue();
    }

    @Override
    public Type getSystemType() {
        return systemType.getValue();
    }

    @Override
    public ProtocolSupport getProtocolSupport() {
        return protocolSupport.getValue();
    }

    @SuppressWarnings("unused")
    public void parse(ByteBuffer bb) {
        nomenclature.parse(bb);
        remoteOperation = RemoteOperation.valueOf(Bits.getUnsignedShort(bb));
        int length = Bits.getUnsignedShort(bb);
        invokeId = Bits.getUnsignedShort(bb);
        commandType = CommandType.valueOf(Bits.getUnsignedShort(bb));
        int length2 = Bits.getUnsignedShort(bb);
        report.parse(bb);
        attrs.parse(bb);

        attrs.get(systemType);
        attrs.get(protocolSupport);
        attrs.get(systemLocalization);
        attrs.get(ipAddressInformation);
    }

    @Override
    public String toString() {
        return "[" + nomenclature + ",remoteOp=" + remoteOperation + ",invokeId=" + invokeId + ",commandType=" + commandType + ",report=" + report
                + ",systemType=" + systemType + ",protocolSupport=" + protocolSupport + ",systemLocalization=" + systemLocalization
                + ",ipAddressInformation=" + ipAddressInformation + "]";
    }

    @Override
    public void format(final ByteBuffer bb) {
        nomenclature.format(bb);
        Bits.putUnsignedShort(bb, remoteOperation.asInt());
        final int invokeId = this.invokeId;
        final EventReport report = this.report;
        final CommandType commandType = this.commandType;
        final AttributeValueList attrs = this.attrs;
        final Attribute<Type> systemType = this.systemType;
        final Attribute<ProtocolSupport> protocolSupport = this.protocolSupport;
        final Attribute<SystemLocalization> systemLocalization = this.systemLocalization;
        final Attribute<IPAddressInformation> ipAddressInformation = this.ipAddressInformation;

        Util.PrefixLengthShort.write(bb, new Formatable() {
            @Override
            public void format(ByteBuffer bb) {
                Bits.putUnsignedShort(bb, invokeId);
                Bits.putUnsignedShort(bb, commandType.asInt());

                Util.PrefixLengthShort.write(bb, new Formatable() {
                    public void format(ByteBuffer bb) {
                        report.format(bb);
                        attrs.reset();
                        attrs.add(systemType);
                        attrs.add(protocolSupport);
                        attrs.add(systemLocalization);
                        attrs.add(ipAddressInformation);

                        attrs.format(bb);
                    }
                });

            }
        });

    }

    public static void main(String[] args) throws SocketException {
        ConnectIndication ci = new ConnectIndicationImpl();
        ci.getIpAddressInformation().setInetAddress(Network.getLocalAddresses().get(1));
        // ci.getSystemType().setOidType(OIDType.lookup(1));
        // ci.getNomenclature().setMajorVersion((short) 5);
        ByteBuffer bb = ByteBuffer.allocate(5000);
        ci.format(bb);
        System.out.println(ci);

        bb.flip();
        System.out.println(HexUtil.dump(bb));

        ConnectIndication ci2 = new ConnectIndicationImpl();
        ci2.parse(bb);
        bb.clear();
        ci2.format(bb);
        bb.flip();
        System.out.println(ci2);
        System.out.println(HexUtil.dump(bb));
    }
}
