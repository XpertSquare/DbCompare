package DbCompare.Engine;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import DbCompare.Data.ITableRepository;
import DbCompare.Data.Oracle.OracleTableRepository;
import DbCompare.Data.SqlServer.SqlServerTableRepository;
import DbCompare.Data.Xml.XmlConfigurationRepository;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DatabaseType;
import DbCompare.Model.DbTable;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.DbTableRecord;
import DbCompare.Model.IConfigurationRepository;
import DbCompare.Model.RecordStatus;
import DbCompare.Model.Utils;

public class DbCompareEngine {

	private static Logger logger = Logger.getLogger(DbCompareEngine.class);
	private static String REPORT_DIRECTORY_NAME ="ComparisonReport";
	private boolean isRunning = false;
	Object lock = new Object();
	
	private static DbCompareEngine singletonObject;

	private DbCompareEngine() {
		//	 Optional Code
	}
	public static synchronized DbCompareEngine getDatabaseComparionEngine() {
		if (singletonObject == null) {
			singletonObject = new DbCompareEngine();
		}
		return singletonObject;
	}
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void runComparison() {
		
		
		synchronized (lock) {
			if (isRunning) {
				logger.info("The comparison is running!");
				return;
			} 
			isRunning = true;
		}

		IConfigurationRepository configRepository = new XmlConfigurationRepository();
		ITableRepository tableRepository = null;

		// DbDefinition databaseDefinition =
		// configRepository.getDatabaseDefinitions();

		ConfigurationDefinition configDefinition = configRepository
				.getConfigurationDefinition();

		if (configDefinition.get_databaseDefinition().get_dbType() == DatabaseType.SqlServer2008) {
			tableRepository = new SqlServerTableRepository();
		}

		if (configDefinition.get_databaseDefinition().get_dbType() == DatabaseType.Oracle) {
			tableRepository = new OracleTableRepository();
		}

		List<DbTable> allTables = tableRepository.LoadContent(configDefinition);

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
							table.setChanged(true);
							baselineRecord.set_status(RecordStatus.Changed);
							targetRecord.set_status(RecordStatus.Changed);
						}
						break;
					}
				}
			}
		}

		// Create directory for writing
		String directoryName = null;

		try {
			String DATE_FORMAT = "yyyyMMdd_HHmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Calendar c1 = Calendar.getInstance();
			directoryName = REPORT_DIRECTORY_NAME + "_"
					+ sdf.format(c1.getTime());

			boolean success = (new File(directoryName)).mkdir();
			if (success) {
				logger.info("The directory " + directoryName + " was created");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal(Utils.buildExceptionMessage(e));
			logger.info("The application has exited...");
			System.exit(0);
		}

		for (DbTable currentTable : allTables) {

			DbTableDefinition definition = currentTable.get_tableDefinition();

			try {
				String filename = null;
				if (null != directoryName) {
					filename = directoryName + File.separator
							+ definition.getTableName() + ".log";
				} else {
					filename = definition.getTableName() + ".log";
				}

				// Create file
				FileWriter fstream = new FileWriter(filename);
				PrintWriter writer = new PrintWriter(fstream);
				writer.println("Table: " + definition.getTableName());

				System.out.println("Table: " + definition.getTableName());
				StringBuilder tableColumns = null;

				for (String pk : definition.getPkColumns()) {
					if (null == tableColumns) {
						tableColumns = new StringBuilder(pk);
					} else {
						tableColumns = tableColumns.append("  | ");
						tableColumns = tableColumns.append(pk);
					}
				}

				for (String columnName : definition.getTableColumns()) {
					if (null == tableColumns) {
						tableColumns = new StringBuilder(columnName);
					} else {
						tableColumns = tableColumns.append("  | ");
						tableColumns = tableColumns.append(columnName);
					}
				}

				writer.println(tableColumns);

				writer.println();
				writer.println("Baseline values");

				for (DbTableRecord record : currentTable
						.get_tableBaselineContent()) {
					writer.print(record.get_status().name() + " : ");
					for (String columnValue : record.get_values()) {
						writer.print(columnValue + "  | ");
					}
					writer.println();
				}

				writer.println();
				writer.println("Target values");

				for (DbTableRecord record : currentTable
						.get_tableTargetContent()) {
					writer.print(record.get_status().name() + " : ");
					for (String columnValue : record.get_values()) {
						writer.print(columnValue + "  | ");
					}
					writer.println();
				}

				writer.flush();
				writer.close();
			} catch (Exception e) {// Catch exception if any
				e.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(e));
				logger.info("The application has exited...");
				System.exit(0);
			}
			isRunning = false;
		}
	}

	private boolean isEqual(DbTableRecord baselineRecord,
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
