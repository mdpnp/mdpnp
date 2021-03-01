package org.mdpnp.apps.testapp;

import java.awt.Component;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

public class LockScreenController {
	
	private IceApplication app;
	
	@FXML TextField username;
	@FXML PasswordField password;
	@FXML GridPane loginPane;
	@FXML GridPane otherUserPane;
	@FXML Button killRunningApps;
	@FXML Button keepRunningApps;
	@FXML Button unlockButton;
	@FXML Label differentUserLabel;
	
	public void setWhatToUnlock(IceApplication whatToUnlock) {
		app=whatToUnlock;
	}
	
	private void hide(Node...nodes) {
		for(Node n : nodes) {
			n.setManaged(false);
			n.setVisible(false);
		}
	}
	
	private void show(Node...nodes) {
		for(Node n : nodes) {
			n.setManaged(true);
			n.setVisible(true);
		}
	}
	
	/**
	 * Hide the "other user" gridpane, and show the login one.
	 */
	public void showLogin() {
		hide(otherUserPane,killRunningApps,keepRunningApps);
		show(loginPane,unlockButton);
	}
	
	public void showOtherUser() {
		hide(loginPane,unlockButton);
		show(otherUserPane,killRunningApps,keepRunningApps);
	}
	
	public void clickUnlock() {
		System.err.println("Need to unlock...");
		String u=username.getText();
		String p=password.getText();
		String previousUser=ICELogin.getCurrentUser();
		
		if( ICELogin.login(u, p)) {
			
			
			if( ! previousUser.equals(u)) {
				differentUserLabel.setText("You are unlocking as "+u+", not as "+previousUser+" who logged in.");
				showOtherUser();
			} else {
				unlockWithKeep();
			}

		} else {
			Alert loginFailed=new Alert(AlertType.ERROR,"Logging in failed",new ButtonType[] {ButtonType.OK});
    		loginFailed.showAndWait();
		}
	}
	
	public void unlockWithKill() {
		app.unlockScreenWithKill();
	}
	
	public void unlockWithKeep() {
		app.unlockScreen();
	}

}
