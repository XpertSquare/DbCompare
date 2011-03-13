package DbCompare.Model;

import java.util.List;

public class ConfigurationDefinition {

	private DbDefinition _databaseDefinition = null;
	private List<DbTableDefinition> _tableDefinitions = null;
	
	public List<DbTableDefinition> get_tableDefinitions() {
		return _tableDefinitions;
	}
	public void set_tableDefinitions(List<DbTableDefinition> _tableDefinitions) {
		this._tableDefinitions = _tableDefinitions;
	}
	public DbDefinition get_databaseDefinition() {
		return _databaseDefinition;
	}
	public void set_databaseDefinition(DbDefinition _databaseDefinition) {
		this._databaseDefinition = _databaseDefinition;
	}
}
