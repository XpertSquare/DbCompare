package DbCompare.Model;

import java.util.List;

public class DbTable {
	

	private List<DbTableRecord> _tableBaselineContent = null;
	private List<DbTableRecord> _tableTargetContent = null;
	private DbTableDefinition _tableDefinition = null;
	private boolean isChanged = false;
	
	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public List<DbTableRecord> get_tableBaselineContent() {
		return _tableBaselineContent;
	}

	public void set_tableBaselineContent(List<DbTableRecord> _tableBaselineContent) {
		this._tableBaselineContent = _tableBaselineContent;
	}

	public List<DbTableRecord> get_tableTargetContent() {
		return _tableTargetContent;
	}

	public void set_tableTargetContent(List<DbTableRecord> _tableTargetContent) {
		this._tableTargetContent = _tableTargetContent;
	}

	public DbTableDefinition get_tableDefinition() {
		return _tableDefinition;
	}

	public void set_tableDefinition(DbTableDefinition _tableDefinition) {
		this._tableDefinition = _tableDefinition;
	}

	public DbTable(DbTableDefinition tableDefinition)
	{
		_tableDefinition = tableDefinition;		
	}	
}
