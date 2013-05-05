package org.mdpnp.nomenclature;

import org.mdpnp.data.text.Text;
import org.mdpnp.data.text.TextImpl;
import org.mdpnp.data.textarray.TextArray;
import org.mdpnp.data.textarray.TextArrayImpl;

public interface Association {
	Text ANNOUNCE_ARRIVE = new TextImpl(Association.class, "ANNOUNCE_ARRIVE");
	Text ACKNOWLEDGE_ARRIVE = new TextImpl(Association.class, "ACKNOWLEDGE_ARRIVE");
	Text ANNOUNCE_DEPART = new TextImpl(Association.class, "ANNOUNCE_DEPART");
	Text ACKNOWLEDGE_DEPART = new TextImpl(Association.class, "ACKNOWLEDGE_DEPART");
	Text HEARTBEAT = new TextImpl(Association.class, "HEARTBEAT");
	
	Text SOLICIT = new TextImpl(Association.class, "SOLICIT");
	TextArray DISSEMINATE = new TextArrayImpl(Association.class, "DISSEMINATE");
	Text REQUEST_DISSEMINATE = new TextImpl(Association.class, "REQUEST_DISSEMINATE");
}
