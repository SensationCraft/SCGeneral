package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KillShout implements CommandExecutor{

	private final Shout shout;

	public KillShout(final Shout shout){
		this.shout = shout;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("kill.shout")){
			arg0.sendMessage(new StringBuilder().append(ChatColor.RED).append("You don't have permission for that!").toString());
			return false;
		}
		this.shout.setDead(!this.shout.isDead());
		final StringBuilder sb = new StringBuilder().append(ChatColor.DARK_RED).append("Shout is now ");
		if(this.shout.isDead())
			sb.append("dead.");
		else
			sb.append("alive!");
		Bukkit.broadcastMessage(sb.toString());
		return true;
	}

}
