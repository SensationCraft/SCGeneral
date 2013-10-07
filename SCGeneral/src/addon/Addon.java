package addon;

import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public abstract class Addon 
{
    
    private final SCGeneral scg;
    
    private final AddonDescriptionFile desc;
    
    public Addon(SCGeneral scg, AddonDescriptionFile desc)
    {
        this.scg = scg;
        this.desc = desc;
    }
    
    public void onEnable()
    {
        
    }
    
    public void onDisable()
    {
        
    }
    
    public SCGeneral getPlugin()
    {
        return this.scg;
    }
    
    public String getName()
    {
        return desc.getName();
    }
    
    public void setData(String key, Object value)
    {
        getPlugin().getData().set(this, key, value);
    }
    
    public boolean hasData(String key)
    {
        return getPlugin().getData().hasKey(this, key);
    }
    
    public Object getData(String key)
    {
        if(!hasData(key))
            return null;
        return getPlugin().getData().get(this, key);
    }
}
