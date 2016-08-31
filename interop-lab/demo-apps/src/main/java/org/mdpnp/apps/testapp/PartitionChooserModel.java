package org.mdpnp.apps.testapp;

import java.util.*;

import com.google.common.eventbus.EventBus;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.mdpnp.apps.testapp.patient.PatientInfo;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.PartitionAssignmentController;

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

    private MDSHandler mdsHandler;

    private final String udi;
    private final Subscriber subscriber;
    private final Publisher publisher;
    private final EventBus eventBus;
    private final ObservableList<PatientInfo> patients = FXCollections.observableArrayList();


    public PartitionChooserModel(String udi, Subscriber subscriber, Publisher publisher, EventBus eventBus) {
        this.udi = udi;
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.eventBus = eventBus;
    }
        
    public void activate(List<String> partitions) {
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

        // now broadcast to the external world
        announceNewAssociation(partitions);
    }

    private void announceNewAssociation(List<String> l) {

        if(mdsHandler != null) {

            String p = PartitionAssignmentController.findMRNPartition(l);
            if(p == null) p = l.isEmpty() ? "" : l.get(0);

            ice.MDSConnectivity val = new ice.MDSConnectivity();
            val.unique_device_identifier = udi;
            val.partition = p;
            mdsHandler.publish(val);
        }
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


    public PartitionChooserModel initModel(final ObservableList<PatientInfo> data) {

        patients.add(NOBODY);
        patients.add(ANYBODY);
        data.forEach((x)->patients.add(x));

        data.addListener(new ListChangeListener<PatientInfo>() {

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends PatientInfo> c) {
                while(c.next()) {
                    if(c.wasAdded()) {
                        c.getAddedSubList().forEach((x)->patients.add(x));
                    }
                    if(c.wasRemoved()) {
                        c.getRemoved().forEach((x)->patients.remove(x));
                    }
                }
            }

        });

        return this;
    }

    public ObservableList<PatientInfo> getPatients() {
        return patients;
    }

    static public final PatientInfo NOBODY  = new PatientInfo("", "", "<Unassigned>", PatientInfo.Gender.U, new Date());
    static public final PatientInfo ANYBODY = new PatientInfo("*", "", "<Anybody>", PatientInfo.Gender.U, new Date());


    public PartitionChooserModel setMdsHandler(MDSHandler mdsHandler) {
        this.mdsHandler = mdsHandler;
        return this;
    }

    public void changePartition(PatientInfo pi) {
        if(null != pi) {
            final List<String> partitions = new ArrayList<String>(1);
            if(PartitionChooserModel.NOBODY.equals(pi)||PartitionChooserModel.ANYBODY.equals(pi)) {
                partitions.add(pi.getMrn());
            } else {
                String s = PartitionAssignmentController.toPartition(pi.getMrn());
                partitions.add(s);
            }
            activate(partitions);
        }
    }
}
