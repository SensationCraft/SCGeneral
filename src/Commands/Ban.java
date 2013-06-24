package Commands;

import java.util.List;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ban implements CommandExecutor{

	private final SCGeneral instance;
	
	public Ban(SCGeneral instance){
		this.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(!sender.hasPermission("essentials.ban")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}else if(args.length == 1){
			sender.sendMessage(ChatColor.RED+"You need to enter a reason!");
			return false;
		}
		Player player = this.instance.getServer().getPlayer(args[0]);
		final List<Player> players = this.instance.getServer().matchPlayer(args[0]);
		if(player == null && players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}else if(player == null && players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please refine your name.");
			return false;
		}else if(player == null){
			player = players.get(0);
		}
		if(player.hasPermission("essentials.ban.exempt")){
			sender.sendMessage(ChatColor.RED+"That player is exempt to bans!");
			return false;
		}
		String message = this.translate(args);
		message += ChatColor.RED+" - "+sender.getName();
		player.setBanned(true);
		player.kickPlayer(message+ChatColor.DARK_RED+" - "+sender.getName());
		for(final Player loopPlayer:this.instance.getServer().getOnlinePlayers()) if(player.hasPermission("essentials.ban.broadcast")) {
			loopPlayer.sendMessage(ChatColor.RED+sender.getName()+" banned "+player.getName()+" for "+ChatColor.BLUE+message);
		}
		return false;
	}
	private String translate(final String[] args) {
		String message = "";
		for (int i=1;i<args.length;i++) {
			message += args[i].concat(" ");
		}
		message = message.trim();
		message = ChatColor.stripColor(message);
		return message;
	}
}
