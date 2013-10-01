/*
 * rs232_comm.h
 *
 *  Created on: Jul 24, 2013
 *      Author: mike
 */

#ifndef FLUKE_SRC_RS232_COMM_H_
#define FLUKE_SRC_RS232_COMM_H_

#include <string>


class RS232Comm
{
public:
  RS232Comm();
  ~RS232Comm();


  static bool IsStatusOk(const int istat);
  std::string get_statusmsg();
  int get_portnumber();
  int get_porthandle();


  /**
   * Open port using the port name and baud rate.
   * @return Zero for success.
   */
  int OpenPort(std::string portname, int baudrate, int* handle);


  /**
   * Close open port using the port number.
   */
  void ClosePort();


private:
  // Disallow use of implicitly generated member functions:
  RS232Comm(const RS232Comm &src);
  RS232Comm &operator=(const RS232Comm &rhs);

  std::string _statusmsg;
  int _portnumber;
};

#endif
