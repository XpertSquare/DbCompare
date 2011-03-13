package DbCompare.Data.Oracle;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



import DbCompare.Model.AppConstants;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DbTable;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.DbTableRecord;
import DbCompare.Model.ITableRepository;

public class OracleTableRepository implements ITableRepository {

	private static final int HASH_MULTIPLIER = 31;

	Connection dbConnectionBaseline = null;
	Connection dbConnectionTarget = null;
	Statement stmt = null;
	ResultSet rs = null;
	String currentSourceDB = null;

	public DbTable LoadTableContent(DbTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DbTable> LoadContent(ConfigurationDefinition configDefinition) {

		List<DbTable> allTables = new ArrayList<DbTable>();
		DbTable currentTable = null;

		for (DbTableDefinition tableDefinition : configDefinition
				.get_tableDefinitions()) {
			currentTable = new DbTable(tableDefinition);
			currentTable.set_tableBaselineContent(getRecords(
					AppConstants.CONN_STRING_BASELINE_DB, configDefinition
							.get_databaseDefinition()
							.get_connStringBaselineDb(), tableDefinition));
			
			currentTable.set_tableTargetContent(getRecords(
					AppConstants.CONN_STRING_TARGET_DB, configDefinition
							.get_databaseDefinition()
							.get_connStringTargetDb(), tableDefinition));
			
			allTables.add(currentTable);
		}

		for (DbTableDefinition tableDefinition : configDefinition
				.get_tableDefinitions()) {
			currentTable = new DbTable(tableDefinition);
			currentTable.set_tableTargetContent(getRecords(
					AppConstants.CONN_STRING_TARGET_DB, configDefinition
							.get_databaseDefinition().get_connStringTargetDb(),
					tableDefinition));
		}

		if (null != dbConnectionBaseline)
			try {
				dbConnectionBaseline.close();
			} catch (Exception e) {
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
				} catch (Exception e) {
				} finally {
					if (dbConnectionTarget != null)
						try {
							dbConnectionTarget.close();
						} catch (Exception e) {
						}
				}

		return allTables;
	}

	private List<DbTableRecord> getRecords(String sourceDB,
			String connectionString, DbTableDefinition tableDefinition) {

		List<DbTableRecord> allRecords = new ArrayList<DbTableRecord>();

		if (sourceDB.equals(AppConstants.CONN_STRING_BASELINE_DB)) {
			try {
				if (null == dbConnectionBaseline
						|| dbConnectionBaseline.isClosed()) {
					// Establish the connection.
					Class.forName("oracle.jdbc.driver.OracleDriver");
					dbConnectionBaseline = DriverManager
							.getConnection(connectionString);
				}

				allRecords = getRecordsFromDb(dbConnectionBaseline,
						tableDefinition);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				if (null == dbConnectionTarget || dbConnectionTarget.isClosed()) {
					// Establish the connection.
					Class.forName("oracle.jdbc.driver.OracleDriver");
					dbConnectionTarget = DriverManager
							.getConnection(connectionString);
				}
				allRecords = getRecordsFromDb(dbConnectionTarget,
						tableDefinition);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// TODO Auto-generated method stub
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
			// TODO: to finish loading the data from DB
			while (rs.next()) {
				currentRecord = new DbTableRecord();
				int pkHashCode = this.getClass().hashCode();
				for (String pkColumn : tableDefinition.getPkColumns()) {
					String columnValue = rs.getString(pkColumn);
					pkHashCode = (pkHashCode * HASH_MULTIPLIER)
							^ columnValue.hashCode();
				}
				currentRecord.set_primaryKey(Integer.toString(pkHashCode));

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
		} catch (Exception e) {
			e.printStackTrace();
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
