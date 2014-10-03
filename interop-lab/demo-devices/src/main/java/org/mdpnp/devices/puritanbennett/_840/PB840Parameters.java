package org.mdpnp.devices.puritanbennett._840;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PB840Parameters extends PB840 {
    public PB840Parameters(final InputStream in, final OutputStream out) {
        super(in, out);
    }
    
    private static final byte[] RSET = new byte[] {'R','S','E','T','\r', '\n'};
    private static final byte[] SNDA = new byte[] {'S', 'N', 'D', 'A', '\r', '\n'};
    private static final byte[] SNDF = new byte[] {'S','N','D','F','\r', '\n'};
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
    
    private static final Pattern miscf = Pattern.compile("^MISCF(\\d{4})(\\d{3})\\02");
    private static final Pattern dataField = Pattern.compile("^([^,\\02]*)[,\2]{1,2}");
    
    private static final Logger log = LoggerFactory.getLogger(PB840Parameters.class);
    
    protected final List<String> fieldValues = new ArrayList<String>();
    
    public boolean receive() throws IOException {
        String line = in.readLine();
        while(line != null) {
            Matcher miscfMatch = miscf.matcher(line);
            if(miscfMatch.matches()) {
                fieldValues.clear();
                @SuppressWarnings("unused")
                int bytes = Integer.parseInt(miscfMatch.group(1));
                int fields = Integer.parseInt(miscfMatch.group(2));
                Matcher fieldMatch = dataField.matcher(line.substring(miscfMatch.end()));
                for(int i = 0; i < fields; i++) {
                    if(fieldMatch.find()) {
                        fieldValues.add(fieldMatch.group(1));
                    } else {
                        log.warn("Missing expected field " + (i+1));
                    }
                }
                receiveMiscF(fieldValues);
            } else {
                log.debug("Not a MISCF response:"+line);
            }
            line = in.readLine();
        }
        return true;
    }
    public void receiveMiscF(List<String> fieldValues) {
        
    }
}
