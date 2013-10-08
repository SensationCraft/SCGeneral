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
import com.earth2me.essentials.utils.DateUtil;
import com.google.common.base.Joiner;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class Ban implements CommandExecutor{

	private final OverrideBan overBan;

	public Ban(final OverrideBan overBan){
		this.overBan = overBan;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(!sender.hasPermission("essentials.ban")){//TODO change to overrideban
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
		try {
			final User user = SCGeneral.getEssentials().getOfflineUser(player.getName());
			final Integer i = (Integer)user.getConfigMap().get("bans");
			final int bans = i == null ? 0:i.intValue();
			if(bans == 3){
				this.overBan.performBan(player, sender, args);
				return;
			}
			final Long timeout = DateUtil.parseDateDiff(this.translateBansToLengthString(bans), true);
			final String message = new StringBuilder(this.translate(args)).append(ChatColor.DARK_RED).append(" - ").append(sender.getName()).toString();
			user.setBanReason(message);
			user.setBanned(true);
			user.setBanTimeout(timeout);
			user.kickPlayer(message);
			user.setConfigProperty("bans", bans+1);
			user.setConfigProperty("ban-reason", message);
			for(final Player loopPlayer:SCGeneral.getInstance().getServer().getOnlinePlayers()) if(loopPlayer.hasPermission("essentials.ban.broadcast"))
				loopPlayer.sendRawMessage(ChatColor.RED+sender.getName()+" banned "+player.getName()+" for "+ChatColor.BLUE+message);
		} catch (final Exception e) {
			SCGeneral.getInstance().getLogger().info("Essentials is annoying.");
		}
	}



	private String translateBansToLengthString(final int bans){
		if(bans == 0)
			return "3h";
		else if(bans == 1)
			return "1d";
		else
			return "7d";
	}
}
