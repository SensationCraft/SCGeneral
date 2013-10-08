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

	public void register(final Addon a)
	{
		this.stor.put(a.getName(), new HashMap<String, Object>());
	}

	/*public void unregister(Addon a)
    {
        this.stor.remove(a.getName());
    }*/

	public Object get(final Addon a, final String key) throws IllegalStateException
	{
		if(!this.stor.containsKey(a.getName()))
			throw new IllegalStateException("Addon not active");
		return this.stor.get(a.getName()).get(key);
	}

	public boolean hasKey(final Addon a, final String key) throws IllegalStateException
	{
		if(!this.stor.containsKey(a.getName()))
			throw new IllegalStateException("Addon not active");
		return this.stor.get(a.getName()).containsKey(key);
	}

	public void set(final Addon a, final String key, final Object val)
	{
		if(!this.stor.containsKey(a.getName()))
			throw new IllegalStateException("Addon not active");
		this.stor.get(a.getName()).put(key, val);
	}
}
