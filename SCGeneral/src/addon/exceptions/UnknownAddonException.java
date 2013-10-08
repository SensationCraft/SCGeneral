package addon.exceptions;

/**
 *
 * @author DarkSeraphim
 */
@SuppressWarnings("serial")
public class UnknownAddonException extends Exception
{

    public UnknownAddonException(String name) 
    {
        super(String.format("Unknown addon: %s", name));
    }
    
}
