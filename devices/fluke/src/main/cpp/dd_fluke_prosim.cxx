/**
 * @file    dd_fluke_prosim.cxx
 *
 * @brief   Contains the main method which configures and runs the application.
 * The application uses an API to communicate with Fluke ProSim 8 Vital Signs
 * Patient Simulator.
 *
 */
//=============================================================================

#include "dicesprojstd.h"
#include <iostream>
#include "fluke_prosim_app.h"
#include "dd_fluke_prosim.h"

using namespace std;


int main(int argc, char *argv[])
{
  int istat = 0;
  FlukeProsimApp app;

  // Parse command line options
  istat = app.ParseCommandLineArgs(argc, argv);
  if (!FlukeProsimApp::IsStatusOk(istat))
  {
    cout << app.get_statusmsg() << endl;
    return istat;
  }

  istat = app.RunApp();
  if (!FlukeProsimApp::IsStatusOk(istat))
  {
    cout << app.get_statusmsg() << endl;
    return istat;
  }

  return istat;
}
