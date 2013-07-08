package me.superckl.scgeneral;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import lockpicks.Listeners;

import net.minecraft.server.v1_6_R1.SharedConstants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import Commands.Ban;
import Commands.ClearInvis;
import Commands.FactionCheck;
import Commands.Heal;
import Commands.Kick;
import Commands.Shout;
import Commands.ShoutMute;
import Commands.TpAccept;
import Commands.TpDeny;
import Commands.Tpa;
import Commands.TpaHere;
import Entity.EntityListener;
import Items.ItemLimiter;


public class SCGeneral extends JavaPlugin
{
	private Scoreboard scoreboard;
	private Shout shout;

	@Override
	public void onEnable()
	{
		this.getLogger().info("[SCGeneral] Startup.");
		this.getLogger().info(" - Registering Scoreboard");
		try {
			this.ModifyAllowedCharacters();
			this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
			final Objective objective = this.scoreboard.registerNewObjective("showHealth", ChatColor.RED+"\u2665");
			objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			objective.setDisplayName("Health");
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException
				| IllegalStateException e) {
			this.getLogger().severe("Failed to create health scoreboard!");
			e.printStackTrace();
		}
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
		
		this.shout = new Shout(this);
		this.getLogger().info("   - shout");
		final PluginCommand shoutCommand = this.getServer().getPluginCommand("shout");
		if(shoutCommand != null) {
			shoutCommand.setExecutor(this.shout);
			shoutCommand.setUsage("");
		} else {
			this.getLogger().warning("Failed to override shout!");
		}

		this.getLogger().info("   - shoutmute");
		final PluginCommand shoutToggleCommand = this.getServer().getPluginCommand("shoutmute");
		if(shoutToggleCommand != null) shoutToggleCommand.setExecutor(new ShoutMute(this));
		else {
				this.getLogger().warning("Failed to override shoutmute!");
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
			kickCommand.setUsage("");
		} else {
			this.getLogger().warning("Failed to override kick!");
		}
		this.getLogger().info("   - ban");
		final PluginCommand banCommand = this.getServer().getPluginCommand("ban");
		if(banCommand != null){
			banCommand.setExecutor(new Ban(this));
			banCommand.setUsage("");
		}
		 else {
				this.getLogger().warning("Failed to override ban!");
			}
		this.getLogger().info("   - heal");
		final PluginCommand healCommand = this.getServer().getPluginCommand("heal");
		if(healCommand != null) {
			healCommand.setExecutor(new Heal(this));
			healCommand.setUsage("");
		} else {
			this.getLogger().warning("Failed to override heal!");
		}
		
		this.getLogger().info("   - tpa");
		Tpa tpa = null;
		final PluginCommand tpaCommand = this.getServer().getPluginCommand("tpa");
		if(tpaCommand != null){
			tpa = new Tpa(this);
			tpaCommand.setExecutor(tpa);
			tpaCommand.setUsage("");
		}else this.getLogger().warning("Failed to override tpa!");
		
		this.getLogger().info("   - tpahere");
		TpaHere tpaHere = null;
		final PluginCommand tpaHereCommand = this.getServer().getPluginCommand("tpahere");
		if(tpaHereCommand != null && tpa != null){
			tpaHere = new TpaHere(tpa, this);
			tpaHereCommand.setExecutor(tpaHere);
			tpaHereCommand.setUsage("");
			tpa.setTpaHere(tpaHere);
		}else this.getLogger().warning("Failed to override tpahere!");
		
		this.getLogger().info("   - tpaccept");
		final PluginCommand tpAcceptCommand = this.getServer().getPluginCommand("tpaccept");
		if(tpAcceptCommand != null && tpa != null && tpaHere != null){
			tpAcceptCommand.setExecutor(new TpAccept(tpa, tpaHere));
			tpAcceptCommand.setUsage("");
			tpAcceptCommand.getAliases().add("tpaaccept");
		}else this.getLogger().warning("Failed to override tpaccept!");
		
		this.getLogger().info("   - tpdeny");
		final PluginCommand tpDenyCommand = this.getServer().getPluginCommand("tpdeny");
		if(tpDenyCommand != null && tpa != null && tpaHere != null){
			tpDenyCommand.setExecutor(new TpDeny(tpa, tpaHere));
			tpDenyCommand.setUsage("");
			tpDenyCommand.getAliases().add("tpadeny");
		}else this.getLogger().warning("Failed to override tpdeny!");

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

	public Shout getShout() {
		return this.shout;
	}
	 public void ModifyAllowedCharacters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	 {
	  Field field = SharedConstants.class.getDeclaredField("allowedCharacters");
	  field.setAccessible(true);
	  Field modifiersField = Field.class.getDeclaredField( "modifiers" );
	  modifiersField.setAccessible( true );
	  modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	  String oldallowedchars = (String)field.get(null);
	  String suits = "\u2665";//\u2666\u2663\u2660
	  StringBuilder sb = new StringBuilder();
	  sb.append( oldallowedchars );
	  sb.append( suits );
	  field.set( null, sb.toString() );
	 }
}