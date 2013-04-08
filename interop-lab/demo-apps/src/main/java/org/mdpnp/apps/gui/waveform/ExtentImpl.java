/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.gui.waveform;

public class ExtentImpl implements WaveformCanvas.Extent {
		private int minX, maxX, minY, maxY;
		
		public ExtentImpl(int minX, int maxX, int minY, int maxY) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}
		
		@Override
		public int getMinX() {
			return minX;
		}

		@Override
		public int getMaxX() {
			return maxX;
		}

		@Override
		public int getMinY() {
			return minY;
		}

		@Override
		public int getMaxY() {
			return maxY;
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof WaveformCanvas.Extent) {
				WaveformCanvas.Extent e = (WaveformCanvas.Extent)obj;
				return e.getMinX()==getMinX() && e.getMaxX()==getMaxX() && e.getMinY()==getMinY() && e.getMaxY()==getMaxY();
			} else {
				return false;
			}
		}

}
