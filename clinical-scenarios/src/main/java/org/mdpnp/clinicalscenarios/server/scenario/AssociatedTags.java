package org.mdpnp.clinicalscenarios.server.scenario;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.annotation.Embed;

/**
 * 
 * @author diego@mdpnp.org
 * 
 * <p>This class contains a Set with the keywords tagged (associated) to a scenario.
 * <p> This feature is the implementation of TICKET-157
 *
 */
//@SuppressWarnings("serial")
@Embed
public class AssociatedTags implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Set<String> associatedTagNames = new HashSet<String>(); //set of tag Names associated to the scenario
	
	public Set<String> getAssociatedTagNames() {
		return associatedTagNames;
	}
	
	public void setAssociatedTagNames(Set<String> associatedTagNames) {
		this.associatedTagNames = associatedTagNames;
	}
	
	/**
	 * XXX
	 * We could have different structures for the tags that a user proposes.
	 * We could have a Hashtable (Key =tagName, Value=Set wit userIDs), so we
	 * have a structure with all the keywords users suggest to tag to a scenario.
	 * The "number of votes" for each tag would be the size of the set.
	 * Still, it would be something tricky anyway to tag a keyword depending on votes, 
	 * since each scenario can have different amount of visitors and scenarios could have
	 * different amount of visitors along the lifespan of the repository.
	 * 
	 * UPDATE: have in mind the freaking restriction of the TRANSPORTABLE TYPES.
	 * Types used as proxy properties must be primitives, boxed primitives, sets/list, 
	 * or entity/value object
	 * 
	 */
	
	

}
