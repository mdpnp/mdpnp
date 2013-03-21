package org.mdpnp.qmdi.types.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mdpnp.qmdi.types.AttributeList;
import org.mdpnp.qmdi.types.AttributeTypeSupport;
import org.mdpnp.qmdi.types.AttributeValueAssertion;
import org.mdpnp.qmdi.types.Bar;
import org.mdpnp.qmdi.types.DIMT_VMO;
import org.mdpnp.qmdi.types.DIMT_VMODataReader;
import org.mdpnp.qmdi.types.DIMT_VMODataWriter;
import org.mdpnp.qmdi.types.DIMT_VMOTypeSupport;
import org.mdpnp.qmdi.types.ExtObjRelationEntry;
import org.mdpnp.qmdi.types.Foo;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.TransportBuiltinKind;
import com.rti.dds.publication.AcknowledgmentInfo;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.ReliableReaderActivityChangedStatus;
import com.rti.dds.publication.ReliableWriterCacheChangedStatus;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.dds.type.builtin.StringTypeSupport;
import com.rti.ndds.config.LogVerbosity;
import com.rti.ndds.config.Logger;

public class TestNumeric implements DataReaderListener, DataWriterListener {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() {
		
		assertTrue(DDS.init());
		
		
		DomainParticipantFactoryQos factqos = new DomainParticipantFactoryQos();
		DomainParticipantFactory.get_instance().get_qos(factqos);
//		factqos.logging.verbosity = LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL;
		DomainParticipantFactory.get_instance().set_qos(factqos);
		
//		Logger.get_instance().set_verbosity(LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
		DomainParticipantQos qos = new DomainParticipantQos();
		
		DomainParticipantFactory.get_instance().get_default_participant_qos(qos);
		
		System.err.println(qos);
//		qos.discovery.initial_peers.clear();
//		qos.discovery.initial_peers.add("udpv4://239.255.0.1");
//		qos.discovery.initial_peers.add("udpv4://127.0.0.1");
//		qos.discovery.accept_unknown_peers = true;
//		
//		qos.transport_builtin.mask = TransportBuiltinKind.UDPv4;
//		qos.resource_limits.type_object_max_serialized_length = 8192;
//		qos.resource_limits.type_object_max_deserialized_length = 8192;
//		System.err.println(qos);
		
		DomainParticipant publisherParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos/*DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT*/, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("publisher participant null", publisherParticipant);
		DomainParticipant subscriberParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos/*DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT*/, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("subscriber participant null", subscriberParticipant);
		
		StringTypeSupport.register_type(publisherParticipant, StringTypeSupport.get_type_name());
		StringTypeSupport.register_type(subscriberParticipant, StringTypeSupport.get_type_name());
		
		DIMT_VMOTypeSupport.register_type(publisherParticipant, DIMT_VMOTypeSupport.get_type_name());
		DIMT_VMOTypeSupport.register_type(subscriberParticipant, DIMT_VMOTypeSupport.get_type_name());
		
		
		
		Topic publisherTopic = publisherParticipant.create_topic("FOOBAR", /*StringTypeSupport.get_type_name()*/DIMT_VMOTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("publisher topic null", publisherTopic);
		Topic subscriberTopic = subscriberParticipant.create_topic("FOOBAR", /*StringTypeSupport.get_type_name()*/DIMT_VMOTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("subscriber topic null", subscriberTopic);
		
		DIMT_VMODataReader dataReader = (DIMT_VMODataReader) subscriberParticipant.create_datareader(subscriberTopic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.STATUS_MASK_ALL);
//		StringDataReader dataReader = (StringDataReader) subscriberParticipant.create_datareader(subscriberTopic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.STATUS_MASK_ALL);
		assertNotNull("data reader null", dataReader);
		DIMT_VMODataWriter dataWriter = (DIMT_VMODataWriter) publisherParticipant.create_datawriter(publisherTopic, Publisher.DATAWRITER_QOS_DEFAULT, this, StatusKind.STATUS_MASK_ALL);
//		final StringDataWriter dataWriter = (StringDataWriter) publisherParticipant.create_datawriter(publisherTopic, Publisher.DATAWRITER_QOS_DEFAULT, this, StatusKind.STATUS_MASK_ALL);
		assertNotNull("data writer null", dataWriter);
		long giveUp = System.currentTimeMillis() + WAIT_SECONDS * 1000L;
		

//		new Thread(new Runnable() {
//			public void run() {
//				dataWriter.write("HERE IS A STRING FOR YOU", InstanceHandle_t.HANDLE_NIL);
//				dataWriter.flush();
//			}
//		}).start();
		
		synchronized(this) {
			while(!readTheData && System.currentTimeMillis()<=giveUp) {
				try {
//					Thread.sleep(1000L);
					this.wait(1000L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
//		System.out.println("DESTRUCTION PHASE COMMENCES");
		
		subscriberParticipant.delete_datareader(dataReader);
		publisherParticipant.delete_datawriter(dataWriter);
		
		publisherParticipant.delete_topic(publisherTopic);
		subscriberParticipant.delete_topic(subscriberTopic);
		
		DIMT_VMOTypeSupport.unregister_type(publisherParticipant, DIMT_VMOTypeSupport.get_type_name());
		DIMT_VMOTypeSupport.unregister_type(subscriberParticipant, DIMT_VMOTypeSupport.get_type_name());
		
		StringTypeSupport.unregister_type(publisherParticipant, StringTypeSupport.get_type_name());
		StringTypeSupport.unregister_type(subscriberParticipant, StringTypeSupport.get_type_name());
		
		DomainParticipantFactory.get_instance().delete_participant(publisherParticipant);
		DomainParticipantFactory.get_instance().delete_participant(subscriberParticipant);
		
		DomainParticipantFactory.finalize_instance();
		
		assertTrue("Data not read within " + WAIT_SECONDS + " seconds", readTheData);
	}

	private volatile boolean readTheData = false;
	private static final int WAIT_SECONDS = 5;
	
	@Override
	public synchronized void on_data_available(DataReader arg0) {
		System.err.println("on_data_available");
		DIMT_VMODataReader reader = (DIMT_VMODataReader) arg0;
//		StringDataReader reader = (StringDataReader) arg0;
		SampleInfo si = new SampleInfo();
		DIMT_VMO vmo = (DIMT_VMO) DIMT_VMO.create();
		
		boolean anyValidData = false;
		
		try {

//			String s = reader.take_next_sample(si);
			reader.take_next_sample(vmo, si);
		
			
			
			while(si.valid_data) {
				anyValidData |= si.valid_data;
//				System.err.println(s);
				System.err.println(vmo);
				reader.take_next_sample(vmo, si);
//				s = reader.take_next_sample(si);
			}
		} catch(RETCODE_NO_DATA seriously) {
			// I expect this ... it's normal ... why use a structured exception here?
		} finally {		
			readTheData |= anyValidData;
			this.notifyAll();
		}
//		this.notifyAll();
		
	}

	@Override
	public void on_liveliness_changed(DataReader arg0,
			LivelinessChangedStatus arg1) {
		System.err.println("on_liveliness_changed:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_requested_deadline_missed(DataReader arg0,
			RequestedDeadlineMissedStatus arg1) {
		System.err.println("on_requested_deadline_missed:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_requested_incompatible_qos(DataReader arg0,
			RequestedIncompatibleQosStatus arg1) {
		System.err.println("on_requested_incompatible_qos:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
		System.err.println("on_sample_lost:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
		System.err.println("on_sample_rejected:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_subscription_matched(DataReader arg0,
			SubscriptionMatchedStatus arg1) {
		
		System.err.println("on_subscription_matched:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_application_acknowledgment(DataWriter arg0,
			AcknowledgmentInfo arg1) {
		System.err.println("on_application_acknowledgment:"+arg0+"\t"+arg1);
	}

	@Override
	public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
		System.err.println("on_data_request:"+arg0+"\t"+arg1);
		return null;
	}

	@Override
	public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
		System.err.println("on_data_return:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_destination_unreachable(DataWriter arg0,
			InstanceHandle_t arg1, Locator_t arg2) {
		System.err.println("on_destination_unreachable:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
		System.err.println("on_instance_replaced:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
		System.err.println("on_liveliness_lost:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_offered_deadline_missed(DataWriter arg0,
			OfferedDeadlineMissedStatus arg1) {
		System.err.println("on_offered_deadline_missed:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_offered_incompatible_qos(DataWriter arg0,
			OfferedIncompatibleQosStatus arg1) {
		System.err.println("on_offered_incompatible_qos:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_publication_matched(final DataWriter arg0,
			PublicationMatchedStatus arg1) {
		System.err.println("on_publication_matched:"+arg0+"\t"+arg1);
		if(arg1.current_count_change>0) {
			TestExecutor.get().execute(new Runnable() {
				public void run() {
					DIMT_VMO vmo = (DIMT_VMO) DIMT_VMO.create();
					
					vmo.class_id = 5;
					vmo.dimhandle = 15;
					vmo.label_string = "JEFF LABEL";
					vmo.name_binding_id = 20;
					vmo.object_type.code = 44;
					vmo.object_type.partition = 66;
					{
						ExtObjRelationEntry e = (ExtObjRelationEntry) ExtObjRelationEntry.create();
						e.related_object = 100;
						e.relation_type = 5000;
						Foo f = (Foo) Foo.create();
						f.fooValue = 777;
						
						AttributeValueAssertion ava = (AttributeValueAssertion) AttributeValueAssertion.create();
						ava.attribute_id = 999;
						ava.attribute_value = f;
						
						e.relation_attributes.userData.add(ava);
						vmo.ext_obj_relations.userData.add(e);
					}		
					
					{
						ExtObjRelationEntry e = (ExtObjRelationEntry) ExtObjRelationEntry.create();
						e.related_object = 200;
						e.relation_type = 10000;
						Bar b = (Bar) Bar.create();
						b.barValue = "888";
						AttributeValueAssertion ava = (AttributeValueAssertion) AttributeValueAssertion.create();
						ava.attribute_id = 222;
						ava.attribute_value = b;
						e.relation_attributes.userData.add(ava);
						vmo.ext_obj_relations.userData.add(e);
					}
					((DIMT_VMODataWriter)arg0).write(vmo,  InstanceHandle_t.HANDLE_NIL);
//					((StringDataWriter)arg0).write("HERE IS A STRING FOR YOU", InstanceHandle_t.HANDLE_NIL);
//					System.err.println("WROTE A STRING");
					arg0.flush();
				}
			});
//			((StringDataWriter)arg0).write("HERE IS A STRING FOR YOU", InstanceHandle_t.HANDLE_NIL);
//			arg0.flush();
		}
	}

	@Override
	public void on_reliable_reader_activity_changed(DataWriter arg0,
			ReliableReaderActivityChangedStatus arg1) {
		System.err.println("on_reliable_reader_activity_changed:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_reliable_writer_cache_changed(DataWriter arg0,
			ReliableWriterCacheChangedStatus arg1) {
		System.err.println("on_reliable_writer_cache_changed:"+arg0+"\t"+arg1);
	}

	@Override
	public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
		System.err.println("on_sample_removed:"+arg0+"\t"+arg1);
	}

}