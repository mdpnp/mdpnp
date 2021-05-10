package org.mdpnp.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.regex.Pattern;

import org.mdpnp.devices.io.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDatabaseValidator {
	private static final String FILE_ENDING = ".sql";

	private static final Logger log = LoggerFactory.getLogger(SQLDatabaseValidator.class);
	private static final String fileSeparator = System.getProperty("file.separator");

	private static Collection<String> getSQLSchemaFilePaths() {
		Pattern pattern = Pattern
				.compile(".*" + "\\" + fileSeparator + "schema\\" + fileSeparator + ".*\\" + FILE_ENDING);
		return ResourceUtil.getResources(pattern);
	}

	private static String getTableNameFromResourcePath(String path) {
		String[] pathComponents = path.split(escape(fileSeparator));
		final String fileName = pathComponents != null && pathComponents.length > 0
				? pathComponents[pathComponents.length - 1]
				: "";
		final int fileNameLength = fileName.length();
		return fileNameLength >= FILE_ENDING.length() + 1 ? fileName.substring(0, fileNameLength - FILE_ENDING.length())
				: null;
	}

	private static String escape(String input) {
		if(input.charAt(0)=='\\') {
			return "\\\\";
		}
		return input;
	}

	private static boolean tableExists(String tableName) throws SQLException, DatabaseConnectionException {
		boolean tableExists = false;
		Connection connection = SQLLogging.getConnection();
		if (connection == null) {
			throw new DatabaseConnectionException("Database connection unavailable");
		}
		try (ResultSet result = connection.getMetaData().getTables(null, null, tableName, new String[] { "TABLE" })) {
			while (result.next()) {
				String resultTableName = result.getString("TABLE_NAME");
				if (resultTableName != null && resultTableName.equals(tableName)) {
					tableExists = true;
					break;
				}
			}
		}
		connection.close();
		return tableExists;
	}

	public static void validate() {
		Collection<String> results = getSQLSchemaFilePaths();
		for (String result : results) {
			final String tableNameFromResourcePath = getTableNameFromResourcePath(result);
			boolean tableExists = false;
			try {
				tableExists = tableExists(tableNameFromResourcePath);
			} catch (SQLException e) {
				System.out.println(tableNameFromResourcePath + "\n" + e.getMessage());
				log.error(e.getMessage());
			} catch (DatabaseConnectionException e) {
				System.out.println(e.getMessage());
				log.error(e.getMessage());
				return;
			}
			if (!tableExists) {
				log.debug("Table: " + tableNameFromResourcePath + " does not exist, executing create statements");
				String sqlStatement = null;
				try {
					sqlStatement = new String(Files.readAllBytes(Paths.get(result)));
				} catch (IOException e) {
					System.out.println(result + "\n" + e.getMessage());
					log.error(e.getMessage());
				}
				log.trace(tableNameFromResourcePath + " - Executing Statement: " + sqlStatement);
				try {
					SQLLogging.getConnection().createStatement().execute(sqlStatement);
				} catch (SQLException e) {
					System.out.println(sqlStatement + "\n" + e.getMessage());
					log.error(e.getMessage());
				}
			} else {
				log.debug("Table: " + tableNameFromResourcePath + " exists, skipping table creation");
			}
		}
	}
}
