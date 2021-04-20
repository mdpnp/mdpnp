package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class DataQualityErrorObjectiveFxListFactory extends AbstractFxListFactory implements FactoryBean<DataQualityErrorObjectiveFxList>, DisposableBean{
private DataQualityErrorObjectiveFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public DataQualityErrorObjectiveFxList getObject() throws Exception {
        if(null == instance) {
            instance = new DataQualityErrorObjectiveFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return SafetyFallbackObjectiveFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
