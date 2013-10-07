package addon.exceptions;

/**
 *
 * @author DarkSeraphim
 */
@SuppressWarnings("serial")
public class InvalidAddonException extends Exception
{

    public InvalidAddonException(String error) 
    {
        super(error);
    }
    
}
