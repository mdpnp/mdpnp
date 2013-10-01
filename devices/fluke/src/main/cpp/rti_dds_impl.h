/*
 * @file      rti_dds_impl.h
 */
//=============================================================================

#ifndef FLUKE_SRC_RTI_DDS_IMPL_H_
#define FLUKE_SRC_RTI_DDS_IMPL_H_

#include <deque>
#include <string>
#include "fluke_prosim_key_record_string.h"
#include "fluke_prosim_key_record_stringSupport.h"
#include "ndds/ndds_cpp.h"


class FlukeInterfaceRTIDDSIMPL
{
public:
  enum
  {
    RC_STATUS_OK = 0,
    RC_FAIL,
    RC_DDS_FAIL,
    RC_WAITSET_TIMEOUT
  };

  FlukeInterfaceRTIDDSIMPL();
  ~FlukeInterfaceRTIDDSIMPL();

  static bool IsStatusOk(const int istat);
  static const char* get_fluke_prosim_command_topic_name();
  static const char* get_fluke_prosim_data_topic_name();
  static const char* get_fluke_prosim_key_record_string_topic_name();
  static const char* get_fluke_prosim_command_key_name();
  static const char* get_fluke_device_command_response_key_name();
  static const int get_max_dds_str_alloc_size();

  std::string get_statusmsg();

  int ParticipantShutdown();
  int CreateParticipant(DDS_DomainId_t domain_id);

  int CreateFlukeCommandDataReader();
  int FlukeCommandDataReaderWaitForSample();
  bool IsCommandWaiting();
  int GetCommandWaiting(std::string* value);

  int CreateFlukeDataDataWriter();
  int WriteFlukeData(const char* value);

  int CreateFlukeKeyRecordStringDataWriter();
  int WriteFlukeKeyRecordString(FlukeProsimKeyRecordString* value);


private:
  // Disallow use of implicitly generated member functions:
  FlukeInterfaceRTIDDSIMPL(const FlukeInterfaceRTIDDSIMPL &src);
  FlukeInterfaceRTIDDSIMPL &operator=(const FlukeInterfaceRTIDDSIMPL &rhs);

  int CreateFlukeCommandTopic();
  int CreateFlukeDataTopic();
  int CreateFlukeKeyRecordStringTopic();

  std::string _statusmsg;
  char* _key_holder;
  DDSDomainParticipant* _participant;
  DDSTopic* _command_topic;
  DDSTopic* _data_topic;
  DDSTopic* _key_record_string_topic;
  DDSWaitSet* _waitset;
  DDSReadCondition* _my_read_condition;
  DDSKeyedStringDataReader* _fluke_command_reader;
  DDSKeyedStringDataWriter* _fluke_data_writer;
  FlukeProsimKeyRecordStringDataWriter* _fluke_key_record_string_writer;
  std::deque<std::string> _cmds_waiting;
};

#endif
