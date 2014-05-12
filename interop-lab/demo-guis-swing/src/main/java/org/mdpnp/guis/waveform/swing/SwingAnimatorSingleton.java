package org.mdpnp.guis.waveform.swing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingAnimatorSingleton {
    private static final SwingAnimatorSingleton instance = new SwingAnimatorSingleton(); 
    
    private SwingAnimatorSingleton() {

    }
    
    
    public static SwingAnimatorSingleton getInstance() {
        return instance;
    }

    private long targetFramesPerSecond = 10L;
    
    public void setTargetFramesPerSecond(long targetFramesPerSeconds) {
        this.targetFramesPerSecond = targetFramesPerSeconds;
    }
    
    public long getTargetFramesPerSecond() {
        return targetFramesPerSecond;
    }
    
    
    private final ThreadFactory threadFactory = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "SwingAnimatorSingleton");
            t.setDaemon(true);
            return t;
        }
        
    };
    
    private ScheduledExecutorService executorService;
    private int executorServiceReferences = 0;
    private final Logger log = LoggerFactory.getLogger(SwingAnimatorSingleton.class);
    
    public synchronized ScheduledExecutorService reference(SwingAnimatable dp) {
        if (0 == executorServiceReferences) {
            executorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        }
        executorServiceReferences++;
        log.debug("Now " + executorServiceReferences + " references to the SwingAnimator");
        dp.setScheduledFuture(executorService.scheduleAtFixedRate(dp, 0, 1000L/targetFramesPerSecond, TimeUnit.MILLISECONDS));
        return executorService;
    }

    public synchronized void release(SwingAnimatable dp) {
        executorServiceReferences--;
        log.debug("Now " + executorServiceReferences + " references to the SwingAnimator");
        if (null != dp.getScheduledFuture()) {
            dp.getScheduledFuture().cancel(true);
            dp.setScheduledFuture(null);
        }
        if (0 == executorServiceReferences) {
            executorService.shutdown();
            executorService = null;
        }
    }

}
