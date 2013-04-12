/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    poxrad_waveform.h
 * @brief   Manage data obtained from the analog port of Masimo Radical 5, 7
 *          pulse oximeter (POX).
 *
 * This manages raw data output from the device Analog Output port. It provides
 * two data channels- Analog1 and Analog2- that output waveform data at a rate
 * faster than we will ever want to sample it. The output signals vary from
 * approximately 0 to 1 volt in a linear fashion.

 * The operator may configure the device Output menu to output continuously to
 * Analog1 and Analog2 the measurements SpO2, pulse rate, pleth waveform or
 * Signal IQ:
 *
 * SpO2 0 - 100%
 * Scales the saturation measurement with 0% being equal to 0 volt and
 * 100% equal to 1 volt.
 *
 * SpO2 50 - 100%
 * Scales the saturation measurement with 50% being equal to 0 volt and
 * 100% equal to 1 volt.
 *
 * PULSE RATE
 * Scales the pulse rate measurement with 0 BPM being equal to 0 volt,
 * and 250 BPM equal to 1 volt.
 *
 * Pleth
 * Traces the plethysmographic waveform as shown on the Radical
 * display.
 *
 * Signal IQ
 * Traces the Signal IQ waveform as shown on the Radical display. A full
 * scale Signal IQ signal (100%) is represented as 1 volt, while a zero
 * Signal IQ signal (0%) is represented as 0 volt.
 *
 * 0V Output
 * A 0 volt calibration signal is mapped to the analog output. Use this
 * signal for calibration of recording devices. (0 volts represents a
 * saturation of 0% and a pulse rate of 0 bpm).
 *
 * 1V Output
 * A 1 volt calibration signal is mapped to the analog output. Use this
 * signal for calibration of recording devices. (1 volt represents a
 * saturation of 100% and a pulse rate of 250 bpm).
 *
 *
 * This uses the names "waveform" and "analog" interchangeably when refering to
 * data read from the analog port.
 */
//=============================================================================
#pragma once
#ifndef  POXRAD_WAVEFORM_H_
#define  POXRAD_WAVEFORM_H_

#include  <string>
#include  <vector>
#include  <cstdlib>

typedef  struct waveformrec
{
  float  analog1;
  float  analog2;

}  waveformrec, *pwaveformrec;

//=============================================================================
class poxrad_waveform
{
private:
// Disallow use of implicitly generated member functions:
// poxrad_waveform(const poxrad_waveform &src);
// poxrad_waveform &operator=(const poxrad_waveform &rhs);

protected:
  std::vector <waveformrec> _wfrecs;
  int  _ireccount;

public:

//=============================================================================
  poxrad_waveform()
    :  _ireccount(0)
  {
  }

//=============================================================================
  ~poxrad_waveform()
  {
    _wfrecs.clear();
  }

//=============================================================================
  poxrad_waveform(const poxrad_waveform &src)
    :  _ireccount(src._ireccount)
  {
    _wfrecs = src._wfrecs;
  }

//=============================================================================
  poxrad_waveform &operator=(const poxrad_waveform &rhs)
  {
    if (this != &rhs)
    {
      _ireccount = rhs._ireccount;
      _wfrecs = rhs._wfrecs;
    }
    return(*this);
  }

//=============================================================================
  inline void clear()
  {
    _ireccount = 0;
  }

//=============================================================================
  void update(const poxrad_waveform &wf)
  {
    clear();
    _ireccount  = wf._ireccount;
    _wfrecs     = wf._wfrecs;
  }

//=============================================================================
  inline int size()
  {
    return(_ireccount);
  }

//=============================================================================
  inline void add(float fanalog1, float fanalog2)
  {
    waveformrec  wfr = { fanalog1, fanalog2 };
    if (_ireccount < static_cast<int>(_wfrecs.size()))
    {
      _wfrecs[_ireccount] = wfr;
    }
    else
    {
      _wfrecs.push_back(wfr);
    }
    _ireccount++;

//std::cout << "add analog " << _ireccount << std::endl;
  }

//=============================================================================
  inline waveformrec get(int iindex)
  {
    waveformrec  wfr = { 0 };
    if (iindex < _ireccount) wfr = _wfrecs[iindex];
    return(wfr);
  }

#if 0
//=============================================================================
  inline void add(char *sanalog1, char *sanalog2)
  {
    float fanalog1  = static_cast<float>(atof(sanalog1));
    float fanalog2 = static_cast<float>(atof(sanalog2));
    add(fanalog1, fanalog2);
  }

//=============================================================================
  inline int get(pwaveformrec pwfr, int iindex)
  {
    int  iret = 0;
    if (iindex < _ireccount)
    {
      if (iindex < _ireccount) *pwfr = _wfrecs[iindex];
      iret = 1;
    }
    return(iret);
  }

//=============================================================================
  inline int get(float *panalog1, float *panalog2, int iindex)
  {
    int  iret = 0;
    if (iindex < _ireccount)
    {
      waveformrec  wfr = _wfrecs[iindex];
      *panalog1 = wfr.analog1;
      *panalog2 = wfr.analog2;
      iret = 1;
    }
    return(iret);
  }

//=============================================================================
  inline int get(char *psanalog1, char *psanalog2, int iindex)
  {
    int  iret = 0;
    if (iindex < _ireccount)
    {
      waveformrec  wfr = _wfrecs[iindex];
      sprintf(psanalog1, "%08.6f", static_cast<double>(wfr.analog1));
      sprintf(psanalog2, "%08.6f", static_cast<double>(wfr.analog2));
      iret = 1;
    }
    return(iret);
  }
#endif

};

#endif
