package Commands.Duel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

public class EndCommand implements CommandExecutor
{
	private final SCGeneral plugin;

	public EndCommand(final SCGeneral plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if(sender.hasPermission("duel.end"))
		{

			this.plugin.getArena().forceEnd();
			return true;
		}
		return false;
	}

}
