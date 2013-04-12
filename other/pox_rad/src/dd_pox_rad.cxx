/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    dd_pox_rad.cxx
 * @brief   Device driver, pulse oxiemter, Masimo Radical 5, 7.
 */
//=============================================================================
#include "projstd.h"
#include <iostream>
#include <string>
#include "icexutils.h"
#include "Property.h"
#include "poxradthread.h"
#include "devcomm.h"
#include "poxrad_discrete.h"
#include "dd_pox_rad.h"

using namespace std;

static bool _continue = true;

static char *string_dupe(const char *str);

#ifndef STRNCASECMP
#ifdef _WIN32
#define STRNCASECMP   _strnicmp
#else
#define STRNCASECMP   strncasecmp
#endif
#endif

#define IS_OPTION(str, option) (STRNCASECMP(str, option, strlen(str)) == 0)

//=============================================================================
int main(int argc, char *argv[])
{
  int rc = 0;
  dd_pox_rad *app = new dd_pox_rad;
  app->_programname = argv[0];
  rc = app->run_me(argc, argv);
  delete app;
  return(rc);
}

char *string_dupe(const char *str)
{
  char *result = new char[strlen(str) + 1];
  if (result) strcpy(result, str);
  return result;
}

//=============================================================================
//=============================================================================

dd_pox_rad::~dd_pox_rad()
{
  if (_argv)
  {
    for (int ix = 0; ix < _argc; ++ix)
    {
      if (_argv[ix]) delete [] _argv[ix];
    }
    delete [] _argv;
    _argv = 0;
    _argc = 0;
  }
}

//=============================================================================
dd_pox_rad::dd_pox_rad()
  : _programname(0),
    _argc(0),
    _argv(0),
    _cfgFile("dd_pox_rad.ini"),
    _isPub(true),
    _handle(0),
    _manufacturer("masimo"),
    _model("radical-5")
{
  t_portinfo *pi = &_portinfo[portindex_serial];
  pi->disabled = false;
  pi->use_program_sim_data = false;
  pi->use_file_sim_data = false;
  pi->sample_data_filename = "";
  pi->samples_per_sec = 1;
  pi->update_period_millisec = 1000;

  pi = &_portinfo[portindex_analog];
  pi->disabled = false;
  pi->use_program_sim_data = false;
  pi->use_file_sim_data = false;
  pi->sample_data_filename = "";
  pi->samples_per_sec = 500;
  pi->update_period_millisec = 1000;
}

//=============================================================================
int dd_pox_rad::run_me(int argc, char *argv[])
{
  int rc = 0;

  while(1)
  {
    if (parse_config(argc, argv))
    {
      rc = __LINE__;
      break;
    }

    if (_isPub)
    {
      cout << endl << "Running as Publisher." << endl;
      rc = operate_device();
    }
    else
    {
      cout << endl << "Running as Subscriber." << endl;
      rc = __LINE__;
    }
    break;
  }
  return(rc);
}

//=============================================================================
bool dd_pox_rad::is_radical5(const std::string &model)
{
  const char *ss = "radical-5";
  return(STRNCASECMP(ss, model.c_str(), strlen(ss)) == 0);
}

//=============================================================================
bool dd_pox_rad::is_radical7(const std::string &model)
{
  const char *ss = "radical-7";
  return(STRNCASECMP(ss, model.c_str(), strlen(ss)) == 0);
}

//=============================================================================
void dd_pox_rad::print_cmd_line_help()
{
  const char *usage_string =
    "Usage:\n"
    "  dd_pox_rad -pub | -sub [options]\n"
    "\nWhere [options] are (case insensitive, partial match OK):\n\n"
    "  -help                - Print this usage message and exit\n"
    "  -pub                 - Run as a publisher\n"
    "  -sub                 - Run as a subscriber\n"
    "  -handle <val>        - Set identifier for this driver instance,\n"
    "                         default 0\n"
    "  -cfgFile <filename>  - Set the name of the .ini configuration file,\n"
    "                         default dd_pox_rad.ini\n"
    "  -model <radical-5|radical-7>\n"
    "                       - Specify the device model, default radical-5\n"
    "  -disableSerialIo     - Disable serial i/o from any source (attached\n"
    "                         device, file or simulated), default serial i/o\n"
    "                         enabled\n"
    "  -disableSerialIo     - Disable anlaog i/o from any source (attached\n"
    "                         device, file or simulated), default anlaog i/o\n"
    "                         enabled\n"
    "  -enableSerialSim     - Enable program-generated simulation analog data,\n"
    "                         default simulated data not enabled. Note: Do not\n"
    "                         enable this if you specify <serial data filename>\n"
    "  -enableAnalogSim     - Enable program-generated simulation serial data,\n"
    "                         default simulated data not enabled. Note: Do not\n"
    "                         enable this if you specify <analog data filename>\n"
    "  -serialDataFilename <filename>\n"
    "                       - Set the name of the file from which to read\n"
    "                         simulated serial sample data (get data from file,\n"
    "                         not from device or program simulation). If\n"
    "                         sepcified, do not enable use of program-simulated\n"
    "                         data <-enableSerialSim>, default no value\n"
    "  -analogDataFilename <filename>\n"
    "                       - Set the name of the file from which to read\n"
    "                         simulated analog sample data (get data from file,\n"
    "                         not from device or program simulation). If\n"
    "                         sepcified, do not enable use of program-simulated\n"
    "                         data <-enableAnalogSim>, default no value\n"
    "  -serialSamplesPerSec <rate>\n"
    "                       - Set serial data samples per sec, default 1\n"
    "  -analogSamplesPerSec <rate>\n"
    "                       - Set analog data samples per sec, default 500\n"
    "  -serialUpdateMilliseconds <msecs>\n"
    "                       - Set serial data update period msecs\n"
    "                         (multiple of 1000), default 1000\n"
    "  -analogUpdateMilliseconds <msecs>\n"
    "                       - Set analog data update period msecs\n"
    "                         (must be same as serial), default 1000\n"
    ;

  fprintf(stdout, "%s", usage_string);
  comm_rs232::print_cmd_line_help();
  comm_analog::print_cmd_line_help();
}

//=============================================================================
int dd_pox_rad::parse_config(int argc, char *argv[])
{
  int rc = 1;  // Assume unsuccessful

  _argc = 0;

  if ((argc < 2) || !argv)
  {
    print_cmd_line_help();
    goto fini;
  }

  _argv = new char *[argc];
  if (!_argv)
  {
    fprintf(stderr, "Problem allocating memory.\n");
    goto fini;
  }

  // Copy every command line argument _argv.
  // Use _argv and _argc to configure system sub-components
  for (; _argc < argc; _argc++)
  {
    _argv[_argc] = string_dupe(argv[_argc]);

    if (!_argv[_argc])
    {
      fprintf(stderr, "Problem allocating memory\n");
      goto fini;
    }
  }

  // first scan for cfgFile, help
  for (int ixx = 1; ixx < argc; ++ixx)
  {
    if ((IS_OPTION(argv[ixx], "-help")) || (IS_OPTION(argv[ixx], "-?")))
    {
      print_cmd_line_help();
      fflush(stderr);
      goto fini;
    }

    if (IS_OPTION(argv[ixx], "-cfgFile")) 
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <fileName> after -cfgFile\n");
        goto fini;
      }
      _cfgFile = argv[ixx];
    }
  }

  // now load configuration values from config file
  if (_cfgFile.length())
  {
    CfgDictionary *cfgsource = 0;

    try
    {
      cfgsource = new CfgDictionary(_cfgFile);
    }
    catch (std::logic_error e)
    {
      fprintf(stderr, "Problem loading cfg file. %s\n", e.what());
      goto fini;
    }

    CfgProfile *cfg = cfgsource->get("dd_pox_rad");

    if (!cfg)
    {
      fprintf(stderr, "Failed to find section [dd_pox_rad] in file %s\n", _cfgFile.c_str());
      goto fini;
    }

    _handle  = cfg->get_int("handle", _handle);
    _model   = cfg->get_string("model", _model);

    t_portinfo *pi = &_portinfo[portindex_serial];

    pi->disabled = cfg->get_bool("disable serial io", pi->disabled);
    pi->use_program_sim_data = cfg->get_bool("enable serial sim", pi->use_program_sim_data);
    pi->sample_data_filename = cfg->get_string("serial data filename", pi->sample_data_filename);
    pi->use_file_sim_data = pi->sample_data_filename.length() ? true : false;
    pi->samples_per_sec =
      cfg->get_double("serial samples per sec", pi->samples_per_sec);
    pi->update_period_millisec =
      cfg->get_int("serial update milliseconds", pi->update_period_millisec);

    pi = &_portinfo[portindex_analog];

    pi->disabled = cfg->get_bool("disable analog io", pi->disabled);
    pi->use_program_sim_data = cfg->get_bool("enable analog sim", pi->use_program_sim_data);
    pi->sample_data_filename = cfg->get_string("analog data filename", pi->sample_data_filename);
    pi->use_file_sim_data = pi->sample_data_filename.length() ? true : false;
    pi->samples_per_sec =
      cfg->get_double("analog samples per sec", pi->samples_per_sec);
    pi->update_period_millisec =
      cfg->get_int("analog update milliseconds", pi->update_period_millisec);
  }

  // now load everything else, command line params override config file
  for (int ixx = 1; ixx < argc; ++ixx) 
  {
    if (IS_OPTION(argv[ixx], "-pub")) 
    {
      _isPub = true;
    }
    else if (IS_OPTION(argv[ixx], "-sub")) 
    {
      _isPub = false;
    }
    else if (IS_OPTION(argv[ixx], "-handle"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <val> after -handle\n");
        goto fini;
      }
      _handle = strtol(argv[ixx], 0, 10);
      if (_handle < 0)
      {
        fprintf(stderr, "Bad handle value\n");
        goto fini;
      }
    }
    else if (IS_OPTION(argv[ixx], "-model"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <model> after -model\n");
        goto fini;
      }
      _model = argv[ixx];

      if (!is_radical5(_model) && !is_radical7(_model))
      {
        fprintf(stderr, "Bad model, use radical-5 or radical-7\n");
        goto fini;
      }
    }
    else if (IS_OPTION(argv[ixx], "-serialDataFilename"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <filename> after -serialDataFilename\n");
        goto fini;
      }
      _portinfo[portindex_serial].sample_data_filename = argv[ixx];
      _portinfo[portindex_serial].use_file_sim_data = true;
    }
    else if (IS_OPTION(argv[ixx], "-analogDataFilename"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <filename> after -serialDataFilename\n");
        goto fini;
      }
      _portinfo[portindex_analog].sample_data_filename = argv[ixx];
      _portinfo[portindex_analog].use_file_sim_data = true;
    }
    else if (IS_OPTION(argv[ixx], "-disableSerialIo"))
    {
      _portinfo[portindex_serial].disabled = true;
    }
    else if (IS_OPTION(argv[ixx], "-disableAnalogIo"))
    {
      _portinfo[portindex_analog].disabled = true;
    }
    else if (IS_OPTION(argv[ixx], "-enableSerialSim"))
    {
      _portinfo[portindex_serial].use_program_sim_data = true;
    }
    else if (IS_OPTION(argv[ixx], "-enableAnalogSim"))
    {
      _portinfo[portindex_analog].use_program_sim_data = true;
    }
    else if (IS_OPTION(argv[ixx], "-serialSamplesPerSec"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <count> after -serialSamplesPerSec\n");
        goto fini;
      }
      _portinfo[portindex_serial].samples_per_sec = strtod(argv[ixx], 0);
      if (_portinfo[portindex_serial].samples_per_sec <= 0)
      {
        fprintf(stderr, "Bad number of serial samples per sec\n");
        goto fini;
      }
    }
    else if (IS_OPTION(argv[ixx], "-analogSamplesPerSec"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <count> after -analogSamplesPerSec\n");
        goto fini;
      }
      _portinfo[portindex_analog].samples_per_sec = strtod(argv[ixx], 0);
      if (_portinfo[portindex_analog].samples_per_sec <= 0)
      {
        fprintf(stderr, "Bad number of analog samples per sec\n");
        goto fini;
      }
    }
    else if (IS_OPTION(argv[ixx], "-serialUpdateMilliseconds"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <msecs> after -serialUpdateMilliseconds\n");
        goto fini;
      }
      _portinfo[portindex_serial].update_period_millisec = strtol(argv[ixx], 0, 10);
      if (_portinfo[portindex_serial].update_period_millisec < 0)
      {
        fprintf(stderr, "Bad serial update milliseconds\n");
        goto fini;
      }
    }
    else if (IS_OPTION(argv[ixx], "-analogUpdateMilliseconds"))
    {
      if ((ixx == (argc - 1)) || *argv[++ixx] == '-')
      {
        fprintf(stderr, "Missing <msecs> after -analogUpdateMilliseconds\n");
        goto fini;
      }
      _portinfo[portindex_analog].update_period_millisec = strtol(argv[ixx], 0, 10);
      if (_portinfo[portindex_analog].update_period_millisec < 0)
      {
        fprintf(stderr, "Bad analog update milliseconds\n");
        goto fini;
      }
    }
  }

  if (_portinfo[portindex_serial].update_period_millisec % 1000)
  {
    char *smsg =
      "Error:  serialUpdateMilliseconds must be a multiple of 1000.\n";
    fprintf(stderr, "%s", smsg);
    goto fini;
  }

  if (  !_portinfo[portindex_serial].disabled
    &&  _portinfo[portindex_serial].use_program_sim_data
    &&  _portinfo[portindex_serial].use_file_sim_data)
  {
    char *smsg =
      "Error:  enableSerialSim is enabled, and serialDataFilename is specified.\n"
      "Do one or the other, not both.\n";
    fprintf(stderr, "%s", smsg);
    goto fini;
  }

  if (  !_portinfo[portindex_analog].disabled
    &&  _portinfo[portindex_analog].use_program_sim_data
    &&  _portinfo[portindex_analog].use_file_sim_data)
  {
    char *smsg =
      "Error:  enableAnalogSim is enabled, and analogDataFilename is specified.\n"
      "Do one or the other, not both.\n";
    fprintf(stderr, "%s", smsg);
    goto fini;
  }

  if (_portinfo[portindex_serial].disabled)
  {
    fprintf(stdout, "INFO: Serial port i/o is disabled.\n");
  }
  if (_portinfo[portindex_analog].disabled)
  {
    fprintf(stdout, "INFO: Analog port i/o is disabled.\n");
  }

  rc = 0;   // Success!

fini:
  return(rc);
}

//=============================================================================
//=============================================================================

//=============================================================================
int dd_pox_rad::operate_device()
{
  int rc = 0;
  comm_rs232  *dc_rs232 = 0;
  comm_analog *dc_analog = 0;
  poxradrs232thread   *tserial = 0;
  poxradanalogthread  *tanalog = 0;
  poxradsenderthread  *tsender = 0;

  cout << endl;

  while(1)
  {
    if (_portinfo[portindex_serial].use_program_sim_data)
    {
      const char *examplestring = is_radical5(_model)
        ? poxrad_discrete::get_radical5_examplestring()
        : poxrad_discrete::get_radical7_examplestring();

      dc_rs232 = new rs232_sim(examplestring);
      cout << "Simulate serial i/o with internal algorithm." << endl;
    }
    else if (_portinfo[portindex_serial].use_file_sim_data)
    {
      dc_rs232 = new rs232_file_sim(_portinfo[portindex_serial].sample_data_filename);
      cout << "Simulate serial i/o with data from file ";
      cout << _portinfo[portindex_serial].sample_data_filename << endl;
    }
    else
    {
      dc_rs232 = new comm_rs232;
      cout << "Get serial data from port ";
      cout << dc_rs232->get_portname() << endl;
    }

    if (!devcomm::result_code_ok(dc_rs232->initialize(_argc, _argv)))
    {
      cout << dc_rs232->getstatusmsg() << endl;
      rc = __LINE__;
      break;
    }
    tserial = new poxradrs232thread(dc_rs232);

    tserial->set_samples_per_sec(_portinfo[portindex_serial].samples_per_sec);
    tserial->set_update_period_millisec(
      _portinfo[portindex_serial].update_period_millisec);

    if (_portinfo[portindex_analog].use_program_sim_data)
    {
      dc_analog = new analog_sim();
      cout << "Simulate analog i/o with internal algorithm." << endl;
    }
    else if (_portinfo[portindex_analog].use_file_sim_data)
    {
      dc_analog = new analog_file_sim(_portinfo[portindex_analog].sample_data_filename);
      cout << "Simulate analog i/o with data from file ";
      cout << _portinfo[portindex_analog].sample_data_filename << endl;
    }
    else
    {
#ifdef SIMULATE_DIO
      char *smsg =
        "The preprocessor macro SIMULATE_DIO is defined, which means this\n"
        "program was built with no linkage to the pcmmio library. The\n"
        "library provides the interface to the analog-digital i/o board.\n"
        "To run the program you must do one of the following:\n\n"
        "  -- Undefine the macro in the project make file and rebuild\n"
        "  -- Use program option -enableAnalogSim\n"
        "  -- Use program option -analogDataFilename <filename>.\n"
        ;
      print_cmd_line_help();
      cout << endl << smsg << endl;
      rc = __LINE__;
      break;
#endif
      dc_analog = new comm_analog();
      cout << "Get actual analog data from DIO device number ";
      cout << dc_analog->get_deviceNumber() << endl;
    }

    if (!devcomm::result_code_ok(dc_analog->initialize(_argc, _argv)))
    {
      cout << dc_analog->getstatusmsg() << endl;
      rc = __LINE__;
      break;
    }
    tanalog = new poxradanalogthread(dc_analog);

    tanalog->set_samples_per_sec(_portinfo[portindex_analog].samples_per_sec);
    tanalog->set_update_period_millisec(
      _portinfo[portindex_analog].update_period_millisec);

    poxradsenderthread  *tsender = new poxradsenderthread;

    cout  << endl;
    cout  << _programname << " is running. Press any key to stop." << endl;
    cout  << endl;

    // Start sender, serial and analog threads
    tsender->start();
    tserial->start();
    tanalog->start();

    boost::posix_time::milliseconds  tmsecs(250);

    // continue until user presses a key, or fatal error.
    while(_continue)
    {
      boost::this_thread::sleep(tmsecs);
      if (icexutils::kbhit()) _continue = false;
      if (  tsender->isstate(poxradthread::tstate_stopping)
        ||  tserial->isstate(poxradthread::tstate_stopping)
        ||  tanalog->isstate(poxradthread::tstate_stopping)) _continue = false;
    }

    cout << endl << "Stopping ..." << endl << endl;

#if 1
    boost::this_thread::sleep(tmsecs);
    tanalog->stop();
    tserial->stop();
    tsender->stop();
#else
    if (!tanalog->isstate(poxradthread::tstate_stopping)) tanalog->stop();
    if (!tserial->isstate(poxradthread::tstate_stopping)) tserial->stop();
    if (!tsender->isstate(poxradthread::tstate_stopping)) tsender->stop();
#endif

    tanalog->join();
    tserial->join();
    tsender->join();
    break;  // goodbye
  }

  if (tserial) delete tserial;
  if (tanalog) delete tanalog;
  if (tsender) delete tsender;

  if (dc_rs232)   delete dc_rs232;
  if (dc_analog)  delete dc_analog;
  return rc;
}
