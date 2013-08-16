package org.mdpnp.apps.testapp.co2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.TopicUtil;

import com.rti.dds.infrastructure.Condition;
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

public class CapnoModelImpl implements CapnoModel {

    private final List<Capno> capnos = Collections.synchronizedList(new ArrayList<Capno>());
    private CapnoModelListener[] listeners = new CapnoModelListener[0];

    private ice.SampleArrayDataReader capnoReader;
    private QueryCondition capnoCondition;
    
    private Subscriber subscriber;
    private EventLoop eventLoop;
    
    
    
    private final EventLoop.ConditionHandler capnoHandler = new EventLoop.ConditionHandler() {
        private final ice.SampleArraySeq sa_seq = new ice.SampleArraySeq();
        private final SampleInfoSeq info_seq = new SampleInfoSeq();

        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    try {
                        capnoReader.read_w_condition(sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (QueryCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                            if (0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                                ice.SampleArray keyHolder = new ice.SampleArray();
                                capnoReader.get_key_value(keyHolder, sampleInfo.instance_handle);
                                removeCapno(keyHolder.universal_device_identifier);
                            } else {
                                if (sampleInfo.valid_data) {
                                    ice.SampleArray s = (ice.SampleArray) sa_seq.get(i);
                                    updateCapno(s, sampleInfo);
                                }
                            }
                        }
                    } finally {
                        capnoReader.return_loan(sa_seq, info_seq);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }
    };
    
    
    protected final int sa_name;
    public CapnoModelImpl(final int name) {
        this.sa_name = name;
    }
    
    @Override
    public int getCount() {
        return capnos.size();
    }

    @Override
    public Capno getCapno(int i) {
        return capnos.get(i);
    }

    @Override
    public void start(Subscriber subscriber, EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        
//      ice.InfusionStatusTypeSupport.register_type(subscriber.get_participant(), ice.InfusionStatusTypeSupport.get_type_name());
      TopicDescription saTopic = TopicUtil.lookupOrCreateTopic(subscriber.get_participant(), ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.class);
      capnoReader = (ice.SampleArrayDataReader) subscriber.create_datareader(saTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
      StringSeq params = new StringSeq();
      params.add(Integer.toString(sa_name));
      capnoCondition = capnoReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "name = %0", params);
      
      eventLoop.addHandler(capnoCondition, capnoHandler);
        
    }

    @Override
    public void stop() {
        eventLoop.removeHandler(capnoCondition);
        capnoReader.delete_readcondition(capnoCondition);
        capnoCondition = null;
        subscriber.delete_datareader(capnoReader);
        capnoReader = null;
        subscriber = null;
        eventLoop = null;
    }
    
    protected void removeCapno(String udi) {
        List<Capno> removed = new ArrayList<Capno>();
        synchronized(capnos) {
            ListIterator<Capno> litr = capnos.listIterator();
            while(litr.hasNext()) {
                Capno capno = litr.next();
                if(capno.getSampleArray().universal_device_identifier.equals(udi)) {
                    removed.add(capno);
                    litr.remove();
                }
            }
        }
        for(Capno c : removed) {
            fireCapnoRemoved(c);
        }
    }
    
    protected void updateCapno(ice.SampleArray sampleArray, SampleInfo sampleInfo) {
        Capno capno = null;
        synchronized(capnos) {
            ListIterator<Capno> itr = capnos.listIterator();
            while(itr.hasNext()) {
                capno = itr.next();
                if(sampleArray.universal_device_identifier.equals(capno.getSampleArray().universal_device_identifier)) {
                    break;
                } else {
                    capno = null;
                }
            }
        }
        if(capno != null) {
            capno.getSampleArray().copy_from(sampleArray);
            capno.getSampleInfo().copy_from(sampleInfo);
            fireCapnoChanged(capno);
        } else {
            capno = new CapnoImpl(this, sampleArray, sampleInfo);
            capnos.add(capno);
            fireCapnoAdded(capno);
        }
        
    }

    @Override
    public void addCapnoListener(CapnoModelListener listener) {
        CapnoModelListener[] oldListeners = this.listeners;
        CapnoModelListener[] newListeners = new CapnoModelListener[oldListeners.length + 1];
        System.arraycopy(oldListeners, 0, newListeners, 0, oldListeners.length);
        newListeners[newListeners.length - 1] = listener;
        this.listeners = newListeners;
    }

    @Override
    public boolean removeCapnoListener(CapnoModelListener listener) {
        CapnoModelListener[] oldListeners = this.listeners;
        List<CapnoModelListener> newListeners = new ArrayList<CapnoModelListener>();
        boolean found = false;
        for (CapnoModelListener cml : oldListeners) {
            if (listener.equals(cml)) {
                found = true;
            } else {
                newListeners.add(cml);
            }
        }

        this.listeners = newListeners.toArray(new CapnoModelListener[0]);
        return found;
    }
    protected void fireCapnoAdded(Capno c) {
        CapnoModelListener[] listeners = this.listeners;
        for (CapnoModelListener cml : listeners) {
            cml.capnoAdded(this, c);
        }
    }

    protected void fireCapnoRemoved(Capno c) {
        CapnoModelListener[] listeners = this.listeners;
        for (CapnoModelListener cml : listeners) {
            cml.capnoRemoved(this, c);
        }
    }

    protected void fireCapnoChanged(Capno c) {
        CapnoModelListener[] listeners = this.listeners;
        for (CapnoModelListener cml : listeners) {
            cml.capnoChanged(this, c);
        }
    }

}
