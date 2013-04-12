/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    icexio.h
 * @brief   Handle serial i/o.
 *
 * @see     Serial Program Guide for POSIX Operating Systems, at
 *          http://www.easysw.com/~mike/serial/serial.html
 */
//=============================================================================
#pragma once
#ifndef	ICEXIO_H_
#define	ICEXIO_H_

#include "projstd.h"
#include <string>
#include <iostream>
#include <time.h>
#include <errno.h>
#include "pb840_commandcfg.h"

using namespace std;

extern  class pb840_commandcfg  _cfg; //  Global configuration

class icexio;
class icexserial;

//=============================================================================
class icexio
{
private:
//	Disallow use of implicitly generated member functions:
	icexio(const icexio &src);
	icexio &operator=(const icexio &rhs);

protected:
	int	_isopen;
	string	_sstatusmsg;

public:

//=============================================================================
	icexio()
		:	_isopen(0)
	{
	}

//=============================================================================
	virtual ~icexio()
	{
		if (isopen())	close();
	}

//=============================================================================
	enum
	{
		icexio_ok	= 0,
		icexio_fail,
		icexio_bufferoverrun,
		icexio_stopsignal,
		icexio_timeout,

	}	icexiostatus;

//=============================================================================
	static inline const int status_ok(const int istat)
	{
		return(istat == icexio_ok ? 1 : 0);
	}

//=============================================================================
	virtual int isopen()	const	{ return(_isopen); }

//=============================================================================
	virtual inline int close()
	{
		int	istat	= icexio_fail;
		if (isopen())
		{
			_isopen	= 0;
			istat	= icexio_ok;
		}
		return(istat);
	}

//=============================================================================
	virtual inline int open()
	{
		int	istat = icexio_fail;	//	Assume fail
		if (isopen())	return(istat);
		istat = icexio_ok;
		return(istat);
	}

	inline string getstatusmsg()	const	{	return(_sstatusmsg);	}

};

//=============================================================================
//=============================================================================
#ifdef	_WIN32

//	Windows world

#include <conio.h>
#include <stdio.h>

#else

//	Not Windows world

#include <stdio.h>	/* Standard input/output definitions */
#include <string.h>	/* String function definitions */
#include <unistd.h>	/* UNIX standard function definitions */
#include <fcntl.h>	/* File control definitions */
#include <termios.h>	/* POSIX terminal control definitions */
#include <sys/select.h>

#ifndef	INVALID_HANDLE_VALUE
#define	INVALID_HANDLE_VALUE	(-1)
#endif
#endif

//=============================================================================
class icexserial : public icexio
{
private:
//	Disallow use of implicitly generated member functions:
	icexserial(const icexserial &src);
	icexserial &operator=(const icexserial &rhs);

protected:
	char	*_psdefaultportname;

	size_t	_ibytesxferredcount;
	int	_isysererrno;

#ifdef	_WIN32
	typedef	DWORD		icexDWORD;
	typedef	HANDLE	icexHANDLE;
#else
	struct	termios	_saveoptions;
	typedef	int	icexDWORD;
	typedef	int	icexHANDLE;
#endif

	icexHANDLE	_hfile;

public:

//=============================================================================
	icexserial()
		:	icexio(),
			_hfile(INVALID_HANDLE_VALUE),
			_ibytesxferredcount(0),
			_isysererrno(0)
	{
#ifdef	_WIN32
		_psdefaultportname	= "COM1";
#else
		_psdefaultportname	= "/dev/ttyS0";
		memset(&_saveoptions, 0, sizeof(_saveoptions));
#endif
	}

//=============================================================================
	~icexserial()
	{
		close();
	}

//=============================================================================
	inline	void	clearcommerror()
	{
#ifdef	_WIN32
		DWORD	errors	= 0; 
		COMSTAT	stat	= { 0 };
		ClearCommError(_hfile, &errors, &stat);
#endif
	}

//=============================================================================
	inline	const	char	*defaultportname()	const
		{ return(_psdefaultportname); }
	inline	const	char	*portname()	const
		{ return((_cfg.getserialportname()).c_str()); }

//=============================================================================
	inline	const	size_t	bytesxferredcount()	const
		{ return(_ibytesxferredcount); }

//=============================================================================
	inline int isopen()	const	{ return(_hfile != INVALID_HANDLE_VALUE); }

//=============================================================================
	inline icexHANDLE getfilehandle()	const	{ return(_hfile); }

//=============================================================================
#ifndef	_WIN32

	//	Not-Windows baudrate constants

	static	inline	speed_t	baudrate(const int ibaud)
	{
		speed_t	ispeed	= B0;
		switch(ibaud)
		{
			case	0:	ispeed	= B0;
			break;
			case	50:	ispeed	= B50;
			break;
			case	75:	ispeed	= B75;
			break;
			case	110:	ispeed	= B110;
			break;
			case	134:	ispeed	= B134;
			break;
			case	150:	ispeed	= B150;
			break;
			case	200:	ispeed	= B200;
			break;
			case	300:	ispeed	= B300;
			break;
			case	600:	ispeed	= B600;
			break;
			case	1200:	ispeed	= B1200;
			break;
			case	1800:	ispeed	= B1800;
			break;
			case	2400:	ispeed	= B2400;
			break;
			case	4800:	ispeed	= B4800;
			break;
			case	9600:	ispeed	= B9600;
			break;
			case	19200:	ispeed	= B19200;
			break;
			case	38400:	ispeed	= B38400;
			break;
			case	57600:	ispeed	= B57600;
			break;
			case	115200:	ispeed	= B115200;
			break;
			case	230400:	ispeed	= B230400;
			break;
			default:	ispeed	= B0;
			break;
		}
		return(ispeed);
	}
#endif

//=============================================================================
	inline	int	close()
	{
		int	istat	= icexio_fail;
		if (isopen())
		{
#ifdef	_WIN32
			CloseHandle(_hfile);
#else
			//	Set options to original
			tcsetattr(_hfile, TCSANOW, &_saveoptions);
			::close(_hfile);
#endif
			_hfile = INVALID_HANDLE_VALUE;
			istat	= icexio_ok;
		}
		return(istat);
	}

//=============================================================================
	inline int open()
	{
		int	istat = icexio_fail;	//	Assume fail
		if (isopen())
		{
			return(istat);
			_sstatusmsg	=	"icexserial::open error. Already open.";
		}

#ifdef	_WIN32

		//	Windows serial i/o

		_hfile = CreateFile(
			(_cfg.getserialportname()).c_str(),
			GENERIC_READ | GENERIC_WRITE,
			0,			//	exclusive access
			NULL,		//	no security
			OPEN_EXISTING,
			0,			//	no overlapped i/o
			NULL);	//	NULL template

		if (_hfile == INVALID_HANDLE_VALUE)
		{
			LPVOID	pmsgbuf	= NULL;
			char	serrbuf[256]	= "";
			_isysererrno	= GetLastError();
			sprintf(
				serrbuf,
				"icexserial::open(%s) CreateFile error %d. ",
				(_cfg.getserialportname()).c_str(),
				_isysererrno);

			FormatMessage(
				FORMAT_MESSAGE_ALLOCATE_BUFFER | 
				FORMAT_MESSAGE_FROM_SYSTEM |
				FORMAT_MESSAGE_IGNORE_INSERTS,
				NULL,
				_isysererrno,
				MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
				(char *)&pmsgbuf,
				0, NULL);

			_sstatusmsg	=	serrbuf;
			_sstatusmsg	+=	(char *)pmsgbuf;
			LocalFree(pmsgbuf);
			return(istat);
		}

		DCB	dcb;
		COMMTIMEOUTS	tmo;
		memset(&dcb, 0, sizeof(dcb));
		memset(&tmo, 0, sizeof(tmo));

		// Set up comm 9600 baud, 8 bit, 1 stop, no parity
		GetCommState(_hfile, &dcb);
		dcb.BaudRate	= _cfg.getserialbaudrate();
		dcb.ByteSize	= _cfg.getserialdatabits();
		dcb.StopBits	= _cfg.getserialstopbits();

		if ((_cfg.getserialparity()).compare("none") == 0)
		{
			dcb.Parity	= NOPARITY;
		}
		else if ((_cfg.getserialparity()).compare("even") == 0)
		{
			dcb.Parity	= EVENPARITY;
		}
		else if ((_cfg.getserialparity()).compare("odd") == 0)
		{
			dcb.Parity	= ODDPARITY;
		}
		else if ((_cfg.getserialparity()).compare("space") == 0)
		{
			dcb.Parity	= SPACEPARITY;
		}
		else if ((_cfg.getserialparity()).compare("mark") == 0)
		{
			dcb.Parity	= MARKPARITY;
		}

		//	Setting fAbortOnError to 0 fixes a weird problem: After 30 
		//	writes to the serial port, the 31st write fails with this error
		//	message:
		//
		//		"The I/O operation has been aborted because of either a thread
		//		exit or an application request." (error code 995)
		//
		//	I found a fix here:
		//	http://zachsaw.blogspot.com/2010/07/net-serialport-woes.html
		//
		dcb.fAbortOnError	= 0;

/*
// set XON/XOFF
dcb.fOutX	= FALSE; // XON/XOFF off for transmit
dcb.fInX	= FALSE; // XON/XOFF off for receive

// set RTSCTS
dcb.fOutxCtsFlow	= TRUE; // turn on CTS flow control
dcb.fRtsControl	= RTS_CONTROL_HANDSHAKE; //

// set DSRDTR
dcb.fOutxDsrFlow	= FALSE; // turn on DSR flow control
dcb.fDtrControl	= DTR_CONTROL_ENABLE; //
// dcb.fDtrControl	= DTR_CONTROL_DISABLE; //
// dcb.fDtrControl	= DTR_CONTROL_HANDSHAKE; //
*/

		// set RTSCTS
		dcb.fOutxCtsFlow	= TRUE; // turn on CTS flow control
		dcb.fRtsControl	= RTS_CONTROL_HANDSHAKE; //

		if (!SetCommState(_hfile, &dcb))
		{
			_sstatusmsg	=	"icexserial::open failed on SetCommState()";
			return(istat);
		}

		// Set comm timeout values
		tmo.ReadIntervalTimeout	= 50;		//	MAXDWORD;
		tmo.ReadTotalTimeoutMultiplier	= 10;
		tmo.ReadTotalTimeoutConstant	= 300;

		tmo.WriteTotalTimeoutMultiplier	= 10;
		tmo.WriteTotalTimeoutConstant	= 400;
		if (!SetCommTimeouts(_hfile, &tmo))
		{
			_sstatusmsg	=	"icexserial::open failed on SetCommTimeouts()";
			return(istat);
		}
		istat = icexio_ok;

#else

		//	Not-Windows serial i/o

		struct termios options;
		unsigned	int	ibaud	= baudrate(_cfg.getserialbaudrate());

		_hfile	= ::open(
			(_cfg.getserialportname()).c_str(),
			O_RDWR | O_NOCTTY | O_NDELAY);
		if (_hfile < 0)
		{
			char	sbuf[128]	= "";
			_hfile = INVALID_HANDLE_VALUE;
			_isysererrno	= errno;
			sprintf(
				sbuf,
				"icexserial::open(%s) ERROR %d: ",
				(_cfg.getserialportname()).c_str(),
				_isysererrno);
			_sstatusmsg	= sbuf;
			_sstatusmsg	+= strerror(errno);
			return(istat);
		}

//		fcntl(_hfile, F_SETFL, 0);		//	Block read/write
		fcntl(_hfile, F_SETFL, FNDELAY);	//	Return immediately upon read/write

		tcgetattr(_hfile, &options);
		_saveoptions	= options;

		cfsetispeed(&options, ibaud);
		cfsetospeed(&options, ibaud);

		options.c_cflag |= (CLOCAL | CREAD);	//	enable

		//	Parity

		int	iparitybitcheckandstrip	= 1;
		if ((_cfg.getserialparity()).compare("none") == 0)
		{
			iparitybitcheckandstrip	= 0;
			options.c_cflag &= ~PARENB;
		}
		else if ((_cfg.getserialparity()).compare("even") == 0)
		{
			options.c_cflag &= ~PARENB;
			options.c_cflag &= ~PARODD;
		}
		else if ((_cfg.getserialparity()).compare("odd") == 0)
		{
			options.c_cflag |= PARENB;
			options.c_cflag |= PARODD;
		}
		else if ((_cfg.getserialparity()).compare("space") == 0)
		{
			options.c_cflag &= ~PARENB;
		}

		if (iparitybitcheckandstrip != 0)
		{
			//	Eable checking and stripping of the parity bit
			options.c_iflag |= (INPCK | ISTRIP);
		}

		//	Stop bits

		if (_cfg.getserialstopbits()	==	2)
		{
			options.c_cflag |= CSTOPB;
		}
		else
		{
			options.c_cflag &= ~CSTOPB;
		}

		//	Byte size

		options.c_cflag &= ~CSIZE;
		if (_cfg.getserialdatabits() == 8)	options.c_cflag |= CS8;
		else if (_cfg.getserialdatabits() == 7)	options.c_cflag |= CS7;
		else if (_cfg.getserialdatabits() == 6)	options.c_cflag |= CS6;
		else if (_cfg.getserialdatabits() == 5)	options.c_cflag |= CS5;

		//	Enable hardware flow control
		options.c_cflag |= CRTSCTS;	//	Also called CNEW_RTSCTS

		//	Disable hardware flow control
//		options.c_cflag &= ~CRTSCTS;	//	Also called CNEW_RTSCTS

		//	Disable software flow control
		options.c_iflag &= ~(IXON | IXOFF | IXANY);

/*
From  http://unixwiz.net/techtips/termios-vmin-vtime.html

VMIN and VTIME defined

VMIN is a character count ranging from 0 to 255 characters, and VTIME is
time measured in 0.1 second intervals, (0 to 25.5 seconds). The value of
"zero" is special to both of these parameters, and this suggests four
combinations discussed below. In every case, the question is when a read()
system call is satisfied. Here is the read() prototype call:

int n = read(fd, buffer, nbytes);

The tty driver maintains an input queue of bytes already
read from the serial line and not passed to the user, so not every read()
call waits for actual I/O - the read may very well be satisfied directly
from the input queue.

VMIN = 0 and VTIME = 0
This is a completely non-blocking read - the call is satisfied
immediately directly from the driver's input queue. If data are available,
they transfer to the caller's buffer up to nbytes and return.
Otherwise zero is immediately returned to indicate "no data". Note that
this is "polling" of the serial port, and it's almost always a bad idea.
If done repeatedly, it can consume enormous amounts of processor time and
is highly inefficient. Don't use this mode unless you really, really know
what you're doing. 

VMIN = 0 and VTIME > 0
This is a pure timed read. If data are available in the input queue, it's
transferred to the caller's buffer up to a maximum of nbytes, and returned
immediately to the caller. Otherwise the driver blocks until data arrive,
or when VTIME tenths expire from the start of the call. If the timer
expires without data, zero is returned. A single byte is sufficient to
satisfy this read call, but if more is available in the input queue, it's
returned to the caller. Note that this is an overall timer, not an
intercharacter one. 

VMIN > 0 and VTIME > 0
A read() is satisfied when either VMIN characters have been transferred to
the caller's buffer, or when VTIME tenths expire between characters. Since
this timer is not started until the first character arrives, this call can
block indefinitely if the serial line is idle. This is the most common
mode of operation, and we consider VTIME to be an intercharacter timeout,
not an overall one. This call should never return zero bytes read. 

VMIN > 0 and VTIME = 0
This is a counted read that is satisfied only when at least VMIN
characters have been transferred to the caller's buffer - there is no
timing component involved. This read can be satisfied from the driver's
input queue (where the call could return immediately), or by waiting for
new data to arrive. In this respect the call could block indefinitely. 
Consider the behavior undefined when nbytes is less than VMIN.
*/

		options.c_cc[VTIME]	= 1;
		options.c_cc[VMIN]	= 1;

		//	Flush the line
		tcflush(_hfile, TCIFLUSH);

		//	Set all options
		tcsetattr(_hfile, TCSANOW, &options);

		istat = icexio_ok;
#endif
		return(istat);
	}

//=============================================================================
	inline void flush()
	{
#ifdef	_WIN32
		PurgeComm(
			_hfile,
			PURGE_RXCLEAR | PURGE_TXCLEAR | PURGE_RXABORT | PURGE_TXABORT);
#else
		tcflush(_hfile, TCIFLUSH);
#endif
	}

//=============================================================================
	inline int writerecord(const char *sbuf, icexDWORD ibytestoxfer)
	{
#ifdef	_WIN32
		//	Windows write

		int	istat	= icexio_ok;
		icexDWORD	ibytesxferred	= 0;

		_ibytesxferredcount	= 0;

		while((_ibytesxferredcount < ibytestoxfer) && (icexio::status_ok(istat)))
		{
			if (WriteFile(_hfile, &sbuf[_ibytesxferredcount],
					ibytestoxfer - _ibytesxferredcount, &ibytesxferred, NULL) != 0)
			{
				_ibytesxferredcount	+= ibytesxferred;
			}
			else
			{
				_isysererrno	=	::GetLastError();
				if (_isysererrno != ERROR_IO_PENDING)
				{
					LPVOID	pmsgbuf	= NULL;
					char	serrbuf[128]	= "";

					FormatMessage(
						FORMAT_MESSAGE_ALLOCATE_BUFFER | 
						FORMAT_MESSAGE_FROM_SYSTEM |
						FORMAT_MESSAGE_IGNORE_INSERTS,
						NULL,
						_isysererrno,
						MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
						(char *)&pmsgbuf,
						0, NULL);

					sprintf(serrbuf, "WriteFile error %d. ", _isysererrno);
					_sstatusmsg	=	serrbuf;
					_sstatusmsg	+=	(char *)pmsgbuf;
					LocalFree(pmsgbuf);
					istat	= icexio_fail;
				}
			}
		}
#else
		//	Not-Windows write

		int	istat	= icexio_ok;
		int	ibytesxferred	= 0;

		_ibytesxferredcount	= 0;

		while((_ibytesxferredcount < ibytestoxfer) && (icexio::status_ok(istat)))
		{
			ibytesxferred	= ::write(_hfile, sbuf, ibytestoxfer);
			if (ibytesxferred < 0)
			{
				istat	= icexio_fail;
				_isysererrno	= errno;
				_sstatusmsg	= "::write error: ";
				_sstatusmsg	+=	strerror(_isysererrno);
cout	<< endl << __FILE__ << ", line no. " << __LINE__
		<< " ::write error " << _isysererrno
		<< ". " << strerror(_isysererrno) << endl;
exit(__LINE__);
			}
			else
			{
				_ibytesxferredcount	+= ibytesxferred;
			}
		}
#endif
		return(istat);
	}

//=============================================================================
	inline int readrecord(char *sbuf, size_t ibuffersize, int terminator)
	{
		int	istat	= icexio_ok;
		int	itimeoutct	= 0;
		static	const	int	imaxtimeouts	= 1;
		icexDWORD	ibytesread	= 0;

		_ibytesxferredcount	= 0;

		while((_ibytesxferredcount < ibuffersize) && status_ok(istat))
		{
			istat	= readchar(&sbuf[_ibytesxferredcount], &ibytesread);

			if (!status_ok(istat))	break;

			_ibytesxferredcount	+= ibytesread;

			if (_ibytesxferredcount > 1)
			{
				if (sbuf[_ibytesxferredcount - 1] == terminator)
				{
					// The input char is terminator. Done for now.
					break;
				}
			}
			if (ibytesread < 1)
			{
				if (++itimeoutct > imaxtimeouts)
				{
					_sstatusmsg	= "icexserial::readrecord error: Timeout";
					istat	= icexio_timeout;
				}
			}
		}
		if (_ibytesxferredcount) sbuf[_ibytesxferredcount]	= '\0';
		return(istat);
	}

//=============================================================================
	inline int readchar(
		char *psbuf,
		icexDWORD *pbytesxferredcount,
		size_t	ibytestoxfer	= 1)
	{
		int	istat	= icexio_ok;

		*pbytesxferredcount	= 0;
#ifdef	_WIN32
		//	Windows read
		if (ReadFile(_hfile, psbuf, 1, pbytesxferredcount, NULL) < 1)
		{
			_isysererrno	=	::GetLastError();
			if (_isysererrno != ERROR_IO_PENDING)
			{
				char	serrbuf[128]	= "";
				LPVOID	pmsgbuf	= NULL;

				FormatMessage(
					FORMAT_MESSAGE_ALLOCATE_BUFFER | 
					FORMAT_MESSAGE_FROM_SYSTEM |
					FORMAT_MESSAGE_IGNORE_INSERTS,
					NULL,
					_isysererrno,
					MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
					(char *)&pmsgbuf,
					0, NULL);

				sprintf(serrbuf, "ReadFile error %d. ", _isysererrno);
				_sstatusmsg	=	serrbuf;
				_sstatusmsg	+=	(char *)pmsgbuf;
				LocalFree(pmsgbuf);
				istat	= icexio_fail;
			}
		}
#else
		//	Not-Windows read
		int	ibytesread	= ::read(_hfile, psbuf, ibytestoxfer);
		//cout << dec << ibytesread << endl;
		if (ibytesread < 1)
		{
			if (errno != EAGAIN)
			{
				_isysererrno	= errno;
				_sstatusmsg	= "::read error: ";
				_sstatusmsg	+=	strerror(_isysererrno);
				istat	= icexio_fail;
			}
		}
		else
		{
			*pbytesxferredcount	= ibytesread;
		}
#endif
		return(istat);
	}

};

#endif
