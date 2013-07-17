/**
 * @file    dd_fluke_threads.cxx
 *
 * @brief   Defines threads which independently communicate with the
 *   Fluke simulator.
 *
 */

#include "dicesprojstd.h"
#include "dd_fluke_threads.h"
#include <boost/thread.hpp>
#include <boost/date_time.hpp>
#include <boost/thread/mutex.hpp>

using namespace std;
using namespace boost;


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


MyFluke::MyFluke(int in_fd, int out_fd)
  :Fluke(in_fd, out_fd)
{
}


MyFluke::~MyFluke()
{
}


int MyFluke::ReceiveDeviceCommandResponse(char* message, size_t size,
  unsigned int sequence_number)
{
  if (!message) return __LINE__;

  std::cout << "Received response to device command (" << sequence_number << "):"
    << std::endl;
  for (size_t ix = 0; ix < size; ix++)
    std::cout << message[ix];

  return 0;
}


int MyFluke::ReceiveResponse(char* message, size_t size)
{
  if (!message) return __LINE__;

  std::cout << "Received response:" << std::endl;
  for (size_t ix = 0; ix < size; ix++)
    std::cout << message[ix];

  return 0;
}


static mutex g_lock;
static condition_variable g_condition;
static MyFluke g_fluke_api(0,0);


FlukeAPIWriter::FlukeAPIWriter()
{
}


FlukeAPIWriter::~FlukeAPIWriter()
{
}


int FlukeAPIWriter::InitAPI()
{
  int istat = g_fluke_api.Init();
  if (!MyFluke::IsStatusOk(istat))
    return __LINE__;

  return 0;
}


void FlukeAPIWriter::set_input_fd(const int fd)
{
  g_fluke_api.set_input_fd(fd);
}


void FlukeAPIWriter::set_output_fd(const int fd)
{
  g_fluke_api.set_output_fd(fd);
}


static void PrintMenu()
{
  stringstream ss;

  const char* command_list =
    "\t1\tIDENT\n"
    "\t2\tLOCAL\n"
    "\t3\tREMOTE\n"
    "\t4\tQMODE\n"
    "\t5\tDIAG=OZYMANDIAS\n"
    "\t6\tCAL=OZYMANDIAS\n"
    "\t7\tEXIT\n"
    "\t8\tVALIDATION=OZYMANDIAS\n"
    "\t9\tVALOFF\n"
    "\t10\tQVAL\n"
    "\t11\tQUI\n"
    "\t12\tQUSB\n"
    "\t13\tRECORD=TRUE\n"
    "\t14\tRECORD=FALSE\n";

  ss << string(80, '-') << "\nCommand Menu\n" << string(80, '-') << "\n"
    << command_list << string(80, '-') << endl;

  cout << ss.str();
}


static void PrintFlukeFlagStates()
{
  cout << "Current Flag states: Validation flag: "
    << g_fluke_api.get_validation_flag() << ", "
    "Record flag: " << g_fluke_api.get_record_flag() << endl;
}


int FlukeAPIWriter::SendCommand(int command)
{
  MyFluke::CommandName cmd_name;
  string device_command;
  string params;
  int istat = 0;
  int api_stat = MyFluke::STATUS_OK;

  unique_lock<mutex> lock(g_lock);
  g_condition.wait(lock);

  switch (command)
  {
    case 1:
      cmd_name = MyFluke::IDENT;
      params = "";
    break;
    case 2:
      cmd_name = MyFluke::LOCAL;
      params = "";
    break;
    case 3:
      cmd_name = MyFluke::REMOTE;
      params = "";
    break;
    case 4:
      cmd_name = MyFluke::QMODE;
      params = "";
    break;
    case 5:
      cmd_name = MyFluke::DIAG;
      params = "OZYMANDIAS";
    break;
    case 6:
      cmd_name = MyFluke::CAL;
      params = "OZYMANDIAS";
    break;
    case 7:
      cmd_name = MyFluke::EXIT;
      params = "";
    break;
    case 8:
      cmd_name = MyFluke::VALIDATION;
      params = "OZYMANDIAS";
    break;
    case 9:
      cmd_name = MyFluke::VALOFF;
      params = "";
    break;
    case 10:
      cmd_name = MyFluke::QVAL;
      params = "";
    break;
    case 11:
      cmd_name = MyFluke::QUI;
      params = "";
    break;
    case 12:
      cmd_name = MyFluke::QUSB;
      params = "";
    break;
    case 13:
      cmd_name = MyFluke::RECORD;
      params = "TRUE";
    break;
    case 14:
      cmd_name = MyFluke::RECORD;
      params = "FALSE";
    break;
    default:
      cout << "The entry (" << command << ") is out of range." << endl;
      PrintMenu();
      istat = __LINE__;
      goto exit_fn;
    break;
  }

  // Using the API, write the device command to the USB port.
  api_stat = g_fluke_api.SendCommand(cmd_name, params.c_str());
  if (!Fluke::IsStatusOk(api_stat))
  {
    // Print message based on the status received.
    if (api_stat == MyFluke::RC_NOT_OK_TO_SEND_COMMAND)
    {
      cout << "FlukeAPIWriter::SendCommand() Warning: Cannot send command "
        "with the following flag states:" << endl;
      PrintFlukeFlagStates();
    }
    else
      cout << g_fluke_api.get_statusmsg() << endl;

    istat = __LINE__;
    goto exit_fn;
  }

exit_fn:
  return istat;
}


FlukeAPIReaderThread::FlukeAPIReaderThread()
  :_tstate(TSTATE_IDLE)
{

}


FlukeAPIReaderThread::~FlukeAPIReaderThread()
{

}


void FlukeAPIReaderThread::Start()
{
  _tstate = TSTATE_STARTING;
  _thread = boost::thread(&FlukeAPIReaderThread::DoWork, this);
}


void FlukeAPIReaderThread::Stop()
{
  _tstate = TSTATE_STOPPING;
  _thread.interrupt();
}


void FlukeAPIReaderThread::Join()
{
  _thread.join();
}


void FlukeAPIReaderThread::Detach()
{
  _thread.detach();
}


int FlukeAPIReaderThread::DoWork()
{
  _tstate = TSTATE_RUNNING;

  while (!isstate(TSTATE_STOPPING))
  {
    millisleep(0); // Give a chance for other process to use CPU

    // Grab lock
    lock_guard<mutex> lock(g_lock);

    // Receive messages until timeout or failure.
    int api_istat = g_fluke_api.ReceiveMessage();
    if (api_istat == MyFluke::RC_READ_TIMEOUT)
    {
      cout << "Read timeout" << endl;

      // Release lock and notify other processes that the condition has
      // changed.
      g_lock.unlock();
      g_condition.notify_all();

      // Ignore timeouts. Go to the next iteration of the loop.
      continue;
    }
    else if (!Fluke::IsStatusOk(api_istat))
    {
      cout << g_fluke_api.get_statusmsg() << endl;
      return __LINE__;
    }

    // Release lock and notify other processes that the condition has
    // changed.
    g_lock.unlock();
    g_condition.notify_all();
  }

  return 0;
}
