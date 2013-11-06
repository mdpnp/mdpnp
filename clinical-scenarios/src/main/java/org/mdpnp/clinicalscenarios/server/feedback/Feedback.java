package org.mdpnp.clinicalscenarios.server.feedback;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.io.Serializable;
import java.util.Date;

import org.mdpnp.clinicalscenarios.server.mailservice.RepositoryMailService;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
/**
 * This class implements a Feedback entity, that keeps track of users' feedback
 * and administrator/developer notes
 * @author diego@mdpnp.org
 *
 */
@SuppressWarnings("serial")
@Entity
public class Feedback  implements Serializable{
	
	//constants for warning emails
	static final String EMAIL_TO_WHO = "md.pnp.team@gmail.com";
	static final String EMAIL_SUBJECT = "Received feedback about the Clinical Scenario Repository";
	
	@Id
	private Long id; //id for the GAE
	private Integer version = 1;
	
	private String usersEmail; //email ID of the user who submitted this feedback
//	private String usersFeedback; //text of users' feedback
	
	private String adminNotes; //text or notes from admin/developers about feedback
	private String associatedTicket; //associated ticket on Sourceforge for this feedback/issue, in case there's one
	
	private Boolean visited = false; //indicated that this messaged has been read/noted/taken into account
	
	private Date feedbackCreationDate  = new Date(); 	//creation date of the feedback text
	private Date noteCreationDate;	//creation date of the admin/developer note
	private Date noteModificationDate;	//modification date for the note created by the admin/developer
	
	@Index
	private String navigationOk; 			//feedback about navigation
	@Index
	private String logicallyOrganized;		//feedback about information/ functionality logically organized
	@Index
	private String troubleLoginIn;			//feedback about any trouble with login system	
	@Index
	private String unclearQuestions;		//feedback about unclear questions in forms
	@Index
	private String missingFields;			//feedbackAbout any possible missing fields/tabs/etc.
	@Index
	private String usefulIfDepartmentAvailable; //would you find this useful if it was available to your department
	@Index
	private String websiteLooksProfessional; //feedback about repository looking professional/trustworthy
	@Index
	private String rateThisWebsite;			//
	@Index
	private String goodVisualDesign;		//Do you like the visual design?
	@Index
	private String generalSuggestions;		//general suggestions about the website
	
//	XXX Add Julian's suggestion fields
	
	@OnSave
	void onPersist() {
	    version++;
	}
	
	public static Feedback create(){
		Feedback fb = new Feedback();
		return fb;
	}

	public Feedback persist(){
	    ofy().save().entity(this).now();
	    //send a warning email to admin
	    sendWarningMail();
	    return this;
	}
	
	private void sendWarningMail(){
		String messageText = "Feedback sent by "+this.usersEmail+"\n";
		
		messageText += "How would you rate this website? \n" + this.rateThisWebsite + "\n";
		messageText += "Is the repository easy to navigate? \n" + this.navigationOk + "\n";
		messageText += "Is the information/functionality logically organized? \n" + this.logicallyOrganized + "\n";
		
		messageText += "Did you have trouble loggin in? \n" + this.troubleLoginIn + "\n";
		messageText += "Are there any unclear questions/fields? \n" + this.unclearQuestions + "\n";
		messageText += "Are the any missing fields/tabs/information? \n" + this.missingFields + "\n";
		
		messageText += "Would you find this useful if it was available to your department? Who would find it useful? \n" + this.usefulIfDepartmentAvailable + "\n";
		messageText += "Does the website appear professional/trustworthy? \n" + this.websiteLooksProfessional + "\n";
		messageText += "Do you like the visual design of the website? \n" + this.goodVisualDesign + "\n";
		messageText += "Do you have any other suggestions/requests/complaints? \n" + this.generalSuggestions + "\n";
		
		RepositoryMailService mailservice = new RepositoryMailService(RepositoryMailService.ADMIN_GMAIL_ACCOUNT, EMAIL_SUBJECT, messageText);
		try{
			mailservice.send();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getUsersEmail() {
		return usersEmail;
	}

	public void setUsersEmail(String usersEmail) {
		this.usersEmail = usersEmail;
	}

	public String getAdminNotes() {
		return adminNotes;
	}

	public void setAdminNotes(String adminNotes) {
		this.adminNotes = adminNotes;
	}

	public String getAssociatedTicket() {
		return associatedTicket;
	}

	public void setAssociatedTicket(String associatedTicket) {
		this.associatedTicket = associatedTicket;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}

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
	
	

//	public Long getId() {
//		// TODO Auto-generated method stub
//		return this.id;
//	}
	
	//other methods
		
	
}
