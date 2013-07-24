/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.pulseox;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.math.DCT;


public class SimulatedPulseOximeter {
	private int count = 0;
	
	protected int postIncrCount() {
		int count = this.count;
		this.count = ++this.count>=pleth.length?0:this.count;
		return count;
	}
	
	
	
	private final class MyTask implements Runnable {
	    private final Number[] plethValues = new Number[SAMPLES_PER_UPDATE];
	    
		@Override
		public void run() {
			for(int i = 0; i < plethValues.length; i++) {
				plethValues[i] = pleth[postIncrCount()];
			}

			state = transition(state);
			currentDraw = state.nextDraw();
			myDate.setTime(currentDraw.getTimestamp());
			receivePulseOx(currentDraw.getTimestamp(), currentDraw.getHeartRate(), currentDraw.getSpO2(), plethValues, MILLISECONDS_PER_SAMPLE);
		}
		
	};
	
	protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, double msPerSample) {
	    
	}
	
	private static class Draw {
		private int heartRate;
		private int spO2;
		private long time;
		
		public long getTimestamp() {
			return time;
		}
		public int getHeartRate() {
			return heartRate;
		}
		public int getSpO2() {
			return spO2;
		}
		public void setHeartRate(int heartRate) {
			this.heartRate = heartRate;
		}
		public void setSpO2(int spO2) {
			this.spO2 = spO2;
		}
		public void setTimestamp(long now) {
			this.time = now;
		}
	}
	
    protected static final long UPDATE_PERIOD = 160L;
    protected static final double MILLISECONDS_PER_SAMPLE = 13.333333333;
    protected static final int SAMPLES_PER_UPDATE = (int) Math.floor(UPDATE_PERIOD/MILLISECONDS_PER_SAMPLE);
	
	private final double[] coeffs = new double[] {572784,-3815,-7452,-2196,51,2412,3227,4118,3404,11455,30013,-28722,-1132,-5540,-125,-3859,2048,-1922,4651,1557,26806,-10959,-8725,4525,39,3857,2839,5123,4767,4598,5504,-13121,-1791,4544,65,3178,890,2998,1112,1703,698,-422,-1836,2910,38,1454,206,1504,337,1153,664,372,-3175,1447,-226,345,-263,520,-158,214,-431,-437,-1592,894,41,292,-13,396,73,287,2,269,-106,416,303,360,185,319,154,267,50,241,-66,-53,78,96,-66,84,-47,89,-91,49,-120,20,32,144,0,158,57,185,63,182,76,180,75,201,64,163,42,131,7,91,-22,56,-46,40,-58,22,-63,22,-56,30,-38,48,-16,67,8,91,30,107,48,118,53,118,49,106,32,91,18,64,-1,44,-17,26,-33,16,-48,2,-46,5,-38,14,-22,31,-4,50,12,63,26,77,37,79,40,76,36,71,28,51,12,35,-6,20,-19,6,-31,-2,-35,-4,-35,0,-24,9,-13,22,2,38,15,49,27,56,33,58,32,55,27,45,16,33,2,18,-11,5,-23,-5,-29,-10,-31,-9,-27,-4,-19,7,-6,19,8,30,18,40,26,45,29,44,27,38,19,29,9,17,-3,5,-15,-7,-24,-13,-27,-16,-27,-12,-21,-5,-12,6,1,17,12,27,21,34,26,36,26,34,22,27,14,17,2,5,-9,-6,-19,-16,-25,-19,-27,-19,-23,-15,-15,-5,-5,6,6,15,16,24,23,30,26,30,23,24,18,16,9,5,-3,-6,-13,-15,-22,-22,-25,-24,-24,-22,-20,-15,-10,-5,};
	private final double[] pleth = new double[coeffs.length];

	
	private void initPleth() {
		DCT.idct(coeffs, pleth);
	}
	
	private ScheduledFuture<?> task;
	
	public void connect(ScheduledExecutorService executor) {
		if(task != null) {
			task.cancel(false);
			task = null;
		}
		task = executor.scheduleAtFixedRate(new MyTask(), 0L, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
	}
	
	public void disconnect() {
		if(task != null) {
			task.cancel(false); 
			task = null;
		}
	}
	
	public SimulatedPulseOximeter() {
		initPleth();
		state = transition(state);
		currentDraw = state.nextDraw();
	}
	
	private static class MiniMean {
		private double basis;
		private final double[] memory;
		private int nextLoc = 0;
		
		MiniMean(int sz, double initialValue) {
			basis = sz * initialValue;
			memory = new double[sz];
			for(int i = 0; i < memory.length; i++) {
				memory[i] = initialValue;
			}
		}
		void apply(double x) {
			basis -= memory[nextLoc>0?(nextLoc-1):(memory.length-1)];
			basis += x;
			memory[nextLoc] = x;
			nextLoc = ++nextLoc>=memory.length?0:nextLoc;
		}
		double get() {
			return basis / memory.length;
		}
	}
	
	private static class State {
		private final double avgHeartRate;
		private final double avgSpO2;
		private final double stdevHeartRate;
		private final double stdevSpO2;
		private final double floorHeartRate;
		private final double floorSpO2;
		private final double ceilingHeartRate;
		private final double ceilingSpO2;
		
		private final Draw draw = new Draw();
		
		private final MiniMean heartRate;
		private final MiniMean spo2;
		
		private final Random random = new Random(System.currentTimeMillis());
		
		State(double avgHeartRate, double avgSpO2, double stdevHeartRate, double stdevSpO2, double floorHeartRate, double ceilingHeartRate, double floorSpO2, double ceilingSpO2) {
			this.avgHeartRate = avgHeartRate;
			// TODO it's pretty stupid to average a bunch of Guassian draws
			// the mean needs some kind of markov property to create a more interesting simulation
			this.heartRate = new MiniMean(100, avgHeartRate);
			this.avgSpO2 = avgSpO2;
			this.spo2 = new MiniMean(4, avgSpO2);
			this.stdevHeartRate = stdevHeartRate;
			this.stdevSpO2 = stdevSpO2;
			this.floorHeartRate = floorHeartRate;
			this.floorSpO2 = floorSpO2;
			this.ceilingHeartRate = ceilingHeartRate;
			this.ceilingSpO2 = ceilingSpO2;
			
		}
		
		public Draw nextDraw() {
			double hr = random.nextGaussian() * stdevHeartRate + avgHeartRate;
			double spo2 = random.nextGaussian() * stdevSpO2 + avgSpO2;
			hr = Math.max(hr, floorHeartRate);
			hr = Math.min(hr, ceilingHeartRate);
			heartRate.apply(hr);
			
			spo2 = Math.max(spo2, floorSpO2);
			spo2 = Math.min(spo2, ceilingSpO2);
			this.spo2.apply(spo2);
			
			draw.setHeartRate((int)Math.round(heartRate.get()));
			draw.setSpO2((int)Math.round(this.spo2.get()));
			draw.setTimestamp(System.currentTimeMillis());
			return draw;
		}
		
	}
	
	private static State transition(State state) {
		return state;
	}
		
	private Draw currentDraw = null;
	private State state = new State(75, 98, 30.0, 0.25, 50, 200, 80, 100);
		
	private final Date myDate = new Date();
	
}
