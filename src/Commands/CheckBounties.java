package Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;

public class CheckBounties implements CommandExecutor{

	private Essentials ess;
	
	public CheckBounties(){
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		User user = null;
		if(arg3.length == 0 && arg0 instanceof Player == false){
			arg0.sendMessage(ChatColor.RED+"That command can only be executed ingame!");
		}else if(arg3.length == 0)
			user = this.ess.getUser(arg0.getName());
		else
			user = this.ess.getOfflineUser(arg3[0]);
		if(user == null){
			arg0.sendMessage(ChatColor.RED+"Player not found. Did you type the name right?");
		}
		if(user.getConfigMap().containsKey("bounties")){
			@SuppressWarnings("unchecked")
			List<String> bounties = (List<String>) user.getConfigMap().get("bounties");
			if(bounties.isEmpty()){
				arg0.sendMessage(ChatColor.GREEN+"You don't have any bounties!");
				return false;
			}
			StringBuilder builder = new StringBuilder().append(ChatColor.BLUE).append("Bounties:\n");
			for(String bounty:bounties){
				String[] split = bounty.split("[:]");
				String format = DateUtil.formatDateDiff(Long.parseLong(split[1]));
				double money = Double.parseDouble(split[0]);
				builder.append(ChatColor.DARK_RED).append("$").append(money).append(ChatColor.GOLD).append("expires in ").append(format).append("\n");
			}
			arg0.sendMessage(builder.toString());
			return true;
		}else
			arg0.sendMessage(ChatColor.GREEN+"You don't have any bounties!");
		return false;
	}

}
