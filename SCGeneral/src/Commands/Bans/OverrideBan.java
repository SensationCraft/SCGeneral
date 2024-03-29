package Commands.Bans;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;
import com.google.common.base.Joiner;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class OverrideBan implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(!sender.hasPermission("essentials.overrideban")){
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
		Player player = SCGeneral.getInstance().getServer().getPlayer(args[0]);
		final List<Player> players = SCGeneral.getInstance().getServer().matchPlayer(args[0]);
		if(player == null && players.size() < 1){
			sender.sendMessage(ChatColor.RED+"Player not found. Performing offline ban...");
			final OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
			this.performBan(offPlayer, sender, args);
			return false;
		}else if(player == null && players.size() > 1){
			sender.sendMessage(ChatColor.RED+"More than one player found! Please refine your name.");
			return false;
		}else if(player == null)
			player = players.get(0);
		if(player.isBanned()){
			sender.sendMessage(ChatColor.RED+"That user is already banned! Making ban permanent...");
			final User user = SCGeneral.getEssentials().getUser(player.getName());
			user.setBanTimeout(0);
			user.setBanned(true);
			user.setConfigProperty("bans", 4);
			return true;
		}
		if(player.hasPermission("essentials.ban.exempt")){
			sender.sendMessage(ChatColor.RED+"That player is exempt to bans!");
			return false;
		}
		this.performBan(player, sender, args);
		return true;
	}
	private String translate(final String[] args) {
		String message = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
		message = ChatColor.stripColor(message);
		return message;
	}
	public void performBan(final OfflinePlayer player, final CommandSender sender, final String[] args){
		final String message = new StringBuilder(this.translate(args)).append(ChatColor.DARK_RED).append(" - ").append(sender.getName()).toString();
		final User user = SCGeneral.getEssentials().getOfflineUser(player.getName());
		user.setBanReason(message);
		user.setBanned(true);
		user.setBanTimeout(0);
		user.kickPlayer(message);
		user.setConfigProperty("bans", 4);
		user.setConfigProperty("ban-reason", message);
		for(final Player loopPlayer:SCGeneral.getInstance().getServer().getOnlinePlayers()) if(loopPlayer.hasPermission("essentials.ban.broadcast"))
			loopPlayer.sendRawMessage(ChatColor.RED+sender.getName()+" banned "+player.getName()+" for "+ChatColor.BLUE+message);
	}
}
