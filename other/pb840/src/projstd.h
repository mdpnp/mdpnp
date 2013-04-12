/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/********************************************************************
	projstd.h

	Include file for standard system include files, or project
	specific include files used frequently, but changed infrequently.
********************************************************************/

#pragma once
#ifndef	PROJSTD_H_
#define	PROJSTD_H_

#if defined(_WIN32)

#define WIN32_LEAN_AND_MEAN	// Exclude rarely-used stuff from Windows headers

#ifndef _WIN32_WINNT		// Allow use of features specific to Windows XP or later.                   
#define _WIN32_WINNT	(0x0501)	// Change this to the appropriate value to target other versions of Windows.
#endif						

#include <windows.h>
#include <stdio.h>

#else

#endif

#endif
