/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

/**
 * A generic interface for a component that has access to waveform data.
 * The data are expected to form a circular buffer from domain x=0 to x=(getMax()-1)
 * getCount() is indicative of the most recently updated point
 * For two invocations of getCount(), x0 and x1 it is implied that all domain
 * values between x0 and x1 have been updated between the invocations
 *  
 *
 */
public interface WaveformSource {
	/**
	 * The waveform at position x
	 * @param x
	 * @return
	 */
	float getValue(int x);
	
	/**
	 * The maximum extent of the waveform domain
	 * @return
	 */
	int getMax();
	
	/**
	 * The most recently updated point
	 * @return
	 */
	int getCount();
	
	
	/**
	 * Resolution of the sample array
	 * @return
	 */
	double getMillisecondsPerSample();
	
	void addListener(WaveformSourceListener listener);
	void removeListener(WaveformSourceListener listener);
}
