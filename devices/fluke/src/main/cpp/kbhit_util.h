/**
 * @file    kbhit_util.h
 * @author  WMS
 * @brief   Return the ascii value of the just-pressed key, or 0 if none.
 *
 * This class is never instatiated. All methods are declared static.
 */
//=============================================================================
#pragma once
#ifndef  KBHIT_UTIL_H_
#define  KBHIT_UTIL_H_

#include <sstream>
#include <cstring>
#include <stdio.h>
#include <cstdio>

#if defined(_WIN32)
#include <conio.h>
#else
#include <iostream>
#include <sys/select.h>
#include <termios.h>
#endif

//=============================================================================
class kbhit_util
{
private:
//  Disallow use of implicitly generated member functions:
  kbhit_util(const kbhit_util &src);
  kbhit_util &operator=(const kbhit_util &rhs);
  kbhit_util();
  ~kbhit_util();

public:

//=============================================================================
inline static int kbhit()
{
  int  ikey  = getkey_nowait();
  return(ikey);
}

#ifdef  _WIN32
//=============================================================================
inline static int getkey_nowait()
{
  int ikey = 0;

  if (_kbhit())
  {
    ikey = _getch();
    if ((ikey == 0) || (ikey == 0xE0)) ikey = (ikey << 8) + _getch();
  }
  return(ikey);
}

#else
//=============================================================================
inline static int getkey_nowait()
{
  int  ikey  = 0;
  struct  termios  orig_term_attr  = { 0 };
  struct  termios  new_term_attr  = { 0 };

  /* set terminal to raw mode */
  tcgetattr(fileno(stdin), &orig_term_attr);
  memcpy(&new_term_attr, &orig_term_attr, sizeof(new_term_attr));
  new_term_attr.c_lflag  &= ~(ICANON);
  new_term_attr.c_lflag |= ECHO;
  new_term_attr.c_cc[VTIME]  = 0;
  new_term_attr.c_cc[VMIN]  = 0;
  tcsetattr(fileno(stdin), TCSANOW, &new_term_attr);

  /* Read a character from the stdin stream without blocking. */
  ikey  = fgetc(stdin);  /* returns EOF (-1) if no character available */

  /* Restore original terminal attributes */
  tcsetattr(fileno(stdin), TCSANOW, &orig_term_attr);

  return((ikey != EOF) ? ikey : 0);
}
#endif

};

#endif
