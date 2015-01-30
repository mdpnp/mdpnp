package org.mdpnp.apps.testapp.export;


import com.rti.dds.subscription.SampleInfo;
import ice.Numeric;
import org.junit.Test;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.ValueImpl;
import org.mdpnp.apps.testapp.vital.Vital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerilogVCDPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(VerilogVCDPersisterTest.class);

    @Test
    public void testPersistValue() throws Exception {

        CSVPersister p = new CSVPersister();
        p.start();

        long now = System.currentTimeMillis()/1000;

        Vital vital = new MockedVital();

        SampleInfo si = new SampleInfo();
        for(int i=0; i<20; i++) {
            Value v =  new ValueImpl("DEVICE0", "METRIC0", 0, vital);
            Numeric n = new Numeric();
            n.value = (float)Math.sin(i);
            n.device_time = new ice.Time_t();
            n.device_time.sec = (int)(now + i);

            v.updateFrom(n, si);
            p.persist(v);
        }
    }
}
