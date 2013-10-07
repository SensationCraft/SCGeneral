package Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class Heal implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(sender instanceof ConsoleCommandSender){
			sender.sendMessage(ChatColor.RED+"Heal must be executed in game!");
			return false;
		}
		Player player = (Player) sender;
		if(args.length <= 0){
			if(sender.hasPermission("essentials.heal")){
				if(player.isDead() || player.getHealth() < 1) return true;
				player.setHealth(20);
				player.setFoodLevel(20);
				sender.sendMessage(ChatColor.GOLD+"You have healed yourself.");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"You don't have permission for that!");
				return false;
			}
		}else if (args.length == 1){
			if(sender.hasPermission("essentials.heal.other")){
				player = SCGeneral.getInstance().getServer().getPlayer(args[0]);
				final List<Player> players = SCGeneral.getInstance().getServer().matchPlayer(args[0]);
				if(player == null && players.size() < 1){
					sender.sendMessage(ChatColor.RED+"Player not found.");
					return false;
				}else if(player == null && players.size() > 1){
					sender.sendMessage(ChatColor.RED+"More than one player found! Please refine your name.");
					return false;
				}else if(player == null)
					player = players.get(0);
				if(SCGeneral.getUser(player.getName()).isInCombat() || SCGeneral.getInstance().getArena().isInArena(player)){
					sender.sendMessage(ChatColor.RED+"You can't heal players that are in combat!");
					return false;
				}
				if(player.isDead() || player.getHealth() < 1) return true;
				player.setHealth(20);
				player.setFoodLevel(20);
				sender.sendMessage(ChatColor.GOLD+"You have healed "+player.getName()+".");
				player.sendMessage(ChatColor.GOLD+sender.getName()+" has healed you.");
				SCGeneral.getInstance().getLogger().info(sender.getName()+" has healed "+player.getName());
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"You don't have permission for that!");
				return false;
			}
		}else{
			sender.sendMessage(ChatColor.RED+"Syntax error. Correct usage: '/heal {player name}'");
			return false;
		}
	}

}
