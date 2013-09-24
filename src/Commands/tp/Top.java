package Commands.tp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class Top implements CommandExecutor
{

	private final String msg = "&6Teleporting to top.".replace('&', ChatColor.COLOR_CHAR);

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmnd, final String label, final String[] args)
	{
		if(sender instanceof Player == false)
		{
			sender.sendMessage(ChatColor.RED+"This command can only be used by players");
			return true;
		}

		if(!sender.hasPermission("essentials.top"))
		{
			sender.sendMessage(ChatColor.RED+"You do not have the permissions to use his command");
			return true;
		}

		final Player player = (Player) sender;
		final Location loc = player.getLocation().clone();
		loc.setY(loc.getWorld().getMaxHeight());
		final World w = loc.getWorld();
		final int x = loc.getBlockX(), z = loc.getBlockZ();
		int y = w.getMaxHeight();
		while(y > 0)
		{
			if(w.getBlockTypeIdAt(x, y - 1, z) > 0)
				break;
			y--;
		}
		loc.setY(y);
		player.teleport(loc);
		player.sendMessage(this.msg);
		return true;
	}
}
