package org.mdpnp.clinicalscenarios.client.user;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class UserInfoSearchPanel extends Composite {

	private static UserInfoSearchPanelUiBinder uiBinder = GWT
			.create(UserInfoSearchPanelUiBinder.class);

	interface UserInfoSearchPanelUiBinder extends
			UiBinder<Widget, UserInfoSearchPanel> {
	}

	@UiField
	FlexTable list;
	
	private static final String[] headers = new String[] {
		"Email", "Title", "Given Name", "Family Name", "Company",
		"Job Title", "Years In Field", "Phone Number"};

	
	
	public UserInfoSearchPanel(UserInfoRequestFactory userInfoRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		UserInfoRequest uir = userInfoRequestFactory.userInfoRequest();
		
		list.insertRow(0);
		for(int j = 0; j < headers.length; j++) {
			
			list.setText(0, j, headers[j]);
		}
		
		
		uir.findAllUserInfo().to(new Receiver<List<UserInfoProxy>>() {
			@Override
			public void onSuccess(List<UserInfoProxy> response) {
				
				for(int i = 0; i < response.size(); i++) {
					list.insertRow(i + 1);
					UserInfoProxy u = response.get(i);
					list.setText(1 + i, 0, u.getEmail());
					list.setText(1 + i, 1, u.getTitle());
					list.setText(1 + i,  2, u.getGivenName());
					list.setText(1 + i, 3, u.getFamilyName());
					list.setText(1 + i, 4, u.getCompany());
					list.setText(1 + i, 5, u.getJobTitle());
					list.setText(1 + i, 6, u.getYearsInField());
					list.setText(1 + i, 7, u.getPhoneNumber());
				}
				
			}
			@Override
			public void onFailure(ServerFailure error) {
				super.onFailure(error);
				Window.alert(error.getMessage());
			}
		}).fire();
	}

}
