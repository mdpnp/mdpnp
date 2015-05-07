package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class LocalAlarmLimitObjectiveFxListFactory extends AbstractFxListFactory implements FactoryBean<LocalAlarmLimitObjectiveFxList>, DisposableBean {
    private LocalAlarmLimitObjectiveFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public LocalAlarmLimitObjectiveFxList getObject() throws Exception {
        if(null == instance) {
            instance = new LocalAlarmLimitObjectiveFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return LocalAlarmLimitObjectiveFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
