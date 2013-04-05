package org.mdpnp.devices.oridion.capnostream;

import static org.mdpnp.devices.io.util.Bits.getUnsignedInt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capnostream {
	private final Set<CapnostreamListener> listeners = new CopyOnWriteArraySet<CapnostreamListener>();
	private static final Logger log = LoggerFactory
			.getLogger(Capnostream.class);

	private final InputStream inputStream;
	private final OutputStream outputStream;

	public Capnostream(InputStream is, OutputStream os) {
		this.inputStream = new MergeBytesInputStream(
				new BufferedInputStream(is));
		this.outputStream = new BufferedOutputStream(os);
	}

	private byte[] inBuffer = new byte[1024];
	private byte[] outBuffer = new byte[1024];

	protected static final Map<Integer, Command> cmdMapping = new HashMap<Integer, Command>();
	protected static final Map<Integer, Response> resMapping = new HashMap<Integer, Response>();
	protected static final Map<Integer, NumericItem> numMapping = new HashMap<Integer, NumericItem>();
	protected static final Map<Integer, DataItem> dataMapping = new HashMap<Integer, DataItem>();
	protected static final Map<Integer, FastStatus> fastMapping = new HashMap<Integer, FastStatus>();
	protected static final Map<Integer, CO2Units> co2Mapping = new HashMap<Integer, CO2Units>();
	protected static final Map<Integer, PatientType> patMapping = new HashMap<Integer, PatientType>();

	public static class FastStatus {
		public static final int INVALID_CO2_VALUE = 0x01;
		public static final int INITIALIZATION = 0x02;
		public static final int OCCLUSION_IN_GAS_INPUT_LINE = 0x04;
		public static final int END_OF_BREATH_INDICATION = 0x08;
		public static final int SFM_IN_PROGRESS = 0x10;
		public static final int PURGING_IN_PROGRESS = 0x20;
		public static final int FILTER_LINE_NOT_CONNECTED = 0x40;
		public static final int CO2_MALFUNCTION = 0x80;
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
				4), EtCO2Low(5), EtCO2High(6), FiCO2High(7), respiratoryRateLow(
				8), respiratoryRateHigh(9), SpO2Low(10), SpO2High(11), PulseRateLow(
				12), PulseRateHigh(13), IPILow(14), SpMetLow(17), SpMetHigh(18), SpHbHigh(
				19), SpHbLow(20), SpCOHigh(21), SpCOLow(22);

		SetupItem(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return code;
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

	public void sendConfigurableSetup(SetupItem si, int value)
			throws IOException {
		int length;

		switch (si) {
		case SpMetHigh:
		case SpMetLow:
			length = 2;
			outBuffer[0] = (byte) (0xFF & (value << 8));
			outBuffer[1] = (byte) (0xFF & value);
			break;
		default:
			length = 1;
			outBuffer[0] = (byte) value;
			break;
		}

		sendCommand(Command.ConfigurableSetup, outBuffer, length);
	}

	public void sendStartLongTrendConfigurableDownload(DataItem di)
			throws IOException {
		outBuffer[0] = (byte) di.getCode();
		sendCommand(Command.StartLongTrendConfigDownload, outBuffer, 1);
	}

	public void sendConfigurePeriodicMessage(DataItem di, boolean messageData)
			throws IOException {
		outBuffer[0] = (byte) di.getCode();
		outBuffer[1] = (byte) (messageData ? 1 : 0);
		sendCommand(Command.ConfigurePeriodicMessage, outBuffer, 2);
	}

	public void sendInquireNumericItem(NumericItem ni) throws IOException {
		outBuffer[0] = (byte) ni.getCode();
		sendCommand(Command.InquireNumericItem, outBuffer, 1);
	}

	public void sendString(Command command, String s, boolean unicode,
			int max_length) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < max_length; i++) {
			sb.append(" ");
		}
		byte[] s_bytes = sb.toString().getBytes(unicode ? "UTF-16" : "ASCII");

		byte[] bytes = s.getBytes(unicode ? "UTF-16" : "ASCII");
		if (bytes.length > ((unicode ? 2 : 1) * max_length)) {
			throw new IllegalArgumentException(command.toString()
					+ " string too long:" + s);
		}
		System.arraycopy(bytes, 0, s_bytes, 0, bytes.length);
		sendCommand(command, s_bytes, s_bytes.length);
	}

	public void sendPatientId(String s) throws IOException {
		sendPatientId(s, false);
	}

	public void sendPatientId(String s, boolean unicode) throws IOException {
		sendString(unicode ? Command.SetPatientIdUnicode
				: Command.SetPatientIdNonUnicode, s, unicode, 20);
	}

	public void sendHostMonitoringId(String s) throws IOException {
		sendHostMonitoringId(s, false);
	}

	public void sendHostMonitoringId(String s, boolean unicode)
			throws IOException {
		sendString(unicode ? Command.SetHostIdUnicode
				: Command.SetHostIdNonUnicode, s, unicode, 17);
	}

	public void sendCommand(Object command) throws IOException {
		sendCommand(command, null, 0);
	}

	private void write(int b) throws IOException {
		outputStream.write(b);
		// log.trace("wrote:" + Integer.toHexString(b));
	}

	private void write(byte[] b, int off, int len) throws IOException {
		outputStream.write(b, off, len);
		// log.trace("wrote(" + off+","+len+"):"+Arrays.toString(b));
	}

	public void sendCommand(Object command, byte[] payload, int length)
			throws IOException {
		write(0x85);
		int code;
		if (command instanceof Command) {
			code = ((Command) command).getCode();
		} else if (command instanceof Number) {
			code = ((Number) command).intValue();
		} else {
			throw new IllegalArgumentException("Unknown command type:"
					+ command);
		}

		// code adds a byte
		write(length + 1);
		write(code);
		if (length > 0) {
			write(payload, 0, length);
		}
		int checksum = (length + 1);
		checksum ^= code;
		for (int i = 0; i < length; i++) {
			checksum ^= payload[i];
		}

		write(checksum);
		outputStream.flush();
	}

	public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
		log.trace("CO2Wave: " + messageNumber + " " + co2 + " " + status);
		for (CapnostreamListener listener : listeners) {
			listener.co2Wave(messageNumber, co2, status);
		}
		return true;
	}

	private static double co2(byte[] buf, int off) {
		return buf[off] + buf[off + 1] / 256.0;
	}

	public boolean receiveNumerics(long date, int etCO2, int FiCO2,
			int respiratoryRate, int spo2, int pulserate) {
		for (CapnostreamListener listener : listeners) {
			listener.numerics(date, etCO2, FiCO2, respiratoryRate, spo2,
					pulserate);
		}
		return true;
	}

	public boolean receiveNumerics(byte[] payload, int length) {
		long dt = 1000L * getUnsignedInt(payload, 0);
		int etco2 = 0xFF & payload[4];
		int fico2 = 0xFF & payload[5];
		int rr = 0xFF & payload[6];
		int spo2 = 0xFF & payload[7];
		int pulse = 0xFF & payload[8];
		// TODO there is more stuff here
		return receiveNumerics(dt, etco2, fico2, rr, spo2, pulse);
	}

	public boolean receiveCO2Wave(byte[] payload, int length) {
		return receiveCO2Wave(payload[0], co2(payload, 1), payload[3]);
	}

	public boolean receiveMessage(Object response, byte[] payload, int length) {

		if (response instanceof Response) {
			switch ((Response) response) {
			case CO2Wave:
				return receiveCO2Wave(payload, length);
			case DeviceIdSoftwareVersion:
				return receiveDeviceIdSoftwareVersion(payload, length);
			case Numerics:
				return receiveNumerics(payload, length);
			default:
				log.debug("Unknown message " + response + " " + length + " "
						+ Arrays.toString(payload));
			}
		}

		return true;
	}

	public boolean receiveDeviceIdSoftwareVersion(String s) {
		log.debug("DeviceIdSoftwareVersion:" + s);
		for (CapnostreamListener listener : listeners) {
			listener.deviceIdSoftwareVersion(s);
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
		if (length < 0) {
			return false;
		}

		int code = inputStream.read();
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

		int checksum = inputStream.read();
		// TODO Check the checksum
		if (checksum < 0) {
			return false;
		}

		return receiveMessage(response, inBuffer, read_length);
	}

	public boolean receive() throws IOException {
		int b = 0;

		while (true) {
			b = 0;
			while (b != MergeBytesInputStream.HEADER) {
				b = inputStream.read();
				if (b == MergeBytesInputStream.EOF) {
					log.trace("received EOF instead of cmd header");
					return false;
				}
				// log.trace("Received:"+Integer.toHexString(b));
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
