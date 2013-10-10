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
		this.getPlugin().getData().set(key, value);
	}

	public <T> boolean hasData(Class<T> clazz, final String key)
	{
		return this.getPlugin().getData().hasKey(clazz, key);
	}

	public <T> T getData(Class<T> clazz, final String key)
	{
		return this.getPlugin().getData().get(clazz, key);
	}
}
