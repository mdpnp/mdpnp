package org.mdpnp.types.polymorphism.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mdpnp.types.polymorphism.AttributeValueAssertion;
import org.mdpnp.types.polymorphism.Bar;
import org.mdpnp.types.polymorphism.DIMT_VMO;
import org.mdpnp.types.polymorphism.DIMT_VMODataReader;
import org.mdpnp.types.polymorphism.DIMT_VMODataWriter;
import org.mdpnp.types.polymorphism.DIMT_VMOTypeSupport;
import org.mdpnp.types.polymorphism.ExtObjRelationEntry;
import org.mdpnp.types.polymorphism.Foo;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
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
import com.rti.dds.type.builtin.StringTypeSupport;

public class TestPolymorphism implements DataReaderListener, DataWriterListener {

	private DomainParticipant subscriberParticipant, publisherParticipant;
	private Topic publisherTopic, subscriberTopic;
	private DIMT_VMODataReader dataReader;
	private DIMT_VMODataWriter dataWriter;
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		assertTrue(DDS.init());

		DomainParticipantQos qos = new DomainParticipantQos();
		
		DomainParticipantFactory.get_instance().get_default_participant_qos(qos);
		
		publisherParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("publisher participant null", publisherParticipant);
		subscriberParticipant = DomainParticipantFactory.get_instance().create_participant(0, qos, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("subscriber participant null", subscriberParticipant);
		
		DIMT_VMOTypeSupport.register_type(publisherParticipant, DIMT_VMOTypeSupport.get_type_name());
		DIMT_VMOTypeSupport.register_type(subscriberParticipant, DIMT_VMOTypeSupport.get_type_name());
		
		publisherTopic = publisherParticipant.create_topic("FOOBAR", DIMT_VMOTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("publisher topic null", publisherTopic);
		subscriberTopic = subscriberParticipant.create_topic("FOOBAR", DIMT_VMOTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
		assertNotNull("subscriber topic null", subscriberTopic);
		
		dataReader = (DIMT_VMODataReader) subscriberParticipant.create_datareader(subscriberTopic, Subscriber.DATAREADER_QOS_DEFAULT, this, StatusKind.DATA_AVAILABLE_STATUS);
		assertNotNull("data reader null", dataReader);
		dataWriter = (DIMT_VMODataWriter) publisherParticipant.create_datawriter(publisherTopic, Publisher.DATAWRITER_QOS_DEFAULT, this, StatusKind.PUBLICATION_MATCHED_STATUS);
		assertNotNull("data writer null", dataWriter);
	}

	@After
	public void tearDown() throws Exception {
		subscriberParticipant.delete_datareader(dataReader);
		publisherParticipant.delete_datawriter(dataWriter);
		
		dataReader = null;
		dataWriter = null;
		
		publisherParticipant.delete_topic(publisherTopic);
		subscriberParticipant.delete_topic(subscriberTopic);
		
		publisherTopic = null;
		subscriberTopic = null;
		
		DIMT_VMOTypeSupport.unregister_type(publisherParticipant, DIMT_VMOTypeSupport.get_type_name());
		DIMT_VMOTypeSupport.unregister_type(subscriberParticipant, DIMT_VMOTypeSupport.get_type_name());
		
		StringTypeSupport.unregister_type(publisherParticipant, StringTypeSupport.get_type_name());
		StringTypeSupport.unregister_type(subscriberParticipant, StringTypeSupport.get_type_name());
		
		DomainParticipantFactory.get_instance().delete_participant(publisherParticipant);
		DomainParticipantFactory.get_instance().delete_participant(subscriberParticipant);
		
		publisherParticipant = null;
		subscriberParticipant = null;
		
		DomainParticipantFactory.finalize_instance();
	}
	
	@Test
	public void test() {
		long giveUp = System.currentTimeMillis() + WAIT_SECONDS * 1000L;
		
		
		synchronized(this) {
			while(!readTheData && System.currentTimeMillis()<=giveUp) {
				try {
					this.wait(1000L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		assertTrue("Data not read within " + WAIT_SECONDS + " seconds", readTheData);
	}

	private volatile boolean readTheData = false;
	private static final int WAIT_SECONDS = 5;
	
	@Override
	public synchronized void on_data_available(DataReader arg0) {
		DIMT_VMODataReader reader = (DIMT_VMODataReader) arg0;

		SampleInfo si = new SampleInfo();
		DIMT_VMO vmo = (DIMT_VMO) DIMT_VMO.create();
		
		boolean anyValidData = false;
		
		try {
			reader.take_next_sample(vmo, si);

			while(si.valid_data) {
				System.out.println("Received:"+listExtObjTypes(vmo));
				anyValidData |= si.valid_data;
				reader.take_next_sample(vmo, si);
			}
		} catch(RETCODE_NO_DATA seriously) {
			// I expect this ... it's normal ... why use a structured exception here?
		} finally {		
			readTheData |= anyValidData;
			this.notifyAll();
		}
	}

	@Override
	public void on_liveliness_changed(DataReader arg0,
			LivelinessChangedStatus arg1) {
	}

	@Override
	public void on_requested_deadline_missed(DataReader arg0,
			RequestedDeadlineMissedStatus arg1) {
	}

	@Override
	public void on_requested_incompatible_qos(DataReader arg0,
			RequestedIncompatibleQosStatus arg1) {
	}

	@Override
	public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
	}

	@Override
	public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
	}

	@Override
	public void on_subscription_matched(DataReader arg0,
			SubscriptionMatchedStatus arg1) {
	}

	@Override
	public void on_application_acknowledgment(DataWriter arg0,
			AcknowledgmentInfo arg1) {
	}

	@Override
	public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
		return null;
	}

	@Override
	public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
	}

	@Override
	public void on_destination_unreachable(DataWriter arg0,
			InstanceHandle_t arg1, Locator_t arg2) {
	}

	@Override
	public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
	}

	@Override
	public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
	}

	@Override
	public void on_offered_deadline_missed(DataWriter arg0,
			OfferedDeadlineMissedStatus arg1) {
	}

	@Override
	public void on_offered_incompatible_qos(DataWriter arg0,
			OfferedIncompatibleQosStatus arg1) {
	}

	private static final List<Class<?>> listExtObjTypes(DIMT_VMO vmo) {
		List<Class<?>> types = new ArrayList<Class<?>>();
		Iterator<?> itr = vmo.ext_obj_relations.userData.iterator();
		while(itr.hasNext()) {
			ExtObjRelationEntry entry = (ExtObjRelationEntry) itr.next();
			Iterator<?> itr2 = entry.relation_attributes.userData.iterator();
			while(itr2.hasNext()) {
				AttributeValueAssertion ava = (AttributeValueAssertion) itr2.next();
				types.add(ava.attribute_value.getClass());
			}
		}
		return types;
	}
	
	@Override
	public void on_publication_matched(final DataWriter arg0,
			PublicationMatchedStatus arg1) {
		if(arg1.current_count_change>0) {
			TestExecutor.get().execute(new Runnable() {
				public void run() {
					DIMT_VMO vmo = (DIMT_VMO) DIMT_VMO.create();
					
//					vmo.class_id = 5;
					vmo.dimhandle = 15;
					vmo.label_string = "JEFF LABEL";
//					vmo.name_binding_id = 20;
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
					System.out.println("Published:"+listExtObjTypes(vmo));
					((DIMT_VMODataWriter)arg0).write(vmo,  InstanceHandle_t.HANDLE_NIL);

					arg0.flush();
				}
			});
		}
	}

	@Override
	public void on_reliable_reader_activity_changed(DataWriter arg0,
			ReliableReaderActivityChangedStatus arg1) {
	}

	@Override
	public void on_reliable_writer_cache_changed(DataWriter arg0,
			ReliableWriterCacheChangedStatus arg1) {
	}

	@Override
	public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
	}

}