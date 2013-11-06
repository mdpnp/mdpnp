package org.mdpnp.clinicalscenarios.server.scenario;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;
import org.mdpnp.clinicalscenarios.server.mailservice.RepositoryMailService;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
//import java.util.logging.Logger;



@SuppressWarnings("serial")
@Entity
public class ScenarioEntity implements java.io.Serializable {
	
//	private final static String SCN_STATUS_UNSUBMITTED = 	"unsubmitted";//created and/or modified, but not yet submitted for approval
//	private final static String SCN_STATUS_SUBMITTED = 		"submitted"; //submitted for approval, not yet revised nor approved 
//	private final static String SCN_STATUS_APPROVED = 		"approved"; //revised and approved
//	private final static String SCN_STATUS_REJECTED = 		"rejected"; //revised but not approved. Rejected for revision 
	
	@Id
	private Long id;
	protected Integer version = 1;
	
	@Index  //--Shouldn't we index the title if we are going to base our basic search on it?
	private String title; //title of the scenario
	@Index
	private String status; //status of the scenario (modified, submitted, ect...)
	@Index
	private String submitter;//creator or submitter of the scenario
	//TODO submitter should probably be a valueProxy of type UserInfo @Embebed UserInfo
	
	private Date creationDate = new Date();
	private Date modificationDate;   //timestamp of last action taken
	private String lastActionTaken;  //description of last action taken
	private String lastActionUser;   //name of the last user to perform an action with the scenario
	private String lockOwner; //name of user who has lock ownership over this scenario
	

	@OnSave
	void onPersist() {
	    version++;
	    modificationDate = new Date();
	}
	
	private BackgroundValue background = new BackgroundValue();
	private Hazards hazards = new Hazards();
	private Environments environments = new Environments();
	private Equipment equipment = new Equipment();
	private ProposedSolutionValue proposedSolution = new ProposedSolutionValue();
	private BenefitsAndRisksValue benefitsAndRisks = new BenefitsAndRisksValue();
	private References references = new References();
	private Acknowledgers acknowledgers = new Acknowledgers();
	private AssociatedTags associatedTags = new AssociatedTags();
	private FeedbackValue feedback = new FeedbackValue();//TICKET-197



	public Hazards getHazards() {
        return hazards;
    }

    public void setHazards(Hazards hazards) {
        this.hazards = hazards;
    }

    public Environments getEnvironments() {
        return environments;
    }

    public void setEnvironments(Environments environments) {
        this.environments = environments;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public ProposedSolutionValue getProposedSolution() {
        return proposedSolution;
    }

    public void setProposedSolution(ProposedSolutionValue proposedSolution) {
        this.proposedSolution = proposedSolution;
    }

    public BenefitsAndRisksValue getBenefitsAndRisks() {
        return benefitsAndRisks;
    }

    public void setBenefitsAndRisks(BenefitsAndRisksValue benefitsAndRisks) {
        this.benefitsAndRisks = benefitsAndRisks;
    }
      	
	public References getReferences() {
		return references;
	}

	public void setReferences(References references) {
		this.references = references;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		return id + " == " + title;
	}
	
	//creation date
	public Date getCreationDate(){
		return creationDate;
	}
	public void setCreationDate(Date d){
		creationDate = d;
	}
	
	//modification date
	public Date getModificationDate(){
		return modificationDate;
	}
	public void setModificationDate(Date d){
		modificationDate = d;
	}
	
	public ScenarioEntity() {
		//creationDate = new Date();
	}
	
	public BackgroundValue getBackground() {
	    return background;
	}
	public void setBackground(BackgroundValue background) {
        this.background = background;
    }
	
	protected Integer getVersion() {
		return version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
		
	public String getLastActionTaken() {
		return lastActionTaken;
	}

	public void setLastActionTaken(String lastActionTaken) {
		this.lastActionTaken = lastActionTaken;
	}

	public String getLastActionUser() {
		return lastActionUser;
	}

	public void setLastActionUser(String lastActionUser) {
		this.lastActionUser = lastActionUser;
	}

	public String getLockOwner() {
		return lockOwner;
	}

	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}
	
	public Acknowledgers getAcknowledgers() {
		return acknowledgers;
	}

	public void setAcknowledgers(Acknowledgers acknowledgers) {
		this.acknowledgers = acknowledgers;
	}

	public AssociatedTags getAssociatedTags() {
		return associatedTags;
	}

	public void setAssociatedTags(AssociatedTags associatedTags) {
		this.associatedTags = associatedTags;
	}
	
	public FeedbackValue getFeedback() {
		return feedback;
	}

	public void setFeedback(FeedbackValue feedback) {
		this.feedback = feedback;
	}

	//constructor
	public static ScenarioEntity create() /*throws Exception*/ {
		try{
		    ScenarioEntity s = new ScenarioEntity();
		    s.setStatus(ScenarioPanel.SCN_STATUS_UNSUBMITTED);//By default, pending of submission
		    s.setLastActionTaken("created new");
		    s.setLastActionUser(s.getSubmitter());
			//to ID the current user
		    UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
		    s.setSubmitter(user.getEmail());
	       // ofy().save().entity(s).now();//TICKET-81 To avoid persist empty SCN
	        return s;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Returns all the scenario IDs
	 * @return All the scenario IDs as a list of Long
	 */
	public static List<Long> findAllIds() {
	    QueryResultIterator<ScenarioEntity> itr = ofy().load().type(ScenarioEntity.class).iterator();
	    List<Long> ids = new ArrayList<Long>();
	    while(itr.hasNext()) {
	        ids.add(itr.next().getId());
	    }
	    return ids;
	}
	
	/**
	 * Finds a scenario by its unique ID
	 * @param id
	 * @return
	 */
	public static ScenarioEntity findById(Long id) {
	    return ofy().load().type(ScenarioEntity.class).id(id).now();
	}
	
		
	/**
	 * Auxiliary method that returns true if the first parameter contains any of the words included in the keyWordList 
	 * that is the second parameter
	 * @param line
	 * @param keyWordsList
	 * @return
	 */
	private static boolean containsStr(String line, List<String> keyWordsList){		
		for(String s : keyWordsList){
			if(line.toUpperCase().contains(s.toUpperCase())) return true;
		}
		return false;
	}
	
	/**
	 * Returns a list of scenario entities within the approved scenarios that contain in their fields title, background, proposed state,
	 * algorithm, process, benefits or risks any of the words included in the parameter 'keywords' (either a single word or more than one
	 * using whitespace as separator).
	 * @param keywords one or more strings (whitespace is the separator)
	 * @return
	 */
	public static List<ScenarioEntity> searchByKeywords(String keywords) {
		keywords = keywords.trim();
//		List<ScenarioEntity> approvedScenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);//starting point
		Set<String> states = new HashSet<String>();
		states.add(ScenarioPanel.SCN_STATUS_APPROVED);
		states.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
		states.add(ScenarioPanel.SCN_STATUS_MODIFIED);
		List<ScenarioEntity> approvedScenarios = searchByStatus(states);//TICKET-186
		List<String> keyWordsList = new ArrayList<String>();
		List<ScenarioEntity> result = new ArrayList<ScenarioEntity>();
		
		try{		
			if(keywords.indexOf(" ")>=0)
				keyWordsList = Arrays.asList(keywords.split("\\s+")); //TICKET-134. Multiword search
			else
				keyWordsList.add(keywords);
			//filter scenarios
			for(ScenarioEntity s : approvedScenarios) {
				//1- Check title
				String title = null == s.getTitle() ? "" : s.getTitle();
				//2- Check background
				String currentState = s.getBackground().getCurrentState()==null ? "" : s.getBackground().getCurrentState().toUpperCase();
				String proposedState = s.getBackground().getProposedState()==null ? "" : s.getBackground().getProposedState().toUpperCase();
				//3- Check Benefits and risks
				String benefits = s.getBenefitsAndRisks().getBenefits()==null ? "" : s.getBenefitsAndRisks().getBenefits().toUpperCase();
				String risks = s.getBenefitsAndRisks().getRisks()==null ? "" : s.getBenefitsAndRisks().getRisks().toUpperCase();
				//4- check Proposed Solution
				String algorithm = s.getProposedSolution().getAlgorithm()==null ? "" : s.getProposedSolution().getAlgorithm().toUpperCase();
				String process = s.getProposedSolution().getProcess()==null ? "" : s.getProposedSolution().getProcess().toUpperCase();
				
				if(containsStr(title, keyWordsList)	
						|| containsStr(currentState, keyWordsList) || containsStr(proposedState, keyWordsList)	
						|| containsStr(benefits, keyWordsList) || containsStr(risks, keyWordsList)	
						|| containsStr(algorithm, keyWordsList) || containsStr(process, keyWordsList))
					
					result.add(s);		
			}
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<ScenarioEntity>();
		}

		return result;
	}
	
//	/**
//	 * Searches scenarios using the keywords
//	 * @param keywords
//	 * @return
//	 */
//	All this legacy code has been left here to study different approaches for filtering results
//	public static List<ScenarioEntity> searchByKeywords(String keywords) {
////		List<ScenarioEntity> scenarios = findAllScenarios();//diego@mdpnp.org The basic search should be among APPROVED Scn, and not all of them
//		List<ScenarioEntity> scenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
//		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();
//		
//		String str = keywords.toUpperCase(); //String comparison in UPPERCASE ******
//		//Declare a keyword separator (WHITESPACE) and tokenize elements to use several Keywords
//		try{
//
//		for(ScenarioEntity s : scenarios) {
//			//1- Check title
//			String title = s.getTitle();
//			title = null == title ? "" : title;
//			if(title.toUpperCase().contains(str)) {
//				matchingScenarios.add(s);
//			}else{
//				//2- Check background
//				String currentState = s.getBackground().getCurrentState()==null ? "" : s.getBackground().getCurrentState().toUpperCase();
//				String proposedState = s.getBackground().getProposedState()==null ? "" : s.getBackground().getProposedState().toUpperCase();
//				if(currentState.toUpperCase().contains(str) || proposedState.toUpperCase().contains(str)){
//					matchingScenarios.add(s);
//				}else{
//					//3- Check Benefits and risks
//					String benefits = s.getBenefitsAndRisks().getBenefits()==null ? "" : s.getBenefitsAndRisks().getBenefits().toUpperCase();
//					String risks = s.getBenefitsAndRisks().getRisks()==null ? "" : s.getBenefitsAndRisks().getRisks().toUpperCase();
//					if(benefits.toUpperCase().contains(str) || risks.toUpperCase().contains(str)){
//						matchingScenarios.add(s);
//					}else{
//						//4- check Proposed Solution
//						String algorithm = s.getProposedSolution().getAlgorithm()==null ? "" : s.getProposedSolution().getAlgorithm().toUpperCase();
//						String process = s.getProposedSolution().getProcess()==null ? "" : s.getProposedSolution().getProcess().toUpperCase();
//						if(algorithm.toUpperCase().contains(str) || process.toUpperCase().contains(str))
//							matchingScenarios.add(s);
//					}
//				}
//				
//			}
//
//		}
//		
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return matchingScenarios;
//	}
	
	/**
	 * Searches for scenarios that have any of the keywords in the associated field
	 * @param sBackground keywords in background
	 * @param sProposed keywords in proposed state
	 * @return
	 */
	public static List<ScenarioEntity> searchByFilter_OrBehavior(String sBackground, String sProposed,
			String sProcess, String sAlgorithm, String sBenefits, String sRisks, String sTitle) {
//		List<ScenarioEntity> scenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
		Set<String> states = new HashSet<String>();
		states.add(ScenarioPanel.SCN_STATUS_APPROVED);
		states.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
		states.add(ScenarioPanel.SCN_STATUS_MODIFIED);
		List<ScenarioEntity> scenarios = searchByStatus(states);//TICKET-186
		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();

		try{

		for(ScenarioEntity s : scenarios) {
			//1- check title
			if(sTitle!=null && s.getTitle()!=null && s.getTitle().toUpperCase().contains(sTitle.toUpperCase())) {
				matchingScenarios.add(s);
			//2- check background
			}else if (sBackground!=null && s.getBackground().getCurrentState()!=null && s.getBackground().getCurrentState().toUpperCase().contains(sBackground.toUpperCase())){
				matchingScenarios.add(s);
			//3- check proposed state
			}else if(sProposed!=null && s.getBackground().getProposedState()!=null && s.getBackground().getProposedState().toUpperCase().contains(sProposed.toUpperCase())){
				matchingScenarios.add(s);
			//4- check process
			}else if(sProcess!=null && s.getProposedSolution().getProcess()!=null && s.getProposedSolution().getProcess().toUpperCase().contains(sProcess.toUpperCase())){
				matchingScenarios.add(s);
			//5- check Algorithm
			}else if(sAlgorithm!=null && s.getProposedSolution().getAlgorithm()!=null && s.getProposedSolution().getAlgorithm().toUpperCase().contains(sAlgorithm.toUpperCase())){
				matchingScenarios.add(s);
			//6- check benefits
			}else if(sBenefits!=null && s.getBenefitsAndRisks().getBenefits()!=null && s.getBenefitsAndRisks().getBenefits().toUpperCase().contains(sBenefits.toUpperCase())){
				matchingScenarios.add(s);
			//7- check risks
			}else if(sRisks!=null && s.getBenefitsAndRisks().getRisks()!=null && s.getBenefitsAndRisks().getRisks().toUpperCase().contains(sRisks.toUpperCase())){
				matchingScenarios.add(s);
			}
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return matchingScenarios;		
	}
		
	
	/**
	 * List Scn by status type 
	 * @param status status type
	 * @return
	 */
	public static List<ScenarioEntity> searchByStatus(String status){
		List<ScenarioEntity> listScn = ofy().load().type(ScenarioEntity.class).filter("status", status).list();
		return listScn;
	}
	
	/**
	 * Searches for scenarios in one of the states contained in the parameter 
	 * @param nStatus Set of status to filter
	 * @return
	 */
	public static List<ScenarioEntity> searchByStatus(Set<String> nStatus){
		List<ScenarioEntity> listAllScn = ofy().load().type(ScenarioEntity.class).list();
		List<ScenarioEntity> filteredList = new ArrayList<ScenarioEntity>();
		for(ScenarioEntity scn : listAllScn){
			String status = scn.getStatus();
			if(nStatus.contains(status)){
					filteredList.add(scn);
			}				
		}
		return filteredList;
	}
	
	/**
	 * Returns all the scenarios of the submitter provided by parameter
	 * @param submitter
	 * @return
	 */
	public static List<ScenarioEntity> searchScnBySubmitter(String submitter){
		List<ScenarioEntity> listScn = ofy().load().type(ScenarioEntity.class).filter("submitter", submitter).list();
		return listScn;
	}
	
	/**
	 *  Returns all the scenarios of the submitter provided by parameter
	 * @param submitter email of the user
	 * @param dateFrom filter for creation date starting date. Can be null
	 * @param dateUntil filter for creation date end date. Can be null
	 * @return
	 */
	public static List<ScenarioEntity> searchScnBySubmitter(String submitter, Date dateFrom, Date dateUntil){
		List<ScenarioEntity> listScn = ofy().load().type(ScenarioEntity.class).filter("submitter", submitter).list();
		if(null == dateFrom && null == dateUntil)
			return listScn;

		List<ScenarioEntity> filteredList = new ArrayList<ScenarioEntity>();		
		for(ScenarioEntity scn : listScn){
			/*
			 * 1- if none of filter dates is null --> between
			 * 2- if dateFrom is null --> before dateUntil
			 * 3- if dateUntil is null --> after dateFrom
			 */
			if(null != dateFrom && null != dateUntil){
				if(!scn.getCreationDate().before(dateFrom) && !scn.getCreationDate().after(dateUntil))
					filteredList.add(scn);
			}else if(null != dateFrom && !scn.getCreationDate().before(dateFrom)){
				filteredList.add(scn);
			}else if(null != dateUntil && !scn.getCreationDate().after(dateUntil)){
				filteredList.add(scn);
			}
		}
				
		return filteredList;
	}
	
	/**
	 * Returns all the scenarios
	 * @return
	 */
	public static List<ScenarioEntity> findAllScenarios() {
	    return ofy().load().type(ScenarioEntity.class).list();
	}
	
//	private static Logger logger = Logger.getLogger(Scenario.class.getName());
	/**
	 * Persists (saves) this entity in the GAE
	 * @return
	 */
	public ScenarioEntity persist() {
		ofy().save().entity(this).now();
	    return this;
	}
	
	/**
	 * Deletes this entity from the GAE
	 */
	public void remove() {
	    ofy().delete().entity(this).now();
	}
	

	
	//DAG option discarded
//	public ScenarioEntity submittScenario() {
//		this.status = SCN_STATUS_SUBMITTED;
//	    ofy().save().entity(this).now();
//	    return this;
//	}
	
	/**
	 * Enhanced persistence functionality to include capability to send methods
	 * @param toWho
	 * @param subject
	 * @param messageText
	 * @return
	 */
	public ScenarioEntity persistWithNotification(String toWho, String subject, String messageText){
		RepositoryMailService mailservice = new RepositoryMailService(toWho, subject, messageText);
		try{
			mailservice.send();
		}catch(Exception e){
			e.printStackTrace();
		}
		ofy().save().entity(this).now();
		return this;
	}
	
	/**
	 * Returns all the scenario whose creation date matches the filtering criteria provided by the parameters. 
	 * Both parameters can't be null.
	 * @param dateFrom
	 * @param dateUntil
	 * @return
	 */
	public static List<ScenarioEntity> searchByCreationDateRange(Date dateFrom, Date dateUntil){
		/*
		Query<ScenarioEntity> queryScn =  ofy().load().type(ScenarioEntity.class)
				.filter("status", ScenarioPanel.SCN_STATUS_APPROVED); //get only approved Scn
		
		 This doesn't do the trick for filtering, so we have to filter manually
		if(null != dateFrom)
			queryScn = queryScn.filter("creationDate >", dateFrom);	
		if(null!=dateUntil)
			queryScn = queryScn.filter("creationDate <", dateUntil);
		
		return queryScn.list();
		*/
		Set<String> states = new HashSet<String>();
		states.add(ScenarioPanel.SCN_STATUS_APPROVED);
		states.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
		states.add(ScenarioPanel.SCN_STATUS_MODIFIED);
		List<ScenarioEntity> scnList = searchByStatus(states);//TICKET-186
//		List<ScenarioEntity> scnList = ofy().load().type(ScenarioEntity.class)
//				.filter("status", ScenarioPanel.SCN_STATUS_APPROVED).list(); //get only approved Scn		
		List<ScenarioEntity> filteredList = new ArrayList<ScenarioEntity>();
		
		for(ScenarioEntity scn : scnList){
			/*
			 * 1- if none of filter dates is null --> between
			 * 2- if dateFrom is null --> before dateUntil
			 * 3- if dateUntil is null --> after dateFrom
			 */
			if(null != dateFrom && null != dateUntil){
				if(!scn.getCreationDate().before(dateFrom) && !scn.getCreationDate().after(dateUntil))
					filteredList.add(scn);
			}else if(null != dateFrom && !scn.getCreationDate().before(dateFrom)){
				filteredList.add(scn);
			}else if(null != dateUntil && !scn.getCreationDate().after(dateUntil)){
				filteredList.add(scn);
			}
		}
				
		return filteredList;
	
	}
	
	/**
	 * Locks the scenario for editing privilege
	 * @param username name of user requesting the lock
	 * @return
	 */
	public ScenarioEntity lock(String username) {
		if(submitter == null){
			this.lockOwner = username;
		    ofy().save().entity(this).now();
		}
	    return this;
	}
	
	/**
	 * Frees the scenario
	 * @return
	 */
	public ScenarioEntity unlock() {
		this.lockOwner = null;
		ofy().save().entity(this).now();
	    return this;
	}
	
	
	public ScenarioEntity submit(){
		ofy().save().entity(this).now();
		sendScenarioReceivedMail();
		return this;
	}
	
	/**
	 * Sends an email to warn administrators that they have a new Scenario submission
	 */
	private void sendScenarioReceivedMail(){
		String subject = "Scenario "+this.id+" submitted by "+this.submitter;
		
		String messageText = "Feedback accompanying this scenario \n";
		messageText += "How would you rate this website? \n" + this.feedback.getRateThisWebsite() + "\n";
		messageText += "Is the repository easy to navigate? \n" + this.feedback.getNavigationOk() + "\n";
		messageText += "Is the information/functionality logically organized? \n" + this.feedback.getLogicallyOrganized() + "\n";
		
		messageText += "Did you have trouble loggin in? \n" + this.feedback.getTroubleLoginIn() + "\n";
		messageText += "Are there any unclear questions/fields? \n" + this.feedback.getUnclearQuestions() + "\n";
		messageText += "Are the any missing fields/tabs/information? \n" + this.feedback.getMissingFields() + "\n";
		
		messageText += "Would you find this useful if it was available to your department? Who would find it useful? \n" 
				+ this.feedback.getUsefulIfDepartmentAvailable() + "\n";
		messageText += "Does the website appear professional/trustworthy? \n" + this.feedback.getWebsiteLooksProfessional() + "\n";
		messageText += "Do you like the visual design of the website? \n" + this.feedback.getGoodVisualDesign() + "\n";
		messageText += "Do you have any other suggestions/requests/complaints? \n" + this.feedback.getGeneralSuggestions() + "\n";
	
		sendMail(RepositoryMailService.ADMIN_GMAIL_ACCOUNT, subject, messageText);
	}
	
	/**
	 * Sends an email
	 * @param toWho
	 * @param subject
	 * @param message
	 */
	private void sendMail(String toWho, String subject, String message){
		RepositoryMailService mailservice = new RepositoryMailService(toWho, subject, message);
		try{
			mailservice.send();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
