package addon.exceptions;

/**
 *
 * @author DarkSeraphim
 */
@SuppressWarnings("serial")
public class InvalidAddonException extends Exception
{

	public InvalidAddonException(final String error)
	{
		super(error);
	}

}
