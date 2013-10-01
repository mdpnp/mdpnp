package org.mdpnp.devices.hospira.symbiq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoSymbiq extends SimInfusionPump {

    private final File pumpControlFile = new File("C:\\pump_control_1.txt");

    @Override
    protected void writeIdentity() {
        deviceIdentity.manufacturer = "Hospira";
        deviceIdentity.model = "Symbiq";
        deviceIdentity.serial_number = "xxx";
        writeDeviceIdentity();
    }
    private static final Logger log = LoggerFactory.getLogger(DemoSymbiq.class);

    private Boolean lastStopThePumpWritten = null;


    @Override
    protected void stopThePump(boolean stopThePump) {
        super.stopThePump(stopThePump);
        if(null == lastStopThePumpWritten || (stopThePump ^ lastStopThePumpWritten)) {


            Writer w;
            try {
                w = new FileWriter(pumpControlFile);
                if(stopThePump) {
                    w.write("Stop, \n");
                } else {
                    w.write("Start, 10\n");
                }
                w.close();
                lastStopThePumpWritten = stopThePump;
            } catch (IOException e) {
                log.error("can't stop", e);
            }

        }

    }

    public DemoSymbiq(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

    }
    @Override
    protected String iconResourceName() {
        return "symbiq.png";
    }

}
