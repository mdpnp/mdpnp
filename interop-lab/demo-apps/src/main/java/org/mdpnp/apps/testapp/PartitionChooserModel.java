package org.mdpnp.apps.testapp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

public class PartitionChooserModel {
    private final Subscriber subscriber;
    private final Publisher publisher;
    
    public PartitionChooserModel(Subscriber subscriber, Publisher publisher) {
        this.subscriber = subscriber;
        this.publisher = publisher;
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
