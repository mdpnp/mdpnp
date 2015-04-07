package org.mdpnp.devices.puritanbennett._840;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class PB840 {
    public enum Units {
        BREATHS_PER_MIN,
        CMH2O,
        LITERS,
        LITERS_PER_MIN,
        PERCENT,
        SECONDS,
        MILLIMETERS,
        KILOGRAMS,
        CMH2O_PER_L_PER_SEC,
        CMH2O_PER_L,
        ML_PER_CMH2O,
        JOULES_PER_LITER,
        UNKNOWN;
        
        
        //XXX Can't override valueOf, so this is a workaround in case that we need to "parse"
        // something worse type hasn't been checked
        public static Units getValue(String value) {
        	if(null== value || value.trim().equals(""))
        		return UNKNOWN;
        try{
        	return valueOf(value);
        }catch(IllegalArgumentException iae){
        	return UNKNOWN; //TODO throw new IllegalArgumentException()
        }catch(Exception e){
        	return UNKNOWN; //TODO throw new Exception()
        }
      }
    }
    
    protected final BufferedReader in;
    protected final OutputStream out;
    
    public PB840(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in, Charset.forName("ASCII")));
        this.out = out;
    }
    public abstract boolean receive() throws IOException;
}
