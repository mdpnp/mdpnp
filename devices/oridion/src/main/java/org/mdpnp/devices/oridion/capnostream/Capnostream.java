package org.mdpnp.devices.oridion.capnostream;

import static org.mdpnp.devices.io.util.Bits.getUnsignedInt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.io.MergeBytesInputStream;
import org.mdpnp.devices.io.SplitBytesOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capnostream {
    private final Set<CapnostreamListener> listeners = new CopyOnWriteArraySet<CapnostreamListener>();
    private static final Logger log = LoggerFactory
            .getLogger(Capnostream.class);

    private final InputStream inputStream;
    private final SplitBytesOutputStream outputStream;

    // Header + 2-byte length + 255 2-byte body + 2-byte checksum
    private static final int WORST_CASE_MSG_LENGTH = 1 + 2 + 2 * 255 + 2;

    public Capnostream(InputStream is, OutputStream os) {
        this.inputStream = new MergeBytesInputStream(new BufferedInputStream(is, WORST_CASE_MSG_LENGTH));
        this.outputStream = new SplitBytesOutputStream(new BufferedOutputStream(os, WORST_CASE_MSG_LENGTH));
    }

    private final byte[] inBuffer = new byte[WORST_CASE_MSG_LENGTH];
    private final byte[] outBuffer = new byte[WORST_CASE_MSG_LENGTH];

    protected static final Map<Integer, Command> cmdMapping = new HashMap<Integer, Command>();
    protected static final Map<Integer, Response> resMapping = new HashMap<Integer, Response>();
    protected static final Map<Integer, NumericItem> numMapping = new HashMap<Integer, NumericItem>();
    protected static final Map<Integer, DataItem> dataMapping = new HashMap<Integer, DataItem>();
    protected static final Map<Integer, FastStatus> fastMapping = new HashMap<Integer, FastStatus>();
    protected static final Map<Integer, CO2Units> co2Mapping = new HashMap<Integer, CO2Units>();
    protected static final Map<Integer, PatientType> patMapping = new HashMap<Integer, PatientType>();

    public static final class FastStatus {
        public static final int INVALID_CO2_VALUE = 0x01;
        public static final int INITIALIZATION = 0x02;
        public static final int OCCLUSION_IN_GAS_INPUT_LINE = 0x04;
        public static final int END_OF_BREATH_INDICATION = 0x08;
        public static final int SFM_IN_PROGRESS = 0x10;
        public static final int PURGING_IN_PROGRESS = 0x20;
        public static final int FILTER_LINE_NOT_CONNECTED = 0x40;
        public static final int CO2_MALFUNCTION = 0x80;

        public static StringBuilder fastStatus(int fastStatus, StringBuilder builder) {
            builder.delete(0, builder.length());
            if(0 != (INVALID_CO2_VALUE & fastStatus)) {
                builder.append("INVALID_CO2_VALUE, ");
            }
            if(0 != (INITIALIZATION & fastStatus)) {
                builder.append("INITIALIZATION, ");
            }
            if(0 != (OCCLUSION_IN_GAS_INPUT_LINE & fastStatus)) {
                builder.append("OCCLUSION_IN_GAS_INPUT_LINE, ");
            }
            if(0 != (END_OF_BREATH_INDICATION & fastStatus)) {
                builder.append("END_OF_BREATH_INDICATION, ");
            }
            if(0 != (SFM_IN_PROGRESS & fastStatus)) {
                builder.append("SFM_IN_PROGRESS, ");
            }
            if(0 != (PURGING_IN_PROGRESS & fastStatus)) {
                builder.append("PURGING_IN_PROGRESS, ");
            }
            if(0 != (FILTER_LINE_NOT_CONNECTED & fastStatus)) {
                builder.append("FILTER_LINE_NOT_CONNECTED, ");
            }
            if(0 != (CO2_MALFUNCTION & fastStatus)) {
                builder.append("CO2_MALFUNCTION, ");
            }

            if(builder.length() > 1) {
                builder.delete(builder.length() - 2, builder.length());
            }
            return builder;
        }
    }



    public static class SlowStatus {
        public static final int PATIENT_TYPE = 0x01;
        public static final int TEMP_ALARM_SILENCE = 0x02;
        public static final int ALL_ALARMS_SILENCED = 0x04;
        public static final int HIGH_PRIORITY_ACTIVE_AUDIBLE = 0x08;
        public static final int LOW_PRIORITY_ACTIVE_AUDIBLE = 0x10;
        public static final int ADVISORY_ACTIVE_AUDIBLE = 0x20;
        public static final int PULSE_BEEPS_SILENCED = 0x40;
        public static final int SPO2_MANUFACTURER = 0x80;
    }

    public static class CO2ActiveAlarms {
        public static final int NO_BREATH = 0x01;
        public static final int ETCO2_HIGH = 0x02;
        public static final int ETCO2_LOW = 0x04;
        public static final int RR_HIGH = 0x08;
        public static final int RR_LOW = 0x10;
        public static final int FICO2_HIGH = 0x20;
    }

    public static class SpO2ActiveAlarms {
        public static final int PULSE_NOT_FOUND = 0x01;
        public static final int SPO2_HIGH = 0x02;
        public static final int SPO2_LOW = 0x04;
        public static final int PULSE_RATE_HIGH = 0x08;
        public static final int PULSE_RATE_LOW = 0x10;
        public static final int SPO2_SENSOR_OFF = 0x20;
        public static final int SPO2_SENSOR_DISCONNECTED = 0x40;
    }

    public static class ExtendedCO2Status {
        public static final int CHECK_CALIBRATION = 0x01;
        public static final int CHECK_FLOW = 0x02;
        public static final int PUMP_OFF = 0x04;
        public static final int BATTERY_LOW = 0x80;
    }

    public enum Alarm {
        Enabled, Disabled
    }

    public enum Case {
        Start, End;
    }

    public enum CommInterruptAdvisory {
        Silent, LowPriorityAlarm
    }

    public enum SetupItem {
        TemporaryAlarmSilence(1), StartEndCase(2), PatientType(3), CommIntIndication(
                4), EtCO2Low(5, 0, 145), EtCO2High(6, 5, 150), FiCO2High(7, 2, 149), respiratoryRateLow(
                8, 0, 145), respiratoryRateHigh(9, 5, 150), SpO2Low(10, 20, 95), SpO2High(11, 25, 100), PulseRateLow(
                12, 25, 235), PulseRateHigh(13, 30, 240), IPILow(14), SpMetLow(17), SpMetHigh(18), SpHbHigh(
                19), SpHbLow(20), SpCOHigh(21), SpCOLow(22);

        SetupItem(int code) {
            this(code, null, null);
        }

        static Map<Integer, SetupItem> mapping = new HashMap<Integer, SetupItem>();

        static {
            for(SetupItem si : values()) {
                mapping.put(si.getCode(), si);
            }
        }

        SetupItem(int code, Integer lower, Integer upper) {
            this.code = code;
            this.lower = lower;
            this.upper = upper;
        }

        public static final SetupItem fromCode(int code) {
            return mapping.get(code);
        }

        private int code;
        private Integer lower, upper;

        public int getCode() {
            return code;
        }
        public Integer getLower() {
            return lower;
        }
        public Integer getUpper() {
            return upper;
        }
    }

    public enum PatientType {
        Adult, Neonate, Pediatric1_3, Pediatric3_6, Pediatric6_12;
    }

    public enum DataItem {
        IPIMessage(1), RainbowParams(5);

        DataItem(int code) {
            this.code = code;
            dataMapping.put(code, this);
        }

        private int code;

        public int getCode() {
            return code;
        }

        public static DataItem fromInt(int i) {
            return dataMapping.get(i);
        }
    }

    public enum CO2Units {
        mmHg(1), kPa(2), VolPct(3);

        CO2Units(int code) {
            this.code = code;
            co2Mapping.put(code, this);
        }

        private int code;

        public int getCode() {
            return code;
        }

        public static CO2Units fromInt(int i) {
            return co2Mapping.get(i);
        }
    }

    public enum NumericItem {
        EtCO2(1), FiCO2(2), RespirationRate(3), SpO2(4), Pulse(5);

        NumericItem(int code) {
            this.code = code;
            numMapping.put(code, this);
        }

        private int code;

        public int getCode() {
            return code;
        }

        public static NumericItem fromInt(int i) {
            return numMapping.get(i);
        }
    }

    public enum Command {
        EnableComm(1), DisableComm(2), InquireNumericItem(3), StartRTComm(4), StopRTComm(
                5), LinkIsActive(6), SetHostIdNonUnicode(7), SetHostIdUnicode(8), InquireProtocolRev(
                9), ConfigurePeriodicMessage(10), InquirePatientId(11), InquireEventsList(
                21), StartLongTrendDownload(54), StopLongTrendDownload(55), StartLongTrendConfigDownload(
                56), ConfigurableSetup(60), SetPatientIdNonUnicode(61), SetPatientIdUnicode(
                62);

        Command(int code) {
            this.code = code;
            cmdMapping.put(code, this);
        }

        private int code;

        public int getCode() {
            return code;
        }

        public static Command fromInt(int i) {
            return cmdMapping.get(i);
        }
    }

    public enum Response {
        CO2Wave(0), Numerics(1), PatientIdNonUnicode(2), NumericItem(3), DeviceIdSoftwareVersion(
                4), ProtocolRevision(9), ConfigurableMessage(10), PatientIdUnicode(
                12), EventsListNonUnicode(21), EventsListUnicode(22), LongTrendDownload(
                55), LongTrendConfigurable(56), NewPatientNonUnicode(57), NewPatientUnicode(
                58), ConfigurableSetup(60);

        Response(int code) {
            this.code = code;
            resMapping.put(code, this);
        }

        private int code;

        public int getCode() {
            return code;
        }

        public static Response fromInt(int i) {
            return resMapping.get(i);
        }
    }

   public boolean sendConfigurableSetup(SetupItem si, int value)
            throws IOException {
       if(si.getLower() != null && value < si.getLower()) {
           log.warn("Cannot set " + si.name() + " to " + value + " because it's less than " + si.getLower());
           value = si.getLower();
       }
       if(si.getUpper() != null && value > si.getUpper()) {
           log.warn("Cannot set " + si.name() + " to " + value + " because it's greater than " + si.getLower());
           value = si.getUpper();
       }

        int length;

        switch (si) {
        case SpMetHigh:
        case SpMetLow:
            length = 3;
            outBuffer[0] = (byte) (0xFF & si.getCode());
            outBuffer[1] = (byte) (0xFF & (value << 8));
            outBuffer[2] = (byte) (0xFF & value);

            break;

        default:
            length = 2;
            outBuffer[0] = (byte) (0xFF & si.getCode());
            outBuffer[1] = (byte) (0xFF & value);
            break;
        }

        return sendCommand(Command.ConfigurableSetup, outBuffer, length);
    }

    public boolean sendStartLongTrendConfigurableDownload(DataItem di)
            throws IOException {
        outBuffer[0] = (byte) di.getCode();
        return sendCommand(Command.StartLongTrendConfigDownload, outBuffer, 1);
    }

    public boolean sendConfigurePeriodicMessage(DataItem di, boolean messageData)
            throws IOException {
        outBuffer[0] = (byte) di.getCode();
        outBuffer[1] = (byte) (messageData ? 1 : 0);
        return sendCommand(Command.ConfigurePeriodicMessage, outBuffer, 2);
    }

    public boolean sendInquireNumericItem(NumericItem ni) throws IOException {
        outBuffer[0] = (byte) ni.getCode();
        return sendCommand(Command.InquireNumericItem, outBuffer, 1);
    }

    public boolean sendString(Command command, String s, boolean unicode,
            int max_length) throws IOException {
        StringBuilder sb = new StringBuilder(s);
        if(sb.length() > max_length) {
            sb.delete(max_length, sb.length());
        }

        for (int i = sb.length(); i < max_length; i++) {
            sb.append(" ");
        }
        byte[] s_bytes = sb.toString().getBytes(unicode ? "UTF-16" : "ASCII");

//		byte[] bytes = s.getBytes(unicode ? "UTF-16" : "ASCII");
//		if (bytes.length > ((unicode ? 2 : 1) * max_length)) {
//			throw new IllegalArgumentException(command.toString()
//					+ " string too long:" + s);
//		}
//		System.arraycopy(bytes, 0, s_bytes, 0, bytes.length);
        return sendCommand(command, s_bytes, s_bytes.length);
    }

    public boolean sendPatientId(String s) throws IOException {
        return sendPatientId(s, false);
    }

    public boolean sendPatientId(String s, boolean unicode) throws IOException {
        return sendString(unicode ? Command.SetPatientIdUnicode
                : Command.SetPatientIdNonUnicode, s, unicode, 20);
    }

    public boolean sendHostMonitoringId(String s) throws IOException {
        return sendHostMonitoringId(s, false);
    }

    public boolean sendHostMonitoringId(String s, boolean unicode)
            throws IOException {
        return sendString(unicode ? Command.SetHostIdUnicode
                : Command.SetHostIdNonUnicode, s, unicode, 17);
    }

    public boolean sendCommand(Object command) throws IOException {
        return sendCommand(command, null, 0);
    }

//    private long nextCommand = 0L;

    public boolean sendCommand(Object command, byte[] payload, int length)
            throws IOException {
        outputStream.writeProtected(0x85);

        int code;
        if (command instanceof Command) {
            code = ((Command) command).getCode();
        } else if (command instanceof Number) {
            code = ((Number) command).intValue();
        } else {
            throw new IllegalArgumentException("Unknown command type:"
                    + command);
        }
        outputStream.write(length+1);
        outputStream.write(code);

        if (length > 0) {
            outputStream.write(payload, 0, length);
        }
        int checksum = (length + 1);
        checksum ^= code;
        for (int i = 0; i < length; i++) {
            switch(0xFF&payload[i]) {
            case 0x85:
                checksum ^= 0x80;
                checksum ^= 0x05;
                break;
            case 0x80:
                checksum ^= 0x80;
                checksum ^= 0x00;
                break;
            default:
                checksum ^= payload[i];
            }
        }
        outputStream.write(checksum);
        outputStream.flush();

        return true;
    }

    public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
        log.trace("CO2Wave: " + messageNumber + " " + co2 + " " + status);
        for (CapnostreamListener listener : listeners) {
            listener.co2Wave(messageNumber, co2, status);
        }
        return true;
    }

    private static double co2(byte[] buf, int off) {
        return (0xFF&buf[off]) + (0xFF&buf[off + 1]) / 256.0;
    }

    public boolean receiveNumerics(long date, int etCO2, int FiCO2,
            int respiratoryRate, int spo2, int pulserate, int slowStatus,
            int CO2ActiveAlarms, int SpO2ActiveAlarms, int noBreathPeriodSeconds, int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh, int rrAlarmLow, int fico2AlarmHigh, int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units, int extendedCO2Status) {
        for (CapnostreamListener listener : listeners) {
            listener.numerics(date, etCO2, FiCO2, respiratoryRate, spo2, pulserate,
                    slowStatus, CO2ActiveAlarms, SpO2ActiveAlarms, noBreathPeriodSeconds,
                    etCo2AlarmHigh, etCo2AlarmLow, rrAlarmHigh, rrAlarmLow,
                    fico2AlarmHigh, spo2AlarmHigh, spo2AlarmLow, pulseAlarmHigh,
                    pulseAlarmLow, units, extendedCO2Status);
        }
        return true;
    }

    private int priorRespiratoryRate = -1;

    public boolean receiveNumerics(byte[] payload, int length) {
        if(length < 27) {
            log.warn("Insufficient length for Numerics payload; ignoring");
            return true;
        }
        long dt = 1000L * getUnsignedInt(payload, 0);
        int etco2 = 0xFF & payload[4];
        int fico2 = 0xFF & payload[5];
        int rr = 0xFF & payload[6];
        int spo2 = 0xFF & payload[7];

        // TODO Report this behavior to Oridion
        if(priorRespiratoryRate != 255 && priorRespiratoryRate == spo2) {
            log.warn("Prior Respiratory Rate == SpO2, " + rr + "==" + spo2 + " ignoring this potentially spurious SpO2");
            spo2 = 0xFF;
            byte[] subpayload = new byte[length];
            System.arraycopy(payload, 0, subpayload, 0, length);
            log.warn("This numerics payload seems to be offset:"+Arrays.toString(subpayload));
            return true;
        }

        int pulse = 0xFF & payload[8];

        int slowStatus = 0xFF & payload[9];
        int co2ActiveAlarms = 0xFF & payload[13];
        int spo2ActiveAlarms = 0xFF & payload[14];
        int noBreathPeriodSeconds = 0xFF & payload[15];
        int etCo2AlarmHigh = 0xFF & payload[16];
        int etCo2AlarmLow = 0xFF & payload[17];
        int rrAlarmHigh = 0xFF & payload[18];
        int rrAlarmLow = 0xFF & payload[19];
        int fico2AlarmHigh = 0xFF & payload[20];
        int spo2AlarmHigh = 0xFF & payload[21];
        int spo2AlarmLow = 0xFF & payload[22];
        int pulseAlarmHigh = 0xFF & payload[23];
        int pulseAlarmLow = 0xFF & payload[24];
        CO2Units units = CO2Units.fromInt(0xFF & payload[25]);
        int extendedCO2Status = 0xFF & payload[26];

        priorRespiratoryRate = rr;

        // TODO there is more stuff here
        return receiveNumerics(dt, etco2, fico2, rr, spo2, pulse,
                slowStatus, co2ActiveAlarms, spo2ActiveAlarms, noBreathPeriodSeconds,
                etCo2AlarmHigh, etCo2AlarmLow, rrAlarmHigh, rrAlarmLow,
                fico2AlarmHigh, spo2AlarmHigh, spo2AlarmLow, pulseAlarmHigh,
                pulseAlarmLow, units, extendedCO2Status);
    }

    public boolean receiveProtocolRevision(byte[] payload, int length) {
        return receiveProtocolRevision((char) payload[0], (int)payload[1]);
    }

    public boolean receiveProtocolRevision(char revisionAsChar, int revisionAsInt) {
        log.debug("revisionAsChar="+revisionAsChar+", revisionAsInt="+revisionAsInt);
        return true;
    }

    public boolean receiveCO2Wave(byte[] payload, int length) {
        return receiveCO2Wave(payload[0], co2(payload, 1), payload[3]);
    }

    public boolean receiveMessage(Object response, byte[] payload, int length) {

        if (response instanceof Response) {
            switch ((Response) response) {
            case ProtocolRevision:
                return receiveProtocolRevision(payload, length);
            case CO2Wave:
                return receiveCO2Wave(payload, length);
            case DeviceIdSoftwareVersion:
                return receiveDeviceIdSoftwareVersion(payload, length);
            case Numerics:
                return receiveNumerics(payload, length);
            case ConfigurableSetup:
                SetupItem si = SetupItem.fromCode(0xFF & payload[0]);
                if(null == si) {
                    log.warn("Not processing unknown SetupItem for ConfigurableSetup:"+(0xFF&payload[0]));
                } else {
                    switch(si){
                    case SpMetHigh:
                    case SpMetLow:
                        // TODO what is meant by "high byte" here ... clearly they don't mean a 16-bit int
                        return true;
                    default:
                        return receiveConfigurableSetup(SetupItem.fromCode(0xFF & payload[0]), 0xFF & payload[1]);
                    }

                }
            default:
                log.debug("Unknown message " + response + " " + length + " "
                        + Arrays.toString(payload));
            }
        }

        return true;
    }

    public boolean receiveConfigurableSetup(SetupItem fromCode, int i) {
        log.debug("Configurable Setup:"+fromCode+"="+i);
        return true;
    }

    protected enum PulseOximetry {
        Masimo,
        Nellcor
    }

    public static void main(String[] args) {
        Capnostream c = new Capnostream(null, null);
        c.receiveDeviceIdSoftwareVersion("V45.67 02/24/2008 B355987654  ");
    }

    public boolean receiveDeviceIdSoftwareVersion(String softwareVersion,
            Date softwareReleaseDate, PulseOximetry pulseOximetry, String revision, String number) {
        log.debug("softwareVersion="+softwareVersion+
                ", softwareReleaseDate="+softwareReleaseDate+", pulseOximetry="+pulseOximetry+", revision="+
                revision+", number="+number);
        return true;
    }
    private static final Pattern deviceIdSoftwareVersionPattern = Pattern.compile("^V(.{5}) (.{10}) (.{2})(.{2})(.{6})  $");
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public boolean receiveDeviceIdSoftwareVersion(String s) {

        log.debug("DeviceIdSoftwareVersion:" + s);
        Matcher m = deviceIdSoftwareVersionPattern.matcher(s);
        if(m.matches()) {
            if(m.groupCount()>=5) {
                String softwareVersion = m.group(1);
                Date softwareReleaseDate;

                String date = m.group(2).trim();
                if(null != date && !"".equals(date)) {
                    try {
                        softwareReleaseDate = dateFormat.parse(date);
                    } catch (ParseException e) {
                        log.error("Error parsing date:"+date, e);
                        softwareReleaseDate = null;
                    }
                } else {
                    softwareReleaseDate = null;
                }
                String po = m.group(3);
                PulseOximetry pulseOximetry = null;
                if("B5".equals(po)) {
                    pulseOximetry = PulseOximetry.Masimo;
                } else if("B2".equals(po)) {
                    pulseOximetry = PulseOximetry.Nellcor;
                } else if("B3".equals(po)) {
                    pulseOximetry = PulseOximetry.Masimo;
                } else {
                    log.warn("Unknown serial number prefix:"+po);
                }
                String revision = m.group(4);
                String serial_number = po + revision + m.group(5);
                for (CapnostreamListener listener : listeners) {
                    listener.deviceIdSoftwareVersion(softwareVersion, softwareReleaseDate, pulseOximetry, revision, serial_number);
                }
                receiveDeviceIdSoftwareVersion(softwareVersion, softwareReleaseDate, pulseOximetry, revision, serial_number);

            } else {
                log.warn("Insufficient matching groups:"+s);
            }
        } else {
            log.warn("Device ID and Software revision doesn't match expected regex:"+s);
        }


        return true;
    }

    public boolean receiveDeviceIdSoftwareVersion(byte[] payload, int length) {
        String s = Charset.forName("ASCII")
                .decode(ByteBuffer.wrap(payload, 0, length)).toString();
        return receiveDeviceIdSoftwareVersion(s);
    }

    public boolean receiveMessage() throws IOException {
        int length = inputStream.read();
        int my_checksum = 0xFF & length;
        if (length < 0) {
            return false;
        }

        int code = inputStream.read();
        my_checksum ^= code;
        if (code < 0) {
            return false;
        }

        Object response = Response.fromInt(code);
        if (response == null) {
            response = (Integer) code;
        }
        length--;

        int read_length = 0;

        while (read_length < length) {
            int b = inputStream.read(inBuffer, read_length, length
                    - read_length);
            if (b < 0) {
                return false;
            } else {
                read_length += b;
            }
        }

        for(int i = 0; i < length; i++) {
            int x = 0xFF & inBuffer[i];

            switch(x) {
            case 0x80:
                my_checksum ^= 0x80;
                my_checksum ^= 0x00;
                break;
            case 0x85:
                my_checksum ^= 0x80;
                my_checksum ^= 0x05;
                break;
            default:
                my_checksum ^= (0xFF & inBuffer[i]);
                break;
            }

        }

        int checksum = inputStream.read();

        if (checksum < 0) {
            return false;
        }

        if(checksum != my_checksum) {
            log.warn("Failed checksum check expected:"+my_checksum+" but received "+checksum+" data are ignored for msg type:"+response+" after read_length=" + read_length + " length="+length+" bytes");
            return true;
        }

        return receiveMessage(response, inBuffer, read_length);
    }

    public boolean receive() throws IOException {
        int b = 0;

        while (true) {
            b = inputStream.read();
            while (b != MergeBytesInputStream.BEGIN_FRAME) {
                if (b == MergeBytesInputStream.END_OF_FILE) {
                    log.trace("received EOF instead of cmd header");
                    return false;
                }
                log.trace("Received Between Messages:"+Integer.toHexString(b));
                b = inputStream.read();
            }
            if (!receiveMessage()) {
                return false;
            }
        }

    }

    public void addListener(CapnostreamListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CapnostreamListener listener) {
        listeners.remove(listener);
    }
}
