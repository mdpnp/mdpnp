/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    EvitaXL.cpp
 * @brief   Driver for EvitaXL.
 */

#include "EvitaXL.h"
using namespace std;

enum { _maxRealtimeDataValues = 6 };

EvitaXL::EvitaXL()
    : isConnected(false),
      flag(true),
      slowDataFlag(true),
      realtimeData(0)

{

}
 int EvitaXL:: init(int port, int verbosity, bool log, bool * rtData, string source)
 {
     int iret = 0;
     realtimeData = new bool [_maxRealtimeDataValues];


     scannedInput = "";
     isConnected = false;
     flag = slowDataFlag = true;
     this->port = port;
     this->verbosity = verbosity;
     this->log = log;
     this->source = source;
     for (int i = 0; i < _maxRealtimeDataValues; ++i)
         realtimeData[i] = rtData[i];
     try
     {

     airwayPressure = new DataSample();
     flow = new DataSample();
     co2 = new DataSample();
     pleth = new DataSample();
     resp_vol_since_insp_begin = new DataSample();
     expVolume = new DataSample();
     temperature = new DataSample();
     minimumAirwayPressure = new DataSample();
     meanAirwayPressure = new DataSample();
     peepAirwayPressure = new DataSample();
     peakAirwayPressure = new DataSample();
     expiratoryTidalVolume = new DataSample();
     } catch (...)
     {
         statusMessage = "bomb: out of memory, failed to create DataSamples";
         iret = exl_err_outofmemory;
         //        int iret = port.open(filename);
         //        if (iret)
         //        {
         //          statusMessage = std::string("Failed to open port ") + string(filename);
         //          rc = -1;
         //        }
     }
     return iret;
     }
void EvitaXL::readByte()
{
    // read 1 byte
    while(PollComport(hPort, &byte, 1) == 0);
    unsigned int character = (unsigned int)byte;// convert to a hex string
    string hex = itoa(character, 16);

    // append a '0' to the beginning of the string if necessary, so that each
    // byte is represented by 2 characters in scannedInput
    if(hex.length() == 1)
    {
        hex.insert(0, "0");
    }
    // append the result to scannedInput
    scannedInput.append(hex);
}

void EvitaXL::receiveMessage()
{
    // read 1 byte
    while(PollComport(hPort, &byte, 1) == 0);
//    if (INCREDIBLYVERBOSE) {
//        cout << "Byte Received: " <<  byte << endl;
//    }
    if (verbosity == 2) {
        cout << "Byte Received: " <<  byte << endl;
    }
    // this signals the beginning of a "slow" command
    if (byte == COMMAND_HEADER)
    {
        scannedInput = "0x1b";// keep reading bytes and appending them to
                              // scannedInput until the end signal is reached
        while(byte != SLOW_MESSAGE_END)
        {
            readByte();

            // a realtime data message has interupted this command, handle it
            // then get back to processing this "slow" commmand
            if(byte >= NONE && byte <= ALL)
            {
                string temp = scannedInput.substr(0, scannedInput.length()-2);
                scannedInput.erase(0, scannedInput.length()-2);
                receiveRealtimeData();
                scannedInput = temp;
            }
        }
        processSlowCommand();
    }

    // this signals the beginning of a realtime data message
    else if(byte >= NONE && byte <= ALL)
    {
        receiveRealtimeData();
    }
    // this signals a response to a "slow" request we sent to the Evita XL
    else if(byte == RESPONSE_HEADER)
    {
        scannedInput = "0x01";
        while(byte != SLOW_MESSAGE_END)
        {
            // a realtime data message has interupted this command, handle it
            // then get back to processing this "slow" commmand
            readByte();
            if(byte >= NONE && byte <= ALL)
            {
                string temp = scannedInput.substr(0, scannedInput.length()-2);
                scannedInput.erase(0, scannedInput.length()-2);
                receiveRealtimeData();
                scannedInput = temp;
            }
        }

        // depending on the content of the message, decide how to handle it

        // "slow" data message
        if(scannedInput.substr(0, 6).compare("0x0124") == 0)
        {
            receiveSlowData();
            return;
        }
        // realtime config response
        else if(scannedInput.substr(0, 6).compare("0x0153") == 0)
            getRealtimeConfiguration();
        flag = false;
    }
}

void EvitaXL::receiveRealtimeData()
{
    string lowOrderBits;// Evaluate the first byte of the realtime message and
    // determine which values are being transmitted by looking at the lower 4
    // bits. Each binary digit represents a different realtime data type and a
    // '1' means a value for that data type is being transmitted.  Order was
    // determined by a realtime config command previously sent to the Evita
    // XL. Note: while it is impossible for the MSB of these 4 bits to equal
    // 1, because the Evita XL can only transmit 3 realtime data at a time,
    // all cases are listed for code reuse in other Drager ventilators which
    // follow the MEDIBUS protocol.
    switch(byte)
    {
        case NONE:
            lowOrderBits = "0000";
            break;
        case FIRST:
            lowOrderBits = "0001";
            break;
        case SECOND:
            lowOrderBits = "0010";
            break;
        case FIRST_AND_SECOND:
            lowOrderBits = "0011";
            break;
        case THIRD:
            lowOrderBits = "0100";
            break;
        case FIRST_AND_THIRD:
            lowOrderBits = "0101";
            break;
        case SECOND_AND_THIRD:
            lowOrderBits = "0110";
            break;
        case FIRST_SECOND_AND_THIRD:
            lowOrderBits = "0111";
            break;
        case FOURTH:
            lowOrderBits = "1000";
            break;
        case FIRST_AND_FOURTH:
            lowOrderBits = "1001";
            break;
        case SECOND_AND_FOURTH:
            lowOrderBits = "1010";
            break;
        case FIRST_SECOND_AND_FOURTH:
            lowOrderBits = "1011";
            break;
        case THIRD_AND_FOURTH:
            lowOrderBits = "1100";
            break;
        case FIRST_THIRD_AND_FOURTH:
            lowOrderBits = "1101";
            break;
        case SECOND_THIRD_AND_FOURTH:
            lowOrderBits = "1110";
            break;
        case ALL:
            lowOrderBits = "1111";
            break;
        default:
            break;
    }
    scannedInput = "";

    // get the first realtime data value in the message
    for(int i = 0; i < 2; ++i)
    {
        readByte();
    }
    string time_stamp = timeStamp();
    processRealtimeCommand(lowOrderBits, time_stamp);
}

void EvitaXL::sendMessage(unsigned char* message, unsigned int length)
{
    if(isValidOutgoingMessage(message, length))
        SendBuf(hPort, message, length);
}

unsigned int EvitaXL::htoi(string& hexString)
{
    unsigned int answer = 0;
    unsigned int i = 0;
    bool valid = true;

    // check to see if the string begins with "0x or "0X". If so, ignore these
    // characters
    if(hexString.at(i) == '0')
    {
        ++i;
        if(hexString.at(i) == 'x' || hexString.at(i) == 'X')
        {
            ++i;
        }
    }

    for( ; valid && i < hexString.length(); ++i)
    {
        char c = hexString.at(i);// multiply by 16 before adding the value of
                                 // the next hex digit
        answer = answer * 16;
        if(c >= '0' && c <= '9')
        {
            answer = answer + (c - '0');
        }
        else
        {
            switch(c)
            {
                case 'a':
                case 'A':
                    answer+= 10;
                    break;
                case 'b':
                case 'B':
                    answer += 11;
                    break;
                case 'c':
                case 'C':
                    answer += 12;
                    break;
                case 'd':
                case 'D':
                    answer+= 13;
                    break;
                case 'e':
                case 'E':
                    answer += 14;
                    break;
                case 'f':
                case 'F':
                    answer += 15;
                    break;
                default:
                    valid = false;
            }
        }
    }
    if(!valid)
    {
        cout << "\nError: An invalid string was passed to the hex parser!!!\n";
        while(true);
    }
    return answer;
}

string EvitaXL:: itoa(int value, unsigned int base) {

    std::string buf;

    // check that the base is valid
    if (base < 2 || base > 16)
    {
        cout << "\n\nERROR: Invalid base value passed to function itoa()- Base"\
                "must be between 2 and 16\n\n";
        return buf;
    }

    enum { kMaxDigits = 35 };
    buf.reserve( kMaxDigits );// Pre-allocate enough space.

    int quotient = value;

    // Translating number to string with base:
    do {
        int index = quotient % base;
        if(index < 0)
            index *= -1;
        buf += "0123456789abcdef"[index];
        quotient /= base;
    } while ( quotient );

    // Append the negative sign
    if ( value < 0) buf += '-';

    // reverse the result
    int i = 0;
    for(int j = buf.length()-1; i < j; ++i)
    {
        char c = buf[i];
        buf[i] = buf[j];
        buf[j] = c;
        --j;
    }

    return buf;
}


string EvitaXL::itoh(unsigned int dec)
{
    int i = 0;
    stack <int>remainder;
    string hex, temp;
    char hexDigits[] = { "0123456789ABCDEF" };
    if(dec == 0)
        hex = hexDigits[0];
    while (dec != 0)
    {
        remainder.push(dec % 16);
        dec /= 16;    ++i;
    }
    while (i != 0)
    {
        if (remainder.top() > 15)
        {
            temp = itoh(remainder.top());
            hex += temp;
        }
        hex.push_back(hexDigits[remainder.top()]);
        remainder.pop();
        --i;
    }
    return hex;
}

void EvitaXL::addNextChar(string& character, string& buff)
{
    // append a character to buff based on the hexidecimal ASCII value in the
    // string character
    if (character.compare("20") == 0)
        ;
    else if (character.compare("2d") == 0)
    {
        buff.append("-");
    }
    else if (character.compare("2e") == 0)
    {
        buff.append(".");
    }
    else if (character.compare("41") == 0)
    {
        buff.append("A");
    }
    else if (character.compare("42") == 0)
    {
        buff.append("B");
    }
    else if (character.compare("43") == 0)
    {
        buff.append("C");
    }
    else if (character.compare("44") == 0)
    {
        buff.append("D");
    }
    else if (character.compare("45") == 0)
    {
        buff.append("E");
    }
    else if (character.compare("46") == 0)
    {
        buff.append("F");
    }
    // must be an integer
    else
    {
        int i = atoi(character.c_str())-30;// ASCII hex values for '0' to '9'
                                           // = 30-39
        string value = itoa(i, 10);
        buff.append(value);
    }
}

int EvitaXL::getBinaryValue()
{
    int result = 0;
    switch(scannedInput.at(0))
    {
        case '9':
        case 'd':
            result += 16;
            break;
        case 'a':
        case 'e':
            result += 32;
            break;
        case 'b':
        case 'f':
            result += 48;
            break;
        default:
            break;
    }

    switch(scannedInput.at(1))
    {
        case '1':
            result += 1;
            break;
        case '2':
            result += 2;
            break;
        case '3':
            result += 3;
            break;
        case '4':
            result += 4;
            break;
        case '5':
            result += 5;
            break;
        case '6':
            result += 6;
            break;
        case '7':
            result += 7;
            break;
        case '8':
            result += 8;
            break;
        case '9':
            result += 9;
            break;
        case 'a':
            result += 10;
            break;
        case 'b':
            result += 11;
            break;
        case 'c':
            result += 12;
            break;
        case 'd':
            result += 13;
            break;
        case 'e':
            result += 14;
            break;
        case 'f':
            result += 15;
            break;
        default:
            break;
    }

    switch(scannedInput.at(2))
    {
        case '9':
        case 'd':
            result += 1024;
            break;
        case 'a':
        case 'e':
            result += 2048;
            break;
        case 'b':
        case 'f':
            result += 3072;
            break;
        default:
            break;
    }

    switch(scannedInput.at(3))
    {
        case '1':
            result += 64;
            break;
        case '2':
            result += 128;
            break;
        case '3':
            result += 192;
            break;
        case '4':
            result += 256;
            break;
        case '5':
            result += 320;
            break;
        case '6':
            result += 384 ;
            break;
        case '7':
            result += 448;
            break;
        case '8':
            result += 512;
            break;
        case '9':
            result += 576;
            break;
        case 'a':
            result += 640;
            break;
        case 'b':
            result += 704;
            break;
        case 'c':
            result += 768;
            break;
        case 'd':
            result += 832;
            break;
        case 'e':
            result += 896;
            break;
        case 'f':
            result += 960;
            break;
        default:
            break;
    }
    return result;

}

void EvitaXL::initializeCommunication()
{
    unsigned char initialCom[] = INITIALIZE_COMMUNICATION_RESPONSE;
    sendMessage(initialCom, 5);
    cout << "\nICC response command sent to Evita XL\n";
}

void EvitaXL::initializeRealtimeCommunication()
{


    unsigned char configureRequest[] = REALTIME_CONFIG_COMMAND;

    unsigned char dataRequest[18];
    dataRequest[0] = COMMAND_HEADER;
    dataRequest[1] = REALTIME_TRANSMISSION_CODE;

    unsigned int length = 2;// current length of the data request message

    unsigned int sum = COMMAND_HEADER + REALTIME_TRANSMISSION_CODE;// checksum
    // for the data request message

    int currentPos = length - 1;

    // If we're observing airway pressure, add the corresponding data code
    // to the realtime data request
    if(realtimeData[0])
    {
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '0' + '0' + '0' + '3';
    }

    // If we're observing flow, add the corresponding data code to the realtime
    // data request
    if(realtimeData[1])
    {
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '1';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '0' + '1' + '0' + '3';
    }

    // If we're observing pleth, add the corresponding data code to the
    // realtime data request
    if(realtimeData[2])
    {
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '2';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '0' + '2' + '0' + '3';
    }

    // If we're observing resp. vol. since insp. began, add the corresponding
    // data code to the realtime data request
    if(realtimeData[3])
    {
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '0' + '3' + '0' + '3';
    }

    // If we're observing CO2, add the corresponding data code to the realtime
    // data request
    if(realtimeData[4])
    {
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '8';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '0' + '8' + '0' + '3';
    }

    // If we're observing expiratory volume, add the corresponding data code
    // to the realtime data request
    if(realtimeData[5])
    {
        dataRequest[++currentPos] = '2';
        dataRequest[++currentPos] = '4';
        dataRequest[++currentPos] = '0';
        dataRequest[++currentPos] = '3';
        length += 4;
        sum = sum + '2' + '4' + '0' + '3';
    }

    string hexSum = itoh(sum);// convert checksum to hexadecimal

    // append the 2 least significant hex digits from the checksum
    dataRequest[++currentPos] = hexSum.at(hexSum.length()-2);
    dataRequest[++currentPos] = hexSum.at(hexSum.length()-1);

    dataRequest[++currentPos] = SLOW_MESSAGE_END;// end of request
    // dataRequest[++currentPos] = '\0';
    length += 3;

    unsigned char enableMessage[] = ENABLE_REALTIME_COM_COMMAND;

    sendMessage(configureRequest, 5);
    cout << "\n\nConfig request sent to Evita XL\n";// wait to receive the
    // realtime configuration
    while (flag)
        receiveMessage();
    flag = true;// tell the Evita XL what data we want to collect
    sendMessage(dataRequest, length);
    cout << "\nData request sent to Evita XL\n";// wait to receive confirmation
    while(flag)
        receiveMessage();
    flag = true;

    // tell the Evita XL to begin transmitting realtime data
    sendMessage(enableMessage, 5);
    cout << "\nEnable Message sent to Evita XL\n\nCollecting patient data...\n";
}

void EvitaXL::getRealtimeConfiguration()
{
    // airway pressure
    string buff = "";
    string substr = "";
    for (int i = 26; i < 35; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    pressureMin = atof(buff.c_str());
    buff.clear();

    for (int i = 36 ; i < 45; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    pressureMax = atof(buff.c_str());
    buff.clear();

    for (int i = 46 ; i < 51; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    pressureMaxBin = htoi(buff);
    buff.clear();

    // flow
    for (int i = 72; i < 81; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    flowMin = atof(buff.c_str());
    buff.clear();

    for (int i = 82 ; i < 91; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    flowMax = atof(buff.c_str());
    buff.clear();

    for (int i = 92; i < 97; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    flowMaxBin = htoi(buff);
    buff.clear();

    // pleth
    for (int i = 118; i < 127; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    plethMin = atof(buff.c_str());
    buff.clear();

    for (int i = 128 ; i < 137; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    plethMax = atof(buff.c_str());
    buff.clear();

    for (int i = 138; i < 143; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    plethMaxBin = htoi(buff);
    buff.clear();

    // Respiratory Volume since Inspiration Began
    for (int i = 164; i < 173; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    resp_vol_since_insp_beginMin = atof(buff.c_str());
    buff.clear();

    for (int i = 174 ; i < 183; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    resp_vol_since_insp_beginMax = atof(buff.c_str());
    buff.clear();

    for (int i = 184; i < 189; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    resp_vol_since_insp_beginMaxBin = htoi(buff);
    buff.clear();

    // CO2
    for (int i = 302; i < 311; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    co2Min = atof(buff.c_str());
    buff.clear();

    for (int i = 312; i < 321; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    co2Max = atof(buff.c_str());
    buff.clear();

    for (int i = 322; i < 327; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    co2MaxBin = htoi(buff);
    buff.clear();

    // Expiratory Volume
    for (int i = 440; i < 449; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    expVolumeMin = atof(buff.c_str());
    buff.clear();

    for (int i = 450 ; i < 459; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    expVolumeMax = atof(buff.c_str());
    buff.clear();

    for (int i = 460; i < 465; i += 2)
    {
        substr = scannedInput.substr(i,2);
        addNextChar(substr, buff);
    }
    expVolumeMaxBin = htoi(buff);
    buff.clear();
}

void EvitaXL::sendDeviceID()
{
    unsigned char deviceID[] = DEVICE_ID_RESPONSE;
    sendMessage(deviceID, 5);
    cout << "\nDevice ID sent to EvitaXL\n";
    isConnected = true;
}


void EvitaXL::sendNOP()
{
    unsigned char nop[] = NOP_COMMAND;
//    int i;
    sendMessage(nop, 5);
}

void EvitaXL::sendNOPResponse()
{
    unsigned char nopResponse[] = NOP_RESPONSE;
//    int i;
    sendMessage(nopResponse, 5);
    cout << "\nNOP Response sent to COM1\n";
}

void EvitaXL::processSlowCommand()
{
    cout << "\nCommand Received: " << scannedInput << endl;
    if (scannedInput.compare(INITIALIZE_COMMUNICATION_COMMAND) == 0)
        initializeCommunication();

    else if (scannedInput.compare(DEVICE_ID_COMMAND) == 0)
    {
        sendDeviceID();
        configureSlowDataTransfer();
        requestSlowData();
        while(slowDataFlag)
            receiveMessage();
        if(realtimeData[0] || realtimeData[1] || realtimeData[2] ||
           realtimeData[3] || realtimeData[4] || realtimeData[5])
            initializeRealtimeCommunication();
    }

    // NOP- respond to keep comunication alive
    else if (scannedInput.compare("0x1b3034420d") == 0)
        sendNOPResponse();
}

void EvitaXL::processRealtimeCommand(string& lowOrderBits, string& timeStamp)
{
    // check to see if the realtime message contains the an event code
    if(scannedInput.compare(START_OF_EXP_CYCLE_INDICATOR) == 0 ||
       scannedInput.compare(START_OF_INSP_CYCLE_INDICATOR) == 0)
    {
        if(lowOrderBits.compare("0000") != 0)
        {
            scannedInput.clear();
            for(int i = 0; i < 2; ++i)
            {
                readByte();
            }
        }
    }

    int binaryValue;

    // check if the message contains a value for the first realtime data type
    // we requested
    if(lowOrderBits.at(3) == '1')
    {
        // 0btain the value that was sent
        binaryValue = getBinaryValue();
        int index;

        // determine which realtime data type we requested first
        for(index = 0; index < 6; ++index)
        {
            if(realtimeData[index])
                break;
        }

        // use the binary value to get the appropriate actual value
        switch(index)
        {
            case 0:
                airwayPressure->set(getAirwayPressure(binaryValue), timeStamp);
                break;
            case 1:
                flow->set(getFlow(binaryValue), timeStamp);
                break;
            case 2:
                pleth->set(getPleth(binaryValue), timeStamp);
                break;
            case 3:
                resp_vol_since_insp_begin->set(get_resp_vol_since_insp_begin(
                                                   binaryValue), timeStamp);
                break;
            case 4:
                co2->set(getCO2(binaryValue), timeStamp);
                break;
            case 5:
                expVolume->set(getExpVolume(binaryValue), timeStamp);
                break;
        }
        // change lowOrderBits so that we can move on to the next data value
        lowOrderBits.replace(3, 1, "0");
        scannedInput.clear();
        if(lowOrderBits.compare("0000") != 0)// check if there's more data in
            // the message
        {
            for (int j = 0; j < 2; ++j)
            {
                readByte();
            }
            processRealtimeCommand(lowOrderBits, timeStamp);
        }
        else
        {
            logData();
        }
    }

    // check if the message contains a value for the 2nd realtime data type we
    // requested
    else if(lowOrderBits.at(2) == '1')
    {
        // 0btain the value that was sent
        binaryValue = getBinaryValue();
        int index, count = 0;

        // determine which realtime data type we requested second
        for(index = 0; index < 6; ++index)
        {
            if(realtimeData[index])
                ++count;
            if(count == 2)
                break;
        }
        // use the binary value to get the appropriate actual value
        switch(index)
        {
            case 1:
                flow->set(getFlow(binaryValue), timeStamp);
                break;
            case 2:
                pleth->set(getPleth(binaryValue), timeStamp);
                break;
            case 3:
                resp_vol_since_insp_begin->set(get_resp_vol_since_insp_begin(
                                                   binaryValue), timeStamp);
                break;
            case 4:
                co2->set(getCO2(binaryValue), timeStamp);
                break;
            case 5:
                expVolume->set(getExpVolume(binaryValue), timeStamp);
                break;
        }
        // change lowOrderBits so that we can move on to the next data value
        lowOrderBits.replace(2, 1, "0");
        scannedInput.clear();// check if there's more data in the message
        if(lowOrderBits.compare("0000") != 0)
        {
            for (int j = 0; j < 2; ++j)
            {
                readByte();
            }
            processRealtimeCommand(lowOrderBits, timeStamp);
        }
        else
        {
            logData();
        }
    }

    // check if the message contains a value for the 3rd realtime data type we
    // requested
    else if(lowOrderBits.at(1) == '1')
    {
        // 0btain the value that was sent
        binaryValue = getBinaryValue();
        int index, count = 0;

        // determine which realtime data type we requested third
        for(index = 0; index < 6; ++index)
        {
            if(realtimeData[index])
                ++count;
            if(count == 3)
                break;
        }
        // use the binary value to get the appropriate actual value
        switch(index)
        {
            case 2:
                pleth->set(getPleth(binaryValue), timeStamp);
                break;
            case 3:
                resp_vol_since_insp_begin->set(get_resp_vol_since_insp_begin(
                                                   binaryValue), timeStamp);
                break;
            case 4:
                co2->set(getCO2(binaryValue), timeStamp);
                break;
            case 5:
                expVolume->set(getExpVolume(binaryValue), timeStamp);
                break;
        }
        // change lowOrderBits so that we can move on to the next data value
        lowOrderBits.replace(1, 1, "0");
        scannedInput.clear();// check if there's more data in the message
        if(lowOrderBits.compare("0000") != 0)
        {
            for (int j = 0; j < 2; ++j)
            {
                readByte();
            }
            processRealtimeCommand(lowOrderBits, timeStamp);
        }
        else
        {
            logData();
        }
    }
}

double EvitaXL::getAirwayPressure(int binaryValue)
{
    double pressure = binaryValue * (pressureMax - pressureMin)
                      / pressureMaxBin;
    pressure += pressureMin;
    return pressure;
}

double EvitaXL::getCO2(int binaryValue)
{
    double carbonDioxide = binaryValue * (co2Max - co2Min) / co2MaxBin;
    carbonDioxide += co2Min;
    return carbonDioxide * 0.1;
}

double EvitaXL::getFlow(int binaryValue)
{
    double result = binaryValue * (flowMax - flowMin) / flowMaxBin;
    result += flowMin;
    return result;
}

double EvitaXL::getPleth(int binaryValue)
{
    double pleth = binaryValue * (plethMax - plethMin) / plethMaxBin;
    pleth += plethMin;
    return pleth;
}

double EvitaXL::get_resp_vol_since_insp_begin(int binaryValue)
{
    double volume = binaryValue * (resp_vol_since_insp_beginMax -
                                   resp_vol_since_insp_beginMin) /
                    resp_vol_since_insp_beginMaxBin;
    volume += resp_vol_since_insp_beginMin;
    return volume;
}

double EvitaXL::getExpVolume(int binaryValue)
{
    double volume = binaryValue * (expVolumeMax - expVolumeMin)
                    / expVolumeMaxBin;
    volume += expVolumeMin;
    return volume;
}

void EvitaXL::requestSlowData()
{
    unsigned char dataRequest[] = SLOW_DATA_COMMAND;
    sendMessage(dataRequest, 5);
}

void EvitaXL::configureSlowDataTransfer()
{
    // a data request must be sent before we can change what "slow" data we
    // want to receive
    requestSlowData();// wait for confirmation
    while(flag)
        receiveMessage();
    flag = true;

    // tell the Evita XL what "slow" data we want to receive upon request
    unsigned char configCommand[] = {COMMAND_HEADER,
                                     DATA_CONFIG_CODE,
                                     SLOW_DATA_CODE,
                                     GAS_TEMPERATURE,
                                     EXP_TIDAL_VOLUME_LITERS,
                                     EXP_TIDAL_VOLUME_MILLILITERS,
                                     MIN_AIRWAY_PRESSURE,
                                     MEAN_AIRWAY_PRESSURE,
                                     PEEP_AIRWAY_PRESSURE,
                                     PEAK_AIRWAY_PRESSURE,
                                     0x39, 0x33,
                                     SLOW_MESSAGE_END};

    sendMessage(configCommand, 20);// wait for confirmation
    while(flag)
        receiveMessage();
    flag = true;
}

void EvitaXL::receiveSlowData()
{
    // ensure this is a data message and not a response to another command
    if(scannedInput.length() > 12)
    {
        string time_stamp = timeStamp();
        int i = 6;// index in data message where data codes/values begin
        getTemperature(i, time_stamp);
        getExpiratoryTidalVolume(i, time_stamp);
        getMinimumAirwayPressure(i,  time_stamp);
        getMeanAirwayPressure(i, time_stamp);
        getPEEPAirwayPressure(i, time_stamp);
        // now we can begin logging data w ithout worrying about "garbage"
        // being displayed for "slow" data values
        getPeakAirwayPressure(i, time_stamp);
        slowDataFlag = false;
    }

    else
    {
        flag = false;
    }
    // if we aren't collecting any realtime data, log the "slow" data
    if(!realtimeData[0] && !realtimeData[1] && !realtimeData[2] &&
       !realtimeData[3] && !realtimeData[4] && !realtimeData[5] &&!slowDataFlag)
    {
        logData();
    }
}


void EvitaXL::getPeakAirwayPressure(int& i, string& timeStamp)
{
    // ensure that we are reading the value for peak airway pressure by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("3744") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        while(temp.compare("20") == 0)
        {
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        for(int j = 0; temp.compare("20") != 0; ++j)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        peakAirwayPressure->set(atoi(result.c_str()), timeStamp);
        ++i;
    }
}

void EvitaXL::getPEEPAirwayPressure(int& i, string& timeStamp)
{
    // ensure that we are reading the value for PEEP airway pressure by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("3738") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        while(temp.compare("20") == 0)
        {
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        for(int j = 0; temp.compare("20") != 0; ++j)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        peepAirwayPressure->set(atoi(result.c_str()), timeStamp);
        ++i;
    }
}

void EvitaXL::getMeanAirwayPressure(int& i, string& timeStamp)
{
    // ensure that we are reading the value for mean airway pressure by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("3733") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        while(temp.compare("20") == 0)
        {
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        for(int j = 0; temp.compare("20") != 0; ++j)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        meanAirwayPressure->set(atoi(result.c_str()), timeStamp);
        ++i;
    }
}
void EvitaXL::getMinimumAirwayPressure(int& i, string& timeStamp)
{
    // ensure that we are reading the value for minimum airway pressure by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("3731") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        while(temp.compare("20") == 0)
        {
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        for(int j = 0; temp.compare("20") != 0; ++j)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        minimumAirwayPressure->set(atoi(result.c_str()), timeStamp);
        ++i;
    }
}

// tell the Evita XL what "slow" data we want to rece
void EvitaXL::getTemperature(int& i, string& timeStamp)
{
    // ensure that we are reading the value for temperature by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("4331") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        while(temp.compare("20") == 0)
        {
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        while(temp.compare("20") !=0)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        temperature->set(atoi(result.c_str()), timeStamp);
        ++i;
    }
}



void EvitaXL::getExpiratoryTidalVolume(int& i, string& timeStamp)
{
    // ensure that we are reading the value for expiratory tidal volume by
    // confirming the data code
    if (scannedInput.substr(i, 4).compare("3832") == 0)
    {
        i+=4;
        string temp = scannedInput.substr(i, 2);
        string result = "";
        ++i;

        for (int j = 0; j < 4; ++j)
        {
            addNextChar(temp, result);
            temp[0] = scannedInput.at(++i);
            temp[1] = scannedInput.at(++i);
        }
        expiratoryTidalVolume->set(atof(result.c_str()), timeStamp);

        // if the value is less than 1, a value for expiratory tidal volume in
        // mL will also be sent, get it and convert to liters for more
        // precision.
        if(expiratoryTidalVolume->getValue() < 1.0)
        {
            i+=3;
            result = "";
            temp[0] = scannedInput.at(i);
            temp[1] = scannedInput.at(++i);
            for(int j = 0; j < 4; ++j)
            {
                addNextChar(temp, result);
                temp[0] = scannedInput.at(++i);
                temp[1] = scannedInput.at(++i);
            }
            // convert to liters
            expiratoryTidalVolume->set(atoi(result.c_str()) * 0.001, timeStamp);
            --i;
        }
        else
        {
            i+=11;
        }
    }
}

string EvitaXL::timeStamp()
{
    time_t ltime;// calendar time
    struct timeval epoch_time;
    gettimeofday(&epoch_time, NULL);
    ltime = static_cast<time_t>(epoch_time.tv_sec);
    string timeStamp = asctime(localtime(&ltime));
    timeStamp.erase(timeStamp.length()-1, 1);
    string milliseconds = itoa((int) epoch_time.tv_usec / 1000, 10);
    switch(milliseconds.length()){
        case 1:
            milliseconds.insert(0, ".00");
            break;
        case 2:
            milliseconds.insert(0, ".0");
            break;
        default:
            milliseconds.insert(0, ".");
    }
    timeStamp.insert(timeStamp.find_last_of(':') + 3, milliseconds);
    return timeStamp;
}

void EvitaXL::logData()
{
    requestSlowData();
    // print time stamp to log file
    fprintf(logFile, "\n%s", timeStamp().c_str());
    fprintf(logFile, "\t");
    printf("\n");
    // print the appropriate realtime data values to the log file
    if(realtimeData[0]){
        printf("%f\t", airwayPressure->getValue());
        fprintf(logFile, "%f\t", airwayPressure->getValue());
    }
    if(realtimeData[1]){
        printf("%f\t", flow->getValue());
        fprintf(logFile, "%f\t", flow->getValue());
    }
    if(realtimeData[2])
    {
        printf("%f\t", pleth->getValue());
        fprintf(logFile, "%f\t", pleth->getValue());
    }
    if(realtimeData[3]){
        printf("%f\t", resp_vol_since_insp_begin->getValue());
        fprintf(logFile, "%f\t", resp_vol_since_insp_begin->getValue());
    }
    if(realtimeData[4]){
        printf("%f\t", co2->getValue());
        fprintf(logFile, "%f\t", co2->getValue());
    }
    if(realtimeData[5])
    {
        printf("%f\t", expVolume->getValue());
        fprintf(logFile, "%f\t", expVolume->getValue());
    }
    // print the "slow" data values to log file
    fprintf(logFile, "\t%d\t%f\t%d\t%d\t%d\t%d",
            (int) temperature->getValue(),
            expiratoryTidalVolume->getValue(),
            (int) minimumAirwayPressure->getValue(),
            (int) peakAirwayPressure->getValue(),
            (int) meanAirwayPressure->getValue(),
            (int) peepAirwayPressure->getValue());
}

bool EvitaXL::isValidOutgoingMessage(unsigned char* message,
                                     unsigned int length)
{
    if(length == 0)
        return true;

    if (message[0] >= NONE && message[0] <= ALL)
        return true;

    if((message[0] != COMMAND_HEADER && message[0] != RESPONSE_HEADER)
       || message[length-1] != SLOW_MESSAGE_END)
    {
        cout << "\n\nError: Attempted to send invalid message to the Evita XL-"\
                "Message is not properly structured.\n\n";
        return false;
    }

    unsigned int sum = 0;
    for (unsigned int i = 0; i < length-3; ++i)
    {
        sum += message[i];
    }
    string hex = itoh(sum);
    unsigned int hexLength = hex.length();
    if(hex.at(hexLength-2) == message[length-3] &&
       hex.at(hexLength-1) == message[length-2])
        return true;
    else
    {
        cout << "\n\nError: Attempted to send invalid message to the"\
                "Evita XL- Incorrect checksum.\n\n";
        return false;
    }
}

bool EvitaXL::isValidIncommingMessage(string& message)
{
    /// @todo
    return false;
}

void EvitaXL::disconnect()
{
    unsigned char stop[] = STOP_COMMAND;
    sendMessage(stop, 5);
    close(hPort);
}

const std::string EvitaXL::getStatusMessage() const
{
  return(statusMessage);
}


/**
 * Prints usage information to the standard out on how to run the program.
 */
static void printUsage(char * name)
{
    std::cout << "NAME\n\t" << name << " - EvitaXL driver "<<endl;
    std::cout << "\nSYNOPSIS\n\t" \
              << name  \
              << " -p portname [-r air|flow|pleth|respvol|co2|expvol] [-v 1|2]"\
                 " [-l on|off]"  \
              << "\n\t\t| -s simulationfile"  \
              << "\n\t\t| -h";
    std::cout << "\nDESCRIPTION\n\t" \
              << "Logs data from EvitaXL medical device." ;
    std::cout << "\nFUNCTION LETTERS\n\t" \
              << "Main operation mode:"  \
              << "\n\n\t-p portname\n\t"  \
              << "The program will attempt to establish a connection to"\
                 "the\n\t" \
              << "specified 'portname'." \
              << endl
              << "\n\t-s simulationfile\n\t"  \
              << "The program will run in simulation mode by retrieving the"\
                 " data  \n\t"  \
              << "specified in 'simulationfile'"  \
              << endl
              << "\n\t-h\n\t"  \
              << "Help. Displays this page." ;
    std::cout << "\nOTHER OPTIONS\n\t" \
              << " Operation modifiers:"  \
              << "\n\n\t-r  air | flow | pleth | respvol | co2 | expvol\n\t"  \
              << "Denotes the option to retrieve real time data. The program "\
                 "is able \n\t" \
              << "to retrieve a maximun of three (3) real time values. "\
                 "If specified,\n\t" \
              << "you must pick at least one (1) real time value. The "\
                 "available real \n\t" \
              << "time values are as follow \n\t" \
              << "\t air     --- Airway Pressure(mbar) \n\t" \
              << "\t flow    --- Flow(L/min) \n\t" \
              << "\t pleth   --- Pleth(%%) \n\t" \
              << "\t respvol --- Resp. Vol. Since Insp. Begin(mL) \n\t" \
              << "\t co2     --- Exp. CO2(Vol.%%) \n\t" \
              << "\t expvol  --- Exp. Volume(mL) \n\t" \
              << "\n\t-v 1 | 2\n\tverbosity level\n\t"  \
              << "1 turns on verbose reporting, 2 prints every byte "\
                 "received\n\t\n"  \
              << "-l on|off\n\t"  \
              << "Turns on/off the log. Default: on";
    std::cout << "\nEXAMPLES\n\t" \
              << "To run using serial port ttyS0, with verbosity reporting,\n"\
              << "collecting Airway Pressure and Exp. Volume and logging "\
                 "capability:"  \
              << "\n\t"
              << name
              << " -p /dev/ttyS0 -r air expvol -v 1 -l on\n\t"  \
              << endl;
    //     std::cout << "\nPress ENTER key to exit...\n";
    //     std::cin.get();
    exit(0);
}

static void err(int errNum)
{


    switch(errNum)
    {
        case 16:
        case 22:
            cout << "! Invalid port name specified for location of the "\
                    "Evita XL ventilator\n";
            break;
        case 1:
            cout << "! Cannot open serial port\n";
            break;
        case 2:
            cout << "! Cannot reate a log file.\n";
            break;

    }

    exit(errNum);
}

int main(int argc, char* argv[])
{
    bool hasArgs = argc > 1;// True if user provided arguments.
                            // False otherwise.
    bool hasReal = false;   // True if user specified real time data arg
                            // False otherwise. (-r)
    bool hasVerbosity = false;  // True if user specified verbosity level arg
                                // False otherwise. (-v)
    bool hasMode = false;       // True if user provided port/simulation arg
                                // False otherwise. (-s|-p)
    bool illegalArgs = false;   // True if bad arguments, False otherwise.
    bool hasHelp = false;       // True if user specified help argument
                                // False otherwise. (-h)
    bool hasLog = false;        // True if user provided log arg
                                // False otherwise. (-l)

    int portNumber;
    std::string source;// portname or filename from which to retrive data

    // legal real time data
    std::string validRealArgs[] = { "air", "flow", "pleth",
                              "respvol", "co2" , "expvol" };
    bool realArr[_maxRealtimeDataValues] = {false, false, false, false, false, false};

    int  verbosity  = 0;// verbosity of output, default to lowest (0)
    bool log = true;// log the data read to a file

    //Verifies arguments are of proper format -- not necessarily valid input.


    for(int i = 1; i < argc  && !illegalArgs  && !hasHelp; i++)
    {
        //        cout << "\ncurrent arg is " << argv[i] << endl;

        if (!strcmp (argv[i], "-h") )
            hasHelp = true;
        else if (i + 1 < argc)
            if ( !strcmp (argv[i], "-l")  )
            {
                if(hasLog)
                    illegalArgs = true;
                else
                {
                    log =  !strcmp (argv[i + 1], "on" ) ? true :
                           !strcmp (argv[i + 1], "off") ? false : true;
                    hasLog = true;
                    i++;
                }
            }
            else if ( !strcmp (argv[i], "-p")  || !strcmp (argv[i], "-s")  )
            {
                if(hasMode)
                    illegalArgs = true;
                else
                {
                    source = argv[++i];
                    hasMode = true;
                }
            }
            else if (!strcmp (argv[i], "-v"))
            {
                if(hasVerbosity)
                    illegalArgs = true;
                else
                {
                    // if not numeric, atoi returns 0 = default verbosity
                    verbosity = atoi(argv[++i]);
                    hasVerbosity = true;
                }
            }
            else if (!strcmp (argv[i], "-r") )
            {
                if(hasReal)
                    illegalArgs = true;
                else
                {
                    hasReal = true;
                    int count = 0;
                    for(int j = i + 1; j < argc && !illegalArgs; j++)
                    {
                        // new parameter
                        if( strchr(argv[j], '-') != NULL ) break;
                        bool isFound  = false;
                        for(unsigned int k = 0; k < sizeof(realArr)/sizeof(bool)
                            && !illegalArgs && !isFound; k++)
                        {
                            if(!strcmp (argv[j], validRealArgs[k].c_str()) )
                            {
                                // realtime data was previously requested
                                if(realArr[k])
                                    illegalArgs = true;
                                // realtime data was not previously requested
                                else
                                {
                                    realArr[k] = true;
                                    isFound  = true;
                                    count++;
                                }
                            }
                        }
                        if(!isFound)
                            illegalArgs = true;
                    }
                    if (count == 0 || count > 3)
                        illegalArgs = true;
                    i += count;
                }
            }
            else
                illegalArgs = true;
        else
            illegalArgs = true;
    }

    if(!hasArgs || illegalArgs || hasHelp || !hasMode)
        printUsage(argv[0]);

    if( source.find("/dev/ttyS")   != std::string::npos)
    {
        portNumber = atoi(source.substr(9).c_str());
        if(portNumber > 16)
            err(16);
    } else if ( source.find("/dev/ttyUSB") != std::string::npos)
    {
        portNumber = 16 + atoi(source.substr(11).c_str());
        if(portNumber > 22)
            err(22);
    }
    else
    {
        try{
            for(int i = source.length() - _maxRealtimeDataValues, j = 0;
                i < source.length(); i++, j++)

                realArr[j] = source[i] == '0' ? false:
                             source[i] == '1' ? true : false;
        } catch(...)
        {
            // bad file name
        }
        portNumber = -1 ;// -1 = run simulation mode
    }


    EvitaXL * evitaXL=new EvitaXL();

    int iret = evitaXL->init(portNumber, verbosity, log, realArr, source);

    if (!EvitaXL::resultcode_ok(iret))
    {
        printf("%s\n", evitaXL->getStatusMessage().c_str());
        exit(iret);
    }

    iret = evitaXL->openComport();
    if (iret == 0)
        iret =  evitaXL->openLogFile();
    else
        err(iret);
    if (iret == 0)
        evitaXL->run();
    else
        err(iret);
}


int EvitaXL::openComport()
{
    int	iret	= 0;

    if(port == -1)
        return iret;
    hPort = OpenComport(port, 19200);
    if(hPort == 1)
    {
        iret = 1;
        return iret;
    }
    if (verbosity)
        cout << "Opened Serial Port " << port << endl;
    return iret;
}

int EvitaXL::openLogFile()
{
    int	iret	= 0;
    if(port == -1 ) return iret;

    if((logFile=fopen("evitaXL_log", "w")) == NULL)
    {
        iret = 1;
        return iret;
    }
    if (verbosity)
        cout << "Log file opened\n";


    fprintf(logFile, "timestamp\t");
    if(realtimeData[0])
        fprintf(logFile, "air(mbar)\t");
    if(realtimeData[1])
        fprintf(logFile, "flow(L/min)\t");
    if(realtimeData[2])
        fprintf(logFile, "pleth(%%)\t");
    if(realtimeData[3])
        fprintf(logFile, "respvol(mL)\t");
    if(realtimeData[4])
        fprintf(logFile, "co2(Vol.%%)\t");
    if(realtimeData[5])
        fprintf(logFile, "expvol(mL)\t");

    /*
    Gas Temperature(C)
    Expiratory Tidal Volume(L)
    Minimum Airway Pressure(mbar)
    Peak Airway Pressure(mbar)
    Mean Airway Pressure(mbar)
    PEEP Airway Pressure(mbar)
    */

    fprintf(logFile, "gastemp(C)\texptidvol(L)\tminap(mbar)\tpeakap(mbar)"\
            "\tmeanap(mbar)\tpeepap(mbar)\n");

    return iret;
}

void EvitaXL::run()
{
    if (verbosity)
        cout << "Listening for messages on Port " + port << endl;

    if(port == -1)  /// @todo needs to sleep 24ms for every record
    {
        //        static const int nsecs = 24; //in milliseconds
        int fieldCount = 7;
        for(int i = 0; i < _maxRealtimeDataValues; i++)  //total real time waves
            if(realtimeData[i])
                fieldCount++;
        FILE *pFile = fopen(source.c_str(), "r");
        if(pFile != NULL)
        {
            DataRecord * pDataRecord = new DataRecord();
            float ftemp = 0.0;
            char stemp[64];
            char times[32];
            static bool firstLine = true;

            //read the values -- one record at a time
            while (!feof(pFile))
            {
                if(firstLine)
                {

                    // read the names and add to pDataRecord's names vector
                    for(int i = 0; i < fieldCount; i++)
                    {
                        fscanf(pFile, "%s", stemp);
                        pDataRecord->names.push_back(stemp);
                    }

                    //for (vector<string>::iterator i =
                    //pDataRecord->names.begin();
                    //i !=  pDataRecord->names.end();
                    //++i)
                    //{
                    //cout  << *i << " " << endl;
                    //}
                    firstLine = false;
                }
                else
                {

                    // empty vector before reading the next record.
                    pDataRecord->values.clear();
                    ftemp = 0.0; //reset value
                    for(int i = 0; i < fieldCount; i++)
                    {
                        if(i == 0) //first field is timestamp -- skip it
                        {
                            while(fgetc(pFile)!='\n')
                            {
                                fgetc(pFile);
                            }
                            fgets (times, 29, pFile);
                        }
                        else
                        {
                            fscanf(pFile, "%f", &ftemp);
                            pDataRecord->values.push_back(ftemp);
                        }
                    }
                    //cout << times << "times" << times << endl;
                    //for (vector<float>::iterator i =
                    //pDataRecord->values.begin();
                    //i !=  pDataRecord->values.end();
                    //++i)
                    //{
                    //cout  << *i << " " ;
                    //}
                    //cout  << endl;
                    //break;
                }
            }
        }
    }


    else
        while(true)
            receiveMessage();

}

