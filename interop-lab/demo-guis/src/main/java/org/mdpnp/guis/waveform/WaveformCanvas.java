/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

public interface WaveformCanvas {
	interface Extent {
		int getMinX();
		int getMaxX();
		int getMinY();
		int getMaxY();
	}
	
	void drawLine(int x0, int y0, int x1, int y1);
	void drawSecondaryLine(int x0, int y0, int x1, int y1);
	void clearRect(int x, int y, int width, int height);
//	void setColor(int r, int g, int b, int a);
//	int[] getColor();
//	void setColor(int[] c);
	Extent getExtent();
//	void clearAll();
}
