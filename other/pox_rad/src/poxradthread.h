/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    poxradthread.h
 * @brief   Handle threads for Masimo Radical POX device driver.
 *
 * Use the Boost portable C++ source libraries to implement thread control.
 *
 * @see www.boost.org
 */
//=============================================================================
#pragma once
#ifndef  POXRADTHREAD_H_
#define  POXRADTHREAD_H_

#include <boost/date_time/posix_time/posix_time_types.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>

class devcomm;
class comm_rs232;
class comm_analog;
class poxrad_waveform;
class poxrad_data;
class poxrad_waveform;

//=============================================================================
/**
 * Handle data recorded from serial and analog threads.
 * Control serial and sender locks.
 */
class radpoxdatastage
{
private:
// Disallow use of implicitly generated member functions:
  radpoxdatastage(const radpoxdatastage &src);
  radpoxdatastage &operator=(const radpoxdatastage &rhs);

protected:

public:
  radpoxdatastage();
  ~radpoxdatastage();

  void setdiscrete(const char *sbuf);
  void setwaveform(
    const poxrad_waveform &wf,
    const boost::posix_time::ptime &timebase);

  boost::condition  _readytosend;
  boost::mutex    _mutexsend;

  boost::condition  _readyserial;
  boost::mutex    _mutexserial;

  poxrad_data  *_poxraddata;

  boost::posix_time::ptime  _timebase;
  boost::posix_time::ptime  _msgtime;

  std::string  _rawserialstring;

};

//=============================================================================
//=============================================================================
/**
 * Parent class of serial analog and sender thread classes.
 */
class poxradthread
{
private:
// Disallow use of implicitly generated member functions:
  poxradthread(const poxradthread &src);
  poxradthread &operator=(const poxradthread &rhs);

public:

  enum
  {
    tstate_idle  = 0,
    tstate_starting,
    tstate_running,
    tstate_stopping,

  }  threadstate;

protected:
  boost::thread  _thread;
  int  _tstate;
  std::string  _sstatusmsg;
  int  _istat;

  enum  { _iserialiobufsize = 1024 };

public:
  poxradthread();
  virtual ~poxradthread();

  inline bool isstate(const int ist) const { return(ist == _tstate); }
  inline int getstate() const { return(_tstate); }
  inline void setstate(const int ist) { _tstate = ist; }

  virtual void start() = 0;
  virtual void stop();
  void join();
  void detach();

};

//=============================================================================
/**
 * poxradrs232thread class.
 * Read and process discrete data from serial port.
 */
class  poxradrs232thread : public poxradthread
{
private:
// Disallow use of implicitly generated member functions:
  poxradrs232thread(const poxradrs232thread &src);
  poxradrs232thread &operator=(const poxradrs232thread &rhs);
  poxradrs232thread();

  char    _sbuf[_iserialiobufsize];
  int     _update_period_millisec;  // Rate at which to update host in milliseconds
  double  _samples_per_sec;

  int dowork();

public:
  poxradrs232thread(comm_rs232 *dc);
  ~poxradrs232thread();

  void start();

  inline int get_update_period_millisec() const { return(_update_period_millisec); }
  inline double get_samples_per_sec() const { return(_samples_per_sec); }

  inline void set_update_period_millisec(const int xx) { _update_period_millisec = xx; }
  inline void set_samples_per_sec(const double xx) { _samples_per_sec = xx; }

  inline char const *getbuf() const { return(_sbuf); }

  comm_rs232 *_devcomm;

};

//=============================================================================
//=============================================================================
/**
 *  Read and process waveform data from analog port(s).
 */
class poxradanalogthread : public poxradthread
{
private:
// Disallow use of implicitly generated member functions:
  poxradanalogthread(const poxradanalogthread &src);
  poxradanalogthread &operator=(const poxradanalogthread &rhs);
  poxradanalogthread();

  int dowork();

private:
  int _update_period_millisec;  // Rate at which to update host in milliseconds
  double _samples_per_sec;

public:
  poxradanalogthread(comm_analog *dc);
  ~poxradanalogthread();

  void start();

  inline int get_update_period_millisec() const { return(_update_period_millisec); }
  inline double get_samples_per_sec() const { return(_samples_per_sec); }

  inline void set_update_period_millisec(const int xx) { _update_period_millisec = xx; }
  inline void set_samples_per_sec(const double xx) { _samples_per_sec = xx; }

  comm_analog *_devcomm;

};

//=============================================================================
//=============================================================================
/**
 * poxradsenderthread class
 * Make the XML message and send it someplace.
 */
class poxradsenderthread : public poxradthread
{
private:
// Disallow use of implicitly generated member functions:
  poxradsenderthread(const poxradsenderthread &src);
  poxradsenderthread &operator=(const poxradsenderthread &rhs);

  int dowork();

public:
  poxradsenderthread();
  ~poxradsenderthread();

  void start();

};

#endif
