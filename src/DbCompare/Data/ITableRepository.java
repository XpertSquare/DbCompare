package DbCompare.Data;

import java.util.List;

import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DbTable;

public interface ITableRepository {
	public List<DbTable> LoadContent(ConfigurationDefinition configDefinition);
}
