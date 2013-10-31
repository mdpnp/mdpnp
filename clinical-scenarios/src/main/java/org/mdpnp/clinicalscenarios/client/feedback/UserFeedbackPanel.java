package org.mdpnp.clinicalscenarios.client.feedback;

import org.mdpnp.clinicalscenarios.client.tag.TagProxy;
import org.mdpnp.clinicalscenarios.client.tag.TagsManagementPanel.SaveHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.Editor.Path;
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
		feedbackEditor.setText("");
		feedbackRequestFactory.feedbackRequest().create().fire(new Receiver<FeedbackProxy>(){

			@Override
			public void onSuccess(FeedbackProxy response) {
				currentFeedbackEntity = response;				
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
	}

	// setter
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
	@Ignore
	TextArea feedbackEditor;
	
	
	@UiField
	@Ignore
	Button sendFeedbackButton;
	
	@UiHandler("sendFeedbackButton")
	public void onClickSendFeedback(ClickEvent clickEvent) {
		FeedbackRequest request = this.feedbackRequestFactory.feedbackRequest();
		currentFeedbackEntity = request.edit(currentFeedbackEntity);
		//XXX make sure that the feedback is not null
		currentFeedbackEntity.setUsersFeedback(feedbackEditor.getText());
		if(userEmail != null)
			currentFeedbackEntity.setUsersEmail(userEmail);
		request.persist().using(currentFeedbackEntity).fire(new Receiver<FeedbackProxy>(){
			@Override
			public void onSuccess(FeedbackProxy response) {
				initialize();
				Window.alert("Thank you!");
				//XXX ??? Redirect user to another screen?
			}
			
			@Override
			public void onFailure(ServerFailure error) {
				Window.alert("Failed to persist FeedbackProxy ... "+ error.getMessage());
				super.onFailure(error);
			}
		});
	}
}
