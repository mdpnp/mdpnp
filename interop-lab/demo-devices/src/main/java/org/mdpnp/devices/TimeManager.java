package org.mdpnp.devices;

import ice.HeartBeat;
import ice.HeartBeatDataReader;
import ice.HeartBeatDataWriter;
import ice.TimeSync;
import ice.TimeSyncDataReader;
import ice.TimeSyncDataWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;



public class TimeManager implements Runnable {
    private final Subscriber subscriber;
    private final Publisher publisher;
    protected final String uniqueDeviceIdentifier, type;
    
    private ice.HeartBeatDataWriter hbWriter;
    private ice.HeartBeatDataReader hbReader;
    private ice.TimeSyncDataWriter tsWriter;
    private ice.TimeSyncDataReader tsReader;
    private Topic hbTopic, tsTopic;
    private boolean ownHbTopic, ownTsTopic;
    private ContentFilteredTopic cfHbTopic, cfTsTopic;
    private ReadCondition hbReadCond;
    private ReadCondition tsReadCond;
    private final GuardCondition guardCondition = new  GuardCondition();
    
    private InstanceHandle_t hbHandle;
    private final ice.HeartBeat hbData = (HeartBeat) ice.HeartBeat.create();
    
    private Thread thread;
    private boolean stop = false;
    
    private Map<InstanceHandle_t, ice.HeartBeat> heartbeats = new java.util.concurrent.ConcurrentHashMap<>();
    
    private static final Logger log = LoggerFactory.getLogger(TimeManager.class);
    
    private static final class TimeSyncHolder {
        public final TimeSync timeSync;
        public final InstanceHandle_t handle;
        
        public TimeSyncHolder(final TimeSync timeSync, final InstanceHandle_t handle) {
            this.timeSync = timeSync;
            this.handle = handle;
        }
    }
    
    public void addListener(TimeManagerListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(TimeManagerListener listener) {
        this.listeners.remove(listener);
    }
    
    private final Map<String,TimeSyncHolder> sync = new HashMap<String, TimeSyncHolder>();
    
    public TimeManager(DomainParticipant participant, String uniqueDeviceIdentifier, String type) {
        this.subscriber = participant.get_implicit_subscriber();
        this.publisher = participant.get_implicit_publisher();
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.type = type;
    }
    
    public TimeManager(Publisher publisher, Subscriber subscriber, String uniqueDeviceIdentifier, String type) {
        if(!publisher.get_participant().equals(subscriber.get_participant())) {
            throw new RuntimeException("publisher and subscriber must be from the same participant");
        }
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.type = type;
    }
    
    public void start() {
        DomainParticipant participant = publisher.get_participant();
        
        
        
        hbTopic = (Topic) participant.lookup_topicdescription(ice.HeartBeatTopic.VALUE);
        if(null == hbTopic) {
            ownHbTopic = true;
            ice.HeartBeatTypeSupport.register_type(participant, ice.HeartBeatTypeSupport.get_type_name());
            hbTopic = participant.create_topic(ice.HeartBeatTopic.VALUE, ice.HeartBeatTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        }
        tsTopic = (Topic) participant.lookup_topicdescription(ice.TimeSyncTopic.VALUE);
        if(null == tsTopic) {
            ownTsTopic = true;
            ice.TimeSyncTypeSupport.register_type(participant, ice.TimeSyncTypeSupport.get_type_name());
            tsTopic = participant.create_topic(ice.TimeSyncTopic.VALUE, ice.TimeSyncTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        }
        StringSeq params = new StringSeq();
        params.add("'"+uniqueDeviceIdentifier+"'");
        cfHbTopic = participant.create_contentfilteredtopic("CF"+ice.HeartBeatTopic.VALUE, hbTopic, "unique_device_identifier <> %0", params);
        cfTsTopic = participant.create_contentfilteredtopic("CF"+ice.TimeSyncTopic.VALUE, tsTopic, "heartbeat_source = %0", params);

        hbReader = (HeartBeatDataReader) subscriber.create_datareader_with_profile(cfHbTopic, QosProfiles.ice_library, QosProfiles.heartbeat, null, StatusKind.STATUS_MASK_NONE);
        hbWriter = (HeartBeatDataWriter) publisher.create_datawriter_with_profile(hbTopic, QosProfiles.ice_library, QosProfiles.heartbeat, null, StatusKind.STATUS_MASK_NONE);

        hbData.unique_device_identifier = uniqueDeviceIdentifier;
        hbData.type = type;
        hbHandle = hbWriter.register_instance(hbData);
        
        tsReader = (TimeSyncDataReader) subscriber.create_datareader_with_profile(cfTsTopic, QosProfiles.ice_library, QosProfiles.timesync, null, StatusKind.STATUS_MASK_NONE);
        tsWriter = (TimeSyncDataWriter) publisher.create_datawriter_with_profile(tsTopic, QosProfiles.ice_library, QosProfiles.timesync, null, StatusKind.STATUS_MASK_NONE);

        
        hbReadCond = hbReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

        tsReadCond = tsReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
        
        thread = new Thread(this, "TimeManager");
        thread.setDaemon(true);
        thread.start();
    }
    
    public void stop() {
        if(null != thread) {
            stop = true;
            guardCondition.set_trigger_value(true);
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        hbReader.delete_readcondition(hbReadCond);
        tsReader.delete_readcondition(tsReadCond);
        
        hbWriter.unregister_instance(hbData, hbHandle);
        
        for(TimeSyncHolder holder : sync.values()) {
            tsWriter.unregister_instance(holder.timeSync, holder.handle);
        }
        sync.clear();
        
        publisher.delete_datawriter(tsWriter);
        publisher.delete_datawriter(hbWriter);
        
        subscriber.delete_datareader(hbReader);
        subscriber.delete_datareader(tsReader);
        
        DomainParticipant participant = publisher.get_participant();
        
        participant.delete_contentfilteredtopic(cfHbTopic);
        participant.delete_contentfilteredtopic(cfTsTopic);
        
        if(ownHbTopic) {
            participant.delete_topic(hbTopic);
            ice.HeartBeatTypeSupport.unregister_type(participant, ice.HeartBeatTypeSupport.get_type_name());
        }
        if(ownTsTopic) {
            participant.delete_topic(tsTopic);
            ice.TimeSyncTypeSupport.unregister_type(participant, ice.TimeSyncTypeSupport.get_type_name());
        }
        
        
    }
    
    private static final void elapsedTime(Time_t t1, Time_t t2, Duration_t elapsed) {
        elapsed.sec = t2.sec - t1.sec;
        elapsed.nanosec = t2.nanosec - t1.nanosec;
        
       normalize(elapsed);
    }
    
    private static final void normalize(Duration_t d) {
     // Normalize to a positive nanoseconds
        while(d.nanosec<0) {
            d.nanosec+=1000000000;
            d.sec-=1;
        }
    }
    
    private static final void elapsedTime(ice.Time_t t1, Time_t t2, Duration_t elapsed) {
        elapsed.sec = t2.sec - t1.sec;
        elapsed.nanosec = t2.nanosec - t1.nanosec;
        
        normalize(elapsed);
    }
    
    private static final void elapsedTime(ice.Time_t t1, ice.Time_t t2, Duration_t elapsed) {
        elapsed.sec = t2.sec - t1.sec;
        elapsed.nanosec = t2.nanosec - t1.nanosec;
        
        normalize(elapsed);
    }
    
    private static final int compare(Duration_t d1, Duration_t d2) {
        int d_sec = d1.sec - d2.sec;
        int d_nanosec = d1.nanosec - d2.nanosec;

        if(d_sec == 0) {
            if(d_nanosec == 0) {
                return 0;
            } else if(d_nanosec<0) {
                return -1;
            } else {
                return 1;
            }
        } else if(d_sec<0){
            return -1;
        } else {
            return 1;
        }
    }
    
    private static final void divide(Duration_t d, int x) {
        int remain = d.sec % x;
        d.sec /= x;
        d.nanosec /= x;
        d.nanosec += (int)(remain / (double) x * 1000000000.0);
        normalize(d);
    }
    
    private static final void subtract(Duration_t d1, Duration_t d2) {
        d1.sec -= d2.sec;
        d1.nanosec -= d2.nanosec;
        normalize(d1);
    }
    
    private final List<TimeManagerListener> listeners = new java.util.concurrent.CopyOnWriteArrayList<>();
    
    private static final Duration_t HEARTBEAT_INTERVAL = new Duration_t(2,0);
    
    protected void processAliveHeartbeat(SampleInfo sampleInfo, ice.HeartBeat heartbeat) {
        for(TimeManagerListener listener : listeners) {
            listener.aliveHeartbeat(sampleInfo, heartbeat);
        }
    }
    
    protected void processNotAliveHeartbeat(SampleInfo sampleInfo, ice.HeartBeat heartbeat) {
        for(TimeManagerListener listener : listeners) {
            listener.notAliveHeartbeat(sampleInfo, heartbeat);
        }
    }
    
    protected void processSynchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
        for(TimeManagerListener listener : listeners) {
            listener.synchronization(remote_udi, latency, clockDifference);
        }
    }

    public void run() {
        final WaitSet waitSet = new WaitSet();
        final Duration_t timeout = new Duration_t();
        final ConditionSeq condSeq = new ConditionSeq();
        final DomainParticipant participant = publisher.get_participant();
        final Time_t now = new Time_t(0,0);
        final Time_t lastHeartbeatEmitted = new Time_t(0,0);
        final Duration_t sinceLastHeartbeat = new Duration_t();
        final ice.HeartBeatSeq hb_seq = new ice.HeartBeatSeq();
        final ice.TimeSyncSeq ts_seq = new ice.TimeSyncSeq();
        final SampleInfoSeq sa_seq = new SampleInfoSeq();
        
        final Duration_t remoteProcessingTime = new Duration_t();
        final Duration_t totalRoundtripTime = new Duration_t();
        final Duration_t latencyTime = new Duration_t();
        final Duration_t clockDifference = new Duration_t();
        
        waitSet.attach_condition(hbReadCond);
        waitSet.attach_condition(tsReadCond);
        waitSet.attach_condition(guardCondition);
        
        
        do {
            try {
                participant.get_current_time(now);
                
                elapsedTime(lastHeartbeatEmitted, now, sinceLastHeartbeat);
                if(compare(sinceLastHeartbeat, HEARTBEAT_INTERVAL)>=0) {
                    hbWriter.write(hbData, hbHandle);
                    lastHeartbeatEmitted.sec = now.sec; lastHeartbeatEmitted.nanosec = now.nanosec;
                }
                for(int i =0; i < condSeq.size(); i++) {
                    Condition c = (Condition) condSeq.get(i);
                    if(hbReadCond.equals(c)) {
                        for(;;) {
                            try { 
                                hbReader.read_w_condition(hb_seq, sa_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, hbReadCond);
                                int size = sa_seq.size();
    
                                for(int j = 0; j < size; j++) {
                                    SampleInfo sampleInfo = (SampleInfo) sa_seq.get(j);
                                    ice.HeartBeat sampleHeartbeat = (HeartBeat) hb_seq.get(j);
                                    

                                    ice.HeartBeat heartbeat = heartbeats.get(sampleInfo.instance_handle);
                                    if(null == heartbeat) {
                                        heartbeat = new ice.HeartBeat();
                                        heartbeats.put(new InstanceHandle_t(sampleInfo.instance_handle), heartbeat);
                                    }
                                    if(sampleInfo.valid_data) {
                                        heartbeat.copy_from(sampleHeartbeat);
                                    }

                                    
                                    TimeSyncHolder holder = sync.get(heartbeat.unique_device_identifier);
                                    if (0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                                        processNotAliveHeartbeat(sampleInfo, heartbeat);
                                        if(null != holder) {
                                            tsWriter.unregister_instance(holder.timeSync, holder.handle);
                                        }
                                    }
                                    if(0!=(InstanceStateKind.ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                                        processAliveHeartbeat(sampleInfo, heartbeat);
                                        if(sampleInfo.valid_data) {
                                            if(holder == null) {
                                                TimeSync ts = new TimeSync();
                                                ts.heartbeat_source = heartbeat.unique_device_identifier;
                                                ts.heartbeat_recipient = this.uniqueDeviceIdentifier;
                                                holder = new TimeSyncHolder(ts, tsWriter.register_instance(ts));
                                                sync.put(heartbeat.unique_device_identifier, holder);
                                            }
                                            holder.timeSync.source_source_timestamp.sec = sampleInfo.source_timestamp.sec;
                                            holder.timeSync.source_source_timestamp.nanosec = sampleInfo.source_timestamp.nanosec;
                                            holder.timeSync.recipient_receipt_timestamp.sec = sampleInfo.reception_timestamp.sec;
                                            holder.timeSync.recipient_receipt_timestamp.nanosec = sampleInfo.reception_timestamp.nanosec;
                                            tsWriter.write(holder.timeSync, holder.handle);
                                        }
                                    }
                                    
                                }
                            } catch (RETCODE_NO_DATA noData) {
                                break;
                            } finally {
                                hbReader.return_loan(hb_seq, sa_seq);
                            }
                        }
                    } else if(tsReadCond.equals(c)) {
                        for(;;) {
                            try {
                                tsReader.read_w_condition(ts_seq, sa_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, tsReadCond);
                                 int size = sa_seq.size();
                                for(int j = 0; j < size; j++) {
                                    SampleInfo sampleInfo = (SampleInfo) sa_seq.get(j);
                                    ice.TimeSync timeSync = (TimeSync) ts_seq.get(j);
                                    
                                    if(sampleInfo.valid_data) {
                                        elapsedTime(timeSync.recipient_receipt_timestamp, sampleInfo.source_timestamp, remoteProcessingTime);
                                        elapsedTime(timeSync.source_source_timestamp, sampleInfo.reception_timestamp, totalRoundtripTime);
                                        latencyTime.sec = totalRoundtripTime.sec;
                                        latencyTime.nanosec = totalRoundtripTime.nanosec;
                                        subtract(latencyTime, remoteProcessingTime);
                                        divide(latencyTime, 2);
                                        
                                        elapsedTime(timeSync.source_source_timestamp, timeSync.recipient_receipt_timestamp, clockDifference);
                                        subtract(clockDifference, latencyTime);
                                        
                                        processSynchronization(timeSync.heartbeat_recipient, latencyTime, clockDifference);
                                    }
                                }
                            } catch (RETCODE_NO_DATA noData) {
                                break;
                            } finally {
                                tsReader.return_loan(ts_seq, sa_seq);
                            }
                        }
                    }
                }
                
              
                timeout.sec = HEARTBEAT_INTERVAL.sec;
                timeout.nanosec = HEARTBEAT_INTERVAL.nanosec;
                subtract(timeout, sinceLastHeartbeat);
                waitSet.wait(condSeq, timeout);
            } catch(RETCODE_TIMEOUT t) {
                
            } catch(Throwable th) {
                log.error("unexpected error in TimeManager", th);
            }
        } while(!stop);
    }
    
    public static void main(String[] args) throws InterruptedException {
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(15, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        TimeManager t1 = new TimeManager(participant, "A", "");
//        TimeManager t2 = new TimeManager(participant, "B", "");
        
        t1.start();
//        t2.start();
//        
        Thread.sleep(10000L);
        
        
//        t2.stop();
        t1.stop();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }
}