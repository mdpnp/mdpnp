/*******************************************************************************
 * Copyright (c) 2017, MD PnP Program
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.IllegalFormatCodePointException;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		@Override
		public void start(Stage primaryStage) throws Exception {
			runConf = Configuration.searchAndLoadSettings(searchPath);

			ConfigurationDialog d = ConfigurationDialog.showDialog(runConf, this);

			// It's nice to be able to change settings even without running
			// Even if the user presses 'quit' save the state so that it can be used
			// to boot strap the dialog later.
			//
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
							DialogUtils.ExceptionDialog("Click OK to terminate application", ex);

						// Any exception here would kill the FX thread - there is no
						// point in attempting to recover as the state of the app is unknown.
						// Just exit out of the VM.
						//
						System.exit(-1);
					}
				} else {
					throw new IllegalStateException("Invalid FX application request " + o);
				}
			}
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
