
package org.mdpnp.apps.testapp.cardiac;

import org.mdpnp.apps.fxbeans.DataQualityErrorObjectiveFxList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Observable;
import java.util.stream.IntStream;

import org.mdpnp.apps.fxbeans.DataQualityErrorObjectiveFx;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SafetyFallbackObjectiveFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;
import javafx.collections.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import com.rti.dds.subscription.Subscriber;

import ice.DataQualityAttributeType;
import ice.FlowRateObjectiveDataWriter;

import javafx.beans.property.SimpleBooleanProperty;

public class CardiacArrestApplication {

	private DeviceListModel deviceListModel;
	private NumericFxList numericList;
	private SampleArrayFxList sampleList;
	private DataQualityErrorObjectiveFxList dqeList;
	
	@FXML
	private ObservableList<CardiacParameter> tableRows;
	
	@FXML
	private Label ecgHrLabel;
	
	@FXML
	private Label piLabel;
	
	@FXML
	private Label systolicLabel;
	
	@FXML
	private Label inversionLabel;
	
	/**
	 * the list of metrics for ECG
	 */
	private static final ArrayList<String> ECG_METRICS = new ArrayList<String>();
	/**
	 * the list of metrics for PPG (pleth) e.g. pulse oximetry.
	 */
	private static final ArrayList<String> PPG_METRICS = new ArrayList<String>();
	/**
	 * the list of metrics for invasive blood pressure.
	 */
	private static final ArrayList<String> IBP_METRICS = new ArrayList<String>();
	
	/**
	 * The number of samples to be used for ECG HR average
	 */
	private static final int ECG_SAMPLE_COUNT=30;
	
	/**
	 * The cardiac inversion threshold value for SVT.
	 */
	private static final float SVT_THRESHOLD=150;
	
	/**
	 * The cardiac inversion threshold value for Systolic BP.
	 */
	private static final float SYS_THRESHOLD=80;
	
	/**
	 * The array of ECH HR values to be averaged.
	 */
	private final float[] ECG_HR_AVERAGE=new float[ECG_SAMPLE_COUNT];
	
	/**
	 * The number of ECG samples we have seen.  Once more than ECG_SAMPLE_COUNT, we can do an average
	 */
	private int ecgHrCount=0;
	
	/**
	 * Indicator if we have seen enough ECG samples to do an average.  We could probably just use ecgHrCount,
	 * but in theory, it could wrap round.  However, that would be 24855 days... 
	 */
	private boolean enoughSamplesForAverage;
	
	/**
	 * An observable for the float value for easy display/refresh.
	 */
	private SimpleFloatProperty ecgAverageProperty;
	
	/**
	 * A change listener that calculates the average ECG HR.  We have a class level field for it
	 * so that we can easily remove it later.
	 */
	private ChangeListener<Date> averageListener;
	
	/**
	 * A change listener that calculates the average ECG HR.  We have a class level field for it
	 * so that we can easily remove it later.
	 */
	private ChangeListener<Float> systolicListener;
	
	/**
	 * A floating point value for the perfusion index.  Unclear how to obtain this value for now.
	 * Presumably if available via a device, it would be a metric and we could have attach a listener
	 * to it. 
	 */
	private float perfusionIndex;
	
	/**
	 * An observable for whether or not pleth is present.
	 */
	private SimpleBooleanProperty plethPresentProperty;
	
	/**
	 * An observable for whether or not to advise cardiac inversion.
	 */
	private SimpleStringProperty okForCardiacInversionProperty;
	
	private NumericFx systolicNumeric;
	
	public CardiacArrestApplication() {
		ECG_METRICS.add("MDC_ECG_LEAD_I");
		ECG_METRICS.add("MDC_ECG_LEAD_II");
		ECG_METRICS.add("MDC_ECG_LEAD_III");
		ECG_METRICS.add("MDC_ECG_HEART_RATE");
		
		PPG_METRICS.add("MDS_PULSE_OXIM_PLETH");
		
		IBP_METRICS.add("MDC_PRESS_BLD_ART_ABP_SYS");
		IBP_METRICS.add("MDC_PRESS_BLD_ART_ABP_DIA");
		IBP_METRICS.add("MDC_PRESS_BLD");	//AVERAGE?
		
		ecgAverageProperty=new SimpleFloatProperty();
		plethPresentProperty=new SimpleBooleanProperty(true);
		okForCardiacInversionProperty=new SimpleStringProperty("No");
		
	}

	public void set(ApplicationContext parentContext, DeviceListModel deviceListModel, NumericFxList numericList,
			SampleArrayFxList sampleList, SafetyFallbackObjectiveFxList safetyFallbackObjectiveList,
			MDSHandler mdsHandler, VitalModel vitalModel,
			Subscriber subscriber, EMRFacade emr, DataQualityErrorObjectiveFxList dataQualityErrorObjectiveList) {
		this.deviceListModel=deviceListModel;
		this.numericList=numericList;
		this.sampleList=sampleList;
		this.dqeList=dataQualityErrorObjectiveList;
		
		//CardiacParameter param=new CardiacParameter("CODE_ROW", true, false, true, true, true);
		//tableRows.add(param);
		addListeners();
		
		ecgHrLabel.textProperty().bind(Bindings.format("%d second average ECG HR %s", ECG_SAMPLE_COUNT, ecgAverageProperty));
		piLabel.textProperty().bind(Bindings.format("Pleth present: %s", plethPresentProperty ));
		inversionLabel.textProperty().bind(Bindings.format("Perform cardiac inversion: %s", okForCardiacInversionProperty));
		
		/*
		 * For the time being, we will just use a single listener to the other properties, and when that changes,
		 * we will calculate whether to do cardiac inversion.  Because, after the first 30 seconds, we can assume
		 * that the ecgAverageProperty will be updated once per second.  That's a good driver for evaluating the
		 * rest of the conditions.
		 */
		ecgAverageProperty.addListener( new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if( newValue.floatValue() > SVT_THRESHOLD && plethPresentProperty.get() && systolicNumeric.getValue() < SYS_THRESHOLD) {
					okForCardiacInversionProperty.set("YES");
				}
				
			}
			
		});

	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		// TODO Auto-generated method stub
		
	}

	public void activate() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private void addListeners() {
		numericList.addListener(new ListChangeListener<NumericFx>() {

			@Override
			public void onChanged(Change<? extends NumericFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach( n -> {
						String metricId=n.getMetric_id();
						if( ! interested(metricId) ) {
							return;
						}
						boolean found=false;
						for(int i=0;i<tableRows.size();i++) {
							if(tableRows.get(i).getMetricId().equals(metricId)) {
								found=true;
								break;
							}
						}
						if(!found) {
							//Metric not there.  Add it
							CardiacParameter param=new CardiacParameter(metricId, true, true, true, true, true);
							tableRows.add(param);
						}
						
						if(metricId.equals("MDC_ECG_HEART_RATE")) {
							addAverageListener(n);
						}
						
						if(metricId.equals("MDC_PRESS_BLD_ART_ABP_SYS")) {
							setSystolicParam(n);
						}
						
					});	//End of getAddedSubList().forEach()
					c.getRemoved().forEach(n -> {
						String metricId=n.getMetric_id();
						//We don't check for interested here as it's either in the table, or not...
						CardiacParameter param=paramFromTable(metricId);
						if(param!=null) {
							tableRows.remove(param);
						}
						if(metricId.equals("MDC_ECG_HEART_RATE")) {
							removeAverageListener(n);
						}
					});
				}
			}
		});
		
		sampleList.addListener(new ListChangeListener<SampleArrayFx>() {

			@Override
			public void onChanged(Change<? extends SampleArrayFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach( n -> {
						String metricId=n.getMetric_id();
						if( ! interested(metricId) ) {
							return;
						}
						boolean found=false;
						for(int i=0;i<tableRows.size();i++) {
							if(tableRows.get(i).getMetricId().equals(metricId)) {
								found=true;
								break;
							}
						}
						if(!found) {
							//Metric not there.  Add it
							CardiacParameter param=new CardiacParameter(metricId, true, true, true, true, true);
							tableRows.add(param);
						}
					});	//End of getAddedSubList().forEach()
					c.getRemoved().forEach(n -> {
						String metricId=n.getMetric_id();
						//We don't check for interested here as it's either in the table, or not...
						CardiacParameter param=paramFromTable(metricId);
						if(param!=null) {
							tableRows.remove(param);
						}
					});
				}
			}
		});
		
		System.err.println("In CAA.addListeners, initial list size is "+dqeList.size());
		dqeList.addListener(new ListChangeListener<DataQualityErrorObjectiveFx>() {

			@Override
			public void onChanged(Change<? extends DataQualityErrorObjectiveFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach( d -> {
						String metricId=d.getMetric_id();
						System.err.println("CAA.addedSubList metricId is "+metricId);
						CardiacParameter param=paramFromTable(metricId);
						if(param!=null) {
							//The alarm corresponds to a row in the table.
							DataQualityAttributeType type=d.getData_quality_attribute_type();
							System.err.println("CAA.addedSubList param was in table.  Ordinal is "+type.ordinal());
							switch (type.ordinal()) {
							case DataQualityAttributeType._accuracy:
								param.setAccuracy(false);
								break;
							case DataQualityAttributeType._completeness:
								param.setCompleteness(false);
								break;
							case DataQualityAttributeType._consistency:
								param.setConsistency(false);
								break;
							case DataQualityAttributeType._credibility:
								param.setCredibility(false);
								break;
							case DataQualityAttributeType._currentness:
								param.setCurrentness(false);
								break;
							default:
								break;
							}
						}
					});
				}
				
			}
			
		});
	}
	
	/**
	 * 
	 * @param n
	 */
	private void addAverageListener(NumericFx n) {
		averageListener=new ChangeListener<Date>() {

			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				float currentVal=n.getValue();
				if(ecgHrCount>29) {
					enoughSamplesForAverage=true;
				}
				if ( ecgHrCount == Integer.MAX_VALUE ) {
					ecgHrCount=0;
				}
				ECG_HR_AVERAGE[ ecgHrCount++ % ECG_SAMPLE_COUNT ]=currentVal;
				if(enoughSamplesForAverage) {
					float avg=0;
					for(int i=0;i<ECG_SAMPLE_COUNT;i++) {
						avg+=ECG_HR_AVERAGE[i];
					}
					avg=avg/ECG_SAMPLE_COUNT;
					ecgAverageProperty.set(avg);
				}
			}
		};
		n.presentation_timeProperty().addListener(averageListener);
	}
	
	private void setSystolicParam(NumericFx n) {
		systolicNumeric=n;
		systolicLabel.textProperty().bind(Bindings.format("Current Systolic BP: %s", systolicNumeric.valueProperty()));
	}
	
	private void removeAverageListener(NumericFx n) {
		n.presentation_timeProperty().removeListener(averageListener);
		enoughSamplesForAverage=false;
		for(int i=0;i<ECG_SAMPLE_COUNT;i++) {
			ECG_HR_AVERAGE[i]=0;
		}
	}

	
	/**
	 * This method checks the list of metrics that we want to use for the app, and returns true if the specified metric
	 * is one of those.
	 * @param metricId the metric to check.
	 * @return true if the metric should be used by the app, otherwise false.
	 */
	private boolean interested(String metricId) {
		if( ! ECG_METRICS.contains(metricId) && ! PPG_METRICS.contains(metricId) && ! IBP_METRICS.contains(metricId)) {
			//Not a metric of interest
			return false;
		}
		return true;
	}
	
	private CardiacParameter paramFromTable(String metricId) {
		CardiacParameter param=null;
		for(int i=0;i<tableRows.size() && param==null;i++) {
			System.err.println("CAA.addedSubList metricId of row is "+tableRows.get(i).getMetricId());
			if(tableRows.get(i).getMetricId().equals(metricId)) {
				param=tableRows.get(i);
			}
		}
		return param;
	}

}
