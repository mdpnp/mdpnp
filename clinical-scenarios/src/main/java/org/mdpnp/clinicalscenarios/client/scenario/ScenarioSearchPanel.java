package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.Collections;
import java.util.List;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class ScenarioSearchPanel extends Composite {
	
	//scenario table columns
	private static int SCN_TITLE_COL = 0;
	private static int SCN_SUBMITTER_COL = 1;
	private static int SCN_STATUS_COL = 2;
	private static int SCN_DELETEBUTTON_COL = 4;
	
	private ScenarioTitleComparator scnTitleComparator = new ScenarioTitleComparator();
	private ScenarioSubmitterComparator scnSubmitterComparator = new ScenarioSubmitterComparator();
	private ScenarioStatusComparator scnStatusComparator = new ScenarioStatusComparator();
	
	//TODO add style names as constants too
	private final static String STYLE_SELECTEDROW = "selectedRow";
	private final static String STYLE_CLICKABLE = "clickable";
	private final static String STYLE_SUBMITTEDSCN =  "submittedScn";
	private final static String STYLE_UNSUBMITTEDSCN =  "unsubmittedScn";
	
	private static final String STYLE_TABLEROWOTHER = "tableRowOther";
	
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
	 * Basic search or listAll
	 * @param text
	 */
	public void doSearch(final String text) {
		ScenarioRequest scenarioRequest = scenarioRequestFactory.scenarioRequest();
		scenarioRequest.searchByKeywords(text)
		.with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution")
		.to(listScnReceiver).fire();
	}	
	
	public void findAllScn(){
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
			drawScenariosListTable(response);//DAG
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
				//call my own methods, which  will call ScenarioRequest w a sort option / method
				Collections.sort(response, scnTitleComparator);
				scnTitleComparator.switchOrder();
				drawScenariosListTable(response);
				//TODO About comparator, on another click,  change to a different comparator w/ the opposite sorting criteria?
				// this way we can sort on the inverse order on a second click
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
		searchResult.getRowFormatter().addStyleName(0, "userListHeader"); //TODO Style this table
		
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
			
			//TODO check if the user is superuser to allow delete scn?
			final int rowDel = row;
			Button deleteButton = new Button("Delete");
			deleteButton.addClickHandler(new ClickHandler() {
					
				@Override
				public void onClick(ClickEvent event) {
					//TODO Add a validation, so the user is asked if (s)he is sure about deleting the scn
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
	
	//-------------------------------------------------
	@UiHandler("searchQuery")
	public void onFocus(FocusEvent focusEvent) {
		searchQuery.setText("");
	}
	
	@UiHandler("submitButton")
	public void onClick(ClickEvent clickEvent) {
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
	
}
