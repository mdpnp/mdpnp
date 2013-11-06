package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.FeedbackValue;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=FeedbackValue.class)
public interface FeedbackProxy extends ValueProxy{
	
	public String getNavigationOk();
	public void setNavigationOk(String navigationOk);
	
	public String getLogicallyOrganized();
	public void setLogicallyOrganized(String logicallyOrganized);
	
	public String getTroubleLoginIn();
	public void setTroubleLoginIn(String troubleLoginIn);
	
	public String getUnclearQuestions();
	public void setUnclearQuestions(String unclearQuestions);
	
	public String getMissingFields();
	public void setMissingFields(String missingFields);
	
	public String getUsefulIfDepartmentAvailable();
	public void setUsefulIfDepartmentAvailable(String usefulIfDepartmentAvailable);
	
	public String getWebsiteLooksProfessional();
	public void setWebsiteLooksProfessional(String websiteLooksProfessional);
	
	public String getRateThisWebsite();
	public void setRateThisWebsite(String rateThisWebsite);
	
	public String getGoodVisualDesign();
	public void setGoodVisualDesign(String goodVisualDesign);
	
	public String getGeneralSuggestions();
	public void setGeneralSuggestions(String generalSuggestions);

}
