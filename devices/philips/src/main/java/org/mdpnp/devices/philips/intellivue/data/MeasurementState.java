package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class MeasurementState implements Parseable, Formatable {

	private int state;

	private static final int INVALID = 0x8000;
	private static final int QUESTIONABLE = 0x4000;
	private static final int UNAVAILABLE = 0x2000;
	private static final int CALIBRATION_ONGOING = 0x1000;
	private static final int TEST_DATA = 0x0800;
	private static final int DEMO_DATA = 0x0400;
	private static final int VALIDATED_DATA = 0x0080;
	private static final int EARLY_INDICATION = 0x0040;
	private static final int MSMT_ONGOING = 0x0020;
	private static final int MSMT_STATE_IN_ALARM = 0x0002;
	private static final int MSMT_STATE_AL_INHIBITED = 0x0001;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, state);
	}

	@Override
	public void parse(ByteBuffer bb) {
		state = Bits.getUnsignedShort(bb);
	}
	
	public boolean isValid() {
		return 0 == (0xFF00 & state); 
	}
	
	/**
	 * The source detects a sufficient degradation to render the data meaningless.
	 */
	public boolean isInvalid() {
		return 0 != (INVALID & state);
	}
	
	/**
	 * A problem exists, but it is still appropriate to present the data. This occurs
	 * when (1) either the degradation in the data is marginal or (2) the source cannot make a definite
	 * judgement on the reliability of the data.
	 */
	public boolean isQuestionable() {
		return 0 != (QUESTIONABLE & state);
	}
	
	/**
	 * The signal does not permit derivation of the numeric in question. This could be a
	 * transient state (e.g. first breath detected after an apnea -> no rate available), or a continuous state (no
	 * etCO2 detection possible on a flat CO2 wave).
	 */
	public boolean isUnavailable() {
		return 0 != (UNAVAILABLE & state);
	}
	
	/**
	 * Parameter is currently being calibrated.
	 */
	public boolean isCalibrationOngoing() {
		return 0 != (CALIBRATION_ONGOING & state);
	}
	
	/**
	 * The signal is an automatically generated test signal only and is not a valid patient
	 * signal. If this bit is set, the value is not suitable for patient diagnosis.
	 */
	public boolean isTestData() {
		return 0 != (TEST_DATA & state);
	}
	
	/** 
	 * The IntelliVue monitor runs in demonstration mode, the signal is automatically
	 * generated and is not a valid patient signal. If this bit is set, the value is not suitable for patient
	 * diagnosis.
	 */
	public boolean isDemoData() {
		return 0 != (DEMO_DATA & state);
	}
	
	/**
	 * The value has been manually validated.
	 */
	public boolean isValidatedData() {
		return 0 != (VALIDATED_DATA & state);
	}
	
	/**
	 * The value represents an early estimate of the actual signal (the Non-
	 * Invasive Blood Pressure measurement e.g. sets this bit as soon as it has derived a systolic value, even
	 * if mean and diastolic values are still missing).
	 */
	public boolean isEarlyIndication() {
		return 0 != (EARLY_INDICATION & state);
	}
	
	/**
	 * A new aperiodic measurement is currently ongoing.
	 */
	public boolean isMeasurementOngoing() {
		return 0 != (MSMT_ONGOING & state);
	}
	
	/**
	 * Indicates that the numeric has an active alarm condition
	 */
	public boolean isMeasurementStateInAlarm() {
		return 0 != (MSMT_STATE_IN_ALARM & state);
	}
	
	/**
	 * Alarms are switched off for the numeric (crossed bell)
	 * The measurement is valid if the first octet of the state is all 0.
	 */
	public boolean isMeasurementStateAlarmInhibited() {
		return 0 != (MSMT_STATE_AL_INHIBITED & state);
	}
	
	@Override
	public java.lang.String toString() {
		StringBuilder sb = new StringBuilder("[");
		if(isInvalid()) {
			sb.append("INVALID ");
		}
		if(isQuestionable()) {
			sb.append("QUESTIONABLE ");
		}
		if(isUnavailable()) {
			sb.append("UNAVAILABLE ");
		}
		if(isCalibrationOngoing()) {
			sb.append("CALIBRATION_ONGOING ");
		}
		if(isTestData()) {
			sb.append("TEST_DATA ");
		}
		if(isDemoData()) {
			sb.append("DEMO_DATA ");
		}
		if(isValidatedData()) {
			sb.append("VALIDATED_DATA ");
		}
		if(isEarlyIndication()) {
			sb.append("EARLY_INDICATION "); 
		}
		if(isMeasurementOngoing()) {
			sb.append("MSMT_ONGOING ");
		}
		if(isMeasurementStateInAlarm()) {
			sb.append("MSMT_STATE_IN_ALARM ");
		}
		if(isMeasurementStateAlarmInhibited()) {
			sb.append("MSMT_STATE_AL_INHIBITED ");
		}
		if(sb.charAt(sb.length()-1)==' ') {
			sb.delete(sb.length()-1, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}
	
}
