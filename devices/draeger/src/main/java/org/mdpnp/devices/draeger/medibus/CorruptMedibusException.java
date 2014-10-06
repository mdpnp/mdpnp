package org.mdpnp.devices.draeger.medibus;

@SuppressWarnings("serial")
public class CorruptMedibusException extends java.io.IOException {
    public CorruptMedibusException(String message, Throwable t) {
        super(message, t);
    }
    
    public CorruptMedibusException(String message) {
        super(message);
    }
    
    public CorruptMedibusException(Throwable t) {
        super(t);
    }
}
