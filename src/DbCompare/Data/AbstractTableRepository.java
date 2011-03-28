package DbCompare.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import DbCompare.Engine.DbCompareEngine;
import DbCompare.Model.AppConstants;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DbDefinition;
import DbCompare.Model.DbTable;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.DbTableRecord;
import DbCompare.Model.Utils;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

public abstract class AbstractTableRepository implements ITableRepository {

	private static final int HASH_MULTIPLIER = 31;

	Connection dbConnectionBaseline = null;
	Connection dbConnectionTarget = null;
	Statement stmt = null;
	ResultSet rs = null;
	String currentSourceDB = null;
	
	private static Logger logger = Logger.getLogger(DbCompareEngine.class);

	public List<DbTable> LoadContent(ConfigurationDefinition configDefinition) {

		List<DbTable> allTables = new ArrayList<DbTable>();
		DbTable currentTable = null;

		for (DbTableDefinition tableDefinition : configDefinition
				.getTableDefinitions()) {
			logger.info("Loading records from table " + tableDefinition.getTableName());
			System.out.print("Loading records from table " + tableDefinition.getTableName() + " .... ");

			currentTable = new DbTable(tableDefinition);
			currentTable
					.set_tableBaselineContent(getRecords(
							AppConstants.CONN_STRING_BASELINE_DB,
							configDefinition.getDatabaseDefinition(),
							tableDefinition));

			currentTable
					.set_tableTargetContent(getRecords(
							AppConstants.CONN_STRING_TARGET_DB,
							configDefinition.getDatabaseDefinition(),
							tableDefinition));

			allTables.add(currentTable);
			System.out.println("DONE");
		}

		for (DbTableDefinition tableDefinition : configDefinition
				.getTableDefinitions()) {
			currentTable = new DbTable(tableDefinition);
			currentTable
					.set_tableTargetContent(getRecords(
							AppConstants.CONN_STRING_TARGET_DB,
							configDefinition.getDatabaseDefinition(),
							tableDefinition));
		}

		if (null != dbConnectionBaseline)
			try {
				dbConnectionBaseline.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(ex));
				logger.info("The application has exited...");
				System.exit(0);	
			} finally {
				if (dbConnectionBaseline != null)
					try {
						dbConnectionBaseline.close();
					} catch (Exception e) {
					}
			}

		if (null != dbConnectionTarget)
			try {
				dbConnectionTarget.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(ex));
				logger.info("The application has exited...");
				System.exit(0);	
			} finally {
				if (dbConnectionTarget != null)
					try {
						dbConnectionTarget.close();
					} catch (Exception e) {
					}
			}

		return allTables;
	}

	protected abstract Connection getDbConnection(DbDefinition dbDefinition,
			String sDatabaseSource);

	private List<DbTableRecord> getRecords(String sourceDB,
			DbDefinition dbDefinition, DbTableDefinition tableDefinition) {

		List<DbTableRecord> allRecords = new ArrayList<DbTableRecord>();

		if (sourceDB.equals(AppConstants.CONN_STRING_BASELINE_DB)) {
			try {
				if (null == dbConnectionBaseline
						|| dbConnectionBaseline.isClosed()) {
					dbConnectionBaseline = getDbConnection(dbDefinition,
							AppConstants.CONN_STRING_BASELINE_DB);
				}

				allRecords = getRecordsFromDb(dbConnectionBaseline,
						tableDefinition);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(ex));
				logger.info("The application has exited...");
				System.exit(0);				
			}
		} else {
			try {
				if (null == dbConnectionTarget || dbConnectionTarget.isClosed()) {
					dbConnectionTarget = getDbConnection(dbDefinition,
							AppConstants.CONN_STRING_TARGET_DB);
				}
				allRecords = getRecordsFromDb(dbConnectionTarget,
						tableDefinition);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(ex));
				logger.info("The application has exited...");
				System.exit(0);	
			}
		}
		return allRecords;
	}

	private List<DbTableRecord> getRecordsFromDb(Connection dbConnection,
			DbTableDefinition tableDefinition) {

		List<DbTableRecord> allRecords = new ArrayList<DbTableRecord>();
		DbTableRecord currentRecord = null;
		try {
			// Create and execute an SQL statement that returns some data.
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(tableDefinition.getSqlQuery());
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				currentRecord = new DbTableRecord();
				int pkHashCode = this.getClass().hashCode();

				String[] recordPkValues = new String[tableDefinition
						.getPkColumns().size()];
				int pkColumnIndex = 0;
				for (String pkColumn : tableDefinition.getPkColumns()) {
					String columnValue = rs.getString(pkColumn);
					pkHashCode = (pkHashCode * HASH_MULTIPLIER)
							^ columnValue.hashCode();

					recordPkValues[pkColumnIndex++] = columnValue;
				}
				currentRecord.set_primaryKey(Integer.toString(pkHashCode));
				currentRecord.set_primaryKeys(recordPkValues);

				int columnIndex = 0;
				String[] recordValues = new String[tableDefinition
						.getTableColumns().size()];

				for (String columnName : tableDefinition.getTableColumns()) {
					String columnValue = rs.getString(columnName);
					recordValues[columnIndex++] = columnValue;
				}
				currentRecord.set_values(recordValues);
				allRecords.add(currentRecord);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.fatal(Utils.buildExceptionMessage(ex));
			logger.info("The application has exited...");
			System.exit(0);	
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
		}
		return allRecords;
	}

}
