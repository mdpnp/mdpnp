/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.draeger.medibus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
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
import org.mdpnp.devices.io.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOT FOR HUMAN USE
 * 
 * Experimental implementation of Draeger Medibus protocol for blocking I/O.
 * This API gives the caller a simple way to emit protocol commands and receive
 * protocol responses. Out of scope is the particular semantics of the protocol.
 * 
 * It is the responsibility of the caller to create a connection to a Draeger
 * device using a serial port connection or otherwise. Once that connection is
 * established the relevant InputStream and OutputStream should be passed to the
 * constructor.
 * 
 * See the Interoperability Lab demo-devices for an example of how this class
 * can be bound to a physical connection (RS-232) and an example of driving the
 * protocol through its semantics.
 * 
 * sendXXX(...) methods are provided for users of this API to emit commands and
 * receiveXXX(...) methods should be overridden to act on the receipt of
 * response messages.
 * 
 * Consumers of this API should designate a thread to repeatedly run the
 * receive() method as long as the receive() call returns true. When receive()
 * returns false processing has stopped and the connection is no longer viable.
 * 
 * 
 * @author Jeff Plourde
 * 
 */
public class Medibus {
    private static final Logger log = LoggerFactory.getLogger(Medibus.class);

    private int versionMajor = 3;
    private int versionMinor = 0;

    protected final InputStream slowIn;
    protected final ChecksumOutputStream out;

    /**
     * When a consumer of this API has established a connection to a Draeger
     * device they may use this constructor to create a Medibus instance for
     * composing messages to send to the device as well as for parsing received
     * messages.
     * 
     * @param in
     *            Source of data from Draeger device
     * @param out
     *            Destination of data bound for Draeger device
     * @throws IOException 
     */
    public Medibus(InputStream in, OutputStream out) throws IOException {
        // partition the slow and fast data
        // fast data have the high order bit set and slow data do not
        InputStreamPartition isp = new InputStreamPartition(new InputStreamPartition.Filter[] { 
                new InputStreamPartition.Filter() {

                    @Override
                    public boolean passes(int b) {
                        if(0 != (b & 0x80)) {
                            fastByte(b);
                            return true;
                        } else {
                            return false;
                        }

                    }
                    // This is an optimization.  Data will be handled in the passes call and there is 
                    // not need to enqueue data onto a pipe
                    public boolean createPipe() { return false; }

                }, new InputStreamPartition.Filter() {

            @Override
            public boolean passes(int b) {
                switch(b) {
                case ASCIIByte.DC1:
                    log.warn("DC1 (0x11) ignored in stream");
                    return false;
                case ASCIIByte.DC3:
                    log.warn("DC3 (0x13) ignored in stream");
                    return false;
                }
                return 0 == (b & 0x80);
            }
            
            @Override
            public boolean createPipe() {
                return true;
            }

        },  }, in);
        isp.getProcessingThread().setName("Medibus I/O Multiplexor");
        this.slowIn = isp.getInputStream(1);
        this.out = new ChecksumOutputStream(out);
        log.trace("Initialized Medibus");
    }

    protected void fastByte(int b) {
        
    }
    
    private static final byte asciiChar(byte b) {
        if (b < 10) {
            return (byte) ('0' + b);
        } else {
            return (byte) ('A' + (b - 10));
        }
    }

    private static final int asciiValue(int v) throws CorruptMedibusException {
        if (v >= '0' && v <= '9') {
            return v - '0';
        } else if (v >= 'A' && v <= 'F') {
            return 10 + v - 'A';
        } else if (v >= 'a' && v <= 'f') {
            return 10 + v - 'a';
        } else if(' ' == v) {
            // attribute no value to spaces
            return 0;
        } else {
            throw new CorruptMedibusException(v + " is not a valid ascii character representing a hex digit");
        }
    }

    protected static final int recvASCIIHex(byte[] buf, int off) throws CorruptMedibusException {
        return recvASCIIHex(buf, off, 2);
    }

    protected static final int recvASCIIHex(byte[] buf, int off, int len) throws CorruptMedibusException {
        int v = 0;
        int totalmask = 0;
        for (int i = 0; i < len; i++) {
            v |= (asciiValue(buf[off + i]) << asciiHexShifts[len - i - 1]) & asciiHexMasks[len - i - 1];
            totalmask |= asciiHexMasks[len - i - 1];
        }
        return totalmask & v;
    }

    private static final byte[] asciihexbuffer = new byte[2];

    @SuppressWarnings("unused")
    private static final int recvASCIIHex(InputStream in) throws IOException {
        in.read(asciihexbuffer);
        return recvASCIIHex(asciihexbuffer, 0);
    }

    private static final int[] asciiHexMasks = new int[] { 0x0F, 0xF0, 0x0F00, 0xF000 };
    private static final int[] asciiHexShifts = new int[] { 0, 4, 8, 12 };

    protected static final void sendASCIIHex(OutputStream out, byte b) throws IOException {
        short s = (short) (0xFF & b);
        int len = 2;
        for (int i = 0; i < len; i++) {
            out.write(asciiChar((byte) ((s & asciiHexMasks[len - i - 1]) >> asciiHexShifts[len - i - 1])));
        }
    }

    private final void sendASCIIHex(byte b) throws IOException {
        sendASCIIHex(out, b);
    }

    /**
     * Send a command with no arguments
     * 
     * @param commandCode
     *            the commandCode to send
     * @return true if the command was sent
     * @throws IOException
     */
    public void sendCommand(Command commandCode) throws IOException {
        sendCommand(commandCode, (byte[])null);
    }

    /**
     * Send a Medibus command with the specified argument and the specified
     * timeout.
     * 
     * The semantics of the timeout need work. Currently it represents the
     * maximum amount of time to await an acknowledgment of the prior command.
     * 
     * @param commandCode
     *            instance of types.Command or a Byte indicating a command code
     * @param argument
     *            Arguments for the command cannot exceed 251 bytes
     * @param timeout
     *            in milliseconds, the maximum time to wait for a response or 0L
     *            to wait forever
     * @return true if the command was sent
     * @throws IOException
     */
    public synchronized void sendCommand(Object commandCode, byte[] argument) throws IOException {
        if (argument != null && argument.length > 251) {
            throw new IllegalArgumentException("Arguments may not exceed 251 bytes");
        }
        if (argument != null && versionMajor < 3) {
            throw new IllegalArgumentException("Arguments may not be specified for medibus versions prior to 3.00 (currently " + versionMajor + "."
                    + versionMinor + ")");
        }
        out.resetChecksum();
        out.write(ASCIIByte.ESC);
        byte cmdByte = commandCode instanceof Command ? ((Command) commandCode).toByte() : (Byte) commandCode;
        out.write(cmdByte);
        if (argument != null) {
            out.write(argument);
        }
        sendASCIIHex((byte) (0xFF & out.getChecksum()));
        out.write(ASCIIByte.CR);
        out.flush();
        if(log.isTraceEnabled()) {
            log.trace("sent command:" + commandCode + " " + (null != argument ? toString(argument) : ""));
        }
    }
    
    public synchronized void sendCommand(Object commandCode, ByteArrayOutputStream baos) throws IOException {
        out.resetChecksum();
        out.write(ASCIIByte.ESC);
        byte cmdByte = commandCode instanceof Command ? ((Command) commandCode).toByte() : (Byte) commandCode;
        out.write(cmdByte);

        if (baos != null) {
            baos.writeTo(out);
        }
        sendASCIIHex((byte) (0xFF & out.getChecksum()));
        out.write(ASCIIByte.CR);
        out.flush();
        if(log.isTraceEnabled()) {
            log.trace("sent command:" + commandCode);
        }
    }

    /**
     * Sends a response to an inbound command
     * 
     * This is called automatically for any command that does not require a
     * special response payload
     * 
     * @param command
     * @param response
     * @throws IOException
     */
    public synchronized void sendResponse(Object command) throws IOException {
        sendResponse(command, (byte[]) null);
    }
    
    public synchronized void sendResponse(Object command, byte[] response) throws IOException {
        out.resetChecksum();
        out.write(ASCIIByte.SOH);
        byte cmdByte = command instanceof Command ? ((Command) command).toByte() : (Byte) command;
        out.write(cmdByte);
        if (response != null) {
            out.write(response);
        }
        sendASCIIHex(out, (byte) (0xFF & out.getChecksum()));
        out.write(ASCIIByte.CR);
        out.flush();
        if(log.isTraceEnabled()) {
            log.trace("sent response:" + command + " " + (null != response ? toString(response) : ""));
        }
    }
    
    public synchronized void sendResponse(Object command, ByteArrayOutputStream baos) throws IOException {
        out.resetChecksum();
        out.write(ASCIIByte.SOH);
        byte cmdByte = command instanceof Command ? ((Command) command).toByte() : (Byte) command;
        out.write(cmdByte);
        baos.writeTo(out);
        sendASCIIHex(out, (byte) (0xFF & out.getChecksum()));
        out.write(ASCIIByte.CR);
        out.flush();
        if(log.isTraceEnabled()) {
            log.trace("sent response:" + command + " ");
        }
    }

    public static final String toString(byte[] arr) {
        if (null == arr) {
            return null;
        } else if (arr.length == 0) {
            return "[]";
        } else {
            StringBuffer sb = new StringBuffer("[" + Integer.toHexString(arr[0]));
            for (int i = 1; i < arr.length; i++) {
                sb.append(", ").append(Integer.toHexString(arr[i]));
            }
            sb.append("]");
            return sb.toString();
        }

    }
    protected final ByteArrayOutputStream scratchpad = new ByteArrayOutputStream();
    /**
     * Sends the ConfigureResponse command with the specified dataTypes as
     * arguments.
     * 
     * @param dataTypes
     * @throws IOException
     */
    public synchronized void configureDataResponse(DataType... dataTypes) throws IOException {
        scratchpad.reset();
        for (DataType dt : dataTypes) {
            sendASCIIHex(scratchpad, dt.toByte());
        }
        sendCommand(Command.ConfigureResponse, scratchpad);
    }

    protected void receiveCorruptResponse() {
        log.debug("corrupt command");
    }

    protected void receiveUnknownResponse(Command cmdEcho) {
        log.debug("Unknown command:" + cmdEcho);
    }

    protected void receiveResponse(byte[] buffer, int len) throws CorruptMedibusException {
        if (len < 1) {
            log.warn("Empty response");
            return;
        }
        Object cmdEcho = Command.fromByteIf(buffer[0]);
        log.trace("Received response:" + cmdEcho);
        if (cmdEcho instanceof Command) {
            Command cmd = (Command) cmdEcho;

            switch (cmd) {
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
                log.debug("Not dealing with valid command:" + cmdEcho);
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
        Data next;

        @Override
        public String toString() {
            return "[code=" + Medibus.toString(code) + ", data=" + data + "]";
        }
        
        private static Data root;
        
        public synchronized static final Data alloc() {
            if(null == root) {
                return new Data();
            } else {
                Data root = Data.root;
                Data.root = root.next;
                return root;
            }
        }
        public synchronized static final void free(Data data) {
            if(null != data) {
                data.next = Data.root;
                Data.root = data;
            }
        }
    }

    private Data[] data = new Data[1];
    protected void receiveDataCodes(Command cmdEcho, byte[] response, int len) throws CorruptMedibusException {
        int codepage = 0;

        switch (cmdEcho) {
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
            throw new IllegalArgumentException("Unknown cmd:" + cmdEcho);
        }
        len -= 3;
        int n = len / 6;
        if(data.length < n) {
            data = new Data[n];
        }

        for (int i = 0; i < n; i++) {
            data[i] = Data.alloc();
            switch (codepage) {
            case 1:
                data[i].code = MeasuredDataCP1.fromByteIf((byte) recvASCIIHex(response, 1 + i * 6));
                break;
            case 2:
                data[i].code = MeasuredDataCP2.fromByteIf((byte) recvASCIIHex(response, 1 + i * 6));
                break;
            }

            data[i].data = new String(response, 1 + i * 6 + 2, 4).intern();
        }
        for(int i = n; i < data.length; i++) {
            Data.free(data[i]);
            data[i] = null;
        }
        switch (cmdEcho) {
        case ReqMeasuredDataCP1:
            receiveMeasuredData(1,data);
            break;
        case ReqMeasuredDataCP2:
            receiveMeasuredData(2,data);
            break;
        case ReqLowAlarmLimitsCP1:
            receiveLowAlarmLimits(1, data);
            break;
        case ReqLowAlarmLimitsCP2:
            receiveLowAlarmLimits(2, data);
            break;
        case ReqHighAlarmLimitsCP1:
            receiveHighAlarmLimits(1,data);
            break;
        case ReqHighAlarmLimitsCP2:
            receiveHighAlarmLimits(2,data);
            break;
        default:
            throw new IllegalArgumentException("Unknown cmd:" + cmdEcho);
        }
    }

    protected void receiveMeasuredData(int codepage, Data[] data) {
        log.debug("Measured Data");
        for (Data d : data) {
            if(null != d) {
                log.debug("\t" + d);
            }
        }
    }

    protected void receiveLowAlarmLimits(int codepage, Data[] data) {
        log.debug("Low Alarm Limits");
        for (Data d : data) {
            if(null != d) {
                log.debug("\t" + d);
            }
        }
    }

    protected void receiveHighAlarmLimits(int codepage, Data[] data) {
        log.debug("High Alarm Limits");
        for (Data d : data) {
            if(null != d) {
                log.debug("\t" + d);
            }
        }
    }

    public static final String toString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof Byte) {
            return Integer.toHexString(0xFF & (Byte) o);
        } else {
            return o.toString();
        }
    }

    static final class Alarm {
        byte priority;
        Object alarmCode;
        String alarmPhrase;
        Alarm next;

        @Override
        public String toString() {
            return "[priority=" + priority + ", alarmCode=" + Medibus.toString(alarmCode) + ", alarmPhrase=" + alarmPhrase + "]";
        }
        private static Alarm root;
        
        public synchronized static final Alarm alloc() {
            if(null == root) {
                return new Alarm();
            } else {
                Alarm root = Alarm.root;
                Alarm.root = root.next;
                return root;
            }
        }
        public synchronized static final void free(Alarm alarm) {
            if(null != alarm) {
                alarm.next = Alarm.root;
                Alarm.root = alarm;
            }
        }        
    }
    private Alarm[] alarm = new Alarm[1];
    protected void receiveAlarmCodes(Command cmdEcho, byte[] response, int len) throws CorruptMedibusException {
        int n = len / 15;
        if(alarm.length < n) {
            alarm = new Alarm[n];
        }
        for (int i = 0; i < n; i++) {
            alarm[i] = Alarm.alloc();
            alarm[i].priority = (byte)(response[1 + 15 * i] - '0');
            switch (cmdEcho) {
            case ReqAlarmsCP1:
                alarm[i].alarmCode = AlarmMessageCP1.fromByteIf((byte) recvASCIIHex(response, 1 + 15 * i + 1));
                break;
            case ReqAlarmsCP2:
                alarm[i].alarmCode = AlarmMessageCP2.fromByteIf((byte) recvASCIIHex(response, 1 + 15 * i + 1));
                break;
            default:
                throw new RuntimeException("Unknown cmd:" + cmdEcho);
            }

            alarm[i].alarmPhrase = new String(response, 1 + 15 * i + 3, 12).intern();
        }
        for(int i = n; i < alarm.length; i++) {
            Alarm.free(alarm[i]);
            alarm[i] = null;
        }
        receiveAlarms(alarm);
    }

    protected void receiveAlarms(Alarm[] alarms) {
        log.debug("Alarms");
        for (int i = 0; i < alarms.length; i++) {
            if(alarms[i]!=null) {
                log.debug("\t" + alarms[i]);
            }
        }

    }

    // This regex is deliberately constructed
    // Sometimes the evitaXL doesn't emit seconds
    // Sometimes the V500 emits no space
    protected static final Pattern dateTimePattern = Pattern.compile("^\\s*(\\d{1,2}):(\\d{2}):?(\\d{0,2})\\s*(\\d+)-([A-Z]+)-(\\d+)");    
    protected static final Map<String, Integer> germanMonths = new HashMap<String, Integer>();
    static {
        // In October 2014 our EvitaXL started emitting OCT as the month;
        // so I added the english-language equivalents here just in case
        germanMonths.put("JAN", Calendar.JANUARY);
        germanMonths.put("FEB", Calendar.FEBRUARY);
        germanMonths.put("MAR", Calendar.MARCH);
        germanMonths.put("APR", Calendar.APRIL);
        germanMonths.put("MAI", Calendar.MAY);
        germanMonths.put("MAY", Calendar.MAY);
        germanMonths.put("JUN", Calendar.JUNE);
        germanMonths.put("JUL", Calendar.JULY);
        germanMonths.put("AUG", Calendar.AUGUST);
        germanMonths.put("SEP", Calendar.SEPTEMBER);
        germanMonths.put("OKT", Calendar.OCTOBER);
        germanMonths.put("OCT", Calendar.OCTOBER);
        germanMonths.put("NOV", Calendar.NOVEMBER);
        germanMonths.put("DEZ", Calendar.DECEMBER);
        germanMonths.put("DEC", Calendar.DECEMBER);
    }

    private final ThreadLocal<Calendar> calendar = new ThreadLocal<Calendar>() {
        protected Calendar initialValue() {
            return Calendar.getInstance();
        };
    };

    protected void receiveDateTime(byte[] response, int len) {
        Calendar cal = this.calendar.get();
        String s = null;
        Matcher m = dateTimePattern.matcher(s = new String(response, 1, len - 3).intern());
        log.trace("Attempting to parse datetime " + s);
        if (m.matches()) {
            if(m.groupCount()<6) {
                // This shouldn't happen because the regex wouldn't have matched
                log.warn("Insufficient capture groups in datetime:" + s);
            } else {
                int field = 1;
                try {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(field++)));
                    cal.set(Calendar.MINUTE, Integer.parseInt(m.group(field++)));
                    String seconds = m.group(field++);
                    cal.set(Calendar.SECOND, seconds.length()>0?Integer.parseInt(seconds):0);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.DATE, Integer.parseInt(m.group(field++)));
                    String germanMonth = m.group(field++);
                    Integer month = germanMonths.get(germanMonth);
                    if(null != month) {
                        cal.set(Calendar.MONTH, month);
                        // Note the V500 as of 12-Mar-2014 emits "14" as the year
                        cal.set(Calendar.YEAR, 2000 + Integer.parseInt(m.group(field++)));
                        receiveDateTime(cal.getTime());
                    } else {
                        log.warn("Cannot process German month \""+germanMonth+"\" in " + s);
                    }
                    
                } catch (NumberFormatException nfe) {
                    log.warn("Unable to parse a field in datetime " + s + " field was \"" + m.group(field)+"\"");
                }
            }
        } else {
            log.warn("Received a bad datetime:" + s);
        }
    }

    protected void receiveDateTime(Date date) {
        log.trace("DateTime:" + date);
    }

    protected void receiveDeviceSetting(byte[] response, int len) throws CorruptMedibusException {
        len -= 3; // leading command and trailing 2byte checksum
        int n = len / 7;
        if(data.length < n) {
            data = new Data[n];
        }
        
        for (int i = 0; i < n; i++) {
            data[i] = Data.alloc();
            data[i].code = Setting.fromByteIf((byte) recvASCIIHex(response, 1 + i * 7));
            data[i].data = new String(response, 1 + i * 7 + 2, 5).intern();
        }
        for(int i = n; i < data.length; i++) {
            Data.free(data[i]);
            data[i] = null;
        }
        receiveDeviceSetting(data);
    }

    protected void receiveDeviceSetting(Data[] data) {
        log.trace("Device Setting");
        for (Data d : data) {
            if(null != d) {
                log.trace("\t" + d);
            }
        }
    }

    protected void receiveTextMessage(byte[] response, int len) throws CorruptMedibusException {
        int off = 0;
        // Take off the checksum bytes and command code from the count
        len -= 3;
        
        int n = 0;
        while(off < len) {
            int length = (0xFF & response[1 + off + 2]) - 0x30;
            off += 4 + length;
            n++;
        }
        if(data.length < n) {
            data = new Data[n];
        }
        off = 0; 
        for(int i = 0; i < n; i++) {
            Data d = data[i] = Data.alloc();
            d.code = TextMessage.fromByteIf((byte) recvASCIIHex(response, 1 + off));
            int length = (0xFF & response[1 + off + 2]) - 0x30;
            d.data = new String(response, 1 + off + 3, length);
            off += 4 + length; // 4 = 2byte code, 1 byte length, 1 byte trailing ETX
        }
        for(int i = n; i < data.length; i++) {
            Data.free(data[i]);
            data[i] = null;
        }
        
        receiveTextMessage(data);
    }

    protected void receiveTextMessage(Data[] data) {
        log.debug("Text Messages");
        for (Data d : data) {
            if(null != d) {
                log.debug("\t" + d);
            }
        }
    }

    private final static Charset ASCII = Charset.forName("ASCII");

    protected synchronized void sendDeviceIdentification(String idNumber, String name, String revision) throws IOException {
        scratchpad.reset();

        scratchpad.write(idNumber.getBytes(ASCII));
        scratchpad.write(("'" + name + "'").getBytes(ASCII));
        scratchpad.write(revision.getBytes(ASCII));
        for(int i = revision.length(); i < 11; i++) {
            scratchpad.write(' ');
        }
        sendResponse(Command.ReqDeviceId, scratchpad);
    }

    private static final byte APOSTROPHE = 0x27;

    protected void receiveDeviceIdentification(byte[] response, int len) {
        // try {
        // sendDeviceIdentification("0161", "ICE", "04.03");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        if (len >= 4) {

            String idNumber = new String(response, 1, 4).intern();
            StringBuilder sb = new StringBuilder();

            int off = 4;
            boolean insideApostrophes = false;
            while (off < len) {
                int v = response[1 + off++];
                if (APOSTROPHE == v) {
                    if (!insideApostrophes) {
                        insideApostrophes = true;
                        continue;
                    } else {
                        break;
                    }
                }
                if (insideApostrophes) {
                    sb.append(Character.valueOf((char) v));
                }
            }

            if ((off + 11) <= len) {
                String revision = new String(response, 1 + off, 11);
                receiveDeviceIdentification(idNumber, sb.length() == 0 ? null : sb.toString(), revision);
            } else {
                receiveDeviceIdentification(idNumber, sb.toString(), null);
            }
        } else {
            // fire this to indicate receipt of the message but no payload
            receiveDeviceIdentification(null, null, null);
        }

    }

    protected void receiveDeviceIdentification(String idNumber, String name, String revision) {
        log.trace("DeviceId=" + idNumber + " name=" + name + " revision=" + revision);
    }

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
        if (freeBuffers.isEmpty()) {
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

        while (true) {
            leading = slowIn.read();
            if (leading < 0) {
                log.trace("receive got " + leading + " from slowIn.read");
                // EOF
                return false;
            }

            switch (leading) {
            case ASCIIByte.SOH:
                buffers.add(0, topBuffer = aFreeBuffer(Buffer.Type.Response, freeBuffers));
                break;
            case ASCIIByte.ESC:
                buffers.add(0, topBuffer = aFreeBuffer(Buffer.Type.Command, freeBuffers));
                break;
            case ASCIIByte.CR:
                if (null != topBuffer) {
                    if(log.isTraceEnabled()) {
                        String msg = topBuffer.getType() + ":"+HexUtil.dump(ByteBuffer.wrap(topBuffer.receiveBuffer, 0, topBuffer.getCount()), 80);
                        log.trace(msg);
                    }
                    try {
                        switch (topBuffer.getType()) {
                        case Command:
                            receiveCommand(topBuffer.getReceiveBuffer(), topBuffer.getCount());
                            break;
                        case Response:
                            receiveResponse(topBuffer.getReceiveBuffer(), topBuffer.getCount());
                            break;
                        }
                    } catch (CorruptMedibusException cme) {
                        // The contents of this frame were invalid, but we will continue onto the next frame
                        // depending on intended use this might not be a desired behaviour; or perhaps more likely
                        // better reporting of this type of error is required.
                        String msg = topBuffer.getType() + ":"+HexUtil.dump(ByteBuffer.wrap(topBuffer.receiveBuffer, 0, topBuffer.getCount()), 80);
                        log.error(msg);
                    }
                    freeBuffers.add(topBuffer);
                    buffers.remove(0);
                    topBuffer = buffers.isEmpty() ? null : buffers.get(0);
                } else {
                    log.warn("Received a CR with no matching SOH or ESC");
                }
                break;
            default:
                if (topBuffer != null) {
                    topBuffer.addByte((byte) leading);
                } else {
                    log.error("Unknown byte: 0x" + Integer.toHexString(leading));
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
        if (len < 1) {
            log.warn("Empty command");
            return;
        }
        Object cmdCode = Command.fromByteIf(buffer[0]);
        log.trace("Received command:" + cmdCode);
        // Check digits
        len -= 2;
        if (cmdCode instanceof Command) {
            switch ((Command) cmdCode) {
            case InitializeComm:
                sendResponse(cmdCode);
                break;
            case TimeChanged:
                sendResponse(cmdCode);
                sendCommand(Command.ReqDateTime);
                break;
            case NoOperation:
            case StopComm:

                sendResponse(cmdCode);
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
                sendResponse(cmdCode);
                log.debug("Not acting on received command:" + cmdCode);
                break;
            }
        } else {
            log.debug("Received unenumerated command:" + cmdCode);
            sendResponse(cmdCode);
        }
    }
}
