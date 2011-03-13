package DbCompare.Model;

public class DbDefinition {
	private DatabaseType _dbType = DatabaseType.None;
	private String _connStringBaselineDb = null;
	private String _connStringTargetDb = null;
	
	public DatabaseType get_dbType() 
	{
		return _dbType;
	}
	public void set_dbType(DatabaseType _dbType) 
	{
		this._dbType = _dbType;
	}
	public String get_connStringBaselineDb() 
	{
		return _connStringBaselineDb;
	}
	public void set_connStringBaselineDb(String _connStringBaselineDb) 
	{
		this._connStringBaselineDb = _connStringBaselineDb;
	}
	public String get_connStringTargetDb() 
	{
		return _connStringTargetDb;
	}
	public void set_connStringTargetDb(String _connStringTargetDb) 
	{
		this._connStringTargetDb = _connStringTargetDb;
	}

}
