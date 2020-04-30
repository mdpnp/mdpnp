package org.mdpnp.sql;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLLogging {
	
	private static Connection dbconn;
	private static PreparedStatement logStatement;
	private static boolean init=false;
	private static boolean placebo=false;
	private static final String JDBC_PROPS_FILE_NAME="icejdbc.properties";
	private static Properties jdbcProps;
	
	/**
	 * In case we have problems logging to the database, we'll log them here...
	 */
	private static final Logger log = LoggerFactory.getLogger(SQLLogging.class);
	
	public static void log(String className,String msg) {
		if(!init) init();
		try {
			logStatement.setString(1, className);
			logStatement.setString(2, msg);
			logStatement.execute();
		} catch (SQLException sqle) {
			log.warn("Failed to perform SQL Log", sqle);
		}
	}
	
	private static void init() {
		jdbcProps=new Properties();
        try {
        	String userHome=System.getProperty("user.home");
        	File propsFile=new File(userHome,JDBC_PROPS_FILE_NAME);
        	if(!propsFile.exists() || !propsFile.canRead()) {
        		log.warn("No "+JDBC_PROPS_FILE_NAME+" in "+userHome );
        		dbconn=new PlaceboConnection();
        		placebo=true;
        	} else {    	
	        	jdbcProps.load(new FileReader(propsFile));
	        	
	        	String url=jdbcProps.getProperty("url");
	        	String username=jdbcProps.getProperty("username");
	        	String password=jdbcProps.getProperty("password");
	        	dbconn = DriverManager.getConnection(url, username, password);
        	}
        	logStatement=dbconn.prepareStatement("INSERT INTO devicelogs(sourceclass,eventtext) VALUES (?,?)");
            init=true;
            
        } catch (FileNotFoundException fnfe) {
            log.warn("No JDBC properties file found",fnfe);
        } catch (IOException ioe) {
			log.warn("Could not read JDBC properties file", ioe);
		} catch (SQLException e) {
			log.warn("Could not connect to database - server probably not running",e);
		}
	}

	/**
	 * A single place to obtain a connection, that means we don't need to have JDBC client
	 * jar files all over the codebase.  It is the callers job to maintain the state of this
	 * connection, and in particular, to close it when it's finished with.
	 *  
	 * @return a JDBC connection object, or null if a connection cannot be created.
	 */
	public static Connection getConnection() {
		if(!init) init();	//Populate the properties
		if(placebo) {
			return new PlaceboConnection();
		}
		try {
			String url=jdbcProps.getProperty("url");
			String username=jdbcProps.getProperty("username");
			String password=jdbcProps.getProperty("password");
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException sqle) {
			log.error("Failed to create database connection", sqle);
			return null;
		}
	}

}
