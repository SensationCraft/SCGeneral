package Commands.help;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class HelpCancel implements CommandExecutor{

	private final HelpRequest help;

	public HelpCancel(final HelpRequest help){
		this.help = help;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("help.cancel")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		final String helpRequest = this.help.removeRequest(arg0.getName());
		if(helpRequest != null)
			arg0.sendMessage(ChatColor.RED+"Your help request has been cancelled.");
		else
			arg0.sendMessage(ChatColor.RED+"You don't have a currently pending help request!");
		for(final Player player:Bukkit.getOnlinePlayers())
			if(player.hasPermission("help.list"))
				player.sendMessage(ChatColor.RED+arg0.getName()+" has cancelled their help request.");
		return true;
	}

}