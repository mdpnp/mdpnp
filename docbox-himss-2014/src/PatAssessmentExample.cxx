/**
 * @file    PatAssessmentExample.cxx
 *
 * @brief   This example program publishes and subscribes Patient Assessment data
 * used to demonstrate a patient assessment application for HIMSS 2014.
 */
//=============================================================================

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include "PatAssessmentApp.h"

using namespace std;


int main(int argc, char *argv[])
{
  PatAssessmentApp app;

  if (app.ParseCommandLineArgs(argc, argv))
  {
    cout << endl << app.get_statusmsg() << endl;
    app.CmdLineHelp(argv[0], 0);
    return __LINE__;
  }

  if (app.RunApp(argv[0]))
  {
    cout << app.get_statusmsg() << endl;
    return __LINE__;
  }
  return 0;
}
