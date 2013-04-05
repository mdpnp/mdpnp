package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Protocol;

public interface DataExportProtocol extends Protocol {
	
	Header getHeader();
	
	void format(DataExportMessage message, ByteBuffer bb);
	
	@Override
	DataExportMessage parse(ByteBuffer bb);

}
