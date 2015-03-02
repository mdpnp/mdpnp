package org.mdpnp.apps.testapp.vital;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 *
 */
public class VitalModelFactory implements FactoryBean<VitalModel> {

    private static final Logger log = LoggerFactory.getLogger(VitalModelFactory.class);

    private VitalModel instance;

    private final DeviceListModel deviceListModel;
    private final EventLoop eventLoop;
    private final Subscriber subscriber;
    private final Publisher publisher;

    @Override
    public VitalModel getObject() throws Exception {
        if(instance == null) {
            instance = new VitalModelImpl(deviceListModel);
            instance.start(subscriber, publisher, eventLoop);

            eventLoop.doLater(new Runnable() {
                @Override
                public void run() {
                    VitalSign.SpO2.addToModel(instance);
                    VitalSign.RespiratoryRate.addToModel(instance);
                    VitalSign.EndTidalCO2.addToModel(instance);
                }
            });
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return VitalModel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public VitalModelFactory(EventLoop eventLoop, Subscriber subscriber,  Publisher publisher, DeviceListModel deviceListModel) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.deviceListModel = deviceListModel;
    }

    public void stop() {
        if(instance != null) {
            log.info("Shutting down the model");
            instance.stop();
        }
    }
}
