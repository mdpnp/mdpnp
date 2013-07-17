/**
 * @file    dd_fluke_threads.h
 *
 * @brief   Declares threads which independently communicate with the
 *   Fluke simulator.
 *
 */

#ifndef FLUKE_SRC_DD_FLUKE_THREADS_H_
#define FLUKE_SRC_DD_FLUKE_THREADS_H_

#include <boost/thread.hpp>
#include "fluke.h"


class MyFluke : public Fluke
{
public:
  MyFluke(int in_fd, int out_fd);
  ~MyFluke();

  int ReceiveDeviceCommandResponse(char* message, size_t size,
    unsigned int sequence_number);
  int ReceiveResponse(char* message, size_t size);

private:

  // Disallow use of implicitly generated member functions:
  MyFluke(const MyFluke &src);
  MyFluke &operator=(const MyFluke &rhs);
};


class FlukeAPIWriter
{
public:
  FlukeAPIWriter();
  ~FlukeAPIWriter();

  int SendCommand(int command);
  void set_input_fd(const int fd);
  void set_output_fd(const int fd);
  int InitAPI();

private:

  // Disallow use of implicitly generated member functions:
  FlukeAPIWriter(const FlukeAPIWriter &src);
  FlukeAPIWriter &operator=(const FlukeAPIWriter &rhs);

  size_t ParseCommandName(const std::string* psource,
    MyFluke::CommandName* pcmd_name);
  int ParseDriverCommand(const char* driver_command,
    MyFluke::CommandName* cmd_name, char* params, size_t size_of_params);
};


class FlukeAPIReaderThread
{
public:

  enum
  {
    TSTATE_IDLE  = 0,
    TSTATE_STARTING,
    TSTATE_RUNNING,
    TSTATE_STOPPING
  };

  FlukeAPIReaderThread();
  ~FlukeAPIReaderThread();

  void Start();
  void Stop();
  void Join();
  void Detach();

  inline bool isstate(const int ist) const { return(ist == _tstate); }
  inline int getstate() const { return(_tstate); }
  inline void setstate(const int ist) { _tstate = ist; }

private:

  // Disallow use of implicitly generated member functions:
  FlukeAPIReaderThread(const FlukeAPIReaderThread &src);
  FlukeAPIReaderThread &operator=(const FlukeAPIReaderThread &rhs);

  int DoWork();

  boost::thread _thread;
  int _tstate;
};

#endif
