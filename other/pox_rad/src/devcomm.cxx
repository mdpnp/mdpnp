/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    devcomm.cxx
 * @brief   Handle device raw data i/o for serial and analog ports.
 */
//=============================================================================
#include "projstd.h"
#include <sstream>
#include <string>
#include <iostream>
#include <cmath>
#include <time.h>
#include <errno.h>
#include "dd_pox_rad.h"
#include "Property.h"
#include "devcomm.h"

using namespace std;

#ifndef STRNCASECMP
#ifdef _WIN32
#define STRNCASECMP   _strnicmp
#else
#define STRNCASECMP   strncasecmp
#endif
#endif

#define IS_OPTION(str, option) (STRNCASECMP(str, option, strlen(str)) == 0)

//=============================================================================
// sample_file object
//=============================================================================
class sample_file
{
private:
//  Disallow use of implicitly generated member functions:
  sample_file(const sample_file &src);
  sample_file &operator=(const sample_file &rhs);
  sample_file();

public:

string _sstatusmsg;
string _filename;
FILE *_fd;

// Input file position indicator
fpos_t _fpos_start;

//=============================================================================
sample_file(std::string filename)
  : _filename(filename),
    _fd(0)
{
  memset(&_fpos_start, 0, sizeof(_fpos_start));
}

//=============================================================================
~sample_file() { close(); }

//=============================================================================
inline std::string get_sstatusmsg() { return _sstatusmsg; }

//=============================================================================
int open()
{
  const char *sfn = "sample_file::open";
  int rc = devcomm::devcomm_rc_fail;

  while(1)
  {
    if (_fd)
    {
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Failed, already open ");
      _sstatusmsg.append(_filename);
      break;
    }

    _fd  = fopen(_filename.c_str(), "rb");
    if (!_fd)
    {
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Failed to open ");
      _sstatusmsg.append(_filename);
      break;
    }

    // Remember start position of data input files so we can return later.
    fgetpos(_fd, &_fpos_start);
    rc = devcomm::devcomm_rc_ok;
    break;
  }
  return(rc);
}

//=============================================================================
// Rewind the input file
inline void rewind() { fsetpos(_fd, &_fpos_start); }

//=============================================================================
int close()
{
  int rc = devcomm::devcomm_rc_ok;
  if (_fd != 0) fclose(_fd);
  _fd  = 0;
  memset(&_fpos_start, 0, sizeof(_fpos_start));
  return(rc);
}

//=============================================================================
inline bool isopen() const { return(_fd != 0); }

};

//=============================================================================
// serial_sample_file object
//=============================================================================
class serial_sample_file : public sample_file
{
private:
//  Disallow use of implicitly generated member functions:
  serial_sample_file(const serial_sample_file &src);
  serial_sample_file &operator=(const serial_sample_file &rhs);
  serial_sample_file();

public:

//=============================================================================
serial_sample_file(const std::string &filename)
  :  sample_file(filename)
{
}

//=============================================================================
int readrecord(
  unsigned char *sbuf,
  const unsigned int ibuffersize,
  const int terminator)
{
  const char *sfn = "serial_sample_file::readrecord";
  int rc = devcomm::devcomm_rc_ok;
  string sstr;

  while(1)
  {
    int  ival = 0;
    int  ipos = 0;
    char cc = '\0';

    if (!sbuf || (ibuffersize < 2))
    {
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Error: invalid argument");
      rc = devcomm::devcomm_rc_fail;
      break;
    }

    do
    {
      // Clear control chars

      ival  = fread(&cc, 1, 1, _fd);
      if (ival < 1)
      {
        _sstatusmsg = sfn;
        _sstatusmsg.append(" Error: fread");
        rc = devcomm::devcomm_rc_fail;
        break;
      }
    }  while (cc < ' ');

    while(devcomm::result_code_ok(rc))
    {
      // Read characters from current record

      sstr.append(1, cc);

      if (cc == terminator) break;

      ival = fread(&cc, 1, 1, _fd);
      if (ival < 1)
      {
        _sstatusmsg = sfn;
        _sstatusmsg.append(" Error: fread");
        rc = devcomm::devcomm_rc_fail;
        break;
      }
    }

    if (!devcomm::result_code_ok(rc))
    {
      // Read error

      if (feof(_fd))
      {
        // End of file error. Rewind the file and try again.
        // RECURSION ALERT!
        rewind();
        rc = readrecord(sbuf, ibuffersize, terminator);
      }
      break;
    }

    strncpy(reinterpret_cast<char *>(sbuf), sstr.c_str(), ibuffersize);
    sbuf[ibuffersize - 1]  = '\0';
    break;
  }
  return(rc);
}

};

//=============================================================================
// analog_sample_file object
//=============================================================================
class analog_sample_file : public sample_file
{
private:
//  Disallow use of implicitly generated member functions:
  analog_sample_file(const analog_sample_file &src);
  analog_sample_file &operator=(const analog_sample_file &rhs);
  analog_sample_file();

  enum { max_sample_file_channels_default = 2 };
  float _fval[max_sample_file_channels_default];

public:

//=============================================================================
analog_sample_file(const std::string filename)
  : sample_file(filename)
{
  memset(_fval, 0, sizeof(_fval));
}

//=============================================================================
int readchannel(const int channel, float *pval)
{
  const char *sfn = "analog_sample_file::readchannel";
  int rc = devcomm::devcomm_rc_fail;

  while (1)
  {
    int ival = 0;

    if ((channel < 0) || (channel >= max_sample_file_channels_default))
    {
      _sstatusmsg  = sfn;
      _sstatusmsg.append(" Error: invalid channel");
      break;
    }

    if (!pval)
    {
      _sstatusmsg  = sfn;
      _sstatusmsg.append(" Error: invalid pval arg");
      break;
    }

    rc = devcomm::devcomm_rc_ok;

    if (!channel)
    {
      // Read new set of values when channel 0 specified
      for (int ix = 0; ix < max_sample_file_channels_default; ix++)
      {
        ival = fscanf(_fd, "%f", &_fval[ix]);
        if (ival == EOF)
        {
          rc = devcomm::devcomm_rc_eof;
          break;
        }
      }
    }

    if (devcomm::result_code_ok(rc))
    {
      *pval = _fval[channel];
    }
    else if (rc == devcomm::devcomm_rc_eof)
    {
      // End of file error. Rewind the file and try again.
      // RECURSION ALERT!
      rewind();
      rc = readchannel(channel, pval);
    }
    break;
  }
  return(rc);
}

};

//=============================================================================
// devcomm object
//=============================================================================
devcomm::devcomm()
  : _isopen(false)
{
}

//=============================================================================
devcomm::~devcomm() { if (isopen()) close(); }

//=============================================================================
int devcomm::close()
{
  int  rc = devcomm_rc_ok;
  if (isopen()) _isopen = false;
  return(rc);
}

//=============================================================================
int devcomm::open()
{
  int rc = devcomm_rc_ok;

  if (!isopen())
  {
    _isopen = true;
  }
  else
  {
    _sstatusmsg =  "devcomm::open error. Already open.";
    rc = devcomm_rc_alreadyopen;
  }
  return(rc);
}

//=============================================================================
bool devcomm::isopen() const { return(_isopen); }

//=============================================================================
// comm_rs232 object
//=============================================================================
#ifdef  _WIN32
// Windows world

#include <conio.h>
#include <stdio.h>
#else
// Not Windows world

#include <stdio.h>  /* Standard input/output definitions */
#include <string.h>  /* String function definitions */
#include <unistd.h>  /* UNIX standard function definitions */
#include <fcntl.h>  /* File control definitions */
#include <termios.h>  /* POSIX terminal control definitions */
#include <sys/select.h>

#ifndef  INVALID_HANDLE_VALUE
#define  INVALID_HANDLE_VALUE  (-1)
#endif
#endif

#ifdef  _WIN32
const string comm_rs232::_default_portname("COM1");
#else
const string comm_rs232::_default_portname("/dev/ttyS0");
#endif

//=============================================================================
comm_rs232::comm_rs232()
  :  devcomm(),
    _hfile(INVALID_HANDLE_VALUE),
    _isysererrno(0),
    _ibytesxferredcount(0),
    _portname(_default_portname),
    _baudrate(9600),
    _databits(8),
    _stopbits(0),   // 0 - 1 stop bit, 1 - 1.5 stop bits, 2 - 2 stop bits
    _handshaking(0),
    _parity("none")
{
#ifndef  _WIN32
  memset(&_saveoptions, 0, sizeof(_saveoptions));
#endif
}

//=============================================================================
comm_rs232::~comm_rs232() { close(); }

//=============================================================================
void comm_rs232::print_cmd_line_help()
{
  const char *usage_string =
    "  -portname <name>     - Set serial port name,\n"
    "                         default /dev/ttyS0 (linux) or COM1 (windows)\n"
    "  -baudrate <rate>     - Set baud rate, default 9600\n"
    "  -databits <count>    - Set data bits, default 8\n"
    "  -stopbits <0|1|2>    - Set stop bits: 0 - 1 stop bit, 1 - 1.5 stop\n"
    "                         bits, 2 - 2 stop bits, default 0\n"
    "  -parity <none|even|odd|space|mark>\n"
    "                       - Set parity, default none\n"
    "  -handshaking <val>   - Set handshaking, default 0\n"
    ;
  fprintf(stdout, "%s", usage_string);
}

//=============================================================================
int comm_rs232::parse_config(int argc, char *argv[])
{
  const char *sfn = "comm_rs232::parse_config";
  int rc = devcomm_rc_ok;

  // first, scan for cfgFile, help
  for (int ixx = 1; ixx < argc; ++ixx)
  {
    if (IS_OPTION(argv[ixx], "-cfgFile"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        _sstatusmsg = sfn;
        _sstatusmsg.append(" Missing <fileName> after -cfgFile");
        rc = devcomm_rc_fail;
        break;
      }
      _cfgFile = argv[ixx];
    }
  }

  // load configuration values from config file
  while (_cfgFile.length())
  {
    CfgDictionary *cfgsource;

    try
    {
      cfgsource = new CfgDictionary(_cfgFile);
    }
    catch (std::logic_error e)
    {
      _sstatusmsg.erase();
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Problem loading cfg file. ");
      _sstatusmsg.append(e.what());
      rc = devcomm_rc_fail;
      break;
    }

    CfgProfile *cfg = cfgsource->get("serial_comm");

    if (!cfg)
    {
      _sstatusmsg.erase();
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Failed to find section [serial_comm] in file ");
      _sstatusmsg.append(_cfgFile);
      rc = devcomm_rc_fail;
      break;
    }

    _portname = cfg->get_string("port name", _portname);
    _baudrate = cfg->get_int("baud rate", _baudrate);
    _databits = cfg->get_int("data bits", _databits);
    _stopbits = cfg->get_int("stop bits", _stopbits);
    _handshaking = cfg->get_int("handshaking", _handshaking);
    _parity   = cfg->get_string("parity", _parity);
    rc = devcomm_rc_ok;
    break;
  }
  return(rc);
}

//=============================================================================
int comm_rs232::initialize(int argc, char *argv[])
{
  int rc = parse_config(argc, argv);
  return(rc);
}

//=============================================================================
bool comm_rs232::isopen() const { return(_hfile == INVALID_HANDLE_VALUE); }

//=============================================================================
void comm_rs232::clearcommerror()
{
#ifdef  _WIN32
  DWORD   errors = 0;
  COMSTAT stat = { 0 };
  ClearCommError(_hfile, &errors, &stat);
#endif
}

//=============================================================================
#ifndef  _WIN32
// Not-Windows baudrate constants

speed_t comm_rs232::baudrate(const int ibaud)
{
  speed_t  ispeed = B0;
  switch(ibaud)
  {
    case  0:  ispeed = B0;
    break;
    case  50:  ispeed = B50;
    break;
    case  75:  ispeed = B75;
    break;
    case  110:  ispeed = B110;
    break;
    case  134:  ispeed = B134;
    break;
    case  150:  ispeed = B150;
    break;
    case  200:  ispeed = B200;
    break;
    case  300:  ispeed = B300;
    break;
    case  600:  ispeed = B600;
    break;
    case  1200:  ispeed = B1200;
    break;
    case  1800:  ispeed = B1800;
    break;
    case  2400:  ispeed = B2400;
    break;
    case  4800:  ispeed = B4800;
    break;
    case  9600:  ispeed = B9600;
    break;
    case  19200:  ispeed = B19200;
    break;
    case  38400:  ispeed = B38400;
    break;
    case  57600:  ispeed = B57600;
    break;
    case  115200:  ispeed = B115200;
    break;
    case  230400:  ispeed = B230400;
    break;
    default:  ispeed = B0;
    break;
  }
  return(ispeed);
}
#endif

//=============================================================================
int comm_rs232::close()
{
  int  rc = devcomm_rc_ok;
  if (isopen())
  {
#ifdef  _WIN32
    CloseHandle(_hfile);
#else
    // Set options to original
    tcsetattr(_hfile, TCSANOW, &_saveoptions);
    ::close(_hfile);
#endif
    _hfile = INVALID_HANDLE_VALUE;
  }
  return(rc);
}

//=============================================================================
int comm_rs232::open()
{
  if (isopen())
  {
    _sstatusmsg =  "comm_rs232::open error. Already open.";
    return(devcomm_rc_alreadyopen);
  }

#ifdef  _WIN32
// Windows rs232 i/o

  _hfile = CreateFileA(
    _portname.c_str(),
    GENERIC_READ | GENERIC_WRITE,
    0,    // exclusive access
    0,    // no security
    OPEN_EXISTING,
    0,    // no overlapped i/o
    0);   // NULL template

  if (_hfile == INVALID_HANDLE_VALUE)
  {
    LPVOID  pmsgbuf = 0;
    char  serrbuf[256] = "";
    _isysererrno = GetLastError();
    sprintf(
      serrbuf,
      "comm_rs232::open(%s) CreateFile error %d. ",
      _portname.c_str(),
      _isysererrno);

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER
      | FORMAT_MESSAGE_FROM_SYSTEM
      | FORMAT_MESSAGE_IGNORE_INSERTS,
      0,
      _isysererrno,
      MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
      (char *)&pmsgbuf,
      0, 0);

    _sstatusmsg =  serrbuf;
    _sstatusmsg  +=  (char *)pmsgbuf;
    LocalFree(pmsgbuf);
    return(devcomm_rc_fail);
  }

  DCB  dcb = { 0 };
  COMMTIMEOUTS  tmo = { 0 };

  // Set up comm 9600 baud, 8 bit, 1 stop, no parity
  GetCommState(_hfile, &dcb);
  dcb.BaudRate = _baudrate;
  dcb.ByteSize = _databits;
  dcb.StopBits = _stopbits;

  if (_parity.compare("none") == 0)
  {
    dcb.Parity = NOPARITY;
  }
  else if (_parity.compare("even") == 0)
  {
    dcb.Parity = EVENPARITY;
  }
  else if (_parity.compare("odd") == 0)
  {
    dcb.Parity = ODDPARITY;
  }
  else if (_parity.compare("space") == 0)
  {
    dcb.Parity = SPACEPARITY;
  }
  else if (_parity.compare("mark") == 0)
  {
    dcb.Parity = MARKPARITY;
  }

  // Setting fAbortOnError to 0 fixes a weird problem: After 30
  // writes to the serial port, the 31st write fails with this error
  // message:
  //
  //   "The I/O operation has been aborted because of either a thread
  //   exit or an application request." (error code 995)
  //
  // I found a fix here:
  // http://zachsaw.blogspot.com/2010/07/net-serialport-woes.html
  //
  dcb.fAbortOnError = 0;

/*
// set XON/XOFF
dcb.fOutX = FALSE; // XON/XOFF off for transmit
dcb.fInX = FALSE; // XON/XOFF off for receive

// set RTSCTS
dcb.fOutxCtsFlow = TRUE; // turn on CTS flow control
dcb.fRtsControl = RTS_CONTROL_HANDSHAKE; //

// set DSRDTR
dcb.fOutxDsrFlow = FALSE; // turn on DSR flow control
dcb.fDtrControl = DTR_CONTROL_ENABLE; //
// dcb.fDtrControl = DTR_CONTROL_DISABLE; //
// dcb.fDtrControl = DTR_CONTROL_HANDSHAKE; //
*/

  // set RTSCTS
  dcb.fOutxCtsFlow = TRUE; // turn on CTS flow control
  dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;

  if (!SetCommState(_hfile, &dcb))
  {
    _sstatusmsg =  "comm_rs232::open failed on SetCommState()";
    return(devcomm_rc_fail);
  }

  // Set comm timeout values
  tmo.ReadIntervalTimeout = 50;    // MAXDWORD;
  tmo.ReadTotalTimeoutMultiplier = 10;
  tmo.ReadTotalTimeoutConstant = 300;

  tmo.WriteTotalTimeoutMultiplier = 10;
  tmo.WriteTotalTimeoutConstant = 400;
  if (!SetCommTimeouts(_hfile, &tmo))
  {
    _sstatusmsg =  "comm_rs232::open failed on SetCommTimeouts()";
    return(devcomm_rc_fail);
  }
#else
// Not-Windows serial i/o

  struct termios options = { 0 };
  speed_t tbaud = baudrate(_baudrate);

  _hfile = ::open(
    _portname.c_str(),
    O_RDWR | O_NOCTTY | O_NDELAY);
  if (_hfile < 0)
  {
    char  sbuf[128] = "";
    _hfile = INVALID_HANDLE_VALUE;
    _isysererrno = errno;
    sprintf(
      sbuf,
      "comm_rs232::open(%s) ERROR %d: ",
      _portname.c_str(),
      _isysererrno);
    _sstatusmsg = sbuf;
    _sstatusmsg  += strerror(errno);
    return(devcomm_rc_fail);
  }

//   fcntl(_hfile, F_SETFL, 0);    // Block read/write
  fcntl(_hfile, F_SETFL, FNDELAY);  // Return immediately upon read/write

  tcgetattr(_hfile, &options);
  _saveoptions = options;

  cfsetispeed(&options, tbaud);
  cfsetospeed(&options, tbaud);

  options.c_cflag |= (CLOCAL | CREAD);  // enable

  // Parity

  int  iparitybitcheckandstrip = 1;
  if (_parity.compare("none") == 0)
  {
    iparitybitcheckandstrip = 0;
    options.c_cflag &= ~PARENB;
  }
  else if (_parity.compare("even") == 0)
  {
    options.c_cflag &= ~PARENB;
    options.c_cflag &= ~PARODD;
  }
  else if (_parity.compare("odd") == 0)
  {
    options.c_cflag |= PARENB;
    options.c_cflag |= PARODD;
  }
  else if (_parity.compare("space") == 0)
  {
    options.c_cflag &= ~PARENB;
  }

  if (iparitybitcheckandstrip != 0)
  {
    // Eable checking and stripping of the parity bit
    options.c_iflag |= (INPCK | ISTRIP);
  }

  // Stop bits

  if (_stopbits == 2)
  {
    options.c_cflag |= CSTOPB;
  }
  else
  {
    options.c_cflag &= ~CSTOPB;
  }

  // Byte size

  options.c_cflag &= ~CSIZE;
  if (_databits == 8)  options.c_cflag |= CS8;
  if (_databits == 7)  options.c_cflag |= CS7;
  if (_databits == 6)  options.c_cflag |= CS6;
  if (_databits == 5)  options.c_cflag |= CS5;

  // Enable hardware flow control
  options.c_cflag |= CRTSCTS;  // Also called CNEW_RTSCTS

  // Disable hardware flow control
//   options.c_cflag &= ~CRTSCTS;  // Also called CNEW_RTSCTS

  // Disable software flow control
  options.c_iflag &= ~(IXON | IXOFF | IXANY);

/*
From  http://unixwiz.net/techtips/termios-vmin-vtime.html

VMIN and VTIME defined

VMIN is a character count ranging from 0 to 255 characters, and VTIME is
time measured in 0.1 second intervals, (0 to 25.5 seconds). The value of
"zero" is special to both of these parameters, and this suggests four
combinations discussed below. In every case, the question is when a read()
system call is satisfied. Here is the read() prototype call:

int n = read(fd, buffer, nbytes);

The tty driver maintains an input queue of bytes already
read from the serial line and not passed to the user, so not every read()
call waits for actual I/O - the read may very well be satisfied directly
from the input queue.

VMIN = 0 and VTIME = 0
This is a completely non-blocking read - the call is satisfied
immediately directly from the driver's input queue. If data are available,
they transfer to the caller's buffer up to nbytes and return.
Otherwise zero is immediately returned to indicate "no data". Note that
this is "polling" of the serial port, and it's almost always a bad idea.
If done repeatedly, it can consume enormous amounts of processor time and
is highly inefficient. Don't use this mode unless you really, really know
what you're doing.

VMIN = 0 and VTIME > 0
This is a pure timed read. If data are available in the input queue, it's
transferred to the caller's buffer up to a maximum of nbytes, and returned
immediately to the caller. Otherwise the driver blocks until data arrive,
or when VTIME tenths expire from the start of the call. If the timer
expires without data, zero is returned. A single byte is sufficient to
satisfy this read call, but if more is available in the input queue, it's
returned to the caller. Note that this is an overall timer, not an
intercharacter one.

VMIN > 0 and VTIME > 0
A read() is satisfied when either VMIN characters have been transferred to
the caller's buffer, or when VTIME tenths expire between characters. Since
this timer is not started until the first character arrives, this call can
block indefinitely if the serial line is idle. This is the most common
mode of operation, and we consider VTIME to be an intercharacter timeout,
not an overall one. This call should never return zero bytes read.

VMIN > 0 and VTIME = 0
This is a counted read that is satisfied only when at least VMIN
characters have been transferred to the caller's buffer - there is no
timing component involved. This read can be satisfied from the driver's
input queue (where the call could return immediately), or by waiting for
new data to arrive. In this respect the call could block indefinitely.
Consider the behavior undefined when nbytes is less than VMIN.
*/

  options.c_cc[VTIME] = 1;
  options.c_cc[VMIN] = 1;

  // Flush the line
  tcflush(_hfile, TCIFLUSH);

  // Set all options
  tcsetattr(_hfile, TCSANOW, &options);

#endif
  return(devcomm_rc_ok);
}

//=============================================================================
void comm_rs232::flush()
{
#ifdef  _WIN32
  PurgeComm(
    _hfile,
    PURGE_RXCLEAR | PURGE_TXCLEAR | PURGE_RXABORT | PURGE_TXABORT);
#else
  tcflush(_hfile, TCIFLUSH);
#endif
}

//=============================================================================
int comm_rs232::writerecord(const unsigned char *sbuf, const unsigned int ibytestoxfer)
{
#ifdef  _WIN32
// Windows write

  int rc = devcomm_rc_ok;
  DWORD ibytesxferred = 0;

  _ibytesxferredcount = 0;

  while((_ibytesxferredcount < ibytestoxfer) && (devcomm::result_code_ok(rc)))
  {
    if (WriteFile(
          _hfile,
          &sbuf[_ibytesxferredcount],
          ibytestoxfer - _ibytesxferredcount,
          &ibytesxferred,
          0))
    {
      _ibytesxferredcount  += ibytesxferred;
    }
    else
    {
      _isysererrno =  ::GetLastError();
      if (_isysererrno != ERROR_IO_PENDING)
      {
        LPVOID  pmsgbuf = 0;
        char  serrbuf[128] = "";

        FormatMessage(
            FORMAT_MESSAGE_ALLOCATE_BUFFER
          | FORMAT_MESSAGE_FROM_SYSTEM
          | FORMAT_MESSAGE_IGNORE_INSERTS,
          0,
          _isysererrno,
          MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
          (char *)&pmsgbuf,
          0, 0);

        sprintf(serrbuf, "WriteFile error %d. ", _isysererrno);
        _sstatusmsg =  serrbuf;
        _sstatusmsg.append((char *)pmsgbuf);
        LocalFree(pmsgbuf);
        rc = devcomm_rc_fail;
      }
    }
  }
#else
// Not-Windows write

  int  rc = devcomm_rc_ok;
  int  ibytesxferred = 0;

  _ibytesxferredcount = 0;

  while((_ibytesxferredcount < ibytestoxfer) && (devcomm::result_code_ok(rc)))
  {
    ibytesxferred = ::write(_hfile, sbuf, ibytestoxfer);
    if (ibytesxferred < 0)
    {
      rc = devcomm_rc_fail;
      _isysererrno = errno;
      _sstatusmsg = "::write error: ";
      _sstatusmsg  +=  strerror(_isysererrno);
cout  << endl << __FILE__ << ", line no. " << __LINE__
  << " ::write error " << _isysererrno
  << ". " << strerror(_isysererrno) << endl;
exit(__LINE__);
    }
    else
    {
      _ibytesxferredcount  += ibytesxferred;
    }
  }
#endif
  return(rc);
}

//=============================================================================
int comm_rs232::readrecord(
  unsigned char *sbuf,
  const unsigned int ibuffersize,
  const int terminator)
{
  int rc = devcomm_rc_ok;
  int itimeoutct = 0;
  static const int imaxtimeouts = 1;
  unsigned int ibytesread = 0;

  _ibytesxferredcount = 0;

  while((_ibytesxferredcount < ibuffersize) && result_code_ok(rc))
  {
    rc = readchar(&sbuf[_ibytesxferredcount], &ibytesread);

    if (!result_code_ok(rc))  break;

    _ibytesxferredcount  += ibytesread;

    if (_ibytesxferredcount > 1)
    {
      if (sbuf[_ibytesxferredcount - 1] == terminator)
      {
        // This input stream contains the specified terminator. Done for now.
        break;
      }
    }
    if (ibytesread < 1)
    {
      if (++itimeoutct > imaxtimeouts)
      {
        _sstatusmsg = "comm_rs232::readrecord error: Timeout";
        rc = devcomm_rc_timeout;
      }
    }
  }
  if( _ibytesxferredcount != 0)
  {
    sbuf[_ibytesxferredcount] = '\0';
  }
  return(rc);
}

//=============================================================================
int comm_rs232::readchar(
  unsigned char *psbuf,
  unsigned int *pbytesxferredcount,
  const unsigned int ibytestoxfer)
{
  int rc = devcomm_rc_ok;

  *pbytesxferredcount = 0;
#ifdef  _WIN32
// Windows read

  DWORD bytesread = 0;
  if (ReadFile(_hfile, psbuf, ibytestoxfer, &bytesread, 0) < 1)
  {
    _isysererrno =  ::GetLastError();
    if (_isysererrno != ERROR_IO_PENDING)
    {
      char  serrbuf[128] = "";
      LPVOID  pmsgbuf = 0;

      FormatMessage(
          FORMAT_MESSAGE_ALLOCATE_BUFFER
        | FORMAT_MESSAGE_FROM_SYSTEM
        | FORMAT_MESSAGE_IGNORE_INSERTS,
        0,
        _isysererrno,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (char *)&pmsgbuf,
        0, 0);

      sprintf(serrbuf, "ReadFile error %d. ", _isysererrno);
      _sstatusmsg =  serrbuf;
      _sstatusmsg  +=  (char *)pmsgbuf;
      LocalFree(pmsgbuf);
      rc = devcomm_rc_fail;
    }
  }
  *pbytesxferredcount = bytesread;
#else
// Not Windows read
  int  ibytesread = ::read(_hfile, psbuf, ibytestoxfer);
  //cout << dec << ibytesread << endl;
  if (ibytesread < 1)
  {
    if (errno != EAGAIN)
    {
      _isysererrno = errno;
      _sstatusmsg = "::read error: ";
      _sstatusmsg  +=  strerror(_isysererrno);
      rc = devcomm_rc_fail;
    }
  }
  else
  {
    *pbytesxferredcount = ibytesread;
  }
#endif
  return(rc);
}

//=============================================================================
// rs232_sim object
//=============================================================================
rs232_sim::rs232_sim(const char *sample_string)
  : _sample_string(sample_string)
{
}

//=============================================================================
rs232_sim::~rs232_sim() { }

//=============================================================================
int rs232_sim::close() { return devcomm::close(); }

//=============================================================================
int rs232_sim::open() { return devcomm::open(); }

//=============================================================================
bool rs232_sim::isopen() const { return devcomm::isopen(); }

//=============================================================================
int rs232_sim::readrecord(
  unsigned char *sbuf,
  const unsigned int ibuffersize,
  const int terminator)
{
  const char *sfn = "rs232_sim::readrecord";
  int rc = devcomm_rc_ok;

  if (sbuf && _sample_string.length() < ibuffersize)
  {
    strcpy(reinterpret_cast<char *>(sbuf), _sample_string.c_str());
    rc = devcomm_rc_ok;
  }
  else
  {
    _sstatusmsg = sfn;
    _sstatusmsg.append(" Supplied buffer too small");
    rc = devcomm_rc_bufferoverrun;
  }
  return(rc);
}

//=============================================================================
// rs232_file_sim object
//=============================================================================
rs232_file_sim::rs232_file_sim(std::string &filename)
{
  _samplefile = new serial_sample_file(filename);
}

//=============================================================================
rs232_file_sim::~rs232_file_sim() { if (isopen()) close(); }

//=============================================================================
int rs232_file_sim::close()
{
  int rc = _samplefile->close();
  return(rc);
}

//=============================================================================
int rs232_file_sim::open()
{
  int rc = _samplefile->open();
  if (!result_code_ok(rc)) _sstatusmsg = _samplefile->get_sstatusmsg();
  return(rc);
}

//=============================================================================
bool rs232_file_sim::isopen() const { return(_samplefile->isopen()); }

//=============================================================================
int rs232_file_sim::readrecord(
  unsigned char *sbuf,
  const unsigned int ibuffersize,
  const int terminator)
{
  int rc = _samplefile->readrecord(sbuf, ibuffersize, terminator);
  if (!result_code_ok(rc)) _sstatusmsg = _samplefile->get_sstatusmsg();
  return(rc);
}

//=============================================================================
std::string rs232_file_sim::get_filename() const { return(_samplefile->_filename); }

//=============================================================================
void rs232_file_sim::set_filename(const std::string &ss) { _samplefile->_filename = ss; }

//=============================================================================
// comm_analog object
//=============================================================================
#ifndef SIMULATE_DIO
// This build will link with DIO/pcmmio analog port interface library.

extern "C"
{
#include "mio_io.h"
}

#else
//=============================================================================
// DIO/pcmmio analog port interface library unavailable.
// Satisfy references to MIO objects with fake stuff.

#define ADC_SINGLE_ENDED 0x80
#define ADC_UNIPOLAR  0x08
#define ADC_TOP_5V    0x00

static char  mio_error_string[128] = "This is error msg";
static int mio_error_code  = 0;

static int adc_set_channel_mode(
  int dev_num,
  int channel,
  int input_mode,
  int duplex,
  int range)
{
  return(0);
}

static float adc_get_channel_voltage(int dev_num, int channel)
{
  return(0);
}
#endif

//=============================================================================
comm_analog::comm_analog()
  :  devcomm(),
    _deviceNumber(0)
{
  for (int ix = 0; ix < max_channels_default; ix++)
  {
    _channel_number.push_back(ix);
  }
}

//=============================================================================
comm_analog::~comm_analog() { _channel_number.clear(); }

//=============================================================================
void comm_analog::print_cmd_line_help()
{
  const char *usage_string =
    "  -deviceNumber <num>  - Set A-D device number, default 0\n"
    "  -analog1ChannelNum <pcmmio channel num>\n"
    "                       - Set pcmmio channel num to read Analog1 output,\n"
    "                         default 0\n"
    "  -analog2ChannelNum <pcmmio channel num>\n"
    "                       - Set pcmmio channel num to read Analog2 output,\n"
    "                         default 1\n"
    ;
  fprintf(stdout, "%s", usage_string);
}

//=============================================================================
int comm_analog::parse_config(int argc, char *argv[])
{
  const char *sfn = "comm_analog::parse_config";
  int rc = devcomm_rc_ok;

  // first scan for cfgFile
  for (int ixx = 1; ixx < argc; ++ixx)
  {
    if (IS_OPTION(argv[ixx], "-cfgFile"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        _sstatusmsg = sfn;
        _sstatusmsg.append(" Missing <fileName> after -cfgFile");
        rc = devcomm_rc_fail;
        break;
      }
      _cfgFile = argv[ixx];
    }
  }

  // load configuration values from config file
  while (_cfgFile.length())
  {
    CfgDictionary *cfgsource;

    try
    {
      cfgsource = new CfgDictionary(_cfgFile);
    }
    catch (std::logic_error e)
    {
      _sstatusmsg.erase();
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Problem loading cfg file. ");
      _sstatusmsg.append(e.what());
      rc = devcomm_rc_fail;
      break;
    }

    CfgProfile *cfg = cfgsource->get("analog_comm");

    if (!cfg)
    {
      _sstatusmsg.erase();
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Failed to find section [analog_comm] in file ");
      _sstatusmsg.append(_cfgFile);
      rc = devcomm_rc_fail;
      break;
    }

    _deviceNumber = cfg->get_int("device number", _deviceNumber);
    _channel_number[0] = cfg->get_int("Analog1 channel", _channel_number[0]);
    _channel_number[1] = cfg->get_int("Analog2 channel", _channel_number[1]);
    rc = devcomm_rc_ok;
    break;
  }
  return(rc);
}

//=============================================================================
int comm_analog::initialize(int argc, char *argv[])
{
  int rc = parse_config(argc, argv);
  return(rc);
}

//=============================================================================
int comm_analog::open()
{
  const char *sfn = "comm_analog::open";
  int ival = 0;
  int rc = devcomm_rc_ok;

  if (isopen())
  {
    _sstatusmsg = sfn;
    _sstatusmsg.append(" Error: already open.");
    return(devcomm_rc_alreadyopen);
  }

  for (size_t ix = 0; ix < _channel_number.size(); ix++)
  {
    ival = adc_set_channel_mode(
      _deviceNumber,
      _channel_number[ix],
      ADC_SINGLE_ENDED, ADC_UNIPOLAR, ADC_TOP_5V);

    if (ival)
    {
      rc  = devcomm_rc_fail;
      _sstatusmsg = sfn;
      _sstatusmsg.append(" Error: ");
      _sstatusmsg.append(mio_error_string);
      break;
    }
  }
  if (result_code_ok(rc))  devcomm::open();
  return(rc);
}

//=============================================================================
int comm_analog::close() { return::devcomm::close(); }

//=============================================================================
bool comm_analog::isopen() const { return::devcomm::isopen(); }

//=============================================================================
int comm_analog::readchannel(int ichannel, float *pval)
{
  int  rc = devcomm_rc_ok;
  *pval = adc_get_channel_voltage(_deviceNumber, _channel_number[ichannel]);

  if (mio_error_code)
  {
    ostringstream oss;
    oss << "comm_analog::readchannel("
        << _channel_number[ichannel] << ", ) error. "
        << mio_error_string;
    _sstatusmsg = oss.str();
    rc = devcomm_rc_fail;
  }
  return(rc);
}

//=============================================================================
// analog_sim object
//=============================================================================
analog_sim::analog_sim() { }

//=============================================================================
analog_sim::~analog_sim() { }

//=============================================================================
int analog_sim::close() { return devcomm::close(); }

//=============================================================================
int analog_sim::open() { return devcomm::open(); }

//=============================================================================
bool analog_sim::isopen() const { return devcomm::isopen(); }

//=============================================================================
int analog_sim::readchannel(int ad_channel, float *pval)
{
  static const double pi = 3.14;
  static double theta = 0;
  double dval = sin(theta);
  *pval = static_cast<float>(dval);
  theta += 0.002;
  if (theta > pi) theta = 0;
  return(devcomm_rc_ok);
}

//=============================================================================
// analog_file_sim object
//=============================================================================
analog_file_sim::analog_file_sim(std::string &filename)
{
  _samplefile = new analog_sample_file(filename);
}

//=============================================================================
analog_file_sim::~analog_file_sim()
{
  if (isopen()) close();
}

//=============================================================================
int analog_file_sim::close()
{
  int rc = _samplefile->close();
  return(rc);
}

//=============================================================================
int analog_file_sim::open()
{
  int rc = _samplefile->open();
  if (!result_code_ok(rc)) _sstatusmsg = _samplefile->get_sstatusmsg();
  return(rc);
}

//=============================================================================
bool analog_file_sim::isopen() const { return(_samplefile->isopen()); }

//=============================================================================
int analog_file_sim::readchannel(int ad_channel, float *pval)
{
  int rc = _samplefile->readchannel(ad_channel, pval);
  if (!result_code_ok(rc)) _sstatusmsg = _samplefile->get_sstatusmsg();
  return(rc);
}

//=============================================================================
std::string analog_file_sim::get_filename() const { return(_samplefile->_filename); }

//=============================================================================
void analog_file_sim::set_filename(const std::string &ss) { _samplefile->_filename = ss; }
