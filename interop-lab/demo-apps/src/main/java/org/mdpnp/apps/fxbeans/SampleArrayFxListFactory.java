package org.mdpnp.apps.fxbeans;

import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;

public class SampleArrayFxListFactory implements FactoryBean<SampleArrayFxList>, DisposableBean {

    private SampleArrayFxList instance;
    
    private String topicName;
    private Subscriber subscriber;
    private EventLoop eventLoop;
    private String expression;
    private StringSeq params;
    private String qosLibrary, qosProfile;
    
    public SampleArrayFxListFactory() {
    }
    
    public void setEventLoop(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }
    public EventLoop getEventLoop() {
        return eventLoop;
    }
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    public String getTopicName() {
        return topicName;
    }
    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
    public Subscriber getSubscriber() {
        return subscriber;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public String getExpression() {
        return expression;
    }
    public void setParams(StringSeq params) {
        this.params = params;
    }
    public StringSeq getParams() {
        return params;
    }
    public void setQosLibrary(String qos_library) {
        this.qosLibrary = qos_library;
    }
    public String getQosLibrary() {
        return qosLibrary;
    }
    public void setQosProfile(String qos_profile) {
        this.qosProfile = qos_profile;
    }
    public String getQosProfile() {
        return qosProfile;
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
