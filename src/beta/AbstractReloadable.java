package beta;

import beta.exceptions.InvalidAddonException;
import beta.exceptions.UnknownAddonException;
import org.bukkit.plugin.Plugin;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public abstract class AbstractReloadable 
{
    public abstract Addon load(SCGeneral plugin) throws UnknownAddonException, InvalidAddonException;
    public abstract void load(Addon addon);
    
    public abstract void unload();
    
    public abstract void validate(Addon a) throws InvalidAddonException;
    
    public Addon reload(SCGeneral plugin)
    {
        Addon a = null;
        try
        {
            a = load(plugin);
        }
        catch(Exception ex)
        {
            
        }
        if(a != null)
        {
            unload();
        }
        return a;
    }
    
    public abstract void enable(Plugin plugin) throws IllegalStateException;
    
    public abstract void disable();
}
