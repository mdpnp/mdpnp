package org.mdpnp.clinicalscenarios.client.user;

import org.mdpnp.clinicalscenarios.server.user.LoginProvider;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=LoginProvider.class)
public interface LoginProviderProxy extends ValueProxy {
    String getName();
    String getLoginURL();
}
