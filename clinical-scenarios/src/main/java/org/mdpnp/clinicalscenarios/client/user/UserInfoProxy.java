package org.mdpnp.clinicalscenarios.client.user;

import java.util.List;

import org.mdpnp.clinicalscenarios.server.user.UserInfo;
import org.mdpnp.clinicalscenarios.server.user.UserInfoLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=UserInfo.class,locator=UserInfoLocator.class)
public interface UserInfoProxy extends EntityProxy {

	String getUserId();

	Integer getVersion();

	void setVersion(Integer version);

	String getTitle();

	void setTitle(String title);

	String getGivenName();

	void setGivenName(String givenName);

	String getFamilyName();

	void setFamilyName(String familyName);

	String getHighestLevelOfEducation();

	void setHighestLevelOfEducation(String highestLevelOfEducation);

	String getCompany();

	void setCompany(String company);

	String getJobTitle();

	void setJobTitle(String jobTitle);

	String getYearsInField();

	void setYearsInField(String yearsInField);

	String getPhoneNumber();

	void setPhoneNumber(String phoneNumber);

	boolean isAgreeToBeContacted();

	void setAgreeToBeContacted(boolean agreeToBeContacted);

	String getEmail();

	String getLogoutURL();

	List<LoginProviderProxy> getLoginURL();
	
	boolean getAdmin();

}