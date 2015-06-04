package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class SampleArrayFxListFactory extends AbstractFxListFactory implements FactoryBean<SampleArrayFxList>, DisposableBean {

    private SampleArrayFxList instance;
    
    public SampleArrayFxListFactory() {
    }
    
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public SampleArrayFxList getObject() throws Exception {
        if(null == instance) {
            instance = new SampleArrayFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return SampleArrayFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
