/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    poxrad_data.h
 * @brief   Manage raw data obtained from Masimo Radical 5, 7 pulse oximeter.
 *
 * This manages raw data output from two different device i/o ports: a serial
 * port and an analog port. The device writes new data to the serial port at a
 * rate of 1-Hz. The analog port provides two data channels- Analog1 and
 * Analog2- that output waveform data at a rate faster than we will ever want
 * to sample it. The operator may configure the device Output menu to 
 * output continuously to Analog1 and Analog2 the measurements SpO2, pulse
 * rate, pleth waveform or Signal IQ.
 *
 * This uses the names "serial" and "discrete" interchangeably when refering to
 * data read from the serial port. This uses the names "waveform" and "analog"
 * interchangeably when refering to data read from the analog port.
 */
//=============================================================================
#pragma once
#ifndef  POXRAD_DATA_H_
#define  POXRAD_DATA_H_

#include  "poxrad_discrete.h"
#include  "poxrad_waveform.h"

using namespace std;

//=============================================================================
class poxrad_data
{
private:
// Disallow use of implicitly generated member functions:
// poxrad_data(const poxrad_data &src);
// poxrad_data &operator=(const poxrad_data &rhs);

public:

  poxrad_discrete  _discrete;
  poxrad_waveform  _waveform;

//=============================================================================
~poxrad_data() { }

//=============================================================================
poxrad_data() { }

//=============================================================================
poxrad_data(const poxrad_data &src)
{
  _discrete  = src._discrete;
  _waveform  = src._waveform;
}

//=============================================================================
poxrad_data &operator=(const poxrad_data &rhs)
{
  if (this != &rhs)
  {
    _discrete  = rhs._discrete;
    _waveform  = rhs._waveform;
  }
  return(*this);
}

//=============================================================================
/**
 * Clear all local data buffers
 */
void clear()
{
  _discrete.clear();
  _waveform.clear();
}

};

#endif
