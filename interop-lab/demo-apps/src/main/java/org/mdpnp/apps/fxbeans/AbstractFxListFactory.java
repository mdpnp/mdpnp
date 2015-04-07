package org.mdpnp.apps.fxbeans;

import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;

public class AbstractFxListFactory {
    protected String topicName;
    protected Subscriber subscriber;
    protected EventLoop eventLoop;
    protected String expression;
    protected StringSeq params;
    protected String qosLibrary, qosProfile;
    
    public AbstractFxListFactory() {
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
    
}
