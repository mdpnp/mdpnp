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
 * @author diego@mdpnp.org 
 * 
 * Class to implement a KEYWORD that will Tag or Label
 * the clinical scenarios  <p>
 * since 09/25/2013, the tags are going to be just a keyword, so we no longer need the description field 
 *
 */
@SuppressWarnings("serial")
@Entity
public class Tag implements Serializable {
	
	@Id
	private Long id; //id for the GAE
	private Integer version =1;
	
	@OnSave
	void onPersist() {
	    version++;
	}
	
	@Index
	private String name; // name (identifier) of the tag
	
//	private String description; //description associated to the tag
	
	//TODO Add auditing info?  creation date/ user and modification date/user
	
	//cons
	public Tag(){};
	
	public Tag(String name, String descrp){
		this.name = name;
//		this.description = descrp;
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

//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	public static Tag create() {
	    Tag t = new Tag();
//        ofy().save().entity(t).now();
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
