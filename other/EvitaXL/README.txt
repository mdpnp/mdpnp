The project contains several files, each of which is listed and described below.

EvitaXL.h - Contains the definition of the EvitaXL class, which is intended to communicate with the EvitaXL ventilator through a serial port connection and log the data it collects to a file called evitaXL_log.

DataSample.h - Contains the definition and implementation of the DataSample class, which the an EvitaXL object uses to store data values.  A DataSample instance stores the value of a particular data sample along with the time (local) it was received from the ventilator.  The file also contains documentation, in the form of comments, explaining the purpose and functionality of each attribute and method of the EvitaXL class.

EvitaXL.cpp - Contains an implementation of the EvitaXL class defined in EvitaXL.h, along with a main method which creates an EvitaXL object and allows it to continuously communicate with the ventilator and log the data it collects.

rs232.h - Part of the freeware used by the two implementations of the EvitaXL class listed above to communicate with the ventilator.  The source code was written by Teunis van Beelen and is available at http://www.teuniz.net/RS-232/.  The code was tested with GCC on linux and MinGW on Windows XP/2000.  The software is distributed under the GNU General Public License (GPL), which is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt. The file contains several method definitions intended for use in serial communication.

rs232.c - Part of the freeware used by the two implementations of the EvitaXL class listed above to communicate with the ventilator.  The source code was written by Teunis van Beelen and is available at http://www.teuniz.net/RS-232/.  The code was tested with GCC on linux and MinGW on Windows XP/2000.  The software is distributed under the GNU General Public License (GPL), which is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt. The file contains the implementation of the methods defined in rs232.h.

evitaXL_log - All data collected from an EvitaXL object is logged to a table in this file, which is organized by time.  In order for the table to be formatted properly, the file should be viewed in a text editor, outside of the terminal, with text wrapping disabled. The contents of this file are overwritten each time the code in EvitaXL.cpp is executed. If this file is deleted, the software will simply create a new file with the same name. 




Compiling and Running the Source Code:

This software is intended for use on a Linux system. In its current state, the software may not compile or run on another operating system.

To create an executable file called EXEC_NAME from the source code in EvitaXL.cpp, run the command: g++ EvitaXL.cpp -o EXEC_NAME

In the above examples, EXEC_NAME can be replaced with any valid file name the user wishes to give the executable.

In order to run an executable file created from EvitaXL.cpp, the user must have administrative privileges. Otherwise, the software will not be granted access to the serial port. The simplest way to obtain administrative privileges is to enter the super user command: su

The user will then be prompted for the root password before being granted administrative privileges.

In order to run an executable file created from EvitaXL.cpp, the user must enter the following command, where EXEC_NAME is the name given to the executable file and PORT_NAME is the port that the ventilator is connected to (for example, /dev/ttyS1): ./EXEC_NAME PORT_NAME

If no PORT_NAME argument is supplied, the software will default to /dev/ttyS0.






NOTE: In order for the software to communicate properly with the EvitaXL ventilator, please ensure that all of the conditions below are met:

1.) Either a null modem or a null modem cable is installed in the connection between the ventilator and the PC.

2.) The serial cable is connected to the port labeled COM1 on the EvitaXL. Otherwise, the ventilator may not be able to send out realtime data. 

3.) The protocol for the COM1 serial port on the ventilator is set to Medibus. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.

4.) The Baud rate for the COM1 serial port on the ventilator is set to 19200. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.

5.) The parity for the COM1 serial port on the ventilator is set to none. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.

6.) The stopbit for the COM1 serial port on the ventilator is set to 1. This setting can be accessed by pressing the "System Setup" button on the right hand side of the EvitaXL, and then selecting the tab labeled "Interface" on the touch-screen.

7.) The user running the software has administrative access to the appropriate serial port.





TO-DO:

The method isValidIncomingMessage() defined in EvitaXL.h has not been implemented in either EvitaXL.cpp.  This method was intended to be used to determine whether or not a received "slow" message is well-structured and contains the correct checksum.

Incorporate functionality which allows users to set the sample rate (every sample, every other, every 3rd, etc.) they would like for each particular realtime data value that they are collecting.






   
