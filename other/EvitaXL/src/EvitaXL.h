/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    EvitaXL.h
 * @brief   Driver for EvitaXL.
 */

#ifndef EVITAXL_H_
#define EVITAXL_H_
#pragma	once

#include "rs232.c"
#include "DataSample.h"
#include "DataRecord.h"

#define COMMAND_HEADER 		0x1B
#define RESPONSE_HEADER		0x01
#define SLOW_MESSAGE_END	'\r'

#define ICC_CODE                0x51 //!< Initialize Communication Command Code
#define DID_CODE                   0x52 //!< Device ID Command Code
#define REALTIME_CONFIG_CODE	   0x53 //!< Realtime Configuration Request Code
#define REALTIME_TRANSMISSION_CODE 0x54 //!< Config Realtime Transmission Code
#define STOP_CODE                  0x55 //!< Stop Communication Code
#define NOP_CODE                   0x30 //!< NOP Command Code
#define SLOW_DATA_CODE             0x24 //!< Slow Data Command Code
#define DATA_CONFIG_CODE           0x4A //!< Data Configuration Code

#define NOP_COMMAND			{                    \
                                COMMAND_HEADER,  \
                                NOP_CODE,        \
                                0x34,            \
                                0x42,            \
                                SLOW_MESSAGE_END \
                            }
#define SLOW_DATA_COMMAND   {                    \
                                COMMAND_HEADER,  \
                                SLOW_DATA_CODE,  \
                                0x33,            \
                                0x46,            \
                                SLOW_MESSAGE_END \
                            }
#define STOP_COMMAND		{                    \
                                COMMAND_HEADER,  \
                                STOP_CODE,       \
                                0x37,            \
                                0x30,            \
                                SLOW_MESSAGE_END \
                            }
#define REALTIME_CONFIG_COMMAND	{                \
                                COMMAND_HEADER,  \
                                REALTIME_CONFIG_CODE, \
                                0x36,            \
                                0x45,            \
                                SLOW_MESSAGE_END \
                                }

#define INITIALIZE_COMMUNICATION_COMMAND	"0x1b5136430d"
#define DEVICE_ID_COMMAND                   "0x1b5236440d"

#define INITIALIZE_COMMUNICATION_RESPONSE	{                   \
                                                RESPONSE_HEADER,\
                                                ICC_CODE,       \
                                                0x35,           \
                                                0x32,           \
                                                SLOW_MESSAGE_END\
                                            }
#define DEVICE_ID_RESPONSE {                    \
                                RESPONSE_HEADER,\
                                DID_CODE,       \
                                0x35,           \
                                0x33,           \
                                SLOW_MESSAGE_END\
                            }
#define NOP_RESPONSE	   {                    \
                                RESPONSE_HEADER,\
                                NOP_CODE,       \
                                0x33,           \
                                0x31,           \
                                SLOW_MESSAGE_END\
                            }

#define GAS_TEMPERATURE                 'C', '1'
#define EXP_TIDAL_VOLUME_LITERS         '8', '2'//!< Expiratory Tidal Vol. in L
#define EXP_TIDAL_VOLUME_MILLILITERS	'8', '8'//!< Expiratory Tidal Vol. in mL
#define MIN_AIRWAY_PRESSURE             '7', '1'
#define MEAN_AIRWAY_PRESSURE            '7', '3'
#define PEEP_AIRWAY_PRESSURE            '7', '8'
#define PEAK_AIRWAY_PRESSURE            '7', 'D'

#define ENABLE_REALTIME_COM_COMMAND	{0xD0, 0xC1, 0xCF, 0xC0, 0xC0}

#define START_OF_INSP_CYCLE_INDICATOR	"c6c0"
#define START_OF_EXP_CYCLE_INDICATOR	"c6c1"

/**
 * Indicators for which data values are being sent in a realtime data message,
 * referring to the order in which they were requested
 */

#define NONE                    0xD0
#define	FIRST                   0xD1
#define SECOND                  0xD2
#define FIRST_AND_SECOND        0xD3
#define THIRD                   0xD4
#define FIRST_AND_THIRD         0xD5
#define	SECOND_AND_THIRD        0xD6
#define	FIRST_SECOND_AND_THIRD	0xD7
#define FOURTH                  0xD8
#define FIRST_AND_FOURTH        0xD9
#define	SECOND_AND_FOURTH       0xDA
#define FIRST_SECOND_AND_FOURTH	0xDB
#define	THIRD_AND_FOURTH        0xDC
#define FIRST_THIRD_AND_FOURTH	0xDD
#define SECOND_THIRD_AND_FOURTH	0xDE
#define ALL                     0xDF


/**
 * @brief The EvitaXL class Ventilator Driver.
 *
 * Driver that allows continuous communication with the ventilator.
 * Logs the data it collects.
 */
class EvitaXL
{
private:
    /**
     * @brief EvitaXL Disallow use of implicitly generated member functions.
     * @param src
     */
    EvitaXL(const EvitaXL &src);
    /**
     * @brief operator =  Disallow use of implicitly generated member functions.
     * @param rhs
     * @return
     */
    EvitaXL &operator=(const EvitaXL &rhs);

    unsigned char byte; //!< the last byte read drom the serial port
    bool log; //!< Turns on/off logging capability, defined as an argument
    bool isConnected;//!< used to determine whether or not the ventilator
                     //!< is connected
    bool flag; //!< used to determine when specific messages have
               //!<  arrived through the serial port
    bool slowDataFlag;  //!< used to ensure that slow data variable are
                        //!< initialized before receiving real-time data
    int port; //!< serial port number to be opened for communications
    int verbosity;        //!< reporting level
    int hPort;            //!< serial port where the Evita XL is connected
    std::string statusMessage;//!< detailed message about current program status
    std::string source;   //!< either port name or file name
    std::string scannedInput;//!< the last complete message read from the serial port
    FILE *logFile;        //!< file where all data will be recorded
    bool *realtimeData;//!< tells which real-time data values will be

//    !< realtimeData[0] - airway pressure
//    !< realtimeData[1] - flow
//    !< realtimeData[2] - pleth
//    !< realtimeData[3] - respiratory volume since insp. began
//    !< realtimeData[4] - CO2
//    !< realtimeData[5] - Exp. Volume

    const int getMaxRealtimeDataValues() const;

    /**
     *  used to calculate values of real-time data
     */
    int pressureMaxBin,
        flowMaxBin,
        co2MaxBin,
        plethMaxBin,
        resp_vol_since_insp_beginMaxBin,
        expVolumeMaxBin;
    double pressureMin,
        pressureMax,
        flowMin,
        flowMax,
        co2Min,
        co2Max,
        plethMin,
        plethMax,
        resp_vol_since_insp_beginMin,
        resp_vol_since_insp_beginMax,
        expVolumeMin,
        expVolumeMax;

    /**
     * current data values
    */
    DataSample *    airwayPressure;
    DataSample *    flow;
    DataSample *    co2;
    DataSample *    pleth;
    DataSample *    resp_vol_since_insp_begin;
    DataSample *    expVolume;
    DataSample *    temperature;
    DataSample *    minimumAirwayPressure;
    DataSample *    meanAirwayPressure;
    DataSample *    peepAirwayPressure;
    DataSample *    peakAirwayPressure;
    DataSample *    expiratoryTidalVolume;

    /**
     * @brief readByte Reads a byte from the serial port. If no data is
     * available, blocks until data arrives.
     */
    void readByte();


    /**
     * @brief addNextChar
     * @param character lowercase hexadecimal
     * @param buff
     *
     * @pre character is the string representation of a hexadecimal number
     * whose value is between 30 and 46('0' - 'F'), equal to 2d('-'), or equal
     * to 2e('.'). All alphabetic characters within character are lower case.
     *
     * @post If the preconditions are met, the ASCII character whose
     * hexadecimal value is equal to the value represented by character is
     * appended to buff. Otherwise, no changes are made to either character
     * nor buff.
     */
    void addNextChar(std::string& character, std::string& buff);

    /**
     * @brief getBinaryValue
     *
     * @pre scannedInput.lenghth() == 4 and scannedInput represents the
     * hexadecimal value of a real-time data value which has been received
     * from the Evita XL in the format specified by the MEDIBUS protocol, which
     * is 2 bytes in length. In addition, all alphabetic characters within
     * scannedInput are lower case.
     *
     * @return The numeric value(according to the MEDIBUS protocol) of the
     *real-time data value represented in scannedInput is returned.
     *
     * @note The value returned by getBinaryValue() is not equal to the value
     * which is being displayed by the Evita XL for that particular real-time
     * value. To obtain the correct value, the result returned by
     * getBinaryValue() must be used to compute it, along with the MIN, MAX, and
     * MaxBIN values which have been obtained, as specified by the MEDIBUS
     * protocol.
     */
    int getBinaryValue();

    /**
     * @brief itoa int to ascii
     * @param value int to be converted
     * @param base the base of the int
     *
     * @pre value is not null and 2 <= base >= 36
     *
     * @return If base < 2 or base > 16, an empty string ("\0") is returned.
     * Otherwise, a string representing value in the base specified is returned.
     */
    std::string itoa(int value, unsigned int base);

    /**
     * @brief htoi hexadecimal to int
     * @param hexString hex to be converted in the form of a string
     * @return Returns the decimal value of the hexadecimal integer
     * represented by hexString.
     * @pre hexString is a string which is composed only of valid hexadecimal
     * characters or "0x" preceeded only by valid hexadecimal characters.
     */
    unsigned int htoi(std::string& hexString);

    /**
     * @brief itoh integer to hexadecimal in the form of a string
     * @param dec int to be converted
     * @return  Returns the hexadecimal representation of dec
     */
    std::string itoh(unsigned int dec);

    /**
     * @brief getTemperature
     * @param i
     * @param timeStamp
     *
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for gas
     * temperature starts.
     *
     * @post temperature is set to the temperature value sent by the Evita XL
     * according to the MEDIBUS protocol
     */
    void getTemperature(int& i, std::string& timeStamp);

    /**
     * @brief getExpiratoryTidalVolume
     * @param i
     * @param timeStamp
     *
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for
     * expiratory tidal volume starts.
     *
     * @post expiratory tidal volume is set to the expiratory tidal volume value
     * sent by the Evita XL according to the MEDIBUS protocol
     */

    void getExpiratoryTidalVolume(int& i, std::string& timeStamp);

    /**
     * @brief getMinimumAirwayPressure
     * @param i
     * @param timeStamp
     *
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for
     * minimum airway pressure starts.
     *
     * @post minimumAirwayPressure is set to the minimum airway pressure value
     * sent by the Evita XL according to the MEDIBUS protocol
     */
    void getMinimumAirwayPressure(int& i, std::string& timeStamp);

    /**
     * @brief getMeanAirwayPressure
     * @param i
     * @param timeStamp
     *
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for mean
     * airway pressure starts.
     *
     * @post meanAirwayPressure is set to the meanAirwayPressure value sent by
     * the Evita XL according to the MEDIBUS protocol
     */
    void getMeanAirwayPressure(int &i, std::string& timeStamp);

    /**
     * @brief getPEEPAirwayPressure
     * @param i
     * @param timeStamp
     *
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for PEEP
     * airway pressure starts.
     *
     * @post peepAirwayPressure is set to the peep airway pressure value sent by
     * the Evita XL according to the MEDIBUS protocol
     */
    void getPEEPAirwayPressure(int& i, std::string& timeStamp);

    /**
     * @brief getPeakAirwayPressure
     * @param i
     * @param timeStamp
     * @pre scannedInput contains a slow data message that was received from the
     * Evita XL and i is the index in scannedInput where the data code for peak
     * airway pressure starts.
     *
     * @post peakAirwayPressure is set to the peak airway pressure value sent by
     * the Evita XL according to the MEDIBUS protocol
   */
    void getPeakAirwayPressure(int& i, std::string& timeStamp);

    /**
     * @brief timeStamp prints a time stamp followed by a newline character
     * ('\n') in logFile
     * @return  timeStamp + NL
     */
    std::string timeStamp();

    /**
     * @brief logData prints the current values for all "slow" and realtime data
     * to logFile
     */
    void logData();

    /**
     * @brief receiveMessage Reads one byte from the serial port.
     *
     * Blocks until at least 1 Byte is available to be read from the serial
     * port. Reads one byte from the serial port and appends it to scannedInput.
     * If the byte read signifies the end of a message, scannedInput is
     * processed as a realtime or slow message.
     */
    void receiveMessage();

    /**
     * @brief sendMessage sends messages to the serial port.
     *
     * Sends a message consisting of ASCII values from message[0] to
     * message[length-1] to the serial port.
     *
     * @param message message to be sent
     * @param length length of the message
     */
    void sendMessage(unsigned char* message, unsigned int length);

    /**
     * @brief initializeCommunication  Sends an "initialize communication"
     * (ICC) MEDIBUS command to the serial port
     */
    void initializeCommunication();

    /**
     * @pre scannedInput contains a response from the Evita XL to a MEDIBUS
     * request for its realtime configuration.
     *
     * @post the min, max, and maxBin values for all 6 different realtimeData
     * are collected.
     */

    void getRealtimeConfiguration();

    /**
     * @brief configureSlowDataTransfer Configures the Evita XL to send all of
     * the slow data values we are collecting.
     */
    void configureSlowDataTransfer();

    /**
     * @pre the Evita XL has responded correctly to an ICC command.
     *
     * @post the Evita XL will begin continuously sending all requested
     * realtime data until the connection fails.
     */
    void initializeRealtimeCommunication();

    /**
     * @brief sendDeviceID An empty response to the MEDIBUS device ID request is
     * sent to the serial port
     */
    void sendDeviceID();

    /**
     *  @brief sendNOP A MEDIBUS No Operation(NOP) command is sent to the serial
     * port. This command is used to keep communication between the Evita XL and
     * the application alive.
     */
    void sendNOP();

    /**
     * @brief sendNOPResponse  Sends a NOP response.
     *
     * A response to the MEDIBUS No Operation(NOP) command is sent to the serial
     * port. This command is used to keep communication between the Evita XL and
     * the application alive.
     */
    void sendNOPResponse();

    /**
     * @pre scannedInput contains a valid "slow" command that was received from
     * the Evita XL.
     *
     * @post The command is processed and the appropriate response is sent to
     * the serial port if necessary.
     */
    void processSlowCommand();

    /**
     * @pre scannedInput contains a valid realtime data transmission that was
     * received from the Evita XL.
     *
     * @post The transmitted data is stored in the appropriate variables
     */
    void processRealtimeCommand(std::string& lowOrderBits, std::string& timeStamp);

    /**
     * @brief getAirwayPressure Calculates and returns the true airway pressure
     * based on the binary value received in a realtime data message using
     * pressureMin, pressureMax, and pressureMaxBin
     *
     * @param binaryValue  binary value received in a realtime data message
     *
     * @return airway pressure
     *
     * @pre values for pressureMin, pressureMax, and pressureMaxBin have all
     * been obtained from the Evita XL.
     */
    double getAirwayPressure(int binaryValue);

    /**
     * @pre values for flowMin, flowMax, and flowMaxBin have all been
     * obtained from the Evita XL.
     *
     * calculates and returns the true flow based on the binary value received
     * in a realtime data message using flowMin, flowMax, and flowMaxBin
     */
    /**
     * @brief getFlow
     * @param binaryValue  binary value received in a realtime data message
     * @return true flow
     */
    double getFlow(int binaryValue);

    /**
     * @pre values for co2Min, co2Max, and co2MaxBin have all been
     * obtained from the Evita XL.
     * @brief getCO2 calculates and returns the true CO2 based on the binary
     * value received in a realtime data message using co2Min, co2Max, and
     * co2MaxBin.
     * @param binaryValue  binary value received in a realtime data message
     * @return true CO2
     */
    double getCO2(int binaryValue);

    /**
     * @pre values for expVolumeMin, expVolumeMax, and expVolumeMaxBin have all
     * been obtained from the Evita XL.
     *
     * @brief getExpVolume calculates and returns the true expiratory volume
     * based on the binary value received in a realtime data message using
     * expVolumeMin, expVolumeMax, and expVolumeMaxBin
     * @param binaryValue  binary value received in a realtime data message
     * @return true expiratory volume
     */
    double getExpVolume(int  binaryValue);

    /**
     * @pre values for plethMin, plethMax, and plethMaxBin have all been
     * obtained from the Evita XL.
     * @brief getPleth calculates and returns the true pleth based on the binary
     * value received in a realtime data message using plethMin, plethMax,
     * and plethMaxBin
     * @param binaryValue  binary value received in a realtime data message
     * @return true pleth
     */
    double getPleth(int binaryValue);

    /**
     * @pre values for resp_vol_since_insp_beginMin,
     * resp_vol_since_insp_beginMax, and resp_vol_since_insp_beginMaxBin have
     * all been obtained from the Evita XL.
     *
     * @brief get_resp_vol_since_insp_begin  calculates and returns the true
     * respiratory volume since inspiration began based on the binary value
     * received in a realtime data message using resp_vol_since_insp_beginMin,
     * resp_vol_since_insp_beginMax, and resp_vol_since_insp_beginMaxBin
     *
     * @param binaryValue  binary value received in a realtime data message
     * @return true respiratory volume since inspiration began
     */
    double get_resp_vol_since_insp_begin(int binaryValue);

    /**
     * @brief requestSlowData sends a request for "slow" data to the serial port
     * according to the MEDIBUS protocol
     */
    void requestSlowData();

    /**
     * @pre scannedInput contains a "slow" data transmission message from the
     * Evita XL.
     *
     * @post values for temperature, minimumAirwayPressure, meanAirwayPressure,
     * peepAirwayPressure, peakAirwayPressure, and expiratoryTidalVolume are
     * extracted from the message
     *
     * @brief receiveSlowData
     */
    void receiveSlowData();

    /**
     * @pre scannedInput contains a realtime data transmission message from the
     * Evita XL.
     *
     * @post values for airwayPressure, flow, co2, pleth,
     * resp_vol_since_insp_begin, and expVolume are extracted from the message
     *
     * @brief receiveRealtimeData
     */
    void receiveRealtimeData();

    /**
     * @brief isValidIncommingMessage Determines whether or not message is a
     * valid MEDIBUS command or response. If so, returns true, otherwise
     * returns false.
     * @param message
     * @return true if valid MEDIBUS command/response, false otherwise.
     */
    bool isValidIncommingMessage(std::string& message);

    /**
     * @brief isValidOutgoingMessage Determines whether or not the string of
     * size length pointed to by message is a valid MEDIBUS command or response.
     * If so, returns true, otherwise returns false.
     * @param message
     * @param length
     * @return  true if valid MEDIBUS command/response, false otherwise.
     */
    bool isValidOutgoingMessage(unsigned char* message, unsigned int length);



public:
    /**
     * @brief EvitaXL Disallow use of implicitly generated member functions.
     */
    EvitaXL();
    ~EvitaXL() {}

    typedef enum
    {
        exl_ok  = 0,
        exl_err_outofmemory,
        exl_err_openport

    }  evita_resultcode;

    static int resultcode_ok(int resultcode) { return resultcode == 0 ? 1 : 0; }

    const std::string getStatusMessage() const;

    /**
     * @brief Initializes global variables.
     * @param port The serial port number used to establish a connection.
     * @param verbosity Reporting level of output.
     * @param log Turn on/off the logging feature
     * @param bool array indicating which real time values to retrieve
     * @param source either port name or file name

     * @pre port is the name of the serial port which the Evita XL is
     * connected to and rtData is an array of booleans indicating which realtime
     * data values will be collected which contains no more than 3 true values.
     * The realtime data type represented by each index is the same as shown at
     * the top of this file for the attribute realtimeData[]
     *
     * @post A new EvitaXL object is created, which continuously collects and
     * logs data from the Evita XL ventilator.
     */
    int init(int port, int verbosity, bool log, bool * rtData, std::string source);
    /**
     * @brief openComport Attempts to open the port.
     * @see port
     */
    int openComport();
    /**
     * @brief openLogFile Attempts to open the log file
     * @see logFile
     */
    int openLogFile();
    /**
     * @brief run Main loop stays listening to the port.
     */
    void run();

    /**
     * @brief disconnect closes the connection to the Evita XL.
     */
    void disconnect();
};

#endif
