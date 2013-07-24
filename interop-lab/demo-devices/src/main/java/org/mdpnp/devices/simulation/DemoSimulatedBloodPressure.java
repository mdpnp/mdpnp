/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import ice.Numeric;

import java.util.Random;

import org.mdpnp.devices.EventLoop;

public class DemoSimulatedBloodPressure extends AbstractSimulatedConnectedDevice implements Runnable {
    
    private final InstanceHolder<Numeric> systolic, diastolic, pulse, inflation, nextInflationTime, state;
    // TODO needs to subscribe to an objective state for triggering a NIBP
    

	private final Random random = new Random();
	
//	@Override
//	public void update(IdentifiableUpdate<?> command) {
//	    if(NoninvasiveBloodPressure.REQUEST_NIBP.equals(command.getIdentifier())) {
//	        doInflate();
//	    }
//	    super.update(command);
//	}
	
	protected void simulateReading(int systolic, int diastolic, int pulserate) {
				
	    numericSample(state, ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE);
	    
		int tgtInflation = systolic + 30;
		int inflation = 0;
		
		while(inflation < tgtInflation) {
		    numericSample(this.inflation, inflation);

			inflation += random.nextInt(10)+1;
			try {
				Thread.sleep(250L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
//		synchronized(this) {
		    
//			stateUpdate.setValue(NBPState.Deflating);
//		}

//		gateway.update(this, stateUpdate);
		numericSample(state, ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE);
		
		while(inflation > 0) {
		    numericSample(this.inflation, inflation);

			inflation -= random.nextInt(10)+1;
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		numericSample(this.inflation, inflation);

		numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE);
//		synchronized(this) {
//			stateUpdate.setValue(NBPState.Waiting);
//
//		}
//
//		gateway.update(this, inflationUpdate, stateUpdate);
		numericSample(this.systolic, systolic);
		numericSample(this.diastolic, diastolic);
		numericSample(this.pulse, pulserate);
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
							numericSample(this.nextInflationTime, getNextInflationTimeRemaining());
//							nextInflationUpdate.setValue(getNextInflationTimeRemaining());
//							gateway.update(this, nextInflationUpdate, stateUpdate);
//							numericSample(this.state, ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						now = System.currentTimeMillis();
						diff = nextInflation - now;
						nextRoundMinute = diff % WAITING_NOTIFY_INTERVAL;
					}
//					stateUpdate.setValue(NBPState.Inflating);
				}
				numericSample(this.nextInflationTime, getNextInflationTimeRemaining());
//				nextInflationUpdate.setValue(getNextInflationTimeRemaining());
//				gateway.update(this, nextInflationUpdate, stateUpdate);
				
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
				    numericSample(this.state, ice.MDC_EVT_STAT_OFF.VALUE);
//					stateUpdate.setValue(NBPState.Waiting);
					this.nextInflation = now + INTERVAL;
					this.notifyAll();
				}
				numericSample(this.nextInflationTime, (float)(long) nextInflation);
//				nextInflationUpdate.setValue(nextInflation);
//				gateway.update(this, nextInflationUpdate, stateUpdate);
			}
		}
	}

	private static final long INTERVAL = 3 * 60 * 1000L;
	
	private Thread t;
	
	public DemoSimulatedBloodPressure(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);
		deviceIdentity.model = "NIBP (Simulated)";
		deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
		
		state = createNumericInstance(ice.MDC_PRESS_CUFF.VALUE);
		systolic = createNumericInstance(ice.MDC_PRESS_CUFF_SYS.VALUE);
		diastolic = createNumericInstance(ice.MDC_PRESS_CUFF_DIA.VALUE);
		nextInflationTime = createNumericInstance(ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE);
		inflation = createNumericInstance(ice.MDC_PRESS_CUFF_INFLATION.VALUE);
		// TODO temporarily more interesting
		pulse = createNumericInstance(ice.MDC_PULS_OXIM_PULS_RATE.VALUE);
//		pulse = createNumericInstance(ice.MDC_PULS_RATE_NON_INV.VALUE);
		
		numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE);
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

	public synchronized void doInflate() {
		this.nextInflation = 0L;
		this.notifyAll();
	}

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
