package org.mdpnp.clinicalscenarios.server.scenario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;

/**
 * 
 * @author diego@mdpnp.org
 *
 */
@SuppressWarnings("serial")
@Embed
public class References implements Serializable {
	
	private List<String> linkedRefenrences = new ArrayList<String>();

	public List<String> getLinkedRefenrences() {
		return linkedRefenrences;
	}

	public void setLinkedRefenrences(List<String> linkedRefenrences) {
		this.linkedRefenrences = linkedRefenrences;
	}


}
