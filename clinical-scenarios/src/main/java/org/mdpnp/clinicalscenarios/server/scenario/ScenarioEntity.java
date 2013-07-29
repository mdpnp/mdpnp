package org.mdpnp.clinicalscenarios.server.scenario;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;
import org.mdpnp.clinicalscenarios.server.mailservice.RepositoryMailService;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.client.Window;
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
	
	//XXX @ Index  --Shouldn't we index the title if we are going to base our basic search on it?
	private String title; //title of the scenario
	@Index
	private String status; //status of the scenario (modified, submitted, ect...)
	@Index
	private String submitter;//creator or submitter of the scenario
	//TODO submitter should probably be a valueProxy of type UserInfo @Embebed UserInfo
		  
    protected int version = 1;
	
	@OnSave
	void onPersist() {
	    version++;
	}
	
	private BackgroundValue background = new BackgroundValue();
	private Hazards hazards = new Hazards();
	private Environments environments = new Environments();
	private Equipment equipment = new Equipment();
	private ProposedSolutionValue proposedSolution = new ProposedSolutionValue();
	private BenefitsAndRisksValue benefitsAndRisks = new BenefitsAndRisksValue();
	



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
	
	public ScenarioEntity() {
		
	}
	
	public BackgroundValue getBackground() {
	    return background;
	}
	public void setBackground(BackgroundValue background) {
        this.background = background;
    }
		
	public static ScenarioEntity create() /*throws Exception*/ {
		try{
		    ScenarioEntity s = new ScenarioEntity();
		    s.setStatus(ScenarioPanel.SCN_STATUS_UNSUBMITTED);//By default, pending of submission
			//to ID the current user
		    UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
		    s.setSubmitter(user.getEmail());
	        ofy().save().entity(s).now();
	        return s;
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Long> findAllIds() {
	    QueryResultIterator<ScenarioEntity> itr = ofy().load().type(ScenarioEntity.class).iterator();
	    List<Long> ids = new ArrayList<Long>();
	    while(itr.hasNext()) {
	        ids.add(itr.next().getId());
	    }
	    return ids;
	}
	
	public static ScenarioEntity findById(Long id) {
	    return ofy().load().type(ScenarioEntity.class).id(id).now();
	}
	
	
	/* XXX diego@mdpnp.org
	 * One concern about the methods herein published for searches: Am I bringing too much business logic to this point?
	 * Should the be simpler searches that I would filter later?
	 */
	
	/**
	 * Searches scenarios using the keywords
	 * @param keywords
	 * @return
	 */
	public static List<ScenarioEntity> searchByKeywords(String keywords) {
//		List<ScenarioEntity> scenarios = findAllScenarios();//XXX diego@mdpnp.org The basic search should be among APPROVED Scn, and not all of them
		List<ScenarioEntity> scenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();
		
		String str = keywords.toUpperCase(); //String comparison in UPPERCASE ******
		//TODO Declare a keyword separator (WHITESPACE) and tokenize elements to use several Keywords
		try{

		for(ScenarioEntity s : scenarios) {
			//1- Check title
			String title = s.getTitle();
			title = null == title ? "" : title;
			if(title.toUpperCase().contains(str)) {
				matchingScenarios.add(s);
			}else{
				//2- Check background
				String currentState = s.getBackground().getCurrentState()==null ? "" : s.getBackground().getCurrentState().toUpperCase();
				String proposedState = s.getBackground().getProposedState()==null ? "" : s.getBackground().getProposedState().toUpperCase();
				if(currentState.toUpperCase().contains(str) || proposedState.toUpperCase().contains(str)){
					matchingScenarios.add(s);
				}else{
					//3- Check Benefits and risks
					String benefits = s.getBenefitsAndRisks().getBenefits()==null ? "" : s.getBenefitsAndRisks().getBenefits().toUpperCase();
					String risks = s.getBenefitsAndRisks().getRisks()==null ? "" : s.getBenefitsAndRisks().getRisks().toUpperCase();
					if(benefits.toUpperCase().contains(str) || risks.toUpperCase().contains(str)){
						matchingScenarios.add(s);
					}else{
						//4- check Proposed Solution
						String algorithm = s.getProposedSolution().getAlgorithm()==null ? "" : s.getProposedSolution().getAlgorithm().toUpperCase();
						String process = s.getProposedSolution().getProcess()==null ? "" : s.getProposedSolution().getProcess().toUpperCase();
						if(algorithm.toUpperCase().contains(str) || process.toUpperCase().contains(str))
							matchingScenarios.add(s);
					}
				}
				
			}

		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return matchingScenarios;
	}
	
	/**
	 * Searches for scenarios that have any of the keywords in the associated field
	 * @param sBackground keywords in background
	 * @param sProposed keywords in proposed state
	 * @return
	 */
	public static List<ScenarioEntity> searchByFilter_OrBehavior(String sBackground, String sProposed,
			String sProcess, String sAlgorithm, String sBenefits, String sRisks) {
		List<ScenarioEntity> scenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();

		String sTitle = null;
//		String sBackground = keywords.containsKey("background")? ((String)keywords.get("background")).toUpperCase():null;
//		String sProposed = keywords.containsKey("proposed")? ((String)keywords.get("proposed")).toUpperCase():null;
		try{

		for(ScenarioEntity s : scenarios) {
			//1- Check title
			if(sTitle!=null && s.getTitle()!=null && s.getTitle().toUpperCase().contains(sTitle)) {
				matchingScenarios.add(s);
			}else if (sBackground!=null && s.getBackground().getCurrentState()!=null && s.getBackground().getCurrentState().toUpperCase().contains(sBackground.toUpperCase())){
				matchingScenarios.add(s);
			}else if(sProposed!=null && s.getBackground().getProposedState()!=null && s.getBackground().getProposedState().toUpperCase().contains(sProposed.toUpperCase())){
				matchingScenarios.add(s);
			}
			
			//TODO Complete this method w/ the search in the other fields
			
//			{
//				//2- Check background
//				String currentState = s.getBackground().getCurrentState()==null ? "" : s.getBackground().getCurrentState();
//				String proposedState = s.getBackground().getProposedState()==null ? "" : s.getBackground().getProposedState();
//				if(currentState.toUpperCase().contains(str) || proposedState.toUpperCase().contains(str)){
//					matchingScenarios.add(s);
//				}else{
//					//3- Check Benefits and risks
//					String benefits = s.getBenefitsAndRisks().getBenefits()==null ? "" : s.getBenefitsAndRisks().getBenefits();
//					String risks = s.getBenefitsAndRisks().getRisks()==null ? "" : s.getBenefitsAndRisks().getRisks();
//					if(benefits.toUpperCase().contains(str) || risks.toUpperCase().contains(str)){
//						matchingScenarios.add(s);
//					}else{
//						//4- check Proposed Solution
//						String algorithm = s.getProposedSolution().getAlgorithm()==null ? "" : s.getProposedSolution().getAlgorithm();
//						String process = s.getProposedSolution().getProcess()==null ? "" : s.getProposedSolution().getProcess();
//						if(algorithm.toUpperCase().contains(str) || process.toUpperCase().contains(str))
//							matchingScenarios.add(s);
//					}
//				}
//				
//			}

		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return matchingScenarios;
		
	}
	
	/**
	 * Searches for scenarios that have all the provided keywords in the appropriate field 
	 * @param sBackground
	 * @param sProposed
	 * @return
	 */
	public static List<ScenarioEntity> searchByFilter_AndBehavior(String sBackground, String sProposed,
			String sProcess, String sAlgorithm, String sBenefits, String sRisks) {
		List<ScenarioEntity> scenarios = searchByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();

		String sTitle = null;
//		String sBackground = keywords.containsKey("background")? ((String)keywords.get("background")).toUpperCase():null;
//		String sProposed = keywords.containsKey("proposed")? ((String)keywords.get("proposed")).toUpperCase():null;
		try{
//TODO Convertir este codigo en hermosos IF anidados para conseguir la operacion AND
		for(ScenarioEntity s : scenarios) {
			//1- Check title
			if(sTitle!=null && s.getTitle()!=null && s.getTitle().toUpperCase().contains(sTitle)) {
				matchingScenarios.add(s);
			}else if (sBackground!=null && s.getBackground().getCurrentState()!=null && s.getBackground().getCurrentState().toUpperCase().contains(sBackground.toUpperCase())){
				matchingScenarios.add(s);
			}else if(sProposed!=null && s.getBackground().getProposedState()!=null && s.getBackground().getProposedState().toUpperCase().contains(sProposed.toUpperCase())){
				matchingScenarios.add(s);
			}
			
//			{
//				//2- Check background
//				String currentState = s.getBackground().getCurrentState()==null ? "" : s.getBackground().getCurrentState();
//				String proposedState = s.getBackground().getProposedState()==null ? "" : s.getBackground().getProposedState();
//				if(currentState.toUpperCase().contains(str) || proposedState.toUpperCase().contains(str)){
//					matchingScenarios.add(s);
//				}else{
//					//3- Check Benefits and risks
//					String benefits = s.getBenefitsAndRisks().getBenefits()==null ? "" : s.getBenefitsAndRisks().getBenefits();
//					String risks = s.getBenefitsAndRisks().getRisks()==null ? "" : s.getBenefitsAndRisks().getRisks();
//					if(benefits.toUpperCase().contains(str) || risks.toUpperCase().contains(str)){
//						matchingScenarios.add(s);
//					}else{
//						//4- check Proposed Solution
//						String algorithm = s.getProposedSolution().getAlgorithm()==null ? "" : s.getProposedSolution().getAlgorithm();
//						String process = s.getProposedSolution().getProcess()==null ? "" : s.getProposedSolution().getProcess();
//						if(algorithm.toUpperCase().contains(str) || process.toUpperCase().contains(str))
//							matchingScenarios.add(s);
//					}
//				}
//				
//			}

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
	
	public static List<ScenarioEntity> searchScnBySubmitter(String submitter){
		List<ScenarioEntity> listScn = ofy().load().type(ScenarioEntity.class).filter("submitter", submitter).list();
		return listScn;
	}
	
	public static List<ScenarioEntity> findAllScenarios() {
	    return ofy().load().type(ScenarioEntity.class).list();
	}
	
//	private static Logger logger = Logger.getLogger(Scenario.class.getName());
	public ScenarioEntity persist() {
	    ofy().save().entity(this).now();
	    return this;
	}
	
	public void remove() {
	    ofy().delete().entity(this).now();
	}
	
	protected int getVersion() {
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
	
}
