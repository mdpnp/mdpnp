/**
 * @file    fluke.h
 * @brief   Header implements Fluke class, which is the API for
 *   the Fluke ProSim 8 simulator.
 *
 * @see     Fluke Biomedical ProSim 6/8 Communications Interface Full document.
 *
 */
//=============================================================================

#ifndef FLUKE_SRC_FLUKE_H__
#define FLUKE_SRC_FLUKE_H__

#include <string>
#include <deque>
#include <boost/thread.hpp>
#include <boost/thread/mutex.hpp>
#include "fluke_listener.h"


//#define TESTING_WITHOUT_DEVICE_ATTACHED

class Fluke : public FlukeListener
{
public:

  enum CommandName
  {
    NOTACOMMAND = -1,
    IDENT = 0, // General commands
    LOCAL,
    REMOTE,
    QMODE,
    DIAG,
    CAL,
    EXIT,
    RESET,
    VALIDATION,
    VALOFF,
    QVAL,
    QUI,
    QUSB,
    RECORD,
    ENTER_BOOT_LOADER, // $,  Enter boot loader (ISP)
    ECGRUN, // ECG simulation commands
    NSRA,
    NSRP,
    NSRAX,
    STDEV,
    ECGAMPL,
    EART,
    EARTSZ,
    EARTLD,
    SPVWAVE,
    PREWAVE,
    VNTWAVE,
    CNDWAVE,
    TVPPOL,
    TVPAMPL,
    TVPWID,
    TVPWAVE,
    ACLSWAVE,
    AFIB,
    VFIB,
    MONOVTACH,
    POLYVTACH,
    PULSE,
    SQUARE,
    SINE,
    TRI,
    RDET,
    QRS,
    TALLT,
    RESPRUN, // Respiration simulation commands
    RESPWAVE,
    RESPRATE,
    RESPRATIO,
    RESPAMPL,
    RESPBASE,
    RESPLEAD,
    RESPAPNEA,
    IBPS, // Invasive blood pressure (IBP) simulation commands
    IBPW,
    IBPP,
    IBPARTP,
    IBPARTM,
    IBPSNS,
    TEMP, // Temperature simulation commands
    COBASE, // Cardiac output simulation commands
    COINJ,
    COWAVE,
    CORUN,
    NIBPRUN, // Non-invasive blood pressure (NIBP) simulation commands
    NIBPP,
    NIBPV,
    NIBPES,
    NIBPTP,
    NIBPLEAK, // NIBP measurement and control commands
    LKOFF,
    LKSTAT,
    NIBPPOP,
    POPOFF,
    POPSTAT,
    PST,
    PS,
    PRESS,
    PRESSX,
    ZPRESS,
    UZPRESS,
    CZPRESS,
    STFIND,
    STHOME,
    STGO,
    STCLOSE,
    STVENT,
    SAT, // SpO2 simulation commands
    PERF,
    TRANS,
    AMBM,
    AMBS,
    AMBF,
    RESPM,
    RESPS,
    SPO2TYPE,
    RATIO,
    SPO2IDENT,
    QSTAT,
    SPO2SLFTST,
    SPO2SNGLTST,
    PCAREV, // Diagnostic command
    POFFATN, // Power circuit
    PWRDWN,
    DSPAND,
    ECGPWR, // ECG/Respiration circuit
    ECGATT,
    EDACLD,
    EDACCLR,
    ECGDAC,
    PACODAC,
    PACOP,
    RESPATT,
    RESPDGR,
    RESPDDGR,
    ECGXSPI,
    ECGX5V,
    IBPPWR, // IBP circuits
    IBPRNG,
    IBPDAC,
    IBPXSPI,
    TCPWR, // Temperature/ cardiac output circuit
    CODGR,
    CODDGR,
    T4DGR,
    T4DDGR,
    T71DGR,
    T71DDGR,
    T72DGR,
    T72DDGR,
    TCOXSPI,
    AX5VPWR, // Auxiliary/ SpO2 port
    AX12VPWR,
    AXPWRSW,
    AXOVC,
    AXATN,
    AX,
    AXOVERRIDE,
    ADCMUX, // NIB circuit and stepper moter
    ADC,
    ADCF,
    ADCO,
    ADCM,
    ADCV,
    STRST,
    STSLP,
    STEN,
    STCUR,
    STDIR,
    STEP,
    STFR,
    APMP,
    HIEPWR,
    HIEDAC,
    NI5VPWR,
    NI12VPWR,
    TSPWR,
    VALV,
    NIAND,
    NIXSPI,
    BLIGHT, // LCD, Keypad, Beeper, Battery, Temp sensor, and RTC
    LCDSOLID,
    KEYTEST,
    BEEP,
    BATDAT,
    GETTEMP,
    GETRTC,
    SETRTC,
    KEY, // Commands
    KEYACCEL,
    LCD,
    LCDSCREEN,
    LCDALL
  };

  enum FlukeFunctionReturnCode
  {
    STATUS_OK = 0,
    RC_BAD_PARAMETER,
    RC_EXCEPTION_THROWN,
    RC_WRITE_ERROR,
    RC_READ_ERROR,
    RC_UNKNOWN_COMMAND,
    RC_UNKNOWN_RESPONSE_TYPE,
    RC_READ_TIMEOUT,
    RC_NOT_OK_TO_SEND_COMMAND,
    RC_INIT_INCOMPLETE
  };

  enum SimulatorDataResponseType
  {
    RT_UNKNOWN_RESPONSE = 0,
    RT_DEVICE_COMMAND_RESPONSE,
    RT_NON_DEVICE_COMMAND_RESPONSE
  };

  enum InitializationOptionType
  {
    INIT_OPTION_VALIDATION_FALSE = 0,
    INIT_OPTION_VALIDATION_TRUE,
    INIT_OPTION_UNKNOWN
  };


  Fluke(int in_fd, int out_fd);
  ~Fluke();

  static bool IsStatusOk(const int istat);
  std::string get_statusmsg();
  unsigned int get_sequence_number();
  void set_input_fd(const int in_fd);
  void set_output_fd(const int out_fd);
  int get_input_fd();
  int get_output_fd();
  bool get_validation_flag();
  bool get_record_flag();

  /**
   * Determines if response is a Key Record String.
   * The format of a Key Record String is:
   * DD,DDD,DDD,HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH\r\n
   * D: Decimal digit
   * H: Hexidecimal digit
   * @param [in] message Pointer to array of characters.
   * @param [in] size Size of the array of characters pointed to by message
   * @return Returns true if the message is a Key Record String.
   */
  static bool IsKeyRecordString(char* message, size_t size);


  /**
   * Initializes the Fluke simulator. Sets both the Validation flag and
   * Record flag to false on the simulator.
   * @return zero for success.
   */
  int Init();


  /**
   * @return Returns true if either the Validation flag is false or the Record
   * flag is false, and returns true if Both flags are false.
   */
  bool OkToSendCommand();


  /**
   * Converts command Identifier into a string command name.
   * @param [in] command Command identifier or enumerator which represents
   *   a specific command name.
   * @param [in] cmd_name Pointer to string which contains the command name
   *   upon function return.
   * @return Zero for success.
   */
  int GetCommandName(const CommandName command, std::string* cmd_name);


  /**
   * Writes device command to an open port. A device command consists of
   * a command name, optional parameters, and a terminator.
   * @param [in] command Command identifier or enumerator which represents a
   *   unique command name.
   * @param [in] params C string which contains optional comma-separated
   *   parameters.
   */
  int SendCommand(CommandName command, const char* params);


  /**
   * Reads simulator data from the port one byte at a time. This function
   * implements a blocking read with a timeout.
   * @return zero for success. Returns a timeout error code when no bytes
   *   are available
   */
  int ReceiveMessage();


  /**
   * This method is defined by the class which intends to use this function.
   * Receives a string of simulator data which is a response to a device
   * command. The sequence number matches the sequence number of the device
   * command.
   * @param [in] message Pointer to string which contains simulator data.
   * @param [in] size Length of simulator data.
   * @param [in] sequence_number Integer value which matches the device
   *   command that prompted the simulator data.
   * @return Returns zero for success.
   */
  virtual int ReceiveDeviceCommandResponse(char* message,
     size_t size, unsigned int sequence_number);


   /**
    * This method is defined by the class which intends to use this function.
    * Receives a string of simulator data which is not a response to a device
    * command (i.e., Key Record string).
    * @param [in] message Pointer to string which contains simulator data.
    * @param [in] size Length of simulator data.
    * @return Returns zero for success.
    */
   virtual int ReceiveResponse(char* message, size_t size);


private:

  // Disallow use of implicitly generated member functions:
  Fluke(const Fluke &src);
  Fluke &operator=(const Fluke &rhs);


  /**
   * Determines which function handles the message.
   * @param [in] response Enumerator which identifies response type
   * @param [in] message Pointer to NULL terminating string which contains
   *   simulator data.
   * @param [in] size Size of the buffer pointed to by message.
   * @return Zero for success.
   */
  int ReceiveMessage(const SimulatorDataResponseType response, char* message,
    size_t size);


  /**
   * @return Returns true if the device has been initialized.
   */
  bool HasBeenInitialized();


  boost::mutex _lock;
  boost::condition_variable _condition;
  unsigned int _sequence_number;
  int _input_fd;
  int _output_fd;
  bool _validation_flag;
  bool _record_flag;
  bool _is_fluke_initialized;
  InitializationOptionType _init_option;
  std::string _statusmsg;
  std::deque<std::string> _flag_cmds_sent;

#ifdef TESTING_WITHOUT_DEVICE_ATTACHED
  std::deque<std::string> _simulated_cmds_sent;
#endif
};

#endif
