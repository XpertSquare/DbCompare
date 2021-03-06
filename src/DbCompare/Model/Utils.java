/*
 * @author Marius Serban
 * 
 * Project description: Database content comparison tool
 * 
 */

package DbCompare.Model;

public class Utils {
	public static String buildExceptionMessage(Exception ex) {
		StringBuilder exceptionMessage = new StringBuilder(
				ex.getLocalizedMessage());

		exceptionMessage = exceptionMessage.append("\n\tCaused by: ");
		for (int indexStack = 0; indexStack < ex.getStackTrace().length - 1; indexStack++) {
			exceptionMessage.append(ex.getStackTrace()[indexStack]);
			exceptionMessage.append("\n\t");
		}
		exceptionMessage
				.append(ex.getStackTrace()[ex.getStackTrace().length - 1]);
		return exceptionMessage.toString();
	}
}
