package org.mdpnp.clinicalscenarios.client.user;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class UserInfoBanner extends Composite {

	private static UserInfoBannerUiBinder uiBinder = GWT
			.create(UserInfoBannerUiBinder.class);

	interface UserInfoBannerUiBinder extends UiBinder<Widget, UserInfoBanner> {
	}

	private static Command NOOP = new Command() {
		@Override
		public void execute() {
		}
	};
	public MenuItem getBasicSearch() {
		return basicSearch;
	}
	public MenuItem getAdvancedSearch(){
		return advancedSearch;
	}
	private MenuItem editProfile = new MenuItem("Edit User Profile", NOOP);//Constructs a new menu item that fires a command when it is selected.
	private MenuItem signOut = new MenuItem("Sign Out", NOOP);
	private MenuBar search = new MenuBar(true);

	private MenuItem listUsers = new MenuItem("List Users", NOOP);
	private MenuItem basicSearch = new MenuItem("Basic Search", NOOP);
	private MenuItem advancedSearch = new MenuItem("Advanced Search", NOOP);
	private MenuItem showLatestSearch = new MenuItem("Latest Search Results", NOOP);
	private MenuItem searchById = new MenuItem("Search Scenario by Id", NOOP);
	private MenuItem searchByDates= new MenuItem("Search Scenarios by Dates", NOOP);
	private MenuItem searchByTags= new MenuItem("Search Scenarios by Tags", NOOP);
	private MenuItem searchBySubmitter= new MenuItem("Search Scenarios by Submitter", NOOP);//Ticket-195
	
	private MenuItem listTags = new MenuItem("List Tags", NOOP);//list the tags
	//list scenarios
	private MenuBar listScenarios = new MenuBar(true);
	private MenuItem listAllScn = new MenuItem("List All Scenarios", NOOP);
	private MenuBar listScnByStatus = new MenuBar(true);
	
	private MenuItem scnUnsubmited = new MenuItem(ScenarioPanel.SCN_STATUS_UNSUBMITTED, NOOP);
	private MenuItem scnSubmited = new MenuItem(ScenarioPanel.SCN_STATUS_SUBMITTED, NOOP);
	private MenuItem scnApproved= new MenuItem(ScenarioPanel.SCN_STATUS_APPROVED, NOOP);
	private MenuItem scnModified = new MenuItem(ScenarioPanel.SCN_STATUS_MODIFIED, NOOP);
	private MenuItem scnRejected = new MenuItem(ScenarioPanel.SCN_STATUS_REJECTED, NOOP);  
	
	private MenuItem listMyScn = new MenuItem("My Scenarios", NOOP);//List Scn for registered users
	private MenuItem listApprvScn = new MenuItem("All Approved Scenarios", NOOP);//list of Approved Scn for Anonymous/registered users
	private MenuItem createNewScn = new MenuItem("Create New Scenario", NOOP);//Ticket-102 Must be independent in the menu bar
	private MenuItem sendFeedback = new MenuItem("Feedback", NOOP);
	
	private MenuItem goBackHome = new MenuItem("Go to Homepage", NOOP);//go back to home page
	
	private UserInfoProxy userInfo;
	
	private String userEmail;
//	private boolean isAdmin;//DAG
	
	public MenuItem getListTags(){
		return listTags;
	}
	
	public MenuItem getListUsers() {
		return listUsers;
	}
	public MenuItem getEditProfile() {
		return editProfile;
	}
	
	public UserInfoProxy getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfoProxy userInfo) {
		this.userInfo = userInfo;
	}
	public MenuItem getListMyScn() {
		return listMyScn;
	}
	public MenuItem getlistApprvScn(){
		return listApprvScn;
	}
	
	public MenuItem getListAllScenarios(){
		return listAllScn;
	}
	
//	public MenuItem getListScnByStatus(){
//		return listScnByStatus;
//	}
	
	public MenuItem getListScnUnsubmitted(){
		return scnUnsubmited;
	}
	public MenuItem getListScnSubmitted(){
		return scnSubmited;
	}
	public MenuItem getListScnApproved(){
		return scnApproved;
	}
	public MenuItem getListScnModified(){
		return scnModified;
	}
	public MenuItem getListScnRejected(){
		return scnRejected;
	}
	
	public MenuItem getCreateNewScenario(){
		return createNewScn;
	}
	public MenuItem getSendFeedback(){
		return sendFeedback;
	}
	public MenuItem getGoBackHome(){
		return goBackHome;
	}
	public MenuItem getShowLatestSearch(){
		return showLatestSearch;
	}
	public MenuItem getSearchById(){
		return searchById;
	}
	public MenuItem getSearchByDates(){
		return searchByDates;
	}
	public MenuItem getSearchByTags() {
		return searchByTags;
	}	
	public MenuItem getSearchBySubmitter() {
		return searchBySubmitter;
	}
	public void setSearchBySubmitter(MenuItem searchBySubmitter) {
		this.searchBySubmitter = searchBySubmitter;
	}
	//	XXX 07/22/13 diego@mdpnp.org Rejected is considered the same state as pending of submission
//	public MenuItem getListScnRejected(){
//		return scnRejected;
//	}
	public String getUserEmail(){
		return this.userEmail;
	}
	
	public interface NewUserHandler {
		void onNewUser(UserInfoProxy userInfo);
		void onAnyUser(UserInfoProxy userInfo);
	}
	
	public UserInfoBanner(final UserInfoRequestFactory userInfoRequestFactory, final NewUserHandler newUserHandler) {
		
		initWidget(uiBinder.createAndBindUi(this));
		username.setAutoOpen(true);
		search.setTitle("Search Scenarios");
		search.addItem(basicSearch);
		search.addItem(advancedSearch);
		searchById.setTitle("Find a scenario by its unique Id");
		search.addItem(searchById);
		//XXX SEarch by dates not yet - consider which date of the scenario to use
		searchByDates.setTitle("Find scenarios in a date range");
		search.addItem(searchByDates);
		searchByTags.setTitle("Find scenarios tagged with the selected keywords");
		search.addItem(searchByTags);
		showLatestSearch.setTitle("Retrieve the previous search results");
//		search.addItem(showLatestSearch); TODO Fix ticket-146 before allowing this functionality

		UserInfoRequest userInfoRequest = userInfoRequestFactory.userInfoRequest();
		userInfoRequest.findCurrentUserInfo(Window.Location.getHref(), true).with("loginURL").to(new Receiver<UserInfoProxy>() {

			@Override
			public void onSuccess(final UserInfoProxy response) {
				//DAG
				userEmail = response.getEmail();				
				UserInfoBanner.this.userInfo = response;
								
				username.addItem("Search Scenarios", search);
				
				if(null == response.getEmail()) {
					MenuBar signIn = new MenuBar(true);
//					System.out.println(""+response.getLoginURL());
					for(final LoginProviderProxy lpp : response.getLoginURL()) {
					    MenuItem mi = new MenuItem(lpp.getName(), new Command() {
					        @Override
					        public void execute() {
					            Window.Location.replace(lpp.getLoginURL());
					        }
					    });
					    signIn.addItem(mi);
					}
					
					username.addItem(listApprvScn);
					sendFeedback.setTitle("Give Feedback about the Repository");
					username.addItem(sendFeedback);
					username.addItem("Sign In", signIn);
					
				} else {
					if(response.getAdmin()) {//ADMIN
//						username.addItem(list);
						/**
						 * List scenarios
						 * + List all scenarios
						 * + list scenarios by status
						 * 		- unsubmitted
						 * 		- submitted
						 * 		- approved
						 * 		- rejected
						 * + Create New Scn
						 */
						listScenarios.setTitle("List Scenarios");
						listAllScn.setTitle("List all scenarios");
						listScenarios.addItem(listAllScn);
						listScnByStatus.setTitle("List Scenarios by Status");
						listScnByStatus.addItem(scnUnsubmited);
						scnUnsubmited.setTitle("Scenarios pending of submission");
						listScnByStatus.addItem(scnSubmited);
						scnSubmited.setTitle("Scenarios pendig of revision and approval");
						listScnByStatus.addItem(scnApproved);
						scnApproved.setTitle("List all approved scenarios");
						listScnByStatus.addItem(scnModified);
						scnModified.setTitle("List all post-approved modified scenarios");
						listScnByStatus.addItem(scnRejected);
						scnRejected.setTitle("List all rejected scenarios");

						listScenarios.addItem("List Scenarios by Status", listScnByStatus);
						listScenarios.addItem(listMyScn);
						listMyScn.setTitle("All scenarios created by this user");

						username.addItem("List Scenarios", listScenarios);
						username.addItem(listUsers);
						username.addItem(listTags);//add tag search
						
						searchBySubmitter.setTitle("Find scenarios by user");//TICKET-195
						search.addItem(searchBySubmitter);
					}else{//registered user (NOT ADMIN)
						listScenarios.setTitle("List Scenarios");
						listScenarios.addItem(listApprvScn);
						listApprvScn.setTitle("All Approved Scenarios");
						listScenarios.addItem(listMyScn);
						listMyScn.setTitle("All scenarios created by this user");

						username.addItem("List Scenarios", listScenarios);
					}
					//Ticket-102
					createNewScn.setTitle("Create a new Clinical Scenario");
					username.addItem(createNewScn);
					sendFeedback.setTitle("Give Feedback about the Repository");//XXX Move to Registered users (not admin)
					username.addItem(sendFeedback);
					
					MenuBar logoutMenu = new MenuBar(true);
					
					
					signOut.setScheduledCommand(new Command() {

						@Override
						public void execute() {
							Window.Location.replace(response.getLogoutURL());
						}
						
					});

					logoutMenu.addItem(editProfile);
					logoutMenu.addItem(goBackHome);
					logoutMenu.addItem(signOut);
					
					username.addItem(response.getEmail(), logoutMenu);
					if(null == response.getGivenName() && null != newUserHandler) {//TICKET-181 
						newUserHandler.onNewUser(response);//display userInfoPanel
					}
					
				}
				if(null != newUserHandler) {
					newUserHandler.onAnyUser(response);
				}
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
			}
			
		}).fire();

	}
	

	
	@UiField
	MenuBar username; 
}
