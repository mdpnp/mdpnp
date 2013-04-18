package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportLinkedResult;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperationLinkedState;
import org.mdpnp.devices.philips.intellivue.dataexport.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExportLinkedResultImpl extends DataExportResultImpl implements DataExportLinkedResult {
	private RemoteOperationLinkedState state;
	private short count;
	
	@Override
	public RemoteOperationLinkedState getLinkedState() {
		return state;
	}
	@Override
	public short getLinkedCount() {
		return count;
	}
	
	public static final int peekInvokeId(ByteBuffer bb) {
		return 0xFFFF & bb.getShort(bb.position() + 2);
	}
	private static final Logger log = LoggerFactory.getLogger(DataExportLinkedResultImpl.class);
	@Override
	public void parse(ByteBuffer bb) {
		state = RemoteOperationLinkedState.valueOf(Bits.getUnsignedByte(bb));
		count = Bits.getUnsignedByte(bb);
		invokeId = Bits.getUnsignedShort(bb);
		int cmdType;
		commandType = CommandType.valueOf(cmdType = Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		if(commandType == null) {
			// TODO error ish
			log.warn("Unrecognized command type " + cmdType);
			bb.position(bb.position() + length);
		} else {
			switch(state) {
			case First:
				command = CommandFactory.buildCommand(commandType, true);
				command.setMessage(this);
				command.parse(bb);
				break;
			case NotFirstNotLast:
				command.parseMore(bb);
				break;
			case Last:
				command.parseMore(bb);
				break;
			}
		}
		
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedByte(bb, state.asShort());
		Bits.putUnsignedByte(bb, count);
		super.format(bb);
	}
}
