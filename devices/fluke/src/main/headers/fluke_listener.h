/*
 * @file    fluke_listener.h
 * @brief   Class declaration which declares virtual functions. Each function
 *   handles a unique type of simulator data.
 *
 */
//=============================================================================

#ifndef FLUKE_SRC_FLUKE_LISTENER_H_
#define FLUKE_SRC_FLUKE_LISTENER_H_


class FlukeListener
{
public:
  virtual ~FlukeListener();

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
    size_t size, unsigned int sequence_number)
  {
    return 0;
  }


  /**
   * This method is defined by the class which intends to use this function.
   * Receives a string of simulator data which is not a response to a device
   * command (i.e., Key Record string).
   * @param [in] message Pointer to string which contains simulator data.
   * @param [in] size Length of simulator data.
   * @return Returns zero for success.
   */
  virtual int ReceiveResponse(char* message, size_t size)
  {
    return 0;
  }


private:
  // Disallow use of implicitly generated member functions:
  FlukeListener(const FlukeListener &src);
  FlukeListener &operator=(const FlukeListener &rhs);


protected:
  FlukeListener()
  {
  }

};

#endif
