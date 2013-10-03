package org.mdpnp.devices;

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTypeSupport;
import ice.SampleArrayDataReader;
import ice.SampleArraySeq;
import ice.SampleArrayTypeSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

public class DeviceMonitor {
    private Subscriber subscriber;
    private final Set<Condition> conditions = new HashSet<Condition>();
    private final Set<DataReader> dataReaders = new HashSet<DataReader>();
    private EventLoop eventLoop;
    private static final Logger log = LoggerFactory.getLogger(DeviceMonitor.class);

    private final String udi;


    private final List<DeviceMonitorListener> listeners = new ArrayList<DeviceMonitorListener>();
    private ThreadLocal<DeviceMonitorListener[]> simpleListeners = new ThreadLocal<DeviceMonitorListener[]>() {
        protected DeviceMonitorListener[] initialValue() {
            return new DeviceMonitorListener[0];
        }
    };
    private final synchronized DeviceMonitorListener[] getListeners() {
        DeviceMonitorListener[] listeners;
        if(null != this.simpleListeners) {
            simpleListeners.set(listeners = this.listeners.toArray(simpleListeners.get()));
        } else {
            listeners = new DeviceMonitorListener[0];
        }
        return listeners;
    }

    public final synchronized void addListener(DeviceMonitorListener listener) {
        this.listeners.add(listener);
    }
    public final synchronized void removeListener(DeviceMonitorListener listener) {
        this.listeners.remove(listener);
    }



    private final Condition c(Condition c) {
        conditions.add(c);
        return c;
    }

    public String getUniqueDeviceIdentifier() {
        return udi;
    }

    public DeviceMonitor(final String udi) {
        this.udi = udi;
    }

    public void start(final DomainParticipant participant, final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        eventLoop.doLater(new Runnable() {
            public void run() {
                _start(participant, eventLoop);
            }
        });
    }

    protected void _start(final DomainParticipant participant, final EventLoop eventLoop) {
        subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        TopicDescription deviceIdentityTopic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.class);
        TopicDescription deviceConnectivityTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
        TopicDescription deviceNumericTopic = lookupOrCreateTopic(participant, ice.NumericTopic.VALUE, NumericTypeSupport.class);
        TopicDescription deviceSampleArrayTopic = lookupOrCreateTopic(participant, ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.class);
        TopicDescription deviceInfusionStatusTopic = lookupOrCreateTopic(participant, ice.InfusionStatusTopic.VALUE, ice.InfusionStatusTypeSupport.class);


        final DeviceIdentityDataReader idReader = (DeviceIdentityDataReader) subscriber.create_datareader(deviceIdentityTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final DeviceConnectivityDataReader connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(deviceConnectivityTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final NumericDataReader numReader = (NumericDataReader) subscriber.create_datareader(deviceNumericTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final SampleArrayDataReader saReader = (SampleArrayDataReader) subscriber.create_datareader(deviceSampleArrayTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final ice.InfusionStatusDataReader ipReader = (ice.InfusionStatusDataReader) subscriber.create_datareader(deviceInfusionStatusTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        dataReaders.add(idReader);
        dataReaders.add(connReader);
        dataReaders.add(numReader);
        dataReaders.add(saReader);
        dataReaders.add(ipReader);

        final StringSeq identity = new StringSeq();
        identity.add("'"+udi+"'");

        final DeviceIdentitySeq id_seq = new DeviceIdentitySeq();
        final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
        final NumericSeq num_seq = new NumericSeq();
        final SampleArraySeq sa_seq = new SampleArraySeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        final ice.InfusionStatusSeq inf_seq = new ice.InfusionStatusSeq();

        eventLoop.addHandler(c(idReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "unique_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        try {
                            idReader.read_w_condition(id_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                            for(DeviceMonitorListener listener : getListeners()) {
                                listener.deviceIdentity(idReader, id_seq, info_seq);
                            }
                        } finally {
                            idReader.return_loan(id_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(connReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "unique_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        try {
                            connReader.read_w_condition(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                            for(DeviceMonitorListener listener : getListeners()) {
                                listener.deviceConnectivity(connReader, conn_seq, info_seq);
                            }
                        } finally {
                            connReader.return_loan(conn_seq, info_seq);
                        }
                    }

                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(numReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "unique_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        try {
                            numReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                            for(DeviceMonitorListener listener : getListeners()) {
                                listener.numeric(numReader, num_seq, info_seq);
                            }
                        } finally {
                            numReader.return_loan(num_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });


        eventLoop.addHandler(c(saReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "unique_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        try {
                            saReader.read_w_condition(sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                            for(DeviceMonitorListener listener : getListeners()) {
                                listener.sampleArray(saReader, sa_seq, info_seq);
                            }
                        } finally {
                            saReader.return_loan(sa_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(ipReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0",  identity)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                for(;;) {
                    try {
                        ipReader.read_w_condition(inf_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(DeviceMonitorListener listener : getListeners()) {
                            listener.infusionPump(ipReader, inf_seq, info_seq);
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        ipReader.return_loan(inf_seq, info_seq);
                    }
                }
            }

        });
    }

    public void stop() {
        eventLoop.doLater(new Runnable() {
            public void run() {
                _stop();
            }
        });
    }

    protected void _stop() {
        synchronized(this) {
            listeners.clear();
            simpleListeners = null;
        }
        for(Condition c : conditions) {
            eventLoop.removeHandler(c);
            if(c instanceof ReadCondition) {
                ((ReadCondition)c).get_datareader().delete_readcondition((ReadCondition)c);
            }
        }
        conditions.clear();
        for(DataReader r : dataReaders) {
            subscriber.delete_datareader(r);
        }
        dataReaders.clear();

        if(null != subscriber) {
            DomainParticipant participant = subscriber.get_participant();
            if(null != participant) {
                participant.delete_subscriber(subscriber);
                subscriber = null;
            } else {
                log.warn("participant is null");
            }
        } else {
            log.warn("subscriber is null");
        }
        log.debug("Shut down a DeviceMonitor");
        this.eventLoop = null;
    }
}
