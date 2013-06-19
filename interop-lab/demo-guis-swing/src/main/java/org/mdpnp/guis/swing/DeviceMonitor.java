package org.mdpnp.guis.swing;

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.Numeric;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTypeSupport;
import ice.SampleArray;
import ice.SampleArrayDataReader;
import ice.SampleArraySeq;
import ice.SampleArrayTypeSupport;

import java.util.HashSet;
import java.util.Set;

import org.mdpnp.devices.EventLoop;
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
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

public class DeviceMonitor {
    private final Subscriber subscriber;
    private final Set<Condition> conditions = new HashSet<Condition>();
    private final Set<DataReader> dataReaders = new HashSet<DataReader>();
    private final EventLoop eventLoop;
    private static final Logger log = LoggerFactory.getLogger(DeviceMonitor.class);
    
    private final Condition c(Condition c) {
        conditions.add(c);
        return c;
    }
    
    public DeviceMonitor(DomainParticipant participant, String udi, final DeviceMonitorListener listener, final EventLoop eventLoop) {
        final StringSeq identity = new StringSeq();
        identity.add("'"+udi+"'");
        
        this.eventLoop = eventLoop;
        
        subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        TopicDescription deviceIdentityTopic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.class);
        TopicDescription deviceConnectivityTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
        TopicDescription deviceNumericTopic = lookupOrCreateTopic(participant, ice.NumericTopic.VALUE, NumericTypeSupport.class);
        TopicDescription deviceSampleArrayTopic = lookupOrCreateTopic(participant, ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.class);
        
        final DeviceIdentityDataReader idReader = (DeviceIdentityDataReader) subscriber.create_datareader(deviceIdentityTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final DeviceConnectivityDataReader connReader = (DeviceConnectivityDataReader) subscriber.create_datareader(deviceConnectivityTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final NumericDataReader numReader = (NumericDataReader) subscriber.create_datareader(deviceNumericTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final SampleArrayDataReader saReader = (SampleArrayDataReader) subscriber.create_datareader(deviceSampleArrayTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        dataReaders.add(idReader);
        dataReaders.add(connReader);
        dataReaders.add(numReader);
        dataReaders.add(saReader);
        
        final DeviceIdentitySeq id_seq = new DeviceIdentitySeq();
        final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
        final NumericSeq num_seq = new NumericSeq();
        final SampleArraySeq sa_seq = new SampleArraySeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();

        eventLoop.addHandler(c(idReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "universal_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        idReader.read_w_condition(id_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                DeviceIdentity di = (DeviceIdentity) id_seq.get(i);
                                listener.deviceIdentity(di, sampleInfo);
                            }
                        }
                        idReader.return_loan(id_seq, info_seq);
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
        });
        
        eventLoop.addHandler(c(connReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, 
                "universal_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        connReader.read_w_condition(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                DeviceConnectivity dc = (DeviceConnectivity) conn_seq.get(i);
                                listener.deviceConnectivity(dc, sampleInfo);
                            }
                        }
                        connReader.return_loan(conn_seq, info_seq);
                    }
                    
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
        });
        
        eventLoop.addHandler(c(numReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "universal_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        numReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                Numeric n = (Numeric) num_seq.get(i);
                                listener.numeric(n, sampleInfo);
                            } else {
                                Numeric n = (Numeric) Numeric.create();
                                numReader.get_key_value(n, sampleInfo.instance_handle);
                                log.debug("No valid_data:"+n);
                            }
                        }
                        numReader.return_loan(num_seq, info_seq);
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
        });
        
        
        eventLoop.addHandler(c(saReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE,
                "universal_device_identifier = %0", identity)), new EventLoop.ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for(;;) {
                        saReader.read_w_condition(sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(sampleInfo.valid_data) {
                                SampleArray sa = (SampleArray) sa_seq.get(i);
                                listener.sampleArray(sa, sampleInfo);
                            }
                        }
                        saReader.return_loan(sa_seq, info_seq);
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                } finally {
                    
                }
            }
        });
        
//        eventLoop.addHandler(reader.create_querycondition(arg0, arg1, arg2, arg3, arg4), conditionHandler)
        log.debug("started a DeviceMonitor");

    }

    public void shutdown() {
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
        
        subscriber.get_participant().delete_subscriber(subscriber);
        log.debug("Shut down a DeviceMonitor");
    }
}
