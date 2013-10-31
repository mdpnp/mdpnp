package org.mdpnp.clinicalscenarios.client;

import java.util.HashSet;
import java.util.Set;

import org.mdpnp.clinicalscenarios.client.feedback.FeedbackProxy;
import org.mdpnp.clinicalscenarios.client.feedback.FeedbackRequestFactory;
import org.mdpnp.clinicalscenarios.client.feedback.UserFeedbackPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioRequestFactory;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioSearchPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioSearchPanel.SearchHandler;
import org.mdpnp.clinicalscenarios.client.tag.TagProxy;
import org.mdpnp.clinicalscenarios.client.tag.TagRequestFactory;
import org.mdpnp.clinicalscenarios.client.tag.TagsManagementPanel;
import org.mdpnp.clinicalscenarios.client.user.UserInfoBanner;
import org.mdpnp.clinicalscenarios.client.user.UserInfoBanner.NewUserHandler;
import org.mdpnp.clinicalscenarios.client.user.UserInfoPanel;
import org.mdpnp.clinicalscenarios.client.user.UserInfoProxy;
import org.mdpnp.clinicalscenarios.client.user.UserInfoRequestFactory;
import org.mdpnp.clinicalscenarios.client.user.UserInfoSearchPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class Clinical_scenarios implements EntryPoint, NewUserHandler, SearchHandler {

	private final UserInfoRequestFactory userInfoRequestFactory = GWT.create(UserInfoRequestFactory.class);
	private final ScenarioRequestFactory scenarioRequestFactory = GWT.create(ScenarioRequestFactory.class);
	private final TagRequestFactory tagRequestFactory = GWT.create(TagRequestFactory.class);
	private final FeedbackRequestFactory feedbackRequestFactory = GWT.create(FeedbackRequestFactory.class);
	
	private ScenarioPanel scenarioPanel;
	private UserInfoBanner userInfoBanner;
	private UserInfoPanel userInfoPanel;
	private UserInfoSearchPanel userInfoSearchPanel;
	private ScenarioSearchPanel scenarioSearchPanel;
	private ScenarioSearchPanel scenarioListPanel;
	private TagsManagementPanel tagsManagementPanel;
	private UserFeedbackPanel userFeedbackPanel;
	
	private Home homePanel = new Home();
	private DockPanel wholeApp = new DockPanel();
	private DeckPanel contents = new DeckPanel();
	
	private static final void showWidget(DeckPanel dp, Widget w) {
		int idx = dp.getWidgetIndex(w);
		dp.showWidget(idx);
	}
	
	private final void showWidget(Widget w) {
		showWidget(contents, w);
	}
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final EventBus eventBus = new SimpleEventBus();
		scenarioRequestFactory.initialize(eventBus);
		userInfoRequestFactory.initialize(eventBus);
		tagRequestFactory.initialize(eventBus);
		feedbackRequestFactory.initialize(eventBus);
		
		scenarioPanel = new ScenarioPanel(scenarioRequestFactory);
		
		scenarioSearchPanel = new ScenarioSearchPanel(scenarioRequestFactory);
		scenarioSearchPanel.setSearchHandler(this);
		scenarioListPanel = new ScenarioSearchPanel(scenarioRequestFactory);
		scenarioListPanel.setSearchHandler(this);
		userInfoSearchPanel = new UserInfoSearchPanel(userInfoRequestFactory);
		userInfoBanner = new UserInfoBanner(userInfoRequestFactory, this);
		
		
		tagsManagementPanel = new TagsManagementPanel(tagRequestFactory);// Tags
		tagsManagementPanel.setSaveHandler(new TagsManagementPanel.SaveHandler(){
			@Override
			public void onSave(TagProxy tagProxy) {
				tagsManagementPanel.setCurrentTag(tagProxy);
			}			
		});
		
		userFeedbackPanel = new UserFeedbackPanel(feedbackRequestFactory);//User feedback panel
		userFeedbackPanel.setSaveHandler(new UserFeedbackPanel.SaveHandler() {
			@Override
			public void onSave(FeedbackProxy feedbackProxy) {
				userFeedbackPanel.setCurrentFeedbackProxy(feedbackProxy);
				
			}
		});


		wholeApp.add(userInfoBanner, DockPanel.NORTH);
		wholeApp.add(contents, DockPanel.CENTER);
		
		contents.add(homePanel);
		
		contents.add(scenarioPanel);
		
		contents.add(userInfoSearchPanel);
		
		userInfoPanel = new UserInfoPanel(userInfoRequestFactory);
		userInfoPanel.setSaveHandler(new UserInfoPanel.SaveHandler() {

			@Override
			public void onSave(UserInfoProxy userInfo) {
				userInfoBanner.setUserInfo(userInfo);
				showWidget(homePanel);
			}
			
		});
		contents.add(userInfoPanel);
				
		userInfoBanner.getEditProfile().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				userInfoPanel.setUserInfo(userInfoBanner.getUserInfo());
				showWidget(userInfoPanel);
			}
			
		});
		userInfoBanner.getGoBackHome().setScheduledCommand(new Command(){
			public void execute() {
				showWidget(homePanel);
			}
		});
		
		
		userInfoBanner.getBasicSearch().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				scenarioSearchPanel.showBasicSearch();
				showWidget(scenarioSearchPanel);
			}
			
		});
//		scenarioListPanel.getSearchQuery().setText("");
//		scenarioListPanel.getSearchQuery().setVisible(false);
//		scenarioListPanel.getPleaseEnterKeywords().setVisible(false);
		scenarioListPanel.getHeader().setText("Scenario List");
//		scenarioListPanel.getSubmitButton().setVisible(false);
		scenarioSearchPanel.hideAllSearchPanels();
		
		
		userInfoBanner.getAdvancedSearch().setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				scenarioSearchPanel.showAdvancedSearch();
				showWidget(scenarioSearchPanel);	
				
			}
		});
		
		//Retrieve the results of the latest search
		userInfoBanner.getShowLatestSearch().setScheduledCommand(new Command() {
			@Override
			public void execute() {
				scenarioSearchPanel.showLatestSearch();
				showWidget(scenarioSearchPanel);	
				
			}
		});
		
		//find scn by Id
		userInfoBanner.getSearchById().setScheduledCommand(new Command() {
			@Override
			public void execute() {
				scenarioSearchPanel.showSearchById();
				showWidget(scenarioSearchPanel);	
				
			}
		});
		
		//find scenarios by date range
		userInfoBanner.getSearchByDates().setScheduledCommand(new Command() {
			@Override
			public void execute() {
				scenarioSearchPanel.showSearchByDates();
				showWidget(scenarioSearchPanel);	
				
			}
		});
		
		//find scenarios by tags
		userInfoBanner.getSearchByTags().setScheduledCommand(new Command() {
			@Override
			public void execute() {
				scenarioSearchPanel.showSearchByTags();
				showWidget(scenarioSearchPanel);	
				
			}
		});

		
//		userInfoBanner.getList().setScheduledCommand(new Command() {

//			@Override
//			public void execute() {
//				scenarioListPanel.doSearch("");
//				showWidget(scenarioListPanel);
//			}
//			
//		});
		
		//command to list all scn
		userInfoBanner.getListAllScenarios().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				scenarioListPanel.findAllScn();
				showWidget(scenarioListPanel);
			}
		});
		
		//commands to list scn by status
		userInfoBanner.getListScnSubmitted().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				Set<String> status = new HashSet<String>();
				status.add(ScenarioPanel.SCN_STATUS_SUBMITTED);
				status.add(ScenarioPanel.SCN_STATUS_UNLOCKED_PRE);
				scenarioListPanel.listScnByStatus(status);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getListScnUnsubmitted().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				scenarioListPanel.listScnByStatus(ScenarioPanel.SCN_STATUS_UNSUBMITTED);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getListScnApproved().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				//XXX should this look for approved only or also unlocked_post and dirty???
				// Does not make much sense to have the re-approval process if we let users see "dirty" scenarios
				scenarioListPanel.listScnByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getListScnModified().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				Set<String> status = new HashSet<String>();
				status.add(ScenarioPanel.SCN_STATUS_MODIFIED);
				status.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
				scenarioListPanel.listScnByStatus(status);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getListScnRejected().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				scenarioListPanel.listScnByStatus(ScenarioPanel.SCN_STATUS_REJECTED);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getListMyScn().setScheduledCommand(new Command(){
			@Override
			public void execute() {
				scenarioListPanel.listScnBySubmitter(userInfoBanner.getUserEmail());
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getlistApprvScn().setScheduledCommand(new Command() {			
			@Override
			public void execute() {
//				Set<String> status = new HashSet<String>();
//				status.add(ScenarioPanel.SCN_STATUS_MODIFIED);
//				status.add(ScenarioPanel.SCN_STATUS_UNLOCKED_POST);
//				status.add(ScenarioPanel.SCN_STATUS_APPROVED);
//				scenarioListPanel.listScnByStatus(status);
				//XXX should this look for approved only or also unlocked_post and dirty???
				// Does not make much sense to have the re-approval process if we let users see "dirty" scenarios
				scenarioListPanel.listScnByStatus(ScenarioPanel.SCN_STATUS_APPROVED);
				showWidget(scenarioListPanel);
			}
		});
		userInfoBanner.getCreateNewScenario().setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				scenarioPanel.selectFirstTab();
				scenarioPanel.setCurrentScenario(null);
				showWidget(scenarioPanel);
			}
		});
		
		contents.add(scenarioSearchPanel);
		contents.add(scenarioListPanel);
		contents.add(userInfoPanel);
		contents.add(tagsManagementPanel);
		contents.add(userFeedbackPanel);

		userInfoBanner.getListUsers().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				userInfoSearchPanel.fetchUsersList();//TICKET-98
				showWidget(userInfoSearchPanel);
			}
			
		});
		
		userInfoBanner.getListTags().setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				tagsManagementPanel.drawTagTable();
				showWidget(tagsManagementPanel);
				
			}
		});
		
		//User Feedback Button Panel
		userInfoBanner.getSendFeedback().setScheduledCommand(new Command() {
			
			@Override
			public void execute() {
				String userEmail = userInfoBanner.getUserEmail();
				if(null!=userEmail && !userEmail.trim().equals(""))
					userFeedbackPanel.setUserEmail(userEmail);
				else
					userFeedbackPanel.setUserEmail("Anonymous user");
				userFeedbackPanel.initialize();
				showWidget(userFeedbackPanel);	
			}
		});
		
		showWidget(homePanel);
		RootPanel.get().add(wholeApp);
	}

	@Override
	public void onNewUser(UserInfoProxy userInfo) {
		userInfoPanel.setUserInfo(userInfo);
		showWidget(userInfoPanel);		
	}

	@Override
	public void onSearchResult(ScenarioProxy sp) {
		scenarioPanel.selectFirstTab();
		scenarioPanel.cleanStatusLabel();
		scenarioPanel.setCurrentScenario(sp);
		showWidget(scenarioPanel);
	}

	@Override
	public void onAnyUser(UserInfoProxy userInfo) {
		scenarioSearchPanel.setUserInfo(userInfo);
	}
}
