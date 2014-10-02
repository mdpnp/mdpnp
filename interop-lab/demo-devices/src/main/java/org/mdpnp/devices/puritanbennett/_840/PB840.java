package org.mdpnp.devices.puritanbennett._840;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PB840 {
    private final BufferedReader in;
    @SuppressWarnings("unused")
    private final OutputStream out;
    
    protected static final Logger log = LoggerFactory.getLogger(PB840.class);
    
    public PB840(InputStream input, OutputStream output) {
        this.in = new BufferedReader(new InputStreamReader(input, Charset.forName("ASCII")));
        this.out = output;
    }
    
    private final static Pattern breathStart = Pattern.compile("^BS\\,\\s*S\\:(\\d+)\\,$");
    private final static Pattern breathEnd = Pattern.compile("^BE");
    private final static Pattern samples = Pattern.compile("^([0-9.-]+)\\,\\s*([0-9.-]+)$");
    private boolean breathActive;
    private final List<Number> flow = new ArrayList<Number>(), pressure = new ArrayList<Number>();
    
    
    
    public void receive() {
        for(;;) {
            try {
                String s = in.readLine();
                if(s == null) {
                    return;
                }
                s = s.trim();
                if(breathActive) {
                    Matcher m = samples.matcher(s);
                    if(m.matches()) {
                        flow.add(Double.parseDouble(m.group(1)));
                        pressure.add(Double.parseDouble(m.group(2)));
                    } else {
                        m = breathEnd.matcher(s);
                        if(m.matches()) {
                            receiveBreath(flow, pressure);
                            flow.clear();
                            pressure.clear();
                            breathActive = false;
                        } else {
                            
                            m = breathStart.matcher(s);
                            if(!m.matches()) {
                                log.warn("Unexpected during breath " + s);
                                breathActive = false;
                            } else {
                                log.warn("Duplicate breath start (previously started) "+s);
                            }
                        }
                    }
                } else {
                    Matcher m = breathStart.matcher(s);
                    if(m.matches()) {
                        breathActive = true;
                    } else {
                        log.warn("Unexpected outside of breath " + s);
                    }
                }

                
                
            } catch (IOException e) {
                log.error("Error reading from the PB840", e);
            }
        }
    }
    
    public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
        
    }
    
}
