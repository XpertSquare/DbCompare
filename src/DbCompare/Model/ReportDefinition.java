package DbCompare.Model;

public class ReportDefinition {

	private ReportType reportType = ReportType.Inline;
	private String reportDirectory = null;
	
	
	public String getReportDirectory() {
		return reportDirectory;
	}
	public void setReportDirectory(String reportDirectory) {
		this.reportDirectory = reportDirectory;
	}
	public ReportType getReportType() {
		return reportType;
	}
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}
	
}
