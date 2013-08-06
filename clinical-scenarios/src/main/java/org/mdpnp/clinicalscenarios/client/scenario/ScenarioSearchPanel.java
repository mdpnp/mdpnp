package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioStatusComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioSubmitterComparator;
import org.mdpnp.clinicalscenarios.client.scenario.comparator.ScenarioTitleComparator;
import org.mdpnp.clinicalscenarios.client.user.UserInfoProxy;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequest;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequestFactory;
import org.mdpnp.clinicalscenarios.server.user.UserInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;

public class ScenarioSearchPanel extends Composite {
	
	//scenario table columns
	private static int SCN_TITLE_COL = 0;
	private static int SCN_UNIQUEID_COL = 1;
	private static int SCN_SUBMITTER_COL = 2;	
	private static int SCN_STATUS_COL = 3;
	private static int SCN_DELETEBUTTON_COL = 4;
	private final int SCN_GRIDLIST_ROWS = 10; //rows in the table showing the Scn List
	private final int SCN_GRIDLIST_COLUMNS = 5;//tilte, uniqueID, submitter,status,deleteButton(optional)
	
	private ScenarioTitleComparator scnTitleComparator = new ScenarioTitleComparator();
	private ScenarioSubmitterComparator scnSubmitterComparator = new ScenarioSubmitterComparator();
	private ScenarioStatusComparator scnStatusComparator = new ScenarioStatusComparator();
	private ScenarioComparator scnComparator = new ScenarioComparator(ScenarioComparator.PROPERTY_TITLE);
	
	//TODO add style names as constants too
	private final static String STYLE_SELECTEDROW = "selectedRow";
	private final static String STYLE_CLICKABLE = "clickable";
	private final static String STYLE_SUBMITTEDSCN =  "submittedScn";
	private final static String STYLE_UNSUBMITTEDSCN =  "unsubmittedScn";
	
	private static final String STYLE_TABLEROWOTHER = "tableRowOther";
	private static final String STYLE_USERLISTHEADER = "userListHeader";
	

	
	private static ScenarioSearchPanelUiBinder uiBinder = GWT.create(ScenarioSearchPanelUiBinder.class);
	
	private UserInfoRequestFactory userInfoRequestFactory = GWT.create(UserInfoRequestFactory.class);
	private enum UserRole {Administrator, RegisteredUser, AnonymousUser}
	private UserRole userRole;

	interface ScenarioSearchPanelUiBinder extends
			UiBinder<Widget, ScenarioSearchPanel> {
	}
	private ScenarioRequestFactory scenarioRequestFactory;
	public ScenarioSearchPanel(ScenarioRequestFactory scenarioRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.scenarioRequestFactory = scenarioRequestFactory;
		
		//check user role
		if(userInfoRequestFactory != null){
			final EventBus eventBus = new SimpleEventBus();
			userInfoRequestFactory.initialize(eventBus);
		
		UserInfoRequest userInfoRequest = userInfoRequestFactory.userInfoRequest();
		userInfoRequest.findCurrentUserInfo(Window.Location.getHref()).to(new Receiver<UserInfoProxy>() {
			@Override
			public void onSuccess(UserInfoProxy response) {
				if(response.getEmail()==null ||response.getEmail().trim().equals("") ){
					//Anonymous user
					userRole = UserRole.AnonymousUser;//can't modify the Scn
					
				}else{
					if(response.getAdmin()) 
						userRole = UserRole.Administrator;
					else
						userRole = UserRole.RegisteredUser;
				}
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
	
	@UiField
	Label pleaseEnterKeywords;
	
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
	
	public Label getPleaseEnterKeywords(){
		return pleaseEnterKeywords;
	}
	
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
	 * Advanced Search: keywords in specific fields
	 * @param keywords
	 */
	public void doAdvancedSearch(String sBackground, String sProposed, 
			String sProcess, String sAlgorithm, String sBenefits, String sRisks, String sTitle){
//		if (isAndSearch){
//			ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
//			scenarioRequest.searchByFilter_AndBehavior(sBackground, sProposed, sProcess, sAlgorithm, sBenefits, sRisks, null)
//			.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
//			.to(listScnReceiver).fire();
//		}else{
			ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
			scenarioRequest.searchByFilter_OrBehavior(sBackground, sProposed, sProcess, sAlgorithm, sBenefits, sRisks, sTitle)
			.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
			.to(listScnReceiver).fire();
//		}

	}

	@UiField
	Label status; 
		
	private void cleanScenarioTable(){
		hideNavigationButtons();
		searchResult2.clear();
		status.setVisible(true);
		
	}
	
	/**
	 * Basic search or listAll
	 * @param text
	 */
	public void doSearch(final String text) {
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchByKeywords(text)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(listScnReceiver).fire();
	}	
	
	public void findAllScn(){
//		hideNavigationButtons();
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.findAllScenarios()
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(listScnReceiver).fire();
	}


	/**
	 * List scn by submitter
	 * @param status
	 */
	public void listScnBySubmitter(final String submitter) {
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchScnBySubmitter(submitter)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(listScnReceiver).fire();
	}
	
	/**
	 * list scn by status
	 * @param status
	 */
	public void listScnByStatus(final String status){
		cleanScenarioTable();
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();		
		scenarioRequest.searchByStatus(status)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(listScnReceiver).fire();		
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Receiver for any search of list of SCN
	 */
	Receiver<List<ScenarioProxy>> listScnReceiver = new Receiver<List<ScenarioProxy>>() {

		@Override
		public void onSuccess(List<ScenarioProxy> response) {
			//drawScenariosListTable(response);//DAG Older way to print the table using FleaxTable and not limiting results shwon
			resetGridAuxVar(response);
			drawScenariosListGrid(response);
			
		}
		@Override
		public void onFailure(ServerFailure error) {
			super.onFailure(error);
		}
	};
	//----------------------------------------------------------------------------------------
	
	/**
	 * Draws the scenario list table
	 */
	@SuppressWarnings("deprecation")
	private void drawScenariosListTable(final List<ScenarioProxy> response){
	    searchResult.removeAllRows();
	    
	    /**
	     * Add table listener for when rows are clicked
	     */
	  /*  searchResult.addTableListener(new TableListener() {
			//XXX Shall we keep the table listener, use clickHandler instead or not worring at all about highlighting table rows??
			@Override
			@Deprecated
			public
			void onCellClicked(SourcesTableEvents sender, int row, int cell) {
//				searchResult.getRowFormatter().removeStyleName(row, "selectedRow");
				for(int i=1; i<searchResult.getRowCount();i++)
					searchResult.getRowFormatter().removeStyleName(i, STYLE_SELECTEDROW);
				searchResult.getRowFormatter().addStyleName(row, STYLE_SELECTEDROW);
				
			}
		});*/
	    
		//HEADER
	    Label lbl_title = new Label("Title");
	    lbl_title.setStyleName(STYLE_CLICKABLE);
	    lbl_title.addClickHandler(new ClickHandler() {//clicking the title, we sort by title			
			@Override
			public void onClick(ClickEvent event) {
				Collections.sort(response, scnTitleComparator);
				scnTitleComparator.switchOrder();
				drawScenariosListTable(response);
			}
		});
	    
	    Label lbl_submitter = new Label("Submitter");
	    lbl_submitter.addStyleName(STYLE_CLICKABLE);
	    lbl_submitter.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {//clicking the submitter, we sort by submitter
				Collections.sort(response, scnSubmitterComparator);//sort list of scn
				scnSubmitterComparator.switchOrder();
				drawScenariosListTable(response);				
			}
		});
	    
	    Label lbl_status = new Label("Status");
	    lbl_status.addStyleName(STYLE_CLICKABLE);
	    lbl_status.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {//on click we sort by status
				Collections.sort(response, scnStatusComparator);//sort list of scn
				scnStatusComparator.switchOrder();
				drawScenariosListTable(response);					
			}
		});
	    
		searchResult.insertRow(0);
		searchResult.setWidget(0, SCN_TITLE_COL,lbl_title);
		searchResult.setWidget(0, SCN_SUBMITTER_COL, lbl_submitter);
		searchResult.setWidget(0, SCN_STATUS_COL, lbl_status);
		searchResult.getRowFormatter().addStyleName(0, STYLE_USERLISTHEADER); //TODO Style this table
		
		int row =1;
		for(final ScenarioProxy sp : response) {
			
			Label lbl = new Label();
			lbl.setStyleName(STYLE_CLICKABLE);
			lbl.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(null != searchHandler) {
					    searchResult.removeAllRows();
						searchHandler.onSearchResult(sp);
					}
				}
				
			});
			String title = sp.getTitle();
			title = null == title || "".equals(title) ? "<none>" : title;//XXX title.trim() ??
			final String auxTitle = title;
			lbl.setText(title);
			searchResult.insertRow(row);
			searchResult.setWidget(row, SCN_TITLE_COL, lbl);
			searchResult.setWidget(row, SCN_SUBMITTER_COL, new Label(sp.getSubmitter()));
			searchResult.setWidget(row, SCN_STATUS_COL, new Label(sp.getStatus()));
			
			final int rowDel = row;
			Button deleteButton = new Button("Delete");
			deleteButton.addClickHandler(new ClickHandler() {
					
				@Override
				public void onClick(ClickEvent event) {
					//Add a validation, so the user is asked if (s)he is sure about deleting the scn
					boolean delete = Window.confirm("Are you sure you want to delete scenario \""+auxTitle+"\"?");
					if(delete){
						searchResult.removeRow(rowDel);
						//delete the entity too (or delete entity and redraw
						ScenarioRequest req = scenarioRequestFactory.scenarioRequest();
						ScenarioProxy mutableProxy = req.edit(sp);
						req.remove().using(mutableProxy).fire();
					}
				}
			});
			if(userRole==userRole.Administrator)//Only Admins should be able to delete Scn
				searchResult.setWidget(row, SCN_DELETEBUTTON_COL, deleteButton);
			
			//style table rows
			if(sp.getStatus()!=null)
			if(sp.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED))
				searchResult.getRowFormatter().addStyleName(row, STYLE_SUBMITTEDSCN);
			else
				searchResult.getRowFormatter().addStyleName(row, STYLE_UNSUBMITTEDSCN);
	        
	        //increase row number (the FOR loop is not increasing our row index variable, which is also final)
			if(row%2==0)
				searchResult.getRowFormatter().addStyleName(row, STYLE_TABLEROWOTHER);
			row+=1;	
			

		}

	}
	
	//aux var to ptint the scn list
	private List<ScenarioProxy> scnList = null;
	private static int scn_search_index = 0;
	private static int scn_list_size = 0;
//	private static int resultsNum1;//Showing resultsNum1 - resultsNum2 of resultListSize
//	private static int resultsNum2;
		
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
	public void hideNavigationButtons(){
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
	public void showNavigationButtons(){
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
	

	
	/** XXX Experimental */
	private void drawScenariosListGrid(){
		drawScenariosListGrid(scnList);
	}
	
	private void drawScenariosListGrid(final List<ScenarioProxy> response){
		status.setVisible(false);
		int row =1;
	
		searchResult2.clear();
		hideNavigationButtons();
//		searchResult2.setVisible(true);
		int size = scn_search_index+SCN_GRIDLIST_ROWS>scn_list_size?(scn_list_size-scn_search_index): SCN_GRIDLIST_ROWS;
//		searchResult2.resizeRows(SCN_GRIDLIST_ROWS+1);
		searchResult2.resizeRows(size+1);
		searchResult2.resizeColumns(SCN_GRIDLIST_COLUMNS);
		
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
	    

		searchResult2.setWidget(0, SCN_TITLE_COL,lbl_title);
		searchResult2.setWidget(0, SCN_UNIQUEID_COL,lbl_uniqueId);
		searchResult2.setWidget(0, SCN_SUBMITTER_COL, lbl_submitter);
		searchResult2.setWidget(0, SCN_STATUS_COL, lbl_status);
		searchResult2.getRowFormatter().addStyleName(0, STYLE_USERLISTHEADER); //TODO Style this table
//		searchResult2.setWidth("500px");
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
		searchResult2.setWidget(row, SCN_TITLE_COL, lbl);
		searchResult2.setWidget(row, SCN_UNIQUEID_COL, new Label(String.valueOf(sp.getId())));
		searchResult2.setWidget(row, SCN_SUBMITTER_COL, new Label(sp.getSubmitter()));
		searchResult2.setWidget(row, SCN_STATUS_COL, new Label(sp.getStatus()));
		
		final int rowDel = row;
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
//		searchResult2.getColumnCount()
		if(userRole==userRole.Administrator)//Only Admins should be able to delete Scn
			searchResult2.setWidget(row, SCN_DELETEBUTTON_COL, deleteButton);
		
		//style table rows
		if(sp.getStatus()!=null){
			//print 'pijama' for scn grid list
			if(sp.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED)){
				searchResult2.getRowFormatter().addStyleName(row, STYLE_SUBMITTEDSCN);
				searchResult2.getRowFormatter().removeStyleName(row, STYLE_UNSUBMITTEDSCN);
			}else{
				searchResult2.getRowFormatter().addStyleName(row, STYLE_UNSUBMITTEDSCN);
				searchResult2.getRowFormatter().removeStyleName(row, STYLE_SUBMITTEDSCN);
			}
		}
        //increase row number (the FOR loop is not increasing our row index variable, which is also final)
		if(row%2==0)
			searchResult2.getRowFormatter().addStyleName(row, STYLE_TABLEROWOTHER);
		else
			searchResult2.getRowFormatter().removeStyleName(row, STYLE_TABLEROWOTHER);
		
		row +=1;
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
//		resultsNum1 =0;
//		resultsNum2 = Math.min(SCN_GRIDLIST_ROWS, scn_list_size);
//		String text = "Results "+ (resultsNum1 +1)+" to "+resultsNum2+" of "+scn_list_size;
//		labelDescription.setText(text);
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
		int aux = scn_list_size%SCN_GRIDLIST_ROWS;
		scn_search_index = scn_list_size%SCN_GRIDLIST_ROWS==0?scn_list_size-SCN_GRIDLIST_ROWS:(scn_list_size/SCN_GRIDLIST_ROWS)*SCN_GRIDLIST_ROWS;
//		resultsNum1 = scn_search_index;
//		resultsNum2 = scn_list_size;
//		String text = "Results "+(resultsNum1+1) +" to "+resultsNum2+" of "+scn_list_size;
//		labelDescription.setText(text);
		drawScenariosListGrid();

	}
	
	@UiHandler("buttonNext")
	public void onClickGoToNext(ClickEvent clickEvent){
		buttonFirst.setEnabled(true);
		buttonPrev.setEnabled(true);

		scn_search_index += SCN_GRIDLIST_ROWS ;
//		resultsNum1 += SCN_GRIDLIST_ROWS;
//		resultsNum2 = Math.min(scn_list_size, resultsNum2+SCN_GRIDLIST_ROWS);
//		if(resultsNum2 == scn_list_size){
//			buttonNext.setEnabled(false);
//			buttonLast.setEnabled(false);
//		}
//		String text = "Results "+(resultsNum1 +1)+" to "+resultsNum2+" of "+scn_list_size;
//		labelDescription.setText(text);
		drawScenariosListGrid();

	}
	
	@UiHandler("buttonPrev")
	public void onClickGoToPrev(ClickEvent clickEvent){
		buttonNext.setEnabled(true);
		buttonLast.setEnabled(true);

		scn_search_index -= SCN_GRIDLIST_ROWS ;
////		resultsNum2 = resultsNum1-1;
//		resultsNum2 = resultsNum1;//-1;
//		resultsNum1 -= SCN_GRIDLIST_ROWS;
////		resultsNum2 = Math.min(resultListSize, resultsNum2-SCN_GRIDLIST_ROWS);
//		
//		if(resultsNum1 == 0){
//			buttonFirst.setEnabled(false);
//			buttonPrev.setEnabled(false);
//		}
//		String text = "Results "+(resultsNum1 +1) +" to "+resultsNum2+" of "+scn_list_size;
//		labelDescription.setText(text);
		drawScenariosListGrid();

	}
	
	
	//-------------------------------------------------
	@UiHandler("searchQuery")
	public void onFocus(FocusEvent focusEvent) {
		searchQuery.setText("");
	}
	
	@UiHandler("submitButton")
	public void onClick(ClickEvent clickEvent) {
		hideBasicSearch();
		doSearch(searchQuery.getText());
	}
	
	@UiHandler("searchQuery")
	public void onKeyUp(KeyUpEvent kue) {
		
		if(kue.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
			
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
	
	public void hideBasicSearch(){
		basicSearch.setVisible(false);		
		status.setVisible(true);
//		searchQuery.setVisible(false);
//		pleaseEnterKeywords.setVisible(false);
//		submitButton.setVisible(false);
	}
	public void showBasicSearch(){
		basicSearch.setVisible(true);
		status.setVisible(false);
//		searchQuery.setText("");
//		searchQuery.setVisible(true);
//		pleaseEnterKeywords.setVisible(true);
//		submitButton.setVisible(true);
		
	}
	
	@UiField
	FlowPanel advancedSearch;
	
	@UiField
	TextBox titleSearch;
	@UiField
	TextBox backgroundSearch;
	@UiField
	TextBox proposedSearch;
	@UiField
	TextBox processSearch;
	@UiField
	TextBox algorithmSearch;
	@UiField
	TextBox benefitsSearch;
	@UiField
	TextBox risksSearch;
	
	@UiField
	Button advancedSearchButton;
	
	@UiHandler("advancedSearchButton")
	public void onClickAdvancedSearchButton(ClickEvent clickEvent) {
		hideAdvancedSearch();
		
		String sBackground = backgroundSearch.getText()!= null && !backgroundSearch.getText().trim().equals("")? backgroundSearch.getText().trim():null;
		String sProposed = proposedSearch.getText()!=null && !proposedSearch.getText().trim().equals("")?proposedSearch.getText().trim():null;
		String sProcess = proposedSearch.getText()!=null && !processSearch.getText().trim().equals("")?processSearch.getText().trim():null;
		String sAlgorithm = algorithmSearch.getText()!=null && !algorithmSearch.getText().trim().equals("")?algorithmSearch.getText().trim():null;
		String sBenefits = benefitsSearch.getText()!=null && !benefitsSearch.getText().trim().equals("")?benefitsSearch.getText().trim():null;
		String sRisks = risksSearch.getText()!=null && !risksSearch.getText().trim().equals("")?risksSearch.getText().trim():null;
		String sTitle= titleSearch.getText()!=null && !titleSearch.getText().trim().equals("")?titleSearch.getText().trim():null;
		
		
		doAdvancedSearch(sBackground, sProposed, sProcess, sAlgorithm, sBenefits, sRisks, sTitle);
	}
	
	public void hideAdvancedSearch(){
		advancedSearch.setVisible(false);
		status.setVisible(true);
		searchResult2.setVisible(false);
	}
	public void showAdvancedSearch(){
		advancedSearch.setVisible(true);
		status.setVisible(false);
		searchResult2.setVisible(false);	
//		radioButtonOr.setValue(true);
	}
	
//	@UiField
//	RadioButton radioButtonAnd;
//	@UiField
//	RadioButton radioButtonOr;
	
}
