/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import java.util.Random;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdate;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.comms.data.numeric.MutableNumericUpdate;
import org.mdpnp.comms.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.comms.nomenclature.NoninvasiveBloodPressure;
import org.mdpnp.comms.nomenclature.Ventilator;

public class SimulatedBloodPressureImpl extends AbstractSimulatedConnectedDevice implements SimulatedBloodPressure, Runnable {
	private final MutableNumericUpdate systolicUpdate = new MutableNumericUpdateImpl(SYSTOLIC);
	private final MutableNumericUpdate diastolicUpdate = new MutableNumericUpdateImpl(DIASTOLIC);
	private final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PULSE);
	private final MutableNumericUpdate inflationUpdate = new MutableNumericUpdateImpl(INFLATION_PRESSURE);
	private final MutableNumericUpdate nextInflationUpdate = new MutableNumericUpdateImpl(NEXT_INFLATION_TIME_REMAINING);
	private final MutableEnumerationUpdate stateUpdate = new MutableEnumerationUpdateImpl(NoninvasiveBloodPressure.STATE);
	
	// TODO this is temporary (as are we all)
	private final MutableNumericUpdate rrUpdate = new MutableNumericUpdateImpl(Ventilator.RESPIRATORY_RATE);
	
	private final Random random = new Random();
	
	protected void simulateReading(int systolic, int diastolic, int pulserate) {
		inflationUpdate.setValue(null);
		systolicUpdate.setValue(null);
		diastolicUpdate.setValue(null);
		pulseUpdate.setValue(null);
		
		gateway.update(this, inflationUpdate, systolicUpdate, diastolicUpdate, pulseUpdate, stateUpdate, rrUpdate);
				
		int tgtInflation = systolic + 30;
		int inflation = 0;
		
		while(inflation < tgtInflation) {
			inflationUpdate.setValue(inflation);

			gateway.update(this, inflationUpdate);
			inflation += random.nextInt(10)+1;
			try {
				Thread.sleep(250L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
		synchronized(this) {
			stateUpdate.setValue(NBPState.Deflating);
		}

		gateway.update(this, stateUpdate);
		
		while(inflation > 0) {
			inflationUpdate.setValue(inflation);
			gateway.update(this, inflationUpdate);
			inflation -= random.nextInt(10)+1;
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		inflationUpdate.setValue(inflation);

		synchronized(this) {
			stateUpdate.setValue(NBPState.Waiting);

		}

		gateway.update(this, inflationUpdate, stateUpdate);
		
		systolicUpdate.setValue(systolic);
		diastolicUpdate.setValue(diastolic);
		pulseUpdate.setValue(pulserate);
		
		gateway.update(this, systolicUpdate, diastolicUpdate, pulseUpdate, rrUpdate);
	}

	protected void simulateRandomReading() {
		float f = random.nextFloat();
		simulateReading((int)(f * 55)+110, (int)(f * 35)+70, (int)(f * 55)+55);		
	}
	
	private boolean running = true;
	
	private int[] singleOverride = null;
	
	private Long nextInflation = 0L;
	
	// TODO this is too quick
	private static final long WAITING_NOTIFY_INTERVAL = 1000L;
	
	public void run() {
		
		while(running) {
			try {
				long now = System.currentTimeMillis();
				synchronized(this) {
					long diff = nextInflation - now;
					long nextRoundMinute = diff % WAITING_NOTIFY_INTERVAL;
					while(diff > 0) {
						try {
							this.wait(Math.max(1, Math.min(diff, nextRoundMinute)));
							nextInflationUpdate.setValue(getNextInflationTimeRemaining());
							gateway.update(this, nextInflationUpdate, stateUpdate);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						now = System.currentTimeMillis();
						diff = nextInflation - now;
						nextRoundMinute = diff % WAITING_NOTIFY_INTERVAL;
					}
					stateUpdate.setValue(NBPState.Inflating);
				}
				
				nextInflationUpdate.setValue(getNextInflationTimeRemaining());
				gateway.update(this, nextInflationUpdate, stateUpdate);
				
				int[] singleOverride = this.singleOverride;
				this.singleOverride = null;
				
				this.nextInflation = null;
				
				if(null != singleOverride) {
					simulateReading(singleOverride[0], singleOverride[1], singleOverride[2]);
				} else {
					simulateRandomReading();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				long now = System.currentTimeMillis();
				synchronized(this) {
					stateUpdate.setValue(NBPState.Waiting);
					this.nextInflation = now + INTERVAL;
					this.notifyAll();
				}
				nextInflationUpdate.setValue(nextInflation);
				gateway.update(this, nextInflationUpdate, stateUpdate);
			}
		}
	}

	private static final long INTERVAL = 3 * 60 * 1000L;
	
	private Thread t;
	
	public SimulatedBloodPressureImpl(Gateway gateway) {
		super(gateway);
		nameUpdate.setValue("NIBP (Simulated)");
		stateUpdate.setValue(NBPState.Waiting);
//		add(nameUpdate);
//		add(guidUpdate);
		add(stateUpdate);
		add(systolicUpdate);
		add(diastolicUpdate);
		add(nextInflationUpdate);
		add(inflationUpdate);
		add(pulseUpdate);
		
		rrUpdate.setValue(12);
		add(rrUpdate);
	}
	
	@Override
	public void connect(String str) {
		if(null != t) {
			running = false;
			try {
				t.join(5000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t = null;
		}
		running = true;
		t = new Thread(this);
		t.setDaemon(true);
		t.start();

		super.connect(str);
	}

	@Override
	public void disconnect() {
		if(t != null) {
			running = false;
			try {
				t.join(5000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t = null;
		}
		super.disconnect();
	}
	
	private Long getNextInflationTimeRemaining() {
		return null == nextInflation ? null : (nextInflation - System.currentTimeMillis());
	}

	@Override
	public synchronized void doInflate() {
		this.nextInflation = 0L;
		this.notifyAll();
	}

	@Override
	public synchronized void doSimulate(int systolic, int diastolic, int pulserate) {
		this.singleOverride = new int[] { systolic, diastolic, pulserate };
		this.nextInflation = 0L;
		this.notifyAll();
	}
	@Override
	protected String iconResourceName() {
		return "nbp.png";
	}
}
