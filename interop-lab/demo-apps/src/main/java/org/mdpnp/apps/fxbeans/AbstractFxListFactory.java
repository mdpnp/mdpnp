package org.mdpnp.apps.fxbeans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public abstract class AbstractFxListFactory<T extends AbstractFxList<?,?,?>> implements FactoryBean<T>, DisposableBean {

}
