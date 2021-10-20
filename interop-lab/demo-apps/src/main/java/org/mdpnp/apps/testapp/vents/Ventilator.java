package org.mdpnp.apps.testapp.vents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.FxmlLoaderFactory;
import org.mdpnp.apps.testapp.HumanReadable;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.guis.javafx.VentilatorPanel;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

import ice.FlowRateObjectiveDataWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.collections.transformation.FilteredList;

import org.mdpnp.apps.device.DeviceDataMonitor;
import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Ventilator {
	
	@FXML TableView numericTable;
	
	@FXML BorderPane ventChart1;
	
	@FXML ComboBox<Device> devicesCombo;
	
	//@FXML BorderPane ventChart2;

	private ApplicationContext parentContext;

	private DeviceListModel dlm;

	private NumericFxList numeric;

	private SampleArrayFxList samples;

	private MDSHandler mdsHandler;
	
	private InfusionStatusFxList infusion;

	private VitalModel vitalModel;
	
	private static final int MAX_POINTS = 1200;
	
	//private VentilatorPanel vp;
	
	private org.mdpnp.apps.device.VentilatorPanel deviceVentilatorPanel;
	
	public Ventilator() {
		
	}
	
	private WaveformPanel flowPanel, pressurePanel;
	
	//final ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();
	//final List<ValueSeriesListener> values = new ArrayList<ValueSeriesListener>();
	
	public void set(ApplicationContext parentContext, DeviceListModel dlm, NumericFxList numeric, SampleArrayFxList samples,
			MDSHandler mdsHandler, InfusionStatusFxList infusionStatus) {
		this.parentContext=parentContext;
		this.dlm=dlm;
		this.numeric=numeric;
		this.samples=samples;
		this.mdsHandler=mdsHandler;
		this.infusion=infusionStatus;
		populateComboBox();
	}
	
	private void populateComboBox() {
		devicesCombo.itemsProperty().set(dlm.getContents());
		devicesCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Device>() {

			@Override
			public void changed(ObservableValue<? extends Device> observable, Device oldValue, Device newValue) {
				if(newValue!=null) {
					deviceVentilatorPanel=new org.mdpnp.apps.device.VentilatorPanel();
					deviceVentilatorPanel.setPrefWidth(400);
					ventChart1.setCenter(deviceVentilatorPanel);
					DeviceDataMonitor ddm=new DeviceDataMonitor(newValue.getUDI(), dlm, numeric, samples, infusion);
					deviceVentilatorPanel.set(ddm);
					
					FilteredList<NumericFx> numericsForUdi=new FilteredList<NumericFx>(numeric, new Predicate<NumericFx>() {

						@Override
						public boolean test(NumericFx t) {
							return t.getUnique_device_identifier().equals(newValue.getUDI());
						}
						
					});
					HumanReadableNumericFxList humanReadableList=new HumanReadableNumericFxList(ice.NumericTopic.VALUE);
					numericsForUdi.addListener(new ListChangeListener<NumericFx>() {

						@Override
						public void onChanged(Change<? extends NumericFx> c) {
							while(c.next()) {
								if(c.wasAdded()) {
									c.getAddedSubList().forEach( n -> {
										HumanReadableNumericFx humanNumeric=new HumanReadableNumericFx(n);
										humanReadableList.add(humanNumeric);
										System.err.println("Added humanNumeric");
									});
								}
								if(c.wasRemoved()) {
									c.getRemoved().forEach( n -> {
										//TODO: Better removal mechanism by index?  Not sure we can guarantee they are present.
										boolean maybeGone=humanReadableList.remove(n);
										System.err.println(maybeGone ? "Removed an element from humanReadable" : "Element was not present to remove in humanReadable");
									});
								}
							}
						}
						
					});
					//That handles the change in the list contents, but we miss the setting of the initial values in it,
					//because we don't start filtering the list until after the set.  We need to just pass through once
					//to duplicate it.
					numericsForUdi.forEach(n -> {
						HumanReadableNumericFx humanNumeric=new HumanReadableNumericFx(n);
						humanReadableList.add(humanNumeric);
					});
					numericTable.setItems(humanReadableList);
					
				} else {
					ventChart1.setCenter(null);
					deviceVentilatorPanel=null;
				}
			}
			
		});
	}
	
	public class HumanReadableNumericFx extends NumericFx {
		private NumericFx parent;
		private StringProperty human_readable_for_metric;
		private StringProperty human_readable_for_unit;
		
	    public String getHuman_readable_for_unit() {
	        return human_readable_for_unit.get();
	    }
	    public void setHuman_readable_for_unit(String human_readable_for_unit) {
	    	human_readable_for_unitProperty().set(human_readable_for_unit);
	    }
	    public StringProperty human_readable_for_unitProperty() {
	        if(null == human_readable_for_unit) {
	        	human_readable_for_unit = new SimpleStringProperty(this, "human_readable_for_unit");
	        }
	        return human_readable_for_unit;
	    }
	    
	    public String getHuman_readable_for_metric() {
	        return human_readable_for_metric.get();
	    }
	    public void setHuman_readable_for_metric(String human_readable_for_metric) {
	    	human_readable_for_metricProperty().set(human_readable_for_metric);
	    }
	    public StringProperty human_readable_for_metricProperty() {
	        if(null == human_readable_for_metric) {
	        	human_readable_for_metric = new SimpleStringProperty(this, "human_readable_for_metric");
	        }
	        return human_readable_for_metric;
	    }
	    
	    HumanReadableNumericFx(NumericFx parent) {
	    	this.parent=parent;
	    	if(HumanReadable.MetricLabels.containsKey(parent.getMetric_id())) {
	    		//If there is a human readable metric, set it
	    		setHuman_readable_for_metric(HumanReadable.MetricLabels.get(parent.getMetric_id()));
	    	} else {
	    		//Else the human readable metric is just the untranslated normal metric 
	    		setHuman_readable_for_metric(parent.getMetric_id());
	    	}
	    	
	    	if(HumanReadable.MetricLabels.containsKey(parent.getUnit_id())) {
	    		setHuman_readable_for_unit(HumanReadable.MetricLabels.get(parent.getUnit_id()));
	    	} else {
	    		setHuman_readable_for_unit(parent.getUnit_id());
	    	}
	    	setEverythingElse();
	    }
	    
	    private void setEverythingElse() {
	    	device_timeProperty().bind(parent.device_timeProperty());
	    	instance_idProperty().bind(parent.instance_idProperty());
	    	metric_idProperty().bind(parent.metric_idProperty());
	    	presentation_timeProperty().bind(parent.presentation_timeProperty());
	    	source_timestampProperty().bind(parent.source_timestampProperty());
	    	unique_device_identifierProperty().bind(parent.unique_device_identifierProperty());
	    	unit_idProperty().bind(parent.unit_idProperty());
	    	valueProperty().bind(parent.valueProperty());
	    	vendor_metric_idProperty().bind(parent.vendor_metric_idProperty());
	    }
	}
	
	class HumanReadableNumericFxList extends NumericFxList {

		public HumanReadableNumericFxList(String topicName) {
			super(topicName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void doAdd(int index, NumericFx element) {
			// TODO Auto-generated method stub
			super.doAdd(index, element);
		}

		@Override
		protected NumericFx doSet(int index, NumericFx element) {
			// TODO Auto-generated method stub
			return super.doSet(index, element);
		}

		@Override
		protected NumericFx doRemove(int index) {
			// TODO Auto-generated method stub
			return super.doRemove(index);
		}
		
		
		
	}
	
}
