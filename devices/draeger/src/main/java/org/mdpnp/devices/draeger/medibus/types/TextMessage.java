package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

public enum TextMessage {
	VentModeIPPV,
	VentModeIPPVAssist,
	VentModeCPPV,
	VentModeCPPVAssist,
	VentModeSIMV,
	VentModeSIMVASB,
	SB,
	ASB,
	CPAP,
	CPAP_ASB,
	MMV,
	MMV_ASB,
	BIPAP,
	SYNCHRON_MASTER,
	SYNCHRON_SLAVE,
	APNEA_VENTILATION,
	DS,
	BIPAP_SMV,
	BIPAP_SMV_ASB,
	BIPAP_APRV,
	Adults,
	Neonates,
	CO2InmmHg,
	CO2InkPa,
	CO2InPercent,
	VentStandby,
	AnesGasHalothane,
	AnesGasEnflurane,
	AnesGasIsoflurane,
	AnesGasDesflurane,
	AnesGasSevoflurane,
	NoAnesGas,
	VentModeManualSpont,
	SelectedLanguage,
	VentModePCV,
	VentModeFreshGasExt,
	CarrierGasAir,
	CarrierGasN2O,
	AnesGas2Halothane,
	AnesGas2Enflurane,
	AnesGas2Isoflurane,
	AnesGas2Desflurane,
	AnesGas2Sevoflurane,
	NoAnesGas2,
	PerformingLeakageTest,
	DeviceInStandby,
	AgentUnitkPa,
	AgentUnitPct,
	HLMModeActive,
	VolumeMode,
	PressureMode,
	PressureSupportMode,
	PressureSupportAdded,
	SyncIntermittentVent;
	
	private static final Map<java.lang.Byte, TextMessage> fromByte;
	
	private byte b;
	
	static {
		try {
			fromByte = EnumHelper.build(TextMessage.class, "text-message.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final TextMessage fromByte(byte b) {
		return fromByte.get(b);
	
	}
	
	public final java.lang.Byte toByte() {
		return b;
	}
	public static final Object fromByteIf(byte b) {
		if(fromByte.containsKey(b)) {
			return fromByte.get(b);
		} else {
			return b;
		}
	}
}
