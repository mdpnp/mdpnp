package org.mdpnp.guis.waveform.swing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SwingAnimatorSingleton {

    private static ScheduledExecutorService executorService;
    private static int executorServiceReferences = 0;
    
    public synchronized static ScheduledExecutorService reference(SwingAnimatable dp) {
        if (0 == executorServiceReferences) {
            executorService = Executors.newScheduledThreadPool(1);
        }
        executorServiceReferences++;
        dp.setScheduledFuture(executorService.scheduleAtFixedRate(dp, 0, 100L, TimeUnit.MILLISECONDS));
        return executorService;
    }

    public synchronized static void release(SwingAnimatable dp) {
        executorServiceReferences--;
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
