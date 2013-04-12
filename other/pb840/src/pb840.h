/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file  pb840.h
 *
 * Handle RS-232 data streams received from the Puritan Bennett 840 Ventilator.
 *
 * This maintains a copy of the entire data buffer and parses out individual
 * fields as called by inline get_ funcs defined below. The parse function
 * copies the contents of the data buffer, specified by argument, to storage
 * maintained by this class.
 *
 * PB840 commands to communicate with the vent using the RS-232 port:
 *    RSET (clear data from the ventilator receive buffer)
 *    SNDA (send info on vent settings, monitored data and alarms)
 *    SNDF (send info on vent settings, monitored data and alarms)
 *
 * NOTE:
 * The ventilator responds only if it receives a carriage return <CR>.
 *
 * @see Puritan Bennett 800 Series Ventilator System Operator’s and Technical
 * Reference Manual. (Part No. 4-075609-00, Rev. M, August 2010.)
 */
//=============================================================================

#ifndef	PB840_H_
#define	PB840_H_

#include <sstream>
#include <string>

#define _STX  (0x02)
#define _ETX  (0x03)
#define _CR   (0x0d)

/**
 *  Specify characteristics of a field contained in the PB 840 datastream.
 */
//=============================================================================
typedef struct
{
  /// This specifies the data contained in each row of Table 19-1: MISCA
  /// response, and Table 19-2: MISCF response, of the reference manual.

  int number;  //  field number (1-based)
  char  *description;
  int length;

} pb840_field_info;


/**
 *  Base class to deal with Puritan Bennett 840 ventilator.
 */
//=============================================================================
class pb840
{
private:
//	Disallow use of implicitly generated member functions:
  pb840(const pb840 &src);
  pb840 &operator=(const pb840 &rhs);

protected:
  /// Number of bytes between <STX> and <ETX>.
  int _idatabytes;

  /// Number of data fields between <STX> and <ETX>.
  int _idatafields;

  /// Local store to receive raw data
  std::string  _sbuf;

public:
  enum  pb840_resultstatus
  {
    rstat_ok  = 0,
    rstat_fail,
    rstat_bad_input_data
  };

  static bool  rstatus_ok(int const istat);

  /**
   *  Enumerate commands that allow RS232 communication to and from the vent.
   *  Commands sent to the device must contain a trailing <CR> (0x0D)
   */
  enum  pb840_commands
  {
    cmd_none,

    //  Clear data from the vent receive buffer
    cmd_rset,

    //  Request vent to send data
    cmd_snda,
    cmd_sndf
  };


//=============================================================================
  pb840();
  virtual ~pb840();

//=============================================================================
/**
 * Get the specified command string.
 *
 * The vent transmits data when it receives a valid command terminated with <CR>.
 *
 * @param icmd
 *    icmd is one of the PB840_COMMANDS command enumerators.
 *
 * @return
 *    The specified command as null-terminated string.
 */
  static const char *get_cmd_str(const int icmd);

//=============================================================================
/**
 * Get the command enum for the specified string.
 *
 * @param sbuf
 *    string containing command text.
 *
 * @return
 *    The command enum for the specified command string.
 */
  static const int which_cmd(const std::string &sbuf);

//=============================================================================
/**
 * Confirm that the specified data buffer contains good PB840 datastream.
 *
 * @return
 *    Return true if good data. Otherwise, return false.
 */
  static bool isgooddata(const std::string &sbuf);

//=============================================================================
/**
 * Receive the PB840 datastream specified by buf. Copy the data buffer contents
 * to local storage.
 *
 * @return
 *    One of PB840_RESULTSTATUS.
 */
  int receive(const std::string &sbuf);

//=============================================================================
/**
 * Output to the specified file the field info table as tab separated values.
 *
 * @return
 *    If success, return 0, otherwise return non-zero.
 */
  int fieldinfo2tsv(
    const char *sfilename,
    const int reccount,
    const pb840_field_info  *pfi);

//=============================================================================
/**
 * Return the info record for the specified field, fieldnumber (1-based).
 *
 * @return
 *    Field info record.
 */
  pb840_field_info  get_field_info(
    int fieldnumber,
    const int reccount,
    const pb840_field_info  *pfi);

//=============================================================================
/**
 * Return "MISCA" or "MISCF".
 */
  static char *get_data_name(int icmd);

//=============================================================================
/**
 * Return "MISCA" or "MISCF".
 */
  virtual const char *get_data_name() = 0;

//=============================================================================
/**
 * Return pointer to the sample test data.
 */
  static char *get_sample_data(const int icmd);

//=============================================================================
/**
 * Return pointer to the sample test data.
 */
  virtual const char *get_sample_data() = 0;

//=============================================================================
/**
 * Return the number of records contained in the field info array.
 *
 * @return
 *    Count of field info records.
 */
  virtual const int get_field_info_rec_count() = 0;

//=============================================================================
/**
 * Return the info record for the specified field, fieldnumber (1-based).
 *
 * @return
 *    Field info record.
 */
  virtual pb840_field_info get_field_info(int fieldnumber) = 0;

#if 0
//=============================================================================
/**
 * Return the field value of type type_t of the specified field.
 *
 * @return
 *    None. Return field value of type_t in parameter pval.
 */
  virtual template <typename type_t>
    void gettval(type_t *pval, int fieldnumber) = 0;
#endif

//=============================================================================
/**
 * Return the enumeration that represents the value of the specified field.
 *
 * @return
 *    Field value represented as enumeration.
 */
  virtual int geteval(const int fieldnumber) = 0;

//=============================================================================
/**
 * Return the text contained in the the specified field. (Field number is
 * one-based, not zero-based.)
 *
 * @return
 *    Field text as string. Empty string if field number is invalid.
 */
  virtual std::string getsval(const int fieldnumber) = 0;

//=============================================================================
/**
 * Return the integer representation of the specified field.
 *
 * @return
 *    Field value as integer.
 */
  virtual int getival(const int fieldnumber)  = 0;

//=============================================================================
/**
 * Return the floating point representation of the specified field.
 *
 * @return
 *    Field value as double.
 */
  virtual double getfval(const int fieldnumber) = 0;

//=============================================================================
  inline  int getbufbytecount()  { return(_sbuf.length()); }
  inline  int getfieldcount()  { return(_idatafields); }
  inline  int getdatabytes()  { return(_idatabytes); }

//=============================================================================

  enum  pb840_type
  {
    vtype_unknown,
    vtype_niv,
    vtype_invasive,

  };

  enum  pb840_mode
  {
    mode_unknown,
    mode_ac,
    mode_bilevl,
    mode_cmv,
    mode_cpap,
    mode_simv,
    mode_spont,

  };

  enum  pb840_on_off_state
  {
    oostate_unknown,
    oostate_on,
    oostate_off,

  };

  enum  pb840_alarm_status
  {
    alarmstatus_unknown,
    alarmstatus_normal,
    alarmstatus_alarm,
    alarmstatus_reset,

  };

  enum  pb840_flow_pattern
  {
    flowpat_unknown,
    flowpat_square,
    flowpat_ramp,

  };

  enum  pb840_mandatory_breaths_constant
  {
    manbreathconst_unknown,
    manbreathconst_itime,
    manbreathconst_ie,
    manbreathconst_etime_or_pcv_inactive,

  };
};


//=============================================================================
//=============================================================================

/**
 *  Class to handle PB840 MISCA data, received in response to SNDA command.
 */
//=============================================================================
class pb840_misca : public pb840
{
private:
// Disallow use of implicitly generated member functions:
  pb840_misca(const pb840_misca &src);
  pb840_misca &operator=(const pb840_misca &rhs);

protected:


public:

//=============================================================================
  pb840_misca();
  ~pb840_misca();

//=============================================================================
/**
 * Return "MISCA".
 */
  inline const char *get_data_name()
  {
    static const  char  sname[] = "MISCA";
    return(sname);
  };

//=============================================================================
/**
 * Return pointer to the sample test data.
 */
  const char *get_sample_data();

//=============================================================================
/**
 * Output to the specified file the field info table as tab separated values.
 *
 * @return
 *    If success, return 0, otherwise return non-zero.
 */
  int fieldinfo2tsv(const char *sfilename);

//=============================================================================
/**
 * Return the info record for the specified fieldnumber (1-based).
 *
 * @return
 *    Field info record.
 */
  pb840_field_info  get_field_info(const int fieldnumber);

//=============================================================================
/**
 * Return the number of records contained in the field info array.
 *
 * @return
 *    Count of field info records.
 */
  const int get_field_info_rec_count();

//=============================================================================
/**
 * Return the field value of type type_t of the specified field.
 *
 * @return
 *    None. Return field value of type_t in parameter pval.
 */
  template <typename type_t>
    void gettval(type_t *pval, int fieldnumber)
  {
    std::stringstream  ss (std::stringstream::in | std::stringstream::out);
    ss.str(getsval(fieldnumber));
    ss >> *pval;
  }

//=============================================================================
/**
 * Return the enumeration that represents the value of the specified field.
 *
 * @return
 *    Field value represented as enumeration.
 */
  int geteval(const int fieldnumber);

//=============================================================================
/**
 * Return the text contained in the the specified field. (Field number is
 * one-based, not zero-based.)
 *
 * @return
 *    Field text as string. Empty string if field number is invalid.
 */
  std::string getsval(const int fieldnumber);

//=============================================================================
/**
 * Return the integer representation of the specified field.
 *
 * @return
 *    Field value as integer.
 */
  int getival(const int fieldnumber);

//=============================================================================
/**
 * Return the floating point representation of the specified field.
 *
 * @return
 *    Field value as double.
 */
  double getfval(const int fieldnumber);

//=============================================================================
/**
 * The pb840_field_number enumeration specifies a field within the RS-232
 * datastream. Enums have the same values as their corresponding field numbers,
 * specified in Table 19-1 of the reference manual.
 *
 * The comment for each enum indicates the field value data type. Note fp =
 * double.
 *
 * A number of fields have duplicate names. These are noted in the comment.
 */
  enum  pb840_field_number
  {
    //  This excludes unused fields

    fn_unused = 0,

    fn_misca  = 1,  //  string
    fn_bytecount  = 2,  //  int
    fn_fieldcount = 3,  //  int
    fn_stx  = 4,  //  non-printable ascii
    fn_venttime = 5,  //  string. First of two, see field 60
    fn_ventid = 6,    //  string
    fn_date = 8,      //  string. First of two, see field 62 for dupe
    fn_mode_setting = 9,  //  enum
    fn_respir_rate_setting = 10, //  fp
    fn_tidal_vol_setting = 11, //  fp
    fn_peakflow_setting = 12, //  int
    fn_o2_setting = 13, //  int
    fn_pressure_sensitivity_setting = 14, //  fp
    fn_peep_setting = 15,   //  fp
    fn_plateau_time = 16,   //  fp
    fn_apnea_interval = 21, //  int
    fn_apnea_tidal_vol_setting = 22, //  fp
    fn_apnea_respir_rate_setting = 23, //  fp. First of two, see field 89
    fn_apnea_peak_flow_setting = 24,   //  int
    fn_apnea_o2_setting = 25,     //  int. First of two, see field 91
    fn_pressure_support_setting = 26, //  int
    fn_flow_pattern_setting = 27, //  enum
    fn_100pct_o2_state  = 30,   //  enum
    fn_respir_rate_total = 34,     //  fp
    fn_tidal_vol_exhaled = 35,   //  fp
    fn_minute_vol_exhaled  = 36, //  fp,
    fn_minute_vol_spontaneous  = 37, //  fp
    fn_max_circuit_pressure = 38,   //  fp
    fn_mean_airway_pressure = 39,   //  fp
    fn_end_inspir_pressure  = 40,   //  fp. First of two, see field 84
    fn_expir_component_monitored  = 41, //  fp
    fn_circuit_pressure_hi_limit  = 42, //  int. First of two, see field 92
    fn_exhaled_tidal_vol_low_limit  = 45, //  fp
    fn_exhaled_minute_vol_low_limit = 46, //  fp
    fn_respir_rate_hi_limit  = 47, //  int
    fn_circuit_pressure_hi_alarm_status  = 48,  //  enum
    fn_tidal_vol_exhaled_low_alarm_status  = 51,  //  enum
    fn_minute_vol_exhaled_low_alarm_status = 52,  //  enum
    fn_respir_rate_hi_alarm_status  = 53,  //  enum
    fn_no_o2_supply_alarm_status = 54,  //  enum
    fn_no_air_supply_alarm_status = 55, //  enum
    fn_apnea_alarm_status = 57,   //  enum. First of two, see field 94
    fn_venttime_2 = 60,   //  string. Second of two, see field 5
    fn_date_2 = 62,   //  string. Second of two, see field 8
    fn_vent_set_base_flow = 70, //  int
    fn_flow_sensitivity = 71,   //  int
    fn_end_inspir_pressure_2  = 84, //  fp. Second of two, see field 40
    fn_inspir_pressure  = 85, //  int
    fn_inspir_time_setting  = 86,   //  fp
    fn_apnea_interval_setting = 87, //  int
    fn_apnea_inspir_pressure_setting  = 88, //  int
    fn_apnea_respir_rate_setting_2 = 89, //  fp. Second of two, see field 23
    fn_apnea_inspir_time_setting  = 90, //  fp
    fn_apnea_o2_setting_2 = 91,   //  int. Second of two, see field 25
    fn_circuit_pressure_hi_limit_2  = 92, //  int. Second of two, see field 42
    fn_alarm_silence_state  = 93,   //  enum
    fn_apnea_alarm_status_2 = 94,   //  enum. Second of two, see field 57
    fn_severe_occlusion_disconn_alarm_status  = 95, //  enum
    fn_inspir_component_ie_ratio_setting  = 96, //  fp
    fn_expir_component_ie_ratio_setting = 97, //  fp
    fn_inspir_component_apnea_setting = 98,   //  fp
    fn_expir_component_apnea_setting  = 99,   //  fp
    fn_mandatory_breaths_constant = 100,    //  int. Setting?
    fn_monitored_ie = 101,  //  string
    fn_etx  = 102,
    fn_cr = 103,
    fn_last,
  };

//=============================================================================
  /// Vent time
  inline std::string get_vent_time()
    { return(getsval(fn_venttime)); }

  /// Vent ID
  inline std::string get_vent_id()
    { return(getsval(fn_ventid)); }

  /// Date
  inline std::string get_date()
    { return(getsval(fn_date)); }

  /// Mode setting
  inline int get_mode_setting()
    { return(geteval(fn_mode_setting)); }

  /// Respir rate setting
  inline double get_respir_rate_setting()
    { return(getfval(fn_respir_rate_setting)); }

  /// Tidal volume setting
  inline double get_tidal_vol_setting()
    { return(getfval(fn_tidal_vol_setting)); }

  /// Peak flow setting
  inline int get_peak_flow_setting()
    { return(getival(fn_peakflow_setting)); }

  /// O2 setting
  inline int get_o2_setting()
    { return(getival(fn_o2_setting)); }

  /// Pressure sensitivity setting
  inline double get_pressure_sensitivity_setting()
    { return(getfval(fn_pressure_sensitivity_setting)); }

  /// PEEP or PEEP Low setting
  inline double get_peep_setting()
    { return(getfval(fn_peep_setting)); }

  /// Plateau time, seconds
  inline double get_plateau_time()
    { return(getfval(fn_plateau_time)); }

  /// Apnea interval, seconds
  inline int get_apnea_interval()
    { return(getival(fn_apnea_interval)); }

  /// Apnea tidal vol
  inline double get_apnea_tidal_vol()
    { return(getfval(fn_apnea_tidal_vol_setting)); }

  /// Apnea respir rate setting
  inline double get_apnea_respir_rate_setting()
    { return(getfval(fn_apnea_respir_rate_setting)); }

  /// Apnea peak flow setting
  inline int get_apnea_peak_flow_setting()
    { return(getival(fn_apnea_peak_flow_setting)); }

  /// Apnea O2% setting
  inline int get_apnea_o2_setting()
    { return(getival(fn_apnea_o2_setting)); }

  /// Pressure support setting
  inline int get_pressure_support_setting()
    { return(getival(fn_pressure_support_setting)); }

  /// Flow pattern setting
  inline int get_flow_pattern_setting()
    { return(geteval(fn_flow_pattern_setting)); }

  /// 100% O2 state
  inline int get_100pct_o2_state()
    { return(geteval(fn_100pct_o2_state)); }

  /// Total respir rate
  inline int get_total_respir_rate()
    { return(getival(fn_respir_rate_total)); }

  /// Exhaled tidal vol
  inline double get_exhaled_tidal_vol()
    { return(getfval(fn_tidal_vol_exhaled)); }

  /// Exhaled minute vol
  inline double get_exhaled_minute_vol()
    { return(getfval(fn_minute_vol_exhaled)); }

  /// Spontaneous minute vol
  inline double get_spontaneous_minute_vol()
    { return(getfval(fn_minute_vol_spontaneous)); }

  /// Max circuit pressure
  inline double get_max_circuit_pressure()
    { return(getfval(fn_max_circuit_pressure)); }

  /// Mean airwary pressure
  inline double get_mean_airway_pressure()
    { return(getfval(fn_mean_airway_pressure)); }

  /// End inspir pressure
  inline double get_end_inspir_pressure()
    { return(getfval(fn_end_inspir_pressure)); }

  /// Expir component of monitored value of I:E ratio
  inline double get_expir_component_monitored()
    { return(getfval(fn_expir_component_monitored)); }

  /// Circuit pressure, hi limit
  inline int get_circuit_pressure_hi_limit()
    { return(getival(fn_circuit_pressure_hi_limit)); }

  /// Exhaled tidal vol, low limit
  inline double get_exhaled_tidal_vol_low_limit()
    { return(getfval(fn_exhaled_tidal_vol_low_limit)); }

  /// Exhaled minute vol, low limit
  inline double get_exhaled_minute_vol_low_limit()
    { return(getfval(fn_exhaled_minute_vol_low_limit)); }

  /// Respir rate, hi limit
  inline int get_respir_rate_hi_limit()
    { return(getival(fn_respir_rate_hi_limit)); }

  /// Circuit pressure, hi alarm status
  inline int get_circuit_pressure_hi_alarm_status()
    { return(geteval(fn_circuit_pressure_hi_alarm_status)); }

  /// Exhaled tidal vol, low alarm status
  inline int get_exhaled_tidal_vol_low_alarm_status()
    { return(geteval(fn_tidal_vol_exhaled_low_alarm_status)); }

  /// Exhaled minute vol, low alarm status
  inline int get_exhaled_minute_vol_low_alarm_status()
    { return(geteval(fn_minute_vol_exhaled_low_alarm_status)); }

  /// Respir rate, hi alarm status
  inline int get_respir_rate_hi_alarm_status()
    { return(geteval(fn_respir_rate_hi_alarm_status)); }

  /// No O2 supply alarm status
  inline int get_no_o2_supply_alarm_status()
    { return(geteval(fn_no_o2_supply_alarm_status)); }

  /// No air supply alarm status
  inline int get_no_air_supply_alarm_status()
    { return(geteval(fn_no_air_supply_alarm_status)); }

  /// Apnea alarm status
  inline int get_apnea_alarm_status()
    { return(geteval(fn_apnea_alarm_status)); }

  /// Vent time
  inline std::string get_vent_time_2()
    { return(getsval(fn_venttime_2)); }

  /// Date
  inline std::string get_date_2()
    { return(getsval(fn_date_2)); }

  /// Vent set base flow
  inline int get_vent_set_base_flow()
    { return(getival(fn_vent_set_base_flow)); }

  /// Flow sensitivity
  inline int get_flow_sensitivity()
    { return(getival(fn_flow_sensitivity)); }

  /// End inspir pressure
  inline double get_end_inspir_pressure_2()
    { return(getfval(fn_end_inspir_pressure_2)); }

  /// Inspir pressure or PEEP High setting
  inline int get_inspir_pressure()
    { return(getival(fn_inspir_pressure)); }

  /// Inspir time setting
  inline double get_inspir_time_setting()
    { return(getfval(fn_inspir_time_setting)); }

  /// Apnea interval setting
  inline int get_apnea_interval_setting()
    { return(getival(fn_apnea_interval_setting)); }

  /// Apnea inspir pressure setting
  inline int get_apnea_inspir_pressure_setting()
    { return(getival(fn_apnea_inspir_pressure_setting)); }

  /// Apnea resp rate setting
  inline double get_apnea_resp_rate_setting()
    { return(getfval(fn_apnea_respir_rate_setting_2)); }

  /// Apnea inspir time setting
  inline double get_apnea_inspir_time_setting()
    { return(getfval(fn_apnea_inspir_time_setting)); }

  /// Apnea O2 setting
  inline int get_apnea_o2_setting_2()
    { return(getival(fn_apnea_o2_setting_2)); }

  /// Circuit pressure, hi limit
  inline int get_circuit_pressure_hi_limit_2()
    { return(getival(fn_circuit_pressure_hi_limit_2)); }

  /// Alarm silence state
  inline int get_alarm_silence_state()
    { return(geteval(fn_alarm_silence_state)); }

  /// Apnea alarm status
  inline int get_apnea_alarm_status_2()
    { return(geteval(fn_apnea_alarm_status_2)); }

  /// Severe Occlusion/Disconnect alarm status
  inline int get_severe_occlusion_disconn_alarm_status()
    { return(geteval(fn_severe_occlusion_disconn_alarm_status)); }

  /// Inspir component of I:E ratio or High component of H:L (Bi-Level) setting
  inline double get_inspir_component_ie_ratio_setting()
    { return(getfval(fn_inspir_component_ie_ratio_setting)); }

  /// Expir component of I:E ratio setting or Low component of H:L (Bi-Level)
  inline double get_expir_component_ie_ratio_setting()
    { return(getfval(fn_expir_component_ie_ratio_setting)); }

  /// Inspir component of apnea I:E ratio setting
  inline double get_inspir_component_apnea_setting()
    { return(getfval(fn_inspir_component_apnea_setting)); }

  /// Expir component of apnea I:E ratio setting
  inline double get_expir_component_apnea_setting()
    { return(getfval(fn_expir_component_apnea_setting)); }

  /// Constant during rate setting change for pressure control mandatory breaths
  inline int get_mandatory_breaths_constant()
    { return(geteval(fn_mandatory_breaths_constant)); }

  /// Monitored value of I:E ratio
  //
  /// The reference manual defines I:E ratio as: "The ratio of inspiratory
  /// time to expiratory time. Also, the operator-set timing variable that
  /// applies to PC and VC+ mandatory breaths."
  //
  /// For this field the sample datastream listed in the .cpp file contains
  /// six characters: <1:5.70> (minus the angle brackets).
  //
  /// The function caller must parse out the values.
  inline std::string get_monitored_ie()
    { return(getsval(fn_monitored_ie)); }

};

//=============================================================================
//=============================================================================

/**
 *  Class to handle PB840 MISCF data, received in response to SNDF command.
 */
//=============================================================================
class pb840_miscf : public pb840
{
private:
//	Disallow use of implicitly generated member functions:
  pb840_miscf(const pb840_miscf &src);
  pb840_miscf &operator=(const pb840_miscf &rhs);

protected:


public:

//=============================================================================
  pb840_miscf();
  ~pb840_miscf();

//=============================================================================
/**
 * Return "MISCF".
 */
  inline const char *get_data_name()
  {
    static const  char  sname[] = "MISCF";
    return(sname);
  };

//=============================================================================
/**
 * Return pointer to the sample test data.
 */
  const char *get_sample_data();

//=============================================================================
/**
 * Output to the specified file the field info table as tab separated values.
 *
 * @return
 *    If success, return 0, otherwise return non-zero.
 */
  int fieldinfo2tsv(const char *sfilename);

//=============================================================================
/**
 * Return the info record for the specified field, fieldnumber (1-based).
 *
 * @return
 *    Field info record.
 */
  pb840_field_info  get_field_info(const int fieldnumber);

//=============================================================================
/**
 * Return the number of records contained in the field info array.
 *
 * @return
 *    Count of field info records.
 */
  const int get_field_info_rec_count();

//=============================================================================
/**
 * Return the field value of type type_t of the specified field.
 *
 * @return
 *    None. Return field value of type_t in parameter pval.
 */
  template <typename type_t>
    void gettval(type_t *pval, int fieldnumber)
  {
    std::stringstream  ss (std::stringstream::in | std::stringstream::out);
    ss.str(getsval(fieldnumber));
    ss >> *pval;
  }

//=============================================================================
/**
 * Return the enumeration that represents the value of the specified field.
 *
 * @return
 *    Field value represented as enumeration.
 */
  int geteval(const int fieldnumber);

//=============================================================================
/**
 * Return the text contained in the the specified field. (Field number is
 * one-based, not zero-based.)
 *
 * @return
 *    Field text as string. Empty string if field number is invalid.
 */
  std::string getsval(const int fieldnumber);

//=============================================================================
/**
 * Return the integer representation of the specified field.
 *
 * @return
 *    Field value as integer.
 */
  int getival(const int fieldnumber);

//=============================================================================
/**
 * Return the floating point representation of the specified field.
 *
 * @return
 *    Field value as double.
 */
  double getfval(const int fieldnumber);

//=============================================================================
/**
 * The pb840_field_number enumeration specifies a field within the RS-232
 * datastream. Enums have the same values as their corresponding field numbers,
 * specified in Table 19-2 of the reference manual.
 *
 * The comment for each enum indicates the field value data type. Note fp =
 * double.
 *
 * A number of fields have duplicate names. These are noted in the comment.
 */
  enum  pb840_field_number
  {
    //  This excludes unused fields

    fn_unused = 0,

    fn_miscf      = 1,  //  string
    fn_bytecount  = 2,  //  int
    fn_fieldcount = 3,  //  int
    fn_stx        = 4,  //  non-printable ascii
    fn_venttime   = 5,  //  string. First of two, see field 60
    fn_ventid     = 6,  //  string
    fn_date       = 7,  //  string. First of two, see field 62 for dupe
    fn_venttype   = 8,  //  enum
    fn_mode_setting = 9,  //  enum

  /**
   *  @todo   Complete the list
   */

    fn_etx  = 172,
    fn_cr   = 173,
    fn_last
  };

//=============================================================================
  /// Vent time
  inline std::string get_vent_time()
    { return(getsval(fn_venttime)); }

  /// Vent ID
  inline std::string get_vent_id()
    { return(getsval(fn_ventid)); }

  /// Date
  inline std::string get_date()
    { return(getsval(fn_date)); }

  /// Vent type
  inline int get_vent_type()
    { return(geteval(fn_venttype)); }

  /// Mode setting
  inline int get_mode_setting()
    { return(geteval(fn_mode_setting)); }

  /**
   *  @todo   Complete the list
   */

};

#endif

