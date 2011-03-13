package DbCompare.Model;

import java.util.List;

public interface ITableRepository {
	public DbTable LoadTableContent(DbTable table);
	public List<DbTable> LoadContent(ConfigurationDefinition configDefinition);
}
