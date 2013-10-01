/*
 * @file      rti_dds_impl.cxx
 */
//=============================================================================

#include <iostream>
#include <string.h>
#include "rti_dds_impl.h"
#include "fluke_prosim_key_record_string.h"
#include "fluke_prosim_key_record_stringSupport.h"
#include "ndds/ndds_cpp.h"
#include "ndds/ndds_namespace_cpp.h"

using namespace std;
using namespace DDS;

static const char* kfluke_prosim_command_topic_name =
  "fluke_prosim_dd_commmand_topic";
static const char* kfluke_prosim_data_topic_name =
  "fluke_prosim_data_topic";
static const char* kfluke_prosim_key_record_string_topic_name =
  "fluke_prosim_key_record_string_topic";
static const char* kfluke_prosim_command_key_name =
  "fluke/prosim_8/command";
static const char* kfluke_device_command_response_key_name =
  "fluke/prosim_8/device_command_response";

static const int kmax_dds_str_alloc_size = 2048;


//=============================================================================
// FlukeInterfaceRTIDDSIMPL
//=============================================================================


FlukeInterfaceRTIDDSIMPL::FlukeInterfaceRTIDDSIMPL()
  :_key_holder(0),
  _participant(0),
  _command_topic(0),
  _data_topic(0),
  _key_record_string_topic(0),
  _waitset(0),
  _my_read_condition(0),
  _fluke_command_reader(0),
  _fluke_data_writer(0),
  _fluke_key_record_string_writer(0)
{
  _key_holder = new (nothrow) char[kmax_dds_str_alloc_size];
  _waitset = new (nothrow) DDSWaitSet();
}


FlukeInterfaceRTIDDSIMPL::~FlukeInterfaceRTIDDSIMPL()
{
  if (_key_holder) delete [] _key_holder;
  if (_waitset) delete _waitset;
}


bool FlukeInterfaceRTIDDSIMPL::IsStatusOk(int istat)
{
  return istat == RC_STATUS_OK;
}


const char* FlukeInterfaceRTIDDSIMPL::
  get_fluke_prosim_command_topic_name()
{
  return kfluke_prosim_command_topic_name;
}


const char* FlukeInterfaceRTIDDSIMPL::
  get_fluke_prosim_data_topic_name()
{
  return kfluke_prosim_data_topic_name;
}


const char* FlukeInterfaceRTIDDSIMPL::
  get_fluke_prosim_key_record_string_topic_name()
{
  return kfluke_prosim_key_record_string_topic_name;
}


const char* FlukeInterfaceRTIDDSIMPL::
  get_fluke_prosim_command_key_name()
{
  return kfluke_prosim_command_key_name;
}


const char* FlukeInterfaceRTIDDSIMPL::
  get_fluke_device_command_response_key_name()
{
  return kfluke_device_command_response_key_name;
}


const int FlukeInterfaceRTIDDSIMPL::get_max_dds_str_alloc_size()
{
  return kmax_dds_str_alloc_size;
}


string FlukeInterfaceRTIDDSIMPL::get_statusmsg()
{
  return _statusmsg;
}


int FlukeInterfaceRTIDDSIMPL::ParticipantShutdown()
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;
  int istat = RC_STATUS_OK;

  if (_participant)
  {
    retcode = _participant->delete_contained_entities();
    if (retcode != DDS_RETCODE_OK)
    {
      _statusmsg = "delete_contained_entities error";
      istat = RC_DDS_FAIL;
    }

    retcode = DDSTheParticipantFactory->delete_participant(_participant);
    if (retcode != DDS_RETCODE_OK)
    {
      _statusmsg = "delete_participant error";
      istat = RC_DDS_FAIL;
    }
  }
  _participant = 0;
  _command_topic = 0;
  _data_topic = 0;
  _key_record_string_topic = 0;
  _my_read_condition = 0;
  _fluke_command_reader = 0;
  _fluke_data_writer = 0;
  _fluke_key_record_string_writer = 0;
  return istat;
}


int FlukeInterfaceRTIDDSIMPL::CreateParticipant(DDS_DomainId_t domain_id)
{
  if (_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateParticipant() "
      "participant already exits error";
    return RC_FAIL;
  }

  _participant = DDSTheParticipantFactory->create_participant(
    domain_id, DDS_PARTICIPANT_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateParticipant() unable "
      "to create participant.";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic()
{
  const char* type_name = 0;
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

  // Check to see if topic was already created
  if (_command_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic() "
      "Command topic already exits";
    return RC_FAIL;
  }

  // Check to see if participant exits
  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic() "
      "Participant is a pointer to NULL";
    return RC_FAIL;
  }

  type_name = DDSKeyedStringTypeSupport::get_type_name();
  retcode = DDSKeyedStringTypeSupport::register_type(
    _participant, type_name);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic() "
      "Unable to register type error";
    return RC_DDS_FAIL;
  }

  _command_topic = _participant->create_topic(
    kfluke_prosim_command_topic_name,
    type_name, DDS_TOPIC_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!_command_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic() "
      "Unable to create topic error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::CreateFlukeDataTopic()
{
  const char* type_name = 0;
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

  // Check to see if it was already created.
  if (_data_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataTopic() "
      "Data topic was already created";
    return RC_FAIL;
  }

  // Check for participant
  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataTopic() "
      "Participant is a pointer to NULL";
    return RC_FAIL;
  }

  type_name = DDSKeyedStringTypeSupport::get_type_name();
  retcode = DDSKeyedStringTypeSupport::register_type(
    _participant, type_name);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandTopic() "
      "Unable to register type error";
    return RC_DDS_FAIL;
  }

  _data_topic = _participant->create_topic(kfluke_prosim_data_topic_name,
    type_name, DDS_TOPIC_QOS_DEFAULT, NULL /* listener */,
    DDS_STATUS_MASK_NONE);
  if (!_data_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataTopic() unable to "
      "Create data topic error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringTopic()
{
  const char* type_name = 0;
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

  // Check to see if it was already created.
  if (_key_record_string_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringTopic() "
      "key record string topic was already created";
    return RC_FAIL;
  }

  // Check for participant
  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringTopic() "
      "participant is a pointer to NULL";
    return RC_FAIL;
  }

  type_name = FlukeProsimKeyRecordStringTypeSupport::get_type_name();
  retcode = FlukeProsimKeyRecordStringTypeSupport::register_type(
    _participant, type_name);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "unable to register type error";
    return RC_DDS_FAIL;
  }

  _key_record_string_topic = _participant->create_topic(
    kfluke_prosim_key_record_string_topic_name,
    type_name, DDS_TOPIC_QOS_DEFAULT, NULL /* listener */,
    DDS_STATUS_MASK_NONE);
  if (!_key_record_string_topic)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringTopic() "
      "unable to create data topic error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader()
{
  DDSSubscriber* subscriber = 0;
  DDSDataReader* reader = 0;
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

  if (_fluke_command_reader)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "Command data reader was already created";
    return RC_FAIL;
  }

  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "Participant is a pointer to NULL error";
    return RC_FAIL;
  }

  if (!_command_topic)
  {
    int istat =  CreateFlukeCommandTopic();
    if (!IsStatusOk(istat)) return istat;
  }

  if (!_waitset)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "unable to create waitset error";
    return RC_DDS_FAIL;
  }

  subscriber = _participant->create_subscriber(
    DDS_SUBSCRIBER_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!subscriber)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "Unable to create subscriber error.";
    return RC_DDS_FAIL;
  }

  reader = subscriber->create_datareader(
    _command_topic, DDS_DATAREADER_QOS_DEFAULT, NULL,
    DDS_STATUS_MASK_ALL);
  if (!reader)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "Unable to create datareader error.";
    return RC_DDS_FAIL;
  }

  _fluke_command_reader = DDSKeyedStringDataReader::narrow(reader);
  if (!_fluke_command_reader)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "Unable to narrow data reader error.";
    return RC_DDS_FAIL;
  }

  if (!_my_read_condition)
  {
    _my_read_condition = reader->create_readcondition(
      DDS_NOT_READ_SAMPLE_STATE,
      DDS_ANY_VIEW_STATE,
      DDS_ANY_INSTANCE_STATE);
    if (!_my_read_condition)
    {
      _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
        "Unable to create read condition error";
      return RC_DDS_FAIL;
    }
  }

  retcode = _waitset->attach_condition(_my_read_condition);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeCommandDataReader() "
      "unable to attach read condition error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::FlukeCommandDataReaderWaitForSample()
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;
  DDS_Duration_t timeout = {0, 1000000}; // 1 millisecond
  DDSConditionSeq active_conditions; // Holder for active conditions
  bool is_cond_triggered = false;

  if (!_key_holder)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
      "FlukeCommandDataReaderWaitForSample() Unable to allocate memory for "
      "key error";
    return RC_FAIL;
  }

  if (!_waitset)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
      "FlukeCommandDataReaderWaitForSample() Unable to create WaitSet error";
    return RC_DDS_FAIL;
  }

  if (!_fluke_command_reader)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
      "FlukeCommandDataReaderWaitForSample() Command data reader wasn't "
      "created.";
    return RC_FAIL;
  }

  // Wait until conditions become true.
  retcode = _waitset->wait(active_conditions, timeout);

  // Do nothing when timeout occurs
  if (retcode == DDS_RETCODE_TIMEOUT) return RC_WAITSET_TIMEOUT;

  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
      "FlukeCommandDataReaderWaitForSample() Wait error";
    return RC_DDS_FAIL;
  }

  // Wait was successful. Check to see if conditions are triggered
  for(int ix = 0; ix < active_conditions.length(); ++ix)
  {
    if (active_conditions[ix] != _my_read_condition) continue;
    is_cond_triggered = true;
    break;
  }

  if (is_cond_triggered)
  {
    DDS_KeyedStringSeq dataSeq;
    DDS_SampleInfoSeq infoSeq;
    DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

    retcode = _fluke_command_reader->take(
      dataSeq,
      infoSeq,
      DDS_LENGTH_UNLIMITED,
      DDS_ANY_SAMPLE_STATE,
      DDS_ANY_VIEW_STATE,
      DDS_ANY_INSTANCE_STATE);

    if (retcode == DDS_RETCODE_NO_DATA)
    {
      _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
        "FlukeCommandDataReaderWaitForSample() Took no data error";
      return RC_DDS_FAIL;
    }
    else if (retcode != DDS_RETCODE_OK)
    {
      _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
        "FlukeCommandDataReaderWaitForSample() Unable to take data "
        "from datareader error";
      return RC_DDS_FAIL;
    }

    for (int ix = 0; ix < dataSeq.length(); ++ix)
    {
      if (infoSeq[ix].valid_data)
      {
        // Reset key to all zeros
        memset(_key_holder, 0, kmax_dds_str_alloc_size);

        // Get instance handle
        DDS_InstanceHandle_t instance_handle = _fluke_command_reader->
          lookup_instance(dataSeq[ix]);

        // Get key
        retcode = _fluke_command_reader->get_key_value(_key_holder,
          instance_handle);
        if (retcode != DDS_RETCODE_OK)
        {
          _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
            "FlukeCommandDataReaderWaitForSample() Unable to get key "
            "value error";
          return RC_DDS_FAIL;
        }

        if (strcmp(_key_holder, kfluke_prosim_command_key_name) != 0)
        {
          cout << "! key mismatch... ignoring publication." << endl;
          retcode = _participant->ignore_publication(instance_handle);
          if (retcode != DDS_RETCODE_OK)
          {
            _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
              "FlukeCommandDataReaderWaitForSample() Unable to ignore "
              "publication error";
            return RC_DDS_FAIL;
          }
        }

        char cmd_str[kmax_dds_str_alloc_size + 1] = {0};
        strncpy(cmd_str, dataSeq[ix].value, kmax_dds_str_alloc_size);

        try
        {
          _cmds_waiting.push_back(cmd_str);
        }
        catch(exception& exc)
        {
          _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
            "FlukeCommandDataReaderWaitForSample() Unable to push onto "
            "queue error";
          return RC_FAIL;
        }
      }
    }

    retcode = _fluke_command_reader->return_loan(dataSeq, infoSeq);
    if (retcode != DDS_RETCODE_OK)
    {
      _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
        "FlukeCommandDataReaderWaitForSample() unable to return loan error";
      return RC_DDS_FAIL;
    }
  }

  return RC_STATUS_OK;
}


bool FlukeInterfaceRTIDDSIMPL::IsCommandWaiting()
{
  return !_cmds_waiting.empty();
}


int FlukeInterfaceRTIDDSIMPL::GetCommandWaiting(string* value)
{
  if (!value)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::"
      "GetCommandWaiting() value is pointer to NULL error";
    return RC_FAIL;
  }

  try
  {
    *value = _cmds_waiting.front();
    _cmds_waiting.pop_front();
  }
  catch(exception& exc)
  {
	_statusmsg = "FlukeInterfaceRTIDDSIMPL::"
	  "GetCommandWaiting() exception thrown, unable to take from queue error";
	return RC_FAIL;
  }

  return RC_STATUS_OK;
}

int FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter()
{
  DDSPublisher* publisher = 0;
  DDSDataWriter* writer = 0;

  if (_fluke_data_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "fluke data writer was already created";
    return RC_FAIL;
  }


  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "participant is pointer to NULL";
    return RC_FAIL;
  }

  if (!_data_topic)
  {
    int istat = CreateFlukeDataTopic();
    if (!IsStatusOk(istat)) return istat;
  }

  publisher = _participant->create_publisher(
    DDS_PUBLISHER_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!publisher)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "Unable to create publisher error";
    return RC_DDS_FAIL;
  }

  writer = publisher->create_datawriter(_data_topic,
    DDS_DATAWRITER_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "unable to create datawriter error";
    return RC_DDS_FAIL;
  }

  _fluke_data_writer = DDSKeyedStringDataWriter::narrow(writer);
  if (!_fluke_data_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeDataDataWriter() "
      "unable to narrow datawriter error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::WriteFlukeData(const char* value)
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;
  DDS_KeyedString keyed_str(kmax_dds_str_alloc_size, kmax_dds_str_alloc_size);

  if (!_fluke_data_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::WriteFlukeData() writer is "
      "a pointer to NULL";
    return RC_FAIL;
  }

  memcpy(keyed_str.value, value, strlen(value));
  memcpy(keyed_str.key, kfluke_device_command_response_key_name,
    strlen(kfluke_device_command_response_key_name));

  retcode = _fluke_data_writer->write(keyed_str, DDS_HANDLE_NIL);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::WriteFlukeData() unable to "
      "write error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter()
{
  DDSPublisher* publisher = 0;
  DDSDataWriter* writer = 0;

  if (_fluke_key_record_string_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter() "
      "key record string data writer already exists";
    return RC_FAIL;
  }

  if (!_participant)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter() "
      "participant is pointer to NULL";
    return RC_FAIL;
  }

  if (!_key_record_string_topic)
  {
    int istat = CreateFlukeKeyRecordStringTopic();
    if (!IsStatusOk(istat)) return istat;
  }

  publisher = _participant->create_publisher(
    DDS_PUBLISHER_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!publisher)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter() "
      "Unable to create publisher error";
    return RC_DDS_FAIL;
  }

  writer = publisher->create_datawriter(_key_record_string_topic,
    DDS_DATAWRITER_QOS_DEFAULT, NULL, // listener
    DDS_STATUS_MASK_NONE);
  if (!writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter() "
      "Unable to create datawriter error";
    return RC_DDS_FAIL;
  }

  _fluke_key_record_string_writer = FlukeProsimKeyRecordStringDataWriter::
    narrow(writer);
  if (!_fluke_key_record_string_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::CreateFlukeKeyRecordStringDataWriter() "
      "unable to narrow datawriter error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeInterfaceRTIDDSIMPL::WriteFlukeKeyRecordString(
  FlukeProsimKeyRecordString* value)
{
  DDS_ReturnCode_t retcode = DDS_RETCODE_OK;

  if (!_fluke_key_record_string_writer)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::WriteFlukeKeyRecordString() "
      "key record string writer is a pointer to NULL error";
    return RC_FAIL;
  }

  if(!value)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::WriteFlukeKeyRecordString() "
      "Value is a pointer to NULL error";
    return RC_FAIL;
  }

  retcode = _fluke_key_record_string_writer->write(*value, DDS_HANDLE_NIL);
  if (retcode != DDS_RETCODE_OK)
  {
    _statusmsg = "FlukeInterfaceRTIDDSIMPL::WriteFlukeKeyRecordString() "
      "Unable to write error";
    return RC_DDS_FAIL;
  }

  return RC_STATUS_OK;
}
