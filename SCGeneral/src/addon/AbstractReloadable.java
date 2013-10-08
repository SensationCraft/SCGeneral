package addon;

import org.bukkit.plugin.Plugin;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.exceptions.InvalidAddonException;
import addon.exceptions.UnknownAddonException;

/**
 *
 * @author DarkSeraphim
 */
public abstract class AbstractReloadable
{

	protected Addon addon;

	public abstract void disable();

	public abstract void enable(Plugin plugin) throws IllegalStateException;
	protected Addon getAddon()
	{
		return this.addon;
	}

	public abstract void load(Addon addon);

	public abstract Addon load(SCGeneral plugin) throws UnknownAddonException, InvalidAddonException;

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

	public abstract void unload();

	public abstract void validate(Addon a) throws InvalidAddonException;
}
