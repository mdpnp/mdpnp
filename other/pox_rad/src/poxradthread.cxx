/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    poxradthread.cxx
 * @brief   Handle threads for Masimo Radical POX device driver.
 *
 * Use the Boost portable C++ source libraries to implement thread control.
 *
 * @see www.boost.org
 */
//=============================================================================
#include <iostream>
#include "devcomm.h"
#include "poxrad_data.h"
#include "poxradthread.h"

// define these when you want to write samples to raw data log file
//#define  LOGFILE_SERIAL  ("_pox_rad_data_log_serial.txt")
//#define  LOGFILE_ANALOG  ("_pox_rad_data_log_analog.txt")

using namespace std;

static const unsigned int _mega = 1000 * 1000;

static radpoxdatastage  _datastage;

static long sample_period_microsecs(const double samples_per_sec);

//=============================================================================
long sample_period_microsecs(const double samples_per_sec)
{
  long ival = 0;
  if (samples_per_sec > 0.0) ival = static_cast<long>(_mega / samples_per_sec);
  return(ival);
}

//=============================================================================
//=============================================================================
/**
 * raw_data_log class
 *
 * Maintain one log file and one backup. When size of current log file exceeds
 * _maxfilesize it becomes backup.
 */

class raw_data_log
{
private:
// Disallow use of implicitly generated member functions:
raw_data_log(const raw_data_log &src);
raw_data_log &operator=(const raw_data_log &rhs);
raw_data_log();

public:
std::string _filename;
std::string _backup_filename;
std::string _header;
long _maxfilesize;

//=============================================================================
raw_data_log(const std::string &filename, const std::string &header)
  : _filename(filename),
    _backup_filename(filename + ".bak"),
    _header(header),
    _maxfilesize(1 * _mega)
{
}

//=============================================================================
~raw_data_log()
{
}

//=============================================================================
/**
 * Open file for write, erase existing content.
 * Write header with no timestamp.
 * Close file.
 */
int init_log()
{
  int rc = -1; // error
  FILE *fd = fopen(_filename.c_str(), "wb");
  if (fd)
  {
    if (fprintf(fd, "%s", _header.c_str()) >= 0) rc = 0;
    fclose(fd);
  }
  return rc;
}

//=============================================================================
/**
 * Open file for write, preserve existing content.
 * If file size exceeds max:
 *  - close file
 *  - delete backup file
 *  - rename file to backup fname
 *  - open file for write
 *
 * Write sbuf with no timestamp.
 * Close file.
 */
int write_log(const char *sbuf)
{
  int rc = -1; // error
  FILE *fd = fopen(_filename.c_str(), "ab");
  while (fd)
  {
    long isize = 0;
    boost::posix_time::ptime tnow =
      boost::posix_time::microsec_clock::universal_time();

    int icount = fprintf(
      fd,
      "%sZ,%s",
      (boost::posix_time::to_iso_string(tnow)).c_str(),
      sbuf);

    if (icount < 0)
    {
      // Failed to write
      fclose(fd);
      break;
    }

    fflush(fd);
    isize = ftell(fd);
    fclose(fd);

    if (isize > _maxfilesize)
    {
      // Size of current log file exceeds max.

      // Delete the backup.
      remove(_backup_filename.c_str());

      // Rename current log file to backup
      if (!rename(_filename.c_str(), _backup_filename.c_str()))
      {
        init_log();
      }
    }
    rc = 0;
    break;
  }
  return rc;
}

};

//=============================================================================
//=============================================================================
/**
 * radpoxdatastage class
 */

//=============================================================================
radpoxdatastage::radpoxdatastage()
  : _poxraddata(0)
{
  _poxraddata = new poxrad_data;
  _timebase = boost::posix_time::microsec_clock::universal_time();
  _msgtime  = _timebase;
}

radpoxdatastage::~radpoxdatastage()
{
  if (_poxraddata) delete _poxraddata;
}

void radpoxdatastage::setdiscrete(const char *sbuf)
{
  boost::mutex::scoped_lock  lock(_mutexsend);
  _poxraddata->_discrete.update(_rawserialstring);
  _rawserialstring = sbuf;
  _readytosend.notify_all();
}

void radpoxdatastage::setwaveform(
  const poxrad_waveform &wf,
  const boost::posix_time::ptime &timebase)
{
  boost::mutex::scoped_lock  lock(_mutexserial);
  _msgtime = _timebase;
  _timebase = boost::posix_time::microsec_clock::universal_time();
  _poxraddata->_waveform.update(wf);
  _readyserial.notify_all();
}

//=============================================================================
//=============================================================================
/**
 * poxradthread base class.
 */

//=============================================================================
poxradthread::poxradthread()
  :  _tstate(tstate_idle),
    _istat(devcomm::devcomm_rc_ok)
{
}

poxradthread::~poxradthread()
{
  if (isstate(tstate_running)) stop();
}

void poxradthread::stop()
{
  setstate(tstate_stopping);
  _thread.interrupt();
}

void poxradthread::join()
{
  _thread.join();
}

void poxradthread::detach()
{
  _thread.detach();
}

//=============================================================================
//=============================================================================
/**
 * poxradrs232thread class
 */

//=============================================================================
int poxradrs232thread::dowork()
{
  const char *sfn = "poxradrs232thread::dowork";
  int isecondscount = 0;

  // Setup serial i/o

  _istat = _devcomm->open();

  if (!devcomm::result_code_ok(_istat))
  {
    // Serial i/o setup failed
    _sstatusmsg = sfn;
    _sstatusmsg.append(": ERROR: ");
    _sstatusmsg.append(_devcomm->getstatusmsg());
    cerr << endl << _sstatusmsg << endl;
    setstate(tstate_stopping);
    return(_istat);

    return(_istat);
  }

#ifdef  LOGFILE_SERIAL
  raw_data_log *rdlog =
    new raw_data_log(LOGFILE_SERIAL, "Timestamp,Serial data stream\r\n");
  rdlog->init_log();
#endif

  setstate(tstate_running);

  try
  {
    while(1)
    {
      // Wait until ready to get serial data.

      boost::mutex::scoped_lock lock(_datastage._mutexserial);
      _datastage._readyserial.wait(lock);
      memset(_sbuf, 0, sizeof(_sbuf));
      _istat = _devcomm->readrecord(
        reinterpret_cast<unsigned char *>(_sbuf),
        _iserialiobufsize,
        poxrad_discrete::get_string_terminator());

#ifdef  LOGFILE_SERIAL
      if (rdlog) rdlog->write_log(_sbuf);
#endif
      _datastage.setdiscrete(_sbuf);

      isecondscount++;
      if (!(isecondscount % 60))
        cout << "Running for " << isecondscount << " secs so far." << endl;
    }
  }
  catch(boost::thread_interrupted&)
  {
    _istat = devcomm::devcomm_rc_stopsignal;
  }

#ifdef  LOGFILE_SERIAL
  if (rdlog) delete rdlog;
#endif
  setstate(tstate_stopping);
  return(_istat);
}

//=============================================================================
poxradrs232thread::poxradrs232thread(comm_rs232 *dc)
  : poxradthread(),
    _update_period_millisec(1000),
    _samples_per_sec(1),
    _devcomm(dc)
{
  memset(_sbuf, 0, sizeof(_sbuf));
}

poxradrs232thread::~poxradrs232thread()
{
  if (isstate(tstate_running))  stop();
  if (_devcomm->isopen())  _devcomm->close();
}

void poxradrs232thread::start()
{
  setstate(tstate_starting);
  _thread = boost::thread(&poxradrs232thread::dowork, this);
}

//=============================================================================
//=============================================================================
/**
 *  poxradanalogthread class
 */

//=============================================================================
int poxradanalogthread::dowork()
{
  const char *sfn = "poxradanalogthread::dowork";
  long ix = 0;
  long samplecount = 0;
  const long samples_per_update =
    static_cast<long>(_samples_per_sec * _update_period_millisec / 1000);
  const long samples_per_update_less_1 = samples_per_update - 1;
  char  sbuf[64] = { 0 };
  float fval[2] = { 0 };
  poxrad_waveform _wf;

  boost::posix_time::time_duration dura_sample_period =
    boost::posix_time::microseconds(
    sample_period_microsecs(_samples_per_sec));

  boost::posix_time::ptime next_sample_time;

#if 1
cout << "analog _samples_per_sec:          " << _samples_per_sec << endl;
cout << "analog _update_period_millisec:   " << _update_period_millisec << endl;
cout << "analog samples_per_update:        " << samples_per_update << endl;
#endif

  // Setup the analog i/o
  _istat = _devcomm->open();

  if (!devcomm::result_code_ok(_istat))
  {
    // Analog i/o setup failed
    _sstatusmsg = sfn;
    _sstatusmsg.append(": ERROR: ");
    _sstatusmsg.append(_devcomm->getstatusmsg());
    cerr << endl << _sstatusmsg << endl;
    setstate(tstate_stopping);
    return(_istat);
  }

#ifdef  LOGFILE_ANALOG
  raw_data_log *rdlog =
    new raw_data_log(LOGFILE_ANALOG, "Timestamp,Analog1,Analog2\r\n");
  rdlog->init_log();
#endif

  setstate(tstate_running);

  _datastage._timebase = boost::posix_time::microsec_clock::universal_time();
  _datastage._msgtime = _datastage._timebase;
  next_sample_time = _datastage._timebase;

  try
  {
    while(1)
    {
      next_sample_time = next_sample_time + dura_sample_period;
      boost::thread::sleep(next_sample_time);

// TODO:  Handle errors
      if (_wf.size() > samples_per_update)
      {
//          _istat = devcomm::devcomm_rc_bufferoverrun;
        _wf.clear();
      }

      _istat = _devcomm->readchannel( ix, &fval[0]);

      if (devcomm::result_code_ok(_istat))
        _istat = _devcomm->readchannel(ix, &fval[1]);

      if (!devcomm::result_code_ok(_istat))
      {
        _sstatusmsg = _devcomm->getstatusmsg();
        break;
      }

      _wf.add(fval[0], fval[1]);

#ifdef  LOGFILE_ANALOG
      sprintf(sbuf, "%08.6f,%08.6f\r\n", fval[0], fval[1]);
      if (rdlog) rdlog->write_log(sbuf);
#endif

      samplecount++;
      if (samplecount > samples_per_update_less_1)
      {
        _datastage.setwaveform(_wf, next_sample_time);
        _wf.clear();
        samplecount = 0;
      }
    }
  }
  catch(boost::thread_interrupted&)
  {
    //_istat = devcomm::devcomm_rc_stopsignal;
  }

  _devcomm->close();

#ifdef  LOGFILE_ANALOG
  if (rdlog) delete rdlog;
#endif
  setstate(tstate_stopping);
  return(_istat);
}

//=============================================================================
poxradanalogthread::poxradanalogthread(comm_analog *dc)
  : poxradthread(),
    _update_period_millisec(1000),
    _samples_per_sec(500),
    _devcomm(dc)
{
}

poxradanalogthread::~poxradanalogthread()
{
  if (isstate(tstate_running)) stop();
  if (_devcomm->isopen()) _devcomm->close();
}

void poxradanalogthread::start()
{
  setstate(tstate_starting);
  _thread = boost::thread(&poxradanalogthread::dowork, this);
}

//=============================================================================
//=============================================================================
/**
 * poxradsenderthread class
 */

//=============================================================================
int poxradsenderthread::dowork()
{
  static bool isfirsttime = true;  // Throw away the first one

  _istat = 0;
  setstate(tstate_running);

  while(1)
  {
    // Wait until ready to send.
    boost::mutex::scoped_lock  lock(_datastage._mutexsend);
    _datastage._readytosend.wait(lock);

    if (isfirsttime)
    {
      // The first time, do nothing.
      isfirsttime = false;
      continue;
    }

    cout << endl
         << boost::posix_time::to_iso_extended_string(_datastage._msgtime)
         << "Z" << endl;
    cout << "Analog1, Analog2 waveforms sample count: "
         << _datastage._poxraddata->_waveform.size() << endl;
    cout << "serial data:" << endl;
    cout << _datastage._poxraddata->_discrete.GetRawDataString() << endl;
  }
  setstate(tstate_stopping);
  return(_istat);
}

poxradsenderthread::poxradsenderthread()
  : poxradthread()
{
}

poxradsenderthread::~poxradsenderthread()
{
  if (isstate(tstate_running)) stop();
}

void poxradsenderthread::start()
{
  setstate(tstate_starting);
  _thread = boost::thread(&poxradsenderthread::dowork, this);
}
