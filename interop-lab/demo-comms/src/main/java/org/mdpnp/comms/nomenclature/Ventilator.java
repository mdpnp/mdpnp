package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;
import org.mdpnp.comms.data.numeric.UnitCode;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.text.TextImpl;
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.data.waveform.WaveformImpl;

public interface Ventilator {
	Waveform AIRWAY_PRESSURE = new WaveformImpl(Ventilator.class, "AIRWAY_PRESSURE", UnitCode.MILLIBAR);
	Waveform FLOW_INSP_EXP = new WaveformImpl(Ventilator.class, "FLOW_INSP_EXP", UnitCode.L_PER_MIN);
	Waveform O2_INSP_EXP = new WaveformImpl(Ventilator.class, "O2_INSP_EXP", UnitCode.PERCENT);
	Waveform EXP_CO2_MMHG = new WaveformImpl(Ventilator.class, "EXP_CO2_MMHG", UnitCode.MM_HG);
	
	Numeric PERCENT_OXYGEN = new NumericImpl(Ventilator.class, "PERCENT_OXYGEN");
	Numeric MAX_INSPIRATION_FLOW = new NumericImpl(Ventilator.class, "MAX_INSPIRATION_FLOW");
	Numeric INSP_TIDAL_VOLUME = new NumericImpl(Ventilator.class, "INSP_TIDAL_VOLUME");
	Numeric INSPIRATORY_TIME = new NumericImpl(Ventilator.class, "INSPIRATORY_TIME");
	Numeric I_PART = new NumericImpl(Ventilator.class, "I_PART");
	Numeric E_PART = new NumericImpl(Ventilator.class, "E_PART");
	Numeric FREQUENCY_IMV = new NumericImpl(Ventilator.class, "FREQUENCY_IMV");
	Numeric PEEP = new NumericImpl(Ventilator.class, "PEEP");
	Numeric INTERMITTENT_PEEP = new NumericImpl(Ventilator.class, "INTERMITTENT_PEEP");
	Numeric BIPAP_LOW_PRESSURE = new NumericImpl(Ventilator.class, "BIPAP_LOW_PRESSURE");
	Numeric BIPAP_HIGH_PRESSURE = new NumericImpl(Ventilator.class, "BIPAP_HIGH_PRESSURE");
	Numeric BIPAP_LOW_TIME = new NumericImpl(Ventilator.class, "BIPAP_LOW_TIME");
	Numeric BIPAP_HIGH_TIME = new NumericImpl(Ventilator.class, "BIPAP_HIGH_TIME");
	Numeric APNEA_TIME = new NumericImpl(Ventilator.class, "APNEA_TIME");
	Numeric PRESSURE_SUPPORT_PRESSURE = new NumericImpl(Ventilator.class, "PRESSURE_SUPPORT_PRESSURE");
	Numeric MAX_INSPIRATION_AIRWAY_PRESSURE = new NumericImpl(Ventilator.class, "MAX_INSPIRATION_AIRWAY_PRESSURE");
	Numeric FREQUENCY_IPPV = new NumericImpl(Ventilator.class, "FREQUENCY_IPPV");
	Numeric ASB_RAMP = new NumericImpl(Ventilator.class, "ASB_RAMP");
	Numeric INSPIRATORY_PRESSURE = new NumericImpl(Ventilator.class, "INSPIRATORY_PRESSURE");
	
	Numeric INSP_PAUSE_INSP_TIME = new NumericImpl(Ventilator.class, "INSP_PAUSE_INSP_TIME");
	Numeric FLOW_TRIGGER = new NumericImpl(Ventilator.class, "FLOW_TRIGGER");
	
	Numeric COMPLIANCE_FRAC = new NumericImpl(Ventilator.class, "COMPLIANCE_FRAC");
	Numeric INSP_AGENT_KPA = new NumericImpl(Ventilator.class, "INSP_AGENT_KPA");
	Numeric EXP_AGENT_KPA = new NumericImpl(Ventilator.class, "EXP_AGENT_KPA");
	Numeric AMBIENT_PRESSURE = new NumericImpl(Ventilator.class, "AMBIENT_PRESSURE");
	Numeric MEAN_BREATHING_PRESSURE = new NumericImpl(Ventilator.class, "MEAN_BREATHING_PRESSURE");
	Numeric PLATEAU_PRESSURE = new NumericImpl(Ventilator.class, "PLATEAU_PRESSURE");
	Numeric PEEP_BREATHING_PRESSURE = new NumericImpl(Ventilator.class, "PEEP_BREATHING_PRESSURE");
	Numeric PEAK_BREATHING_PRESSURE = new NumericImpl(Ventilator.class, "PEAK_BREATHING_PRESSURE");
	Numeric TIDAL_VOLUME = new NumericImpl(Ventilator.class, "TIDAL_VOLUME");
	Numeric INSP_MAC = new NumericImpl(Ventilator.class, "INSP_MAC");
	Numeric EXP_MAC = new NumericImpl(Ventilator.class, "EXP_MAC");
	Numeric RESPIRATORY_RATE_PRESSURE = new NumericImpl(Ventilator.class, "RESPIRATORY_RATE_PRESSURE");
	Numeric RESPIRATORY_MINUTE_VOLUME_FRAC = new NumericImpl(Ventilator.class, "RESPIRATORY_MINUTE_VOLUME_FRAC");
	Numeric APNEA_DURATION = new NumericImpl(Ventilator.class, "APNEA_DURATION");
	Numeric DELTA_O2 = new NumericImpl(Ventilator.class, "DELTA_O2");
	
	Numeric RESPIRATORY_RATE_VOLUME_PER_FLOW = new NumericImpl(Ventilator.class, "RESPIRATORY_RATE_VOLUME_PER_FLOW");
	Numeric RESPIRATORY_RATE_DERIVED = new NumericImpl(Ventilator.class, "RESPIRATORY_RATE_DERIVED");
	Numeric INSP_CO2_PCT = new NumericImpl(Ventilator.class, "INSP_CO2_PCT");
	Numeric INSP_CO2_MMHG = new NumericImpl(Ventilator.class, "INSP_CO2_MMHG");
	Numeric END_TIDAL_CO2_PERCENT = new NumericImpl(Ventilator.class, "END_TIDAL_CO2_PERCENT");
	Numeric END_TIDAL_CO2_KPA = new NumericImpl(Ventilator.class, "END_TIDAL_CO2_KPA");
	Numeric END_TIDAL_CO2_MMHG = new NumericImpl(Ventilator.class, "END_TIDAL_CO2_MMHG");
	Numeric INSP_AGENT_PCT = new NumericImpl(Ventilator.class, "INSP_AGENT_PCT");
	Numeric EXP_AGENT_PCT = new NumericImpl(Ventilator.class, "EXP_AGENT_PCT");
	Numeric EXP_O2 = new NumericImpl(Ventilator.class, "EXP_O2");
	Numeric INSP_O2 = new NumericImpl(Ventilator.class, "INSP_O2");
	Numeric INSP_N2O_PCT = new NumericImpl(Ventilator.class, "INSP_N2O_PCT");
	Numeric EXP_N2O_PCT = new NumericImpl(Ventilator.class, "EXP_N2O_PCT");
	Numeric INSP_CO2_KPA = new NumericImpl(Ventilator.class, "INSP_CO2_KPA");

	
	Text START_INSPIRATORY_CYCLE = new TextImpl(Ventilator.class, "START_INSPIRATORY_CYCLE");
	Text START_EXPIRATORY_CYCLE = new TextImpl(Ventilator.class, "START_EXPIRATORY_CYCLE");
	
	Numeric RESPIRATORY_RATE = new NumericImpl(Ventilator.class, "RESPIRATORY_RATE");
	

	
	/*
	Numeric TRIGGER_PRESSURE = 
	0x11	ApneaTime	sec
	0x12	PressureSupportPressure	mbar
	0x13	MaxInspirationAirwayPressure	mbar
	0x15	TriggerPressure	mbar
	0x16	TachyapneaFrequency	OnePerMin
	0x17	TachyapneaDuration	sec
	0x27	InspPause_InspTime	pct
	0x29	FlowTrigger	LPerMin
	0x2E	ASBRamp	sec
	0x2F	FreshgasFlow	mLPerMin
	0x42	MinimalFrequency	OnePerMin
	0x45	InspiratoryPressure	mbar
	0x4A	Age	a
	0x4B	Weight	kg
	*/
}
