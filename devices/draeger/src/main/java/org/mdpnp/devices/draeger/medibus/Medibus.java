package org.mdpnp.devices.draeger.medibus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.ASCIIByte;
import org.mdpnp.devices.draeger.medibus.types.AlarmMessageCP1;
import org.mdpnp.devices.draeger.medibus.types.AlarmMessageCP2;
import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.DataType;
import org.mdpnp.devices.draeger.medibus.types.MeasuredDataCP1;
import org.mdpnp.devices.draeger.medibus.types.MeasuredDataCP2;
import org.mdpnp.devices.draeger.medibus.types.Setting;
import org.mdpnp.devices.draeger.medibus.types.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NOT FOR HUMAN USE
 *
 * Experimental implementation of Draeger Medibus protocol for blocking I/O.
 * This API gives the caller a simple way to emit protocol commands and receive
 * protocol responses.  Out of scope is the particular semantics of the protocol.
 *
 * It is the responsibility of the caller to create a connection to a Draeger
 * device using a serial port connection or otherwise.  Once that connection is
 * established the relevant InputStream and OutputStream should be passed to the
 * constructor.
 *
 * See the Interoperability Lab demo-devices for an example of how this class can be
 * bound to a physical connection (RS-232) and an example of driving the protocol
 * through its semantics.
 *
 * sendXXX(...) methods are provided for users of this API to emit commands and receiveXXX(...)
 * methods should be overridden to act on the receipt of response messages.
 *
 * Consumers of this API should designate a thread to repeatedly run the receive() method
 * as long as the receive() call returns true.  When receive() returns false processing has
 * stopped and the connection is no longer viable.
 *
 *
 * @author jplourde
 *
 */
public class Medibus {
    private static final Logger log = LoggerFactory.getLogger(Medibus.class);

    private int versionMajor = 3;
    private int versionMinor = 0;

    protected final InputStream slowIn, fastIn;
    protected final OutputStream out;


    /**
     * When a consumer of this API has established a connection to a Draeger device
     * they may use this constructor to create a Medibus instance for composing messages
     * to send to the device as well as for parsing received messages.
     * @param in Source of data from Draeger device
     * @param out Destination of data bound for Draeger device
     */
    public Medibus(InputStream in, OutputStream out) {
        // Implementing software flow control doesn't seem to be necessary
//		bis =  new SuspendableInputStream(ASCIIByte.DC1, ASCIIByte.DC3, bis);

        // partition the slow and fast data
        // fast data have the high order bit set and slow data do not
        InputStreamPartition isp = new InputStreamPartition(new InputStreamPartition.Filter[] {
                new InputStreamPartition.Filter() {

                    @Override
                    public boolean passes(int b) {
                        return 0==(b & 0x80);
                    }

                },
                new InputStreamPartition.Filter() {

                    @Override
                    public boolean passes(int b) {
                        return 0!=(b & 0x80);
                    }

                }
        }, in);
        isp.getProcessingThread().setName("Medibus I/O Multiplexor");
        this.slowIn = isp.getInputStream(0);
        this.fastIn = isp.getInputStream(1);
        this.out = out;
        log.trace("Initialized Medibus");
    }

    private static final byte asciiChar(byte b) {
        if(b < 10) {
            return (byte)('0'+b);
        } else {
            return (byte)('A'+(b-10));
        }
    }

    private static final int asciiValue(int v) {
        if(v >= '0' && v <= '9') {
            return v - '0';
        } else if(v >= 'A' && v <= 'F') {
            return 10 + v - 'A';
        } else if(v >= 'a' && v <= 'f') {
            return 10 + v - 'a';
        } else {
            throw new IllegalArgumentException(v + " is not a valid ascii character representing a hex digit");
        }
    }
    protected static final int recvASCIIHex(byte[] buf, int off) {
        return recvASCIIHex(buf, off, 2);
    }

    protected static final int recvASCIIHex(byte[] buf, int off, int len) {
        int v = 0;
        int totalmask = 0;
        for(int i = 0; i < len; i++) {
            v |= (asciiValue(buf[off+i])<<asciiHexShifts[len - i - 1])&asciiHexMasks[len - i - 1];
            totalmask |= asciiHexMasks[len - i - 1];
        }
        return totalmask&v;
    }
    private static final byte[] asciihexbuffer= new byte[2];
    @SuppressWarnings("unused")
    private static final int recvASCIIHex(InputStream in) throws IOException {
        in.read(asciihexbuffer);
        return recvASCIIHex(asciihexbuffer, 0);
    }

    private static final int[] asciiHexMasks = new int[] { 0x0F, 0xF0, 0x0F00, 0xF000 };
    private static final int[] asciiHexShifts = new int[] { 0, 4, 8, 12 };
    protected static final void sendASCIIHex(OutputStream out, byte b) throws IOException {
        short s = (short)(0xFF & b);
        int len = 2;
        for(int i = 0; i < len; i++) {
            out.write(asciiChar( (byte)((s & asciiHexMasks[len - i - 1]) >> asciiHexShifts[len - i - 1])));
        }
    }
    private final void sendASCIIHex(byte b) throws IOException {
        sendASCIIHex(out, b);
    }

    /**
     * Send a command with no arguments
     * @param commandCode the commandCode to send
     * @return true if the command was sent
     * @throws IOException
     */
    public void sendCommand(Command commandCode) throws IOException {
        sendCommand(commandCode, null);
    }

    /**
     * Send a Medibus command with the specified argument and the specified timeout.
     *
     * The semantics of the timeout need work.  Currently it represents the maximum amount
     * of time to await an acknowledgment of the prior command.
     *
     * @param commandCode instance of types.Command or a Byte indicating a command code
     * @param argument Arguments for the command cannot exceed 251 bytes
     * @param timeout in milliseconds, the maximum time to wait for a response or 0L to wait forever
     * @return true if the command was sent
     * @throws IOException
     */
    public synchronized  void sendCommand(Object commandCode, byte[] argument) throws IOException {
        if(argument != null && argument.length > 251) {
            throw new IllegalArgumentException("Arguments may not exceed 251 bytes");
        }
        if(argument != null && versionMajor < 3) {
            throw new IllegalArgumentException("Arguments may not be specified for medibus versions prior to 3.00 (currently " + versionMajor + "." + versionMinor + ")");
        }


        out.write(ASCIIByte.ESC);
        byte cmdByte = commandCode instanceof Command ? ((Command)commandCode).toByte() : (Byte) commandCode;
        out.write(cmdByte);
        int sum = ASCIIByte.ESC;
        sum += (0xFF & cmdByte);
        if(argument != null) {
            for(int i = 0; i < argument.length; i++) {
                sum += (0xFF & argument[i]);
            }
            out.write(argument);
        }
        sendASCIIHex((byte)(0xFF & sum));
        out.write(ASCIIByte.CR);
        out.flush();
        log.trace("sent command:"+commandCode+" " + (null != argument ? toString(argument) : ""));
    }

    /**
     * Sends a response to an inbound command
     *
     * This is called automatically for any command that does not require a special
     * response payload
     *
     * @param command
     * @param response
     * @throws IOException
     */
    public synchronized void sendResponse(Object command, byte[] response) throws IOException {
        // TODO can't be permanent
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(ASCIIByte.SOH);
        byte cmdByte = command instanceof Command ? ((Command)command).toByte() : (Byte) command;

        out.write(cmdByte);
        int sum = ASCIIByte.SOH;
        sum += (0xFF & cmdByte);
        if(response != null) {
            for(int i = 0; i < response.length; i++) {
                sum += (0xFF & response[i]);
            }
            out.write(response);
        }
        sendASCIIHex(out, (byte)(0xFF & sum));
        out.write(ASCIIByte.CR);
        this.out.write(out.toByteArray());
        this.out.flush();


        log.trace("sent response:"+command + " " + (null != response? toString(response) : ""));
    }

    public static final String toString(byte[] arr) {
        if(null == arr) {
            return null;
        } else if(arr.length == 0) {
            return "[]";
        } else {
            StringBuffer sb = new StringBuffer("["+Integer.toHexString(arr[0]));
            for(int i = 1; i < arr.length; i++) {
                sb.append(", ").append(Integer.toHexString(arr[i]));
            }
            sb.append("]");
            return sb.toString();
        }

    }
    /**
     * Sends the ConfigureResponse command with the specified dataTypes
     * as arguments.
     * @param dataTypes
     * @throws IOException
     */
    public void configureDataResponse(DataType... dataTypes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(DataType dt : dataTypes) {
            sendASCIIHex(baos, dt.toByte());
        }
        sendCommand(Command.ConfigureResponse, baos.toByteArray());
    }

    protected void receiveCorruptResponse() {
        log.debug("corrupt command");
    }


    protected void receiveUnknownResponse(Command cmdEcho) {
        log.debug("Unknown command:"+cmdEcho);
    }

    protected void receiveResponse(byte[] buffer, int len) {
        if(len < 1) {
            log.warn("Empty response");
            return;
        }
        Object cmdEcho = Command.fromByteIf(buffer[0]);
        log.trace("Received response:"+cmdEcho);
        if(cmdEcho instanceof Command) {
            Command cmd = (Command) cmdEcho;

            switch(cmd) {
            case ReqMeasuredDataCP1:
            case ReqLowAlarmLimitsCP1:
            case ReqHighAlarmLimitsCP1:
            case ReqMeasuredDataCP2:
            case ReqLowAlarmLimitsCP2:
            case ReqHighAlarmLimitsCP2:
                receiveDataCodes(cmd, buffer, len);
                break;
            case ReqAlarmsCP1:
            case ReqAlarmsCP2:
                receiveAlarmCodes(cmd, buffer, len);
                break;
            case ReqDateTime:
                receiveDateTime(buffer, len);
                break;
            case ReqDeviceSetting:
                receiveDeviceSetting(buffer, len);
                break;
            case ReqTextMessages:
                receiveTextMessage(buffer, len);
                break;
            case ReqDeviceId:
                receiveDeviceIdentification(buffer, len);
                break;
            case InitializeComm:
                // TODO this is important
            case StopComm:
                // TODO this is important
                break;
            case TimeChanged:
            case ConfigureResponse:
            case Corrupt:
                receiveCorruptResponse();
                break;
            case NoOperation:
                log.debug("Not dealing with valid command:"+cmdEcho);
                break;
            default:
                receiveUnknownResponse(cmd);
                break;
            }
        } else {
            log.debug("response to unenumerated command " + cmdEcho);
        }
    }

    static final class Data {
        Object code;
        String data;

        @Override
        public String toString() {
            return "[code="+Medibus.toString(code)+", data="+data+"]";
        }
    }

    protected Data[] data = new Data[0];

    protected Data[] ensureLength(int n) {
        if(data.length < n) {
            Data[] new_data = new Data[n];
            System.arraycopy(this.data, 0, new_data, 0, data.length);
            for(int i = this.data.length; i < n; i++) {
                new_data[i] = new Data();
            }
            this.data = new_data;
        }
        return this.data;
    }


    protected void receiveDataCodes(Command cmdEcho, byte[] response, int len) {
        int codepage = 0;

        switch(cmdEcho) {
        case ReqMeasuredDataCP1:
        case ReqLowAlarmLimitsCP1:
        case ReqHighAlarmLimitsCP1:
            codepage = 1;
            break;
        case ReqMeasuredDataCP2:
        case ReqLowAlarmLimitsCP2:
        case ReqHighAlarmLimitsCP2:
            codepage = 2;
            break;
        default:
            throw new IllegalArgumentException("Unknown cmd:"+cmdEcho);
        }
        int n = len / 6;
        Data[] data = ensureLength(n);

//		Data[] data = new Data[len / 6];
        for(int i = 0; i < n; i++) {
            if(null == data[i]) {
                log.warn("Allocating data on the fly .. this shouldn't happen");
                data[i] = new Data();
            }
//			data[i] = new Data();
            switch(codepage) {
            case 1:
                data[i].code = MeasuredDataCP1.fromByteIf((byte)recvASCIIHex(response, 1+i*6));
                break;
            case 2:
                data[i].code = MeasuredDataCP2.fromByteIf((byte)recvASCIIHex(response, 1+i*6));
                break;
            }

            data[i].data = new String(response, 1+i*6+2, 4);
        }
        for(int i = n; i < this.data.length; i++) {
            data[i] = null;
        }
        switch(cmdEcho) {
        case ReqMeasuredDataCP1:
        case ReqMeasuredDataCP2:
            receiveMeasuredData(data, n);
            break;
        case ReqLowAlarmLimitsCP1:
        case ReqLowAlarmLimitsCP2:
            receiveLowAlarmLimits(data, n);
            break;
        case ReqHighAlarmLimitsCP1:
        case ReqHighAlarmLimitsCP2:
            receiveHighAlarmLimits(data, n);
            break;
        default:
            throw new IllegalArgumentException("Unknown cmd:"+cmdEcho);
        }
    }
    protected void receiveMeasuredData(Data[] data, int n) {
        log.debug("Measured Data");
        for(int i = 0; i < n; i++) {
            log.debug("\t"+data[i]);
        }
    }
    protected void receiveLowAlarmLimits(Data[] data, int n) {
        log.debug("Low Alarm Limits");
        for(int i = 0; i < n; i++) {
            log.debug("\t"+data[i]);
        }
    }
    protected void receiveHighAlarmLimits(Data[] data, int n) {
        log.debug("High Alarm Limits");
        for(int i = 0; i < n; i++) {
            log.debug("\t"+data[i]);
        }
    }

    public static final String toString(Object o) {
        if(o == null) {
            return "null";
        } else if(o instanceof Byte) {
            return Integer.toHexString(0xFF&(Byte)o);
        } else {
            return o.toString();
        }
    }

    static final class Alarm {
        byte priority;
        Object alarmCode;
        String alarmPhrase;
        @Override
        public String toString() {
            return "[priority="+priority+", alarmCode="+Medibus.toString(alarmCode)+", alarmPhrase="+alarmPhrase+"]";
        }
    }
    protected void receiveAlarmCodes(Command cmdEcho, byte[] response, int len) {
        Alarm[] alarms = new Alarm[len / 15];
        for(int i = 0; i < alarms.length; i++) {
            alarms[i] = new Alarm();
            alarms[i].priority = response[1+15*i];
            switch(cmdEcho) {
            case ReqAlarmsCP1:
                alarms[i].alarmCode = AlarmMessageCP1.fromByteIf((byte) recvASCIIHex(response, 1+15*i+1));
                break;
            case ReqAlarmsCP2:
                alarms[i].alarmCode = AlarmMessageCP2.fromByteIf((byte) recvASCIIHex(response, 1+15*i+1));
                break;
            default:
                throw new RuntimeException("Unknown cmd:"+cmdEcho);
            }

            alarms[i].alarmPhrase = new String(response, 1+15*i+3, 12);
        }
        receiveAlarms(alarms);
    }

    protected void receiveAlarms(Alarm[] alarms) {
        log.debug("Alarms");
        for(int i = 0; i < alarms.length; i++) {
            log.debug("\t"+alarms[i]);
        }

    }
    private static final Pattern timePattern = Pattern.compile("^(\\d+):(\\d+):(\\d+)$");
    private static final Pattern datePattern = Pattern.compile("(.+)-(.+)-(.+)");
    private static final Map<String, Integer> germanMonths = new HashMap<String, Integer>();
    static {
        germanMonths.put("JAN", Calendar.JANUARY);
        germanMonths.put("FEB", Calendar.FEBRUARY);
        germanMonths.put("MAR", Calendar.MARCH);
        germanMonths.put("APR", Calendar.APRIL);
        germanMonths.put("MAI", Calendar.MAY);
        germanMonths.put("JUN",  Calendar.JUNE);
        germanMonths.put("JUL", Calendar.JULY);
        germanMonths.put("AUG", Calendar.AUGUST);
        germanMonths.put("SEP", Calendar.SEPTEMBER);
        germanMonths.put("OKT", Calendar.OCTOBER);
        germanMonths.put("NOV", Calendar.NOVEMBER);
        germanMonths.put("DEZ", Calendar.DECEMBER);
    }
    protected void receiveDateTime(byte[] response, int len) {
        Calendar cal = Calendar.getInstance();
        String s = null;
        Matcher m = timePattern.matcher(s = new String(response, 1, 8));
        if(m.matches()) {
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(1)));
            cal.set(Calendar.MINUTE, Integer.parseInt(m.group(2)));
            cal.set(Calendar.SECOND, Integer.parseInt(m.group(3)));
            cal.set(Calendar.MILLISECOND, 0);
            m = datePattern.matcher(s = new String(response, 9, 9));
            if(m.matches()) {
                cal.set(Calendar.DATE, Integer.parseInt(m.group(1)));
                cal.set(Calendar.MONTH, germanMonths.get(m.group(2)));
                cal.set(Calendar.YEAR, 1900 + Integer.parseInt(m.group(3)));
                receiveDateTime(cal.getTime());
            } else {
                log.warn("Received a bad date:"+s);
            }
        } else {
            log.warn("Received a bad time:" + s);
        }
    }
    protected void receiveDateTime(Date date) {
        log.trace("DateTime:"+date);
    }
    protected void receiveDeviceSetting(byte[] response, int len) {
        int n = len / 7;
        ensureLength(n);
//		Data[] data = new Data[len / 7];
        for(int i = 0; i < n; i++) {
//			data[i] = new Data();
            data[i].code = Setting.fromByteIf((byte)recvASCIIHex(response, 1+i*7));
            data[i].data = new String(response, 1+i*7+2, 5);
        }
        receiveDeviceSetting(data, n);
    }

    protected void receiveDeviceSetting(Data[] data, int n) {
        log.trace("Device Setting");
        for(Data d : data) {
            log.trace
            ("\t"+d);
        }
    }

    protected void receiveTextMessage(byte[] response, int len) {
        int off = 0;
//		java.util.List<Data> data = new java.util.ArrayList<Data>();
        int i = 0;
        while(off < len) {
            ensureLength(i + 1);
//			Data d = new Data();
//			data.add(d);
            Data d = data[i++];
            d.code = TextMessage.fromByteIf((byte) recvASCIIHex(response, 1+off));
            off+=2;
            int length = 0xFF&response[1+off];
            length -= 0x30;
            off++;
            d.data = new String(response, 1+off, length);
            off+=length;
            // ETX
            off++;
        }
        receiveTextMessage(data, i);
    }
    protected void receiveTextMessage(Data[] data, int n) {
        log.debug("Text Messages");
        for(int i = 0; i < n; i++) {
            Data d = data[i];
            log.debug("\t"+d);
        }
    }

    private final static Charset ASCII = Charset.forName("ASCII");
    protected void sendDeviceIdentification(String idNumber, String name, String revision) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(idNumber.getBytes(ASCII));
        baos.write(("'"+name+"'").getBytes(ASCII));
        StringBuilder sb = new StringBuilder(revision);
        while(sb.length() < 11) {
            sb.append(" ");
        }
        baos.write(sb.toString().getBytes(ASCII));
        sendResponse(Command.ReqDeviceId, baos.toByteArray());
    }

    private static final byte APOSTROPHE = 0x27;

    protected void receiveDeviceIdentification(byte[] response, int len) {
//		try {
//			sendDeviceIdentification("0161", "ICE", "04.03");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        if(len >= 4) {

            String idNumber = new String(response, 1, 4);
            StringBuilder sb = new StringBuilder();

            int off = 4;
            boolean insideApostrophes = false;
            while(off < len) {
                int v = response[1+off++];
                if(APOSTROPHE == v) {
                    if(!insideApostrophes) {
                        insideApostrophes = true;
                        continue;
                    } else {
                        break;
                    }
                }
                if(insideApostrophes) {
                    sb.append(Character.valueOf((char) v));
                }
            }

            if( (off+11) <= len) {
                String revision = new String(response, 1+off, 11);
                receiveDeviceIdentification(idNumber, sb.length()==0?null:sb.toString(), revision);
            } else {
                receiveDeviceIdentification(idNumber, sb.toString(), null);
            }
        } else {
            // fire this to indicate receipt of the message but no payload
            receiveDeviceIdentification(null, null, null);
        }

    }

    protected void receiveDeviceIdentification(String idNumber, String name, String revision) {
        log.trace("DeviceId="+idNumber+" name="+name+" revision="+revision);
    }

    private final byte[] receiveBuffer = new byte[32000];

    private static class Buffer {
        public Buffer(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public int getCount() {
            return count;
        }

        public byte[] getReceiveBuffer() {
            return receiveBuffer;
        }

        public void addByte(byte b) {
            receiveBuffer[count++] = b;
        }
        public Buffer reset(Type type) {
            this.type = type;
            this.count = 0;
            return this;
        }
        private final byte[] receiveBuffer = new byte[8000];
        private int count;
        private Type type;
        public enum Type {
            Command, Response
        }
    }

    private static final Buffer aFreeBuffer(Buffer.Type type, List<Buffer> freeBuffers) {
        if(freeBuffers.isEmpty()) {
            return new Buffer(type);
        } else {
            return freeBuffers.remove(0).reset(type);
        }
    }

    public boolean receive() throws IOException {
        int leading = 0;

        List<Buffer> buffers = new ArrayList<Buffer>();
        List<Buffer> freeBuffers = new ArrayList<Buffer>();

        Buffer topBuffer = null;

        while(true) {
            leading = slowIn.read();
            if(leading < 0) {
                log.trace("receive got " + leading + " from slowIn.read");
                // EOF
                return false;
            }

            switch(leading) {
            case ASCIIByte.SOH:
                buffers.add(0, topBuffer = aFreeBuffer(Buffer.Type.Response, freeBuffers));
                break;
            case ASCIIByte.ESC:
                buffers.add(0, topBuffer = aFreeBuffer(Buffer.Type.Command, freeBuffers));
                break;
            case ASCIIByte.CR:
                if(null != topBuffer) {
                    switch(topBuffer.getType()) {
                    case Command:
                        receiveCommand(topBuffer.getReceiveBuffer(), topBuffer.getCount());
                        break;
                    case Response:
                        receiveResponse(topBuffer.getReceiveBuffer(), topBuffer.getCount());
                        break;
                    }
                    freeBuffers.add(topBuffer);
                    buffers.remove(0);
                    topBuffer = buffers.isEmpty() ? null : buffers.get(0);
                } else {
                    log.warn("Received a CR with no matching SOH or ESC");
                }
                break;
            default:
                if(topBuffer != null) {
                    topBuffer.addByte((byte) leading);
                } else {
                    log.error("Unknown byte: 0x"+Integer.toHexString(leading));
                }
            }
        }
    }
    protected String getIdNumber() {
        return "0161";
    }
    protected String getName() {
        return "ICE";
    }
    protected String getRevision() {
        return "04.03";
    }
    protected void receiveCommand(byte[] buffer, int len) throws IOException {
        if(len < 1) {
            log.warn("Empty command");
            return;
        }
        Object cmdCode = Command.fromByteIf(buffer[0]);
        log.trace("Received command:"+cmdCode);
        // Check digits
        len -= 2;
        if(cmdCode instanceof Command) {
            switch((Command)cmdCode) {
            case InitializeComm:
                sendResponse(cmdCode, null);
                break;
            case TimeChanged:
                sendResponse(cmdCode, null);
                sendCommand(Command.ReqDateTime);
                break;
            case NoOperation:
            case StopComm:

                sendResponse(cmdCode, null);
                break;
            case ReqDeviceId:
                sendDeviceIdentification(getIdNumber(), getName(), getRevision());
                break;


            case ConfigureResponse:
            case Corrupt:

            case ReqAlarmsCP1:
            case ReqAlarmsCP2:
            case ReqDateTime:
            case ReqDeviceSetting:
            case ReqHighAlarmLimitsCP1:
            case ReqHighAlarmLimitsCP2:
            case ReqLowAlarmLimitsCP1:
            case ReqLowAlarmLimitsCP2:
            case ReqMeasuredDataCP1:
            case ReqMeasuredDataCP2:

            case ReqTextMessages:
            default:
                sendResponse(cmdCode, null);
                log.debug("Not acting on received command:"+cmdCode);
                break;
            }
        } else {
            log.debug("Received unenumerated command:"+cmdCode);
            sendResponse(cmdCode, null);
        }
    }
}
