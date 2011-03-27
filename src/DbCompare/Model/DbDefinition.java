package DbCompare.Model;

public class DbDefinition {
	private DatabaseType dbType = DatabaseType.None;
	private String _connStringBaselineDb = null;
	private String _connStringTargetDb = null;
	private String baselineEnvironment = null;
	private String targetEnvironment = null;
	
	public String getBaselineEnvironment() {
		return baselineEnvironment;
	}
	public void setBaselineEnvironment(String baselineEnvironment) {
		this.baselineEnvironment = baselineEnvironment;
	}
	public String getTargetEnvironment() {
		return targetEnvironment;
	}
	public void setTargetEnvironment(String targetEnvironment) {
		this.targetEnvironment = targetEnvironment;
	}

	public DatabaseType getDbType() 
	{
		return dbType;
	}
	public void setDbType(DatabaseType dbType) 
	{
		this.dbType = dbType;
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
