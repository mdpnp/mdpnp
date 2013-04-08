package org.mdpnp.apps.testapp;

import javax.swing.plaf.metal.MetalLookAndFeel;

public class MDPnPLookAndFeel extends MetalLookAndFeel {

	@Override
	public String getName() {
		return "MDPnP Look And Feel";
	}

	@Override
	public String getID() {
		return "MDPNP";
	}

	@Override
	public String getDescription() {
		return "For MDPnP ICE Components";
	}


}
