package DbCompare.Engine;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class DbCompareEngineLauncher {

	static Logger logger = Logger.getLogger(DbCompareEngineLauncher.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure("log4j.config");

		logger.info("App started...");
		System.out.println("Database compare started...");
		
		DbCompareEngine engine = DbCompareEngine.getDatabaseComparionEngine();
		engine.runComparison();

		logger.info("App finished successfully!");
		System.out.println("Database compare completed!");
	}
}
