package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class SafetyFallbackObjectiveFxListFactory extends AbstractFxListFactory implements FactoryBean<SafetyFallbackObjectiveFxList>, DisposableBean{
private SafetyFallbackObjectiveFxList instance;
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public SafetyFallbackObjectiveFxList getObject() throws Exception {
        if(null == instance) {
            instance = new SafetyFallbackObjectiveFxList(topicName);
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
