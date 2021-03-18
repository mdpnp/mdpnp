package org.mdpnp.sql;

public class DatabaseConnectionException extends Exception {

	/**
	 * Indicates an error with connection to database
	 */
	private static final long serialVersionUID = -2867692014693316419L;

	public DatabaseConnectionException() {
	}

	public DatabaseConnectionException(String message) {
		super(message);
	}
}
