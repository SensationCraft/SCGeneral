package Commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpRead implements CommandExecutor{

	private final HelpRequest help;

	public HelpRead(final HelpRequest help){
		this.help = help;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("help.read")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		if(arg3.length == 0){
			arg0.sendMessage(ChatColor.RED+"You must specify a number!");
			return false;
		}
		if(!arg3[0].matches("[0-9]*")){
			arg0.sendMessage(ChatColor.RED+"You must specify a number!");
			return false;
		}
		final int number = Integer.parseInt(arg3[0]);
		final String message = this.help.getRequests().get(number);
		if(message == null){
			arg0.sendMessage(ChatColor.RED+"That help request doesn't exist!");
			return false;
		}
		final StringBuilder sb = new StringBuilder("\n").append(ChatColor.GREEN).append(number).append(". ").append(ChatColor.RESET).append(message).append("\n");
		arg0.sendMessage(sb.toString());
		return true;
	}

}