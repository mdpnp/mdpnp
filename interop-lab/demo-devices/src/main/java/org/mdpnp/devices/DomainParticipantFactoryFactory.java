package org.mdpnp.devices;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipantFactory;

public class DomainParticipantFactoryFactory implements FactoryBean<com.rti.dds.domain.DomainParticipantFactory>, DisposableBean {
    private com.rti.dds.domain.DomainParticipantFactory instance;
    
    public DomainParticipantFactoryFactory() {
    }
    
    @Override
    public com.rti.dds.domain.DomainParticipantFactory getObject() throws Exception {
        if(null == instance) {
            instance = DomainParticipantFactory.get_instance();
        }
        return instance;
    }

    @Override
    public Class<DomainParticipantFactory> getObjectType() {
        return DomainParticipantFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            DomainParticipantFactory.finalize_instance();
            instance = null;
        }
    }

}
