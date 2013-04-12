/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/

/* This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */

import java.io.*;
import java.util.*;
import javax.comm.*;

public class SmartSat {
    static boolean            VERBOSE = true;
    static Enumeration	      portList;
    static CommPortIdentifier portId;
    static String	      messageString = "SSS098\r\n";
    static SerialPort	      serialPort;
    static OutputStream       outputStream;
    static boolean	      outputBufferEmptyFlag = false;
    static int                delayTime = 100;
//    static String             defaultPort = "/dev/ttyS0";
    static String             defaultPort = "/dev/ttyUSB0";

    /**
     * Method declaration
     *
     *
     * @param args
     *
     * @see
     */
    public static void main(String args[]){
	SmartSat ss = new SmartSat();
	ss.openPort();
	int i = 0;
	
	System.out.println("Running Tests");

	System.out.println("  * Locking Keypad.");
	ss.lockKeypad();

	System.out.println("  * Running through Sp02 values.");
	for (i = -1; i <= 101; i++){
	ss.setSPO2(i);
	}
       
	System.out.println("  * Running through Pulse Rate values.");		
	for (i = 19; i <= 301; i++){
	    ss.setPR(i);
	}

	System.out.println("  * Setting SpO2 to 95, PR to 60.");
	ss.setSPO2(95);
	ss.setPR(60);
	
	System.out.println("  * Unlocking Keypad.");
	ss.unlockKeypad();

	ss.closePort();
	

    }



    public void unlockKeypad(){
	messageString = "SSK0\r\n";

	try {
	    outputStream.write(messageString.getBytes());
	} catch (IOException e) {}
	
	if(VERBOSE){
	    System.out.println(
			       "Writing \""+messageString+"\" to "
			       +serialPort.getName());
	}

	try {
	    Thread.sleep(delayTime);  // Be sure data is xferred before closing
	} catch (Exception e) {}
    }

    public void lockKeypad(){
	messageString = "SSK1\r\n";

	try {
	    outputStream.write(messageString.getBytes());
	} catch (IOException e) {}
	
	if(VERBOSE){
	    System.out.println(
			       "Writing \""+messageString+"\" to "
			       +serialPort.getName());
	}

	try {
	    Thread.sleep(delayTime);  // Be sure data is xferred before closing
	} catch (Exception e) {}
    }

    public int setSPO2(int spo2){
	this.openPort();
	if (spo2 < 0 || spo2 > 100){
	    if (VERBOSE) {
		System.out.println("Invalid SpO2 value: " + spo2 + "\n");
	    }
	    return 0;
	}

	if (spo2 == 100){
	    messageString = "SSS100\r\n";
	}
	else if (spo2 == 0) {
	    messageString = "SSS000\r\n";
	} else {
	    messageString = "SSS0" + spo2 + "\r\n";
	}

	try {
	    outputStream.write(messageString.getBytes());
	} catch (IOException e) {}
	
	if(VERBOSE){
	    System.out.println(
			       "Writing \""+messageString+"\" to "
			       +serialPort.getName());
	}

	try {
	    Thread.sleep(delayTime);  // Be sure data is xferred before closing
	} catch (Exception e) {}
	  
	this.closePort();
	return 1;
    }


    public int setPR(int pr){
	if (pr < 20 || pr > 300){
	    if (VERBOSE) {
		System.out.println("Invalid Pulse Rate Value: " + pr + "\n");
	    }
	    return 0;
	}

	if (pr >= 100){
	    messageString = "SSR"+pr+"\r\n";
	} else if (pr >= 10){
	    messageString = "SSR0"+pr+"\r\n";
	} else if (pr == 0) {
	    messageString = "SSR000\r\n";
	} else {
	    messageString = "SSR00" + pr + "\r\n";
	}

	this.openPort();
	try {
	    outputStream.write(messageString.getBytes());
	} catch (IOException e) {}
	this.closePort();
	if(VERBOSE){
	    System.out.println(
			       "Writing \""+messageString+"\" to "
			       +serialPort.getName());
	}

	try {
	    Thread.sleep(delayTime);  // Be sure data is xferred before closing
	} catch (Exception e) {}
	  
	return 1;
    }


	
public void openPort() {
    boolean portFound = false;

    portList = CommPortIdentifier.getPortIdentifiers();

    while (portList.hasMoreElements()) {
	portId = (CommPortIdentifier) portList.nextElement();

	if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

	    if (portId.getName().equals(defaultPort)) {
		if(VERBOSE){
		System.out.println("Found port " + defaultPort);
		}
		portFound = true;

		try {
		    serialPort = 
			(SerialPort) portId.open("SimpleWrite", 2000);
		} catch (PortInUseException e) {
		    System.out.println("Port in use.");

		    continue;
		} 

		try {
		    outputStream = serialPort.getOutputStream();
		} catch (IOException e) {}



		boolean resetSpeed = false;
		while (!resetSpeed)
			try {
				System.out.println("Trying to set serial params...");
serialPort.setSerialPortParams(1200, 
			       //serialPort.getDataBits(),   
SerialPort.DATABITS_8, 
						   serialPort.getStopBits(), 
						   SerialPort.PARITY_NONE);
				resetSpeed = true;
				System.out.println("Serial parameters set!");
			} catch (Exception e) {
				System.out.println("SetSerialPortParams failed!...");
			}



		//  System.out.println("hallo wat een gedoe...");
		//try {
		    /*		    serialPort.setSerialPortParams(1200, 
						   SerialPort.DATABITS_8, 
						   SerialPort.STOPBITS_1, 
						   SerialPort.PARITY_NONE);
		    */
		//            System.out.println("nog meer printlines zodat gremlins hun werk kunnen doen");
		//  System.out.println("alalalal still waiting");
		//  System.out.println("nou nou duurt wel lang...");

		//	    serialPort.setSerialPortParams(1200, 
		//			serialPort.getDataBits(),   //SerialPort.DATABITS_8, 
		//	   serialPort.getStopBits(), 
						       //			   SerialPort.PARITY_NONE);
		//		} catch (UnsupportedCommOperationException e) {}
	

		try {
		    serialPort.notifyOnOutputEmpty(true);
		} catch (Exception e) {
		    System.out.println("Error setting event notification");
		    System.out.println(e.toString());
		    System.exit(-1);
		}
		/***/	
	    }
	}
    }

    if (!portFound) {
	System.out.println("port " + defaultPort + " not found.");
    } 
} 


public void closePort(){
    serialPort.close();
}




}




