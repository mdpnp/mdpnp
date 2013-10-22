package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioAcksComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioStatusComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioSubmitterComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioTitleComparator;
import org.mdpnp.clinicalscenarios.client.tag.TagProxy;
import org.mdpnp.clinicalscenarios.client.tag.TagRequest;
import org.mdpnp.clinicalscenarios.client.tag.TagRequestFactory;
import org.mdpnp.clinicalscenarios.client.tag.comparator.TagComparator;
import org.mdpnp.clinicalscenarios.client.user.UserInfoProxy;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequest;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequestFactory;

import com.google.gwt.core.client.GWT;
//import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DefaultDateTimeFormatInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class ScenarioSearchPanel extends Composite {
	
	//scenario table columns
	private static int SCN_TABLE_FIRST_COL = 0;
	private static int SCN_TABLE_SECOND_COL = 1;
	private static int SCN_TABLE_THIRD_COL = 2;
	private static int SCN_TABLE_FOURTH_COL = 3;
	private static int SCN_TABLE_FIFTH_COL = 4;	
	private static int SCN_TABLE_SIXTH_COL = 5;
	private static int SCN_TABLE_SEVENTH_COL = 6;
	private static int SCN_TABLE_EIGTH_COL = 7;
	private static int SCN_TABLE_NINTH_COL = 8;
	
	private static int TAGS_SEARCH_NUM_COLS = 8;
	
	private final int SCN_GRIDLIST_ROWS = 10; //rows in the table showing the Scn List
	private final int SCN_GRIDLIST_COLUMNS_admin = 9;//title, uniqueID, #acks, status, submitter, created, lastAction, lockebBy, deleteButton
	private final int SCN_GRIDLIST_COLUMNS_RegUser = 6;//title, uniqueID, #acks, status, creationDate, lastAction
	private final int SCN_GRIDLIST_COLUMNS_UnregUser = 3;//title, uniqueID, #acks
	
	private ScenarioTitleComparator scnTitleComparator = new ScenarioTitleComparator();
	private ScenarioSubmitterComparator scnSubmitterComparator = new ScenarioSubmitterComparator();
	private ScenarioStatusComparator scnStatusComparator = new ScenarioStatusComparator();
	private ScenarioComparator scnComparator = new ScenarioComparator(ScenarioComparator.PROPERTY_TITLE);
	private ScenarioAcksComparator scnAcksComparator = new ScenarioAcksComparator();
	
	//TODO add style names as constants too
	private final static String STYLE_SELECTEDROW = "selectedRow";
	private final static String STYLE_CLICKABLE = "clickable";
	private final static String STYLE_SUBMITTEDSCN =  "submittedScn";
	private final static String STYLE_UNSUBMITTEDSCN =  "unsubmittedScn";
	private final static String STYLE_REJECTEDSCN =  "rejectedScn";
	
	private static final String STYLE_TABLEROWOTHER = "tableRowOther";
	private static final String STYLE_USERLISTHEADER = "userListHeader";
	
	
	private static ScenarioSearchPanelUiBinder uiBinder = GWT.create(ScenarioSearchPanelUiBinder.class);
	private TagRequestFactory tagRequestFactory = GWT.create(TagRequestFactory.class);//TICKET-157
	
	private UserInfoRequestFactory userInfoRequestFactory = GWT.create(UserInfoRequestFactory.class);
	private enum UserRole {Administrator, RegisteredUser, AnonymousUser}
	private UserRole userRole;
	private String submitterName;//email of the current user
	
	private DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
	private DateTimeFormat dtf = new DateTimeFormat("MM/dd/yyyy", info) {}; 

	interface ScenarioSearchPanelUiBinder extends
			UiBinder<Widget, ScenarioSearchPanel> {
	}
	private ScenarioRequestFactory scenarioRequestFactory;
	public ScenarioSearchPanel(ScenarioRequestFactory scenarioRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.scenarioRequestFactory = scenarioRequestFactory;
		
		advancedSearchDateBoxFrom.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("MMMM dd, yyyy")));
		advancedSearchDateBoxUntil.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("MMMM dd, yyyy")));
		advancedSearchHazardSeverityListbox.clear();
		advancedSearchHazardSeverityListbox.addItem(" ");// to ad no-filter option
		for(String s : ScenarioPanel.getHazardSeverityValues()){
			advancedSearchHazardSeverityListbox.addItem(s);
		}
		
		//Ticket-157
		if(tagRequestFactory != null){
			final EventBus eventBus = new SimpleEventBus();
			tagRequestFactory.initialize(eventBus);
		}
		
		//check user role
		if(userInfoRequestFactory != null){
			final EventBus eventBus = new SimpleEventBus();
			userInfoRequestFactory.initialize(eventBus);
		
		UserInfoRequest userInfoRequest = userInfoRequestFactory.userInfoRequest();
		userInfoRequest.findCurrentUserInfo(Window.Location.getHref(), false).with("loginURL").to(new Receiver<UserInfoProxy>() {
			@Override
			public void onSuccess(UserInfoProxy response) {
				if(response.getEmail()==null ||response.getEmail().trim().equals("") ){
					//Anonymous user
					userRole = UserRole.AnonymousUser;//can't modify the Scn
					
				}else{
					submitterName = response.getEmail();
					if(response.getAdmin()) 
						userRole = UserRole.Administrator;
					else
						userRole = UserRole.RegisteredUser;
				}
			}
			
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
			}

	
		}).fire();}
	}
	@UiField
	TextBox searchQuery;
	
	@UiField
	FlexTable searchResult;
	
	@UiField
	Grid searchResult2;
	
 	
	@UiField
	Label header;
	
//	@UiField
//	Label pleaseEnterKeywords;
	
	@UiField
	Button submitButton;
	
	@UiField
	Button createNew;
	
	@UiField
	Label pleaseSignIn;

	public Label getHeader() {
		return header;
	}
	public Button getSubmitButton() {
		return submitButton;
	}
	
	public interface SearchHandler {
		void onSearchResult(ScenarioProxy sp);
	}
	
	private SearchHandler searchHandler;
	
	public void setSearchHandler(SearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}
	public TextBox getSearchQuery() {
		return searchQuery;
	}
	
//	public Label getPleaseEnterKeywords(){
//		return pleaseEnterKeywords;
//	}
	
	public Button getCreateNewScnButton(){
		return createNew;
	}
	
	public void setUserInfo(UserInfoProxy ui) {
		if(null == ui || null == ui.getEmail()) {
			pleaseSignIn.setVisible(true);
			createNew.setVisible(false);
		} else {
			pleaseSignIn.setVisible(false);
			createNew.setVisible(true);
		}
	}
	
	/**
	 * Advanced Search; Gets the list of approved scn with the keywords and then filters using the other fields 
	 */
//	public void doAdvancedSearch(){
//		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
//		scenarioRequest.searchByKeywords(advancedSearchKeywordsTextBox.getText())
//			.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references")
//			.to(new Receiver<List<ScenarioProxy>> () {
//
//				@Override
//				public void onSuccess(List<ScenarioProxy> response) {
//					if(null==response || response.size()==0){ 
//						status.setVisible(false); 
//						return;
//					}
//					
//					List<ScenarioProxy> filteredResult= new ArrayList<ScenarioProxy>();
//					HashSet<ScenarioProxy> nScenarios = new HashSet<ScenarioProxy>();
//					//we filter the results returned
////					/**
////					 * Unregistered user: can see scn only is it is APPROVED
////					 * Registered user: can see Scn if is APPROVED or if they own it
////					 * Administrators: can see the scn w/out restriction
////					 */
////					if(userRole==UserRole.Administrator)
////						response.add(result);
////					else if (userRole==UserRole.RegisteredUser){
////						if(result.getSubmitter().equals(submitterName))
////							response.add(result);
////					}else if (result.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED))
////						response.add(result);
//					Date dateFrom = advancedSearchDateBoxFrom.getValue();
//					Date dateUntil = advancedSearchDateBoxUntil.getValue();
//					String hazardSeverity = advancedSearchHazardSeverityListbox.getValue(advancedSearchHazardSeverityListbox.getSelectedIndex());
//					String clinicianInvolved = advancedSearchCliniciansTextBox.getText();
//					String environmentInvolved = advancedSearchEnvironmentsTextBox.getText();
//					String deviceType = advancedSearchEquipmentTypeTextBox.getText();
//					String deviceManufacturer = advancedSearchEquipmentManufacturerTextBox.getText();
//					for(ScenarioProxy scn : response){
//						//1- filter creation date range
//						if(null != dateFrom && null != dateUntil){
//							if(scn.getCreationDate().after(dateFrom) && scn.getCreationDate().before(dateUntil))
//								nScenarios.add(scn);
//						}else if(null != dateFrom && scn.getCreationDate().after(dateFrom)){
//							nScenarios.add(scn);
//						}else if(null != dateUntil && scn.getCreationDate().before(dateUntil)){
//							nScenarios.add(scn);
//						}
//						//2-filter hazard severity. The scenario has a List of hazards
//						List<HazardsEntryProxy> hazardsList = scn.getHazards().getEntries();
//						if(hazardSeverity.trim()!="" && null != hazardsList && hazardsList.size()>0){
//							for(int i=0;i<hazardsList.size();i++){
//								HazardsEntryProxy hep = hazardsList.get(i);
//								if(hep.getSeverity().trim().equalsIgnoreCase(hazardSeverity)){
//									nScenarios.add(scn); break;
//								}
//							}
//						}
//						//3- filter clinicians involved
//						List<String> clinicians = scn.getEnvironments().getCliniciansInvolved();
//						if(!clinicianInvolved.trim().equals("") && clinicianInvolved.trim()!= null && null!=clinicians && clinicians.size()>0){
//							for(int i=0;i<clinicians.size();i++){
//								if(clinicians.get(i).trim().equalsIgnoreCase(clinicianInvolved)){
//									nScenarios.add(scn); break;
//								}
//							}
//						}
//						//4- filter environments
//						List<String> env = scn.getEnvironments().getClinicalEnvironments();
//						if(!environmentInvolved.trim().equals("")  && clinicianInvolved.trim()!= null && null!=env && env.size()>0){
//							for(int i=0;i<env.size();i++){
//								if(env.get(i).trim().equalsIgnoreCase(environmentInvolved)){
//									nScenarios.add(scn); break;
//								}
//							}
//						}
//						//5- filter device type and device manufacturer
//						List<EquipmentEntryProxy> devices = scn.getEquipment().getEntries();
//						if(devices!=null && devices.size()>0){
//							for(int i=0;i<devices.size();i++){
//								EquipmentEntryProxy eep = devices.get(i);
//								if(!deviceType.trim().equals("")  && eep.getDeviceType()!=null && deviceType.trim().equalsIgnoreCase(eep.getDeviceType().trim()) ){
//									nScenarios.add(scn); break;
//								}
//								if(!deviceManufacturer.equals("")  && eep.getManufacturer()!=null && deviceManufacturer.trim().equalsIgnoreCase(eep.getManufacturer().trim())){
//									nScenarios.add(scn); break;
//								}
//							}
//							
//						}
//					}
//					
//					//convert set into list
//					java.util.Iterator<ScenarioProxy> it = nScenarios.iterator();
//					while(it.hasNext()){
//						filteredResult.add(it.next());
//					}
//					
//					resetGridAuxVar(filteredResult);
//					drawScenariosListGrid(filteredResult);
//					
//				}
//				@Override
//				public void onFailure(ServerFailure error) {
//					super.onFailure(error);
//				}
//			}).fire();
//	}
	
	/**
	 * Advanced Search; Gets the list of approved scn with the keywords and then filters using the other fields 
	 */
	private void doAdvancedSearch(){
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchByKeywords(advancedSearchKeywordsTextBox.getText())
			.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
			.to(new Receiver<List<ScenarioProxy>> () {

				@Override
				public void onSuccess(List<ScenarioProxy> response) {
					if(null==response || response.size()==0){ 
						status.setVisible(false); 
						resetGridAuxVar(new ArrayList<ScenarioProxy>());
						drawScenariosListGrid(new ArrayList<ScenarioProxy>());
						return;
					}
					
					List<ScenarioProxy> filteredResult= new ArrayList<ScenarioProxy>();
					HashSet<ScenarioProxy> nScenarios = new HashSet<ScenarioProxy>(response);
					//we filter the results returned
//					/**
//					 * Unregistered user: can see scn only is it is APPROVED
//					 * Registered user: can see Scn if is APPROVED or if they own it
//					 * Administrators: can see the scn w/out restriction
//					 */
//					if(userRole==UserRole.Administrator)
//						response.add(result);
//					else if (userRole==UserRole.RegisteredUser){
//						if(result.getSubmitter().equals(submitterName))
//							response.add(result);
//					}else if (result.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED))
//						response.add(result);
					Date dateFrom = advancedSearchDateBoxFrom.getValue();
					Date dateUntil = advancedSearchDateBoxUntil.getValue();
					String hazardSeverity = advancedSearchHazardSeverityListbox.getValue(advancedSearchHazardSeverityListbox.getSelectedIndex());
					String clinicianInvolved = advancedSearchCliniciansTextBox.getText();
					String environmentInvolved = advancedSearchEnvironmentsTextBox.getText();
					String deviceType = advancedSearchEquipmentTypeTextBox.getText();
					String deviceManufacturer = advancedSearchEquipmentManufacturerTextBox.getText();
					boolean found = false;
					for(ScenarioProxy scn : response){
						//1- filter creation date range
						if(null != dateFrom && null != dateUntil){
							if(scn.getCreationDate().before(dateFrom) || scn.getCreationDate().after(dateUntil))
								nScenarios.remove(scn);
						}else if(null != dateFrom && scn.getCreationDate().before(dateFrom)){
							nScenarios.remove(scn); 
						}else if(null != dateUntil && scn.getCreationDate().after(dateUntil)){
							nScenarios.remove(scn);
						}
						//2-filter hazard severity. The scenario has a List of hazards
						List<HazardsEntryProxy> hazardsList = scn.getHazards().getEntries();
						if(!hazardSeverity.trim().equals("")){
							if(null != hazardsList && hazardsList.size()>0){
								found = false;
								for(int i=0;i<hazardsList.size();i++){
									HazardsEntryProxy hep = hazardsList.get(i);
									if(hep.getSeverity().trim().equalsIgnoreCase(hazardSeverity)){
										found=true; break;
									}
								}
								if(!found){
									nScenarios.remove(scn);
								}
							}else{
								nScenarios.remove(scn);//there's a filter but no list of hazards
							}
						}
						//3- filter clinicians involved
						List<String> clinicians = scn.getEnvironments().getCliniciansInvolved();
						if(!clinicianInvolved.trim().equals("")){
							if(clinicianInvolved.trim()!= null && null!=clinicians && clinicians.size()>0){
								found = false;
								for(int i=0;i<clinicians.size();i++){
									if(clinicians.get(i).trim().equalsIgnoreCase(clinicianInvolved)){
										found =true; break;
									}
								}
								if(!found){
									nScenarios.remove(scn);
								}
							}else{
								nScenarios.remove(scn);//there's a filter but no list of clinicians
							}
						}
							
						//4- filter environments
						List<String> env = scn.getEnvironments().getClinicalEnvironments();
						if(!environmentInvolved.trim().equals("")){
							if(clinicianInvolved.trim()!= null && null!=env && env.size()>0){
								found = false;
								for(int i=0;i<env.size();i++){
									if(env.get(i).trim().equalsIgnoreCase(environmentInvolved)){
										found = true; break;
									}
								}
								if(!found){
									nScenarios.remove(scn); 
								}
							}else{
								nScenarios.remove(scn);//there's a filter but no list of environments
							}
						}
						//5- filter device type and device manufacturer
						List<EquipmentEntryProxy> devices = scn.getEquipment().getEntries();
						if(devices!=null && devices.size()>0){
							found = false;
							for(int i=0;i<devices.size();i++){
								EquipmentEntryProxy eep = devices.get(i);
								if(!deviceType.trim().equals("")  && eep.getDeviceType()!=null && deviceType.trim().equalsIgnoreCase(eep.getDeviceType().trim()) ){
									found = true; break;
								}
								if(!deviceManufacturer.equals("")  && eep.getManufacturer()!=null && deviceManufacturer.trim().equalsIgnoreCase(eep.getManufacturer().trim())){
									found = true; break;
								}
							}
							if((!deviceType.trim().equals("") || !deviceManufacturer.equals(""))  && !found){
								nScenarios.remove(scn); 
							}
							
						}else{
							if(!deviceType.trim().equals("")  || !deviceManufacturer.equals("")){
								nScenarios.remove(scn); //filters for devices, but no list of equipment
							}
						}
					}
					
					//convert set into list
					java.util.Iterator<ScenarioProxy> it = nScenarios.iterator();
					while(it.hasNext()){
						filteredResult.add(it.next());
					}
					
					Window.alert(" "+filteredResult.size());
					resetGridAuxVar(filteredResult);
					drawScenariosListGrid(filteredResult);
					
				}
				@Override
				public void onFailure(ServerFailure error) {
					super.onFailure(error);
				}
			}).fire();
	}

	@UiField
	Label status; //status: "Loading..."
	
	@UiField
	Label searchResultCaption; //"results from search: blablabla"
	
	/**
	 * Hides navigation buttons and appropriate labels and clears the results table.
	 */
	private void cleanScenarioTable(){
		hideNavigationButtons();
		searchResult2.clear();
		status.setVisible(true);
		
	}
	
	/**
	 * Basic search or listAll
	 * @param text
	 */
	private void doSearch(final String text) {
//		hideAllSearchPanels();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchByKeywords(text)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(listScnReceiver).fire();
	}	
	
	public void findAllScn(){
//		hideNavigationButtons();
		hideAllSearchPanels();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.findAllScenarios()
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(listScnReceiver).fire();
	}


	/**
	 * List scn by submitter
	 * @param status
	 */
	public void listScnBySubmitter(final String submitter) {
		hideAllSearchPanels();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchScnBySubmitter(submitter)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(listScnReceiver).fire();
	}
	
	/**
	 * list scn by status
	 * @param status
	 */
	public void listScnByStatus(final String status){
		hideAllSearchPanels();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();		
		scenarioRequest.searchByStatus(status)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(listScnReceiver).fire();		
	}
	
	/**
	 * list scn by status
	 * @param status
	 */
	public void listScnByStatus(final Set<String> status){
		hideAllSearchPanels();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();		
		scenarioRequest.searchByStatus(status)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(listScnReceiver).fire();		
	}
	
	//----------------------------------------------------------------------------------------
	//RECEIVERS for REQUESTCONTEXT
	/**
	 * Receiver for any search of list of SCN
	 */
	Receiver<List<ScenarioProxy>> listScnReceiver = new Receiver<List<ScenarioProxy>>() {

		@Override
		public void onSuccess(List<ScenarioProxy> response) {
			//drawScenariosListTable(response);//DAG Older way to print the table using FleaxTable and not limiting results shown
			if(null!=response){
				resetGridAuxVar(response);
				drawScenariosListGrid(response);
			}else{
				resetGridAuxVar(new ArrayList<ScenarioProxy>());
				drawScenariosListGrid(new ArrayList<ScenarioProxy>());
			}

			
		}
		@Override
		public void onFailure(ServerFailure error) {
			super.onFailure(error);
		}
	};
	
	/**
	 * Receiver for any search of SINGLE Scenario
	 */
	Receiver<ScenarioProxy> scnReceiver = new Receiver<ScenarioProxy>() {

		@Override
		public void onSuccess(ScenarioProxy result) {
			List<ScenarioProxy> response = new ArrayList<ScenarioProxy>();
			if(null!=result)
				response.add(result);
			resetGridAuxVar(response);
			drawScenariosListGrid(response);
			
		}
		@Override
		public void onFailure(ServerFailure error) {
			super.onFailure(error);
		}
	};
	//----------------------------------------------------------------------------------------
	
	//Aux var to print the scn list, move thru the scenarios of the list (navigation buttons) and 
	// fetch the previous search results
	private static List<ScenarioProxy> scnList = null;
	private static int scn_search_index = 0;
	private static int scn_list_size = 0;

		
	@UiField
	FlowPanel navigationButtons;
	@UiField
	Button buttonFirst; //|<
	@UiField
	Button buttonPrev; //<
	@UiField
	Button buttonNext; //>
	@UiField
	Button buttonLast; //>|
	@UiField
	Label labelDescription; 
	
	/**
	 * Hides (makes invisible) the navigation buttons panel for the scenario list
	 */
	private void hideNavigationButtons(){
		navigationButtons.setVisible(false);
		buttonFirst.setVisible(false);
		buttonPrev.setVisible(false);
		buttonNext.setVisible(false);
		buttonLast.setVisible(false);
//		labelDescription.setText("");
		labelDescription.setVisible(false);
	}
	
	/**
	 * Shows (makes visible) the navigation buttons panel for the scenario list
	 */
	private void showNavigationButtons(){
		navigationButtons.setVisible(true);
		buttonFirst.setVisible(true);
		buttonPrev.setVisible(true);
		buttonNext.setVisible(true);
		buttonLast.setVisible(true);
		labelDescription.setVisible(true);
	}
	
	/**
	 * resets the aux variables that we need to move thru the scn list editor
	 */
	private void resetGridAuxVar(List<ScenarioProxy> response){
//		showNavigationButtons();
		hideNavigationButtons();
		scnList = response;
		scn_search_index = 0; //beginning of the search
		scn_list_size =scnList.size();
		buttonFirst.setEnabled(false);
		buttonPrev.setEnabled(false);
		//FIXME Do I need to do this or can I rely that is checked later?
		if(scn_list_size>SCN_GRIDLIST_ROWS){
			buttonNext.setEnabled(true);
			buttonLast.setEnabled(true);
		}else{
			buttonNext.setEnabled(false);
			buttonLast.setEnabled(false);
		}
			
		String text;//FIXME Do I need to do this or can I rely that is checked later?
		if(response.size()==0){
			text = "No resulsts found";
		}else
			text = "Results 1 to "+Math.min(SCN_GRIDLIST_ROWS, response.size())+" of "+response.size();
		labelDescription.setText(text);

		navigationButtons.setStylePrimaryName("navigationPanel");

	}
	
	/**
	 * Shows the users latest search (if any)
	 */
	public void showLatestSearch(){			
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		status.setVisible(false);
		if(scnList!= null && scnList.size()>0){			
			onClickGoToFirst(null);
		}else{
			searchResultCaption.setText("No previous search information available");
			searchResultCaption.setVisible(true);
		}
	}
	
	
	/** XXX Experimental */
	private void drawScenariosListGrid(){
		drawScenariosListGrid(scnList);
	}
	
	
	/**
	 * Draws the table with the scenario information
	 * @param response
	 */
	private void drawScenariosListGrid(final List<ScenarioProxy> response){
		
		this.scnList = response;//update aux var
		
//		hideAllSearchPanels();//updates status label to "visible"
		status.setVisible(false);
		int row =1;
		int size = scn_search_index+SCN_GRIDLIST_ROWS>scn_list_size?(scn_list_size-scn_search_index): SCN_GRIDLIST_ROWS;
//		searchResult2.resizeRows(SCN_GRIDLIST_ROWS+1);
		searchResult2.resizeRows(size+1);//add +1 for title row
		
		if(userRole == UserRole.Administrator){
			searchResult2.resizeColumns(SCN_GRIDLIST_COLUMNS_admin);
		}else if(userRole == UserRole.RegisteredUser){
			searchResult2.resizeColumns(SCN_GRIDLIST_COLUMNS_RegUser);
		}else 
			searchResult2.resizeColumns(SCN_GRIDLIST_COLUMNS_UnregUser);
		
		
		//HEADER
	    Label lbl_title = new Label("Title");
	    lbl_title.setStyleName(STYLE_CLICKABLE);
	    lbl_title.addClickHandler(new ClickHandler() {//clicking the title, we sort by title			
			@Override
			public void onClick(ClickEvent event) {
				Collections.sort(response, scnTitleComparator);//TODO Maybe now we can use the 
				scnTitleComparator.switchOrder();
				resetGridAuxVar(response);
				drawScenariosListGrid(response);
			}
		});
	    
	    Label lbl_uniqueId = new Label("Unique Id");
	    lbl_uniqueId.setStyleName(STYLE_CLICKABLE);
	    lbl_uniqueId.addClickHandler(new ClickHandler() {//clicking the title, we sort by title			
			@Override
			public void onClick(ClickEvent event) {
				scnComparator.setProperty(ScenarioComparator.PROPERTY_ID);
				Collections.sort(response, scnComparator);
				scnComparator.switchReverseOrder();
				resetGridAuxVar(response);
				drawScenariosListGrid(response);
			}
		});
	    
	    Label lbl_submitter = new Label("Submitter");
	    lbl_submitter.addStyleName(STYLE_CLICKABLE);
	    lbl_submitter.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {//clicking the submitter, we sort by submitter
				Collections.sort(response, scnSubmitterComparator);//sort list of scn
				scnSubmitterComparator.switchOrder();
				resetGridAuxVar(response);
				drawScenariosListGrid(response);				
			}
		});
	    
	    Label lbl_status = new Label("Status");
	    lbl_status.addStyleName(STYLE_CLICKABLE);
	    lbl_status.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {//on click we sort by status
				Collections.sort(response, scnStatusComparator);//sort list of scn
				scnStatusComparator.switchOrder();
				resetGridAuxVar(response);
				drawScenariosListGrid(response);					
			}
		});
	    
	    //TICKET-163
	    Label lbl_acks = new Label("# ACKs");
	    lbl_acks.setTitle("Displays nunber of registered users that aclnowledge the content of the scenario");
	    lbl_acks.addStyleName(STYLE_CLICKABLE);
	    lbl_acks.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {//on click we sort by status
				Collections.sort(response, scnAcksComparator);//sort list of scn
				scnAcksComparator.switchOrder();
				resetGridAuxVar(response);
				drawScenariosListGrid(response);					
			}
		});
	    

		searchResult2.setWidget(0, SCN_TABLE_FIRST_COL,lbl_title);
		searchResult2.setWidget(0, SCN_TABLE_SECOND_COL,lbl_uniqueId);
		searchResult2.setWidget(0, SCN_TABLE_THIRD_COL, lbl_acks);//Ticket-163
		//TODO: 1- sorting for this label; 2- find a better caption; 3-use title/tooltip to describe this field
		
		if(userRole == UserRole.Administrator){
			searchResult2.setWidget(0, SCN_TABLE_FOURTH_COL, lbl_status);
			searchResult2.setWidget(0, SCN_TABLE_FIFTH_COL, lbl_submitter);
			searchResult2.setWidget(0, SCN_TABLE_SIXTH_COL, new Label("Created"));
			searchResult2.setWidget(0, SCN_TABLE_SEVENTH_COL, new Label("Last Modified"));
			searchResult2.setWidget(0, SCN_TABLE_EIGTH_COL, new Label("Locked by"));
		}
		if(userRole == UserRole.RegisteredUser){
			searchResult2.setWidget(0, SCN_TABLE_FOURTH_COL, lbl_status);
			searchResult2.setWidget(0, SCN_TABLE_FIFTH_COL, new Label("Created"));
			searchResult2.setWidget(0, SCN_TABLE_SIXTH_COL, new Label("Last Modified"));
		}
		searchResult2.getRowFormatter().addStyleName(0, STYLE_USERLISTHEADER); //TODO Style this table
		searchResult2.getColumnFormatter().addStyleName(0, "titleColumn");
		
		ScenarioProxy[] responseArray = new ScenarioProxy[response.size()];
		responseArray = response.toArray(responseArray);
		int arrayIndex = scn_search_index;
		
		//draw table rows
		while(row<=SCN_GRIDLIST_ROWS && arrayIndex<responseArray.length){
				final ScenarioProxy sp = 	responseArray[arrayIndex];///it.next();
		Label lbl = new Label();
		lbl.setStyleName(STYLE_CLICKABLE);
		lbl.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(null != searchHandler) {
					searchResult2.clear();
					searchHandler.onSearchResult(sp);
				}
			}
			
		});
		String title = sp.getTitle();
		title = null == title || "".equals(title) ? "<none>" : title;//XXX title.trim() ??
		final String auxTitle = title;
		lbl.setText(title);
		searchResult2.setWidget(row, SCN_TABLE_FIRST_COL, lbl);
		searchResult2.setWidget(row, SCN_TABLE_SECOND_COL, new Label(String.valueOf(sp.getId())));
		Set<String> ackEdgers = sp.getAcknowledgers().getAcknowledgersIDs();
//		List<String> auxAckEdgers = sp.getAcknowledgers().getAcknowledgersIDs();
//		Set<String> ackEdgers = new HashSet<String>(auxAckEdgers);
//		if(null==ackEdgers)
//			ackEdgers = new HashSet<String>();
		Label acksLabel = ackEdgers==null? new Label("0"):new Label(String.valueOf(ackEdgers.size()));
		searchResult2.setWidget(row, SCN_TABLE_THIRD_COL, acksLabel);//ticket-163

		if(userRole == UserRole.Administrator){
			searchResult2.setWidget(row, SCN_TABLE_FOURTH_COL, new Label(sp.getStatus()));
			searchResult2.setWidget(row, SCN_TABLE_FIFTH_COL, new Label(sp.getSubmitter()));
			searchResult2.setWidget(row, SCN_TABLE_SIXTH_COL, new Label(dtf.format(sp.getCreationDate())));
			String action = null==sp.getLastActionTaken()? "action unknown" :sp.getLastActionTaken();
			String user = null==sp.getLastActionUser()? "user unknown" : sp.getLastActionUser();
			String date = null==sp.getModificationDate()? "date unknown" : dtf.format(sp.getCreationDate());
			String lastAction = action+" by " +user+" on "+date;
			searchResult2.setWidget(row, SCN_TABLE_SEVENTH_COL, new Label(lastAction));
			String lockOwner = null==sp.getLockOwner()? "-unlocked-" : sp.getLockOwner();
			searchResult2.setWidget(row, SCN_TABLE_EIGTH_COL, new Label(lockOwner));
		}
		if(userRole == UserRole.RegisteredUser){
			searchResult2.setWidget(row, SCN_TABLE_FOURTH_COL, new Label(sp.getStatus()));
			searchResult2.setWidget(row, SCN_TABLE_FIFTH_COL, new Label(dtf.format(sp.getCreationDate())));
			String modifDate = null==sp.getModificationDate() ? "-unknown-" : dtf.format(sp.getModificationDate());
			searchResult2.setWidget(row, SCN_TABLE_SIXTH_COL, new Label(modifDate));
		}

		final int arrayIndex2 = arrayIndex;
		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {
				
			@Override
			public void onClick(ClickEvent event) {
				boolean delete = Window.confirm("Are you sure you want to delete scenario \""+auxTitle+"\"?");
				if(delete){
					//delete the entity too (or delete entity and redraw
					ScenarioRequest req = scenarioRequestFactory.scenarioRequest();
					ScenarioProxy mutableProxy = req.edit(sp);
					req.remove().using(mutableProxy).fire();
					
					//update rows shown
					if(arrayIndex2==scn_search_index && arrayIndex2==scn_list_size-1)//show previous
						scn_search_index-=SCN_GRIDLIST_ROWS;
					scnList.remove(arrayIndex2);
					scn_list_size -=1;
					drawScenariosListGrid(scnList);
				}
			}
		});

		if(userRole==UserRole.Administrator)//Only Admins should be able to delete Scn
			searchResult2.setWidget(row, SCN_TABLE_NINTH_COL, deleteButton);
		
		//style table rows
		if(sp.getStatus()!=null){
			searchResult2.getRowFormatter().removeStyleName(row, STYLE_UNSUBMITTEDSCN);
			searchResult2.getRowFormatter().removeStyleName(row, STYLE_SUBMITTEDSCN);
			searchResult2.getRowFormatter().removeStyleName(row, STYLE_REJECTEDSCN);
			if(sp.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED)){
				searchResult2.getRowFormatter().addStyleName(row, STYLE_SUBMITTEDSCN);
			}else if(sp.getStatus().equals(ScenarioPanel.SCN_STATUS_REJECTED)){
				searchResult2.getRowFormatter().addStyleName(row, STYLE_REJECTEDSCN);
			}else{
				searchResult2.getRowFormatter().addStyleName(row, STYLE_UNSUBMITTEDSCN);
			}
		}
        //print pijama
		if(row%2==0)
			searchResult2.getRowFormatter().addStyleName(row, STYLE_TABLEROWOTHER);
		else
			searchResult2.getRowFormatter().removeStyleName(row, STYLE_TABLEROWOTHER);
		
		row +=1;//increase row number (the FOR loop is not increasing our row index variable, which is also final)
		arrayIndex++;
		}
		
		searchResult2.setVisible(true);
		if(arrayIndex<scn_list_size){
			buttonNext.setEnabled(true);
			buttonLast.setEnabled(true);
		}else{
			buttonNext.setEnabled(false);
			buttonLast.setEnabled(false);
		}
		showNavigationButtons();
		//description of the result search and navigation buttons
		String text;// = "Results "+ (scn_search_index +1)+" to "+arrayIndex+" of "+scn_list_size;
		if(response.size()==0){
			text = "No resulsts found";
		}else
			text = "Results "+ (scn_search_index +1)+" to "+arrayIndex+" of "+scn_list_size;
		labelDescription.setText(text);

	}
	

	
	@UiHandler("buttonFirst")
	public void onClickGoToFirst(ClickEvent clickEvent) {
		buttonFirst.setEnabled(false);
		buttonPrev.setEnabled(false);
		if(scn_list_size>SCN_GRIDLIST_ROWS){
			buttonNext.setEnabled(true);
			buttonLast.setEnabled(true);
		}
		scn_search_index = 0;
		drawScenariosListGrid();
	}
	
	@UiHandler("buttonLast")
	public void onClickGoToLast(ClickEvent clickEvent){
		buttonNext.setEnabled(false);
		buttonLast.setEnabled(false);
		if(scn_list_size>SCN_GRIDLIST_ROWS){
			buttonFirst.setEnabled(true);
			buttonPrev.setEnabled(true);
		}
//		int aux = scn_list_size%SCN_GRIDLIST_ROWS;
		scn_search_index = scn_list_size%SCN_GRIDLIST_ROWS==0?scn_list_size-SCN_GRIDLIST_ROWS:(scn_list_size/SCN_GRIDLIST_ROWS)*SCN_GRIDLIST_ROWS;
		drawScenariosListGrid();

	}
	
	@UiHandler("buttonNext")
	public void onClickGoToNext(ClickEvent clickEvent){
		buttonFirst.setEnabled(true);
		buttonPrev.setEnabled(true);

		scn_search_index += SCN_GRIDLIST_ROWS ;
		drawScenariosListGrid();

	}
	
	@UiHandler("buttonPrev")
	public void onClickGoToPrev(ClickEvent clickEvent){
		buttonNext.setEnabled(true);
		buttonLast.setEnabled(true);

		scn_search_index -= SCN_GRIDLIST_ROWS ;
		drawScenariosListGrid();

	}
	
	
	//-------------------------------------------------
	@UiHandler("searchQuery")
	public void onFocus(FocusEvent focusEvent) {
		searchQuery.setText("");
	}
	
	@UiHandler("submitButton")
	public void onClick(ClickEvent clickEvent) {		
		hideAllSearchPanels();
//		searchResultCaption.setText("Search results for: \""+searchQuery.getText()+"\""); TICKET-134. Multiword search
		if (searchQuery.getText().trim().indexOf(" ")<0)
			searchResultCaption.setText("Search results for: \""+searchQuery.getText()+"\"");
		else{
			String word = new String();
			List<String> keyWordsList = Arrays.asList(searchQuery.getText().trim().split("\\s+"));
			for(String s : keyWordsList){
				word += "\""+s+"\", ";
			}
			searchResultCaption.setText("Search results for: "+word.substring(0, word.length()-2));
		}
			
		searchResultCaption.setVisible(true);
		doSearch(searchQuery.getText());
	}
	
	@UiHandler("searchQuery")
	public void onKeyUp(KeyUpEvent kue) {
		
		if(kue.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
			hideAllSearchPanels();//TICKET-119
//			searchResultCaption.setText("Search results for: \""+searchQuery.getText()+"\""); TICKET-134. Multiword search
			if (searchQuery.getText().trim().indexOf(" ")<0)
				searchResultCaption.setText("Search results for: \""+searchQuery.getText()+"\"");
			else{
				String word = new String();
				List<String> keyWordsList = Arrays.asList(searchQuery.getText().trim().split("\\s+"));
				for(String s : keyWordsList){
					word += "\""+s+"\", ";
				}
				searchResultCaption.setText("Search results for: "+word.substring(0, word.length()-2));
			}
			searchResultCaption.setVisible(true);
			doSearch(searchQuery.getText());
			
		}
	}
	
	@UiHandler("createNew")
	public void onClickNew(ClickEvent clickEvent) {
		
		if(userRole==UserRole.AnonymousUser){
			Window.alert("Please, Log In to create new Clinical Scenarios");
		}else if(null != searchHandler) {
			searchHandler.onSearchResult(null);
		}
		
	}
	
	@UiField
	FlowPanel basicSearch;
	
	private void hideBasicSearch(){
		basicSearch.setVisible(false);		
	}
	public void showBasicSearch(){
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		basicSearch.setVisible(true);
		status.setVisible(false);		
	}
	
	@UiField
	FlowPanel advancedSearch;
	@UiField
	@Ignore
	FlexTable advancedSearchComponentsTable;
	
//	@UiField
	TextBox advancedSearchKeywordsTextBox = new TextBox();
//	@UiField
	DateBox advancedSearchDateBoxFrom = new DateBox();	
//	@UiField
	DateBox advancedSearchDateBoxUntil = new DateBox();
//	@UiField
	ListBox advancedSearchHazardSeverityListbox = new ListBox();
//	@UiField
	SuggestBox advancedSearchCliniciansTextBox = new SuggestBox(ScenarioPanel.getClinicianSuggestOracle());
//	@UiField
	SuggestBox advancedSearchEnvironmentsTextBox = new SuggestBox(ScenarioPanel.getEnvironmentSuggestOracle());
//	@UiField
	TextBox advancedSearchEquipmentTypeTextBox = new TextBox();
//	@UiField
	TextBox advancedSearchEquipmentManufacturerTextBox = new TextBox();
	
	@UiField
	Button advancedSearchButton;
	
	@UiHandler("advancedSearchButton")
	public void onClickAdvancedSearchButton(ClickEvent clickEvent) {
		Date dateFrom = advancedSearchDateBoxFrom.getValue();
		Date dateUntil = advancedSearchDateBoxUntil.getValue();

		//1- Validation that date-from in not after date-until
		if(dateFrom!=null && dateUntil!=null && dateFrom.after(dateUntil)){
			String msg = "Date \"from\" can not be after date \"until\"";
			Window.alert(msg);	
			return;
		}
		
		hideAllSearchPanels();		
		doAdvancedSearch();
	}
	
	private void hideAdvancedSearch(){
		advancedSearch.setVisible(false);
		searchResult2.setVisible(false);
	}
	public void showAdvancedSearch(){
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		advancedSearch.setVisible(true);
		status.setVisible(false);
		searchResult2.setVisible(false);	
//		radioButtonOr.setValue(true);
		initializeAdvancedSearchPanel();


	}
	
	/**
	 * Initializes the flextable with the components for the advanced search
	 */
	private void initializeAdvancedSearchPanel(){
		advancedSearchComponentsTable.removeAllRows();
		//first row
		advancedSearchComponentsTable.setWidget(1, 1, new Label("Keywords in scenario"));
		advancedSearchComponentsTable.setWidget(1, 2, advancedSearchKeywordsTextBox);
		//second row
		advancedSearchComponentsTable.setWidget(2, 1, new Label("Creation date from "));
		advancedSearchComponentsTable.setWidget(2, 2, advancedSearchDateBoxFrom);
		advancedSearchComponentsTable.setWidget(2, 3, new Label("  up to date "));
		advancedSearchComponentsTable.setWidget(2, 4, advancedSearchDateBoxUntil);
		//third row
		advancedSearchComponentsTable.setWidget(3, 1, new Label("Hazard severity"));
		advancedSearchComponentsTable.setWidget(3, 2, advancedSearchHazardSeverityListbox);
		//XXX This could go somewhere else and being called ONCE instead of EACH TIME.
//		advancedSearchDateBoxFrom.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("MMMM dd, yyyy")));
//		advancedSearchDateBoxUntil.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("MMMM dd, yyyy")));
//		advancedSearchHazardSeverityListbox.clear();
//		advancedSearchHazardSeverityListbox.addItem(" ");// to ad no-filter option
//		for(String s : ScenarioPanel.getHazardSeverityValues()){
//			advancedSearchHazardSeverityListbox.addItem(s);
//		}
		//
		//fourth row
		advancedSearchComponentsTable.setWidget(4, 1, new Label("Clinicians Involved "));
		advancedSearchComponentsTable.setWidget(4, 2, advancedSearchCliniciansTextBox);
		advancedSearchComponentsTable.setWidget(4, 3, new Label("Clinicial Environments Involved"));
		advancedSearchComponentsTable.setWidget(4, 4, advancedSearchEnvironmentsTextBox);
		//fifth row
		advancedSearchComponentsTable.setWidget(5, 1, new Label("Device Type"));
		advancedSearchComponentsTable.setWidget(5, 2, advancedSearchEquipmentTypeTextBox);
		advancedSearchComponentsTable.setWidget(5, 3, new Label("Device Manufacturer"));
		advancedSearchComponentsTable.setWidget(5, 4, advancedSearchEquipmentManufacturerTextBox);
		
	}
	
//	@UiField
//	RadioButton radioButtonAnd;
//	@UiField
//	RadioButton radioButtonOr;
	
	/**
	 * HIDES all components on search panels (to allow re-drawing of panel)
	 */
	public void hideAllSearchPanels(){
		hideNavigationButtons();
		hideAdvancedSearch();
		hideBasicSearch();
		searchResult2.clear();//clear scn table
		searchResultCaption.setVisible(false);
		//We hide the search panels to show something new, that will be the result of the search
		// so we update/show this label to indicate that we're fetching data
		status.setVisible(true);
		hideSearchById();// Search by ID
		hideSearchByDates();// Search by dates
		hideSearchByTags();
	}
	
	//---------------------------------------
	//Search Scenarios by Id feature
	@UiField
	FlowPanel searchById;
	
	@UiField
	TextBox searchQueryById;
	
	@UiField
	Button buttonSearchById;
	
	private void hideSearchById(){
		searchById.setVisible(false);
	}
	public void showSearchById(){
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		searchById.setVisible(true);
		status.setVisible(false);
	}
		
	@UiHandler("buttonSearchById")
	public void onClickButtonSearchById(ClickEvent clickEvent) {	
		Long scnId;
		//Validation of numeric ID
		try{
			scnId = Long.parseLong(searchQueryById.getText());
		}catch(NumberFormatException e){
			String msg = searchQueryById.getText()+" is NOT a valid number. Scenarios Id MUST be a number.";
			Window.alert(msg);	
			return;
		}
		hideAllSearchPanels();
		searchResultCaption.setText("Search results for ID #"+searchQueryById.getText()+".");
		searchResultCaption.setVisible(true);
		doSearchById(scnId);
	}
	
	@UiHandler("searchQueryById")
	public void onFocusSearchQueryById(FocusEvent focusEvent) {
		searchQueryById.setText("");
	}
	
	@UiHandler("searchQueryById")
	public void onKeyUpButtonSearchById(KeyUpEvent kue) {		
		if(kue.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
			Long scnId;
			//Validation of numeric ID
			try{
				scnId = Long.parseLong(searchQueryById.getText());
			}catch(NumberFormatException e){
				String msg = searchQueryById.getText()+" is NOT a valid number. Scenarios Id MUST be a number.";
				Window.alert(msg);	
				return;
			}
			hideAllSearchPanels();
			searchResultCaption.setText("Search results for ID #"+searchQueryById.getText()+".");
			searchResultCaption.setVisible(true);
			doSearchById(scnId);
			
		}
	}
	
	/**
	 * Searches a Scenario by its unique ID. Checks the user has privileges to see this Scenario.
	 * @param scnId
	 */
	private void doSearchById(Long scnId) {
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.findById(scnId)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution", "references", "acknowledgers", "associatedTags")
		.to(new Receiver<ScenarioProxy>() {

			@Override
			public void onSuccess(ScenarioProxy result) {
				List<ScenarioProxy> response = new ArrayList<ScenarioProxy>();
				
				if(null==result){ 
					status.setVisible(false); 					
				}else{
				/**
				 * Unregistered user: can see scn only is it is APPROVED
				 * Registered user: can see Scn if is APPROVED or if they own it
				 * Administrators: can see the scn w/out restriction
				 */
				if(userRole==UserRole.Administrator)
					response.add(result);
				else if (userRole==UserRole.RegisteredUser){
					if(result.getSubmitter().equals(submitterName))
						response.add(result);
				}else if (result.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED) 
						|| result.getStatus().equals(ScenarioPanel.SCN_STATUS_UNLOCKED_POST) //TICKET-186
						|| result.getStatus().equals(ScenarioPanel.SCN_STATUS_MODIFIED))
					response.add(result);
				}
				
				resetGridAuxVar(response);
				drawScenariosListGrid(response);
				
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
			}
		}).fire();
	}	
	//----------------------------------------------------------------------------------------------------------
	
	//---------------------------------------
	//Search by Dates
	@UiField
	FlowPanel searchByDates;
	
	@UiField
	@Ignore
	FlexTable dateRangeSearchComponentsTable;
	
	@UiField
	Button buttonSearchByDates;
	
	private void hideSearchByDates(){
		searchByDates.setVisible(false);
	}
	
	public void showSearchByDates(){
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		searchByDates.setVisible(true);
		status.setVisible(false);
		//one row, four columns
		dateRangeSearchComponentsTable.setWidget(1, 1, new Label("Search: from date "));
		dateRangeSearchComponentsTable.setWidget(1, 2, advancedSearchDateBoxFrom);
		dateRangeSearchComponentsTable.setWidget(1, 3, new Label(" up to date "));
		dateRangeSearchComponentsTable.setWidget(1, 4, advancedSearchDateBoxUntil);
	}
	
	@UiHandler("buttonSearchByDates")
	public void onClickButtonSearchByDates(ClickEvent clickEvent) {	

		Date dateFrom = advancedSearchDateBoxFrom.getValue();
		Date dateUntil = advancedSearchDateBoxUntil.getValue();

		//1- validation that not both dates are NULL
		if(dateFrom==null && dateUntil==null){
			String msg = "Both dates can't be empty";
			Window.alert(msg);	
			return;
		}
		//2-Validation that date-from in not after date-until
		if(dateFrom!=null && dateUntil!=null && dateFrom.after(dateUntil)){
			String msg = "Date \"from\" can not be after date \"until\"";
			Window.alert(msg);	
			return;
		}
		
		hideAllSearchPanels();
		String headline = "Search results for scenarios created";
		if(null != advancedSearchDateBoxFrom.getValue() && null != advancedSearchDateBoxUntil.getValue())
			headline += " between "+dtf.format(advancedSearchDateBoxFrom.getValue())+" and "+dtf.format(advancedSearchDateBoxUntil.getValue())+".";
		else if (null != advancedSearchDateBoxFrom.getValue())
			headline += " after "+dtf.format(advancedSearchDateBoxFrom.getValue())+".";
		else 	
			headline += " before "+dtf.format(advancedSearchDateBoxUntil.getValue())+".";
		searchResultCaption.setText(headline);
		searchResultCaption.setVisible(true);
		doSearchByDates(dateFrom, dateUntil);
		//XXX For Search by dates, which date do we use? creation date, modification date, auditing date???
	}
	
	private void doSearchByDates(Date dateFrom, Date dateUntil){
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchByCreationDateRange(dateFrom, dateUntil)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(new Receiver<List<ScenarioProxy>> () {

			@Override
			public void onSuccess(List<ScenarioProxy> result) {
				if(null==result){ 
					status.setVisible(false); 
					resetGridAuxVar(new ArrayList<ScenarioProxy>());
					drawScenariosListGrid(new ArrayList<ScenarioProxy>());
				
				}else{
				
//				List<ScenarioProxy> response = new ArrayList<ScenarioProxy>();
//				/**
//				 * Unregistered user: can see scn only is it is APPROVED
//				 * Registered user: can see Scn if is APPROVED or if they own it
//				 * Administrators: can see the scn w/out restriction
//				 */
//				if(userRole==UserRole.Administrator)
//					response.add(result);
//				else if (userRole==UserRole.RegisteredUser){
//					if(result.getSubmitter().equals(submitterName))
//						response.add(result);
//				}else if (result.getStatus().equals(ScenarioPanel.SCN_STATUS_APPROVED))
//					response.add(result);
				
					resetGridAuxVar(result);
					drawScenariosListGrid(result);
				}
				
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
			}
		}).fire();
	}
	
	//-------------------------------------------------------
	// search by tags
	@UiField
	FlowPanel searchByTags;
	
	@UiField
	@Ignore
	FlexTable tagSearchTable;
	
	@UiField
	Button buttonSearchByTags;
	
	private void hideSearchByTags(){
		searchByTags.setVisible(false);
	}
	
	public void showSearchByTags(){
		hideAllSearchPanels();//HIDE all the others; SHOW this one
		searchByTags.setVisible(true);
		status.setVisible(false);
		tagSearchTable.clear();
		if(tagRequestFactory != null){
			TagRequest tagRequest = tagRequestFactory.tagRequest();
			tagRequest.findAll().to(new Receiver<List<TagProxy>>() {
				
				@Override
				public void onSuccess(List<TagProxy> response) {
					int indexRow = 0;
					int indexCol = 0;
					Collections.sort(response, new TagComparator());
					for(TagProxy tagProxy : response){
						String tagName = tagProxy.getName();
						CheckBox cb = new CheckBox();
						Label lb = new Label(tagName);
						tagSearchTable.setWidget(indexRow/TAGS_SEARCH_NUM_COLS, indexCol%(2*TAGS_SEARCH_NUM_COLS), cb);	
						tagSearchTable.setWidget(indexRow/TAGS_SEARCH_NUM_COLS, (indexCol%(2*TAGS_SEARCH_NUM_COLS))+1, lb);
						tagSearchTable.getCellFormatter().setStyleName(indexRow/TAGS_SEARCH_NUM_COLS, (indexCol%(2*TAGS_SEARCH_NUM_COLS))+1, "paddingRight");
						indexRow++;
						indexCol+=2;
					}					
				}
				
			}).fire();
		}
	}
	
	@UiHandler("buttonSearchByTags")
	public void onClickButtonSearchByTags(ClickEvent clickEvent) {	
		hideAllSearchPanels();
		Set<String> checkedTags = new HashSet<String>() ;
		String headline = "Search results for scenaros tagged with:"; 
		for(int i=0;i<tagSearchTable.getRowCount();i++){
			for(int j=0;j<(TAGS_SEARCH_NUM_COLS*2);j+=2){
				//even numbers are checkboxes, odd are labels
				if(tagSearchTable.isCellPresent(i, j)){//make sure the column exists
					Widget wCheck = tagSearchTable.getWidget(i, j);
					Widget wLabel = tagSearchTable.getWidget(i, j+1);
					if(wCheck instanceof CheckBox && ((CheckBox) wCheck).getValue()){
						if(wLabel instanceof Label){
							checkedTags.add(((Label) wLabel).getText().toLowerCase());
							headline += " "+((Label) wLabel).getText() +",";
						}
					}
				}

			}			
		}
		if(headline.indexOf(",")>0)
			headline = headline.substring(0, headline.length()-1)+".";
		else
			headline += " <no tag selected>";
		searchResultCaption.setText(headline);
		searchResultCaption.setVisible(true);
		doSearchByTags(checkedTags);
	}
	
	private void doSearchByTags(final Set<String> checkedTags){
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		
		Set<String> statusSet = new HashSet<String>();
		statusSet.add(ScenarioPanel.SCN_STATUS_MODIFIED);
		statusSet.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
		statusSet.add(ScenarioPanel.SCN_STATUS_APPROVED);
		scenarioRequest.searchByStatus(statusSet)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(new Receiver<List<ScenarioProxy>> () {

			@Override
			public void onSuccess(List<ScenarioProxy> result) {
				List <ScenarioProxy> filteredResults = new ArrayList<ScenarioProxy>();
				if(null==result || result.size()==0){ 
					status.setVisible(false); 
				}else{
					//TODO filter approved scenarios containing the selected tags
					//filter results whose tags are not in associatedTags
					for(ScenarioProxy scn : result){
						Set<String> associatedTags = scn.getAssociatedTags().getAssociatedTagNames();						
						if(null!=associatedTags && associatedTags.size()>0){
							associatedTags.retainAll(checkedTags);//associatedTags has now the INTERSECTION of both sets
							if(associatedTags.size() >0){
								filteredResults.add(scn);
							}
						}
					}
						
				}

				resetGridAuxVar(filteredResults);
				drawScenariosListGrid(filteredResults);
				
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
			}
		}).fire();
	}
	
}
