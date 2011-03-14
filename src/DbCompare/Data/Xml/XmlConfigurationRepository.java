package DbCompare.Data.Xml;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import DbCompare.Model.AppConstants;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DatabaseType;
import DbCompare.Model.DbDefinition;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.IConfigurationRepository;
import DbCompare.Model.InvalidConfigurationException;
import DbCompare.Model.Utils;


public class XmlConfigurationRepository implements IConfigurationRepository {

	private static final String APP_CONFIG_XML = "appConfig.xml";

	private static final String ELEMENT_NAME_ATTR = "name";
	private static final String ELEMENT_VALUE_ATTR = "value";
	private static final String TABLE_NODE_NAME = "Table";
	private static final String COLUMN_NODE_NAME = "Column";
	private static final String PKCOLUMN_NODE_NAME = "PrimaryKeyColumn";
	private static final String DATABASE_NODE_NAME = "Database";
	private static final String DATABASE_TYPE_ATTR = "type";
	private static final String CONNECTION_STRING_NODE_NAME = "ConnectionString";

	private static Logger logger = Logger
			.getLogger(XmlConfigurationRepository.class);

	private ConfigurationDefinition configDefinition = null;

	public ConfigurationDefinition getConfigurationDefinition() {
		if (null == configDefinition) {
			configDefinition = new ConfigurationDefinition();

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				File file = new File(APP_CONFIG_XML);
				if (file.exists()) {
					Document doc = db.parse(file);
					Element docRootElement = doc.getDocumentElement();

					configDefinition
							.set_databaseDefinition(getDatabaseDefinitions(docRootElement));
					configDefinition
							.set_tableDefinitions(getTableDefinitions(docRootElement));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.fatal(Utils.buildExceptionMessage(ex));
				logger.info("The application has exited...");
				System.exit(0);
			}

		}

		return configDefinition;
	}

	private List<DbTableDefinition> getTableDefinitions(
			Element documentRootElement) {

		DbTableDefinition definition = null;
		List<DbTableDefinition> tableDefinitions = null;

		try {

			NodeList tableList = documentRootElement
					.getElementsByTagName(TABLE_NODE_NAME);

			if (tableList != null && tableList.getLength() > 0) {
				tableDefinitions = new ArrayList<DbTableDefinition>();

				for (int tableIndex = 0; tableIndex < tableList.getLength(); tableIndex++) {
					definition = new DbTableDefinition();
					Node tableNode = tableList.item(tableIndex);
					String tableName = tableNode.getAttributes()
							.getNamedItem(ELEMENT_NAME_ATTR).getNodeValue();
					definition.setTableName(tableName);

					if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) tableNode;
						// Table primary key columns
						NodeList pkColumnList = e
								.getElementsByTagName(PKCOLUMN_NODE_NAME);
						if (pkColumnList != null
								&& pkColumnList.getLength() > 0) {
							List<String> tablePkColumns = new ArrayList<String>();
							for (int i = 0; i < pkColumnList.getLength(); i++) {
								Node columnNode = pkColumnList.item(i);
								String pkColumnName = columnNode
										.getAttributes()
										.getNamedItem(ELEMENT_NAME_ATTR)
										.getNodeValue();
								tablePkColumns.add(pkColumnName);
							}
							definition.setPkColumns(tablePkColumns);
						}

						// Table columns
						NodeList columnList = e
								.getElementsByTagName(COLUMN_NODE_NAME);
						if (columnList != null && columnList.getLength() > 0) {
							List<String> tableColumns = new ArrayList<String>();
							for (int i = 0; i < columnList.getLength(); i++) {
								Node columnNode = columnList.item(i);
								String columnName = columnNode.getAttributes()
										.getNamedItem(ELEMENT_NAME_ATTR)
										.getNodeValue();
								tableColumns.add(columnName);
							}
							definition.setTableColumns(tableColumns);
						}
					}
					generateSQLStatement(definition);
					tableDefinitions.add(definition);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.fatal(Utils.buildExceptionMessage(ex));
			logger.info("The application has exited...");
			System.exit(0);
		}
		return tableDefinitions;
	}

	private DbDefinition getDatabaseDefinitions(Element documentRootElement) {
		DbDefinition databaseDefinition = null;
		try {
			NodeList databaseList = documentRootElement
					.getElementsByTagName(DATABASE_NODE_NAME);

			if (databaseList != null && databaseList.getLength() > 0) {
				databaseDefinition = new DbDefinition();

				Node databaseNode = databaseList.item(0);
				Node databaseTypeNode = databaseNode.getAttributes()
						.getNamedItem(DATABASE_TYPE_ATTR);

				String databaseTypeName = null;
				if (null != databaseTypeNode) {
					databaseTypeName = databaseTypeNode.getNodeValue();
				} else {
					throw new InvalidConfigurationException(
							"The database type was not set in the configuration file!");
				}

				if (databaseTypeName.equalsIgnoreCase(DatabaseType.Oracle
						.toString()))
					databaseDefinition.set_dbType(DatabaseType.Oracle);

				if (databaseTypeName
						.equalsIgnoreCase(DatabaseType.SqlServer2008.toString()))
					databaseDefinition.set_dbType(DatabaseType.SqlServer2008);

				if (databaseDefinition.get_dbType() == DatabaseType.None)
					throw new InvalidConfigurationException(
							"The database type <"
									+ databaseDefinition.get_dbType()
									+ "> is not supported! The supported types are 'Oracle' and 'SqlServer2008'");

				// Connection strings
				Element e = (Element) databaseNode;
				NodeList connStringList = e
						.getElementsByTagName(CONNECTION_STRING_NODE_NAME);
				if (connStringList != null && connStringList.getLength() > 0) {
					for (int i = 0; i < connStringList.getLength(); i++) {
						Node connStringNode = connStringList.item(i);
						String connStringName = null;
						String connStringValue = null;

						Node connStringNameNode = connStringNode
								.getAttributes()
								.getNamedItem(ELEMENT_NAME_ATTR);
						if (null != connStringNameNode)
							connStringName = connStringNameNode.getNodeValue();

						Node connStringValueNode = connStringNode
								.getAttributes().getNamedItem(
										ELEMENT_VALUE_ATTR);
						if (null != connStringValueNode)
							connStringValue = connStringValueNode
									.getNodeValue();

						if ((null != connStringName)
								&& (null != connStringValue)) {
							if (connStringName
									.equalsIgnoreCase(AppConstants.CONN_STRING_BASELINE_DB))
								databaseDefinition
										.set_connStringBaselineDb(connStringValue);

							if (connStringName
									.equalsIgnoreCase(AppConstants.CONN_STRING_TARGET_DB))
								databaseDefinition
										.set_connStringTargetDb(connStringValue);
						} else {
							throw new InvalidConfigurationException(
									"The connection string name and value must be set in the configuration file!");
						}
					}
					if (null == databaseDefinition.get_connStringBaselineDb()
							|| null == databaseDefinition
									.get_connStringTargetDb()) {
						throw new InvalidConfigurationException(
								"The connection strings values must be set in the configuration file!");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.fatal(Utils.buildExceptionMessage(ex));
			logger.info("The application has exited...");
			System.exit(0);
		}
		return databaseDefinition;
	}

	private void generateSQLStatement(DbTableDefinition definition) {
		StringBuilder sqlQuery = new StringBuilder("SELECT ");
		for (String pkColumnName : definition.getPkColumns()) {
			sqlQuery = sqlQuery.append(pkColumnName);
			sqlQuery = sqlQuery.append(", ");
		}

		for (String columnName : definition.getTableColumns()) {
			sqlQuery = sqlQuery.append(columnName);
			sqlQuery = sqlQuery.append(", ");
		}

		sqlQuery.replace(sqlQuery.length() - 2, sqlQuery.length(), " ");

		sqlQuery = sqlQuery.append("FROM ");
		sqlQuery = sqlQuery.append(definition.getTableName());

		definition.setSqlQuery(sqlQuery.toString());
	}
}
