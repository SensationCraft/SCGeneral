package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class Expel implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(arg0 instanceof Player == false){
			arg0.sendMessage(ChatColor.RED+"Only players can execute that command!");
			return false;
		}
		if(!arg0.hasPermission("scgeneral.expel")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission for that!");
			return false;
		}
		if(arg3.length != 1){
			arg0.sendMessage(ChatColor.RED+"Syntax Error. Proper usage: '/expel {radius}'");
			return false;
		}
		if(!arg3[0].matches("[0-9]*")){
			arg0.sendMessage(ChatColor.RED+"Radius must be an integer!");
			return false;
		}
		final double radius = Math.pow(Double.parseDouble(arg3[0]), 2);
		final Location base = ((Player)arg0).getLocation();
		final Location spawn = Bukkit.getWorld("Spawn").getSpawnLocation();
		int expelled = 0;
		int total = 0;
		for(final Player player:Bukkit.getOnlinePlayers())
			if(player.getLocation().distanceSquared(base) <= radius){
				total++;
				if(player.teleport(spawn)){
					arg0.sendMessage(new StringBuilder().append(ChatColor.GOLD).append("Expelled ").append(player.getName()).append(".").toString());
					expelled++;
				}
			}
		arg0.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("Expelled ").append(expelled).append(" of ").append(total).append(" players in a ").append(arg3[0]).append(" block radius.").toString());
		return true;
	}

}
