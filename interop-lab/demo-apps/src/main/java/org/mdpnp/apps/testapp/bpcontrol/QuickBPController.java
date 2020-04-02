package org.mdpnp.apps.testapp.bpcontrol;

import ice.BPObjectiveDataWriter;
import ice.BPObjectiveTypeSupport;

public class QuickBPController {

	public QuickBPController() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ice.BPObjective objective=new ice.BPObjective();
		objective.changeBy=10f;
		objective.unique_device_identifier="";
		
		//BPObjectiveDataWriter writer=new BPObjectiveDataWriter();

	}

}
