package org.mdpnp.devices.fluke.prosim8;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlukeProSim8 {

    private static final byte
            CR = 0x0D,
            LF = 0x0A,
            SP = 0x20,
            BS = 0x08,
            ESC = 0x1B,
            STX = 0x02,
            ETX = 0x03,
            ACK = 0x06,
            NAK = 0x15;
    private final String CRLF = "\r\n";

    private final String OZYMANDIAS = "OZYMANDIAS";

    public enum KeyCode {
        F1("01"),
        F2("02"),
        F3("03"),
        F4("04"),
        F5("05"),
        UpArrow("06"),
        DownArrow("07"),
        LeftArrow("08"),
        RightArrow("09"),
        Enter("10"),
        ECG("11"),
        NIBP("12"),
        SpecialFunctions("13"),
        SpO2("14"),
        IBP("15"),
        Setup("16"),
        Backlight("17");

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
        if(null != timeZone) {
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
        return sendCommand("SETRTC", 1,
                dateText.substring(11, 13),
                dateText.substring(14, 16),
                dateText.substring(17, 19),
                dateText.substring(0, 2),
                dateText.substring(3, 5),
                dateText.substring(6, 10))[0];
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
        if(sat < 0 || sat > 100) {
            throw new IllegalArgumentException("Invalid Sat:"+sat);
        }
        if(sat < 1) {
            return sendCommand("SAT", 1, "000")[0];
        } else if (sat < 10) {
            return sendCommand("SAT", 1, "00" + sat)[0];
        } else if (sat < 100) {
            return sendCommand("SAT", 1, "0" + sat)[0];
        } else {
            return sendCommand("SAT", 1, ""+sat)[0];
        }
    }

    public String respirationRate(int rate) throws IOException {
        if(rate < 10 || rate > 150) {
            throw new IllegalArgumentException("Invalid Rate:"+rate);
        }
        if(rate < 100) {
            return sendCommand("RESPRATE", 1, "0"+rate)[0];
        } else {
            return sendCommand("RESPRATE", 1, ""+rate)[0];
        }
    }

    public String normalSinusRhythmAdult(int rate) throws IOException {
        if(rate < 10 || rate > 360) {
            throw new IllegalArgumentException("Invalid Rate:"+rate);
        }
        if(rate < 100) {
            return sendCommand("NSRA", 1, "0"+rate)[0];
        } else {
            return sendCommand("NSRA", 1, ""+rate)[0];
        }
    }

    public String sendKey(KeyCode code, int cycles) throws IOException {
        return sendCommand("KEY", 1, code.number, ""+cycles)[0];
    }

    public synchronized String[] sendCommand(String command, int expectedLines, String... arguments) throws IOException {
        writer.write(command);
        if(arguments.length > 0) {
            writer.write("=");
            writer.write(arguments[0]);
            for(int i = 1; i < arguments.length; i++) {
                writer.write(",");
                writer.write(arguments[i]);
            }
        }

        writer.write(CRLF);
        writer.flush();

        String[] result = new String[expectedLines];

        for(int i = 0; i < result.length; i++) {
            // TODO A 2-second timeout is totally arbitrary here
            long giveup = System.currentTimeMillis() + 2000L;

            while(this.result == null && System.currentTimeMillis() < giveup) {
                try {
                    this.wait(250L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(null == this.result) {
                log.warn("No response " + (i+1) + "/" + result.length + " for command " + command);
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
        synchronized(this) {
            while(this.result != null) {
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
        log.debug("Received string="+line);
    }
}
