package org.mdpnp.apps.fxbeans;

public class SafetyFallbackObjectiveFxList extends AbstractFxList<ice.SafetyFallbackObjective, ice.SafetyFallbackObjectiveDataReader, SafetyFallbackObjectiveFx> {

	public SafetyFallbackObjectiveFxList(final String topicName) {
		super(topicName, ice.SafetyFallbackObjective.class, ice.SafetyFallbackObjectiveDataReader.class, ice.SafetyFallbackObjectiveTypeSupport.class, 
                ice.SafetyFallbackObjectiveSeq.class, SafetyFallbackObjectiveFx.class);
	}
}
