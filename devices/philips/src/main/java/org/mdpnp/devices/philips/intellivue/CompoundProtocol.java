package org.mdpnp.devices.philips.intellivue;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.association.AssociationMessage;
import org.mdpnp.devices.philips.intellivue.association.AssociationProtocol;
import org.mdpnp.devices.philips.intellivue.association.AssociationProtocolImpl;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationProtocol;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationProtocolImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportProtocol;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportProtocolImpl;

public class CompoundProtocol implements Protocol {
	private final DataExportProtocol dataExportProtocol = new DataExportProtocolImpl();
	private final ConnectIndicationProtocol connectIndicationProtocol = new ConnectIndicationProtocolImpl();
	private final AssociationProtocol associationProtocol = new AssociationProtocolImpl();
	@Override
	public Message parse(ByteBuffer bb) {
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
