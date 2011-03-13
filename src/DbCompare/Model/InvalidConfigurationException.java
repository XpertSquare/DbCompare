package DbCompare.Model;

public class InvalidConfigurationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8731835609395393219L;

	public InvalidConfigurationException()
	{
		super();
	}
	
	public InvalidConfigurationException(String exceptionMessage)
	{
		super(exceptionMessage);
	}

}

