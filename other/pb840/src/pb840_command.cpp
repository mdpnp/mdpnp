/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    pb840_command.cpp
 * @brief   Device driver for Puritan Bennett 840 ventilator.
 */
//=============================================================================
#include <string>
#include <iostream>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>

#include "pb840.h"

#ifdef  _WIN32
#include <windows.h>

#else
// Not Windows
#include <termios.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <limits.h>
#endif

#include "pb840_commandcfg.h"
#include "icexio.h"
#include "ice_time_utils.h"

using namespace std;

#define _CR   (0x0d)

pb840_commandcfg  _cfg; // Global configuration
static  char  *_sfilenamecfg  = "pb840_commandcfg.xml";
static  const int MESSAGE_END = _CR;
static  int  _idoportread  = 1;

static char  *_psthisprogramname;
static FILE *_fd;

static void show_data_as_hex(char *bytes, const int length);
static void millisleep(const int millisecs);

//=============================================================================
void millisleep(const int millisecs)
{
#ifdef  _WIN32
  Sleep(millisecs);
#else
  static  const  int  mega  = 1000 * 1000;
  struct timespec  tspec  = { millisecs / 1000, (millisecs % 1000) * mega };
  struct timespec  tspecdummy  = { 0 };
  nanosleep(&tspec, &tspecdummy);
#endif
}

//=============================================================================
void show_data_as_hex(char *bytes, const int length)
{
  int  ix  = 0;
  unsigned int  ilinecount  = 0;
  char  *pend  = bytes + length;

  while (bytes < pend)
  {
    // Not yet at the end of the data, so show another line of
    // hex encoded values

    // Show byte count in column 1
    printf("\n%04X\t", 16 * ilinecount++);

    // Show 8 bytes
    for (ix = 0; (ix < 8) && (bytes < pend); ix++)
    {    
      printf("%02X ", (unsigned int)*bytes);
      bytes++;
    }

    // Space character separates first 8 bytes from second 8 bytes
    printf(" ");

    // Show next 8 bytes
    for (ix = 0; (ix < 8) && (bytes < pend); ix++)
    {    
      printf("%02X ", (unsigned int)*bytes);
      bytes++;
    }
  }
}

//=============================================================================
//=============================================================================
class rs232io
{
private:
  // Disallow use of implicitly generated member functions:
  rs232io(const rs232io &src);
  rs232io &operator=(const rs232io &rhs);

protected:
  char _byte;     // the last byte read from serial port
  string  _iobuf; // the last complete msg read from serial port
  icexserial  *_pio;

public:

  //=============================================================================
  rs232io(icexserial  *pio)
    :  _pio(pio),
      _byte(0)
  {
  }

  //=============================================================================
  ~rs232io()
  {

  }

  //=============================================================================
  enum
  {
    rs232io_ok  = 0,
    rs232io_fail,
    rs232io_bufferoverrun,
    rs232io_stopsignal,
    rs232io_timeout,

  }  rs232iostatus;

  //=============================================================================
  const string *getiobuf() const {  return(&_iobuf);  }

  //=============================================================================
  void setiobuf(const char *sbuf)  {  _iobuf = sbuf;  }

  //=============================================================================
  int bytesxferredcount() const
  {
    return(_iobuf.length());
  }

  //=============================================================================
  int readbyte()
  {
    int bytecount = 0;
    int istat = _pio->readchar(&_byte, &bytecount); // read 1 byte
    _iobuf.append(1, (const char)_byte);
    return(istat);
  }

  //=============================================================================
  int  readrecord()
  {
    int bytecount = 0;
    int istat = icexio::icexio_ok;

    _iobuf.erase(0, _iobuf.length());
    istat = _pio->readchar(&_byte, &bytecount); // read 1 byte
    _iobuf.append(1, (const char)_byte);

    while (_byte != MESSAGE_END)
    {
      readbyte();
    }
    return(istat);
  }

  //=============================================================================
  int writerecord() const
  {
    int istat = _pio->writerecord(_iobuf.c_str(), _iobuf.length());
    return(istat);
  }
};

//=============================================================================
//=============================================================================
class pb840svr
{
private:
  // Disallow use of implicitly generated member functions:
  pb840svr(const pb840svr &src);
  pb840svr &operator=(const pb840svr &rhs);

protected:
  string  _sstatusmsg;

public:

  //=============================================================================
  pb840svr()
  {
  }

  //=============================================================================
  ~pb840svr()
  {
  }

  //=============================================================================
  int sendsampledata(rs232io *pio, const int icmd)
  {
    int istat = pb840::rstat_ok;
    pio->setiobuf(pb840::get_sample_data(icmd));
    istat = pio->writerecord();
    if (!pb840::rstatus_ok(istat))
    {
      cout << endl << "rs232io::writerecord failed (" << istat << ")" << endl;
    }
    return(istat);
  }

  //=============================================================================
  void runserver(rs232io *pio)
  {
    int icmd  = -1;
    int istat = pb840::rstat_ok;

    while(pio != 0)
    {
      cout << endl;

      // Read bytes
      pio->readrecord();

      switch(icmd = pb840::which_cmd(*(pio->getiobuf())))
      {
      case  pb840::cmd_rset:
        cout << "RSET command received. Do nothing." << endl;
        break;

      case  pb840::cmd_snda:
        cout << "SNDA command received. Send data ..." << endl;
        istat = sendsampledata(pio, icmd);
        break;

      case  pb840::cmd_sndf:
        cout << "SNDF command received. Send data ..." << endl;
        istat = sendsampledata(pio, icmd);
        break;

      default:
        cout << "Unknown command received:" << endl;
        show_data_as_hex(
          const_cast<char *>(pio->getiobuf()->c_str()),
          pio->getiobuf()->length());
        break;
      }
      millisleep(0);
    }
  }

  //=============================================================================
  void runclient(rs232io *pio)
  {
    static  int firsttime  = 1;
    string  *sbuf;
    int cc  = 0;
    int ichar  = 0;
    int icmd  = _cfg.getpbcommand();
    int istat = pb840::rstat_ok;
    unsigned long totalbytecount  = 0;
    time_t  tnow  = time(NULL);
    time_t  tstart  = tnow;
    time_t  tlast = tnow;
    time_t  tend  = tnow + _cfg.getsecondstorun();
    time_t  tsecondssofar  = 0;
    string  stimestamp;

    string  sfout(_psthisprogramname);
    sfout.append("_log.dat");

    _fd = fopen(sfout.c_str(), "wb");
    if (_fd == NULL)
    {
      cout << endl << "Failed to open output file " << sfout << endl;
      return;
    }
    cout << endl << "Save serial data to " << sfout << "." << endl;

    pio->setiobuf(pb840::get_cmd_str(pb840::cmd_rset));
    istat  = pio->writerecord();

    while((pio != 0) && (tnow < tend))
    {
      cout << endl;

      pio->setiobuf(pb840::get_cmd_str(icmd));
      istat  = pio->writerecord();

      // Read bytes
      pio->readrecord();
      sbuf  = const_cast<string *>(pio->getiobuf());
      cout << endl;
      cout << "Received " << sbuf->length() << " bytes: ";
      cout << sbuf->substr(0, 40) << endl;

if (firsttime)
{
  firsttime = 0;
  cout << *pio->getiobuf();
// show_data_as_hex(const_cast<char *>(pio->getiobuf()->c_str()), pio->getiobuf()->length());
}
      if (_fd)
      {
        int ilen  = pio->getiobuf()->length();

        stimestamp.clear();
        stimestamp = ice_time_utils::timenow_utc_string_microsec();
        stimestamp.append("Z");
        if (ilen > 0) stimestamp.append(",");
        fprintf(_fd, "%s", stimestamp.c_str());

        for (int ix = 0; ix < ilen; ix++)
        {
          ichar  = pio->getiobuf()->at(ix);
          if ((cc  = fputc(ichar, _fd)) == EOF)
          {
            cout << endl << "fputc error" << istat << endl << endl;
            break;
          }
          totalbytecount++;
        }
      }
      tnow  = time(NULL);
      tsecondssofar = tnow - tstart;
      if (tlast != tnow)
      {
        if (!(tsecondssofar % 10))
        {
          cout << "So far: " << tsecondssofar << " seconds, " << totalbytecount << " bytes." << endl;
        }
        tlast = tnow;
      }
      millisleep(0);
    }
    if (_fd != 0)  fclose(_fd);
    _fd = 0;
  }

//=============================================================================
void runwaveform()
{
  time_t tstart = 0;
  time_t tlast  = 0;
  time_t tend   = 0;
  time_t tnow   = time(NULL);
  time_t tsecondssofar = 0;
  unsigned long totalbytecount  = 0;
  int bytecount = 0;
  int istat = icexio::icexio_ok;
  int cc = 0;
  char ichar = 0;
  int gotendofrecord = 0;
  icexserial xsio;
  string stimestamp;

  string sfout(_psthisprogramname);
  sfout.append("_log.dat");

  _fd = fopen(sfout.c_str(), "wb");
  if (_fd == 0)
  {
    cout << endl << "Failed to open output file " << sfout << endl;
    return;
  }
  cout << endl << "Save serial data to " << sfout << "." << endl;

  cout << endl;
  cout << _psthisprogramname << " is running. Output to " << sfout << endl;
  cout << "Run for " << _cfg.getsecondstorun() << " seconds." << endl;
  cout << endl;

  // Show time with specified format
  printf(
    "\n%sZ read/write port %s\n\n",
    ice_time_utils::timenow_utc_string_second().c_str(),
    _cfg.getserialportname().c_str());

  // Setup serial i/o
  istat  = xsio.open();

  if (!icexio::status_ok(istat))
  {
    // Serial i/o setup failed
    cout << endl << "icexserial::open ERROR: " << xsio.getstatusmsg() << endl;
    return;
  }

  fprintf(_fd, "%sZ\n", ice_time_utils::timenow_utc_string_second().c_str());
  tnow  = time(NULL);
  tstart  = tnow;
  tlast = tnow;
  tend  = tnow + _cfg.getsecondstorun();

  while (tnow < tend)
  {
    ichar = 0;
    bytecount = 0;
    istat = xsio.readchar(&ichar, &bytecount); // read 1 byte
    if (istat != icexio::icexio_ok)
    {
      cout << endl << "icexio::readchar error " << istat << endl << endl;
      break;
    }

    if (bytecount > 0)
    {
      if (gotendofrecord)
      {
        gotendofrecord  = 0;

        stimestamp.clear();
        stimestamp = ice_time_utils::timenow_utc_string_microsec();
        stimestamp.append("Z");
        stimestamp.append(",");

        fprintf(_fd, "%s", stimestamp.c_str());
      }
      if ((cc  = fputc(ichar, _fd)) == EOF)
      {
        cout << endl << "fputc error" << istat << endl << endl;
        break;
      }
      if ((cc == '\x0a') || (cc == '\x0d'))
      {
        gotendofrecord  = 1;
      }
      totalbytecount += bytecount;
    }

    tnow  = time(0);
    tsecondssofar = tnow - tstart;
    if (tlast != tnow)
    {
      if (!(tsecondssofar % 10))
      {
        cout << "So far: " << tsecondssofar << " seconds, " << totalbytecount << " bytes." << endl;
      }
      tlast = tnow;
    }
  }

  fprintf(_fd, "\n%sZ\n", ice_time_utils::timenow_utc_string_second().c_str());

  if (xsio.isopen())  xsio.close();

  cout << endl << "Totals: " << tsecondssofar << " seconds, " << totalbytecount << " bytes." << endl;

  printf("\n%sZ Done\n", ice_time_utils::timenow_utc_string_second().c_str());

  if (_fd)  fclose(_fd);
  _fd = 0;
}

};

//=============================================================================
// Print help
//=============================================================================
void cmdlinehelp(int exitprogram)
{
  cout << endl;
  cout << "USAGE:" << endl;
  cout << 
    "   " << _psthisprogramname << " [args]" << endl;
  cout << endl;
  cout << "Optional [args]:" << endl;
  cout << "  -s  :  specify server mode for testing." << endl;
  cout << "  -w  :  get waveform data only." << endl;
  cout << endl;
  cout << "In (default) client mode, continuously write command to" << endl;
  cout << "PB840, get response and write it to file. The command to" << endl;
  cout << "write is specified by config file " << _sfilenamecfg << "." << endl;
  cout << endl;
  cout << "In server (-s) mode, read PB840 commands from serial" << endl;
  cout << "port specified by xml config file " << _sfilenamecfg << endl;
  cout << "and respond with (sample) data stream." << endl;
  cout << endl;
  cout << "In waveform (-w) mode, continuously read PB840 serial serial port" << endl;
  cout << "and write input to file." << endl;
  cout << endl;

  if (exitprogram)  exit(__LINE__);
}

//=============================================================================
int main(int argc, char *argv[])
{
  char *portname = 0;
  int  istat  = 0;
  pb840svr  pbsvr;
  icexserial  xsio;
  rs232io serio(&xsio);
  char  *psfncfg  = _sfilenamecfg;
  FILE  *fdcfg  = 0;

  _psthisprogramname  = argv[0];

  cmdlinehelp(0);

  // Load config file
  fdcfg  = fopen(psfncfg, "rb");
  if (fdcfg == NULL)
  {
    cout << endl << "Failed to open cfg file " << psfncfg << endl;
    return(__LINE__);
  }

  istat  = _cfg.load(fdcfg);
  fclose(fdcfg);
  fdcfg  = 0;
  if (!pb840_commandcfg::xmlcfgstatok(istat))
  {
    cout  << endl
        << "Error " << istat << " loading cfg file " << psfncfg
        << endl;
    return(__LINE__);
  }

  cout  << endl;
  cout  << _psthisprogramname << " is running." << endl;

  // Show UTC time
  printf("\n%sZ read/write port %s\n\n",
    ice_time_utils::timenow_utc_string_second().c_str(),
    _cfg.getserialportname().c_str());

  // Setup serial i/o
  istat  = xsio.open();

  if (!icexio::status_ok(istat))
  {
    // Serial i/o setup failed
    cout << endl << "icexserial::open ERROR: " << xsio.getstatusmsg() << endl;
    return(istat);
  }

  if (argc > 1)
  {
    if (  (strcmp(argv[1], "-s") == 0)
      ||  (strcmp(argv[1], "-S") == 0))
    {
      pbsvr.runserver(&serio);
    }

    if (  (strcmp(argv[1], "-w") == 0)
      ||  (strcmp(argv[1], "-W") == 0))
    {
      pbsvr.runwaveform();
    }
  }
  else
  {
    pbsvr.runclient(&serio);
  }

  if (xsio.isopen())  xsio.close();

  // Show time with specified format
  printf("\n%sZ Done\n", ice_time_utils::timenow_utc_string_second().c_str());

  if (_fd)  fclose(_fd);
  return(0);
}

