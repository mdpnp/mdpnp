package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class BypassStatusFxListFactory extends AbstractFxListFactory
		implements FactoryBean<BypassStatusFxList>, DisposableBean {

	private BypassStatusFxList instance;

	public BypassStatusFxListFactory() {
	}

	@Override
	public void destroy() throws Exception {
		if (null != instance) {
			instance.stop();
		}
	}

	@Override
	public BypassStatusFxList getObject() throws Exception {
		if (null == instance) {
			instance = new BypassStatusFxList(topicName);
			instance.start(subscriber, eventLoop, expression, params, qosLibrary, qosProfile);
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return BypassStatusFxList.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
