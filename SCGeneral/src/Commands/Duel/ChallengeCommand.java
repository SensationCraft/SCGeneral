package Commands.Duel;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

public class ChallengeCommand implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED+"You must enter a player's name!");
			return false;
		}
		if(sender.getName().equalsIgnoreCase(args[0])){
			sender.sendMessage(ChatColor.RED+"You can't duel yourself!");
			return false;
		}
		final Player challenged = SCGeneral.getInstance().getServer().getPlayer(args[0]);
		if(challenged == null){
			sender.sendMessage(ChatColor.RED+"Player not found!");
			return false;
		}
		final Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(challenged.getName(), sender.getName());
		if(SCGeneral.getInstance().getArena().containsEntry(entry)){
			sender.sendMessage(ChatColor.RED+"You have already sent a duel request to that person!");
			return false;
		}else if(SCGeneral.getInstance().getArena().getDuelRequests().containsKey(challenged.getName())) SCGeneral.getInstance().getArena().getDuelRequests().remove(challenged.getName());
		SCGeneral.getInstance().getArena().getDuelRequests().put(challenged.getName(), sender.getName());
		challenged.sendMessage(sender.getName()+ChatColor.GOLD+" would like to duel you!");
		challenged.sendMessage(ChatColor.GREEN+"/accept"+ChatColor.GOLD+" to accept.");
		challenged.sendMessage(ChatColor.RED+"/deny"+ChatColor.GOLD+" to deny.");
		sender.sendMessage(ChatColor.GOLD+"Duel request sent.");
		return true;
	}

}
