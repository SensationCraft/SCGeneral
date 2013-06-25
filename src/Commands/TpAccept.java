package Commands;

import me.superckl.combatlogger.CombatLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAccept implements CommandExecutor{

	private Tpa tpa;
	private TpaHere tpaHere;
	private CombatLogger combatLogger;
	
	public TpAccept(Tpa tpa, TpaHere tpaHere){
		this.tpa = tpa;
		this.tpaHere = tpaHere;
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		String tpaSender = this.tpa.removeOriginByDestination(sender.getName());
		if(tpaSender != null){
			Player player = Bukkit.getPlayer(tpaSender);
			if(player != null){
				if(combatLogger.getCombatListeners().isInCombat(player.getName())){
					sender.sendMessage(ChatColor.RED+"You can't teleport players that are in combat!");
					return false;
				}
				Player tpa = (Player) sender;
				tpa.teleport(player);
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"Oops! It seems the sender of that request has logged off!");
			}
		}else{
			String tpaHereSender = this.tpaHere.removeDestinationByOrigin(sender.getName());
			if(tpaHereSender != null){
				Player player = Bukkit.getPlayer(tpaHereSender);
				if(player != null){
					if(combatLogger.getCombatListeners().isInCombat(player.getName())){
						sender.sendMessage(ChatColor.RED+"You can't teleport to players that are in combat!");
						return false;
					}
					Player tpaHere = (Player) sender;
					player.teleport(tpaHere);
					return true;
				}else{
					sender.sendMessage(ChatColor.RED+"Oops! It seems the sender of that request has logged off!");
				}
			}else{
				sender.sendMessage(ChatColor.RED+"You have not received a teleport request.");
			}
		}
		return false;
	}
}
