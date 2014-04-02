package org.mdpnp.guis.waveform.swing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingAnimatorSingleton {

    private static ScheduledExecutorService executorService;
    private static int executorServiceReferences = 0;
    private static final Logger log = LoggerFactory.getLogger(SwingAnimatorSingleton.class);
    public synchronized static ScheduledExecutorService reference(SwingAnimatable dp) {
        if (0 == executorServiceReferences) {
            executorService = Executors.newScheduledThreadPool(1);
        }
        executorServiceReferences++;
        log.debug("Now " + executorServiceReferences + " references to the SwingAnimator");
        dp.setScheduledFuture(executorService.scheduleAtFixedRate(dp, 0, 1000L/30L, TimeUnit.MILLISECONDS));
        return executorService;
    }

    public synchronized static void release(SwingAnimatable dp) {
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
