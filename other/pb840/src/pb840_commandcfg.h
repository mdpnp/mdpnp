/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
 * @file    pb840_commandcfg.h
 * @brief   Interface to cfg file for ICE device driver for Puritan Bennett 840.
 */
//=============================================================================
#pragma once
#ifndef	PB840_COMMANDCFG_H_
#define	PB840_COMMANDCFG_H_

#ifdef	_WIN32
#define WIN32_LEAN_AND_MEAN	//	Exclude rarely-used stuff from Win headers
#include	<windows.h>
#else
#include <termios.h>
#endif

#include <stdio.h>
#include <stdlib.h>
#include <string>

using namespace std;

//=============================================================================
class pb840_commandcfg
{
private:
//	Disallow use of implicitly generated member functions:
	pb840_commandcfg(const pb840_commandcfg &src);
	pb840_commandcfg &operator=(const pb840_commandcfg &rhs);

private:
	string	_serialportname;
	unsigned	int _serialbaudrate;
	unsigned	int _serialdatabits;
	unsigned	int _serialstopbits; //	 0 = 1 stop bit, 1 = 1.5, 2 = 2
	string	_serialparity;
	unsigned	int _serialhandshaking;
	unsigned	int _secondstorun;
	unsigned	int _pbcommand;

	/*
	*	Trim leading and ending spaces from the string str
	*/
	void trim(string& str)
	{
		string::size_type pos = str.find_last_not_of(' ');
		if (pos != string::npos)
		{
			str.erase(pos + 1);
			pos = str.find_first_not_of(' ');
			if (pos != string::npos) str.erase(0, pos);
		}
		else
		{
			str.erase(str.begin(), str.end());
		}
	}

	/*
	*	Determine if the string provided is the beginning of an XML comment,
	*	excluding open and closing brackets(i.e. s takes the form "!-- ...")
	*	If so, and s does not end with the substring "--", call the method
	*	readcomment()
	*/
	bool iscomment(FILE *xml, string &s)
	{
		bool	bret	= false;	//	Assume not

		if (s[0] == '!' && s[1] == '-' && s[2] == '-')
		{
			if (s[s.length()-2] != '-' || s[s.length()-1] != '-')
				readcomment(xml);
			bret	= true;
		}
		return(bret);
	}

	/*
	*	Precondition: xml is a valid XML file and the current position of
	*	the cursor is in an XML comment within the file.
	*
	*	Postcondition: The current position in xml moves to the end of the
	*	comment (after '>' character)
	*/
	void readcomment(FILE *xml)
	{
		int	cc	= '~';

		//	Comment ends with "-->"
		while (cc != '-')
		{
			cc	= fgetc(xml);
			if (cc == EOF)	return;
		}

		if ((cc	= fgetc(xml)) == EOF)	return;

		if (cc == '-')
		{
			if ((cc	= fgetc(xml)) == EOF)	return;
			switch(cc)
			{
				case '>':
					return;
				default:
					readcomment(xml); //	this is not the end of the comment, keep reading
			}
		}
		else
		{
			readcomment(xml); //	this is not the end of the comment, keep reading
		}
	}

	/*
	*	Precondition: xml is a valid XML file and the current position of
	*	the cursor is immediately after an openning bracket ('<')
	*
	*	If the cursur is currently within an XML tag, return the contents of
	*	that tag are returned. Otherwise, (cursor is within a comment)
	*	return the contents of the next XML tag in the file
	*/
	string readtag(FILE *xml)
	{
		string	result;
		int	cc = '~';

		while (cc != '>')
		{
			if ((cc	= fgetc(xml)) == EOF)	return(result);
			if (cc != '>')	result += cc;
		}

		if (iscomment(xml, result))
		{
			//	The result is actually a comment. Read the next tag
			while (cc != '<')
			{
				cc	= fgetc(xml);
				if (cc == EOF)	return ("");
			}
			return readtag(xml);
		}
		return result;
	}

	/*
	*	Precondition: xml is a valid XML file and the current position of the
	*	cursor is immediately after the closing bracket ('>') of the opening 
	*	tag for a simple XML element which contains only a single basic type
	*
	*	Postcondition: The cursor in xml is moved to the postition immediately
	*	after the closing bracket ('>') to the next XML tag and currenttag is
	*	filled with the contents of that tag
	*
	*	Return a string containing the data read
	*/
	string readdata(FILE *xml, string& currenttag)
	{
		string data;
		int	cc	= '~';
		int	ccx	= '~';

		while ((cc != '<') && (cc != EOF) && (ccx != EOF))
		{
			if ((cc	= fgetc(xml)) == EOF)	break;

			if (cc != '<')
			{
				//	keep adding chars to data until we find comment or tag
				data += cc;
			}
			else
			{
				//	 a comment or tag has been found
				ccx	= '~';
				currenttag.erase();
				while (ccx != '>')
				{
					//	fill currenttag with the contents of the comment or tag
					if ((ccx	= fgetc(xml)) == EOF)	break;
					if (ccx != '>')	currenttag += ccx;
				}

				//	if currenttag is a comment, change cc to a different
				//	character to continue to read data
				if (iscomment(xml, currenttag))	cc = '~';
			}
		}
		trim(data);	//	eliminate any leading or trailing white space characters in the data
		return data;
	}

	/*
	*	Precondition: xml is a valid XML file
	*
	*	Postcondition: The cursor in xml moves to the postition immediately
	*	after the next opening bracket ('<')
	*/
	void movetonexttag(FILE *xml)
	{
		int	cc	= '\0';
		while ((cc != '<') && (cc != EOF))
		{
			cc	= fgetc(xml);
		}
	}

public: 
	enum
	{
		xmlcfgstat_ok = 0,
		xmlcfgstat_fail,
		xmlcfgstat_errbadschema,
		xmlcfgstat_unknownelement,
		xmlcfgstat_closetag,
	};

	static int xmlcfgstatok(int istat)
		{	return(istat == xmlcfgstat_ok);	}

	pb840_commandcfg()
	{
#ifdef	_WIN32
		_serialportname	= "COM1";
#else
		_serialportname	= "/dev/ttyS0";
#endif

#ifdef	_WIN32
		_serialbaudrate	= 9600;
		_serialdatabits	= 8;
		_serialstopbits	= 0;		//	zero value specifies 1 stop bit
		_serialparity	= "none";
		_serialhandshaking	=  0;
#else
		_serialbaudrate	= 9600;
		_serialdatabits	= 8;
		_serialstopbits	= 0;
		_serialparity	= "none";
		_serialhandshaking	=  0;
#endif
		_pbcommand	=  2;
		_secondstorun	=  60;
	}

	~pb840_commandcfg()
	{

	}

	inline string getserialportname()	const
		{	return(_serialportname);	}
	inline unsigned int getserialbaudrate()	const
		{	return(_serialbaudrate);	}
	inline unsigned int getserialdatabits()	const
		{	return(_serialdatabits);	}
	inline unsigned int getserialstopbits()	const
		{	return(_serialstopbits);	} 
	inline string getserialparity()	const
		{	return(_serialparity);	}
	inline unsigned int getserialhandshaking()	const
		{	return(_serialhandshaking);	}
	inline unsigned int getsecondstorun()	const
		{	return(_secondstorun);	}
	inline unsigned int getpbcommand()	const
		{	return(_pbcommand);	}

	///////////////////////////////////////////////////////////////////////
	int loadvalue(FILE *xml, string &currenttag)
	{
		int	istat	= xmlcfgstat_ok;
		string	data;
		string	endtag;

		while(1)
		{
			if (currenttag.compare("serialportname") == 0)
			{
				//	serialportname
				_serialportname = readdata(xml, endtag);

				if (endtag.compare("/serialportname") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("serialbaudrate") == 0)
			{
				//	serialbaudrate
				data = readdata(xml, endtag);
				_serialbaudrate = atoi(data.c_str());

				if (endtag.compare("/serialbaudrate") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("serialdatabits") == 0)
			{
				//	serialdatabits
				data = readdata(xml, endtag);
				_serialdatabits = atoi(data.c_str());

				if (endtag.compare("/serialdatabits") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("serialstopbits") == 0)
			{
				//	serialstopbits
				data = readdata(xml, endtag);
				_serialstopbits = atoi(data.c_str());

				if (endtag.compare("/serialstopbits") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("serialparity") == 0)
			{
				//	serialparity
				_serialparity = readdata(xml, endtag);

				if (endtag.compare("/serialparity") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("serialhandshaking") == 0)
			{
				//	serialhandshaking
				data = readdata(xml, endtag);
				_serialhandshaking = atoi(data.c_str());

				if (endtag.compare("/serialhandshaking") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("secondstorun") == 0)
			{
				//	secondstorun
				data = readdata(xml, endtag);
				_secondstorun = atoi(data.c_str());

				if (endtag.compare("/secondstorun") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("pbcommand") == 0)
			{
				//	pbcommand
				data = readdata(xml, endtag);
				_pbcommand = atoi(data.c_str());

				if (endtag.compare("/pbcommand") != 0)
					istat	= xmlcfgstat_errbadschema;
				break;
			}
			else if (currenttag.compare("/pb840_commandcfg") == 0)
			{
				//	/pb840_commandcfg
				//	That's all folks!
				istat	= xmlcfgstat_closetag;
				break;
			}
			istat	= xmlcfgstat_unknownelement;
			break;
		}
		return(istat);
	}

	/*
	*	Precondition:  The file spcecified by file descriptor xml is open
	*	for read. The file is a valid XML file which conforms to the XML
	*	schema pb840_commandcfg.xsd
	*
	*	Postcondition: the member variables are populated with the data from
	*	the XML file.
	*
	*	Return 0 if this loads the data successfully.
	*	Return non-zero value otherwise.
	*/
	int load(FILE	*xml)
	{
		int	istat	= xmlcfgstat_ok;
		string data, currenttag, rootelement;

		//	Ensure the file is open for business
		if (xml == NULL)	return xmlcfgstat_fail;

		//	read the XML declaration
		movetonexttag(xml);
		currenttag = readtag(xml);

		//	read the root element tag
		movetonexttag(xml);
		currenttag = readtag(xml);

		//	Is the root element correct?
		int	ix	= currenttag.find_first_of(' ');
		rootelement = currenttag.substr(0, ix);
		if (rootelement.compare("pb840_commandcfg") != 0)
			return xmlcfgstat_errbadschema;

		//	Get the config values
		while (xmlcfgstatok(istat))
		{
			movetonexttag(xml);
			currenttag	= readtag(xml);
			istat	= loadvalue(xml, currenttag);
		}
		if (istat == xmlcfgstat_closetag)	istat	= xmlcfgstat_ok;
		return(istat);
	}

};

#endif
