package org.mdpnp.clinicalscenarios.server.scenario;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;

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
	
	private final static String SCN_STATUS_UNSUBMITTED = 	"unsubmitted";//created and/or modified, but not yet submitted for approval
	private final static String SCN_STATUS_SUBMITTED = 		"submitted"; //submitted for approval, not yet revised nor approved 
	private final static String SCN_STATUS_APPROVED = 		"approved"; //revised and approved
	private final static String SCN_STATUS_REJECTED = 		"rejected"; //revised but not approved. Rejected for revision 
	
	@Id
	private Long id;
	
	//XXX @ Index Shouldn't we index the title if we are going to base our basic search on it?
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
		
	public static ScenarioEntity create() {
	    ScenarioEntity s = new ScenarioEntity();
	    s.setStatus(SCN_STATUS_UNSUBMITTED);//By default, pending of submission
		//to ID the current user
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();//final? What happens when we change the user?
	    s.setSubmitter(user.getEmail());
        ofy().save().entity(s).now();
        return s;
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
	
	public static List<ScenarioEntity> searchByKeywords(String keywords) {
		List<ScenarioEntity> scenarios = findAllScenarios();
		List<ScenarioEntity> matchingScenarios = new ArrayList<ScenarioEntity>();
		
		String str = keywords.toUpperCase();
		
		for(ScenarioEntity s : scenarios) {
			String title = s.getTitle();
			title = null == title ? "" : title;
			if(title.toUpperCase().contains(str)) {
				matchingScenarios.add(s);
//				logger.info("Search RefId:"+System.identityHashCode(s)+" Id:"+s.getId()+" Title:"+s.getTitle());
			}
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
	
}
