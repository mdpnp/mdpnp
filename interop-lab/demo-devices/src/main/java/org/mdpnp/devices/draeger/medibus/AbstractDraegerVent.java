package org.mdpnp.devices.draeger.medibus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.draeger.medibus.Medibus.Alarm;
import org.mdpnp.devices.draeger.medibus.Medibus.Data;
import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.MeasuredDataCP1;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDraegerVent extends AbstractDelegatingSerialDevice<RTMedibus> {

    private static final Logger log = LoggerFactory.getLogger(AbstractDraegerVent.class);

    private Map<Enum<?>, Integer> numerics = new HashMap<Enum<?>, Integer>();
    private Map<Enum<?>, Integer> waveforms = new HashMap<Enum<?>, Integer>();
    
    protected Map<Enum<?>, InstanceHolder<ice.Numeric>> numericUpdates = new HashMap<Enum<?>, InstanceHolder<ice.Numeric>>();
    protected Map<Enum<?>, InstanceHolder<ice.SampleArray>> sampleArrayUpdates = new HashMap<Enum<?>, InstanceHolder<ice.SampleArray>>();

    protected InstanceHolder<ice.Numeric> startInspiratoryCycleUpdate;
    protected InstanceHolder<ice.Numeric> timeUpdate;

    protected InstanceHolder<ice.Numeric> doNumericUpdate(InstanceHolder<ice.Numeric> update, Object value, int name) {
        try {
            // TODO There are weird number formats in medibus .. this will need
            // enhancement
            if (value instanceof Number) {
                return numericSample(update, ((Number) value).floatValue(), name);
            } else {
                String s = null == value ? null : value.toString().trim();
                if (null != s) {
                    return numericSample(update, Float.parseFloat(s), name);
                } else {
                    return numericSample(update, (Float) null, name);
                }
            }

        } catch (NumberFormatException nfe) {
            log.trace("Invalid number:" + value);
            return numericSample(update, (Float) null, name);
        }
    }

    protected void processStartInspCycle() {
        // TODO This should not be triggered as a numeric; it's a bad idea
        startInspiratoryCycleUpdate = numericSample(startInspiratoryCycleUpdate, 0, ice.MDC_START_OF_BREATH.VALUE);
    }

    private static final int BUFFER_SAMPLES = 10;

    // Theoretical maximum 16 streams, practical limit seems to be 3
    // Buffering ten points is for testing, size of this buffer might be
    // a function of the sampling rate
    private final Number[][] realtimeBuffer = new Number[16][BUFFER_SAMPLES];
    private final int[] realtimeBufferCount = new int[16];
    private long lastRealtime;

    private final Date date = new Date();

    protected void processRealtime(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object code,
            double value) {
        lastRealtime = System.currentTimeMillis();
        if (streamIndex >= realtimeBuffer.length) {
            log.warn("Invalid realtime streamIndex=" + streamIndex);
            return;
        }
        realtimeBuffer[streamIndex][realtimeBufferCount[streamIndex]++] = value;
        if (realtimeBufferCount[streamIndex] == realtimeBuffer[streamIndex].length) {
            realtimeBufferCount[streamIndex] = 0;
            
            // flush
            if (code instanceof Enum<?>) {
                Integer name = waveforms.get(code);
                if(null != name) {
                    sampleArrayUpdates.put((Enum<?>)code, sampleArraySample(sampleArrayUpdates.get(code), realtimeBuffer[streamIndex], (int) (1.0 * config.interval * multiplier / 1000.0), name));
                } else {
                    log.trace("No nomenclature code for enum code=" + code + " class=" + code.getClass().getName());
                }
            } else {
                log.trace("No enumerated type for code=" + code + " class=" + code.getClass().getName());
            }
        }
    }
    
    @Override
    protected void unregisterAllNumericInstances() {
        super.unregisterAllNumericInstances();
        numericUpdates.clear();
    }
    
    @Override
    protected void unregisterAllSampleArrayInstances() {
        super.unregisterAllSampleArrayInstances();
        sampleArrayUpdates.clear();
    }

    protected void process(Object code, Object data) {
        if (code instanceof Enum<?>) {
            Integer name = numerics.get(code);
            if(null != name) {
                numericUpdates.put((Enum<?>)code, doNumericUpdate(numericUpdates.get(code), data, name));
            } else {
                log.trace("No nomenclature code for enum code=" + code + " class=" + code.getClass().getName());
            }
        } else {
            log.trace("No enumerated type for code=" + code + " class=" + code.getClass().getName());
        }
    }

    protected void process(Data d) {
        process(d.code, d.data);
    }

    protected void process(Alarm a) {
        process(a.alarmCode, a.alarmPhrase);
    }

    protected void process(Alarm[] alarms) {
        for (Alarm a : alarms) {
            process(a);
        }
    }

    protected void process(Date date) {
        // TODO Don't do this
        timeUpdate = numericSample(timeUpdate, (int)date.getTime(), ice.MDC_TIME_MSEC_SINCE_EPOCH.VALUE);
    }

    protected void processCorrupt() {
    }

    protected void process(Data[] data, int n) {
        for (int i = 0; i < n; i++) {
            process(data[i]);
        }
    }

    private class MyRTMedibus extends RTMedibus {
        public MyRTMedibus(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        protected void receiveValidResponse(Object cmdEcho, byte[] response, int len) {
            super.receiveValidResponse(cmdEcho, response, len);
            if(Command.InitializeComm.equals(cmdEcho)) {
                initializeCommAcknowledged();
            }
        }
        
        @Override
        protected void receiveDeviceIdentification(String idNumber, String name, String revision) {
            receiveDeviceId(idNumber, name);
        }

        @Override
        protected void receiveTextMessage(Data[] data, int n) {
            process(data, n);
        }

        @Override
        protected void receiveDeviceSetting(Data[] data, int n) {
            process(data, n);
        }

        @Override
        protected void receiveMeasuredData(Data[] data, int n) {
            process(data, n);
        }

        @Override
        protected void receiveCorruptResponse() {
            processCorrupt();
        }

        @Override
        public void receiveDataValue(RTMedibus.RTDataConfig config, int multiplier, int streamIndex,
                Object realtimeData, double data) {
            processRealtime(config, multiplier, streamIndex, realtimeData, data);
        }

        @Override
        protected void receiveAlarms(Alarm[] alarms) {
            process(alarms);
        }

        @Override
        protected void receiveDateTime(Date date) {
            process(date);
        }

        @Override
        public void startInspiratoryCycle() {
            processStartInspCycle();
        }

    }

    private static final Command[] REQUEST_COMMANDS = {
             Command.ReqDateTime,
            Command.ReqDeviceSetting,
//             Command.ReqAlarmsCP1,
            Command.ReqMeasuredDataCP1,
    // Command.ReqAlarmsCP2,
    // Command.ReqMeasuredDataCP2,
    // Command.ReqTextMessages
    };

    private class RequestSlowData implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    if ((System.currentTimeMillis() - lastRealtime) >= getMaximumQuietTime()) {
                        log.warn("" + (System.currentTimeMillis() - lastRealtime)
                                + "ms since realtime data, requesting anew");

                        if (!getDelegate().enableRealtime(RealtimeData.AirwayPressure,
                                RealtimeData.FlowInspExp, RealtimeData.ExpiratoryCO2mmHg, RealtimeData.O2InspExp)) {
                            log.debug("timed out waiting to issue enableRealtime");
                        }
                    }

                    RTMedibus medibus = AbstractDraegerVent.this.getDelegate();
                    for (Command c : REQUEST_COMMANDS) {
                        medibus.sendCommand(c);
                        Thread.sleep(200L);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
    }

    @Override
    public void disconnect() {
        stopRequestSlowData();
        RTMedibus medibus = null;
        synchronized (this) {
            medibus = getDelegate(false);
        }
        if (null != medibus) {
            try {
                medibus.sendCommand(Command.StopComm);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("rtMedibus was already null in disconnect");
        }
        super.disconnect();
    }

    private void init() {
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentity.manufacturer = "Dr\u00E4ger";
        deviceIdentity.model = "???";
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
    }

    private static void loadMap(Map<Enum<?>, Integer> numerics, Map<Enum<?>, Integer> waveforms) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    AbstractDraegerVent.class.getResourceAsStream("draeger.map")));
            String line = null;
            String draegerPrefix = MeasuredDataCP1.class.getPackage().getName() + ".";

            while (null != (line = br.readLine())) {
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length < 3) {
                        log.debug("Bad line:" + line);
                    } else {
                        String c[] = v[0].split("\\.");
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        Enum<?> draeger = (Enum<?>) Enum.valueOf(
                                (Class<? extends Enum>) Class.forName(draegerPrefix + c[0]), c[1]);
                        Integer tag = getValue(v[1]);
                        if(tag == null) {
                            log.warn("cannot find value for " + v[1]);
                            continue;
                        }
                        log.trace("Adding " + draeger + " mapped to " + tag);
                        v[2] = v[2].trim();
                        if ("W".equals(v[2])) {
                            waveforms.put(draeger, tag);
                        } else if ("N".equals(v[2])) {
                            numerics.put(draeger, tag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ScheduledFuture<?> requestSlowData;

    private synchronized void stopRequestSlowData() {
        if (null != requestSlowData) {
            requestSlowData.cancel(false);
            requestSlowData = null;
            log.trace("Canceled slow data request task");
        } else {
            log.trace("Slow data request already canceled");
        }
    }

    private synchronized void startRequestSlowData() {
        if (null == requestSlowData) {
            requestSlowData = executor
                    .scheduleWithFixedDelay(new RequestSlowData(), 2000L, 500L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled slow data request task");
        } else {
            log.trace("Slow data request already scheduled");
        }
    }

    private static Integer getValue(String name) throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException {
        try {
            Class<?> cls = Class.forName(name);
            return cls.getField("VALUE").getInt(null);
        } catch (ClassNotFoundException e) {
            // If it's not a class then maybe it's a static member
            int lastIndexOfDot = name.lastIndexOf('.');
            if (lastIndexOfDot < 0) {
                return null;
            }
            Class<?> cls = Class.forName(name.substring(0, lastIndexOfDot));
            Object obj = cls.getField(name.substring(lastIndexOfDot + 1, name.length())).get(null);
            return (Integer) obj.getClass().getMethod("value").invoke(obj);

        }

    }

    public static void main(String[] args) {
        Map<Enum<?>, Integer> numerics = new HashMap<Enum<?>, Integer>();
        Map<Enum<?>, Integer> waveforms = new HashMap<Enum<?>, Integer>();
        loadMap(numerics, waveforms);
        System.out.println(numerics);
        System.out.println(waveforms);
    }

    public AbstractDraegerVent(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        init();
        loadMap(numerics, waveforms);
    }

    public AbstractDraegerVent(int domainId, EventLoop eventLoop, SerialSocket serialSocket) {
        super(domainId, eventLoop, serialSocket);
        init();
        loadMap(numerics, waveforms);
    }

    @Override
    protected RTMedibus buildDelegate(InputStream in, OutputStream out) {
        log.trace("Creating an RTMedibus");
        return new MyRTMedibus(in, out);
    }

    @Override
    protected boolean delegateReceive(RTMedibus delegate) throws IOException {
        return delegate.receive();
    }

    protected synchronized void receiveDeviceId(String guid, String name) {
        log.trace("receiveDeviceId:guid=" + guid + ", name=" + name);

        boolean writeIt = false;
        if (null != guid) {
            deviceIdentity.serial_number = guid;
            writeIt = true;

        }
        if (null != name) {
            deviceIdentity.model = name;
            writeIt = true;
        }
        if (writeIt) {
            deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
        }
        startRequestSlowData();
        reportConnected();
    }

    @Override
    protected void doInitCommands() throws IOException {
        super.doInitCommands();
        RTMedibus rtMedibus = getDelegate();

        rtMedibus.sendCommand(Command.InitializeComm);
    }

    protected void initializeCommAcknowledged() {
        try {
            getDelegate().sendCommand(Command.ReqDeviceId);
        } catch(IOException ioe) {
            log.error("Unable to request device id", ioe);
        }
    }
    
    @Override
    protected long getMaximumQuietTime() {
        return 1000L;
    }

    @Override
    protected long getConnectInterval() {
        return 1000L;
    }
    
    @Override
    protected void process(InputStream inputStream, OutputStream outputStream) throws IOException {

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    // Will block until the delegate is available
                    final RTMedibus rtMedibus = getDelegate();
                    rtMedibus.receiveFast();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Medibus FAST data");
        t.setDaemon(true);
        t.start();
        log.trace("spawned a fast data processor");

        // really the RTMedibus thread will block until
        // the super.process populates an InputStream to allow
        // building of the delegate
        super.process(inputStream, outputStream);

    }

}
