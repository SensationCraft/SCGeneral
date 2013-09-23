package Commands.tp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Commands.tp.TpSuite.TpRequest;

public class TpDeny
{

	public void execute(final Player player, final TpRequest req)
	{
		final Player other = Bukkit.getPlayerExact(req.getRequester());
		if (other != null)
			other.sendMessage(ChatColor.RED + String.format("%s has cancelled your teleport request.", player.getName()));
		player.sendMessage(ChatColor.GOLD+String.format("You denied %s's request", req.getRequester()));
	}
}
