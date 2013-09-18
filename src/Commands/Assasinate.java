package Commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.utils.DateUtil;

public class Assasinate implements CommandExecutor{
	
	private Essentials ess;
	
	public Assasinate(){
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2,
			String[] args) {
		if(sender instanceof Player == false){
			sender.sendMessage(ChatColor.RED+"That command must be executed ingame!");
			return false;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED+"You must enter a player's name!");
			return false;
		}else if(args.length == 1){
			sender.sendMessage(ChatColor.RED+"You must enter a money value!");
			return false;
		}
		if(!args[1].matches("[0-9]*.[0-9]*")){
			sender.sendMessage(ChatColor.RED+"Your money value was not recognized as a number. Please try again.");
			return false;
		}
		final BigDecimal money = new BigDecimal(Double.parseDouble(args[1]));
			try {
				if(!Economy.hasEnough(sender.getName(), money)){
					sender.sendMessage(ChatColor.RED+"You don't have that much money!");
					return false;
				}
				User user = this.ess.getOfflineUser(args[0]);
				if(this.addBounty(user, money.doubleValue())){
					Economy.substract(sender.getName(), money);
					sender.sendMessage(ChatColor.GREEN+"Your bounty has been placed.");
					if(user.isOnline()){
						user.sendMessage(ChatColor.DARK_RED+"A bounty has been placed on you for $"+money.doubleValue());
						user.playSound(user.getLocation(), Sound.WITHER_SPAWN, 5, 1);
					}
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private boolean addBounty(User user, double money) throws Exception{
		long timeStamp = DateUtil.parseDateDiff("1w", true);
		List<String> bounties = null;
		if(user.getConfigMap().containsKey("bounties"))
			bounties = (List<String>) user.getConfigMap().get("bounties");
		else
			bounties = new ArrayList<>();
		bounties.add(money+":"+timeStamp);
		user.setConfigProperty("bounties", bounties);
		return true;
	}

}
