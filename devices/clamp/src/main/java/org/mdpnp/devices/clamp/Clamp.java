package org.mdpnp.devices.clamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Clamp {
    
    private final BufferedReader in;
    private final OutputStream out;
    
    public Clamp(final InputStream in, final OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }
    
    private static final byte[] sendHeartbeatResponse = new byte[] {'#', '\r'};
    public void sendHeartbeatResponse() throws IOException {
        out.write(sendHeartbeatResponse);
        out.flush();
    }
    private static final byte[] lockTriggerSolenoid = new byte[] {'l', 't', '\r'};
    public void lockTriggerSolenoid() throws IOException {
        out.write(lockTriggerSolenoid);
        out.flush();
    }
    
    private static final byte[] unlockTriggerSolenoid = new byte[] {'u','t','\r'};
    public final void unlockTriggerSolenoid() throws IOException {
        out.write(unlockTriggerSolenoid);
        out.flush();
    }
    
    private static final byte[] lockLockoutSolenoid = new byte[] {'l','l','\r'};
    public final void lockLockoutSolenoid() throws IOException {
        out.write(lockLockoutSolenoid);
        out.flush();
    }
    
    private static final byte[] unlockLockoutSolenoid = new byte[] {'u', 'l', '\r'};
    public final void unlockLockoutSolenoid() throws IOException {
        out.write(unlockLockoutSolenoid);
        out.flush();
    }
    private static final byte[] setPowerLedOff = new byte[] {'p', 'l', 'x', '\r'};
    public final void setPowerLedOff() throws IOException {
        out.write(setPowerLedOff);
        out.flush();
    }
    private static final byte[] setPowerLedGreen = new byte[] {'p','l','g','\r'};
    public final void setPowerLedGreen() throws IOException {
        out.write(setPowerLedGreen);
        out.flush();
    }
    private static final byte[] setPowerLedRed = new byte[] {'p','l','r','\r'};
    public final void setPowerLedRed() throws IOException {
        out.write(setPowerLedRed);
        out.flush();
    }
    
    private static final byte[] setReadyLedOff = new byte[] {'r','l','x','\r'};
    public final void setReadyLedOff() throws IOException {
        out.write(setReadyLedOff);
        out.flush();
    }
    private static final byte[] setReadyLedGreen = new byte[] {'r','l','g','\r'};
    public final void setReadyLedGreen() throws IOException {
        out.write(setReadyLedGreen);
        out.flush();
    }
    private static final byte[] setReadyLedRed = new byte[] {'r','l','r','\r'};
    public final void setReadyLedRed() throws IOException {
        out.write(setReadyLedRed);
        out.flush();
    }
    
    public void receive() throws IOException {
        String line = null;
        while(null != (line = in.readLine())) {
//            System.out.println(line);
            if(12!=line.length()) {
                continue;
            }
            try {
                int loadCell = Integer.decode("0x"+line.substring(0, 4));
                boolean triggerSolenoidStatus = '1'==line.charAt(4);
                boolean lockoutSolenoidStatus = '1'==line.charAt(5);
                // skip 1
                byte switch_values = (byte) line.charAt(7);
                switch_values -= 0x30;
                boolean triggerSwitchStatus = 0 == (switch_values & 0x04);
                boolean tubeSwitchStatusIn = 0 == (switch_values & 0x02);
                boolean doorSwitchStatusShut = 0 == (switch_values & 0x01);
                
                int batteryLife = Integer.decode("0x"+line.substring(8, 12));
                
    
                
                receiveMessage(loadCell, triggerSolenoidStatus, lockoutSolenoidStatus, triggerSwitchStatus, tubeSwitchStatusIn, doorSwitchStatusShut, batteryLife);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        
    }
    public void receiveMessage(int loadCell, boolean triggerSolenoidStatus,
                               boolean lockoutSolenoidStatus, boolean triggerSwitchStatus,
                               boolean tubeSwitchStatusIn, boolean doorSwitchStatusShut,
                               int batteryLife) {
        
    }
    
}