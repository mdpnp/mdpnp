/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    DataRecord.h
 * @brief   A record from the simulated file.
 */

#ifndef DATARECORD_H
#define DATARECORD_H
#pragma	once

#include <string>
#include <vector>

/**
 * @brief The DataRecord class. Holds one record from the simulated file.
 */
class DataRecord
{
private:
    DataRecord(const DataRecord &src);
    DataRecord &operator=(const DataRecord &rhs);

public:
    DataRecord()    {}
    ~DataRecord()   {}


    //convert to struct
    std::vector<std::string> names; //!< field names @todo -- make private
    std::vector<float> values;//!< field values @todo -- make private

    //make string
    char time[32];//!< timestamp @todo -- make private
};

#endif // DATARECORD_H
