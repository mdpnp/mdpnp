package org.mdpnp.devices.simulation.pump;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SimulatedInfusionPump {

    private final class MyTask implements Runnable {
        @Override
        public void run() {
            // We might be in a waiting period before reset
            if(resumeTime > 0L) {
                if(resumeTime <= System.currentTimeMillis()) {
                    // restart the infusion
                    infusionFractionComplete = 0f;
                    // Well only really active if no interlockStop
                    infusionActive = !interlockStop;
                    resumeTime = 0L;
                } else {
                    // in the waiting period
                    infusionActive = false;
                }
            } else {
                if(interlockStop) {
                    // we're under an interlock stop
                    infusionActive = false;
                } else {
                    infusionFractionComplete += 1.0f / infusionDurationSeconds;
                    if(infusionFractionComplete >= 1.0f) {
                        infusionActive = false;
                        resumeTime = System.currentTimeMillis() + WAITING_PERIOD;
                    } else {
                        infusionActive = true;
                    }
                    
                }
            }
            
            receivePumpStatus(drugName, infusionActive, drugMassMcg, solutionVolumeMl, volumeToBeInfusedMl, infusionDurationSeconds, infusionFractionComplete);
        }
        
    };
    
    protected void receivePumpStatus(String drugName, boolean infusionActive, int drugMassMcg, int solutionVolumeMl, int volumeToBeInfusedMl, int infusionDurationSeconds, float infusionFractionComplete) {
        
    }
    
    protected static final long UPDATE_PERIOD = 1000L;
    protected static final long WAITING_PERIOD = 10000L;
    
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
    
    public SimulatedInfusionPump() {

    }
    
    public void setInterlockStop(boolean interLockStop) {
        this.interlockStop = interLockStop; 
    }
    
    private String drugName = "Morphine";
    private boolean infusionActive = true, interlockStop = false;
    private int drugMassMcg = 20, solutionVolumeMl = 120, volumeToBeInfusedMl = 100, infusionDurationSeconds = 3600;
    private float infusionFractionComplete = 0f;
    
    // This pump will infuse at intervals
    private long resumeTime;
    
}
