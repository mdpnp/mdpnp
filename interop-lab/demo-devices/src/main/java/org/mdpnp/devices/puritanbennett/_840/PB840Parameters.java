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
    
//    private static final Pattern miscf = Pattern.compile("^MISCF,(\\d{4}),(\\d{3})\\s*,\\02");
    private static final Pattern dataField = Pattern.compile("([^,\\03]*)[,\\03]{1,2}");
    
    private static final Logger log = LoggerFactory.getLogger(PB840Parameters.class);
    
    protected final List<String> fieldValues = new ArrayList<String>();
    
    public boolean receive() throws IOException {
        String line = in.readLine();
        log.trace("READ A PARAMETER LINE:"+line);
        while(line != null) {
            Matcher dataFieldMatch = dataField.matcher(line);
            fieldValues.clear();
            if(dataFieldMatch.find() && "MISCF".equals(dataFieldMatch.group(1))) {
                if(dataFieldMatch.find()) {
                    @SuppressWarnings("unused")
                    int bytes = Integer.parseInt(dataFieldMatch.group(1).trim());
                    if(dataFieldMatch.find()) {
                        String s = dataFieldMatch.group(1).trim();
                        int fields = Integer.parseInt(s);
                        for(int i = 0; i < fields; i++) {
                            if(dataFieldMatch.find()) {
                                fieldValues.add(dataFieldMatch.group(1).trim());
                            } else {
                                log.warn("Missing expected field " + (i+1));
                            }
                        }
                        receiveMiscF(fieldValues);
                    } else {
                        log.warn("Not a valid MISCF response, no field count:"+line);
                    }
                } else {
                    log.warn("Not a valid MISCF response, no bytes:"+line);
                }
                
            } else {
                log.warn("Not a MISCF response:"+line);
            }
            line = in.readLine();
        }
        return true;
    }
    public void receiveMiscF(List<String> fieldValues) {
        
    }
}
