package org.mdpnp.devices.philips.intellivue.data;


public enum NomPartition {
	Object,
	Scada,
	Event,
	Dimension,
	ParameterGroup,
	Infrastructure;
	
	public short asShort() {
		switch(this) {
		case Object:
			return 1;
		case Scada:
			return 2;
		case Event:
			return 3;
		case Dimension:
			return 4;
		case ParameterGroup:
			return 6;
		case Infrastructure:
			return 8;
		default:
			throw new IllegalArgumentException("Unknown Nomenclature Partition:"+this);
		}
	}
	
	public static NomPartition valueOf(int s) {
			switch(s) {
			case 1:
				return NomPartition.Object;
			case 2:
				return NomPartition.Scada;
			case 3:
				return NomPartition.Event;
			case 4:
				return NomPartition.Dimension;
			case 6:
				return NomPartition.ParameterGroup;
			case 8:
				return NomPartition.Infrastructure;
			default:
				throw new IllegalArgumentException("Unknown Nomenclature Partition:"+(0xFFFF&s));
			}
	}

}
