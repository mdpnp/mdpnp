package org.mdpnp.guis.waveform.swing;

import java.util.concurrent.ScheduledFuture;

public interface SwingAnimatable extends Runnable {
    void setScheduledFuture(ScheduledFuture<?> future);
    ScheduledFuture<?> getScheduledFuture();
}
