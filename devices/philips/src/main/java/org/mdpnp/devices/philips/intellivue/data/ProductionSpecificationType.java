package org.mdpnp.devices.philips.intellivue.data;

public enum ProductionSpecificationType {
	UNSPECIFIED,
	SERIAL_NUMBER,
	PART_NUMBER,
	HW_REVISION,
	SW_REVISION,
	FW_REVISION,
	PROTOCOL_REVISION;
	
	public static ProductionSpecificationType valueOf(int x) {
		switch(x) {
		case 0:
			return UNSPECIFIED;
		case 1:
			return SERIAL_NUMBER;
		case 2:
			return PART_NUMBER;
		case 3:
			return HW_REVISION;
		case 4:
			return SW_REVISION;
		case 5:
			return FW_REVISION;
		case 6:
			return PROTOCOL_REVISION;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case UNSPECIFIED:
			return 0;
		case SERIAL_NUMBER:
			return 1;
		case PART_NUMBER:
			return 2;
		case HW_REVISION:
			return 3;
		case SW_REVISION:
			return 4;
		case FW_REVISION:
			return 5;
		case PROTOCOL_REVISION:
			return 6;
		default:
			throw new IllegalArgumentException("Unknown ProductionSpecificationType:"+this);
		}
	}
}
