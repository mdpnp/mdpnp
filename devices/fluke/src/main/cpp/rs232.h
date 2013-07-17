/*
***************************************************************************
*
* Author: Teunis van Beelen
*
* Copyright (C) 2005, 2006, 2007, 2008, 2009 Teunis van Beelen
*
* teuniz@gmail.com
*
***************************************************************************
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation version 2 of the License.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
***************************************************************************
*
* This version of GPL is at
* http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
*
***************************************************************************
*/
#ifndef rs232_INCLUDED
#define rs232_INCLUDED

#ifdef __cplusplus
extern "C" {
#endif

int sio_GetMaxComports();
int sio_OpenComport(const char *portname, const int baudrate);
int sio_PollComport(const int portnumber, char *buf, int size);
int sio_SendBuf(const int portnumber, const char *buf, const int size);
int sio_SendByte(const int portnumber, const char byte);
int sio_GetPortHandle(const int portnumber);
int sio_FlushComport(const int portnumber);
void sio_CloseComport(const int portnumber);
void sio_cprintf(const int portnumber, char *text);
int sio_IsCTSEnabled(const int portnumber);
int sio_IsOpen(const int portnumber);
void sio_UnitTestGetComPortNumber(); // must define TEST_GETCOMPORTNUMBER macro
#ifdef __cplusplus
} /* extern "C" */
#endif

#endif
