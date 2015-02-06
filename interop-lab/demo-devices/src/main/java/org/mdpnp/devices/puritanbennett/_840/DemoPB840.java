package org.mdpnp.devices.puritanbennett._840;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoPB840 extends AbstractDelegatingSerialDevice<PB840> {
    private static final Logger log = LoggerFactory.getLogger(DemoPB840.class);
    private InstanceHolder<ice.SampleArray> flowSampleArray, pressureSampleArray;
    
    private static final int IGNORED_FIELDS = 5;// to skip fields: header, # of bytes, # of fields, <STX>
    
    private class MyPB840Waveforms extends PB840Waveforms {

        public MyPB840Waveforms(InputStream input, OutputStream output) {
            super(input, output);
        }
        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            flowSampleArray = sampleArraySample(flowSampleArray, flow, rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, null);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE, 50, null);
        }
    }
    
    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "840";
        writeDeviceIdentity();
    }
    
    //settings
    protected InstanceHolder<ice.Numeric> respRateSetting, tidalVolSetting, peakFlowSetting, o2PctSetting, pressureSensitivitySetting;
    protected InstanceHolder<ice.Numeric> apneaIntervalSetting, apneaTidalVolSetting, apneaRespiratoryRateSetting, apneaPeakFlowSetting, apneaO2PctSetting;
    //monitored data
    protected InstanceHolder<ice.Numeric> respRate, totalRespiratoryRate, exhaledTidalVolume, exhaledMinuteVolume, spontMinuteVol;
    //alarms
    protected InstanceHolder<ice.AlarmSettings> inspPressure, exhaledMV, exhaledMandTidalVolume, exhaledSpontTidalVolume, respRateAlarm, inspiredTidalVolume;
    
    //System Alarms
    protected InstanceHolder<ice.Alert> alarmSilence;//field 105 XXX what should be the most appropriate type for these?
    //Patient Alarms
    //protected InstanceHolder<ice.PatientAlertTopic> Patient Alarms
    
    protected final List<InstanceHolder<ice.Numeric>> otherFields = new ArrayList<InstanceHolder<ice.Numeric>>();
    
//    private static final String[] fieldNames = new String[] {
//        "PB_TIME", null, "PB_DATE", "PB_VENT_TYPE", "PB_MODE", "PB_MANDATORY_TYPE", "PB_SPONTANEOUS_TYPE", "PB_TRIGGER_TYPE",
//        "PB_SETTING_RESP_RATE", "PB_SETTING_TIDAL_VOLUME", "PB_SETTING_PEAK_FLOW", "PB_SETTING_O2PCT",
//        "PB_SETTING_PRESS_SENSITIVITY", "PB_SETTING_PEEP_CPAP", "PB_SETTING_PLATEAU", "PB_SETTING_APNEA_INTERVAL",
//        "PB_SETTING_APNEA_TIDAL_VOLUME", "PB_SETTING_APNEA_RESPIRATORY_RATE", "PB_SETTING_APNEA_PEAK_FLOW",
//        "PB_SETTING_APNEA_O2PCT", "PB_SETTING_PCV_APNEA_INSP_PRESSURE", "PB_SETTING_PCV_APNEA_INSP_TIME",
//        "PB_SETTING_APNEA_FLOW_PATTERN", "PB_SETTING_MANDATORY", "PB_APNEA_IE_INSP_COMPONENT",
//        "PB_SETTING_IE_EXP_COMPONENT", "PB_SETTING_SUPPORT_PRESSURE", "PB_SETTING_FLOW_PATTERN",
//        "PB_SETTING_100PCT_O2_SUCTION", null /* insp press high alarm*/, null /* exp press low alarm*/,
//        null /* exhaled MV high*/, null /* exhaled MV low*/, null /* exhaled mand tidal volume high*/,
//        null /* exhaled mand tidal volume low*/, null /* exhaled spont tidal volume high*/,
//        null /* exhaled spont tidal volume low*/, null /* high resp rate */, null /* high inspired tidal volume*/,
//        "PB_SETTING_BASE_FLOW", "PB_SETTING_FLOW_SENSITIVITY", "PB_SETTING_PCV_INSP_PRESSURE",
//        "PB_SETTING_PCV_INSP_TIME", "PB_SETTING_IE_INSP_COMPONENT", "PB_SETTING_IE_EXP_COMPONENT",
//        "PB_SETTING_CONSTANT_DURING_RATE_CHANGE", "PB_SETTING_TUBE_ID", "PB_SETTING_TUBE_TYPE",
//        "PB_SETTING_HUMIDIFICATION_TYPE", "PB_SETTING_HUMIDIFIER_VOLUME", "PB_SETTING_O2_SENSOR",
//        "PB_SETTING_DISCONNECT_SENSITIVITY", "PB_SETTING_RISE_TIME_PCT", "PB_SETTING_PAVPCT_SUPPORT",
//        "PB_SETTING_EXP_SENSITIVITY", "PB_SETTING_IBW", "PB_SETTING_TARGET_SUPP_VOLUME", 
//    };
    
    private static final String[] miscFfieldNames = new String[] {
    	"PB_TIME",//field 5
    	null, //ventilator ID
    	"PB_DATE", 
    	"PB_VENT_TYPE",
    	"PB_MODE",

    	"PB_MANDATORY_TYPE", //field 10
    	"PB_SPONTANEOUS_TYPE",
    	"PB_TRIGGER_TYPE",
    	"PB_SETTING_RESP_RATE",
    	"PB_SETTING_TIDAL_VOLUME",
    	"PB_SETTING_PEAK_FLOW",
    	"PB_SETTING_O2PCT",
    	"PB_SETTING_PRESS_SENSITIVITY", //field 17
    	"PB_SETTING_PEEP_CPAP",
    	"PB_SETTING_PLATEAU",

    	"PB_SETTING_APNEA_INTERVAL",//field 20
    	"PB_SETTING_APNEA_TIDAL_VOLUME",
    	"PB_SETTING_APNEA_RESPIRATORY_RATE",
    	"PB_SETTING_APNEA_PEAK_FLOW",
    	"PB_SETTING_APNEA_O2PCT",
    	"PB_SETTING_PCV_APNEA_INSP_PRESSURE",
    	"PB_SETTING_PCV_APNEA_INSP_TIME", //field 26
    	"PB_SETTING_APNEA_FLOW_PATTERN",
    	"PB_SETTING_MANDATORY_TYPE",
    	"PB_APNEA_IE_INSP_COMPONENT",

    	"PB_SETTING_IE_EXP_COMPONENT",//field 30
    	"PB_SETTING_SUPPORT_PRESSURE",
    	"PB_SETTING_FLOW_PATTERN",
    	"PB_SETTING_100PCT_O2_SUCTION",
    	null /* insp press high alarm*/,
    	null /* exp press low alarm*/,
    	null /* exhaled MV high*/,
    	null /* exhaled MV low*/,
    	null /* exhaled mand tidal volume high*/,
    	null /* exhaled mand tidal volume low*/,

    	null /* exhaled spont tidal volume high*/,//field 40
    	null /* exhaled spont tidal volume low*/,
    	null /* high resp rate */,
    	null /* high inspired tidal volume*/,
    	"PB_SETTING_BASE_FLOW",//field 44
    	"PB_SETTING_FLOW_SENSITIVITY",
    	"PB_SETTING_PCV_INSP_PRESSURE",
    	"PB_SETTING_PCV_INSP_TIME",
    	"PB_SETTING_IE_INSP_COMPONENT",
    	"PB_SETTING_IE_EXP_COMPONENT",

    	"PB_SETTING_CONSTANT_DURING_RATE_CHANGE",//field 50
    	"PB_SETTING_TUBE_ID",//field 51
    	"PB_SETTING_TUBE_TYPE",
    	"PB_SETTING_HUMIDIFICATION_TYPE",
    	"PB_SETTING_HUMIDIFIER_VOLUME",
    	"PB_SETTING_O2_SENSOR",
    	"PB_SETTING_DISCONNECT_SENSITIVITY",
    	"PB_SETTING_RISE_TIME_PCT",
    	"PB_SETTING_PAVPCT_SUPPORT",
    	"PB_SETTING_EXP_SENSITIVITY",

    	"PB_SETTING_IBW", //field 60
    	"PB_SETTING_TARGET_SUPP_VOLUME",
    	"PB_SETTING_HIGH_PEEP",
    	"PB_SETTING_LOW_PEEP",
    	"PB_SETTING_HIGH_PEEP_TIME",
    	"PB_SETTING_HIGH_SP_INS_TIME_LIM",
    	"PB_SETTING_CIRCUIT_TYPE",
    	"PB_SETTING_LOW_PEEP_TIME",
    	"PB_SETTING_EXPIRATORY_TIME",
    	"PB_END_INSPIRATORY_PRESSURE",

    	"PB_RESPIRATORY_RATE",//FIELD 70
    	"PB_EXHALED_TIDAL_VOL",
    	"PB_PATIENT_EXHALED_TIDAL_VOL",
    	"PB_PEAK_AIRWAY_PRESSURE",
    	"PB_MEAN_AIRWAY_PRESS",
    	"PB_EXPIRATORY_COMPONENT_IE_RATION",
    	"PB_IE_RATIO",
    	"PB_DELIVERED_O2",
    	"PB_INSPIRED_TIDAL_VOLUME",
    	"PB_INTRINSIC_PEEP",

    	"PB_ESTIMATED_TOTAL_RESISTANCE", //FIELD 80 
    	"PB_ESTIMATED_PATIENT_RESISTANCE",
    	"PB_ESTIMATED_PATIENT_ELASTANCE",
    	"PB_ESTIMATED_PATIENT_COMPLIANCE",
    	"PB_SALLOW_BREATHING_INDEX",
    	"PB_SPONTANEOUS_PCT_INSPIRATORY_TIME",
    	"PB_MONITORED_PEEP_CMH2O",
    	"PB_SPONTANEOUS_INSPIRATORY_TIME",
    	"PB_EXHALED_SPONTANEOUS_MINUTE_VOL",

    	"PB_INTRINSIC_PEEP_EXPIRATORY_PAUSE", //FIELD 90
    	"PB_TOTAL_PEEP_EXPIRATORY_PAUSE",
    	"PB_STATIC_COMPLIANCE_INSPIRATORY_PAUSE",
    	"PB_STATIC_RESISTANCE_INSPIRATORY_PAUSE",
    	"PB_PLATEAU_PRESSURE_INSPIRATORY_PAUSE",
    	"PB_HIGH_SPONTANEOUS_INSPIRATORY_ALERT",
    	"PB_DYNAMIC_COMPLIANCE_ML_CMH2O_",
    	"PB_DYNAMIC_RESISTANCE_CMH2OL",
    	"PB_PEAK_SPONTANEOUS_FLOW",
    	"PB_PEAK_EXPIRATORY_FLOW",

    	"PB_END_EXPIRATORY_FOW",//FIELD 100
    	null,//reserved
    	"PB_NEGATIVE_INSPIRATORY_FORCE",
    	"PB_P01_PRESSURE_CHANGE",
    	"PB_VITAL_CAPACITY",
    	"PB_ALARM_SILENCE",
    	"PB_APNEA_VENTILATION_ALARM",
    	"PB_HIGH_EXHALED_MINUTE_ALARM",
    	"PB_HIGH_EXHALED_TIDAL_VOL",
    	"PB_HIGH_O2_PCT_ALARM", 

    	"PB_HIGH_INSPIRATORY_PRESSURE_ALARM",//FIELD 110
    	"PB_HIGH_VENTILATOR_PRESSURE_ALARM",
    	"PB_HIGH_RESPIRATORY_RATE_ALARM",
    	"PB_AC_POWER_LOST_ALARM",
    	"PB_INOPERATIVE_BATTERY_ALARM",
    	"PB_LOW_BATTERY_ALARM",
    	"PB_LOSS_POWER_ALARM",
    	"PB_LOW_EXHA_MANDATORY_TIDAL_VOL",
    	"PB_LOW_EXHA_MINUTE_TIDAL_VOL",
    	"PB_LOW_EXHA_SPONTANEOUS_TIDAL_VOL",

    	"PB_LOW_O2_ALARM",//FIELD 120
    	"PB_LOW_AIR_SUPPLY_PRESSURE_ALRM",
    	"PB_LOW_O2_SUPPLY_PRESS_ALARM",
    	"PB_COMPRESSOR_INOPERATIVE_ALARM",
    	"PB_DISCONNECT_ALARM",
    	"PB_SEVERE_OCCLUSION_ALARM",
    	"PB_INSPIRATION_TOO_LONG_ALARM",
    	"PB_PROCEDURE_ERROR",
    	"PB_COMPLIANCE_LIMITED_TIDAL_VOL",
    	"PB_HIGH_INSP_SPT_TIDAL_VOL",

    	"PB_HIGH_INSPIRED_MANDATORY_TIDAL_VOL",//FIELD 130
    	"PB_HIGH_COMPENSATION_LIMIT",
    	"PB_PAV_STARTUP_TOOLONG_ALARM",
    	"PB_RC_NOT_ASSESSED_ALARM",
    	"PB_VOLUME_NOT_DELIVERED_VC_ALARM",
    	"PB_VOLUMEN_NOT_DELIVERED_VS_ALARM",
    	"PB_LOW_INSPIRATORY_PRESS_ALARM",
    	"PB_TECH_MALFC_A5",
    	"PB_TECH_MALFC_A10",
    	"PB_TECH_MALFC_A15",

    	"PB_TECH_MALFC_A20", //FIELD 140
    	"PB_TECH_MALFC_A25",
    	"PB_TECH_MALFC_A30",
    	"PB_TECH_MALFC_A35",
    	"PB_TECH_MALFC_A40",
    	"PB_TECH_MALFC_A45",
    	"PB_TECH_MALFC_A50",
    	"PB_TECH_MALFC_A55",
    	"PB_TECH_MALFC_A60",
    	"PB_TECH_MALFC_A65",

    	"PB_TECH_MALFC_A70",//FIELD 150
    	"PB_TECH_MALFC_A75",
    	"PB_TECH_MALFC_A80",
    	"PB_TECH_MALFC_A85",
    	"PB_SP_TIDAL_VOL",
    	"PB_TOTAL_WORK_BREATHING",
    	"PB_LEAK_COMPENSATOIN_STATE",
    	"PB_PCT_LEAK",
    	"PB_LEAK_AT_PEEP",
    	"PB_V_LEAK",

    	null, //field 160
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,

    	null, //field 170
    	null,
    	
    };
    
    /**
     * field names for the miscA response
     */
    private static final String[] miscAfieldNames = new String[] {
    	"PB_TIME",//field 5
    	null, //ventilator ID
    	null,
    	"PB_DATE", 
    	"PB_MODE",

    	"PB_SETTING_RESP_RATE",//field 10
    	"PB_SETTING_TIDAL_VOLUME",
    	"PB_SETTING_PEAK_FLOW",
    	"PB_SETTING_O2PCT",
    	"PB_SETTING_PRESS_SENSITIVITY",
    	"PB_SETTING_PEEP_CPAP",
    	"PB_SETTING_PLATEAU",
    	null,
    	null,
    	null,

    	null,//field 20
    	"PB_SETTING_APNEA_INTERVAL",
    	"PB_SETTING_APNEA_TIDAL_VOLUME",
    	"PB_SETTING_APNEA_RESPIRATORY_RATE",
    	"PB_SETTING_APNEA_PEAK_FLOW",
    	"PB_SETTING_APNEA_O2PCT",
    	"PB_SETTING_PCV_APNEA_INSP_PRESSURE",
    	"PB_SETTING_PRESSURE_SUPPORT", 
    	"PB_SETTING_FLOW_PATTERN",//field 27
    	null,
    	null,

    	"PB_O2_PCT_STATE", //field 30
    	null,
    	null,
    	null,
    	"PB_TOTAL_REPS_RATE",
    	"PB_EXHALED_TIDAL_VOL",
    	"PB_EXHALED_MINUTE_VOL",
    	"PB_SPONTANEOUS_VOL_", 
    	"PB_MAX_CIRCUIT_PRESSURE",
    	"PB_MEAN_AIRWAY_PRESSURE",

    	"PB_END_INSPIRATORY_PRESS",//FIELD 40
    	"PB_EXPIRATORY_COMPONENT_IE_RAT",
    	"PB_HIGH_CIRCUIT_PRESS_LIMIT", 
    	null,
    	null,
    	"PB_LOW_EXHALED_TIDAL_VOL",
    	"PB_LOW_EXHALED_MINUTE_VOL",
    	"PB_HIGH_RESP_RATE_LIMIT",
    	"PB_HIGH_CIRCUIT_PRESS_ALARM",
    	null,

    	null, //field 50
    	"PB_LOW_EXHALED_TIDAL_VOL_ALARAM",
    	"PB_LOW_EXHALED_MINUTE_VOL_ALARM",
    	"PB_HIGH_RESPIRATORY_RATE_ALARM",
    	"PB_NO_O2_ALARM",
    	"PB_NO_AIR_SUPPLY",
    	null,
    	"PB_APNEA_ALARM",
    	null,
    	null,

    	"PB_VENTILATOR_TIME", //FIELD 60
    	null,
    	"PB_DATE",
    	"PB_SATIC_COMPLIANCE",
    	"PB_SATIC_RESISTANCE",
    	"PB_NEGATIVE_INSPIRATORY_FORCE",
    	"PB_VITAL_CAPACITY",
    	"PB_PEAK_SPONTANEOUS_FLOW",

    	"PB_VENTILATOR_SETBASE_FLOW", //FIELD 70
    	"PB_FLOW_SENSITIVITY_SETTINGS",
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,
    	null,

    	null,//field 80
    	null,
    	null,
    	null,
    	"PB_END_INSPIRATORY_PRESS",
    	"PB_INSPIRATORY_PRESS",
    	"PB_INSPIRATORY_TIME",
    	"PB_SETTING_APNEA_INTERVAL",
    	"PB_APNEA_INSPIRATORY_PRESS_SETTING",
    	"PB_APNEA_RESPIRATORY_RATE_SETTING",

    	"PB_APENA_INPIRATORY_TIME_SETTING",//FIELD 90
    	"PB_APENA_O2_SETTING",
    	"PB_APENEA_HIGH_CIRCUIT_PRESS",
    	"PB_ALARM_SILENCE_STATUS",
    	"PB_APENA_ALARM_STATUS",
    	"PB_SEVERE_OCC_ALARM",
    	"PB_INSPIRATORY_IE",
    	"PB_EXPIRATORY_COMPONENT_IE",
    	"PB_APNEA_INSPIRATORY_COMPONENT",
    	"PB_APNEA_EXPIRATORY_COMPONENT",

    	"PB_MAN_BREATHS",//FIELD 100
    	"PB_MONITORED_IE_VAL",   	
    };
    
    protected static Float parseFloat(String s) {
        if(s == null || s.isEmpty() || "OFF".equals(s)) {
            return null;
        } else {
            return Float.parseFloat(s);
        }
    }
    
    protected static Integer parseInt(String s){
    	Integer i;
    	try{
    		//checks null and empty cases. Are there others, like "OFF"
    		i = Integer.valueOf(s);
    		return i;
    	}catch(NumberFormatException e){
    		return null;
    	}
    }
    
    private class MyPB840Parameters extends PB840Parameters {
        public MyPB840Parameters(InputStream input, OutputStream output) {
            super(input, output);
        }
        @Override
        public void receiveMiscF(List<String> fieldValues) {
            reportConnected("Received MISCF");
            if(!fieldValues.get(1).equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = fieldValues.get(1);
                writeDeviceIdentity();
            }
            //alarms
            inspPressure = alarmSettingsSample(inspPressure, parseFloat(fieldValues.get(30)), parseFloat(fieldValues.get(29)), "PB_INSP_PRESSURE");
            exhaledMV = alarmSettingsSample(exhaledMV, parseFloat(fieldValues.get(32)), parseFloat(fieldValues.get(31)), "PB_EXHALED_MV");
            exhaledMandTidalVolume = alarmSettingsSample(exhaledMandTidalVolume, parseFloat(fieldValues.get(34)), parseFloat(fieldValues.get(33)), "PB_EXHALED_MAND_TIDAL_VOLUME");
            exhaledSpontTidalVolume = alarmSettingsSample(exhaledSpontTidalVolume, parseFloat(fieldValues.get(36)), parseFloat(fieldValues.get(35)), "PB_EXHALED_SPONT_TIDAL_VOLUME");
            respRateAlarm = alarmSettingsSample(respRateAlarm, null, parseFloat(fieldValues.get(37)), "PB_RESP_RATE");
            inspiredTidalVolume = alarmSettingsSample(inspiredTidalVolume, null, parseFloat(fieldValues.get(38)), "PB_INSPIRED_TIDAL_VOLUME");
            
            //settings
            respRateSetting = numericSample(respRateSetting, parseInt(fieldValues.get(8)) /*field 13*/, miscFfieldNames[8], null /*new Time_t(0)*/);
            tidalVolSetting = numericSample(tidalVolSetting, parseInt(fieldValues.get(9)) /*field 14*/, miscFfieldNames[9], null /*new Time_t(0)*/); 
            peakFlowSetting = numericSample(peakFlowSetting, parseInt(fieldValues.get(10)) /*field 15*/, miscFfieldNames[10], null /*new Time_t(0)*/);
            o2PctSetting = numericSample(o2PctSetting, parseInt(fieldValues.get(11)) /*field 16*/, miscFfieldNames[11], null /*new Time_t(0)*/);
            pressureSensitivitySetting = numericSample(pressureSensitivitySetting, parseInt(fieldValues.get(12)) /*field 17*/, miscFfieldNames[12], null /*new Time_t(0)*/);
            apneaIntervalSetting = numericSample(apneaIntervalSetting, parseInt(fieldValues.get(15)) /*field 20*/, miscFfieldNames[15], null /*new Time_t(0)*/);
            apneaTidalVolSetting = numericSample(apneaTidalVolSetting, parseInt(fieldValues.get(16)) /*field 21*/, miscFfieldNames[16], null /*new Time_t(0)*/);
            apneaRespiratoryRateSetting = numericSample(apneaRespiratoryRateSetting, parseInt(fieldValues.get(17)) /*field 22*/, miscFfieldNames[17], null /*new Time_t(0)*/);
            apneaPeakFlowSetting = numericSample(apneaPeakFlowSetting, parseInt(fieldValues.get(18)) /*field 23*/, miscFfieldNames[18], null /*new Time_t(0)*/);
            apneaO2PctSetting = numericSample(apneaO2PctSetting, parseInt(fieldValues.get(19)) /*field 24*/, miscFfieldNames[19], null /*new Time_t(0)*/);
            
            //monitored values
            respRate = numericSample(respRate, parseInt(fieldValues.get(65)) /*field 70*/ , "PB_RESPIRATORY_RATE", null);
            exhaledTidalVolume = numericSample(exhaledTidalVolume, parseInt(fieldValues.get(66)) /* field 71*/, "PB_EXHALED_TIDAL_VOL", null);
            exhaledMinuteVolume = numericSample(exhaledMinuteVolume, parseInt(fieldValues.get(67)) /* field 72*/, miscFfieldNames[67], null);
            
            markOldTechnicalAlertInstances();
            for(int i = 0; i < fieldValues.size(); i++) {
//                String name = i < fieldNames.length ? fieldNames[i] : ("PB_F_"+(i+5));
            	String name = i < miscFfieldNames.length ? miscFfieldNames[i] : ("PB_F_"+(i+IGNORED_FIELDS));//5
                if(null != name) {
                    try {
                        float f = Float.parseFloat(fieldValues.get(i));
                        while(i >= otherFields.size()) {
                            otherFields.add(null);
                        }
                        otherFields.set(i, numericSample(otherFields.get(i), f, name, rosetta.MDC_DIM_DIMLESS.VALUE, null));
                    } catch(NumberFormatException nfe) {
                        writeTechnicalAlert(name, fieldValues.get(i));
                    }
                }
            }
            clearOldTechnicalAlertInstances();
        }
        
        @Override
        public void receiveMiscA(List<String> fieldValues) {
            reportConnected("Received MISCF");
            if(!fieldValues.get(1).equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = fieldValues.get(1);
                writeDeviceIdentity();
            }
            
            //MISCA settings
            respRateSetting = numericSample(respRateSetting, parseInt(fieldValues.get(5)) /*field 10*/, miscAfieldNames[5], null /*new Time_t(0)*/);
            tidalVolSetting = numericSample(tidalVolSetting, parseInt(fieldValues.get(6)) /*field 11*/, miscAfieldNames[6], null /*new Time_t(0)*/);
            peakFlowSetting = numericSample(peakFlowSetting, parseInt(fieldValues.get(7)) /*field 12*/, miscAfieldNames[7], null /*new Time_t(0)*/);
            o2PctSetting = numericSample(o2PctSetting, parseInt(fieldValues.get(8)) /*field 13*/, miscAfieldNames[8], null /*new Time_t(0)*/);
            pressureSensitivitySetting = numericSample(pressureSensitivitySetting, parseInt(fieldValues.get(9)) /*field 14*/, miscAfieldNames[9], null /*new Time_t(0)*/);
            apneaIntervalSetting = numericSample(apneaIntervalSetting, parseInt(fieldValues.get(16)) /*field 21*/, miscAfieldNames[16], null /*new Time_t(0)*/);
            apneaTidalVolSetting = numericSample(apneaTidalVolSetting, parseInt(fieldValues.get(17)) /*field 22*/, miscAfieldNames[17], null /*new Time_t(0)*/);
            apneaRespiratoryRateSetting = numericSample(apneaRespiratoryRateSetting, parseInt(fieldValues.get(18)) /*field 23*/, miscAfieldNames[18], null /*new Time_t(0)*/);
            apneaPeakFlowSetting = numericSample(apneaPeakFlowSetting, parseInt(fieldValues.get(19)) /*field 24*/, miscAfieldNames[19], null /*new Time_t(0)*/);
            apneaO2PctSetting = numericSample(apneaO2PctSetting, parseInt(fieldValues.get(20)) /*field 25*/, miscAfieldNames[20], null /*new Time_t(0)*/);           
            
            //monitored values
            totalRespiratoryRate = numericSample(totalRespiratoryRate, parseInt(fieldValues.get(29)) /* field 34*/, miscAfieldNames[29], null); 
            exhaledTidalVolume = numericSample(exhaledTidalVolume, parseInt(fieldValues.get(30)) /* field 35*/, miscAfieldNames[30], null); 
            exhaledMinuteVolume = numericSample(exhaledMinuteVolume, parseInt(fieldValues.get(31)) /* field 36*/, miscAfieldNames[31], null); 
            spontMinuteVol  = numericSample(spontMinuteVol, parseInt(fieldValues.get(32)) /* field 37*/, miscAfieldNames[32], null);
            
            
            
            markOldTechnicalAlertInstances();
            for(int i = 0; i < fieldValues.size(); i++) {
                String name = i < miscAfieldNames.length ? miscAfieldNames[i] : ("PB_F_"+(i+IGNORED_FIELDS));//+5
                if(null != name) {
                    try {
                        float f = Float.parseFloat(fieldValues.get(i));
                        while(i >= otherFields.size()) {
                            otherFields.add(null);
                        }
                        otherFields.set(i, numericSample(otherFields.get(i), f, name, rosetta.MDC_DIM_DIMLESS.VALUE, null));
                    } catch(NumberFormatException nfe) {
                        writeTechnicalAlert(name, fieldValues.get(i));
                    }
                }
            }
            clearOldTechnicalAlertInstances();
        }
    }

    private class RequestSlowData implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    PB840Parameters params = (PB840Parameters) getDelegate(0);
                    params.sendF();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }

        }
    }
    
    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        switch(idx) {
        case 0:
            ((PB840Parameters)getDelegate(idx)).sendF();
            break;
        default:
            
        }
    }
    
    private ScheduledFuture<?> requestSlowData;
    
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {

        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startRequestSlowData();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopRequestSlowData();
        }
        super.stateChanged(newState, oldState, transitionNote);
    }    
    
    private synchronized void startRequestSlowData() {
        if (null == requestSlowData) {
            requestSlowData = executor.scheduleWithFixedDelay(new RequestSlowData(), 1000L, 1000L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled slow data request task");
        } else {
            log.trace("Slow data request already scheduled");
        }
    }
    private synchronized void stopRequestSlowData() {
        if (null != requestSlowData) {
            requestSlowData.cancel(false);
            requestSlowData = null;
            log.trace("Canceled slow data request task");
        } else {
            log.trace("Slow data request already canceled");
        }
    }

    
    @Override
    protected PB840 buildDelegate(int idx, InputStream in, OutputStream out) {
        switch(idx) {
        case 0:
            return new MyPB840Parameters(in, out);
        case 1:
            return new MyPB840Waveforms(in, out);
        default:
            return null;
        }
    }

    @Override
    protected boolean delegateReceive(int idx, PB840 delegate) throws IOException {
        return delegate.receive();
    }
    
    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx).duplicate();
        switch(idx) {
        case 0:
            serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
            break;
        case 1:
            serialProvider.setDefaultSerialSettings(38400, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
            break;
        }
        
        return serialProvider;
    }
    
    @Override
    protected long getMaximumQuietTime(int idx) {
        return 5000L;
    }
    
    @Override
    protected String iconResourceName() {
        return "pb840.png";
    }

}