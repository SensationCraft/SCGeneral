package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Commands.TpSuite.TpRequest;

import com.earth2me.essentials.Essentials;

public class Tpa
{

	private final Essentials ess;

	public Tpa()
	{
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	public void execute(final Player player, final TpSuite suite, final TpRequest req, final String[] args)
	{
		if (args.length == 0)
		{
			player.sendMessage(ChatColor.RED + "You need to enter a player's name!");
			return;
		}

		final Player other = Bukkit.getPlayerExact(args[0]);
		if (other == null)
		{
			player.sendMessage(ChatColor.DARK_RED + "Player offline.");
			return;
		}

		if (!this.ess.getUser(player.getName()).isTeleportEnabled())
		{
			player.sendMessage(ChatColor.RED + "That player has teleportation disabled!");
			return;
		}

		if (req != null)
		{
			final Player third = Bukkit.getPlayerExact(req.getRequester());
			if (third != null)
				third.sendMessage(ChatColor.RED + String.format("%s has cancelled their your teleport request.", player.getName()));
		}

		suite.request(player.getName(), other.getName(), false);
		player.sendMessage(ChatColor.GOLD + "Request sent to " + other.getName() + ".");
		other.sendMessage(ChatColor.GOLD + player.getName() + " would like to teleport to " + ChatColor.RED + "you" + ChatColor.GOLD + ":");
		other.sendMessage(ChatColor.GOLD + "'" + ChatColor.GREEN + "/tpaccept" + ChatColor.GOLD + "' to accept.");
		other.sendMessage(ChatColor.GOLD + "'" + ChatColor.RED + "/tpdeny" + ChatColor.GOLD + "' to deny.");
	}
}
