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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import DbCompare.Data.ITableRepository;
import DbCompare.Data.Oracle.OracleTableRepository;
import DbCompare.Data.SqlServer.SqlServerTableRepository;
import DbCompare.Data.Xml.XmlConfigurationRepository;
import DbCompare.Model.AppConstants;
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
	private static String REPORT_DIRECTORY_NAME = "ComparisonReport";
	private static String STATUS_COLUMN_NAME = "STATUS";

	private boolean isRunning = false;
	Object lock = new Object();

	private static DbCompareEngine singletonObject;

	private DbCompareEngine() {
		// Optional Code
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
				

				if (currentTable.isChanged()) {
					

					System.out.println("Table: " + definition.getTableName());
					// Record status will be the first column in the grid.
					int headerColumnCount = 1
							+ definition.getPkColumns().size()
							+ definition.getTableColumns().size();
					int[] columnSize = new int[headerColumnCount];
					List<String[]> tableBaselineContents = new ArrayList<String[]>();

					String[] header = new String[headerColumnCount];
					int headerIndex = 0;

					header[headerIndex] = STATUS_COLUMN_NAME;
					columnSize[headerIndex] = STATUS_COLUMN_NAME.length();

					headerIndex++;

					for (String pk : definition.getPkColumns()) {
						header[headerIndex] = pk;
						if (columnSize[headerIndex] < pk.length()) {
							columnSize[headerIndex] = pk.length();
						}
						headerIndex++;
					}

					for (String columnName : definition.getTableColumns()) {
						header[headerIndex] = columnName;
						if (columnSize[headerIndex] < columnName.length()) {
							columnSize[headerIndex] = columnName.length();
						}
						headerIndex++;
					}

					tableBaselineContents.add(header);

					for (DbTableRecord record : currentTable
							.get_tableBaselineContent()) {
						if (record.get_status() != RecordStatus.Identical) {

							String[] recordToAdd = new String[headerColumnCount];

							int columnIndex = 0;
							recordToAdd[columnIndex] = record.get_status()
									.name();
							if (columnSize[columnIndex] < record.get_status()
									.name().length()) {
								columnSize[columnIndex] = record.get_status()
										.name().length();
							}

							columnIndex++;

							for (String pkColumnValue : record
									.get_primaryKeys()) {
								recordToAdd[columnIndex] = pkColumnValue;
								if (columnSize[columnIndex] < pkColumnValue
										.length()) {
									columnSize[columnIndex] = pkColumnValue
											.length();
								}
								columnIndex++;
							}

							for (String columnValue : record.get_values()) {
								if (null == columnValue)
									columnValue = "null";
								recordToAdd[columnIndex] = columnValue;

								if (columnSize[columnIndex] < columnValue
										.length()) {
									columnSize[columnIndex] = columnValue
											.length();
								}
								columnIndex++;
							}
							tableBaselineContents.add(recordToAdd);

						}

					}
					
					List<String[]> tableTargetContents = new ArrayList<String[]>();
					tableTargetContents.add(header);

					for (DbTableRecord record : currentTable
							.get_tableTargetContent()) {
						if (record.get_status() != RecordStatus.Identical) {

							String[] recordToAdd = new String[headerColumnCount];

							int columnIndex = 0;
							recordToAdd[columnIndex] = record.get_status()
									.name();
							if (columnSize[columnIndex] < record.get_status()
									.name().length()) {
								columnSize[columnIndex] = record.get_status()
										.name().length();
							}

							columnIndex++;

							for (String pkColumnValue : record
									.get_primaryKeys()) {
								recordToAdd[columnIndex] = pkColumnValue;
								if (columnSize[columnIndex] < pkColumnValue
										.length()) {
									columnSize[columnIndex] = pkColumnValue
											.length();
								}
								columnIndex++;
							}

							for (String columnValue : record.get_values()) {
								if (null == columnValue)
									columnValue = "null";
								recordToAdd[columnIndex] = columnValue;

								if (columnSize[columnIndex] < columnValue
										.length()) {
									columnSize[columnIndex] = columnValue
											.length();
								}
								columnIndex++;
							}
							tableTargetContents.add(recordToAdd);
						}
					}
					
					
					String filename = null;
					if (null != directoryName) {
						filename = directoryName + File.separator
								+ definition.getTableName() + "_Baseline.log";
					} else {
						filename = definition.getTableName() + "_Baseline.log";
					}

					FileWriter fstream = new FileWriter(filename);
					PrintWriter writer = new PrintWriter(fstream);				
					
					writer.println("Baseline: " + configDefinition.get_databaseDefinition().getBaselineEnvironment());
					writer.println();
					printTableValues(writer, tableBaselineContents, columnSize);
					
					writer.flush();
					writer.close();
					
					if (null != directoryName) {
						filename = directoryName + File.separator
								+ definition.getTableName() + "_Target.log";
					} else {
						filename = definition.getTableName() + "_Target.log";
					}

					// Create file
					fstream = new FileWriter(filename);
					writer = new PrintWriter(fstream);
					
					writer.println("Target: " + configDefinition.get_databaseDefinition().getTargetEnvironment());
					writer.println();
					printTableValues(writer, tableTargetContents, columnSize);
					
					writer.flush();
					writer.close();
					
				}
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
			if (baselineRecordValues[columnIndex] == null) {
				if (targetRecordValues[columnIndex] == null) {
					continue;
				} else {
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

	private void printTableValues(PrintWriter writer,
			List<String[]> baselineRecords, int[] columnSize) {
		for (String[] record : baselineRecords) {
			writer.println(formatRecordForPrint(record, columnSize));
		}
	}

	private String formatRecordForPrint(String[] record, int[] columnSize) {
		StringBuilder sbRecord = new StringBuilder();
		sbRecord = sbRecord.append(AppConstants.COLUMN_SEPARATOR);

		for (int columnIndex = 0; columnIndex < record.length; columnIndex++) {
			sbRecord = sbRecord.append(fillRecordWithSpaces(
					record[columnIndex], columnSize[columnIndex]));
			sbRecord = sbRecord.append(AppConstants.COLUMN_SEPARATOR);
		}

		return sbRecord.toString();
	}

	private String fillRecordWithSpaces(String columnValue, int columnLength) {
		StringBuilder sbColumn = new StringBuilder(" ");
		sbColumn.append(columnValue);

		for (int i = 0; i < columnLength - columnValue.length() + 1; i++) {
			sbColumn = sbColumn.append(AppConstants.COLUMN_SPACING);
		}

		return sbColumn.toString();
	}
}
