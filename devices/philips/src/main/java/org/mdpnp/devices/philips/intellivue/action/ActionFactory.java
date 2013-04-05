package org.mdpnp.devices.philips.intellivue.action;

import org.mdpnp.devices.philips.intellivue.action.impl.ExtendedPollDataRequestImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.ExtendedPollDataResultImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.SinglePollDataRequestImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.SinglePollDataResultImpl;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;

public class ActionFactory {
	public static final DataExportAction buildAction(OIDType actionType, boolean request) {
		switch(ObjectClass.valueOf(actionType.getType())) {
		case NOM_ACT_POLL_MDIB_DATA:
			return request? new SinglePollDataRequestImpl() : new SinglePollDataResultImpl();
		case NOM_ACT_POLL_MDIB_DATA_EXT:
			return request? new ExtendedPollDataRequestImpl() : new ExtendedPollDataResultImpl();
		default:
			return null;
		}
	}
}
