package Commands;

import me.superckl.combatlogger.CombatLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAccept implements CommandExecutor{

	private final Tpa tpa;
	private final TpaHere tpaHere;
	private final CombatLogger combatLogger;

	public TpAccept(final Tpa tpa, final TpaHere tpaHere){
		this.tpa = tpa;
		this.tpaHere = tpaHere;
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		final String tpaSender = this.tpa.removeOriginByDestination(sender.getName());
		if(tpaSender != null){
			final Player player = Bukkit.getPlayer(tpaSender);
			if(player != null){
				if(this.combatLogger.getCombatListeners().isInCombat(player.getName())){
					player.sendMessage(ChatColor.RED+"You can't teleport players that are in combat!");
					return false;
				}
				player.sendMessage(ChatColor.GOLD+sender.getName()+" has accepted your tpa request.");
				sender.sendMessage(ChatColor.GOLD+"You have accepted "+player.getName()+"'s tpa request.");
				final Player tpa = (Player) sender;
				player.teleport(tpa);
				return true;
			} else
				sender.sendMessage(ChatColor.RED+"Oops! It seems the sender of that request has logged off!");
		}else{
			final String tpaHereSender = this.tpaHere.removeDestinationByOrigin(sender.getName());
			if(tpaHereSender != null){
				final Player player = Bukkit.getPlayer(tpaHereSender);
				if(player != null){
					if(this.combatLogger.getCombatListeners().isInCombat(player.getName())){
						sender.sendMessage(ChatColor.RED+"You can't teleport to players that are in combat!");
						return false;
					}
					player.sendMessage(ChatColor.GOLD+sender.getName()+" has accepted your tpahere request.");
					sender.sendMessage(ChatColor.GOLD+"You have accepted "+player.getName()+"'s tpahere request.");
					final Player tpaHere = (Player) sender;
					tpaHere.teleport(player);
					return true;
				} else
					sender.sendMessage(ChatColor.RED+"Oops! It seems the sender of that request has logged off!");
			} else
				sender.sendMessage(ChatColor.RED+"You have not received a teleport request.");
		}
		return false;
	}
}
