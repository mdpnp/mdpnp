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
package org.mdpnp.devices.fluke.prosim8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class FlukeProSim8 {

    private static final byte CR = 0x0D, LF = 0x0A, SP = 0x20, BS = 0x08, ESC = 0x1B, STX = 0x02, ETX = 0x03, ACK = 0x06, NAK = 0x15;
    private final String CRLF = "\r\n";

    private final String OZYMANDIAS = "OZYMANDIAS";

    public enum KeyCode {
        F1("01"), F2("02"), F3("03"), F4("04"), F5("05"), UpArrow("06"), DownArrow("07"), LeftArrow("08"), RightArrow("09"), Enter("10"), ECG("11"), NIBP(
                "12"), SpecialFunctions("13"), SpO2("14"), IBP("15"), Setup("16"), Backlight("17");

        private String number;

        KeyCode(String number) {
            this.number = number;
        }
    }

    private final InputStream in;
    private final OutputStream out;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    private final Logger log = LoggerFactory.getLogger(FlukeProSim8.class);

    private final Charset ascii;

    public FlukeProSim8(InputStream in, OutputStream out) {
        this.ascii = Charset.forName("ASCII");
        this.in = in;
        this.reader = new BufferedReader(new InputStreamReader(in, ascii), 128);
        this.out = out;
        this.writer = new BufferedWriter(new OutputStreamWriter(out, ascii), 128);
    }

    private String result = null;

    public String ident() throws IOException {
        String[] results = sendCommand("IDENT", 1);
        return results[0];
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public Date getRealTimeClock(TimeZone timeZone) throws IOException, ParseException {
        String[] results = sendCommand("GETRTC", 2);
        if (null != timeZone) {
            // TODO not even a little threadsafe
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat.parse(results[1] + " " + results[0]);
    }

    public Date getRealTimeClock() throws IOException, ParseException {
        return getRealTimeClock(null);
    }

    public String setRealTimeClock(Date date) throws IOException {
        String dateText = dateFormat.format(date);
        return sendCommand("SETRTC", 1, dateText.substring(11, 13), dateText.substring(14, 16), dateText.substring(17, 19), dateText.substring(0, 2),
                dateText.substring(3, 5), dateText.substring(6, 10))[0];
    }

    public String validationOn() throws IOException {
        String[] results = sendCommand("VALIDATION", 1, OZYMANDIAS);
        return results[0];
    }

    public String validationOff() throws IOException {
        String[] results = sendCommand("VALOFF", 1);
        return results[0];
    }

    public String remoteMode() throws IOException {
        return sendCommand("REMOTE", 1)[0];
    }

    public String localMode() throws IOException {
        return sendCommand("LOCAL", 1)[0];
    }

    public String saturation(int sat) throws IOException {
        rangeCheck("sat", sat, 0, 100);

        return sendCommand("SAT", 1, leadingZeroes(false, 3, sat))[0];
    }

    public enum Wave {
        Arterial("ART"), RadialArtery("RART"), LeftVentricle("LV"), LeftAtrium("LA"), RightVentricle("RV"), PulmonaryArtery("PA"), PAWedge("PAW"), RightAtriumCVP(
                "RA");
        public final String code;

        Wave(final String code) {
            this.code = code;
        }
    }

    private static final void rangeCheck(String s, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("Invalid " + s + " value:" + value);
        }
    }

    private static final void channelCheck(int channel) {
        if (channel < 1 || channel > 2) {
            throw new IllegalArgumentException("Invalid channel:" + channel);
        }
    }

    private static final ThreadLocal<StringBuilder> builder = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder();
        };
    };

    private static final String leadingZeroes(boolean sign, int places, int value) {
        StringBuilder b = builder.get();
        b.delete(0, b.length());
        b.append(value);
        while (b.length() < places) {
            b.insert(0, "0");
        }
        if (sign) {
            if (value >= 0) {
                b.insert(0, "+");
            } else {
                b.insert(0, "-");
            }
        }
        return b.toString();
    }

    public String nonInvasiveBloodPressureDynamic(int systolic, int diastolic) throws IOException {
        rangeCheck("systolic", systolic, 0, 400);
        rangeCheck("diastolic", diastolic, 0, 400);
        return sendCommand("NIBPP", 1, leadingZeroes(false, 3, systolic), leadingZeroes(false, 3, diastolic))[0];
    }

    public String invasiveBloodPressureDynamic(int channel, int systolic, int diastolic) throws IOException {
        channelCheck(channel);
        rangeCheck("systolic", systolic, 0, 300);
        rangeCheck("diastolic", diastolic, 0, 300);

        return sendCommand("IBPP", 1, "" + channel, leadingZeroes(false, 3, systolic), leadingZeroes(false, 3, diastolic))[0];
    }

    public String invasiveBloodPressureWave(int channel, Wave wave) throws IOException {
        channelCheck(channel);
        return sendCommand("IBPW", 1, "" + channel, wave.code)[0];
    }

    public String invasiveBloodPressureStatic(int channel, int value) throws IOException {
        channelCheck(channel);
        rangeCheck("pressure", value, -10, 300);

        return sendCommand("IBPS", 1, "" + channel, leadingZeroes(true, 3, value))[0];
    }

    public String respirationRate(int rate) throws IOException {
        rangeCheck("rate", rate, 10, 150);
        return sendCommand("RESPRATE", 1, leadingZeroes(false, 3, rate))[0];
    }

    public String normalSinusRhythmAdult(int rate) throws IOException {
        rangeCheck("rate", rate, 10, 360);
        return sendCommand("NSRA", 1, leadingZeroes(false, 3, rate))[0];
    }

    public String sendKey(KeyCode code, int cycles) throws IOException {
        return sendCommand("KEY", 1, code.number, "" + cycles)[0];
    }

    public synchronized String[] sendCommand(String command, int expectedLines, String... arguments) throws IOException {
        writer.write(command);
        if (arguments.length > 0) {
            writer.write("=");
            writer.write(arguments[0]);
            for (int i = 1; i < arguments.length; i++) {
                writer.write(",");
                writer.write(arguments[i]);
            }
        }

        writer.write(CRLF);
        writer.flush();

        String[] result = new String[expectedLines];

        for (int i = 0; i < result.length; i++) {
            // TODO A 2-second timeout is totally arbitrary here
            long giveup = System.currentTimeMillis() + 2000L;

            while (this.result == null && System.currentTimeMillis() < giveup) {
                try {
                    this.wait(250L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null == this.result) {
                log.warn("No response " + (i + 1) + "/" + result.length + " for command " + command);
                return result;
            } else {
                result[i] = this.result;
                this.result = null;
                notifyAll();

            }
        }
        return result;
    }

    public void receiveCommand() throws IOException {
        String result = reader.readLine();
        synchronized (this) {
            while (this.result != null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            receiveString(result);
            this.result = result;
            notifyAll();
        }

    }

    public void receiveString(String line) {
        log.debug("Received string=" + line);
    }
}
