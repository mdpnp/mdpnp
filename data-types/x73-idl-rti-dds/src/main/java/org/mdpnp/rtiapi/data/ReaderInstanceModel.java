package org.mdpnp.rtiapi.data;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.Subscriber;

public interface ReaderInstanceModel<D extends Copyable, R extends DataReader> {

    void addListener(InstanceModelListener<D,R> listener);
    void iterateAndAddListener(InstanceModelListener<D, R> listener);
    void iterate(InstanceModelListener<D,R> listener);
    void iterateAndAddListener(InstanceModelListener<D,R> listener, int maxSamples);
    void removeListener(InstanceModelListener<D,R> listener);
    
    void startReader(Subscriber subscriber, EventLoop eventLoop, String expression, StringSeq params, String qosLibrary, String qosProfile);
    void startReader(Subscriber subscriber, EventLoop eventLoop, String qosLibrary, String qosProfile);
    void startReader(Subscriber subscriber, EventLoop eventLoop);
    void stopReader();

    R getReader();
    EventLoop getEventLoop();
    
    int size();
}
