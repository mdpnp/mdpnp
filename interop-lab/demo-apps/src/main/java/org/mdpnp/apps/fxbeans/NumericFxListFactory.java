package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class NumericFxListFactory extends AbstractFxListFactory implements FactoryBean<NumericFxList>, DisposableBean {

    private NumericFxList instance;
    
    public NumericFxListFactory() {
    }
        
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public NumericFxList getObject() throws Exception {
        if(null == instance) {
            instance = new NumericFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return NumericFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
