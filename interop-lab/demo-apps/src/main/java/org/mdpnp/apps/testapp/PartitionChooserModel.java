package org.mdpnp.apps.testapp;

import java.util.*;

import com.google.common.eventbus.EventBus;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

public class PartitionChooserModel {

    public static class PartitionChooserChangeEvent extends EventObject {
        public PartitionChooserChangeEvent(PartitionChooserModel source) {
            super(source);
        }

        public List<String> getPartitions() {
            List<String> p = new ArrayList<>();
            ((PartitionChooserModel)getSource()).get(p);
            return p;
        }

        public boolean partitionIsPatient() {
            List<String> l = getPartitions();
            boolean b = l.size()==1&&l.get(0).startsWith("MRN");
            return b;
        }
    }

    private final Subscriber subscriber;
    private final Publisher publisher;
    private final EventBus eventBus;

    public PartitionChooserModel(Subscriber subscriber, Publisher publisher, EventBus eventBus) {
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.eventBus = eventBus;
    }
        
    public void set(List<String> partitions) {
        SubscriberQos sQos = new SubscriberQos();
        
        subscriber.get_qos(sQos);
        sQos.partition.name.clear();
        for(String s : partitions) {
            sQos.partition.name.add(s);
        }
        subscriber.set_qos(sQos);

        PublisherQos pQos = new PublisherQos();
        publisher.get_qos(pQos);
        pQos.asynchronous_publisher.thread.priority = Thread.NORM_PRIORITY;
        pQos.asynchronous_publisher.asynchronous_batch_thread.priority = Thread.NORM_PRIORITY;
        pQos.partition.name.clear();
        for(String s : partitions) {
            pQos.partition.name.add(s);
        }
        publisher.set_qos(pQos);

        eventBus.post(new PartitionChooserChangeEvent(this));
    }
    
    public void get(List<String> list) {
        list.clear();
        Set<String> parts = new HashSet<String>();
        if(subscriber != null) {
            SubscriberQos qos = new SubscriberQos();
            subscriber.get_qos(qos);
            for(int i = 0; i < qos.partition.name.size(); i++) {
                parts.add((String)qos.partition.name.get(i));
            }
        }
        if(publisher != null) {
            PublisherQos qos = new PublisherQos();
            publisher.get_qos(qos);
            for(int i = 0; i < qos.partition.name.size(); i++) {
                parts.add((String)qos.partition.name.get(i));
            }
        }
        list.addAll(parts);
    }
    
    public Publisher getPublisher() {
        return publisher;
    }
    public Subscriber getSubscriber() {
        return subscriber;
    }
}
