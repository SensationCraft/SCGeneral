package addon.storage;

import java.util.HashMap;
import java.util.Map;

import addon.Addon;

/**
 *
 * @author DarkSeraphim
 */
public class Storage
{
	Map<String, AbstractStorage> stor = new HashMap<String, AbstractStorage>();

    /**
     * 
     * @param clazz Expected class
     * @param key Key of the value requested
     * @return Value of type T
     * @throws IllegalStateException 
     * @throws ClassCastException if the value is not of the type clazz specified
     * Check with hasKey before get!
     */
	public <T> T get(Class<T> clazz, final String key) throws IllegalStateException, ClassCastException
	{
        return clazz.cast(this.stor.get(key));
	}

	public <T> boolean hasKey(Class<T> clazz, final String key) throws IllegalStateException
	{
        if(this.stor.containsKey(key))
        {
            return clazz.isInstance(this.stor.get(key));
        }
        return true;
	}

	public void set(final String key, final AbstractStorage val)
	{
		this.stor.put(key, val);
	}
}
