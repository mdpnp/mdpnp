package org.mdpnp.devices.fhir;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import ice.ConnectionType;
import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JsFhirDevice extends FhirDevice {

    private static final Logger log = LoggerFactory.getLogger(JsFhirDevice.class);

    private Invocable invocable;

    public JsFhirDevice(String jsImpl, Subscriber subscriber, Publisher publisher, EventLoop eventLoop)  throws Exception {

        super(subscriber, publisher, eventLoop);

        initJSRuntime(jsImpl);

        populateIdentity(deviceIdentity, deviceConnectivity);
        writeDeviceIdentity();

    }

    private final void initJSRuntime(String jsFile) throws ScriptException {

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        InputStream is = getClass().getResourceAsStream(jsFile);

        engine.eval(new InputStreamReader(is));

        invocable = (Invocable) engine;
    }

    protected void populateIdentity(DeviceIdentity deviceIdentity, DeviceConnectivity deviceConnectivity) throws Exception {

        ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("getDeviceId");
        deviceIdentity.unique_device_identifier = (String)result.get("unique_device_identifier");
        deviceIdentity.manufacturer             = (String)result.get("manufacturer");
        deviceIdentity.model                    = (String)result.get("model");
        deviceIdentity.serial_number            = (String)result.get("serial_number");

        deviceConnectivity.type = getConnectionType();
    }


    protected Set<Observation> getObservations(DeviceClock.Reading t) throws Exception {

        Date asOf = new Date(t.getTime().toEpochMilli());
        Set<Observation> set = new HashSet<>();

        ScriptObjectMirror result = (ScriptObjectMirror) invocable.invokeFunction("readObservations");
        if(result.isArray()) {
            Set<String> keys = result.keySet();
            for(String key: keys) {
                ScriptObjectMirror o = (ScriptObjectMirror) result.get(key);
                String code = (String) ((ScriptObjectMirror) o.get("valueQuantity")).get("code");
                Number value = (Number) ((ScriptObjectMirror) o.get("valueQuantity")).get("value");
                set.add(createObservation(code, value.doubleValue(), asOf));
            }
        }
        return set;
    }

    @Override
    protected long getSampleRateMs() {

        if(invocable == null)
            throw new IllegalStateException("Need to init JS runtime first to get sample rate");

        try {
            Number value = (Number) invocable.invokeFunction("getSampleRateMs");
            return value.longValue();
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected final ConnectionType getConnectionType() {

        // The default 'super' implementation calls this in the ctor before we get a chance to
        // init the runtime. We return null - seems to be harmless - and the init the field manually
        // later
        //
        if(invocable == null)
            return null;

        try {
            String value = (String) invocable.invokeFunction("getConnectionType");
            ConnectionType ct = (ConnectionType)ConnectionType.valueOf(ConnectionType.class, value);
            return ct;
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void shutdown() {
        try {
            invocable.invokeFunction("stop");
        } catch (Exception ex) {
            log.error("Failed to shutdown javascript engine ", ex);
        }
        finally {
            super.shutdown();
        }
    }

    @Override
    public boolean connect(String address) {

        try {
            invocable.invokeFunction("start", address);
        } catch (Exception ex) {
            log.error("Failed to start javascript engine ", ex);
            throw new RuntimeException(ex);
        }
        return super.connect("");
    }
}

