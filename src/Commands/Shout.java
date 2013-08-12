package Commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.github.DarkSeraphim.SCPvP.Titles;

public class Shout implements CommandExecutor
{

	private final Essentials ess;
	private final SCGeneral instance;
	private final long SHOUT_DELAY = 15000;
	private boolean dead = false;

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
			return false;
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
		final String title = Titles.getInstance().getTitle(playerName);
		final StringBuilder shout = (new StringBuilder()).append(ChatColor.RED).append("[S] ").append(ChatColor.RESET);
		if(!title.equals(""))
			shout.append(ChatColor.DARK_RED).append(ChatColor.BOLD).append("[").append(title)
			.append(ChatColor.DARK_RED).append(ChatColor.BOLD).append("] ").append(ChatColor.RESET);

		if (p.isOp())
			shout.append(ChatColor.GOLD).append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.HeadAdmin"))
			shout.append("[").append(ChatColor.BLACK).append("H").append(ChatColor.GOLD).append("A").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.AdminPlus"))
			shout.append("[").append(ChatColor.DARK_RED).append("A").append(ChatColor.YELLOW).append("+").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.Admin"))
			shout.append("[").append(ChatColor.DARK_RED).append("A").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.Mod"))
			shout.append("[").append(ChatColor.BLUE).append("M").append(ChatColor.RESET).append("] ").append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.PremiumPlus"))
			shout.append(ChatColor.BLUE).append(playerName).append(ChatColor.YELLOW).append("+").append(ChatColor.RESET);
		else if (p.hasPermission("Shout.Premium"))
			shout.append(ChatColor.BLUE).append(playerName).append(ChatColor.RESET);
		else if (p.hasPermission("Shout.VIPPlus"))
			shout.append(ChatColor.GREEN).append(playerName).append(ChatColor.YELLOW).append("+").append(ChatColor.RESET);
		else if (p.hasPermission("Shout.VIP"))
			shout.append(ChatColor.GREEN).append(playerName).append(ChatColor.RESET);
		else
			shout.append(playerName);

		shout.append(": ").append(ChatColor.BOLD).append(message);
		final Player players[] = this.instance.getServer().getOnlinePlayers();
		for(final Player player:players)
			if(!this.disabled.contains(player.getName()) && !(this.dead && !player.hasPermission("shout.bypass.kill")))
				player.sendMessage(shout.toString());
		this.instance.getLogger().info(shout.toString());
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
}