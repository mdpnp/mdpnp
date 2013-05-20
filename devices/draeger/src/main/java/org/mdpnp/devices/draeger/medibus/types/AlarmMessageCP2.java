package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

/**
 * Alarm Messages of Code Page 2
 */
public enum AlarmMessageCP2 {
	InspN2OHigh,
	ExpHalothaneExceedsHighLimit,
	ExpEnfluraneExceedsHighLimit,
	ExpIsofluraneExceedsHighLimit,
	ExpDesfluraneExceedsHighLimit,
	ExpSevofluraneExceedsHighLimit,
	InspFlowSensorInoperable,
	PowerSupplyError,
	O2CylinderPressureLowWithoutWallSupply,
	O2CylinderEmptyWithoutWallSupply,
	O2CylinderNotConnected,
	N2OCylinderEmpty,
	N2ODeliveryFailure,
	O2DeliveryFailure,
	AIRDeliveryFailure,
	SetFreshGasFlowNotAttained,
	InternalExternalSwitchoverValveError,
	CircleOccluded,
	BreathingSystemDisconnected,
	LossOfData,
	ApneaVentilation,
	CircleLeakage,
	VentNotInLockedPosition,
	SetTidalVolumeNotAttained,
	SettingCanceled,
	FreshGasFlowTooHigh,
	FreshGasFlowActive,
	OxygenCylinderOpen,
	N2OCylinderOpen,
	AirCylinderOpen,
	N2OCylinderSensorNotConnected,
	AirCylinderSensorNotConnected,
	O2CylinderSensorNotConnected,
	AirCylinderPressureLow,
	AirFreshGasFlowMeasurementInoperable,
	O2FreshGasFlowMeasurementInoperable,
	N2OFreshGasFlowMeasurementInoperable,
	NoAirSupply,
	NoN2OSupply;
	
	
	private static final Map<java.lang.Byte, AlarmMessageCP2> fromByte;
	
	private byte b;
	
	static {
		try {
			fromByte = EnumHelper.build(AlarmMessageCP2.class, "alarm-message-cp2.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final AlarmMessageCP2 fromByte(byte b) {
		return fromByte.get(b);
	
	}
	
	public static final Object fromByteIf(byte b) {
		if(fromByte.containsKey(b)) {
			return fromByte.get(b);
		} else {
			return b;
		}
	}
	
	public final java.lang.Byte toByte() {
		return b;
	}
	
}
