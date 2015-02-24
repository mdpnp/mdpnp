package org.mdpnp.devices.puritanbennett._840;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PB840Parameters extends PB840 {
    public PB840Parameters(final InputStream in, final OutputStream out) {
        super(in, out);
        fields.put("MISCF", miscFFields);
        fields.put("MISCA", miscAFields);
    }
    

    
    private static final byte[] RSET = new byte[] {'R','S','E','T','\r'};
    private static final byte[] SNDA = new byte[] {'S', 'N', 'D', 'A', '\r'};
    private static final byte[] SNDF = new byte[] {'S','N','D','F','\r'};
    public void sendReset() throws IOException {
        out.write(RSET);
        out.flush();
    }
    public void sendA() throws IOException {
        out.write(SNDA);
        out.flush();
    }
    public void sendF() throws IOException {
        out.write(SNDF);
        out.flush();
    }
    
    private static final Pattern dataField = Pattern.compile("([^,\\03]*)[,\\03]{1,2}");
    
    private static final Logger log = LoggerFactory.getLogger(PB840Parameters.class);
    
    protected final List<String> fieldValues = new ArrayList<String>(173);
    
    
    /**
     * Receives and parses a MISCA or MISCF response from the PB840
     * @return
     * @throws IOException
     */
    public boolean receive() throws IOException {
        String line = in.readLine();
        log.trace("READ A PARAMETER LINE:"+line);
        while(line != null) {
            Matcher dataFieldMatch = dataField.matcher(line);
            fieldValues.clear();
            fieldValues.add("ZERO");
            if(dataFieldMatch.find()) {
                final String responseType = dataFieldMatch.group(1).trim();
                fieldValues.add(responseType);
                if(dataFieldMatch.find()) {
                    fieldValues.add(dataFieldMatch.group(1).trim());
                    @SuppressWarnings("unused")
                    int bytes = Integer.parseInt(dataFieldMatch.group(1).trim());
                    //#bytes between <STX> and <CR>
                    if(dataFieldMatch.find()) {
                        String s = dataFieldMatch.group(1).trim();
                        fieldValues.add(s);
                        int fieldCount = Integer.parseInt(s);//#fields between <STX> and <CR>
                        fieldValues.add("<STX"); // This will keep field numbers consistent
                        try {
                            receiveStartResponse(responseType);
                            for(int i = 0; i < fieldCount; i++) {
                                if(dataFieldMatch.find()) {
                                    fieldValues.add(dataFieldMatch.group(1).trim());
                                } else {
                                    log.warn("Missing next expected field " + fieldValues.size());
                                }
                            }
                            fieldValues.add("<ETX>"); // for consistency
                            fieldValues.add("<CR>");
                            final Field[] fields = this.fields.get(responseType);
                            if(fields != null) {
                                for (Field field : fields) {
                                    field.handle(fieldValues);
                                }
                            } else {
                                log.warn("Unknown response type " + responseType);
                            }
                        } finally {
                            receiveEndResponse();
                        }
                    } else {
                        log.warn("Not a valid response, no field count:"+line);
                    }
                } else {
                    log.warn("Not a valid response, no bytes:"+line);
                }
            } else {         
                log.warn("Not a valid response:"+line);
            }
            line = in.readLine();
        }
        return true;
    }
    

    /**
     * Receives the response for a command type
     * e.g. MISCF - request for ventilator settings, 
     *              monitored data and alarm information
     *      MISCA - request for ventilator settings 
     *              and monitored data
     * @param fieldValues
     */
    public void receiveStartResponse(String type) {
        
    }
    
    public void receiveEndResponse() {
        
    }
    
    public void receiveSetting(String name, String value) {
        
    }
    
    public void receiveNumeric(String name, Units units, String value) {
        
    }
    
    public void receiveAlarmSetting(String name, String lower, String upper) {
        
    }
    
    public void receiveTechnicalAlert(String name, String value) {
        
    }
    
    public void receivePatientAlert(String name, String value) {
        
    }
    
    public void receiveVentilatorId(String id) {
        
    }
    
    abstract class Field {
        final String name;

        public Field(final String name) {
            this.name = name;
        }

        abstract void handle(List<String> fieldValues);
    }

    class Numeric extends Field {
        final int fieldNumber;
        final Units units;

        
        public Numeric(final String name, final Units units, final int fieldNumber) {
            super(name);
            this.units = units;
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveNumeric(name, units, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "Numeric["+name+","+units+","+fieldNumber+"]";
        }
    }
    class AlarmSetting extends Field {
        final int lowFieldNumber, highFieldNumber;

        public AlarmSetting(final String name, final int lowFieldNumber, final int highFieldNumber) {
            super(name);
            this.lowFieldNumber = lowFieldNumber;
            this.highFieldNumber = highFieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveAlarmSetting(name, 
                    lowFieldNumber < 0 ? null : fieldValues.get(lowFieldNumber), 
                    highFieldNumber < 0 ? null : fieldValues.get(highFieldNumber));
        }
        
        @Override
        public String toString() {
            return "AlarmSetting["+name+","+lowFieldNumber+","+highFieldNumber+"]";
        }
    }
    
    class PatientAlert extends Field {
        final int fieldNumber;

        public PatientAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receivePatientAlert(name, fieldValues.get(fieldNumber));
        }
        @Override
        public String toString() {
            return "PatientAlert["+name+","+fieldNumber+"]";
        }
    }
    
    class TechnicalAlert extends Field {
        final int fieldNumber;

        public TechnicalAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveTechnicalAlert(name, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "TechnicalAlert["+name+","+fieldNumber+"]";
        }
    }
    
    class VentilatorId extends Field {
        final int fieldNumber;

        public VentilatorId(final int fieldNumber) {
            super(null);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveVentilatorId(fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "VentilatorId["+name+","+fieldNumber+"]";
        }
    }
    
    
    private final Field[] miscFFields = new Field[] {
            // TODO these should be externalized in a resource file
            new TechnicalAlert("PB_TIME", 5),
            new VentilatorId(6),
            new TechnicalAlert("PB_DATE", 7),
            new TechnicalAlert("PB_VENT_TYPE", 8),
            new TechnicalAlert("PB_MODE", 9),
            new TechnicalAlert("PB_MANDATORY_TYPE", 10),
            new TechnicalAlert("PB_SPONTANEOUS_TYPE", 11),
            new TechnicalAlert("PB_TRIGGER_TYPE", 12),
            new Numeric("PB_SETTING_RESP_RATE", Units.BREATHS_PER_MIN, 13),
            new Numeric("PB_SETTING_TIDAL_VOLUME", Units.LITERS, 14),
            new Numeric("PB_SETTING_PEAK_FLOW", Units.LITERS_PER_MIN, 15),
            new Numeric("PB_SETTING_O2PCT", Units.PERCENT, 16),
            new Numeric("PB_SETTING_PRESS_SENSITIVITY", Units.CMH2O, 17),
            new Numeric("PB_SETTING_PEEP_CPAP", Units.CMH2O, 18),
            new Numeric("PB_SETTING_PLATEAU", Units.SECONDS, 19),
            new Numeric("PB_SETTING_APNEA_INTERVAL", Units.SECONDS, 20),
            new Numeric("PB_SETTING_APNEA_TIDAL_VOLUME", Units.LITERS, 21),
            new Numeric("PB_SETTING_APNEA_RESPIRATORY_RATE", Units.UNKNOWN, 22),
            new Numeric("PB_SETTING_APNEA_PEAK_FLOW", Units.LITERS_PER_MIN, 23),
            new Numeric("PB_SETTING_APNEA_O2PCT", Units.PERCENT, 24),
            new Numeric("PB_SETTING_PCV_APNEA_INSP_PRESSURE", Units.CMH2O, 25),
            new Numeric("PB_SETTING_PCV_APNEA_INSP_TIME", Units.SECONDS, 26),
            new TechnicalAlert("PB_SETTING_APNEA_FLOW_PATTERN", 27),
            new TechnicalAlert("PB_SETTING_MANDATORY_TYPE", 28),
            new Numeric("PB_APNEA_IE_INSP_COMPONENT", Units.UNKNOWN, 29),
            new Numeric("PB_SETTING_IE_EXP_COMPONENT", Units.UNKNOWN, 30),
            new Numeric("PB_SETTING_SUPPORT_PRESSURE", Units.CMH2O, 31),
            new TechnicalAlert("PB_SETTING_FLOW_PATTERN", 32),
            new TechnicalAlert("PB_SETTING_100PCT_O2_SUCTION", 33),
            new AlarmSetting("PB_INSP_PRESSURE", 35, 34),
            new AlarmSetting("PB_EXHALED_MV", 37, 36),
            new AlarmSetting("PB_EXHALED_MAND_TIDAL_VOLUME", 39, 38),
            new AlarmSetting("PB_EXHALED_SPONT_TIDAL_VOLUME", 41, 40),
            new AlarmSetting("PB_RESP_RATE", -1, 42), // or "OFF" ... could
                                                           // be a problem
            new AlarmSetting("PB_INSPIRED_TIDAL_VOLUME", -1, 43),
            new Numeric("PB_SETTING_BASE_FLOW", Units.LITERS_PER_MIN, 44),
            new Numeric("PB_SETTING_FLOW_SENSITIVITY", Units.LITERS_PER_MIN, 45),
            new Numeric("PB_SETTING_PCV_INSP_PRESSURE", Units.SECONDS, 46),
            new Numeric("PB_SETTING_PCV_INSP_TIME", Units.SECONDS, 47),
            new Numeric("PB_SETTING_IE_INSP_COMPONENT", Units.UNKNOWN, 48),
            new Numeric("PB_SETTING_IE_EXP_COMPONENT", Units.UNKNOWN, 49),
            new Numeric("PB_SETTING_CONSTANT_DURING_RATE_CHANGE", Units.UNKNOWN, 50),
            new Numeric("PB_SETTING_TUBE_ID", Units.MILLIMETERS, 51),
            new TechnicalAlert("PB_SETTING_TUBE_TYPE", 52),
            new TechnicalAlert("PB_SETTING_HUMIDIFICATION_TYPE", 53),
            new Numeric("PB_SETTING_HUMIDIFIER_VOLUME", Units.LITERS, 54),
            new TechnicalAlert("PB_SETTING_O2_SENSOR", 55),
            new Numeric("PB_SETTING_DISCONNECT_SENSITIVITY", Units.PERCENT, 56), // or
                                                                                                      // "OFF"
            new Numeric("PB_SETTING_RISE_TIME_PCT", Units.PERCENT, 57),
            new Numeric("PB_SETTING_PAVPCT_SUPPORT", Units.PERCENT, 58),
            new Numeric("PB_SETTING_EXP_SENSITIVITY", Units.UNKNOWN, 59),
            new Numeric("PB_SETTING_IBW", Units.KILOGRAMS, 60),
            new Numeric("PB_SETTING_TARGET_SUPP_VOLUME", Units.LITERS, 61),
            new Numeric("PB_SETTING_HIGH_PEEP", Units.CMH2O, 62),
            new Numeric("PB_SETTING_LOW_PEEP", Units.CMH2O, 63),
            new Numeric("PB_SETTING_HIGH_PEEP_TIME", Units.SECONDS, 64),
            new Numeric("PB_SETTING_HIGH_SP_INS_TIME_LIM", Units.SECONDS, 65),
            new TechnicalAlert("PB_SETTING_CIRCUIT_TYPE", 66),
            new Numeric("PB_SETTING_LOW_PEEP_TIME", Units.SECONDS, 67),
            new Numeric("PB_SETTING_EXPIRATORY_TIME", Units.SECONDS, 68),
            new Numeric("PB_END_INSPIRATORY_PRESSURE", Units.CMH2O, 69),
            new Numeric("PB_RESPIRATORY_RATE", Units.UNKNOWN, 70),
            new Numeric("PB_EXHALED_TIDAL_VOL", Units.LITERS, 71),
            new Numeric("PB_PATIENT_EXHALED_MINUTE_VOL", Units.LITERS_PER_MIN, 72),
            new Numeric("PB_PEAK_AIRWAY_PRESSURE", Units.CMH2O, 73),
            new Numeric("PB_MEAN_AIRWAY_PRESS", Units.CMH2O, 74),
            new Numeric("PB_EXPIRATORY_COMPONENT_IE_RATION", Units.UNKNOWN, 75),
            new TechnicalAlert("PB_IE_RATIO", 76),
            new Numeric("PB_DELIVERED_O2_PCT", Units.PERCENT, 77),
            new Numeric("PB_INSPIRED_TIDAL_VOLUME", Units.LITERS, 78),
            new Numeric("PB_INTRINSIC_PEEP", Units.CMH2O, 79),
            new Numeric("PB_ESTIMATED_TOTAL_RESISTANCE", Units.CMH2O_PER_L_PER_SEC, 80),
            new Numeric("PB_ESTIMATED_PATIENT_RESISTANCE", Units.CMH2O_PER_L_PER_SEC, 81),
            new Numeric("PB_ESTIMATED_PATIENT_ELASTANCE", Units.CMH2O_PER_L, 82),
            new Numeric("PB_ESTIMATED_PATIENT_COMPLIANCE", Units.ML_PER_CMH2O, 83),
            new Numeric("PB_NORM_RAPID_SHALLOW_BREATHING_INDEX", Units.UNKNOWN, 84),
            new Numeric("PB_RAPID_SHALLOW_BREATHING_INDEX", Units.UNKNOWN, 85),
            new Numeric("PB_SPONTANEOUS_PCT_INSPIRATORY_TIME", Units.UNKNOWN, 86),
            new Numeric("PB_MONITORED_PEEP_CMH2O", Units.CMH2O, 87),
            new Numeric("PB_SPONTANEOUS_INSPIRATORY_TIME", Units.SECONDS, 88),
            new Numeric("PB_EXHALED_SPONTANEOUS_MINUTE_VOL", Units.LITERS_PER_MIN, 89),
            new Numeric("PB_INTRINSIC_PEEP_EXPIRATORY_PAUSE", Units.CMH2O, 90),
            new Numeric("PB_TOTAL_PEEP_EXPIRATORY_PAUSE", Units.CMH2O, 91),
            new Numeric("PB_STATIC_COMPLIANCE_INSPIRATORY_PAUSE", Units.ML_PER_CMH2O, 92),
            new Numeric("PB_STATIC_RESISTANCE_INSPIRATORY_PAUSE", Units.CMH2O_PER_L_PER_SEC, 93),
            new Numeric("PB_PLATEAU_PRESSURE_INSPIRATORY_PAUSE", Units.CMH2O, 94),
            new TechnicalAlert("PB_HIGH_SPONTANEOUS_INSPIRATORY_ALERT", 95),
            new Numeric("PB_DYNAMIC_COMPLIANCE_ML_CMH2O_", Units.ML_PER_CMH2O, 96),
            new Numeric("PB_DYNAMIC_RESISTANCE_CMH2OL", Units.CMH2O_PER_L_PER_SEC, 97),
            new Numeric("PB_PEAK_SPONTANEOUS_FLOW", Units.LITERS_PER_MIN, 98),
            new Numeric("PB_PEAK_EXPIRATORY_FLOW", Units.LITERS_PER_MIN, 99),
            new Numeric("PB_END_EXPIRATORY_FOW", Units.LITERS_PER_MIN, 100),
            // 101 is reserved
            new Numeric("PB_NEGATIVE_INSPIRATORY_FORCE", Units.CMH2O, 102),
            new Numeric("PB_P01_PRESSURE_CHANGE", Units.CMH2O, 103),
            new Numeric("PB_VITAL_CAPACITY", Units.LITERS, 104),
            new PatientAlert("PB_ALARM_SILENCE", 105),
            new PatientAlert("PB_APNEA_VENTILATION", 106),
            new PatientAlert("PB_HIGH_EXHALED_MINUTE_VOLUME", 107),
            new PatientAlert("PB_HIGH_EXHALED_TIDAL_VOLUME", 108),
            new PatientAlert("PB_HIGH_O2_PCT", 109),

            new PatientAlert("PB_HIGH_INSPIRATORY_PRESSURE", 110), // FIELD
                                                                        // 110
            new PatientAlert("PB_HIGH_VENTILATOR_PRESSURE", 111),
            new PatientAlert("PB_HIGH_RESPIRATORY_RATE", 112),
            new TechnicalAlert("PB_AC_POWER_LOSS", 113),
            new TechnicalAlert("PB_INOPERATIVE_BATTERY", 114),
            new TechnicalAlert("PB_LOW_BATTERY", 115),
            new TechnicalAlert("PB_LOSS_OF_POWER", 116),
            new PatientAlert("PB_LOW_EXHALED_MANDATORY_TIDAL_VOLUME", 117),
            new PatientAlert("PB_LOW_EXHALED_MINUTE_VOLUME", 118),
            new PatientAlert("PB_LOW_EXHALED_SPONTANEOUS_TIDAL_VOLUME", 119),

            new PatientAlert("PB_LOW_O2_PCT", 120), // FIELD 120
            new PatientAlert("PB_LOW_AIR_SUPPLY_PRESSURE", 121),
            new PatientAlert("PB_LOW_O2_SUPPLY_PRESSURE", 122),
            new TechnicalAlert("PB_COMPRESSOR_INOPERATIVE_ALARM", 123),
            new TechnicalAlert("PB_DISCONNECT", 124),
            new PatientAlert("PB_SEVERE_OCCLUSION", 125),
            new PatientAlert("PB_INSPIRATION_TOO_LONG", 126),
            new PatientAlert("PB_PROCEDURE_ERROR", 127),
            new PatientAlert("PB_COMPLIANCE_LIMITED_TIDAL_VOLUME", 128),
            new PatientAlert("PB_HIGH_INSPIRED_SPONTANEOUS_TIDAL_VOLUME", 129),

            new PatientAlert("PB_HIGH_INSPIRED_MANDATORY_TIDAL_VOLUME", 130), // FIELD
                                                                                   // 130
            new PatientAlert("PB_HIGH_COMPENSATION_LIMIT", 131),
            new PatientAlert("PB_PAV_STARTUP_TOO LONG", 132),
            new PatientAlert("PB_RC_NOT_ASSESSED", 133),
            new PatientAlert("PB_VOLUME_NOT_DELIVERED_VC", 134),
            new PatientAlert("PB_VOLUME_NOT_DELIVERED_VS", 135),
            new PatientAlert("PB_LOW_INSPIRATORY_PRESSURE", 136),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A5", 137),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A10", 138),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A15", 139),

            new TechnicalAlert("PB_TECH_MALFUNCTION_A20", 140), // FIELD
                                                                     // 140
            new TechnicalAlert("PB_TECH_MALFUNCTION_A25", 141), new TechnicalAlert("PB_TECH_MALFUNCTION_A30", 142),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A35", 143),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A40", 144),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A45", 145),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A50", 146),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A55", 147),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A60", 148),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A65", 149),

            new TechnicalAlert("PB_TECH_MALFUNCTION_A70", 150), // FIELD
                                                                     // 150
            new TechnicalAlert("PB_TECH_MALFUNCTION_A75", 151), new TechnicalAlert("PB_TECH_MALFUNCTION_A80", 152),
            new TechnicalAlert("PB_TECH_MALFUNCTION_A85", 153), new Numeric("PB_SP_TIDAL_VOL", Units.LITERS, 154),
            new Numeric("PB_TOTAL_WORK_BREATHING", Units.JOULES_PER_LITER, 155),
            new TechnicalAlert("PB_LEAK_COMPENSATOIN_STATE", 156), new TechnicalAlert("PB_PCT_LEAK", 157),
            new TechnicalAlert("PB_LEAK_AT_PEEP", 158), new TechnicalAlert("PB_V_LEAK", 159),
    // 160-171 reserved
    };

    /**
     * field names for the miscA response
     */
    private final Field[] miscAFields = new Field[] {
            // TODO these should be externalized in a resource file
            new TechnicalAlert("PB_TIME", 5),
            new VentilatorId(6),
            // 7 not used
            new Numeric("PB_SETTING_RESP_RATE", Units.UNKNOWN, 10),
            new Numeric("PB_SETTING_TIDAL_VOLUME", Units.LITERS, 11),
            new Numeric("PB_SETTING_PEAK_FLOW", Units.LITERS_PER_MIN, 12),
            new Numeric("PB_SETTING_O2PCT", Units.PERCENT, 13),
            new Numeric("PB_SETTING_PRESS_SENSITIVITY", Units.CMH2O, 14),
            new Numeric("PB_SETTING_PEEP_OR_PEEP_LOW", Units.CMH2O, 15),
            new Numeric("PB_SETTING_PLATEAU", Units.SECONDS, 16),
            new Numeric("PB_SETTING_APNEA_INTERVAL", Units.SECONDS, 21),
            new Numeric("PB_SETTING_APNEA_TIDAL_VOLUME", Units.LITERS, 22),
            new Numeric("PB_SETTING_APNEA_RESPIRATORY_RATE", Units.UNKNOWN, 23),
            new Numeric("PB_SETTING_APNEA_PEAK_FLOW", Units.LITERS_PER_MIN, 24),
            new Numeric("PB_SETTING_APNEA_O2PCT", Units.PERCENT, 25),
            new Numeric("PB_SETTING_SUPPORT_PRESSURE", Units.CMH2O, 26),
            new TechnicalAlert("PB_SETTING_FLOW_PATTERN", 27),
            new TechnicalAlert("PB_SETTING_100PCT_O2_STATE", 30),
            new Numeric("PB_RESPIRATORY_RATE", Units.UNKNOWN, 34),
            new Numeric("PB_EXHALED_TIDAL_VOL", Units.LITERS, 35),
            new Numeric("PB_EXHALED_MINUTE_VOLUME", Units.LITERS, 36), // TODO
                                                                                       // shouldn't
                                                                                       // MV
                                                                                       // be
                                                                                       // L/min?
                                                                                       // spec
                                                                                       // says
                                                                                       // liters...
            new Numeric("PB_EXHALED_SPONTANEOUS_MINUTE_VOL", Units.LITERS_PER_MIN, 37), // TODO
                                                                                                        // also
                                                                                                        // shouldn't
                                                                                                        // this
                                                                                                        // be
                                                                                                        // a
                                                                                                        // rate?
            new Numeric("PB_MAX_CIRCUIT_PRESSURE", Units.CMH2O, 38),
            new Numeric("PB_MEAN_AIRWAY_PRESSURE", Units.CMH2O, 39),
            new Numeric("PB_END_INSPIRATORY_PRESSURE", Units.CMH2O, 40),
            new Numeric("PB_EXP_COMP_IE_RATIO", Units.UNKNOWN, 41),
            new Numeric("PB_HIGH_CIRCUIT_PRESSURE_LIMIT", Units.CMH2O, 42),
            new AlarmSetting("PB_EXHALED_TIDAL_VOLUME", 45, -1), new AlarmSetting("PB_EXHALED_MINUTE_VOLUME", 46, -1),
            new TechnicalAlert("PB_CIRCUIT_PRESSURE", 48), new PatientAlert("PB_EXHALED_TIDAL_VOLUME", 51),
            new PatientAlert("PB_EXHALED_MINUTE_VOLUME", 52), new PatientAlert("PB_RESPIRATORY_RATE", 53),
            new TechnicalAlert("PB_NO_O2_SUPPLY", 54), new TechnicalAlert("PB_NO_AIR_SUPPLY", 55), new TechnicalAlert("PB_APNEA", 57),
            new TechnicalAlert("PB_TIME2", 60), new TechnicalAlert("PB_DATE", 62),
            new Numeric("PB_STATIC_COMPLIANCE_FROM_INSP_PAUSE", Units.ML_PER_CMH2O, 63),
            new Numeric("PB_STATIC_RESISTANCE_FROM_INSP_PAUSE", Units.CMH2O_PER_L_PER_SEC, 64),
            new Numeric("PB_DYNAMIC_COMPLIANCE", Units.ML_PER_CMH2O, 65),
            new Numeric("PB_DYNAMIC_RESISTANCE", Units.CMH2O_PER_L_PER_SEC, 66),
            new Numeric("PB_NEGATIVE_INSP_FORCE", Units.CMH2O, 67),
            new Numeric("PB_VITAL_CAPACITY", Units.LITERS, 68),
            new Numeric("PB_PEAK_SPONTANEOUS_FLOW", Units.LITERS_PER_MIN, 69),
            new Numeric("PB_VENTILATOR_SET_BASE_FLOW", Units.LITERS_PER_MIN, 70),
            new Numeric("PB_SETTING_FLOW_SENSITIVITY", Units.LITERS_PER_MIN, 71),
            new Numeric("PB_END_INSPIRATORY_PRESSURE", Units.CMH2O, 84),
            new Numeric("PB_INSP_PRESSURE_OR_PEEP_HIGH_SETTING", Units.CMH2O, 85),
            new Numeric("PB_INSP_TIME_OR_PEEP_HIGH_TIME_SETTING", Units.SECONDS, 86),
            new Numeric("PB_SETTING_APNEA_INTERVAL", Units.SECONDS, 87),
            new Numeric("PB_SETTING_APNEA_INSP_PRESSURE", Units.CMH2O, 88),
            new Numeric("PB_SETTING_APNEA_RESP_RATE", Units.UNKNOWN, 89),
            new Numeric("PB_SETTING_APNEA_INSP_TIME", Units.SECONDS, 90),
            new Numeric("PB_SETTING_APNEA_O2_PCT", Units.PERCENT, 91),
            new Numeric("PB_APNEA_HIGH_CIRCUIT_PRESSURE_LIMIT", Units.CMH2O, 92),
            new TechnicalAlert("PB_ALARM_SILENCE_STATE", 93), new TechnicalAlert("PB_APNEA_ALARM_STATUS", 94),
            new TechnicalAlert("PB_SEVERE_OCCLUSION_DISCONNECT", 95), new TechnicalAlert("PB_SETTING_INSP_COMPONENT_OF_IE_RATIO", 96),
            new TechnicalAlert("PB_SETTING_EXP_COMPONENT_OF_IE_RATIO", 97),
            new TechnicalAlert("PB_SETTING_INSP_COMPONENT_OF_APNEA_IE_RATIO", 98),
            new TechnicalAlert("PB_SETTING_EXP_COMPONENT_OF_APNEA_IE_RATION", 99),
            new TechnicalAlert("PB_CONSTANT_DURING_RATE_SETTING_CHANGE", 100), new TechnicalAlert("PB_MONITORED_VALUE_OF_IE_RATIO", 101), };

    protected final Map<String, Field[]> fields = new HashMap<String, Field[]>();
    protected void loadFields(Map<String, Field[]> fields) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(PB840Parameters.class.getResourceAsStream("pb840.fields")));
            String line = null;

            List<Field> currentFields = new ArrayList<Field>();
            String currentResponseType = null;
            int lineNumber = 0;
            
            while (null != (line = br.readLine())) {
                lineNumber++;
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length < 1) {
                        log.warn("Bad line" + lineNumber + ":" + line);
                    } else if(v.length < 2) {
                        if(null != currentResponseType) {
                            fields.put(currentResponseType, currentFields.toArray(new Field[0]));
                            currentFields.clear();
                        }
                        if(!currentFields.isEmpty()) {
                            log.warn(currentFields.size() + " orphaned fields:"+currentFields);
                            currentFields.clear();
                        }
                        currentResponseType = v[0];
                    } else {
                        if("N".equals(v[0]) && v.length == 5) {
                            currentFields.add(new Numeric(v[1], Units.valueOf(v[2]), Integer.parseInt(v[3])));
                        } else if("AS".equals(v[0]) && v.length == 4) {
                            currentFields.add(new AlarmSetting(v[1], Integer.parseInt(v[2]), Integer.parseInt(v[3])));
                        } else if("PA".equals(v[0]) && v.length == 3) {
                            currentFields.add(new PatientAlert(v[1], Integer.parseInt(v[2])));
                        } else if("TA".equals(v[0]) && v.length == 3) {
                            currentFields.add(new TechnicalAlert(v[1], Integer.parseInt(v[2])));
                        } else {
                            
                        }
                    }
                }
            }
            if(null != currentResponseType) {
                fields.put(currentResponseType, currentFields.toArray(new Field[0]));
                currentFields.clear();
            }
            if(!currentFields.isEmpty()) {
                log.warn(currentFields.size() + " orphaned fields:"+currentFields);
                currentFields.clear();
            }
            currentResponseType = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
