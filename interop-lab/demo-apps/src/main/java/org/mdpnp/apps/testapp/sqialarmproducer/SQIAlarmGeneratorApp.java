package org.mdpnp.apps.testapp.sqialarmproducer;

import java.util.ArrayList;
import java.util.Hashtable;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.DeviceClock.WallClock;
import org.mdpnp.devices.DomainClock;
import org.mdpnp.rtiapi.data.EventLoop;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.Subscriber;

import himss.DDS_DOMAIN_ID_MANAGER;
import ice.DataQualityErrorObjective;
import ice.DataQualityErrorObjectiveDataWriter;
import ice.FlowRateObjectiveDataWriter;
import ice.NumericSQIObjectiveDataWriter;
import ice.SampleArray;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class SQIAlarmGeneratorApp {
	
	@FXML
	ComboBox<Device> devices;
	@FXML
	ComboBox<String> metrics;
	@FXML
	TextField requestedAccuracy;
	@FXML
	TextField requestedCompleteness;
	@FXML
	TextField requestedConsistency;
	@FXML
	TextField requestedCredibility;
	@FXML
	TextField requestedCurrentness;
	@FXML
	Button publishErrorButton;
	
	private DataQualityErrorObjectiveDataWriter dqeObjectiveWriter;
	
	private Hashtable<Device, ArrayList<String>> metricsByDevice;
	private NumericFxList numericList;
	private SampleArrayFxList sampleList;
	private DeviceListModel deviceListModel;
	
	DeviceClock timesource=new DeviceClock.WallClock();

	public void set(DeviceListModel deviceListModel, NumericFxList numericList, SampleArrayFxList sampleList,
			FlowRateObjectiveDataWriter objectiveWriter, NumericSQIObjectiveDataWriter sqiObjectiveWriter,
			MDSHandler mdsHandler, DataQualityErrorObjectiveDataWriter dqeObjectiveWriter) {
		this.dqeObjectiveWriter=dqeObjectiveWriter;
		
		deviceListModel.getContents().addListener( 
				new ListChangeListener<Device>() {

					@Override
					public void onChanged(Change<? extends Device> c) {
						while(c.next()) {
							c.getAddedSubList().forEach( d -> {
								devices.getItems().add(d);
							});
							c.getRemoved().forEach( d -> {
								devices.getItems().remove(d);
							});
						};
						
					}
					
				}
		);
		devices.setCellFactory(new Callback<ListView<Device>,ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> device) {
				return new DeviceListCell();
			}
			
		});
		
		devices.setConverter(new StringConverter<Device>() {

			@Override
			public Device fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString(Device device) {
				// TODO Auto-generated method stub
				return device.getModel()+"("+device.getComPort()+")";
			}
			
		});
		
		metricsByDevice=new Hashtable<>();
		
		this.numericList=numericList;
		this.sampleList=sampleList;
		this.deviceListModel=deviceListModel;
		
		numericList.addListener( new ListChangeListener<NumericFx>() {

			@Override
			public void onChanged(Change<? extends NumericFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach(n -> {
						String metricId=n.getMetric_id();
						String deviceId=n.getUnique_device_identifier();
						Device d=deviceListModel.getByUniqueDeviceIdentifier(deviceId);
						if( ! metricsByDevice.containsKey(d)) {
							//No metrics for this device yet
							ArrayList<String> metrics=new ArrayList<>();
							metrics.add(metricId);
							metricsByDevice.put(d, metrics);
						} else {
							//Existing metrics
							ArrayList<String> metrics=metricsByDevice.get(d);
							if (! metrics.contains(metricId)) {
								metrics.add(metricId);
							}
						}
					});
				}
				
			}
			
		});
		
		sampleList.addListener( new ListChangeListener<SampleArrayFx>() {

			@Override
			public void onChanged(Change<? extends SampleArrayFx> c) {
				while(c.next()) {
					c.getAddedSubList().forEach(n -> {
						String metricId=n.getMetric_id();
						String deviceId=n.getUnique_device_identifier();
						Device d=deviceListModel.getByUniqueDeviceIdentifier(deviceId);
						if( ! metricsByDevice.containsKey(d)) {
							//No metrics for this device yet
							ArrayList<String> metrics=new ArrayList<>();
							metrics.add(metricId);
							metricsByDevice.put(d, metrics);
						} else {
							//Existing metrics
							ArrayList<String> metrics=metricsByDevice.get(d);
							if (! metrics.contains(metricId)) {
								metrics.add(metricId);
							}
						}
					});
				}
				
			}
			
		});
		
		
		devices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Device>() {

			@Override
			public void changed(ObservableValue<? extends Device> observable, Device oldValue, Device newValue) {
				metrics.getItems().removeAll(metrics.getItems());
				if(newValue!=null && metricsByDevice.get(newValue)!=null) {
					metrics.getItems().addAll(metricsByDevice.get(newValue));
				}
			}
			
		});
	}
	
	class DeviceListCell extends ListCell<Device> {
        @Override protected void updateItem(Device device, boolean empty) {
            super.updateItem(device, empty);
            if (!empty && device != null) {
                setText(device.getModel()+"("+device.getComPort()+")");
            } else {
                setText(null);
            }
        }
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
	
	public void publishError() {
		DataQualityErrorObjective objective=new DataQualityErrorObjective();
		
		int len1=requestedAccuracy.getText().length();
		int len2=requestedCompleteness.getText().length();
		int len3=requestedConsistency.getText().length();
		int len4=requestedCredibility.getText().length();
		int len5=requestedCurrentness.getText().length();
		
		if(len1>0 && /*len2>0 && */ len3>0 && len4>0 && len5>0) {
			System.err.println("Only one param at a time");
			return;
		}
		
		if(len1>0) {
			objective.data_quality_attribute_type=ice.DataQualityAttributeType.accuracy;
			try {
				objective.value=Float.parseFloat(requestedAccuracy.getText());
			} catch (NumberFormatException nfe) {
				System.err.println("Bad number");
				return;
			}
		}
		if(len2>0) {
			objective.data_quality_attribute_type=ice.DataQualityAttributeType.completeness;
			try {
				objective.value=Float.parseFloat(requestedCompleteness.getText());
			} catch (NumberFormatException nfe) {
				System.err.println("Bad number");
				return;
			}
		}
		
		if(len3>0) {
			objective.data_quality_attribute_type=ice.DataQualityAttributeType.consistency;
			try {
				objective.value=Float.parseFloat(requestedConsistency.getText());
			} catch (NumberFormatException nfe) {
				System.err.println("Bad number");
				return;
			}
		}
		
		if(len4>0) {
			objective.data_quality_attribute_type=ice.DataQualityAttributeType.credibility;
			try {
				objective.value=Float.parseFloat(requestedCredibility.getText());
			} catch (NumberFormatException nfe) {
				System.err.println("Bad number");
				return;
			}
		}
		
		if(len5>0) {
			objective.data_quality_attribute_type=ice.DataQualityAttributeType.currentness;
			try {
				objective.value=Float.parseFloat(requestedCurrentness.getText());
			} catch (NumberFormatException nfe) {
				System.err.println("Bad number");
				return;
			}
		}
		
		objective.unique_device_identifier=devices.getSelectionModel().getSelectedItem().getUDI();
		objective.metric_id=metrics.getSelectionModel().getSelectedItem();
		
		Time_t tt=DomainClock.toDDSTime(timesource.instant().getDeviceTime());
		ice.Time_t iceTime=new ice.Time_t();
		iceTime.nanosec=tt.nanosec;
		iceTime.sec=tt.sec;
		
		objective.presentation_time=iceTime;
		
		dqeObjectiveWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
	}

}
