/*
 * @file    fluke_prosim_app.cxx
 * @brief   Class handles command line options and open port.
 *
 */
//=============================================================================

#include <iostream>
#include <sstream>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "rs232.h"
#include "fluke_prosim_app.h"

using namespace std;

static const int kDefaultBaudrate = 115200;

#ifdef _WIN32
#define STRNCASECMP   _strnicmp
#else
#define STRNCASECMP   strncasecmp
#endif
#define IS_OPTION(str, option) (STRNCASECMP(str, option, strlen(str)) == 0)


FlukeProSim8App::FlukeProSim8App()
  :_baudrate(kDefaultBaudrate),
   _portnumber(-1)
{
}


FlukeProSim8App::~FlukeProSim8App()
{
}


bool FlukeProSim8App::IsStatusOk(const int istat)
{
  return istat == 0;
}


string FlukeProSim8App::get_statusmsg()
{
  return _statusmsg;
}


int FlukeProSim8App::get_portnumber()
{
  return _portnumber;
}


int FlukeProSim8App::ParseCommandLineArgs(int argc, char* argv[])
{
  // Load command line parameters
  for (int ix = 0; ix < argc; ++ix)
  {
    if (IS_OPTION(argv[ix], "-help") || IS_OPTION(argv[ix], "?"))
    {
      cmdlinehelp(argv[0], 1);
    }
    else if (IS_OPTION(argv[ix], "-portname"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: "
          "Missing <name> after -portname";
        return __LINE__;
      }
      _portname = argv[ix];
    }
    else if (IS_OPTION(argv[ix], "-baud"))
    {
      if ((ix == (argc-1)) || *argv[++ix] == '-')
      {
        _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: "
          "Missing <number> after -baud";
        return __LINE__;
      }
      _baudrate = atoi(argv[ix]);
    }
  }

  if (_portname.empty())
  {
    _statusmsg = "Fluke_ProSim8_App::ParseCommandLineArgs() Error: Set "
      "portname with option -portname. Enter option -help for help message";
    return __LINE__;
  }

  return 0;
}


int FlukeProSim8App::OpenPort()
{
  if (_portname.empty())
  {
    _statusmsg = "Fluke_ProSim8_App::OpenPort() Set portname with option"
      " -portname. Enter option -help for help message.";
    return __LINE__;
  }

  if (sio_IsOpen(_portnumber))
  {
    cout << "OK. Port already open(" << _portname << ", "
      << _baudrate << ")" << endl;
    return 0;
  }

  // Open port
  _portnumber = sio_OpenComport(_portname.c_str(), _baudrate);
  if (_portnumber < 0)
  {
    stringstream ss;
    ss << "Fluke_ProSim8_App::OpenPort() Failed to open port "
      << _portname << ", baud" << _baudrate;
    _statusmsg = ss.str();
    return __LINE__;
  }

  cout << "OK. Open port(" << _portname
    << ", " << _baudrate << ")"
    << endl;

  return 0;
}


/**
 * Print help message.
 * @param [in] thisprogramname The name of the executable (argv[0]).
 * @param [in] exitprogram 0 or 1 value signifying whether or not to close
 * application after help message is printed.
 */
void FlukeProSim8App::cmdlinehelp(char *thisprogramname, int exitprogram)
{
  const char *smsg =
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
    "\n"
    "Brief Description:\n"
    "Demo application that uses the Fluke API.\n\n";

  printf(smsg, thisprogramname);
  if (exitprogram) exit(__LINE__);
}
