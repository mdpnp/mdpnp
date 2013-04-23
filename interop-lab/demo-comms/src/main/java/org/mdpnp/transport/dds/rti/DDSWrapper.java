package org.mdpnp.transport.dds.rti;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.MutableIdentifiableUpdate;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.comms.data.image.ImageUpdate;
import org.mdpnp.comms.data.numeric.NumericUpdate;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.data.textarray.TextArrayUpdate;
import org.mdpnp.comms.data.waveform.WaveformUpdate;
import org.mdpnp.devices.io.util.StateMachine;
import org.mdpnp.transport.Wrapper;
import org.mdpnp.transport.dds.rti.RTICLibrary.DDS_DataReaderListener.DDS_DataReaderListener_LivelinessChangedCallback;
import org.mdpnp.transport.dds.rti.RTICLibrary.DDS_DataWriterListener.DDS_DataWriterListener_LivelinessLostCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class DDSWrapper implements Wrapper /*implements RTICLibrary.DDS_DataReaderListener.DDS_DataReaderListener_LivelinessChangedCallback*/ {
	
	private final boolean ownedParticipant;
	private final Pointer domainParticipant;
	private final Pointer subscriber, publisher;
	
	private static class DataHandler implements GatewayListener, RTICLibrary.DDS_DataReaderListener.DDS_DataReaderListener_DataAvailableCallback, DDS_DataReaderListener_LivelinessChangedCallback, DDS_DataWriterListener_LivelinessLostCallback {
		private final Class<?> updateClass;
		private final Class<?> mutableUpdateImplClass;
		private final Constructor<?> mutableConstructor;
		private final MutableIdentifiableUpdate<?> mutableUpdate;
		private static final Logger log = LoggerFactory.getLogger(DDSWrapper.DataHandler.class);
		private final Pointer typeCode;
		private final Pointer topic;
		private final Pointer reader, writer;
		private final Pointer read_data, write_data;
		private final Pointer typeSupport;
		private String typeCodeName;
		private final RTICLibrary.DDS_SampleInfo sampleInfo = new RTICLibrary.DDS_SampleInfo();
		
		private final RTICLibrary.DDS_DataReaderListener dataReaderListener = new RTICLibrary.DDS_DataReaderListener();
		private final RTICLibrary.DDS_DataWriterListener dataWriterListener = new RTICLibrary.DDS_DataWriterListener();
		private final Gateway gateway;
		
		private final IntByReference exception = new IntByReference();
		
		private final DDSInterface dds = new DDSInterface();
		
//		protected final DDS_DynamicDataTypeProperty_t dynamicDataTypeProperty = new DDS_DynamicDataTypeProperty_t();
		
		public DataHandler(Gateway gateway, Pointer domainParticipant, Pointer subscriber, Pointer publisher, Class<? extends IdentifiableUpdate<?>> updateClass) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			Native.setCallbackThreadInitializer(this, new CallbackThreadInitializer(true, false, "on_data_available handler"));
			dataReaderListener.on_data_available = this;
			dataReaderListener.on_liveliness_changed = this;
			dataReaderListener.write();
			
			dataWriterListener.on_liveliness_lost = this;
			dataWriterListener.write();
			
			this.gateway = gateway;
			this.updateClass = DDSInterface.findPersistent(updateClass).getClazz();
//			this.mutableUpdateClass = Class.forName(this.updateClass.getPackage().getName()+".Mutable"+this.updateClass.getSimpleName());
			this.mutableUpdateImplClass = Class.forName(this.updateClass.getPackage().getName()+".Mutable"+this.updateClass.getSimpleName()+"Impl");
			this.mutableConstructor = this.mutableUpdateImplClass.getConstructor();
			this.mutableUpdate = (MutableIdentifiableUpdate<?>) mutableConstructor.newInstance();
			
			this.typeCode = dds.buildTypeCode(updateClass);
			typeCodeName = RTICLibrary.INSTANCE.DDS_TypeCode_name(this.typeCode, exception);
			DDSInterface.checkException(exception);
			
			
//			dynamicDataTypeProperty.data.buffer_max_size = DDSInterface.OCTET_SEQUENCE_EXTENT;
//			dynamicDataTypeProperty.write();
			typeSupport = RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_new(this.typeCode, RTICLibrary.DDS_DYNAMIC_DATA_TYPE_PROPERTY_DEFAULT);
			if(null == typeSupport) {
				throw new RuntimeException("Unable to new typesupport");
			}
			
			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_register_type(typeSupport, domainParticipant, RTICLibrary.INSTANCE.DDS_String_dup(typeCodeName)));

			this.read_data = RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_create_data(typeSupport);
			if(null == read_data) {
				throw new RuntimeException("Unable to create new read_data");
			}
			
			this.write_data = RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_create_data(typeSupport);
			if(null == write_data) {
				throw new RuntimeException("Unable to create new write_data");
			}
			
			this.topic = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_topic(domainParticipant, RTICLibrary.INSTANCE.DDS_String_dup(updateClass.getName()), RTICLibrary.INSTANCE.DDS_String_dup(typeCodeName) , RTICLibrary.DDS_TOPIC_QOS_DEFAULT, Pointer.NULL, RTICLibrary.DDS_STATUS_MASK_NONE);
			if(null == topic) {
				throw new RuntimeException("Unable to create topic");
				
			}
			
			
			
			
			
			//  
			
//			RTICLibrary.INSTANCE.DDS_StringSeq_initialize(filterParameters.getPointer());
//			filterParameters.read();
//			RTICLibrary.INSTANCE.DDS_StringSeq_ensure_length(filterParameters.getPointer(), 2, 2);
//			filterParameters.read();
//			RTICLibrary.INSTANCE.DDS_StringSeq_get_reference(filterParameters.getPointer(), 0).setPointer(0, RTICLibrary.INSTANCE.DDS_String_dup("*"));
//			RTICLibrary.INSTANCE.DDS_StringSeq_get_reference(filterParameters.getPointer(), 1).setPointer(0, RTICLibrary.INSTANCE.DDS_String_dup("*"));
//			filterParameters.write();
//			
//			this.content_filtered_topic = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_contentfilteredtopic(domainParticipant, updateClass.getName(), topic, " (%0 = '*' or getSource = %0) and (%1 = '*' or getTarget = %1) ", filterParameters.getPointer());
//			if(null == content_filtered_topic) {
//				throw new RuntimeException("Unable to create content_filtered_topic");
//			}
			
//			Native.setProtected(true);
//			RTICLibrary.DDS_DataWriterQos dataWriterQos = new RTICLibrary.DDS_DataWriterQos();
//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Publisher_get_default_datawriter_qos(publisher, dataWriterQos.getPointer()));
//			dataWriterQos.read();
//			dataWriterQos.liveliness.lease_duration = new RTICLibrary.DDS_Duration_t(5, 0);
//			dataWriterQos.liveliness.kind = RTICLibrary.DDS_AUTOMATIC_LIVELINESS_QOS;
//			dataWriterQos.write();
////			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_set_default_datareader_qos(subscriber, dataReaderQos.getPointer()));
			
			this.writer = RTICLibrary.INSTANCE.DDS_Publisher_create_datawriter(publisher, topic, RTICLibrary.DDS_DATAWRITER_QOS_DEFAULT, dataWriterListener.getPointer(), RTICLibrary.DDS_LIVELINESS_LOST_STATUS);
			
			
//			this.writer = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_datawriter(domainParticipant, topic, RTICLibrary.DDS_DATAWRITER_QOS_DEFAULT, Pointer.NULL, RTICLibrary.DDS_STATUS_MASK_NONE);
			if(null == writer) {
				throw new RuntimeException("Unable to create writer");
			}

			// this is actually a macro that adds an offset of 8 bytes
			
//			 * struct DDS_TopicWrapperI {
//    /*i                                                                                                                                                                                             
//     * Pointer to Entity supertype instance.                                                                                                                                                        
//     */
//    DDS_Entity*            _as_Entity;
//
//    /*i                                                                                                                                                                                             
//     * Pointer to TopicDescription supertype instance.                                                                                                                                              
//     */
//    DDS_TopicDescription*  _as_TopicDescription;
//
//    /*i                                                                                                                                                                                             
//     * Pointer to opaque Topic implementation fields.                                                                                                                                               
//     */
//    struct DDS_TopicImpl* _impl;
//};

			 
//			Pointer topicDescription = RTICLibrary.INSTANCE.DDS_Topic_as_topicdescription(topic);
//			if(null == topicDescription) {
//				throw new RuntimeException("Unable to widen topic to topicdescription");
//			}
			
//			listener.write();
			
			
//			RTICLibrary.DDS_DataReaderQos dataReaderQos = new RTICLibrary.DDS_DataReaderQos();
//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_get_default_datareader_qos(subscriber, dataReaderQos.getPointer()));
//			dataReaderQos.read();
//			dataReaderQos.liveliness.lease_duration = new RTICLibrary.DDS_Duration_t(5, 0);
//			dataReaderQos.liveliness.kind = RTICLibrary.DDS_AUTOMATIC_LIVELINESS_QOS;
//			dataReaderQos.write();
//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_set_default_datareader_qos(subscriber, dataReaderQos.getPointer()));
			
//			this.reader = RTICLibrary.INSTANCE.DDS_Subscriber_create_datareader(subscriber, content_filtered_topic.getPointerArray(0)[0], RTICLibrary.DDS_DATAREADER_QOS_DEFAULT, listener.getPointer(), RTICLibrary.DDS_DATA_AVAILABLE_STATUS);
			this.reader = RTICLibrary.INSTANCE.DDS_Subscriber_create_datareader(subscriber, topic.getPointerArray(0)[1], RTICLibrary.DDS_DATAREADER_QOS_DEFAULT, dataReaderListener.getPointer(), RTICLibrary.DDS_DATA_AVAILABLE_STATUS | RTICLibrary.DDS_LIVELINESS_CHANGED_STATUS);
//			this.reader = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_datareader(domainParticipant, topic.getPointerArray(0)[1], RTICLibrary.DDS_DATAREADER_QOS_DEFAULT, listener.getPointer(), RTICLibrary.DDS_DATA_AVAILABLE_STATUS);
			if(null == reader) {
				throw new RuntimeException("Unable to create reader");
			}
			gateway.addListener(this);
		}
		
		
		public enum ReadState {
			Reading,
			TearingDown,
			Idle,
			Destroyed
		}
		
		public enum WriteState {
			Writing,
			TearingDown,
			Idle,
			Destroyed
		}
		
		private static final WriteState[][] writeLegalTransitions = new WriteState[][] {
			{WriteState.Idle, WriteState.Writing},
			{WriteState.Idle, WriteState.TearingDown},
			{WriteState.Writing, WriteState.Idle},
			{WriteState.TearingDown, WriteState.Destroyed}
		};
		
		private static final ReadState[][] readLegalTransitions = new ReadState[][] {
			{ReadState.Idle, ReadState.Reading},
			{ReadState.Idle, ReadState.TearingDown},
			{ReadState.Reading, ReadState.Idle},
			{ReadState.TearingDown, ReadState.Destroyed}
		};
		
		private final StateMachine<ReadState> readStateMachine = new StateMachine<ReadState>(readLegalTransitions, ReadState.Idle); 
		private final StateMachine<WriteState> writeStateMachine = new StateMachine<WriteState>(writeLegalTransitions, WriteState.Idle); 
		
		public void tearDown(Pointer domainParticipant, Pointer subscriber, Pointer publisher) {
			if(!writeStateMachine.transitionWhenLegal(WriteState.TearingDown, 5000L)) {
				throw new RuntimeException("Unable to tearDown for writer");
			}
			if(!readStateMachine.transitionWhenLegal(ReadState.TearingDown, 5000L)) {
				throw new RuntimeException("Unable to tearDown for reader");
			}
			
			try {
				// Currently set_listener(NULL) deadlocks .. must change on_data_available threading
	//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DataReader_set_listener(reader, Pointer.NULL, RTICLibrary.DDS_STATUS_MASK_NONE));
				gateway.removeListener(this);
				
				// Currently DataReader_delete_contained_entities deadlocks .. must change on_data_available threading
				// TODO this may be fixed ... must test
	//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DataReader_delete_contained_entities(reader));
				
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_delete_datareader(subscriber, this.reader));
	//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_datareader(domainParticipant, this.reader));
				
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Publisher_delete_datawriter(publisher, this.writer));
	//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_datawriter(domainParticipant, this.writer));
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_topic(domainParticipant, topic));
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_unregister_type(typeSupport, domainParticipant, RTICLibrary.INSTANCE.DDS_String_dup(typeCodeName)));
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_delete_data(typeSupport, read_data));
				
				DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_delete_data(typeSupport, write_data));
				RTICLibrary.INSTANCE.DDS_DynamicDataTypeSupport_delete(typeSupport);
				Pointer factory = RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_instance();
				RTICLibrary.INSTANCE.DDS_TypeCodeFactory_delete_tc(factory, typeCode, exception);
				DDSInterface.checkException(exception);
	//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_contentfilteredtopic(domainParticipant, content_filtered_topic));
			} finally {
				if(!readStateMachine.transitionWhenLegal(ReadState.Destroyed, 5000L)) {
					throw new RuntimeException("Unable to enter Destroyed state for reader");
				}
				if(!writeStateMachine.transitionWhenLegal(WriteState.Destroyed, 5000L)) {
					throw new RuntimeException("Unable to enter Destroyed state for writer");
				}
			}
		}
		@Override
		public void on_data_available(Pointer listener_data, Pointer reader) {
			RTICLibrary.DDS_SampleInfo sampleInfo = this.sampleInfo;
			// If we are in the TearingDown or Destroyed state we are relying on this
			// timeout to prevent a deadlock as entities cannot be destroyed while callbacks
			// are being processed
			if(!readStateMachine.transitionWhenLegal(ReadState.Reading, 1000L)) {
				log.error("Could not enter Reading state for on_data_available");
				return;
			}
			try {
				
				while(true) {
					int retcode = RTICLibrary.INSTANCE.DDS_DynamicDataReader_take_next_sample(reader, read_data, sampleInfo.getPointer());
					if(RTICLibrary.DDS_RETCODE_NO_DATA==retcode) {
						return;
					}
					DDSInterface.checkReturnCode(retcode);
					sampleInfo.read();
					if(sampleInfo.valid_data!=0) {
//						MutableIdentifiableUpdate<?> iu;
						try {
//							iu = (MutableIdentifiableUpdate<?>) mutableConstructor.newInstance();
							dds.get(read_data, mutableUpdate);
							gateway.update(this, mutableUpdate);
						} catch (Throwable e) {
							log.error("Error firing update", e);
						}
						
					
					}
				}
			} finally {
				if(!readStateMachine.transitionWhenLegal(ReadState.Idle, 5000L)) {
					log.error("could not enter Idle state after on_data_available");
				}
			}
		}

		@Override
		public void update(IdentifiableUpdate<?> update) {
			if(updateClass.isAssignableFrom(update.getClass())) {
				if(!writeStateMachine.transitionWhenLegal(WriteState.Writing, 10000L)) {
					log.error("Could not enter Writing state for update");
					return;
				}
				try {
					dds.set(update, write_data);
					DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicDataWriter_write(writer, write_data, RTICLibrary.DDS_HANDLE_NIL));
				} finally {
					if(!writeStateMachine.transitionWhenLegal(WriteState.Idle, 5000L)) {
						log.error("could not enter the Idle state after update");
					}
				}
			}
		}

		@Override
		public void on_liveliness_changed(Pointer listener_data,
				Pointer reader, Pointer status) {
			RTICLibrary.DDS_LivelinessChangedStatus liveliness = new RTICLibrary.DDS_LivelinessChangedStatus(status);
			log.info("alive_count="+liveliness.alive_count+",alive_count_change="+liveliness.alive_count_change+",not_alive_count="+liveliness.not_alive_count+",not_alive_count_change="+liveliness.not_alive_count_change);

			
		}

		@Override
		public void on_liveliness_lost(Pointer listener_data, Pointer writer,
				Pointer status) {
			log.info("writer liveliness_lost called");
			
		}
	}

	public DDSWrapper(int domainId, Gateway gateway) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(null, domainId, Role.Promiscuous, gateway);
	}
	
	public DDSWrapper(int domainId, Role role, Gateway gateway) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(null, domainId, role, gateway);
	}
	
	public DDSWrapper(Pointer domainParticipant, Role role, Gateway gateway) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(domainParticipant, RTICLibrary.INSTANCE.DDS_DomainParticipant_get_domain_id(domainParticipant), role, gateway);
	}
	
	private final List<DataHandler> handlers = new ArrayList<DataHandler>();
	private static final Class<?>[] classes = new Class<?>[] {
		TextUpdate.class,
		NumericUpdate.class,
		TextArrayUpdate.class,
		EnumerationUpdate.class,
		WaveformUpdate.class,
		IdentifierArrayUpdate.class,
		ImageUpdate.class
	};
//	private final Role role;
//	private final RTICLibrary.DDS_DomainParticipantQos participantQos = new RTICLibrary.DDS_DomainParticipantQos();
	private final RTICLibrary.DDS_SubscriberQos subscriberQos = new RTICLibrary.DDS_SubscriberQos();
	private final RTICLibrary.DDS_PublisherQos publisherQos = new RTICLibrary.DDS_PublisherQos();
	
	private final DDSInterface wrapperDDS = new DDSInterface();
	private final Logger log = LoggerFactory.getLogger(DDSWrapper.class);
	@SuppressWarnings("unchecked")
    private DDSWrapper(Pointer domainParticipant, int domainId, Role role, Gateway gateway) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	    
	    String domainIdStr=System.getProperty("DDS_WRAPPER_DOMAIN_ID"); 
	    if(null != domainIdStr) {
	        try {
	            domainId = Integer.parseInt(domainIdStr);
	            log.info("Using DDS_WRAPPER_DOMAIN_ID="+domainId);
	        } catch (NumberFormatException nfe) {
	            log.warn(nfe.getMessage(), nfe);
	        }
	    }
	    
//		this.role = role;
		//		this.gateway = gateway;
		if(null == domainParticipant) {
			Pointer factory = RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_get_instance();
			if(null == factory) {
				throw new RuntimeException("Unable to get DomainParticipantFactory");
			}
			
//			RTICLibrary.DDS_DomainParticipantQos qos = new RTICLibrary.DDS_DomainParticipantQos();
			
//			Memory mem = new Memory(3000);
//			RTICLibrary.INSTANCE.DDS_DomainParticipantQos_initialize(participantQos.getPointer());
//			participantQos.read();
//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_get_default_participant_qos(factory, participantQos.getPointer()));
//			participantQos.read();
//			participantQos.transport_builtin.mask = RTICLibrary.DDS_TRANSPORTBUILTIN_UDPv4;
//			participantQos.write();
			
//			mem.setInt(120, RTICLibrary.DDS_TRANSPORTBUILTIN_UDPv4);
			
//			mem.setByte(388, RTICLibrary.RTI_TRUE);
//			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_set_default_participant_qos(factory, mem));
			
			
			domainParticipant = RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_create_participant(factory, domainId, /*participantQos.getPointer()*/RTICLibrary.DDS_PARTICIPANT_QOS_DEFAULT, Pointer.NULL, 0);
			if(null == domainParticipant) {
				throw new RuntimeException("Unable to create participant");
			}
		    ownedParticipant = true;
		} else {
			ownedParticipant = false;
		}
		
		this.domainParticipant = domainParticipant;
		
//		this.subscriberListener.as_datareaderlistener.on_liveliness_changed = this;
//		this.subscriberListener.write();
		
		

		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_SubscriberQos_initialize(subscriberQos.getPointer()));
		subscriberQos.read();
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_get_default_subscriber_qos(domainParticipant, subscriberQos.getPointer()));
		subscriberQos.read();
		
//		if(0 == RTICLibrary.INSTANCE.DDS_StringSeq_initialize(subscriberQos.partition.name.getPointer())) {
//			throw new RuntimeException("Unable to initialize sequence");
//		}
		
		wrapperDDS.arrayToSequence(role.getReceivePartitions(), subscriberQos.partition.name);
		
//		subscriberQos.write();
		
		Pointer subscriber = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_subscriber(domainParticipant, subscriberQos.getPointer() /* RTICLibrary.DDS_SUBSCRIBER_QOS_DEFAULT */, Pointer.NULL, RTICLibrary.DDS_STATUS_MASK_NONE);
		if(subscriber == null) {
			throw new RuntimeException("Unable to create subscriber");
		}
		this.subscriber = subscriber;
		
		
		
		
//		RTICLibrary.DDS_DataReaderQos dataReaderQos = new RTICLibrary.DDS_DataReaderQos();
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_get_default_datareader_qos(subscriber, dataReaderQos.getPointer()));
//		dataReaderQos.read();
//		dataReaderQos.liveliness.lease_duration.sec = 5;
//		dataReaderQos.liveliness.lease_duration.nanosec = 0;
//		dataReaderQos.liveliness.lease_duration.write();
//		dataReaderQos.liveliness.kind = RTICLibrary.DDS_AUTOMATIC_LIVELINESS_QOS;
//		dataReaderQos.liveliness.write();
//		dataReaderQos.write();
		
//		this.dataReaderQos = dataReaderQos.getPointer();
		
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_set_default_datareader_qos(subscriber, dataReaderQos.getPointer()));
		
		
		
		
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_PublisherQos_initialize(publisherQos.getPointer()));
		publisherQos.read();
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_get_default_publisher_qos(domainParticipant, publisherQos.getPointer()));
		publisherQos.read();
		
		wrapperDDS.arrayToSequence(role.getSendPartitions(), publisherQos.partition.name);
		

		
//		DDS_Sequence name = publisherQos.partition.name;
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_PublisherQos_initialize(name.getPointer()));
//		name.read();
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_StringSeq_initialize(name.getPointer()));
//		name.read();
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_StringSeq_ensure_length(name.getPointer(), 1, 1));
//		name.read();
//		RTICLibrary.INSTANCE.DDS_StringSeq_get_reference(name.getPointer(), 0).setString(0, CONTROLLER_PARTITION);
//		name.read();
		
		
		
		
		Pointer publisher = RTICLibrary.INSTANCE.DDS_DomainParticipant_create_publisher(domainParticipant, publisherQos.getPointer()/*RTICLibrary.DDS_PUBLISHER_QOS_DEFAULT*/, Pointer.NULL, RTICLibrary.DDS_STATUS_MASK_NONE);
		if(publisher == null) {
			throw new RuntimeException("Unable to create publisher");
		}
		this.publisher = publisher;
		
//		Memory mem = new Memory(1000);
////		RTICLibrary.DDS_DataWriterQos dataWriterQos = new RTICLibrary.DDS_DataWriterQos();
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Publisher_get_default_datawriter_qos(publisher, mem));
////		dataWriterQos.read();
//		mem.setInt(56, 5);
//		mem.setInt(60, 0);
//		mem.setInt(52, RTICLibrary.DDS_AUTOMATIC_LIVELINESS_QOS);
		
//		dataWriterQos.liveliness.lease_duration.sec = 5;
//		dataWriterQos.liveliness.lease_duration.nanosec = 0;
//		dataWriterQos.liveliness.lease_duration.write();
//		dataWriterQos.liveliness.kind = RTICLibrary.DDS_AUTOMATIC_LIVELINESS_QOS;
//		dataWriterQos.write();
//		this.dataWriterQos = mem;
//		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Publisher_set_default_datawriter_qos(publisher, mem));
		
		for(Class<?> cls : classes) {
			handlers.add(new DataHandler(gateway, domainParticipant, subscriber, publisher, (Class<? extends IdentifiableUpdate<?>>) cls));
		}

//		gateway.addListener(this);
	}


	public void tearDown() {
//		gateway.removeListener(this);
		
		for(DataHandler dh : handlers) {
			dh.tearDown(domainParticipant, subscriber, publisher);
		}
		
		
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Subscriber_delete_contained_entities(subscriber));
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_subscriber(domainParticipant, subscriber));
		
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_Publisher_delete_contained_entities(publisher));
		DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_publisher(domainParticipant, publisher));
		
		if(ownedParticipant) {
			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipant_delete_contained_entities(domainParticipant));
			Pointer factory = RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_get_instance();
			DDSInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_delete_participant(factory, domainParticipant));
//			JNADDSTypeFromInterface.checkReturnCode(RTICLibrary.INSTANCE.DDS_DomainParticipantFactory_finalize_instance());
		}
	}
	
	private static class MyGatewayListener implements GatewayListener {
		private String name;
		
		public MyGatewayListener(String name) {
			this.name = name;
		}
		
		@Override
		public void update(IdentifiableUpdate<?> update) {
			System.out.println(name+":"+update);
		}
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		Pointer logger = RTICLibrary.INSTANCE.NDDS_Config_Logger_get_instance();
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_verbosity(logger, RTICLibrary.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_print_format(logger, RTICLibrary.NDDS_CONFIG_LOG_PRINT_FORMAT_MAXIMAL);
		
//		DDS.init();
		
//		DomainParticipantQos qos1 = new DomainParticipantQos();
//		DomainParticipantFactory.get_instance().get_default_participant_qos(qos1);
//		qos1.discovery.accept_unknown_peers = true;
//		qos1.discovery.enabled_transports.remove(TransportBuiltinKind.SHMEM_ALIAS);
//		qos1.discovery.enabled_transports.add(TransportBuiltinKind.UDPv4_ALIAS);
//		qos1.discovery.multicast_receive_addresses.add("239.255.0.1");
//		DomainParticipantFactory.get_instance().set_default_participant_qos(qos1);
		
		Gateway gateway1 = new Gateway();
//		Gateway gateway2 = new Gateway();		
		
		
		
		DDSWrapper wrapper1 = new DDSWrapper(0, Role.Promiscuous, gateway1);
//		DDSWrapper wrapper2 = new DDSWrapper(0, Role.Device, gateway2);
		
		GatewayListener listener1 = new MyGatewayListener("GW1");
//		DeviceListener listener2 = new GatewayListener("GW2");
		
		gateway1.addListener(listener1);
//		gateway2.addListener(listener2);
		
//		MutableNumericUpdate mnu = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
//		mnu.setSource("*");
//		mnu.setTarget("*");
//		MutableImageUpdate miu = new MutableImageUpdateImpl(Device.ICON);
//		miu.setSource("*");
//		miu.setTarget("*");
		
//		miu.setWidth(127);
//		miu.setHeight(127);
//		miu.setRaster(new byte[127*127*4]);
//		for(int i = 0; i < 10; i++) {
//			mnu.setValue(i+60);
//			gateway1.update(listener1, mnu);
//		}
//		MutableTextUpdate mtu = new MutableTextUpdateImpl(Device.NAME, "JEFF HAS A DEVICE");
//		mtu.setSource("*");
//		mtu.setTarget("*");
//		gateway1.update(listener1, mtu);
		System.in.read();
//		wrapper2.tearDown();
		wrapper1.tearDown();
		System.exit(0);
	}
/*
	@Override
	public void on_liveliness_changed(Pointer listener_data, Pointer reader, Pointer status) {
		RTICLibrary.DDS_LivelinessChangedStatus liveliness = new RTICLibrary.DDS_LivelinessChangedStatus(status);
		
	}
	*/
}
