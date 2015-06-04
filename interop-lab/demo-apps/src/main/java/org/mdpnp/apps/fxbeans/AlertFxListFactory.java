package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class AlertFxListFactory extends AbstractFxListFactory implements FactoryBean<AlertFxList>, DisposableBean {
    private AlertFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public AlertFxList getObject() throws Exception {
        if(null == instance) {
            instance = new AlertFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return AlertFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
