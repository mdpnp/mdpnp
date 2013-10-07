/**
 * @file    DDSImpl.java
 *
 * @brief   Defines a class which handles and manages all DDS entities.
 *
 * @author  M Szwaja
 */
//=============================================================================

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.publication.Publisher;

import ice.DeviceIdentity;
import ice.DeviceIdentityTopic;
import ice.DeviceIdentityTypeSupport;
import ice.DeviceIdentityDataWriter;
import ice.DeviceIdentityDataReader;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityTopic;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceConnectivityDataWriter;
import ice.DeviceConnectivityDataReader;

import ice.DeviceConnectivityObjective;
import ice.DeviceConnectivityObjectiveTopic;
import ice.DeviceConnectivityObjectiveTypeSupport;
import ice.DeviceConnectivityObjectiveDataWriter;
import ice.DeviceConnectivityObjectiveDataReader;

import ice.Numeric;
import ice.NumericTopic;
import ice.NumericTypeSupport;
import ice.NumericDataWriter;
import ice.NumericDataReader;

import ice.PatientDemographics;
import ice.PatientDemographicsTopic;
import ice.PatientDemographicsTypeSupport;
import ice.PatientDemographicsDataWriter;
import ice.PatientDemographicsDataReader;

import ice.Text;
import ice.TextTopic;
import ice.TextTypeSupport;
import ice.TextDataWriter;
import ice.TextDataReader;


/**
 * Handles and manages all DDS entities.
 */
public class DDSImpl
{
  private String _statusmsg;
  
  private DomainParticipant _participant;

  // DDS Topics
  private Topic _dev_id_topic;
  private Topic _dev_conn_topic;
  private Topic _dev_conn_obj_topic;
  private Topic _numeric_topic;
  private Topic _pat_demog_topic;
  private Topic _text_topic;

  // DDS DataWriters
  private DeviceIdentityDataWriter _dev_id_writer;
  private DeviceConnectivityDataWriter _dev_conn_writer;
  private DeviceConnectivityObjectiveDataWriter _dev_conn_obj_writer;
  private NumericDataWriter _numeric_writer;
  private PatientDemographicsDataWriter _pat_demog_writer;
  private TextDataWriter _text_writer;

  // DDS DataReaders
  private DeviceIdentityDataReader _dev_id_reader;
  private DeviceConnectivityDataReader _dev_conn_reader;
  private DeviceConnectivityObjectiveDataReader _dev_conn_obj_reader;
  private NumericDataReader _numeric_reader;
  private PatientDemographicsDataReader _pat_demog_reader;
  private TextDataReader _text_reader;


  public DDSImpl()
  {
    _participant = null;

    _dev_id_topic = null;
    _dev_conn_topic = null;
    _dev_conn_obj_topic = null;
    _numeric_topic = null;
    _pat_demog_topic = null;
    _text_topic = null;

    _dev_id_writer = null;
    _dev_conn_writer = null;
    _dev_conn_obj_writer = null;
    _numeric_writer = null;
    _pat_demog_writer = null;
    _text_writer = null;
    
    _dev_id_reader = null;
    _dev_conn_reader = null;
    _dev_conn_obj_reader = null;
    _numeric_reader = null;
    _pat_demog_reader = null;
    _text_reader = null;
  }


  public final String get_statusmsg()
  {
    return _statusmsg;
  }


  public final int CreateDomainParticipant(final int domainId)
  {
    if (_participant != null)
    {
      _statusmsg = "DDSImpl::CreateDomainParticipant() Participant "
        + "already exists";
      return 1;
    }

    System.out.println("Creating Domain Participant on domain " + domainId);
    _participant = 
      DomainParticipantFactory.get_instance().create_participant(
        domainId,
        DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);

    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDomainParticipant() Unable to create "
        + "participant";
      return 1;
    }

    return 0;
  }


  public final int ParticipantShutdown()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::ParticipantShutdown() Participant is null";
      return 1;
    }

    _participant.delete_contained_entities();

    DomainParticipantFactory.TheParticipantFactory.
      delete_participant(_participant);

    DomainParticipantFactory.finalize_instance();

    _participant = null;

    _dev_id_topic = null;
    _dev_conn_topic = null;
    _dev_conn_obj_topic = null;
    _numeric_topic = null;
    _pat_demog_topic = null;
    _text_topic = null;

    _dev_id_writer = null;
    _dev_conn_writer = null;
    _dev_conn_obj_writer = null;
    _numeric_writer = null;
    _pat_demog_writer = null;
    _text_writer = null;

    _dev_id_reader = null;
    _dev_conn_reader = null;
    _dev_conn_obj_reader = null;
    _numeric_reader = null;
    _pat_demog_reader = null;
    _text_reader = null;

    return 0;
  }


  private int CreateDeviceIdentityTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityTopic() Participant is null";
      return 1;
    }

    if (_dev_id_topic != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityTopic() Topic already exists";
      return 1;
    }

    String type_name = DeviceIdentityTypeSupport.get_type_name();
    DeviceIdentityTypeSupport.register_type(_participant, type_name);

    _dev_id_topic = _participant.create_topic(
      DeviceIdentityTopic.VALUE,
      DeviceIdentityTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_dev_id_topic == null)
    {
      System.err.println("DDSImpl::CreateDeviceIdentityTopic() Unable to "
        + "create topic " + DeviceIdentityTopic.VALUE);
      return 1;
    }           

    return 0;
  }


  private int CreateDeviceConnectivityTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityTopic() Participant is "
        + "null";
      return 1;
    }

    if (_dev_conn_topic != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityTopic() Topic already "
        + "exists";
      return 1;
    }

    String type_name = DeviceConnectivityTypeSupport.get_type_name();
    DeviceConnectivityTypeSupport.register_type(_participant, type_name);

    _dev_conn_topic = _participant.create_topic(
      DeviceConnectivityTopic.VALUE,
      DeviceConnectivityTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_dev_conn_topic == null)
    {
      System.err.println("DDSImpl::CreateDeviceConnectivityTopic() Unable to "
        + "create topic " + DeviceConnectivityTopic.VALUE);
      return 1;
    }

    return 0;
  }


  private int CreateDeviceConnectivityObjectiveTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveTopic() "
        + "Participant is null";
      return 1;
    }

    if (_dev_conn_obj_topic != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveTopic() Topic "
        + "already exists";
      return 1;
    }

    String type_name = DeviceConnectivityObjectiveTypeSupport.get_type_name();
    DeviceConnectivityObjectiveTypeSupport.register_type(_participant,
      type_name);

    _dev_conn_obj_topic = _participant.create_topic(
      DeviceConnectivityObjectiveTopic.VALUE,
      DeviceConnectivityObjectiveTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_dev_conn_obj_topic == null)
    {
      System.err.println("DDSImpl::CreateDeviceConnectivityObjectiveTopic() "
        + "Unable to create topic " + DeviceConnectivityObjectiveTopic.VALUE);
      return 1;
    }

    return 0;
  }


  private int CreateNumericTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateNumericTopic() Participant is "
        + "null";
      return 1;
    }

    if (_numeric_topic != null)
    {
      _statusmsg = "DDSImpl::CreateNumericTopic() Topic already "
        + "exists";
      return 1;
    }

    String type_name = NumericTypeSupport.get_type_name();
    NumericTypeSupport.register_type(_participant, type_name);

    _numeric_topic = _participant.create_topic(
      NumericTopic.VALUE,
      NumericTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_numeric_topic == null)
    {
      System.err.println("DDSImpl::CreateNumericTopic() Unable to "
        + "create topic " + NumericTopic.VALUE);
      return 1;
    }

    return 0;
  }


  private int CreatePatientDemographicsTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsTopic() Participant is "
        + "null";
      return 1;
    }

    if (_pat_demog_topic != null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsTopic() Topic already "
        + "exists";
      return 1;
    }

    String type_name = PatientDemographicsTypeSupport.get_type_name();
    PatientDemographicsTypeSupport.register_type(_participant, type_name);

    _pat_demog_topic = _participant.create_topic(
      PatientDemographicsTopic.VALUE,
      PatientDemographicsTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_pat_demog_topic == null)
    {
      System.err.println("DDSImpl::CreatePatientDemographicsTopic() Unable to "
        + "create topic " + PatientDemographicsTopic.VALUE);
      return 1;
    }

    return 0;
  }


  private int CreateTextTopic()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateTextTopic() Participant is "
        + "null";
      return 1;
    }

    if (_text_topic != null)
    {
      _statusmsg = "DDSImpl::CreateTextTopic() Topic already "
        + "exists";
      return 1;
    }

    String type_name = TextTypeSupport.get_type_name();
    TextTypeSupport.register_type(_participant, type_name);

    _text_topic = _participant.create_topic(
      TextTopic.VALUE,
      TextTypeSupport.get_type_name(), 
      DomainParticipant.TOPIC_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_text_topic == null)
    {
      System.err.println("DDSImpl::CreateTextTopic() Unable to "
        + "create topic " + TextTopic.VALUE);
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceIdentityWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityWriter() Participant is null";
      return 1;
    }

    if (_dev_id_topic == null)
    {
      if (CreateDeviceIdentityTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceIdentityWriter() Unable to create "
          + "topic";
        return 1;
      }
    }

    if (_dev_id_writer != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityWriter() DataWriter already "
        + "exists";
      return 1;
    }

    _dev_id_writer = (DeviceIdentityDataWriter) _participant.
      create_datawriter(
        _dev_id_topic,
        Publisher.DATAWRITER_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);
    if (_dev_id_writer == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityWriter() Unable to create DDS "
        + "data writer";
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceConnectivityWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityWriter() Participant "
        + "is null";
      return 1;
    }

    if (_dev_conn_topic == null)
    {
      if (CreateDeviceConnectivityTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceConnectivityWriter() Unable to "
          + "create topic";
        return 1;
      }
    }

    if (_dev_conn_writer != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityWriter() DataWriter "
        + "already exists";
      return 1;
    }

    _dev_conn_writer = (DeviceConnectivityDataWriter) _participant.
      create_datawriter(
        _dev_conn_topic,
        Publisher.DATAWRITER_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);
    if (_dev_conn_writer == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityWriter() Unable to "
        + "create DDS data writer";
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceConnectivityObjectiveWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveWriter() "
        + "Participant is null";
      return 1;
    }

    if (_dev_conn_obj_topic == null)
    {
      if (CreateDeviceConnectivityObjectiveTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveWriter() "
          + "Unable to create topic";
        return 1;
      }
    }

    if (_dev_conn_obj_writer != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveWriter() "
        + "DataWriter already exists";
      return 1;
    }

    _dev_conn_obj_writer = (DeviceConnectivityObjectiveDataWriter) _participant.
      create_datawriter(
        _dev_conn_obj_topic,
        Publisher.DATAWRITER_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);
    if (_dev_conn_obj_writer == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveWriter() "
        + "Unable to create DDS data writer";
      return 1;
    }

    return 0;
  }


  public final int CreateNumericWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateNumericWriter() Participant is null";
      return 1;
    }

    if (_numeric_topic == null)
    {
      if (CreateNumericTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateNumericWriter() Unable to create "
          + "topic";
        return 1;
      }
    }

    if (_numeric_writer != null)
    {
      _statusmsg = "DDSImpl::CreateNumericWriter() DataWriter already "
        + "exists";
      return 1;
    }

    _numeric_writer = (NumericDataWriter) _participant.create_datawriter(
      _numeric_topic,
      Publisher.DATAWRITER_QOS_DEFAULT,
      null, // listener
      StatusKind.STATUS_MASK_NONE);
    if (_numeric_writer == null)
    {
      _statusmsg = "DDSImpl::CreateNumericWriter() Unable to create DDS "
        + "data writer";
      return 1;
    }

    return 0;
  }


  public final int CreatePatientDemographicsWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsWriter() Participant "
        + "is null";
      return 1;
    }

    if (_pat_demog_topic == null)
    {
      if (CreatePatientDemographicsTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreatePatientDemographicsWriter() Unable to "
          + "create topic";
        return 1;
      }
    }

    if (_pat_demog_writer != null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsWriter() DataWriter "
        + "already exists";
      return 1;
    }

    _pat_demog_writer = (PatientDemographicsDataWriter) _participant.
      create_datawriter(
        _pat_demog_topic,
        Publisher.DATAWRITER_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);
    if (_pat_demog_writer == null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsWriter() Unable to "
        + "create DDS data writer";
      return 1;
    }

    return 0;
  }


  public final int CreateTextWriter()
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateTextWriter() Participant "
        + "is null";
      return 1;
    }

    if (_text_topic == null)
    {
      if (CreateTextTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateTextWriter() Unable to "
          + "create topic";
        return 1;
      }
    }

    if (_text_writer != null)
    {
      _statusmsg = "DDSImpl::CreateTextWriter() DataWriter "
        + "already exists";
      return 1;
    }

    _text_writer = (TextDataWriter) _participant.
      create_datawriter(
        _text_topic,
        Publisher.DATAWRITER_QOS_DEFAULT,
        null, // listener
        StatusKind.STATUS_MASK_NONE);
    if (_text_writer == null)
    {
      _statusmsg = "DDSImpl::CreateTextWriter() Unable to "
        + "create DDS data writer";
      return 1;
    }

    return 0;
  }


  public final int WriteDeviceIdentity(final DeviceIdentity dev_id)
  {
    if (_dev_id_writer == null)
    {
      _statusmsg = "DDSImpl::WriteDeviceIdentity() data writer is null";
      return 1;
    }

    try
    {
      _dev_id_writer.write(dev_id, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WriteDeviceIdentity() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }


  public final int WriteDeviceConnectivity(final DeviceConnectivity dev_conn)
  {
    if (_dev_conn_writer == null)
    {
      _statusmsg = "DDSImpl::WriteDeviceConnectivity() data writer is null";
      return 1;
    }

    try
    {
      _dev_conn_writer.write(dev_conn, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WriteDeviceConnectivity() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }


  public final int WriteDeviceConnectivityObjective(
    final DeviceConnectivityObjective dev_conn_obj)
  {
    if (_dev_conn_obj_writer == null)
    {
      _statusmsg = "DDSImpl::WriteDeviceConnectivityObjective() data writer "
        + "is null";
      return 1;
    }

    try
    {
      _dev_conn_obj_writer.write(dev_conn_obj, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WriteDeviceConnectivityObjective() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }
  
  
  public final int WriteNumeric(final Numeric numeric)
  {
    if (_numeric_writer == null)
    {
      _statusmsg = "DDSImpl::WriteNumeric() data writer is null";
      return 1;
    }

    try
    {
      _numeric_writer.write(numeric, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WriteNumeric() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }
  
  
  public final int WritePatientDemographics(final PatientDemographics pat_demog)
  {
    if (_pat_demog_writer == null)
    {
      _statusmsg = "DDSImpl::WritePatientDemographics() data writer is null";
      return 1;
    }

    try
    {
      _pat_demog_writer.write(pat_demog, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WritePatientDemographics() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }


  public final int WriteText(final Text txt)
  {
    if (_text_writer == null)
    {
      _statusmsg = "DDSImpl::WriteText() data writer is null";
      return 1;
    }

    try
    {
      _text_writer.write(txt, InstanceHandle_t.HANDLE_NIL);
    }
    catch (RETCODE_ERROR error)
    {
      _statusmsg = "DDSImpl::WriteText() Write error "
        + error.getClass().toString() + ": " + error.getMessage();
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceIdentityReader(
    final DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityReader() Participant is null";
      return 1;
    }

    if (_dev_id_topic == null)
    {
      if (CreateDeviceIdentityTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceIdentityReader() Unable to create "
          + "topic";
        return 1;
      }
    }

    if (_dev_id_reader != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityReader() DataReader already "
        + "exists";
      return 1;
    }

    _dev_id_reader = (DeviceIdentityDataReader) _participant.
      create_datareader(
        _dev_id_topic,
        Subscriber.DATAREADER_QOS_DEFAULT,
        listener, // listener
        StatusKind.STATUS_MASK_ALL);
    if (_dev_id_reader == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceIdentityReader() Unable to create DDS "
        + "DataReader";
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceConnectivityReader(
    final DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityReader() Participant "
        + "is null";
      return 1;
    }

    if (_dev_conn_topic == null)
    {
      if (CreateDeviceConnectivityTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceConnectivityReader() Unable to "
          + "create topic";
        return 1;
      }
    }

    if (_dev_conn_reader != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityReader() DataReader "
        + "already exists";
      return 1;
    }

    _dev_conn_reader = (DeviceConnectivityDataReader) _participant.
      create_datareader(
        _dev_conn_topic,
        Subscriber.DATAREADER_QOS_DEFAULT,
        listener,
        StatusKind.STATUS_MASK_ALL);
    if (_dev_conn_reader == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityReader() Unable to "
        + "create DDS DataReader";
      return 1;
    }

    return 0;
  }


  public final int CreateDeviceConnectivityObjectiveReader(
    final DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveReader() "
        + "Participant is null";
      return 1;
    }

    if (_dev_conn_obj_topic == null)
    {
      if (CreateDeviceConnectivityObjectiveTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveReader() "
          + "Unable to create topic";
        return 1;
      }
    }

    if (_dev_conn_obj_reader != null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveReader() "
        + "DataWriter already exists";
      return 1;
    }

    _dev_conn_obj_reader = (DeviceConnectivityObjectiveDataReader) _participant.
      create_datareader(
        _dev_conn_obj_topic,
        Subscriber.DATAREADER_QOS_DEFAULT,
        listener,
        StatusKind.STATUS_MASK_ALL);
    if (_dev_conn_obj_reader == null)
    {
      _statusmsg = "DDSImpl::CreateDeviceConnectivityObjectiveReader() "
        + "Unable to create DDS data writer";
      return 1;
    }

    return 0;
  }


  public final int CreateNumericReader(DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateNumericReader() Participant is null";
      return 1;
    }

    if (_numeric_topic == null)
    {
      if (CreateNumericTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateNumericReader() Unable to create "
          + "topic";
        return 1;
      }
    }

    if (_numeric_reader != null)
    {
      _statusmsg = "DDSImpl::CreateNumericReader() DataReader already "
        + "exists";
      return 1;
    }

    _numeric_reader = (NumericDataReader) _participant.create_datareader(
      _numeric_topic,
      Subscriber.DATAREADER_QOS_DEFAULT,
      listener,
      StatusKind.STATUS_MASK_ALL);
    if (_numeric_reader == null)
    {
      _statusmsg = "DDSImpl::CreateNumericReader() Unable to create DDS "
        + "DataReader";
      return 1;
    }

    return 0;
  }


  public final int CreatePatientDemographicsReader(
    final DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsReader() Participant "
        + "is null";
      return 1;
    }

    if (_pat_demog_topic == null)
    {
      if (CreatePatientDemographicsTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreatePatientDemographicsReader() Unable to "
          + "create topic";
        return 1;
      }
    }

    if (_pat_demog_reader != null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsReader() DataReader "
        + "already exists";
      return 1;
    }

    _pat_demog_reader = (PatientDemographicsDataReader) _participant.
      create_datareader(
        _pat_demog_topic,
        Subscriber.DATAREADER_QOS_DEFAULT,
        listener,
        StatusKind.STATUS_MASK_ALL);
    if (_pat_demog_reader == null)
    {
      _statusmsg = "DDSImpl::CreatePatientDemographicsReader() Unable to "
        + "create DDS DataReader";
      return 1;
    }

    return 0;
  }
  
  
  public final int CreateTextReader(final DataReaderListener listener)
  {
    if (_participant == null)
    {
      _statusmsg = "DDSImpl::CreateTextReader() Participant is null";
      return 1;
    }

    if (_text_topic == null)
    {
      if (CreateTextTopic() != 0)
      {
        _statusmsg = "DDSImpl::CreateTextReader() Unable to create "
          + "topic";
        return 1;
      }
    }

    if (_text_reader != null)
    {
      _statusmsg = "DDSImpl::CreateTextReader() DataReader already "
        + "exists";
      return 1;
    }

    _text_reader = (TextDataReader) _participant.create_datareader(
      _text_topic,
      Subscriber.DATAREADER_QOS_DEFAULT,
      listener,
      StatusKind.STATUS_MASK_ALL);
    if (_text_reader == null)
    {
      _statusmsg = "DDSImpl::CreateTextReader() Unable to create DDS "
        + "DataReader";
      return 1;
    }

    return 0;
  }
}
