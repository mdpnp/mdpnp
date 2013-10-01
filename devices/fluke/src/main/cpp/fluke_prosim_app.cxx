/*
 * @file    fluke_prosim_app.cxx
 */
//=============================================================================

#include "dicesprojstd.h"
#include <iostream>
#include <sstream>
#include <iomanip>
#include <map>
#include <stdio.h>
#include <stdlib.h>
#include "kbhit_util.h"
#include "dd_fluke_prosim.h"
#include "my_fluke_impl.h"
#include "rs232_comm.h"
#include "rti_dds_impl.h"
#include "fluke_prosim_app.h"

using namespace std;


#ifdef _WIN32
#define STRNCASECMP   _strnicmp
#else
#define STRNCASECMP   strncasecmp
#endif
#define IS_OPTION(str, option) (STRNCASECMP(str, option, strlen(str)) == 0)

static const int kAsciiCr = 0x0D;
static const int kAsciiLf = 0x0A;
static const int kAsciiEsc = 0x1B;
static const int kAsciiBs = 0x08;
static const int kAsciiSpace = 0x20;
static const int kAsciiEquals = 0x3D;
static const int kDefaultBaudrate = 115200;


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


FlukeProsimApp::FlukeProsimApp()
  : _baudrate(kDefaultBaudrate),
    _domain_id(0),
    _fluke_api(0)
{
  _fluke_api = new (nothrow) MyFluke(-1, -1);
}


FlukeProsimApp::~FlukeProsimApp()
{
  if(_fluke_api) delete _fluke_api;
}


bool FlukeProsimApp::IsStatusOk(const int istat)
{
  return istat == RC_STATUS_OK;
}


string FlukeProsimApp::get_statusmsg()
{
  return _statusmsg;
}


/**
 * Print help message.
 * @param [in] thisprogramname The name of the executable (argv[0]).
 * @param [in] exitprogram 0 or 1 value signifying whether or not to close
 * application after help message is printed.
 */
void FlukeProsimApp::CmdLineHelp(const char *thisprogramname, int exitprogram)
{
  const char* smsg =
    "\nUSAGE:\n"
    "   %s {-portname <name>} [-baud <rate>]\n"
    "   {} Required\n"
    "   [] Optional\n"
    "   |  Separator between option choices\n"
    "where:\n"
    "   -help              Print help message.\n"
    "   -portname <name>   /dev/ttySn or /dev/ttyUSBn, /dev/ttyACMn or COMn\n"
    "                      (required, no default)\n"
    "   -baud <rate>       Baud rate (optional, default 115200)\n"
    "   -domainId <id>     DDS domain id (option, default 0)\n"
    "\n"
    "Brief Description:\n"
    "Demo application that uses the Fluke API.\n\n";

  printf(smsg, thisprogramname);
  if (exitprogram) exit(RC_STATUS_OK);
}


int FlukeProsimApp::ParseCommandLineArgs(int argc, char* argv[])
{
  // Load command line parameters
  for (int ix = 0; ix < argc; ++ix)
  {
    if (IS_OPTION(argv[ix], "-help") || IS_OPTION(argv[ix], "?"))
    {
      CmdLineHelp(argv[0], 1);
    }
    else if (IS_OPTION(argv[ix], "-portname"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: "
          "Missing <name> after -portname";
        return RC_FAIL;
      }
      _portname = argv[ix];
    }
    else if (IS_OPTION(argv[ix], "-baud"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: "
          "Missing <number> after -baud";
        return RC_FAIL;
      }
      _baudrate = atoi(argv[ix]);
    }
    else if (IS_OPTION(argv[ix], "-domainId"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: "
          "Missing <id> after -domainId";
        return RC_FAIL;
      }
      _domain_id = atoi(argv[ix]);
    }
  }

  if (_portname.empty())
  {
    _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: Set "
      "portname with option -portname. Enter option -help for help message";
    return RC_FAIL;
  }

  return RC_STATUS_OK;
}


int FlukeProsimApp::RunApp()
{
  int istat = 0;
  int port_handle = 0;
  int cc = 0;
  DDS_DomainId_t domain_id = _domain_id;
  RS232Comm rs232_comm;
  FlukeInterfaceRTIDDSIMPL rti_dds_impl;

  // Open port using configuration settings and get port handle.
  istat = rs232_comm.OpenPort(_portname, _baudrate, &port_handle);
  if (!RS232Comm::IsStatusOk(istat))
  {
    _statusmsg = rs232_comm.get_statusmsg();
    istat = RC_FAIL;
    return istat;
  }

  // Prepare API to read and write from the same open port.
  _fluke_api->set_input_fd(port_handle);
  _fluke_api->set_output_fd(port_handle);

  // Create DDS domain participant
  cout << "Creating participant on domain (" << domain_id << ")..." << endl;
  istat = rti_dds_impl.CreateParticipant(domain_id);
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = rti_dds_impl.get_statusmsg();
    istat = RC_FAIL;
    goto exit_fn;
  }

  // Create the DDS datawriters that the fluke API implementation needs
  istat = _fluke_api->CreateDDSDataWriters(&rti_dds_impl);
  if (!MyFluke::IsStatusOk(istat))
  {
	_statusmsg = _fluke_api->get_statusmsg();
	istat = RC_FAIL;
	goto exit_fn;
  }

  // Create a DDS datareader for commands
  cout << "Creating DataReader..."<< endl;
  istat = rti_dds_impl.CreateFlukeCommandDataReader();
  if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
  {
    _statusmsg = rti_dds_impl.get_statusmsg();
    istat = RC_FAIL;
    goto exit_fn;
  }

  // Run the application until operator presses the escape key.
  while (cc != kAsciiEsc)
  {
	int api_init_istat = 0;
	int rcv_msg_istat = 0;
	static bool init_finished = false;

	millisleep(0);

	// Check for key press
	cc = 0;
	cc = kbhit_util::kbhit();

	// Initialize Validation and Record flags
	if (!init_finished)
    {
      api_init_istat = _fluke_api->Init();
      if (!MyFluke::IsStatusOk(api_init_istat) &&
        api_init_istat != MyFluke::RC_INIT_INCOMPLETE)
      {
        istat = RC_FAIL;
        break;
      }
    }

	// Using the Fluke API to read open port for data
	rcv_msg_istat = _fluke_api->ReceiveMessage();

	// Check status and ignore timeout status
	if (!MyFluke::IsStatusOk(rcv_msg_istat) &&
      rcv_msg_istat != MyFluke::RC_READ_TIMEOUT)
	{
	  _statusmsg = _fluke_api->get_statusmsg();
	  istat = RC_FAIL;
	  break;
	}

    // Check to see if initialize finished
	if (!init_finished)
	{
      if (!MyFluke::IsStatusOk(api_init_istat)) continue;

      init_finished = true;
    }

    DDS_KeyedString instance(
      FlukeInterfaceRTIDDSIMPL::get_max_dds_str_alloc_size(),
      FlukeInterfaceRTIDDSIMPL::get_max_dds_str_alloc_size());

    if (!rti_dds_impl.IsCommandWaiting())
    {
      istat = rti_dds_impl.FlukeCommandDataReaderWaitForSample();
      if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
      {
        if(istat != FlukeInterfaceRTIDDSIMPL::RC_WAITSET_TIMEOUT) break;
      }
    }

    if (rti_dds_impl.IsCommandWaiting())
    {
      string command_str;
      istat = rti_dds_impl.GetCommandWaiting(&command_str);
      if (!FlukeInterfaceRTIDDSIMPL::IsStatusOk(istat))
      {
        cout << rti_dds_impl.get_statusmsg() << endl;
        break;
      }

      istat = ProcessData(command_str);
      if (!IsStatusOk(istat))
      {
        if (istat != RC_BAD_SAMPLE_FORMAT) break;

        cerr << _statusmsg << endl;
        _statusmsg.clear();
      }
    }
  }

  cout << endl;

exit_fn:

  istat = rti_dds_impl.ParticipantShutdown();

  rs232_comm.ClosePort();

  return istat;
}


int FlukeProsimApp::ProcessData(const string& cmd)
{
  size_t cr_pos = string::npos;
  size_t lf_pos = string::npos;
  size_t esc_pos = string::npos;
  size_t equals_pos = string::npos;
  size_t end_of_command = string::npos;
  string command = cmd;
  string parameters;
  string command_name;
  Fluke::CommandName name_enum = Fluke::NOTACOMMAND;

  // @see Fluke Biomedical ProSim 6/8 Communication interface document
  // The command protocol section describes the parsing of the Fluke
  // command below.

  esc_pos = command.find(0x1B, 0);
  if (esc_pos != string::npos)
  {
    _statusmsg =  "FlukeProsimApp::ProcessData() The Fluke command "
      "contains an Escape character. The command is erased.";
    return RC_BAD_SAMPLE_FORMAT;
  }

  // Convert command to all capital letters.
  for(size_t ix = 0; ix < command.length(); ix++)
    command[ix] = toupper(command[ix]);

  // Remove all space characters from the command.
  size_t iindex = 0;
  while (1)
  {
    iindex = command.find(kAsciiSpace, iindex);
    if (iindex == string::npos) break;

    command.erase(iindex, 1);
  }

  // Handle all backspace characters from the command.
  iindex = 0;
  while (1)
  {
    iindex = command.find(kAsciiBs, iindex);

    if (iindex == string::npos) break;

    // The backspace (BS) character erases the last character from the command
    if (iindex == 0) command.erase(iindex, 1); // BS is first char
    if (iindex >= 1)
    {
      command.erase(iindex - 1, 2); // BS is not first char
      iindex--;
    }
  }

  // The communication interface document states that commands must be
  // terminated by CR or LF or both
  cr_pos = command.find(kAsciiCr, 0);
  lf_pos = command.find(kAsciiLf, 0);

  if (cr_pos == string::npos && lf_pos == string::npos)
  {
    _statusmsg =  "FlukeProsimApp::ProcessData() A valid Fluke command "
      "is terminated by a CR or LF or both.";
    return RC_BAD_SAMPLE_FORMAT;
  }

  // A terminating character exits.
  // Determine if the a single character terminates the the command (CR or LF)
  // or both characters (CRLF or LFCR).
  if (cr_pos < lf_pos) end_of_command = cr_pos;
  if (lf_pos == (cr_pos + 1)) end_of_command = lf_pos; // Found CRLF pair

  if (lf_pos < cr_pos) end_of_command = lf_pos;
  if (cr_pos == (lf_pos + 1)) end_of_command = cr_pos; // Found LFCR pair

  // Search for an equals sign in the command.
  equals_pos = command.find(kAsciiEquals, 0);
  if (equals_pos != string::npos)
  {
    // Parse command name before equals sign, and parameters after equals
    // sign.
    command_name = command.substr(0, equals_pos);
    parameters = command.substr(equals_pos + 1,
      end_of_command - (equals_pos + 1));
  }
  else
  {
    // Found a command with no parameters.
    command_name = command.substr(0, end_of_command);
  }

  name_enum = static_cast<Fluke::CommandName>(
    ConvertStringCommandNameToEnum(command_name));
  if (name_enum == Fluke::NOTACOMMAND)
  {
    stringstream ss;
    ss << "ProcessData() unable to convert " << command_name
       << " to enumerator" << endl;
    _statusmsg = ss.str();
    return RC_BAD_SAMPLE_FORMAT;
  }

  int api_stat = _fluke_api->SendCommand(name_enum, parameters.c_str());
  if (!Fluke::IsStatusOk(api_stat))
  {
    // Print message based on the status received
    if (api_stat != MyFluke::RC_NOT_OK_TO_SEND_COMMAND)
    {
      _statusmsg = _fluke_api->get_statusmsg();
      return RC_FAIL;
    }

    cerr << "FlukeProsimApp::ProcessData() Warning: Cannot send command "
      "with the current flag states" << endl;
  }

  return RC_STATUS_OK;
}

int FlukeProsimApp::ConvertStringCommandNameToEnum(const string &command_name)
{
  if (!command_name.compare("IDENT")) return Fluke::IDENT;
  else if (!command_name.compare("LOCAL")) return Fluke::LOCAL;
  else if (!command_name.compare("REMOTE")) return Fluke::REMOTE;
  else if (!command_name.compare("QMODE")) return Fluke::QMODE;
  else if (!command_name.compare("DIAG")) return Fluke::DIAG;
  else if (!command_name.compare("CAL")) return Fluke::CAL;
  else if (!command_name.compare("EXIT")) return Fluke::EXIT;
  else if (!command_name.compare("RESET")) return Fluke::RESET;
  else if (!command_name.compare("VALIDATION")) return Fluke::VALIDATION;
  else if (!command_name.compare("VALOFF")) return Fluke::VALOFF;
  else if (!command_name.compare("QVAL")) return Fluke::QVAL;
  else if (!command_name.compare("QUI")) return Fluke::QUI;
  else if (!command_name.compare("QUSB")) return Fluke::QUSB;
  else if (!command_name.compare("RECORD")) return Fluke::RECORD;
  else if (!command_name.compare("$")) return Fluke::ENTER_BOOT_LOADER;
  else if (!command_name.compare("ECGRUN")) return Fluke::ECGRUN;
  else if (!command_name.compare("NSRA")) return Fluke::NSRA;
  else if (!command_name.compare("NSRP")) return Fluke::NSRP;
  else if (!command_name.compare("NSRAX")) return Fluke::NSRAX;
  else if (!command_name.compare("STDEV")) return Fluke::STDEV;
  else if (!command_name.compare("ECGAMPL")) return Fluke::ECGAMPL;
  else if (!command_name.compare("EART")) return Fluke::EART;
  else if (!command_name.compare("EARTSZ")) return Fluke::EARTSZ;
  else if (!command_name.compare("EARTLD")) return Fluke::EARTLD;
  else if (!command_name.compare("SPVWAVE")) return Fluke::SPVWAVE;
  else if (!command_name.compare("PREWAVE")) return Fluke::PREWAVE;
  else if (!command_name.compare("VNTWAVE")) return Fluke::VNTWAVE;
  else if (!command_name.compare("CNDWAVE")) return Fluke::CNDWAVE;
  else if (!command_name.compare("TVPPOL")) return Fluke::TVPPOL;
  else if (!command_name.compare("TVPAMPL")) return Fluke::TVPAMPL;
  else if (!command_name.compare("TVPWID")) return Fluke::TVPWID;
  else if (!command_name.compare("TVPWAVE")) return Fluke::TVPWAVE;
  else if (!command_name.compare("ACLSWAVE")) return Fluke::ACLSWAVE;
  else if (!command_name.compare("AFIB")) return Fluke::AFIB;
  else if (!command_name.compare("VFIB")) return Fluke::VFIB;
  else if (!command_name.compare("MONOVTACH")) return Fluke::MONOVTACH;
  else if (!command_name.compare("POLYVTACH")) return Fluke::POLYVTACH;
  else if (!command_name.compare("PULSE")) return Fluke::PULSE;
  else if (!command_name.compare("SQUARE")) return Fluke::SQUARE;
  else if (!command_name.compare("SINE")) return Fluke::SINE;
  else if (!command_name.compare("TRI")) return Fluke::TRI;
  else if (!command_name.compare("RDET")) return Fluke::RDET;
  else if (!command_name.compare("QRS")) return Fluke::QRS;
  else if (!command_name.compare("TALLT")) return Fluke::TALLT;
  else if (!command_name.compare("RESPRUN")) return Fluke::RESPRUN;
  else if (!command_name.compare("RESPWAVE")) return Fluke::RESPWAVE;
  else if (!command_name.compare("RESPRATE")) return Fluke::RESPRATE;
  else if (!command_name.compare("RESPRATIO")) return Fluke::RESPRATIO;
  else if (!command_name.compare("RESPAMPL")) return Fluke::RESPAMPL;
  else if (!command_name.compare("RESPBASE")) return Fluke::RESPBASE;
  else if (!command_name.compare("RESPLEAD")) return Fluke::RESPLEAD;
  else if (!command_name.compare("RESPAPNEA")) return Fluke::RESPAPNEA;
  else if (!command_name.compare("IBPS")) return Fluke::IBPS;
  else if (!command_name.compare("IBPW")) return Fluke::IBPW;
  else if (!command_name.compare("IBPP")) return Fluke::IBPP;
  else if (!command_name.compare("IBPARTP")) return Fluke::IBPARTP;
  else if (!command_name.compare("IBPARTM")) return Fluke::IBPARTM;
  else if (!command_name.compare("IBPSNS")) return Fluke::IBPSNS;
  else if (!command_name.compare("TEMP")) return Fluke::TEMP;
  else if (!command_name.compare("COBASE")) return Fluke::COBASE;
  else if (!command_name.compare("COINJ")) return Fluke::COINJ;
  else if (!command_name.compare("COWAVE")) return Fluke::COWAVE;
  else if (!command_name.compare("CORUN")) return Fluke::CORUN;
  else if (!command_name.compare("NIBPRUN")) return Fluke::NIBPRUN;
  else if (!command_name.compare("NIBPP")) return Fluke::NIBPP;
  else if (!command_name.compare("NIBPV")) return Fluke::NIBPV;
  else if (!command_name.compare("NIBPES")) return Fluke::NIBPV;
  else if (!command_name.compare("NIBPTP")) return Fluke::NIBPTP;
  else if (!command_name.compare("NIBPLEAK")) return Fluke::NIBPLEAK;
  else if (!command_name.compare("LKOFF")) return Fluke::LKOFF;
  else if (!command_name.compare("LKSTAT")) return Fluke::LKSTAT;
  else if (!command_name.compare("NIBPPOP")) return Fluke::NIBPPOP;
  else if (!command_name.compare("POPOFF")) return Fluke::POPOFF;
  else if (!command_name.compare("POPSTAT")) return Fluke::POPSTAT;
  else if (!command_name.compare("PST")) return Fluke::PST;
  else if (!command_name.compare("PS")) return Fluke::PS;
  else if (!command_name.compare("PRESS")) return Fluke::PRESS;
  else if (!command_name.compare("PRESSX")) return Fluke::PRESSX;
  else if (!command_name.compare("ZPRESS")) return Fluke::ZPRESS;
  else if (!command_name.compare("UZPRESS")) return Fluke::UZPRESS;
  else if (!command_name.compare("CZPRESS")) return Fluke::CZPRESS;
  else if (!command_name.compare("STFIND")) return Fluke::STFIND;
  else if (!command_name.compare("STHOME")) return Fluke::STHOME;
  else if (!command_name.compare("STGO")) return Fluke::STGO;
  else if (!command_name.compare("STCLOSE")) return Fluke::STCLOSE;
  else if (!command_name.compare("STVENT")) return Fluke::STVENT;
  else if (!command_name.compare("SAT")) return Fluke::SAT;
  else if (!command_name.compare("PERF")) return Fluke::PERF;
  else if (!command_name.compare("TRANS")) return Fluke::TRANS;
  else if (!command_name.compare("AMBM")) return Fluke::AMBM;
  else if (!command_name.compare("AMBS")) return Fluke::AMBS;
  else if (!command_name.compare("AMBF")) return Fluke::AMBF;
  else if (!command_name.compare("RESPM")) return Fluke::RESPM;
  else if (!command_name.compare("RESPS")) return Fluke::RESPS;
  else if (!command_name.compare("SPO2TYPE")) return Fluke::SPO2TYPE;
  else if (!command_name.compare("RATIO")) return Fluke::RATIO;
  else if (!command_name.compare("SPO2IDENT")) return Fluke::SPO2IDENT;
  else if (!command_name.compare("QSTAT")) return Fluke::QSTAT;
  else if (!command_name.compare("SPO2SLFTST")) return Fluke::SPO2SLFTST;
  else if (!command_name.compare("SPO2SNGLTST")) return Fluke::SPO2SNGLTST;
  else if (!command_name.compare("PCAREV")) return Fluke::PCAREV;
  else if (!command_name.compare("POFFATN")) return Fluke::POFFATN;
  else if (!command_name.compare("PWRDWN")) return Fluke::PWRDWN;
  else if (!command_name.compare("DSPAND")) return Fluke::DSPAND;
  else if (!command_name.compare("ECGPWR")) return Fluke::ECGPWR;
  else if (!command_name.compare("ECGATT")) return Fluke::ECGATT;
  else if (!command_name.compare("EDACLD")) return Fluke::EDACLD;
  else if (!command_name.compare("EDACCLR")) return Fluke::EDACCLR;
  else if (!command_name.compare("ECGDAC")) return Fluke::ECGDAC;
  else if (!command_name.compare("PACODAC")) return Fluke::PACODAC;
  else if (!command_name.compare("PACOP")) return Fluke::PACOP;
  else if (!command_name.compare("RESPATT")) return Fluke::RESPATT;
  else if (!command_name.compare("RESPDGR")) return Fluke::RESPDGR;
  else if (!command_name.compare("RESPDDGR")) return Fluke::RESPDDGR;
  else if (!command_name.compare("ECGXSPI")) return Fluke::ECGXSPI;
  else if (!command_name.compare("ECGX5V")) return Fluke::ECGX5V;
  else if (!command_name.compare("IBPPWR")) return Fluke::IBPPWR;
  else if (!command_name.compare("IBPRNG")) return Fluke::IBPRNG;
  else if (!command_name.compare("IBPDAC")) return Fluke::IBPDAC;
  else if (!command_name.compare("IBPXSPI")) return Fluke::IBPXSPI;
  else if (!command_name.compare("TCPWR")) return Fluke::TCPWR;
  else if (!command_name.compare("CODGR")) return Fluke::CODGR;
  else if (!command_name.compare("CODDGR")) return Fluke::CODDGR;
  else if (!command_name.compare("T4DGR")) return Fluke::T4DGR;
  else if (!command_name.compare("T4DDGR")) return Fluke::T4DDGR;
  else if (!command_name.compare("T71DGR")) return Fluke::T71DGR;
  else if (!command_name.compare("T71DDGR")) return Fluke::T71DDGR;
  else if (!command_name.compare("T72DGR")) return Fluke::T72DGR;
  else if (!command_name.compare("T72DDGR")) return Fluke::T72DGR;
  else if (!command_name.compare("TCOXSPI")) return Fluke::TCOXSPI;
  else if (!command_name.compare("AX5VPWR")) return Fluke::AX5VPWR;
  else if (!command_name.compare("AX12VPWR")) return Fluke::AX12VPWR;
  else if (!command_name.compare("AXPWRSW")) return Fluke::AXPWRSW;
  else if (!command_name.compare("AXOVC")) return Fluke::AXOVC;
  else if (!command_name.compare("AXATN")) return Fluke::AXATN;
  else if (!command_name.compare("AX")) return Fluke::AX;
  else if (!command_name.compare("AXOVERRIDE")) return Fluke::AXOVERRIDE;
  else if (!command_name.compare("ADCMUX")) return Fluke::ADCMUX;
  else if (!command_name.compare("ADC")) return Fluke::ADC;
  else if (!command_name.compare("ADCF")) return Fluke::ADCF;
  else if (!command_name.compare("ADCO")) return Fluke::ADCO;
  else if (!command_name.compare("ADCM")) return Fluke::ADCM;
  else if (!command_name.compare("ADCV")) return Fluke::ADCV;
  else if (!command_name.compare("STRST")) return Fluke::STRST;
  else if (!command_name.compare("STSLP")) return Fluke::STSLP;
  else if (!command_name.compare("STEN")) return Fluke::STEN;
  else if (!command_name.compare("STCUR")) return Fluke::STCUR;
  else if (!command_name.compare("STDIR")) return Fluke::STDIR;
  else if (!command_name.compare("STEP")) return Fluke::STEP;
  else if (!command_name.compare("STFR")) return Fluke::STFR;
  else if (!command_name.compare("APMP")) return Fluke::APMP;
  else if (!command_name.compare("HIEPWR")) return Fluke::HIEPWR;
  else if (!command_name.compare("HIEDAC")) return Fluke::HIEDAC;
  else if (!command_name.compare("NI5VPWR")) return Fluke::NI5VPWR;
  else if (!command_name.compare("NI12VPWR")) return Fluke::NI12VPWR;
  else if (!command_name.compare("TSPWR")) return Fluke::TSPWR;
  else if (!command_name.compare("VALV")) return Fluke::VALV;
  else if (!command_name.compare("NIAND")) return Fluke::NIAND;
  else if (!command_name.compare("NIXSPI")) return Fluke::NIXSPI;
  else if (!command_name.compare("BLIGHT")) return Fluke::BLIGHT;
  else if (!command_name.compare("LCDSOLID")) return Fluke::LCDSOLID;
  else if (!command_name.compare("KEYTEST")) return Fluke::KEYTEST;
  else if (!command_name.compare("BEEP")) return Fluke::BEEP;
  else if (!command_name.compare("BATDAT")) return Fluke::BATDAT;
  else if (!command_name.compare("GETTEMP")) return Fluke::GETTEMP;
  else if (!command_name.compare("GETRTC")) return Fluke::GETRTC;
  else if (!command_name.compare("SETRTC")) return Fluke::SETRTC;
  else if (!command_name.compare("KEY")) return Fluke::KEY;
  else if (!command_name.compare("KEYACCEL")) return Fluke::KEYACCEL;
  else if (!command_name.compare("LCD")) return Fluke::LCD;
  else if (!command_name.compare("LCDSCREEN")) return Fluke::LCDSCREEN;
  else if (!command_name.compare("LCDALL")) return Fluke::LCDALL;

  return Fluke::NOTACOMMAND;
}

