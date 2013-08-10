package Commands;

import me.superckl.combatlogger.CombatLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import Commands.TpSuite.TpRequest;

public class TpAccept {

	private final CombatLogger combatLogger;

	public TpAccept()
	{
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
	}

	public void execute(final Player player, final TpRequest req)
	{
		final Player other = Bukkit.getPlayerExact(req.getRequester());
		if(other == null)
		{
			player.sendMessage(ChatColor.RED+"Oops, seems the player was offline.");
			return;
		}

		if(this.combatLogger.getCombatListeners().isInCombat(other.getName()))
		{
			player.sendMessage(ChatColor.RED+"You cannot teleport to people while they are in combat!");
			return;
		}
		else if(this.combatLogger.getCombatListeners().isInCombat(player.getName()))
		{
			player.sendMessage(ChatColor.RED+"You cannot teleport to people while in combat!");
			return;
		}

		Location to;
		Player teleporter;
		if(req.isTpaHere())
		{
			teleporter = player;
			to = other.getLocation();
		}
		else
		{
			teleporter = other;
			to = player.getLocation();
		}
		player.sendMessage(ChatColor.GOLD+String.format("You accepted %s's request", other.getName()));
		teleporter.sendMessage(ChatColor.GOLD+"Teleporting...");
		teleporter.teleport(to);
	}
}
