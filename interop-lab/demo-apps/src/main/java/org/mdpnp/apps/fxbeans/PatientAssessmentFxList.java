package org.mdpnp.apps.fxbeans;


public class PatientAssessmentFxList extends AbstractFxList<himss.PatientAssessment, himss.PatientAssessmentDataReader, PatientAssessmentFx> {

    public PatientAssessmentFxList(final String topicName) {
        super(topicName, himss.PatientAssessment.class, himss.PatientAssessmentDataReader.class, himss.PatientAssessmentTypeSupport.class, himss.PatientAssessmentSeq.class, PatientAssessmentFx.class);
    }

}
