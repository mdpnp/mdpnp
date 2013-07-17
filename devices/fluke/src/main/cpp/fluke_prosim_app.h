/*
 * @file    fluke_prosim_app.h
 * @brief   TODO
 *
 */
//=============================================================================

#ifndef FLUKE_SRC_FLUKE_PROSIM_APP_H__
#define FLUKE_SRC_FLUKE_PROSIM_APP_H__

#include <string>

class FlukeProSim8App
{
public:
  FlukeProSim8App();
  ~FlukeProSim8App();


  static bool IsStatusOk(const int istat);
  std::string get_statusmsg();
  int get_portnumber();


  /**
   * Parses command line options and sets variables for setup.
   * @param [in] argc Number of elements in argv array
   * @param [in] argv pointer to array of strings
   * @return Zero for success.
   */
  int ParseCommandLineArgs(int argc, char* argv[]);


  /**
   * Open port using the port name and baud rate.
   * @return Zero for success.
   */
  int OpenPort();


private:
  // Disallow use of implicitly generated member functions:
  FlukeProSim8App(const FlukeProSim8App &src);
  FlukeProSim8App &operator=(const FlukeProSim8App &rhs);


  /**
   * Print help message.
   * @param [in] thisprogramname The name of the executable (argv[0]).
   * @param [in] exitprogram 0 or 1 value signifying whether or not to close
   * application after help message is printed.
   */
  void cmdlinehelp(char *thisprogramname, int exitprogram);

  std::string _portname;
  int _baudrate;
  std::string _statusmsg;
  int _portnumber;
};

#endif



