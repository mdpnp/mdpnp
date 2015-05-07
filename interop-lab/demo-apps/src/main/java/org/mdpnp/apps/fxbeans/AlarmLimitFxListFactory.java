package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class AlarmLimitFxListFactory extends AbstractFxListFactory implements FactoryBean<AlarmLimitFxList>, DisposableBean {
    private AlarmLimitFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public AlarmLimitFxList getObject() throws Exception {
        if(null == instance) {
            instance = new AlarmLimitFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return AlarmLimitFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
