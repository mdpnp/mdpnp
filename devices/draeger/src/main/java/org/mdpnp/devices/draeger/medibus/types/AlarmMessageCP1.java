package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

/**
 * Alarm messages of Codepage 1
 * @author jplourde
 *
 */
public enum AlarmMessageCP1 {
	AirwayPressureExceedsHighLimit,
	MinuteVolumeBelowLowLimit,
	VolumeNotConstant,
	TachyapenaAlarmDisabled,
	VolumeAlarmDisabled,
	RespiratoryRateExceedsHighLimit,
	ApnoeDetectedByEvita,
	MinuteVolumeExceedsHighLimit,
	VolumeMeasurementInoperable,
	MinVolAlarmDisabled,
	PressureMeasurementInoperable,
	AirwayTemperatureMeasurementInoperable,
	CheckAirwayTemperatureSensor,
	AirwayTemperatureExceedsHighLimit,
	CommunicationErrorRS232Port2,
	InternalCommunicationError,
	InpiredOxygenBelowLowLimit,
	PercentOxygenExceedsHighLimit,
	InspiredOxygenMeasurementInoperable,
	OxygenMeasurementInoperable,
	InpiredOxygenExceedsHighLimit,
	InspiredOxygenBelowLowLimit,
	CheckAirSupply,
	AssistedSpontaneousBreathingExceeds4Seconds,
	DisconnectionVentilator,
	ProblemsWithRespirator,
	CheckExpirationValve,
	TooHighRespiratorDeviceTemp,
	SighModeActive,
	BreathingSystemVented,
	CheckOxygenSupply,
	GasMixerInoperableAdvisory,
	TimeLimitedRespiratoryVolume,
	PressureLimitedRespiratoryVolume,
	HighRespiratorDeviceTemp,
	RespiratorSynchronizationInoperable,
	FailToCycle,
	GasMixerInoperableAlarm,
	CheckFlowSensor,
	PEEPHighPressureLimit,
	EndTidalCO2BelowLowLimit,
	EndTidalCO2ExceedsHighLimit,
	CO2MonInLowAccMode,
	CO2WindowOccluded,
	CO2DeviceFailure,
	CO2SensorDisconnectedOrFault,
	TidalVolumeExceedsHighLimit,
	NeoVolumeMeasurementInoperable,
	ApneaAlarmOff,
	MinuteVolumeAlarmLowOff,
	MinuteVolumeAlarmHighOff,
	InspHalothaneExceedsHighLimit,
	InspEnfluraneExceedsHighLimit,
	InspIsofluraneExceedsHighLimit,
	InspSevofluraneExceedsHighLimit,
	InspDesfluraneExceedsHighLimit,
	InspHalothaneBelowLowLimit,
	InspEnfluraneBelowLowLimit,
	InspIsofluraneBelowLowLimit,
	InspDesfluraneBelowLowLimit,
	InspSevofluraneBelowLowLimit,
	MixedAgentDetected,
	MultigasMonitorDeviceFailure,
	N2OMeasurementInoperable,
	AgentMeasurementInoperable,
	TwoAgentsDetected,
	ApneaCombinedSource,
	ApneaNoVolumeExhaled30Seconds,
	ApneaPressureAbsent15Seconds,
	MeanAirwayPressureBelowMinus2mbar,
	FlowMeasurementInoperable,
	PEEPExceedsPressureThreshold15Seconds,
	ApneaNoCO2Fluct30Seconds,
	InspCO2ExceedsHighLimit,
	CO2PatientSensorLineBlocked,
	CO2AlarmDisabled,
	InspCO2AlarmsOff,
	BatteryLow,
	PrimarySpeakerFailure,
	CommunicationErrorRS232Port1,
	InternalTemperatureHigh,
	FanFailure,
	PowerFail,
	NoSpO2Pulse10Seconds,
	SpO2PulseBelowLowLimit,
	O2SatBelowLowLimit,
	SpO2PulseExceedsHighLimit,
	O2SatExceedsHighLimit,
	SpO2SensorDisconnectedOrFault,
	OximeterAlarmDisabled,
	OximeterDeviceFailure,
	CheckGasSupply,
	CheckO2Supply,
	VentCommunicationLost,
	FreshGasDeliveryFailure,
	CheckN2OSupply,
	CheckSettingOfPmax,
	O2SafetyFlowOpenDuringNormalOperation;
	
	
	private static final Map<java.lang.Byte, AlarmMessageCP1> fromByte;
	
	private byte b;
	
	static {
		try {
			fromByte = EnumHelper.build(AlarmMessageCP1.class, "alarm-message-cp1.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	/**
	 * Returns the CodePage1 Alarm messages associated with the specified byte
	 * @param b byte
	 * @return alarm message
	 */
	public static final AlarmMessageCP1 fromByte(byte b) {
		return fromByte.get(b);
	
	}
	
	/**
	 * If byte b is recognized as an AlarmMessage from CodePage1 then return that AlarmMessage. 
	 * Otherwise returns Byte b
	 * @param b byte
	 * @return alarm message or Byte
	 */
	public static final Object fromByteIf(byte b) {
		if(fromByte.containsKey(b)) {
			return fromByte.get(b);
		} else {
			return b;
		}
	}
	
	/**
	 * The byte associated with this CodePage1 AlarmMessage
	 * @return byte
	 */
	public final java.lang.Byte toByte() {
		return b;
	}
	
}
