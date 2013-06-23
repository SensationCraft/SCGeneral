package Commands;

import me.superckl.combatlogger.CombatLogger;
import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Heal implements CommandExecutor{

	private final CombatLogger combatLogger;
	private final SCGeneral instance;

	public Heal(final SCGeneral instance){
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
		this.instance = instance;
	}

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
				player = Bukkit.getPlayer(args[0]);
				if(player == null){
					sender.sendMessage(ChatColor.RED+"Player not found!");
					return false;
				}
				if(this.combatLogger.getCombatListeners().isInCombat(player.getName()) || this.combatLogger.getArena().isInArena(player)){
					sender.sendMessage(ChatColor.RED+"You can't heal players that are in combat!");
					return false;
				}
				player.setHealth(20);
				player.setFoodLevel(20);
				sender.sendMessage(ChatColor.GOLD+"You have healed "+player.getName()+".");
				player.sendMessage(ChatColor.GOLD+sender.getName()+" has healed you.");
				this.instance.getLogger().info(sender.getName()+" has healed "+player.getName());
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
