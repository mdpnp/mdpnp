package org.mdpnp.gip.ui.units;

/**
 * @author Jeff Plourde
 *
 */
public class RatioUnitsImpl implements RatioUnits {

	private final Units[] units;
	private final String name, abbreviatedName;
	
	
	public RatioUnitsImpl(Units... u) {
		this.units = u;
		if(u.length == 0) {
			name = "";
			abbreviatedName = "";
		} else {
			StringBuilder sbName = new StringBuilder(u[0].getName());
			StringBuilder sbAbbreviatedName = new StringBuilder(u[0].getAbbreviatedName());
			
			for(int i = 1; i < units.length; i++) {
				sbName.append("/").append(this.units[i].getName());
				sbAbbreviatedName.append("/").append(this.units[i].getAbbreviatedName());
			}
			this.name = sbName.toString();
			this.abbreviatedName = sbAbbreviatedName.toString();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAbbreviatedName() {
		return abbreviatedName;
	}

	@Override
	public Double to(Double l) {
		// TODO problematic semantics .. won't work robustly
		return units[0].to(l);
	}

	@Override
	public Double from(Double l) {
		// TODO problem
		return units[0].from(l);
	}

}
