package org.mdpnp.devices.philips.intellivue.action;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;

public interface ExtendedPollDataRequest extends DataExportAction {
	int getPollNumber();
	void setPollNumber(int pollNumber);
	OIDType getPolledAttributeGroup();
	void setPolledAttributeGroup(OIDType lookup);
	Type getPolledObjectType();
	AttributeValueList getPollExtra();
}
