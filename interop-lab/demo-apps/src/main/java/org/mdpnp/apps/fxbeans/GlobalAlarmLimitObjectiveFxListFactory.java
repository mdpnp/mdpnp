package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class GlobalAlarmLimitObjectiveFxListFactory extends AbstractFxListFactory implements FactoryBean<GlobalAlarmLimitObjectiveFxList>, DisposableBean {
    private GlobalAlarmLimitObjectiveFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public GlobalAlarmLimitObjectiveFxList getObject() throws Exception {
        if(null == instance) {
            instance = new GlobalAlarmLimitObjectiveFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return GlobalAlarmLimitObjectiveFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
