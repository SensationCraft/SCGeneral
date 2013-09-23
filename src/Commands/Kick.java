package Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

public class Kick implements CommandExecutor{

	private final Map<String, Long> cooldowns = new HashMap<String, Long>();

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

		final Long l = this.cooldowns.get(sender.getName());

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
		Player player = SCGeneral.getInstance().getServer().getPlayer(args[0]);
		final List<Player> players = SCGeneral.getInstance().getServer().matchPlayer(args[0]);
		if(player == null || players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}else if( players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please refine your name.");
			return false;
		}else
			player = players.get(0);
		if((sender instanceof Player) && !((Player)sender).canSee(player))
		{
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}
		if(player.hasPermission("essentials.kick.exempt")){
			sender.sendMessage(ChatColor.RED+"That player is exempt to kicks!");
			return false;
		}
		if(SCGeneral.getUser(player.getName()).isInCombat()){
			sender.sendMessage(ChatColor.RED+"You can't kick players while they are in combat!");
			return false;
		}
		final String reason = new StringBuilder(this.translate(args)).append(ChatColor.DARK_RED).append(" - ").append(sender.getName()).toString();;
		if(!sender.hasPermission("essentials.kick.bypasscooldown"))
			this.cooldowns.put(sender.getName(), 20*60*5L);
		player.kickPlayer(reason);
		for(final Player loopPlayer:SCGeneral.getInstance().getServer().getOnlinePlayers()) if(loopPlayer.hasPermission("essentials.kick.broadcast"))
			loopPlayer.sendRawMessage(ChatColor.RED+sender.getName()+" kicked "+player.getName()+" for "+ChatColor.BLUE+reason);
		return true;
	}
}
