package org.mdpnp.apps.fxbeans;

import himss.AssessmentEntry;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.*;

import org.mdpnp.apps.fxbeans.AbstractFx;

import com.rti.dds.subscription.SampleInfo;

import org.mdpnp.apps.fxbeans.Updatable;

public class PatientAssessmentFx extends AbstractFx<himss.PatientAssessment> implements Updatable<himss.PatientAssessment> {
    public PatientAssessmentFx() {
    }

    private StringProperty operator_id;

    public StringProperty operator_idProperty() {
        if(null == this.operator_id) {
            this.operator_id = new SimpleStringProperty();
        }
        return operator_id;
    }

    public String getOperator_id() {
        return operator_idProperty().get();
    }

    public void setOperator_id(String operator_id) {
        this.operator_idProperty().set(operator_id);
    }

    private ObjectProperty<Date> date_and_time;

    public ObjectProperty<Date> date_and_timeProperty() {
        if(null == this.date_and_time) {
            this.date_and_time = new SimpleObjectProperty<Date>();
        }
        return date_and_time;
    }

    public Date getDate_and_time() {
        return date_and_timeProperty().get();
    }

    public void setDate_and_time(Date date_and_time) {
        this.date_and_timeProperty().set(date_and_time);
    }

    private ObjectProperty<List<Map.Entry<String,String>>> assessments;

    public ObjectProperty<List<Map.Entry<String,String>>> assessmentsProperty() {
        if(null == this.assessments) {
            this.assessments = new SimpleObjectProperty<List<Map.Entry<String,String>>>();
        }
        return assessments;
    }

    public List<Map.Entry<String,String>> getAssessments() {
        return assessmentsProperty().get();
    }

    public void setAssessments(List<Map.Entry<String,String>> assessments) {
        this.assessmentsProperty().set(assessments);
    }

    @Override
    public void update(himss.PatientAssessment v, SampleInfo s) {
        setOperator_id(v.operator_id);
        setDate_and_time(new Date(v.date_and_time.seconds*1000L+v.date_and_time.nanoseconds/1000000L));
        List<Map.Entry<String,String>> assessments = new ArrayList<Map.Entry<String,String>>();
        for(int i = 0; i < v.assessments.userData.size(); i++) {
            himss.AssessmentEntry ae = (AssessmentEntry) v.assessments.userData.get(i);
            assessments.add(new AbstractMap.SimpleEntry<String,String>(ae.name, ae.value));
        }
        setAssessments(assessments);
        super.update(v, s);
    }
}
