package org.mdpnp.apps.testapp.data;

import javax.swing.ListModel;

import org.mdpnp.devices.EventLoop;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReaderImpl;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.util.LoanableSequence;

public interface InstanceModel<D extends Copyable, R extends DataReaderImpl> extends ListModel<D> {

    public void start(Subscriber subscriber, EventLoop eventLoop, String expression, StringSeq params, String qosLibrary, String qosProfile);

    void stop();

    R getReader();

    void start(Subscriber subscriber, EventLoop eventLoop, String qosLibrary, String qosProfile);
}
