package org.mdpnp.apps.testapp.pump;

import org.mdpnp.devices.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public interface PumpModel {
    int getCount();
    Pump getPump(int i);
    void setStop(Pump pump, boolean stop);
    
    void start(Subscriber subscriber, Publisher publisher, EventLoop eventLoop);
    void stop();
    
    void addListener(PumpModelListener listener);
    boolean removeListener(PumpModelListener listener);
}
