package org.mdpnp.gip.ui.units;

public interface Units {
	String getName();
	
	String getAbbreviatedName();
	
	/**
	 * Convert to standard units
	 * @param l
	 * @return
	 */
	Double to(Double l);
	
	/**
	 * Convert from standard units
	 * @param l
	 * @return
	 */
	Double from(Double l);
}
