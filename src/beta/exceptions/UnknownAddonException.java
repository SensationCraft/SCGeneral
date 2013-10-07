package beta.exceptions;

/**
 *
 * @author DarkSeraphim
 */
public class UnknownAddonException extends Exception
{

    public UnknownAddonException(String name) 
    {
        super(String.format("Unknown addon: %s", name));
    }
    
}
