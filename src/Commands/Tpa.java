package Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

public class Tpa implements CommandExecutor{

	private final Map<String, String> requests = new HashMap<String, String>();
	private TpaHere tpaHere;
	private final SCGeneral instance;

	public Tpa(final SCGeneral instance){
		this.instance = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}
		if(sender instanceof ConsoleCommandSender)
			sender.sendMessage(ChatColor.RED+"That command can only be executed in game!");
		Player player = this.instance.getServer().getPlayer(args[0]);
		final List<Player> players = this.instance.getServer().matchPlayer(args[0]);
		if(player == null && players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found.");
			return false;
		}else if(player == null && players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please type more of their name.");
			return false;
		}else if(player == null)
			player = players.get(0);
		final Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if(!ess.getUser(player.getName()).isTeleportEnabled()){
			sender.sendMessage(ChatColor.RED+"That player has teleportation disabled!");
			return false;
		}
		final String tpaHere = this.tpaHere.removeDestinationByOrigin(sender.getName());
		if(tpaHere != null){
			final Player tpaHerePlayer = this.instance.getServer().getPlayer(tpaHere);
			if(tpaHerePlayer != null) tpaHerePlayer.sendMessage(ChatColor.RED+sender.getName()+" has cancelled their tpahere request.");
		}
		this.requests.put(player.getName(), sender.getName());
		sender.sendMessage(ChatColor.GOLD+"Request sent to "+player.getName()+".");
		player.sendMessage(ChatColor.GOLD+sender.getName()+" would like to teleport to "+ChatColor.RED+"you"+ChatColor.GOLD+":");
		player.sendMessage(ChatColor.GOLD+"'"+ChatColor.GREEN+"/tpaccept"+ChatColor.GOLD+"' to accept.");
		player.sendMessage(ChatColor.GOLD+"'"+ChatColor.RED+"/tpdeny"+ChatColor.GOLD+"' to deny.");
		return true;
	}

	public String getOriginByDestination(final String dest){
		return this.requests.get(dest);
	}
	public String removeOriginByDestination(final String dest){
		return this.requests.remove(dest);
	}

	public void setTpaHere(final TpaHere tpaHere){
		this.tpaHere = tpaHere;
	}
}
