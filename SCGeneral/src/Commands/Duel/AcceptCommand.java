package Commands.Duel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class AcceptCommand implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(!SCGeneral.getInstance().getArena().getDuelRequests().containsKey(sender.getName())){
			sender.sendMessage(ChatColor.RED+"You have not received a duel request!");
			return false;
		}
		if(SCGeneral.getInstance().getArena().isRunning()){
			sender.sendMessage(ChatColor.RED+"Please wait until the arena is empty.");
			return false;
		}
		final String name = SCGeneral.getInstance().getArena().getDuelRequests().get(sender.getName());
		final Player player = SCGeneral.getInstance().getServer().getPlayer(name);
		if(player == null){
			sender.sendMessage(ChatColor.RED+"Your opponent has logged off!");
			return false;
		}
		final Faction factionOne = FPlayers.i.get((Player) sender).getFaction();
		final Faction factionTwo = FPlayers.i.get(player).getFaction();
		if(factionOne.isPeaceful()){
			sender.sendMessage(ChatColor.RED+"Your faction is peaceful!");
			return false;
		}
		if(factionTwo.isPeaceful()){
			sender.sendMessage(ChatColor.RED+"Your opponent is in a peaceful faction!");
			return false;
		}
		if(factionOne.getRelationTo(factionTwo) == Relation.MEMBER && !factionOne.isNone()){
			sender.sendMessage(ChatColor.RED+"Your opponent is in your faction!");
			return false;
		}else if(factionOne.getRelationTo(factionTwo) == Relation.ALLY){
			sender.sendMessage(ChatColor.RED+"Your opponent is your ally!");
			return false;
		}
		SCGeneral.getInstance().getArena().getDuelRequests().remove(sender.getName());
		SCGeneral.getInstance().getArena().startMatch((Player) sender, player);
		return true;
	}

}
