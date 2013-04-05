package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class MeasureMode implements Value {
	private int bitfield;

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, bitfield);
	}

	@Override
	public void parse(ByteBuffer bb) {
		bitfield = Bits.getUnsignedShort(bb);
	}
	
	private static final int CO2_SIDESTREAM = 0x0400;
	
	public boolean isCO2SideStream() {
		return 0 != (CO2_SIDESTREAM & bitfield);
	}
	
	
	private static final int ECG_PACED = 0x0200;
	public boolean isECGPaced() {
		return 0 != (ECG_PACED & bitfield);
	}
	
	private static final int ECG_NONPACED = 0x0100;
	public boolean isECGNonPaced() {
		return 0 != (ECG_NONPACED & bitfield);
	}
	
	
	private static final int ECG_DIAG = 0x0080;
	
	public boolean isECGDiag() {
		return 0 != (ECG_DIAG & bitfield);
	}
	
	private static final int ECG_MONITOR = 0x0040;
	public boolean isECGMonitor() {
		return 0 != (ECG_MONITOR & bitfield);
	}
	private static final int ECG_FILTER = 0x0020;
	public boolean isECGFilter() {
		return 0 != (ECG_FILTER & bitfield);
	}
	private static final int ECG_MODE_EASI = 0x0008;
	public boolean isECGModeEASI() {
		return 0 != (ECG_MODE_EASI & bitfield);
	}
	private static final int ECG_LEAD_PRIMARY = 0x0004;
	public boolean isECGLeadPrimary() {
		return 0 != (ECG_LEAD_PRIMARY & bitfield);
	}
	
	@Override
	public java.lang.String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(isCO2SideStream()) {
			sb.append("CO2_SIDESTREAM ");
		}
		if(isECGPaced()) {
			sb.append("ECG_PACED " );
		}
		if(isECGNonPaced()) {
			sb.append("ECG_NONPACED ");
		}
		if(isECGDiag()) {
			sb.append("ECG_DIAG ");
		}
		if(isECGMonitor()) {
			sb.append("ECG_MONITOR ");
		}
		if(isECGFilter()) {
			sb.append("ECG_FILTER ");
		}
		if(isECGModeEASI()) {
			sb.append("ECG_MODE_EASI ");
		}
		if(isECGLeadPrimary()) {
			sb.append("ECG_LEAD_PRIMARY ");
		}
		return sb.toString();
	}
	

}
