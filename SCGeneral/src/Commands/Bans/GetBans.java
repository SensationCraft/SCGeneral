package Commands.Bans;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class GetBans implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(args.length == 0){
			final User user = SCGeneral.getEssentials().getUser(sender.getName());
			final Integer i = (Integer) user.getConfigMap().get("bans");
			final String reason = (String) user.getConfigMap().get("ban-reason");
			final int bans = i == null ? 0:i.intValue();
			sender.sendMessage(ChatColor.GREEN+"You have "+ChatColor.RED+bans+ChatColor.GREEN+" bans.");
			if(reason != null && !reason.isEmpty())
				sender.sendMessage(ChatColor.GREEN+"Reason: "+ChatColor.RED+reason+ChatColor.GREEN+".");
			return true;
		}
		if(!sender.hasPermission("essentials.getbans")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		final User user = SCGeneral.getEssentials().getOfflineUser(args[0]);
		if(user == null){
			sender.sendMessage(ChatColor.RED+"Player not found, did you spell it right?");
			return false;
		}

		String reason = (String) user.getConfigMap().get("ban-reason");
		if(reason == null || reason.isEmpty())
			reason = user.getBanReason();
		sender.sendMessage(ChatColor.GREEN+"Bans: "+user.getConfigMap().get("bans"));
		sender.sendMessage(ChatColor.GREEN+"Reason: "+ChatColor.RED+reason+ChatColor.GREEN+".");
		return true;
	}

}
