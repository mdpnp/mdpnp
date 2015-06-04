package org.mdpnp.apps.testapp.vital;

import javafx.application.Platform;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.publication.Publisher;

/**
 *
 */
public class VitalModelFactory implements FactoryBean<VitalModel>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(VitalModelFactory.class);

    private VitalModel instance;

    private final DeviceListModel deviceListModel;
    private final EventLoop eventLoop;
    private final NumericFxList numericList;
    private final Publisher publisher;

    @Override
    public VitalModel getObject() throws Exception {
        if(instance == null) {
            instance = new VitalModelImpl(deviceListModel, numericList);
            
            instance.start(publisher, eventLoop);
            
            Platform.runLater( () -> {
                VitalSign.RespiratoryRate.addToModel(instance);
                VitalSign.HeartRate.addToModel(instance);
                VitalSign.SpO2.addToModel(instance);
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

    public VitalModelFactory(EventLoop eventLoop, Publisher publisher, DeviceListModel deviceListModel, NumericFxList numericList) {
        this.eventLoop = eventLoop;
        this.publisher = publisher;
        this.deviceListModel = deviceListModel;
        this.numericList = numericList;
    }

    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            log.info("Shutting down the model");
            instance.stop();
            instance = null;
        }        
    }
}
