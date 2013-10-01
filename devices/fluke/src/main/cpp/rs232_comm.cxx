/*
 * @file rs232_comm.cxx
 */
//=============================================================================

#include <iostream>
#include <string>
#include <sstream>
#include "rs232_comm.h"
#include "rs232.h"
#include "fluke.h"

using namespace std;


RS232Comm::RS232Comm()
  :_portnumber(-1)
{
}


RS232Comm::~RS232Comm()
{
}


bool RS232Comm::IsStatusOk(const int istat)
{
  return istat == 0;
}


string RS232Comm::get_statusmsg()
{
  return _statusmsg;
}


int RS232Comm::get_portnumber()
{
  return _portnumber;
}


int RS232Comm::get_porthandle()
{
  return sio_GetPortHandle(_portnumber);
}


int RS232Comm::OpenPort(string portname, int baudrate, int* handle)
{
  int istat = 0;

  if (!handle)
  {
    _statusmsg = "RS232Comm::OpenPort() handle is a pointer to NULL";
    return __LINE__;
  }

#ifndef TESTING_WITHOUT_DEVICE_ATTACHED

  if (portname.empty())
  {
    _statusmsg = "RS232Comm::OpenPort() Set portname with option"
      " -portname. Enter option -help for help message.";
    return __LINE__;
  }

  if (sio_IsOpen(_portnumber))
  {
    cout << "OK. Port already open(" << portname << ", "
         << baudrate << ")" << endl;
    goto exit_fn;
  }

  // Open port
  _portnumber = sio_OpenComport(portname.c_str(), baudrate);
  if (_portnumber < 0)
  {
    stringstream ss;
    ss << "RS232Comm::OpenPort() Failed to open port "
       << portname << ", baud" << baudrate;
    _statusmsg = ss.str();
    return __LINE__;
  }

  cout << "OK. Open port(" << portname
       << ", " << baudrate << ")" << endl;

exit_fn:

  if (IsStatusOk(istat)) *handle = sio_GetPortHandle(_portnumber);

#endif

  return istat;
}


void RS232Comm::ClosePort()
{
#ifndef TESTING_WITHOUT_DEVICE_ATTACHED
  sio_CloseComport(_portnumber);
  _portnumber = -1; // Reset to invalid port number
#endif
}
