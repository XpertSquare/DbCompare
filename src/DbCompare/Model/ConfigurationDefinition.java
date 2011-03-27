/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

package DbCompare.Model;

import java.util.List;

public class ConfigurationDefinition {

	private DbDefinition databaseDefinition = null;
	private ReportDefinition reportDefinition = null;
	private List<DbTableDefinition> tableDefinitions = null;

	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}
	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}

	
	public List<DbTableDefinition> getTableDefinitions() {
		return tableDefinitions;
	}
	public void setTableDefinitions(List<DbTableDefinition> tableDefinitions) {
		this.tableDefinitions = tableDefinitions;
	}
	public DbDefinition getDatabaseDefinition() {
		return databaseDefinition;
	}
	public void setDatabaseDefinition(DbDefinition databaseDefinition) {
		this.databaseDefinition = databaseDefinition;
	}
}
