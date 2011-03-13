package DbCompare.Data.Mock;

import java.util.ArrayList;
import java.util.List;

import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DbDefinition;
import DbCompare.Model.DbTableDefinition;
import DbCompare.Model.IConfigurationRepository;

public class MockConfigurationRepository implements IConfigurationRepository {

	private List<DbTableDefinition> definitions = null;
	private ConfigurationDefinition configDefinition = null;

	private List<DbTableDefinition> getTableDefinitions() {

		DbTableDefinition definition = new DbTableDefinition();

		definition.setTableName("TBDEMO");

		List<String> pkColumns = new ArrayList<String>();
		pkColumns.add("ID");
		definition.setPkColumns(pkColumns);

		List<String> columns = new ArrayList<String>();
		columns.add("NAME");
		columns.add("DESCRIPTION");
		definition.setTableColumns(columns);

		definitions = new ArrayList<DbTableDefinition>();
		definitions.add(definition);

		return definitions;
	}

	private DbDefinition getDatabaseDefinitions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConfigurationDefinition getConfigurationDefinition() {
		if (null == configDefinition) {
			configDefinition = new ConfigurationDefinition();

			try {

				configDefinition
						.set_databaseDefinition(getDatabaseDefinitions());
				configDefinition.set_tableDefinitions(getTableDefinitions());
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(0);
			}
		}

		return configDefinition;
	}
}
