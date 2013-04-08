package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Message;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportLinkedResult;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportProtocol;
import org.mdpnp.devices.philips.intellivue.dataexport.Header;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class DataExportProtocolImpl implements DataExportProtocol {

	private final Header header = new Header();
	
	@Override
	public Header getHeader() {
		return header;
	}
	
	private final Map<Integer, DataExportLinkedResult> linked = new HashMap<Integer, DataExportLinkedResult>();
	
	@SuppressWarnings("unused")
    @Override
	public DataExportMessage parse(ByteBuffer bb) {
		header.parse(bb);
		RemoteOperation remoteOperation = RemoteOperation.valueOf(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		
		// Peek .. this is awful
//		int invokeId = 0xFFFF & bb.getShort(bb.position());
		int invokeId;
		
		DataExportMessage message = null;
		switch(remoteOperation) {
		case Error:
			message = new DataExportErrorImpl();
			message.parse(bb);
			return message;
		case Invoke:
			message = new DataExportInvokeImpl();
			message.parse(bb);
			return message;
		case Result:
			invokeId = DataExportResultImpl.peekInvokeId(bb);
			if(linked.containsKey(invokeId)) {
				DataExportLinkedResult r = linked.remove(invokeId);
				r.parseMore(bb);
				return r;
			} else {
				message = new DataExportResultImpl();
				message.parse(bb);
				return message;
			}
		case LinkedResult:
			invokeId = DataExportLinkedResultImpl.peekInvokeId(bb);

			if(linked.containsKey(invokeId)) {
				linked.get(invokeId).parse(bb);
				return null;
			} else {
				message = new DataExportLinkedResultImpl();
				message.parse(bb);
				linked.put(message.getInvoke(), (DataExportLinkedResult) message);
				
				return null;
			}
		}

		return null;
	}

	@Override
	public void format(Message message, ByteBuffer bb) {
		if(message instanceof DataExportMessage) {
			format((DataExportMessage)message, bb);
		}
	}
	
	@Override
	public void format(DataExportMessage message, ByteBuffer bb) {
		header.format(bb);
		Bits.putUnsignedShort(bb,  message.getRemoteOperation().asInt());
		Util.PrefixLengthShort.write(bb, message);
	}

}
