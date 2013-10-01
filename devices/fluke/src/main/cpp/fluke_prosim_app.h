/*
 * @file    fluke_prosim_app.h
 */
//=============================================================================

#ifndef FLUKE_SRC_FLUKE_PROSIM_APP_H_
#define FLUKE_SRC_FLUKE_PROSIM_APP_H_

#include <string>
#include "ndds/ndds_cpp.h"

class MyFluke;


class FlukeProsimApp
{
public:
  enum
  {
    RC_STATUS_OK = 0,
    RC_FAIL,
    RC_BAD_SAMPLE_FORMAT
  };

  FlukeProsimApp();
  ~FlukeProsimApp();

  static bool IsStatusOk(const int istat);
  static int ConvertStringCommandNameToEnum(const std::string &command_name);
  static void CmdLineHelp(const char *thisprogramname, int exitprogram);

  std::string get_statusmsg();
  int ParseCommandLineArgs(int argc, char* argv[]);
  int RunApp();

private:
  // Disallow use of implicitly generated member functions:
  FlukeProsimApp(const FlukeProsimApp &src);
  FlukeProsimApp &operator=(const FlukeProsimApp &rhs);

  int ProcessData(const std::string& cmd);

  std::string _statusmsg;
  std::string _portname;
  int _baudrate;
  int _domain_id;
  MyFluke* _fluke_api;
};


#endif
