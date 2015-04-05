package org.mdpnp.apps.testapp;

import org.mdpnp.devices.TimeManager;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.subscription.Subscriber;

public class DeviceListModelFactory implements FactoryBean<DeviceListModel>, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DeviceListModelFactory.class);

    private DeviceListModel instance;

    private final EventLoop eventLoop;
    private final Subscriber subscriber;
    private final TimeManager timeManager;

    @Override
    public DeviceListModel getObject() throws Exception {
        if(instance == null) {
            instance = new DeviceListModelImpl(subscriber, eventLoop, timeManager);
            // TODO Figure out why it is so important that this be scheduled on the EventLoop
            eventLoop.doNow(() -> {
                instance.start();
            });
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return DeviceListModel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public DeviceListModelFactory(EventLoop eventLoop, Subscriber subscriber,  TimeManager timeManager) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.timeManager = timeManager;
    }

    public void stop() {
        
    }

    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            log.info("Shutting down the model");
            instance.tearDown();
        }
    }

}
