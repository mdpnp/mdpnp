package org.mdpnp.devices.fhir;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.base.composite.BaseQuantityDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import ice.Numeric;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rosetta.MDC_DIM_DIMLESS;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author mfeinberg
 */
public abstract class FhirDevice extends AbstractConnectedDevice {

    private static final Logger log = LoggerFactory.getLogger(FhirDevice.class);

    public FhirDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);
    }

    protected abstract Set<Observation> getObservations(DeviceClock.Reading t) throws Exception;

    protected abstract long getSampleRateMs();

    protected DeviceClock.Reading getDeviceClockReading() {
        DeviceClock clock = getClockProvider();
        return clock.instant();
    }

    public static Observation createObservation(String metricId, Number value, Date asOf) {

        Observation obs = new Observation();
        obs.setValue(new QuantityDt(value.doubleValue()).setUnits(MDC_DIM_DIMLESS.VALUE).setCode(metricId).setSystem("OpenICE"));
        obs.setApplies(new DateTimeDt(asOf, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone("UTC")));
        obs.setStatus(ObservationStatusEnum.PRELIMINARY);

        return obs;
    }


    private final class DataPublisher implements Runnable {

        public DataPublisher() {
        }

        @Override
        public void run() {

            DeviceClock.Reading t = getDeviceClockReading();

            Set<Observation> data;
            try {
                data = getObservations(t);
            } catch (Exception ex) {
                log.warn("Provider failed to returned invalid list of observations for " + t, ex);
                data = Collections.emptySet();
            }

            if (data == null) {
                log.warn("Provider returned invalid (null) list of observations for " + t);
                data = Collections.emptySet();
            }
            publishObservations(t, data);
        }
    }

    private void publishObservations(DeviceClock.Reading t, Set<Observation> data) {

        log.info("Publishing " + data.size() + " observations");
        for (Observation obs : data) {
            try {
                ObservationConverter.NumericObservation ice = observationConvertor.observationOnIce(obs);
                numericSample(ice.holder, (float)ice.value, ice.time);
            } catch (Exception ex) {
                log.error("Failed to convert/publish observation " + obs, ex);
            }
        }
    }

    ObservationConverter observationConvertor  = new ObservationConverter() {
        InstanceHolder<Numeric> getInstanceHolderForCode(String code) {
            return FhirDevice.this.getInstanceHolderForCode(code);
        }
    };

    static abstract class ObservationConverter {

        static class NumericObservation {

            public NumericObservation(InstanceHolder<Numeric> holder, double value, DeviceClock.Reading time) {
                this.holder = holder;
                this.value = value;
                this.time = time;
            }

            final InstanceHolder<Numeric> holder;
            final double value;
            final DeviceClock.Reading time;
        }

        NumericObservation observationOnIce(Observation obs) {

            BaseQuantityDt bqdt = (BaseQuantityDt) obs.getValue();
            double value = bqdt.getValueElement().getValue().doubleValue();

            String code = bqdt.getCodeElement().getValue();
            InstanceHolder<Numeric> holder = getInstanceHolderForCode(code);

            log.info("Converting observation:" + code + "=" + value);

            DateTimeDt dt = (DateTimeDt) obs.getApplies();
            Date d = dt.getValue();
            DeviceClock.Reading clockReading = new DeviceClock.ReadingImpl(d.getTime());

            return new NumericObservation(holder, value, clockReading);
        }

        abstract InstanceHolder<Numeric> getInstanceHolderForCode(String code);
    }

    private synchronized InstanceHolder<Numeric> getInstanceHolderForCode(String code) {

        if(holders.containsKey(code))
            return holders.get(code);

        InstanceHolder<Numeric> holder = createNumericInstance(code, "");
        holders.put(code, holder);
        return holder;
    }

    private Map<String, InstanceHolder<Numeric>> holders = new HashMap<>();

    private ScheduledFuture<?> task;

    public boolean connect(String address) {
        ScheduledExecutorService exec = getExecutor();
        connect(exec);
        return true;
    }

    public void connect(ScheduledExecutorService executor) {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        long updatePriod = getSampleRateMs();
        long now = System.currentTimeMillis();
        task = executor.scheduleAtFixedRate(new DataPublisher(), updatePriod - now % updatePriod, updatePriod, TimeUnit.MILLISECONDS);
    }

    public void disconnect() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }
}
