package Commands.tp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import Commands.tp.TpSuite.TpRequest;

public class TpaHere
{

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

		if (!SCGeneral.getEssentials().getUser(player.getName()).isTeleportEnabled())
		{
			player.sendMessage(ChatColor.RED + "That player has teleportation disabled!");
			return;
		}

		if (req != null)
		{
			final Player third = Bukkit.getPlayerExact(req.getRequester());
			if (third != null)
				third.sendMessage(ChatColor.RED + String.format("%s has cancelled your teleport request.", player.getName()));
		}

		suite.request(player.getName(), other.getName(), true);
		player.sendMessage(ChatColor.GOLD + "Request sent to " + other.getName() + ".");
		other.sendMessage(ChatColor.GOLD + player.getName() + " would like to teleport to you " + ChatColor.RED + "to them" + ChatColor.GOLD + ":");
		other.sendMessage(ChatColor.GOLD + "'" + ChatColor.GREEN + "/tpaccept" + ChatColor.GOLD + "' to accept.");
		other.sendMessage(ChatColor.GOLD + "'" + ChatColor.RED + "/tpdeny" + ChatColor.GOLD + "' to deny.");
	}
}
