package org.mdpnp.clinicalscenarios.client.scenario;

import java.util.List;

import org.mdpnp.clinicalscenarios.client.user.UserInfoProxy;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class ScenarioSearchPanel extends Composite {
	
	private static int SCN_TITLE_COL = 0;
	private static int SCN_SUBMITTER_COL = 1;
	private static int SCN_STATUS_COL = 2;
	private static int SCN_DELETEBUTTON_COL = 4;
	
	private static ScenarioSearchPanelUiBinder uiBinder = GWT
			.create(ScenarioSearchPanelUiBinder.class);

	interface ScenarioSearchPanelUiBinder extends
			UiBinder<Widget, ScenarioSearchPanel> {
	}
	private ScenarioRequestFactory scenarioRequestFactory;
	public ScenarioSearchPanel(ScenarioRequestFactory scenarioRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.scenarioRequestFactory = scenarioRequestFactory;
	}
	@UiField
	TextBox searchQuery;
	
	@UiField
	FlexTable searchResult;
	
	@UiField
	Label header;
	
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
		scenarioRequest.searchByKeywords(text).with("background", "benefitsAndRisks", "environments", "equipment", "hazards", "proposedSolution").to(new Receiver<List<ScenarioProxy>>() {

			@Override
			public void onSuccess(List<ScenarioProxy> response) {
			    searchResult.removeAllRows();
//				int i = 1;
				//HEADER
				searchResult.insertRow(0);
				searchResult.setText(0, SCN_TITLE_COL, "Title");
				searchResult.setText(0, SCN_SUBMITTER_COL, "Submitter");
				searchResult.setText(0, SCN_STATUS_COL, "State");
				searchResult.getRowFormatter().addStyleName(0, "userListHeader"); //TODO Style this table
				
				int row =1;
				for(final ScenarioProxy sp : response) {
					
					Label lbl = new Label();
					lbl.setStyleName("clickable");
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
					searchResult.setWidget(row, SCN_DELETEBUTTON_COL, deleteButton);
					
					//style table rows
					if(sp.getStatus()!=null)
					if(sp.getStatus().equals(ScenarioPanel.SCN_STATUS_SUBMITTED))
						searchResult.getRowFormatter().addStyleName(row, "submittedScn");
					else
						searchResult.getRowFormatter().addStyleName(row, "unsubmittedScn");
			        
			        //increase row number (the FOR loop is not increasing our row index variable, which is also final)
					row+=1;	
					

				}
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
			}
		}).fire();
		
	}
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
		
		if(null != searchHandler) {
			searchHandler.onSearchResult(null);
		}
		
	}
	
}
