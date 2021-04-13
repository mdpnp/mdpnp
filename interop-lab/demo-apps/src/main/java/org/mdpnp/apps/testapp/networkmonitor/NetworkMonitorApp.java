package org.mdpnp.apps.testapp.networkmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;

import ice.SafetyFallbackObjective;
import ice.SafetyFallbackObjectiveDataWriter;
import ice.SafetyFallbackObjectiveTopic;
import ice.SafetyFallbackObjectiveTypeSupport;
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
	private DeviceListModel deviceListModel;
	private Map<String, Device> devices = new HashMap<String,Device>();
	private Subscriber assignedSubscriber;	//Use a slightly different name here to avoid poss conflict with any other subscriber variable.
	private Subscriber constructorSubscriber;
	private EventLoop eventLoop;
	private ApplicationContext parentContext;
	private Multimap<String, NetworkQualityMetric> deviceNetworkQualityMetrics;
	private ObservableMap<String, Double> deviceAverages;
	private ObservableList<Entry<String, Double>> data;
	
	/**
	 * A domain participant for publishing things in DDS if required.
	 */
	private DomainParticipant participant;
	private Topic safetyFallbackObjectiveTopic;
	
	/**
	 * DDS publisher for anything we want to publish.
	 */
	private Publisher publisher;
	private SafetyFallbackObjectiveDataWriter safetyFallbackObjectiveWriter;
	
	/**
	 * Instance handle for SafetyFallbackObjective
	 */
	private InstanceHandle_t safetyFallbackObjectiveHandle;
	
	private Map<String,Date> sentNotifications;
	
	public NetworkMonitorApp() {
		SAMPLE_SIZE = System.getProperty("mdpnp.network.monitor.samplesize") == null ? SAMPLE_SIZE_DEFAULT
				: Integer.parseInt(System.getProperty("mdpnp.network.monitor.samplesize"));
		LATENCY_THRESHOLD = System.getProperty("mdpnp.network.monitor.latencythreshold") == null ? LATENCY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty("mdpnp.network.monitor.latencythreshold"));
		
		sentNotifications = new HashMap<String,Date>();
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
		column1.setPrefWidth(320);
		column1.setSortable(true);
		column1.setEditable(false);
		column1.setResizable(false);
		column1.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) -> new SimpleStringProperty(p.getValue().getKey()));
		
		TableColumn<Map.Entry<String, Double>, String> column2 = new TableColumn<Map.Entry<String, Double>, String>();
		column2.setText("Device Model");
		column2.setMinWidth(10);
		column2.setMaxWidth(5000);
		column2.setPrefWidth(400);
		column2.setSortable(true);
		column2.setEditable(false);
		column2.setResizable(false);
		column2.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) -> devices.get(p.getValue().getKey()).modelProperty());
		
		TableColumn<Map.Entry<String, Double>, String> column3 = new TableColumn<Map.Entry<String, Double>, String>();
		column3.setText("Average Latency (ms)");
		column3.setMinWidth(10);
		column3.setMaxWidth(5000);
		column3.setPrefWidth(185);
		column3.setSortable(true);
		column3.setEditable(false);
		column3.setResizable(false);
		column3.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getValue())));
		
		averagesTable.getColumns().setAll(column1, column2, column3);
		averagesTable.autosize();
	}
	
	public void set(ApplicationContext context, Subscriber subscriber, DeviceListModel deviceListModel, NumericFxList numericFxList, SampleArrayFxList sampleFxList) {
		this.parentContext = context;
		this.assignedSubscriber = subscriber;
		this.deviceListModel = deviceListModel;
		this.numericList = numericFxList;
		this.sampleFxList = sampleFxList;
	}
	
	public void start(EventLoop eventLoop, Subscriber subscriber) {
		deviceListModel.getContents().forEach( d-> {
			devices.put(d.getUDI(),d);
		});
		
		deviceListModel.getContents().addListener(new ListChangeListener<Device>() {
			@Override
			public void onChanged(Change<? extends Device> change) {
				while(change.next()) {
					change.getAddedSubList().forEach( d -> {
						devices.put(d.getUDI(),d);
					});
					change.getRemoved().forEach( d-> {
						devices.remove(d.getUDI());
					});
				}
			}
		});
		
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
		Date sentDate = sentNotifications.get(deviceId+type.toString());
		if(sentDate == null || sentDate.before(new Date(System.currentTimeMillis() - 30000))) {
			SafetyFallbackObjective safetyFallbackObjective = new SafetyFallbackObjective();
			safetyFallbackObjective.identifier = UUID.randomUUID().toString();
			safetyFallbackObjective.unique_device_identifier = deviceId;
			safetyFallbackObjective.safety_fallback_type = type;
			switch(type.value()) {
			case SafetyFallbackType._device_network_quality:
				safetyFallbackObjective.message = "Device Network Quality has deteriorated and has triggered a SafetyFallback";
				break;
			case SafetyFallbackType._system_network_quality:
				safetyFallbackObjective.message = "System Network Quality has deteriorated and has triggered a SafetyFallback";
				break;
			case SafetyFallbackType._other:
				safetyFallbackObjective.message = "SafetyFallback has been triggered for non-network-related reason";
				break;
			default:
				safetyFallbackObjective.message = "SafetyFallback has been triggered for an unknown reason";
			}
			
			participant=assignedSubscriber.get_participant();
			
			SafetyFallbackObjectiveTypeSupport.register_type(participant, SafetyFallbackObjectiveTypeSupport.get_type_name());
			
			safetyFallbackObjectiveTopic=TopicUtil.findOrCreateTopic(participant, SafetyFallbackObjectiveTopic.VALUE, SafetyFallbackObjectiveTypeSupport.class);
			
			publisher=parentContext.getBean("publisher", Publisher.class);
			safetyFallbackObjectiveWriter=(SafetyFallbackObjectiveDataWriter)publisher.create_datawriter_with_profile(safetyFallbackObjectiveTopic, QosProfiles.ice_library,
	                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
			
			safetyFallbackObjectiveHandle=safetyFallbackObjectiveWriter.register_instance(safetyFallbackObjective);
			
			sentNotifications.put(deviceId+type.toString(), new Date());
			safetyFallbackObjectiveWriter.write(safetyFallbackObjective, safetyFallbackObjectiveHandle);
			System.out.println("Fallback Initiated by " + deviceId + " with average of " + average);
		}
	}
	
	public void stop() {
	}
	
	public void destroy() {
	}
}