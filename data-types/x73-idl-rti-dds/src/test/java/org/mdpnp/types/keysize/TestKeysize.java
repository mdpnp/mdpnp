package org.mdpnp.types.keysize;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.lang.Thread.UncaughtExceptionHandler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.GuardCondition;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;

public class TestKeysize  {

    private static DomainParticipant subscriberParticipant, publisherParticipant;

    private static Topic publisherLargeStringKeysTopic, subscriberLargeStringKeysTopic;
    private static ContentFilteredTopic subscriberLargeStringCFTKeysTopic;
    private static Topic publisherIntegerKeysTopic, subscriberIntegerKeysTopic;

    private static LargeStringKeysDataReader largeStringKeysDataReader;
    private static LargeStringKeysDataReader largeStringKeysCFTDataReader;
    private static LargeStringKeysDataWriter largeStringKeysDataWriter;

    private static IntegerKeysDataReader integerKeysDataReader;
    private static IntegerKeysDataWriter integerKeysDataWriter;

    private static final String LARGE_STRING_KEYS_TOPIC = "LargeStringKeys";
    private static final String INTEGER_KEYS_TOPIC = "IntegerKeys";

    private static Throwable thrownException;
    private static Thread thrownFrom;

    private static final ThreadGroup threadGroup = new ThreadGroup("Tests Group") {
        public void uncaughtException(Thread t, Throwable e) {
            thrownFrom = t;
            thrownException = e;
        };
    };


    @BeforeClass
    public static void setUpBeforeClass() {
        assertTrue(DDS.init());
        DomainParticipantQos qos = new DomainParticipantQos();

        DomainParticipantFactory.get_instance().get_default_participant_qos(qos);

        publisherParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("publisher participant null", publisherParticipant);
        subscriberParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("subscriber participant null", subscriberParticipant);

        LargeStringKeysTypeSupport.register_type(publisherParticipant, LargeStringKeysTypeSupport.get_type_name());
        LargeStringKeysTypeSupport.register_type(subscriberParticipant, LargeStringKeysTypeSupport.get_type_name());

        IntegerKeysTypeSupport.register_type(publisherParticipant, IntegerKeysTypeSupport.get_type_name());
        IntegerKeysTypeSupport.register_type(subscriberParticipant, IntegerKeysTypeSupport.get_type_name());

        publisherLargeStringKeysTopic = publisherParticipant.create_topic(LARGE_STRING_KEYS_TOPIC, LargeStringKeysTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("publisherLargeStringKeysTopic null", publisherLargeStringKeysTopic);
        subscriberLargeStringKeysTopic = subscriberParticipant.create_topic(LARGE_STRING_KEYS_TOPIC, LargeStringKeysTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("subscriberLargeStringKeysTopic null", subscriberLargeStringKeysTopic);
        StringSeq params = new StringSeq();
        params.add("'"+fill((char)0, 128)+"'");
        params.add("'"+fill('A', 64)+"'");
        subscriberLargeStringCFTKeysTopic = subscriberParticipant.create_contentfilteredtopic(LARGE_STRING_KEYS_TOPIC+"Filtered", subscriberLargeStringKeysTopic, "unique_device_identifier = %0 AND metric_id = %1", params);

        publisherIntegerKeysTopic = publisherParticipant.create_topic(INTEGER_KEYS_TOPIC, IntegerKeysTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("publisherIntegerKeysTopic null", publisherIntegerKeysTopic);
        subscriberIntegerKeysTopic = subscriberParticipant.create_topic(INTEGER_KEYS_TOPIC, IntegerKeysTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("subscriberIntegerKeysTopic null", subscriberIntegerKeysTopic);

        DataReaderQos drQos = new DataReaderQos();
        subscriberParticipant.get_default_datareader_qos(drQos);
        // In this case I want to read all the samples published
        drQos.history.depth = SAMPLES;
        drQos.history.kind = HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS;
        drQos.resource_limits.initial_samples = SAMPLES;
        drQos.reader_resource_limits.initial_infos = SAMPLES;

        // Reliability (Requested and Offered) so that I can terminate gracefully knowing that
        // my reader has received the samples from my writer
        drQos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        subscriberParticipant.set_default_datareader_qos(drQos);
        largeStringKeysDataReader = (LargeStringKeysDataReader) subscriberParticipant.create_datareader(subscriberLargeStringKeysTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("largeStringKeysDataReader null", largeStringKeysDataReader);
        largeStringKeysCFTDataReader = (LargeStringKeysDataReader) subscriberParticipant.create_datareader(subscriberLargeStringCFTKeysTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("largeStringKeysCFTDataReader null", largeStringKeysCFTDataReader);
        integerKeysDataReader = (IntegerKeysDataReader) subscriberParticipant.create_datareader(subscriberIntegerKeysTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("integerKeysDataReader null", integerKeysDataReader);

        DataWriterQos dwQos = new DataWriterQos();
        publisherParticipant.get_default_datawriter_qos(dwQos);
        dwQos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        dwQos.reliability.max_blocking_time.sec = Duration_t.DURATION_INFINITE_SEC;
        dwQos.reliability.max_blocking_time.nanosec = Duration_t.DURATION_INFINITE_NSEC;
        dwQos.resource_limits.initial_samples = SAMPLES;
        dwQos.resource_limits.initial_instances = 10;
        dwQos.resource_limits.initial_samples = SAMPLES;
        publisherParticipant.set_default_datawriter_qos(dwQos);

        largeStringKeysDataWriter = (LargeStringKeysDataWriter) publisherParticipant.create_datawriter(publisherLargeStringKeysTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("largeStringKeysDataWriter null", largeStringKeysDataWriter);
        integerKeysDataWriter = (IntegerKeysDataWriter) publisherParticipant.create_datawriter(publisherIntegerKeysTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        assertNotNull("integerKeysDataWriter null", integerKeysDataWriter);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        publisherParticipant.delete_datawriter(integerKeysDataWriter);
        publisherParticipant.delete_datawriter(largeStringKeysDataWriter);

        integerKeysDataWriter = null;
        largeStringKeysDataWriter = null;

        subscriberParticipant.delete_datareader(integerKeysDataReader);
        subscriberParticipant.delete_datareader(largeStringKeysDataReader);
        subscriberParticipant.delete_datareader(largeStringKeysCFTDataReader);

        integerKeysDataReader = null;
        largeStringKeysDataReader = null;
        largeStringKeysCFTDataReader = null;

        subscriberParticipant.delete_contentfilteredtopic(subscriberLargeStringCFTKeysTopic);
        subscriberLargeStringCFTKeysTopic = null;

        publisherParticipant.delete_topic(publisherIntegerKeysTopic);
        publisherParticipant.delete_topic(publisherLargeStringKeysTopic);

        publisherIntegerKeysTopic = null;
        publisherLargeStringKeysTopic = null;

        subscriberParticipant.delete_topic(subscriberIntegerKeysTopic);
        subscriberParticipant.delete_topic(subscriberLargeStringKeysTopic);

        subscriberIntegerKeysTopic = null;
        subscriberLargeStringKeysTopic = null;

        LargeStringKeysTypeSupport.unregister_type(publisherParticipant, LargeStringKeysTypeSupport.get_type_name());
        LargeStringKeysTypeSupport.unregister_type(subscriberParticipant, LargeStringKeysTypeSupport.get_type_name());

        IntegerKeysTypeSupport.unregister_type(publisherParticipant, IntegerKeysTypeSupport.get_type_name());
        IntegerKeysTypeSupport.unregister_type(subscriberParticipant, IntegerKeysTypeSupport.get_type_name());

        DomainParticipantFactory.get_instance().delete_participant(publisherParticipant);
        DomainParticipantFactory.get_instance().delete_participant(subscriberParticipant);

        publisherParticipant = null;
        subscriberParticipant = null;

        DomainParticipantFactory.finalize_instance();
    }

    @Before
    public void setUp() throws Exception {
        thrownException = null;
        thrownFrom = null;
    }

    @After
    public void tearDown() throws Exception {

    }

    private static final int SAMPLES = 10000;

    private static long[] fill(char s, int length) {
        long[] newLongs = new long[length / (Long.SIZE / Byte.SIZE)];
        for(int i = 0; i < newLongs.length; i++) {
            for(int j = 0; j < (Long.SIZE / Byte.SIZE); j++) {
                newLongs[i] |= (0xFFL & s) << (j*Byte.SIZE);
            }
        }
//        System.out.println(Long.toHexString(newLongs[0]));
        return newLongs;
    }

    private static String fill(String s, int length) {
        StringBuilder sb = new StringBuilder();
        while(sb.length() < length) {
            sb.append(s);
        }
        return sb.toString();
    }

    // a - b
    private static final long microdiff(Time_t a, Time_t b) {
        long microsecs = (a.sec - b.sec) * 1000000L;
        microsecs += (a.nanosec/1000L - b.nanosec/1000L); // signed!
        return microsecs;
    }
    @Test
    public void testLargeStringKeys3() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "NEW NEW LargeString", denseLargeStringInstances);
    }

    @Test
    public void testLargeStringKeys2() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "NEW LargeString", denseLargeStringInstances);
    }

    @Test
    public void testLargeStringKeys() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
//        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys", denseLargeStringInstances);
    }

    @Test
    public void testIntegerKeys2() throws Exception {
        runATest(integerKeysDataWriter, integerKeysDataReader, "NEW Integer", integerInstances);
    }


    @Test
    public void testIntegerKeys() throws Exception {
        runATest(integerKeysDataWriter, integerKeysDataReader, "IntegerKeys", integerInstances);
    }

    @Test
    public void testLargeStringQCKeys() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys (w/QueryCondition)", denseLargeStringQCInstances);
    }

    @Test
    public void testLargeStringCFTKeys() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysCFTDataReader, "LargeStringKeys (w/ContentFilteredTopic)", denseLargeStringInstances);
    }

    interface Instances {
        Object[] createInstances();
        void updateInstance(Object instance);
        ReadCondition readCondition(DataReader reader);
    }

    private abstract class DenseLargeStringInstances implements Instances {
        private final java.util.Random random = new java.util.Random();
        @Override
        public Object[] createInstances() {
            LargeStringKeys[] instances = new LargeStringKeys[1];
            for(int i = 0; i < instances.length; i++) {
                instances[i] = (LargeStringKeys) LargeStringKeys.create();
                instances[i].unique_device_identifier = fill(Integer.toString(i), 128);
                instances[i].metric_id = fill("A", 64);
            }
            return instances;
        }

        @Override
        public void updateInstance(Object instance) {
            ((LargeStringKeys)instance).value = random.nextFloat();
        }
    }

    private abstract class SparseLargeStringInstances implements Instances {
        private final java.util.Random random = new java.util.Random();
        @Override
        public Object[] createInstances() {
            LargeStringKeys[] instances = new LargeStringKeys[1];
            for(int i = 0; i < instances.length; i++) {
                instances[i] = (LargeStringKeys) LargeStringKeys.create();
                instances[i].unique_device_identifier = fill(Integer.toString(i), 32);
                instances[i].metric_id = fill("A", 8);
            }
            return instances;
        }

        @Override
        public void updateInstance(Object instance) {
            ((LargeStringKeys)instance).value = random.nextFloat();
        }
    }

    private final Instances denseLargeStringQCInstances = new DenseLargeStringInstances() {
        @Override
        public ReadCondition readCondition(DataReader reader) {
            StringSeq params = new StringSeq();
            params.add("'"+fill("0", 128)+"'");
            params.add("'"+fill("A", 64)+"'");
            return reader.create_querycondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "unique_device_identifier = %0 AND metric_id = %1", params);
        }
    };



    private final Instances denseLargeStringInstances = new DenseLargeStringInstances() {
        @Override
        public ReadCondition readCondition(DataReader reader) {
            return reader.create_readcondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
        }
    };

    private final Instances sparseLargeStringInstances = new SparseLargeStringInstances() {
        @Override
        public ReadCondition readCondition(DataReader reader) {
            return reader.create_readcondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
        }
    };

    private final Instances integerInstances = new Instances() {
        private final java.util.Random random = new java.util.Random();
        @Override
        public Object[] createInstances() {
            IntegerKeys[] instances = new IntegerKeys[1];
            for(int i = 0; i < instances.length; i++) {
                instances[i] = (IntegerKeys) IntegerKeys.create();
                instances[i].unique_device_identifier = fill((char) ('0'+i), 128);
                instances[i].metric_id = fill('A', 64);
            }
            return instances;
        }

        @Override
        public void updateInstance(Object instance) {
            ((IntegerKeys)instance).value = random.nextFloat();
        }

        @Override
        public ReadCondition readCondition(DataReader reader) {
            return reader.create_readcondition(SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
        }
    };

    private static void runATest(final DataWriter writer, final DataReader reader, final String name, final Instances instances) throws Exception {
        // Block until matched
        long now = System.currentTimeMillis();
        long giveup = now + 5000L;
        PublicationMatchedStatus pms = new PublicationMatchedStatus();
        writer.get_publication_matched_status(pms);
        while(now < giveup && pms.current_count <= 0) {
            Thread.sleep(100);
            writer.get_publication_matched_status(pms);
            now = System.currentTimeMillis();
        }
        System.out.println("PMS:"+pms);

        if(now >= giveup) {
            throw new Exception("Timed out waiting for a publication match");
        }

        final GuardCondition guardCondition = new GuardCondition();

        Thread writingThread = new Thread(threadGroup, new Runnable() {
            public void run() {
                Object[] lsk = instances.createInstances();
                InstanceHandle_t[] handles = new InstanceHandle_t[lsk.length];

                for(int i = 0; i < lsk.length; i++) {
                    handles[i] = writer.register_instance_untyped(lsk[i]);
                }
                int wrote = 0;
                for(int i = 0; i < SAMPLES; i++) {
                    for(int j = 0; j < lsk.length; j++) {
                        instances.updateInstance(lsk[j]);
                        writer.write_w_timestamp_untyped(lsk[j], handles[j], Time_t.now());
                        wrote++;
                    }
                }
                writer.flush();
                System.out.println("Wrote " + wrote);
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                for(int i = 0; i < lsk.length; i++) {
                    writer.unregister_instance_untyped(lsk[i], handles[i]);
                }

                guardCondition.set_trigger_value(true);

            }
        });
        Thread readingThread = new Thread(threadGroup, new Runnable() {
            public void run() {
                LargeStringKeysSeq data_seq = new LargeStringKeysSeq();
                SampleInfoSeq info_seq = new SampleInfoSeq();
                WaitSet ws = new WaitSet();
                ConditionSeq cond_seq = new ConditionSeq();
                Duration_t duration = new Duration_t(Duration_t.DURATION_INFINITY_SEC, Duration_t.DURATION_INFINITY_NSEC);
//                StringSeq params = new StringSeq();
//                params.add("'"+fill("0", 128)+"'");
//                params.add("'"+fill("A", 64)+"'");
//                QueryCondition qc = largeStringKeysDataReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "unique_device_identifier = %0 AND metric_id = %1", params);
                ReadCondition rc = instances.readCondition(reader);
                ws.attach_condition(rc);
                ws.attach_condition(guardCondition);
                long basis = 0L;
                long count = 0L;

                for(;;) {
                    ws.wait(cond_seq, duration);
                    if(cond_seq.contains(rc)) {
                        try {
                            reader.take_w_condition_untyped(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, rc);
                            for(int i = 0; i < info_seq.size(); i++) {
                                SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);

                                if(sampleInfo.valid_data) {
                                    basis += microdiff(Time_t.now(), sampleInfo.source_timestamp);
                                    count++;
                                } else {
                                    System.out.println("No sample data");
                                }
                            }
                            System.out.println("Total Read:"+count);
                        } finally {
                            reader.return_loan_untyped(data_seq, info_seq);
                        }
                    }
                    if(count == SAMPLES) {
                        break;
                    }
//                    if(cond_seq.contains(guardCondition)) {
//                        break;
//                    }

                }
                ws.detach_condition(guardCondition);
                ws.detach_condition(rc);
                reader.delete_readcondition(rc);
                ws.delete();
                assertEquals(SAMPLES, count);
                System.out.println(name +" time: "+(basis/count)+" microseconds");
            }
        });
        readingThread.start();
        writingThread.start();
        readingThread.join();
        writingThread.join();
        if(null != thrownException) {
            throw new Exception("Thrown from " + thrownFrom.getName(), thrownException);
        }
//        assertNull("Exception thrown in thread " + (null==thrownFrom?"":thrownFrom.getName()), thrownException);
    }

    @Test
    public void testSparseLargeStringKeys() throws Exception {
        runATest(largeStringKeysDataWriter, largeStringKeysDataReader, "LargeStringKeys (sparse)", sparseLargeStringInstances);
    }


}