package Commands.Duel;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class CancelCommand implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED+"You must specify which player you want to cancel your request with!");
			return false;
		}
		final Set<String> keys = SCGeneral.getInstance().getArena().getKeysByValue(sender.getName());
		boolean match = false;
		for(final String name:keys){
			if(!name.equalsIgnoreCase(args[0])) continue;
			SCGeneral.getInstance().getArena().getDuelRequests().remove(name);
			sender.sendMessage(ChatColor.GREEN+"Your duel request with "+name+" has been cancelled.");
			match = true;
		}
		if(!match) sender.sendMessage(ChatColor.RED+"You don't have a duel request with that person!");
		return match;
	}
}
