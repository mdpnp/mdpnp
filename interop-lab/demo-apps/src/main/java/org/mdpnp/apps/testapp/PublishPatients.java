package org.mdpnp.apps.testapp;

import ice.Patient;
import ice.PatientDataWriter;
import ice.PatientTypeSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.qos.IceQos;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.topic.Topic;

public class PublishPatients {
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length > 1) {
            List<Patient> patients = new ArrayList<Patient>();
            {
                File patientFile = new File(args[1]);
                if(!patientFile.exists() || !patientFile.canRead()) {
                    System.err.println("Unable to open file " + args[1]);
                }
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(patientFile), "UTF-8"));
                String line = null;
                while (null!=(line = reader.readLine())) {
                    ice.Patient p = new ice.Patient();
                    String[] fields = line.split("\t");
                    p.mrn = fields[0];
                    p.family_name = fields[1];
                    p.given_name = fields[2];
                    patients.add(p);
                }
                reader.close();
            }
            IceQos.loadAndSetIceQos();
            
            
            
            DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(Integer.parseInt(args[0]), DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            PatientTypeSupport.register_type(participant, PatientTypeSupport.get_type_name());
            Topic patientTopic = participant.create_topic(ice.PatientTopic.VALUE, PatientTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            PublisherQos pQos = new PublisherQos();
            participant.get_default_publisher_qos(pQos);
            pQos.partition.name.add("*");
            Publisher publisher = participant.create_publisher(pQos, null, StatusKind.STATUS_MASK_NONE);

            PatientDataWriter writer = (PatientDataWriter) publisher.create_datawriter_with_profile(patientTopic, QosProfiles.ice_library, QosProfiles.device_identity, null, StatusKind.STATUS_MASK_NONE);
            
            for(ice.Patient p : patients) {
                System.out.println(p);
                writer.write(p, InstanceHandle_t.HANDLE_NIL);
            }
            
            final CountDownLatch latch = new CountDownLatch(1);
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("Type quit to exit");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String line;
                    try {
                        while(null != (line=reader.readLine())) {
                            if("quit".equals(line)) {
                                break;
                            } else {
                                System.err.println("Unknown command " + line);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    latch.countDown();
                }
            }));
            
            latch.await();
            
            publisher.delete_datawriter(writer);
            participant.delete_publisher(publisher);
            participant.delete_topic(patientTopic);
            DomainParticipantFactory.get_instance().delete_participant(participant);
            DomainParticipantFactory.finalize_instance();
            System.out.println("ALL DONE");
            System.exit(0);
        } else {
            System.err.println("Usage: PublishPatients [domain id] [patient file]");
        }
    }
}
