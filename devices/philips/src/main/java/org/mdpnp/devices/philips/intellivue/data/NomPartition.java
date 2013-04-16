package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum NomPartition implements OrdinalEnum.ShortType {
	Object(1),
	Scada(2),
	Event(3),
	Dimension(4),
	ParameterGroup(6),
	Infrastructure(8);
	
	private final short x;
	
	private NomPartition(int x) {
	    this((short)x);
	}
	
	private NomPartition(short x) {
	    this.x = x;
    }
	
	private static final Map<Short, NomPartition> map = OrdinalEnum.buildShort(NomPartition.class);
	
	public short asShort() {
	    return x;
	}
	
	public static NomPartition valueOf(int s) {
	    return valueOf((short)s);
	}
	
	public static NomPartition valueOf(short s) {
	    return map.get(s);
	}

}
