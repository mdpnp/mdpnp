/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file  pb840.cpp
 *
 * Deal with the data stream input from Puritan Bennett 840 Ventilator.
 */
//=============================================================================
#include <cstring>
#include <fstream>
#include "pb840.h"

#define _PB840_UNIT_TEST_ONLY

using namespace std;

#ifdef  _PB840_UNIT_TEST_ONLY
//=============================================================================
/**
 * Sample MISCA
 *
 * From PB840 manual: byte at position 14 (1-based) should be <STX> (0x02),
 * next-to-last byte should be <ETX> (0x03), last byte should be <CR> (0x0D).
 *
 * Note: documentation does not explicitly specify comma separated fields but a
 * diagram shows this in the reference manual. The diagram, labeled "The MISCA
 * response follows this format:", shows commas. Further, this sample from Dave
 * Arney contains the commas (but not the non-printable control chars). Note:
 * the diagram does not show commas separating the first three fields. But
 * assume the buffer contains them, because Dave's sample data does. Confirm
 * this by checking more real data from the device.
 *
 * This assumes a comma FOLLOWS each DATA field. Therefore, the STX and ETX do
 * not have a trailing comma???
 *
 * Determine data type of individual fields by inspecting the sample data
 * (reference manual does not specify).
 */
static  const char  _sample_misca[] =
/*          10        20        30        40        50        60        70          */
/* 12345678901234567890123456789012345678901234567890123456789012345678901234567890 */
  "MISCA,706,97,\x02"
                "14:47 ,840 3510053249    ,      ,NOV 22 2011 ,CMV   ,14.0  ,0.36  "
  ",55    ,21    ,0.0   ,5.0   ,0.0   ,      ,      ,      ,      ,20    ,0.50  ,20"
  ".0  ,55    ,100   ,0     ,RAMP  ,      ,      ,OFF   ,      ,      ,      ,14   "
  " ,0.38  ,5.29  ,0.0   ,24.0  ,8.6   ,24.0  ,5.00  ,50    ,      ,      ,0.35  ,4"
  ".5   ,40    ,NORMAL,      ,      ,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORM"
  "AL,      ,      ,14:47 ,      ,NOV 22 2011 ,0.0   ,0.0   ,20.0  ,5.10  ,0     ,0"
  ".000 ,0     ,4     ,3     ,      ,      ,      ,      ,      ,      ,      ,    "
  "  ,      ,      ,      ,NOOP C,24.00 ,0     ,0.00  ,20    ,0     ,20.0  ,0.00  ,"
  "100   ,50    ,OFF   ,NORMAL,NORMAL,0.00  ,0.00  ,0.00  ,0.00  ,         ,1:5.00,"
  "\x03\x0d";
  

/**
 * Sample MISCF
 *
 * From PB840 manual: byte at position 16 or 17 (field number 4) should be
 * <STX> (0x02). Don't know for sure because the manual says one thing, sample
 * data another. (Sample data shows field number 3 length = 4, manual specifies
 * 3.) Next-to-last byte should be <ETX> (0x03), last byte should be <CR>
 * (0x0D).
 */
static  const char  _sample_miscf[] =
/*          10        20        30        40        50        60        70          */
/* 12345678901234567890123456789012345678901234567890123456789012345678901234567890 */
  "MISCF,1225,169 ,\x02"
                   "14:51 ,840 3510053249    ,NOV 22 2011 ,INVASIVE ,A/C   ,VC    ,"
  "      ,V-Trig,14.0  ,0.365 ,55.0  ,21    ,      ,5.0   ,0.0   ,20    ,0.500 ,20."
  "0  ,55.0  ,100   ,      ,      ,RAMP  ,VC    ,      ,      ,      ,RAMP  ,OFF   "
  ",50    ,      ,23.000,4.500 ,1200  ,350   ,1200  ,255   ,40    ,      ,4.5   ,3."
  "0   ,      ,      ,      ,      ,         ,      ,      ,HME               ,    "
  "  ,Enabled  ,75    ,      ,      ,      ,50.0  ,      ,      ,      ,      ,    "
  "  ,ADULT    ,      ,      ,24.0  ,14.0  ,0.378 ,5.290 ,24.0  ,8.6   ,5.00  ,1:5."
  "00,20    ,      ,      ,      ,      ,      ,      ,      ,      ,      ,5.8   ,"
  "      ,      ,0.0   ,0.0   ,0.0   ,0.0   ,0.0   ,      ,20.0  ,5.1   ,      ,101"
  ".0 ,0.0   ,      ,0.0   ,0.0   ,0.000 ,OFF   ,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL"
  ",NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,RESET ,NORMAL,NORMAL,NORMAL,NO"
  "RMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMA"
  "L,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,N"
  "ORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,      ,      ,    "
  "  ,      ,      ,      ,      ,      ,      ,      ,      ,      ,      ,      ,"
  "      ,      ,      ,      ,      ,      ,"
  "\x03\x0d";
#endif

//=============================================================================
static  const char  _rset_command[] =
{
  'R', 'S', 'E', 'T', _CR, '\0'
};

//=============================================================================
static  const char  _snda_command[] =
{
  'S', 'N', 'D', 'A', _CR, '\0'
};

//=============================================================================
static  const char  _sndf_command[] =
{
  'S', 'N', 'D', 'F', _CR, '\0'
};

//=============================================================================
/**
 * Specify characteristics of fields contained in the PB 840 datastream that
 * respond to the MISCA command.
 *
 * Note: Maintain the same order as the list in the PB 840 reference manual.
 */
const static  pb840_field_info _fi_misca[] =
{
  { 1, "MISCA Response to SNDA command", 5 },
  { 2, "706 The number of bytes between <STX> and <ETX>", 3 },
  { 3, "97 The number of fields between <STX> and <ETX>", 2 },
  { 4, "<STX> Start of transmission character (0x02)", 1 },
  { 5, "Ventilator time (HH:MM_)", 6 },
  { 6, "Ventilator ID to allow external hosts to uniquely identify each 840 ventilator", 18 },
  { 7, "Not used", 6 },
  { 8, "Date (MMM_DD_YYYY_)", 12 },
  { 9, "Mode (CMV___, SIMV__, CPAP__ or BILEVL) (CMV = A/C) setting", 6 },
  { 10, "Respiratory rate setting in breaths per minute", 6 },
  { 11, "Tidal volume setting in liters", 6 },
  { 12, "Peak flow setting in liters per minute", 6 },
  { 13, "O2% setting", 6 },
  { 14, "Pressure sensitivity setting in cmH2O", 6 },
  { 15, "PEEP or PEEP Low (in BILEVEL) setting in cmH2O", 6 },
  { 16, "Plateau time in seconds", 6 },
  { 17, "Not used", 6 },
  { 18, "Not used", 6 },
  { 19, "Not used", 6 },
  { 20, "Not used", 6 },
  { 21, "Apnea interval in seconds", 6 },
  { 22, "Apnea tidal volume setting in liters", 6 },
  { 23, "Apnea respiratory rate setting in breaths per minute", 6 },
  { 24, "Apnea peak flow setting in liters per minute", 6 },
  { 25, "Apnea O2% setting", 6 },
  { 26, "Pressure support setting in cmH2O", 6 },
  { 27, "Flow pattern setting (SQUARE or RAMP__)", 6 },
  { 28, "Not used", 6 },
  { 29, "Not used", 6 },
  { 30, "100% O2 state (ON____ or OFF___)", 6 },
  { 31, "Not used", 6 },
  { 32, "Not used", 6 },
  { 33, "Not used", 6 },
  { 34, "Total respiratory rate in breaths per minute", 6 },
  { 35, "Exhaled tidal volume in liters", 6 },
  { 36, "Exhaled minute volume in liters", 6 },
  { 37, "Spontaneous minute volume in liters", 6 },
  { 38, "Maximum circuit pressure in cmH2O", 6 },
  { 39, "Mean airway pressure in cmH2O", 6 },
  { 40, "End inspiratory pressure in cmH2O", 6 },
  { 41, "Expiratory component of monitored value of I:E ratio, assuming inspiratory component of 1", 6 },
  { 42, "High circuit pressure limit in cmH2O", 6 },
  { 43, "Not used", 6 },
  { 44, "Not used", 6 },
  { 45, "Low exhaled tidal volume limit in liters", 6 },
  { 46, "Low exhaled minute volume limit in liters", 6 },
  { 47, "High respiratory rate limit in breaths per minute", 6 },
  { 48, "High circuit pressure alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 49, "Not used", 6 },
  { 50, "Not used", 6 },
  { 51, "Low exhaled tidal volume (mandatory or spontaneous) alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 52, "Low exhaled minute volume alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 53, "High respiratory rate alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 54, "No O2 supply alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 55, "No air supply alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 56, "Not used", 6 },
  { 57, "Apnea alarm status (NORMAL, ALARM_, or RESET_)", 6 },
  { 58, "Not used", 6 },
  { 59, "Not used", 6 },
  { 60, "Ventilator time (HH:MM_)", 6 },
  { 61, "Not used", 6 },
  { 62, "Date (MMM_DD_YYYY_)", 12 },
  { 63, "Static compliance (CSTAT) from inspiratory pause maneuver in mL/cmH2O", 6 },
  { 64, "Static resistance (RSTAT) from inspiratory pause maneuver in cmH2O/L/s", 6 },
  { 65, "Dynamic compliance (CDYN) in mL/cmH2O*", 6 },
  { 66, "Dynamic resistance (RDYN) in cmH2O/L/s*", 6 },
  { 67, "Negative inspiratory force (NIF) in cmH2O*", 6 },
  { 68, "Vital capacity (VC) in L*", 6 },
  { 69, "Peak spontaneous flow (PSF) in L/min*", 6 },
  { 70, "Ventilator-set base flow in liters per minute", 6 },
  { 71, "Flow sensitivity setting in liters per minute", 6 },
  { 72, "Not used", 6 },
  { 73, "Not used", 6 },
  { 74, "Not used", 6 },
  { 75, "Not used", 6 },
  { 76, "Not used", 6 },
  { 77, "Not used", 6 },
  { 78, "Not used", 6 },
  { 79, "Not used", 6 },
  { 80, "Not used", 6 },
  { 81, "Not used", 6 },
  { 82, "Not used", 6 },
  { 83, "Not used", 6 },
  { 84, "End inspiratory pressure in cmH2O", 6 },
  { 85, "Inspiratory pressure or PEEP High setting in cmH2O", 6 },
  { 86, "Inspiratory time or PEEP High time setting in seconds", 6 },
  { 87, "Apnea interval setting in seconds", 6 },
  { 88, "Apnea inspiratory pressure setting in cmH2O", 6 },
  { 89, "Apnea respiratory rate setting in breaths per minute", 6 },
  { 90, "Apnea inspiratory time setting in seconds", 6 },
  { 91, "Apnea O2% setting", 6 },
  { 92, "High circuit pressure limit in cmH2O", 6 },
  { 93, "Alarm silence state (ON____ or OFF___)", 6 },
  { 94, "Apnea alarm status (NORMAL or ALARM_)", 6 },
  { 95, "Severe Occlusion/Disconnect alarm status (NORMAL or ALARM_)", 6 },
  { 96, "Inspiratory component of I:E ratio or High component of H:L (Bi-Level) setting", 6 },
  { 97, "Expiratory component of I:E ratio setting or Low component of H:L (Bi-Level)", 6 },
  { 98, "Inspiratory component of apnea I:E ratio setting", 6 },
  { 99, "Expiratory component of apnea I:E ratio setting", 6 },
  /*
     For the following field the manual specifies field length of 6 characters.
     But with some versions of the firmware the actual length is 9.
  */
  { 100, "Constant during rate setting change for pressure control mandatory breaths (I-TIME or I/E___ or______, where ______ represents E-TIME or PCV not active)", 9 },
  { 101, "Monitored value of I:E ratio", 6 },
  { 102, "<ETX> End of transmission character (0x03)", 1 },
  { 103, "<CR> Terminating carriage return (0x0D)", 1 }
};


//=============================================================================
/**
 * Specify characteristics of fields contained in the PB 840 datastream that
 * respond to the MISCF command.
 *
 * Note: Maintain the same order as the list in the PB 840 reference manual.
 *
 * Note: Assume that fields identified as "Reserved" have length of 6 bytes.
 * Not specified in manual but represented this way in the sample data.
 *
 * Note: Fields that specify alarms or Technical malfunction, with "*" in the
 * description below, will contain one of the following responses: NORMAL, LOW,
 * MEDIUM, HIGH, or RESET.
 */
const static  pb840_field_info _fi_miscf[] =
{
  { 1, "MISCF Response to SNDF command", 5 },
  { 2, "1225 Number of bytes between <STX> and <CR> (1229 if Phillips is selected for serial port in Communication Setup)", 4 },
  { 3, "169 Number of fields between <STX> and <ETX>", 3 },
  { 4, "<STX> Start of transmission character (0x02)", 1 },
  { 5, "Ventilator time (HH:MM_)", 6 },
  { 6, "Ventilator ID to allow external hosts to uniquely identify each Puritan Bennett 840 Ventilator System", 18 },
  { 7, "Date (MMM_DD_YYYY_)", 12 },
  { 8, "Vent Type (NIV______ or INVASIVE_)", 9 },
  { 9, "Mode (A/C___, SIMV__, SPONT_ or BILEVL)", 6 },
  { 10, "Mandatory Type (PC____, VC____, VC+___)", 6 },
  { 11, "Spontaneous Type (NONE__, PS____, TC____, VS____, PA____)", 6 },
  { 12, "Trigger Type setting (V-Trig or P-Trig)", 6 },
  { 13, "Respiratory rate setting in bpm", 6 },
  { 14, "Tidal volume setting in L", 6 },
  { 15, "Peak flow setting in L/min", 6 },
  { 16, "O2% setting", 6 },
  { 17, "Pressure sensitivity setting in cmH2O", 6 },
  { 18, "PEEP/CPAP in cmH2O", 6 },
  { 19, "Plateau setting in seconds", 6 },
  { 20, "Apnea interval setting in seconds", 6 },
  { 21, "Apnea tidal volume setting in L", 6 },
  { 22, "Apnea respiratory rate setting in bpm", 6 },
  { 23, "Apnea peak flow setting in L/min", 6 },
  { 24, "Apnea O2% setting", 6 },
  { 25, "PCV apnea inspiratory pressure setting in cmH2O", 6 },
  { 26, "PCV Apnea Inspiratory Time setting in seconds", 6 },
  { 27, "Apnea flow pattern setting (SQUARE or RAMP)", 6 },
  { 28, "Apnea mandatory type setting (PC or VC)", 6 },
  { 29, "Inspiratory component of Apnea I:E ratio (if apnea mandatory type is PC)", 6 },
  { 30, "Expiratory component of Apnea I:E ratio (if apnea mandatory type is PC)", 6 },
  { 31, "Support pressure setting (cmH2O) (Note: field length not specified in doc)", 6 },
  { 32, "Flow pattern setting (SQUARE or RAMP)", 6 },
  { 33, "100% O2 Suction (ON or OFF)", 6 },
  { 34, "High inspiratory pressure alarm setting (2PPEAK) in cmH2O", 6 },
  { 35, "Low inspiratory pressure alarm setting (4PPEAK) in cmH2O or OFF", 6 },
  { 36, "High exhaled minute volume (2VE TOT) alarm setting in L/min or OFF", 6 },
  { 37, "Low exhaled minute volume (4VE TOT) alarm setting in L/min or OFF", 6 },
  { 38, "High exhaled mandatory tidal volume (2VTE MAND) alarm setting in mL or OFF", 6 },
  { 39, "Low exhaled mandatory tidal volume (4VTE MAND) alarm setting in mL or OFF", 6 },
  { 40, "High exhaled spontaneous tidal volume (2VTE SPONT) alarm setting in mL or OFF", 6 },
  { 41, "Low exhaled spontaneous tidal volume (4VTE SPONT) alarm setting in mL or OFF", 6 },
  { 42, "High respiratory rate (2fTOT) alarm setting in bpm or OFF", 6 },
  { 43, "High inspired tidal volume (2VTI) alarm setting in mL", 6 },
  { 44, "Base flow setting in L/min", 6 },
  { 45, "Flow sensitivity setting in L/min", 6 },
  { 46, "PCV inspiratory pressure (PI) setting in cmH2O", 6 },
  { 47, "PCV inspiratory time (TI) setting in seconds", 6 },
  { 48, "Inspiratory component of I:E ratio setting or High component of H:L ratio setting", 6 },
  { 49, "Expiratory component of I:E ratio setting or Low component of H:L ratio setting", 6 },
  { 50, "Constant during rate change setting (I-time, I/E, or E-time)", 6 },
  { 51, "Tube I.D. setting in mm", 6 },
  { 52, "Tube type setting (ET or TRACH)", 6 },
  { 53, "Humidification type setting (Non-Heated Exp, Heated Exp, or HME)", 18 },
  { 54, "Humidifier volume setting in L", 6 },
  { 55, "O2 sensor setting (Enabled or Disabled)", 9 },
  { 56, "Disconnect sensitivity setting in % or OFF", 6 },
  { 57, "Rise time % setting", 6 },
  { 58, "PAV+ percent support setting", 6 },
  { 59, "Expiratory sensitivity (ESENS) setting in % or L/min for PA breath type", 6 },
  { 60, "IBW setting in kg", 6 },
  { 61, "Target support volume (VT SUPP) setting in L", 6 },
  { 62, "High PEEP (PEEPH) setting in cmH2O", 6 },
  { 63, "Low PEEP (PEEPL) setting in cmH2O", 6 },
  { 64, "High PEEP time (TH) setting in seconds", 6 },
  { 65, "High spontaneous inspiratory time limit (2TI SPONT) setting in seconds", 6 },
  { 66, "Circuit type setting (ADULT, PEDIATRIC, or NEONATAL)", 9 },
  { 67, "Low PEEP time (TL) setting in seconds", 6 },
  { 68, "Expiratory time (TE) setting in seconds", 6 },
  { 69, "End inspiratory pressure (PI END) in cmH2O", 6 },
  { 70, "Respiratory rate (fTOT) in bpm", 6 },
  { 71, "Exhaled tidal volume (VTE) in L", 6 },
  { 72, "Patient exhaled minute volume (VE TOT) in L/min", 6 },
  { 73, "Peak airway pressure (PPEAK) in cmH2O", 6 },
  { 74, "Mean airway pressure (PMEAN) in cmH2O", 6 },
  { 75, "Expiratory component of monitored value of I:E ratio, assuming inspiratory component of 1", 6 },
  { 76, "I:E ratio", 6 },
  { 77, "Delivered O2%", 6 },
  { 78, "Inspired tidal volume (VTI) in L", 6 },
  { 79, "Intrinsic PEEP (PEEPI) in cmH2O", 6 },
  { 80, "Estimated total resistance (RTOT) in cmH2O/L/s", 6 },
  { 81, "Estimated patient resistance (RPAV) in cmH2O/L/s", 6 },
  { 82, "Estimated patient elastance (EPAV) in cmH2O/L", 6 },
  { 83, "Estimated patient compliance (CPAV) in mL/cmH2O", 6 },
  { 84, "Normalized rapid shallow breathing index (f/VT//kg)", 6 },
  { 85, "Rapid shallow breathing index (f/VT)", 6 },
  { 86, "Spontaneous percent inspiratory time (TI/TTOT)", 6 },
  { 87, "Monitored PEEP in cmH2O", 6 },
  { 88, "Spontaneous inspiratory time (TI SPONT) in seconds", 6 },
  { 89, "Exhaled spontaneous minute volume (VE SPONT) in L/min", 6 },
  { 90, "Intrinsic PEEP (PEEPI) from expiratory pause maneuver in cmH2O", 6 },
  { 91, "Total PEEP (PEEPTOT) from expiratory pause maneuver in cmH2O", 6 },
  { 92, "Static compliance (CSTAT) from inspiratory pause maneuver in mL/cmH2O", 6 },
  { 93, "Static resistance (RSTAT) from inspiratory pause maneuver in cmH2O/L/s", 6 },
  { 94, "Plateau pressure (PPL) from inspiratory pause maneuver in cmH2O", 6 },
  { 95, "High spontaneous inspiratory time (ALERT_ or blank)", 6 },
  { 96, "Dynamic compliance (CDYN) in mL/cmH2O", 6 },
  { 97, "Dynamic resistance (RDYN) in cmH2O/L/s", 6 },
  { 98, "Peak spontaneous flow (PSF) in L/min", 6 },
  { 99, "Peak expiratory flow (PEF) in L/min", 6 },
  { 100, "End expiratory flow (EEF) in L/min", 6 },
  { 101, "Reserved", 6 },
  { 102, "Negative inspiratory force (NIF) in cmH2O", 6 },
  { 103, "P0.1 pressure change in cmH2O", 6 },
  { 104, "Vital capacity (VC) in L", 6 },
  { 105, "Alarm Silence (ON or OFF)", 6 },
  { 106, "Apnea ventilation alarm*", 6 },
  { 107, "High exhaled minute volume alarm* (1VE TOT)", 6 },
  { 108, "High exhaled tidal volume alarm* (1VTE)", 6 },
  { 109, "High O2% alarm*", 6 },
  { 110, "High inspiratory pressure alarm* (1PPEAK)", 6 },
  { 111, "High ventilator pressure alarm* (1PVENT)", 6 },
  { 112, "High respiratory rate alarm* (1fTOT)", 6 },
  { 113, "AC power loss alarm*", 6 },
  { 114, "Inoperative battery alarm*", 6 },
  { 115, "Low battery alarm*", 6 },
  { 116, "Loss of power alarm*", 6 },
  { 117, "Low exhaled mandatory tidal volume alarm* (3VTE MAND)", 6 },
  { 118, "Low exhaled minute volume alarm* (3VE TOT)", 6 },
  { 119, "Low exhaled spontaneous tidal volume (3VTE SPONT) alarm*", 6 },
  { 120, "Low O2% alarm*", 6 },
  { 121, "Low air supply pressure alarm*", 6 },
  { 122, "Low O2 supply pressure alarm*", 6 },
  { 123, "Compressor inoperative alarm*", 6 },
  { 124, "Disconnect alarm*", 6 },
  { 125, "Severe occlusion alarm*", 6 },
  { 126, "Inspiration too long alarm*", 6 },
  { 127, "Procedure error*", 6 },
  { 128, "Compliance limited tidal volume (VT) alarm*", 6 },
  { 129, "High inspired spontaneous tidal volume (1VTI SPONT) alarm*", 6 },
  { 130, "High inspired mandatory tidal volume (1VTI MAND) alarm*", 6 },
  { 131, "High compensation limit (1PCOMP) alarm*", 6 },
  { 132, "PAV startup too long alarm*", 6 },
  { 133, "PAV R and C not assessed alarm*", 6 },
  { 134, "Volume not delivered (VC+) alarm*", 6 },
  { 135, "Volume not delivered (VS) alarm*", 6 },
  { 136, "Low inspiratory pressure (3PPEAK) alarm*", 6 },
  { 137, "Technical malfunction A5*", 6 },
  { 138, "Technical malfunction A10*", 6 },
  { 139, "Technical malfunction A15*", 6 },
  { 140, "Technical malfunction A20*", 6 },
  { 141, "Technical malfunction A25*", 6 },
  { 142, "Technical malfunction A30*", 6 },
  { 143, "Technical malfunction A35*", 6 },
  { 144, "Technical malfunction A40*", 6 },
  { 145, "Technical malfunction A45*", 6 },
  { 146, "Technical malfunction A50*", 6 },
  { 147, "Technical malfunction A55*", 6 },
  { 148, "Technical malfunction A60*", 6 },
  { 149, "Technical malfunction A65*", 6 },
  { 150, "Technical malfunction A70*", 6 },
  { 151, "Technical malfunction A75*", 6 },
  { 152, "Technical malfunction A80*", 6 },
  { 153, "Technical malfunction A85*", 6 },
  { 154, "Spontaneous tidal volume (VTE SPONT) in liters", 6 },
  { 155, "Total work of breathing (WOBTOT) in Joules/L", 6 },
  { 156, "Leak compensation state (enable, disable, or blank)", 9 },
  { 157, "%LEAK", 6 },
  { 158, "LEAK @ PEEP", 6 },
  { 159, "VLEAK", 6 },
  { 160, "Reserved", 6 },
  { 161, "Reserved", 6 },
  { 162, "Reserved", 6 },
  { 163, "Reserved", 6 },
  { 164, "Reserved", 6 },
  { 165, "Reserved", 6 },
  { 166, "Reserved", 6 },
  { 167, "Reserved", 6 },
  { 168, "Reserved", 6 },
  { 169, "Reserved", 6 },
  { 170, "Reserved", 6 },
  { 171, "Reserved", 6 },
  { 172, "<ETX> End of transmission character (0x03)", 1 },
  { 173, "<CR> Terminating carriage return (0x0D)", 1 }

};

//=============================================================================
//=============================================================================
pb840::pb840()
  : _idatabytes(0),
    _idatafields(0)
{

}

//=============================================================================
pb840::~pb840()
{

}

//=============================================================================
bool pb840::rstatus_ok(const int istat)
{
  return(istat == rstat_ok);
}

//=============================================================================
const char *pb840::get_cmd_str(const int icmd)
{
  char  *pstr = 0;

  if      (icmd == cmd_rset) pstr = const_cast<char *>(_rset_command);
  else if (icmd == cmd_snda) pstr = const_cast<char *>(_snda_command);
  else if (icmd == cmd_sndf) pstr = const_cast<char *>(_sndf_command);
  return(pstr);
}

//=============================================================================
const int pb840::which_cmd(const string &sbuf)
{
  int icmd  = cmd_none;

  if      (sbuf.find(_rset_command) != string::npos) icmd = cmd_rset;
  else if (sbuf.find(_snda_command) != string::npos) icmd = cmd_snda;
  else if (sbuf.find(_sndf_command) != string::npos) icmd = cmd_sndf;
  return(icmd);
}

//=============================================================================
bool  pb840::isgooddata(const string &sbuf)
{
  bool  bret  = false;  //  assume no good
  while (1)
  {
    size_t ilen  = 0;
    ilen  = sbuf.length();
    if (ilen < 5) break;
    if (ilen < strlen(_sample_misca))  break;
    if (    (sbuf.at(ilen - 1) != _CR)
        ||  (sbuf.at(ilen - 2) != _ETX))  break;
    if (    (sbuf.at(0) != 'M')
        ||  (sbuf.at(1) != 'I')
        ||  (sbuf.at(2) != 'S')
        ||  (sbuf.at(3) != 'C'))  break;

    if ((sbuf.at(4) != 'A') && (sbuf.at(4) != 'F'))  break;
    bret  = true;
    break;
  }
  return(bret);
}

//=============================================================================
char *pb840::get_sample_data(const int icmd)
{
  char  *psd  = 0;
  if (icmd == cmd_snda) psd = const_cast<char *>(_sample_misca);
  if (icmd == cmd_sndf) psd = const_cast<char *>(_sample_miscf);
  return(psd);
}

//=============================================================================
char *pb840::get_data_name(const int icmd)
{
  static const  char  smisca[] = "MISCA";
  static const  char  smiscf[] = "MISCF";
  char  *psn  = 0;
  if (icmd == cmd_snda) psn = const_cast<char *>(smisca);
  if (icmd == cmd_sndf) psn = const_cast<char *>(smiscf);
  return(psn);
}

//=============================================================================
int pb840::receive(const string &sbuf)
{
  int istat = rstat_bad_input_data;

  _sbuf.erase(0, _sbuf.length());
  _idatabytes = 0;
  _idatafields  = 0;

  while (isgooddata(sbuf))
  {
    string  sb;
    sb.assign(sbuf);
    size_t  ipos_stx  = sb.find(_STX);
    size_t  ipos_etx  = sb.find(_ETX);
    stringstream  ssdatabytes (stringstream::in | stringstream::out);
    stringstream  ssdatafields (stringstream::in | stringstream::out);

    if (  (ipos_stx == string::npos)
       || (ipos_etx == string::npos)) break;

    if (sb[4] == 'A')
    {
      if (sb.length() < 14)  break;

      ssdatabytes.str(sb.substr(6, 3));
      ssdatafields.str(sb.substr(10, 2));
    }
    else if (sb[4] == 'F')
    {
      if (sb.length() < 16) break;

      ssdatabytes.str(sb.substr(6, 4));
      ssdatafields.str(sb.substr(11, 3));
    }
    else
    {
      break;
    }

    //  Number of bytes between STX and ETX
    ssdatabytes >> _idatabytes;

    //  Number of fields between STX and ETX
    ssdatafields >> _idatafields;

    _sbuf = sb;
    istat = rstat_ok;
    break;
  }
  return(istat);
}

//=============================================================================
int pb840::fieldinfo2tsv(
  const char *sfilename,
  const int reccount,
  const pb840_field_info *pfi)
{
  int ix    = 0;
  int fn    = 0;
  pb840_field_info  fir = { 0 };
  char  scrlf[]  = { 0x0d, 0x0a, 0x00 };
  std::ofstream  fs(sfilename, std::ios::out | std::ios::binary);

  if (!fs.is_open()) return(1);

  for (; ix < reccount; ix++)
  {
    fn  = ix + 1;
    fir = get_field_info(fn, reccount, pfi);
    fs  << fir.number
        << "\t"
        << fir.length
        << "\t"
        << fir.description
        << scrlf;
  }
  fs.close();
  return(0);
}

//=============================================================================
pb840_field_info  pb840::get_field_info(
  int fieldnumber,
  const int reccount,
  const pb840_field_info  *pfi)
{
  pb840_field_info  fir = { 0 };

  if (fieldnumber > 0)
  {
    fieldnumber--;
    if (fieldnumber < reccount) fir = pfi[fieldnumber];
  }
  return(fir);
}


//=============================================================================
//=============================================================================
pb840_misca::pb840_misca()
{

}

//=============================================================================
pb840_misca::~pb840_misca()
{

}

//=============================================================================
int pb840_misca::fieldinfo2tsv(const char *sfilename)
{
  int iret = pb840::fieldinfo2tsv(
    sfilename,
    get_field_info_rec_count(),
    _fi_misca);
  return(iret);
}

//=============================================================================
const int pb840_misca::get_field_info_rec_count()
{
  static  const int ifieldinforeccount(sizeof(_fi_misca) / sizeof(_fi_misca[0]));
  return(ifieldinforeccount);
}

//=============================================================================
pb840_field_info pb840_misca::get_field_info(int fieldnumber)
{
  pb840_field_info  fir = pb840::get_field_info(
    fieldnumber,
    get_field_info_rec_count(),
    _fi_misca);
  return(fir);
}

//=============================================================================
string pb840_misca::getsval(const int fieldnumber)
{
  int isize = _sbuf.length();
  int ipos  = 0;
  int flen  = 0;
  string  sval  = "";

  if ((fieldnumber > fn_stx) && (fieldnumber < fn_etx))
  {
    //  The specified field lies between the STX and ETX bytes.
    //  Find the start of the field and its length.
    int fcount  = fn_stx + 1;
    ipos  = 14;

    while(ipos < isize)
    {
      if (fcount == fieldnumber)
      {
        flen  = _fi_misca[fcount - 1].length;
        break;
      }
      ipos  += _fi_misca[fcount - 1].length + 1;
      fcount++;
    }
  }
  else
  {
    switch (fieldnumber)
    {
      case  fn_misca:
        ipos  = 0;
        flen  = 5;
        break;

      case  fn_bytecount:
        ipos  = 6,
        flen  = 3;
        break;

      case  fn_fieldcount:
        ipos  = 10,
        flen  = 2;
        break;

      case  fn_stx:
        ipos  = 13;
        flen  = 1;
        break;

      case  fn_etx:
        ipos  = isize - 2;
        flen  = 1;
        break;

      case  fn_cr:
        ipos  = isize - 1;
        flen  = 1;
        break;

      default:
        break;
    }
  }
  if (flen > 0)
  {
    sval  = _sbuf.substr(ipos, flen);

    //  Remove trailing spaces
    string::size_type pos = sval.find_last_not_of(' ');
    if (pos != string::npos) sval.erase(pos + 1);
  }
  return(sval);
}

//=============================================================================
int pb840_misca::getival(const int fieldnumber)
{
  int val  = -1;
  gettval(&val, fieldnumber);
  return(val);
}

//=============================================================================
double pb840_misca::getfval(const int fieldnumber)
{
  double val  = -1;
  gettval(&val, fieldnumber);
  return(val);
}

//=============================================================================
int pb840_misca::geteval(const int fieldnumber)
{
  int val = -1;
  string  sfv = getsval(fieldnumber);

  switch(fieldnumber)
  {
    case  fn_mode_setting:
      if (sfv.find("CMV") != string::npos)
      {
        val = mode_cmv;
      }
      else if (sfv.find("SIMV") != string::npos)
      {
        val = mode_simv;
      }
      else if (sfv.find("CPAP") != string::npos)
      {
        val = mode_cpap;
      }
      else if (sfv.find("BILEVL") != string::npos)
      {
        val = mode_bilevl;
      }
      else
      {
        val = mode_unknown;
      }
      break;

    case  fn_flow_pattern_setting:
      if (sfv.find("SQUARE") != string::npos)
      {
        val = flowpat_square;
      }
      else if (sfv.find("RAMP") != string::npos)
      {
        val = flowpat_ramp;
      }
      else
      {
        val = flowpat_unknown;
      }
      break;

    case  fn_100pct_o2_state:
    case  fn_alarm_silence_state:
      if (sfv.find("ON") != string::npos)
      {
        val = oostate_on;
      }
      else if (sfv.find("OFF") != string::npos)
      {
        val = oostate_off;
      }
      else
      {
        val = oostate_unknown;
      }
      break;

    case  fn_circuit_pressure_hi_alarm_status:
    case  fn_tidal_vol_exhaled_low_alarm_status:
    case  fn_minute_vol_exhaled_low_alarm_status:
    case  fn_respir_rate_hi_alarm_status:
    case  fn_no_o2_supply_alarm_status:
    case  fn_no_air_supply_alarm_status:
    case  fn_apnea_alarm_status:
    case  fn_apnea_alarm_status_2:
    case  fn_severe_occlusion_disconn_alarm_status:
      if (sfv.find("NORMAL") != string::npos)
      {
        val = alarmstatus_normal;
      }
      else if (sfv.find("ALARM") != string::npos)
      {
        val = alarmstatus_alarm;
      }
      else if (sfv.find("RESET") != string::npos)
      {
        val = alarmstatus_reset;
      }
      else
      {
        val = alarmstatus_unknown;
      }
      break;

    case  fn_mandatory_breaths_constant:
      if (sfv.find("(I-TIME") != string::npos)
      {
        val = manbreathconst_itime;
      }
      else if (sfv.find("I/E") != string::npos)
      {
        val = manbreathconst_ie;
      }
      else if (sfv.find("      ") != string::npos)
      {
        val = manbreathconst_etime_or_pcv_inactive;
      }
      else
      {
        val = manbreathconst_unknown;
      }
      break;

    default:
      break;
  }
  return(val);
}

#ifdef  _PB840_UNIT_TEST_ONLY
//=============================================================================
const char *pb840_misca::get_sample_data()
{
  return(_sample_misca);
}
#endif

//=============================================================================
//=============================================================================
pb840_miscf::pb840_miscf()
{
}

//=============================================================================
pb840_miscf::~pb840_miscf()
{
}

//=============================================================================
int pb840_miscf::fieldinfo2tsv(const char *sfilename)
{
  int iret = pb840::fieldinfo2tsv(
    sfilename,
    get_field_info_rec_count(),
    _fi_miscf);
  return(iret);
}

//=============================================================================
const int pb840_miscf::get_field_info_rec_count()
{
  static  const int ifieldinforeccount(sizeof(_fi_miscf) / sizeof(_fi_miscf[0]));
  return(ifieldinforeccount);
}

//=============================================================================
pb840_field_info  pb840_miscf::get_field_info(const int fieldnumber)
{
  pb840_field_info  fir = pb840::get_field_info(
    fieldnumber,
    get_field_info_rec_count(),
    _fi_miscf);
  return(fir);
}

//=============================================================================
string  pb840_miscf::getsval(const int fieldnumber)
{
  int isize = _sbuf.length();
  int ipos  = 0;
  int flen  = 0;
  string  sval  = "";

  if ((fieldnumber > fn_stx) && (fieldnumber < fn_etx))
  {
    //  The specified field lies between the STX and ETX bytes.
    //  Find the start of the field and its length.
    int fcount  = fn_stx + 1;
    ipos  = 16;

    while(ipos < isize)
    {
      if (fcount == fieldnumber)
      {
        flen  = _fi_miscf[fcount - 1].length;
        break;
      }
      ipos  += _fi_miscf[fcount - 1].length + 1;
      fcount++;
    }
  }
  else
  {
    switch (fieldnumber)
    {
      case  fn_miscf:
        ipos  = 0;
        flen  = 5;
        break;

      case  fn_bytecount:
        ipos  = 6,
        flen  = 4;
        break;

      case  fn_fieldcount:
        ipos  = 11,
        flen  = 3;
        break;

      case  fn_stx:
        ipos  = 13;
        flen  = 1;
        break;

      case  fn_etx:
        ipos  = isize - 2;
        flen  = 1;
        break;

      case  fn_cr:
        ipos  = isize - 1;
        flen  = 1;
        break;

      default:
        break;
    }
  }
  if (flen > 0)
  {
    sval  = _sbuf.substr(ipos, flen);

    //  Remove trailing spaces
    string::size_type pos = sval.find_last_not_of(' ');
    if (pos != string::npos) sval.erase(pos + 1);
  }
  return(sval);
}

//=============================================================================
int pb840_miscf::getival(const int fieldnumber)
{
  int val  = -1;
  gettval(&val, fieldnumber);
  return(val);
}

//=============================================================================
double pb840_miscf::getfval(const int fieldnumber)
{
  double val  = -1;
  gettval(&val, fieldnumber);
  return(val);
}

//=============================================================================
int pb840_miscf::geteval(const int fieldnumber)
{
  int val = -1;
  string  sfv = getsval(fieldnumber);

  switch(fieldnumber)
  {
    case  fn_venttype:
      if (sfv.find("NIV") != string::npos)
      {
        val = vtype_niv;
      }
      else if (sfv.find("INVASIVE") != string::npos)
      {
        val = vtype_invasive;
      }
      else
      {
        val = vtype_unknown;
      }
      break;

    case  fn_mode_setting:
      if (sfv.find("A/C") != string::npos)
      {
        val = mode_ac;
      }
      else if (sfv.find("SIMV") != string::npos)
      {
        val = mode_simv;
      }
      else if (sfv.find("SPONT") != string::npos)
      {
        val = mode_spont;
      }
      else if (sfv.find("BILEVL") != string::npos)
      {
        val = mode_bilevl;
      }
      else
      {
        val = mode_unknown;
      }
      break;

    default:
      break;
  }
  return(val);
}

#ifdef  _PB840_UNIT_TEST_ONLY
//=============================================================================
const char *pb840_miscf::get_sample_data()
{
  return(_sample_miscf);
}

#endif

