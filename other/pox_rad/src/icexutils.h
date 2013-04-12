/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    icexutils.h
 * @brief   Utility functions.
 *
 * This class is never instatiated.  All methods are declared static.
 */
//=============================================================================
#pragma once
#ifndef  ICEXUTILS_H_
#define  ICEXUTILS_H_

#include <sstream>
#include <cstring>
#include <stdio.h>
#include <time.h>

#if defined(_WIN32)
#include <conio.h>

#else
#include <iostream>
#include <sys/select.h>
#include <termios.h>
#endif

//=============================================================================
class icexutils
{
private:
//  Disallow use of implicitly generated member functions:
  icexutils(const icexutils &src);
  icexutils &operator=(const icexutils &rhs);

  icexutils();
  ~icexutils();

protected:

public:

  enum { maxchars_tid = 10 };

  static const unsigned tid_invalid  = static_cast<unsigned int>(-1);

//=============================================================================
inline static bool valid_tid(unsigned int tid)
{
  return(tid != tid_invalid);
}

//=============================================================================
// Get a transaction id (tid) as unsigned integer.
// This will generate maxtidpersec tids per second.
// tid is unique for approx 17 years, starting 1 JAN 2010.
// This does not guarantee sequential TIDs.
//=============================================================================
inline static unsigned int gettid()
{
  static  time_t  lasttime  = 0;
  static  const  unsigned int  ishiftval  = 3;
  static  const  unsigned int  maxtidpersec  = (1<<(ishiftval));
  static  unsigned int  icounter  = 0;

  time_t  ttid  = static_cast<time_t>(-1);
  unsigned int  tid  = tid_invalid;
  time_t  tnow  = time(NULL);

  if (lasttime != tnow)
  {
    ttid  = tnow;
    lasttime  = tnow;
    icounter  = 0;
  }
  else
  {
    if (icounter < maxtidpersec)
    {
      ttid  = lasttime;
    }
  }

  if (ttid != static_cast<time_t>(-1))
  {
    tid  = static_cast<unsigned int>(ttid);
    tid  = (tid << (ishiftval));
    tid  += icounter++;
  }
  return(tid);
}

//=============================================================================
/**
 * Get a transaction id (TID) as null terminated string.
 *
 * If successful return pointer to stid parameter. Otherwise return NULL.
 *
 * Caller should size the supplied buffer to at least maxchars_tid + 1 bytes.
 * This is specified by function argument imaxsize.
 *
 * #see gettid()
 */
inline static char *gettid(char *stid, unsigned int imaxsize)
{
  if (stid != NULL)
  {
    std::ostringstream  oss;
    unsigned int tid = gettid();
    oss << tid;

    if (oss.str().length() < imaxsize)
    {
      strcpy(stid, oss.str().c_str());
    }
    else
    {
      stid = NULL;
    }
  }
  return(stid);
}

//=============================================================================
inline static void millisleep(int millisecs)
{
#ifdef  _WIN32
  Sleep(millisecs);
#else
  static  const  int  mega  = 1000 * 1000;
  struct timespec  tspec  = { millisecs / 1000, (millisecs % 1000) * mega };
  struct timespec  tspecdummy  = { 0 };
  nanosleep(&tspec, &tspecdummy);
#endif
}

//=============================================================================
/**
 * For the specified month string return the month number. January = 1.
 * Decision is based on the first three characters (only) of smonth.
 */
static  int  month_s2i(const char *smonth)
{
  int  imon  = 0;

  while((smonth != 0) && (strlen(smonth) > 2))
  {
    char  c0  = smonth[0];
    char  c1  = smonth[1];
    char  c2  = smonth[2];

    if ((c0 == 'J') || (c0 == 'j'))
    {
      /// First letter is J
      if ((c1 == 'A') || (c1 == 'a'))  imon  = 1;  // Jan
      if ((c1 == 'U') || (c1 == 'u'))
      {
        if ((c2 == 'N') || (c2 == 'n'))  imon  = 6;  // Jun
        if ((c2 == 'L') || (c2 == 'l'))  imon  = 7;  // Jul
      }
      break;
    }

    if ((c0 == 'F') || (c0 == 'f'))
    {
      /// First letter is F
      imon  = 2;  // Feb
      break;
    }

    if ((c0 == 'M') || (c0 == 'm'))
    {
      /// First letter is M
      if ((c1 == 'A') || (c1 == 'a'))
      {
        if ((c2 == 'R') || (c2 == 'r'))  imon  = 3;  // Mar
        if ((c2 == 'Y') || (c2 == 'y'))  imon  = 5;  // May
      }
      break;
    }

    if ((c0 == 'A') || (c0 == 'a'))
    {
      /// First letter is A
      if ((c1 == 'P') || (c1 == 'p'))  imon  = 4;  // Apr
      if ((c1 == 'U') || (c1 == 'u'))  imon  = 8;  // Aug
      break;
    }

    if ((c0 == 'S') || (c0 == 's'))  imon  = 9;  // Sep
    if ((c0 == 'O') || (c0 == 'o'))  imon  = 10;  // Oct
    if ((c0 == 'N') || (c0 == 'n'))  imon  = 11;  // Nov
    if ((c0 == 'D') || (c0 == 'd'))  imon  = 12;  // Dec
    break;
  }
  return(imon);
}

#ifdef  _WIN32
//=============================================================================
inline static int getkey_nowait()
{
  int key = 0;

  if (_kbhit())
  {
    key = _getch();
    if ((key == 0) || (key == 0xE0))  key = (key << 8) + _getch();
  }
  return(key);
}

//=============================================================================
inline static int kbhit()
{
  int key = 0;

  if (_kbhit())
  {
    key = _getch();
    if ((key == 0) || (key == 0xE0))  key = (key << 8) + _getch();
  }
  return(key);
}

//=============================================================================
inline static int cancontinue()
{
  int  iret  = 1;  // Assume can continue
  if (kbhit() == 27)  iret  = 0;  // But not if Escape key pressed
  return(iret);
}

#else

#define  RETURN_THE_KEY_VALUE
#ifdef  RETURN_THE_KEY_VALUE
//=============================================================================
inline static int getch()
{
  unsigned  char  uchar  = 0;
  int  ival  = read(0, &uchar, sizeof(uchar));
  if (ival < 0)  ival  = 0;
  else  ival  = static_cast<int>(uchar);
  return(ival);
}
#endif

//=============================================================================
inline static int getkey_nowait()
{
  int  ikey  = 0;
  struct  termios  orig_term_attr  = { 0 };
  struct  termios  new_term_attr  = { 0 };

  /* set terminal to raw mode */
  tcgetattr(fileno(stdin), &orig_term_attr);
  memcpy(&new_term_attr, &orig_term_attr, sizeof(struct termios));
//  &new_term_attr  = orig_term_attr;
  new_term_attr.c_lflag  &= ~(ECHO|ICANON);
  new_term_attr.c_cc[VTIME]  = 0;
  new_term_attr.c_cc[VMIN]  = 0;
  tcsetattr(fileno(stdin), TCSANOW, &new_term_attr);

  /* Read a character from the stdin stream without blocking. */
  ikey  = fgetc(stdin);  /* returns EOF (-1) if no character available */

  /* Restore original terminal attributes */
  tcsetattr(fileno(stdin), TCSANOW, &orig_term_attr);

  return((ikey != EOF) ? ikey : 0);
}

//=============================================================================
inline static int kbhit()
{
#if 1
  int  ikey  = getkey_nowait();
  return(ikey);
#else
  int  iret  = 0;  // Assume no chars pending
  struct  timeval  tv  = { 0 };
  fd_set  read_fd  = { 0 };
  static  const  int  stdin_file_descriptor  = 0;

  /* Do not wait at all, not even a microsecond */
  tv.tv_sec  = 0;
  tv.tv_usec = 0;

  /* First, initialize read_fd */
  FD_ZERO(&read_fd);

  /* Make select() ask if input is ready. */
  FD_SET(stdin_file_descriptor, &read_fd);

  /* The first parameter is the number of the
   * largest file descriptor to check + 1. */
  if (select(1, &read_fd, NULL, /*No writes*/NULL, /*No exceptions*/&tv) != -1)
  {
    /* No error */

    /* read_fd now holds a bit map of files that are readable. */
    /* Test the entry for the standard input (file 0). */

    if (FD_ISSET(stdin_file_descriptor, &read_fd))
    {
        /* Character pending on stdin */
      iret  = 1;
    }
  }
  return(iret);
#endif
}

//=============================================================================
inline static int cancontinue() { return(kbhit() ? 0 : 1); }

#endif

};

#endif
