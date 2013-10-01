/*
 * @file     my_fluke_impl.cxx
 * @brief    This file defines a class which is this project's implementation
 * of the Fluke API. The virtual functions inherited communicate messages
 * using RTI DDS.
 *
 */
//=============================================================================

#include "my_fluke_impl.h"
#include "rti_dds_impl.h"
#include "boost/date_time/posix_time/posix_time.hpp"

using namespace std;


MyFluke::MyFluke(int in_fd, int out_fd)
  :Fluke(in_fd, out_fd),
   _rti_dds_impl(0)
{
}


MyFluke::~MyFluke()
{
}


string MyFluke::get_statusmsg()
{
  return _statusmsg;
}


int MyFluke::CreateDDSDataWriters(FlukeInterfaceRTIDDSIMPL* rti_dds_impl)
{
  int istat = 0;

  if (!rti_dds_impl)
  {
    _statusmsg = "MyFluke::CreateDDSEntities() rti_dds_impl is a pointer to "
      "NULL";
	return __LINE__;
  }

  _rti_dds_impl = rti_dds_impl;

  cout << "Creating DataWriter..."<< endl;
  istat = _rti_dds_impl->CreateFlukeDataDataWriter();
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = _rti_dds_impl->get_statusmsg();
    return istat;
  }

  cout << "Creating KeDataWriter..."<< endl;
  istat = _rti_dds_impl->CreateFlukeKeyRecordStringDataWriter();
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = _rti_dds_impl->get_statusmsg();
    return istat;
  }

  return istat;
}


int MyFluke::ReceiveDeviceCommandResponse(char* message, size_t size,
  unsigned int sequence_number)
{
  if (!message)
  {
    _statusmsg = "MyFluke::ReceiveDeviceCommandResponse() message is a "
      "pointer to NULL";
    return __LINE__;
  }

  int istat = 0;
  string pub_message(message, size);

  // Print response
  cout << "Received response to device command (" << sequence_number << "):"
    << "\n" << pub_message;

  // Publish response
  istat = _rti_dds_impl->WriteFlukeData(pub_message.c_str());
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = _rti_dds_impl->get_statusmsg();
    return istat;
  }

  return istat;
}


int MyFluke::ReceiveResponse(char* message, size_t size)
{
  if (!message)
  {
    _statusmsg = "MyFluke::ReceiveDeviceCommandResponse() message is a "
      "pointer to NULL";
    return __LINE__;
  }

  int istat = 0;
  string pub_message(message, size);
  FlukeProsimKeyRecordString* key_record_str = 0;

  // Print response
  std::cout << "Received response:" << "\n" << pub_message;

  key_record_str = FlukeProsimKeyRecordStringTypeSupport::create_data();
  if (!key_record_str)
  {
    _statusmsg = "MyFluke::ReceiveResponse() unable to "
      "create data";
    return __LINE__;
  }

  // Determine if there is a Key Record String to publish.
  if (!IsKeyRecordString(message, size)) return istat;

  // Get UTC time stamp
  boost::posix_time::ptime current_time(
    boost::posix_time::microsec_clock::universal_time());
  string timestamp = boost::posix_time::to_iso_extended_string(current_time);

  // Store values needed for DDS transport structure
  // UDI
  memcpy(key_record_str->udi, "Fluke Prosim 8", strlen("Fluke Prosim 8"));

  // UTC time stamp
  key_record_str->absolute_timestamp.century =
    atoi(timestamp.substr(0, 2).c_str());
  key_record_str->absolute_timestamp.year =
    atoi(timestamp.substr(2, 2).c_str());
  key_record_str->absolute_timestamp.month =
    atoi(timestamp.substr(5, 2).c_str());
  key_record_str->absolute_timestamp.day =
    atoi(timestamp.substr(8, 2).c_str());
  key_record_str->absolute_timestamp.hour =
    atoi(timestamp.substr(11, 2).c_str());
  key_record_str->absolute_timestamp.minute =
    atoi(timestamp.substr(14, 2).c_str());
  key_record_str->absolute_timestamp.second =
    atoi(timestamp.substr(17, 2).c_str());
  key_record_str->absolute_timestamp.sec_fractions =
    atoi(timestamp.substr(20, 2).c_str()); // Hundredth of a second

  // Key Record String
  key_record_str->key_code = atoi(pub_message.substr(0, 2).c_str());
  key_record_str->number_of_cycles_pressed =
    atoi(pub_message.substr(3, 3).c_str());
  key_record_str->lcd_screen_number = atoi(pub_message.substr(7, 3).c_str());
  memcpy(key_record_str->lcd_checksum,
    pub_message.substr(11, 32).c_str(), 32);

  // Publish the Key Record String
  istat = _rti_dds_impl->WriteFlukeKeyRecordString(key_record_str);
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = _rti_dds_impl->get_statusmsg();
    return istat;
  }

  return istat;
}
