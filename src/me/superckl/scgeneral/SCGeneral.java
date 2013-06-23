package me.superckl.scgeneral;

import lockpicks.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import Commands.ClearInvis;
import Commands.FactionCheck;
import Commands.Heal;
import Commands.Kick;
import Commands.Shout;
import Entity.EntityListener;
import Items.ItemLimiter;


public class SCGeneral extends JavaPlugin
{
	private Scoreboard scoreboard;

	@Override
	public void onEnable()
	{
		this.getLogger().info("[SCGeneral] Startup.");
		this.getLogger().info(" - Registering Scoreboard");
		this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
		final Objective objective = this.scoreboard.registerNewObjective("showHealth", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("Health");
		this.getLogger().info(" - Registering EntityListener");
		this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		this.getLogger().info(" - Registering ItemLimiter");
		this.getServer().getPluginManager().registerEvents(new ItemLimiter(), this);
		this.getLogger().info(" - Registering AutoSaving");
		new BukkitRunnable(){
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			}
		}.runTaskTimer(this, 6000L, 6000L);
		this.getLogger().info(" - Registering LockPicks");
		this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
		this.getLogger().info(" - Overriding commands");

		this.getLogger().info("   - shout");
		final PluginCommand shoutCommand = this.getServer().getPluginCommand("shout");
		if(shoutCommand != null) {
			shoutCommand.setExecutor(new Shout(this));
		} else {
			this.getLogger().warning("Failed to override shout!");
		}

		this.getLogger().info("   - factioncheck");
		final PluginCommand factionCheckCommand = this.getServer().getPluginCommand("factioncheck");
		if(factionCheckCommand != null) {
			factionCheckCommand.setExecutor(new FactionCheck());
		} else {
			this.getLogger().warning("Failed to override factioncheck!");
		}

		this.getLogger().info("   - clearinvis");
		final PluginCommand clearInvisCommand = this.getServer().getPluginCommand("clearinvis");
		if(clearInvisCommand != null) {
			clearInvisCommand.setExecutor(new ClearInvis(this));
		} else {
			this.getLogger().warning("Failed to override clearinvis!");
		}

		this.getLogger().info("   - kick");
		final PluginCommand kickCommand = this.getServer().getPluginCommand("kick");
		if(kickCommand != null) {
			kickCommand.setExecutor(new Kick(this));
		} else {
			this.getLogger().warning("Failed to override kick!");
		}

		this.getLogger().info("   - heal");
		final PluginCommand healCommand = this.getServer().getPluginCommand("heal");
		if(healCommand != null) {
			healCommand.setExecutor(new Heal(this));
		} else {
			this.getLogger().warning("Failed to override heal!");
		}

		this.getLogger().info("[SCGeneral] SCGeneral enabled.");

		new BukkitRunnable()
		{
			private boolean messageSwitch = false;
			private final String messageOne = (new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Donate for exclusive ranks www.sensationcraft.info").toString();
			private final String messageTwo = (new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Don't forget to vote every day for free diamonds").toString();

			@Override
			public void run()
			{
				if(this.messageSwitch) {
					Bukkit.broadcastMessage(this.messageOne);
				} else {
					Bukkit.broadcastMessage(this.messageTwo);
				}
				this.messageSwitch = !this.messageSwitch;
			}
		}.runTaskTimer(this, 20*30L, 20*30L);
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
}