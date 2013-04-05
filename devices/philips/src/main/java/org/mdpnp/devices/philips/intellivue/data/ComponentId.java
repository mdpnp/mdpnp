package org.mdpnp.devices.philips.intellivue.data;

public enum ComponentId {
	ID_COMP_PRODUCT,
	ID_COMP_CONFIG,
	ID_COMP_BOOT,
	ID_COMP_MAIN_BD,
	ID_COMP_APPL_SW;
	
	public static ComponentId valueOf(int x) {
		switch(x) {
		case 0x0008:
			return ID_COMP_PRODUCT;
		case 0x0010:
			return ID_COMP_CONFIG;
		case 0x0018:
			return ID_COMP_BOOT;
		case 0x0050:
			return ID_COMP_MAIN_BD;
		case 0x0058:
			return ID_COMP_APPL_SW;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case ID_COMP_PRODUCT:
			return 0x0008;
		case ID_COMP_CONFIG:
			return 0x0010;
		case ID_COMP_BOOT:
			return 0x0018;
		case ID_COMP_MAIN_BD:
			return 0x0050;
		case ID_COMP_APPL_SW:
			return 0x0058;
		default:
			throw new IllegalArgumentException("Unknown ComponentId:"+this);
		}
	}
}
