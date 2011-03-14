package DbCompare.Data.Mock;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

import java.util.ArrayList;
import java.util.List;

import DbCompare.Data.ITableRepository;
import DbCompare.Model.ConfigurationDefinition;
import DbCompare.Model.DbTable;
import DbCompare.Model.DbTableRecord;

public class MockTableRepository implements ITableRepository {

	DbTableRecord record = null;
	List<DbTableRecord> allRecords = new ArrayList<DbTableRecord>(); 
	
	public DbTable LoadTableContent(DbTable table) {
		
		
		//Baseline table contents
		for(int i =0; i<100; i++)
		{
			record = new DbTableRecord();
			
			//Primary key
			StringBuffer primaryKeyValue = new StringBuffer();
			for(String pk:table.get_tableDefinition().getPkColumns())
			{
				primaryKeyValue = primaryKeyValue.append(getColumnValue(pk));
			}
			
			record.set_primaryKey(primaryKeyValue.toString());
			
			//all columns
			String[] columnValues = new String[table.get_tableDefinition().getTableColumns().size()];
			int columnIndex = 0;
			String columnValue = null;
			for(String column:table.get_tableDefinition().getTableColumns())
			{
				columnValue = column;
				columnValues[columnIndex++] =columnValue;
			}
			
			record.set_values(columnValues);			
			
			allRecords.add(record);
		}
		
		table.set_tableBaselineContent(allRecords);
		
		//Target table contents
		
		allRecords = new ArrayList<DbTableRecord>();
		
		for(String pk:table.get_tableDefinition().getPkColumns())
		{

			IdGenerator.ResetID(pk);
		}
		
		for(int i =0; i<100; i++)
		{
			record = new DbTableRecord();
			
			//Primary key
			StringBuffer primaryKeyValue = new StringBuffer();
			for(String pk:table.get_tableDefinition().getPkColumns())
			{
				primaryKeyValue = primaryKeyValue.append(getColumnValue(pk));
			}
			
			record.set_primaryKey(primaryKeyValue.toString());
			
			//all columns
			String[] columnValues = new String[table.get_tableDefinition().getTableColumns().size()];
			int columnIndex = 0;
			String columnValue = null;
			for(String column:table.get_tableDefinition().getTableColumns())
			{
				if(i%10==0)
				{
					columnValue = column + "_" + i;
					columnValues[columnIndex++] = columnValue;
				}
				else
				{
					columnValue = column;
					columnValues[columnIndex++] = columnValue;
				
				}
			}
			
			record.set_values(columnValues);			
			
			allRecords.add(record);
		}
		
		table.set_tableTargetContent(allRecords);
		
		return table;
	}
	private String getColumnValue(String pk) {
		return IdGenerator.GetNextID(pk);
	}
	public List<DbTable> LoadContent(ConfigurationDefinition configDefinition) {
		// TODO Auto-generated method stub
		return null;
	}
}
