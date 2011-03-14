package DbCompare.Model;


public class DbTableRecord {
	private String[] _values = null;
	private RecordStatus _status = RecordStatus.New;
	private String[] _primaryKeys = null;
	public String[] get_primaryKeys() {
		return _primaryKeys;
	}

	public void set_primaryKeys(String[] _primaryKeys) {
		this._primaryKeys = _primaryKeys;
	}

	private String _primaryKey = null;

	public String get_primaryKey() {
		return _primaryKey;
	}

	public void set_primaryKey(String _primaryKey) {
		this._primaryKey = _primaryKey;
	}

	public RecordStatus get_status() {
		return _status;
	}

	public void set_status(RecordStatus status) {
		this._status = status;
	}
	
	public String[] get_values() {
		return _values;
	}

	public void set_values(String[] _values) {
		this._values = _values;
	}

}
