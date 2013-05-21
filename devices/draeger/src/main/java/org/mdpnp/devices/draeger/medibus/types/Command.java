package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;


/**
 * Available Medibus commands
 * @author jplourde
 *
 */
public enum Command {
	NoOperation,
	InitializeComm,
	StopComm,
	ReqMeasuredDataCP1,
	ReqMeasuredDataCP2,
	ReqLowAlarmLimitsCP1,
	ReqLowAlarmLimitsCP2,
	ReqHighAlarmLimitsCP1,
	ReqHighAlarmLimitsCP2,
	ReqAlarmsCP1,
	ReqAlarmsCP2,
	ReqDateTime,
	ReqDeviceSetting,
	ReqTextMessages,
	ReqDeviceId,
	TimeChanged,
	ConfigureResponse,
	ReqRealtimeConfig,
	ConfigureRealtime,
	RealtimeConfigChanged,
	Corrupt;
	
	private static final Map<java.lang.Byte, Command> fromByte;
	
	private byte b;
	
	static {
		try {
			fromByte = EnumHelper.build(Command.class, "command.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final Command fromByte(byte b) {
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
