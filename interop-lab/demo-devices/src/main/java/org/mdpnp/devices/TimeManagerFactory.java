package org.mdpnp.devices;

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public class TimeManagerFactory implements FactoryBean<TimeManager> {
    private static final Logger log = LoggerFactory.getLogger(TimeManagerFactory.class);

    private TimeManager instance;

    private final EventLoop eventLoop;
    private final Subscriber subscriber;
    private final Publisher publisher;
    private final String uniqueDeviceIdentifier;
    private final String type;

    @Override
    public TimeManager getObject() throws Exception {
        if(instance == null) {
            instance = new TimeManager(publisher, subscriber, uniqueDeviceIdentifier, type);
            eventLoop.doLater(new Runnable() {
                @Override
                public void run() {
                    instance.start();
                }
            });
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return TimeManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public TimeManagerFactory(final EventLoop eventLoop, final Publisher publisher, 
            final Subscriber subscriber, final String uniqueDeviceIdentifier, 
            final String type) {
        this.eventLoop = eventLoop;
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.type = type;
    }

    public void stop() {
        if(instance != null) {
            log.info("Shutting down the TimeManager");
            instance.stop();
        }
    }


}
