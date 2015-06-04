package org.mdpnp.devices;

import ice.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.Property_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;

import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description="TimeManager Controller")
public class TimeManager {
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> heartbeatTask;
    private final EventLoop eventLoop;
    private final Subscriber subscriber;
    private final Publisher publisher;
	private final String uniqueDeviceIdentifier, type;
    
    private ice.HeartBeatDataWriter hbWriter;
    private ice.HeartBeatDataReader hbReader;
	private ice.TimeSyncDataReader tsReader;
    private Topic hbTopic, tsTopic;
    private ContentFilteredTopic cfTsTopic, cfHbTopic;
    private ReadCondition hbReadCond;
    private ReadCondition tsReadCond;
    private Map<InstanceHandle_t, String> hostnameByPublicationHandle = new WeakHashMap<>(); 
    
    private InstanceHandle_t hbHandle;
    private final ice.HeartBeat hbData = (HeartBeat) ice.HeartBeat.create();

    private TimeSyncHandler timeSyncHandler=null;

    private Map<InstanceHandle_t, ice.HeartBeat> heartbeats = new java.util.concurrent.ConcurrentHashMap<>();


    public void addListener(TimeManagerListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(TimeManagerListener listener) {
        this.listeners.remove(listener);
    }

    public TimeManager(ScheduledExecutorService executor, EventLoop eventLoop, Publisher publisher, Subscriber subscriber, DeviceIdentity deviceIdentifier) {
        this(executor, eventLoop, publisher, subscriber, deviceIdentifier, null);
    }
    
    public TimeManager(ScheduledExecutorService executor, EventLoop eventLoop, Publisher publisher, Subscriber subscriber, DeviceIdentity deviceIdentifier, String type) {
        this(executor, eventLoop, publisher, subscriber, deviceIdentifier.unique_device_identifier, type);
    }

    public TimeManager(ScheduledExecutorService executor, EventLoop eventLoop, Publisher publisher, Subscriber subscriber, String deviceIdentifier) {
        this(executor, eventLoop, publisher, subscriber, deviceIdentifier, null);
    }

    public TimeManager(ScheduledExecutorService executor, EventLoop eventLoop, Publisher publisher, Subscriber subscriber, String deviceIdentifier, String type) {
        if(!publisher.get_participant().equals(subscriber.get_participant())) {
            throw new RuntimeException("publisher and subscriber must be from the same participant");
        }
        this.executor = executor;
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.uniqueDeviceIdentifier = deviceIdentifier;
        this.type = type;
    }
    
    private static int instanceCounter = 0;
    private int instanceNumber = instanceCounter++;
    private final Logger log = LoggerFactory.getLogger("TimeManager"+instanceNumber);
    
    public void start() {
        eventLoop.doNow( () -> {
            SubscriberQos sQos = new SubscriberQos();
            subscriber.get_qos(sQos);
            boolean wasSubscriberAutoenable = sQos.entity_factory.autoenable_created_entities;
            sQos.entity_factory.autoenable_created_entities = false;
            subscriber.set_qos(sQos);
            
            DomainParticipant participant = publisher.get_participant();
    
            ice.HeartBeatTypeSupport.register_type(participant, ice.HeartBeatTypeSupport.get_type_name());
            
            hbTopic = TopicUtil.findOrCreateTopic(participant, ice.HeartBeatTopic.VALUE, ice.HeartBeatTypeSupport.class);
            StringSeq params = new StringSeq();
            params.add("'"+uniqueDeviceIdentifier+"'");
            cfHbTopic = participant.create_contentfilteredtopic("CF"+ice.HeartBeatTopic.VALUE+instanceNumber, hbTopic, "unique_device_identifier <> %0", params);
            hbReader = (HeartBeatDataReader) subscriber.create_datareader_with_profile(cfHbTopic, QosProfiles.ice_library, QosProfiles.heartbeat, null, StatusKind.STATUS_MASK_NONE);
            hbReadCond = hbReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            eventLoop.addHandler(hbReadCond, hbReadHandler);
            
            if(null != type) {
                ice.TimeSyncTypeSupport.register_type(participant, ice.TimeSyncTypeSupport.get_type_name());
                tsTopic = TopicUtil.findOrCreateTopic(participant, ice.TimeSyncTopic.VALUE, ice.TimeSyncTypeSupport.class);
                
                cfTsTopic = participant.create_contentfilteredtopic("CF"+ice.TimeSyncTopic.VALUE+instanceNumber, tsTopic, "heartbeat_source = %0", params);
                
                hbWriter = (HeartBeatDataWriter) publisher.create_datawriter_with_profile(hbTopic, QosProfiles.ice_library, QosProfiles.heartbeat, null, StatusKind.STATUS_MASK_NONE);

                hbData.unique_device_identifier = uniqueDeviceIdentifier;
                hbData.type = type;
                hbHandle = hbWriter.register_instance(hbData);
                
                tsReader = (TimeSyncDataReader) subscriber.create_datareader_with_profile(cfTsTopic, QosProfiles.ice_library, QosProfiles.timesync, null, StatusKind.STATUS_MASK_NONE);

				ice.TimeSyncDataWriter tsWriter = (TimeSyncDataWriter) publisher.create_datawriter_with_profile(tsTopic, QosProfiles.ice_library, QosProfiles.timesync, null, StatusKind.STATUS_MASK_NONE);
				timeSyncHandler = TimeSyncHandler.makeTimeSyncHandler(TimeSyncHandler.HandlerType.SupervisorAware,
						                                              uniqueDeviceIdentifier,
						                                              tsWriter);

                tsReadCond = tsReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
                eventLoop.addHandler(tsReadCond, tsReadHandler);
                tsReader.enable();
                
                heartbeatTask = executor.scheduleAtFixedRate(() -> hbWriter.write(hbData, hbHandle), 
                        0L, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
            }
            
            hbReader.enable();
            
            
            sQos.entity_factory.autoenable_created_entities = wasSubscriberAutoenable;
            subscriber.set_qos(sQos);

        });
    }
    
    public void stop() {
        eventLoop.doNow( () -> {
            Iterator<Entry<InstanceHandle_t, HeartBeat>> itr = heartbeats.entrySet().iterator();
            while(itr.hasNext()) {
                Entry<InstanceHandle_t, HeartBeat> hb = itr.next();
                processNotAliveHeartbeat(hb.getValue().unique_device_identifier, hb.getValue().type);
                itr.remove();
            }
            
            if(null != heartbeatTask) {
                heartbeatTask.cancel(true);
                heartbeatTask = null;
            }
            
            if(null != hbReader && null != hbReadCond) {
                eventLoop.removeHandler(hbReadCond);
                hbReader.delete_readcondition(hbReadCond);
                hbReadCond = null;
            }
            if(null != tsReader && null != tsReadCond) {
                eventLoop.removeHandler(tsReadCond);
                tsReader.delete_readcondition(tsReadCond);
                tsReadCond = null;
            }
            
            if(null != hbWriter && null != hbData && null != hbHandle) {
                hbWriter.unregister_instance(hbData, hbHandle);
                hbHandle = null;
            }


            if(null != timeSyncHandler) {
				ice.TimeSyncDataWriter tsWriter = timeSyncHandler.shutdown();
				timeSyncHandler = null;
                publisher.delete_datawriter(tsWriter);
            }
            
            if(null != hbWriter) {
                publisher.delete_datawriter(hbWriter);
                hbWriter = null;
            }
            
            if(null != hbReader) {
                subscriber.delete_datareader(hbReader);
                hbReader = null;
            }
            
            if(null != tsReader) {
                subscriber.delete_datareader(tsReader);
                tsReader = null;
            }
            
            DomainParticipant participant = publisher.get_participant();
            
            if(null != cfHbTopic) {
                participant.delete_contentfilteredtopic(cfHbTopic);
                cfHbTopic = null;
            }
            if(null != cfTsTopic) {
                participant.delete_contentfilteredtopic(cfTsTopic);
                cfTsTopic = null;
            }
            if(null != hbTopic) {
                participant.delete_topic(hbTopic);
                hbTopic = null;
            }
            if(null != tsTopic) {
                participant.delete_topic(tsTopic);
                tsTopic = null;
            }
        });
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
    
    private static final long HEARTBEAT_INTERVAL = 2000L;
    
    protected void processAliveHeartbeat(final String unique_device_identifier, final String type, String host_name) {
        
        log.trace("ALIVE:{}",unique_device_identifier);
        for(TimeManagerListener listener : listeners) {
            listener.aliveHeartbeat(unique_device_identifier, type, host_name);
        }
    }
    
    protected void processNotAliveHeartbeat(final String unique_device_identifier, final String type) {
        log.trace("NOT ALIVE:{}",unique_device_identifier);
        for(TimeManagerListener listener : listeners) {
            listener.notAliveHeartbeat(unique_device_identifier, type);
        }
    }
    
    protected void processSynchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
        for(TimeManagerListener listener : listeners) {
            listener.synchronization(remote_udi, latency, clockDifference);
        }
    }
    
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 

    private final ConditionHandler hbReadHandler = new ConditionHandler() {
        private final ice.HeartBeatSeq hb_seq = new ice.HeartBeatSeq();
        private final SampleInfoSeq sa_seq = new SampleInfoSeq();
        
        @Override
        public void conditionChanged(Condition condition) {
            for(;;) {
                try { 
                    hbReader.read(hb_seq, sa_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    int size = sa_seq.size();

                    for(int j = 0; j < size; j++) {
                        SampleInfo sampleInfo = (SampleInfo) sa_seq.get(j);
                        ice.HeartBeat sampleHeartbeat = (HeartBeat) hb_seq.get(j);

                        // Ignore our own heartbeats
                        if(uniqueDeviceIdentifier.equals(sampleHeartbeat.unique_device_identifier)) {
                            log.warn("Received my own heartbeat; check content filtering");
                            continue;
                        }
                        
                        
                        ice.HeartBeat heartbeat = heartbeats.get(sampleInfo.instance_handle);
                        if(null == heartbeat) {
                            if(!sampleInfo.valid_data) {
                                log.warn("Received an initial heartbeat with no valid_data");
                                hbReader.get_key_value(sampleHeartbeat, sampleInfo.instance_handle);
                            }
                            heartbeat = new ice.HeartBeat();
                            heartbeats.put(new InstanceHandle_t(sampleInfo.instance_handle), heartbeat);
                        }
                        
                        if(sampleInfo.valid_data) {
                            heartbeat.copy_from(sampleHeartbeat);
                        }

						if(0!=(InstanceStateKind.NOT_ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                            processNotAliveHeartbeat(heartbeat.unique_device_identifier, heartbeat.type);
							if(timeSyncHandler != null)
								timeSyncHandler.processNotAliveHeartbeat(heartbeat.unique_device_identifier);
                        }
						else if(0!=(InstanceStateKind.ALIVE_INSTANCE_STATE&sampleInfo.instance_state)) {
                            String host_name = hostnameByPublicationHandle.get(sampleInfo.publication_handle);
                            if(null == host_name) {
                                ParticipantBuiltinTopicData data = null;
                                
                                try {
                                    data = new ParticipantBuiltinTopicData();
                                    hbReader.get_matched_publication_participant_data(data, sampleInfo.publication_handle);
                                    host_name = getHostname(data);
                                    hostnameByPublicationHandle.put(new InstanceHandle_t(sampleInfo.publication_handle), host_name);
                                } catch(Exception e) {
                                    log.warn("Unable to get participant information for HeartBeat publication");
                                }
                            }
                            
                            if(null != df && System.currentTimeMillis() < 31536000000L) {
                                // one time attempt to set the system clock
                                String dt = df.format(new Date(sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L));
                                try {
                                    log.warn("Attempting date --set " + dt);
                                    // This may or may not work, in any event we only try once
                                    Runtime.getRuntime().exec(new String[]{"sudo", "date","--set",dt});
                                } catch (IOException e) {
                                    log.error("Error invoking 'date'", e);
                                }
                                df = null;
                            }
                            
                            
                            processAliveHeartbeat(heartbeat.unique_device_identifier, heartbeat.type, host_name);
							if(timeSyncHandler != null)
								timeSyncHandler.handleTimeSync(sampleInfo, heartbeat);
						}
                        
                    }
                } catch (RETCODE_NO_DATA noData) {
                    break;
                } finally {
                    hbReader.return_loan(hb_seq, sa_seq);
                }
            }
        }

    };

    private final ConditionHandler tsReadHandler = new ConditionHandler() {
        private final ice.TimeSyncSeq ts_seq = new ice.TimeSyncSeq();
        private final SampleInfoSeq sa_seq = new SampleInfoSeq();
        
        private final Duration_t remoteProcessingTime = new Duration_t();
        private final Duration_t totalRoundtripTime = new Duration_t();
        private final Duration_t latencyTime = new Duration_t();
        private final Duration_t clockDifference = new Duration_t();
        
        @Override
        public void conditionChanged(Condition condition) {
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
        
    };
    
    public static final String getHostname(ParticipantBuiltinTopicData participantData) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < participantData.property.value.size(); i++) {
            Property_t prop = (Property_t) participantData.property.value.get(i);
            if ("dds.sys_info.hostname".equals(prop.name)) {
                sb.append(prop.value).append(" ");
            }
        }

        for (int i = 0; i < participantData.default_unicast_locators.size(); i++) {
            Locator_t locator = (Locator_t) participantData.default_unicast_locators.get(i);
            try {
                InetAddress addr = null;
                switch (locator.kind) {
                case Locator_t.KIND_TCPV4_LAN:
                case Locator_t.KIND_TCPV4_WAN:
                case Locator_t.KIND_TLSV4_LAN:
                case Locator_t.KIND_TLSV4_WAN:
                case Locator_t.KIND_UDPv4:
                    addr = InetAddress
                            .getByAddress(new byte[] { locator.address[12], locator.address[13], locator.address[14], locator.address[15] });
                    break;
                case Locator_t.KIND_UDPv6:
                default:
                    addr = InetAddress.getByAddress(locator.address);
                    break;
                }
                sb.append(addr.getHostAddress()).append(" ");
            } catch (UnknownHostException e) {
                // TODO log
//                 log.error("getting locator address", e);
            }
        }
        return sb.toString();
    }    
}