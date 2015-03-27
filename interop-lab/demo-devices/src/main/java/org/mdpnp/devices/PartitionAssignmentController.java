package org.mdpnp.devices;


import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import ice.DeviceIdentity;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartitionAssignmentController implements MDSConnectivityAdapter.MDSConnectivityListener {

    private static final Logger log = LoggerFactory.getLogger(PartitionAssignmentController.class);

    private long lastPartitionFileTime = 0L;

    public void checkForPartitionFile() {
        File f = new File("device.partition");
        checkForPartitionFile(f);
    }

    void checkForPartitionFile(File f) {

        if (f == null || !f.exists()) {
            // File once existed
            if (lastPartitionFileTime != 0L) {
                setPartition(new String[0]);
                lastPartitionFileTime = 0L;
            } else {
                // No file and it never existed
            }
        } else if (f.canRead() && f.lastModified() > lastPartitionFileTime) {
            try {
                List<String> partition = new ArrayList<String>();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while (null != (line = br.readLine())) {
                    if (!line.startsWith("#"))
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

        if (null == partition) {
            partition = new String[0];
        }

        boolean same = partition.length == pQos.partition.name.size();

        if (same) {
            for (int i = 0; i < partition.length; i++) {
                if (!partition[i].equals(pQos.partition.name.get(i))) {
                    same = false;
                    break;
                }
            }
        }

        if (!same) {
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

    private final MDSConnectivityAdapter connectivityAdapter;
    private final DeviceIdentity deviceIdentity;
    private final Publisher publisher;
    private final Subscriber subscriber;


    public void start() {
        connectivityAdapter.start();
        connectivityAdapter.addConnectivityListener(this);
    }

    public void shutdown() {
        connectivityAdapter.removeConnectivityListener(this);
        connectivityAdapter.shutdown();
    }


    public PartitionAssignmentController(DeviceIdentity d, EventLoop e, Publisher p, Subscriber s) {
        deviceIdentity = d;
        publisher = p;
        subscriber = s;
        connectivityAdapter = new MDSConnectivityAdapter(e, publisher, subscriber);

    }

    /**
     * default ctor only useful to tests of disconnected functionality
     */
    PartitionAssignmentController(DeviceIdentity d) {
        deviceIdentity = d;
        publisher = null;
        subscriber = null;
        connectivityAdapter = null;
    }

    @Override
    public void handleDataSampleEvent(MDSConnectivityAdapter.MDSConnectivityEvent evt) {
        ice.MDSConnectivityObjective  o = (ice.MDSConnectivityObjective )evt.getSource();
        if(deviceIdentity.unique_device_identifier.equals(o.unique_device_identifier)) {
            String p[] = {o.partition};
            setPartition(p);
        }
    }
}
