package org.mdpnp.devices.puritanbennett._840;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PB840Waveforms extends PB840 {
    protected static final Logger log = LoggerFactory.getLogger(PB840Waveforms.class);
    
    public PB840Waveforms(InputStream input, OutputStream output) {
        super(input, output);
    }
    
    private final static Pattern breathStart = Pattern.compile("^BS\\,\\s*S\\:(\\d+)\\,$");
    private final static Pattern breathEnd = Pattern.compile("^BE");
    private final static Pattern samples = Pattern.compile("^([0-9.-]+)\\,\\s*([0-9.-]+)$");
    private boolean breathActive;
    private final List<Number> flow = new ArrayList<Number>(), pressure = new ArrayList<Number>();
    
    
    
    public boolean receive() {
        for(;;) {
            try {
                String s = in.readLine();
                if(s == null) {
                    return true;
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
