package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class PatientAssessmentFxListFactory extends AbstractFxListFactory implements FactoryBean<PatientAssessmentFxList>, DisposableBean {

    private PatientAssessmentFxList instance;
    
    public PatientAssessmentFxListFactory() {
    }
        
    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            instance.stop();
        }
    }

    @Override
    public PatientAssessmentFxList getObject() throws Exception {
        if(null == instance) {
            instance = new PatientAssessmentFxList(topicName);
            instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return PatientAssessmentFxList.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
