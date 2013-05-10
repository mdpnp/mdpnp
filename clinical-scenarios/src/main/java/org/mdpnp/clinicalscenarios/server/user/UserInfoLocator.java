package org.mdpnp.clinicalscenarios.server.user;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import com.google.web.bindery.requestfactory.shared.Locator;

public class UserInfoLocator extends Locator<UserInfo, String> {
	
	@Override
	public UserInfo create(Class<? extends UserInfo> clazz) {
	    UserInfo ui = new UserInfo();
	    ofy().save().entity(ui).now();
	    return ui;
	}

	@Override
	public UserInfo find(Class<? extends UserInfo> clazz, String id) {
	    return ofy().load().type(clazz).id(id).now();
	}

	@Override
	public Class<UserInfo> getDomainType() {
		return UserInfo.class;
	}

	@Override
	public String getId(UserInfo domainObject) {
		return domainObject.getUserId();
	}

	@Override
	public Class<String> getIdType() {
		return String.class;
	}

	@Override
	public Object getVersion(UserInfo domainObject) {
		return domainObject.getVersion();
	}

}
