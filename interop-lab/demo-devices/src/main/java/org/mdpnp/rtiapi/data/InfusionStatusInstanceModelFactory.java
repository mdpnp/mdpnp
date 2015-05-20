package org.mdpnp.rtiapi.data;

import com.rti.dds.subscription.Subscriber;
import ice.InfusionObjectiveDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 */
public class InfusionStatusInstanceModelFactory implements FactoryBean<InfusionStatusInstanceModel> {

    private static final Logger log = LoggerFactory.getLogger(InfusionStatusInstanceModelFactory.class);

    private InfusionStatusInstanceModel instance;

    private final EventLoop eventLoop;
    private final Subscriber subscriber;

    @Override
    public InfusionStatusInstanceModel getObject() throws Exception {
        if(instance == null) {
            instance = new InfusionStatusInstanceModelImpl(ice.InfusionStatusTopic.VALUE);
            instance.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return InfusionObjectiveDataWriter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public InfusionStatusInstanceModelFactory(EventLoop eventLoop, Subscriber subscriber) {
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
