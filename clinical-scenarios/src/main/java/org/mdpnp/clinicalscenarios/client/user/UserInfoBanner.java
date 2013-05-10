package org.mdpnp.clinicalscenarios.client.user;

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
	private MenuItem editProfile = new MenuItem("Edit User Profile", NOOP);
	private MenuItem signOut = new MenuItem("Sign Out", NOOP);
	private MenuBar search = new MenuBar(true);
	private MenuItem list = new MenuItem("List Scenarios", NOOP);
	private MenuItem listUsers = new MenuItem("List Users", NOOP);
	private MenuItem basicSearch = new MenuItem("Basic Search", NOOP);
	private MenuItem advancedSearch = new MenuItem("Advanced Search", NOOP);
	
	private UserInfoProxy userInfo;
	
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
	public MenuItem getList() {
		return list;
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
		
		UserInfoRequest userInfoRequest = userInfoRequestFactory.userInfoRequest();
		userInfoRequest.findCurrentUserInfo(Window.Location.getHref()).to(new Receiver<UserInfoProxy>() {

			@Override
			public void onSuccess(final UserInfoProxy response) {
				
				UserInfoBanner.this.userInfo = response;
				
				
				username.addItem("Search Scenarios", search);
				
				
				if(null == response.getEmail()) {
					MenuItem signIn = new MenuItem("Sign In", new Command() {

						@Override
						public void execute() {
							Window.Location.replace(response.getLoginURL());
						}
						
					});
					username.addItem(signIn);
				} else {
					if(response.getAdmin()) {
						username.addItem(list);
						username.addItem(listUsers);
					}
					MenuBar logoutMenu = new MenuBar(true);
					
					
					signOut.setScheduledCommand(new Command() {

						@Override
						public void execute() {
							Window.Location.replace(response.getLogoutURL());
						}
						
					});

					logoutMenu.addItem(editProfile);
					logoutMenu.addItem(signOut);
					username.addItem(response.getEmail(), logoutMenu);
					if(null == response.getGivenName() && null != newUserHandler) {
						newUserHandler.onNewUser(response);
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
