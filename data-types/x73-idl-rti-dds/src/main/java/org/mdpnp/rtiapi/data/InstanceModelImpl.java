package org.mdpnp.rtiapi.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.mdpnp.rtiapi.data.ListenerList.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_PRECONDITION_NOT_MET;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReaderImpl;
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
import com.rti.dds.topic.TopicDescription;
import com.rti.dds.topic.TypeSupportImpl;
import com.rti.dds.util.LoanableSequence;

@SuppressWarnings("serial")
public class InstanceModelImpl<D extends Copyable, R extends DataReaderImpl> extends AbstractListModel<D> implements InstanceModel<D,R> {
    private final ListenerList<InstanceModelListener<D, R>> listeners = new ListenerList<InstanceModelListener<D,R>>(InstanceModelListener.class);
    private final List<InstanceHandle_t> instances = Collections.synchronizedList(new ArrayList<InstanceHandle_t>());
    
    @Override
    public void addListener(InstanceModelListener<D, R> listener) {
        listeners.addListener(listener);
    }
    
    @Override
    public void iterateAndAddListener(InstanceModelListener<D, R> listener) {
        iterateAndAddListener(listener, ResourceLimitsQosPolicy.LENGTH_UNLIMITED);
    }
    
    @Override
    public void iterateAndAddListener(InstanceModelListener<D, R> listener, int maxSamples) {
        // TODO ordering issues if an instance becomes unalive while I'm catching up this listener
        addListener(listener);
        LoanableSequence sa_seq = InstanceModelImpl.this.sa_seq.get();
        SampleInfoSeq info_seq = InstanceModelImpl.this.info_seq.get();
        for(InstanceHandle_t handle : instances) {
            try {
                readInstance.invoke(reader, sa_seq, info_seq, maxSamples, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
                boolean reportedAlive = false;
                for(int i = 0; i < info_seq.size(); i++) {
                    if(!reportedAlive) {
                        listener.instanceAlive(this,  reader, (D)sa_seq.get(i), (SampleInfo) info_seq.get(i));
                        reportedAlive = true;
                    }
                    if(((SampleInfo)info_seq.get(i)).valid_data) {
                        listener.instanceSample(this, reader, (D)sa_seq.get(i), (SampleInfo) info_seq.get(i));
                    }
                }
            } catch (Exception e) {
                if(!(e.getCause() instanceof RETCODE_NO_DATA)) {
                    log.error("read_instance", e);
                }
                
            } finally {
                try {
                    returnLoan.invoke(reader, sa_seq, info_seq);
                } catch (Exception e) {
                    log.error("return_loan", e);
                }
            }
        }
    }
    
    @Override
    public void removeListener(InstanceModelListener<D, R> listener) {
        listeners.removeListener(listener);
    }
    
    private abstract class AbstractDispatcher<T extends AbstractDispatcher<?>> implements Dispatcher<InstanceModelListener<D,R>> {
        protected D data;
        protected SampleInfo sampleInfo;
        
        @SuppressWarnings("unchecked")
        public T set(D data, SampleInfo sampleInfo) {
            this.data = data;
            this.sampleInfo = sampleInfo;
            return (T) this;
        }
    }
    
    private class InstanceAliveDispatcher extends AbstractDispatcher<InstanceAliveDispatcher> {
        @Override
        public void dispatch(InstanceModelListener<D, R> l) {
            l.instanceAlive(InstanceModelImpl.this, getReader(), data, sampleInfo);
        }
    }
    private class InstanceNotAliveDispatcher extends AbstractDispatcher<InstanceNotAliveDispatcher> {
        @Override
        public void dispatch(InstanceModelListener<D, R> l) {
            l.instanceNotAlive(InstanceModelImpl.this, getReader(), data, sampleInfo);
        }
    }
    private class InstanceSampleDispatcher extends AbstractDispatcher<InstanceSampleDispatcher> {
        @Override
        public void dispatch(InstanceModelListener<D, R> l) {
            l.instanceSample(InstanceModelImpl.this, getReader(), data, sampleInfo);
        }
    }
    
    private final InstanceAliveDispatcher instanceAlive = new InstanceAliveDispatcher();
    private final InstanceNotAliveDispatcher instanceNotAlive = new InstanceNotAliveDispatcher();
    private final InstanceSampleDispatcher instanceSample = new InstanceSampleDispatcher();
    
    private R reader;
    private ReadCondition condition;

    private Subscriber subscriber;
    private EventLoop eventLoop;
    private ContentFilteredTopic filteredTopic;
    
    @Override
    public EventLoop getEventLoop() {
        return eventLoop;
    }

    @Override
    public R getReader() {
        return reader;
    }
    
    protected final ThreadLocal<LoanableSequence> sa_seq = new ThreadLocal<LoanableSequence>() {
        protected LoanableSequence initialValue() {
            try {
                return sequenceClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    };
    protected final ThreadLocal<LoanableSequence> sa_seq1 = new ThreadLocal<LoanableSequence>() {
        protected LoanableSequence initialValue() {
            try {
                return sequenceClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    };
    protected final ThreadLocal<SampleInfoSeq> info_seq = new ThreadLocal<SampleInfoSeq>() {
        protected SampleInfoSeq initialValue() {
            return new SampleInfoSeq();
        };
    };
    protected final ThreadLocal<SampleInfoSeq> info_seq1 = new ThreadLocal<SampleInfoSeq>() {
        protected SampleInfoSeq initialValue() {
            return new SampleInfoSeq();
        };
    };
    
    private static final Logger log = LoggerFactory.getLogger(InstanceModelImpl.class);
    
    protected void fireInstanceAlive(D data, SampleInfo sampleInfo) {
        listeners.fire(instanceAlive.set(data, sampleInfo));
    }
    
    protected void fireInstanceNotAlive(D keyHolder, SampleInfo sampleInfo) {
        listeners.fire(instanceNotAlive.set(keyHolder, sampleInfo));
    }
    
    protected void fireInstanceSample(D data, SampleInfo sampleInfo) {
        listeners.fire(instanceSample.set(data, sampleInfo));
    }
    
    private final EventLoop.ConditionHandler handler = new EventLoop.ConditionHandler() {
        @SuppressWarnings("unchecked")
        @Override
        public void conditionChanged(Condition condition) {
            LoanableSequence sa_seq = InstanceModelImpl.this.sa_seq.get();
            SampleInfoSeq info_seq = InstanceModelImpl.this.info_seq.get();
            R reader = InstanceModelImpl.this.reader;
            try {
                readWCondition.invoke(reader, sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                InstanceHandle_t lastHandle = InstanceHandle_t.HANDLE_NIL;
                
                for (int i = 0; i < info_seq.size(); i++) {
                    SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                    if (0 != (sampleInfo.instance_state & InstanceStateKind.NOT_ALIVE_INSTANCE_STATE)) {
                        fireInstanceNotAlive((D) sa_seq.get(i), sampleInfo);
                        int idx = instances.indexOf(sampleInfo.instance_handle);
                        if(idx>=0) {
                            instances.remove(idx);
                            fireIntervalRemoved(InstanceModelImpl.this, idx, idx);
                        } else {
                            log.warn("Unable to find instance for removal:"+sampleInfo.instance_handle);
                        }
                    } else {
                        if(!lastHandle.equals(sampleInfo.instance_handle) && 0 != (sampleInfo.view_state & ViewStateKind.NEW_VIEW_STATE)) {
                            fireInstanceAlive((D) sa_seq.get(i), sampleInfo);
                        }
                        fireInstanceSample((D) sa_seq.get(i), sampleInfo);
                        int idx = instances.indexOf(sampleInfo.instance_handle);
                        if(idx>=0) {
                            fireContentsChanged(InstanceModelImpl.this, idx, idx);
                        } else {
                            instances.add(new InstanceHandle_t(sampleInfo.instance_handle));
                            fireIntervalAdded(InstanceModelImpl.this, instances.size()-1, instances.size()-1);
                        }
                    }
                    lastHandle = sampleInfo.instance_handle;
                }
            } catch (InvocationTargetException ite) {
                if(!(ite.getCause() instanceof RETCODE_NO_DATA)) {
                    log.error("reading", ite);
                }
            } catch (Exception e) {
                log.error("reading ", e);
            } finally {
                try {
                    returnLoan.invoke(reader, sa_seq, info_seq);
                } catch (Exception e) {
                    log.error("return_loan", e);
                }
            }
        }
    };

    protected final String topic;
    protected final Class<D> dataClass;
    protected final Class<? extends TypeSupportImpl> typeSupportClass;
    protected final Class<? extends LoanableSequence> sequenceClass;
    
    protected final Method getKeyValue, returnLoan, readWCondition, readInstance;
    
    public InstanceModelImpl(final String topic, Class<D> dataClass, Class<R> readerClass, Class<? extends TypeSupportImpl> typeSupportClass, Class<? extends LoanableSequence> sequenceClass) {
        this.topic = topic;
        this.dataClass = dataClass;
        this.typeSupportClass = typeSupportClass;
        this.sequenceClass = sequenceClass;
        try {
            getKeyValue = readerClass.getMethod("get_key_value", dataClass, InstanceHandle_t.class);
            returnLoan = readerClass.getMethod("return_loan", sequenceClass, SampleInfoSeq.class);
            readWCondition = readerClass.getMethod("read_w_condition", sequenceClass, SampleInfoSeq.class, int.class, ReadCondition.class);
            readInstance = readerClass.getMethod("read_instance", sequenceClass, SampleInfoSeq.class, int.class, InstanceHandle_t.class, int.class, int.class, int.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void start(Subscriber subscriber, EventLoop eventLoop, String qosLibrary, String qosProfile) {
        start(subscriber, eventLoop, null, null, qosLibrary, qosProfile);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void start(Subscriber subscriber, EventLoop eventLoop, String expression, StringSeq params, String qosLibrary, String qosProfile) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;

        TopicDescription saTopic = TopicUtil.lookupOrCreateTopic(subscriber.get_participant(), topic,
                typeSupportClass);
        if(null != expression) {
            log.info(getClass()+" Filtered Expression:"+expression+" Filter values:"+params);
            // TODO time since epoch is a klugey way to get some likelihood of uniqueness
            filteredTopic = subscriber.get_participant().create_contentfilteredtopic("Filtered"+topic+System.currentTimeMillis(), (Topic) saTopic, expression, params);
            if(null == filteredTopic) {
                log.debug("Unable to create filtered topic " + getClass());
            }
        }
        SubscriberQos sQos = new SubscriberQos();
        subscriber.get_qos(sQos);
        sQos.entity_factory.autoenable_created_entities = false;
        subscriber.set_qos(sQos);
        
        reader = (R) subscriber.create_datareader_with_profile(null==filteredTopic?saTopic:filteredTopic, qosLibrary,
                qosProfile, null, StatusKind.STATUS_MASK_NONE);
        sQos.entity_factory.autoenable_created_entities = true;
        subscriber.set_qos(sQos);

        condition = reader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE);

        eventLoop.addHandler(condition, handler);
        
        reader.enable();

    }

    @Override
    public void stop() {
        if(null != eventLoop) {
            eventLoop.removeHandler(condition);
            eventLoop = null;
        }
        if(condition != null) {
            reader.delete_readcondition(condition);
            condition = null;
        }
        if(subscriber != null) {
            if(reader != null) {
                reader.delete_contained_entities();
                try {
                    subscriber.delete_datareader(reader);
                } catch (RETCODE_PRECONDITION_NOT_MET preCondition) {
                    // TODO nail this down an awful lot tighter
                    log.warn("Ignoring an error in delete_datareader", preCondition);
                }
                reader = null;
            }
            if(null != filteredTopic) {
                subscriber.get_participant().delete_contentfilteredtopic(filteredTopic);
                filteredTopic = null;
            }
            subscriber = null;
        }
    }

    @Override
    public int getSize() {
        return instances.size();
    }

    @Override
    public D getElementAt(int index) {
        InstanceHandle_t handle;
        // Doesn't seem likely but I've seen it happen
        synchronized(instances) {
            if(index < instances.size()) {
                handle = instances.get(index);
            } else {
                return null;
            }
        }
        
        LoanableSequence sa_seq = InstanceModelImpl.this.sa_seq1.get();
        SampleInfoSeq info_seq = InstanceModelImpl.this.info_seq1.get();
        R reader = this.reader;
        try {
            readInstance.invoke(reader, sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
            D d = dataClass.newInstance();
            d.copy_from(sa_seq.get(sa_seq.size()-1));
            return d;
        } catch (Exception e) {
            if(!(e.getCause() instanceof RETCODE_NO_DATA)) {
                log.error("read_instance", e);
            }
        } finally {
            try {
                returnLoan.invoke(reader, sa_seq, info_seq);
            } catch (Exception e) {
                log.error("return_loan", e);
            }
        }
        return null;
    }
}