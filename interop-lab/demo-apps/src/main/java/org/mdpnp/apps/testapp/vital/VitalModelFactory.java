package org.mdpnp.apps.testapp.vital;

import javafx.application.Platform;

import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
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
    private final NumericInstanceModel numericInstanceModel;
    private final Publisher publisher;
    private VitalModelNumericProvider provider;

    @Override
    public VitalModel getObject() throws Exception {
        if(instance == null) {
            instance = new VitalModelImpl(deviceListModel);
            provider = new VitalModelNumericProvider(instance);
            numericInstanceModel.iterateAndAddListener(provider);
            
            instance.start(publisher, eventLoop);
            
            Platform.runLater( () -> {
                VitalSign.SpO2.addToModel(instance);
                VitalSign.RespiratoryRate.addToModel(instance);
                VitalSign.EndTidalCO2.addToModel(instance);
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

    public VitalModelFactory(EventLoop eventLoop, Publisher publisher, DeviceListModel deviceListModel, NumericInstanceModel numericInstanceModel) {
        this.eventLoop = eventLoop;
        this.publisher = publisher;
        this.deviceListModel = deviceListModel;
        this.numericInstanceModel = numericInstanceModel;
    }

    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            log.info("Shutting down the model");
            numericInstanceModel.removeListener(provider);
            instance.stop();
            instance = null;
        }        
    }
}
