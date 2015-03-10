package org.mdpnp.devices.ivy._450c;


import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.cpc.ansarB.AnsarB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DemoIvy450CTest {

    private static final Logger log = LoggerFactory.getLogger(DemoIvy450CTest.class);

    @Test
    public void testAnsarBRead() throws Exception {

        final ArrayList l = new ArrayList();

        DeviceClock.WallClock clock = new DeviceClock.WallClock();

        InputStream is = getClass().getResourceAsStream("Ivy450C.1.data");
        AnsarB dev = new AnsarB(clock, is, null) {
            @Override
            public boolean receiveMessage(byte[] message, int off, int len) throws IOException {
                l.add(new Object());
                return super.receiveMessage(message, off, len);
            }
            @Override
            protected void receiveLine(DeviceClock.Reading timeStamp, String line) {
                log.info(line);
                super.receiveLine(timeStamp, line);
            }
        };
        dev.receive();

        Assert.assertEquals("Invalid number of messages parsed", 27, l.size());

    }
}
