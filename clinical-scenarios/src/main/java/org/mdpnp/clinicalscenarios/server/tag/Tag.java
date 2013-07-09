package org.mdpnp.clinicalscenarios.server.tag;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

/**
 * 
 * @author dalonso@mdpnp.org 
 * 
 * Class to implement a KEYWORD that will Tag or Label
 * the clincial scenarios 
 *
 */
@SuppressWarnings("serial")
@Entity
public class Tag implements Serializable {
	
	@Id
	private Long id; //id for the GAE
	private int version =1;
	
	@Index
	private String name; // name (identifier) of the tag
	
	private String description; //description associated to the tag
	
	//TODO Add auditing info?  creation date/ user and modification date/user
	
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
	
	
	public static Tag create() {
	    Tag t = new Tag();
        ofy().save().entity(t).now();
        return t;
	}
	
	/**
	 * Persist / save the item
	 * @return
	 */
	public Tag persist() {
	    ofy().save().entity(this).now();
	    return this;
	}
	@OnSave
	void onPersist() {
	    version++;
	}
	
	/**
	 * Find all tags
	 * @return
	 */
	public static List<Tag> findAll() {
		
	    return ofy().load().type(Tag.class).list();	    
	}
	
	/**
	 * removes the current tag
	 */
	public void remove() {
	    ofy().delete().entity(this).now();
	}
	

}
