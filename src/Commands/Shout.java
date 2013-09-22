package Commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.github.DarkSeraphim.SCPvP.Titles;

public class Shout implements CommandExecutor
{

	private final Essentials ess;
	private final SCGeneral instance;
	private final long SHOUT_DELAY = 15000;
	private boolean dead = false;
	private final String shoutFormat = "&c[S] &r%s%s&r: &l%s".replace('&', ChatColor.COLOR_CHAR);
	private final String titleFormat = "&4&l[%s&r&4&l]&r ".replace('&', ChatColor.COLOR_CHAR);

	public Shout(final SCGeneral instance)
	{
		this.instance = instance;
		this.ess = (Essentials)this.instance.getServer().getPluginManager().getPlugin("Essentials");
	}

	private final Map<String, Long> coolDowns = new HashMap<String, Long>();
	private final Set<String> disabled = new HashSet<String>();

	public void setDead(final boolean dead){
		this.dead = dead;
	}

	public boolean isDead(){
		return this.dead;
	}

	@Override
	public boolean onCommand(final CommandSender p, final Command arg1, final String arg2,
			final String[] args) {
		if(args.length <= 0){
			p.sendMessage(ChatColor.RED+"You must type a message!");
			return false;
		}

		if(this.dead && !p.hasPermission("shout.bypass.kill")){
			p.sendMessage(ChatColor.RED+"Shout is currently disabled! Try again later.");
			return true;
		}
		final String playerName = p.getName();
		if(this.disabled.contains(playerName)){
			p.sendMessage(ChatColor.RED+"You have turned shout off. '/shoutmute' to turn it back on.");
			return false;
		}
		if(this.ess != null)
		{
			final User user = this.ess.getUser(playerName);
			if(user != null && user.isMuted())
			{
				p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You are muted!").toString());
				return false;
			}
		}
		final Long l = this.coolDowns.get(playerName);
		if(l != null && l.longValue() > System.currentTimeMillis())
		{
			p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You must wait at least 15 seconds in between shouts.").toString());
			return false;
		}

		final boolean bypass = p.hasPermission("Shout.Bypass");

		final StringBuilder sb = new StringBuilder();
		for(final String s : args)
			sb.append(s).append(" ");

		final String message = sb.toString().trim();
		String title = Titles.getInstance().getTitle(playerName);
		if(!title.equals(""))
			title = String.format(this.titleFormat, title);

		String prefix = playerName;

		if (p.isOp())
			prefix = (ShoutPrefix.OP.get(playerName));
		else if (p.hasPermission("Shout.HeadAdmin"))
			prefix = (ShoutPrefix.HA.get(playerName));
		else if (p.hasPermission("Shout.AdminPlus"))
			prefix = (ShoutPrefix.AP.get(playerName));
		else if (p.hasPermission("Shout.Admin"))
			prefix = (ShoutPrefix.A.get(playerName));
		else if (p.hasPermission("Shout.Mod"))
			prefix = (ShoutPrefix.MOD.get(playerName));
		else if (p.hasPermission("Shout.PremiumPlus"))
			prefix = (ShoutPrefix.PREMIUMP.get(playerName));
		else if (p.hasPermission("Shout.Premium"))
			prefix = (ShoutPrefix.PREMIUM.get(playerName));
		else if (p.hasPermission("Shout.VIPPlus"))
			prefix = (ShoutPrefix.VIPP.get(playerName));
		else if (p.hasPermission("Shout.VIP"))
			prefix = (ShoutPrefix.VIP.get(playerName));


		final String shout = String.format(this.shoutFormat, title, prefix, message);

		final Player players[] = this.instance.getServer().getOnlinePlayers();
		for(final Player player:players)
			if(!this.disabled.contains(player.getName()) && !(this.dead && !player.hasPermission("shout.bypass.kill")))
				player.sendMessage(shout);
		this.instance.getLogger().info(shout);
		if(!bypass)
			this.coolDowns.put(playerName,System.currentTimeMillis()+this.SHOUT_DELAY);

		return true;
	}

	public Set<String> getDisabled() {
		return this.disabled;
	}

	public Map<String, Long> getCooldowns(){
		return this.coolDowns;
	}

	private enum ShoutPrefix
	{
		OP(ChatColor.GOLD+"%s"),
		HA("["+ChatColor.BLACK+"H"+ChatColor.GOLD+"A"+ChatColor.RESET+"] %s"),
		AP("["+ChatColor.DARK_RED+"A"+ChatColor.YELLOW+"+"+ChatColor.RESET+"] %s"),
		A("["+ChatColor.DARK_RED+"A"+ChatColor.RESET+"] %s"),
		MOD("["+ChatColor.BLUE+"M"+ChatColor.RESET+"] %s"),
		PREMIUMP(ChatColor.BLUE+"%s"+ChatColor.YELLOW+"+"),
		PREMIUM(ChatColor.BLUE+"%s"),
		VIPP(ChatColor.GREEN+"%s"+ChatColor.YELLOW+"+"),
		VIP(ChatColor.GREEN+"%s"),
		;

		final String prefix;

		ShoutPrefix(final String prefix)
		{
			this.prefix = prefix;
		}

		public String get(final String name)
		{
			return String.format(this.prefix, name);
		}
	}
}