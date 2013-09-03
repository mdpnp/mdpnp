/*
***************************************************************************
*
* Author: Teunis van Beelen
*
* Copyright (C) 2005, 2006, 2007, 2008, 2009 Teunis van Beelen
*
* teuniz@gmail.com
*
***************************************************************************
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation version 2 of the License.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
***************************************************************************
*
* This version of GPL is at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
*
***************************************************************************
*/
#ifdef	_WIN32
#include <windows.h>
#include <time.h>
#include <string.h>
#include <stdio.h>

#else
#include <string>
#include <iostream>
#include <time.h>

#include <stdio.h>
#include <string.h>

#include <termios.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <limits.h>
#endif

#include "rs232.h"

//#define TEST_GETCOMPORTNUMBER

#ifdef	_WIN32
typedef HANDLE  SIO_PORTHANDLE;
static char *_comports[] =
{
  "\\\\.\\COM1",  "\\\\.\\COM2",  "\\\\.\\COM3",  "\\\\.\\COM4",
  "\\\\.\\COM5",  "\\\\.\\COM6",  "\\\\.\\COM7",  "\\\\.\\COM8",
  "\\\\.\\COM9",  "\\\\.\\COM10", "\\\\.\\COM11", "\\\\.\\COM12",
  "\\\\.\\COM13", "\\\\.\\COM14", "\\\\.\\COM15", "\\\\.\\COM16"
};

#else
typedef int     SIO_PORTHANDLE;
static const char *_comports[] =
{
  "/dev/ttyS0",   "/dev/ttyS1",   "/dev/ttyS2",   "/dev/ttyS3",
  "/dev/ttyS4",   "/dev/ttyS5",   "/dev/ttyS6",   "/dev/ttyS7",
  "/dev/ttyS8",   "/dev/ttyS9",   "/dev/ttyS10",  "/dev/ttyS11",
  "/dev/ttyS12",  "/dev/ttyS13",  "/dev/ttyS14",  "/dev/ttyS15",
  "/dev/ttyUSB0", "/dev/ttyUSB1", "/dev/ttyUSB2", "/dev/ttyUSB3",
  "/dev/ttyUSB4", "/dev/ttyUSB5", "/dev/ttyACM0", "/dev/ttyACM1",
  "/dev/ttyACM2", "/dev/ttyACM3", "/dev/ttyACM4", "/dev/ttyACM5"
};
#endif

enum { _sio_max_comports = sizeof(_comports) / sizeof(_comports[0]) };

#ifdef _WIN32
static bool isgoodportnumber(const int portnumber);
#endif
static int get_com_portnumber(const char *portname);
static int get_tty_portnumber(const char *portname);
static int get_portnumber(const char *portname);


#ifdef THIS_IS_NOT_YET_IMPLEMENTED
// TODO:
// replace all output to monitor (printf, perror) with _statusmsg
// and provide access with sio_GetStatusMsg function.
static enum { _maxsize_statusmsg = 256 };
static char _statusmsg[_maxsize_statusmsg];

//=============================================================================
char *sio_GetStatusMsg() { return(_statusmsg); }
#endif

//=============================================================================
#ifdef	_WIN32
// WindowsWindowsWindowsWindowsWindowsWindowsWindowsWindowsWindowsWindowsWindow

static int _is_open[_sio_max_comports];
static SIO_PORTHANDLE _hport[_sio_max_comports];
static char _sdci[64];  // device control information

//=============================================================================
void sio_CloseComport(const int portnumber)
{
  if (isgoodportnumber(portnumber))
  {
    if (_is_open[portnumber]) CloseHandle(_hport[portnumber]);
    _is_open[portnumber] = 0;
  }
}

//=============================================================================
// Open the COMM port sepcified by portname and baudrate.
// If success, return the port number (0 - n). Otherwise return -1.
int sio_OpenComport(const char *portname, const int baudrate)
{
  DCB port_settings = { 0 };
  COMMTIMEOUTS cptimeouts = { 0 };
  int iret = -1;
  int portnumber = get_portnumber(portname);

//============================================
  printf("%s\n",
    "sio_OpenComport(): may need to be modified for Windows. "
    "Notice in the linux version, the port is blocking, and "
	  "there is a timeout. Modify windows sio_FlushComport() as well. -Mike");
  return iret;
//============================================

  while(1)
  {
    if ((portnumber >= _sio_max_comports) || (portnumber < 0))
    {
      printf("Illegal port name %s\n", portname);
      break;
    }

    if (_is_open[portnumber])
    {
      printf("Error: Port %s already open\n", portname);
      break;
    }

    if      (baudrate ==    110) strcpy(_sdci, "baud=110 data=8 parity=N stop=1");
    else if (baudrate ==    300) strcpy(_sdci, "baud=300 data=8 parity=N stop=1");
    else if (baudrate ==    600) strcpy(_sdci, "baud=600 data=8 parity=N stop=1");
    else if (baudrate ==   1200) strcpy(_sdci, "baud=1200 data=8 parity=N stop=1");
    else if (baudrate ==   2400) strcpy(_sdci, "baud=2400 data=8 parity=N stop=1");
    else if (baudrate ==   4800) strcpy(_sdci, "baud=4800 data=8 parity=N stop=1");
    else if (baudrate ==   9600) strcpy(_sdci, "baud=9600 data=8 parity=N stop=1");
    else if (baudrate ==  19200) strcpy(_sdci, "baud=19200 data=8 parity=N stop=1");
    else if (baudrate ==  38400) strcpy(_sdci, "baud=38400 data=8 parity=N stop=1");
    else if (baudrate ==  57600) strcpy(_sdci, "baud=57600 data=8 parity=N stop=1");
    else if (baudrate == 115200) strcpy(_sdci, "baud=115200 data=8 parity=N stop=1");
    else if (baudrate == 128000) strcpy(_sdci, "baud=128000 data=8 parity=N stop=1");
    else if (baudrate == 256000) strcpy(_sdci, "baud=256000 data=8 parity=N stop=1");
    else
    {
      printf("invalid baudrate %d\n", baudrate);
      break;
    }

    _hport[portnumber] = CreateFileA(
      _comports[portnumber],
      GENERIC_READ | GENERIC_WRITE,
      0,   // no share
      0,   // no security
      OPEN_EXISTING,
      0,   // no threads
      0);  // no templates

    if (_hport[portnumber] == INVALID_HANDLE_VALUE)
    {
      int lasterr = GetLastError();
      char errmsg[256] = "";
      printf("unable to open comport %s (%d)\n", _comports[portnumber], lasterr);
      FormatMessage(
        FORMAT_MESSAGE_FROM_SYSTEM,
        0,
        lasterr,
        0,
        errmsg,
        sizeof(errmsg),
        0);
      printf("%s", errmsg);
      break;
    }

    port_settings.DCBlength = sizeof(port_settings);

    if (!BuildCommDCBA(_sdci, &port_settings))
    {
      printf("unable to set comport dcb settings\n");
      CloseHandle(_hport[portnumber]);
      break;
    }

    if (!SetCommState(_hport[portnumber], &port_settings))
    {
      printf("unable to set comport cfg settings\n");
      CloseHandle(_hport[portnumber]);
      break;
    }

    cptimeouts.ReadIntervalTimeout = MAXDWORD;

    if (!SetCommTimeouts(_hport[portnumber], &cptimeouts))
    {
      printf("unable to set comport time-out settings\n");
      CloseHandle(_hport[portnumber]);
      break;
    }
    _is_open[portnumber] = 1;
    iret = portnumber;
    break;
  }
  return(iret);
}

//=============================================================================
int sio_GetPortHandle(const int portnumber)
{
  return reinterpret_cast<int>(_hport[portnumber]);
}

//=============================================================================
int sio_PollComport(const int portnumber, char *buf, int size)
{
  int n = 0;
  if (size > 4096) size = 4096;

/* added the void pointer cast, otherwise gcc will complain about */
/* "warning: dereferencing type-punned pointer will break strict aliasing rules" */

  if (isgoodportnumber(portnumber))
  {
    ReadFile(_hport[portnumber], buf, size, (LPDWORD)((void *)&n), NULL);
  }
  return(n);
}

//=============================================================================
int sio_SendByte(const int portnumber, const char byte)
{
  int n = 0;
  if (isgoodportnumber(portnumber))
  {
    WriteFile(_hport[portnumber], &byte, 1, (LPDWORD)((void *)&n), NULL);
  }
  else
  {
    n = -1;
  }
  if (n < 0)  return(1);
  return(0);
}

//=============================================================================
int sio_SendBuf(const int portnumber, const char *buf, const int size)
{
  int n = 0;
  if (isgoodportnumber(portnumber))
  {
    if (!WriteFile(
      _hport[portnumber], buf, size, (LPDWORD)((void *)&n), NULL)) n = -1;
  }
  else
  {
    n = -1;
  }
  return(n);
}

//=============================================================================
int sio_IsCTSEnabled(const int portnumber)
{
  int iret = 0;
  int status = 0;
  GetCommModemStatus(_hport[portnumber], (LPDWORD)((void *)&status));
  if (status & MS_CTS_ON) iret = 1;
  return(iret);
}

int sio_FlushComport(const int portnumber)
{
//============================================
  printf("%s\n", "sio_FlushComport(): implement windows flush. -Mike");
  return -1;
//============================================
}

//=============================================================================
#else
// NotWindowsNotWindowsNotWindowsNotWindowsNotWindowsNotWindowsNotWindowsNotWin
//=============================================================================

static int _is_open[_sio_max_comports];
static SIO_PORTHANDLE _hport[_sio_max_comports];
static int _error;
struct termios  _old_port_settings[_sio_max_comports];

//=============================================================================
//=============================================================================
// Open the COMM port sepcified by portnumber and baudrate.
// If success, return 0. Otherwise return non-0.
int sio_OpenComport(const char *portname, const int baudrate)
{
  int iret = -1;
  int baudr = 0;
  int portnumber = get_portnumber(portname);
  struct termios  new_port_settings = { 0 };

  while(1)
  {
    if ((portnumber >= _sio_max_comports) || (portnumber < 0))
    {
      printf("Error: Illegal port name %s\n", portname);
      break;
    }

    if (_is_open[portnumber])
    {
      printf("Error: Port %s already open\n", portname);
      break;
    }

    if      (baudrate ==      50) baudr = B50;
    else if (baudrate ==      75) baudr = B75;
    else if (baudrate ==     110) baudr = B110;
    else if (baudrate ==     134) baudr = B134;
    else if (baudrate ==     150) baudr = B150;
    else if (baudrate ==     200) baudr = B200;
    else if (baudrate ==     300) baudr = B300;
    else if (baudrate ==     600) baudr = B600;
    else if (baudrate ==    1200) baudr = B1200;
    else if (baudrate ==    1800) baudr = B1800;
    else if (baudrate ==    2400) baudr = B2400;
    else if (baudrate ==    4800) baudr = B4800;
    else if (baudrate ==    9600) baudr = B9600;
    else if (baudrate ==   19200) baudr = B19200;
    else if (baudrate ==   38400) baudr = B38400;
    else if (baudrate ==   57600) baudr = B57600;
    else if (baudrate ==  115200) baudr = B115200;
    else if (baudrate ==  230400) baudr = B230400;
    else if (baudrate ==  460800) baudr = B460800;
    else if (baudrate ==  500000) baudr = B500000;
    else if (baudrate ==  576000) baudr = B576000;
    else if (baudrate ==  921600) baudr = B921600;
    else if (baudrate == 1000000) baudr = B1000000;
    else
    {
      printf("For port %s, invalid baudrate %d\n", portname, baudrate);
      break;
    }

    _hport[portnumber] =
      open(_comports[portnumber], O_RDWR | O_NOCTTY );
    if (_hport[portnumber] < 0)
    {
      perror("unable to open comport ");
      break;
    }

    _error = tcgetattr(_hport[portnumber], _old_port_settings + portnumber);
    if (_error == -1)
    {
      close(_hport[portnumber]);
      perror("unable to read portsettings ");
      break;
    }

    new_port_settings.c_cflag = CS8 | CLOCAL | CREAD;
    cfsetspeed(&new_port_settings, baudr);
    new_port_settings.c_iflag = IGNPAR;
    new_port_settings.c_cc[VMIN] = 0;   // block until n bytes are received
    new_port_settings.c_cc[VTIME] = 3;  // block until a timer expires (n * 100 mSec)
    _error = tcsetattr(_hport[portnumber], TCSANOW, &new_port_settings);
    if (_error == -1)
    {
      close(_hport[portnumber]);
      perror("unable to adjust portsettings ");
      break;
    }

    _is_open[portnumber] = 1;
    iret = portnumber;
    break;
  }
  return(iret);
}

//=============================================================================
SIO_PORTHANDLE sio_GetPortHandle(const int portnumber)
{
  return _hport[portnumber];
}

//=============================================================================
int sio_FlushComport(const int portnumber)
{
  usleep(1000);
  tcflush(_hport[portnumber], TCIOFLUSH);
  return 0;
}

//=============================================================================
int sio_PollComport(const int portnumber, char *buf, int size)
{
  int n = 0;
#ifndef __STRICT_ANSI__
  // __STRICT_ANSI__ is defined when the -ansi option is used for gcc
  if (size > SSIZE_MAX) size = (int)SSIZE_MAX; // SSIZE_MAX is defined in limits.h
#else
  if (size > 4096)  size = 4096;
#endif
  n = read(_hport[portnumber], buf, size);
  return(n);
}

//=============================================================================
int sio_SendByte(const int portnumber, const char byte)
{
  int iret = 0;
  int n = write(_hport[portnumber], static_cast<const char*>(&byte), 1);
  if (n < 0) iret = 1;
  return(iret);
}

//=============================================================================
int sio_SendBuf(const int portnumber, const char *buf, const int size)
{
  return(write(_hport[portnumber], reinterpret_cast<const unsigned char*>(buf), size));
}

//=============================================================================
void sio_CloseComport(const int portnumber)
{
  if (_is_open[portnumber])
  {
    close(_hport[portnumber]);
    tcsetattr(_hport[portnumber], TCSANOW, _old_port_settings + portnumber);
    _is_open[portnumber] = 0;
  }
}

/*
Constant  Description
TIOCM_LE  DSR (data set ready/line enable)
TIOCM_DTR DTR (data terminal ready)
TIOCM_RTS RTS (request to send)
TIOCM_ST  Secondary TXD (transmit)
TIOCM_SR  Secondary RXD (receive)
TIOCM_CTS CTS (clear to send)
TIOCM_CAR DCD (data carrier detect)
TIOCM_CD  Synonym for TIOCM_CAR
TIOCM_RNG RNG (ring)
TIOCM_RI  Synonym for TIOCM_RNG
TIOCM_DSR DSR (data set ready)
*/

//=============================================================================
int sio_IsCTSEnabled(const int portnumber)
{
  int iret = 0;
  int status = ioctl(_hport[portnumber], TIOCMGET, &status);
  if (status & TIOCM_CTS) iret = 1;
  return(iret);
}
#endif

//=============================================================================
int sio_GetMaxComports() { return(_sio_max_comports); }

//=============================================================================
int sio_IsOpen(const int portnumber) { return(_is_open[portnumber]); }

//=============================================================================
// send string to serial port
void sio_cprintf(const int portnumber, char *text)
{
  while (*text != '\0') sio_SendByte(portnumber, *(text++));
}

//=============================================================================
//=============================================================================

//=============================================================================
#ifdef _WIN32
bool isgoodportnumber(const int portnumber)
{
  bool bval =
    ((portnumber < 0) || (portnumber > _sio_max_comports)) ? false : true;
  return(bval);
}
#endif

//=============================================================================
// Return port number for the specified port name, formatted as:
//    /dev/ttySx, (where x = 0 - 15)
//    OR
//    /dev/ttyUSBx, (where x = 0 - 5)
//
// Use port number as index to _comports array.
int get_tty_portnumber(const char *portname)
{
  int nport = -1;
  const char *prefix = "/dev/tty";
  int len = strlen(prefix);

  while (!strncmp(portname, prefix, len))
  {
    char cc = portname[len];
    portname += len;
    len = strlen(portname);

    if (len > 1)
    {
      if (cc == 'S')
      {
        cc = portname[1];
        if (len < 3)
        {
          // Confirm portname has format /dev/ttySx, where 0 <= x < 10.

          if ((cc < '0') || (cc > '9')) break;
          nport = cc - '0';
        }
        else if ((len < 4) && (cc == '1'))
        {
          // Confirm portname has format /dev/ttySxx, where 9 < xx < 16.

          cc = portname[2];
          if ((cc < '0') || (cc > '5')) break;
          nport = 10 + cc - '0';
        }
      }
      else if (len > 3)
      {
        if ((portname[0] == 'U') && (portname[1] == 'S') && (portname[2] == 'B'))
        {
          // Confirm portname has format /dev/ttyUSBx, where 0 <= x < 6.

          cc = portname[3];
          if ((cc < '0') || (cc > '5')) break;
          nport = 16 + cc - '0';
        }
		if ((portname[0] == 'A') && (portname[1] == 'C') && (portname[2] == 'M'))
        {
          // Confirm portname has format /dev/ttyACMx, where 0 <= x < 6.

          cc = portname[3];
          if ((cc < '0') || (cc > '5')) break;
          nport = 22 + cc - '0';
        }
      }
    }
    break;
  }
  return(nport);
}

//=============================================================================
// Return port number for the specified port name, formatted as:
//    COMx, (where x = 0 - 16)
//
// Use port number as index to _comports array.
int get_com_portnumber(const char *portname)
{
  int nport = -1;
  int len = strlen(portname);

  while ( ( len > 3)
    &&  ( (portname[0] == 'C') || (portname[0] == 'c'))
    &&  ( (portname[1] == 'O') || (portname[1] == 'o'))
    &&  ( (portname[2] == 'M') || (portname[2] == 'm')))
  {
    char cc = portname[3];

    if (len < 5)
    {
      // Confirm portname has format COMx, where 0 < x < 10.

      if ((cc < '1') || (cc > '9')) break;
      nport = cc - '1';
      if      (cc == '1') nport = 0;
      else if (cc == '2') nport = 1;
      else if (cc == '3') nport = 2;
      else if (cc == '4') nport = 3;
      else if (cc == '5') nport = 4;
      else if (cc == '6') nport = 5;
      else if (cc == '7') nport = 6;
      else if (cc == '8') nport = 7;
      else if (cc == '9') nport = 8;
    }
    else if ((len < 6) && (cc == '1'))
    {
      // Confirm portname has format COMxx, where 9 < xx < 17.

      cc = portname[4];
      if ((cc < '0') || (cc > '6')) break;
      nport = 9 + cc - '0';
    }
    break;
  }
  return(nport);
}

//=============================================================================
int get_portnumber(const char *portname)
{
  int nport = get_tty_portnumber(portname);
  if (nport < 0) nport = get_com_portnumber(portname);
#ifdef TEST_GETCOMPORTNUMBER
printf("get_portnumber(%s)\t= %2d\n", portname, nport);
#endif
  return(nport);
}

//=============================================================================
#ifdef TEST_GETCOMPORTNUMBER
void sio_UnitTestGetComPortNumber()
{
  // bad format
  get_portnumber("/dev/ttyS-1");
  get_portnumber("/dev/tty");
  get_portnumber("/dev/ttyS16");
  get_portnumber("");
  get_portnumber("/dev/ttyUSB");
  get_portnumber("/dev/ttyUSBABC");
  get_portnumber("COM-1");
  get_portnumber("COM/");
  get_portnumber("COM17");
  get_portnumber("COM/");
  get_portnumber("COMABC");

  // good format
  get_portnumber("/dev/ttyS0");
  get_portnumber("/dev/ttyS1");
  get_portnumber("/dev/ttyS2");
  get_portnumber("/dev/ttyS3");
  get_portnumber("/dev/ttyS4");
  get_portnumber("/dev/ttyS5");
  get_portnumber("/dev/ttyS6");
  get_portnumber("/dev/ttyS7");
  get_portnumber("/dev/ttyS8");
  get_portnumber("/dev/ttyS9");
  get_portnumber("/dev/ttyS10");
  get_portnumber("/dev/ttyS11");
  get_portnumber("/dev/ttyS12");
  get_portnumber("/dev/ttyS13");
  get_portnumber("/dev/ttyS14");
  get_portnumber("/dev/ttyS15");
  get_portnumber("/dev/ttyUSB0");
  get_portnumber("/dev/ttyUSB1");
  get_portnumber("/dev/ttyUSB2");
  get_portnumber("/dev/ttyUSB3");
  get_portnumber("/dev/ttyUSB4");
  get_portnumber("/dev/ttyUSB5");
  get_portnumber("COM1");
  get_portnumber("COM2");
  get_portnumber("COM3");
  get_portnumber("COM4");
  get_portnumber("COM5");
  get_portnumber("COM6");
  get_portnumber("COM7");
  get_portnumber("COM8");
  get_portnumber("COM9");
  get_portnumber("COM10");
  get_portnumber("COM11");
  get_portnumber("COM12");
  get_portnumber("COM13");
  get_portnumber("COM14");
  get_portnumber("COM15");
  get_portnumber("COM16");
}
#endif
