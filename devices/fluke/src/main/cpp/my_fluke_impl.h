/*
 * @file    my_fluke_impl.h
 * @brief   Declares class which inherits from the Fluke API. This class
 * implements virtual functions of the API.
 *
 */
//=============================================================================
#ifndef FLUKE_SRC_MY_FLUKE_IMPL_H_
#define FLUKE_SRC_MY_FLUKE_IMPL_H_

#include "ndds/ndds_cpp.h"
#include "fluke.h"
#include "rti_dds_impl.h"


class MyFluke : public Fluke
{
public:
  MyFluke(int in_fd, int out_fd);
  ~MyFluke();

  std::string get_statusmsg();
  static const char* get_fluke_device_command_response_key();

  int ReceiveDeviceCommandResponse(char* message, size_t size,
    unsigned int sequence_number);
  int ReceiveResponse(char* message, size_t size);

  int CreateDDSDataWriters(FlukeInterfaceRTIDDSIMPL* rti_dds_impl);

private:

  // Disallow use of implicitly generated member functions:
  MyFluke(const MyFluke &src);
  MyFluke &operator=(const MyFluke &rhs);

  FlukeInterfaceRTIDDSIMPL* _rti_dds_impl;
  std::string _statusmsg;
};

#endif
