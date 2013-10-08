package addon;

import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public abstract class Addon
{

	private final AddonDescriptionFile desc;

	private final SCGeneral scg;

	public Addon(SCGeneral scg, AddonDescriptionFile desc)
	{
		this.scg = scg;
		this.desc = desc;
	}

	public Object getData(String key)
	{
		if(!hasData(key))
			return null;
		return getPlugin().getData().get(this, key);
	}

	public String getName()
	{
		return desc.getName();
	}

	public SCGeneral getPlugin()
	{
		return this.scg;
	}

	public boolean hasData(String key)
	{
		return getPlugin().getData().hasKey(this, key);
	}

	public void onDisable()
	{

	}

	public void onEnable()
	{

	}

	public void setData(String key, Object value)
	{
		getPlugin().getData().set(this, key, value);
	}
}
