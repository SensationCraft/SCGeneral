package Commands.Duel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class SpectateCommand implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if(sender instanceof Player) SCGeneral.getInstance().getArena().spectate((Player) sender);
		return true;
	}

}
