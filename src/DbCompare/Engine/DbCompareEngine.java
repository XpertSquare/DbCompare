package DbCompare.Engine;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import DbCompare.Data.SqlServer.SqlServerTableRepository;
import DbCompare.Data.Xml.XmlConfigurationRepository;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DatabaseType;
import DbCompare.Model.DbTable;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.DbTableRecord;
import DbCompare.Model.IConfigurationRepository;
import DbCompare.Model.ITableRepository;
import DbCompare.Model.RecordStatus;

public class DbCompareEngine {

	static Logger logger = Logger.getLogger(DbCompareEngine.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DOMConfigurator.configure("log4j.config");

		logger.info("App started...");

		IConfigurationRepository configRepository = new XmlConfigurationRepository();
		ITableRepository tableRepository =null;;

		// DbDefinition databaseDefinition =
		// configRepository.getDatabaseDefinitions();

		ConfigurationDefinition configDefinition = configRepository
				.getConfigurationDefinition();

		if(configDefinition.get_databaseDefinition().get_dbType()==DatabaseType.SqlServer2008)
		{
			tableRepository = new SqlServerTableRepository();
		}
		
		List<DbTable> allTables = tableRepository.LoadContent(configDefinition);

		

		

		runComparison(allTables);

		for (DbTable currentTable : allTables) {
			DbTableDefinition definition = currentTable.get_tableDefinition();
			System.out.println("Table: " + definition.getTableName());
			StringBuilder tableColumns = null;

			/*
			 * for(String pk:definition.get_pkColumns()) {
			 * if(null==tableColumns) { tableColumns = new StringBuilder(pk); }
			 * else { tableColumns = tableColumns.append("  | "); tableColumns =
			 * tableColumns.append(pk); } }
			 */

			for (String columnName : definition.getTableColumns()) {
				if (null == tableColumns) {
					tableColumns = new StringBuilder(columnName);
				} else {
					tableColumns = tableColumns.append("  | ");
					tableColumns = tableColumns.append(columnName);
				}
			}

			System.out.println(tableColumns);

			System.out.println();
			System.out.println("Baseline values");

			for (DbTableRecord record : currentTable.get_tableBaselineContent()) {
				System.out.print(record.get_status().name() + " : ");
				for (String columnValue : record.get_values()) {
					System.out.print(columnValue + "  | ");
				}
				System.out.println();
			}

			System.out.println();
			System.out.println("Target values");

			for (DbTableRecord record : currentTable.get_tableTargetContent()) {
				System.out.print(record.get_status().name() + " : ");
				for (String columnValue : record.get_values()) {
					System.out.print(columnValue + "  | ");
				}
				System.out.println();
			}

		}

	}

	public static void runComparison(List<DbTable> allTables) {
		for (DbTable table : allTables) {
			for (DbTableRecord baselineRecord : table
					.get_tableBaselineContent()) {
				String primaryKey = baselineRecord.get_primaryKey();
				for (DbTableRecord targetRecord : table
						.get_tableTargetContent()) {
					String targetPrimarykey = targetRecord.get_primaryKey();
					if (primaryKey.equals(targetPrimarykey)) {
						if (isEqual(baselineRecord, targetRecord)) {
							baselineRecord.set_status(RecordStatus.Identical);
							targetRecord.set_status(RecordStatus.Identical);
						} else {
							baselineRecord.set_status(RecordStatus.Changed);
							targetRecord.set_status(RecordStatus.Changed);
						}
						break;
					}
				}
			}
		}
	}

	private static boolean isEqual(DbTableRecord baselineRecord,
			DbTableRecord targetRecord) {
		boolean isRecordEqual = true;

		String[] baselineRecordValues = baselineRecord.get_values();
		String[] targetRecordValues = targetRecord.get_values();
		for (int columnIndex = 0; columnIndex < baselineRecordValues.length; columnIndex++) {
			if(baselineRecordValues[columnIndex]==null)
			{
				if(targetRecordValues[columnIndex]==null)
				{
					continue;
				}
				else
				{
					isRecordEqual = false;
					break;
				}
			}
			if (!baselineRecordValues[columnIndex]
					.equals(targetRecordValues[columnIndex])) {
				isRecordEqual = false;
				break;
			}
		}
		return isRecordEqual;
	}
}
