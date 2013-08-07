package org.mdpnp.clinicalscenarios.client.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class UserInfoPanel extends Composite implements Editor<UserInfoProxy> {

	private static UserInfoPanelUiBinder uiBinder = GWT
			.create(UserInfoPanelUiBinder.class);

	interface UserInfoPanelUiBinder extends UiBinder<Widget, UserInfoPanel> {
	}
	interface Driver extends RequestFactoryEditorDriver<UserInfoProxy, UserInfoPanel> {
		
	}
	Driver driver = GWT.create(Driver.class);
	UserInfoRequestFactory userInfoRequestFactory;
	UserInfoProxy currentUserInfo;
	
	public UserInfoPanel(final UserInfoRequestFactory userInfoRequestFactory) {
		this.userInfoRequestFactory = userInfoRequestFactory;
		initWidget(uiBinder.createAndBindUi(this));
		driver.initialize(userInfoRequestFactory, this);
	}
	public interface SaveHandler {
		void onSave(UserInfoProxy userInfo);
	}
	SaveHandler saveHandler;
	
	public void setSaveHandler(SaveHandler saveHandler) {
		this.saveHandler = saveHandler;
	}
	public void setUserInfo(UserInfoProxy userInfo) {
//		Window.alert(userInfo.getEmail());
		UserInfoRequest context = userInfoRequestFactory.userInfoRequest();
		userInfo = context.edit(userInfo); 
		driver.edit(userInfo, context);
		this.currentUserInfo = userInfo;
		
		// TODO This pains me
		emailEditor.setText(userInfo.getEmail());
	}
	@UiField
	Label emailEditor;
	@UiField
	TextBox titleEditor;
	@UiField
	TextBox givenNameEditor;
	@UiField
	TextBox familyNameEditor;
	@UiField
	TextBox companyEditor;
	@UiField
	TextBox jobTitleEditor;
	@UiField
	TextBox yearsInFieldEditor;
	@UiField
	TextBox phoneNumberEditor;	
	@UiField	
	Button saveButton;
	
	@UiHandler("saveButton")
	void onClick(ClickEvent e) {
		UserInfoRequest uir = (UserInfoRequest) driver.flush();
		uir.persist().using(currentUserInfo).with(driver.getPaths()).to(new Receiver<UserInfoProxy>() {

			@Override
			public void onSuccess(UserInfoProxy response) {
				saveHandler.onSave(response);
				currentUserInfo = response;
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
			}
			
		}).fire();
	}
}
