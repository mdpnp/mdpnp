package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

/**
 * This class is direct implementation of the TICKET-197. 
 * <p> Contains feedback that is embeded on the scenario
 * @author diego@mdpnp.org
 *
 */

@SuppressWarnings("serial")
@Embed
public class FeedbackValue implements java.io.Serializable {

	private String navigationOk; 			//feedback about navigation
	private String logicallyOrganized;		//feedback about information/ functionality logically organized
	private String troubleLoginIn;			//feedback about any trouble with login system
	private String unclearQuestions;		//feedback about unclear questions in forms
	private String missingFields;			//feedbackAbout any possible missing fields/tabs/etc.
	private String usefulIfDepartmentAvailable; //would you find this useful if it was available to your department
	private String websiteLooksProfessional; //feedback about repository looking professional/trustworthy
	private String rateThisWebsite;			//
	private String goodVisualDesign;		//Do you like the visual design?
	private String generalSuggestions;		//general suggestions about the website
	
	//getters and setters
	public String getNavigationOk() {
		return navigationOk;
	}
	public void setNavigationOk(String navigationOk) {
		this.navigationOk = navigationOk;
	}
	public String getLogicallyOrganized() {
		return logicallyOrganized;
	}
	public void setLogicallyOrganized(String logicallyOrganized) {
		this.logicallyOrganized = logicallyOrganized;
	}
	public String getTroubleLoginIn() {
		return troubleLoginIn;
	}
	public void setTroubleLoginIn(String troubleLoginIn) {
		this.troubleLoginIn = troubleLoginIn;
	}
	public String getUnclearQuestions() {
		return unclearQuestions;
	}
	public void setUnclearQuestions(String unclearQuestions) {
		this.unclearQuestions = unclearQuestions;
	}
	public String getMissingFields() {
		return missingFields;
	}
	public void setMissingFields(String missingFields) {
		this.missingFields = missingFields;
	}
	public String getUsefulIfDepartmentAvailable() {
		return usefulIfDepartmentAvailable;
	}
	public void setUsefulIfDepartmentAvailable(String usefulIfDepartmentAvailable) {
		this.usefulIfDepartmentAvailable = usefulIfDepartmentAvailable;
	}
	public String getWebsiteLooksProfessional() {
		return websiteLooksProfessional;
	}
	public void setWebsiteLooksProfessional(String websiteLooksProfessional) {
		this.websiteLooksProfessional = websiteLooksProfessional;
	}
	public String getRateThisWebsite() {
		return rateThisWebsite;
	}
	public void setRateThisWebsite(String rateThisWebsite) {
		this.rateThisWebsite = rateThisWebsite;
	}
	public String getGoodVisualDesign() {
		return goodVisualDesign;
	}
	public void setGoodVisualDesign(String goodVisualDesign) {
		this.goodVisualDesign = goodVisualDesign;
	}
	public String getGeneralSuggestions() {
		return generalSuggestions;
	}
	public void setGeneralSuggestions(String generalSuggestions) {
		this.generalSuggestions = generalSuggestions;
	}
	
	
}
