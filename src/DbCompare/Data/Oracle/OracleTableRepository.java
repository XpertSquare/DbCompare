package DbCompare.Data.Oracle;

/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import DbCompare.Data.AbstractTableRepository;
import DbCompare.Model.AppConstants;
import DbCompare.Model.DbDefinition;
import DbCompare.Model.Utils;

public class OracleTableRepository extends AbstractTableRepository {

	private static Logger logger = Logger
			.getLogger(OracleTableRepository.class);

	@Override
	protected Connection getDbConnection(DbDefinition dbDefinition,
			String sDatabaseSource) {
		Connection dbConnection = null;
		String connectionString = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			if (sDatabaseSource.equals(AppConstants.CONN_STRING_BASELINE_DB)) {
				connectionString = dbDefinition.get_connStringBaselineDb();
				dbConnection = DriverManager.getConnection(connectionString);
			} else {
				connectionString = dbDefinition.get_connStringTargetDb();
				dbConnection = DriverManager.getConnection(connectionString);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.fatal(Utils.buildExceptionMessage(ex));
			logger.info("The application has exited...");
			System.exit(0);
		}
		return dbConnection;
	}

}
