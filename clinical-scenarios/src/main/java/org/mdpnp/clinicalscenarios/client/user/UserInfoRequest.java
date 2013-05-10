package org.mdpnp.clinicalscenarios.client.user;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("org.mdpnp.clinicalscenarios.server.user.UserInfo")
public interface UserInfoRequest extends RequestContext { 
	Request<UserInfoProxy> findCurrentUserInfo(String urlDestination);
	InstanceRequest<UserInfoProxy, UserInfoProxy> persist();
	Request<List<UserInfoProxy>> findAllUserInfo();
}
