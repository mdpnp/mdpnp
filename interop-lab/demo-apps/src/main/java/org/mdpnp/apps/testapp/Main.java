/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Jeff Plourde
 * 
 */
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private final static File[] searchPath = new File[] { new File(".JumpStartSettings"),
			new File(System.getProperty("user.home"), ".JumpStartSettings") };

	public static void main(final String[] args) throws Exception {

		loadSystemProps();

		Configuration runConf;
		if (args.length > 0) {
			runConf = Configuration.read(args);

			if (null == runConf) {
				return;
			} else {
				Configuration.searchAndSaveSettings(runConf, searchPath);
			}
			Configuration.HeadlessCommand cmd = runConf.getCommand();
			int retCode = cmd.execute(runConf);
			log.info("This is the end, exit code=" + retCode);
			System.exit(retCode);

		} else {
			// check for RFID readers
			if (!CardReader.findTerminals()) {
				System.out.println("No Card Terminals Found. Connect a Terminal and Restart "
						+ "or Enter Username and Password Manually to Proceed.");
			}
			javafx.application.Application.launch(Main.FxApplication.class, args);

			Platform.exit();
			log.info("This is the end, exit code=" + 0);
			System.exit(0);
		}
	}

	static void loadSystemProps() throws IOException {
		URL u = Main.class.getResource("/ice.system.properties");
		if (u != null) {
			log.info("Loading base system configuration from " + u.toExternalForm());
			InputStream is = u.openStream();
			System.getProperties().load(is);
			is.close();
		}
		File f = new File("ice.system.properties");
		if (f.exists()) {
			log.info("Loading user overrides configuration from " + f.getAbsolutePath());
			InputStream is = new FileInputStream(f);
			System.getProperties().load(is);
			is.close();
		}
	}

	public static class FxApplication extends javafx.application.Application {

		private Configuration runConf;
		private IceApplication app;
		private ConfigurationDialog d;

		private void show() {
			try {
				runConf = Configuration.searchAndLoadSettings(searchPath);
				d = ConfigurationDialog.showDialog(runConf, this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
			// this is kind of janky, but it'll prove the concept... definitely try to fix it up a bit
			
			primaryStage.setTitle("Login Screen");
			GridPane grid = new GridPane();

			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25, 25, 25, 25));

			final Text scenetitle = new Text("");
			scenetitle.setId("login-text");
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userName = new Label("Username:");
			grid.add(userName, 0, 1);

			final TextField userTextField = new TextField();
			grid.add(userTextField, 1, 1);

			Label pw = new Label("Password:");
			grid.add(pw, 0, 2);

			final PasswordField pwBox = new PasswordField();
			grid.add(pwBox, 1, 2);

			Button btn = new Button("Sign-in");
			HBox hbBtn = new HBox(10);
			hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
			hbBtn.getChildren().add(btn);
			grid.add(hbBtn, 1, 4);
			// btn.defaultButtonProperty().bind(btn.focusedProperty());
			btn.setDefaultButton(true);

			final Text actiontarget = new Text();
			grid.add(actiontarget, 1, 6);

			Scene scene = new Scene(grid, 450, 300);
			primaryStage.setScene(scene);
			// URL css = Login.class.getResource("Login.css");
			// String css = Login.class.getResource("Login.css").toExternalForm();
			// scene.getStylesheets().add(css.getPath());
			scene.getStylesheets().addAll(this.getClass().getResource("Login.css").toExternalForm());
			primaryStage.show();

			new Thread(() -> {

				CardReader.Reader();
				if (CardReader.getResponse() != null) {
					scenetitle.setText("Welcome!");
					scenetitle.setId("welcome-text");
					userTextField.setText("badge");
					pwBox.setText("badge");
				}
			}).start();

			final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					// String[] args = {};

					// new Thread() {
					// @Override
					// public void run() {
					// try {
					// Platform.exit();
					// // System.exit(0);
					// // Main.main(args);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// }
					// }.start();

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								try {

									// It's nice to be able to change settings even without running
									// Even if the user presses 'quit' save the state so that it can be used
									// to boot strap the dialog later.
									if (d.getQuitPressed()) {
										Configuration c = d.getLastConfiguration();
										Configuration.searchAndSaveSettings(c, searchPath);
										runConf = null;
									} else {
										runConf = d.getLastConfiguration();
										Object o = runConf.getApplication().getAppClass().newInstance();

										if (o instanceof Configuration.GUICommand) {
											o = ((Configuration.GUICommand) o).create(runConf);
										}

										if (o instanceof IceApplication) {
											app = (IceApplication) o;

											try {
												app.setConfiguration(runConf);
												app.init();
												app.start(primaryStage);
											} catch (Throwable ex) {

												log.error("Failed to start application", ex);

												ex = unwind(ex, ControlFlowHandler.ConfirmedError.class);
												if (!(ex instanceof ControlFlowHandler.ConfirmedError))
													DialogUtils.ExceptionDialog("Click OK to terminate application",
															ex);

												// Any exception here would kill the FX thread - there is no
												// point in attempting to recover as the state of the app is unknown.
												// Just exit out of the VM.

												System.exit(-1);
											}
										} else {
											throw new IllegalStateException("Invalid FX application request " + o);
										}
									}
								} catch (Exception e) {
									System.out.println("this didn't work...");
									e.printStackTrace();
								}
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					});
				}
			}));

			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					final String usernameText = userTextField.getText();
					final String passwordText = pwBox.getText();
					if (!usernameText.isEmpty() && !passwordText.isEmpty()) {
						scenetitle.setText("Welcome!");
						scenetitle.setId("welcome-text");
						scene.getWindow().hide();
						show();
						timeline.play();
						// String[] args = {};

					} else {
						scenetitle.setText("Invalid");
						scenetitle.setId("reject-text");
					}

				}
			});

		}

		@Override
		public void stop() throws Exception {
			super.stop();
			if (null != app) {
				app.stop();
				app = null;
			}
		}
	}

	private static Throwable unwind(Throwable t, Class<? extends Throwable> clazz) {

		while (!clazz.isAssignableFrom(t.getClass()) && t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}
}
