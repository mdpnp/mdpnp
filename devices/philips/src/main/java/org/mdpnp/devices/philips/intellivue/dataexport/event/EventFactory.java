package org.mdpnp.devices.philips.intellivue.dataexport.event;

import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;
import org.mdpnp.devices.philips.intellivue.dataexport.event.impl.MdsCreateEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFactory {
	private static final Logger log = LoggerFactory.getLogger(EventFactory.class);
	public static final DataExportEvent buildEvent(OIDType oid) {
		ObjectClass objClass = ObjectClass.valueOf(oid.getType());
		if(null == objClass) {
			log.warn("Unknown object class " + oid);
			return null;
		}
		
		switch(objClass) {
		case NOM_NOTI_MDS_CREAT:
			return new MdsCreateEventImpl();
		default:
			return null;
		}
	}
}
