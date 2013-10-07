package addon;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DarkSeraphim
 */
public class Storage
{
    Map<String, Map<String, Object>> stor = new HashMap<String, Map<String, Object>>();
    
    public Storage()
    {
    }
    
    public void register(Addon a)
    {
        this.stor.put(a.getName(), new HashMap<String, Object>());
    }
    
    /*public void unregister(Addon a)
    {
        this.stor.remove(a.getName());
    }*/
    
    public Object get(Addon a, String key) throws IllegalStateException
    {
        if(!this.stor.containsKey(a.getName()))
            throw new IllegalStateException("Addon not active");
        return this.stor.get(a.getName()).get(key);
    }
    
    public boolean hasKey(Addon a, String key) throws IllegalStateException
    {
        if(!this.stor.containsKey(a.getName()))
            throw new IllegalStateException("Addon not active");
        return this.stor.get(a.getName()).containsKey(key);
    }
    
    public void set(Addon a, String key, Object val)
    {
        if(!this.stor.containsKey(a.getName()))
            throw new IllegalStateException("Addon not active");
        this.stor.get(a.getName()).put(key, val);
    }
}
