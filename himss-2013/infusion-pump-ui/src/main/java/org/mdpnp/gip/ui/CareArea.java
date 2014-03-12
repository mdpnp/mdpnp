package org.mdpnp.gip.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Plourde
 *
 */
public class CareArea {
	private final String name;
	private final List<DrugEntry> drugEntries = new ArrayList<DrugEntry>();
	
	public List<DrugEntry> getDrugEntries() {
		return drugEntries;
	}
	
	public CareArea(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
//		return Util.toString(this);
	}
}
