package org.mdpnp.clinicalscenarios.client;

import org.mdpnp.clinicalscenarios.client.scenario.ScenarioPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioProxy;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioRequestFactory;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioSearchPanel;
import org.mdpnp.clinicalscenarios.client.scenario.ScenarioSearchPanel.SearchHandler;
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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class Clinical_scenarios implements EntryPoint, NewUserHandler, SearchHandler {

	private final UserInfoRequestFactory userInfoRequestFactory = GWT.create(UserInfoRequestFactory.class);
	private final ScenarioRequestFactory scenarioRequestFactory = GWT.create(ScenarioRequestFactory.class);
	private ScenarioPanel scenarioPanel;
	private UserInfoBanner userInfoBanner;
	private UserInfoPanel userInfoPanel;
	private UserInfoSearchPanel userInfoSearchPanel;
	private ScenarioSearchPanel scenarioSearchPanel;
	private ScenarioSearchPanel scenarioListPanel;
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
		scenarioPanel = new ScenarioPanel(scenarioRequestFactory);
		scenarioSearchPanel = new ScenarioSearchPanel(scenarioRequestFactory);
		scenarioSearchPanel.setSearchHandler(this);
		scenarioListPanel = new ScenarioSearchPanel(scenarioRequestFactory);
		scenarioListPanel.setSearchHandler(this);
		userInfoSearchPanel = new UserInfoSearchPanel(userInfoRequestFactory);
		userInfoBanner = new UserInfoBanner(userInfoRequestFactory, this);
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
		
		
		userInfoBanner.getBasicSearch().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				showWidget(scenarioSearchPanel);
			}
			
		});
		scenarioListPanel.getSearchQuery().setText("");
		scenarioListPanel.getSearchQuery().setVisible(false);
		scenarioListPanel.getHeader().setText("List Scenarios");
		scenarioListPanel.getSubmitButton().setVisible(false);
		
		userInfoBanner.getList().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				scenarioListPanel.doSearch("");
				showWidget(scenarioListPanel);
			}
			
		});
		contents.add(scenarioSearchPanel);
		contents.add(scenarioListPanel);
		contents.add(userInfoPanel);

		userInfoBanner.getListUsers().setScheduledCommand(new Command() {

			@Override
			public void execute() {
				showWidget(userInfoSearchPanel);
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
		scenarioPanel.setCurrentScenario(sp);
		showWidget(scenarioPanel);
	}

	@Override
	public void onAnyUser(UserInfoProxy userInfo) {
		scenarioSearchPanel.setUserInfo(userInfo);
	}
}
