package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class DeviceAlertCondition implements Value {
    private final AlertState deviceAlertState = new AlertState();
    private int alStatChgCount;
    private final AlertType maxPatientAlarm = new AlertType();
    private final AlertType maxTechnicalAlarm = new AlertType();
    private final AlertType maxAudAlarm = new AlertType();

    @Override
    public void format(ByteBuffer bb) {
        deviceAlertState.format(bb);
        Bits.putUnsignedShort(bb, alStatChgCount);
        maxPatientAlarm.format(bb);
        maxTechnicalAlarm.format(bb);
        maxAudAlarm.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        deviceAlertState.parse(bb);
        alStatChgCount = Bits.getUnsignedShort(bb);
        maxPatientAlarm.parse(bb);
        maxTechnicalAlarm.parse(bb);
        maxAudAlarm.parse(bb);
    }

    @Override
    public java.lang.String toString() {
        return "[deviceAlertState="+deviceAlertState+",alStatChgCount="+alStatChgCount+",maxPatientAlarm="+maxPatientAlarm+",maxTechnicalAlarm="+maxTechnicalAlarm+",maxAudAlarm="+maxAudAlarm+"]";
    }

    public AlertState getDeviceAlertState() {
        return deviceAlertState;
    }

    public int getAlStatChgCount() {
        return alStatChgCount;
    }
    public AlertType getMaxAudAlarm() {
        return maxAudAlarm;
    }
    public AlertType getMaxPatientAlarm() {
        return maxPatientAlarm;
    }
    public AlertType getMaxTechnicalAlarm() {
        return maxTechnicalAlarm;
    }
}
