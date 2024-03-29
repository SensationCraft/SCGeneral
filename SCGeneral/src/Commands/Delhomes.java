package Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;
import com.google.common.base.Joiner;

public class Delhomes implements CommandExecutor
{

	private final String msg = "&6Deleted homes:&c %s".replace('&', ChatColor.COLOR_CHAR);

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmnd, final String label, final String[] args)
	{
		if(sender instanceof Player == false)
		{
			sender.sendMessage(ChatColor.RED+"Only ingame players can use this command");
			return true;
		}
		if(args.length < 1 || !args[0].matches("[\\-]{0,1}[0-9]*"))
		{
			sender.sendMessage(ChatColor.RED+"Usage: /delhomes <radius>: -1 for all homes");
			return true;
		}
		int r = Integer.parseInt(args[0]);
		if(r >= 0)
			r = (int) Math.pow(r, 2);
		else
			r = Integer.MAX_VALUE;

		final User u = SCGeneral.getEssentials().getUser(sender);

		final Location loc = u.getLocation();

		final List<String> deleted = new ArrayList<String>();

		for(final String home : u.getHomes())
			try
		{
				final Location homeloc = u.getHome(home);
				if(homeloc == null)
					throw new Exception("Home not found");
				if(loc.distanceSquared(homeloc) < r)
				{
					u.delHome(home);
					deleted.add(home);
				}
		}
		catch (final Exception ex)
		{
			// Silence, my dear
		}
		if(!deleted.isEmpty())
			sender.sendMessage(String.format(this.msg, Joiner.on(", ").join(deleted)));
		else
			sender.sendMessage(ChatColor.RED+"No homes found within the radius");
		return true;
	}

}
