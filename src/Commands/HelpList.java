package Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpList implements CommandExecutor{

	private final HelpRequest help;

	public HelpList(final HelpRequest help){
		this.help = help;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("help.list")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		final StringBuilder sb = new StringBuilder().append("\n").append(ChatColor.BLUE).append("Current help requests:\n");
		for(final Integer i:this.help.getRequests().keySet())
			sb.append(ChatColor.GREEN).append(i).append(". ").append(ChatColor.RESET).append(this.help.getRequests().get(i)).append("\n");
		
		arg0.sendMessage(sb.toString());
		return false;
	}

}
