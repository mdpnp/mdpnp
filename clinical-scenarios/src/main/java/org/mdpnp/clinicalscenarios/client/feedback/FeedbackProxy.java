package org.mdpnp.clinicalscenarios.client.feedback;

import org.mdpnp.clinicalscenarios.server.feedback.Feedback;
import org.mdpnp.clinicalscenarios.server.feedback.FeedbackLocator;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value=Feedback.class,locator=FeedbackLocator.class)
public interface FeedbackProxy extends EntityProxy{
	
	String getUsersEmail();
	void setUsersEmail(String usersEmail);
	
	String getNavigationOk();
	void setNavigationOk(String navigationOk);
	
	String getLogicallyOrganized();
	void setLogicallyOrganized(String logicallyOrganized);
	
	String getTroubleLoginIn();
	void setTroubleLoginIn(String troubleLoginIn);
	
	String getUnclearQuestions();
	void setUnclearQuestions(String unclearQuestions);
	
	String getMissingFields();
	void setMissingFields(String missingFields);
	
	String getUsefulIfDepartmentAvailable();
	void setUsefulIfDepartmentAvailable(String usefulIfDepartmentAvailable);
	
	String getWebsiteLooksProfessional();
	void setWebsiteLooksProfessional(String websiteLooksProfessional);
	
	String getRateThisWebsite();
	void setRateThisWebsite(String rateThisWebsite);
	
	String getGoodVisualDesign();
	void setGoodVisualDesign(String goodVisualDesign);
	
	String getGeneralSuggestions();
	void setGeneralSuggestions(String generalSuggestions);

}
