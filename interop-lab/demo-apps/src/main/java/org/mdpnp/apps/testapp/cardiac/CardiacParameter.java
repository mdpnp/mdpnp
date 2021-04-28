package org.mdpnp.apps.testapp.cardiac;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;


/**
 * A simple object model to map all the properties we need for the cardiac arrest table.
 * @author simon
 *
 */
public class CardiacParameter {
	
	private SimpleStringProperty metricId=new SimpleStringProperty(this,"metricId","");
	private SimpleBooleanProperty accuracy=new SimpleBooleanProperty(this,"accuracy");
	private SimpleBooleanProperty completeness=new SimpleBooleanProperty(this,"completeness");
	private SimpleBooleanProperty currentness=new SimpleBooleanProperty(this,"currentness");
	private SimpleBooleanProperty credibility=new SimpleBooleanProperty(this,"credibility");
	private SimpleBooleanProperty consistency=new SimpleBooleanProperty(this,"consistency");
	
	public String getMetricId() {
		return metricId.get();
	}

	public void setMetricId(String metricId) {
		this.metricId.set(metricId);
	}
	
	public SimpleBooleanProperty accuracyProperty() {
		return accuracy;
	}

	public void setAccuracy(boolean accuracy) {
		this.accuracy.set(accuracy);
	}

	public SimpleBooleanProperty completenessProperty() {
		return completeness;
	}

	public void setCompleteness(boolean completeness) {
		this.completeness.set(completeness);
	}

	public SimpleBooleanProperty currentnessProperty() {
		return currentness;
	}

	public void setCurrentness(boolean currentness) {
		this.currentness.set(currentness);
	}

	public SimpleBooleanProperty credibilityProperty() {
		return credibility;
	}

	public void setCredibility(boolean credibility) {
		this.credibility.set(credibility);
	}

	public SimpleBooleanProperty consistencyProperty() {
		return consistency;
	}

	public void setConsistency(boolean consistency) {
		this.consistency.set(consistency);
	}

	public CardiacParameter() {
		
	}
	
	public CardiacParameter(String metricId, boolean accuracy, boolean completeness, boolean currentness,
			boolean credibility, boolean consistency) {
		setMetricId(metricId);
		setAccuracy(accuracy);
		setCompleteness(completeness);
		setCurrentness(currentness);
		setCredibility(credibility);
		setConsistency(consistency);
	}
	

}
