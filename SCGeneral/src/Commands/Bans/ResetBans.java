package Commands.Bans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class ResetBans implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(!sender.hasPermission("essentials.resetbans")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED+"You need to enter a player's name!");
			return false;
		}
		final User user = SCGeneral.getEssentials().getOfflineUser(args[0]);
		if(user == null){
			sender.sendMessage(ChatColor.RED+"Player not found, did you spell it right?");
			return false;
		}
		if(!user.isBanned()){
			sender.sendMessage(ChatColor.RED+"That player isn't banned!");
			return false;
		}
		user.setConfigProperty("bans", 0);
		user.setConfigProperty("ban-reason", "");
		user.setBanned(false);
		user.setBanTimeout(0);
		if(sender instanceof Player)
			((Player)sender).sendRawMessage(ChatColor.GREEN+"Player's bans reset.");
		else
			sender.sendMessage(ChatColor.GREEN+"Player's bans reset.");
		for(final Player player:Bukkit.getOnlinePlayers())
			if(player.hasPermission("essentials.unban") || player.hasPermission("essentials.resetbans"))
				player.sendRawMessage(ChatColor.AQUA+sender.getName()+" has reset bans for "+user.getName());
		return true;
	}

}
