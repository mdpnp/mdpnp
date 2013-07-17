/**
 * @file    fluke_prosim.cxx
 * @brief   Uses Fluke API to communicate with Fluke ProSim 8 simulator.
 *
 * @see Fluke Biomedical ProSim 6/8 Communications Interface Full document.
 *
 */
//=============================================================================

#include "dicesprojstd.h"
#include <iostream>
#include <iomanip>
#include <stdio.h>
#include <stdlib.h>
#include "fluke.h"
#include "fluke_prosim_app.h"
#include "rs232.h"
#include "fluke_listener.h"
#include "dd_fluke_threads.h"
#include "kbhit_util.h"

using namespace std;

static const int kAsciiEsc = 0x1B;
static const int kAsciiLF = 0x0A;

extern MyFluke g_fluke_api(0,0);


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


/**
 * Read keyboard input up to three characters. The character string
 * may not contain all digits.
 * @param [in] key Pointer to key value that was read from the keyboard.
 * @param [in] entered_str Pointer to the resulting string of this function.
 * @return Returns zero for success.
 */
int HandleKeyboardHit(char* key, string* entered_str)
{
  if (!key || !entered_str) return __LINE__;

  do
  {
    if (*key == kAsciiEsc) return __LINE__;

    // Add key received to a string.
    if (*key != 0) *entered_str += *key;

    // Looking for a 3 digit integer.
    if (entered_str->size() >= 3) break;

    // Get another key
    *key = kbhit_util::kbhit();

    // loop until enter key is pressed or a three digit number is entered.
  } while (*key != kAsciiLF);

  return 0;
}


int main(int argc, char *argv[])
{
  int istat = 0;
  FlukeProSim8App app;
  int port_number = -1;
  int port_handle = 0;

  istat = app.ParseCommandLineArgs(argc, argv);
  if (!FlukeProSim8App::IsStatusOk(istat))
  {
    cout << app.get_statusmsg() << endl;
    return istat;
  }

#ifndef TESTING_WITHOUT_DEVICE_ATTACHED
  istat = app.OpenPort();
  if (!FlukeProSim8App::IsStatusOk(istat))
  {
    cout << app.get_statusmsg() << endl;
    return istat;
  }

  port_number = app.get_portnumber();
  port_handle = sio_GetPortHandle(port_number);
#endif

  // Create writer
  FlukeAPIWriter fluke_writer;

  // Setup port handles
  fluke_writer.set_input_fd(port_handle);
  fluke_writer.set_output_fd(port_handle);

  // Create and start the reader
  FlukeAPIReaderThread fluke_reader_thread;

  cout << "Starting read thread..." << endl;
  fluke_reader_thread.Start();

  // Initialize Validation and Record flags
  fluke_writer.InitAPI();

  int cc = 0;
  string command_value;

  // Run the application until operator presses the escape key.
  while (cc != kAsciiEsc)
  {
    // Determine what should be done with key entered.
    istat = HandleKeyboardHit((char*)&cc, &command_value);
    if (istat) break;
    else
    {
      int name_enum = atoi(command_value.c_str());
      cout << "Entered<" << command_value << ">, value:"
        << atoi(command_value.c_str()) << endl;

      fluke_writer.SendCommand(name_enum);
      command_value.erase();
    }

    cc = 0;
    cc = kbhit_util::kbhit();

    millisleep(0);
  }
  cout << endl;

  cout << "Stopping read thread..." << endl;
  fluke_reader_thread.Stop();

  cout << "Joining read thread..." << endl;
  fluke_reader_thread.Join();

  cout << "Exiting..." << endl;
  millisleep(2000); // Sleep 2 seconds

#ifndef TESTING_WITHOUT_DEVICE_ATTACHED
  sio_CloseComport(app.get_portnumber());
#endif

  return istat;
}
