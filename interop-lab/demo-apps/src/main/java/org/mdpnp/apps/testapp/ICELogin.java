package org.mdpnp.apps.testapp;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * This class handles login, authentication etc. for OpenICE.
 * @author simon
 *
 */

//https://docs.oracle.com/javase/8/docs/technotes/guides/security/jaas/tutorials/GeneralAcnOnly.html


/*public*/ class ICELogin {
	
	private static String currentUser;
	
	/**
	 * Get the currently logged in user, or null if nobody is logged in
	 * @return The current username in the system
	 */
	public static String getCurrentUser() {
		return currentUser;
	}

	static boolean trivialLogin(String username, String password) {
		if(!username.equals("x") || !password.equals("y")) {
			return false;
		}
		return true;
	}
	
	static boolean login(String username, String password) {
		try {
			LoginContext loginContext=new LoginContext("icelogin",
					new CallbackHandler() {

						@Override
						public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
							for(int i=0;i<callbacks.length;i++) {
								if(callbacks[i] instanceof NameCallback) {
									System.err.println("Setting username in callback to "+username);
									((NameCallback)callbacks[i]).setName(username);
								}
								else if(callbacks[i] instanceof PasswordCallback) {
									System.err.println("Setting password in callback to "+password);
									((PasswordCallback)callbacks[i]).setPassword(password.toCharArray());
								}
								else if(callbacks[i] instanceof TextInputCallback) {
									TextInputCallback tic=(TextInputCallback)callbacks[i];
									String prompt=tic.getPrompt();
									System.err.println("We got prompted with "+prompt);
								} else {
									throw new RuntimeException("Unhandled callback type "+callbacks[i].getClass().getName());
								}
							}
							
						}
				
			});
			loginContext.login();
			currentUser=username;
			return true;
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	static void logout() {
		//Log the action...
		currentUser=null;
	}
	

}
