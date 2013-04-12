/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    ice_time_utils.h
 * @brief   Provide basic time functionality.
 */
//=============================================================================
#pragma once
#ifndef	ICE_TIME_UTILS_H_
#define	ICE_TIME_UTILS_H_

#include <string>
#include <boost/date_time/posix_time/posix_time_types.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

class ice_time_utils
{
private:
// Disallow use of implicitly generated member functions:
  ice_time_utils(const ice_time_utils &src);
  ice_time_utils &operator=(const ice_time_utils &rhs);

// To date, all functions are static so disallow default construct/desctructor
  ice_time_utils();
  ~ice_time_utils();

public:
//=============================================================================
/**
 * Return current time as string of format "2009-10-14T16:07:38"
 */
static std::string timenow_utc_string_second()
{
  boost::posix_time::ptime  tnow  =
    boost::posix_time::second_clock::universal_time();
  return(boost::posix_time::to_iso_extended_string(tnow));
}

//=============================================================================
/**
 * Return current time as string of format "2009-10-14T16:07:38.375890"
 */
static std::string timenow_utc_string_microsec()
{
  boost::posix_time::ptime  tnow  =
    boost::posix_time::microsec_clock::universal_time();
  return(boost::posix_time::to_iso_extended_string(tnow));
}

};

#endif
