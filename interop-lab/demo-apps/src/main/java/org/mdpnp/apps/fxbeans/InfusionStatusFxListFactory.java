package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class InfusionStatusFxListFactory extends AbstractFxListFactory implements FactoryBean<InfusionStatusFxList>, DisposableBean {

    private InfusionStatusFxList instance;
    
    public InfusionStatusFxListFactory() {
    }
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public InfusionStatusFxList getObject() throws Exception {
        if(null == instance) {
            instance = new InfusionStatusFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return InfusionStatusFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
