package Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TpaHere implements CommandExecutor{
	
	private Map<String, String> requests = new HashMap<String, String>();
	private Tpa tpa;
	private SCGeneral instance;
	
	public TpaHere(Tpa tpa, SCGeneral instance){
		this.tpa = tpa;
		this.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}
		if(sender instanceof ConsoleCommandSender){
			sender.sendMessage(ChatColor.RED+"That command can only be executed in game!");
		}
		Player player = this.instance.getServer().getPlayer(args[0]);
		final List<Player> players = this.instance.getServer().matchPlayer(args[0]);
		if(player == null && players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}else if(player == null && players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please type more of their name.");
			return false;
		}else if(player == null){
			player = players.get(0);
		}
		String tpa = this.tpa.removeOriginByDestination(sender.getName());
		if(tpa != null){
			Player tpaPlayer = this.instance.getServer().getPlayer(tpa);
			if(tpaPlayer != null) tpaPlayer.sendMessage(ChatColor.RED+sender.getName()+" has cancelled their tpa request.");
		}
		this.requests.put(player.getName(), sender.getName());
		sender.sendMessage(ChatColor.GOLD+"Request sent to "+player.getName()+".");
		player.sendMessage(ChatColor.GOLD+sender.getName()+" would like you to teleport to "+ChatColor.RED+"them"+ChatColor.GOLD+":");
		player.sendMessage(ChatColor.GOLD+"'"+ChatColor.GREEN+"/tpaccept"+ChatColor.GOLD+"' to accept.");
		player.sendMessage(ChatColor.GOLD+"'"+ChatColor.RED+"/tpdeny"+ChatColor.GOLD+"' to accept.");
		return true;
	}

	public String getDestinationByOrigin(String origin){
		return this.requests.get(origin);
	}
	public String removeDestinationByOrigin(String origin){
		return this.requests.remove(origin);
	}
}
