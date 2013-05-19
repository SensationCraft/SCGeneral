package me.superckl.scgeneral;

import lockpicks.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import Entity.EntityListener;
import FactionCheck.FactionCheck;
import Items.ItemLimiter;
import Shout.Shout;


public class Main extends JavaPlugin
{
	private Shout shout;
	private FactionCheck factionCheck;
	private Scoreboard scoreboard;

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String cmd, final String args[])
	{
		if(cmd.toLowerCase().equals("shout") || cmd.toLowerCase().equals("s")){
			if(sender instanceof Player)
			{
				if(args.length > 0) {
					this.shout.doShout(((Player)sender).getPlayer(), args);
				}
				return true;
			}
			else
			{
				this.getLogger().warning("[Shout] Command can not be executed from console!");
				return false;
			}
		}else if(cmd.toLowerCase().equals("factioncheck") && sender.hasPermission("check.faction")) return this.factionCheck.checkFaction(sender, args);
		else if(cmd.toLowerCase().equals("clearinvis") && sender.hasPermission("scgeneral.clearinvis")){
			for(final Player player:this.getServer().getOnlinePlayers()) {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			sender.sendMessage(ChatColor.GREEN+"Invisibility cleared.");
			return true;
		}
		return false;
	}

	@Override
	public void onEnable()
	{
		this.shout = new Shout();
		this.factionCheck = new FactionCheck();
		this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
		final Objective objective = this.scoreboard.registerNewObjective("showHealth", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("Health");
		/*Bukkit.getWorld("world").regenerateChunk(-791, -1166);
        this.getLogger().info("regenerated chunk");
        Bukkit.getWorld("world").regenerateChunk(-795, -1166);
        this.getLogger().info("regenerated chunk");
        Bukkit.getWorld("world").regenerateChunk(-796, -1166);
        this.getLogger().info("regenerated chunk");
        Bukkit.getWorld("world").regenerateChunk(-794, -1167);
        this.getLogger().info("regenerated chunk");
        Bukkit.getWorld("world").regenerateChunk(-796, -1166);
        this.getLogger().info("regenerated chunk");
        Bukkit.getWorld("world").regenerateChunk(-795, -1167);
        this.getLogger().info("regenerated chunk");*/
		this.getLogger().info("[SCGeneral] Startup.");
		this.getLogger().info(" - Registering EntityListener");
		this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		this.getLogger().info(" - Registering ItemLimiter");
		this.getServer().getPluginManager().registerEvents(new ItemLimiter(), this);
		this.getLogger().info(" - Registering AutoSaving");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Autosave(), 6000L, 6000L);
		this.getLogger().info(" - Registering LockPicks");
		this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
		this.getLogger().info("[SCGeneral] SCGeneral enabled.");

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Bukkit.broadcastMessage((new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Donate for exclusive ranks www.sensationcraft.info").toString());

			}
		}.runTaskTimer(this, 20*30L, 20*60L);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Bukkit.broadcastMessage((new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Don't forget to vote every day for free diamonds").toString());
			}
		}.runTaskTimer(this, 20*60L, 20*60L);
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
}