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

	protected Addon getAddon()
	{
		return this.addon;
	}

	public abstract Addon load(SCGeneral plugin) throws UnknownAddonException, InvalidAddonException;
	public abstract void load(Addon addon);

	public abstract void unload();

	public abstract void validate(Addon a) throws InvalidAddonException;

	public Addon reload(final SCGeneral plugin)
	{
		Addon a = null;
		try
		{
			a = this.load(plugin);
		}
		catch(final Exception ex)
		{

		}
		if(a != null)
			this.unload();
		return a;
	}

	public abstract void enable(Plugin plugin) throws IllegalStateException;

	public abstract void disable();
}
