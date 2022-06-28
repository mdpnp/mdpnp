package org.mdpnp.apps.testapp.vents.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.swing.ListCellRenderer;

import org.mdpnp.apps.device.DeviceDataMonitor;
import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.HumanReadable;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.devices.MDSHandler;
import org.springframework.context.ApplicationContext;

import com.rti.dds.infrastructure.InstanceHandle_t;

import ice.KeyValueObjectiveDataWriter;
import ice.VentModeObjectiveDataWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class VentControl {
	
	@FXML TableView numericTable;
	
	@FXML BorderPane ventChart1;
	
	@FXML ComboBox<Device> devicesCombo;
	
	@FXML TableView<AlertFx> patientAlertTable; 
	
	//@FXML BorderPane ventChart2;

	private ApplicationContext parentContext;

	private DeviceListModel dlm;

	private NumericFxList numeric;

	private SampleArrayFxList samples;
	
	private AlertFxList alerts;

	private MDSHandler mdsHandler;
	
	private InfusionStatusFxList infusion;

	private VitalModel vitalModel;
	
	private static final int MAX_POINTS = 1200;
	
	//private VentilatorPanel vp;
	
	private org.mdpnp.apps.device.VentilatorPanel deviceVentilatorPanel;
	
	/*
	 * List of the different buttons and other controls.  They should
	 * display or not depending on the operating mode.
	 * 
	 */
	
	Button opModeButton;
	Button vtButton;
	Button tButton;
	Button rrButton;
	Button peepButton;
	Button fiO2Button;
	Button ftrigButton;
	Button deltaPCButton;
	Button psButton;
	Button cpapButton;
	Button pHighButton;
	Button pLowButton;
	Button tHighButton;
	Button tLowButton;
	
	
	/**
	 * Set param button
	 */
	@FXML Button setParamButton;
	
	private Button[][] modeButtons;
	
	@FXML
	private HBox controlButtons;

	@FXML Label numericInputLabel;
	@FXML TextField numericTextInput;

	private static final ArrayList<String> operatingModes=new ArrayList<>();
	
	private VentModeObjectiveDataWriter ventModeWriter;
	
	private KeyValueObjectiveDataWriter keyValueWriter;
	
	private HashMap<String, Button> metricsToButtons;
	
	/**
	 * Current operating mode flag
	 */
	private int currentOpMode;
	
	public VentControl() {
		
	}
	
	public void set(ApplicationContext parentContext, DeviceListModel dlm, NumericFxList numeric, SampleArrayFxList samples, AlertFxList patientAlerts,
			MDSHandler mdsHandler, InfusionStatusFxList infusionStatus, VentModeObjectiveDataWriter ventModeWriter, KeyValueObjectiveDataWriter keyValueWriter) {
		this.parentContext=parentContext;
		this.dlm=dlm;
		this.numeric=numeric;
		this.samples=samples;
		this.alerts=patientAlerts;
		this.mdsHandler=mdsHandler;
		this.infusion=infusionStatus;
		this.ventModeWriter=ventModeWriter;
		this.keyValueWriter=keyValueWriter;
		populateOperatingModes();
		populateComboBox();
		createSettingButtons();
		populateButtonMappings();
		addMenuToModeButton();
	}
	
	private class DeviceCell extends ListCell<Device> {

		@Override
		protected void updateItem(Device item, boolean empty) {
			super.updateItem(item, empty);
			
			setText(item==null ? "" : item.getMakeAndModel());
		}
		
	}
	
	private void populateComboBox() {
		devicesCombo.setEditable(false);
		devicesCombo.setCellFactory(new Callback<ListView<Device>, ListCell<Device>>() {

			@Override
			public ListCell<Device> call(ListView<Device> param) {
				return new DeviceCell();
			}
			
		});
		devicesCombo.setButtonCell(new DeviceCell());
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
						System.err.println("One time loop through numerics has metric "+n.getMetric_id());
						HumanReadableNumericFx humanNumeric=new HumanReadableNumericFx(n);
						humanReadableList.add(humanNumeric);
						//For control, bind to the operating mode numeric...
						if(n.getMetric_id().equals("NKV_550_OP_MODE")) {
							System.err.println("Found KNV_550_OP_MODE");
							n.valueProperty().addListener( l -> {
								int newMode=(int)n.getValue();
								currentOpMode=newMode;
								//TODO: extract these mode mappings somewhere else and share them with the device code.
								setOpModeButtonLabel(newMode);
								configureButtonsForMode(newMode);
							});
							System.err.println("Added listener to it");
							setOpModeButtonLabel((int)n.getValue());
							System.err.println("And called set on op mode button label");
							configureButtonsForMode((int)n.getValue());
						}
						Button b;
						if( (b=metricsToButtons.get(n.getMetric_id())) != null ) {
							System.err.println("There's a setting button for metric "+n.getMetric_id());
							n.valueProperty().addListener( l -> {
								float f=(float)n.getValue();
								String currentText=b.getText();
								if(currentText.indexOf('\n')!=-1) {
									currentText=currentText.substring(0,currentText.indexOf('\n'));
								}
								b.setText(currentText+"\n"+f);
							});
							String currentText=b.getText();
							if(currentText.indexOf('\n')!=-1) {
								currentText=currentText.substring(0,currentText.indexOf('\n'));
							}
							float f=(float)n.getValue();
							b.setText(currentText+"\n"+f);
						} else {
							System.err.println("No setting button for metric "+n.getMetric_id());
							System.err.println("Keys are "+metricsToButtons.keySet().toString());
						}

					});
					numericTable.getItems().clear();
					numericTable.setItems(humanReadableList);
					
					FilteredList<AlertFx> alertsForUDI=new FilteredList<AlertFx>(alerts, new Predicate<AlertFx>() {

						@Override
						public boolean test(AlertFx t) {
							if(t.getUnique_device_identifier().equals(newValue.getUDI())) {
								return true;
							}
							return false;
						}
					
					});
					patientAlertTable.setItems(alertsForUDI);
					
					
				} else {
					ventChart1.setCenter(null);
					deviceVentilatorPanel=null;
				}
			}
			
		});
	}
	
	private EventHandler<ActionEvent> settingButtonHandler=new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			Button b=(Button)event.getSource();
			String labelText=b.getText().substring(0,b.getText().indexOf('\n'));
			String newLabelText="New Value For "+labelText;
			numericInputLabel.setText(newLabelText);
			numericTextInput.setText("");	//Empty any previous contents
			//Pass the user data from the source button to the setParamButton button
			setParamButton.setUserData(b.getUserData());
		}
		
	};
	
	private void populateButtonMappings() {
		metricsToButtons=new HashMap<>();
		metricsToButtons.put("NKV_550_VT_SETTING", vtButton);
		metricsToButtons.put("NKV_550_PRESSURE_SUPPORT_SETTING", psButton);
		metricsToButtons.put("NKV_550_PEEP_SETTING", peepButton);
		metricsToButtons.put("NKV_550_APRV_PRES_HIGH_SETTING", pHighButton);
		metricsToButtons.put("NKV_550_APRV_PRES_LOW_SETTING", pLowButton);
		metricsToButtons.put("NKV_550_APRV_TIME_HIGH_SETTING", tHighButton);
		metricsToButtons.put("NKV_550_APRV_TIME_LOW_SETTING", tLowButton);
		metricsToButtons.put("NKV_550_RR_SETTING", rrButton);
		metricsToButtons.put("NKV_550_FIO2_SETTING", fiO2Button);
		metricsToButtons.put("NKV_550_FTRIG_SETTING", ftrigButton);
		metricsToButtons.put("NKV_550_TI_SETTING", tButton);
		metricsToButtons.put("NKV_550_CPAP_SETTING", cpapButton);
		metricsToButtons.put("NKV_550_DELTAPC_SETTING", deltaPCButton);
		/*
		 * settingIdToSettings=new HashMap<>();
		settingIdToSettings.put(2, new NKV550Settings(opModeHolder, "NKV_550_OP_MODE", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(64,new NKV550Settings(vtSettingHolder, "NKV_550_VT_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(66,new NKV550Settings(psSettingHolder, "NKV_550_PRESSURE_SUPPORT_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(67,new NKV550Settings(peepSettingHolder, "NKV_550_PEEP_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(68,new NKV550Settings(aprvPressureHighSettingHolder, "NKV_550_APRV_PRES_HIGH_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(69,new NKV550Settings(aprvPressureLowSettingHolder, "NKV_550_APRV_PRES_LOW_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(70,new NKV550Settings(aprvTimeHighSettingHolder, "NKV_550_APRV_TIME_HIGH_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(71,new NKV550Settings(aprvTimeLowSettingHolder, "NKV_550_APRV_TIME_LOW_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(77,new NKV550Settings(rrSettingHolder, "NKV_550_RR_SETTING", "MDC_DIM_RESP_PER_MIN"));
		settingIdToSettings.put(77,new NKV550Settings(fiO2SettingHolder, "NKV_550_FIO2_SETTING", rosetta.MDC_DIM_PERCENT.VALUE));
		settingIdToSettings.put(80,new NKV550Settings(fTrigSettingHolder, "NKV_550_FTRIG_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(83,new NKV550Settings(tiSettingHolder, "NKV_550_TI_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(86,new NKV550Settings(cpapSettingHolder, "NKV_550_CPAP_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		settingIdToSettings.put(131,new NKV550Settings(deltaPCSettingHolder, "NKV_550_DELTAPC_SETTING", rosetta.MDC_DIM_DIMLESS.VALUE));
		 */
	}
	
	private void createSettingButtons() {
		opModeButton=new Button("Mode");
		opModeButton.setPrefHeight(100);
		opModeButton.setPrefWidth(100);
		opModeButton.setMnemonicParsing(false);
		
		vtButton=new Button("VT");
		vtButton.setPrefHeight(100);
		vtButton.setPrefWidth(100);
		vtButton.setMnemonicParsing(false);
		vtButton.setOnAction(settingButtonHandler);
		vtButton.setUserData("tidalVolumeSetting");
		
		tButton=new Button("Ti");
		tButton.setPrefHeight(100);
		tButton.setPrefWidth(100);
		tButton.setMnemonicParsing(false);
		tButton.setOnAction(settingButtonHandler);
		tButton.setUserData("inspiratoryTimeSetting");
		
		rrButton=new Button("RR");
		rrButton.setPrefHeight(100);
		rrButton.setPrefWidth(100);
		rrButton.setMnemonicParsing(false);
		rrButton.setOnAction(settingButtonHandler);
		rrButton.setUserData("respiratoryRateSetting");	//This is the name of a setting in the 550.
		
		peepButton=new Button("PEEP");
		peepButton.setPrefHeight(100);
		peepButton.setPrefWidth(100);
		peepButton.setMnemonicParsing(false);
		peepButton.setOnAction(settingButtonHandler);
		peepButton.setUserData("peepSetting");
		
		fiO2Button=new Button("FiO\u2082");
		fiO2Button.setPrefHeight(100);
		fiO2Button.setPrefWidth(100);
		fiO2Button.setMnemonicParsing(false);
		fiO2Button.setOnAction(settingButtonHandler);
		fiO2Button.setUserData("o2percentSetting");
		
		ftrigButton=new Button("Ftrig");
		ftrigButton.setPrefHeight(100);
		ftrigButton.setPrefWidth(100);
		ftrigButton.setMnemonicParsing(false);
		ftrigButton.setOnAction(settingButtonHandler);
		ftrigButton.setUserData("flowTriggerSetting");
		
		deltaPCButton=new Button("\u0394PC");
		deltaPCButton.setPrefHeight(100);
		deltaPCButton.setPrefWidth(100);
		deltaPCButton.setMnemonicParsing(false);
		deltaPCButton.setOnAction(settingButtonHandler);
		deltaPCButton.setUserData("recruitmentPressureControl");
		
		psButton=new Button("PS");
		psButton.setPrefHeight(100);
		psButton.setPrefWidth(100);
		psButton.setMnemonicParsing(false);
		psButton.setOnAction(settingButtonHandler);
		psButton.setUserData("pressureSupportSetting");
		
		pHighButton=new Button("Phigh");
		pHighButton.setPrefHeight(100);
		pHighButton.setPrefWidth(100);
		pHighButton.setMnemonicParsing(false);
		pHighButton.setOnAction(settingButtonHandler);
		pHighButton.setUserData("aprvPressureHighSetting");
		
		pLowButton=new Button("Plow");
		pLowButton.setPrefHeight(100);
		pLowButton.setPrefWidth(100);
		pLowButton.setMnemonicParsing(false);
		pLowButton.setOnAction(settingButtonHandler);
		pLowButton.setUserData("aprvPressureLowSetting");

		tHighButton=new Button("Thigh");
		tHighButton.setPrefHeight(100);
		tHighButton.setPrefWidth(100);
		tHighButton.setMnemonicParsing(false);
		tHighButton.setOnAction(settingButtonHandler);
		tHighButton.setUserData("aprvTimeHighSetting");
		
		tLowButton=new Button("Tlow");
		tLowButton.setPrefHeight(100);
		tLowButton.setPrefWidth(100);
		tLowButton.setMnemonicParsing(false);
		tLowButton.setOnAction(settingButtonHandler);
		tLowButton.setUserData("aprvTimeLowSetting");
		
		modeButtons = new Button[][] {
				{ opModeButton, vtButton, tButton, rrButton, peepButton, fiO2Button, ftrigButton },			//Mode 0, ACMV_VC
				{ opModeButton, deltaPCButton, tButton, rrButton, peepButton, fiO2Button, ftrigButton },	//Mode 1, ACMV_PC
				{ opModeButton, vtButton, tButton, rrButton, peepButton, fiO2Button, ftrigButton },			//Mode 2, ACMV_PRVC
				{ opModeButton, vtButton, tButton, rrButton, peepButton, fiO2Button, psButton, ftrigButton },//Mode 3, SIMV_VC
				{ opModeButton, deltaPCButton, tButton, rrButton, peepButton, fiO2Button, psButton, ftrigButton },	//Mode 4, SIMV_PC
				{ opModeButton, vtButton, tButton, rrButton, peepButton, fiO2Button, psButton, ftrigButton },	//Mode 5, SIMV_PRVC
				{ opModeButton, cpapButton, fiO2Button, ftrigButton },										//Mode 6, SPONT_CPAP
				{ opModeButton, psButton, peepButton, fiO2Button, ftrigButton },							//Mode 7, SPONT_PS
				{ opModeButton, vtButton, peepButton, fiO2Button, ftrigButton },							//Mode 8, SPONT_VS
				{ opModeButton, pHighButton, pLowButton, tHighButton, tLowButton, fiO2Button, ftrigButton}	//Mode 9, SPONT_APRV
				
		};
		
		/*
		 * Operating mode graph limits
		 * 
		 * Mode 7 - SPONT_PS - Pressure Upper 50, Pressure Lower, -5, Flow Upper 60, Flow Lower -60, Volume Upper 600, Volume lower -60
		 * 
		 * 
		 * 
		 */
	}
	
	/**
	 * Configure the visible buttons according to the operating mode 
	 */
	private void configureButtonsForMode(int mode) {
		System.err.println("Getting buttons for mode "+mode);
		if(mode>modeButtons.length) {
			throw new RuntimeException("Unknown operating mode "+mode);
		}
		ObservableList<Node> children=controlButtons.getChildren();
		children.clear();	//Remove all existing buttons from the HBox.
		Button[] newButtons=modeButtons[mode];
		for(Button enable : newButtons) {
			enable.setDisable(false);
		}
		children.addAll(newButtons);
	}
	
	/**
	 * Populate the array list with all the operating modes for the 550.
	 * It's CRITICAL that the order of these do not change, because the
	 * index matches the value we need to map to/from the integer values
	 * that the device publishes. 
	 */
	private void populateOperatingModes() {
		operatingModes.add("ACMV_VC");
		operatingModes.add("ACMV_PC");
		operatingModes.add("ACMV_PRVC");
		operatingModes.add("SIMV_VC");
		operatingModes.add("SIMV_PC");
		operatingModes.add("SIMV_PRVC");
		operatingModes.add("SPONT_CPAP");
		operatingModes.add("SPONT_PS");
		operatingModes.add("SPONT_VS");
		operatingModes.add("SPONT_ARPV");
	}
	
	private void setOpModeButtonLabel(int newMode) {
		if(newMode<0 || newMode>operatingModes.size()-1) {
			opModeButton.setText("UNKNOWN");
			return;
		}
		opModeButton.setText(operatingModes.get(newMode));
	}
	
	private void addMenuToModeButton() {
		ContextMenu menu=new ContextMenu();
		operatingModes.forEach( s -> {
			MenuItem mi=new MenuItem(s);
			mi.setMnemonicParsing(false);
			menu.getItems().add(mi);
			mi.setOnAction( v -> {
				setOperatingMode(s);
			});
		});
		opModeButton.setContextMenu(menu);
		opModeButton.setOnMouseClicked( e -> {
			if(e.getButton()==MouseButton.PRIMARY) {
				opModeButton.getContextMenu().show(opModeButton, Side.RIGHT, -30, 30);
			}
		});
	}
	
	private void setOperatingMode(String mode) {
		int newMode=operatingModes.indexOf(mode);
		ice.VentModeObjective objective=new ice.VentModeObjective();
		objective.newMode=newMode;
		objective.requestor="App";
		String udi=devicesCombo.getSelectionModel().getSelectedItem().getUDI();
		objective.unique_device_identifier=udi;
		ventModeWriter.write(objective, InstanceHandle_t.HANDLE_NIL);
		Button[] buttonsToDisable=modeButtons[currentOpMode];
		for(Button disable : buttonsToDisable) {
			disable.setDisable(true);
		}
		
	}
	
	/**
	 * Set the specified parameter
	 */
	public void setParam() {
		//The user data tells us what param to set in the objective.
		String paramToSet=(String)setParamButton.getUserData();
		ice.KeyValueObjective obj=new ice.KeyValueObjective();
		obj.paramName=paramToSet;
		obj.unique_device_identifier=devicesCombo.getSelectionModel().getSelectedItem().getUDI();
		obj.newValue=Float.parseFloat(numericTextInput.getText());
		keyValueWriter.write(obj, InstanceHandle_t.HANDLE_NIL);
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
	    		//Else the human readable metric is just the untranslated normal metr)ic 
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
