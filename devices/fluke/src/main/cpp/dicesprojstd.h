/**
 * @file  projstd.h
 *
 * Include file for standard system include files, or project
 * specific include files used frequently, but changed infrequently.
 */
//=============================================================================
#pragma once
#ifndef  PROJSTD_H_
#define  PROJSTD_H_

#if defined(_WIN32)

/* Windows */

#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN  // Exclude rarely-used stuff from Windows headers
#endif

#ifndef _WIN32_WINNT    // Allow use of features specific to Windows XP or later.
//#define _WIN32_WINNT  (0x0501)  // Change this to the appropriate value to target other versions of Windows.
#endif

#include <windows.h>
#include <stdio.h>

#else

/* Not Windows */

#endif

#endif
