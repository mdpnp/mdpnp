package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TabBar.Tab;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.EntityProxyChange;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.web.bindery.requestfactory.shared.WriteOperation;

public class ScenarioPanel extends Composite implements Editor<ScenarioProxy> {
	
	private static final int EQUIPMENT_DEVICETYPE_COL = 0;
	private static final int EQUIPMENT_MANUFACTURER_COL = 1;
	private static final int EQUIPMENT_MODEL_COL = 2;
	private static final int EQUIPMENT_ROSSETAID_COL = 3;
	private static final int EQUIPMENT_DElETEBUTTON_COL = 4;
	
	
	private static ScenarioPanelUiBinder uiBinder = GWT.create(ScenarioPanelUiBinder.class);

	interface Driver extends RequestFactoryEditorDriver<ScenarioProxy, ScenarioPanel> {
		
	}
	Driver driver = GWT.create(Driver.class);
	ScenarioRequestFactory scenarioRequestFactory;
	
	interface ScenarioPanelUiBinder extends UiBinder<Widget, ScenarioPanel> {
	}

	 private static class MyDialog extends DialogBox {

		    public MyDialog(String str, String txt) {
		      setText(str);
		      FlowPanel fp = new FlowPanel();
		      Button ok = new Button("Close");
		      ok.addClickHandler(new ClickHandler() {
		        public void onClick(ClickEvent event) {
		          MyDialog.this.hide();
		        }
		      });
		      Label lbl = new Label(txt);
		      fp.add(lbl);
		      fp.add(ok);
		      add(fp);
		    }
		  }
	private static final String[] hazardExpected = new String[] {"Expected", "Unexpected", "Unknown"};
	private static final String[] hazardSeverity = new String[] {"Mild", "Moderate", "Severe", "Life Threatening", "Fatal", "Unknown"};
	
	//XXX Do I need this?  Clean up!!
	private static  enum ScenarioStatus {
		unsubmitted(0), //created and modified, but not yet submitted
		submitted(1), //submitted for approval, not yet revised nor approved 
		approved(2), //revised and approved
		rejected(3); //revised but not approved. Rejected for revision
		
		private int currentStatus;
		
		ScenarioStatus(int currentStatus) {
			this.currentStatus = currentStatus;
		}
	    public int getCurrentStatus()
	    {
	    	return currentStatus;
	    }
	     
	    public static ScenarioStatus getValue(int i)
	    {
	        for (ScenarioStatus scn : ScenarioStatus.values())
	        {
	            if (scn.getCurrentStatus() == i)
	                return scn;
	        }
	        throw new IllegalArgumentException("Invalid scenario status: " + i);
	    }
	}
	//////////////////////////////
	
	private static final ListBox buildListBox(String[] strings) {
		ListBox box = new ListBox();
		for(String s : strings) {
			box.addItem(s);
		}
		return box;
	}
	
	
//	private static final String[] testCasesHeader = new String[] {"Id", "Description", "Step/Order", "Author", "Requirements", "Configuration", "Remarks", "Summary"};
//	private final void buildTestCasesTable() {
//		testCasesTable.insertRow(0);
//		for(int i = 0; i < testCasesHeader.length; i++) {
//			testCasesTable.setText(0,  i, testCasesHeader[i]);
//		}
//		for(int i = 0; i < 2; i++) {
//			testCasesTable.insertRow(i+1);
//			for(int j = 0; j < testCasesHeader.length; j++) {
//				testCasesTable.setWidget(i+1, j, new TextBox());
//			}
//		}
//	}
	
	private final void buildCliniciansTable() {
		
	}
	
	private final void buildEnvironmentsTable() {
		
	}
	
	/**
	 * Prints/drwas the Equipment tab table.
	 * @param isNew indicates if we are drawing a new/empty table os we are going to
	 *  populate it with data from the scenario
	 */
	private final void buildEquipmentTable(boolean isNew) {
		equipmentTable.removeAllRows();//clear rows to draw again
		//HEADERS
		equipmentTable.insertRow(0);
		equipmentTable.setText(0, EQUIPMENT_DEVICETYPE_COL, "Device Type");
		equipmentTable.setText(0, EQUIPMENT_MANUFACTURER_COL, "Manufacturer");
		equipmentTable.setText(0, EQUIPMENT_MODEL_COL, "Model");
		equipmentTable.setText(0, EQUIPMENT_ROSSETAID_COL, "Rosetta ID");

		
		{if(isNew || currentScenario==null || 
				currentScenario.getEquipment().getEntries()==null || currentScenario.getEquipment().getEntries().isEmpty()){
			//the table will have no elements, either because we have anew scenario or because the current one
			// has no elements on its equipment list
			for(int i = 0; i < 1; i++) {
				equipmentTable.insertRow(i + 1);
				for(int j = 0; j < 4; j++) {//add four textboxes for data
					equipmentTable.setWidget(i+1, j, new TextBox());
				}
				//add button to delete current row 
				Button deleteButton = new Button("Delete");
				equipmentTable.setWidget(i+1, EQUIPMENT_DElETEBUTTON_COL, deleteButton);
				final int row = i+1;
				//click handler that deletes the current row
				deleteButton.addClickHandler(new ClickHandler() {	
					@Override
					public void onClick(ClickEvent event) {
						equipmentTable.removeRow(row);
					}
				});				
			}
		
		}else{
			//populate the table w/ the data from the equipment list of the scenario
			List<?> eqEntries = currentScenario.getEquipment().getEntries();
			for(int i=0; i<eqEntries.size();i++){
				final int row = i+1;
				equipmentTable.insertRow(row);
				EquipmentEntryProxy eep = (EquipmentEntryProxy) eqEntries.get(i);
				TextBox dtTextbox = new TextBox(); dtTextbox.setText(eep.getDeviceType());
				equipmentTable.setWidget(row, EQUIPMENT_DEVICETYPE_COL, dtTextbox);
				TextBox manufTextBox = new TextBox(); manufTextBox.setText(eep.getManufacturer());
				equipmentTable.setWidget(row, EQUIPMENT_MANUFACTURER_COL, manufTextBox);
				TextBox modelTextBox = new TextBox(); modelTextBox.setText(eep.getModel());
				equipmentTable.setWidget(row, EQUIPMENT_MODEL_COL, modelTextBox);
				TextBox rossTextBox = new TextBox(); rossTextBox.setText(eep.getRosettaId());
				equipmentTable.setWidget(i+1, EQUIPMENT_ROSSETAID_COL, rossTextBox);
				Button deleteButton = new Button("Delete");
				equipmentTable.setWidget(row, EQUIPMENT_DElETEBUTTON_COL, deleteButton);
				
				//add click handler to the delete button
				deleteButton.addClickHandler(new ClickHandler() {				
					@Override
					public void onClick(ClickEvent event) {
						equipmentTable.removeRow(row);
					}
				});
			}			
		}
		
		}
	}
	
	private final void buildHazardsTable() {
		hazardsTable.insertRow(0);
		hazardsTable.setText(0, 0, "Description");
		hazardsTable.setText(0, 1, "Factors");
		hazardsTable.setText(0,  2, "Expected");
		hazardsTable.setText(0,  3, "Severity");
		
		hazardsTable.insertRow(1);
		hazardsTable.setHTML(1, 0, "<div style=\"font-size: 8pt; width:350px;\">Make sure to point out if this risk results in death, is life threatening, requires inpatient hospitalization or prolongation of existing hospitalization, results in persistent or significant disability/incapacity, is a congenital anomaly/birth defect</div>");
		hazardsTable.setHTML(1, 1, "<div style=\"font-size: 8pt; width:350px;\">Determine which factors are contributing to the risk described above. Examples may be a clinician, a specific device, or an aspect of the clinical envirnomnet etc.</div>");
		hazardsTable.setHTML(1,  2, "<div style=\"width:350px; font-size:8pt;\">Unexpected: Risk is not consistent with the any of risks known (from a manual, label, protocol, instructions, brochure, etc) in the Current State. If the above documents are not required or available, the risk is unexpected if specificity or severity is not consistent with the risk information described in the protocol or it is more severe to the specified risk. Example, Hepatic necrosis would be unexpected (by virtue of greater severity) if the investigator brochure only referred to elevated hepatic enzymes or hepatitis. Similarly, cerebral vasculitis would be unexpected (by virtue of greater specificity) if the investigator brochure only listed cerebral vascular accidents.</div>");
		hazardsTable.setHTML(1,  3, "<div style=\"width:350px; font-size:8pt;\"><ul><li>Mild: Barely noticeable, does not influence functioning, causing no limitations of usual activities</li><li>Moderate: Makes patient uncomfortable, influences functioning, causing some limitations of usual activities</li><li>Severe: Severe discomfort, treatment needed, severe and undesirable, causing inability to carry out usual activities</li><li>Life Threatening: Immediate risk of deat, life threatening or disabling</li><li>Fatal: Causes death of the patient</li></ul></div>");
		{
			
			hazardsTable.insertRow(2);
			final TextArea hazardDescription = new TextArea();
			hazardDescription.setVisibleLines(10);
			hazardDescription.setCharacterWidth(40);
			
			final TextArea hazardFactors = new TextArea();
			hazardFactors.setVisibleLines(10);
			hazardFactors.setCharacterWidth(40);
			
			hazardsTable.setWidget(2, 0, hazardDescription);
			hazardsTable.setWidget(2, 1, hazardFactors);
			hazardsTable.setWidget(2, 2, buildListBox(hazardExpected));
			hazardsTable.setWidget(2, 3, buildListBox(hazardSeverity));
		}
		

		{
			hazardsTable.insertRow(3);
			hazardsTable.setWidget(3, 0, new Anchor("Add New...", "#"));//FIXME like add new equipment?
		}
		
		
	}
	
	private Logger logger = Logger.getLogger(ScenarioPanel.class.getName());
	
	
	
	/**
	 * Saves or persist a Scenario
	 * <p> 1- The TextArea fields are asociated automativcally w/ the corresponding attributes in the Scenario object
	 * <p> 2- save the list of equipment
	 */
	private void save(){
		status.setText("SAVING");			
		ScenarioRequest rc = (ScenarioRequest) driver.flush();
			
		//SAVE THE LIST OF EQUIPMENT
		currentScenario.getEquipment().getEntries().clear();//clear equipment list entries 
		for(int i = 0; i < equipmentTable.getRowCount(); i++) {
			
			Widget wDevType = equipmentTable.getWidget(i, 0);//getWidget row column		
			Widget wManu = equipmentTable.getWidget(i, 1);//getWidget row column		
			Widget wModel = equipmentTable.getWidget(i, 2);//getWidget row column		
			Widget wRoss = equipmentTable.getWidget(i, 3);//getWidget row column		
			EquipmentEntryProxy eep = rc.create(EquipmentEntryProxy.class);
			
			boolean isAdding = false;
			
			if(wDevType instanceof TextBox) {
				eep.setDeviceType(((TextBox)wDevType).getText().trim()); isAdding=true;}
			if(wManu instanceof TextBox) {
				eep.setManufacturer(((TextBox)wManu).getText().trim()); isAdding=true;}
			if(wModel instanceof TextBox) {
				eep.setModel(((TextBox)wModel).getText().trim()); isAdding=true;}
			if(wRoss instanceof TextBox) {
				eep.setRosettaId(((TextBox)wRoss).getText().trim()); isAdding=true;}					
			
			//FIXME This is not really adding. No only consider just texboxes, careful w/ the items we already have.
			if(isAdding)
				currentScenario.getEquipment().getEntries().add(eep);
		}
		
//		List l = currentScenario.getEquipment().getEntries();
		rc.persist().using(currentScenario).with(driver.getPaths()).with("equipment").to(new Receiver<ScenarioProxy>() {

			@Override
			public void onSuccess(ScenarioProxy response) {
			    logger.info("RESPONSE|currentState:"+response.getBackground().getCurrentState()+" proposedState:"+response.getBackground().getProposedState());
				status.setText("SAVED");
				scenarioRequestFactory.getEventBus().fireEvent(new EntityProxyChange<ScenarioProxy>(response, WriteOperation.UPDATE));
//				logger.info("DURING:"+response.getTitle());
				setCurrentScenario(response);
//				logger.info("AFTER:"+currentScenario.getTitle());
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
			}
			
		}).fire();
		
	}
	
	public ScenarioPanel(final ScenarioRequestFactory scenarioRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.scenarioRequestFactory = scenarioRequestFactory;
		driver.initialize(scenarioRequestFactory, this);
		buildHazardsTable();
//		buildEquipmentTable();//DAG
		buildCliniciansTable();
		buildEnvironmentsTable();
//		buildTestCasesTable();

		ChangeHandler saveOnChange = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				status.setText("SAVING");			
				ScenarioRequest rc = (ScenarioRequest) driver.flush();
				
				logger.info("getPaths:"+Arrays.toString(driver.getPaths()));
				logger.info("LOCAL|currentState:"+currentScenario.getBackground().getCurrentState()+" proposedState:"+currentScenario.getBackground().getProposedState());
				
				currentScenario.getEquipment().getEntries().clear();//clear equipment list entries 
				for(int i = 0; i < equipmentTable.getRowCount(); i++) {
					
					Widget wDevType = equipmentTable.getWidget(i, 0);//getWidget row column		
					Widget wManu = equipmentTable.getWidget(i, 1);//getWidget row column		
					Widget wModel = equipmentTable.getWidget(i, 2);//getWidget row column		
					Widget wRoss = equipmentTable.getWidget(i, 3);//getWidget row column		
					EquipmentEntryProxy eep = rc.create(EquipmentEntryProxy.class);
					
					boolean isAdding = false;
					
					if(wDevType instanceof TextBox) {
						eep.setDeviceType(((TextBox)wDevType).getText().trim()); isAdding=true;}
					if(wManu instanceof TextBox) {
						eep.setManufacturer(((TextBox)wManu).getText().trim()); isAdding=true;}
					if(wModel instanceof TextBox) {
						eep.setModel(((TextBox)wModel).getText().trim()); isAdding=true;}
					if(wRoss instanceof TextBox) {
						eep.setRosettaId(((TextBox)wRoss).getText().trim()); isAdding=true;}					
					
					//FIXME This is not really adding. No only consider just texboxes, careful w/ the items we already have.
					//Means not persisting EMPTY lists/items/rows
					if(isAdding)
						currentScenario.getEquipment().getEntries().add(eep);
				}
				
//				List l = currentScenario.getEquipment().getEntries();
				rc.persist().using(currentScenario).with(driver.getPaths()).with("equipment").to(new Receiver<ScenarioProxy>() {

					@Override
					public void onSuccess(ScenarioProxy response) {
					    logger.info("RESPONSE|currentState:"+response.getBackground().getCurrentState()+" proposedState:"+response.getBackground().getProposedState());
						status.setText("SAVED");
						scenarioRequestFactory.getEventBus().fireEvent(new EntityProxyChange<ScenarioProxy>(response, WriteOperation.UPDATE));
//						logger.info("DURING:"+response.getTitle());
						setCurrentScenario(response);
//						logger.info("AFTER:"+currentScenario.getTitle());
					}
					
					@Override
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
						Window.alert(error.getMessage());
					}
					
				}).fire();

			}
			
		};
		


		titleEditor.addChangeHandler(saveOnChange);
		proposedStateEditor.addChangeHandler(saveOnChange);
		currentStateEditor.addChangeHandler(saveOnChange);
		benefits.addChangeHandler(saveOnChange);
		risks.addChangeHandler(saveOnChange);
		clinicalProcesses.addChangeHandler(saveOnChange);
		algorithmDescription.addChangeHandler(saveOnChange);
		
		//DAG
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
//				Window.alert("selected panel");

				if(currentScenario!= null && 
//						!currentScenario.getEquipment().getEntries().isEmpty())
						isPersitEquipment()){
					save();
//					Window.alert("persisted "+equipmentTable.getRowCount()+" row");
				}
//				buildEquipmentTable();
			}
		});
		
	
		tabPanel.selectTab(0);
	}

	/**
	 * indicates if we need to persist our equipment list
	 * @return
	 */
	private boolean isPersitEquipment(){
		if (equipmentTable.getRowCount()<2)
			return false;//we dont even have one row of data
		Widget wDevType = equipmentTable.getWidget(1, 0);//getWidget row column		
		Widget wManu = equipmentTable.getWidget(1, 1);//getWidget row column		
		Widget wModel = equipmentTable.getWidget(1, 2);//getWidget row column		
		Widget wRoss = equipmentTable.getWidget(1, 3);//getWidget row column	
		ScenarioRequest rc = (ScenarioRequest) driver.flush();
		EquipmentEntryProxy eep = rc.create(EquipmentEntryProxy.class);
		
		boolean isWorthAdding = false;
		
		if(wDevType instanceof TextBox) {
			isWorthAdding |= !((TextBox)wDevType).getText().trim().equals(""); }
		if(wManu instanceof TextBox) {
			isWorthAdding |= !((TextBox)wManu).getText().trim().equals("");}
		if(wModel instanceof TextBox) {
			isWorthAdding |= !((TextBox)wModel).getText().trim().equals("");}
		if(wRoss instanceof TextBox) {
			isWorthAdding |= !((TextBox)wRoss).getText().trim().equals(""); }	
		
		return isWorthAdding;
		
	}
	
	
	private ScenarioProxy currentScenario;
	
	public void setCurrentScenario(ScenarioProxy currentScenario) {
		
		ScenarioRequest context = scenarioRequestFactory.scenarioRequest();
		if(null == currentScenario) {
		    context.create().with("background", "benefitsAndRisks", /*"environments",*/ "equipment", /*"hazards",*/ "proposedSolution").to(new Receiver<ScenarioProxy>() {
	    	
                @Override
                public void onSuccess(ScenarioProxy response) {
                    logger.info(""+response.getBackground());
                    ScenarioRequest context = scenarioRequestFactory.scenarioRequest();
                    ScenarioProxy currentScenario = context.edit(response); 
                    driver.edit(currentScenario, context);
//                  logger.info("Driver:"+driver);
                    //DAG Empty equipment list (CLEAN data from the new BEAN to clean the form)
//                    currentScenario.getEquipment().getEntries().clear(); delayed
                    
                    ScenarioPanel.this.currentScenario = currentScenario;
                }
		        
		    }).fire();
//		    tabPanel.selectTab(0);
		    buildEquipmentTable(true);//DAG new scn. No equipment list

		} else {
		    currentScenario = context.edit(currentScenario); 
            driver.edit(currentScenario, context);
            this.currentScenario = currentScenario;
            buildEquipmentTable(false);//
		}

//		buildEquipmentTable();//DAG

	}
	
	@UiField
	TabPanel tabPanel;
	
	@UiField
	@Path("background.currentState")
	TextArea currentStateEditor;
	
	@UiField
	@Path("background.proposedState")
	TextArea proposedStateEditor;
	
	@UiField
	@Ignore
	Label status;
	
	@UiField
	@Ignore
	FlexTable hazardsTable;
	
	@UiField
	FlexTable equipmentTable;

	@UiField
	@Ignore
	Anchor currentStateExample;
	
	@UiField
	@Ignore
	Anchor proposedStateExample;
	
	@UiField
	@Ignore
	Anchor clinicalProcessesExample;
	
	@UiField
	@Ignore
	Anchor algorithmDescriptionExample;

	@UiField
	@Ignore
	Anchor benefitsExample;
	
	@UiField
	@Ignore
	Anchor risksExample;
	
	@UiField
	@Ignore
	FlexTable cliniciansTable;
	
	@UiField
	@Ignore
	FlexTable environmentsTable;
	
	@UiField
	@Ignore
	Anchor addNewClinician;
	
	@UiField
	@Ignore
	Anchor addNewEnvironment;
	
	@UiField
	@Ignore
	Anchor addNewEquipment;
	
	
	private static class ClinicianSuggestOracle extends MultiWordSuggestOracle {
		private static String[] values = new String[] {
			"Allergist",
			"Anesthesiologist",
			"Cardiologist",
			"Chief Nurse",
			"Clinical Staff",
			"CNA - Certified Nurse Assistant",
			"Cosmetic Surgeon",
			"Critical Care Nurse",
			"CRNA - Certified Registered Nurse Asst",
			"Dentist",
			"Dermatologist",
			"Emergency Medicine Doctor",
			"Endocrinologist",
			"Epidemiologist",
			"Gastrologist",
			"General Practitioner",
			"Geriatrics Specialist",
			"Gynecologist",
			"Hematologist",
			"HHN - Home Health Nurse",
			"Hospitalist",
			"Infectious Disease Nurse",
			"Labor-Delivery Nurse",
			"LPN - Licensed Practical Nurse",
			"LVN - Licensed Vocational Nurse",
			"Medical Assistant",
			"Medical Emergency Team",
			"Neonatologist",
			"Neurologist",
			"Nurse",
			"Nurse Assistant",
			"Occupational Health Nurse",
			"Oncologist",
			"ORRN - Operating Room Registered Nurse",
			"Pathologist",
			"Physician",
			"Radiologist",
			"Respiratory Therapist",
			"Rheumatologist",
			"RN - Registered Nurse",
			"RRT",
			"Surgeon",
			"Toxicologist",
			"Urologist",
			"X-Ray Technician"
			
		};
		public ClinicianSuggestOracle() {
			super();
			setComparator(new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if(null == o1) {
						if(null == o2) {
							return 0;
						} else {
							return -1;
						}
					} else {
						if(null == o2) {
							return 1;
						} else {
							return o1.toUpperCase().compareTo(o2.toUpperCase());
						}
					}
				}
				
			});
			for(String v : values) {
				add(v);
			}
		}
		
	}
	private final ClinicianSuggestOracle clinicianSuggestOracle = new ClinicianSuggestOracle();
	
	private static class EnvironmentSuggestOracle extends MultiWordSuggestOracle {
		private static String[] values = new String[] {"Acute assessment unit",
		"Ambulatory wing",
		"Birthing Room/LDR Room/LDRP Room",
		"Breast screening unit",
		"Burn Care Unit",
		"Cafeteria",
		"CCU-cardiac care unit",
		"Delivery room",
		"Discharge lounge",
		"Discharge unit",
		"ENT-Ear nose and throat",
		"ER-emergency room",
		"Geriatrics",
		"Hospital room",
		"ICU-intensive care unit",
		"Lab/pathology",
		"Maternity wards",
		"Neonatal unit",
		"Nurse station",
		"On-call room",
		"OR-operating room",
		"Patient bay",
		"Pediatrics",
		"Physical therapy/rehab dept",
		"Post surgical care unit",
		"Psychiatric ward",
		"Radiology/imaging",
		"Recovery room",
		"Renal unit",
		"Telemetry",
		"Transport",
		"Trauma center",
		"Ultrasound Unit",
		"Volunteers room",
		"Waiting room",
		"Wound Care Unit",};
		public EnvironmentSuggestOracle() {
			super();
			setComparator(new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if(null == o1) {
						if(null == o2) {
							return 0;
						} else {
							return -1;
						}
					} else {
						if(null == o2) {
							return 1;
						} else {
							return o1.toUpperCase().compareTo(o2.toUpperCase());
						}
					}
				}
				
			});
			for(String v : values) {
				add(v);
			}
		}
		
	}
	
	//when clicking in AddNeww Equipment
	@UiHandler("addNewEquipment")
	void onANTClick(ClickEvent click) {
		final int rows = equipmentTable.getRowCount();
		equipmentTable.insertRow(rows);
		for(int j = 0; j < 4; j++) {//add four text boxes
			equipmentTable.setWidget(rows, j, new TextBox());
		}
		Button deleteButton = new Button("Delete");
		equipmentTable.setWidget(rows, EQUIPMENT_DElETEBUTTON_COL, deleteButton);

		//click handler that deletes the current row
		deleteButton.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				equipmentTable.removeRow(rows);
			}
		});
	}
	private final EnvironmentSuggestOracle environmentSuggestOracle = new EnvironmentSuggestOracle();
	
	@UiHandler("addNewClinician")
	void onANCClick(ClickEvent click) {
		final int rows = cliniciansTable.getRowCount();
		cliniciansTable.insertRow(rows);
		final SuggestBox sb = new SuggestBox(clinicianSuggestOracle);
		sb.setStyleName("wideSuggest");
		cliniciansTable.setWidget(rows, 0, sb);
		sb.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
					Label lbl = new Label(sb.getText());
					cliniciansTable.setWidget(rows, 0, lbl);
				}
			}
			
		});
	}
	
	@UiHandler("addNewEnvironment")
	void onANEClick(ClickEvent click) {
		final int rows = environmentsTable.getRowCount();
		environmentsTable.insertRow(rows);
		final SuggestBox sb = new SuggestBox(environmentSuggestOracle);
		sb.setStyleName("wideSuggest");
		environmentsTable.setWidget(rows, 0, sb);
		sb.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
					Label lbl = new Label(sb.getText());
					environmentsTable.setWidget(rows, 0, lbl);
				}
			}
			
		});
	}
	
	@UiHandler("currentStateExample")
	void onCSEClick(ClickEvent click) {
		MyDialog md = new MyDialog("Current State Example", "A 49-year-old woman underwent an uneventful total abdominal hysterectomy and bilateral salpingo-oophorectomy. Postoperatively, the patient complained of severe pain and received intravenous morphine sulfate in small increments. She began receiving a continuous infusion of morphine via a patient controlled analgesia (PCA) pump. A few hours after leaving the PACU [post anesthesia care unit] and arriving on the floor, she was found pale with shallow breathing, a faint pulse, and pinpoint pupils. The nursing staff called a 'code,' and the patient was resuscitated and transferred to the intensive care unit on a respirator [ventilator]. Based on family wishes, life support was withdrawn and the patient died. Review of the case by providers implicated a PCA overdose. Delayed detection of respiratory compromise in PATIENTS undergoing PCA therapy is not uncommon because monitoring of respiratory status has been confounded by excessive nuisance alarm conditions (poor alarm condition specificity).");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("proposedStateExample")
	void onPSEClick(ClickEvent click) {
		MyDialog md = new MyDialog("Proposed State Example", "While on the PCA infusion pump, the PATIENT is monitored with a respiration rate monitor and a pulse oximeter. If physiological parameters move outside the pre-determined range, the infusion is stopped and clinical staff is notified to examine the PATIENT and restart the infusion if appropriate. The use of two independent physiological measurements of respiratory function (oxygen saturation and respiratory rate) enables a smart algorithm to optimize sensitivity, thereby enhancing the detection of respiratory compromise while reducing nuisance alarm conditions.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("clinicalProcessesExample")
	void onCPClick(ClickEvent click) {
		MyDialog md = new MyDialog("Clinical Processes Example", "The patient is connected to a PCA infusion pump containing morphine sulfate, a large volume infusion pump acting as a carrier line of saline, a pulse oximeter, a non-invasive blood pressure device, a respiration rate monitor and a distributed alarm system. Heart rate and blood pressure, respiration rate, pain score and sedation score are collected as directed by the clinical process for set-up of a PCA pump. An intravenous (IV) line assessment is also completed. The PCA infusion pump, large volume infusion pump, and pulse oximeter are attached to the integrated system. The system queries the hospital information system for the patient's weight, age, and medication list (specifically, whether the patient is receiving sedatives or non-PCA opioids), and searches for a diagnosis of sleep apnea. The system then accesses the physician's orders from the computerized physician order entry system for dosage and rate for the PCA and large volume infusion pump, and verifies the values programmed into the infusion pump. The patient's SpO2 (arterial oxygen saturation measured by pulse oximetry) and respiration rate are monitored continuously.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("algorithmDescriptionExample")
	void onADClick(ClickEvent click) {
		MyDialog md = new MyDialog("Algorithm Description Example", "The system uses an algorithm based on weight, age, medication list, diagnoses, SpO2 and respiration rate to determine the state of the patient. Sedation and pain scores also contribute to this algorithm. If the algorithm detects decreases in the patient's SpO2 and/or respiration rate below the calculated or pre-set threshold, a command is sent to stop the PCA pump to prevent further drug overdose, and the system generates a respiratory distress medium priority alarm condition sent via the distributed alarm system. Furthermore, if the algorithm detects that both the SpO2 and respiration rate indicate distress, the system generates an extreme respiratory distress high priority alarm condition sent via the distributed alarm system.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	@UiHandler("benefitsExample")
	void onBClick(ClickEvent click) {
		MyDialog md = new MyDialog("Benefits Example", "Add error resistance to the x-ray procedure by eliminating the dependence on the operator (e.g. anesthesia provider) to remember to turn the ventilator back on. Shorten or eliminate the period of apnea, thereby reducing potentially adverse responses to apnea; and Provide the ability to synchronize x-ray exposure with inspiratory hold, without requiring anyone to be present in the x-ray exposure area to manually generate sustained inspiration");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("risksExample")
	void onRClick(ClickEvent click) {
		MyDialog md = new MyDialog("Risks Example", "A synchronization error could lead to x-ray exposure at an incorrect phase of respiration.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
//	@UiField
//	FlexTable testCasesTable;
	
	@UiField
	TextBox titleEditor;
	
	@UiField
	@Path(value="benefitsAndRisks.benefits")
	TextArea benefits;
	
	@UiField
	@Path(value="benefitsAndRisks.risks")
	TextArea risks;
	
	@UiField
	@Path(value="proposedSolution.process")
	TextArea clinicalProcesses;
	
	@UiField
	@Path(value="proposedSolution.algorithm")
	TextArea algorithmDescription;
	
	@UiField
	Button submitButton;
	
	@UiField
	Button saveButton;
	
	@UiHandler("submitButton")
	public void onClickSubmit(ClickEvent clickEvent) {
		int i = 0 ;//dummy		
	}
	
	@UiHandler("saveButton")
	public void onClickSave(ClickEvent clickEvent) {
		int i = 0 ;//dummy		
	}
}
