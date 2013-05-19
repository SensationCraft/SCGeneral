package Shout;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class Shout
{

	Essentials ess;

	final long SHOUT_DELAY = 15000;

	public Shout()
	{
		this.ess = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
	}

	public void doShout(final Player p, final String args[])
	{
		final String playerName = p.getName();

		if(this.ess != null)
		{
			final User user = this.ess.getUser(playerName);
			if(user != null && user.isMuted())
			{
				p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You are muted!").toString());
				return;
			}
		}
		final Long l = this.CoolDowns.get(playerName);
		if(l != null && l.longValue() > System.currentTimeMillis())
		{
			p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You must wait at least 15 seconds in between shouts.").toString());
			return;
		}

		final boolean bypass = p.hasPermission("Shout.Bypass");

		final StringBuilder sb = new StringBuilder();
		for(final String s : args)
		{
			sb.append(s).append(" ");
		}

		final String message = sb.toString().trim();

		final StringBuilder shout = (new StringBuilder()).append(ChatColor.RED).append("[SHOUT] ").append(ChatColor.RESET);

		final boolean VIP = p.hasPermission("Shout.VIP");
		final boolean VIPPlus = p.hasPermission("Shout.VIPPlus");
		final boolean Premium = p.hasPermission("Shout.Premium");
		final boolean PremiumPlus = p.hasPermission("Shout.PremiumPlus");
		final boolean Mod = p.hasPermission("Shout.Mod");
		final boolean Admin = p.hasPermission("Shout.Admin");
		final boolean AdminPlus = p.hasPermission("Shout.AdminPlus");
		final boolean HeadAdmin = p.hasPermission("Shout.HeadAdmin");
		if (p.isOp()) {
			shout.append(ChatColor.GOLD).append(playerName).append(ChatColor.RESET);
		} else if (HeadAdmin) {
			shout.append("[").append(ChatColor.BLACK).append("H").append(ChatColor.GOLD).append("A").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		} else if (AdminPlus) {
			shout.append("[").append(ChatColor.DARK_RED).append("A").append(ChatColor.YELLOW).append("+").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		} else if (Admin) {
			shout.append("[").append(ChatColor.DARK_RED).append("A").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		} else if (Mod) {
			shout.append("[").append(ChatColor.BLUE).append("M").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		} else if (PremiumPlus) {
			shout.append(ChatColor.BLUE).append(playerName).append(ChatColor.YELLOW).append("+").append(ChatColor.RESET);
		} else if (Premium) {
			shout.append(ChatColor.BLUE).append(playerName).append(ChatColor.RESET);
		} else if (VIPPlus) {
			shout.append(ChatColor.GREEN).append(playerName).append(ChatColor.YELLOW).append("+").append(ChatColor.RESET);
		} else if (VIP) {
			shout.append(ChatColor.GREEN).append(playerName).append(ChatColor.RESET);
		} else {
			shout.append(playerName);
		}

		shout.append(": ").append(ChatColor.BOLD).append(message);
		Bukkit.broadcastMessage(shout.toString());

		if(!bypass) {
			this.CoolDowns.put(playerName,System.currentTimeMillis()+this.SHOUT_DELAY);
		}
	}

	public Map<String, Long> CoolDowns = new HashMap<String, Long>();
}