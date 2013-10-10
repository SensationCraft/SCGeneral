package Commands;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class KillShout implements CommandExecutor
{

    private final SCGeneral plugin;
    
	public KillShout(final SCGeneral plugin)
    {
        this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(!arg0.hasPermission("kill.shout"))
        {
			arg0.sendMessage(new StringBuilder().append(ChatColor.RED).append("You don't have permission for that!").toString());
			return false;
		}
        AtomicBoolean dead = this.plugin.getData().get(AtomicBoolean.class, "shoutkill");
		final StringBuilder sb = new StringBuilder().append(ChatColor.DARK_RED).append("Shout is now ");
		boolean next = !dead.get();
        if(next)
			sb.append("dead.");
		else
			sb.append("alive!");
        dead.set(next);
		Bukkit.broadcastMessage(sb.toString());
		return true;
	}

}
