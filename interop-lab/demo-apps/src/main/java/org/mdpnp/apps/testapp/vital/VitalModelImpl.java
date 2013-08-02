package org.mdpnp.apps.testapp.vital;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTopic;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.DeviceIdentityTopic;
import ice.DeviceIdentityTypeSupport;
import ice.Numeric;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTopic;
import ice.NumericTypeSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.TopicUtil;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

public class VitalModelImpl implements VitalModel {

    private final List<Vital> vitals = Collections.synchronizedList(new ArrayList<Vital>());
    
    private VitalModelListener[] listeners = new VitalModelListener[0];
    
    private DeviceIdentityDataReader deviceIdentityReader;
    private DeviceConnectivityDataReader deviceConnectivityReader;
    protected NumericDataReader numericReader;
    private final Map<Vital, Set<QueryCondition>> queryConditions = new HashMap<Vital, Set<QueryCondition>>();
    
    private Subscriber subscriber;
    private EventLoop eventLoop;
    
    private final EventLoop.ConditionHandler numericHandler = new EventLoop.ConditionHandler() {
        private final NumericSeq num_seq = new NumericSeq();
        private final SampleInfoSeq info_seq = new SampleInfoSeq();
        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    try {
                        numericReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (QueryCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if(0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                                Numeric keyHolder = new Numeric();
                                numericReader.get_key_value(keyHolder, sampleInfo.instance_handle);
                                removeNumeric(keyHolder.universal_device_identifier, keyHolder.name);
                            } else {
                                if (sampleInfo.valid_data) {
                                    Numeric n = (Numeric) num_seq.get(i);
                                    updateNumeric(n, sampleInfo);
                                }
                            }
                        }
                    } finally {
                        numericReader.return_loan(num_seq, info_seq);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }
    };
    protected void removeNumeric(String udi, int name) {
        for(Vital v : vitals) {
            boolean updated = false;
            for(int x : v.getNames()) {
                if(x == name) {
                    ListIterator<Value> li = v.getValues().listIterator();
                    while(li.hasNext()) {
                        Value va = li.next();
                        if(va.getUniversalDeviceIdentifier().equals(udi)) {
                            li.remove();
                            updated = true;
                        }
                    }
                }
            }
            if(updated) {
                fireVitalChanged(v);
            }
        }
    }
    protected void updateNumeric(Numeric n, SampleInfo si) {
        for(Vital v : vitals) {
            for(int x : v.getNames()) {
                // Change to this vital from a source
                if(x == n.name) {
                    boolean updated = false;
                    for(Value va : v.getValues()) {
                        if(va.getUniversalDeviceIdentifier().equals(n.universal_device_identifier)) {
                            va.getNumeric().copy_from(n);
                            va.getSampleInfo().copy_from(si);
                            updated = true;
                        }
                    }
                    if(!updated) {
                        v.getValues().add(new ValueImpl(n.universal_device_identifier, v));
                    }
                    fireVitalChanged(v);
                }
            }
        }
    }
    
    @Override
    public int getCount() {
        return vitals.size();
    }

    @Override
    public Vital getVital(int i) {
        return vitals.get(i);
    }

    @Override
    public Vital addVital(String label, int[] names, int minimum, int maximum) {
        Vital v = new VitalImpl(this, label, names, minimum, maximum);
        vitals.add(v);
        fireVitalAdded(v);
        return v;
    }

    @Override
    public boolean removeVital(Vital vital) {
        boolean r = vitals.remove(vital);
        if(r) {
            fireVitalRemoved(vital);
        }
        return r;
    }

    @Override
    public Vital removeVital(int i) {
        Vital v = vitals.remove(i);
        if(v != null) {
            fireVitalRemoved(v);
        }
        return v;
    }

    @Override
    public synchronized void addListener(VitalModelListener vitalModelListener) {
        VitalModelListener[] oldListeners = this.listeners;
        VitalModelListener[] newListeners = new VitalModelListener[oldListeners.length+1];
        System.arraycopy(oldListeners, 0, newListeners, 0, oldListeners.length);
        newListeners[newListeners.length - 1] = vitalModelListener;
        this.listeners = newListeners;
    }

    @Override
    public synchronized boolean removeListener(VitalModelListener vitalModelListener) {
        VitalModelListener[] oldListeners = this.listeners;
        List<VitalModelListener> newListeners = new ArrayList<VitalModelListener>();
        boolean found = false;
        for(VitalModelListener vml : oldListeners) {
            if(vitalModelListener.equals(vml)) {
                found = true;
            } else {
                newListeners.add(vml);
            }
        }
        
        this.listeners = newListeners.toArray(new VitalModelListener[0]);
        return found;
    }
    
    protected void fireVitalAdded(Vital v) {
        VitalModelListener[] listeners = this.listeners;
        for(VitalModelListener vml : listeners) {
            vml.vitalAdded(this, v);
        }
    }
    protected void fireVitalRemoved(Vital v) {
        VitalModelListener[] listeners = this.listeners;
        for(VitalModelListener vml : listeners) {
            vml.vitalRemoved(this, v);
        }
    }
    protected void fireVitalChanged(Vital v) {
        VitalModelListener[] listeners = this.listeners;
        for(VitalModelListener vml : listeners) {
            vml.vitalChanged(this, v);
        }
    }

    @Override
    public DeviceIdentity getDeviceIdentity(String udi) {
        DeviceIdentity keyHolder = (DeviceIdentity) DeviceIdentity.create();
        InstanceHandle_t handle = new InstanceHandle_t();
        keyHolder.universal_device_identifier = udi;
        deviceIdentityReader.get_key_value(keyHolder, handle);
        DeviceIdentitySeq data_seq = new DeviceIdentitySeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();
        
        try {
            deviceIdentityReader.read_instance(data_seq, info_seq, 1, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
            return new DeviceIdentity((DeviceIdentity) data_seq.get(0));
        } finally {
            deviceIdentityReader.return_loan(data_seq, info_seq);
        }
    }

   
    @Override
    public DeviceConnectivity getDeviceConnectivity(String udi) {
        DeviceConnectivity keyHolder = (DeviceConnectivity) DeviceConnectivity.create();
        InstanceHandle_t handle = new InstanceHandle_t();
        keyHolder.universal_device_identifier = udi;
        deviceConnectivityReader.get_key_value(keyHolder, handle);
        DeviceConnectivitySeq data_seq = new DeviceConnectivitySeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();
        try {
            deviceConnectivityReader.read_instance(data_seq, info_seq, 1, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
            return new DeviceConnectivity((DeviceConnectivity)data_seq.get(0));
        } finally {
            deviceConnectivityReader.return_loan(data_seq, info_seq);
        }
    }

    private void addQueryConditions(Vital v) {
        if(null != subscriber) {
            // TODO this should probably be a ContentFilteredTopic to allow the
            // writer to do the filtering
            StringSeq params = new StringSeq();
            Set<QueryCondition> set = queryConditions.get(v);
            set = null == set ? new HashSet<QueryCondition>() : set;
            for(int x : v.getNames()) {
                params.add(Integer.toString(x));
                QueryCondition qc = numericReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                        ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "name = %0", params);
                set.add(qc);
                eventLoop.addHandler(qc, numericHandler);
            }
        }
    }
    
    @Override
    public void start(Subscriber subscriber, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        DomainParticipant participant = subscriber.get_participant();
        DeviceIdentityTypeSupport.register_type(participant, DeviceIdentityTypeSupport.get_type_name());
        TopicDescription diTopic = TopicUtil.lookupOrCreateTopic(participant, DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.class);
        deviceIdentityReader = (DeviceIdentityDataReader) subscriber.create_datareader(diTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        DeviceConnectivityTypeSupport.register_type(participant, DeviceConnectivityTypeSupport.get_type_name());
        TopicDescription dcTopic = TopicUtil.lookupOrCreateTopic(participant, DeviceConnectivityTopic.VALUE, DeviceConnectivityTypeSupport.class);
        deviceConnectivityReader = (DeviceConnectivityDataReader) subscriber.create_datareader(dcTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        NumericTypeSupport.register_type(participant, NumericTypeSupport.get_type_name());
        TopicDescription nTopic = TopicUtil.lookupOrCreateTopic(participant, NumericTopic.VALUE, NumericTypeSupport.class);
        numericReader = (NumericDataReader) subscriber.create_datareader(nTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        for(Vital v : vitals) {
            addQueryConditions(v);
        }
    }

    @Override
    public void stop() {
        for(Set<QueryCondition> qc : queryConditions.values()) {
            for(QueryCondition q : qc) {
                numericReader.delete_readcondition(q);
            }
        }
        queryConditions.clear();
        numericReader.delete_contained_entities();
        subscriber.delete_datareader(numericReader);
        
        deviceIdentityReader.delete_contained_entities();
        subscriber.delete_datareader(deviceIdentityReader);
        deviceConnectivityReader.delete_contained_entities();
        subscriber.delete_datareader(deviceConnectivityReader);
        this.subscriber = null;
        this.eventLoop = null;
    }
    
    public static void main(String[] args) {
        DDS.init(false);
        DomainParticipant p = DomainParticipantFactory.get_instance().create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Subscriber s = p.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        VitalModel vm = new VitalModelImpl();
        
        vm.addListener(new VitalModelListener() {
            
            @Override
            public void vitalRemoved(VitalModel model, Vital vital) {
                System.out.println("Removed:"+vital);
            }
            
            @Override
            public void vitalChanged(VitalModel model, Vital vital) {
                System.out.println(new Date()+" Changed:"+vital);
            }
            
            @Override
            public void vitalAdded(VitalModel model, Vital vital) {
                System.out.println("Added:"+vital);
            }
        });
        vm.addVital("Heart Rate", new int[] {ice.MDC_PULS_OXIM_PULS_RATE.VALUE}, 20, 200);
        EventLoop eventLoop = new EventLoop();
        EventLoopHandler eventLoopHandler = new EventLoopHandler(eventLoop);
        
        vm.start(s, eventLoop);
    }

}
