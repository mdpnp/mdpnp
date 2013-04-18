package org.mdpnp.transport.dds.rti;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public interface RTICLibrary extends Library {
    
    
    
	public static final int DDS_TYPECODE_MEMBER_ID_INVALID = 0x7FFFFFFF;
	
	public class DDS_Listener extends Structure {
		public Pointer listener_data = Pointer.NULL;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"listener_data"});
		}
	}

	public class DDS_DynamicDataProperty_t extends Structure {
		public int buffer_initial_size = 0;
		public int buffer_max_size = 65536;
		public int buffer_max_size_increment = 1024;
		public byte buffer_check_size = 0;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList("buffer_initial_size", "buffer_max_size", "buffer_max_size_increment", "buffer_check_size");
		}
		
		public DDS_DynamicDataProperty_t() {
			super();
			write();
		}
		public DDS_DynamicDataProperty_t(int buffer_initial_size, int buffer_max_size, int buffer_max_size_increment) {
			super();
			this.buffer_initial_size = buffer_initial_size;
			this.buffer_max_size = buffer_max_size;
			this.buffer_max_size_increment = buffer_max_size_increment;
			write();
		}
	}
	
	public class DDS_DynamicDataTypeSerializationProperty_t extends Structure {
		public byte use_42e_compatible_alignment = 0;
		public int max_size_serialized = 0xFFFFFFFF;
		public int min_size_serialized = 0xFFFFFFFF;
		public byte trim_to_size = 0;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"use_42e_compatible_alignment", "max_size_serialized", "min_size_serialized", "trim_to_size"});
		}
		
		public DDS_DynamicDataTypeSerializationProperty_t() {
			super();
			write();
		}
	}
	
	public class DDS_DynamicDataTypeProperty_t extends Structure {
		public DDS_DynamicDataProperty_t data = new DDS_DynamicDataProperty_t();
		public DDS_DynamicDataTypeSerializationProperty_t serialization = new DDS_DynamicDataTypeSerializationProperty_t();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"data", "serialization"});
		}
		
		public DDS_DynamicDataTypeProperty_t() {
			super();
			write();
		}
	}
	
	public class DDS_DataWriterListener extends Structure {
		public interface DDS_DataWriterListener_OfferedDeadlineMissedCallback extends Callback {
			void on_offered_deadline_missed(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_LivelinessLostCallback extends Callback {
			void on_liveliness_lost(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_OfferedIncompatibleQosCallback extends Callback {
			void on_offered_incompatible_qos(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_PublicationMatchedCallback extends Callback {
			void on_publication_matched(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_ReliableWriterCacheChangedCallback extends Callback {
			void on_reliablewriter_cache_changed(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_ReliableReaderActivityChangedCallback extends Callback {
			void on_reliablereader_activity(Pointer listener_data, Pointer writer, Pointer status);
		}
		public interface DDS_DataWriterListener_DestinationUnreachableCallback extends Callback {
			void on_destination_unreachable(Pointer listener_data, Pointer writer, Pointer handle, Pointer destination);
		}
		public interface DDS_DataWriterListener_DataRequestCallback extends Callback {
			Pointer on_datarequest(Pointer listener_data, Pointer writer, Pointer cookie);
		}
		public interface DDS_DataWriterListener_DataReturnCallback extends Callback {
			void on_datareturn(Pointer listener_data, Pointer writer, Pointer instance_data, Pointer cookie);
		}
		public interface DDS_DataWriterListener_SampleRemovedCallback extends Callback {
			void on_sample_removed(Pointer listener_data, Pointer writer, Pointer cookie);
		}
		public interface DDS_DataWriterListener_InstanceReplacedCallback extends Callback {
			void on_instance_replaced(Pointer listener_data, Pointer writer, Pointer handle);
		}
		public interface DDS_DataWriterListener_OnApplicationAcknowledgmentCallback extends Callback {
            void on_instance_replaced(Pointer listener_data, Pointer writer, Pointer handle);
        }
		public DDS_Listener as_listener;
		public DDS_DataWriterListener_OfferedDeadlineMissedCallback on_offered_deadline_missed;
		public DDS_DataWriterListener_OfferedIncompatibleQosCallback on_offered_incompatible_qos;
		public DDS_DataWriterListener_LivelinessLostCallback on_liveliness_lost;
		public DDS_DataWriterListener_PublicationMatchedCallback on_publication_matched;
		public DDS_DataWriterListener_ReliableWriterCacheChangedCallback on_reliable_writer_cache_changed;
		public DDS_DataWriterListener_ReliableReaderActivityChangedCallback on_reliable_reader_activity_changed;
		public DDS_DataWriterListener_DestinationUnreachableCallback on_destination_unreachable;
		public DDS_DataWriterListener_DataRequestCallback on_data_request;
		public DDS_DataWriterListener_DataReturnCallback on_data_return;
		public DDS_DataWriterListener_SampleRemovedCallback on_sample_removed;
		public DDS_DataWriterListener_InstanceReplacedCallback on_instance_replaced;
		public DDS_DataWriterListener_OnApplicationAcknowledgmentCallback on_application_acknowledgment;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList("as_listener", "on_offered_deadline_missed", "on_offered_incompatible_qos", "on_liveliness_lost",
					"on_publication_matched","on_reliable_writer_cache_changed","on_reliable_reader_activity_changed","on_destination_unreachable",
					"on_data_request","on_data_return","on_sample_removed","on_instance_replaced", "on_application_acknowledgment");
		}
	}

 	public class DDS_DataReaderListener extends Structure {
 		public interface DDS_DataReaderListener_RequestedDeadlineMissedCallback extends Callback {
 			void on_requested_deadline_missed(Pointer listener_data, Pointer reader, Pointer status);
 		}
 		public interface DDS_DataReaderListener_RequestedIncompatibleQosCallback extends Callback {
 	 		void on_requested_incompatible_qos(Pointer listener_data, Pointer reader, Pointer status);
 	 	}
 		public interface DDS_DataReaderListener_SampleRejectedCallback extends Callback {
 	 		void on_sample_rejected(Pointer listener_data, Pointer reader, Pointer status);
 	 	}
 	 
 		public interface DDS_DataReaderListener_LivelinessChangedCallback extends Callback {
 	 		void on_liveliness_changed(Pointer listener_data, Pointer reader, Pointer status);
 	 	} 
 		public interface DDS_DataReaderListener_DataAvailableCallback extends Callback {
 	 		void on_data_available(Pointer listener_data, Pointer reader);
 	 	}
 	  
 		public interface DDS_DataReaderListener_SubscriptionMatchedCallback extends Callback {
 	 		void on_subscription_matched(Pointer listener_data, Pointer reader, Pointer status);
 	 	}
 	 
 		public interface DDS_DataReaderListener_SampleLostCallback extends Callback {
 	 		void on_sample_lost(Pointer listener_data, Pointer reader, Pointer status);
 	 	} 
 		public DDS_Listener dds_listener;
 		public DDS_DataReaderListener_RequestedDeadlineMissedCallback on_requested_deadline_missed;
 		public DDS_DataReaderListener_RequestedIncompatibleQosCallback on_requested_incompatible_qos;
 		public DDS_DataReaderListener_SampleRejectedCallback on_sample_rejected;
 		public DDS_DataReaderListener_LivelinessChangedCallback on_liveliness_changed;
 		public DDS_DataReaderListener_DataAvailableCallback on_data_available;
 		public DDS_DataReaderListener_SubscriptionMatchedCallback on_subscription_matched;
 		public DDS_DataReaderListener_SampleLostCallback on_sample_lost;
 		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"dds_listener", "on_requested_deadline_missed", "on_requested_incompatible_qos", "on_sample_rejected",
					"on_liveliness_changed","on_data_available","on_subscription_matched","on_sample_lost"});
		}
 	}
 	
	public class DDS_SubscriberListener extends Structure {
		public interface DDS_SubscriberListener_DataOnReadersCallback extends Callback {
			void on_data_on_readers(Pointer listener_data, Pointer subscriber);
		}
		public DDS_DataReaderListener as_datareaderlistener;
		public DDS_SubscriberListener_DataOnReadersCallback on_data_on_readers;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"as_datareaderlistener", "on_data_on_readers"});
		}
	}

 	
	public static final short DDS_VM_NONE = 0;

	public static final short DDS_VM_CUSTOM = 1;

	public static final short DDS_VM_ABSTRACT = 2;

	public static final short DDS_VM_TRUNCATABLE = 3;

	public static final short DDS_PRIVATE_MEMBER = 0;

	public static final short DDS_PUBLIC_MEMBER = 1;

	public static final byte DDS_TYPECODE_NONKEY_MEMBER = 0;

	public static final byte DDS_TYPECODE_KEY_MEMBER = 1;

	public static final byte DDS_TYPECODE_NONKEY_REQUIRED_MEMBER = 2;
	
    public static final int DDS_TK_NULL = 0;

    public static final int DDS_TK_SHORT = 1;

    public static final int DDS_TK_LONG = 2;

    public static final int DDS_TK_USHORT = 3;

    public static final int DDS_TK_ULONG = 4;

    public static final int DDS_TK_FLOAT = 5;

    public static final int DDS_TK_DOUBLE = 6;

    public static final int DDS_TK_BOOLEAN = 7;

    public static final int DDS_TK_CHAR = 8;

    public static final int DDS_TK_OCTET = 9;

    public static final int DDS_TK_STRUCT = 10;

    public static final int DDS_TK_UNION = 11;

    public static final int DDS_TK_ENUM = 12;

    public static final int DDS_TK_STRING = 13;

    public static final int DDS_TK_SEQUENCE = 14;

    public static final int DDS_TK_ARRAY = 15;

    public static final int DDS_TK_ALIAS = 16;

    public static final int DDS_TK_LONGLONG = 17;

    public static final int DDS_TK_ULONGLONG = 18;

    public static final int DDS_TK_LONGDOUBLE = 19;

    public static final int DDS_TK_WCHAR = 20;

    public static final int DDS_TK_WSTRING = 21;

    public static final int DDS_TK_VALUE = 22;

    public static final int DDS_TK_SPARSE = 23;

    public static final int DDS_TK_RAW_BYTES = 0x7e;

    public static final int DDS_TK_RAW_BYTES_KEYED = 0x7f;
    
    public static final int DDS_DYNAMIC_DATA_MEMBER_ID_UNSPECIFIED = 0;
	
    public static final int DDS_STATUS_MASK_NONE = 0;
    
    public static final int DDS_INCONSISTENT_TOPIC_STATUS = 0x0001 << 0;
    public static final int DDS_OFFERED_DEADLINE_MISSED_STATUS = 0x0001 << 1;
    public static final int  DDS_REQUESTED_DEADLINE_MISSED_STATUS = 0x0001 << 2;
    public static final int DDS_OFFERED_INCOMPATIBLE_QOS_STATUS = 0x0001 << 5;
    public static final int DDS_REQUESTED_INCOMPATIBLE_QOS_STATUS = 0x0001 << 6;
    public static final int DDS_SAMPLE_LOST_STATUS = 0x0001 << 7;
    public static final int DDS_SAMPLE_REJECTED_STATUS = 0x0001 << 8;
    public static final int DDS_DATA_ON_READERS_STATUS = 0x0001 << 9;
    public static final int DDS_DATA_AVAILABLE_STATUS = 0x0001 << 10;
    public static final int DDS_LIVELINESS_LOST_STATUS = 0x0001 << 11;
    public static final int DDS_LIVELINESS_CHANGED_STATUS = 0x0001 << 12;
    public static final int DDS_PUBLICATION_MATCHED_STATUS = 0x0001 << 13;
    public static final int DDS_SUBSCRIPTION_MATCHED_STATUS = 0x0001 << 14;

    public static final int DDS_RETCODE_OK                   = 0;
	public static final int DDS_RETCODE_ERROR                = 1;

	public static final int DDS_RETCODE_UNSUPPORTED         = 2;

	public static final int DDS_RETCODE_BAD_PARAMETER        = 3;

	public static final int DDS_RETCODE_PRECONDITION_NOT_MET = 4;
	public static final int DDS_RETCODE_OUT_OF_RESOURCES     = 5;

	public static final int DDS_RETCODE_NOT_ENABLED          = 6;

	public static final int DDS_RETCODE_IMMUTABLE_POLICY     = 7;

	public static final int DDS_RETCODE_INCONSISTENT_POLICY  = 8;

	public static final int DDS_RETCODE_ALREADY_DELETED  = 9;

	public static final int DDS_RETCODE_TIMEOUT  = 10;

	public static final int DDS_RETCODE_NO_DATA  = 11;

	public static final int DDS_RETCODE_ILLEGAL_OPERATION  = 12;

	public static final int DDS_NO_EXCEPTION_CODE              = 0;
	public static final int DDS_USER_EXCEPTION_CODE            = 1;
	public static final int DDS_SYSTEM_EXCEPTION_CODE          = 2;
	public static final int DDS_BAD_PARAM_SYSTEM_EXCEPTION_CODE = 3;
	public static final int DDS_NO_MEMORY_SYSTEM_EXCEPTION_CODE = 4;
	public static final int DDS_BAD_TYPECODE_SYSTEM_EXCEPTION_CODE = 5;
	public static final int DDS_BADKIND_USER_EXCEPTION_CODE    = 6;
	public static final int DDS_BOUNDS_USER_EXCEPTION_CODE     = 7;
	public static final int DDS_IMMUTABLE_TYPECODE_SYSTEM_EXCEPTION_CODE = 8;
	public static final int DDS_BAD_MEMBER_NAME_USER_EXCEPTION_CODE  = 9;
	public static final int DDS_BAD_MEMBER_ID_USER_EXCEPTION_CODE  = 10;

	
	
	public class DDS_Duration_t extends Structure {
		public DDS_Duration_t() {
			super();
			write();
		}
		
		public DDS_Duration_t(int sec, int nanosec) {
			super();
			this.sec = sec;
			this.nanosec = nanosec;
			write();
		}

		public int sec = 0;
		public int nanosec = 0;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"sec", "nanosec"});
		}
	}
	public static final int DDS_SEQUENCE_MAGIC_NUMBER = 0x7344;
	public class DDS_Sequence extends Structure {
		public byte owned = 1;
		public Pointer contiguous_buffer = Pointer.NULL;
		public Pointer discontiguous_buffer = Pointer.NULL;
		public int maximum = 0;
		public int length = 0;
		public int sequence_init = DDS_SEQUENCE_MAGIC_NUMBER;
		public Pointer read_token1 = Pointer.NULL;
		public Pointer read_token2 = Pointer.NULL;
		public byte elementsPointersAllocation = 1;

		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"owned", "contiguous_buffer", "discontiguous_buffer", "maximum",
					"length","sequence_init","read_token1","read_token2","elementsPointersAllocation"});
		}
		
		public void initialize() {
			
		}
		
		public DDS_Sequence() {
			super();
			write();
			initialize();
			read();
		}
		
		public DDS_Sequence(Pointer p) {
			super(p);
		}
	}
	
	public class DDS_StringSequence extends DDS_Sequence {

		public void initialize() {
			INSTANCE.DDS_StringSeq_initialize(getPointer());
			read();
		}
		
		public DDS_StringSequence() {
			super();
		}
		
		@Override
		protected void finalize() throws Throwable {
		}
		
	}
	
	public class DDS_DoubleSequence extends DDS_Sequence {

		public void initialize() {
			INSTANCE.DDS_DoubleSeq_initialize(getPointer());
			read();
		}
		
		public DDS_DoubleSequence() {
			super();
		}
		
		@Override
		protected void finalize() throws Throwable {
		}
	}
	
	public class DDS_OctetSequence extends DDS_Sequence {

		public void initialize() {
			INSTANCE.DDS_OctetSeq_initialize(getPointer());
			read();
		}
		
		public DDS_OctetSequence() {
			super();
		}
		
		@Override
		protected void finalize() throws Throwable {
		}
	}
	

    public static final int DDS_READ_SAMPLE_STATE     = 0x0001 << 0;
    public static final int DDS_NOT_READ_SAMPLE_STATE = 0x0001 << 1;

    public static final int DDS_NEW_VIEW_STATE     = 0x0001 << 0;
    public static final int DDS_NOT_NEW_VIEW_STATE = 0x0001 << 1;
    
    public static final int DDS_ALIVE_INSTANCE_STATE                = 0x0001 << 0;
    public static final int DDS_NOT_ALIVE_DISPOSED_INSTANCE_STATE   = 0x0001 << 1;
    public static final int DDS_NOT_ALIVE_NO_WRITERS_INSTANCE_STATE = 0x0001 << 2;
	
    public class DDS_InstanceHandle_t extends Structure {
    	public int a;
    	public int b;
    	public int c;
    	public int d;
    	public int e;
    	public int f;
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"a", "b", "c", "d",
					"e","f"});
		}
    }
    public class DDS_Cookie_t extends Structure {
        public DDS_Cookie_t() {
            super();
            write();
        }
        public DDS_Cookie_t(Pointer p) {
            super(p);
        }
        public DDS_OctetSequence value = new DDS_OctetSequence();
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("value");
        }
    }
    public class DDS_SampleIdentity_t extends Structure {
        public DDS_SampleIdentity_t() {
        }
        public DDS_SampleIdentity_t(Pointer p) {
            super(p);
        }
        public DDS_GUID_t writer_guid;
        public DDS_SequenceNumber_t sequence_number;
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("writer_guid", "sequence_number");
        }
    }
    public class DDS_AckResponseData_t extends Structure {
        public DDS_AckResponseData_t() {
        }
        public DDS_AckResponseData_t(Pointer p) {
            super(p);
        }
        public DDS_OctetSequence value;
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("value");
        }
    }
    
    public class DDS_AcknowledgmentInfo extends Structure {
        public DDS_AcknowledgmentInfo() {
        }
        public DDS_AcknowledgmentInfo(Pointer p) {
            super(p);
        }
        public DDS_InstanceHandle_t subscription_handle;
        public DDS_SampleIdentity_t sample_identity;
        public DDS_Cookie_t cookie;
        public byte valid_response_data;
        public DDS_AckResponseData_t response_data;
        
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("subscription_handle", "sample_identity", "cookie", "valid_response_data", "response_data");
        }
    }
    
    public class DDS_LivelinessChangedStatus extends Structure {
    	public DDS_LivelinessChangedStatus() {
		}
    	public DDS_LivelinessChangedStatus(Pointer p) {
    		super(p);
		}
	    public long alive_count;
	    public long not_alive_count;                                   
	    public long alive_count_change;
	    public long not_alive_count_change;
	    public DDS_InstanceHandle_t last_publication_handle;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"alive_count", "not_alive_count", "alive_count_change", "not_alive_count_change",
					"last_publication_handle"});
		}
    }
    
    public class DDS_GUID_t extends Structure {
    	public long high;
    	public long low;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"high", "low"});
		}
    }
    
	public class DDS_SampleInfo extends Structure {
		    public int sample_state;
		    public int view_state;
		    public int instance_state;
		    public long source_timestamp;
		    public DDS_InstanceHandle_t instance_handle;
		    public DDS_InstanceHandle_t publication_handle;
		    public int disposed_generation_count;
		    public int no_writers_generation_count;
		    public int sample_rank;
		    public int generation_rank;
		    public int absolute_generation_rank;
		    public byte valid_data;
		    public long reception_timestamp;
		    public long publication_sequence_number;
		    public long reception_sequence_number;
		    public DDS_GUID_t publication_virtual_guid;
		    public long publication_virtual_sequence_number;
		    public DDS_GUID_t original_publication_virtual_guid;
		    public long original_publication_virtual_sequence_number;
		    public DDS_GUID_t related_original_publication_virtual_guid;
		    public long related_original_publication_virtual_sequence_number;
			@Override
			protected List<?> getFieldOrder() {
				return Arrays.asList("sample_state", "view_state", "instance_state", "source_timestamp",
						"instance_handle","publication_handle","disposed_generation_count","no_writers_generation_count",
						"sample_rank","generation_rank","absolute_generation_rank","valid_data","reception_timestamp","publication_sequence_number","reception_sequence_number",
						"publication_virtual_guid","publication_virtual_sequence_number","original_publication_virtual_guid","original_publication_virtual_sequence_number",
						"related_original_publication_virtual_guid","related_original_publication_virtual_sequence_number");
			}

	}
	
	public static final String NDDS_C_LIB_NAME = "nddsc";
	
	Library CORE_INSTANCE = (Library) Native.loadLibrary("nddscore", Library.class);
//	RTICLibrary INSTANCE = (RTICLibrary) Native.loadLibrary(NDDS_C_LIB_NAME, RTICLibrary.class);
	
	RTICLibrary INSTANCE = new RTICLibraryDirect();
	
	Pointer DDS_PARTICIPANT_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_PARTICIPANT_QOS_DEFAULT");
	Pointer DDS_TOPIC_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_TOPIC_QOS_DEFAULT");
	Pointer DDS_DATAREADER_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_DATAREADER_QOS_DEFAULT");
	Pointer DDS_DATAWRITER_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_DATAWRITER_QOS_DEFAULT");
	Pointer DDS_HANDLE_NIL = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_HANDLE_NIL");
	Pointer DDS_DYNAMIC_DATA_TYPE_PROPERTY_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_DYNAMIC_DATA_TYPE_PROPERTY_DEFAULT");
	Pointer DDS_PUBLISHER_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_PUBLISHER_QOS_DEFAULT");
	Pointer DDS_SUBSCRIBER_QOS_DEFAULT = NativeLibrary.getInstance(NDDS_C_LIB_NAME).getGlobalVariableAddress("DDS_SUBSCRIBER_QOS_DEFAULT");
	
	public static final int RTI_LOG_BIT_SILENCE = 0x00000000;
	public static final int RTI_LOG_BIT_EXCEPTION = 0x00000001;
	public static final int RTI_LOG_BIT_WARN = 0x00000002;
	public static final int RTI_LOG_BIT_LOCAL = 0x00000004;
	public static final int RTI_LOG_BIT_REMOTE = 0x00000008;
	public static final int RTI_LOG_BIT_PERIODIC = 0x00000010;
	public static final int RTI_LOG_BIT_ACTIVITY = 0x00000020;

	public static final int NDDS_CONFIG_LOG_VERBOSITY_SILENT = RTI_LOG_BIT_SILENCE;
	public static final int NDDS_CONFIG_LOG_VERBOSITY_ERROR = RTI_LOG_BIT_EXCEPTION;
	public static final int NDDS_CONFIG_LOG_VERBOSITY_WARNING = NDDS_CONFIG_LOG_VERBOSITY_ERROR | RTI_LOG_BIT_WARN;
	public static final int NDDS_CONFIG_LOG_VERBOSITY_STATUS_LOCAL = NDDS_CONFIG_LOG_VERBOSITY_WARNING | RTI_LOG_BIT_LOCAL;
	public static final int NDDS_CONFIG_LOG_VERBOSITY_STATUS_REMOTE = NDDS_CONFIG_LOG_VERBOSITY_STATUS_LOCAL | RTI_LOG_BIT_REMOTE;
	public static final int NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL = NDDS_CONFIG_LOG_VERBOSITY_STATUS_REMOTE | RTI_LOG_BIT_PERIODIC;
	
    public static final int NDDS_CONFIG_LOG_CATEGORY_PLATFORM = 0;
    public static final int NDDS_CONFIG_LOG_CATEGORY_COMMUNICATION = 1;
    public static final int NDDS_CONFIG_LOG_CATEGORY_DATABASE = 2;
    public static final int NDDS_CONFIG_LOG_CATEGORY_ENTITIES = 3;
    public static final int NDDS_CONFIG_LOG_CATEGORY_API = 4;
    public static final int NDDS_CONFIG_LOG_CATEGORY_ALL = 5;
    
    public static final int RTI_LOG_PRINT_BIT_NUMBER = 0x1;
    public static final int RTI_LOG_PRINT_BIT_MSG = 0x2;
    public static final int RTI_LOG_PRINT_BIT_AT_MODULE = 0x04;
    public static final int RTI_LOG_PRINT_BIT_AT_FILE = 0x08;
    public static final int RTI_LOG_PRINT_BIT_AT_METHOD = 0x10;
    public static final int RTI_LOG_PRINT_BIT_AT_LINE = 0x20;
    public static final int RTI_LOG_PRINT_LOCATION_MASK = 
            (RTI_LOG_PRINT_BIT_AT_MODULE | RTI_LOG_PRINT_BIT_AT_FILE |
             RTI_LOG_PRINT_BIT_AT_METHOD | RTI_LOG_PRINT_BIT_AT_LINE);

    public static final int ADVLOG_PRINT_MASK_RTILOG = 0x00ff;
    public static final int ADVLOG_PRINT_MASK = 0xff00;
    public static final int ADVLOG_PRINT_BIT_TIMESTAMP = 0x100;
    public static final int ADVLOG_PRINT_BIT_THREAD_ID = 0x200;
    public static final int ADVLOG_PRINT_BIT_CONTEXT = 0x400;
    public static final int ADVLOG_PRINT_BIT_TWO_LINES = 0x800;
    public static final int ADVLOG_PRINT_MASK_DEFAULT = ADVLOG_PRINT_BIT_CONTEXT;
    public static final int ADVLOG_PRINT_MASK_USE_DEFAULT = 0xffff;
    
    
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_NUMBER = RTI_LOG_PRINT_BIT_NUMBER;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_MESSAGE = RTI_LOG_PRINT_BIT_MSG;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_MODULE = RTI_LOG_PRINT_BIT_AT_MODULE;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_FILELINE = RTI_LOG_PRINT_BIT_AT_FILE | RTI_LOG_PRINT_BIT_AT_LINE;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_METHOD = RTI_LOG_PRINT_BIT_AT_METHOD;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_TIMESTAMP = ADVLOG_PRINT_BIT_TIMESTAMP;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_THREAD_ID = ADVLOG_PRINT_BIT_THREAD_ID;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_CONTEXT = ADVLOG_PRINT_BIT_CONTEXT;
    public static final int NDDS_CONFIG_LOG_PRINT_BIT_TWO_LINES = ADVLOG_PRINT_BIT_TWO_LINES;
    
    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_DEFAULT =
            NDDS_CONFIG_LOG_PRINT_BIT_MESSAGE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_METHOD |
            NDDS_CONFIG_LOG_PRINT_BIT_CONTEXT;
    
    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_TIMESTAMPED =
            NDDS_CONFIG_LOG_PRINT_FORMAT_DEFAULT |
            NDDS_CONFIG_LOG_PRINT_BIT_TIMESTAMP;

    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_VERBOSE =
            NDDS_CONFIG_LOG_PRINT_BIT_MESSAGE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_METHOD |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_MODULE |
            NDDS_CONFIG_LOG_PRINT_BIT_THREAD_ID |
            NDDS_CONFIG_LOG_PRINT_BIT_CONTEXT |
            NDDS_CONFIG_LOG_PRINT_BIT_TWO_LINES;

    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_VERBOSE_TIMESTAMPED =
            NDDS_CONFIG_LOG_PRINT_FORMAT_VERBOSE |
            NDDS_CONFIG_LOG_PRINT_BIT_TIMESTAMP;

    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_DEBUG =
            NDDS_CONFIG_LOG_PRINT_BIT_NUMBER |
            NDDS_CONFIG_LOG_PRINT_BIT_MESSAGE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_FILELINE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_MODULE |
            NDDS_CONFIG_LOG_PRINT_BIT_THREAD_ID;

    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_MINIMAL =
            NDDS_CONFIG_LOG_PRINT_BIT_NUMBER |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_METHOD;
    
    public static final int NDDS_CONFIG_LOG_PRINT_FORMAT_MAXIMAL =
            NDDS_CONFIG_LOG_PRINT_BIT_NUMBER |
            NDDS_CONFIG_LOG_PRINT_BIT_MESSAGE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_METHOD |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_FILELINE |
            NDDS_CONFIG_LOG_PRINT_BIT_LOCATION_MODULE |
            NDDS_CONFIG_LOG_PRINT_BIT_THREAD_ID |
            NDDS_CONFIG_LOG_PRINT_BIT_CONTEXT |
            NDDS_CONFIG_LOG_PRINT_BIT_TIMESTAMP |
            NDDS_CONFIG_LOG_PRINT_BIT_TWO_LINES;

	
	Pointer NDDS_Config_Logger_get_instance();
	void    NDDS_Config_Logger_set_verbosity(Pointer self, int verbosity);
	int     NDDS_Config_Logger_get_verbosity(Pointer self);
	int     NDDS_Config_Logger_get_verbosity_by_category(Pointer self, int category);
	void    NDDS_Config_Logger_set_verbosity_by_category(Pointer self, int category, int verbosity);
	int     NDDS_Config_Logger_get_print_format(Pointer self);
	byte    NDDS_Config_Logger_set_print_format(Pointer self, int print_format);
	
	Pointer DDS_DomainParticipantFactory_get_instance();
	int     DDS_DomainParticipantFactory_finalize_instance();
	Pointer DDS_DomainParticipantFactory_create_participant(Pointer self, int domainId, Pointer qos, Pointer listener, int mask);
	int     DDS_DomainParticipantFactory_delete_participant(Pointer self, Pointer a_participant);
	int     DDS_DomainParticipantFactory_get_default_participant_qos(Pointer self, Pointer qos);
	int     DDS_DomainParticipantFactory_set_default_participant_qos(Pointer self, Pointer qos);
	
	Pointer DDS_DomainParticipant_find_topic(Pointer self, Pointer topicName, DDS_Duration_t duration);
	Pointer DDS_DomainParticipant_create_topic(Pointer self, Pointer topicName, Pointer typeName, Pointer qos, Pointer listener, int mask);
	int     DDS_DomainParticipant_delete_contained_entities(Pointer self);
	int     DDS_DomainParticipant_delete_topic(Pointer self, Pointer topic);
	Pointer DDS_DomainParticipant_create_datawriter(Pointer self, Pointer topic, Pointer qos, Pointer listener, int mask);
	Pointer DDS_DomainParticipant_create_datareader(Pointer self, Pointer topic, Pointer qos, Pointer listener, int mask);
	int     DDS_DomainParticipant_delete_datareader(Pointer self, Pointer reader);
	int     DDS_DomainParticipant_delete_datawriter(Pointer self, Pointer writer);
	int     DDS_DomainParticipant_get_domain_id(Pointer self);
	Pointer	DDS_DomainParticipant_create_publisher(Pointer self, Pointer qos, Pointer listener, int mask);
	Pointer DDS_DomainParticipant_create_subscriber(Pointer self, Pointer qos, Pointer listener, int mask);
	int     DDS_DomainParticipant_delete_publisher(Pointer self, Pointer publisher);
	int     DDS_DomainParticipant_delete_subscriber(Pointer self, Pointer subscriber);
	Pointer DDS_DomainParticipant_create_contentfilteredtopic(Pointer self, Pointer name, Pointer related_topic, Pointer filter_expression, Pointer expression_parameters);
	Pointer DDS_DomainParticipant_create_contentfilteredtopic_with_filter(Pointer self, Pointer name, Pointer related_topic, Pointer filter_expression, Pointer expression_parameters, Pointer filter_name);
	int     DDS_DomainParticipant_delete_contentfilteredtopic(Pointer self, Pointer content_filtered_topic);
	int     DDS_DomainParticipant_register_contentfilter(Pointer self, Pointer filter_name, Pointer content_filter);
	int     DDS_DomainParticipant_unregister_contentfilter(Pointer self, Pointer filter_name);
	int     DDS_DomainParticipant_get_default_publisher_qos(Pointer self, Pointer qos);
	int     DDS_DomainParticipant_get_default_subscriber_qos(Pointer self, Pointer qos);
	
	
	Pointer DDS_DynamicData_new(Pointer typeCode, Pointer property);
	void    DDS_DynamicData_delete(Pointer self);
	int     DDS_DynamicData_clear_all_members(Pointer self);
	Pointer DDS_DynamicData_get_type(Pointer self);
	
	int     DDS_DynamicData_set_string(Pointer self, Pointer member_name, int member_id, Pointer value);
	int     DDS_DynamicData_set_long(Pointer self, Pointer member_name, int member_id, int value);
	int     DDS_DynamicData_set_double(Pointer self, Pointer member_name, int member_id, double value);
	int     DDS_DynamicData_set_float(Pointer self, Pointer member_name, int member_id, float value);
	int     DDS_DynamicData_set_longlong(Pointer self, Pointer member_name, int member_id, long value);
	int     DDS_DynamicData_set_short(Pointer self, Pointer member_name, int member_id, short value);
	int     DDS_DynamicData_set_octet(Pointer self, Pointer member_name, int member_id, byte value);
	int     DDS_DynamicData_set_boolean(Pointer self, Pointer member_name, int member_id, boolean value);
	int     DDS_DynamicData_set_double_seq(Pointer self, Pointer member_name, int member_id, Pointer value);
	int     DDS_DynamicData_set_octet_seq(Pointer self, Pointer member_name, int member_id, Pointer value);
	
	int     DDS_DynamicData_get_string(Pointer self, PointerByReference value, IntByReference size, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_long(Pointer self, IntByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_double(Pointer self, DoubleByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_float(Pointer self, FloatByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_longlong(Pointer self, LongByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_short(Pointer self, ShortByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_octet(Pointer self, ByteByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_boolean(Pointer self, ByteByReference value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_double_seq(Pointer self, Pointer value, Pointer member_name, int member_id);
	int     DDS_DynamicData_get_octet_seq(Pointer self, Pointer value, Pointer member_name, int member_id);
	
	
	int     DDS_DataReader_set_listener(Pointer self, Pointer listener, int mask);
	int     DDS_DataReader_delete_contained_entities(Pointer self);
	
//	Pointer DDS_DynamicDataDataReader_as_datareader(Pointer reader);
	int     DDS_DynamicDataWriter_write(Pointer self, Pointer data, Pointer handle);
	int	    DDS_DynamicDataReader_take_next_sample(Pointer self, Pointer data, Pointer sampleInfo);
	
	
	Pointer DDS_DynamicDataTypeSupport_new(Pointer type, Pointer props);
	void    DDS_DynamicDataTypeSupport_delete(Pointer self);
	Pointer DDS_DynamicDataTypeSupport_create_data(Pointer self);
	int     DDS_DynamicDataTypeSupport_register_type(Pointer self, Pointer participant, Pointer typeName);
	int     DDS_DynamicDataTypeSupport_unregister_type(Pointer self, Pointer participant, Pointer typeName);
	int     DDS_DynamicDataTypeSupport_delete_data(Pointer self, Pointer data);
	
	Pointer DDS_TypeCodeFactory_create_sparse_tc(Pointer self, Pointer name, int modifier, Pointer concreteBase, IntByReference ex);
	Pointer DDS_TypeCodeFactory_get_instance();
	Pointer DDS_TypeCodeFactory_create_string_tc(Pointer self, int bound, IntByReference ex);
	Pointer DDS_TypeCodeFactory_create_sequence_tc(Pointer self, int bound, Pointer elementType, IntByReference ex);
	Pointer DDS_TypeCodeFactory_get_primitive_tc(Pointer self, int x);
	Pointer DDS_TypeCodeFactory_create_struct_tc(Pointer self, Pointer name, Pointer members, IntByReference exception);
	Pointer DDS_TypeCodeFactory_create_value_tc(Pointer self, Pointer name, int modifier, Pointer concrete_base, Pointer members, IntByReference exception);
	void    DDS_TypeCodeFactory_delete_tc(Pointer self, Pointer typeCode, IntByReference exception);
	
	int     DDS_TypeCode_add_member(Pointer self, Pointer name, int id, Pointer typeCode, int member_flags, IntByReference ex);
	Pointer DDS_TypeCode_member_type(Pointer self, int index, IntByReference ex);
	int     DDS_TypeCode_find_member_by_name(Pointer self, Pointer name, IntByReference ex);
	int     DDS_TypeCode_kind(Pointer self, IntByReference ex);
	Pointer DDS_TypeCode_content_type(Pointer self, IntByReference ex);
	String  DDS_TypeCode_name(Pointer self, IntByReference ex);
	int     DDS_TypeCode_member_id(Pointer self, int index, IntByReference ex);
	
	byte    DDS_DoubleSeq_initialize(Pointer self);
	byte    DDS_DoubleSeq_ensure_length(Pointer self, int length, int max);
	Pointer DDS_DoubleSeq_get_reference(Pointer self, int i);
	int     DDS_DoubleSeq_get_length(Pointer self);
	double  DDS_DoubleSeq_get(Pointer self, int i);
	byte    DDS_DoubleSeq_finalize(Pointer self);
	
	byte    DDS_StringSeq_initialize(Pointer self);
	byte    DDS_StringSeq_ensure_length(Pointer self, int length, int max);
	Pointer DDS_StringSeq_get_reference(Pointer self, int i);
	int     DDS_StringSeq_get_length(Pointer self);
	double  DDS_StringSeq_get(Pointer self, int i);
	byte    DDS_StringSeq_finalize(Pointer self);
	
	byte    DDS_OctetSeq_initialize(Pointer self);
	byte    DDS_OctetSeq_finalize(Pointer self);
	Pointer DDS_OctetSeq_copy(Pointer self, Pointer from);
	byte    DDS_OctetSeq_ensure_length(Pointer self, int length, int max);
	Pointer DDS_OctetSeq_get_reference(Pointer self, int i);
	int     DDS_OctetSeq_get_length(Pointer self);
	byte    DDS_OctetSeq_get(Pointer self, int i);
	
	Pointer DDS_Publisher_create_datawriter(Pointer self, Pointer topic, Pointer qos, Pointer listener, int mask);
	int     DDS_Publisher_delete_datawriter(Pointer self, Pointer a_datawriter);
	int     DDS_Publisher_get_default_datawriter_qos(Pointer self, Pointer qos);
	int     DDS_Publisher_set_default_datawriter_qos(Pointer self, Pointer qos);
	int     DDS_Publisher_delete_contained_entities(Pointer self);
	
	int     DDS_Subscriber_get_default_datareader_qos(Pointer self, Pointer qos);
	int     DDS_Subscriber_set_default_datareader_qos(Pointer self, Pointer qos);
	
	
	Pointer DDS_Subscriber_create_datareader(Pointer self, Pointer topic, Pointer qos, Pointer listener, int mask);
	int     DDS_Subscriber_delete_datareader(Pointer self, Pointer a_datareader);
	int     DDS_Subscriber_delete_contained_entities(Pointer self);
	
//	Pointer DDS_Topic_as_topicdescription(Pointer topic);
//	Pointer DDS_ContentFilteredTopic_as_topicdescription(Pointer content_filtered_topic);
	
	Pointer DDS_String_alloc(int length);
	Pointer DDS_String_dup(String str);
	void    DDS_String_free(Pointer str);
	
	public static final byte RTI_TRUE = 1;
	public static final byte RTI_FALSE = 0;
	
    public static final int DDS_VOLATILE_DURABILITY_QOS = 0;
    public static final int DDS_TRANSIENT_LOCAL_DURABILITY_QOS = 1;
    public static final int DDS_TRANSIENT_DURABILITY_QOS = 2;
    public static final int DDS_PERSISTENT_DURABILITY_QOS = 3;
	
	public class DDS_DurabilityQosPolicy extends Structure {
		public int kind = DDS_VOLATILE_DURABILITY_QOS;
		public byte direct_communication = 1;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind", "direct_communication"});
		}
		
		public DDS_DurabilityQosPolicy() {
			super();
			write();
		}
		public DDS_DurabilityQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_DurabilityQosPolicy(int kind, byte direct_communication) {
			super();
			this.kind = kind;
			this.direct_communication = direct_communication;
			write();
		}
	}
	
	public static final DDS_DurabilityQosPolicy DDS_DURABILITY_QOS_POLICY_DEFAULT = new DDS_DurabilityQosPolicy();
	
	public class DDS_DeadlineQosPolicy extends Structure {
		public DDS_Duration_t period = new DDS_Duration_t(0x7fffffff,  0x7fffffff);
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"period"});
		}
		
		public DDS_DeadlineQosPolicy() {
			super();
			write();
		}
		public DDS_DeadlineQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_DeadlineQosPolicy(DDS_Duration_t period) {
			super();
			this.period = period;
			write();
		}
	}
	
	public static final DDS_DeadlineQosPolicy DDS_DEADLINE_QOS_POLICY_DEFAULT = new DDS_DeadlineQosPolicy();
	
	
	public class DDS_LatencyBudgetQosPolicy extends Structure {
		public DDS_Duration_t duration = new DDS_Duration_t(0, 0);
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"duration"});
		}
		
		public DDS_LatencyBudgetQosPolicy() {
			super();
			write();
		}
		public DDS_LatencyBudgetQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_LatencyBudgetQosPolicy(DDS_Duration_t duration) {
			super();
			this.duration = duration;
			write();
		}
		
	}
	public static final DDS_LatencyBudgetQosPolicy DDS_LATENCY_BUDGET_QOS_POLICY_DEFAULT = new DDS_LatencyBudgetQosPolicy();
	
    public static final int DDS_AUTOMATIC_LIVELINESS_QOS = 0;
    public static final int DDS_MANUAL_BY_PARTICIPANT_LIVELINESS_QOS = 1;
    public static final int DDS_MANUAL_BY_TOPIC_LIVELINESS_QOS = 2;
    
    public class DDS_LivelinessQosPolicy extends Structure {
    	public int kind = DDS_AUTOMATIC_LIVELINESS_QOS;
    	public DDS_Duration_t lease_duration = new DDS_Duration_t(0x7fffffff,  0x7fffffff);
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind", "lease_duration"});
		}
    	
    	public DDS_LivelinessQosPolicy() {
    		super();
    		write();
		}
    	public DDS_LivelinessQosPolicy(Pointer p) {
    		super(p);
    	}
    	public DDS_LivelinessQosPolicy(int kind, DDS_Duration_t lease_duration) {
    		super();
    		this.kind = kind;
    		this.lease_duration = lease_duration;
    		write();
    	}
    }
    public static final DDS_LivelinessQosPolicy DDS_LIVELINESS_QOS_POLICY_DEFAULT = new DDS_LivelinessQosPolicy();
    
    public static final int DDS_BEST_EFFORT_RELIABILITY_QOS = 0;
    public static final int DDS_RELIABLE_RELIABILITY_QOS = 1;

    public static final int DDS_PROTOCOL_ACKNOWLEDGMENT_MODE = 0;
    public static final int DDS_APPLICATION_AUTO_ACKNOWLEDGMENT_MODE = 1;
    public static final int DDS_APPLICATION_ORDERED_ACKNOWLEDGMENT_MODE = 2;
    public static final int DDS_APPLICATION_EXPLICIT_ACKNOWLEDGMENT_MODE = 3;

    public class DDS_ReliabilityQosPolicy extends Structure {
    	public int kind = DDS_RELIABLE_RELIABILITY_QOS;
    	public DDS_Duration_t max_blocking_time = new DDS_Duration_t(0, 100000000);
    	public int acknowledgment_kind = DDS_PROTOCOL_ACKNOWLEDGMENT_MODE;
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind", "max_blocking_time", "acknowledgment_kind"});
		}
    	
    	public DDS_ReliabilityQosPolicy() {
    		super();
    		write();
		}
    	public DDS_ReliabilityQosPolicy(Pointer p) {
    		super(p);
    	}
    	public DDS_ReliabilityQosPolicy(short kind, DDS_Duration_t max_blocking_time, int acknowledgment_kind) {
    		super();
    		this.kind = kind;
    		this.max_blocking_time = max_blocking_time;
    		this.acknowledgment_kind = acknowledgment_kind;
    		write();
    	}
    }
    
    public static final DDS_Duration_t DDS_RELIABILITY_QOS_POLICY_MAX_BLOCKING_TIME_DEFAULT = new DDS_Duration_t(0, 100000000);
    
    public static final DDS_ReliabilityQosPolicy DDS_RELIABILITY_QOS_POLICY_DEFAULT = new DDS_ReliabilityQosPolicy();
    
    public static final int DDS_BY_RECEPTION_TIMESTAMP_DESTINATIONORDER_QOS = 0;
    public static final int DDS_BY_SOURCE_TIMESTAMP_DESTINATIONORDER_QOS = 1;

    public static final int DDS_INSTANCE_SCOPE_DESTINATIONORDER_QOS = 0;
    public static final int DDS_TOPIC_SCOPE_DESTINATIONORDER_QOS = 1;
    
    public class DDS_DestinationOrderQosPolicy extends Structure {
    	public int kind = DDS_BY_RECEPTION_TIMESTAMP_DESTINATIONORDER_QOS;
    	public int scope = DDS_INSTANCE_SCOPE_DESTINATIONORDER_QOS;
    	public DDS_Duration_t source_timestamp_tolerance = new DDS_Duration_t(0, 100000000);
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind", "scope", "source_timestamp_tolerance"});
		}
    	
    	public DDS_DestinationOrderQosPolicy() {
    		super();
    		write();
		}
    	public DDS_DestinationOrderQosPolicy(Pointer p) {
    		super(p);
		}
    	public DDS_DestinationOrderQosPolicy(int kind, int scope, DDS_Duration_t source_timestamp_tolerance) {
    		super();
    		this.kind = kind;
    		this.scope = scope;
    		this.source_timestamp_tolerance = source_timestamp_tolerance;
    		write();
		}
    }
    
    public static final DDS_DestinationOrderQosPolicy DDS_DESTINATION_ORDER_QOS_POLICY_DEFAULT = new DDS_DestinationOrderQosPolicy();
    
    public static final int DDS_KEEP_LAST_HISTORY_QOS = 0;
    public static final int DDS_KEEP_ALL_HISTORY_QOS = 1;

	public static final int DDS_NONE_REFILTER_QOS = 0;
	public static final int DDS_ALL_REFILTER_QOS = 1;
	public static final int DDS_ON_DEMAND_REFILTER_QOS = 2;
	
	public class DDS_HistoryQosPolicy extends Structure {
		public int kind = DDS_KEEP_LAST_HISTORY_QOS;
		public int depth = 1;
		public int refilter = DDS_NONE_REFILTER_QOS;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind", "depth", "refilter"});
		}
		
		public DDS_HistoryQosPolicy() {
			super();
		}
		public DDS_HistoryQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_HistoryQosPolicy(int kind, int depth, int refilter) {
			super();
			this.kind = kind;
			this.depth = depth;
			this.refilter = refilter;
			write();
		}
	}
	
	public static final DDS_HistoryQosPolicy DDS_HISTORY_QOS_POLICY_DEFAULT = new DDS_HistoryQosPolicy();
	
	public class DDS_ResourceLimitsQosPolicy extends Structure {
		public int max_samples = -1;
		public int max_instances = -1;
		public int max_samples_per_instance = -1;
		public int initial_samples = 32;
		public int initial_instances = 32;
		public int instance_hash_buckets = 1;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"max_samples", "max_instances", "max_samples_per_instance", "initial_samples",
					"initial_instances","instance_hash_buckets"});
		}
		
		public DDS_ResourceLimitsQosPolicy() {
			super();
			write();
		}
		public DDS_ResourceLimitsQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_ResourceLimitsQosPolicy(int max_samples, int max_instances, int max_samples_per_instance, int initial_samples, int initial_instances, int instance_hash_buckets) {
			super();
			this.max_samples = max_samples;
			this.max_instances = max_instances;
			this.max_samples_per_instance = max_samples_per_instance;
			this.initial_samples = initial_samples;
			this.initial_instances = initial_instances;
			this.instance_hash_buckets = instance_hash_buckets;
			write();
		}
	}
	
	public static final DDS_ResourceLimitsQosPolicy DDS_RESOURCE_LIMITS_QOS_POLICY_DEFAULT = new DDS_ResourceLimitsQosPolicy();

	public class DDS_UserDataQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		
		public DDS_UserDataQosPolicy() {
			super();
			write();
		}
		public DDS_UserDataQosPolicy(Pointer p) {
			super(p);
		}
		public DDS_UserDataQosPolicy(DDS_Sequence value) {
			super();
			this.value = value;
			write();
		}
	}
	
	public static final DDS_UserDataQosPolicy DDS_USER_DATA_QOS_POLICY_DEFAULT = new DDS_UserDataQosPolicy();
	
    public static final int DDS_SHARED_OWNERSHIP_QOS = 0;
    public static final int DDS_EXCLUSIVE_OWNERSHIP_QOS = 1;
    
    public class DDS_OwnershipQosPolicy extends Structure {
    	public int kind = DDS_SHARED_OWNERSHIP_QOS;
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind"});
		}
    	
    	public DDS_OwnershipQosPolicy() {
    		super();
		}
    	public DDS_OwnershipQosPolicy(Pointer p) {
    		super(p);
		}
    	public DDS_OwnershipQosPolicy(int kind) {
    		super();
    		this.kind = kind;
    		write();
		}
    }
    
    public static final DDS_OwnershipQosPolicy DDS_OWNERSHIP_QOS_POLICY_DEFAULT = new DDS_OwnershipQosPolicy();
    
    public class DDS_TimeBasedFilterQosPolicy extends Structure {
    	public DDS_Duration_t minimum_separation = new DDS_Duration_t(0, 0);
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"minimum_separation"});
		}
    	
    	public DDS_TimeBasedFilterQosPolicy() {
			super();
			write();
		}
    	public DDS_TimeBasedFilterQosPolicy(Pointer p) {
			super(p);
		}
    	public DDS_TimeBasedFilterQosPolicy(DDS_Duration_t minimum_separation) {
			super();
			this.minimum_separation = minimum_separation;
			write();
		}
    }
    
    public static final DDS_TimeBasedFilterQosPolicy DDS_TIME_BASED_FILTER_QOS_POLICY_DEFAULT = new DDS_TimeBasedFilterQosPolicy();
    
    public class DDS_ReaderDataLifecycleQosPolicy extends Structure {
    	public DDS_Duration_t autopurge_nowriter_samples_delay = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
    	public DDS_Duration_t autopurge_disposed_samples_delay = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"autopurge_nowriter_samples_delay", "autopurge_disposed_samples_delay"});
		}
    	
    	public DDS_ReaderDataLifecycleQosPolicy() {
    		super();
    		write();
		}
    	public DDS_ReaderDataLifecycleQosPolicy(Pointer p) {
    		super(p);
		}
    	public DDS_ReaderDataLifecycleQosPolicy(DDS_Duration_t autopurge_nowriter_samples_delay, DDS_Duration_t autopurge_disposed_samples_delay) {
    		super();
    		this.autopurge_nowriter_samples_delay = autopurge_nowriter_samples_delay;
    		this.autopurge_disposed_samples_delay = autopurge_disposed_samples_delay;
    		write();
		}
    }
    public static final DDS_ReaderDataLifecycleQosPolicy DDS_READER_DATA_LIFECYCLE_QOS_POLICY_DEFAULT = new DDS_ReaderDataLifecycleQosPolicy( );
    
    public static final int DDS_DISALLOW_TYPE_COERCION = 0;
    public static final int DDS_ALLOW_TYPE_COERCION = 1;

    
    public class DDS_TypeConsistencyEnforcementQosPolicy extends Structure {
        public DDS_TypeConsistencyEnforcementQosPolicy() {
            super();
            write();
        }
        public DDS_TypeConsistencyEnforcementQosPolicy(Pointer p) {
            super(p);
        }
        public int kind = DDS_ALLOW_TYPE_COERCION;
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("kind");
        }
    }
    
    
    public class DDS_DataReaderResourceLimitsQosPolicy extends Structure {
        public int max_remote_writers = -1;
        public int max_remote_writers_per_instance = -1;
        public int max_samples_per_remote_writer = -1;
        public int max_infos = -1;
        public int initial_remote_writers = 2;
        public int initial_remote_writers_per_instance = 2;
        public int initial_infos = 32;
        public int initial_outstanding_reads = 2;
        public int max_outstanding_reads = -1;
        public int max_samples_per_read = 1024;
        public byte disable_fragmentation_support = 0;
        public int max_fragmented_samples = 1024;
        public int initial_fragmented_samples = 4;
        public int max_fragmented_samples_per_remote_writer = 256;
        public int max_fragments_per_sample = 512;
        public byte dynamically_allocate_fragmented_samples = 0;
        public int max_total_instances = 0;
        public int max_remote_virtual_writers = -1;
        public int initial_remote_virtual_writers = 2;
        public int max_remote_virtual_writers_per_instance = -1;
        public int initial_remote_virtual_writers_per_instance = 2;
        public int max_remote_writers_per_sample = 3;
        public int max_query_condition_filters = 4;
        public int max_app_ack_response_length = 0;
        
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList("max_remote_writers", "max_remote_writers_per_instance", "max_samples_per_remote_writer", "max_infos",
					"initial_remote_writers","initial_remote_writers_per_instance","initial_infos","initial_outstanding_reads",
					"max_outstanding_reads","max_samples_per_read","disable_fragmentation_support","max_fragmented_samples",
					"initial_fragmented_samples","max_fragmented_samples_per_remote_writer","max_fragments_per_sample","dynamically_allocate_fragmented_samples",
					"max_total_instances","max_remote_virtual_writers","initial_remote_virtual_writers","max_remote_virtual_writers_per_instance",
					"initial_remote_virtual_writers_per_instance","max_remote_writers_per_sample","max_query_condition_filters", "max_app_ack_response_length");
		}
        
        public DDS_DataReaderResourceLimitsQosPolicy() {
        	super();
        	write();
		}
        public DDS_DataReaderResourceLimitsQosPolicy(Pointer p) {
        	super(p);
		}
    }
    public static final DDS_DataReaderResourceLimitsQosPolicy DDS_DATA_READER_RESOURCE_LIMITS_QOS_POLICY_DEFAULT = new DDS_DataReaderResourceLimitsQosPolicy();

    
    public class DDS_RtpsReliableReaderProtocol_t extends Structure {
        public DDS_Duration_t min_heartbeat_response_delay = new DDS_Duration_t(0, 0);
        public DDS_Duration_t max_heartbeat_response_delay = new DDS_Duration_t(0, 500000000);
        public DDS_Duration_t heartbeat_suppression_duration = new DDS_Duration_t(0, 62500 * 1000);
        public DDS_Duration_t nack_period = new DDS_Duration_t(5, 0);
        public int receive_window_size = 256;
        public DDS_Duration_t round_trip_time = new DDS_Duration_t(0, 0);
        public DDS_Duration_t app_ack_period = new DDS_Duration_t(5, 0);
        public DDS_Duration_t min_app_ack_response_keep_duration = new DDS_Duration_t(0,0);
        public int samples_per_app_ack = 1;
        
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"min_heartbeat_response_delay","max_heartbeat_response_delay","heartbeat_suppression_duration","nack_period","receive_window_size","round_trip_time",
			        "app_ack_period", "min_app_ack_response_keep_duration", "samples_per_app_ack"});
		}
        
        public DDS_RtpsReliableReaderProtocol_t() {
        	super();
        	write();
        }
        public DDS_RtpsReliableReaderProtocol_t(Pointer p) {
        	super(p);
        }
        public DDS_RtpsReliableReaderProtocol_t(DDS_Duration_t min_heartbeat_response_delay, DDS_Duration_t max_heartbeat_response_delay, DDS_Duration_t heartbeat_suppression_duration, DDS_Duration_t nack_period, int receive_window_size, DDS_Duration_t round_trip_time) {
        	super();
        	this.min_heartbeat_response_delay = min_heartbeat_response_delay;
        	this.max_heartbeat_response_delay = max_heartbeat_response_delay;
        	this.heartbeat_suppression_duration = heartbeat_suppression_duration;
        	this.nack_period = nack_period;
        	this.receive_window_size = receive_window_size;
        	this.round_trip_time = round_trip_time;
        	write();
        }
    };
    
    public static final DDS_RtpsReliableReaderProtocol_t DDS_RTPS_RELIABLE_READER_PROTOCOL_DEFAULT = new DDS_RtpsReliableReaderProtocol_t();
    public static final DDS_RtpsReliableReaderProtocol_t DDS_RTPS_RELIABLE_READER_PROTOCOL_DISCOVERY_CONFIG_DEFAULT = new DDS_RtpsReliableReaderProtocol_t(new DDS_Duration_t(0, 0), new DDS_Duration_t(0, 0), new DDS_Duration_t(0, 62500 * 1000), new DDS_Duration_t(5, 0), 256, new DDS_Duration_t(0, 0));
    public static final DDS_RtpsReliableReaderProtocol_t DDS_RTPS_PARTICIPANT_MESSAGE_READER_DISCOVERY_CONFIG_DEFAULT = new DDS_RtpsReliableReaderProtocol_t(new DDS_Duration_t(0, 0), new DDS_Duration_t(0, 0), new DDS_Duration_t(0, 62500 * 1000), new DDS_Duration_t(5, 0), 256, new DDS_Duration_t(0, 0));
    

	public class DDS_DataReaderProtocolQosPolicy extends Structure {
		public DDS_GUID_t virtual_guid = new DDS_GUID_t();
		public int rtps_object_id = 0;
		public byte expects_inline_qos = 0;
		public byte disable_positive_acks = 0;
    	public byte propagate_dispose_of_unregistered_instances = 0;
    	public DDS_RtpsReliableReaderProtocol_t rtps_reliable_reader = new DDS_RtpsReliableReaderProtocol_t();
    	public byte vendor_specific_entity = 0;
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"virtual_guid","rtps_object_id","expects_inline_qos","disable_positive_acks","propagate_dispose_of_unregistered_instances","rtps_reliable_reader","vendor_specific_entity"});
		}
    	
    	public DDS_DataReaderProtocolQosPolicy() {
    		super();
    		write();
    	}
    	public DDS_DataReaderProtocolQosPolicy(Pointer p) {
    		super(p);
    	}
	}
	
	public static final DDS_DataReaderProtocolQosPolicy DDS_DATA_READER_PROTOCOL_QOS_POLICY_DEFAULT = new DDS_DataReaderProtocolQosPolicy();
	
	public class DDS_TransportSelectionQosPolicy extends Structure {
		public DDS_StringSequence enabled_transports = new DDS_StringSequence();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"enabled_transports"});
		}
		
		public DDS_TransportSelectionQosPolicy() {
			super();
			write();
		}
		
		public DDS_TransportSelectionQosPolicy(Pointer p) {
			super(p);
		}
		
	}
	public static final DDS_TransportSelectionQosPolicy DDS_TRANSPORT_SELECTION_QOS_POLICY_DEFAULT = new DDS_TransportSelectionQosPolicy();
	
	public class DDS_TransportUnicastQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		public DDS_TransportUnicastQosPolicy() {
			super();
			write();
		}
		public DDS_TransportUnicastQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TransportUnicastQosPolicy DDS_TRANSPORT_UNICAST_QOS_POLICY_DEFAULT = new DDS_TransportUnicastQosPolicy();
	
	public static final int DDS_AUTOMATIC_TRANSPORT_MULTICAST_QOS = 0;
	public static final int DDS_UNICAST_ONLY_TRANSPORT_MULTICAST_QOS = 1;
	
	public class DDS_TransportMulticastQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		public int kind;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value","kind"});
		}
		
		public DDS_TransportMulticastQosPolicy() {
			super();
			write();
		}
		public DDS_TransportMulticastQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TransportMulticastQosPolicy DDS_TRANSPORT_MULTICAST_QOS_POLICY_DEFAULT = new DDS_TransportMulticastQosPolicy();
	
	public class DDS_TransportMulticastMappingQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		
		public DDS_TransportMulticastMappingQosPolicy() {
			super();
			write();
		}
		public DDS_TransportMulticastMappingQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TransportMulticastMappingQosPolicy DDS_TRANSPORT_MULTICAST_MAPPING_QOS_POLICY_DEFAULT = new DDS_TransportMulticastMappingQosPolicy();
	
	public class DDS_PropertyQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		
		public DDS_PropertyQosPolicy() {
			super();
			write();
		}
		public DDS_PropertyQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_PropertyQosPolicy DDS_PROPERTY_QOS_POLICY_DEFAULT = new DDS_PropertyQosPolicy();
	
	public class DDS_AvailabilityQosPolicy extends Structure {
	    public byte enable_required_subscriptions;
		public DDS_Duration_t max_data_availability_waiting_time = new DDS_Duration_t(0xffffffff, 0);
		public DDS_Duration_t max_endpoint_availabilty_waiting_time = new DDS_Duration_t(0xffffffff, 0);
		public DDS_Sequence required_matched_endpoint_groups = new DDS_Sequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"enable_required_subscriptions", "max_data_availability_waiting_time","max_endpoint_availabilty_waiting_time","required_matched_endpoint_groups"});
		}
		public DDS_AvailabilityQosPolicy() {
			super();
			write();
		}
		public DDS_AvailabilityQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_AvailabilityQosPolicy DDS_AVAILABILITY_QOS_POLICY_DEFAULT = new DDS_AvailabilityQosPolicy();
	
	public class DDS_EntityNameQosPolicy extends Structure {
		public Pointer name = Pointer.NULL;
		public Pointer role_name = Pointer.NULL;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"name","role_name"});
		}
		public DDS_EntityNameQosPolicy() {
			super();
		}
		public DDS_EntityNameQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_EntityNameQosPolicy DDS_ENTITY_NAME_QOS_POLICY_DEFAULT = new DDS_EntityNameQosPolicy();
	
	public class DDS_TypeSupportQosPolicy extends Structure {
		public Pointer plugin_data = Pointer.NULL;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"plugin_data"});
		}
		public DDS_TypeSupportQosPolicy() {
			super();
			write();
		}
		public DDS_TypeSupportQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TypeSupportQosPolicy DDS_TYPESUPPORT_QOS_POLICY_DEFAULT = new DDS_TypeSupportQosPolicy();
	
	public class DDS_TransportEncapsulationQosPolicy extends Structure {
		public DDS_Sequence value = new DDS_Sequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		public DDS_TransportEncapsulationQosPolicy() {
			super();
			write();
		}
		public DDS_TransportEncapsulationQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TransportEncapsulationQosPolicy DDS_TRANSPORT_ENCAPSULATION_QOS_POLICY_DEFAULT = new DDS_TransportEncapsulationQosPolicy();
	

	public class DDS_DurabilityServiceQosPolicy extends Structure {
		public DDS_Duration_t service_cleanup_delay = new DDS_Duration_t(0,0);
		public int history_kind = DDS_KEEP_LAST_HISTORY_QOS;
		public int history_depth = 1;
		public int max_samples = -1;
		public int max_instances = -1;
		public int max_samples_per_instance = -1;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"service_cleanup_delay","history_kind","history_depth","max_samples","max_instances","max_samples_per_instance"});
		}
		public DDS_DurabilityServiceQosPolicy() {
			super();
			write();
		}
		public DDS_DurabilityServiceQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_DurabilityServiceQosPolicy DDS_DURABILITY_SERVICE_QOS_POLICY_DEFAULT = new DDS_DurabilityServiceQosPolicy();
	
	public static final int DDS_NO_SERVICE_QOS = 0;
	public static final int DDS_PERSISTENCE_SERVICE_QOS = 1;

	public class DDS_ServiceQosPolicy extends Structure {
		public int kind = DDS_NO_SERVICE_QOS;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind"});
		}
		public DDS_ServiceQosPolicy() {
			super();
			write();
		}
		public DDS_ServiceQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_ServiceQosPolicy DDS_SERVICE_QOS_POLICY_DEFAULT = new DDS_ServiceQosPolicy();
	
	public class DDS_TransportPriorityQosPolicy extends Structure {
		public int value = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		public DDS_TransportPriorityQosPolicy() {
			super();
			write();
		}
		public DDS_TransportPriorityQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_TransportPriorityQosPolicy DDS_TRANSPORT_PRIORITY_QOS_POLICY_DEFAULT = new DDS_TransportPriorityQosPolicy();
	
	public class DDS_LifespanQosPolicy extends Structure {
		public DDS_Duration_t duration = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"duration"});
		}
		public DDS_LifespanQosPolicy() {
			super();
			write();
		}
		public DDS_LifespanQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_LifespanQosPolicy DDS_LIFESPAN_QOS_POLICY_DEFAULT = new DDS_LifespanQosPolicy();
	
	public class DDS_OwnershipStrengthQosPolicy extends Structure {
		public int value = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
		public DDS_OwnershipStrengthQosPolicy() {
			super();
			write();
		}
		public DDS_OwnershipStrengthQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_OwnershipStrengthQosPolicy DDS_OWNERSHIP_STRENGTH_QOS_POLICY_DEFAULT = new DDS_OwnershipStrengthQosPolicy(); 
	
	public static final int PRES_LENGTH_UNLIMITED = -1;
	
	public class DDS_BatchQosPolicy extends Structure {
		public byte enable = 0;
		public int max_data_bytes = 1024;
		public int max_meta_data_bytes = PRES_LENGTH_UNLIMITED;
	    public int max_samples = PRES_LENGTH_UNLIMITED;
	    public DDS_Duration_t max_flush_delay = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
		public DDS_Duration_t source_timestamp_resolution = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
		public byte thread_safe_write = 1;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"enable","max_data_bytes","max_meta_data_bytes","max_samples","max_flush_delay","source_timestamp_resolution","thread_safe_write"});
		}
		public DDS_BatchQosPolicy() {
			super();
			write();
		}
		public DDS_BatchQosPolicy(Pointer p) {
			super(p);
		}
	}
	
	public static final DDS_BatchQosPolicy DDS_BATCH_QOS_POLICY_DEFAULT = new DDS_BatchQosPolicy();
	
	public class DDS_MultiChannelQosPolicy extends Structure {
		public DDS_Sequence channels = new DDS_Sequence();
		public Pointer filter_name = Pointer.NULL;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"channels","filter_name"});
		}
		
		public DDS_MultiChannelQosPolicy() {
			super();
			write();
		}
		public DDS_MultiChannelQosPolicy(Pointer p) {
			super(p);
		}
	}
	public static final DDS_MultiChannelQosPolicy DDS_MULTICHANNEL_QOS_POLICY = new DDS_MultiChannelQosPolicy();
	
	public static final int DDS_SYNCHRONOUS_PUBLISH_MODE_QOS = 0;
	public static final int DDS_ASYNCHRONOUS_PUBLISH_MODE_QOS = 1; 

	public static final int DDS_PUBLICATION_PRIORITY_UNDEFINED = 0;
	public static final int DDS_PUBLICATION_PRIORITY_AUTOMATIC = -1;

	  
	public class DDS_PublishModeQosPolicy extends Structure {
		public int kind = DDS_SYNCHRONOUS_PUBLISH_MODE_QOS;
		public Pointer flow_controller_name = Pointer.NULL;
		public int priority = DDS_PUBLICATION_PRIORITY_UNDEFINED;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"kind","flow_controller_name","priority"});
		}
		public DDS_PublishModeQosPolicy() {
			super();
			write();
		}
		public DDS_PublishModeQosPolicy(Pointer p) {
			super(p);
		}
	}
	
	public static final DDS_PublishModeQosPolicy DDS_PUBLISH_MODE_QOS_POLICY_DEFAULT = new DDS_PublishModeQosPolicy();
	
	public class DDS_WriterDataLifecycleQosPolicy extends Structure {
		public byte autodispose_unregistered_instances = 1;
		public DDS_Duration_t autopurge_unregistered_instances_delay = new DDS_Duration_t(0x7fffffff, 0x7fffffff);
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"autodispose_unregistered_instances","autopurge_unregistered_instances_delay"});
		}
		
		public DDS_WriterDataLifecycleQosPolicy() {
			super();
			write();
		}
		public DDS_WriterDataLifecycleQosPolicy(Pointer p) {
			super(p);
		}
	}
	
	public static final DDS_WriterDataLifecycleQosPolicy DDS_WRITER_DATA_LIFECYCLE_QOS_POLICY_DEFAULT = new DDS_WriterDataLifecycleQosPolicy();

	public static final int DDS_UNREGISTERED_INSTANCE_REPLACEMENT = 0;
	public static final int DDS_ALIVE_INSTANCE_REPLACEMENT = 1;
	public static final int DDS_DISPOSED_INSTANCE_REPLACEMENT = 2;
	public static final int DDS_ALIVE_THEN_DISPOSED_INSTANCE_REPLACEMENT = 3;
	public static final int DDS_DISPOSED_THEN_ALIVE_INSTANCE_REPLACEMENT = 4;
	public static final int DDS_ALIVE_OR_DISPOSED_INSTANCE_REPLACEMENT = 5;
	
	public class DDS_DataWriterResourceLimitsQosPolicy  extends Structure {
	    public int initial_concurrent_blocking_threads = 1;
	    public int max_concurrent_blocking_threads = -1;
	    public int max_remote_reader_filters = 32;
	    public int initial_batches = 8;
	    public int max_batches = -1;
	    public int cookie_max_length = -1;
	    public int instance_replacement = DDS_UNREGISTERED_INSTANCE_REPLACEMENT;
	    public byte  replace_empty_instances = 0;
	    public byte autoregister_instances = 0;
	    public int initial_virtual_writers = 1;
	    public int max_virtual_writers = -1;
	    public int max_remote_readers = -1;
	    public int max_app_ack_remote_readers = -1;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"initial_concurrent_blocking_threads","max_concurrent_blocking_threads","max_remote_reader_filters",
					"initial_batches","max_batches","cookie_max_length","instance_replacement","replace_empty_instances","autoregister_instances",
					"initial_virtual_writers","max_virtual_writers", "max_remote_readers", "max_app_ack_remote_readers"});
		}
	    public DDS_DataWriterResourceLimitsQosPolicy() {
	    	super();
	    	write();
		}
	    public DDS_DataWriterResourceLimitsQosPolicy(Pointer p) {
	    	super(p);
	    }
	}

	public static final DDS_DataWriterResourceLimitsQosPolicy DDS_DATA_WRITER_RESOURCE_LIMITS_QOS_POLICY_DEFAULT = new DDS_DataWriterResourceLimitsQosPolicy();
	
	public static final int DDS_RTPS_AUTO_ID = 0;
	
	public class DDS_SequenceNumber_t extends Structure {
		public int high;
		public int low;
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"high","low"});
		}
		public DDS_SequenceNumber_t() {
			super();
			write();
		}
		public DDS_SequenceNumber_t(int high, int low) {
			super();
			this.high = high;
			this.low = low;
			write();
		}
		public DDS_SequenceNumber_t(Pointer p) {
			super(p);
		}
	}

	public class DDS_RtpsReliableWriterProtocol_t extends Structure {
		public int low_watermark = 0;
	    public int high_watermark = 1;
	    public DDS_Duration_t heartbeat_period = new DDS_Duration_t(3, 0);
	    public DDS_Duration_t fast_heartbeat_period = new DDS_Duration_t(3, 0);
	    public DDS_Duration_t late_joiner_heartbeat_period = new DDS_Duration_t(3, 0);
	    public DDS_Duration_t virtual_heartbeat_period = new DDS_Duration_t(-1, 0);
	    public int samples_per_virtual_heartbeat = -1;
	    public int max_heartbeat_retries = 10;
	    public byte inactivate_nonprogressing_readers = 0;
	    public int heartbeats_per_max_samples = 8;
	    public DDS_Duration_t min_nack_response_delay = new DDS_Duration_t(0,0);
	    public DDS_Duration_t max_nack_response_delay = new DDS_Duration_t(0, 200000000);
	    public DDS_Duration_t nack_suppression_duration = new DDS_Duration_t(0,0);
	    public int max_bytes_per_nack_response = 131072;
	    public DDS_Duration_t disable_positive_acks_min_sample_keep_duration = new DDS_Duration_t(0, 1000000);
	    public DDS_Duration_t disable_positive_acks_max_sample_keep_duration = new DDS_Duration_t(1, 0);
	    public DDS_Duration_t disable_positive_acks_sample_min_separation = new DDS_Duration_t(0, 100000);
	    public byte disable_positive_acks_enable_adaptive_sample_keep_duration = 1;
	    public byte disable_positive_acks_enable_spin_wait = 0;
	    public int disable_positive_acks_decrease_sample_keep_duration_factor = 95;
	    public int disable_positive_acks_increase_sample_keep_duration_factor = 150;
	    public int min_send_window_size = -1;
	    public int max_send_window_size = -1;
	    public DDS_Duration_t send_window_update_period = new DDS_Duration_t(3, 0);
	    public int send_window_increase_factor = 105;
	    public int send_window_decrease_factor = 70;
	    public byte enable_multicast_periodic_heartbeat = 0;	    
	    public int multicast_resend_threshold = 2;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"low_watermark","high_watermark","heartbeat_period","fast_heartbeat_period",
					"late_joiner_heartbeat_period","virtual_heartbeat_period","samples_per_virtual_heartbeat","max_heartbeat_retries",
					"inactivate_nonprogressing_readers","heartbeats_per_max_samples","min_nack_response_delay","max_nack_response_delay",
					"nack_suppression_duration","max_bytes_per_nack_response","disable_positive_acks_min_sample_keep_duration",
					"disable_positive_acks_max_sample_keep_duration","disable_positive_acks_sample_min_separation",
					"disable_positive_acks_enable_adaptive_sample_keep_duration","disable_positive_acks_enable_spin_wait",
					"disable_positive_acks_decrease_sample_keep_duration_factor","disable_positive_acks_increase_sample_keep_duration_factor",
					"min_send_window_size","max_send_window_size","send_window_update_period","send_window_increase_factor","send_window_decrease_factor",
					"enable_multicast_periodic_heartbeat","multicast_resend_threshold"});
		}
	    
	    public DDS_RtpsReliableWriterProtocol_t() {
	    	super();
	    	write();
		}
	    public DDS_RtpsReliableWriterProtocol_t(Pointer p) {
	    	super(p);
	    }
	}
	
	public static final DDS_RtpsReliableWriterProtocol_t DDS_RTPS_RELIABLE_WRITER_PROTOCOL_DEFAULT = new DDS_RtpsReliableWriterProtocol_t();
	
	public class DDS_DataWriterProtocolQosPolicy  extends Structure {
	    public DDS_GUID_t virtual_guid = new DDS_GUID_t();
	    public int rtps_object_id = DDS_RTPS_AUTO_ID;
	    public byte push_on_write = 1;
	    public byte disable_positive_acks = 0;
	    public byte disable_inline_keyhash = 0;
	    public byte serialize_key_with_dispose = 0;
	    public DDS_RtpsReliableWriterProtocol_t rtps_reliable_writer = new DDS_RtpsReliableWriterProtocol_t();
	    public DDS_SequenceNumber_t initial_virtual_sequence_number = new DDS_SequenceNumber_t(-1,-1);
	    public byte vendor_specific_entity = 0;
	    
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"virtual_guid","rtps_object_id","push_on_write","disable_positive_acks","disable_inline_keyhash","serialize_key_with_dispose",
					"rtps_reliable_writer","initial_virtual_sequence_number","vendor_specific_entity"});
		}
	    
	    public DDS_DataWriterProtocolQosPolicy() {
	    	super();
//	    	setAlignType(ALIGN_NONE);
	    	write();
		}
	    public DDS_DataWriterProtocolQosPolicy(Pointer p) {
	    	super(p);
//	    	setAlignType(ALIGN_NONE);
	    }
	}
	
	public static final DDS_DataWriterProtocolQosPolicy DDS_DATAWRITER_PROTOCOL_QOS_POLICY_DEFAULT = new DDS_DataWriterProtocolQosPolicy();


	
	public class DDS_DataReaderQos extends Structure {
		public DDS_DurabilityQosPolicy durability = new DDS_DurabilityQosPolicy();
		public DDS_DeadlineQosPolicy deadline = new DDS_DeadlineQosPolicy();
		public DDS_LatencyBudgetQosPolicy latency_budget = new DDS_LatencyBudgetQosPolicy();
		public DDS_LivelinessQosPolicy liveliness = new DDS_LivelinessQosPolicy();
		public DDS_ReliabilityQosPolicy reliability = new DDS_ReliabilityQosPolicy();
		public DDS_DestinationOrderQosPolicy destination_order = new DDS_DestinationOrderQosPolicy();
		public DDS_HistoryQosPolicy history = new DDS_HistoryQosPolicy();
		public DDS_ResourceLimitsQosPolicy resource_limits = new DDS_ResourceLimitsQosPolicy();
		public DDS_UserDataQosPolicy user_data = new DDS_UserDataQosPolicy();
		public DDS_OwnershipQosPolicy ownership = new DDS_OwnershipQosPolicy();
		public DDS_TimeBasedFilterQosPolicy time_based_filter = new DDS_TimeBasedFilterQosPolicy();
		public DDS_ReaderDataLifecycleQosPolicy reader_data_lifecycle = new DDS_ReaderDataLifecycleQosPolicy();
		public DDS_TypeConsistencyEnforcementQosPolicy type_consistency = new DDS_TypeConsistencyEnforcementQosPolicy();
		public DDS_DataReaderResourceLimitsQosPolicy reader_resource_limits = new DDS_DataReaderResourceLimitsQosPolicy();
		public DDS_DataReaderProtocolQosPolicy protocol = new DDS_DataReaderProtocolQosPolicy();
		public DDS_TransportSelectionQosPolicy transport_selection = new DDS_TransportSelectionQosPolicy();
		public DDS_TransportUnicastQosPolicy unicast = new DDS_TransportUnicastQosPolicy();
		public DDS_TransportMulticastQosPolicy multicast = new DDS_TransportMulticastQosPolicy();
		public DDS_TransportEncapsulationQosPolicy encapsulation = new DDS_TransportEncapsulationQosPolicy();
		public DDS_PropertyQosPolicy property = new DDS_PropertyQosPolicy();
		public DDS_ServiceQosPolicy service = new DDS_ServiceQosPolicy();
		public DDS_AvailabilityQosPolicy availability = new DDS_AvailabilityQosPolicy();
		public DDS_EntityNameQosPolicy subscription_name = new DDS_EntityNameQosPolicy();
		public DDS_TypeSupportQosPolicy type_support = new DDS_TypeSupportQosPolicy();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"durability","deadline","latency_budget","liveliness","reliability","destination_order","history","resource_limits","user_data",
					"ownership","time_based_filter","reader_data_lifecycle","type_consistency","reader_resource_limits","protocol","transport_selection","unicast","multicast","encapsulation","property",
					"service","availability","subscription_name","type_support"});
		}
		
		public DDS_DataReaderQos() {
			super();
			// These are the real defaults for DDS_DATAREADER_QOS_POLICY_DEFAULT
			reliability.kind = DDS_BEST_EFFORT_RELIABILITY_QOS;
			destination_order.source_timestamp_tolerance = new DDS_Duration_t(30,0);
			write();
		}
		
		public DDS_DataReaderQos(Pointer p) {
			super(p);
		}
	}
	
	
	
	public class DDS_DataWriterQos extends Structure {
		public DDS_DurabilityQosPolicy durability = new DDS_DurabilityQosPolicy();
		public DDS_DurabilityServiceQosPolicy durability_service = new DDS_DurabilityServiceQosPolicy();
		public DDS_DeadlineQosPolicy deadline = new DDS_DeadlineQosPolicy();
		public DDS_LatencyBudgetQosPolicy latency_budget = new DDS_LatencyBudgetQosPolicy();
		public DDS_LivelinessQosPolicy liveliness = new DDS_LivelinessQosPolicy();
		public DDS_ReliabilityQosPolicy reliability = new DDS_ReliabilityQosPolicy();
		public DDS_DestinationOrderQosPolicy destination_order = new DDS_DestinationOrderQosPolicy();
		public DDS_HistoryQosPolicy history = new DDS_HistoryQosPolicy();
		public DDS_ResourceLimitsQosPolicy resource_limits = new DDS_ResourceLimitsQosPolicy();
		public DDS_TransportPriorityQosPolicy transport_priority = new DDS_TransportPriorityQosPolicy();
		public DDS_LifespanQosPolicy lifespan = new DDS_LifespanQosPolicy();
		public DDS_UserDataQosPolicy user_data = new DDS_UserDataQosPolicy();
		public DDS_OwnershipQosPolicy ownership = new DDS_OwnershipQosPolicy();
		public DDS_OwnershipStrengthQosPolicy ownership_strength = new DDS_OwnershipStrengthQosPolicy();
		public DDS_WriterDataLifecycleQosPolicy writer_data_lifecycle = new DDS_WriterDataLifecycleQosPolicy();
		public DDS_DataWriterResourceLimitsQosPolicy writer_resource_limits = new DDS_DataWriterResourceLimitsQosPolicy();
		public DDS_DataWriterProtocolQosPolicy protocol = new DDS_DataWriterProtocolQosPolicy();
		public DDS_TransportSelectionQosPolicy transport_selection = new DDS_TransportSelectionQosPolicy();
		public DDS_TransportUnicastQosPolicy unicast = new DDS_TransportUnicastQosPolicy();
		public DDS_TransportEncapsulationQosPolicy encapsulation = new DDS_TransportEncapsulationQosPolicy();
		public DDS_PublishModeQosPolicy publish_mode = new DDS_PublishModeQosPolicy();
		public DDS_PropertyQosPolicy property = new DDS_PropertyQosPolicy();
		public DDS_ServiceQosPolicy service = new DDS_ServiceQosPolicy();
		public DDS_BatchQosPolicy batch = new DDS_BatchQosPolicy();
		public DDS_MultiChannelQosPolicy multi_channel = new DDS_MultiChannelQosPolicy();
		public DDS_AvailabilityQosPolicy availability = new DDS_AvailabilityQosPolicy();
		public DDS_EntityNameQosPolicy publication_name = new DDS_EntityNameQosPolicy();
		public DDS_TypeSupportQosPolicy type_support = new DDS_TypeSupportQosPolicy();

		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"durability","durability_service","deadline","latency_budget","liveliness","reliability","destination_order","history","resource_limits",
					"transport_priority","lifespan","user_data","ownership","ownership_strength","writer_data_lifecycle","writer_resource_limits","protocol","transport_selection","unicast",
					"encapsulation","publish_mode","property","service","batch","multi_channel","availability","publication_name","type_support"});
		}
		public DDS_DataWriterQos() {
			super();
			write();
		}
	}
	public class DDS_EntityFactoryQosPolicy extends Structure {
	    public byte autoenable_created_entities = 1;
//	    public byte padding = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"autoenable_created_entities"});
		}
	    
	    public DDS_EntityFactoryQosPolicy() {
	    	super();
	    	write();
	    }
	};
	
	public static final int DDS_RTPS_AUTO_ID_FROM_IP = 0;
	public static final int DDS_RTPS_AUTO_ID_FROM_MAC = 1;

	
	public class DDS_RtpsWellKnownPorts_t extends Structure{
	    public int port_base = 7400;
	    public int domain_id_gain = 250;
	    public int participant_id_gain = 2;
	    public int builtin_multicast_port_offset = 0;
	    public int builtin_unicast_port_offset = 10;
	    public int user_multicast_port_offset = 1;
	    public int user_unicast_port_offset = 11;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"port_base","domain_id_gain","participant_id_gain","builtin_multicast_port_offset","builtin_unicast_port_offset","user_multicast_port_offset","user_unicast_port_offset"});
		}
	    
	    public DDS_RtpsWellKnownPorts_t() {
	    	super();
	    	write();
	    }
	}
	
	public static final int DDS_RTPS_RESERVED_PORT_BUILTIN_UNICAST = 0x0001 << 0;
	public static final int DDS_RTPS_RESERVED_PORT_BUILTIN_MULTICAST = 0x0001 << 1;
	public static final int DDS_RTPS_RESERVED_PORT_USER_UNICAST = 0x0001 << 2;
	public static final int DDS_RTPS_RESERVED_PORT_USER_MULTICAST = 0x0001 << 3;


	public static final int DDS_RTPS_RESERVED_PORT_MASK_DEFAULT  = DDS_RTPS_RESERVED_PORT_BUILTIN_UNICAST
	    | DDS_RTPS_RESERVED_PORT_BUILTIN_MULTICAST | DDS_RTPS_RESERVED_PORT_USER_UNICAST;

	
	public class DDS_WireProtocolQosPolicy extends Structure {
	    public int participant_id = -1;
	    public int rtps_host_id = DDS_RTPS_AUTO_ID;
	    public int  rtps_app_id = DDS_RTPS_AUTO_ID;
	    public int  rtps_instance_id = DDS_RTPS_AUTO_ID;
	    public DDS_RtpsWellKnownPorts_t rtps_well_known_ports = new DDS_RtpsWellKnownPorts_t();
	    public int rtps_reserved_port_mask = DDS_RTPS_RESERVED_PORT_MASK_DEFAULT;
	    public int rtps_auto_id_kind = DDS_RTPS_AUTO_ID_FROM_IP;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"participant_id","rtps_host_id","rtps_app_id","rtps_instance_id","rtps_well_known_ports","rtps_reserved_port_mask","rtps_auto_id_kind"});
		}
	    
	    public DDS_WireProtocolQosPolicy() {
	    	super();
	    	write();
	    }
	}

   public static final int DDS_TRANSPORTBUILTIN_UDPv4 = 0x0001 << 0;
   public static final int DDS_TRANSPORTBUILTIN_SHMEM = 0x0001 << 1;
   public static final int DDS_TRANSPORTBUILTIN_INTRA = 0x0001 << 2;
   public static final int DDS_TRANSPORTBUILTIN_UDPv6 = 0x0001 << 3;
	
   public static final int DDS_TRANSPORTBUILTIN_MASK_NONE = 0;


   public static final int DDS_TRANSPORTBUILTIN_MASK_DEFAULT = DDS_TRANSPORTBUILTIN_UDPv4 | DDS_TRANSPORTBUILTIN_SHMEM;
   public static final int DDS_TRANSPORTBUILTIN_MASK_ALL = ~DDS_TRANSPORTBUILTIN_MASK_NONE;
    
   
   public class DDS_TransportBuiltinQosPolicy extends Structure {
	    public int mask = DDS_TRANSPORTBUILTIN_MASK_DEFAULT;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"mask"});
		}
	    
	    public DDS_TransportBuiltinQosPolicy() {
	    	super();
	    	write();
		}
	};
	
	public class DDS_DiscoveryQosPolicy extends Structure {
	    public DDS_Sequence enabled_transports = new DDS_Sequence();
	    public DDS_Sequence initial_peers = new DDS_Sequence();
	    public DDS_Sequence multicast_receive_addresses = new DDS_Sequence();
	    public int metatraffic_transport_priority = 0;
	    public byte accept_unknown_peers = 1;

		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"enabled_transports","initial_peers","multicast_receive_addresses","metatraffic_transport_priority","accept_unknown_peers"});
		}
	    
	    public DDS_DiscoveryQosPolicy() {
	    	super();
	    	write();
	    }
	};
	
	public class DDS_AllocationSettings_t extends Structure {
	    public int initial_count;
	    public int max_count;
	    public int incremental_count;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"initial_count","max_count","incremental_count"});
		}
	    
	    public DDS_AllocationSettings_t() {
	    	super();
	    }
	    
	    public DDS_AllocationSettings_t(Pointer p) {
	    	super(p);
	    }
	    
	    public DDS_AllocationSettings_t(int initial_count, int max_count, int incremental_count) {
	    	super();
	    	this.initial_count = initial_count;
	    	this.max_count = max_count;
	    	this.incremental_count = incremental_count;
	    	write();
	    }
	};
	
	public class DDS_DomainParticipantResourceLimitsQosPolicy extends Structure {
	    public DDS_AllocationSettings_t local_writer_allocation = new DDS_AllocationSettings_t(16, -1, -1);
	    public DDS_AllocationSettings_t local_reader_allocation = new DDS_AllocationSettings_t(16, -1, -1);
	    public DDS_AllocationSettings_t local_publisher_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t local_subscriber_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t local_topic_allocation = new DDS_AllocationSettings_t(16, -1, -1);
	    public DDS_AllocationSettings_t remote_writer_allocation = new DDS_AllocationSettings_t(64, -1, -1);
	    public DDS_AllocationSettings_t remote_reader_allocation = new DDS_AllocationSettings_t(64, -1, -1);
	    public DDS_AllocationSettings_t remote_participant_allocation = new DDS_AllocationSettings_t(16, -1, -1);
	    public DDS_AllocationSettings_t matching_writer_reader_pair_allocation = new DDS_AllocationSettings_t(32, -1, -1);
	    public DDS_AllocationSettings_t matching_reader_writer_pair_allocation = new DDS_AllocationSettings_t(32, -1, -1);
	    public DDS_AllocationSettings_t ignored_entity_allocation = new DDS_AllocationSettings_t(8, -1, -1);
	    public DDS_AllocationSettings_t content_filtered_topic_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t content_filter_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t read_condition_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t query_condition_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public DDS_AllocationSettings_t outstanding_asynchronous_sample_allocation = new DDS_AllocationSettings_t(64, -1, -1);
	    public DDS_AllocationSettings_t flow_controller_allocation = new DDS_AllocationSettings_t(4, -1, -1);
	    public int local_writer_hash_buckets = 4;
	    public int local_reader_hash_buckets = 4;
	    public int local_publisher_hash_buckets = 1;
	    public int local_subscriber_hash_buckets = 1;
	    public int local_topic_hash_buckets = 4;
	    public int remote_writer_hash_buckets = 16;
	    public int remote_reader_hash_buckets = 16;
	    public int remote_participant_hash_buckets = 4;
	    public int matching_writer_reader_pair_hash_buckets = 32;
	    public int matching_reader_writer_pair_hash_buckets= 32;
	    public int ignored_entity_hash_buckets = 1;
	    public int content_filtered_topic_hash_buckets = 1;
	    public int content_filter_hash_buckets = 1;
	    public int flow_controller_hash_buckets = 1;
	    public int max_gather_destinations= 8;
	    public int participant_user_data_max_length = 256;
	    public int inter_participant_data_max_length = 256;;
	    public int topic_data_max_length = 256;
	    public int publisher_group_data_max_length = 256;
	    public int subscriber_group_data_max_length = 256;
	    public int writer_user_data_max_length = 256;
	    public int reader_user_data_max_length = 256;
	    public int max_partitions = 64;
	    public int max_partition_cumulative_characters = 256;
	    public byte default_partition_matches_all = 0;
	    public byte allow_no_partitions = 0;
	    public int type_code_max_serialized_length = 2048;
	    public int type_object_max_serialized_length = 3072;
	    public int serialized_type_object_dynamic_allocation_threshold = 3072;
	    public int type_object_max_deserialized_length = -1;
	    public int deserialized_type_object_dynamic_allocation_threshold = 4096;
	    public int contentfilter_property_max_length = 256;
	    public int channel_seq_max_length = 32;
	    public int channel_filter_expression_max_length = 256;
	    public int participant_property_list_max_length = 32;
	    public int participant_property_string_max_length = 1024;
	    public int writer_property_list_max_length = 32;
	    public int writer_property_string_max_length = 1024;
	    public int reader_property_list_max_length = 32;
	    public int reader_property_string_max_length = 1024;
	    public int plugin_info_parameter_max_length = 256;
	    public int max_endpoint_groups = 32;
	    public int max_endpoint_group_cumulative_characters = 1024;
	    
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"local_writer_allocation","local_reader_allocation","local_publisher_allocation",
					"local_subscriber_allocation", "local_topic_allocation","remote_writer_allocation","remote_reader_allocation",
					"remote_participant_allocation","matching_writer_reader_pair_allocation", "matching_reader_writer_pair_allocation",
					"ignored_entity_allocation","content_filtered_topic_allocation","content_filter_allocation","read_condition_allocation",
					"query_condition_allocation","outstanding_asynchronous_sample_allocation","flow_controller_allocation",
					"local_writer_hash_buckets","local_reader_hash_buckets", "local_publisher_hash_buckets","local_subscriber_hash_buckets",
					"local_topic_hash_buckets","remote_writer_hash_buckets","remote_reader_hash_buckets","remote_participant_hash_buckets",
					"matching_writer_reader_pair_hash_buckets","matching_reader_writer_pair_hash_buckets","ignored_entity_hash_buckets",
					"content_filtered_topic_hash_buckets","content_filter_hash_buckets","flow_controller_hash_buckets","max_gather_destinations",
					"participant_user_data_max_length","inter_participant_data_max_length","topic_data_max_length","publisher_group_data_max_length",
					"subscriber_group_data_max_length","writer_user_data_max_length","reader_user_data_max_length","max_partitions",
					"max_partition_cumulative_characters","default_partition_matches_all","allow_no_partitions","type_code_max_serialized_length",
					"type_object_max_serialized_length","serialized_type_object_dynamic_allocation_threshold",
					"type_object_max_deserialized_length", "deserialized_type_object_dynamic_allocation_threshold",
					"contentfilter_property_max_length","channel_seq_max_length","channel_filter_expression_max_length",
					"participant_property_list_max_length","participant_property_string_max_length","writer_property_list_max_length",
					"writer_property_string_max_length","reader_property_list_max_length","reader_property_string_max_length",
					"plugin_info_parameter_max_length","max_endpoint_groups","max_endpoint_group_cumulative_characters"});
		}
	    
	    public DDS_DomainParticipantResourceLimitsQosPolicy() {
	    	super();
	    	write();
	    }
	}
	
	// TODO Defaults not mapped .. up against a deadline
	public class DDS_ThreadSettings_t extends Structure {
	    public int mask = 0; // 0xff676981;
	    public int priority = 16; // 0xffffffff;
	    public int stack_size = -1;
	    public DDS_Sequence cpu_list = new DDS_Sequence();
	    public int cpu_rotation = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"mask","priority","stack_size","cpu_list","cpu_rotation"});
		}
	    public DDS_ThreadSettings_t() {
	    	super();
	    	write();
	    }
	};

	public class DDS_EventQosPolicy extends Structure {
	    public DDS_ThreadSettings_t thread = new DDS_ThreadSettings_t();
	    public int initial_count = 256;
	    public int max_count = -1;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"thread","initial_count","max_count"});
		}
	    public DDS_EventQosPolicy() {
	    	super();
	    	write();
		}
	};

	public class DDS_ReceiverPoolQosPolicy extends Structure {
	    public DDS_ThreadSettings_t thread = new DDS_ThreadSettings_t();
	    public int initial_receive_threads = 4;
	    public int max_receive_threads = -1;
	    public int buffer_size = 9216;
	    public int buffer_alignment = 16;
	    public byte is_timestamp_enabled = 1;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"thread","initial_receive_threads","max_receive_threads","buffer_size","buffer_alignment","is_timestamp_enabled"});
		}
	    
	    public DDS_ReceiverPoolQosPolicy() {
	    	super();
	    	write();
	    }

	};
	

	public class DDS_DatabaseQosPolicy extends Structure {
	    public DDS_ThreadSettings_t thread = new DDS_ThreadSettings_t();
	    public DDS_Duration_t shutdown_timeout = new DDS_Duration_t(15, 0);
	    public DDS_Duration_t cleanup_period = new DDS_Duration_t(61, 0);
	    public DDS_Duration_t shutdown_cleanup_period = new DDS_Duration_t(1,0);
	    public int initial_records = 1024;
	    public int max_skiplist_level = 7;
	    public int table_allocation_block_size = 48;
	    public int max_weak_references = -1;
	    public int initial_weak_references = 2049;
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"thread","shutdown_timeout","cleanup_period","shutdown_cleanup_period","initial_records","max_skiplist_level","table_allocation_block_size","max_weak_references","initial_weak_references"});
		}
	    
	    public DDS_DatabaseQosPolicy() {
	    	super();
	    	write();
		}

	};
    public static final int DDS_DISCOVERYCONFIG_BUILTIN_SPDP = 0x0001 << 0;
    public static final int DDS_DISCOVERYCONFIG_BUILTIN_SEDP = 0x0001 << 1;
    public static final int DDS_DISCOVERYCONFIG_BUILTIN_SDP= DDS_DISCOVERYCONFIG_BUILTIN_SPDP |
                                      DDS_DISCOVERYCONFIG_BUILTIN_SEDP;
    public static final int DDS_DISCOVERYCONFIG_BUILTIN_EDS = 0x0001 << 2;

    public class DDS_BuiltinTopicReaderResourceLimits_t extends Structure {
        public int initial_samples = 64;
        public int max_samples = -1;
        public int initial_infos = 64;
        public int max_infos = -1;
        public int initial_outstanding_reads = 2;
        public int max_outstanding_reads = -1;
        public int max_samples_per_read = 1024;
        public byte disable_fragmentation_support = 0;
        public int max_fragmented_samples = 1024;
        public int initial_fragmented_samples = 4;
        public int max_fragmented_samples_per_remote_writer = 256;
        public int max_fragments_per_sample = 512;
        public byte dynamically_allocate_fragmented_samples = 1;
        
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"initial_samples","max_samples","initial_infos","max_infos","initial_outstanding_reads","max_outstanding_reads","max_samples_per_read",
			        "disable_fragmentation_support","max_fragmented_samples","initial_fragmented_samples","max_fragmented_samples_per_remote_writer","max_fragments_per_sample","dynamically_allocate_fragmented_samples"});
		}
        
        public DDS_BuiltinTopicReaderResourceLimits_t() {
        	super();
        	write();
		}
    };
    
    public class DDS_DiscoveryBuiltinReaderFragmentationResourceLimits_t extends Structure {
        public byte disable_fragmentation_support = 0;
        public int max_fragmented_samples = 1024;
        public int initial_fragmented_samples = 4;
        public int max_fragmented_samples_per_remote_writer = 256;
        public int max_fragments_per_sample = 512;
        public byte dynamically_allocate_fragmented_samples = 0;
        
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"disable_fragmentation_support","max_fragmented_samples","initial_fragmented_samples",
					"max_fragmented_samples_per_remote_writer","max_fragments_per_sample","dynamically_allocate_fragmented_samples"});
		}
        
        public DDS_DiscoveryBuiltinReaderFragmentationResourceLimits_t() {
        	super();
        	write();
		}
    };

    
    public class DDS_DiscoveryConfigQosPolicy extends Structure {
        public DDS_Duration_t participant_liveliness_lease_duration = new DDS_Duration_t(100, 0);
        public DDS_Duration_t participant_liveliness_assert_period = new DDS_Duration_t(30, 0);
        public int remote_participant_purge_kind;
        public DDS_Duration_t max_liveliness_loss_detection_period = new DDS_Duration_t(60, 0);
        public int initial_participant_announcements = 5;
        public DDS_Duration_t min_initial_participant_announcement_period = new DDS_Duration_t(1, 0);
        public DDS_Duration_t max_initial_participant_announcement_period = new DDS_Duration_t(1,0);
        public DDS_BuiltinTopicReaderResourceLimits_t participant_reader_resource_limits = new DDS_BuiltinTopicReaderResourceLimits_t();
        public DDS_RtpsReliableReaderProtocol_t publication_reader = new DDS_RtpsReliableReaderProtocol_t();
        public DDS_BuiltinTopicReaderResourceLimits_t publication_reader_resource_limits = new DDS_BuiltinTopicReaderResourceLimits_t();
        public DDS_RtpsReliableReaderProtocol_t subscription_reader = new DDS_RtpsReliableReaderProtocol_t();
        public DDS_BuiltinTopicReaderResourceLimits_t subscription_reader_resource_limits = new DDS_BuiltinTopicReaderResourceLimits_t();
        public DDS_RtpsReliableWriterProtocol_t publication_writer = new DDS_RtpsReliableWriterProtocol_t();
        public DDS_WriterDataLifecycleQosPolicy publication_writer_data_lifecycle = new DDS_WriterDataLifecycleQosPolicy();
        public DDS_RtpsReliableWriterProtocol_t subscription_writer = new DDS_RtpsReliableWriterProtocol_t();
        public DDS_WriterDataLifecycleQosPolicy subscription_writer_data_lifecycle = new DDS_WriterDataLifecycleQosPolicy();
        public int endpoint_plugin_redundancy_level;
        public int builtin_discovery_plugins;
        public DDS_RtpsReliableReaderProtocol_t participant_message_reader = new DDS_RtpsReliableReaderProtocol_t();
        public DDS_RtpsReliableWriterProtocol_t participant_message_writer = new DDS_RtpsReliableWriterProtocol_t();
        public DDS_PublishModeQosPolicy publication_writer_publish_mode = new DDS_PublishModeQosPolicy();
        public DDS_PublishModeQosPolicy subscription_writer_publish_mode = new DDS_PublishModeQosPolicy();
        public DDS_AsynchronousPublisherQosPolicy asynchronous_publisher = new DDS_AsynchronousPublisherQosPolicy();
        public byte sedp_rely_on_spdp_only;
        public DDS_LatencyBudgetQosPolicy publication_writer_latency_budget = new DDS_LatencyBudgetQosPolicy();
        public byte publication_writer_push_on_write;
        public DDS_LatencyBudgetQosPolicy subscription_writer_latency_budget = new DDS_LatencyBudgetQosPolicy();
        public byte subscription_writer_push_on_write;
        public DDS_RtpsReliableWriterProtocol_t participant_state_writer = new DDS_RtpsReliableWriterProtocol_t();
        public DDS_LatencyBudgetQosPolicy participant_state_writer_latency_budget = new DDS_LatencyBudgetQosPolicy();
        public byte participant_state_writer_push_on_write;
        public DDS_PublishModeQosPolicy participant_state_writer_publish_mode = new DDS_PublishModeQosPolicy();
        public DDS_RtpsReliableReaderProtocol_t participant_proxy_reader = new DDS_RtpsReliableReaderProtocol_t();
        public DDS_DiscoveryBuiltinReaderFragmentationResourceLimits_t
            participant_proxy_reader_fragmentation_resource_limits = new DDS_DiscoveryBuiltinReaderFragmentationResourceLimits_t();
        public int plugin_promiscuity_kind;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"participant_liveliness_lease_duration","participant_liveliness_assert_period","remote_participant_purge_kind",
					"max_liveliness_loss_detection_period","initial_participant_announcements","min_initial_participant_announcement_period",
					"max_initial_participant_announcement_period","participant_reader_resource_limits","publication_reader","publication_reader_resource_limits",
					"subscription_reader","subscription_reader_resource_limits","publication_writer","publication_writer_data_lifecycle",
					"subscription_writer","subscription_writer_data_lifecycle",
					"endpoint_plugin_redundancy_level","builtin_discovery_plugins",
					"participant_message_reader","participant_message_writer",
					"publication_writer_publish_mode","subscription_writer_publish_mode","asynchronous_publisher",
					"sedp_rely_on_spdp_only",
					"publication_writer_latency_budget","publication_writer_push_on_write",
					"subscription_writer_latency_budget","subscription_writer_push_on_write",
					"participant_state_writer","participant_state_writer_latency_budget","participant_state_writer_push_on_write",
					"participant_state_writer_publish_mode","participant_proxy_reader","participant_proxy_reader_fragmentation_resource_limits",
					"plugin_promiscuity_kind"
			});
		}
        
        public DDS_DiscoveryConfigQosPolicy() {
        	super();
        	write();
		}
    }
    public class DDS_UserObjectSettings_t extends Structure {
        public DDS_UserObjectSettings_t() {
            super();
            write();
        }
        public DDS_UserObjectSettings_t(Pointer p) {
            super(p);
        }
        public int size;
        public int alignment;
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("size", "alignment");
        }
    }
	public class DDS_UserObjectQosPolicy extends Structure {
	    public DDS_UserObjectQosPolicy() {
	        super();
	        write();
        }
	    public DDS_UserObjectQosPolicy(Pointer p) {
            super(p);
        }
	    public DDS_UserObjectSettings_t participant_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t topic_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t content_filtered_topic_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t publisher_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t data_writer_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t subscriber_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t data_reader_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t read_condition_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t query_condition_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t index_condition_user_object = new DDS_UserObjectSettings_t();
	    public DDS_UserObjectSettings_t flow_controller_user_object = new DDS_UserObjectSettings_t();
	    
	    @Override
	    protected List<?> getFieldOrder() {
    	      return Arrays.asList("participant_user_object","topic_user_object","content_filtered_topic_user_object",
    	              "publisher_user_object","data_writer_user_object","subscriber_user_object","data_reader_user_object",
    	              "read_condition_user_object","query_condition_user_object","index_condition_user_object","flow_controller_user_object");
	    }
	}
    public class DDS_DomainParticipantProtocolQosPolicy extends Structure {
        public DDS_DomainParticipantProtocolQosPolicy() {
            super();
            write();
        }
        public DDS_DomainParticipantProtocolQosPolicy(Pointer p) {
            super(p);
        }
        public byte vendor_specific_entity;
        @Override
        protected List<?> getFieldOrder() {
            return Arrays.asList("vendor_specific_entity");
        }
    }
    
	public class DDS_DomainParticipantQos extends Structure {
		public DDS_UserDataQosPolicy user_data = new DDS_UserDataQosPolicy();
		public DDS_EntityFactoryQosPolicy entity_factory = new DDS_EntityFactoryQosPolicy();
		public DDS_WireProtocolQosPolicy wire_protocol = new DDS_WireProtocolQosPolicy();
		public DDS_TransportBuiltinQosPolicy transport_builtin = new DDS_TransportBuiltinQosPolicy();
		public DDS_TransportUnicastQosPolicy default_unicast = new DDS_TransportUnicastQosPolicy();
		public DDS_DiscoveryQosPolicy discovery = new DDS_DiscoveryQosPolicy();
		public DDS_DomainParticipantResourceLimitsQosPolicy resource_limits = new DDS_DomainParticipantResourceLimitsQosPolicy();
		public DDS_EventQosPolicy event = new DDS_EventQosPolicy();
		public DDS_ReceiverPoolQosPolicy receiver_pool = new DDS_ReceiverPoolQosPolicy();
		public DDS_DatabaseQosPolicy database = new DDS_DatabaseQosPolicy();
		public DDS_DiscoveryConfigQosPolicy discovery_config = new DDS_DiscoveryConfigQosPolicy();
		public DDS_ExclusiveAreaQosPolicy exclusive_area = new DDS_ExclusiveAreaQosPolicy();
		public DDS_PropertyQosPolicy property = new DDS_PropertyQosPolicy();
		public DDS_EntityNameQosPolicy participant_name = new DDS_EntityNameQosPolicy();
		public DDS_TransportMulticastMappingQosPolicy multicast_mapping = new DDS_TransportMulticastMappingQosPolicy();
		public DDS_UserObjectQosPolicy user_object = new DDS_UserObjectQosPolicy();
		public DDS_DomainParticipantProtocolQosPolicy protocol = new DDS_DomainParticipantProtocolQosPolicy();
		public DDS_TypeSupportQosPolicy type_support = new DDS_TypeSupportQosPolicy();
		
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"user_data","entity_factory","wire_protocol","transport_builtin","default_unicast","discovery","resource_limits",
					"event","receiver_pool","database","discovery_config","exclusive_area", "property","participant_name","multicast_mapping","user_object", "protocol", "type_support"});
		}
		
		public DDS_DomainParticipantQos() {
			super();
			write();
		}
		
	}
	
    public static final int DDS_INSTANCE_PRESENTATION_QOS = 0;
    public static final int DDS_TOPIC_PRESENTATION_QOS = 1;
    public static final int DDS_GROUP_PRESENTATION_QOS = 2;
    public static final int DDS_HIGHEST_OFFERED_PRESENTATION_QOS = 3;
    
    public class DDS_PresentationQosPolicy extends Structure {
        public int access_scope = DDS_INSTANCE_PRESENTATION_QOS;
        public byte coherent_access = 0;
        public byte ordered_access = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"access_scope","coherent_access","ordered_access"});
		}
        public DDS_PresentationQosPolicy() {
        	super();
        	write();
        }
    };

    public class DDS_PartitionQosPolicy extends Structure {
    	public DDS_StringSequence name = new DDS_StringSequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"name"});
		}
    	public DDS_PartitionQosPolicy() {
    		super();
    		write();
		}
    }
    
    public class DDS_GroupDataQosPolicy extends Structure {
    	public DDS_Sequence value = new DDS_Sequence();
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"value"});
		}
    	public DDS_GroupDataQosPolicy() {
    		super();
    		write();
		}
    }
	
   public static final int DDS_SEMAPHORE_BLOCKING_KIND = 0;
   public static final int DDS_SPIN_BLOCKING_KIND = 1;
    
    public class DDS_AsynchronousPublisherQosPolicy extends Structure {
         public byte disable_asynchronous_write = 0;
         
         public DDS_ThreadSettings_t thread = new DDS_ThreadSettings_t();
         public byte disable_asynchronous_batch = 0;
         public DDS_ThreadSettings_t asynchronous_batch_thread = new DDS_ThreadSettings_t();
         public int asynchronous_batch_blocking_kind = DDS_SEMAPHORE_BLOCKING_KIND;
         
 		@Override
 		protected List<?> getFieldOrder() {
 			return Arrays.asList(new String[] {"disable_asynchronous_write","thread","disable_asynchronous_batch","asynchronous_batch_thread","asynchronous_batch_blocking_kind"});
 		}
         
         public DDS_AsynchronousPublisherQosPolicy() {
        	 super();
        	 write();
		}
    };
    
    public class DDS_ExclusiveAreaQosPolicy extends Structure {
    	public byte use_shared_exclusive_area = 0;
    	public int level = -1;
    	
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"use_shared_exclusive_area","level"});
		}
    	
    	public DDS_ExclusiveAreaQosPolicy() {
    		super();
    		write();
		}
    };

    public class DDS_PublisherProtocolQosPolicy extends Structure {
        public byte vendor_specific_entity = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"vendor_specific_entity"});
		}
        public DDS_PublisherProtocolQosPolicy() {
        	super();
        	write();
		}
    };
    
    public class DDS_SubscriberProtocolQosPolicy extends Structure {
        public byte vendor_specific_entity = 0;
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"vendor_specific_entity"});
		}
        public DDS_SubscriberProtocolQosPolicy() {
        	super();
        	write();
		}
    };

    
	public int DDS_DomainParticipantQos_initialize(Pointer self);
	
	public class DDS_PublisherQos extends Structure {
		public DDS_PresentationQosPolicy presentation = new DDS_PresentationQosPolicy();
	    public DDS_PartitionQosPolicy partition = new DDS_PartitionQosPolicy();
	    public DDS_GroupDataQosPolicy group_data = new DDS_GroupDataQosPolicy();
	    public DDS_EntityFactoryQosPolicy entity_factory = new DDS_EntityFactoryQosPolicy();
	    public DDS_AsynchronousPublisherQosPolicy asynchronous_publisher = new DDS_AsynchronousPublisherQosPolicy();
	    public DDS_ExclusiveAreaQosPolicy exclusive_area = new DDS_ExclusiveAreaQosPolicy();
	    public DDS_PublisherProtocolQosPolicy protocol = new DDS_PublisherProtocolQosPolicy();
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"presentation","partition","group_data","entity_factory","asynchronous_publisher","exclusive_area","protocol"});
		}
	    
	    public DDS_PublisherQos() {
	    	super();
	    	
	    	write();
		}
	};
	
	public int DDS_PublisherQos_initialize(Pointer self);
	
	public class DDS_SubscriberQos extends Structure {
	    public DDS_PresentationQosPolicy presentation = new DDS_PresentationQosPolicy();
	    public DDS_PartitionQosPolicy partition = new DDS_PartitionQosPolicy();
	    public DDS_GroupDataQosPolicy group_data = new DDS_GroupDataQosPolicy();
	    public DDS_EntityFactoryQosPolicy entity_factory = new DDS_EntityFactoryQosPolicy();
	    public DDS_ExclusiveAreaQosPolicy exclusive_area = new DDS_ExclusiveAreaQosPolicy();
	    public DDS_SubscriberProtocolQosPolicy protocol = new DDS_SubscriberProtocolQosPolicy();
	    
		@Override
		protected List<?> getFieldOrder() {
			return Arrays.asList(new String[] {"presentation","partition","group_data","entity_factory","exclusive_area","protocol"});
		}
	    
	    public DDS_SubscriberQos() {
	    	super();
	    	
	    	write();
	    }
	}
	
	public int DDS_SubscriberQos_initialize(Pointer self);
}
