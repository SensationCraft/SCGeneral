package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpDeny implements CommandExecutor{

	private final HelpRequest help;

	public HelpDeny(final HelpRequest help){
		this.help = help;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("help.deny")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		if(arg3.length == 0){
			arg0.sendMessage(ChatColor.RED+"You must specify a number!");
			return false;
		}
		if(!arg3[0].matches("[0-9]")){
			arg0.sendMessage(ChatColor.RED+"You must specify a number!");
			return false;
		}
		int number = Integer.parseInt(arg3[0]);
		if(number == 0 || number-- > this.help.getRequests().size()){
			arg0.sendMessage(ChatColor.RED+"That help request doesn't exist!");
			return false;
		}
		final String[] split = this.help.getRequests().remove(number).split("[:]", 2);
		final Player player = Bukkit.getPlayer(split[0]);
		if(player == null){
			arg0.sendMessage(ChatColor.RED+"That player is for some reason offline... The request has been removed.");
			return false;
		}
		player.sendMessage(ChatColor.RED+arg0.getName()+" has denied your help request!");
		arg0.sendMessage(ChatColor.RED+"You have denied "+player.getName()+"'s help request!");
		return true;
	}

}
