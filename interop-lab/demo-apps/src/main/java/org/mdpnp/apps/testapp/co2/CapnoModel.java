package org.mdpnp.apps.testapp.co2;

import org.mdpnp.devices.EventLoop;

import com.rti.dds.subscription.Subscriber;

public interface CapnoModel {
    int getCount();
    Capno getCapno(int i);
    
    void start(Subscriber subscriber, EventLoop eventLoop);
    void stop();
    
    void addCapnoListener(CapnoModelListener listener);
    boolean removeCapnoListener(CapnoModelListener listener);
}
