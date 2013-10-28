package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdpnp.clinicalscenarios.client.tag.TagProxy;
import org.mdpnp.clinicalscenarios.client.tag.TagRequest;
import org.mdpnp.clinicalscenarios.client.tag.TagRequestFactory;
import org.mdpnp.clinicalscenarios.client.tag.comparator.TagComparator;
import org.mdpnp.clinicalscenarios.client.user.UserInfoProxy;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequest;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequestFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.EntityProxyChange;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.web.bindery.requestfactory.shared.WriteOperation;

public class ScenarioPanel extends Composite implements Editor<ScenarioProxy> {
	
	//equipment tab panel
	private static final int EQUIPMENT_DEVICETYPE_COL = 0;
	private static final int EQUIPMENT_MANUFACTURER_COL = 1;
	private static final int EQUIPMENT_MODEL_COL = 2;
	private static final int EQUIPMENT_ROSSETAID_COL = 3;
//	private static final int EQUIPMENT_DElETEBUTTON_COL = 4;//ticket-140
	private static final int EQUIPMENT_EXPLANATION_COL  = 4;
	private static final int EQUIPMENT_TRAINING_PROBLEM_COL  = 5;
	private static final int EQUIPMENT_INSTRUCTION_PROBLEM_COL  = 6;
	private static final int EQUIPMENT_CONFUSING_USAGE_COL  = 7;
	private static final int EQUIPMENT_SW_PROBLEM_COL = 8;
	private static final int EQUIPMENT_HW_PROBLEM_COL = 9;
	private static final int EQUIPMENT_DElETEBUTTON_COL = 10;//ticket-140
	
	//hazards tab panel
	private static final int HAZARDS_DESCRIPTION_COL = 0;
	private static final int HAZARDS_FACTORS_COL = 1;
	private static final int HAZARDS_EXPECTED_COL = 2;
	private static final int HAZARDS_SEVERITY_COL = 3;
	private static final int HAZARDS_DELETEBUTTON_COL = 4;
	
	//clinicians table
	private static final int CLINICIANS_TYPE_COL = 0;
	private static final int CLINICIANS_DELETEBUTTON_COL = 1;
	
	//environments table
	private static final int ENVIRONMENT_TYPE_COL = 0;
	private static final int ENVIRONMENT_DELETEBUTTON_COL = 1;
	
	//references table
	private static final int REFERENCE_http_COL = 0;
	private static final int REFERENCE_TEXT_COL = 1;
	private static final int REFERENCE_DELETEBUTTON_COL = 2;
	private static final int REFERENCE_FOLLOWBUTTON_COL = 3;
	
	//tags tab
	private static final int TAGS_TAB_NUM_COL = 6; //number of columns for this panel
	
	//scenario status
	public final static String SCN_STATUS_UNSUBMITTED = 	"unsubmitted";//created and/or modified, but not yet submitted for approval. Only modificable by owner
	public final static String SCN_STATUS_SUBMITTED = 		"submitted"; //submitted for approval, not yet revised nor approved 
	public final static String SCN_STATUS_UNLOCKED_PRE =	"unlocked(pre)"; //unlocked pre-scenario submission	
	public final static String SCN_STATUS_APPROVED = 		"approved"; //revised and approved
	public final static String SCN_STATUS_REJECTED = 		"rejected"; //rejected or killed. The scenario reaches a 'dead' state
	public final static String SCN_STATUS_UNLOCKED_POST =	"unlocked(post)"; //unlocked post-scenario submission	
	public final static String SCN_STATUS_MODIFIED = 		"dirty"; //an approved scenario modified post-approval
	
	//scenario last action taken
	public final static String SCN_LAST_ACTION_EDITED = 	"edited"; //after scenario modification
	public final static String SCN_LAST_ACTION_SUBMITTED = "submitted"; //after submission for approval
	public final static String SCN_LAST_ACTION_APPROVED = "approved"; //after FIRST approval by admin
	public final static String SCN_LAST_ACTION_REAPPROVED = "reapproved"; //after the second and sucesive approvals
	public final static String SCN_LAST_ACTION_REJECTED = "rejected"; //after scenario has been definitely rejected or killed
	public final static String SCN_LAST_ACTION_RETURNED = "returned"; //after scenario has been returned for clarification
	public final static String SCN_LAST_ACTION_LOCKED = "locked";     //after scenario has been locked
	public final static String SCN_LAST_ACTION_UNLOCKED = "unlocked"; //after scenario has been unlocked
	public final static String SCN_LAST_ACTION_ACK = "acknowledged"; //after scenario has been unlocked
	public final static String SCN_LAST_ACTION_TAG = "tagged"; //after scenario has been unlocked
	
	private final static String STYLE_ROW_EQUIPMENT = "styleRowEquipment";
	private final static String EQUIPMENT_TEXTBOX_WIDTH = "100px";
	private final static String STYLE_WIDTH_90_PERCENT = "90%";
	
	private final static int STYLE_HAZARDS_TEXT_LINES = 5;
	
	/**
	 * Tabs for our CConOps (Clinical concept of Operations)
	 * Background -->tab (0)
	 * Hazards
	 * Environments
	 * Equipment
	 * Proposed State
	 * Benefits+Risks -->tab(5)
	 * References (links)
	 * Tags (Associated Keywords)
	 * 
	 * Feedback-->Approve or reject
	 */	
	private final static int APPRV_SCN_TAB_POS = 8;//position of the tab to approve or reject scn
	private final static int HAZARDS_TAB_POS = 1;//hazards tab
	private final static int EQUIPMENT_TAB_POS = 3;
	
	private static ScenarioPanelUiBinder uiBinder = GWT.create(ScenarioPanelUiBinder.class);
	private UserInfoRequestFactory userInfoRequestFactory = GWT.create(UserInfoRequestFactory.class);
	private TagRequestFactory tagRequestFactory = GWT.create(TagRequestFactory.class);//TICKET-157
	
	//to Id our user
	private enum UserRole {Administrator, RegisteredUser, AnonymousUser}
	private UserRole userRole;	
	private String userEmail;
	private String userId; //TICKET-163 identify user
	
	private boolean editable = false; //indicates if the scenario is editable
	
	/**
	 * Check if its possible to edit this scenario information and updates the flag;
	 */
	private void checkEditable(){
		if(null==currentScenario){
			editable = false;
			return;
		}
		editable = true;
		//Only states unsubmitted, unlocked_pre and unlocked_post are editables
		if (!(currentScenario.getStatus().equals(SCN_STATUS_UNSUBMITTED) || 
				currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE) ||
				currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST))) editable = false;
		//if unsumbitted, only the creator can edit
		if(currentScenario.getStatus().equals(SCN_STATUS_UNSUBMITTED) && !userEmail.equals(currentScenario.getSubmitter())) editable = false;
		
		//if unlocked_pre or unlocked_post, only owner of lock can edit.
		if((currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE) || currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST)) 
				&& !currentScenario.getLockOwner().equals(userEmail)) editable = false;
		
		//Anonymous users can't modify the scenario information
		if(userRole==UserRole.AnonymousUser) editable = false;
	}


	interface Driver extends RequestFactoryEditorDriver<ScenarioProxy, ScenarioPanel> {
		
	}
	Driver driver = GWT.create(Driver.class);
	ScenarioRequestFactory scenarioRequestFactory;
	
	interface ScenarioPanelUiBinder extends UiBinder<Widget, ScenarioPanel> {
	}
	
	/**
	 * Receiver for any search of SINGLE Scenario
	 */
	private Receiver<ScenarioProxy> singleScenarioReceiver = new Receiver<ScenarioProxy>() {
		@Override
		public void onSuccess(ScenarioProxy response) {
			setCurrentScenario(response);
		}
		
		@Override
		public void onFailure(ServerFailure error) {
			super.onFailure(error);
			logger.log(Level.SEVERE, error.getMessage());
		}
	};

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
	 
	 /**
	  * Level of knowledge of hazard occurrence
	  */
	private static final String[] hazardExpected = new String[] {"Unknown", "Expected", "Unexpected"};
	
	/**
	 * Returns the associated index or a word of the hazardExpected array
	 * @param word
	 * @return
	 */
	private int getHazardExpectedIndex(String word){
		for(int i=0; i<hazardExpected.length; i++){
			if(word.equals(hazardExpected[i])) return i;
		}
		return -1;	
	}
	
//	private static final String[] hazardSeverity = new String[] {"Unknown", "Mild", "Moderate", "Severe", "Life Threatening", "Fatal"};
	/**
	 * List of different types/degree of hazards
	 */
	private static final String[] hazardSeverity = new String[] {"Unknown", "No Harm", "Mild", "Moderate", "Severe", "Permanent Harm", "Life Threatening", "Fatal"};
	public static String[] getHazardSeverityValues(){
		return hazardSeverity;
	}
	
	/**
	 * Returns the associated index or a word of the hazardSeverity array
	 * @param word
	 * @return
	 */
	private int getHazardSeverityIndex(String word){
		for(int i=0; i<hazardSeverity.length; i++){
			if(word.equals(hazardSeverity[i])) return i;
		}
		return -1;	
	}
	
	private static final ListBox buildListBox(String[] strings) {
		ListBox box = new ListBox();
		for(String s : strings) {
			box.addItem(s);
		}
		return box;
	}
	
	
	//XXX Legacy code, investigate what these were ment for
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
	
	/**
	 * Print/draws the clinicians table
	 * @param isDrawNew indicates if we are drawing a new/empty table or we are going to
	 *  populate it with data from an existing scenario.
	 */
	private final void buildCliniciansTable(boolean isDrawNew) {
		cliniciansTable.removeAllRows();
		if(isDrawNew || currentScenario.getEnvironments().getCliniciansInvolved().isEmpty()){
			addNewClinicianRow("");			
		}else{
			List<String> clinicians =currentScenario.getEnvironments().getCliniciansInvolved();
			for(int i=0; i<clinicians.size(); i++){
				String text = clinicians.get(i);
				addNewClinicianRow(text);
			}
		}
	}
	
	/**
	 * Prints/draws the environments table
	 * @param isDrawNew indicates if we are drawing a new/empty table or we are going to
	 *  populate it with data from an existing scenario.
	 */
	private final void buildEnvironmentsTable(boolean isDrawNew) {
		environmentsTable.removeAllRows();
		if(isDrawNew || currentScenario.getEnvironments().getClinicalEnvironments().isEmpty()){
			addNewEnvironmentRow("");
		}else{
			List<String> environments = currentScenario.getEnvironments().getClinicalEnvironments();
			for(int i =0; i<environments.size(); i++){
				String text = environments.get(i);
				addNewEnvironmentRow(text);
			}
		}		
	}
	
	//labels for the HEADERS row of the Devices tab table. we use the following static block to initialize some of
	// their properties, such as their title/tooltip TICKET-176; TICKET-140
	private static Label labelEquipmentDeviceType = new Label("Device Type");
	private static Label labelEquipmentManufacturer = new Label("Manufacturer");
	private static Label labelEquipmentModel = new Label("Model");
	private static Label labelEquipmentRosettaId = new Label("Rosetta/Unique ID");
	private static Label labelEquipmentProblemDescrp = new Label("Problem Description");
	private static Label labelEquipmentTrainingProblem = new  Label("Training Related Problem");
	private static Label labelEquipmentInstructionsProblem = new  Label("Instructions Related Problem");
	private static Label labelEquipmentConfusingUsage = new Label("Confusing device Usage");
	private static Label labelEquipmentSoftWProblem = new Label("Software related problem"); 
	private static Label labelEquipmentHardWProblem = new Label("Hardware related problem"); 
	
	{
		//initialize the labels
		labelEquipmentDeviceType.setTitle("Type of device, such as \"ventilator\" or \"Infusion pump\" ");
		labelEquipmentManufacturer.setTitle("Manufacturer/company or device provider");
		labelEquipmentModel.setTitle("Model of the device");
		labelEquipmentRosettaId.setTitle("Unique identifier of this device");
		labelEquipmentProblemDescrp.setTitle("Brief description of the device malfunctioning");
		labelEquipmentTrainingProblem.setTitle("A gap, lack of or inadequate training with this device contributted to the problem");
		labelEquipmentInstructionsProblem.setTitle("Instructions or documentation for this device were confusing, unavailable or outdated" +
				"contributting to the problem");
		labelEquipmentConfusingUsage.setTitle("Confusing interfaces, settings, controls, o general usage procedure of the device contributted to the problem");
		labelEquipmentSoftWProblem.setTitle("A software problem contributed to the problem");
		labelEquipmentHardWProblem.setTitle("A hardware problem contributed to the problem");	
	}
	
	/**
	 * Prints/draws the Equipment tab table.
	 * @param isDrawNew indicates if we are drawing a new/empty table or we are going to
	 *  populate it with data from the scenario.
	 */
	private final void buildEquipmentTable(boolean isDrawNew) {
		equipmentTable.removeAllRows();//clear rows to draw again
		equipmentTable.setStyleName(STYLE_ROW_EQUIPMENT);

		//HEADERS
		equipmentTable.insertRow(0);
		equipmentTable.setWidget(0, EQUIPMENT_DEVICETYPE_COL, labelEquipmentDeviceType);
		equipmentTable.setWidget(0, EQUIPMENT_MANUFACTURER_COL, labelEquipmentManufacturer);
		equipmentTable.setWidget(0, EQUIPMENT_MODEL_COL, labelEquipmentModel);
		equipmentTable.setWidget(0, EQUIPMENT_ROSSETAID_COL, labelEquipmentRosettaId);
		equipmentTable.setWidget(0, EQUIPMENT_EXPLANATION_COL, labelEquipmentProblemDescrp);
		equipmentTable.setWidget(0, EQUIPMENT_TRAINING_PROBLEM_COL, labelEquipmentTrainingProblem);
		equipmentTable.setWidget(0, EQUIPMENT_INSTRUCTION_PROBLEM_COL, labelEquipmentInstructionsProblem);
		equipmentTable.setWidget(0, EQUIPMENT_CONFUSING_USAGE_COL, labelEquipmentConfusingUsage);
		equipmentTable.setWidget(0, EQUIPMENT_SW_PROBLEM_COL, labelEquipmentSoftWProblem);
		equipmentTable.setWidget(0, EQUIPMENT_HW_PROBLEM_COL, labelEquipmentHardWProblem);

		//draw the tables
		if(isDrawNew || currentScenario.getEquipment().getEntries().isEmpty()){
			addNewEquipmentRow();
		
		}else{
			//populate the table w/ the data from the equipment list of the scenario
			List<EquipmentEntryProxy> eqEntries = currentScenario.getEquipment().getEntries();
			for(EquipmentEntryProxy eep : eqEntries){
				addNewEquipmentRow(eep);
			}
		}
	}
	
	//Labels for the header row of the HAZARDS table. We are using the static block
	// to initialize some of the labels properties, such as the title/tooltip of them
	private final static Label labelHazardDescription = new Label("Description");
	private final static Label labelHazardFactors = new Label("Contributing Factors");
	private final static Label labelHazardsExpected = new Label("Expected Risk");
	private final static Label labelHazardsSeverity = new Label("Severity");
	
	{
		//initialize te labels
		labelHazardDescription.setTitle("Describe the risk. Make sure to point out if this risk results in death, is life threatening, requires inpatient hospitalization or prolongation of existing hospitalization, results in persistent or significant disability/incapacity, is a congenital anomaly/birth defect");
		labelHazardFactors.setTitle("Determine which factors are contributing to the risk described below. Examples may be a clinician, a specific device, an aspect of the clinical envirnomnet, etc.");		
		labelHazardsExpected.setTitle("Click to see definition and examples");
		labelHazardsExpected.setStyleName("clickableTitle");
		labelHazardsSeverity.setTitle("Click to see risk severity definition");
		labelHazardsSeverity.setStyleName("clickableTitle");
		
		labelHazardsExpected.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MyDialog md = new MyDialog("Unexpected: Risk is not consistent with the any of risks known (from a manual, label, protocol, instructions, brochure, etc) in the Current State. If these documents are not required or available, the risk is unexpected if specificity or severity is not consistent with the risk information described in the protocol, or it is more severe to the specified risk",
						"Example, Hepatic necrosis would be unexpected (by virtue of greater severity) if the investigator brochure only referred to elevated hepatic enzymes or hepatitis. Similarly, cerebral vasculitis would be unexpected (by virtue of greater specificity) if the investigator brochure only listed cerebral vascular accidents.");
				md.setAutoHideEnabled(true);
				md.showRelativeTo(labelHazardsExpected);
			}
		});
		labelHazardsSeverity.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DialogBox md = new DialogBox(true);
				md.setHTML("<ul>" +
						"<li><b>No Harm</b>: An undesiderable event occurred, but either did not reach the patient or no harm was evident</li>" +
						"<li><b>Mild</b>: Barely noticeable, transient anxiety, pain, or physical discomfort that does not influence functioning or causes limitations of usual activities</li>" +
						"<li><b>Moderate</b>: Makes patient uncomfortable, influences functioning, causing some limitations of usual activities</li>" +
						"<li><b>Severe</b>: Severe discomfort, aditional intervention or treatment needed, severe and undesirable harm, causing inability to carry out usual activities</li>" +
						"<li><b>Permanent Harm</b>: Lifelong bodily or psychological injury or increased susceptibility to disease</li>" +
						"<li><b>Life Threatening</b>: Immediate risk of death, life threatening or disabling</li>" +
						"<li><b>Fatal</b>: Causes death of the patient</li></ul>");
				md.showRelativeTo(labelHazardsSeverity);
			}
		});		
	}
	
	/**
	 * print / draws the hazards table
	 */
	private final void buildHazardsTable(boolean isDrawNew) {
		
		hazardsTable.removeAllRows();//clear and re-populate
		//headers
		hazardsTable.insertRow(0);
		hazardsTable.setWidget(0, HAZARDS_DESCRIPTION_COL, labelHazardDescription);
		hazardsTable.setWidget(0, HAZARDS_FACTORS_COL, labelHazardFactors);
		hazardsTable.setWidget(0,  HAZARDS_EXPECTED_COL, labelHazardsExpected);
		hazardsTable.setWidget(0,  HAZARDS_SEVERITY_COL, labelHazardsSeverity);
		
		//table data
		if(isDrawNew || currentScenario.getHazards().getEntries().isEmpty())
			addNewHazardTableRow();
		else{
			//populate the table
			List<HazardsEntryProxy> hazards = currentScenario.getHazards().getEntries();
			for(int i=0;i<hazards.size();i++){
				HazardsEntryProxy hep = (HazardsEntryProxy) hazards.get(i);
				addNewHazardTableRow(hep.getDescription(), hep.getFactors(), hep.getExpected(), hep.getSeverity());
			}
		}	
		
	}
	
	/**
	 * prints/draws the references table 
	 */
	private final void buildReferencesTable(boolean isDrawNew) {
		referencesTable.removeAllRows();//clear
		//XXX currentScenario.getReferences()!=null because was added after all the other fields
		if(isDrawNew || currentScenario.getReferences()==null || currentScenario.getReferences().getLinkedRefenrences().isEmpty()){
			addNewLinkedReference();			
		}else{
			//populate the table
			for(String ref : currentScenario.getReferences().getLinkedRefenrences()){
				addNewLinkedReference(ref);
			}
		}

	}
	
	/**
	 * TICKET-157. Build the panel with the associated keywords
	 * @param isDrawnNew
	 */
	private final void buildTagsTable(boolean isDrawnNew){
		tagsAssociatedTable.clear();
		tagsScrollPanel.setHeight("0px");//hide
		if(isDrawnNew //||userRole==UserRole.AnonymousUser
				|| !(currentScenario.getStatus().equals(SCN_STATUS_APPROVED) ||
				currentScenario.getStatus().equals(SCN_STATUS_MODIFIED) ||
				currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST) )){
			//XXX needs to be just approved or unlocked too? WHO can tag? Any user?
			tagsLabel.setText("The scenario needs to be APPROVED before any keyword can be tagged");
		}else{
			tagsLabel.setText("Loading Keywords...");
			final boolean tagable = /* editable && TICKET-192 */ userRole==UserRole.Administrator 
					&&(currentScenario.getStatus().equals(SCN_STATUS_APPROVED) ||
							currentScenario.getStatus().equals(SCN_STATUS_MODIFIED) ||
							currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST) );
						
			if(tagRequestFactory != null){
				TagRequest tagRequest = tagRequestFactory.tagRequest();
				tagRequest.findAll().to(new Receiver<List<TagProxy>>() {
					
					@Override
					public void onSuccess(List<TagProxy> response) {
						tagsLabel.setText("");
						Set<String> associatedTags = currentScenario.getAssociatedTags().getAssociatedTagNames();
						int indexRow = 0;
						int indexCol = 0;
						//sort the response w/ the appropriate comparator before traversing
						Collections.sort(response, new TagComparator());
						for(TagProxy tagProxy : response){
							String tagName = tagProxy.getName();
							CheckBox cb = new CheckBox();
							cb.setEnabled(tagable);
							if(associatedTags!=null && associatedTags.size()>0){
								cb.setValue(associatedTags.contains(tagName.toLowerCase()));
							}else{
								cb.setValue(false);
							}
							Label lb = new Label(tagName);
							tagsAssociatedTable.setWidget(indexRow/TAGS_TAB_NUM_COL, indexCol%(2*TAGS_TAB_NUM_COL), cb);	
							tagsAssociatedTable.setWidget(indexRow/TAGS_TAB_NUM_COL, (indexCol%(2*TAGS_TAB_NUM_COL))+1, lb);
							tagsAssociatedTable.getCellFormatter().setStyleName(indexRow/TAGS_TAB_NUM_COL, (indexCol%(2*TAGS_TAB_NUM_COL))+1, "paddingRight");
							indexRow++;
							indexCol+=2;
						}
						tagsScrollPanel.setWidget(tagsAssociatedTable);
						tagsScrollPanel.setHeight("150px");
					}
		
				}).fire();
			}
			
		}
		
	}
	
	private Logger logger = Logger.getLogger(ScenarioPanel.class.getName());
	
	/**
	 * Checks if we need to persist the equipment list. <p>
	 * It deletes all rows of the list of equipment associate with the current scenario and 
	 * adds all the data that is in the table.
	 * @param rc ScenarioRequest
	 */
	private void checkEquipmentListForPersistence(ScenarioRequest rc){
//		if(currentScenario!=null){
			currentScenario.getEquipment().getEntries().clear();//clear equipment list entries. We will re-populate 
			for(int row = 1; row < equipmentTable.getRowCount(); row++) {//Row 0 is HEADERS
				
				Widget wDevType = equipmentTable.getWidget(row, EQUIPMENT_DEVICETYPE_COL);		
				Widget wManu = equipmentTable.getWidget(row, EQUIPMENT_MANUFACTURER_COL);	
				Widget wModel = equipmentTable.getWidget(row, EQUIPMENT_MODEL_COL);	
				Widget wRoss = equipmentTable.getWidget(row, EQUIPMENT_ROSSETAID_COL);	
				Widget wExplanation = equipmentTable.getWidget(row, EQUIPMENT_EXPLANATION_COL);	
				Widget wTraining = equipmentTable.getWidget(row, EQUIPMENT_TRAINING_PROBLEM_COL);	
				Widget wInstruction = equipmentTable.getWidget(row, EQUIPMENT_INSTRUCTION_PROBLEM_COL);	
				Widget wUsage = equipmentTable.getWidget(row, EQUIPMENT_CONFUSING_USAGE_COL);	
				Widget wSoftProblem = equipmentTable.getWidget(row, EQUIPMENT_SW_PROBLEM_COL);	
				Widget wHardProblem = equipmentTable.getWidget(row, EQUIPMENT_HW_PROBLEM_COL);					
				
				EquipmentEntryProxy eep = rc.create(EquipmentEntryProxy.class);
				
				boolean isAdding = false;
				String text = null;
				
				//check if at least one of the textboxes has non-empty data
				if(wDevType instanceof TextBox) {
					text = ((TextBox)wDevType).getText().trim();
					if(!text.equals(""))
						{eep.setDeviceType(text); isAdding=true;}
				}
				if(wManu instanceof TextBox) {
					text = ((TextBox)wManu).getText().trim();
					if(!text.equals(""))
					{eep.setManufacturer(text); isAdding=true;}
				}
				if(wModel instanceof TextBox) {
					text = ((TextBox)wModel).getText().trim();
					if(!text.equals(""))
					{eep.setModel(text); isAdding=true;}
				}
				if(wRoss instanceof TextBox) {
					text = ((TextBox)wRoss).getText().trim();
					if(!text.equals(""))
					{eep.setRosettaId(text); isAdding=true;}		
				}
				if(wExplanation instanceof TextArea) {
					text = ((TextArea)wExplanation).getText().trim();
					if(!text.equals(""))
					{eep.setProblemDescription(text); isAdding=true;}		
				}
				
				//The ckeckboxes by themselves don't mofify the "adding" condition.
				// At least any other ot fields must be non-empty
				eep.setTrainingRelatedProblem(((CheckBox) wTraining).getValue());
				eep.setInstructionsRelatedProblem(((CheckBox) wInstruction).getValue());
				eep.setConfusingDeviceUsage(((CheckBox) wUsage).getValue());
				eep.setSoftwareRelatedProblem(((CheckBox) wSoftProblem).getValue());
				eep.setHardwareRelatedProblem(((CheckBox) wHardProblem).getValue());
				
				if(isAdding)
					currentScenario.getEquipment().getEntries().add(eep);
			}
//		}//currente scen null
	}
	
	/**
	 * Checks if we need to persist the hazards list <p>
	 * @param rc ScenarioRequest
	 */
	private void checkHazardsListForPersistence(ScenarioRequest rc){
			currentScenario.getHazards().getEntries().clear();//clean and re-populate
//			for(int row =2; row<hazardsTable.getRowCount();row++){//row 0 headers; row 1 description
			for(int row =1; row<hazardsTable.getRowCount();row++){//row 0 headers+description
				
				Widget wDescription = hazardsTable.getWidget(row, HAZARDS_DESCRIPTION_COL);	
				Widget wFactor = hazardsTable.getWidget(row, HAZARDS_FACTORS_COL);	
				Widget wExpected = hazardsTable.getWidget(row, HAZARDS_EXPECTED_COL);		
				Widget wSeverity = hazardsTable.getWidget(row, HAZARDS_SEVERITY_COL);	
				HazardsEntryProxy hep = rc.create(HazardsEntryProxy.class);
				
				boolean isAdding = false;
				String text = null;
				
				//check for non-empty data 
				//We assume that makes no sense having no text for DEscription/factor and trying to persist
				// values for risk/severity (there would be no hazard identification) 
				if(wDescription instanceof TextArea){
					text = ((TextArea) wDescription).getText().trim();
					if(!text.equals("")){hep.setDescription(text); isAdding=true;}
				}
				if(wFactor instanceof TextArea){
					text = ((TextArea) wFactor).getText().trim();
					if(!text.equals("")){hep.setFactors(text); isAdding =true;}
				}
				
				if(isAdding){
					if(wExpected instanceof ListBox){
						int val = ((ListBox) wExpected).getSelectedIndex();
						text = hazardExpected[val];
						hep.setExpected(text);
					}
					if(wSeverity instanceof ListBox){
						int val = ((ListBox) wSeverity).getSelectedIndex();
						text = hazardSeverity[val];
						hep.setSeverity(text);
					}
					//add values to the list
					currentScenario.getHazards().getEntries().add(hep);
				}
			}
			
	}
	
	/**
	 * Checks if we need to persist the clinicians list
	 * @param scn
	 */
	private void checkCliniciansListForPersistence(/*ScenarioRequest scn*/){
			currentScenario.getEnvironments().getCliniciansInvolved().clear();
			//delete the list and repopulate w/ data from the table
			for(int row=0; row<cliniciansTable.getRowCount();row++){
				Widget wClinician = cliniciansTable.getWidget(row, CLINICIANS_TYPE_COL);
				if(wClinician instanceof SuggestBox){
					String text = ((SuggestBox) wClinician).getText().trim();
					if(!text.equals(""))
						currentScenario.getEnvironments().getCliniciansInvolved().add(text);
				}
			}		
	}
	
	/**
	 * Checks if we need to persist the environments list
	 */
	private void checkEnvironmentsListForPersistence(){
			currentScenario.getEnvironments().getClinicalEnvironments().clear();
			//delete the list and re-populate w/ data from the table
			for(int row=0; row<environmentsTable.getRowCount();row++){
				Widget wEnvironment = environmentsTable.getWidget(row, ENVIRONMENT_TYPE_COL);
				if(wEnvironment instanceof SuggestBox){
					String text = ((SuggestBox) wEnvironment).getText().trim();
					if(!text.equals(""))
						currentScenario.getEnvironments().getClinicalEnvironments().add(text);
				}
			}		
	}
	
	/**
	 * checks if we need to persist references
	 */
	private void checkReferencesListForPersistence(){
		currentScenario.getReferences().getLinkedRefenrences().clear();
		//delete and repopulate
		for(int row =0; row<referencesTable.getRowCount(); row++){
			Widget w = referencesTable.getWidget(row, REFERENCE_TEXT_COL);
			if(w instanceof TextArea){
				String ref = ((TextArea) w).getText();
				if(!ref.trim().equals(""))
					currentScenario.getReferences().getLinkedRefenrences().add(ref);
			}
		}
	}
	
	/**
	 * Checks the content of the user interface fields and updates the information in the 
	 * scenario entity that we are going to use for persistence
	 * @param scnReq a ScenarioRequest object is needed to create some of the components of our list-type parameters
	 *  (list of hazards, list of equipment).
	 */
	private void checkScenarioFields(ScenarioRequest scnReq){
		
		//Save equipment list
		checkEquipmentListForPersistence(scnReq);
		//Save hazards list
		checkHazardsListForPersistence(scnReq);
		//save clinicians list
		checkCliniciansListForPersistence();
		//save environments
		checkEnvironmentsListForPersistence();	
		//save references
		checkReferencesListForPersistence();
	}
	
	/**
	 * Saves or persist a Scenario
	 * <p> 1- The TextArea fields are associated automatically w/ the corresponding attributes in the Scenario object 
	 * (Background, proposed solution and Benefits & risks)
	 * <p> 2- Save the list of equipment
	 * <p> 3- Save the list of hazards
	 * <p> 4- Save Clinicians and Environment List
	 * <p> Persist the scenario entity with all its associated values
	 */
	private void save(){	
		if(!editable) return;	//Ticket-144 Mutual exclusion / persistence permission	
		if(isEmptyScenario()) return;//Ticket-81 Don't persist empty Scn
		
		currentScenario.setLastActionTaken(SCN_LAST_ACTION_EDITED);
		currentScenario.setLastActionUser(userEmail);
		
		status.setText("SAVING");			
		ScenarioRequest rc = (ScenarioRequest) driver.flush();
	
		checkScenarioFields(rc);
			
		//persist scenario entity
		rc.persist().using(currentScenario)
		.with(driver.getPaths()).with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
//		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(new Receiver<ScenarioProxy>() {


			@Override
			public void onSuccess(ScenarioProxy response) {
//			    logger.info("RESPONSE|currentState:"+response.getBackground().getCurrentState()+" proposedState:"+response.getBackground().getProposedState());
				status.setText("SAVED");
				scenarioRequestFactory.getEventBus().fireEvent(new EntityProxyChange<ScenarioProxy>(response, WriteOperation.UPDATE));
//				logger.info("DURING:"+response.getTitle());
				setCurrentScenario(response);
//				logger.info("AFTER:"+currentScenario.getTitle());
				logger.info("SAVED "+currentScenario.toString()+" @ "+ (new Date()));
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
				//log message
			}
			
		}).fire();
		
	}
	
	/**
	 * Selects the first tab of the form. <p>
	 * XXX Careful with circular calls selectTab->save->setScenario->selectTab(0)
	 */
	public void selectFirstTab(){
		editable = false;//prevent save() call due to SelectionHandler and so funny behavior
		/**
		 * This editable = false condition is here to prevent the following behavior:
		 *  In case that we are in an editable scenario, in a tab different from the first one, when
		 *  we move to a different scenario this selectFirstTab() method is called. Since we have a 
		 *  SelectionHandler attached to the tabPanel, this method will fire the handler event.
		 *  
		 *  The effect is that we see the new selected scenario briefly and the it changes to the old one,
		 *  because the save() method from the tabSelection also performs a setCurrentScenario, with the information
		 *  of the (old/previous) scenario being saved.
		 */
		
		if(tabPanel.getTabBar().getSelectedTab()!=0)
			tabPanel.selectTab(0);

	}
	/**
	 * Listener for when the tabs are clicked (user moves to a different tab).
	 * Should save the current scenario. Dependig on the tab we are some properties (style) change
	 */
	SelectionHandler<Integer> tabPanelSelectionHandler = new SelectionHandler<Integer>() {			
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			if(currentScenario != null){					 
				save();
			}
			//TICKET-165
			if(event.getSelectedItem().equals(new Integer(HAZARDS_TAB_POS)) ){
//					||event.getSelectedItem().equals(EQUIPMENT_TAB_POS)){//XXXX ???
				tabPanel.setWidth("80%"); 
			}else{
				tabPanel.setWidth("60%"); //TICKET-165
			}
				
		}
	};
	
	//constructor
	public ScenarioPanel(final ScenarioRequestFactory scenarioRequestFactory) {
		logger.setLevel(Level.INFO);
		
		initWidget(uiBinder.createAndBindUi(this));
		this.scenarioRequestFactory = scenarioRequestFactory;
		driver.initialize(scenarioRequestFactory, this);

		//Handler to save the entity when something changes in the data fields
		ChangeHandler saveOnChange = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				save();
			}			
		};

		//associate handlers and value entities
	    titleEditor.addChangeHandler(saveOnChange);
		proposedStateEditor.addChangeHandler(saveOnChange);
		currentStateEditor.addChangeHandler(saveOnChange);
		benefits.addChangeHandler(saveOnChange);
		risks.addChangeHandler(saveOnChange);
		clinicalProcesses.addChangeHandler(saveOnChange);
		algorithmDescription.addChangeHandler(saveOnChange);
		
		//Listener for when the tabs are clicked (user moves to a different tab)
		tabPanel.addSelectionHandler(tabPanelSelectionHandler);
//		tabPanel.setWidth("80%"); //TICKET-165
		hazardsTable.setWidth("100%");
		
		//select first tab
		selectFirstTab();

		manageScnStatus = tabPanel.getWidget(APPRV_SCN_TAB_POS);//get Feedback_Tab (reject/approve Scn widget)
		
		//check user role
		if(userInfoRequestFactory != null){
			final EventBus eventBus = new SimpleEventBus();
			userInfoRequestFactory.initialize(eventBus);
		
		UserInfoRequest userInfoRequest = userInfoRequestFactory.userInfoRequest();
		userInfoRequest.findCurrentUserInfo(Window.Location.getHref(), false).with("loginURL").to(new Receiver<UserInfoProxy>() {

				@Override
				public void onSuccess(UserInfoProxy response) {
					userEmail = response.getEmail();
					userId =response.getUserId();

					if(response.getEmail()==null ||response.getEmail().trim().equals("") ){
						//Anonymous user
						userRole = UserRole.AnonymousUser;
					}else{
						if(response.getAdmin()) 
							userRole = UserRole.Administrator;
						else
							userRole = UserRole.RegisteredUser;
					}
					checkEditable();
					
				}
				@Override
				public void onFailure(ServerFailure error) {
					super.onFailure(error);
					Window.alert(error.getMessage());
				}
			}).fire();
		
		}
		
		//Ticket-157
		if(tagRequestFactory != null){
			final EventBus eventBus = new SimpleEventBus();
			tagRequestFactory.initialize(eventBus);
		}
		
		rejectScnButton.setTitle("Reject this scenario if is not even worth asking for submiter's clarification, but want to keep it instead of delete. Can NOT be undone");
		returnScnButton.setTitle("Return this scenario to the submitter for clarification.");
		approveScnButton.setTitle("Validate this scenario and make it available to all users.");
					
	}
	
	/**
	 * Disables the "editable" property of a Scenario. We don't see the buttons to save, 
	 * we can't interact w/ the panels
	 */
	private void disableSaveScenario(){
//		status.setText("You don't have permission to modify the scenario");
		status.setVisible(false);
				
		algorithmDescription.setReadOnly(true);
		clinicalProcesses.setReadOnly(true);
		risks.setReadOnly(true);
		benefits.setReadOnly(true);
		titleEditor.setReadOnly(true);
		currentStateEditor.setReadOnly(true);
		proposedStateEditor.setReadOnly(true);
	}
	
	/**
	 * Enables the different fields of the panels to allow editing information
	 */
	private void enableSaveScenario(){				
		status.setVisible(true);//TICKET-176
		algorithmDescription.setReadOnly(false);
		clinicalProcesses.setReadOnly(false);
		risks.setReadOnly(false);
		benefits.setReadOnly(false);
		titleEditor.setReadOnly(false);
		currentStateEditor.setReadOnly(false);
		proposedStateEditor.setReadOnly(false);	
	}
	
	/**
	 * builds the tables of the different panels and fills them with the appropriate information
	 */
	private void buildTabsTables(boolean drawNew){
	    buildEquipmentTable(drawNew);//new scn. No equipment list
	    buildHazardsTable(drawNew);
		buildCliniciansTable(drawNew);
		buildEnvironmentsTable(drawNew);
		buildReferencesTable(drawNew);
		buildTagsTable(drawNew);//Ticket-157
	}

	
	private ScenarioProxy currentScenario;
	
	public void setCurrentScenario(ScenarioProxy currentScenario) {
		
		ScenarioRequest context = scenarioRequestFactory.scenarioRequest();
		if(null == currentScenario) {		
			    context.create()
			    .with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
			    .to(new Receiver<ScenarioProxy>() {
		    	
	                @Override
	                public void onSuccess(ScenarioProxy response) {
	                	if(response==null){
	                		Window.alert("Please, Sign In to create new Scenarios");
	                		titleEditor.setText("Please sign in to create new Clinical Scenarios");
	                	}
	                    logger.info(""+response.getBackground());
	                    ScenarioRequest context = scenarioRequestFactory.scenarioRequest();
	                    ScenarioProxy currentScenario = context.edit(response); 
	                    driver.edit(currentScenario, context);  
	                    ScenarioPanel.this.currentScenario = currentScenario;
                  	                    
	                    //after the Entity has been succesfully created, we populate the widgets w/ the entity info and draw it
	        			configureComponents();  
	        			buildTabsTables(true);//Ticket-175 build tables AFTER "editable" has been checked on configureComponents()
	    			    status.setText("");
	    			    uniqueId.setText("");	
	                }
	                
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
						logger.log(Level.SEVERE, error.getMessage());
					}
			        
			    }).fire();

		} else {
		    currentScenario = context.edit(currentScenario); 
            driver.edit(currentScenario, context);
            this.currentScenario = currentScenario;
    		if(currentScenario.getId()!=null)
    			uniqueId.setText("Scenario Unique ID: "+String.valueOf(currentScenario.getId()));  		

    		configureComponents();//XXX Maybe configureComponents() should be called inside buildTabsTables
    		buildTabsTables(false);//Ticket-175 build tables AFTER "editable" has been checked on configureComponents()
		}
		
		//select first tab
//		tabPanel.selectTab(0); DANGER DANGER DANGER
		//XXX CAREFUL!!! Selecting a tab here triggers again the associated tab ClickHandler, that calls this very same method
		// and we have a circular call and therefore an infinite loop. 
 

	}
	
	/**
	 * Configures the different components (status label, buttons, etc.) according to scenario status, user role...
	 */
	private void configureComponents(){
		checkEditable();
		//1- Save button
		if(!editable){
			saveButton.setVisible(false);
			submitButton.setVisible(false);
			disableSaveScenario();
		}else{
			saveButton.setVisible(true);
			submitButton.setVisible(true);
			enableSaveScenario();
		}
		//2- submit button
		if(currentScenario.getStatus().equals(SCN_STATUS_UNSUBMITTED) && currentScenario.getSubmitter().equals(userEmail)){
			submitButton.setVisible(true);
		}else{
			submitButton.setVisible(false);
		}
		//3- lock button
		if(userRole==UserRole.Administrator){
			if(currentScenario.getStatus().equals(SCN_STATUS_UNSUBMITTED) || currentScenario.getStatus().equals(SCN_STATUS_REJECTED)){
				lockButton.setVisible(false);
			}else{
				lockButton.setVisible(true);
				if(currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE) || currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST)){
					lockButton.setText("Lock");
				}else{
					lockButton.setText("Unlock");
				}
			}
		}else{
			lockButton.setVisible(false);
		}

		//4- Feedback tab panel
		if(userRole==UserRole.Administrator && (//currentScenario.getStatus().equals(SCN_STATUS_SUBMITTED) ||
				currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE) ||
				currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST))){
			if(tabPanel.getWidgetCount() <= APPRV_SCN_TAB_POS)//add Feedback tab if is not there
				tabPanel.add(manageScnStatus, "Approve or Reject");
			//feedbak tab buttons
			//4.1 Approve button: always visible
			approveScnButton.setVisible(true);
			//4.2 and 4.3 Return and reject scenario, only possible for unlocked pre-approved scenarios.
			if(currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE)){
				returnScnButton.setVisible(true);
				rejectScnButton.setVisible(true);
				feedbackBodyLabel.setVisible(true);//ticket-190
				feedbackSubjectLabel.setVisible(true);
				feedback.setVisible(true);
				feedbackLabel.setVisible(true);
			}else{
				returnScnButton.setVisible(false);
				rejectScnButton.setVisible(false);
				feedbackBodyLabel.setVisible(false);//ticket-190
				feedbackSubjectLabel.setVisible(false);
				feedback.setVisible(false);
				feedbackLabel.setVisible(false);
			}

		}else{
			//if the tab is already showing we remove it
			if(tabPanel.getWidgetCount() > APPRV_SCN_TAB_POS){
				tabPanel.remove(APPRV_SCN_TAB_POS);
				selectFirstTab();
			}
		}
//		//5- Export to file button
//		if(currentScenario.getStatus().equals(SCN_STATUS_REJECTED) )
//			exportScenario.setVisible(false);
//		else
//			exportScenario.setVisible(true);
		
		//5- Acknowledge Scenario
		//If this user has previously acknowledged the scenario, we hide the button
		//Ack button only present for REGISTERED users on APRROVED scenarios
		Set<String> ackEdgers = currentScenario.getAcknowledgers().getAcknowledgersIDs();
		if(userRole!= UserRole.AnonymousUser && (currentScenario.getStatus().equals(SCN_STATUS_APPROVED) 
				|| currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST)
				|| currentScenario.getStatus().equals(SCN_STATUS_MODIFIED))){		
			if(ackEdgers == null){
				//can be null because is a new field, and the previous entities on the GAE don't have this field 
				ackButton.setVisible(true);//because is obvious that this user didn't ack the scenario
			}else{
				ackButton.setVisible(!currentScenario.getAcknowledgers().getAcknowledgersIDs().contains(userId));
			}
//			ackButton.setVisible(currentScenario.getAcknowledgers().contains(userId));
			//XXX display the above information only if the current scenarios already has acks?

		}else{
			ackButton.setVisible(false);//either unregistered user or scn not approved
		}
		
		if(ackEdgers != null && currentScenario.getAcknowledgers().getAcknowledgersIDs().size()>0){
			//we show ack feedback in all cases
			ackLabel.setVisible(true);
			ackLabel.setText(currentScenario.getAcknowledgers().getAcknowledgersIDs().size()+ " Users have acknowledged this scenario");
			ackLabel.setTitle(currentScenario.getAcknowledgers().getAcknowledgersIDs().size()+" Users confirmed the importance and genuinity of this scenario"+
					" by having experienced or known of similar problems to the ones described in it.");
		}else{
			ackLabel.setVisible(false);
		}
		//6 Add Tags to Scenario
		//XXX This condition could be approved (or post approved) + no unregistered user 
		boolean tagable = /* editable && XXX TICKET-192 */ userRole==UserRole.Administrator 
				&&(currentScenario.getStatus().equals(SCN_STATUS_APPROVED) ||
						currentScenario.getStatus().equals(SCN_STATUS_MODIFIED) ||
						currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST) );
		saveTagsButton.setVisible(tagable);

	}
	
	
	@UiField
	TabPanel tabPanel;
	
	final Widget manageScnStatus; //= tabPanel.getWidget(APPRV_SCN_TAB_POS);
	
	@UiField
	@Ignore
	TextArea feedback;//feedback for Scn approval / rejection
	
	@UiField
	@Ignore
	Label feedbackSubjectLabel;//Ticket-190
	
	@UiField
	@Ignore
	Label feedbackBodyLabel;//Ticket-190
	
	@UiField
	@Ignore
	Label feedbackLabel;//Ticket-190
		
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
	Anchor clinicanExample;
	
	@UiField
	@Ignore
	Anchor	environmentExample;
	
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
	Anchor addNewEquipment;//adds a new empty equipment row
	
	@UiField
	@Ignore
	Anchor addNewHazard;//adds a new empty hazards row
	
	//references tab
	@UiField
	@Ignore
	FlexTable referencesTable;
	
	@UiField
	@Ignore
	Anchor addNewLinkedReference;//adds a new reference row
	
	@UiField
	@Ignore
	Button saveTagsButton;
	
	@UiField
	@Ignore
	FlexTable tagsAssociatedTable; //tagsTable (tagsTab) TICKET-157
	
	@UiField
	@Ignore
	ScrollPanel tagsScrollPanel; //tagsTable (tagsTab) TICKET-157
	
//	@UiField
//	@Ignore
//	VerticalPanel tagsVerticalPanel; //tagsTable (tagsTab) TICKET-157
	
	@UiField
	@Ignore
	Label tagsLabel; // (tagsTable)
	
	
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
	public static ClinicianSuggestOracle getClinicianSuggestOracle(){
		return new ClinicianSuggestOracle();
	}
	
	
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
		"ICU-intensive care unit",
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
	private final EnvironmentSuggestOracle environmentSuggestOracle = new EnvironmentSuggestOracle();
	public static EnvironmentSuggestOracle getEnvironmentSuggestOracle(){
		return new EnvironmentSuggestOracle();
	}
	
	//-----------------------------------------
	//ANCHORS 
	//-----------------------------------------
	//When clicking in "AddNew Equipment" anchor
	@UiHandler("addNewEquipment")
	void onAddNewEqClick(ClickEvent click) {
		if(editable){//TICKET-110
			addNewEquipmentRow();
		}
	}
	
	private void addNewEquipmentRow(){
		final int rows = equipmentTable.getRowCount();
		equipmentTable.insertRow(rows);

		//Textboxes for type, manufacturer, model and Id
		for(int j = 0; j < EQUIPMENT_EXPLANATION_COL; j++) {//add four text boxes
			TextBox textbox = new TextBox();
			textbox.setWidth(EQUIPMENT_TEXTBOX_WIDTH);
			textbox.setReadOnly(!editable);
			equipmentTable.setWidget(rows, j, textbox);
		}
		
		//Add problem explanation column
		TextArea textArea = new TextArea();
		textArea.setReadOnly(!editable);
		equipmentTable.setWidget(rows, EQUIPMENT_EXPLANATION_COL, textArea);
		
		//checkboxes for training, instructions, usage, SW and HW problems
		for(int j = EQUIPMENT_TRAINING_PROBLEM_COL; j < EQUIPMENT_DElETEBUTTON_COL; j++) {//add four check boxes
			CheckBox cb = new CheckBox();
			cb.setEnabled(editable);
			equipmentTable.setWidget(rows, j, cb);
		}
		
		//add delete button
		Button deleteButton = new Button("Delete");
		if(editable)
			equipmentTable.setWidget(rows, EQUIPMENT_DElETEBUTTON_COL, deleteButton);

		//click handler that deletes the current row
		deleteButton.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				equipmentTable.removeRow(rows);
			}
		});
	}
	
	private void addNewEquipmentRow(EquipmentEntryProxy eep){
		final int rows = equipmentTable.getRowCount();
		equipmentTable.insertRow(rows);

		TextBox dtTextbox = new TextBox();
		dtTextbox.setText(eep.getDeviceType());
		dtTextbox.setReadOnly(!editable);
		dtTextbox.setWidth(EQUIPMENT_TEXTBOX_WIDTH);
		equipmentTable.setWidget(rows, EQUIPMENT_DEVICETYPE_COL, dtTextbox);
		
		TextBox manufTextBox = new TextBox(); 
		manufTextBox.setText(eep.getManufacturer());
		manufTextBox.setReadOnly(!editable);
		manufTextBox.setWidth(EQUIPMENT_TEXTBOX_WIDTH);
		equipmentTable.setWidget(rows, EQUIPMENT_MANUFACTURER_COL, manufTextBox);
		
		TextBox modelTextBox = new TextBox(); 
		modelTextBox.setText(eep.getModel());
		modelTextBox.setReadOnly(!editable);
		modelTextBox.setWidth(EQUIPMENT_TEXTBOX_WIDTH);
		equipmentTable.setWidget(rows, EQUIPMENT_MODEL_COL, modelTextBox);
		
		TextBox rossTextBox = new TextBox(); 
		rossTextBox.setText(eep.getRosettaId());
		rossTextBox.setReadOnly(!editable);
		rossTextBox.setWidth(EQUIPMENT_TEXTBOX_WIDTH);
		equipmentTable.setWidget(rows, EQUIPMENT_ROSSETAID_COL, rossTextBox);
		
		TextArea explanationTextArea = new TextArea();
		explanationTextArea.setText(eep.getProblemDescription());
		explanationTextArea.setReadOnly(!editable);
		equipmentTable.setWidget(rows, EQUIPMENT_EXPLANATION_COL, explanationTextArea);
		
		CheckBox trainingCheckBox = new CheckBox();
		trainingCheckBox.setEnabled(editable);
		trainingCheckBox.setValue(eep.getTrainingRelatedProblem());
		equipmentTable.setWidget(rows, EQUIPMENT_TRAINING_PROBLEM_COL, trainingCheckBox);
		
		CheckBox instructionsCheckBox = new CheckBox();
		instructionsCheckBox.setEnabled(editable);
		instructionsCheckBox.setValue(eep.getInstructionsRelatedProblem());
		equipmentTable.setWidget(rows, EQUIPMENT_INSTRUCTION_PROBLEM_COL, instructionsCheckBox);
		
		CheckBox usageCheckBox = new CheckBox();
		usageCheckBox.setEnabled(editable);
		usageCheckBox.setValue(eep.getConfusingDeviceUsage());
		equipmentTable.setWidget(rows, EQUIPMENT_CONFUSING_USAGE_COL, usageCheckBox);	
		
		CheckBox softProblemCheckBox = new CheckBox();
		softProblemCheckBox.setEnabled(editable);
		softProblemCheckBox.setValue(eep.getSoftwareRelatedProblem());
		equipmentTable.setWidget(rows, EQUIPMENT_SW_PROBLEM_COL, softProblemCheckBox);
		
		CheckBox hardProblemCheckBox = new CheckBox();
		hardProblemCheckBox.setEnabled(editable);
		hardProblemCheckBox.setValue(eep.getHardwareRelatedProblem());
		equipmentTable.setWidget(rows, EQUIPMENT_HW_PROBLEM_COL, hardProblemCheckBox);
		
		Button deleteButton = new Button("Delete");
		if(editable)
			equipmentTable.setWidget(rows, EQUIPMENT_DElETEBUTTON_COL, deleteButton);
		
		//add click handler to the delete button
		deleteButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				equipmentTable.removeRow(rows);
			}
		});
	}
	
	
	//When clicking in "AddNew Equipment" anchor
	@UiHandler("addNewHazard")
	void onAddNewHazardClick(ClickEvent click) {
		/**
		 * We use the currentStateEditor to know if the components have been enabled/disabled for modification
		 * and thus know if we should allow to create new empty rows
		 */
		if(currentStateEditor.isEnabled())//TICKET-110
			addNewHazardTableRow();
	}
	
	/**
	 * Adds a new empty row to the hazards table
	 */
	private void addNewHazardTableRow(){
		
		final int row = hazardsTable.getRowCount();
		hazardsTable.insertRow(row);
		final TextArea hazardDescription = new TextArea();
		hazardDescription.setVisibleLines(STYLE_HAZARDS_TEXT_LINES);
		hazardDescription.setCharacterWidth(40);
		hazardDescription.setReadOnly(!editable);
		hazardDescription.setWidth(STYLE_WIDTH_90_PERCENT);

		final TextArea hazardFactors = new TextArea();
		hazardFactors.setVisibleLines(STYLE_HAZARDS_TEXT_LINES);
		hazardFactors.setCharacterWidth(40);
		hazardFactors.setReadOnly(!editable);
		hazardFactors.setWidth(STYLE_WIDTH_90_PERCENT);
		
		hazardsTable.setWidget(row, HAZARDS_DESCRIPTION_COL, hazardDescription);
		hazardsTable.setWidget(row, HAZARDS_FACTORS_COL, hazardFactors);
		hazardsTable.setWidget(row, HAZARDS_EXPECTED_COL, buildListBox(hazardExpected));
		hazardsTable.setWidget(row, HAZARDS_SEVERITY_COL, buildListBox(hazardSeverity));
		
		Button deleteButton = new Button("Delete");
		if(editable)
			hazardsTable.setWidget(row, HAZARDS_DELETEBUTTON_COL, deleteButton);
		deleteButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				hazardsTable.removeRow(row);
				
			}
		});
	}
	
	/**
	 * Adds a new empty row to the hazards table
	 * @param description
	 * @param factors
	 * @param expected
	 * @param severity
	 */
	private void addNewHazardTableRow(String description, String factors, String expected, String severity){
		final int row = hazardsTable.getRowCount();
		hazardsTable.insertRow(row);
		final TextArea hazardDescription = new TextArea();
		hazardDescription.setText(description);
		hazardDescription.setVisibleLines(STYLE_HAZARDS_TEXT_LINES);
		hazardDescription.setCharacterWidth(40);
		hazardDescription.setReadOnly(!editable);
		hazardDescription.setWidth(STYLE_WIDTH_90_PERCENT);
		
		final TextArea hazardFactors = new TextArea();
		hazardFactors.setText(factors);
		hazardFactors.setVisibleLines(STYLE_HAZARDS_TEXT_LINES);
		hazardFactors.setCharacterWidth(40);
		hazardFactors.setReadOnly(!editable);
		hazardFactors.setWidth(STYLE_WIDTH_90_PERCENT);
		
		final ListBox hazardsExpected = buildListBox(hazardExpected);
		int indexExpected = getHazardExpectedIndex(expected);
		hazardsExpected.setSelectedIndex(indexExpected);
		
		final ListBox hazardsSeverity = buildListBox(hazardSeverity);
		int indexSeverity = getHazardSeverityIndex(severity);
		hazardsSeverity.setSelectedIndex(indexSeverity);
		
		hazardsTable.setWidget(row, HAZARDS_DESCRIPTION_COL, hazardDescription);
		hazardsTable.setWidget(row, HAZARDS_FACTORS_COL, hazardFactors);
		hazardsTable.setWidget(row, HAZARDS_EXPECTED_COL, hazardsExpected);
		hazardsTable.setWidget(row, HAZARDS_SEVERITY_COL, hazardsSeverity);
		
		Button deleteButton = new Button("Delete");
		if(editable){ hazardsTable.setWidget(row, HAZARDS_DELETEBUTTON_COL, deleteButton);}
		
		deleteButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				hazardsTable.removeRow(row);
				
			}
		});
	}
	
	/**
	 * Add a new empty reference box
	 */
	private void addNewLinkedReference(){
		final int rows = referencesTable.getRowCount();
		referencesTable.insertRow(rows);//add new roe
		referencesTable.setWidget(rows, REFERENCE_http_COL, new Label("http://"));
		final TextArea reference = new TextArea();
		reference.setVisibleLines(1);
		reference.setCharacterWidth(70);
		reference.setReadOnly(!editable);
		referencesTable.setWidget(rows, REFERENCE_TEXT_COL, reference);
		
		Button deleteButton = new Button("Delete");
		if(editable)
			referencesTable.setWidget(rows, REFERENCE_DELETEBUTTON_COL, deleteButton);
		
		deleteButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				referencesTable.removeRow(rows);				
			}
		});
		
	}
	
	//When clicking in "AddNew addNewLinkedReference" anchor
	@UiHandler("addNewLinkedReference")
	void onAddNewReferenceClick(ClickEvent click) {
		if(editable)
			addNewLinkedReference();
	}
	
	private void addNewLinkedReference(String ref){
		final int rows = referencesTable.getRowCount();
		referencesTable.insertRow(rows);//add new roe
		referencesTable.setWidget(rows, REFERENCE_http_COL, new Label("http://"));
		final TextArea reference = new TextArea();
		reference.setVisibleLines(1);
		reference.setCharacterWidth(70);
		reference.setText(ref);
		reference.setReadOnly(!editable);
		referencesTable.setWidget(rows, REFERENCE_TEXT_COL, reference);
		
		Button deleteButton = new Button("Delete");
		if(editable)
			referencesTable.setWidget(rows, REFERENCE_DELETEBUTTON_COL, deleteButton);
		
		deleteButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				referencesTable.removeRow(rows);
				
			}
		});
		
		Button followLink = new Button("Follow link");
		referencesTable.setWidget(rows, REFERENCE_FOLLOWBUTTON_COL, followLink);
		followLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
//				System.out.println(reference.getText());
				if(!reference.getText().trim().equals(""))
					Window.open("http://"+reference.getText(), "_blank", "");//use "enabled" as third argument to open in new tab	
//				Window.open("http://www.google.com/", "_blank", "");
			}
		});
	}
	

	
	@UiHandler("addNewClinician")
	void onANCClick(ClickEvent click) {
		/**
		 * We use the currentStateEditor to know if the components have been enabled/disabled for modification
		 * and thus know if we should allow to create new empty rows
		 */
		if(editable){//TICKET-110
			final int rows = cliniciansTable.getRowCount();
			cliniciansTable.insertRow(rows);
			final SuggestBox sb = new SuggestBox(clinicianSuggestOracle);
			sb.setStyleName("wideSuggest");
			cliniciansTable.setWidget(rows, CLINICIANS_TYPE_COL, sb);
				
			//to delete this entry
			Button deleteButton = new Button("Delete");
			deleteButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					cliniciansTable.removeRow(rows);
					
				}
			});
			cliniciansTable.setWidget(rows, CLINICIANS_DELETEBUTTON_COL, deleteButton);
		}
	}
	
	/**
	 * Adds a new clinician to the clinicians table
	 * @param clinician name/type of clinician
	 */
	private void addNewClinicianRow(String clinician) {
		final int rows = cliniciansTable.getRowCount();
		cliniciansTable.insertRow(rows);
		final SuggestBox sb = new SuggestBox(clinicianSuggestOracle);
		sb.setText(clinician);
		sb.setStyleName("wideSuggest");
		sb.setEnabled(editable);
		cliniciansTable.setWidget(rows, CLINICIANS_TYPE_COL, sb);
				
		//to delete this entry
		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cliniciansTable.removeRow(rows);
				
			}
		});
		if(editable)
			cliniciansTable.setWidget(rows, CLINICIANS_DELETEBUTTON_COL, deleteButton);
	}
	
	@UiHandler("addNewEnvironment")
	void onANEClick(ClickEvent click) {
		/**
		 * We use the currentStateEditor to know if the components have been enabled/disabled for modification
		 * and thus know if we should allow to create new empty rows
		 */
		if(editable){//TICKET-110
			final int rows = environmentsTable.getRowCount();
			environmentsTable.insertRow(rows);
			final SuggestBox sb = new SuggestBox(environmentSuggestOracle);
			sb.setStyleName("wideSuggest");
			environmentsTable.setWidget(rows, ENVIRONMENT_TYPE_COL, sb);
				
			//to delete this entry
			Button deleteButton = new Button("Delete");
			deleteButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					environmentsTable.removeRow(rows);
					
				}
			});
			environmentsTable.setWidget(rows, ENVIRONMENT_DELETEBUTTON_COL, deleteButton);
	}
	}
	
	/**
	 * Adds a new row to the environment table
	 * @param environment name of the clinical environment
	 */
	private void addNewEnvironmentRow(String environment) {
		final int rows = environmentsTable.getRowCount();
		environmentsTable.insertRow(rows);
		final SuggestBox sb = new SuggestBox(environmentSuggestOracle);
		sb.setText(environment);
		sb.setStyleName("wideSuggest");
		sb.setEnabled(editable);
		environmentsTable.setWidget(rows, ENVIRONMENT_TYPE_COL, sb);
			
		//to delete this entry
		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				environmentsTable.removeRow(rows);
				
			}
		});
		if(editable)
			environmentsTable.setWidget(rows, ENVIRONMENT_DELETEBUTTON_COL, deleteButton);
	}
	
	@UiHandler("currentStateExample")
	void onCSEClick(ClickEvent click) {
//		MyDialog md = new MyDialog("Current State Example", "A 49-year-old woman underwent an uneventful total abdominal hysterectomy and bilateral salpingo-oophorectomy. Postoperatively, the patient complained of severe pain and received intravenous morphine sulfate in small increments. She began receiving a continuous infusion of morphine via a patient controlled analgesia (PCA) pump. A few hours after leaving the PACU [post anesthesia care unit] and arriving on the floor, she was found pale with shallow breathing, a faint pulse, and pinpoint pupils. The nursing staff called a 'code,' and the patient was resuscitated and transferred to the intensive care unit on a respirator [ventilator]. Based on family wishes, life support was withdrawn and the patient died. Review of the case by providers implicated a PCA overdose. Delayed detection of respiratory compromise in PATIENTS undergoing PCA therapy is not uncommon because monitoring of respiratory status has been confounded by excessive nuisance alarm conditions (poor alarm condition specificity).");
		String header = "\"Current State\" describes an adverse event or barrier to provide clinical care or to improving workflow, and the clinical challenges that could be solved with the proposed system."; 
		String example = "Example: A 49-year-old woman underwent an uneventful total abdominal hysterectomy and bilateral salpingo-oophorectomy. Postoperatively, the patient complained of severe pain and received intravenous morphine sulfate in small increments. She began receiving a continuous infusion of morphine via a patient controlled analgesia (PCA) pump. A few hours after leaving the PACU [post anesthesia care unit] and arriving on the floor, she was found pale with shallow breathing, a faint pulse, and pinpoint pupils. The nursing staff called a 'code,' and the patient was resuscitated and transferred to the intensive care unit on a respirator [ventilator]. Based on family wishes, life support was withdrawn and the patient died. Review of the case by providers implicated a PCA overdose. Delayed detection of respiratory compromise in PATIENTS undergoing PCA therapy is not uncommon because monitoring of respiratory status has been confounded by excessive nuisance alarm conditions (poor alarm condition specificity).";
		MyDialog md = new MyDialog(header, example);
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("proposedStateExample")
	void onPSEClick(ClickEvent click) {
		String header = "\"Proposed State\" is a brief description of the improvement in safety and effectivenes obtained by applying the proposed system.";
		String example ="Example: While on the PCA infusion pump, the PATIENT is monitored with a respiration rate monitor and a pulse oximeter. If physiological parameters move outside the pre-determined range, the infusion is stopped and clinical staff is notified to examine the PATIENT and restart the infusion if appropriate. The use of two independent physiological measurements of respiratory function (oxygen saturation and respiratory rate) enables a smart algorithm to optimize sensitivity, thereby enhancing the detection of respiratory compromise while reducing nuisance alarm conditions.";
		MyDialog md = new MyDialog(header, example);
//		MyDialog md = new MyDialog("Proposed State Example", "While on the PCA infusion pump, the PATIENT is monitored with a respiration rate monitor and a pulse oximeter. If physiological parameters move outside the pre-determined range, the infusion is stopped and clinical staff is notified to examine the PATIENT and restart the infusion if appropriate. The use of two independent physiological measurements of respiratory function (oxygen saturation and respiratory rate) enables a smart algorithm to optimize sensitivity, thereby enhancing the detection of respiratory compromise while reducing nuisance alarm conditions.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("clinicalProcessesExample")
	void onCPClick(ClickEvent click) {
		String header = "\"Clinical Processes\" describes how this approach affects the practicce environment, both clinically and from the business/process perspective.";
		String example = "Example: The patient is connected to a PCA infusion pump containing morphine sulfate, a large volume infusion pump acting as a carrier line of saline, a pulse oximeter, a non-invasive blood pressure device, a respiration rate monitor and a distributed alarm system. Heart rate and blood pressure, respiration rate, pain score and sedation score are collected as directed by the clinical process for set-up of a PCA pump. An intravenous (IV) line assessment is also completed. The PCA infusion pump, large volume infusion pump, and pulse oximeter are attached to the integrated system. The system queries the hospital information system for the patient's weight, age, and medication list (specifically, whether the patient is receiving sedatives or non-PCA opioids), and searches for a diagnosis of sleep apnea. The system then accesses the physician's orders from the computerized physician order entry system for dosage and rate for the PCA and large volume infusion pump, and verifies the values programmed into the infusion pump. The patient's SpO2 (arterial oxygen saturation measured by pulse oximetry) and respiration rate are monitored continuously.";
		MyDialog md = new MyDialog(header, example);
//		MyDialog md = new MyDialog("Clinical Processes Example", "The patient is connected to a PCA infusion pump containing morphine sulfate, a large volume infusion pump acting as a carrier line of saline, a pulse oximeter, a non-invasive blood pressure device, a respiration rate monitor and a distributed alarm system. Heart rate and blood pressure, respiration rate, pain score and sedation score are collected as directed by the clinical process for set-up of a PCA pump. An intravenous (IV) line assessment is also completed. The PCA infusion pump, large volume infusion pump, and pulse oximeter are attached to the integrated system. The system queries the hospital information system for the patient's weight, age, and medication list (specifically, whether the patient is receiving sedatives or non-PCA opioids), and searches for a diagnosis of sleep apnea. The system then accesses the physician's orders from the computerized physician order entry system for dosage and rate for the PCA and large volume infusion pump, and verifies the values programmed into the infusion pump. The patient's SpO2 (arterial oxygen saturation measured by pulse oximetry) and respiration rate are monitored continuously.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("algorithmDescriptionExample")
	void onADClick(ClickEvent click) {
		String header = "\"Algorithm Description\" is a brief explanation of the new workflow.";
		String example ="Example: The system uses an algorithm based on weight, age, medication list, diagnoses, SpO2 and respiration rate to determine the state of the patient. Sedation and pain scores also contribute to this algorithm. If the algorithm detects decreases in the patient's SpO2 and/or respiration rate below the calculated or pre-set threshold, a command is sent to stop the PCA pump to prevent further drug overdose, and the system generates a respiratory distress medium priority alarm condition sent via the distributed alarm system. Furthermore, if the algorithm detects that both the SpO2 and respiration rate indicate distress, the system generates an extreme respiratory distress high priority alarm condition sent via the distributed alarm system.";
		MyDialog md = new MyDialog(header, example);
//		MyDialog md = new MyDialog("Algorithm Description Example", "The system uses an algorithm based on weight, age, medication list, diagnoses, SpO2 and respiration rate to determine the state of the patient. Sedation and pain scores also contribute to this algorithm. If the algorithm detects decreases in the patient's SpO2 and/or respiration rate below the calculated or pre-set threshold, a command is sent to stop the PCA pump to prevent further drug overdose, and the system generates a respiratory distress medium priority alarm condition sent via the distributed alarm system. Furthermore, if the algorithm detects that both the SpO2 and respiration rate indicate distress, the system generates an extreme respiratory distress high priority alarm condition sent via the distributed alarm system.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	@UiHandler("benefitsExample")
	void onBClick(ClickEvent click) {
		String header = "\"Benefits\" describes obstacles to efficiency, teamwork or safety that could be aliminated with the proposed system.";
		String example ="Example: Add error resistance to the x-ray procedure by eliminating the dependence on the operator (e.g. anesthesia provider) to remember to turn the ventilator back on. Shorten or eliminate the period of apnea, thereby reducing potentially adverse responses to apnea; and Provide the ability to synchronize x-ray exposure with inspiratory hold, without requiring anyone to be present in the x-ray exposure area to manually generate sustained inspiration.";
		MyDialog md = new MyDialog(header, example);
//		MyDialog md = new MyDialog("Benefits Example", "Add error resistance to the x-ray procedure by eliminating the dependence on the operator (e.g. anesthesia provider) to remember to turn the ventilator back on. Shorten or eliminate the period of apnea, thereby reducing potentially adverse responses to apnea; and Provide the ability to synchronize x-ray exposure with inspiratory hold, without requiring anyone to be present in the x-ray exposure area to manually generate sustained inspiration");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("risksExample")
	void onRClick(ClickEvent click) {
		String header = "\"Risks\" is a description of new risks that could be introduced with the proposed and a how they could be mitigated.";
		String example ="Example: A synchronization error could lead to x-ray exposure at an incorrect phase of respiration.";
		MyDialog md = new MyDialog(header, example);
//		MyDialog md = new MyDialog("Risks Example", "A synchronization error could lead to x-ray exposure at an incorrect phase of respiration.");
//		md.setPopupPosition(click.getClientX(), click.getClientY());
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("clinicanExample")
	void onClinicianExampleClick(ClickEvent click) {
		String header = "Include a \"Clinician\" for each role or actor present in the scenario .";
		String example ="Example: Anesthesioligist, Oncologist or Surgeon.";
		MyDialog md = new MyDialog(header, example);;
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
	@UiHandler("environmentExample")
	void onEnvironmentExampleClick(ClickEvent click) {
		String header = "\"Environments\" is the environment of use: home , transport or hospital (with specific clinical areas).";
		String example ="Example: 	ICU-intensive care unit, OR-operating room, Patients bay, Nurse station, Transport or even Cafeteria.";
		MyDialog md = new MyDialog(header, example);;
		md.setAutoHideEnabled(true);
		md.showRelativeTo(titleEditor);
	}
	
//	@UiField
//	FlexTable testCasesTable;
	
	@UiField
	TextBox titleEditor;
	
	@UiField
	@Ignore
	Label uniqueId;
	
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
	Button submitButton;//submit Scn for approval
	
	@UiField
	Button saveButton; //persist the Scn info
	
	@UiField
	Button lockButton; //lock/unlock for editing
	
	@UiField
	Button ackButton; //TICKET-163; "Like" and acknowledge my scenario
	
	@UiField
	@Ignore
	Label ackLabel;//TICKET-163;
	
//	@UiField
//	Button exportScenario; //export the Scn info to file
//	Button b = new Button("Download", new ClickListener() { 
//
//        public void onClick(Widget sender) { 
//                Window.open(GWT.getModuleBaseURL() +"/downloadServlet?fileId=123", 
//"123", ""); 
//        } 
//
//}); 
	
	@UiHandler("lockButton")
	public void onClickLock(ClickEvent clickEvent) {
		ScenarioRequest scnReq = (ScenarioRequest) driver.flush();
		currentScenario.setLastActionUser(userEmail);
		if(!editable){//unlock
			currentScenario.setLockOwner(userEmail);
			currentScenario.setLastActionTaken(SCN_LAST_ACTION_UNLOCKED);
			
			if(currentScenario.getStatus().equals(SCN_STATUS_SUBMITTED))
				currentScenario.setStatus(SCN_STATUS_UNLOCKED_PRE);
			if(currentScenario.getStatus().equals(SCN_STATUS_APPROVED))
				currentScenario.setStatus(SCN_STATUS_UNLOCKED_POST);
			if(currentScenario.getStatus().equals(SCN_STATUS_MODIFIED))
				currentScenario.setStatus(SCN_STATUS_UNLOCKED_POST);	
			editable = true;
		}else{//lock
			currentScenario.setLockOwner(null);
			currentScenario.setLastActionTaken(SCN_LAST_ACTION_LOCKED);
			if(currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_PRE))
				currentScenario.setStatus(SCN_STATUS_SUBMITTED);
			if(currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST))
				currentScenario.setStatus(SCN_STATUS_MODIFIED);
			editable = false;
		}
		scnReq.persist()
		.using(currentScenario)
		.with(driver.getPaths())
		.with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
		.fire(singleScenarioReceiver);
		
	}
	
	@UiHandler("submitButton")
	public void onClickSubmit(ClickEvent clickEvent) {
		//TICKET-106 
		// Either call save, or check the list-type components
		// option 3 we do like 'Approve' or 'Reject' an also send a email message
		// scnReq.persistWithNotification
		
		if(isEmptyScenario()){
			Window.alert("You can't submit an empty scenario for revision.");
			return;
		}
		//TICKET-115
		if(titleEditor.getText().trim().equals("") ||
				currentStateEditor.getText().trim().equals("") ||
				proposedStateEditor.getText().trim().equals("")){
			String t = "The sceanrio \"Title\", \"Current State\" and \"Proposed State\" (see the \"Sceanrio Description\" tab) are MANDATORY fields." +
					" \nPlease complete this information";
			Window.alert(t);
			return;
		}
	
//		if(!currentScenario.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED)){
		if(!isEmptyScenario() && currentScenario.getStatus().equals(ScenarioPanel.SCN_STATUS_UNSUBMITTED)){
			final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();

			boolean confirm = Window.confirm("Are you sure you want to SUBMIT this scenario?");
			if(confirm){
				
				currentScenario.setStatus(SCN_STATUS_SUBMITTED);
				currentScenario.setLastActionTaken(SCN_LAST_ACTION_SUBMITTED);
				currentScenario.setLastActionUser(userEmail);
				checkScenarioFields(scnReq);
				scnReq.persist().using(currentScenario)
				.with(driver.getPaths()).with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
				.fire(new Receiver<ScenarioProxy>() {
	
					@Override
					public void onSuccess(ScenarioProxy response) {
						Window.alert("This Clinical Scenario has been submitted for approval");	
						setCurrentScenario(response);
					}
					
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
						//log error
					}
				});
				configureComponents();
			}
		}

	}
	
	@UiHandler("saveButton")
	public void onClickSave(ClickEvent clickEvent) {
		save();//persist our entities
	}
	
//	@UiHandler("exportScenario")
//	public void onClickExport(ClickEvent clickEvent) {
//		//generate file and
//		// Window.open(GWT.getHostPageBaseURL() + "FileRepository/doDownload?docId=" + dokument.getId(), "", "");
////		Window.open(url, name, features)
//		Window.open("./mdpnp.jpg", "_blank", ""); 
//	}
	

	@UiField
	Button approveScnButton; 	//button to approve the scn
	
	@UiHandler("approveScnButton")
	public void onClickApproveScn(ClickEvent clickEvent) {
		final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();

		boolean confirm = Window.confirm("Are you sure you want to APPROVE this scenario?");
		if(confirm){
			String subject ="Your scenario \""+currentScenario.getTitle()+"\" has been approved";
			String message = "Your scenario \""+currentScenario.getTitle()+"\" has been approved by the MD PnP Clinical Scenario Repository Administrators.\n"
			+"Thank you for your submission.";
			message += "\n\n"+feedback.getText();
			message += "\n The MD PnP Team \n www.mdpnp.org";
			
			boolean sendEmail = false; //FIXME Mail servce as its own entity w/ services
			
			if(currentScenario.getStatus().equals(SCN_STATUS_UNLOCKED_POST)){
				currentScenario.setLastActionTaken(SCN_LAST_ACTION_REAPPROVED);
			}else{
				currentScenario.setLastActionTaken(SCN_LAST_ACTION_APPROVED);
				sendEmail = true;
			}			
			currentScenario.setStatus(SCN_STATUS_APPROVED);//update entity information
			currentScenario.setLockOwner(null);
			currentScenario.setLastActionUser(userEmail);

			checkScenarioFields(scnReq);//not really that necessary, because it has been updated when clicking the FeedBack tab
			
			if(sendEmail){
				scnReq.persistWithNotification(currentScenario.getSubmitter(), subject, message)
				.using(currentScenario)
				.with(driver.getPaths()).with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
				.fire(new Receiver<ScenarioProxy>() {
		
					@Override
					public void onSuccess(ScenarioProxy response) {
						Window.alert("This Clinical Scenario has been approved");	
						setCurrentScenario(response);
					}
					
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
					}
				});
			}else{
				scnReq.persist()
				.using(currentScenario)
				.with(driver.getPaths()).with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
				.fire(new Receiver<ScenarioProxy>() {
		
					@Override
					public void onSuccess(ScenarioProxy response) {
						Window.alert("This Clinical Scenario has been approved");	
						setCurrentScenario(response);
					}
					
					public void onFailure(ServerFailure error) {
						super.onFailure(error);
					}
				});
			}
		}
	}
	
	
	@UiField
	Button returnScnButton; //Button to return the Scn for clarification
	
	@UiHandler("returnScnButton")
	public void onClickReturnScn(ClickEvent clickEvent) {
		final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();

		boolean confirm = Window.confirm("Are you sure you want to RETURN this scenario?");
		if(confirm){
			String subject ="Requested clarification for your scenario \""+currentScenario.getTitle()+"\".";
			String message = "The MD PnP Clinical Scenario Repository Administrators " +
					"have requested further clarification for your scenario "+currentScenario.getTitle()
					+".\n Please see the comments below \n";
			message += "\n \n"+feedback.getText();
			message += "\n The MD PnP Team \n www.mdpnp.org";
			
			currentScenario.setStatus(SCN_STATUS_UNSUBMITTED);//update entity information
			currentScenario.setLockOwner(null);
			currentScenario.setLastActionTaken(SCN_LAST_ACTION_RETURNED);
			currentScenario.setLastActionUser(userEmail);
			checkScenarioFields(scnReq);//not really that necessary, because it would be updated when clicking the FeedBack tab
			
			scnReq.persistWithNotification(currentScenario.getSubmitter(), subject, message)
			.using(currentScenario)
			.with(driver.getPaths())
			.with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
			.fire(new Receiver<ScenarioProxy>() {
	
				@Override
				public void onSuccess(ScenarioProxy response) {
					Window.alert("This Clinical Scenario has been returned for clarification");	
					setCurrentScenario(response);
				}
				
				public void onFailure(ServerFailure error) {
					super.onFailure(error);
				}
				
			});
			
//			configureComponents();
		}
	}
	
	@UiField
	Button rejectScnButton; //Button to reject the Scn
	
	@UiHandler("rejectScnButton")
	public void onClickRejectScn(ClickEvent clickEvent) {
		final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();

		boolean confirm = Window.confirm("Are you sure you want to REJECT this scenario?");
		if(confirm){
			
			currentScenario.setStatus(SCN_STATUS_REJECTED);//update entity information
			currentScenario.setLockOwner(null);
			currentScenario.setLastActionTaken(SCN_LAST_ACTION_REJECTED);
			currentScenario.setLastActionUser(userEmail);
			checkScenarioFields(scnReq);//not really that necessary, because it would be updated when clicking the FeedBack tab
			
			scnReq.persist()
			.using(currentScenario)
			.with(driver.getPaths())
			.with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
			.fire(new Receiver<ScenarioProxy>() {
	
				@Override
				public void onSuccess(ScenarioProxy response) {
					Window.alert("This Clinical Scenario has been rejected");	
					setCurrentScenario(response);
				}
				
				public void onFailure(ServerFailure error) {
					super.onFailure(error);
					currentScenario.setStatus(SCN_STATUS_SUBMITTED);//update entity information
					//setCurrent?
				}
				
			});
			
		}
	}
	
	/**
	 * Returns true if the current scenario has no meaningful information <p>
	 * 1- no title <p>
	 * 2- no background <p>
	 * 3- no/empty hazards list <p>
	 * 4- no/empty environment list  <p>
	 * 5- no/empty equipment list <p>
	 * 6- no proposed solution <p>
	 * 7- no benefits and risks  <p>
	 * 
	 * @return true/false
	 */
	private boolean isEmptyScenario(){
		
		/*
		 * TODO Maybe all this CHAOS and coded could be spared if we had a flag Empty, 
		 * that is true for "new born" scenarios and false for any other recovered from a query 
		 * (if its in the GAE, must have been persisted because it was not empty.
		 * --> the constructor and setCurrentScenario would modifiy the flag
		 * 
		 * This flag would be checked on this method, or its used sustituded wherever we are using the function
		 * isEmptyScenario().
		 * 
		 * To update the value of the flag (from true to false) we could use the event handlers that detect
		 * we changed the textboxes.
		 * 
		 * It could be tricky, since I could edit the title (it would be not empty) and then delete, leaving
		 * the scenario information empty again.
		 */
		
		final int FIRST_ROW_HAZARDS_EQUIPMENT = 1;
		final int FIRST_ROW_CLINICIANS_ENV = 0;
		
		//Text Areas
		if(!titleEditor.getText().trim().equals("")) return false;
		if(!currentStateEditor.getText().trim().equals("") ||
			!proposedStateEditor.getText().trim().equals("")) return false;
		if(!benefits.getText().trim().equals("") || 
				!risks.getText().trim().equals("")) return false;
		if(!clinicalProcesses.getText().trim().equals("") ||
				!algorithmDescription.getText().trim().equals("")) return false;
		//Flextables
		if(hazardsTable.getRowCount()>FIRST_ROW_HAZARDS_EQUIPMENT+1) return false;
		if(equipmentTable.getRowCount()>FIRST_ROW_HAZARDS_EQUIPMENT+1) return false;
		if(cliniciansTable.getRowCount()>FIRST_ROW_CLINICIANS_ENV+1) return false;
		if(environmentsTable.getRowCount()>FIRST_ROW_CLINICIANS_ENV+1) return false;
		
		/**
		 * We could still have the problem of persisting Scn that have multiple empty hazards/equipment rows
		 */
		
		boolean isEmpty = true;
		String text = null;
		
		//find at least a non-empty element
		//Hazards
		for(int j=0; j<HAZARDS_EXPECTED_COL; j++){
			Widget widget = hazardsTable.getWidget(FIRST_ROW_HAZARDS_EQUIPMENT, j);
			text = ((TextArea)widget).getText().trim();
			if(!text.equals(""))
				isEmpty=false;
		}
		if(!isEmpty) return false;

		//equipment TODO update for ticket-140
		for(int j=0; j<EQUIPMENT_EXPLANATION_COL; j++){
			Widget widget = equipmentTable.getWidget(FIRST_ROW_HAZARDS_EQUIPMENT, j);
			text = ((TextBox)widget).getText().trim();
			if(!text.equals(""))
				isEmpty=false;
		}
		if(!isEmpty) return false;
		
		Widget wExplain = equipmentTable.getWidget(FIRST_ROW_HAZARDS_EQUIPMENT, EQUIPMENT_EXPLANATION_COL);
		text = ((TextArea)wExplain).getText().trim();
		if(!text.equals("")) return false;
		
		//clinicians
		Widget widget = cliniciansTable.getWidget(FIRST_ROW_CLINICIANS_ENV, 0);
		text = ((SuggestBox)widget).getText().trim();
		if(!text.equals(""))
			return false;
		
		//environments
		widget = environmentsTable.getWidget(FIRST_ROW_CLINICIANS_ENV, 0);
		text = ((SuggestBox)widget).getText().trim();
		if(!text.equals(""))
			return false;

		//if not, means the scenario has no meaningful info
		return true;
	}
	
	@UiHandler("ackButton")
	public void onClickAcknowledge(ClickEvent clickEvent) {
		final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();
		
		boolean confirm = Window.confirm("Click Ok to acknowledge the content of this scenario.");
		if(confirm){
			Set<String> ackedgers = currentScenario.getAcknowledgers().getAcknowledgersIDs()==null? new HashSet<String>() : currentScenario.getAcknowledgers().getAcknowledgersIDs();
			ackedgers.add(userId);
			currentScenario.getAcknowledgers().setAcknowledgersIDs(ackedgers);
			currentScenario.setLastActionTaken(SCN_LAST_ACTION_ACK);
			currentScenario.setLastActionUser(userEmail);			
			checkScenarioFields(scnReq);//not really that necessary, because it would be updated when clicking the FeedBack tab
			
			scnReq.persist().using(currentScenario)
			.with(driver.getPaths())
			.with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
			.fire(singleScenarioReceiver);
//			scnReq.persist()
//			.using(currentScenario).with(driver.getPaths())
//			.fire(new Receiver<ScenarioProxy>() {
//	
//				@Override
//				public void onSuccess(ScenarioProxy response) {
//					setCurrentScenario(response);
//				}
//				
//				public void onFailure(ServerFailure error) {
//					super.onFailure(error);
//					//undo
//					Set<String> ackedgers = currentScenario.getAcknowledgers().getAcknowledgersIDs();
//					ackedgers.remove(userId);
//					currentScenario.getAcknowledgers().setAcknowledgersIDs(ackedgers);
//				}
//				
//			});
			
		}
	}
	
	@UiHandler("saveTagsButton")
	public void onClickSaveTagsButton(ClickEvent clickEvent) {
		final ScenarioRequest scnReq = (ScenarioRequest) driver.flush();
		Set<String> associatedTags = new HashSet<String>() ;
		for(int i=0;i<tagsAssociatedTable.getRowCount();i++){
			for(int j=0;j<(TAGS_TAB_NUM_COL*2);j+=2){
				//even numbers are checkboxes, odd are labels
				if(tagsAssociatedTable.isCellPresent(i, j)){//make sure the column exists
					Widget wCheck = tagsAssociatedTable.getWidget(i, j);
					Widget wLabel = tagsAssociatedTable.getWidget(i, j+1);
					if(wCheck instanceof CheckBox && ((CheckBox) wCheck).getValue()){
						if(wLabel instanceof Label){
							associatedTags.add(((Label) wLabel).getText().toLowerCase());
						}
					}
				}

			}			
		}
		
		currentScenario.getAssociatedTags().setAssociatedTagNames(associatedTags);
		currentScenario.setLastActionTaken(SCN_LAST_ACTION_TAG);
		currentScenario.setLastActionUser(userEmail);			
//		checkScenarioFields(scnReq);//not really that necessary, because it would be updated when clicking the FeedBack tab
			
		scnReq.persist()
		.using(currentScenario)
		.with(driver.getPaths())
		.with("equipment", "hazards", "environments", "references", "acknowledgers", "associatedTags")
		.fire(new Receiver<ScenarioProxy>() {
			@Override
			public void onSuccess(ScenarioProxy response) {
				Window.alert("The tags have been associated to the scenario");
				setCurrentScenario(response);
			}
			
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
			}
			
		});
	}
	

	
}
