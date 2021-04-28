package org.mdpnp.apps.testapp.dataqualitymonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DomainClock;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.springframework.context.ApplicationContext;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;

import ice.DataQualityAttributeType;
import ice.DataQualityErrorObjective;
import ice.DataQualityErrorObjectiveDataWriter;
import ice.DataQualityErrorObjectiveTopic;
import ice.DataQualityErrorObjectiveTypeSupport;
import ice.MDC_ECG_LEAD_I;
import ice.MDC_ECG_LEAD_II;
import ice.MDC_ECG_LEAD_III;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import rosetta.MDC_ECG_HEART_RATE;
import rosetta.MDC_FLOW_FLUID_PUMP;
import rosetta.MDC_PRESS_BLD_ART_ABP;
import rosetta.MDC_PRESS_BLD_ART_ABP_DIA;
import rosetta.MDC_PRESS_BLD_ART_ABP_SYS;
import rosetta.MDC_PRESS_BLD_DIA;
import rosetta.MDC_PRESS_BLD_MEAN;
import rosetta.MDC_PRESS_BLD_SYS;
import rosetta.MDC_PRESS_CUFF_DIA;
import rosetta.MDC_PRESS_CUFF_SYS;
import rosetta.MDC_PULS_OXIM_PERF_REL;
import rosetta.MDC_PULS_OXIM_PULS_RATE;
import rosetta.MDC_PULS_RATE;
import rosetta.MDC_PULS_RATE_NON_INV;

public class DataQualityMonitorApp {

	private static final long TIME_BETWEEN_REPOSTING_DQM_ERRORS_DEFAULT = 60000L;
	private static final int SAMPLE_SIZE_DEFAULT = 30;
	private static final float PULSE_CONSISTENCY_WINDOW_DEFAULT = 30.0f;
	private static final float BP_CONSISTENCY_WINDOW_DEFAULT = 60.0f;
	private static final int CURRENTNESS_THRESHOLD_DEFAULT = 10;
	private static final int CREDIBILITY_THRESHOLD_DEFAULT = 1;
	private static final int INFUSION_RATE_CONSISTENCY_THRESHOLD_DEFAULT = 30;
	private static final int BP_CONSISTENCY_THRESHOLD_DEFAULT = 20;
	private static final int PULSE_CONSISTENCY_THRESHOLD_DEFAULT = 20;
	private static final int CONSISTENCY_THRESHOLD_DEFAULT = 20;
	private static final int COMPLETENESS_THRESHOLD_DEFAULT = 95;
	private static final int ACCURACY_THRESHOLD_DEFAULT = 90;

	private static final String MDPNP_MONITOR_DATAQUALITY_SAMPLE_SIZE = "mdpnp.monitor.dataquality.samplesize";
	private static final String MDPNP_MONITOR_DATAQUALITY_ACCURACY_THRESHOLD = "mdpnp.monitor.dataquality.accuracythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_COMPLETENESS_THRESHOLD = "mdpnp.monitor.dataquality.completenessthreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_CONSISTENCY_THRESHOLD = "mdpnp.monitor.dataquality.consistencythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_THRESHOLD = "mdpnp.monitor.dataquality.pulseconsistencythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_THRESHOLD = "mdpnp.monitor.dataquality.bpconsistencythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_INFUSION_RATE_CONSISTENCY_THRESHOLD = "mdpnp.monitor.dataquality.infusionrateconsistencythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_CREDIBILITY_THRESHOLD = "mdpnp.monitor.dataquality.credibilitythreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_CURRENTNESS_THRESHOLD = "mdpnp.monitor.dataquality.currentnessthreshold";
	private static final String MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_WINDOW = "mdpnp.monitor.dataquality.bpconsistencywindow";
	private static final String MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_WINDOW = "mdpnp.monitor.dataquality.pulseconsistencywindow";
	private static final String MDPNP_MONITOR_DATAQUALITY_TIME_BETWEEN_REPOSTING_DQM_ERRORS = "mdpnp.monitor.dataquality.timebetweendqmerrors";

	private static final String KEY_COMBO = "::::";

	private static int SAMPLE_SIZE;
	private static int ACCURACY_THRESHOLD;
	private static int COMPLETENESS_THRESHOLD;
	private static int CONSISTENCY_THRESHOLD;
	private static int PULSE_CONSISTENCY_THRESHOLD;
	private static int BP_CONSISTENCY_THRESHOLD;
	private static int INFUSION_RATE_CONSISTENCY_THRESHOLD;
	private static int CREDIBILITY_THRESHOLD;
	private static int CURRENTNESS_THRESHOLD;
	private static double BP_CONSISTENCY_WINDOW;
	private static double PULSE_CONSISTENCY_WINDOW;
	private static long TIME_BETWEEN_REPOSTING_DQM_ERRORS;

	@FXML
	TreeTableView<DataQualityDisplayWrapper> averagesTable;
	private NumericFxList numericList;
	private SampleArrayFxList sampleFxList;
	private DeviceListModel deviceListModel;
	private Map<String, Device> devices = new HashMap<String, Device>();
	private Subscriber assignedSubscriber; // Use a slightly different name here to avoid poss conflict with any other
											// subscriber variable.
	private Subscriber constructorSubscriber;
	private EventLoop eventLoop;
	private ApplicationContext parentContext;
	private Multimap<String, DataQualityMetric> deviceNetworkQualityMetrics;
	private ObservableList<DataQualityDisplayWrapper> data = FXCollections.observableArrayList();
	private TreeItem<DataQualityDisplayWrapper> root = new TreeItem<DataQualityDisplayWrapper>();
	private List<TreeItem<DataQualityDisplayWrapper>> deviceSiblingRoots = new ArrayList<TreeItem<DataQualityDisplayWrapper>>();

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
	protected final DeviceClock clock = new DeviceClock.WallClock();

	/**
	 * Instance handle for SafetyFallbackObjective
	 */
	private InstanceHandle_t dataQualityErrorObjectiveHandle;

	private Map<String, Date> sentNotifications;

	public DataQualityMonitorApp() {

		SAMPLE_SIZE = System.getProperty(MDPNP_MONITOR_DATAQUALITY_SAMPLE_SIZE) == null ? SAMPLE_SIZE_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_SAMPLE_SIZE));

		ACCURACY_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_ACCURACY_THRESHOLD) == null
				? ACCURACY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_ACCURACY_THRESHOLD));

		COMPLETENESS_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_COMPLETENESS_THRESHOLD) == null
				? COMPLETENESS_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_COMPLETENESS_THRESHOLD));

		CONSISTENCY_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_CONSISTENCY_THRESHOLD) == null
				? CONSISTENCY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_CONSISTENCY_THRESHOLD));

		PULSE_CONSISTENCY_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_THRESHOLD) == null
				? PULSE_CONSISTENCY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_THRESHOLD));

		BP_CONSISTENCY_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_THRESHOLD) == null
				? BP_CONSISTENCY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_THRESHOLD));

		INFUSION_RATE_CONSISTENCY_THRESHOLD = System
				.getProperty(MDPNP_MONITOR_DATAQUALITY_INFUSION_RATE_CONSISTENCY_THRESHOLD) == null
						? INFUSION_RATE_CONSISTENCY_THRESHOLD_DEFAULT
						: Integer.parseInt(
								System.getProperty(MDPNP_MONITOR_DATAQUALITY_INFUSION_RATE_CONSISTENCY_THRESHOLD));

		CREDIBILITY_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_CREDIBILITY_THRESHOLD) == null
				? CREDIBILITY_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_CREDIBILITY_THRESHOLD));

		CURRENTNESS_THRESHOLD = System.getProperty(MDPNP_MONITOR_DATAQUALITY_CURRENTNESS_THRESHOLD) == null
				? CURRENTNESS_THRESHOLD_DEFAULT
				: Integer.parseInt(System.getProperty(MDPNP_MONITOR_DATAQUALITY_CURRENTNESS_THRESHOLD));

		BP_CONSISTENCY_WINDOW = System.getProperty(MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_WINDOW) == null
				? BP_CONSISTENCY_WINDOW_DEFAULT
				: Double.parseDouble(System.getProperty(MDPNP_MONITOR_DATAQUALITY_BP_CONSISTENCY_WINDOW));

		PULSE_CONSISTENCY_WINDOW = System.getProperty(MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_WINDOW) == null
				? PULSE_CONSISTENCY_WINDOW_DEFAULT
				: Double.parseDouble(System.getProperty(MDPNP_MONITOR_DATAQUALITY_PULSE_CONSISTENCY_WINDOW));

		TIME_BETWEEN_REPOSTING_DQM_ERRORS = System
				.getProperty(MDPNP_MONITOR_DATAQUALITY_TIME_BETWEEN_REPOSTING_DQM_ERRORS) == null
						? TIME_BETWEEN_REPOSTING_DQM_ERRORS_DEFAULT
						: Long.parseLong(
								System.getProperty(MDPNP_MONITOR_DATAQUALITY_TIME_BETWEEN_REPOSTING_DQM_ERRORS));
		sentNotifications = new HashMap<String, Date>();
	}

	public void activate() {
		setupTable();
	}

	@SuppressWarnings("unchecked")
	private void setupTable() {
		averagesTable.setEditable(false);
		TreeTableViewSelectionModel<DataQualityDisplayWrapper> defaultSelectionModel = averagesTable
				.getSelectionModel();
		averagesTable.setSelectionModel(defaultSelectionModel);
		averagesTable.setRoot(root);
		averagesTable.setShowRoot(false);

		TreeTableColumn<DataQualityDisplayWrapper, String> column1 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column1.setText("Device");
		column1.setMinWidth(10);
		column1.setMaxWidth(5000);
		column1.setPrefWidth(320);
		column1.setSortable(true);
		column1.setEditable(false);
		column1.setResizable(false);
		column1.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						p.getValue().getValue().getName()));

		TreeTableColumn<DataQualityDisplayWrapper, String> column2 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column2.setText("MetricId");
		column2.setMinWidth(10);
		column2.setMaxWidth(5000);
		column2.setPrefWidth(320);
		column2.setSortable(true);
		column2.setEditable(false);
		column2.setResizable(false);
		column2.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						p.getValue().getValue().getMetricId()));

		TreeTableColumn<DataQualityDisplayWrapper, String> column3 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column3.setText("Accuracy %");
		column3.setMinWidth(10);
		column3.setMaxWidth(5000);
		column3.setPrefWidth(185);
		column3.setSortable(true);
		column3.setEditable(false);
		column3.setResizable(false);
		column3.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						String.format("%.3f", p.getValue().getValue().getAccuracy())));
		column3.setCellFactory(
				new Callback<TreeTableColumn<DataQualityDisplayWrapper, String>, TreeTableCell<DataQualityDisplayWrapper, String>>() {

					@Override
					public TreeTableCell<DataQualityDisplayWrapper, String> call(
							TreeTableColumn<DataQualityDisplayWrapper, String> param) {
						return new TreeTableCell<DataQualityDisplayWrapper, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								if (!empty) {
									int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
									TreeItem<DataQualityDisplayWrapper> treeItem = param.getTreeTableView()
											.getTreeItem(currentIndex);
									DataQualityDisplayWrapper treeItemValue = treeItem.getValue();
									if (treeItem.getChildren().isEmpty() && treeItemValue.getAccuracy() != null) {
										double value = treeItemValue.getAccuracy();
										if (value > ACCURACY_THRESHOLD) {
											setTextFill(Color.GREEN);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else if (value < ACCURACY_THRESHOLD) {
											setTextFill(Color.RED);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else {
											setTextFill(Color.BLACK);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										}
									}
								}
							}
						};
					}

				});

		TreeTableColumn<DataQualityDisplayWrapper, String> column4 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column4.setText("Completeness %");
		column4.setMinWidth(10);
		column4.setMaxWidth(5000);
		column4.setPrefWidth(185);
		column4.setSortable(true);
		column4.setEditable(false);
		column4.setResizable(false);
		column4.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						String.format("%.3f", p.getValue().getValue().getCompleteness())));
		column4.setCellFactory(
				new Callback<TreeTableColumn<DataQualityDisplayWrapper, String>, TreeTableCell<DataQualityDisplayWrapper, String>>() {

					@Override
					public TreeTableCell<DataQualityDisplayWrapper, String> call(
							TreeTableColumn<DataQualityDisplayWrapper, String> param) {
						return new TreeTableCell<DataQualityDisplayWrapper, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								if (!empty) {
									int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
									TreeItem<DataQualityDisplayWrapper> treeItem = param.getTreeTableView()
											.getTreeItem(currentIndex);
									DataQualityDisplayWrapper treeItemValue = treeItem.getValue();
									if (treeItem.getChildren().isEmpty() && treeItemValue.getCompleteness() != null) {
										double value = treeItem.getValue().getCompleteness();
										value = value > 100.0f ? 100.0f : value;
										if (value > COMPLETENESS_THRESHOLD) {
											setTextFill(Color.GREEN);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else if (value < COMPLETENESS_THRESHOLD) {
											setTextFill(Color.RED);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else {
											setTextFill(Color.BLACK);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										}
									}
								}
							}
						};
					}

				});

		TreeTableColumn<DataQualityDisplayWrapper, String> column5 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column5.setText("Consistency");
		column5.setMinWidth(10);
		column5.setMaxWidth(5000);
		column5.setPrefWidth(185);
		column5.setSortable(true);
		column5.setEditable(false);
		column5.setResizable(false);
		column5.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						String.format("%.3f", p.getValue().getValue().getConsistency())));
		column5.setCellFactory(
				new Callback<TreeTableColumn<DataQualityDisplayWrapper, String>, TreeTableCell<DataQualityDisplayWrapper, String>>() {

					@Override
					public TreeTableCell<DataQualityDisplayWrapper, String> call(
							TreeTableColumn<DataQualityDisplayWrapper, String> param) {
						return new TreeTableCell<DataQualityDisplayWrapper, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								if (!empty) {
									int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
									TreeItem<DataQualityDisplayWrapper> treeItem = param.getTreeTableView()
											.getTreeItem(currentIndex);
									DataQualityDisplayWrapper treeItemValue = treeItem.getValue();
									if (treeItem.getChildren().isEmpty() && treeItemValue.getConsistency() != null) {
										double value = treeItem.getValue().getConsistency();
										int thresholdToUse = 0;
										switch (treeItemValue.getMetricId()) {
										case MDC_PULS_RATE_NON_INV.VALUE:
										case MDC_PRESS_CUFF_DIA.VALUE:
										case MDC_PRESS_CUFF_SYS.VALUE:
										case MDC_PRESS_BLD_SYS.VALUE:
										case MDC_PRESS_BLD_DIA.VALUE:
											thresholdToUse = BP_CONSISTENCY_THRESHOLD;
											break;
										case MDC_PULS_OXIM_PULS_RATE.VALUE:
										case MDC_ECG_HEART_RATE.VALUE:
											thresholdToUse = PULSE_CONSISTENCY_THRESHOLD;
											break;
										case MDC_FLOW_FLUID_PUMP.VALUE:
											thresholdToUse = INFUSION_RATE_CONSISTENCY_THRESHOLD;
											break;
										default:
											thresholdToUse = CONSISTENCY_THRESHOLD;
											break;
										}
										if (value > thresholdToUse) {
											setTextFill(Color.RED);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else if (value < thresholdToUse) {
											setTextFill(Color.GREEN);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else {
											setTextFill(Color.BLACK);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										}
									}
								}
							}
						};
					}

				});

		TreeTableColumn<DataQualityDisplayWrapper, String> column6 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column6.setText("Credibility");
		column6.setMinWidth(10);
		column6.setMaxWidth(5000);
		column6.setPrefWidth(185);
		column6.setSortable(true);
		column6.setEditable(false);
		column6.setResizable(false);
		column6.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						String.format("%.3f", p.getValue().getValue().getCredibility())));
		column6.setCellFactory(
				new Callback<TreeTableColumn<DataQualityDisplayWrapper, String>, TreeTableCell<DataQualityDisplayWrapper, String>>() {

					@Override
					public TreeTableCell<DataQualityDisplayWrapper, String> call(
							TreeTableColumn<DataQualityDisplayWrapper, String> param) {
						return new TreeTableCell<DataQualityDisplayWrapper, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								if (!empty) {
									int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
									TreeItem<DataQualityDisplayWrapper> treeItem = param.getTreeTableView()
											.getTreeItem(currentIndex);
									DataQualityDisplayWrapper treeItemValue = treeItem.getValue();
									if (treeItem.getChildren().isEmpty() && treeItemValue.getCredibility() != null) {
										double value = treeItem.getValue().getCredibility();
										if (value >= CREDIBILITY_THRESHOLD) {
											setTextFill(Color.GREEN);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else if (value < CREDIBILITY_THRESHOLD) {
											setTextFill(Color.RED);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else {
											setTextFill(Color.BLACK);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										}
									}
								}
							}
						};
					}
				});

		TreeTableColumn<DataQualityDisplayWrapper, String> column7 = new TreeTableColumn<DataQualityDisplayWrapper, String>();
		column7.setText("Currentness (ms)");
		column7.setMinWidth(10);
		column7.setMaxWidth(5000);
		column7.setPrefWidth(185);
		column7.setSortable(true);
		column7.setEditable(false);
		column7.setResizable(false);
		column7.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<DataQualityDisplayWrapper, String> p) -> new SimpleStringProperty(
						String.format("%.3f", p.getValue().getValue().getCurrentness())));
		column7.setCellFactory(
				new Callback<TreeTableColumn<DataQualityDisplayWrapper, String>, TreeTableCell<DataQualityDisplayWrapper, String>>() {

					@Override
					public TreeTableCell<DataQualityDisplayWrapper, String> call(
							TreeTableColumn<DataQualityDisplayWrapper, String> param) {
						return new TreeTableCell<DataQualityDisplayWrapper, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								if (!empty) {
									int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
									TreeItem<DataQualityDisplayWrapper> treeItem = param.getTreeTableView()
											.getTreeItem(currentIndex);
									DataQualityDisplayWrapper treeItemValue = treeItem.getValue();
									if (treeItem.getChildren().isEmpty() && treeItemValue.getCurrentness() != null) {
										double value = treeItem.getValue().getCurrentness();
										if (value > CURRENTNESS_THRESHOLD) {
											setTextFill(Color.RED);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else if (value < CURRENTNESS_THRESHOLD) {
											setTextFill(Color.GREEN);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										} else {
											setTextFill(Color.BLACK);
											setStyle("-fx-font-weight: bold");
											setText(String.format("%.3f", value));
										}
									}
								}
							}
						};
					}

				});

		averagesTable.getColumns().setAll(column1, column2, column3, column4, column5, column6, column7);
		averagesTable.autosize();
	}

	public void set(ApplicationContext context, Subscriber subscriber, DeviceListModel deviceListModel,
			NumericFxList numericFxList, SampleArrayFxList sampleFxList) {
		this.parentContext = context;
		this.assignedSubscriber = subscriber;
		this.deviceListModel = deviceListModel;
		this.numericList = numericFxList;
		this.sampleFxList = sampleFxList;
	}

	public void start(EventLoop eventLoop, Subscriber subscriber) {
		deviceListModel.getContents().forEach(d -> {
			devices.put(d.getUDI(), d);
			addSiblingRoot(d);
		});

		deviceListModel.getContents().addListener(new ListChangeListener<Device>() {
			@Override
			public void onChanged(Change<? extends Device> change) {
				while (change.next()) {
					change.getAddedSubList().forEach(d -> {
						devices.put(d.getUDI(), d);
						addSiblingRoot(d);
					});
					change.getRemoved().forEach(d -> {
						devices.remove(d.getUDI());
						removeSiblingRoot(d);
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
								addDeviceMetric(n.getUnique_device_identifier(), n.getMetric_id(),
										n.getPresentation_time(), n, null);
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
								addDeviceMetric(n.getUnique_device_identifier(), n.getMetric_id(),
										n.getPresentation_time(), null, n);
							}
						});
					});
				}
			}
		});
	}

	private void addSiblingRoot(Device d) {
		if (deviceSiblingRoots.stream().noneMatch(n -> {
			return d.getUDI().equals(n.getValue().getDeviceId());
		})) {
			TreeItem<DataQualityDisplayWrapper> siblingRoot = new TreeItem<DataQualityDisplayWrapper>();
			DataQualityDisplayWrapper wrapper = new DataQualityDisplayWrapper();
			wrapper.setDeviceId(d.getUDI());
			siblingRoot.setValue(wrapper);
			deviceSiblingRoots.add(siblingRoot);
			root.getChildren().add(siblingRoot);
		}
	}

	private void removeSiblingRoot(Device d) {
		deviceSiblingRoots.removeIf(n -> {
			return n.getValue().getDeviceId().equals(d.getUDI());
		});
		root.getChildren().removeIf(n -> {
			return n.getValue().getDeviceId().equals(d.getUDI());
		});
	}

	private void addDeviceMetric(String deviceId, String metricId, Date presentationDate, NumericFx numeric,
			SampleArrayFx sampleArray) {
		String deviceMetricIdKey = getDeviceMetricKey(deviceId, metricId);
		int sampleCount = 0;
		double frequency = 0;
		if (sampleArray != null) {
			sampleCount = sampleCount + sampleArray.getValues().length;
			frequency = sampleArray.getFrequency();
		}
		if (numeric != null) {
			sampleCount++;
			frequency = frequency + numeric.getSQI_frequency();
		}

		Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).put(deviceMetricIdKey, new DataQualityMetric(
				deviceId, metricId, presentationDate, numeric, sampleArray, sampleCount, frequency));
		Collection<DataQualityMetric> collection = deviceNetworkQualityMetrics.get(deviceMetricIdKey);

		Optional<DataQualityMetric> findFirst = null;
		int count = collection.size();
		if (count > SAMPLE_SIZE) {
			findFirst = deviceNetworkQualityMetrics.get(deviceMetricIdKey).stream().findFirst();
			if (findFirst != null && findFirst.isPresent()) {
				DataQualityMetric first = findFirst.get();
				Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics).get(deviceMetricIdKey).remove(first);
				// TODO: Performance improvement to not re-calculate data quality attribute from
				// scratch and use changed values to simplify complexity
			}
		} else {

		}
		buildTree();
		averagesTable.refresh();
	}

	private String getDeviceMetricKey(String deviceId, String metricId) {
		String deviceMetricIdKey = deviceId + KEY_COMBO + metricId;
		return deviceMetricIdKey;
	}

	private void buildTree() {
		deviceNetworkQualityMetrics.keySet().forEach(p -> {
			Collection<DataQualityMetric> collection = deviceNetworkQualityMetrics.get(p);
			DataQualityDisplayWrapper wrapper = new DataQualityDisplayWrapper();
			List<String> deviceIdAndMetricId = parseDeviceIdAndMetricId(p);
			String deviceId = deviceIdAndMetricId.get(0);
			String metricId = deviceIdAndMetricId.get(1);
			wrapper.setDeviceId(deviceId);
			wrapper.setMetricId(metricId);
			wrapper.setDeviceModel(deviceListModel.getByUniqueDeviceIdentifier(deviceId).getModel());
			boolean update = data.contains(wrapper);
			if (update) {
				DataQualityDisplayWrapper wrapperCopy = new DataQualityDisplayWrapper();
				wrapperCopy.setDeviceId(deviceId);
				wrapperCopy.setMetricId(metricId);
				wrapper = data.stream().filter(e -> {
					return e.equals(wrapperCopy);
				}).findFirst().orElse(null);
				wrapper.setAccuracy(determineAccuracy(collection));
				setCompleteness(collection, wrapper, deviceId, metricId);
				setConsistency(collection, wrapper, deviceId, metricId);
				setCredibility(collection, wrapper, deviceId, metricId);
				setCurrentness(collection, wrapper, deviceId, metricId);
			} else {
				wrapper.setAccuracy(determineAccuracy(collection));
				setCompleteness(collection, wrapper, deviceId, metricId);
				setConsistency(collection, wrapper, deviceId, metricId);
				setCredibility(collection, wrapper, deviceId, metricId);
				setCurrentness(collection, wrapper, deviceId, metricId);
				data.add(wrapper);
			}
			checkWrapper(wrapper);
		});
		root.getChildren().forEach(n -> {
			DataQualityDisplayWrapper wrapper = n.getValue();
			String deviceId = wrapper.getDeviceId();
			if (wrapper.getDeviceModel() == null || wrapper.getDeviceModel().isEmpty()) {
				wrapper.setDeviceModel((deviceListModel.getByUniqueDeviceIdentifier(deviceId).getModel()));
				wrapper.setName(wrapper.getDeviceModel() + "-" + wrapper.getDeviceId());
			}

			data.stream().filter(s -> {
				return s.getDeviceId().equals(deviceId);
			}).forEach(match -> {
				TreeItem<DataQualityDisplayWrapper> treeItem = new TreeItem<DataQualityDisplayWrapper>();
				treeItem.setValue(match);
				ObservableList<TreeItem<DataQualityDisplayWrapper>> children = n.getChildren();
				if (children.stream().noneMatch(child -> {
					return child.getValue().getMetricId().equals(match.getMetricId());
				})) {
					children.add(treeItem);
				}
			});
		});
	}

	private void setCompleteness(Collection<DataQualityMetric> collection, DataQualityDisplayWrapper wrapper,
			String deviceId, String metricId) {
		switch (metricId) {
		case MDC_PRESS_CUFF_DIA.VALUE:
		case MDC_PRESS_CUFF_SYS.VALUE:
		case MDC_PRESS_BLD_SYS.VALUE:
		case MDC_PRESS_BLD_DIA.VALUE:
			Double timeDifferenceNIBP = determineTimeDifferenceNIBP(deviceId);
			wrapper.setCompleteness(
					timeDifferenceNIBP != null && timeDifferenceNIBP < 1 && timeDifferenceNIBP >= 0 ? 100.000 : 0.000);
			break;
		default:
			wrapper.setCompleteness(determineCompleteness(collection));
			break;
		}
	}

	private Double determineTimeDifferenceNIBP(String deviceId) {
		Collection<DataQualityMetric> prNIBP = deviceNetworkQualityMetrics
				.get(getDeviceMetricKey(deviceId, MDC_PULS_RATE_NON_INV.VALUE));
		Collection<DataQualityMetric> diastolicNIBP = deviceNetworkQualityMetrics
				.get(getDeviceMetricKey(deviceId, MDC_PRESS_CUFF_DIA.VALUE));
		Collection<DataQualityMetric> systolicNIBP = deviceNetworkQualityMetrics
				.get(getDeviceMetricKey(deviceId, MDC_PRESS_CUFF_SYS.VALUE));
		prNIBP = prNIBP == null || prNIBP.size() == 0
				? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_ECG_HEART_RATE.VALUE))
				: prNIBP;
		diastolicNIBP = diastolicNIBP == null || diastolicNIBP.size() == 0
				? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_DIA.VALUE))
				: diastolicNIBP;
		systolicNIBP = systolicNIBP == null || systolicNIBP.size() == 0
				? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_SYS.VALUE))
				: systolicNIBP;
		Date dbpDate = diastolicNIBP.stream().map(q -> {
			return q.getPresentationDate();
		}).max(Date::compareTo).orElse(null);
		Date sbpDate = systolicNIBP.stream().map(q -> {
			return q.getPresentationDate();
		}).max(Date::compareTo).orElse(null);
		Date prDate = prNIBP.stream().map(q -> {
			return q.getPresentationDate();
		}).max(Date::compareTo).orElse(null);
		if (dbpDate != null && sbpDate != null && prDate != null) {
			List<Date> dates = new ArrayList<Date>();
			dates.add(dbpDate);
			dates.add(sbpDate);
			dates.add(prDate);
			Date max = dates.stream().max(Date::compareTo).get();
			Date min = dates.stream().min(Date::compareTo).get();
			double seconds = (max.getTime() - min.getTime()) / 1000.0;
			return seconds;
		} else {
			return null;
		}
	}

	private Double determineAccuracy(Collection<DataQualityMetric> collection) {
		double average = collection.stream().mapToDouble(n -> {
			NumericFx numeric = n.getNumeric();
			SampleArrayFx sampleArray = n.getSampleArray();
			if (numeric != null) {
				return numeric.getSQI_accuracy();
			} else if (sampleArray != null) {
				return sampleArray.getSQI_accuracy();
			} else {
				return 0;
			}
		}).average().orElse(0);

		return average == 0.000 ? null : average;
	}

	private Double determineCompleteness(Collection<DataQualityMetric> collection) {
		return collection.stream().mapToDouble(n -> {
			Date minDate = collection.stream().map(p -> {
				return p.getPresentationDate();
			}).min(Date::compareTo).get();
			Date maxDate = collection.stream().map(p -> {
				return p.getPresentationDate();
			}).max(Date::compareTo).get();
			double elapsedTime = (maxDate.getTime() - minDate.getTime()) / 1000;
			double sumOfSamples = collection.stream().mapToInt(p -> {
				return p.getSampleCount();
			}).sum();

			double perfectSumOfSamples = n.getFrequency() * elapsedTime;
			return sumOfSamples / perfectSumOfSamples * 100.000f;
		}).average().orElse(0);
	}

	private void setCredibility(Collection<DataQualityMetric> collection, DataQualityDisplayWrapper wrapper,
			String deviceId, String metricId) {
		switch (metricId) {
		case MDC_PRESS_BLD_ART_ABP.VALUE:
			Device device = deviceListModel.getByUniqueDeviceIdentifier(deviceId);
			String model = device.getMakeAndModel();
			DataQualityMetric abp = Iterables.getLast(collection);
			float[] minAndMax = getMinAndMax(abp.getSampleArray().getValues());
			if(model != null && model.startsWith("Multiparameter (Simulated)")) {
				wrapper.setCredibility(bpCredible(minAndMax[0]/5.000f, minAndMax[1]/5.000f) ? 1.00 : 0.00);
			} else {
				wrapper.setCredibility(bpCredible(minAndMax[0], minAndMax[1]) ? 1.00 : 0.00);
			}
			break;
		case MDC_PRESS_BLD_ART_ABP_DIA.VALUE:
		case MDC_PRESS_BLD_ART_ABP_SYS.VALUE:
		case MDC_PRESS_BLD_SYS.VALUE:
		case MDC_PRESS_BLD_DIA.VALUE:
			Collection<DataQualityMetric> diastolicNIBP = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_ART_ABP_DIA.VALUE));
			diastolicNIBP = diastolicNIBP == null || diastolicNIBP.size() == 0
					? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_DIA.VALUE))
					: diastolicNIBP;
			Collection<DataQualityMetric> systolicNIBP = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_ART_ABP_SYS.VALUE));
			systolicNIBP = systolicNIBP == null || systolicNIBP.size() == 0
					? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_SYS.VALUE))
					: systolicNIBP;
			if (diastolicNIBP != null && diastolicNIBP.size() > 0 && systolicNIBP != null && systolicNIBP.size() > 0) {
				float diastolic = Iterables.getLast(diastolicNIBP).getNumeric().getValue();
				float systolic = Iterables.getLast(systolicNIBP).getNumeric().getValue();
				wrapper.setCredibility(bpCredible(diastolic, systolic) ? 1.00 : 0.00);
			}
			break;
		case MDC_PULS_OXIM_PULS_RATE.VALUE:
		case MDC_ECG_HEART_RATE.VALUE:
		case MDC_PULS_RATE_NON_INV.VALUE:
		case MDC_PULS_RATE.VALUE:
			if (collection != null && collection.size() > 0) {
				float heartRate = Iterables.getLast(collection).getNumeric().getValue();
				double credible = 1.0;
				if (heartRate < 20 || heartRate > 200) {
					credible = 0.0;
				}
				wrapper.setCredibility(credible);
			}
			break;
		case MDC_PULS_OXIM_PERF_REL.VALUE:
			Collection<DataQualityMetric> perfusionIndex = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PULS_OXIM_PERF_REL.VALUE));
			if (perfusionIndex != null && perfusionIndex.size() > 0) {
				double credible = 1.0;
				double perfusionIndexRateAverage = perfusionIndex.stream().mapToDouble(n -> n.getNumeric().getValue())
						.average().orElse(0.0);
				if (perfusionIndexRateAverage < 0.3) {
					credible = 0.0;
				}
				wrapper.setCredibility(credible);
			}
			break;
		}
	}

	private boolean bpCredible(float diastolic, float systolic) {
		double pm = diastolic + (systolic - diastolic) / 3;
		return !(diastolic <= 20 || systolic >= 300 || (systolic - diastolic) < 20 || pm < 30 || pm > 200);
	}

	private void setCurrentness(Collection<DataQualityMetric> collection, DataQualityDisplayWrapper wrapper,
			String deviceId, String metricId) {

		DataQualityMetric first = Iterables.getFirst(collection, new DataQualityMetric());
		if (first.getNumeric() != null) {
			wrapper.setCurrentness(collection.stream().mapToDouble(n -> {
				return Double.valueOf(n.getNumeric().getDelta());
			}).average().orElse(0.0));
		} else {
			wrapper.setCurrentness(collection.stream().mapToDouble(n -> {
				return n.getSampleArray().getDelta();
			}).average().orElse(0.0));
		}
	}

	@SuppressWarnings("unchecked")
	private void setConsistency(Collection<DataQualityMetric> collection, DataQualityDisplayWrapper wrapper,
			String deviceId, String metricId) {
		switch (metricId) {
		case MDC_PRESS_CUFF_DIA.VALUE:
		case MDC_PRESS_CUFF_SYS.VALUE:
		case MDC_PRESS_BLD_SYS.VALUE:
		case MDC_PRESS_BLD_DIA.VALUE:
		case MDC_PRESS_BLD_MEAN.VALUE:
			Collection<DataQualityMetric> diastolicNIBP = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PRESS_CUFF_DIA.VALUE));
			diastolicNIBP = diastolicNIBP == null || diastolicNIBP.size() == 0
					? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_DIA.VALUE))
					: diastolicNIBP;
			Collection<DataQualityMetric> systolicNIBP = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PRESS_CUFF_SYS.VALUE));
			systolicNIBP = systolicNIBP == null || systolicNIBP.size() == 0
					? deviceNetworkQualityMetrics.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_SYS.VALUE))
					: systolicNIBP;
			Collection<DataQualityMetric> meanIBP = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PRESS_BLD_MEAN.VALUE));
			if (diastolicNIBP != null && diastolicNIBP.size() > 0 && systolicNIBP != null && systolicNIBP.size() > 0
					&& meanIBP != null && meanIBP.size() > 0) {
				Date meanIBPDate = meanIBP.stream().map(q -> {
					return q.getPresentationDate();
				}).max(Date::compareTo).orElse(null);
				if (meanIBPDate != null) {
					Date bpBeforeDate = new Date((long) (meanIBPDate.getTime() - BP_CONSISTENCY_WINDOW * 1000.0));
					Predicate<? super DataQualityMetric> dateFilterPredicate = n -> {
						return n.getPresentationDate().after(bpBeforeDate)
								|| n.getPresentationDate().equals(bpBeforeDate);
					};
					ToDoubleFunction<? super DataQualityMetric> wrapperToNumericDoubleMapper = n -> {
						return n.getNumeric().getValue();
					};
					double averageMeanIBP = meanIBP.stream().filter(dateFilterPredicate)
							.mapToDouble(wrapperToNumericDoubleMapper).average().orElse(0.0);
					double averageSystolicNIBP = systolicNIBP.stream().filter(dateFilterPredicate)
							.mapToDouble(wrapperToNumericDoubleMapper).average().orElse(0.0);
					double averageDiastolicNIBP = diastolicNIBP.stream().filter(dateFilterPredicate)
							.mapToDouble(wrapperToNumericDoubleMapper).average().orElse(0.0);
					double calculatedNIBPMAP = ((2.0 * averageDiastolicNIBP) + averageSystolicNIBP) / 3;
					wrapper.setConsistency(Math.abs(averageMeanIBP - calculatedNIBPMAP));
				}
			}
			break;
		case MDC_PULS_OXIM_PULS_RATE.VALUE:
		case MDC_ECG_HEART_RATE.VALUE:
		case MDC_PULS_RATE_NON_INV.VALUE:
		case MDC_PULS_RATE.VALUE:
			Collection<DataQualityMetric> pulseCollection = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_PULS_OXIM_PULS_RATE.VALUE));
			Collection<DataQualityMetric> heartRateCollection = Multimaps
					.synchronizedMultimap(deviceNetworkQualityMetrics)
					.get(getDeviceMetricKey(deviceId, MDC_ECG_HEART_RATE.VALUE));
			Collection<DataQualityMetric> prNINV = Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics)
					.get(getDeviceMetricKey(deviceId, MDC_PULS_RATE_NON_INV.VALUE));
			Collection<DataQualityMetric> pr = Multimaps.synchronizedMultimap(deviceNetworkQualityMetrics)
					.get(getDeviceMetricKey(deviceId, MDC_PULS_RATE.VALUE));
			Collection<DataQualityMetric> decidingCollection = null;
			if (metricId.equals(MDC_PULS_OXIM_PULS_RATE.VALUE)) {
				decidingCollection = pulseCollection;
			} else if (metricId.equals(MDC_ECG_HEART_RATE.VALUE)) {
				decidingCollection = heartRateCollection;
			} else if (metricId.equals(MDC_PULS_RATE_NON_INV.VALUE)) {
				decidingCollection = prNINV;
			} else if (metricId.equals(MDC_PULS_RATE.VALUE)) {
				decidingCollection = pr;
			}
			Date compareDate = decidingCollection.stream().map(q -> {
				return q.getPresentationDate();
			}).max(Date::compareTo).orElse(null);
			if (compareDate != null) {
				Date beforeDate = new Date((long) (compareDate.getTime() - PULSE_CONSISTENCY_WINDOW * 1000.0));
				wrapper.setConsistency(stdDev(combineAndFilterCollectionsByDate(beforeDate, pulseCollection,
						heartRateCollection, prNINV, pr)));
			} else {
				wrapper.setConsistency(null);
			}
			break;
		case MDC_ECG_LEAD_I.VALUE:
		case MDC_ECG_LEAD_II.VALUE:
		case MDC_ECG_LEAD_III.VALUE:
			Collection<DataQualityMetric> collectionI = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_ECG_LEAD_I.VALUE));
			Collection<DataQualityMetric> collectionII = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_ECG_LEAD_II.VALUE));
			Collection<DataQualityMetric> collectionIII = deviceNetworkQualityMetrics
					.get(getDeviceMetricKey(deviceId, MDC_ECG_LEAD_III.VALUE));
			Collection<DataQualityMetric> decidingCollectionI = null;
			if (metricId.equals(MDC_ECG_LEAD_I.VALUE)) {
				decidingCollectionI = collectionI;
			} else if (metricId.equals(MDC_ECG_LEAD_II.VALUE)) {
				decidingCollectionI = collectionII;
			} else if (metricId.equals(MDC_ECG_LEAD_III.VALUE)) {
				decidingCollectionI = collectionIII;
			}
			Date compareDateI = decidingCollectionI.stream().map(q -> {
				return q.getPresentationDate();
			}).max(Date::compareTo).orElse(null);
			if (compareDateI != null) {
				Date beforeDate = new Date((long) (compareDateI.getTime() - PULSE_CONSISTENCY_WINDOW * 1000.0));
				wrapper.setConsistency(stdDev(
						combineAndFilterCollectionsByDate(beforeDate, collectionI, collectionII, collectionIII)));
			}
			break;
		case MDC_FLOW_FLUID_PUMP.VALUE:
			if (collection.size() >= 2) {
				Float min = collection.stream().map(n -> {
					return n.getNumeric().getValue();
				}).min(Double::compare).get();
				Float max = collection.stream().map(n -> {
					return n.getNumeric().getValue();
				}).max(Double::compare).get();
				wrapper.setConsistency((double) Math.abs(max - min));
			}
			break;
		}
	}

	private void checkWrapper(DataQualityDisplayWrapper wrapper) {
		String metricId = wrapper.getMetricId();
		Double accuracy = wrapper.getAccuracy();
		if (accuracy != null && accuracy < ACCURACY_THRESHOLD) {
			sendDataQualityErrorMessage(DataQualityAttributeType.accuracy, wrapper.getDeviceId(), wrapper.getMetricId(),
					accuracy);
		}

		Double completeness = wrapper.getCompleteness();
		if (completeness != null && completeness < COMPLETENESS_THRESHOLD) {
			sendDataQualityErrorMessage(DataQualityAttributeType.completeness, wrapper.getDeviceId(),
					wrapper.getMetricId(), completeness);
		}

		Double consistency = wrapper.getConsistency();
		if (consistency != null) {
			switch (metricId) {
			case MDC_PRESS_CUFF_DIA.VALUE:
			case MDC_PRESS_CUFF_SYS.VALUE:
			case MDC_PRESS_BLD_SYS.VALUE:
			case MDC_PRESS_BLD_DIA.VALUE:
			case MDC_PRESS_BLD_MEAN.VALUE:
				if (consistency > BP_CONSISTENCY_THRESHOLD) {
					sendDataQualityErrorMessage(DataQualityAttributeType.consistency, wrapper.getDeviceId(),
							wrapper.getMetricId(), consistency);
				}
				break;
			case MDC_PULS_OXIM_PULS_RATE.VALUE:
			case MDC_ECG_HEART_RATE.VALUE:
				if (consistency > PULSE_CONSISTENCY_THRESHOLD) {
					sendDataQualityErrorMessage(DataQualityAttributeType.consistency, wrapper.getDeviceId(),
							wrapper.getMetricId(), consistency);
				}
				break;
			case MDC_FLOW_FLUID_PUMP.VALUE:
				if (consistency > INFUSION_RATE_CONSISTENCY_THRESHOLD) {
					sendDataQualityErrorMessage(DataQualityAttributeType.consistency, wrapper.getDeviceId(),
							wrapper.getMetricId(), consistency);
				}
				break;
			}
		}

		Double credibility = wrapper.getCredibility();
		if (credibility != null) {
			if (credibility < CREDIBILITY_THRESHOLD) {
				sendDataQualityErrorMessage(DataQualityAttributeType.credibility, wrapper.getDeviceId(),
						wrapper.getMetricId(), credibility);
			}
		}

		Double currentness = wrapper.getCurrentness();
		if (currentness != null) {
			if (currentness > CURRENTNESS_THRESHOLD) {
				sendDataQualityErrorMessage(DataQualityAttributeType.currentness, wrapper.getDeviceId(),
						wrapper.getMetricId(), currentness);
			}
		}
	}

	private List<String> parseDeviceIdAndMetricId(String n) {
		return Lists.newArrayList(Splitter.on(KEY_COMBO).split(n));
	}

	public void sendDataQualityErrorMessage(DataQualityAttributeType type, String deviceId, String metricId,
			Double average) {
		Date sentDate = sentNotifications.get(deviceId + metricId + type.toString());
		if (sentDate == null
				|| sentDate.before(new Date(System.currentTimeMillis() - TIME_BETWEEN_REPOSTING_DQM_ERRORS))) {
			DataQualityErrorObjective dataQualityErrorObjective = new DataQualityErrorObjective();
			dataQualityErrorObjective.metric_id = metricId;
			dataQualityErrorObjective.unique_device_identifier = deviceId;
			dataQualityErrorObjective.data_quality_attribute_type = type;
			dataQualityErrorObjective.presentation_time.copy_from(getTime());

			participant = assignedSubscriber.get_participant();

			DataQualityErrorObjectiveTypeSupport.register_type(participant,
					DataQualityErrorObjectiveTypeSupport.get_type_name());

			dataQualityErrorObjectiveTopic = TopicUtil.findOrCreateTopic(participant,
					DataQualityErrorObjectiveTopic.VALUE, DataQualityErrorObjectiveTypeSupport.class);

			publisher = parentContext.getBean("publisher", Publisher.class);
			dataQualityErrorObjectiveWriter = (DataQualityErrorObjectiveDataWriter) publisher
					.create_datawriter_with_profile(dataQualityErrorObjectiveTopic, QosProfiles.ice_library,
							QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

			dataQualityErrorObjectiveHandle = dataQualityErrorObjectiveWriter
					.register_instance(dataQualityErrorObjective);

			sentNotifications.put(deviceId + metricId + type.toString(), new Date());
			dataQualityErrorObjectiveWriter.write(dataQualityErrorObjective, dataQualityErrorObjectiveHandle);
			System.err.println("Data Quality Error: " + type.name() + " " + deviceId + " " + metricId
					+ " with value of " + String.format("%.3f", average));
		}
	}

	private ice.Time_t getTime() {
		Time_t ddsTime = DomainClock.toDDSTime(clock.instant().getTime());
		ice.Time_t iceTime = (ice.Time_t) ice.Time_t.create();
		iceTime.nanosec = ddsTime.nanosec;
		iceTime.sec = ddsTime.sec;
		return iceTime;
	}

	@SuppressWarnings("unchecked")
	private List<Double> combineAndFilterCollectionsByDate(Date beforeDate,
			Collection<DataQualityMetric>... collections) {
		List<DataQualityMetric> combined = Stream.of(collections).flatMap(Collection::stream).filter(n -> {
			return n.getPresentationDate().after(beforeDate) || n.getPresentationDate().equals(beforeDate);
		}).collect(Collectors.toList());
		if (combined.get(0).getNumeric() != null) {
			return combined.stream().map(n -> {
				return Double.valueOf(n.getNumeric().getValue());
			}).collect(Collectors.toList());
		} else {
			return combined.stream().map(n -> {
				return n.getSampleArray().getValues();
			}).flatMap(n -> {
				return Arrays.stream(n);
			}).map(n -> n.doubleValue()).collect(Collectors.toList());
		}
	}

	private double stdDev(List<Double> input) {
		double[] array = input.stream().mapToDouble(Double::doubleValue).toArray();
		DescriptiveStatistics ds = new DescriptiveStatistics(array);
		return ds.getStandardDeviation();
	}

	private float[] getMinAndMax(Number[] numbers) {
		float[] minAndMax = new float[] { numbers[0].floatValue(), numbers[0].floatValue() };
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i].floatValue() < minAndMax[0])
				minAndMax[0] = numbers[i].floatValue();
			if (numbers[i].floatValue() > minAndMax[1])
				minAndMax[1] = numbers[i].floatValue();
		}

		return minAndMax;
	}

	public void stop() {
	}

	public void destroy() {
	}
}