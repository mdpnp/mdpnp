package org.mdpnp.devices;

import java.util.concurrent.ScheduledExecutorService;

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public class TimeManagerFactory implements FactoryBean<TimeManager>, DisposableBean {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TimeManagerFactory.class);

    private TimeManager instance;

    private ScheduledExecutorService executor;
    private EventLoop eventLoop;
    private Subscriber subscriber;
    private Publisher publisher;
    private String uniqueDeviceIdentifier;
    private String type;

    public EventLoop getEventLoop() {
        return eventLoop;
    }
    public void setEventLoop(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }
    public ScheduledExecutorService getExecutor() {
        return executor;
    }
    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }
    public Publisher getPublisher() {
        return publisher;
    }
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    public Subscriber getSubscriber() {
        return subscriber;
    }
    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setUniqueDeviceIdentifier(String uniqueDeviceIdentifier) {
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
    }
    public String getUniqueDeviceIdentifier() {
        return uniqueDeviceIdentifier;
    }
    
    @Override
    public TimeManager getObject() throws Exception {
        if(instance == null) {
            instance = new TimeManager(executor, eventLoop, publisher, subscriber, uniqueDeviceIdentifier, type);
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
    
    public TimeManagerFactory() {
    }

    public TimeManagerFactory(ScheduledExecutorService executor, EventLoop eventLoop, Publisher publisher, Subscriber subscriber, String uniqueDeviceIdentifier,
            String type) {
        this.executor = executor;
        this.eventLoop = eventLoop;
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.type = type;
    }
    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            instance.stop();
        }
    }


}
