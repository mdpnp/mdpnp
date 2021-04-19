package org.mdpnp.apps.fxbeans;

public class DataQualityErrorObjectiveFxList extends AbstractFxList<ice.DataQualityErrorObjective, ice.DataQualityErrorObjectiveDataReader, DataQualityErrorObjectiveFx> {

	public DataQualityErrorObjectiveFxList(final String topicName) {
		super(topicName, ice.DataQualityErrorObjective.class, ice.DataQualityErrorObjectiveDataReader.class, ice.DataQualityErrorObjectiveTypeSupport.class, 
                ice.DataQualityErrorObjectiveSeq.class, DataQualityErrorObjectiveFx.class);
	}
}
