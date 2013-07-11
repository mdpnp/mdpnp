package org.mdpnp.apps.testapp;

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.Numeric;
import ice.NumericSeq;
import ice.SampleArraySeq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class VitalsModel extends AbstractListModel implements ListModel, ListDataListener {
    private final Set<QueryCondition> numericInterest = new HashSet<QueryCondition>();
    private final Set<QueryCondition> sampleArrayInterest = new HashSet<QueryCondition>();

    private final NumericSeq num_seq = new NumericSeq();
    private final SampleArraySeq sa_seq = new SampleArraySeq();
    private final SampleInfoSeq info_seq = new SampleInfoSeq();

    private final EventLoop.ConditionHandler numericHandler = new EventLoop.ConditionHandler() {

        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    numericDataReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                            (QueryCondition) condition);
                    for (int i = 0; i < info_seq.size(); i++) {
                        SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                        if (sampleInfo.valid_data) {
                            Numeric n = (Numeric) num_seq.get(i);
                            // log.trace("VitalsModel interested in: " + n);
                            updateNumeric(n, sampleInfo);

                        }
                    }
                    numericDataReader.return_loan(num_seq, info_seq);
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }
    };
    
    private final EventLoop.ConditionHandler sampleArrayHandler = new ConditionHandler() {

        @Override
        public void conditionChanged(Condition condition) {
            try {
                for (;;) {
                    sampleArrayDataReader.read_w_condition(sa_seq, info_seq,
                            ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (QueryCondition) condition);
                    for (int i = 0; i < info_seq.size(); i++) {
                        SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                        if (sampleInfo.valid_data) {
                            ice.SampleArray sa = (ice.SampleArray) sa_seq.get(i);
                            // TODO report SampleArray changes to listener
                        }
                    }
                    sampleArrayDataReader.return_loan(sa_seq, info_seq);
                }
            } catch (RETCODE_NO_DATA noData) {

            } finally {

            }
        }

    };

    public void addNumericInterest(Integer i) {
        // TODO this should probably be a ContentFilteredTopic to allow the
        // writer to do the filtering
        StringSeq params = new StringSeq();
        params.add(Integer.toString(i));
        QueryCondition qc = numericDataReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "name = %0", params);
        numericInterest.add(qc);
        eventLoop.addHandler(qc, numericHandler);
        log.debug("New QueryCondition for numeric name=" + i);
    }

    public void addSampleArrayInterest(Integer i) {
        StringSeq params = new StringSeq();
        params.add(Integer.toString(i));
        QueryCondition qc = sampleArrayDataReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "name = %0", params);
        sampleArrayInterest.add(qc);
        eventLoop.addHandler(qc, sampleArrayHandler);
        log.debug("New QueryCondition for sampleArray name=" + i);
    }

    private static final Logger log = LoggerFactory.getLogger(VitalsModel.class);

    public interface VitalsListener {
        void update(ice.Numeric n, SampleInfo sampleInfo, Device device);

        void deviceRemoved(Device device);

        void deviceAdded(Device device);

        void deviceChanged(Device device);
    }

    private VitalsListener listener;

    public void setListener(VitalsListener listener) {
        this.listener = listener;
    }

    private final ice.NumericDataReader numericDataReader;
    private final ice.SampleArrayDataReader sampleArrayDataReader;
    private final DeviceListModel deviceModel;
    private final EventLoop eventLoop;
    private final Subscriber subscriber;

    public VitalsModel(Subscriber subscriber, DeviceListModel deviceModel, EventLoop eventLoop) {
        this.deviceModel = deviceModel;
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;

        TopicDescription numericTopic = lookupOrCreateTopic(subscriber.get_participant(), ice.NumericTopic.VALUE,
                ice.NumericTypeSupport.class);
        numericDataReader = (ice.NumericDataReader) subscriber.create_datareader(numericTopic,
                Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        TopicDescription sampleArrayTopic = lookupOrCreateTopic(subscriber.get_participant(),
                ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.class);
        sampleArrayDataReader = (ice.SampleArrayDataReader) subscriber.create_datareader(sampleArrayTopic,
                Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        deviceModel.addListDataListener(this);

    }

    public void tearDown() {
        for(QueryCondition qc : numericInterest) {
            eventLoop.removeHandler(qc);
            numericDataReader.delete_readcondition(qc);
        }
        numericInterest.clear();
        for(QueryCondition qc : sampleArrayInterest) {
            eventLoop.removeHandler(qc);
            sampleArrayDataReader.delete_readcondition(qc);
        }
        sampleArrayInterest.clear();
        numericDataReader.delete_contained_entities();
        sampleArrayDataReader.delete_contained_entities();
        subscriber.delete_datareader(numericDataReader);
        subscriber.delete_datareader(sampleArrayDataReader);
    }
    
    public static final class Vitals {
        private final Device device;
        private final ice.Numeric numeric;

        public Vitals(Device device, ice.Numeric numeric) {
            this.device = device;
            this.numeric = new ice.Numeric(numeric);
        }

        public Device getDevice() {
            return device;
        }

        public ice.Numeric getNumeric() {
            return numeric;
        }

    }

    private final List<Vitals> vitals = new ArrayList<Vitals>();

    @Override
    public int getSize() {
        return vitals.size();
    }

    @Override
    public Object getElementAt(int index) {
        return vitals.get(index);
    }

    /**
     * Reflect changes in a device for every associated vital
     * 
     * @param device
     */
    public void fireDeviceChanged(Device device) {
        for (int i = 0; i < vitals.size(); i++) {
            if (vitals.get(i).getDevice().equals(device)) {
                fireContentsChanged(this, i, i);
                if (listener != null) {
                    listener.deviceChanged(device);
                }
            }
        }
    }

    /**
     * 
     * @param device
     * @return
     */
    public void removeDevice(Device device) {
        boolean goAgain = true;

        while (goAgain) {
            goAgain = false;
            for (int i = 0; i < vitals.size(); i++) {
                if (vitals.get(i).getDevice().equals(device)) {
                    log.debug("removed " + vitals.get(i).getNumeric().name + " " + device.getMakeAndModel());
                    vitals.remove(i);
                    fireIntervalRemoved(this, 0, 1);
                    fireContentsChanged(this, 0, vitals.size() - 1);
                    goAgain = true;
                }
            }
        }
        if (listener != null) {
            listener.deviceRemoved(device);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {

    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (e.getIndex0() != e.getIndex1()) {
            throw new IllegalArgumentException("Cannot handle multi-row deletes; needs refactoring to support it");
        }
        removeDevice(((DeviceListModel) e.getSource()).getLastRemoved());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        for (int idx = e.getIndex0(); idx <= e.getIndex1(); idx++) {
            removeDevice(((DeviceListModel) e.getSource()).getElementAt(idx));
        }
    }

    protected void updateNumeric(ice.Numeric n, SampleInfo sampleInfo) {
        Vitals v = null;
        for (int i = 0; i < vitals.size(); i++) {
            v = vitals.get(i);
            if(n.universal_device_identifier.equals(v.getNumeric().universal_device_identifier) &&
               n.name == v.getNumeric().name) {
            // This vital already known from this device!
                v.getNumeric().copy_from(n);

                if (listener != null) {
                    listener.update(v.getNumeric(), sampleInfo, v.getDevice());
                }
                fireContentsChanged(this, i, i);
                return;
            }
        }
        // New vital/device combination
        Device device = deviceModel.getByUniversalDeviceIdentifier(n.universal_device_identifier);
        if (null != device) {
            v = new Vitals(device, n);
//            System.out.println("Added new Vitals for " + n.name + " " + n.universal_device_identifier);
            vitals.add(0, v);
            fireIntervalAdded(this, 0, 0);
            if (listener != null) {
                listener.deviceAdded(device);
            }
        } else {
            log.warn("Numeric from unknown Device:" + n.universal_device_identifier);
        }
    }
}
