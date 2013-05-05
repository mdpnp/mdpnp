package org.mdpnp.messaging.dds.rti;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public class RTICLibraryDirect implements RTICLibrary {
	static {
		Native.register(NDDS_C_LIB_NAME);
	}
	@Override
	public native Pointer NDDS_Config_Logger_get_instance();

	@Override
	public native void NDDS_Config_Logger_set_verbosity(Pointer self, int verbosity);

	@Override
	public native int NDDS_Config_Logger_get_verbosity(Pointer self);

	@Override
	public native int NDDS_Config_Logger_get_verbosity_by_category(Pointer self,
			int category);

	@Override
	public native void NDDS_Config_Logger_set_verbosity_by_category(Pointer self,
			int category, int verbosity);

	@Override
	public native int NDDS_Config_Logger_get_print_format(Pointer self);

	@Override
	public native byte NDDS_Config_Logger_set_print_format(Pointer self,
			int print_format);

	@Override
	public native Pointer DDS_DomainParticipantFactory_get_instance();

	@Override
	public native int DDS_DomainParticipantFactory_finalize_instance();

	@Override
	public native Pointer DDS_DomainParticipantFactory_create_participant(
			Pointer self, int domainId, Pointer qos, Pointer listener, int mask);

	@Override
	public native int DDS_DomainParticipantFactory_delete_participant(Pointer self,
			Pointer a_participant);

	@Override
	public native int DDS_DomainParticipantFactory_get_default_participant_qos(
			Pointer self, Pointer qos);

	@Override
	public native int DDS_DomainParticipantFactory_set_default_participant_qos(
			Pointer self, Pointer qos);
	@Override
	public native Pointer DDS_DomainParticipant_find_topic(Pointer self,
			Pointer topicName, DDS_Duration_t duration);

	@Override
	public native Pointer DDS_DomainParticipant_create_topic(Pointer self,
			Pointer topicName, Pointer typeName, Pointer qos, Pointer listener,
			int mask);

	@Override
	public native int DDS_DomainParticipant_delete_contained_entities(Pointer self);

	@Override
	public native int DDS_DomainParticipant_delete_topic(Pointer self, Pointer topic);

	@Override
	public native Pointer DDS_DomainParticipant_create_datawriter(Pointer self,
			Pointer topic, Pointer qos, Pointer listener, int mask);

	@Override
	public native Pointer DDS_DomainParticipant_create_datareader(Pointer self,
			Pointer topic, Pointer qos, Pointer listener, int mask);

	@Override
	public native int DDS_DomainParticipant_delete_datareader(Pointer self,
			Pointer reader);

	@Override
	public native int DDS_DomainParticipant_delete_datawriter(Pointer self,
			Pointer writer);

	@Override
	public native int DDS_DomainParticipant_get_domain_id(Pointer self);

	@Override
	public native Pointer DDS_DomainParticipant_create_publisher(Pointer self,
			Pointer qos, Pointer listener, int mask);

	@Override
	public native Pointer DDS_DomainParticipant_create_subscriber(Pointer self,
			Pointer qos, Pointer listener, int mask);

	@Override
	public native int DDS_DomainParticipant_delete_publisher(Pointer self,
			Pointer publisher);

	@Override
	public native int DDS_DomainParticipant_delete_subscriber(Pointer self,
			Pointer subscriber);

	@Override
	public native Pointer DDS_DomainParticipant_create_contentfilteredtopic(
			Pointer self, Pointer name, Pointer related_topic,
			Pointer filter_expression, Pointer expression_parameters);

	@Override
	public native Pointer DDS_DomainParticipant_create_contentfilteredtopic_with_filter(
			Pointer self, Pointer name, Pointer related_topic,
			Pointer filter_expression, Pointer expression_parameters,
			Pointer filter_name);
	
	@Override
	public native int DDS_DomainParticipant_delete_contentfilteredtopic(Pointer self,
			Pointer content_filtered_topic);
	
	@Override
	public native int DDS_DomainParticipant_register_contentfilter(Pointer self,
			Pointer filter_name, Pointer content_filter);

	@Override
	public native int DDS_DomainParticipant_unregister_contentfilter(Pointer self,
			Pointer filter_name);

	@Override
	public native int DDS_DomainParticipant_get_default_publisher_qos(Pointer self,
			Pointer qos);

	@Override
	public native int DDS_DomainParticipant_get_default_subscriber_qos(Pointer self,
			Pointer qos);

	@Override
	public native Pointer DDS_DynamicData_new(Pointer typeCode, Pointer property);

	@Override
	public native void DDS_DynamicData_delete(Pointer self);

	@Override
	public native int DDS_DynamicData_clear_all_members(Pointer self);

	@Override
	public native Pointer DDS_DynamicData_get_type(Pointer self);

	@Override
	public native int DDS_DynamicData_set_string(Pointer self, Pointer member_name,
			int member_id, Pointer value);

	@Override
	public native int DDS_DynamicData_set_long(Pointer self, Pointer member_name,
			int member_id, int value);

	@Override
	public native int DDS_DynamicData_set_double(Pointer self, Pointer member_name,
			int member_id, double value);

	@Override
	public native int DDS_DynamicData_set_float(Pointer self, Pointer member_name,
			int member_id, float value);

	@Override
	public native int DDS_DynamicData_set_longlong(Pointer self, Pointer member_name,
			int member_id, long value);

	@Override
	public native int DDS_DynamicData_set_short(Pointer self, Pointer member_name,
			int member_id, short value);

	@Override
	public native int DDS_DynamicData_set_octet(Pointer self, Pointer member_name,
			int member_id, byte value);

	@Override
	public native int DDS_DynamicData_set_boolean(Pointer self, Pointer member_name,
			int member_id, boolean value);

	@Override
	public native int DDS_DynamicData_set_double_seq(Pointer self,
			Pointer member_name, int member_id, Pointer value);

	@Override
	public native int DDS_DynamicData_set_octet_seq(Pointer self, Pointer member_name,
			int member_id, Pointer value);


	@Override
	public native int DDS_DynamicData_get_string(Pointer self,
			PointerByReference value, IntByReference size, Pointer member_name,
			int member_id);

	@Override
	public native int DDS_DynamicData_get_long(Pointer self, IntByReference value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_double(Pointer self,
			DoubleByReference value, Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_float(Pointer self, FloatByReference value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_longlong(Pointer self,
			LongByReference value, Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_short(Pointer self, ShortByReference value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_octet(Pointer self, ByteByReference value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_boolean(Pointer self, ByteByReference value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_double_seq(Pointer self, Pointer value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DynamicData_get_octet_seq(Pointer self, Pointer value,
			Pointer member_name, int member_id);

	@Override
	public native int DDS_DataReader_set_listener(Pointer self, Pointer listener,
			int mask);

	@Override
	public native int DDS_DataReader_delete_contained_entities(Pointer self);

//	@Override
//	public native Pointer DDS_DynamicDataDataReader_as_datareader(Pointer reader);

	@Override
	public native int DDS_DynamicDataWriter_write(Pointer self, Pointer data,
			Pointer handle);

	@Override
	public native int DDS_DynamicDataReader_take_next_sample(Pointer self,
			Pointer data, Pointer sampleInfo);

	@Override
	public native Pointer DDS_DynamicDataTypeSupport_new(Pointer type, Pointer props);

	@Override
	public native void DDS_DynamicDataTypeSupport_delete(Pointer self);

	@Override
	public native Pointer DDS_DynamicDataTypeSupport_create_data(Pointer self);

	@Override
	public native int DDS_DynamicDataTypeSupport_register_type(Pointer self,
			Pointer participant, Pointer typeName);

	@Override
	public native int DDS_DynamicDataTypeSupport_unregister_type(Pointer self,
			Pointer participant, Pointer typeName);

	@Override
	public native int DDS_DynamicDataTypeSupport_delete_data(Pointer self, Pointer data);

	@Override
	public native Pointer DDS_TypeCodeFactory_create_sparse_tc(Pointer self,
			Pointer name, int modifier, Pointer concreteBase, IntByReference ex);

	@Override
	public native Pointer DDS_TypeCodeFactory_get_instance();

	@Override
	public native Pointer DDS_TypeCodeFactory_create_string_tc(Pointer self,
			int bound, IntByReference ex);

	@Override
	public native Pointer DDS_TypeCodeFactory_create_sequence_tc(Pointer self,
			int bound, Pointer elementType, IntByReference ex);

	@Override
	public native Pointer DDS_TypeCodeFactory_get_primitive_tc(Pointer self, int x);

	@Override
	public native Pointer DDS_TypeCodeFactory_create_struct_tc(Pointer self,
			Pointer name, Pointer members, IntByReference exception);

	@Override
	public native Pointer DDS_TypeCodeFactory_create_value_tc(Pointer self,
			Pointer name, int modifier, Pointer concrete_base, Pointer members,
			IntByReference exception);

	@Override
	public native void DDS_TypeCodeFactory_delete_tc(Pointer self, Pointer typeCode,
			IntByReference exception);

	@Override
	public native int DDS_TypeCode_add_member(Pointer self, Pointer name, int id,
			Pointer typeCode, int member_flags, IntByReference ex);

	@Override
	public native Pointer DDS_TypeCode_member_type(Pointer self, int index,
			IntByReference ex);

	@Override
	public native int DDS_TypeCode_find_member_by_name(Pointer self, Pointer name,
			IntByReference ex);

	@Override
	public native int DDS_TypeCode_kind(Pointer self, IntByReference ex);

	@Override
	public native Pointer DDS_TypeCode_content_type(Pointer self, IntByReference ex);

	@Override
	public native String DDS_TypeCode_name(Pointer self, IntByReference ex);

	@Override
	public native int DDS_TypeCode_member_id(Pointer self, int index, IntByReference ex);

	@Override
	public native byte DDS_DoubleSeq_initialize(Pointer self);

	@Override
	public native byte DDS_DoubleSeq_ensure_length(Pointer self, int length, int max);

	@Override
	public native Pointer DDS_DoubleSeq_get_reference(Pointer self, int i);

	@Override
	public native int DDS_DoubleSeq_get_length(Pointer self);

	@Override
	public native double DDS_DoubleSeq_get(Pointer self, int i);

	@Override
	public native byte DDS_DoubleSeq_finalize(Pointer self);

	@Override
	public native byte DDS_StringSeq_initialize(Pointer self);
	
	@Override
	public native byte DDS_StringSeq_ensure_length(Pointer self, int length, int max);

	@Override
	public native Pointer DDS_StringSeq_get_reference(Pointer self, int i);

	@Override
	public native int DDS_StringSeq_get_length(Pointer self);

	@Override
	public native double DDS_StringSeq_get(Pointer self, int i);

	@Override
	public native byte DDS_StringSeq_finalize(Pointer self);

	@Override
	public native byte DDS_OctetSeq_initialize(Pointer self);

	@Override
	public native byte DDS_OctetSeq_finalize(Pointer self);

	@Override
	public native Pointer DDS_OctetSeq_copy(Pointer self, Pointer from);

	@Override
	public native byte DDS_OctetSeq_ensure_length(Pointer self, int length, int max);

	@Override
	public native Pointer DDS_OctetSeq_get_reference(Pointer self, int i);

	@Override
	public native int DDS_OctetSeq_get_length(Pointer self);

	@Override
	public native byte DDS_OctetSeq_get(Pointer self, int i);

	@Override
	public native Pointer DDS_Publisher_create_datawriter(Pointer self, Pointer topic,
			Pointer qos, Pointer listener, int mask);

	@Override
	public native int DDS_Publisher_delete_datawriter(Pointer self,
			Pointer a_datawriter);

	@Override
	public native int DDS_Publisher_get_default_datawriter_qos(Pointer self,
			Pointer qos);

	@Override
	public native int DDS_Publisher_set_default_datawriter_qos(Pointer self,
			Pointer qos);

	@Override
	public native int DDS_Publisher_delete_contained_entities(Pointer self);

	@Override
	public native int DDS_Subscriber_get_default_datareader_qos(Pointer self,
			Pointer qos);

	@Override
	public native int DDS_Subscriber_set_default_datareader_qos(Pointer self,
			Pointer qos);

	@Override
	public native Pointer DDS_Subscriber_create_datareader(Pointer self,
			Pointer topic, Pointer qos, Pointer listener, int mask);
	@Override
	public native int DDS_Subscriber_delete_datareader(Pointer self,
			Pointer a_datareader);

	@Override
	public native int DDS_Subscriber_delete_contained_entities(Pointer self);

//	@Override
//	public native Pointer DDS_Topic_as_topicdescription(Pointer topic);

//	@Override
//	public native Pointer DDS_ContentFilteredTopic_as_topicdescription(
//			Pointer content_filtered_topic);

	@Override
	public native Pointer DDS_String_alloc(int length);

	@Override
	public native Pointer DDS_String_dup(String str);

	@Override
	public native void DDS_String_free(Pointer str);

	@Override
	public native int DDS_DomainParticipantQos_initialize(Pointer self);

	@Override
	public native int DDS_PublisherQos_initialize(Pointer self);

	@Override
	public native int DDS_SubscriberQos_initialize(Pointer self);


}
