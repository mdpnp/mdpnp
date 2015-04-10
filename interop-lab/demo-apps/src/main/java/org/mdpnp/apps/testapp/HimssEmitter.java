package org.mdpnp.apps.testapp;

import himss.MetricDataQualityDataWriter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

public class HimssEmitter {
    private Publisher publisher;
    private NumericFxList numericList;
    private Topic topic;
    private himss.MetricDataQualityDataWriter writer;
    
    private final Logger log = LoggerFactory.getLogger(HimssEmitter.class);
    
    private static final class Holder implements ChangeListener<Date> {
        private himss.MetricDataQuality quality = new himss.MetricDataQuality();
        private InstanceHandle_t instanceHandle;
        private NumericFx data;
        private final himss.MetricDataQualityDataWriter writer;
        
        public Holder(final himss.MetricDataQualityDataWriter writer) {
            this.writer = writer;
        }
        
        public void setInstanceHandle(InstanceHandle_t instanceHandle) {
            this.instanceHandle = instanceHandle;
        }
        public void setQuality(himss.MetricDataQuality quality) {
            this.quality = quality;
        }
        public InstanceHandle_t getInstanceHandle() {
            return instanceHandle;
        }
        public himss.MetricDataQuality getQuality() {
            return quality;
        }
        
        public void setData(final NumericFx data) {
            if(null != this.data) {
                this.data.source_timestampProperty().removeListener(this);
            }
            this.data = data;
            if(null != data) {
                data.source_timestampProperty().addListener(this);
                writer.write(quality, instanceHandle);
            }
        }
        public NumericFx getData() {
            return data;
        }

        @Override
        public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
            if(Math.random()<0.05) {
                quality.validated_data = !quality.validated_data;
            }
            writer.write(quality, instanceHandle);
        }


    }
    
    private static final Map<String, Integer> numericCodes = new HashMap<String, Integer>();
    private static Integer numericCode(String name) {
        Integer code = numericCodes.get(name);
        if(null == code) {
            try {
                Class<?> cls = Class.forName("himss."+name);
                code = (Integer) cls.getField("VALUE").get(null);
            } catch (Exception e) {
                // Sentinel value so we don't try again
                code = -1;
                
            }
            
            numericCodes.put(name, code);
        }
        return null == code || code < 0 ? null: code;
    }
    
    private final Map<InstanceHandle_t, Holder> instances = new HashMap<InstanceHandle_t, Holder>();
    
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    
    public void setNumericList(NumericFxList numericList) {
        this.numericList = numericList;
    }
    
    private void add(final NumericFx data) {
        Holder h = instances.get(data.getHandle());
        if(null == h) {
            Integer code = numericCode(data.getMetric_id());
            if(null == code) {
                // log
                return;
                
            }
            
            log.info("registered " + data.getMetric_id() + " with " + code);
            h = new Holder(writer);
            himss.MetricDataQuality q = new himss.MetricDataQuality();
            q.udi = data.getUnique_device_identifier().substring(0, Math.min(16, data.getUnique_device_identifier().length()));
            q.instance_id = data.getInstance_id();
            q.metric_id = code;
            q.validated_data = false;
            h.setQuality(q);
            h.setInstanceHandle(writer.register_instance(q));
            instances.put(new InstanceHandle_t(data.getHandle()), h);
            h.setData(data);
        }
    }
    
    private void remove(final NumericFx data) {
        Holder h = instances.remove(data.getHandle());
        if(null != h) {
            h.setData(null);
            writer.unregister_instance(h.getQuality(), h.getInstanceHandle());
        }
    }
    
    private final OnListChange<NumericFx> listener = new OnListChange<>((t)->add(t), null, (t)->remove(t)); 
    
    public void start() {
        topic = TopicUtil.findOrCreateTopic(publisher.get_participant(), himss.MetricDataQualityTopic.VALUE, himss.MetricDataQualityTypeSupport.class);
        writer = (MetricDataQualityDataWriter) publisher.create_datawriter_with_profile(topic, "ice_library", "himss", null, StatusKind.STATUS_MASK_NONE);
        numericList.addListener(listener);
        numericList.forEach((t)->add(t));
    }
    public void stop() {
        numericList.removeListener(listener);
        numericList.forEach((t)->remove(t));
        publisher.delete_datawriter(writer);
        publisher.get_participant().delete_topic(topic);
    }
}
