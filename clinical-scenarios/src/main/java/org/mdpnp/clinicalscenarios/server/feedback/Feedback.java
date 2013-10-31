package org.mdpnp.clinicalscenarios.server.feedback;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
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
	
	@Id
	private Long id; //id for the GAE
	private Integer version = 1;
	
	private String usersEmail; //email ID of the user who submitted this feedback
	private String usersFeedback; //text of users' feedback
	
	private String adminNotes; //text or notes from admin/developers about feedback
	private String associatedTicket; //associated ticket on Sourceforge for this feedback/issue, in case there's one
	
	private Boolean visited = false; //indicated that this messaged has been read/noted/taken into account
	
	private Date feedbackCreationDate  = new Date(); 	//creation date of the feedback text
	private Date noteCreationDate;	//creation date of the admin/developer note
	private Date noteModificationDate;	//modification date for the note created by the admin/developer
	
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
	    return this;
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

	public String getUsersFeedback() {
		return usersFeedback;
	}

	public void setUsersFeedback(String usersFeedback) {
		this.usersFeedback = usersFeedback;
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

//	public Long getId() {
//		// TODO Auto-generated method stub
//		return this.id;
//	}
	
	//other methods
		
	
}
