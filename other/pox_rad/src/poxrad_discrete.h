/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    poxrad_discrete.h
 * @brief   Manage data obtained from Masimo Radical 5, 7 POX serial port.
 *
 * The device sends ASCII text data to the serial interface at a rate of 1-Hz.
 * The data include: date, time, SpO2, pulse rate, PI, and alarm and exception
 * values. Additionally, the Radical-7 ASCII data will include SPCO, SPMET,
 * DESAT, PIDELTA and PVI. All text is single line followed by line-feed,
 * carriage-return.
 *
 * This provides the following functions to return example serial data strings:
 *
 *  poxrad_discrete::get_radical5_examplestring()
 *  poxrad_discrete::get_radical7_examplestring()
 *
 * This uses the names "serial" and "discrete" interchangeably when refering to
 * data read from the serial port.
 */
//=============================================================================
#pragma once
#ifndef  POXRAD_DISCRETE_H_
#define  POXRAD_DISCRETE_H_

#include <string>
#include <ctime>
#include <string.h>

// Radical-7 example input string
static const char *_radical7_examplestring =
  "12/01/05 11:56:29 SN=0123456789 SPO2=098% BPM=240 PI=20.00% "
  "SPCO=01.0% SPMET=00.5% DESAT=01 PIDELTA=-02 PVI=027 ALARM=00"
  "00 EXC=000820\x0d\x0a";
// 012345678901234567890123456789012345678901234567890123456789

// Radical-5 example input string
static const char *_radical5_examplestring =
  "09/15/10 15:10:29 SPO2=097% BPM=064 PI=11.82% ALARM=20 EXC=800\x0d\x0a";
// 0123456789012345678901234567890123456789012345678901234567890123

//=============================================================================
class poxrad_discrete
{
private:
// Disallow use of implicitly generated member functions:
// poxrad_discrete(const poxrad_discrete &src);
// poxrad_discrete &operator=(const poxrad_discrete &rhs);

  std::string _srawdata;

  // Pointer to array of field values parsed from input stream.
  // The serialdataindex enumerators below specify array element indexes.
  std::string *_pserialdatavals;

public:

// Indexes to data contained in serial stream buffer.
// This is a superset and covers both Radical 5 and 7.
enum serialdataindex
{
  sdi_date,   // string, radical 5, and radical 7
  sdi_time,   // string, 5, 7
  sdi_sn,     // string, 7
  sdi_spo2,   // int, 5, 7
  sdi_bpm,    // int, 5, 7
  sdi_pi,     // fp, 5, 7
  sdi_spco,   // fp, 7
  sdi_spmet,  // fp, 7
  sdi_desat,  // int, 7
  sdi_pidelta,  // int, 7
  sdi_pvi,      // int, 7
  sdi_alarm,    // bitfield, 5, 7
  sdi_exc,      // bitfield, 5, 7

  sdi_lastinlist  // Ensure this one is always last in list
};

//=============================================================================
~poxrad_discrete()
{
  delete [] _pserialdatavals;
}

//=============================================================================
poxrad_discrete()
  : _pserialdatavals(0)
{
  _pserialdatavals = new std::string [sdi_lastinlist];
}

//=============================================================================
poxrad_discrete(const poxrad_discrete &src)
  : _pserialdatavals(0)
{
  _srawdata = src._srawdata;

  if (src._pserialdatavals)
  {
    _pserialdatavals = new std::string[sdi_lastinlist];
    for (int ix = 0; ix < static_cast<int>(sdi_lastinlist); ix++)
    {
      _pserialdatavals[ix] = src._pserialdatavals[ix];
    }
  }
}

//=============================================================================
poxrad_discrete &operator=(const poxrad_discrete &rhs)
{
  if (this == &rhs)  return(*this);

  _srawdata = rhs._srawdata;

  if (rhs._pserialdatavals)
  {
    _pserialdatavals = new std::string[sdi_lastinlist];
    for (int ix = 0; ix < static_cast<int>(sdi_lastinlist); ix++)
    {
      _pserialdatavals[ix] = rhs._pserialdatavals[ix];
    }
  }
  return(*this);
}

//=============================================================================
/**
 * Clear all local data buffers
 */
void clear()
{
  _srawdata.erase();

  for (int ix = 0; ix < static_cast<int>(sdi_lastinlist); ix++)
  {
    _pserialdatavals[ix].erase();
  }
}

//=============================================================================
/**
 * Parse data elements from the input buffer and store them as
 * strings in the array pointed to by _pserialdatavals.
 */
int update(const std::string &srawdata)
{
  int istat = 1;   // Assume bad

  // Clear all local data buffers
  clear();

  if (has_date_and_time(srawdata))
  {
    // The input string very probably has date and time vals, at least.

    // Get the field values as strings
    for (int ix = 0; ix < sdi_lastinlist; ix++)
    {
      _pserialdatavals[ix] = getfieldvaluestring(ix, srawdata);
    }

    _srawdata = srawdata;  // Keep a copy of the data stream
    istat = 0;  // Success.
  }
  return(istat);
}

//=============================================================================
static int get_string_terminator() { return(0x0a); }

//=============================================================================
inline std::string GetRawDataString() const { return(_srawdata); }

//=============================================================================
/**
 * If the input buffer contains a valid Radical data and time string, return
 * non-zero. Otherwise, return 0.
 *
 * This checks only the date and time fields, the first two fields
 * of the string.
 */
bool has_date_and_time(const std::string &srawdata) const
{
  bool  isok = false;  // Assume not ok.
  while(srawdata.length())
  {
    if (srawdata.length() < 17)  break;  // Too short.

    if (  (srawdata.at(2) != '/')
      ||  (srawdata.at(5) != '/')
      ||  (srawdata.at(8) != ' ')
      ||  (srawdata.at(11) != ':')
      ||  (srawdata.at(14) != ':'))  break;  // Format???

    isok = true;
    break;
  }
  return(isok);
}

//=============================================================================
/**
 * If the specified field is available, return true.
 * Otherwise, return false.
 */
bool isavailable(const int ifieldtag) const
{
  bool bret = 0;  // Assume not avail.
  while(1)
  {
    // Does the specified field index make sense?
    if ((ifieldtag < 0) || (ifieldtag >= sdi_lastinlist))  break;

    // Is the field available?
    if (_pserialdatavals[ifieldtag].length() < 2)  break;

    // The device indicates "unavailable" values by filling the
    // field with '-'. Check the field's second character for the
    // unavailable character (values may have a sign symbol (+=) as
    // the first character).
    if (_pserialdatavals[ifieldtag][1] == '-')  break;
    bret = true;
    break;
  }
  return(bret);
}

//=============================================================================
inline std::string getfieldvalue(const int ifieldtag) const
{
  return(_pserialdatavals[ifieldtag]);
}

//=============================================================================
/**
 * Get the label for the field specified by ifieldtag.
 */
static char *getfieldtag(const int ifieldtag)
{
  char *fl = 0;

  switch(ifieldtag)
  {
  case sdi_sn:      fl = "SN=";       break;
  case sdi_spo2:    fl = "SPO2=";     break;
  case sdi_bpm:     fl = "BPM=";      break;
  case sdi_pi:      fl = "PI=";       break;
  case sdi_spco:    fl = "SPCO=";     break;
  case sdi_spmet:   fl = "SPMET=";    break;
  case sdi_desat:   fl = "DESAT=";    break;
  case sdi_pidelta: fl = "PIDELTA=";  break;
  case sdi_pvi:     fl = "PVI=";      break;
  case sdi_alarm:   fl = "ALARM=";    break;
  case sdi_exc:     fl = "EXC=";      break;
  default:          fl = "";          break;
  }
  return(fl);
}

//=============================================================================
/**
 * Get from the specified raw data stream the value of field specified
 * by ifieldtag.
 */
std::string getfieldvaluestring(int ifieldtag, const std::string &srawdata)
{
  std::string  sval;
  size_t  ilen = srawdata.length();

  if (ilen)
  {
    std::string sfieldtag(getfieldtag(ifieldtag));

    while(sfieldtag.length())
    {
      // Found the tag associated with the specified field.
      // Get the field value.
      char ichar = 0;
      size_t ipos = srawdata.find(sfieldtag);

      if (ipos == std::string::npos)  break;

      ipos += sfieldtag.length();

      // Don't know how the device handles a signed value such as PIDELTA.
      // If negative, the first byte of the value field will be '-'. But what
      // about a positive signed value? Will the first byte be '+' or ' '?
      // This checks for the possibility the byte is ' ', which otherwise
      // indicates the end-of-field.
      if (srawdata.at(ipos) == ' ') ipos++;

      for (; ipos < ilen; ipos++)
      {
        ichar = srawdata.at(ipos);

        // Check for end of field
        if (  (ichar == '%')
          ||  (ichar == ' ')
          ||  (ichar == '\t'))  break;

        // Check for end of record
        if (  (ichar == '\0')   /* Note: this one should never happen. */
          ||  (ichar == '\x0D')
          ||  (ichar == '\x0A'))  break;

        // This char is part of the field's value
        sval.append(1, ichar);
      }
      break;
    }
  }
  return(sval);
}

//=============================================================================
/**
 * Return text message associated with specified alarm status code.
 */
char *alarmmessage(int icode) const
{
  char *smsg = 0;

  switch(icode)
  {
  case 0x0000:  smsg = "Normal operation, no alarms active";    break;
  case 0x0001:  smsg = "SPO2 High Alarm Limit violated";        break;
  case 0x0002:  smsg = "SPO2 Low Alarm Limit violated";         break;
  case 0x0004:  smsg = "PULSE RATE High Alarm Limit violated";  break;
  case 0x0008:  smsg = "PULSE RATE Low Alarm Limit violated";   break;
  case 0x0010:  smsg = "Alarm Active";    break;
  case 0x0020:  smsg = "Alarm Silenced";  break;
  case 0x0040:  smsg = "Low Battery";     break;
  case 0x0080:  smsg = "reserved";        break;
  case 0x0100:  smsg = "SPCO High Alarm Limit violated";        break;
  case 0x0200:  smsg = "SPCO Low Alarm Limit violated";         break;
  case 0x0400:  smsg = "SPMET High Alarm Limit violated";       break;
  case 0x0800:  smsg = "SPMET Low Alarm Limit violated";        break;
  case 0x1000:  smsg = "Desaturation Index Alarm violated";     break;
  case 0x2000:  smsg = "PI Rate of Change Alarm violated";      break;
  default:      smsg = "Unkown alarm code";                     break;
  }
  return(smsg);
}

//=============================================================================
/**
 * Return text message associated with specified exception status code.
 */
char *exceptionmessage(int icode) const
{
  char *smsg = 0;

  switch(icode)
  {
  case 0x000000:  smsg = "Normal operation, no exceptions"; break;
  case 0x000001:  smsg = "No Sensor";                       break;
  case 0x000002:  smsg = "Defective Sensor";                break;
  case 0x000004:  smsg = "Low Perfusion";                   break;
  case 0x000008:  smsg = "Pulse Search";                    break;
  case 0x000010:  smsg = "Interference";                    break;
  case 0x000020:  smsg = "Sensor Off";                      break;
  case 0x000040:  smsg = "Ambient Light";                   break;
  case 0x000080:  smsg = "Unrecognized Sensor";             break;
  case 0x000100:  smsg = "Incompatible Sensor";             break;
  case 0x000200:  smsg = "reserved";                        break;
  case 0x000400:  smsg = "Low Signal IQ";                   break;
  case 0x000800:  smsg = "Masimo SET";                      break;
  case 0x001000:  smsg = "No Cable Connected";              break;
  case 0x002000:  smsg = "Cable Life Expired";              break;
  case 0x004000:  smsg = "Incompatible Cable";              break;
  case 0x008000:  smsg = "Unrecognized Cable";              break;
  case 0x010000:  smsg = "Defective Cable";                 break;
  case 0x020000:  smsg = "Sensor Calibrating";              break;
  case 0x040000:  smsg = "Sensor Life Expired";             break;
  case 0x080000:  smsg = "Emitter Temp Out of Range";       break;
  case 0x100000:  smsg = "Sensor Current Limit Exceeded";   break;
  case 0x200000:  smsg = "SpCO Low Confidence";             break;
  case 0x400000:  smsg = "SpMET Low Confidence";            break;
  case 0x800000:  smsg = "reserved";                        break;
  default:        smsg = "Unknown exception code";          break;
  }
  return(smsg);
}

//=============================================================================
static const char *get_radical7_examplestring()
{
  return(_radical7_examplestring);
}

//=============================================================================
static const char *get_radical5_examplestring()
{
  return(_radical5_examplestring);
}

};

#endif
