package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportError;
import org.mdpnp.devices.philips.intellivue.dataexport.RemoteOperation;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetail;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailAccessDeniedImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailGetListImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidArgumentValueImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidObjectInstanceImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailInvalidScopeImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchActionImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchObjectClassImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailNoSuchObjectInstanceImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailProcessingFailureImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetailSetListImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.error.RemoteError;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class DataExportErrorImpl implements DataExportError {

	private int invoke;
	private RemoteError error;
	private ErrorDetail detail;
	
	private static final ErrorDetail buildErrorDetail(RemoteError error) {
		switch(error) {
		case GetListError:
			return new ErrorDetailGetListImpl();
		case SetListError:
			return new ErrorDetailSetListImpl();
		case NoSuchAction:
			return new ErrorDetailNoSuchActionImpl();
		case NoSuchObjectClass:
			return new ErrorDetailNoSuchObjectClassImpl();
		case NoSuchObjectInstance:
			return new ErrorDetailNoSuchObjectInstanceImpl();
		case AccessDenied:
			return new ErrorDetailAccessDeniedImpl();
		case ProcessingFailure:
			return new ErrorDetailProcessingFailureImpl();
		case InvalidArgumentValue:
			return new ErrorDetailInvalidArgumentValueImpl();
		case InvalidScope:
			return new ErrorDetailInvalidScopeImpl();
		case InvalidObjectInstance:
			return new ErrorDetailInvalidObjectInstanceImpl();
		default:
			throw new IllegalArgumentException("Unknown error type:"+error);
		}
	}
	
	@SuppressWarnings("unused")
    @Override
	public void parse(ByteBuffer bb) {
		invoke = Bits.getUnsignedShort(bb);
		error = RemoteError.valueOf(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		detail = buildErrorDetail(error);
		
		detail.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, invoke);
		Bits.putUnsignedShort(bb, error.asInt());
		Util.PrefixLengthShort.write(bb, detail);
	}
	@Override
	public RemoteError getError() {
		return error;
	}
	@Override
	public int getInvoke() {
		return invoke;
	}
	
	@Override
	public ErrorDetail getErrorDetail() {
		return detail;
	}
	
	@Override
	public String toString() {
		return "[error="+error+",invoke="+invoke+",detail="+detail+"]";
	}
	@Override
	public RemoteOperation getRemoteOperation() {
		return RemoteOperation.Error;
	}

	@Override
	public void setInvoke(int i) {
		this.invoke = i;
	}
	
}
