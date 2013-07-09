package org.mdpnp.clinicalscenarios.server.tag;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.io.Serializable;
import java.util.List;

import org.mdpnp.clinicalscenarios.server.user.UserInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * @author dalonso@mdpnp.org 
 * 
 * Class to implement a KEYWORD that will Tag or Label
 * the clincial scenarios 
 *
 */
@Entity
public class Tag implements Serializable {
	
	@Id
	private Long id; //id for the GAE
	private int version =1;
	
	@Index
	private String name; // name (identifier) of the tag
	
	private String description; //description associated to the tag
	
	//cons
	public Tag(){};
	
	public Tag(String name, String descrp){
		this.name = name;
		this.description = descrp;
	}

	//getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * Persist / save the item
	 * @return
	 */
	public Tag persist() {
	    ofy().save().entity(this).now();
	    return this;
	}
	
	/**
	 * Find all tags
	 * @return
	 */
	public static List<Tag> findAll() {
		
	    return ofy().load().type(Tag.class).list();//XXX is this ok? argument of Type? do I need to id the user?
	    /**
	     * 	UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		UserInfo ui = null;
		
		if(null == user) {
			// Create a non-persisted temporary object
			// client will find a null userid and null email and 
			// know to only examine login and logout urls
			ui = new UserInfo();
		} else {
		    ui = ofy().load().type(UserInfo.class).id(user.getUserId()).now();

			if(null == ui) {
				// This user is authenticated but we do not have any information about them
				ui = new UserInfo();
				ui.setUserId(user.getUserId());
				ui.setEmail(user.getEmail());
				ofy().save().entity(ui).now();
			} else {
				// This is an authenticated user that we know
			}
		}
		ui.setAdmin(userService.isUserLoggedIn() && userService.isUserAdmin());
		ui.setLoginURL(userService.createLoginURL(url));
		ui.setLogoutURL(userService.createLogoutURL(url));
		
		return ui;
	     */
	    
	}
	

}
