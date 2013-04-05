package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.OIDType;

public class ErrorDetailNoSuchActionImpl implements ErrorDetailNoSuchAction {

	private OIDType objectClass;
	private OIDType action;
	
	@Override
	public void parse(ByteBuffer bb) {
		objectClass = OIDType.parse(bb);
		action = OIDType.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		objectClass.format(bb);
		action.format(bb);
	}

	@Override
	public OIDType getActionType() {
		return action;
	}
	
	@Override
	public OIDType getObjectClass() {
		return objectClass;
	}

	@Override
	public String toString() {
		return "[objectClass="+objectClass+",action="+action+"]";
	}

}
