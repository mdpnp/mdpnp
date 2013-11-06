package org.mdpnp.clinicalscenarios.client.feedback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class UserFeedbackPanel extends Composite implements Editor<FeedbackProxy> {

	interface UserFeedbackPanelUiBinder extends UiBinder<Widget, UserFeedbackPanel> {}
	
	private static UserFeedbackPanelUiBinder uiBinder = GWT.create(UserFeedbackPanelUiBinder.class);

	interface Driver extends RequestFactoryEditorDriver<FeedbackProxy, UserFeedbackPanel> {}

	Driver driver = GWT.create(Driver.class);
	private FeedbackRequestFactory feedbackRequestFactory;
	private FeedbackProxy currentFeedbackEntity;
	
	String userEmail;//user email or Id
	
	public UserFeedbackPanel(FeedbackRequestFactory feedbackRequestFactory){
		this.feedbackRequestFactory = feedbackRequestFactory;
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(feedbackRequestFactory, this);
	}
	
	public void initialize(){	
		feedbackRequestFactory.feedbackRequest().create()
//		.with(driver.getPaths())
		.fire(new Receiver<FeedbackProxy>(){

			@Override
			public void onSuccess(FeedbackProxy response) {
				currentFeedbackEntity = response;		
				FeedbackRequest request = feedbackRequestFactory.feedbackRequest();
				response = request.edit(response);
				driver.edit(response, request);
				currentFeedbackEntity = response;
				if(userEmail != null)
					currentFeedbackEntity.setUsersEmail(userEmail);
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				Window.alert("Failed to create FeedbackProxy ... "+ error.getMessage());
				super.onFailure(error);
			}
			
		});
	}
	
	//save handler
	public interface SaveHandler {
		void onSave(FeedbackProxy feedbackProxy);
		void onSendFeedback();
	}
	SaveHandler saveHandler;

	public void setSaveHandler(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
	}
	
	public void setCurrentFeedbackProxy(FeedbackProxy currentFeedbackEntity){
		this.currentFeedbackEntity = currentFeedbackEntity;
	}
	
	public void setUserEmail(String userEmail){
		this.userEmail = userEmail;
	}
	
	@UiField
	TextArea navigationOkEditor;
	
	@UiField
	TextArea logicallyOrganizedEditor;
	
	@UiField
	TextArea troubleLoginInEditor;
	
	@UiField
	TextArea unclearQuestionsEditor;
	
	@UiField
	TextArea missingFieldsEditor;
	
	@UiField
	TextArea usefulIfDepartmentAvailableEditor;
	
	@UiField
	TextArea websiteLooksProfessionalEditor;
	
	@UiField
	TextArea rateThisWebsiteEditor;
	
	@UiField
	TextArea goodVisualDesignEditor;
	
	@UiField
	TextArea generalSuggestionsEditor;
		
	@UiField
	@Ignore
	Button sendFeedbackButton;
	
	@UiHandler("sendFeedbackButton")
	public void onClickSendFeedback(ClickEvent clickEvent) {
		//validation to warn the user that (s)he is about to send empty feedback 
		if(isEmpty()){
			Window.alert("Please, provide some feedback.");
			return;
		}
		
		FeedbackRequest request = this.feedbackRequestFactory.feedbackRequest();
		
		request = (FeedbackRequest) driver.flush();//update the object with the current state of the editor
		request.persist().using(currentFeedbackEntity).with(driver.getPaths())
		.fire(new Receiver<FeedbackProxy>(){
			@Override
			public void onSuccess(FeedbackProxy response) {
				saveHandler.onSave(response);
				Window.alert("Thank you. We appreciate your feedback and your help to improve this repository.");
				saveHandler.onSendFeedback();//redirect user to home panel
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				Window.alert("Failed to persist FeedbackProxy ... "+ error.getMessage());
				super.onFailure(error);
			}
		});
	}
	
	/**
	 * Indicates if the Feedback proxy is empty (no information on any of its fields)
	 * @return
	 */
	private boolean isEmpty(){
		if(!navigationOkEditor.getText().trim().equals("")) return false;
		if(!logicallyOrganizedEditor.getText().trim().equals("")) return false;
		if(!troubleLoginInEditor.getText().trim().equals("")) return false;
		if(!unclearQuestionsEditor.getText().trim().equals("")) return false;
		if(!missingFieldsEditor.getText().trim().equals("")) return false;
		
		if(!usefulIfDepartmentAvailableEditor.getText().trim().equals("")) return false;
		if(!websiteLooksProfessionalEditor.getText().trim().equals("")) return false;
		if(!rateThisWebsiteEditor.getText().trim().equals("")) return false;
		if(!goodVisualDesignEditor.getText().trim().equals("")) return false;
		if(!generalSuggestionsEditor.getText().trim().equals("")) return false;
		
		return true;
	}
}
