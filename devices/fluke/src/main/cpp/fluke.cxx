/*
 * @file    fluke.cxx
 * @brief   Defines the Fluke API
 *
 * @see Fluke Biomedical ProSim 6/8 Communications Interface full
 */
//=============================================================================

#ifdef _WIN32
#include <windows.h>
#include <time.h>
#include <string.h>
#include <stdio.h>
#include <sstream>
#include <iostream>
#include <iomanip>
#else
#include <iostream>
#include <iomanip>
#include <sstream>
#include <errno.h>
#include <string.h>
#include <string>
#include <limits.h>
#include <exception>
#endif

#include "fluke.h"
#include "fluke_listener.h"

using namespace std;
using namespace boost;

static const int kAsciiCR = 0x0d;
static const int kAsciiLF = 0x0a;


#ifdef TESTING_WITHOUT_DEVICE_ATTACHED
static const char* kfluke_sim_responses[] =
{
  "PROSIM6,1.00.06\r\n", // IDENT
  "LOCAL\r\n", // LOCAL or QMODE
  "RMAIN\r\n", // REMOTE or QMODE or EXIT
  "DIAG\r\n", // DIAG or QMODE
  "CAL\r\n", // CAL or QMODE
  "T\r\n", // QVAL
  "F\r\n", // QVAL
  "*\r\n", // Most commands return this for success
  "NOTHING\r\n", // QUSB
  "01,001,019,123456789ABCDEF00123456789ABCDEF\r\n" // Key Record string
};
#endif


/**
 * Convert character into a hexidecimal formatted string.
 * @param [in] cc Character to be represented in the string.
 * @return Returns hexidecimal string which represents cc.
 */
static string convert_to_hex(char cc)
{
  stringstream ss;
  ss << "(0x" << hex << setw(2) << setfill('0') << (int)cc << ")";
  return ss.str();
}


/**
 * Format a string so that each character is legible. Control characters
 * are represented in hexidecimal format.
 * @param [in] Pointer to string which contains the string to be converted.
 * @return Returns the converted string.
 */
static string get_human_readable_string(const string* str)
{
  stringstream ss;
  for (size_t index = 0; index < str->size(); index++)
  {
    if ((*str)[index] < 32 || (*str)[index] > 126)
    {
      // Print the linefeed along with hex so that the string retains format
      if ((*str)[index] == kAsciiLF) ss << (*str)[index];

      ss << convert_to_hex((*str)[index]);
    }
    else
      ss << (*str)[index];
  }

  return ss.str();
}


/**
 * Sleep a number of milliseconds
 * @param [in] millisecs number of milliseconds to sleep
 */
static void millisleep(int millisecs)
{
#ifdef _WIN32
  Sleep(millisecs);
#else
  static const int mega = 1000 * 1000;
  struct timespec tspec = { millisecs / 1000, (millisecs % 1000) * mega };
  struct timespec tspecdummy = { 0 };
  nanosleep(&tspec, &tspecdummy);
#endif
}


Fluke::Fluke(int in_fd, int out_fd)
  :_sequence_number(0),
   _input_fd(0),
   _output_fd(0),
   _validation_flag(false),
   _record_flag(false),
   _is_fluke_initialized(false),
   _init_option(INIT_OPTION_UNKNOWN)
{
  _input_fd = in_fd;
  _output_fd = out_fd;
}


Fluke::~Fluke()
{
}


FlukeListener::~FlukeListener()
{
}


int Fluke::Init()
{
  int istat = STATUS_OK;
  _is_fluke_initialized = true;

  static bool sent_qval = false;
  static bool initialize_complete = false;

  // Query the Validation flag state.
  if (!sent_qval)
  {
    istat = SendCommand(QVAL, "");
    if (!IsStatusOk(istat)) return istat;

    sent_qval = true;
    millisleep(100); // Allow some time to receive response
  }

  // If the Validation flag is false, then the Record flag is false.
  // If the Validation flag is true, then send command to set the Record
  // flag to false.
  if (_init_option == INIT_OPTION_VALIDATION_FALSE)
  {
    initialize_complete = true;
  }
  else if (_init_option == INIT_OPTION_VALIDATION_TRUE)
  {
    // Set Record flag to false
    istat = SendCommand(RECORD, "FALSE");
    if (!IsStatusOk(istat)) return istat;

    initialize_complete = true;
  }

  if (!IsStatusOk(istat)) return istat;
  else if (initialize_complete) return istat;
  else return RC_INIT_INCOMPLETE;
}


bool Fluke::OkToSendCommand()
{
  return !_validation_flag || !_record_flag;
}


bool Fluke::IsStatusOk(const int istat)
{
  return istat == STATUS_OK;
}


bool Fluke::HasBeenInitialized()
{
  return _is_fluke_initialized;
}


string Fluke::get_statusmsg()
{
  return _statusmsg;
}


unsigned int Fluke::get_sequence_number()
{
  return _sequence_number;
}


bool Fluke::get_validation_flag()
{
  return _validation_flag;
}


bool Fluke::get_record_flag()
{
  return _record_flag;
}


void Fluke::set_input_fd(const int in_fd)
{
  _input_fd = in_fd;
}


void Fluke::set_output_fd(const int out_fd)
{
  _output_fd = out_fd;
}


int Fluke::get_input_fd()
{
  return _input_fd;
}


int Fluke::get_output_fd()
{
  return _output_fd;
}


int Fluke::GetCommandName(const CommandName command, string* cmd_name)
{
  if (!cmd_name)
  {
    _statusmsg = "Fluke::GetCommandName cmd_name pointer is null.";
    return RC_BAD_PARAMETER;
  }

  int istat = STATUS_OK;

  switch (command)
  {
    case IDENT:
      *cmd_name = "IDENT";
    break;
    case LOCAL:
      *cmd_name = "LOCAL";
    break;
    case REMOTE:
      *cmd_name = "REMOTE";
    break;
    case QMODE:
      *cmd_name = "QMODE";
    break;
    case DIAG:
      *cmd_name = "DIAG="; // "=" means requires parameter
    break;
    case CAL:
      *cmd_name = "CAL=";
    break;
    case EXIT:
      *cmd_name = "EXIT";
    break;
    case RESET:
      *cmd_name = "RESET";
    break;
    case VALIDATION:
      *cmd_name = "VALIDATION=";
    break;
    case VALOFF:
      *cmd_name = "VALOFF";
    break;
    case QVAL:
      *cmd_name = "QVAL";
    break;
    case QUI:
      *cmd_name = "QUI";
    break;
    case QUSB:
      *cmd_name = "QUSB";
    break;
    case RECORD:
      *cmd_name = "RECORD=";
    break;
    case ENTER_BOOT_LOADER:
      *cmd_name = "$";
    break;
    case ECGRUN:
      *cmd_name = "ECGRUN=";
    break;
    case NSRA:
      *cmd_name = "NSRA=";
    break;
    case NSRP:
      *cmd_name = "NSRP=";
    break;
    case NSRAX:
      *cmd_name = "NSRAX=";
    break;
    case STDEV:
      *cmd_name = "STDEV=";
    break;
    case ECGAMPL:
      *cmd_name = "ECGAMPL=";
    break;
    case EART:
      *cmd_name = "EART=";
    break;
    case EARTSZ:
      *cmd_name = "EARTSZ=";
    break;
    case EARTLD:
      *cmd_name = "EARTLD=";
    break;
    case SPVWAVE:
      *cmd_name = "SPVWAVE=";
    break;
    case PREWAVE:
      *cmd_name = "PREWAVE=";
    break;
    case VNTWAVE:
      *cmd_name = "VNTWAVE=";
    break;
    case CNDWAVE:
      *cmd_name = "CNDWAVE=";
    break;
    case TVPPOL:
      *cmd_name = "TVPPOL=";
    break;
    case TVPAMPL:
      *cmd_name = "TVPAMPL=";
    break;
    case TVPWID:
      *cmd_name = "TVPWID=";
    break;
    case TVPWAVE:
      *cmd_name = "TVPWAVE=";
    break;
    case ACLSWAVE:
      *cmd_name = "ACLSWAVE=";
    break;
    case AFIB:
      *cmd_name = "AFIB=";
    break;
    case VFIB:
      *cmd_name = "VFIB=";
    break;
    case MONOVTACH:
      *cmd_name = "MONOVTACH=";
    break;
    case POLYVTACH:
      *cmd_name = "POLYVTACH=";
    break;
    case PULSE:
      *cmd_name = "PULSE=";
    break;
    case SQUARE:
      *cmd_name = "SQUARE=";
    break;
    case SINE:
      *cmd_name = "SINE=";
    break;
    case TRI:
      *cmd_name = "TRI=";
    break;
    case RDET:
      *cmd_name = "RDET=";
    break;
    case QRS:
      *cmd_name = "QRS=";
    break;
    case TALLT:
      *cmd_name = "TALLT=";
    break;
    case RESPRUN:
      *cmd_name = "RESPRUN=";
    break;
    case RESPWAVE:
      *cmd_name = "RESPWAVE=";
    break;
    case RESPRATE:
      *cmd_name = "RESPRATE=";
    break;
    case RESPRATIO:
      *cmd_name = "RESPRATIO=";
    break;
    case RESPAMPL:
      *cmd_name = "RESPAMPL=";
    break;
    case RESPBASE:
      *cmd_name = "RESPBASE=";
    break;
    case RESPLEAD:
      *cmd_name = "RESPLEAD=";
    break;
    case RESPAPNEA:
      *cmd_name = "RESPLEAD=";
    break;
    case IBPS:
      *cmd_name = "IBPS=";
    break;
    case IBPW:
      *cmd_name = "IBPW=";
    break;
    case IBPP:
      *cmd_name = "IBPP=";
    break;
    case IBPARTP:
      *cmd_name = "IBPARTP=";
    break;
    case IBPARTM:
      *cmd_name = "IBPARTM=";
    break;
    case IBPSNS:
      *cmd_name = "IBPSNS=";
    break;
    case TEMP:
      *cmd_name = "TEMP=";
    break;
    case COBASE:
      *cmd_name = "COBASE=";
    break;
    case COINJ:
      *cmd_name = "COINJ=";
    break;
    case COWAVE:
      *cmd_name = "COWAVE=";
    break;
    case CORUN:
      *cmd_name = "CORUN=";
    break;
    case NIBPRUN:
      *cmd_name = "NIBPRUN=";
    break;
    case NIBPP:
      *cmd_name = "NIBPP=";
    break;
    case NIBPV:
      *cmd_name = "NIBPV=";
    break;
    case NIBPES:
      *cmd_name = "NIBPES=";
    break;
    case NIBPTP:
      *cmd_name = "NIBPTP=";
    break;
    case NIBPLEAK:
      *cmd_name = "NIBPLEAK=";
    break;
    case LKOFF:
      *cmd_name = "LKOFF";
    break;
    case LKSTAT:
      *cmd_name = "LKSTAT";
    break;
    case NIBPPOP:
      *cmd_name = "NIBPPOP=";
    break;
    case POPOFF:
      *cmd_name = "POPOFF";
    break;
    case POPSTAT:
      *cmd_name = "POPSTAT";
    break;
    case PST:
      *cmd_name = "PST=";
    break;
    case PS:
      *cmd_name = "PS=";
    break;
    case PRESS:
      *cmd_name = "PRESS";
    break;
    case PRESSX:
      *cmd_name = "PRESSX";
    break;
    case ZPRESS:
      *cmd_name = "ZPRESS";
    break;
    case UZPRESS:
      *cmd_name = "UZPRESS";
    break;
    case CZPRESS:
      *cmd_name = "CZPRESS";
    break;
    case STFIND:
      *cmd_name = "STFIND";
    break;
    case STHOME:
      *cmd_name = "STHOME";
    break;
    case STGO:
      *cmd_name = "STGO=";
    break;
    case STCLOSE:
      *cmd_name = "STCLOSE";
    break;
    case STVENT:
      *cmd_name = "STVENT";
    break;
    case SAT:
      *cmd_name = "SAT=";
    break;
    case PERF:
      *cmd_name = "PERF=";
    break;
    case TRANS:
      *cmd_name = "TRANS=";
    break;
    case AMBM:
      *cmd_name = "AMBM=";
    break;
    case AMBS:
      *cmd_name = "AMBS=";
    break;
    case AMBF:
      *cmd_name = "AMBF=";
    break;
    case RESPM:
      *cmd_name = "RESPM=";
    break;
    case RESPS:
      *cmd_name = "RESPS=";
    break;
    case SPO2TYPE:
      *cmd_name = "SPO2TYPE=";
    break;
    case RATIO:
      *cmd_name = "RATIO=";
    break;
    case SPO2IDENT:
      *cmd_name = "SPO2IDENT";
    break;
    case QSTAT:
      *cmd_name = "QSTAT";
    break;
    case SPO2SLFTST:
      *cmd_name = "SPO2SLFTST";
    break;
    case SPO2SNGLTST:
      *cmd_name = "SPO2SNGLTST=";
    break;
    case PCAREV:
      *cmd_name = "PCAREV";
    break;
    case POFFATN:
      *cmd_name = "POFFATN";
    break;
    case PWRDWN:
      *cmd_name = "PWRDWN";
    break;
    case DSPAND:
      *cmd_name = "DSPAND=";
    break;
    case ECGPWR:
      *cmd_name = "ECGPWR=";
    break;
    case ECGATT:
      *cmd_name = "ECGATT=";
    break;
    case EDACLD:
      *cmd_name = "EDACLD=";
    break;
    case EDACCLR:
      *cmd_name = "EDACCLR=";
    break;
    case ECGDAC:
      *cmd_name = "ECGDAC=";
    break;
    case PACODAC:
      *cmd_name = "PACODAC=";
    break;
    case PACOP:
      *cmd_name = "PACOP=";
    break;
    case RESPATT:
      *cmd_name = "RESPATT=";
    break;
    case RESPDGR:
      *cmd_name = "RESPDGR=";
    break;
    case RESPDDGR:
      *cmd_name = "RESPDDGR=";
    break;
    case ECGXSPI:
      *cmd_name = "ECGXSPI";
    break;
    case ECGX5V:
      *cmd_name = "ECGX5V";
    break;
    case IBPPWR:
      *cmd_name = "IBPPWR=";
    break;
    case IBPRNG:
      *cmd_name = "IBPRNG=";
    break;
    case IBPDAC:
      *cmd_name = "IBPDAC=";
    break;
    case IBPXSPI:
      *cmd_name = "IBPXSPI=";
    break;
    case TCPWR:
      *cmd_name = "TCPWR=";
    break;
    case CODGR:
      *cmd_name = "CODGR=";
    break;
    case T4DGR:
      *cmd_name = "T4DGR=";
    break;
    case T4DDGR:
      *cmd_name = "T4DDGR=";
    break;
    case T71DGR:
      *cmd_name = "T71DGR=";
    break;
    case T71DDGR:
      *cmd_name = "T71DDGR=";
    break;
    case T72DGR:
      *cmd_name = "T72DGR=";
    break;
    case T72DDGR:
      *cmd_name = "T72DDGR=";
    break;
    case TCOXSPI:
      *cmd_name = "TCOXSPI";
    break;
    case AX5VPWR:
      *cmd_name = "AX5VPWR=";
    break;
    case AX12VPWR:
      *cmd_name = "AX12VPWR=";
    break;
    case AXPWRSW:
      *cmd_name = "AXPWRSW=";
    break;
    case AXOVC:
      *cmd_name = "AXOVC";
    break;
    case AXATN:
      *cmd_name = "AXATN";
    break;
    case AX:
      *cmd_name = "AX=";
    break;
    case AXOVERRIDE:
      *cmd_name = "AXOVERRIDE=";
    break;
    case ADCMUX:
      *cmd_name = "ADCMUX=";
    break;
    case ADC:
      *cmd_name = "ADC";
    break;
    case ADCF:
      *cmd_name = "ADCF=";
    break;
    case ADCO:
      *cmd_name = "ADCO=";
    break;
    case ADCM:
      *cmd_name = "ADCM";
    break;
    case ADCV:
      *cmd_name = "ADCV";
    break;
    case STRST:
      *cmd_name = "STRST=";
    break;
    case STSLP:
      *cmd_name = "STSLP=";
    break;
    case STEN:
      *cmd_name = "STEN=";
    break;
    case STCUR:
      *cmd_name = "STCUR=";
    break;
    case STDIR:
      *cmd_name = "STDIR=";
    break;
    case STEP:
      *cmd_name = "STEP=";
    break;
    case STFR:
      *cmd_name = "STFR=";
    break;
    case APMP:
      *cmd_name = "APMP=";
    break;
    case HIEPWR:
      *cmd_name = "HIEPWR=";
    break;
    case HIEDAC:
      *cmd_name = "HIEDAC=";
    break;
    case NI5VPWR:
      *cmd_name = "NI5VPWR=";
    break;
    case NI12VPWR:
      *cmd_name = "NI12VPWR=";
    break;
    case TSPWR:
      *cmd_name = "TSPWR=";
    break;
    case VALV:
      *cmd_name = "VALV=";
    break;
    case NIAND:
      *cmd_name = "NIAND=";
    break;
    case NIXSPI:
      *cmd_name = "NIXSPI";
    break;
    case BLIGHT:
      *cmd_name = "BLIGHT=";
    break;
    case LCDSOLID:
      *cmd_name = "LCDSOLID=";
    break;
    case KEYTEST:
      *cmd_name = "KEYTEST=";
    break;
    case BEEP:
      *cmd_name = "BEEP=";
    break;
    case BATDAT:
      *cmd_name = "BATDAT";
    break;
    case GETTEMP:
      *cmd_name = "GETTEMP";
    break;
    case GETRTC:
      *cmd_name = "GETRTC";
    break;
    case SETRTC:
      *cmd_name = "SETRTC=";
    break;
    case KEY:
      *cmd_name = "KEY=";
    break;
    case KEYACCEL:
      *cmd_name = "KEYACCEL=";
    break;
    case LCD:
      *cmd_name = "LCD=";
    break;
    case LCDSCREEN:
      *cmd_name = "LCDSCREEN";
    break;
    case LCDALL:
      *cmd_name = "LCDALL";
    break;
    default:
      _statusmsg = "Fluke::GetCommandName() Unknown command enumerator";
      istat = RC_UNKNOWN_COMMAND;
    break;
  }

  return istat;
}


int Fluke::SendCommand(CommandName command, const char* params)
{
  string command_name;
  int istat = STATUS_OK;
  int nbytes = -1;

  // Wait for condition to lock mutex
  unique_lock<mutex> lock(_lock);

  // Set timeout to occur in the 100 ms from now.
  system_time timeout = boost::get_system_time() +
    boost::posix_time::milliseconds(100);

  // Wait for condition to become true or timeout.
  _condition.timed_wait(lock, timeout);

  //_condition.wait(lock); // No Timeout?

  // Make sure Init() function was called before first command
  // is sent to the simulator.
  if (!HasBeenInitialized()) return RC_NOT_OK_TO_SEND_COMMAND;

  // Must be able to change flag states when the current flag states
  // do not allow commands to be sent.
  if (command != RECORD && command != VALIDATION && command != VALOFF)
    if (!OkToSendCommand()) return RC_NOT_OK_TO_SEND_COMMAND;

  // Convert CommandName enumerator into string
  istat = GetCommandName(command, &command_name);
  if (!IsStatusOk(istat)) return istat;

  // Construct complete command to send
  string device_command = command_name + params;
  device_command += kAsciiCR;

#ifdef TESTING_WITHOUT_DEVICE_ATTACHED
  try
  {
    _simulated_cmds_sent.push_back(command_name);
  }
  catch(std::exception& exc)
  {
    stringstream ss;
    ss << "Fluke::SendCommand() Exception thrown " << exc.what()
       << " Failed to push device command onto deque";
    _statusmsg = ss.str();
    return RC_EXCEPTION_THROWN;
  }
#endif

  // Increase sequence number for every command sent
  if (OkToSendCommand() || command == RECORD || command == VALIDATION
    || command == VALOFF)
  {
    _sequence_number++;
    cout << "Sending device command<" << get_human_readable_string(
      &device_command) << "> with sequence "
      "number (" << get_sequence_number() << ")..." << endl;
  }

  // Monitor each device command that modifies flag values.
  if (command == RECORD || command == VALIDATION || command == VALOFF)
  {
    try
    {
      // Store in FIFO queue each device command sent while current
      // flag states allow commands to be sent.
      _flag_cmds_sent.push_back(device_command);
    }
    catch(std::exception& exc)
    {
      stringstream ss;
      ss << "Fluke::SendCommand() Exception thrown " << exc.what()
        << " Failed to push device command onto deque";
      _statusmsg = ss.str();
      return RC_EXCEPTION_THROWN;
    }
  }

#ifndef TESTING_WITHOUT_DEVICE_ATTACHED


#ifdef _WIN32
  WriteFile(reinterpret_cast<HANDLE>(_output_fd),
    reinterpret_cast<const unsigned char*>(device_command.c_str()),
    strlen(device_command.c_str()),
    (LPDWORD)((void *)&nbytes), NULL);
#else
  nbytes = write(_output_fd,
    reinterpret_cast<const unsigned char*>(device_command.c_str()),
    strlen(device_command.c_str()));
#endif


  if (nbytes < 0)
  {
    _statusmsg = "Fluke::SendCommand write() error - ";
    _statusmsg += strerror(errno);
    return RC_WRITE_ERROR;
  }
#endif

  return istat;
}


int Fluke::ReceiveMessage()
{
  int istat = STATUS_OK;
  string message;

#ifdef TESTING_WITHOUT_DEVICE_ATTACHED

  string cmd_sent;

  if (!OkToSendCommand() && _flag_cmds_sent.size() == 0)
    message = kfluke_sim_responses[9];

  if (!_simulated_cmds_sent.empty())
  {
    cmd_sent = _simulated_cmds_sent.front();
    _simulated_cmds_sent.pop_front();

    if (!cmd_sent.compare("IDENT")) message = kfluke_sim_responses[0];
    else if (!cmd_sent.compare("LOCAL")) message = kfluke_sim_responses[1];
    else if (!cmd_sent.compare("REMOTE")) message = kfluke_sim_responses[2];
    else if (!cmd_sent.compare("QMODE")) message = kfluke_sim_responses[2];
    else if (!cmd_sent.compare("QUSB")) message = kfluke_sim_responses[8];
    else if (!cmd_sent.compare("DIAG=")) message = kfluke_sim_responses[3];
    else if (!cmd_sent.compare("CAL=")) message = kfluke_sim_responses[4];
    else if (!cmd_sent.compare("EXIT")) message = kfluke_sim_responses[2];
    else if (!cmd_sent.compare("QVAL")) message = kfluke_sim_responses[5];
    else message = kfluke_sim_responses[7]; // All other commands
  }

#endif

  // Beginning of code block. The mutex is released
  // (unlocked) when out of scope of this code block.
  {
    // Grab lock
    lock_guard<mutex> lock(_lock);

    while(1)
    {
#ifndef TESTING_WITHOUT_DEVICE_ATTACHED
      // Start reading one byte at a time
      while(1)
      {
        unsigned char byte = 0;
        int nbytes = 0;

        // Read character from port
        // Will block indefinitely if USB is idle
#ifdef _WIN32
        ReadFile(
          reinterpret_cast<HANDLE>(_input_fd),
          reinterpret_cast<char*>(&byte),
          1,
          (LPDWORD)((void *)&nbytes),
          NULL);
#else
        nbytes = read(_input_fd, reinterpret_cast<char*>(&byte), 1);
#endif

        if (nbytes > 0)
        {
          message += byte;

          if (message.size() >= 2)
          if (message[message.size() - 2] == kAsciiCR
            && message[message.size() - 1] == kAsciiLF) break;
        }
        else
        {
          istat = RC_READ_TIMEOUT;
          break;
        }

        millisleep(0);
      }
#endif

      if (IsStatusOk(istat))
      {
        SimulatorDataResponseType resp_type = RT_UNKNOWN_RESPONSE;

        // If it's okay to send command or a command was sent while
        // it was not okay, then we are expecting a response to a
        // device command.
        if (OkToSendCommand() || _flag_cmds_sent.size() > 0)
        {
          // If the command was sent while not okay, then it's possible to
          // receive a key record string instead of the command response.
          if (!OkToSendCommand() && IsKeyRecordString((char*)message.c_str(),
            strlen(message.c_str())))
            resp_type = RT_NON_DEVICE_COMMAND_RESPONSE;
          else
            resp_type = RT_DEVICE_COMMAND_RESPONSE;
        }
        else
        {
          resp_type = RT_NON_DEVICE_COMMAND_RESPONSE;
        }

        istat = ReceiveMessage(resp_type, (char*)message.c_str(),
          strlen(message.c_str()));
        if (!IsStatusOk(istat)) break;

#ifdef TESTING_WITHOUT_DEVICE_ATTACHED
        millisleep(1000);
        istat = RC_READ_TIMEOUT; // Fake the timeout for testing
#endif
        message.erase();
      }
      else
      {
        break;
      }
    }
  } // End of code block. Mutex is released

  millisleep(0);

  // Notify *this that the condition has changed.
  _condition.notify_one();

  // Give a chance for other thread waiting on *this to lock mutex
  millisleep(0);

  return istat;
}


int Fluke::ReceiveMessage(const SimulatorDataResponseType response_type,
  char* message, size_t size)
{
  if (!message)
  {
    _statusmsg = "Fluke::ReceiveMessage message pointer is null.";
    return RC_BAD_PARAMETER;
  }

  int istat = STATUS_OK;
  static bool first_init_resp = true;
  static bool second_init_resp = false;

  switch(response_type)
  {
    case RT_DEVICE_COMMAND_RESPONSE:

      // Make sure there is a command response
      if (size < 1) break;

      ReceiveDeviceCommandResponse(message, size, _sequence_number);

      // Check to see if there is a flag command waiting for a Response
      // (i.e., RECORD, VALIDATION, or VALOFF).
      if (_flag_cmds_sent.size() > 0)
      {
        // RECORD, VALIDATION, and VALOFF return '*' when successful executed
        if (message[0] == '*')
        {
          // Get first device command waiting in queue and remove it from
          // the queue
          string device_command = _flag_cmds_sent.front();
          _flag_cmds_sent.pop_front();

          // Convert string to all capital letters to prepare
          // for easy string comparisons.
          for (size_t ix = 0; ix < device_command.size(); ix++)
            device_command[ix] = toupper(device_command[ix]);

          // Determine which device command the command response belongs
          if (device_command.find("RECORD", 0) != std::string::npos)
          {
            // Check to see if the command sets the Record flag to
            // true or false.
            if (device_command.find("TRUE", 0) != std::string::npos)
            {
              _record_flag = true;
            }
            else if (device_command.find("FALSE", 0) != std::string::npos)
            {
              _record_flag = false;
            }
          }
          else if (device_command.find("VALIDATION", 0) != string::npos)
          {
            _validation_flag = true;
          }
          else if (device_command.find("VALOFF", 0) != string::npos)
          {
            // If the Validation flag is false, then the Record flag is false
            _validation_flag = false;
            if (_record_flag) _record_flag = false;
          }
        }
        else if (message[0] == '!') // Each error coded message starts with '!'
        { // The device command sent resulted in an error coded message
          // The flag values will not change.
          _flag_cmds_sent.pop_front(); // Remove the device command from queue
        }
      }

      // The first command response is a response to the device command QVAL
      if (first_init_resp)
      {
        first_init_resp = false; // Never enter this if statement again.

        // Expecting a string of three characters (i.e., "T\r\n" or "F\r\n")
        if (size != 3)
        {
          // The response doesn't belong to QVAL or an error coded
          // message was received.
          _statusmsg = "Fluke::ReceiveMessage() Cannot set validation "
            "flag value";
          return __LINE__;
        }

        // Check the first character for true ('T') or false ('F').
        if (message[0] == 'T')
        {
          // The command response says that the Validation flag is set
          // to true upon startup of the API. Prepare the Init() function's
          // course of action.
          _init_option = INIT_OPTION_VALIDATION_TRUE;
          _validation_flag = true;
        }
        else if (message[0] == 'F')
        {
          // The command response says that the Validation flag is set
          // to false upon startup of the API. Prepare the Init() function's
          // course of action.
          _init_option = INIT_OPTION_VALIDATION_FALSE;
          _validation_flag = false;
          _record_flag = false;
        }
        else
        {
          // The command response doesn't belong to QVAL
          _statusmsg = "Fluke::ReceiveMessage() Cannot set validation "
            "flag value";
          return __LINE__;
        }

        second_init_resp = true;
        break;
      }

      if (second_init_resp && _init_option == INIT_OPTION_VALIDATION_TRUE)
      {
        // The Validation flag is set to true and the next command sent by
        // the Init() function was the RECORD command (with "FALSE" parameter).
        second_init_resp = false; // Never enter this if statement again

        // A successful command response to the RECORD command
        // contains three characters (i.e., "*\r\n")
        if (size != 3)
        {
          // Received a different command response or an error coded message.
          _statusmsg = "Fluke::ReceiveMessage() Cannot set Record "
            "flag value";
          return __LINE__;
        }

        // A successful command response to the RECORD contains '*' as the
        // first character
        if (message[0] == '*')
        {
          _record_flag = false;
        }
        else
        {
          // The command response doesn't belong the the RECORD command.
          _statusmsg = "Fluke::ReceiveMessage() Initialization failed "
            "to set Record flag";
          return __LINE__;
        }

        break;
      }

    break;
    case RT_NON_DEVICE_COMMAND_RESPONSE:
      // Check for empty message.
      if (size < 1) break;

      ReceiveResponse(message, size);
    break;
    default:
      _statusmsg = "Fluke::ReceiveMessage() Unknown response_type enumerator";
      istat = RC_UNKNOWN_RESPONSE_TYPE;
    break;
  }

  return istat;
}


bool Fluke::IsKeyRecordString(char* message, size_t size)
{
  // All Key Record Strings have 45 characters
  if (size != 45) return false;

  int iindex = 0;

  // The first 2 characters are digits which represent the key code.
  for (; iindex < 2; iindex++)
  {
    // Checks for char < '0' or char > '9'
    if (message[iindex] < 0x30 || message[iindex] > 0x39)
      return false;
  }

  // Check for comma
  if (message[iindex] != 0x2C) return false;
  iindex++;

  // The next 3 characters are digits which represent the number of cycles
  // the key was pressed.
  for (; iindex < 6; iindex++)
  {
    // Checks for char < '0' or char > '9'
    if (message[iindex] < 0x30 || message[iindex] > 0x39)
      return false;
  }

  // Check for comma
  if (message[iindex] != 0x2C) return false;
  iindex++;

  // The next 3 characters are digits which represent the LCD screen number.
  for (; iindex < 10; iindex++)
  {
    // Checks for char < '0' or char > '9'
    if (message[iindex] < 0x30 || message[iindex] > 0x39)
      return false;
  }

  // Check for comma
  if (message[iindex] != 0x2C) return false;
  iindex++;

  // The next 32 characters are hexidecimal digits which represent
  // the LCD screen checksum.
  for (; iindex < 43; iindex++)
  {
    if (message[iindex] < 0x30 // char < '0'
      || (message[iindex] > 0x39 && message[iindex] < 0x41) // '9' < char < 'A'
      || (message[iindex] > 0x46 && message[iindex] < 0x61) // 'F' < char < 'a'
      || message[iindex] > 0x66) // char > 'f'
      return false;
  }

  // Check for carriage return
  if (message[iindex] != kAsciiCR) return false;
  iindex++;

  // Check for linefeed
  if (message[iindex] != kAsciiLF) return false;

  return true;
}


int Fluke::ReceiveDeviceCommandResponse(char* message,
  size_t size, unsigned int sequence_number)
{
  return 0;
}


int Fluke:: ReceiveResponse(char* message, size_t size)
{
  return 0;
}
