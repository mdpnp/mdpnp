package org.mdpnp.rtiapi.data;

import javax.swing.ListModel;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReaderImpl;
import com.rti.dds.subscription.Subscriber;

public interface InstanceModel<D extends Copyable, R extends DataReaderImpl> extends ListModel<D> {

    void addListener(InstanceModelListener<D,R> listener);
    void iterateAndAddListener(InstanceModelListener<D, R> listener);
    void iterateAndAddListener(InstanceModelListener<D,R> listener, int maxSamples);
    void removeListener(InstanceModelListener<D,R> listener);
    
    public void start(Subscriber subscriber, EventLoop eventLoop, String expression, StringSeq params, String qosLibrary, String qosProfile);

    void stop();

    R getReader();
    EventLoop getEventLoop();

    void start(Subscriber subscriber, EventLoop eventLoop, String qosLibrary, String qosProfile);
}
