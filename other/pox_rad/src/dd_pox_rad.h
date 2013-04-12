/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    dd_pox_rad.h
 * @brief   Device driver, pulse oxiemter, Masimo Radical 5, 7.
 *
 * @see Radical Signal Extraction Pulse Oximeter Operator’s Manual
 *
 * The Radical POX has two data output ports: serial and analog. This
 * driver may collect data from both, depending on program configuration.
 *
 * The device sends ASCII text data to the serial interface at a rate of 1-Hz.
 * The data include: date, time, SpO2, pulse rate, PI, and alarm and exception
 * values. Additionally, the Radical-7 ASCII data will include SPCO, SPMET,
 * DESAT, PIDELTA and PVI.
 *
 * The device Analog Output port provides two data channels: Analog1 and
 * Analog2. The output signals vary from approximately 0 to 1 volt in a
 * linear fashion. The operator may configure the device Output menu to output
 * continuously to Analog1 and Analog2 the measurements SpO2, pulse rate, pleth
 * waveform or Signal IQ.
 */
//=============================================================================
#pragma once
#ifndef  DD_POX_RAD_H_
#define  DD_POX_RAD_H_

#include <string>

//=============================================================================
class dd_pox_rad
{
private:
// Disallow use of implicitly generated member functions:
  dd_pox_rad(const dd_pox_rad &src);
  dd_pox_rad &operator=(const dd_pox_rad &rhs);

public:
  enum
  {
    portindex_serial,
    portindex_analog,
    portindex_portcount   // ensure this is always last in list

  } portindex;

  /// High-level io port info
  typedef struct
  {
    /// Port i/o is disabled
    bool disabled;

    /// Get sample data from program alogrithm (not from device or file)
    bool use_program_sim_data;

    /// Get sample data from data file (not from device or algorithm).
    /// See sample_data_filename
    bool use_file_sim_data;

    /// Name of file from which to get sample data. See use_file_sim_data
    std::string sample_data_filename;

    double samples_per_sec;
    int update_period_millisec;

  } t_portinfo;

  dd_pox_rad();
  ~dd_pox_rad();

  int run_me(int argc, char *argv[]);
  int parse_config(int argc, char *argv[]);

  static void print_cmd_line_help();

  char *_programname;

private:
  int   _argc;
  char  **_argv;
  std::string _cfgFile;
  bool  _isPub;   // If true, this gets data from device and publishes.
                  // Otherwise, this subscribes to the published data.

  /// Uniquely identify instance of this object in a context meaningful to host
  int   _handle;

  std::string _manufacturer;  // masimo
  std::string _model;         // radical-5, or radical-7
  t_portinfo  _portinfo[portindex_portcount];

#if 0
  std::string  _serialPortName;
  int _serialBaudrate;
  int _serialDataBits;

  // 0 = 1 stop bit, 1 = 1.5, 2 = 2
  int _serialStopBits;
  std::string  _serialParity;
  int _serialHandshaking;

  std::string  _deviceManufacturer;
  std::string  _deviceModel;
  std::string  _deviceSoftwareVersion;
  std::string  _devicePropertyId;

  // The Radical provides a selectable range of 0 - 100 or 50 - 100
  // for the SpO2 value. Specify range minimum (0 or 50) here.
  int  _analogSpo2Min;
  int  _waveformAveraging;
#endif

  int operate_device();

  static bool is_radical5(const std::string &model);
  static bool is_radical7(const std::string &model);

};

#endif
