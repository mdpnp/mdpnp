package org.mdpnp.clinicalscenarios.client.tag.comparator;

import java.util.Comparator;

import org.mdpnp.clinicalscenarios.client.tag.TagProxy;

public class TagComparator implements Comparator<TagProxy> {

		public int compare(TagProxy o1, TagProxy o2) {
//			o1.getName().compareToIgnoreCase(o2.getName());
			return o1.getName().toLowerCase().compareToIgnoreCase(o2.getName().toLowerCase());
		}
		
	}
