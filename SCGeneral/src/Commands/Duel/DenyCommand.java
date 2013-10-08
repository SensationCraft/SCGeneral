package Commands.Duel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class DenyCommand implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if(!SCGeneral.getInstance().getArena().getDuelRequests().containsKey(sender.getName())){
			sender.sendMessage(ChatColor.RED+"You have not received a duel request!");
			return false;
		}
		SCGeneral.getInstance().getArena().getDuelRequests().remove(sender.getName());
		sender.sendMessage(ChatColor.DARK_GREEN+"Duel request denied.");
		return true;
	}

}
