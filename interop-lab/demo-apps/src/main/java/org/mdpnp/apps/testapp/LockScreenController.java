package org.mdpnp.apps.testapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class LockScreenController {
	
	private IceApplication app;
	
	@FXML TextField username;
	@FXML PasswordField password;
	
	public void setWhatToUnlock(IceApplication whatToUnlock) {
		app=whatToUnlock;
	}
	
	public void clickUnlock() {
		System.err.println("Need to unlock...");
		String u=username.getText();
		String p=password.getText();
		if( ICELogin.login(u, p)) {
			app.unlockScreen();
		} else {
			Alert loginFailed=new Alert(AlertType.ERROR,"Logging in failed",new ButtonType[] {ButtonType.OK});
    		loginFailed.showAndWait();
		}
	}

}
