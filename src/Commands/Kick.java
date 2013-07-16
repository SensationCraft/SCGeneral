package Commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.superckl.combatlogger.CombatLogger;
import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Kick implements CommandExecutor{

	private final SCGeneral plugin;
        private final Map<String, Long> cooldowns = new HashMap<String, Long>();
	//private final Set<String> cooldowns = Collections.synchronizedSet(new HashSet<String>());

	public Kick(final SCGeneral plugin){
		this.plugin = plugin;
	}

	/*public boolean doKick(final CommandSender sender, String[] args){
		if(!sender.hasPermission("scgeneral.kick")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		if(this.cooldowns.contains(sender.getName())){
			sender.sendMessage(ChatColor.RED+"You must wait 5 minutes between kicks!");
			return false;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}else if(args.length == 1){
			sender.sendMessage(ChatColor.RED+"You need to enter a reason!");
			return false;
		}
		List<Player> players = this.plugin.getServer().matchPlayer(args[0]);
		if(players.size() != 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}
		if(players.get(0).hasPermission("scgeneral.kick.exempt")){
			sender.sendMessage(ChatColor.RED+"That player is exempt to kicks!");
			return false;
		}
		CombatLogger combatLogger = (CombatLogger) this.plugin.getServer().getPluginManager().getPlugin("CombatLogger");
		if(combatLogger.getCombatListeners().isInCombat(players.get(0).getName())){
			sender.sendMessage(ChatColor.RED+"You can't kick players while they are in combat!");
			return false;
		}
		String reason = this.translate(args);
		if(!sender.hasPermission("scgeneral.kick.bypasscooldown")){
			this.cooldowns.add(sender.getName());
			new BukkitRunnable(){
				@Override
				public void run() {
					Kick.this.cooldowns.remove(sender.getName());
				}
			}.runTaskLaterAsynchronously(this.plugin, 20*60*5L);
		}
		players.get(0).kickPlayer(reason+ChatColor.DARK_RED+" - "+sender.getName());
		for(Player player:this.plugin.getServer().getOnlinePlayers()) if(player.hasPermission("scgeneral.kick.broadcast")) player.sendMessage(ChatColor.RED+sender.getName()+" kicked "+players.get(0).getName()+" for "+ChatColor.BLUE+reason);
		return true;
	}*/
	private String translate(final String[] args) {
		String message = "";
		for (int i=1;i<args.length;i++)
			message += args[i].concat(" ");
		message = message.trim();
		message = ChatColor.stripColor(message);
		return message;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String arg2,
			final String[] args) {
		if(!sender.hasPermission("essentials.kick")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
                
                Long l = this.cooldowns.get(sender.getName());
                
		if(l != null && l.longValue() > System.currentTimeMillis()){
			sender.sendMessage(ChatColor.RED+"You must wait 5 minutes between kicks!");
			return false;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}else if(args.length == 1){
			sender.sendMessage(ChatColor.RED+"You need to enter a reason!");
			return false;
		}
		Player player = this.plugin.getServer().getPlayer(args[0]);
		final List<Player> players = this.plugin.getServer().matchPlayer(args[0]);
		if(player == null && players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}else if(player == null && players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please refine your name.");
			return false;
		}else if(player == null)
			player = players.get(0);
		if(player.hasPermission("essentials.kick.exempt")){
			sender.sendMessage(ChatColor.RED+"That player is exempt to kicks!");
			return false;
		}
		final CombatLogger combatLogger = (CombatLogger) this.plugin.getServer().getPluginManager().getPlugin("CombatLogger");
		if(combatLogger.getCombatListeners().isInCombat(player.getName())){
			sender.sendMessage(ChatColor.RED+"You can't kick players while they are in combat!");
			return false;
		}
		final String reason = this.translate(args);
		if(!sender.hasPermission("essentials.kick.bypasscooldown")){
			this.cooldowns.put(sender.getName(), 20*60*5L);
		}
		player.kickPlayer(reason+ChatColor.DARK_RED+" - "+sender.getName());
		for(final Player loopPlayer:this.plugin.getServer().getOnlinePlayers()) if(loopPlayer.hasPermission("essentials.kick.broadcast"))
			loopPlayer.sendMessage(ChatColor.RED+sender.getName()+" kicked "+player.getName()+" for "+ChatColor.BLUE+reason);
		return true;
	}
}
