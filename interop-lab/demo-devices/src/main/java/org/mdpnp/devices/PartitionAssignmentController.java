package org.mdpnp.devices;


import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartitionAssignmentController {

    private static final Logger log = LoggerFactory.getLogger(PartitionAssignmentController.class);

    private long lastPartitionFileTime = 0L;

    public PartitionAssignmentController(Publisher publisher, Subscriber subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    public void start() {}
    public void shutdown() {}

    public void checkForPartitionFile() {
        File f = new File("device.partition");
        checkForPartitionFile(f);
    }

    void checkForPartitionFile(File f) {

        if(f==null || !f.exists()) {
            // File once existed
            if(lastPartitionFileTime!=0L) {
                setPartition(new String[0]);
                lastPartitionFileTime = 0L;
            } else {
                // No file and it never existed
            }
        } else if(f.canRead() && f.lastModified()>lastPartitionFileTime) {
            try {
                List<String> partition = new ArrayList<String>();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while(null != (line = br.readLine())) {
                    if(!line.startsWith("#"))
                        partition.add(line.trim());
                }
                br.close();
                setPartition(partition.toArray(new String[0]));
            } catch (FileNotFoundException e) {
                log.error("Reading partition info", e);
            } catch (IOException e) {
                log.error("Reading partition info", e);
            }

            lastPartitionFileTime = f.lastModified();
        }
    }


    public String[] getPartition() {
        PublisherQos pQos = new PublisherQos();
        publisher.get_qos(pQos);
        String[] partition = new String[pQos.partition.name.size()];
        for (int i = 0; i < partition.length; i++) {
            partition[i] = (String) pQos.partition.name.get(i);
        }
        return partition;
    }

    public void addPartition(String partition) {
        List<String> currentPartition = new ArrayList<String>(Arrays.asList(getPartition()));
        currentPartition.add(partition);
        setPartition(currentPartition.toArray(new String[0]));
    }

    public void removePartition(String partition) {
        List<String> currentPartition = new ArrayList<String>(Arrays.asList(getPartition()));
        currentPartition.remove(partition);
        setPartition(currentPartition.toArray(new String[0]));
    }

    public void setPartition(String[] partition) {
        PublisherQos pQos = new PublisherQos();
        SubscriberQos sQos = new SubscriberQos();
        publisher.get_qos(pQos);
        subscriber.get_qos(sQos);

        if(null == partition) {
            partition = new String[0];
        }

        boolean same = partition.length == pQos.partition.name.size();

        if(same) {
            for(int i = 0; i < partition.length; i++) {
                if(!partition[i].equals(pQos.partition.name.get(i))) {
                    same = false;
                    break;
                }
            }
        }

        if(!same) {
            log.info("Changing partition to " + Arrays.toString(partition));
            pQos.partition.name.clear();
            sQos.partition.name.clear();
            pQos.partition.name.addAll(Arrays.asList(partition));
            sQos.partition.name.addAll(Arrays.asList(partition));
            publisher.set_qos(pQos);
            subscriber.set_qos(sQos);
        } else {
            log.info("Not changing to same partition " + Arrays.toString(partition));
        }
    }

    protected final Publisher publisher;
    protected final Subscriber subscriber;

    /*
        private final ReadCondition rc;


        deviceConnectivity = new DeviceConnectivity();
        deviceConnectivity.type = getConnectionType();
        deviceConnectivity.state = ice.ConnectionState.Disconnected;

        deviceConnectivityObjective = (DeviceConnectivityObjective) DeviceConnectivityObjective.create();
        DeviceConnectivityObjectiveTypeSupport.register_type(domainParticipant, DeviceConnectivityObjectiveTypeSupport.get_type_name());
        deviceConnectivityObjectiveTopic = domainParticipant.create_topic(DeviceConnectivityObjectiveTopic.VALUE,
                DeviceConnectivityObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceConnectivityObjectiveReader = (DeviceConnectivityObjectiveDataReader) subscriber.create_datareader_with_profile(
                deviceConnectivityObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        rc = deviceConnectivityObjectiveReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE);

        final DeviceConnectivityObjectiveSeq data_seq = new DeviceConnectivityObjectiveSeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();

        eventLoop.addHandler(rc, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    deviceConnectivityObjectiveReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                    for (int i = 0; i < info_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if (si.valid_data) {
                            DeviceConnectivityObjective dco = (DeviceConnectivityObjective) data_seq.get(i);
                            if (deviceIdentity.unique_device_identifier.equals(dco.unique_device_identifier)) {

                                if (dco.connected) {
                                    log.info("Issuing connect for " + deviceIdentity.unique_device_identifier + " to " + dco.target);
                                    connect(dco.target);

                                } else {
                                    log.info("Issuing disconnect for " + deviceIdentity.unique_device_identifier);
                                    disconnect();
                                }
                            }
                        }
                    }

                } catch (RETCODE_NO_DATA noData) {

                } finally {
                    deviceConnectivityObjectiveReader.return_loan(data_seq, info_seq);
                }
            }

        });

     */

    /* stop

        eventLoop.removeHandler(rc);

        deviceConnectivityObjectiveReader.delete_readcondition(rc);
        subscriber.delete_datareader(deviceConnectivityObjectiveReader);
        domainParticipant.delete_topic(deviceConnectivityObjectiveTopic);
        DeviceConnectivityObjectiveTypeSupport.unregister_type(domainParticipant, DeviceConnectivityObjectiveTypeSupport.get_type_name());



     */
}
