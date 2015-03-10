package org.mdpnp.apps.testapp;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelImpl;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class NumericInstanceModelFactory implements FactoryBean<NumericInstanceModel> {

    private static final Logger log = LoggerFactory.getLogger(NumericInstanceModelFactory.class);

    private NumericInstanceModel instance;

    private final EventLoop eventLoop;
    private final Subscriber subscriber;

    @Override
    public NumericInstanceModel getObject() throws Exception {
        if(instance == null) {
            instance = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
            instance.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.numeric_data);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return NumericInstanceModel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public NumericInstanceModelFactory(EventLoop eventLoop, Subscriber subscriber) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
    }

    public void stop() {
        if(instance != null) {
            log.info("Shutting down the model");
            instance.stop();
        }
    }
}
