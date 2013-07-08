package Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.sensationcraft.login.PlayerManager;
import org.sensationcraft.login.SCLogin;

import com.earth2me.essentials.Essentials;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionCheck implements CommandExecutor
{

	private final Essentials ess;

	public FactionCheck()
	{
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	public boolean onCommand(final CommandSender logTo, final Command arg1, final String arg2,
			final String[] args) {
		if (this.ess == null)
		{
			logTo.sendMessage(ChatColor.RED + "Essentials not found!");
			return true;
		}if(!logTo.hasPermission("check.faction")){
			logTo.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		final SCLogin scLogin = SCLogin.getInstance();

		if (args.length < 1)
		{
			logTo.sendMessage(ChatColor.RED + "Invalid arguments. Use:");
			return false;
		}

		Faction faction = null;
		// First we try an exact match
		if (faction == null)
		{
			faction = Factions.i.getByTag(args[0]);
		}

		// Next we match faction tags
		if (faction == null)
		{
			faction = Factions.i.getBestTagMatch(args[0]);
		}

		// Next we match player names
		if (faction == null)
		{
			final FPlayer fplayer = FPlayers.i.getBestIdMatch(args[0]);
			if (fplayer != null)
			{
				faction = fplayer.getFaction();
			}
		}

		if (faction == null || faction.isNone())
		{
			logTo.sendMessage(ChatColor.RED + "Faction not found!");
			return true;
		}
		final List<String> flags = new ArrayList<String>();
		if (args.length > 1)
		{
			for (final String s : Arrays.copyOfRange(args, 1, args.length))
			{
				if (s.startsWith("-"))
				{
					flags.add(s.toLowerCase());
				}
			}
		}

		final boolean hideSingletons = !flags.contains("-all");
		final boolean showNames = flags.contains("-names");

		final Map<String, Integer> ips = new HashMap<String, Integer>();
		final Map<String, List<String>> ipToNames = new HashMap<String, List<String>>();

		final List<String> names = new ArrayList<String>();
		for (final FPlayer fp : faction.getFPlayers())
		{
			names.add(fp.getName());
		}


		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				final PlayerManager manager = scLogin.getPlayerManager();
				for (final String name : names)
				{
					final String ip = manager.getLastIp(name);
					if (ips.get(ip) != null)
					{
						ipToNames.get(ip).add(name);
						ips.put(ip, ips.get(ip) + 1);
					}
					else
					{
						ipToNames.put(ip, new ArrayList<String>(Arrays.asList(name)));
						ips.put(ip, 1);
					}
				}

				if (hideSingletons)
				{
					for (final Map.Entry<String, Integer> ip : ips.entrySet())
					{
						if (ip.getValue() != null && ip.getValue() < 2)
						{
							ip.setValue(null);
						}
					}
				}
				final StringBuilder glue = new StringBuilder(ChatColor.GOLD.toString()).append("IP list:");
				for (final Map.Entry<String, Integer> ip : ips.entrySet())
				{
					if (ip.getValue() != null)
					{
						glue.append("\n - ").append((ChatColor.AQUA)).append(ip.getKey())
						.append(ChatColor.RED).append(" (x").append(ip.getValue()).append(")");
						if (showNames)
						{
							final String names = com.google.common.base.Joiner.on(ChatColor.RESET + ", " + ChatColor.GOLD).join(ipToNames.get(ip.getKey()));
							glue.append(ChatColor.RESET).append(" - ").append(ChatColor.GOLD).append(names);
						}
					}
				}

				logTo.sendMessage(glue.toString().split("\n"));

			}
		}.runTaskAsynchronously(scLogin);
		return true;
	}
}

