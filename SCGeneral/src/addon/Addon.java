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

	public Addon(final SCGeneral scg, final AddonDescriptionFile desc)
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
		return this.desc.getName();
	}

	public void setData(final String key, final Object value)
	{
		this.getPlugin().getData().set(this, key, value);
	}

	public boolean hasData(final String key)
	{
		return this.getPlugin().getData().hasKey(this, key);
	}

	public Object getData(final String key)
	{
		if(!this.hasData(key))
			return null;
		return this.getPlugin().getData().get(this, key);
	}
}
