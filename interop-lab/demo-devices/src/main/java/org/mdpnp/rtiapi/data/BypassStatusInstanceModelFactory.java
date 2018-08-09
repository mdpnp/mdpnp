package org.mdpnp.rtiapi.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.subscription.Subscriber;

import ice.BypassObjectiveDataWriter;

public class BypassStatusInstanceModelFactory implements FactoryBean<BypassStatusInstanceModel> {

    private static final Logger log = LoggerFactory.getLogger(BypassStatusInstanceModelFactory.class);

    private BypassStatusInstanceModel instance;

    private final EventLoop eventLoop;
    private final Subscriber subscriber;

    @Override
    public BypassStatusInstanceModel getObject() throws Exception {
        if(instance == null) {
            instance = new BypassStatusInstanceModelImpl(ice.BypassStatusTopic.VALUE);
            instance.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return BypassObjectiveDataWriter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public BypassStatusInstanceModelFactory(EventLoop eventLoop, Subscriber subscriber) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
    }

    public void stop() {
        if(instance != null) {
            log.info("Shutting down the model");
            instance.stopReader();
        }
    }
}
