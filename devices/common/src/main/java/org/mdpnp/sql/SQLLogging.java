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
	private static final String JDBC_PROPS_FILE_NAME="icejdbc.properties";
	
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
		Properties jdbcProps=new Properties();
        try {
        	
        	jdbcProps.load(new FileReader(new File(System.getProperty("user.home"),JDBC_PROPS_FILE_NAME)));
        	String driverClass=jdbcProps.getProperty("driver");
        	//Class.forName(driverClass);
        	
        	String url=jdbcProps.getProperty("url");
        	String username=jdbcProps.getProperty("username");
        	String password=jdbcProps.getProperty("password");
        	dbconn = DriverManager.getConnection(url, username, password);
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

}
