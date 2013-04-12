/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/**
* @file    doxygen_mainpage.h
* @mainpage




@section  _svn_sec_ Getting the code from SVN:

EvitaXL source code is stored in SVN on alopex. Check it out from the command line using a command like this (replace USER with your username):

svn co svn+ssh://USER@alopex.mgh.harvard.edu/home/svn/EvitaXL EvitaXL --username USER

@section _info_sec_ What does each file contain?

The project contains several files, each of which is listed and described below.

Medibus_Descr.pdf - Contains the definition for the Drager MEDIBUS protocol for RS232 communication, revision level 4.03.

Medibus_RS232.pdf - Contains the definition for the Drager MEDIBUS protocol for RS232 communication, revision level 6.00.

Medibus for Draeger ICU Devices.pdf - Contains the MEDIBUS specification, i.e. the available data and commands for several Drager ICU devices. These devices are the Evita, Evita 2, Evita 4, Evita 2 dura, EvitaXL, Savina, Graphic Screen, and Capnostat.

EvitaXL.h - Contains the definition of the EvitaXL class, which is intended to communicate with the EvitaXL ventilator through a serial port connection and log the data it collects to a file called evitaXL_log.

DataSample.h - Contains the definition and implementation of the DataSample class, which the an EvitaXL object uses to store data values.  A DataSample instance stores the value of a particular data sample along with the time (local) it was received from the ventilator.  The file also contains documentation, in the form of comments, explaining the purpose and functionality of each attribute and method of the EvitaXL class.

EvitaXL.cpp - Contains an implementation of the EvitaXL class defined in EvitaXL.h, along with a main method which creates an EvitaXL object and allows it to continuously communicate with the ventilator and log the data it collects.

EvitaFail.cpp - Contains an alternate implementation of the EvitaXL class defined in EvitaXL.h which is intended to crash the ventilator. A main method similar to the one found in EvitaXL.cpp creates an EvitaXL object, and allows it to repeatedly crash the ventilator.

rs232.h - Part of the freeware used by the two implementations of the EvitaXL class listed above to communicate with the ventilator.  The source code was written by Teunis van Beelen and is available at http://www.teuniz.net/RS-232/.  The code was tested with GCC on linux and MinGW on Windows XP/2000.  The software is distributed under the GNU General Public License (GPL), which is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt. The file contains several method definitions intended for use in serial communication.

rs232.c - Part of the freeware used by the two implementations of the EvitaXL class listed above to communicate with the ventilator.  The source code was written by Teunis van Beelen and is available at http://www.teuniz.net/RS-232/.  The code was tested with GCC on linux and MinGW on Windows XP/2000.  The software is distributed under the GNU General Public License (GPL), which is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt. The file contains the implementation of the methods defined in rs232.h.

evitaXL_log - All data collected from an EvitaXL object is logged to a table in this file, which is organized by time.  In order for the table to be formatted properly, the file should be viewed in a text editor, outside of the terminal, with text wrapping disabled. The contents of this file are overwritten each time the code in EvitaXL.cpp or EvitaFail.cpp is executed. If this file is deleted, the software will simply create a new file with the same name.



@section _build_sec_ Compiling and Running the Source Code:

This software is intended for use on a Linux system. In its current state, the software may not compile or run on another operating system.

To create an executable file called EXEC_NAME from the source code in EvitaXL.cpp, run the command: g++ EvitaXL.cpp -o EXEC_NAME

Likewise, to generate an executable file called EXEC_NAME from the source code in EvitaFail.cpp, run the command: g++ EvitaFail.cpp -o EXEC_NAME

In the above examples, EXEC_NAME can be replaced with any valid file name the user wishes to give the executable.

In order to run an executable file created from EvitaXL.cpp or EvitaFail.cpp, the user must have administrative privileges. Otherwise, the software will not be granted access to the serial port. The simplest way to obtain administrative privileges is to enter the super user command: su

The user will then be prompted for the root password before being granted administrative privileges.

In order to run an executable file created from EvitaXL.cpp or EvitaFail.cpp, the user must enter the following command, where EXEC_NAME is the name given to the executable file and PORT_NAME is the port that the ventilator is connected to (for example, /dev/ttyS1): ./EXEC_NAME PORT_NAME

If no PORT_NAME argument is supplied, the software will default to /dev/ttyS0.



@section _notes_sec_ Notes
@note In order for the software to communicate properly with the EvitaXL ventilator, please ensure that all of the conditions below are met:
    - 1.) Either a null modem or a null modem cable is installed in the connection between the ventilator and the PC.
    - 2.) The serial cable is connected to the port labeled COM1 on the EvitaXL. Otherwise, the ventilator may not be able to send out realtime data.
    - 3.) The protocol for the COM1 serial port on the ventilator is set to Medibus. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.
    - 4.) The Baud rate for the COM1 serial port on the ventilator is set to 19200. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.
    - 5.) The parity for the COM1 serial port on the ventilator is set to none. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.
    - 6.) The stopbit for the COM1 serial port on the ventilator is set to 1. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.
    - 7.) The user running the software has administrative access to the appropriate serial port.



@section _todo_sec_ TODOs
@todo The method isValidIncomingMessage() defined in EvitaXL.h has not been implemented in either EvitaXL.cpp or EvitaFail.cpp.  This method was intended to be used to determine whether or not a received "slow" message is well-structured and contains the correct checksum.
@todo Incorporate functionality which allows users to set the sample rate (every sample, every other, every 3rd, etc.) they would like for each particular realtime data value that they are collecting.


@page _man_page_ EvitaXL Man Page
 @section  _man_sec_ man page
    @subsection _name_sec_ NAME

./EvitaXL   - EvitaXL driver

    @subsection _syn_sec_ SYNOPSIS

   ./EvitaXL
   -p portname
        [-r air|flow|pleth|respvol|co2|expvol]
        [-v 1|2]
        [-l on|off]
   | -s simulationfile
   | -h

    @subsection _des_sec_ DESCRIPTION

Logs data from EvitaXL medical device.

    @subsection _func_sec_ FUNCTION LETTERS

   Main operation mode:

   -p portname

   The program will attempt to establish a connection to
     the specified 'portname'.

   -s simulationfile

   The program will run in simulation mode by retrieving the
     data specified in 'simulationfile'

   -h

   Help. Displays this page.

    @subsection _oth_sec_ OTHER OPTIONS

    Operation modifiers:

   -r  air | flow | pleth | respvol | co2 | expvol

   Denotes the option to retrieve real time data. The program
   is able to retrieve a maximun of three (3) real time values.
   If specified, you must pick at least one (1) real time value.
   The available real time values are as follow
    - air     --- Airway Pressure(mbar)
    - flow    --- Flow(L/min)
    - pleth   --- Pleth(%%)
    - respvol --- Resp. Vol. Since Insp. Begin(mL)
    - co2     --- Exp. CO2(Vol.%%)
    - expvol  --- Exp. Volume(mL)

    -v 1 | 2

verbosity level.
   1 turns on verbose reporting, 2 prints every byte
     received

   -l on|off

   Turns on/off the log. Default: on

    @subsection _exm_sec_ EXAMPLES

   To run using serial port ttyS0, with verbosity reporting,
   collecting Airway Pressure and Exp. Volume and logging capability:

   ./EvitaXL -p /dev/ttyS0 -r air expvol -v 1 -l on

*/


/** @manonly
*
*
* @endmanonly
*/
#ifndef DOXYGEN_MAINPAGE_H
#define DOXYGEN_MAINPAGE_H
#pragma once
#endif // DOXYGEN_MAINPAGE_H
