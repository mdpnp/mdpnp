package org.mdpnp.devices.philips.intellivue;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.association.AssociationMessage;
import org.mdpnp.devices.philips.intellivue.association.AssociationProtocol;
import org.mdpnp.devices.philips.intellivue.association.AssociationProtocolImpl;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationProtocol;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationProtocolImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportProtocol;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportProtocolImpl;
import org.mdpnp.devices.philips.intellivue.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompoundProtocol implements Protocol {
    private final static Logger log = LoggerFactory.getLogger(CompoundProtocol.class);
    private final DataExportProtocol dataExportProtocol = new DataExportProtocolImpl();
    private final ConnectIndicationProtocol connectIndicationProtocol = new ConnectIndicationProtocolImpl();
    private final AssociationProtocol associationProtocol = new AssociationProtocolImpl();
    @Override
    public Message parse(ByteBuffer bb) {
        try {
            if(bb.hasRemaining()) {
                int firstByte = 0xFF & bb.get(0);

                switch(firstByte) {
                case 0xE1:
                    return dataExportProtocol.parse(bb);
                case 0x00:
                    return connectIndicationProtocol.parse(bb);
                default:
                    return associationProtocol.parse(bb);
                }
            } else {
                return null;
            }
        } catch (RuntimeException re) {
            bb.position(0);
            log.trace("Offending buffer:\n"+HexUtil.dump(bb, 20));
            throw new RuntimeException(re);
        }
    }
    @Override
    public void format(Message message, ByteBuffer bb) {
        if(message instanceof DataExportMessage) {
            dataExportProtocol.format((DataExportMessage)message, bb);
        } else if(message instanceof AssociationMessage) {
            associationProtocol.format((AssociationMessage)message, bb);
        } else if(message instanceof ConnectIndication) {
            connectIndicationProtocol.format((ConnectIndication) message, bb);
        } else {
            // TODO something to alert the user
        }
    }


}
