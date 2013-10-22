package org.mdpnp.clinicalscenarios.server.scenario;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.annotation.Embed;
/**
 * 
 * @author diego@mdpnp.org
 * 
 * <p>This class represents the acknowledgers of an scenario (people who
 * clicked on the "like" button). We use a Set of IDs, so we have the ID
 * of the users instead of their email information sent to the browser and 
 * also to ease the comparison of items (check if a user has already acknowledged) the scenario.
 * 
 * <p>This feature is the implementation of TICKET-163
 *
 */
@SuppressWarnings("serial")
@Embed
public class Acknowledgers implements Serializable{
	
	private Set<String> acknowledgersIDs = new HashSet<String>(); //set with the IDs of the users who clicked the button to ack/like this scenario 
	
	public Set<String> getAcknowledgersIDs() {
		return acknowledgersIDs;
	}

	public void setAcknowledgersIDs(Set<String> acknowledgersIDs) {
		this.acknowledgersIDs = acknowledgersIDs;
	}

}
