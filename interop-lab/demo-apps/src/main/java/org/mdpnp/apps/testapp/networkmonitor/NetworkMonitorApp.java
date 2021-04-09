package org.mdpnp.apps.testapp.networkmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.Subscriber;

import ice.SafetyFallbackObjectiveDataWriter;
import ice.SafetyFallbackType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class NetworkMonitorApp {
	private static final int LATENCY_THRESHOLD_DEFAULT = 600;
	private static final int SAMPLE_SIZE_DEFAULT = 30;
	
	private static int SAMPLE_SIZE;
	private static int LATENCY_THRESHOLD;

	@FXML TableView<Map.Entry<String, Double>> averagesTable;
	private NumericFxList numericList;
	private SampleArrayFxList sampleFxList;
	private Subscriber subscriber;
	private EventLoop eventLoop;
	private ApplicationContext context;
	private SafetyFallbackObjectiveDataWriter objectiveWriter;
	private Multimap<String, NetworkQualityMetric> deviceNetworkQualityMetrics;
	private ObservableMap<String, Double> deviceAverages;
	private ObservableList<Entry<String, Double>> data;
	
	public NetworkMonitorApp() {
		SAMPLE_SIZE = System.getProperty("mdpnp.network.monitor.samplesize") == null ? SAMPLE_SIZE_DEFAULT
				: Integer.parseInt(System.getProperty("mdpnp.network.monitor.samplesize"));
		LATENCY_THRESHOLD = System.getProperty("mdpnp.network.monitor.latencythreshold") == null ? LATENCY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty("mdpnp.network.monitor.latencythreshold"));
	}
	
	public void activate() {
		setupTable();
	}
	
	@SuppressWarnings("unchecked")
	private void setupTable() {
		averagesTable.setItems(data);
		averagesTable.setEditable(false);
		
		TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<Map.Entry<String, Double>, String>();
		column1.setText("DeviceId");
		column1.setMinWidth(10);
		column1.setMaxWidth(5000);
		column1.setPrefWidth(350);
		column1.setSortable(true);
		column1.setEditable(false);
		column1.setResizable(false);
		column1.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) -> new SimpleStringProperty(p.getValue().getKey()));
		
		TableColumn<Map.Entry<String, Double>, String> column2 = new TableColumn<Map.Entry<String, Double>, String>();
		column2.setText("Average Latency");
		column2.setMinWidth(10);
		column2.setMaxWidth(5000);
		column2.setPrefWidth(285);
		column2.setSortable(true);
		column2.setEditable(false);
		column2.setResizable(false);
		column2.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) -> new SimpleStringProperty(p.getValue().getValue().toString()));
		
		averagesTable.getColumns().setAll(column1, column2);
		averagesTable.autosize();
	}
	
	public void set(ApplicationContext context, Subscriber subscriber, SafetyFallbackObjectiveDataWriter objectiveWriter, NumericFxList numericFxList, SampleArrayFxList sampleFxList) {
		this.context = context;
		this.subscriber = subscriber;
		this.objectiveWriter = objectiveWriter;
		this.numericList = numericFxList;
		this.sampleFxList = sampleFxList;
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		deviceNetworkQualityMetrics = MultimapBuilder.treeKeys().treeSetValues().build();
		deviceAverages = FXCollections.observableHashMap();
		data = FXCollections.observableArrayList(new ArrayList<>(deviceAverages.entrySet()));
		
		numericList.addListener(new ListChangeListener<NumericFx>() {
			@Override
			public void onChanged(Change<? extends NumericFx> change) {
				while (change.next()) {
					change.getAddedSubList().forEach(n -> {
						n.presentation_timeProperty().addListener(new ChangeListener<Date>() {
							@Override
							public void changed(ObservableValue<? extends Date> observable, Date oldValue,
									Date newValue) {
								addDeviceMetric(n.getUnique_device_identifier(), n.getPresentation_time(), n.getDelta());
							}
						});
					});
				}
			}
		});

		sampleFxList.addListener(new ListChangeListener<SampleArrayFx>() {
			@Override
			public void onChanged(Change<? extends SampleArrayFx> change) {
				while (change.next()) {
					change.getAddedSubList().forEach(n -> {
						n.presentation_timeProperty().addListener(new ChangeListener<Date>() {
							@Override
							public void changed(ObservableValue<? extends Date> observable, Date oldValue,
									Date newValue) {
								addDeviceMetric(n.getUnique_device_identifier(), n.getPresentation_time(), n.getDelta());
							}
						});
					});
				}
			}
		});
		
		deviceAverages.addListener(new MapChangeListener<String, Double>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Double> change) {
				if (change.wasAdded()) {
					Double valueAdded = change.getValueAdded();
					if(valueAdded > LATENCY_THRESHOLD) {
						sendSafetyFallbackMessage(SafetyFallbackType.device_network_quality, change.getKey(), valueAdded);
					}
					double systemLatency = change.getMap().values().stream().mapToDouble(n -> n.doubleValue()).average().orElse(0.0);
					if(systemLatency > LATENCY_THRESHOLD) {
						sendSafetyFallbackMessage(SafetyFallbackType.system_network_quality, null, systemLatency);
					}
				}
			}
		});
	}
	
	private void addDeviceMetric(String deviceId, Date presentationDate, long delta) {
		Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).put(deviceId, new NetworkQualityMetric(deviceId, presentationDate, delta));
		Collection<NetworkQualityMetric> collection = deviceNetworkQualityMetrics.get(deviceId);
		
		Optional<NetworkQualityMetric> findFirst = null;
		int count = collection.size();
		Double deviceAverage = deviceAverages.get(deviceId);
		int oldCount = count - 1;
		if(count > SAMPLE_SIZE) {
			findFirst = Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).get(deviceId).stream().findFirst();
			if(findFirst != null && findFirst.isPresent()) {
				NetworkQualityMetric first = findFirst.get();
				long deltaBeingRemoved = first.getDelta();
				boolean removed = Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).get(deviceId).remove(first);
				if(removed) {
					deviceAverages.put(deviceId, new Double((Math.abs((deviceAverage * oldCount - deltaBeingRemoved)) + delta)/oldCount));
				} else {
					// Shouldn't hit this, but just in case.
					deviceAverages.put(deviceId, (deviceAverage * oldCount + delta)/count);
				}
			}
		} else {
			if(deviceAverage == null) {
				deviceAverages.put(deviceId, new Double(delta));
			} else {
				deviceAverages.put(deviceId, (deviceAverage * oldCount + delta)/count);
			}
		}
		data.clear();
		deviceAverages.entrySet().stream().forEachOrdered(o -> data.add(o));
	}
	
	public void sendSafetyFallbackMessage(SafetyFallbackType type, String deviceId, Double average) {
		ice.SafetyFallbackObjective objective=new ice.SafetyFallbackObjective();
		objective.identifier = UUID.randomUUID().toString();
		objective.unique_device_identifier = deviceId;
		objective.safety_fallback_type = type;
		switch(type.value()) {
		case SafetyFallbackType._device_network_quality:
			objective.message = "Device Network Quality has deteriorated and has triggered a SafetyFallback";
			break;
		case SafetyFallbackType._system_network_quality:
			objective.message = "System Network Quality has deteriorated and has triggered a SafetyFallback";
			break;
		case SafetyFallbackType._other:
			objective.message = "SafetyFallback has been triggered for non-network-related reason";
			break;
		default:
			objective.message = "SafetyFallback has been triggered for an unknown reason";
		}
		
		objectiveWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
		System.out.println("Fallback Initiated by " + deviceId + " with average of " + average);
	}
	
	public void stop() {
	}
	
	public void destroy() {
	}
}