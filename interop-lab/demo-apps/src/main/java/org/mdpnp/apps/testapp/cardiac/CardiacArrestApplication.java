
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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import com.rti.dds.subscription.Subscriber;

import ice.DataQualityAttributeType;
import ice.FlowRateObjectiveDataWriter;

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
	private static final int ECG_SAMPLE_COUNT=5;
	//private static final int ECG_SAMPLE_COUNT=30;
	
	/**
	 * The cardiac inversion threshold value for SVT.
	 */
	private static final float SVT_THRESHOLD=150;
	
	/**
	 * The low heart rate threshold
	 */
	private static final float LOW_ECG_HR_THRESHOLD=20;
	
	/**
	 * The cardiac inversion threshold value for Systolic BP.
	 */
	private static final float SYS_THRESHOLD=25;
	//private static final float SYS_THRESHOLD=80;
	
	/**
	 * The threshold at which the non ECG pulse signal is low
	 */
	private static final float LOW_PULSE_THRESHOLD=30;
	
	/**
	 * The perfusion index threshold
	 */
	private static final float PERF_THRESHOLD=0.3f;
	
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
	
	/**
	 * Metric ID for invasive BP from MX800
	 */
	//private final String ARTERIAL=rosetta.MDC_PRESS_BLD_ART_ABP.VALUE;
	
	/**
	 * Alternative to metric for systolic, because with MX800 we derive the systolic from the waveform.
	 */
	//private IntegerProperty systolicProperty=new SimpleIntegerProperty();
	
	/**
	 * The pulse rate property - whatever other pulse source we are using as well as the ECG
	 */
	private SimpleFloatProperty pulseRateProperty=new SimpleFloatProperty(); 
	
	private NumericFx systolicNumeric;
	
	private Thread monitorThread;
	
	private boolean pleaseStop;
	
	public CardiacArrestApplication() {
		ECG_METRICS.add("MDC_ECG_LEAD_I");
		ECG_METRICS.add("MDC_ECG_LEAD_II");
		ECG_METRICS.add("MDC_ECG_LEAD_III");
		ECG_METRICS.add("MDC_ECG_HEART_RATE");
		
		PPG_METRICS.add("MDC_PULSE_OXIM_PLETH");
		PPG_METRICS.add("MDC_PULSE_OXIM_PULS_RATE");
		PPG_METRICS.add("MDC_PULS_OXIM_PERF_REL");
		
		IBP_METRICS.add("MDC_PRESS_BLD_ART_ABP_SYS");
		IBP_METRICS.add("MDC_PRESS_BLD_ART_ABP_DIA");
		IBP_METRICS.add("MDC_PRESS_BLD");	//AVERAGE?
		//IBP_METRICS.add(ARTERIAL);
		
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
		//inversionLabel.textProperty().bind(Bindings.format("Perform cardioversion: %s", okForCardiacInversionProperty));
		inversionLabel.textProperty().bind(Bindings.format("Cardiac Arrest: %s", okForCardiacInversionProperty));
		//systolicLabel.textProperty().bind(Bindings.format("Current Systolic BP: %s", systolicProperty));
		
		
		/*
		 * For the time being, we will just use a single listener to the other properties, and when that changes,
		 * we will calculate whether to do cardiac inversion.  Because, after the first 30 seconds, we can assume
		 * that the ecgAverageProperty will be updated once per second.  That's a good driver for evaluating the
		 * rest of the conditions.
		 */
		//THIS DOESN'T WORK WHEN THE VALUES ARE STABLE.  LISTEN TO THE TIME CHANGE ON SOMETHING INSTEAD OR JUST SLEEP
		//FOR A SECOND IN A WHILE LOOP
		/*
		ecgAverageProperty.addListener( new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if(
					newValue.floatValue() < LOW_ECG_HR_THRESHOLD && 
					pulseRateProperty.floatValue() < LOW_PULSE_THRESHOLD &&
					systolicNumeric.getValue() < SYS_THRESHOLD
						
				) {
						okForCardiacInversionProperty.set("YES");
						//inversionLabel
						//ALERT - RULE OUT CARDIAC ARREST
						//THEN SEND MESSAGE
				}
				
			}
			
		});
		*/
		monitorThread=new Thread() {
			public void run() {
				while(true) {
					try {
						sleep(1000);
					} catch (InterruptedException ie) {
						if(pleaseStop) {
							return;
						}
					}
					if(
							ecgAverageProperty!=null && ecgAverageProperty.floatValue() < LOW_ECG_HR_THRESHOLD && 
							pulseRateProperty!=null && pulseRateProperty.floatValue() < LOW_PULSE_THRESHOLD &&
							systolicNumeric!=null && systolicNumeric.getValue() < SYS_THRESHOLD
								
						) {
						Platform.runLater(new Runnable() {
							public void run() {
								okForCardiacInversionProperty.set("YES");
								inversionLabel.setTextFill(Color.RED);
								postNotification();
							}
						});
							
					} else {
						Platform.runLater(new Runnable() {
							public void run() {
								okForCardiacInversionProperty.set("NO");
								inversionLabel.setTextFill(Color.BLACK);
							}
						});
					}
				}
			}
		};
		monitorThread.start();
		
	}
	
	private void postNotification() {
		NumericFx n=new NumericFx();
		n.setMetric_id("CARDIAC_ARREST_NOTIFICATION");
		n.setPresentation_time(new Date());
		n.setUnique_device_identifier("CA_APP");
		n.setValue(1);
		numericList.add(n);
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
		pleaseStop=true;
		monitorThread.interrupt();
		
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
						
						if(metricId.equals("MDC_PULSE_OXIM_PULS_RATE")) {
							setPulseProperty(n);
						}
						
						if(metricId.equals("MDC_PRESS_BLD_ART_ABP_SYS")) {
							setSystolicParam(n);
						}
						if(metricId.equals("MDC_PULS_OXIM_PERF_REL")) {
							addPerfListener(n);
						}
						
						//
						
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
//						if( metricId.equals(ARTERIAL) ) {
//							addMaxMinListener(n);
//						}
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
	
	class SampleValuesChangeListener implements ChangeListener<Number[]> {

		@Override
		public void changed(ObservableValue<? extends Number[]> observable, Number[] oldValue, Number[] newValue) {
			//Ignore the old values.  Just get new ones.
			float[] minMax=getMinAndMax(newValue);
			
		}
		
	}
	
	SampleValuesChangeListener bpArrayListener=new SampleValuesChangeListener();
	
	private void addMaxMinListener(SampleArrayFx s) {
		s.valuesProperty().addListener(bpArrayListener);
	}
	
	private float[] getMinAndMax(Number[] numbers) {
		float[] minAndMax=new float[] {numbers[0].floatValue(),numbers[0].floatValue()};
		for(int i=1;i<numbers.length;i++) {
			if(numbers[i].floatValue()<minAndMax[0]) minAndMax[0]=numbers[i].floatValue();
			if(numbers[i].floatValue()>minAndMax[1]) minAndMax[1]=numbers[i].floatValue();
		}
		//diastolicProperty.set((int)minAndMax[0]);
		//systolicProperty.set((int)minAndMax[1]);
		//This getMinAndMax method is called before the start button is pressed, in which case the numericBPDevice
		//won't have been created yet, hence the null check.
		return minAndMax;
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
				if(ecgHrCount>ECG_SAMPLE_COUNT) {
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
	
	/**
	 * Bind our pulse rate property to the selected value.
	 * @param n
	 */
	private void setPulseProperty(NumericFx n) {
		pulseRateProperty.bind(n.valueProperty());
	}
	
	private void setSystolicParam(NumericFx n) {
		systolicNumeric=n;
		systolicLabel.textProperty().bind(Bindings.format("Current Systolic BP: %s", systolicNumeric.valueProperty()));
	}
	
	private void addPerfListener(NumericFx n) {
		n.presentation_timeProperty().addListener(new ChangeListener<Date>() {

			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				float val=n.getValue();
				if(val<PERF_THRESHOLD) {
					plethPresentProperty.set(false);
				} else {
					plethPresentProperty.set(true);
				}
				
			}
			
		});
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
