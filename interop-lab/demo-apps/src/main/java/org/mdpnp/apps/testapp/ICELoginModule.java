package org.mdpnp.apps.testapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.mdpnp.sql.SQLLogging;

public class ICELoginModule implements LoginModule {
	
	private CallbackHandler handler;
	
	private Map<String, ?> options;
	
	private boolean didWeLogin;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.handler=callbackHandler;
		this.options=options;
	}

	@Override
	public boolean login() throws LoginException {
		try {
			boolean debug=new Boolean((String)options.get("debug")).booleanValue();
			if(debug) {
				System.err.println("ICELoginModule.login begins");
			}
			String osUser=System.getProperty("user.name");
			NameCallback nameCallback=new NameCallback("Please enter the username", osUser);
			PasswordCallback passCallback=new PasswordCallback("Please enter the password", true);
			handler.handle(new Callback[] {nameCallback, passCallback});
			if(debug) {
				System.err.println("ICELoginModule.login username is "+nameCallback.getName()+" with password[0] of "+passCallback.getPassword()[0]);
			}
			Connection c=SQLLogging.getConnection();
			if(c==null) {
				throw new NullPointerException("Could not connect to database");
			}
			PreparedStatement ps=c.prepareStatement("SELECT password FROM logins WHERE username=?");
			ps.setString(1, nameCallback.getName());
			ps.execute();
			ResultSet rs=ps.getResultSet();
			if( ! rs.next() ) {
				if(debug) {
					System.err.println("ICELoginModule.login got no rows from database query");
				}
				throw new LoginException("User does not exist");
			}
			String dbPassword=rs.getString(1);
			if(dbPassword.equals(new String(passCallback.getPassword()))) {
				didWeLogin=true;
				return true;
			}
			throw new LoginException("Authentication failed");
		} catch (SQLException e) {
			LoginException le=new LoginException(e.getMessage());
			le.initCause(e);
			throw le;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;	//We should not be ignored.
	}

	@Override
	public boolean commit() throws LoginException {
		if(didWeLogin) {
			//Associate something on the subject?
		}
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
	}

}
