package Commands.Bans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class GetBans implements CommandExecutor{

	private final Essentials ess;

	public GetBans(){
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(args.length == 0){
			final User user = this.ess.getUser(sender.getName());
			final Integer i = (Integer) user.getConfigMap().get("bans");
			final int bans = i == null ? 0:i.intValue();
			sender.sendMessage(ChatColor.GREEN+"You have "+ChatColor.RED+bans+ChatColor.GREEN+" bans.");
			return true;
		}
		if(!sender.hasPermission("essentials.getbans")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		final User user = this.ess.getOfflineUser(args[0]);
		if(user == null){
			sender.sendMessage(ChatColor.RED+"Player not found, did you spell it right?");
			return false;
		}
		if(sender instanceof Player)
			((Player)sender).sendRawMessage(ChatColor.GREEN+"Bans: "+user.getConfigMap().get("bans"));
		else
			sender.sendMessage(ChatColor.GREEN+"Bans: "+user.getConfigMap().get("bans"));
		return true;
	}

}
