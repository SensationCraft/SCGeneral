package Commands.Bans;

import java.util.List;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;

public class Ban implements CommandExecutor{
	
	private final SCGeneral instance;
	private final Essentials ess;
	private final OverrideBan overBan;

	public Ban(final SCGeneral instance, OverrideBan overBan){
		this.instance = instance;
		this.overBan = overBan;
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
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
		Player player = this.instance.getServer().getPlayer(args[0]);
		final List<Player> players = this.instance.getServer().matchPlayer(args[0]);
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
		String message = "";
		for (int i=1;i<args.length;i++)
			message += args[i].concat(" ");
		message = message.trim();
		message = ChatColor.stripColor(message);
		return message;
	}
	
	public void performBan(final OfflinePlayer player, final CommandSender sender, final String[] args){
		try {
			User user = this.ess.getOfflineUser(player.getName());
			Integer i = (Integer)user.getConfigMap().get("bans");
			int bans = i == null ? 0:i.intValue();
			if(bans == 2){
				this.overBan.performBan(player, sender, args);
				return;
			}
			Long timeout = DateUtil.parseDateDiff(this.translateBansToLengthString(bans), true);
			final String message = new StringBuilder(this.translate(args)).append(ChatColor.DARK_RED).append(" - ").append(sender.getName()).toString();
			user.setBanReason(message);
			user.setBanned(true);
			user.setBanTimeout(timeout);
			user.kickPlayer(message);
			user.setConfigProperty("bans", bans+1);
			for(final Player loopPlayer:this.instance.getServer().getOnlinePlayers()) if(loopPlayer.hasPermission("essentials.ban.broadcast"))
				loopPlayer.sendMessage(ChatColor.RED+sender.getName()+" banned "+player.getName()+" for "+ChatColor.BLUE+message);
		} catch (Exception e) {
			this.instance.getLogger().info("Essentials is annoying.");
		}
	}
	
	
	
	private String translateBansToLengthString(int bans){
		if(bans == 0)
			return "3h";
		else if(bans == 1)
			return "1d";
		else
			return "7d";
	}
}
