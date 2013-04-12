/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    DataSample.h
 * @brief   DataSample obj holds real time value from the EvitaXL.
 */

#ifndef DATASAMPLE_H_
#define DATASAMPLE_H_
#pragma	once

#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <time.h>
#include <sys/time.h>
#include <stack>
#include <boost/thread/mutex.hpp>

//using namespace std;
/**
 * @brief The DataSample class Holds data samples obtained
 * from the ExitaXL driver.
 *
 * Contains data obtained from the EvitaXL driver. Also, has mutex support by
 * creating wrapper methods.
 */
class DataSample
{

private:
    /**
     * @brief DataSample Disallow use of implicitly generated member functions.
     * @param src
     */
    DataSample(const DataSample &src);
    /**
     * @brief operator = Disallow use of implicitly generated member functions.
     * @param rhs
     * @return
     */
    DataSample &operator=(const DataSample &rhs);

    double value;//!< value obtained from the driver
    std::string timeStamp;//!< time when value was collected
    boost::mutex locked;//!< lock variables, thread implementation
public:
    /**
     * @brief Explicit default constructor initializes variables.
     */
    DataSample()
    {
        value = 0.0;
        timeStamp = "";
    }

    ~DataSample(); //!< explicit desctructor
    /**
     * @brief lock wrapper for boost::mutex::lock()
     */
    void lock()
    {
        locked.lock();
    }
    /**
     * @brief unlock wrapper for boost::mutex::unlock();
     */
    void unlock()
    {
        locked.unlock();
    }
    /**
     * @brief getTimeStamp getter for timeStamp
     * @return timeStamp
     */
    std::string getTimeStamp()
    {
        return timeStamp;
    }
    /**
     * @brief getValue getter for value
     * @return value
     */
    double getValue()
    {
        return value;
    }

    /**
     * @brief set setter for value and timeStamp
     * @param val new value
     * @param time_stamp new timeStamp
     */
    void set(double val, std::string& time_stamp)
    {
        locked.lock();
        value = val;
        timeStamp = time_stamp;
        locked.unlock();
    }
};




#endif
