package org.mdpnp.apps.testapp.dataqualitymonitor;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;

import ice.DataQualityAttributeType;
import ice.DataQualityErrorObjective;
import ice.DataQualityErrorObjectiveDataWriter;
import ice.DataQualityErrorObjectiveTopic;
import ice.DataQualityErrorObjectiveTypeSupport;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class DataQualityMonitorApp {
	private static final String KEY_COMBO = "::::";
	private static final int LATENCY_THRESHOLD_DEFAULT = 600;
	private static final int SAMPLE_SIZE_DEFAULT = 30;
	
	private static int SAMPLE_SIZE;
	private static int LATENCY_THRESHOLD;

	@FXML TableView<DataQualityDisplayWrapper> averagesTable;
	private NumericFxList numericList;
	private SampleArrayFxList sampleFxList;
	private DeviceListModel deviceListModel;
	private Map<String, Device> devices = new HashMap<String,Device>();
	private Subscriber assignedSubscriber;	//Use a slightly different name here to avoid poss conflict with any other subscriber variable.
	private Subscriber constructorSubscriber;
	private EventLoop eventLoop;
	private ApplicationContext parentContext;
	private Multimap<String, DataQualityMetric> deviceNetworkQualityMetrics;
	private ObservableList<DataQualityDisplayWrapper> data = FXCollections.observableArrayList();
	
	/**
	 * A domain participant for publishing things in DDS if required.
	 */
	private DomainParticipant participant;
	private Topic dataQualityErrorObjectiveTopic;
	
	/**
	 * DDS publisher for anything we want to publish.
	 */
	private Publisher publisher;
	private DataQualityErrorObjectiveDataWriter dataQualityErrorObjectiveWriter;
	
	/**
	 * Instance handle for SafetyFallbackObjective
	 */
	private InstanceHandle_t dataQualityErrorObjectiveHandle;
	
	private Map<String,Date> sentNotifications;
	
	public DataQualityMonitorApp() {
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
		averagesTable.setEditable(false);
		TableViewSelectionModel<DataQualityDisplayWrapper> defaultSelectionModel = averagesTable.getSelectionModel();
		averagesTable.setSelectionModel(defaultSelectionModel);
		averagesTable.setItems(data);
		averagesTable.sort();
		
		TableColumn<DataQualityDisplayWrapper, String> column1 = new TableColumn<DataQualityDisplayWrapper, String>();
		column1.setText("DeviceId");
		column1.setMinWidth(10);
		column1.setMaxWidth(5000);
		column1.setPrefWidth(320);
		column1.setSortable(true);
		column1.setEditable(false);
		column1.setResizable(false);
		column1.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(p.getValue().getDeviceId()));
		
		TableColumn<DataQualityDisplayWrapper, String> column2 = new TableColumn<DataQualityDisplayWrapper, String>();
		column2.setText("MetricId");
		column2.setMinWidth(10);
		column2.setMaxWidth(5000);
		column2.setPrefWidth(320);
		column2.setSortable(true);
		column2.setEditable(false);
		column2.setResizable(false);
		column2.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(p.getValue().getMetricId()));
		
		TableColumn<DataQualityDisplayWrapper, String> column3 = new TableColumn<DataQualityDisplayWrapper, String>();
		column3.setText("Accuracy");
		column3.setMinWidth(10);
		column3.setMaxWidth(5000);
		column3.setPrefWidth(185);
		column3.setSortable(true);
		column3.setEditable(false);
		column3.setResizable(false);
		column3.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getAccuracy())));
		column3.setCellFactory(new Callback<TableColumn<DataQualityDisplayWrapper, String>, TableCell<DataQualityDisplayWrapper, String>>() {

			@Override
			public TableCell<DataQualityDisplayWrapper, String> call(TableColumn<DataQualityDisplayWrapper, String> param) {
				return new TableCell<DataQualityDisplayWrapper, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty()
                                    .getValue() < 0 ? 0
                                    : indexProperty().getValue();
                            double value = param
                                    .getTableView().getItems()
                                    .get(currentIndex).getAccuracy();
                            if (value > LATENCY_THRESHOLD) {
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else if (value < LATENCY_THRESHOLD){
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            }
                        }
                    }
                };
			}
			
		});
		
		TableColumn<DataQualityDisplayWrapper, String> column4 = new TableColumn<DataQualityDisplayWrapper, String>();
		column4.setText("Completeness");
		column4.setMinWidth(10);
		column4.setMaxWidth(5000);
		column4.setPrefWidth(185);
		column4.setSortable(true);
		column4.setEditable(false);
		column4.setResizable(false);
		column4.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getCompleteness())));
		column4.setCellFactory(new Callback<TableColumn<DataQualityDisplayWrapper, String>, TableCell<DataQualityDisplayWrapper, String>>() {

			@Override
			public TableCell<DataQualityDisplayWrapper, String> call(TableColumn<DataQualityDisplayWrapper, String> param) {
				return new TableCell<DataQualityDisplayWrapper, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty()
                                    .getValue() < 0 ? 0
                                    : indexProperty().getValue();
                            double value = param
                                    .getTableView().getItems()
                                    .get(currentIndex).getCompleteness();
                            if (value > LATENCY_THRESHOLD) {
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else if (value < LATENCY_THRESHOLD){
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            }
                        }
                    }
                };
			}
			
		});
		
		TableColumn<DataQualityDisplayWrapper, String> column5 = new TableColumn<DataQualityDisplayWrapper, String>();
		column5.setText("Consistency");
		column5.setMinWidth(10);
		column5.setMaxWidth(5000);
		column5.setPrefWidth(185);
		column5.setSortable(true);
		column5.setEditable(false);
		column5.setResizable(false);
		column5.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getConsistency())));
		column5.setCellFactory(new Callback<TableColumn<DataQualityDisplayWrapper, String>, TableCell<DataQualityDisplayWrapper, String>>() {

			@Override
			public TableCell<DataQualityDisplayWrapper, String> call(TableColumn<DataQualityDisplayWrapper, String> param) {
				return new TableCell<DataQualityDisplayWrapper, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty()
                                    .getValue() < 0 ? 0
                                    : indexProperty().getValue();
                            double value = param
                                    .getTableView().getItems()
                                    .get(currentIndex).getConsistency();
                            if (value > LATENCY_THRESHOLD) {
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else if (value < LATENCY_THRESHOLD){
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            }
                        }
                    }
                };
			}
			
		});
		
		TableColumn<DataQualityDisplayWrapper, String> column6 = new TableColumn<DataQualityDisplayWrapper, String>();
		column6.setText("Credibility");
		column6.setMinWidth(10);
		column6.setMaxWidth(5000);
		column6.setPrefWidth(185);
		column6.setSortable(true);
		column6.setEditable(false);
		column6.setResizable(false);
		column6.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getCredibility())));
		column6.setCellFactory(new Callback<TableColumn<DataQualityDisplayWrapper, String>, TableCell<DataQualityDisplayWrapper, String>>() {

			@Override
			public TableCell<DataQualityDisplayWrapper, String> call(TableColumn<DataQualityDisplayWrapper, String> param) {
				return new TableCell<DataQualityDisplayWrapper, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty()
                                    .getValue() < 0 ? 0
                                    : indexProperty().getValue();
                            double value = param
                                    .getTableView().getItems()
                                    .get(currentIndex).getCredibility();
                            if (value > LATENCY_THRESHOLD) {
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else if (value < LATENCY_THRESHOLD){
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            }
                        }
                    }
                };
			}
			
		});
		
		TableColumn<DataQualityDisplayWrapper, String> column7 = new TableColumn<DataQualityDisplayWrapper, String>();
		column7.setText("Currentness");
		column7.setMinWidth(10);
		column7.setMaxWidth(5000);
		column7.setPrefWidth(185);
		column7.setSortable(true);
		column7.setEditable(false);
		column7.setResizable(false);
		column7.setCellValueFactory((TableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(String.format("%.3f", p.getValue().getCurrentness())));
		column7.setCellFactory(new Callback<TableColumn<DataQualityDisplayWrapper, String>, TableCell<DataQualityDisplayWrapper, String>>() {

			@Override
			public TableCell<DataQualityDisplayWrapper, String> call(TableColumn<DataQualityDisplayWrapper, String> param) {
				return new TableCell<DataQualityDisplayWrapper, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty()
                                    .getValue() < 0 ? 0
                                    : indexProperty().getValue();
                            double value = param
                                    .getTableView().getItems()
                                    .get(currentIndex).getCurrentness();
                            if (value > LATENCY_THRESHOLD) {
                                setTextFill(Color.RED);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else if (value < LATENCY_THRESHOLD){
                                setTextFill(Color.GREEN);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("-fx-font-weight: bold");
                                setText(String.format("%.3f",value));
                            }
                        }
                    }
                };
			}
			
		});
		
		averagesTable.getColumns().setAll(column1, column2, column3, column4, column5, column6, column7);
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
		
		numericList.addListener(new ListChangeListener<NumericFx>() {
			@Override
			public void onChanged(Change<? extends NumericFx> change) {
				while (change.next()) {
					change.getAddedSubList().forEach(n -> {
						n.presentation_timeProperty().addListener(new ChangeListener<Date>() {
							@Override
							public void changed(ObservableValue<? extends Date> observable, Date oldValue,
									Date newValue) {
								addDeviceMetric(n.getUnique_device_identifier(), n.getMetric_id(), n.getPresentation_time(), n, null);
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
								addDeviceMetric(n.getUnique_device_identifier(), n.getMetric_id(), n.getPresentation_time(), null, n);
							}
						});
					});
				}
			}
		});
	}
	
	private void addDeviceMetric(String deviceId, String metricId, Date presentationDate, NumericFx numeric, SampleArrayFx sampleArray) {
		String deviceMetricIdKey = getDeviceMetricKey(deviceId, metricId);
		Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).put(deviceMetricIdKey, new DataQualityMetric(deviceId, metricId, presentationDate, numeric, sampleArray));
		Collection<DataQualityMetric> collection = deviceNetworkQualityMetrics.get(deviceMetricIdKey);
		
		Optional<DataQualityMetric> findFirst = null;
		int count = collection.size();
		if(count > SAMPLE_SIZE) {
			findFirst = Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).get(deviceMetricIdKey).stream().findFirst();
			if(findFirst != null && findFirst.isPresent()) {
				DataQualityMetric first = findFirst.get();
				Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).get(deviceMetricIdKey).remove(first);
			}
		} else {
			
		}
		refreshCalculations();
		averagesTable.refresh();		
	}

	private String getDeviceMetricKey(String deviceId, String metricId) {
		String deviceMetricIdKey = deviceId+ KEY_COMBO +metricId;
		return deviceMetricIdKey;
	}
	
	private void refreshCalculations() {
		deviceNetworkQualityMetrics.keySet().forEach(n -> {
			Collection<DataQualityMetric> collection = deviceNetworkQualityMetrics.get(n);
			DataQualityDisplayWrapper wrapper = new DataQualityDisplayWrapper();
			List<String> deviceIdAndMetricId = parseDeviceIdAndMetricId(n);
			String deviceId = deviceIdAndMetricId.get(0);
			String metricId = deviceIdAndMetricId.get(1);
			wrapper.setDeviceId(deviceId);
			wrapper.setMetricId(metricId);
			boolean update = data.contains(wrapper);
			if(update) {
				DataQualityDisplayWrapper wrapperCopy = new DataQualityDisplayWrapper();
				wrapperCopy.setDeviceId(deviceId);
				wrapperCopy.setMetricId(metricId);
				wrapper = data.stream().filter(e -> {
					return e.equals(wrapperCopy);
				}).findFirst().orElse(null);
				wrapper.setAccuracy(determineAccuracy(collection));
				wrapper.setCompleteness(0.0);
				wrapper.setConsistency(0.0);
				wrapper.setCredibility(0.0);
				wrapper.setCurrentness(0.0);
			} else {
				wrapper.setAccuracy(determineAccuracy(collection));
				wrapper.setCompleteness(0.0);
				wrapper.setConsistency(0.0);
				wrapper.setCredibility(0.0);
				wrapper.setCurrentness(0.0);
				data.add(wrapper);
			}
		});
	}
	
	private Double determineAccuracy(Collection<DataQualityMetric> collection) {
		return collection.stream().mapToDouble(n -> {
			NumericFx numeric = n.getNumeric();
			SampleArrayFx sampleArray = n.getSampleArray();
			if(numeric != null) {
				return numeric.getSQI_accuracy();
			} else if(sampleArray != null) {
				return Math.random();
			} else {
				return 0;
			}
		}).average().orElse(0);
	}
	
	private List<String> parseDeviceIdAndMetricId(String n) {
		return Lists.newArrayList(Splitter.on(KEY_COMBO).split(n));
	}
	
	public void sendDataQualityErrorMessage(DataQualityAttributeType type, String deviceId, String metricId, Double average) {
		Date sentDate = sentNotifications.get(deviceId+metricId+type.toString());
		if(sentDate == null || sentDate.before(new Date(System.currentTimeMillis() - 30000))) {
			DataQualityErrorObjective dataQualityErrorObjective = new DataQualityErrorObjective();
			dataQualityErrorObjective.metric_id = metricId;
			dataQualityErrorObjective.unique_device_identifier = deviceId;
			dataQualityErrorObjective.data_quality_attribute_type = type;
			
			participant=assignedSubscriber.get_participant();
			
			DataQualityErrorObjectiveTypeSupport.register_type(participant, DataQualityErrorObjectiveTypeSupport.get_type_name());
			
			dataQualityErrorObjectiveTopic=TopicUtil.findOrCreateTopic(participant, DataQualityErrorObjectiveTopic.VALUE, DataQualityErrorObjectiveTypeSupport.class);
			
			publisher=parentContext.getBean("publisher", Publisher.class);
			dataQualityErrorObjectiveWriter=(DataQualityErrorObjectiveDataWriter)publisher.create_datawriter_with_profile(dataQualityErrorObjectiveTopic, QosProfiles.ice_library,
	                QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
			
			dataQualityErrorObjectiveHandle=dataQualityErrorObjectiveWriter.register_instance(dataQualityErrorObjective);
			
			sentNotifications.put(deviceId+metricId+type.toString(), new Date());
			dataQualityErrorObjectiveWriter.write(dataQualityErrorObjective, dataQualityErrorObjectiveHandle);
			System.out.println("Fallback Initiated by " + deviceId + " " + metricId + " " + " with average of " + average);
		}
	}
	
	public void stop() {
	}
	
	public void destroy() {
	}
}