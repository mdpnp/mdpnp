/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nonin.pulseox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Packet {
	
    private final Logger log = LoggerFactory.getLogger(Packet.class);
    
	public Packet() {
		for(int i = 0; i < status.length; i++) {
			status[i] = new Status();
		}
	}
	
	public Status getCurrentStatus() {
		return status[getCurrentFrame()];
	}
	
	public Status[] getStatus() {
		return status;
	}

	public int getAvgHeartRateFourBeat() {
		return avgHeartRateFourBeat;
	}

	public int getFirmwareRevision() {
		return firmwareRevision;
	}

	public int getTimer() {
		return timer;
	}

	public boolean isSmartPoint() {
		return smartPoint;
	}

	public boolean isLowBattery() {
		return lowBattery;
	}

	public int getAvgSpO2FourBeat() {
		return avgSpO2FourBeat;
	}

	public int getAvgSpO2FourBeatFast() {
		return avgSpO2FourBeatFast;
	}

	public int getSpO2BeatToBeat() {
		return SpO2BeatToBeat;
	}

	public int getAvgHeartRateEightBeat() {
		return avgHeartRateEightBeat;
	}

	public int getAvgSpO2EightBeat() {
		return avgSpO2EightBeat;
	}

	public int getAvgSpO2EightBeatForDisplay() {
		return avgSpO2EightBeatForDisplay;
	}

	public int getAvgHeartRateFourBeatForDisplay() {
		return avgHeartRateFourBeatForDisplay;
	}

	public int getAvgHeartRateEightBeatForDisplay() {
		return avgHeartRateEightBeatForDisplay;
	}

	private final int[] pleth = new int[FRAMES];
	private final Status[] status = new Status[FRAMES];
	
	private int avgHeartRateFourBeat;
	private int firmwareRevision;
	private int timer;
	private boolean smartPoint;
	private boolean lowBattery;
	private int avgSpO2FourBeat;
	private int avgSpO2FourBeatFast;
	private int SpO2BeatToBeat;
	private int avgHeartRateEightBeat;
	private int avgSpO2EightBeat;
	private int avgSpO2EightBeatForDisplay;
	private int avgHeartRateFourBeatForDisplay;
	private int avgHeartRateEightBeatForDisplay;
	
	private int currentFrame;
	
	private void setvar(int b) {
		switch(currentFrame) {
		case 0:
			avgHeartRateFourBeat = (b << 8);
			break;
		case 1:
			avgHeartRateFourBeat |= b;
			break;
		case 2:
			avgSpO2FourBeat = b;
			break;
		case 3:
			firmwareRevision = b;
			break;
		// 4 reserved
		case 5:
			timer = (b << 8);
			break;
		case 6:
			timer |= b;
			break;
		case 7:
			smartPoint = 0 != (0x20&b);
			lowBattery = 0 != (0x01&b);
			break;
		case 8:
			avgSpO2FourBeat = b;
			break;
		case 9:
			avgSpO2FourBeatFast = b;
			break;
		case 10:
			SpO2BeatToBeat = b;
			break;
		// 11/12 reserved
		case 13:
			avgHeartRateEightBeat = (b << 8);
			break;
		case 14:
			avgHeartRateEightBeat |= b;
			break;
		case 15:
			avgSpO2EightBeat = b;
			break;
		case 16:
			avgSpO2EightBeatForDisplay = b;
			break;
		// 17/18 reserved
		case 19:
			avgHeartRateFourBeatForDisplay = (b << 8);
			break;
		case 20:
			avgHeartRateFourBeatForDisplay |= b;
			break;
		case 21:
			avgHeartRateEightBeatForDisplay = (b << 8);
			break;
		case 22:
			avgHeartRateEightBeatForDisplay |= b;
			break;
		default:
		}
	}
	
	public int getCurrentFrame() {
		return currentFrame>0?(currentFrame-1):(FRAMES-1);
	}
	
	private long frameTime;
	
	public long getFrameTime() {
		return frameTime;
	}
	
	public static boolean validChecksum(byte[] b, int off) {
		int checksum = (0xFF&b[off+4]);
		int expectedchk = ((0xFF&b[off+0]) + (0xFF&b[off+1]) + (0xFF&b[off+2]) + (0xFF&b[off+3]))%256;
		return checksum == expectedchk;
	}
	
	public boolean setFrame(byte[] b, int off) {
		if(!status[currentFrame].set(b[off+0]).isHighBitSet()) {
			throw new IllegalArgumentException("High bit not set ");
		}
		if(status[currentFrame].isSync()) {
			currentFrame = 0;
			status[currentFrame].set(b[off+0]);
		}
		pleth[currentFrame] = ((0xFF&b[off+1]) << 8) | (0xFF&b[off+2]);
		setvar(0xFF&b[off+3]);

		if(!validChecksum(b, off)) {
			log.warn("Frame="+currentFrame+" "/*+Util.bytesString(b, off, FRAME_LENGTH)*/);
			throw new IllegalArgumentException("In frame=" + currentFrame + " Invalid checksum");
		}
		currentFrame=(++currentFrame==FRAMES?0:currentFrame);
		frameTime = System.currentTimeMillis();
		return 0 == currentFrame;
		
	}
	
	public void set(byte[] b, int off, int basePlethIdx) {
	
		for(int i = 0; i < LENGTH; i+=FRAME_LENGTH) {
			setFrame(b, off + i);
		}
	}
	public static final int FRAME_LENGTH = 5;
	public static final int FRAMES = 25;
	public static final int LENGTH = FRAME_LENGTH * FRAMES;
	
	public int getPleth(int x) {
		return pleth[x];
	}
}
