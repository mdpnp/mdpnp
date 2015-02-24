package org.mdpnp.devices.puritanbennett._840;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private class MyPB840Waveforms extends PB840Waveforms {

        public MyPB840Waveforms(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            flowSampleArray = sampleArraySample(flowSampleArray, flow, "", rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, null);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, "", rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE,
                    50, null);
        }
    }

    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        fields.put("MISCF", miscFFields);
        fields.put("MISCA", miscAFields);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "840";
        writeDeviceIdentity();
    }

    abstract class PB840Field {
        final String name;

        public PB840Field(final String name) {
            this.name = name;
        }

        abstract void handle(List<String> fieldValues);
    }

    class PB840Numeric extends PB840Field {
        final int fieldNumber;
        final String units;

        public PB840Numeric(final String name, final String units, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
            this.units = units;
        }

        @Override
        void handle(List<String> fieldValues) {
            try {
                numericInstances.put(name,
                        numericSample(numericInstances.get(name), parseFloat(fieldValues.get(fieldNumber)), name, name, units, null));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted numeric " + name + " " + fieldValues.get(fieldNumber), nfe);
            }
        }
    }

    class PB840AlarmSetting extends PB840Field {
        final int lowFieldNumber, highFieldNumber;

        public PB840AlarmSetting(final String name, final int lowFieldNumber, final int highFieldNumber) {
            super(name);
            this.lowFieldNumber = lowFieldNumber;
            this.highFieldNumber = highFieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            try {
                // TODO using FLOAT_MIN, FLOAT_MAX as reserved values because
                // otherwise cannot publish AlarmSettings
                // with only one boundary condition
                alarmSettingsInstances.put(
                        name,
                        alarmSettingsSample(alarmSettingsInstances.get(name),
                                lowFieldNumber >= 0 ? parseFloat(fieldValues.get(lowFieldNumber), Float.MIN_VALUE) : Float.MIN_VALUE,
                                highFieldNumber >= 0 ? parseFloat(fieldValues.get(highFieldNumber), Float.MAX_VALUE) : Float.MAX_VALUE, name));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted alarm setting " + name + " " + (lowFieldNumber >= 0 ? fieldValues.get(lowFieldNumber) : null) + " "
                        + (highFieldNumber >= 0 ? fieldValues.get(highFieldNumber) : null), nfe);
            }
        }
    }

    class PB840PatientAlert extends PB840Field {
        final int fieldNumber;

        public PB840PatientAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            writePatientAlert(name, fieldValues.get(fieldNumber));
        }
    }

    class PB840TechnicalAlert extends PB840Field {
        final int fieldNumber;

        public PB840TechnicalAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            writeTechnicalAlert(name, fieldValues.get(fieldNumber));
        }
    }

    class PB840VentilatorId extends PB840Field {
        final int fieldNumber;

        public PB840VentilatorId(final int fieldNumber) {
            super(null);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            if (!fieldValues.get(fieldNumber).equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = fieldValues.get(fieldNumber);
                writeDeviceIdentity();
            }
        }
    }

    protected Map<String, InstanceHolder<ice.Numeric>> numericInstances = new HashMap<String, InstanceHolder<ice.Numeric>>();
    protected Map<String, InstanceHolder<ice.AlarmSettings>> alarmSettingsInstances = new HashMap<String, InstanceHolder<ice.AlarmSettings>>();

    @Override
    protected void unregisterAllAlarmSettingsInstances() {
        alarmSettingsInstances.clear();
        super.unregisterAllAlarmSettingsInstances();
    }

    @Override
    protected void unregisterAllNumericInstances() {
        numericInstances.clear();
        super.unregisterAllNumericInstances();
    }

    private final PB840Field[] miscFFields = new PB840Field[] {
            // TODO these should be externalized in a resource file
            new PB840TechnicalAlert("PB_TIME", 5),
            new PB840VentilatorId(6),
            new PB840TechnicalAlert("PB_DATE", 7),
            new PB840TechnicalAlert("PB_VENT_TYPE", 8),
            new PB840TechnicalAlert("PB_MODE", 9),
            new PB840TechnicalAlert("PB_MANDATORY_TYPE", 10),
            new PB840TechnicalAlert("PB_SPONTANEOUS_TYPE", 11),
            new PB840TechnicalAlert("PB_TRIGGER_TYPE", 12),
            new PB840Numeric("PB_SETTING_RESP_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 13),
            new PB840Numeric("PB_SETTING_TIDAL_VOLUME", rosetta.MDC_DIM_L.VALUE, 14),
            new PB840Numeric("PB_SETTING_PEAK_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 15),
            new PB840Numeric("PB_SETTING_O2PCT", rosetta.MDC_DIM_PERCENT.VALUE, 16),
            new PB840Numeric("PB_SETTING_PRESS_SENSITIVITY", rosetta.MDC_DIM_CM_H2O.VALUE, 17),
            new PB840Numeric("PB_SETTING_PEEP_CPAP", rosetta.MDC_DIM_CM_H2O.VALUE, 18),
            new PB840Numeric("PB_SETTING_PLATEAU", rosetta.MDC_DIM_SEC.VALUE, 19),
            new PB840Numeric("PB_SETTING_APNEA_INTERVAL", rosetta.MDC_DIM_SEC.VALUE, 20),
            new PB840Numeric("PB_SETTING_APNEA_TIDAL_VOLUME", rosetta.MDC_DIM_L.VALUE, 21),
            new PB840Numeric("PB_SETTING_APNEA_RESPIRATORY_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 22),
            new PB840Numeric("PB_SETTING_APNEA_PEAK_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 23),
            new PB840Numeric("PB_SETTING_APNEA_O2PCT", rosetta.MDC_DIM_PERCENT.VALUE, 24),
            new PB840Numeric("PB_SETTING_PCV_APNEA_INSP_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 25),
            new PB840Numeric("PB_SETTING_PCV_APNEA_INSP_TIME", rosetta.MDC_DIM_SEC.VALUE, 26),
            new PB840TechnicalAlert("PB_SETTING_APNEA_FLOW_PATTERN", 27),
            new PB840TechnicalAlert("PB_SETTING_MANDATORY_TYPE", 28),
            new PB840Numeric("PB_APNEA_IE_INSP_COMPONENT", rosetta.MDC_DIM_DIMLESS.VALUE, 29),
            new PB840Numeric("PB_SETTING_IE_EXP_COMPONENT", rosetta.MDC_DIM_DIMLESS.VALUE, 30),
            new PB840Numeric("PB_SETTING_SUPPORT_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 31),
            new PB840TechnicalAlert("PB_SETTING_FLOW_PATTERN", 32),
            new PB840TechnicalAlert("PB_SETTING_100PCT_O2_SUCTION", 33),
            new PB840AlarmSetting("PB_INSP_PRESSURE", 35, 34),
            new PB840AlarmSetting("PB_EXHALED_MV", 37, 36),
            new PB840AlarmSetting("PB_EXHALED_MAND_TIDAL_VOLUME", 39, 38),
            new PB840AlarmSetting("PB_EXHALED_SPONT_TIDAL_VOLUME", 41, 40),
            new PB840AlarmSetting("PB_RESP_RATE", -1, 42), // or "OFF" ... could
                                                           // be a problem
            new PB840AlarmSetting("PB_INSPIRED_TIDAL_VOLUME", -1, 43),
            new PB840Numeric("PB_SETTING_BASE_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 44),
            new PB840Numeric("PB_SETTING_FLOW_SENSITIVITY", rosetta.MDC_DIM_L_PER_MIN.VALUE, 45),
            new PB840Numeric("PB_SETTING_PCV_INSP_PRESSURE", rosetta.MDC_DIM_SEC.VALUE, 46),
            new PB840Numeric("PB_SETTING_PCV_INSP_TIME", rosetta.MDC_DIM_SEC.VALUE, 47),
            new PB840Numeric("PB_SETTING_IE_INSP_COMPONENT", rosetta.MDC_DIM_DIMLESS.VALUE, 48),
            new PB840Numeric("PB_SETTING_IE_EXP_COMPONENT", rosetta.MDC_DIM_DIMLESS.VALUE, 49),
            new PB840Numeric("PB_SETTING_CONSTANT_DURING_RATE_CHANGE", rosetta.MDC_DIM_DIMLESS.VALUE, 50),
            new PB840Numeric("PB_SETTING_TUBE_ID", rosetta.MDC_DIM_MILLI_M.VALUE, 51),
            new PB840TechnicalAlert("PB_SETTING_TUBE_TYPE", 52),
            new PB840TechnicalAlert("PB_SETTING_HUMIDIFICATION_TYPE", 53),
            new PB840Numeric("PB_SETTING_HUMIDIFIER_VOLUME", rosetta.MDC_DIM_L.VALUE, 54),
            new PB840TechnicalAlert("PB_SETTING_O2_SENSOR", 55),
            new PB840Numeric("PB_SETTING_DISCONNECT_SENSITIVITY", rosetta.MDC_DIM_PERCENT.VALUE, 56), // or
                                                                                                      // "OFF"
            new PB840Numeric("PB_SETTING_RISE_TIME_PCT", rosetta.MDC_DIM_PERCENT.VALUE, 57),
            new PB840Numeric("PB_SETTING_PAVPCT_SUPPORT", rosetta.MDC_DIM_PERCENT.VALUE, 58),
            new PB840Numeric("PB_SETTING_EXP_SENSITIVITY", rosetta.MDC_DIM_DIMLESS.VALUE, 59),
            new PB840Numeric("PB_SETTING_IBW", rosetta.MDC_DIM_KILO_G.VALUE, 60),
            new PB840Numeric("PB_SETTING_TARGET_SUPP_VOLUME", rosetta.MDC_DIM_L.VALUE, 61),
            new PB840Numeric("PB_SETTING_HIGH_PEEP", rosetta.MDC_DIM_CM_H2O.VALUE, 62),
            new PB840Numeric("PB_SETTING_LOW_PEEP", rosetta.MDC_DIM_CM_H2O.VALUE, 63),
            new PB840Numeric("PB_SETTING_HIGH_PEEP_TIME", rosetta.MDC_DIM_SEC.VALUE, 64),
            new PB840Numeric("PB_SETTING_HIGH_SP_INS_TIME_LIM", rosetta.MDC_DIM_SEC.VALUE, 65),
            new PB840TechnicalAlert("PB_SETTING_CIRCUIT_TYPE", 66),
            new PB840Numeric("PB_SETTING_LOW_PEEP_TIME", rosetta.MDC_DIM_SEC.VALUE, 67),
            new PB840Numeric("PB_SETTING_EXPIRATORY_TIME", rosetta.MDC_DIM_SEC.VALUE, 68),
            new PB840Numeric("PB_END_INSPIRATORY_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 69),
            new PB840Numeric("PB_RESPIRATORY_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 70),
            new PB840Numeric("PB_EXHALED_TIDAL_VOL", rosetta.MDC_DIM_L.VALUE, 71),
            new PB840Numeric("PB_PATIENT_EXHALED_MINUTE_VOL", rosetta.MDC_DIM_L_PER_MIN.VALUE, 72),
            new PB840Numeric("PB_PEAK_AIRWAY_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 73),
            new PB840Numeric("PB_MEAN_AIRWAY_PRESS", rosetta.MDC_DIM_CM_H2O.VALUE, 74),
            new PB840Numeric("PB_EXPIRATORY_COMPONENT_IE_RATION", rosetta.MDC_DIM_DIMLESS.VALUE, 75),
            new PB840TechnicalAlert("PB_IE_RATIO", 76),
            new PB840Numeric("PB_DELIVERED_O2_PCT", rosetta.MDC_DIM_PERCENT.VALUE, 77),
            new PB840Numeric("PB_INSPIRED_TIDAL_VOLUME", rosetta.MDC_DIM_L.VALUE, 78),
            new PB840Numeric("PB_INTRINSIC_PEEP", rosetta.MDC_DIM_CM_H2O.VALUE, 79),
            new PB840Numeric("PB_ESTIMATED_TOTAL_RESISTANCE", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 80),
            new PB840Numeric("PB_ESTIMATED_PATIENT_RESISTANCE", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 81),
            new PB840Numeric("PB_ESTIMATED_PATIENT_ELASTANCE", rosetta.MDC_DIM_CM_H2O_PER_L.VALUE, 82),
            new PB840Numeric("PB_ESTIMATED_PATIENT_COMPLIANCE", rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE, 83),
            new PB840Numeric("PB_NORM_RAPID_SHALLOW_BREATHING_INDEX", rosetta.MDC_DIM_DIMLESS.VALUE, 84),
            new PB840Numeric("PB_RAPID_SHALLOW_BREATHING_INDEX", rosetta.MDC_DIM_DIMLESS.VALUE, 85),
            new PB840Numeric("PB_SPONTANEOUS_PCT_INSPIRATORY_TIME", rosetta.MDC_DIM_DIMLESS.VALUE, 86),
            new PB840Numeric("PB_MONITORED_PEEP_CMH2O", rosetta.MDC_DIM_CM_H2O.VALUE, 87),
            new PB840Numeric("PB_SPONTANEOUS_INSPIRATORY_TIME", rosetta.MDC_DIM_SEC.VALUE, 88),
            new PB840Numeric("PB_EXHALED_SPONTANEOUS_MINUTE_VOL", rosetta.MDC_DIM_L_PER_MIN.VALUE, 89),
            new PB840Numeric("PB_INTRINSIC_PEEP_EXPIRATORY_PAUSE", rosetta.MDC_DIM_CM_H2O.VALUE, 90),
            new PB840Numeric("PB_TOTAL_PEEP_EXPIRATORY_PAUSE", rosetta.MDC_DIM_CM_H2O.VALUE, 91),
            new PB840Numeric("PB_STATIC_COMPLIANCE_INSPIRATORY_PAUSE", rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE, 92),
            new PB840Numeric("PB_STATIC_RESISTANCE_INSPIRATORY_PAUSE", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 93),
            new PB840Numeric("PB_PLATEAU_PRESSURE_INSPIRATORY_PAUSE", rosetta.MDC_DIM_CM_H2O.VALUE, 94),
            new PB840TechnicalAlert("PB_HIGH_SPONTANEOUS_INSPIRATORY_ALERT", 95),
            new PB840Numeric("PB_DYNAMIC_COMPLIANCE_ML_CMH2O_", rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE, 96),
            new PB840Numeric("PB_DYNAMIC_RESISTANCE_CMH2OL", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 97),
            new PB840Numeric("PB_PEAK_SPONTANEOUS_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 98),
            new PB840Numeric("PB_PEAK_EXPIRATORY_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 99),
            new PB840Numeric("PB_END_EXPIRATORY_FOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 100),
            // 101 is reserved
            new PB840Numeric("PB_NEGATIVE_INSPIRATORY_FORCE", rosetta.MDC_DIM_CM_H2O.VALUE, 102),
            new PB840Numeric("PB_P01_PRESSURE_CHANGE", rosetta.MDC_DIM_CM_H2O.VALUE, 103),
            new PB840Numeric("PB_VITAL_CAPACITY", rosetta.MDC_DIM_L.VALUE, 104),
            new PB840PatientAlert("PB_ALARM_SILENCE", 105),
            new PB840PatientAlert("PB_APNEA_VENTILATION", 106),
            new PB840PatientAlert("PB_HIGH_EXHALED_MINUTE_VOLUME", 107),
            new PB840PatientAlert("PB_HIGH_EXHALED_TIDAL_VOLUME", 108),
            new PB840PatientAlert("PB_HIGH_O2_PCT", 109),

            new PB840PatientAlert("PB_HIGH_INSPIRATORY_PRESSURE", 110), // FIELD
                                                                        // 110
            new PB840PatientAlert("PB_HIGH_VENTILATOR_PRESSURE", 111),
            new PB840PatientAlert("PB_HIGH_RESPIRATORY_RATE", 112),
            new PB840TechnicalAlert("PB_AC_POWER_LOSS", 113),
            new PB840TechnicalAlert("PB_INOPERATIVE_BATTERY", 114),
            new PB840TechnicalAlert("PB_LOW_BATTERY", 115),
            new PB840TechnicalAlert("PB_LOSS_OF_POWER", 116),
            new PB840PatientAlert("PB_LOW_EXHALED_MANDATORY_TIDAL_VOLUME", 117),
            new PB840PatientAlert("PB_LOW_EXHALED_MINUTE_VOLUME", 118),
            new PB840PatientAlert("PB_LOW_EXHALED_SPONTANEOUS_TIDAL_VOLUME", 119),

            new PB840PatientAlert("PB_LOW_O2_PCT", 120), // FIELD 120
            new PB840PatientAlert("PB_LOW_AIR_SUPPLY_PRESSURE", 121),
            new PB840PatientAlert("PB_LOW_O2_SUPPLY_PRESSURE", 122),
            new PB840TechnicalAlert("PB_COMPRESSOR_INOPERATIVE_ALARM", 123),
            new PB840TechnicalAlert("PB_DISCONNECT", 124),
            new PB840PatientAlert("PB_SEVERE_OCCLUSION", 125),
            new PB840PatientAlert("PB_INSPIRATION_TOO_LONG", 126),
            new PB840PatientAlert("PB_PROCEDURE_ERROR", 127),
            new PB840PatientAlert("PB_COMPLIANCE_LIMITED_TIDAL_VOLUME", 128),
            new PB840PatientAlert("PB_HIGH_INSPIRED_SPONTANEOUS_TIDAL_VOLUME", 129),

            new PB840PatientAlert("PB_HIGH_INSPIRED_MANDATORY_TIDAL_VOLUME", 130), // FIELD
                                                                                   // 130
            new PB840PatientAlert("PB_HIGH_COMPENSATION_LIMIT", 131),
            new PB840PatientAlert("PB_PAV_STARTUP_TOO LONG", 132),
            new PB840PatientAlert("PB_RC_NOT_ASSESSED", 133),
            new PB840PatientAlert("PB_VOLUME_NOT_DELIVERED_VC", 134),
            new PB840PatientAlert("PB_VOLUME_NOT_DELIVERED_VS", 135),
            new PB840PatientAlert("PB_LOW_INSPIRATORY_PRESSURE", 136),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A5", 137),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A10", 138),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A15", 139),

            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A20", 140), // FIELD
                                                                     // 140
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A25", 141), new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A30", 142),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A35", 143),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A40", 144),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A45", 145),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A50", 146),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A55", 147),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A60", 148),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A65", 149),

            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A70", 150), // FIELD
                                                                     // 150
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A75", 151), new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A80", 152),
            new PB840TechnicalAlert("PB_TECH_MALFUNCTION_A85", 153), new PB840Numeric("PB_SP_TIDAL_VOL", rosetta.MDC_DIM_L.VALUE, 154),
            new PB840Numeric("PB_TOTAL_WORK_BREATHING", rosetta.MDC_DIM_JOULES_PER_L.VALUE, 155),
            new PB840TechnicalAlert("PB_LEAK_COMPENSATOIN_STATE", 156), new PB840TechnicalAlert("PB_PCT_LEAK", 157),
            new PB840TechnicalAlert("PB_LEAK_AT_PEEP", 158), new PB840TechnicalAlert("PB_V_LEAK", 159),
    // 160-171 reserved
    };

    /**
     * field names for the miscA response
     */
    private final PB840Field[] miscAFields = new PB840Field[] {
            // TODO these should be externalized in a resource file
            new PB840TechnicalAlert("PB_TIME", 5),
            new PB840VentilatorId(6),
            // 7 not used
            new PB840Numeric("PB_SETTING_RESP_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 10),
            new PB840Numeric("PB_SETTING_TIDAL_VOLUME", rosetta.MDC_DIM_L.VALUE, 11),
            new PB840Numeric("PB_SETTING_PEAK_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 12),
            new PB840Numeric("PB_SETTING_O2PCT", rosetta.MDC_DIM_PERCENT.VALUE, 13),
            new PB840Numeric("PB_SETTING_PRESS_SENSITIVITY", rosetta.MDC_DIM_CM_H2O.VALUE, 14),
            new PB840Numeric("PB_SETTING_PEEP_OR_PEEP_LOW", rosetta.MDC_DIM_CM_H2O.VALUE, 15),
            new PB840Numeric("PB_SETTING_PLATEAU", rosetta.MDC_DIM_SEC.VALUE, 16),
            new PB840Numeric("PB_SETTING_APNEA_INTERVAL", rosetta.MDC_DIM_SEC.VALUE, 21),
            new PB840Numeric("PB_SETTING_APNEA_TIDAL_VOLUME", rosetta.MDC_DIM_L.VALUE, 22),
            new PB840Numeric("PB_SETTING_APNEA_RESPIRATORY_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 23),
            new PB840Numeric("PB_SETTING_APNEA_PEAK_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 24),
            new PB840Numeric("PB_SETTING_APNEA_O2PCT", rosetta.MDC_DIM_PERCENT.VALUE, 25),
            new PB840Numeric("PB_SETTING_SUPPORT_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 26),
            new PB840TechnicalAlert("PB_SETTING_FLOW_PATTERN", 27),
            new PB840TechnicalAlert("PB_SETTING_100PCT_O2_STATE", 30),
            new PB840Numeric("PB_RESPIRATORY_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 34),
            new PB840Numeric("PB_EXHALED_TIDAL_VOL", rosetta.MDC_DIM_L.VALUE, 35),
            new PB840Numeric("PB_EXHALED_MINUTE_VOLUME", rosetta.MDC_DIM_L.VALUE, 36), // TODO
                                                                                       // shouldn't
                                                                                       // MV
                                                                                       // be
                                                                                       // L/min?
                                                                                       // spec
                                                                                       // says
                                                                                       // liters...
            new PB840Numeric("PB_EXHALED_SPONTANEOUS_MINUTE_VOL", rosetta.MDC_DIM_L_PER_MIN.VALUE, 37), // TODO
                                                                                                        // also
                                                                                                        // shouldn't
                                                                                                        // this
                                                                                                        // be
                                                                                                        // a
                                                                                                        // rate?
            new PB840Numeric("PB_MAX_CIRCUIT_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 38),
            new PB840Numeric("PB_MEAN_AIRWAY_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 39),
            new PB840Numeric("PB_END_INSPIRATORY_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 40),
            new PB840Numeric("PB_EXP_COMP_IE_RATIO", rosetta.MDC_DIM_DIMLESS.VALUE, 41),
            new PB840Numeric("PB_HIGH_CIRCUIT_PRESSURE_LIMIT", rosetta.MDC_DIM_CM_H2O.VALUE, 42),
            new PB840AlarmSetting("PB_EXHALED_TIDAL_VOLUME", 45, -1), new PB840AlarmSetting("PB_EXHALED_MINUTE_VOLUME", 46, -1),
            new PB840TechnicalAlert("PB_CIRCUIT_PRESSURE", 48), new PB840PatientAlert("PB_EXHALED_TIDAL_VOLUME", 51),
            new PB840PatientAlert("PB_EXHALED_MINUTE_VOLUME", 52), new PB840PatientAlert("PB_RESPIRATORY_RATE", 53),
            new PB840TechnicalAlert("PB_NO_O2_SUPPLY", 54), new PB840TechnicalAlert("PB_NO_AIR_SUPPLY", 55), new PB840TechnicalAlert("PB_APNEA", 57),
            new PB840TechnicalAlert("PB_TIME2", 60), new PB840TechnicalAlert("PB_DATE", 62),
            new PB840Numeric("PB_STATIC_COMPLIANCE_FROM_INSP_PAUSE", rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE, 63),
            new PB840Numeric("PB_STATIC_RESISTANCE_FROM_INSP_PAUSE", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 64),
            new PB840Numeric("PB_DYNAMIC_COMPLIANCE", rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE, 65),
            new PB840Numeric("PB_DYNAMIC_RESISTANCE", rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE, 66),
            new PB840Numeric("PB_NEGATIVE_INSP_FORCE", rosetta.MDC_DIM_CM_H2O.VALUE, 67),
            new PB840Numeric("PB_VITAL_CAPACITY", rosetta.MDC_DIM_L.VALUE, 68),
            new PB840Numeric("PB_PEAK_SPONTANEOUS_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 69),
            new PB840Numeric("PB_VENTILATOR_SET_BASE_FLOW", rosetta.MDC_DIM_L_PER_MIN.VALUE, 70),
            new PB840Numeric("PB_SETTING_FLOW_SENSITIVITY", rosetta.MDC_DIM_L_PER_MIN.VALUE, 71),
            new PB840Numeric("PB_END_INSPIRATORY_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 84),
            new PB840Numeric("PB_INSP_PRESSURE_OR_PEEP_HIGH_SETTING", rosetta.MDC_DIM_CM_H2O.VALUE, 85),
            new PB840Numeric("PB_INSP_TIME_OR_PEEP_HIGH_TIME_SETTING", rosetta.MDC_DIM_SEC.VALUE, 86),
            new PB840Numeric("PB_SETTING_APNEA_INTERVAL", rosetta.MDC_DIM_SEC.VALUE, 87),
            new PB840Numeric("PB_SETTING_APNEA_INSP_PRESSURE", rosetta.MDC_DIM_CM_H2O.VALUE, 88),
            new PB840Numeric("PB_SETTING_APNEA_RESP_RATE", rosetta.MDC_DIM_DIMLESS.VALUE, 89),
            new PB840Numeric("PB_SETTING_APNEA_INSP_TIME", rosetta.MDC_DIM_SEC.VALUE, 90),
            new PB840Numeric("PB_SETTING_APNEA_O2_PCT", rosetta.MDC_DIM_PERCENT.VALUE, 91),
            new PB840Numeric("PB_APNEA_HIGH_CIRCUIT_PRESSURE_LIMIT", rosetta.MDC_DIM_CM_H2O.VALUE, 92),
            new PB840TechnicalAlert("PB_ALARM_SILENCE_STATE", 93), new PB840TechnicalAlert("PB_APNEA_ALARM_STATUS", 94),
            new PB840TechnicalAlert("PB_SEVERE_OCCLUSION_DISCONNECT", 95), new PB840TechnicalAlert("PB_SETTING_INSP_COMPONENT_OF_IE_RATIO", 96),
            new PB840TechnicalAlert("PB_SETTING_EXP_COMPONENT_OF_IE_RATIO", 97),
            new PB840TechnicalAlert("PB_SETTING_INSP_COMPONENT_OF_APNEA_IE_RATIO", 98),
            new PB840TechnicalAlert("PB_SETTING_EXP_COMPONENT_OF_APNEA_IE_RATION", 99),
            new PB840TechnicalAlert("PB_CONSTANT_DURING_RATE_SETTING_CHANGE", 100), new PB840TechnicalAlert("PB_MONITORED_VALUE_OF_IE_RATIO", 101), };

    protected final Map<String, PB840Field[]> fields = new HashMap<String, PB840Field[]>();
    
    protected static Float parseFloat(String s) throws NumberFormatException {
        return parseFloat(s, null);
    }

    protected static Float parseFloat(String s, Float ifNull) throws NumberFormatException {
        if (s == null || s.isEmpty() || "OFF".equals(s)) {
            return ifNull;
        } else {
            return Float.parseFloat(s);
        }
    }

    private class MyPB840Parameters extends PB840Parameters {
        public MyPB840Parameters(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receive(String responseType, List<String> fieldValues) {
            final PB840Field[] fields = DemoPB840.this.fields.get(responseType);
            if(fields != null) {
                reportConnected("Received "+responseType);

                markOldPatientAlertInstances();
                markOldTechnicalAlertInstances();
    
                for (PB840Field field : fields) {
                    field.handle(fieldValues);
                }
    
                clearOldPatientAlertInstances();
                clearOldTechnicalAlertInstances();
            } else {
                log.warn("Unknown response type " + responseType);
            }
        }
    }

    private class RequestSlowData implements Runnable {
        public void run() {
            log.trace("RequestSlowData called");
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    PB840Parameters params = (PB840Parameters) getDelegate(0);
                    params.sendF();
                    log.trace("Issued SENDF");
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            } else {
                log.trace("Not issuing SENDF where state=" + getState());
            }

        }
    }

    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        switch (idx) {
        case 0:
            ((PB840Parameters) getDelegate(idx)).sendReset();
            log.trace("Issued a RSET for doInitCommands");
            ((PB840Parameters) getDelegate(idx)).sendF();
            log.trace("Issued a SNDF for doInitCommands");
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
        switch (idx) {
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
        switch (idx) {
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
        switch (idx) {
        case 0:
            return 5000L;
        case 1:
            // There is no protocol negotiation for waveform data
            // so there is no utility in interrupting the main parameter
            // collection when waveform data is absent
            return Long.MAX_VALUE;
        default:
            return super.getMaximumQuietTime(idx);
        }
    }

    @Override
    protected String iconResourceName() {
        return "pb840.png";
    }

}
