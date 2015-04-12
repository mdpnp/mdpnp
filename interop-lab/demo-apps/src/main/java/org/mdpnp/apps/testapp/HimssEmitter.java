package org.mdpnp.apps.testapp;

import himss.MetricDataQualityDataWriter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.testapp.validate.Validation;
import org.mdpnp.apps.testapp.validate.ValidationOracle;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

public class HimssEmitter {
    private Publisher publisher;
    private ValidationOracle validationOracle;
    private Topic topic;
    private himss.MetricDataQualityDataWriter writer;
    
    private final Logger log = LoggerFactory.getLogger(HimssEmitter.class);
    
    private static final class Holder implements ChangeListener<Date> {
        private himss.MetricDataQuality quality = new himss.MetricDataQuality();
        private InstanceHandle_t instanceHandle;
        private Validation data;
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
        
        public void setData(final Validation data) {
            if(null != this.data) {
                this.data.getNumeric().source_timestampProperty().removeListener(this);
            }
            this.data = data;
            if(null != data) {
                data.getNumeric().source_timestampProperty().addListener(this);
                writer.write(quality, instanceHandle);
            }
        }
        public Validation getData() {
            return data;
        }

        @Override
        public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
            quality.validated_data = data.isValidated();
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
    
    public void setValidationOracle(ValidationOracle validationOracle) {
        this.validationOracle = validationOracle;
    }
    
    private void add(final Validation data) {
        Holder h = instances.get(data.getNumeric().getHandle());
        if(null == h) {
            Integer code = numericCode(data.getNumeric().getMetric_id());
            if(null == code) {
                // log
                return;
                
            }
            
            log.debug("registered " + data.getNumeric().getMetric_id() + " with " + code);
            h = new Holder(writer);
            himss.MetricDataQuality q = new himss.MetricDataQuality();
            q.udi = data.getNumeric().getUnique_device_identifier().substring(0, Math.min(16, data.getNumeric().getUnique_device_identifier().length()));
            q.instance_id = data.getNumeric().getInstance_id();
            q.metric_id = code;
            q.validated_data = data.validatedProperty().get();
            h.setQuality(q);
            h.setInstanceHandle(writer.register_instance(q));
            instances.put(new InstanceHandle_t(data.getNumeric().getHandle()), h);
            h.setData(data);
        }
    }
    
    private void remove(final Validation data) {
        Holder h = instances.remove(data.getNumeric().getHandle());
        if(null != h) {
            h.setData(null);
            writer.unregister_instance(h.getQuality(), h.getInstanceHandle());
        }
    }
    
    private final OnListChange<Validation> listener = new OnListChange<>((t)->add(t), null, (t)->remove(t)); 
    
    public void start() {
        topic = TopicUtil.findOrCreateTopic(publisher.get_participant(), himss.MetricDataQualityTopic.VALUE, himss.MetricDataQualityTypeSupport.class);
        writer = (MetricDataQualityDataWriter) publisher.create_datawriter_with_profile(topic, "ice_library", "himss", null, StatusKind.STATUS_MASK_NONE);
        validationOracle.addListener(listener);
        validationOracle.forEach((t)->add(t));
    }
    public void stop() {
        validationOracle.removeListener(listener);
        validationOracle.forEach((t)->remove(t));
        publisher.delete_datawriter(writer);
        publisher.get_participant().delete_topic(topic);
    }
}
