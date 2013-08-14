package me.superckl.scgeneral;

import lockpicks.Listeners;
import mcMMOFix.Fix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import Commands.AntiBear;
import Commands.ClearInvis;
import Commands.FactionCheck;
import Commands.Heal;
import Commands.Kick;
import Commands.KillShout;
import Commands.Repair;
import Commands.Shout;
import Commands.ShoutMute;
import Commands.StopCommand;
import Commands.Bans.Ban;
import Commands.Bans.GetBans;
import Commands.Bans.OverrideBan;
import Commands.Bans.ResetBans;
import Commands.Bans.Unban;
import Commands.help.HelpAccept;
import Commands.help.HelpCancel;
import Commands.help.HelpDeny;
import Commands.help.HelpList;
import Commands.help.HelpRead;
import Commands.help.HelpRequest;
import Commands.tp.TpSuite;
import Entity.EntityListener;
import FactionFix.HomeFix;
import Items.ItemLimiter;
import Items.SuperItems;
import Potion.PotionListener;
import mcMMOFix.DisarmBlocker;
import mcMMOFix.FactionParty;
import org.bukkit.command.CommandExecutor;


public class SCGeneral extends JavaPlugin
{
	private Scoreboard scoreboard;
	private Shout shout;

	@Override
	public void onEnable()
	{
		this.getLogger().info("[SCGeneral] Startup.");
		this.getLogger().info(" - Registering Faction fixes");
		this.getServer().getPluginManager().registerEvents(new HomeFix(), this);
                this.getLogger().info(" - Registering mcMMO disarm protect");
		this.getServer().getPluginManager().registerEvents(new DisarmBlocker(), this);
                this.getLogger().info(" - Registering mcMMO party control");
		this.getServer().getPluginManager().registerEvents(new FactionParty(this), this);
		this.getLogger().info(" - Registering mcMMO fixes");
		this.getServer().getPluginManager().registerEvents(new Fix(), this);
		this.getLogger().info(" - Registering ItemLimiter");
		this.getServer().getPluginManager().registerEvents(new ItemLimiter(), this);
		this.getLogger().info(" - Registering PotionPatch");
		this.getServer().getPluginManager().registerEvents(new PotionListener(), this);
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
		// No its not ignored, it just handles the shizzle in the constructor
		new StopCommand(this);
		this.shout = new Shout(this);
		this.getLogger().info("   - shout");
		final PluginCommand shoutCommand = this.getServer().getPluginCommand("shout");
		if(shoutCommand != null) {
			shoutCommand.setExecutor(this.shout);
			shoutCommand.setUsage("");
		} else
			this.getLogger().warning("Failed to override shout!");

		this.getLogger().info("   - shoutmute");
		final PluginCommand shoutToggleCommand = this.getServer().getPluginCommand("shoutmute");
		if(shoutToggleCommand != null) shoutToggleCommand.setExecutor(new ShoutMute(this));
		else
			this.getLogger().warning("Failed to override shoutmute!");

		this.getLogger().info("   - repair");
		final PluginCommand repairCommand = this.getServer().getPluginCommand("repair");
		if(repairCommand != null) 
                {
                    CommandExecutor ce = repairCommand.getExecutor();
                    repairCommand.setExecutor(new Repair(ce));
                }
		else
			this.getLogger().warning("Failed to override repair!");

		this.getLogger().info("   - factioncheck");
		final PluginCommand factionCheckCommand = this.getServer().getPluginCommand("factioncheck");
		if(factionCheckCommand != null)
			factionCheckCommand.setExecutor(new FactionCheck());
		else
			this.getLogger().warning("Failed to override factioncheck!");

		this.getLogger().info("   - clearinvis");
		final PluginCommand clearInvisCommand = this.getServer().getPluginCommand("clearinvis");
		if(clearInvisCommand != null)
			clearInvisCommand.setExecutor(new ClearInvis(this));
		else
			this.getLogger().warning("Failed to override clearinvis!");

		this.getLogger().info("   - kick");
		final PluginCommand kickCommand = this.getServer().getPluginCommand("kick");
		if(kickCommand != null) {
			kickCommand.setExecutor(new Kick(this));
			kickCommand.setUsage("");
		} else
			this.getLogger().warning("Failed to override kick!");
		this.getLogger().info("   - overrideban");
		final PluginCommand overbanCommand = this.getServer().getPluginCommand("overrideban");
		OverrideBan overBan = new OverrideBan(this);
		if(overbanCommand != null){
			overbanCommand.setExecutor(overBan);
			overbanCommand.setUsage("");
		} else
			this.getLogger().warning("Failed to override ban!");
		
		this.getLogger().info("   - resetbans");
		final PluginCommand resetBansCommand = this.getServer().getPluginCommand("resetbans");
		if(resetBansCommand != null)
			resetBansCommand.setExecutor(new ResetBans());
		else
			this.getLogger().warning("Failed to override resetbans!");
		
		this.getLogger().info("   - getbans");
		final PluginCommand getBansCommand = this.getServer().getPluginCommand("getbans");
		if(getBansCommand != null)
			getBansCommand.setExecutor(new GetBans());
		else
			this.getLogger().warning("Failed to override getbans!");
		
		this.getLogger().info("   - ban");
		final PluginCommand banCommand = this.getServer().getPluginCommand("ban");
		if(banCommand != null){
			banCommand.setExecutor(new Ban(this, overBan));
			banCommand.setUsage("");
		}else
			this.getLogger().warning("Failed to override ban!");

		this.getLogger().info("   - unban");
		final PluginCommand unbanCommand = this.getServer().getPluginCommand("unban");
		if(unbanCommand != null){
			unbanCommand.setExecutor(new Unban());
			unbanCommand.setUsage("");
		}else
			this.getLogger().warning("Failed to override unban!");
		
		this.getLogger().info("   - heal");
		final PluginCommand healCommand = this.getServer().getPluginCommand("heal");
		if(healCommand != null) {
			healCommand.setExecutor(new Heal(this));
			healCommand.setUsage("");
		} else
			this.getLogger().warning("Failed to override heal!");

		this.getLogger().info("   - tpa");
		final TpSuite tpsuite = new TpSuite();
		final PluginCommand tpaCommand = this.getServer().getPluginCommand("tpa");
		final PluginCommand tpaHereCommand = this.getServer().getPluginCommand("tpahere");
		final PluginCommand tpAcceptCommand = this.getServer().getPluginCommand("tpaccept");
		final PluginCommand tpDenyCommand = this.getServer().getPluginCommand("tpdeny");
		if(tpaCommand != null && tpaHereCommand != null && tpAcceptCommand != null && tpDenyCommand != null)
		{
			tpaCommand.setExecutor(tpsuite);
			tpaCommand.setUsage("");
			tpaHereCommand.setExecutor(tpsuite);
			tpaHereCommand.setUsage("");
			tpAcceptCommand.setExecutor(tpsuite);
			tpAcceptCommand.setUsage("");
			tpDenyCommand.setExecutor(tpsuite);
			tpDenyCommand.setUsage("");
		}else this.getLogger().warning("Failed to override teleportation!");

		this.getLogger().info("   - helprequest");
		final HelpRequest help = new HelpRequest();
		final PluginCommand helpRequestCommand = this.getServer().getPluginCommand("helprequest");
		if(helpRequestCommand != null)
			helpRequestCommand.setExecutor(help);
		else this.getLogger().warning("Failed to override helprequest!");

		this.getLogger().info("   - helpread");
		final PluginCommand helpReadCommand = this.getServer().getPluginCommand("helpread");
		if(helpReadCommand != null)
			helpReadCommand.setExecutor(new HelpRead(help));
		else this.getLogger().warning("Failed to override helpread!");

		this.getLogger().info("   - helplist");
		final PluginCommand helpListCommand = this.getServer().getPluginCommand("helplist");
		if(helpListCommand != null)
			helpListCommand.setExecutor(new HelpList(help));
		else this.getLogger().warning("Failed to override helplist!");

		this.getLogger().info("   - helpaccept");
		final PluginCommand helpAcceptCommand = this.getServer().getPluginCommand("helpaccept");
		if(helpAcceptCommand != null)
			helpAcceptCommand.setExecutor(new HelpAccept(help));
		else this.getLogger().warning("Failed to override helpaccept!");

		this.getLogger().info("   - helpdeny");
		final PluginCommand helpDenyCommand = this.getServer().getPluginCommand("helpdeny");
		if(helpDenyCommand != null)
			helpDenyCommand.setExecutor(new HelpDeny(help));
		else this.getLogger().warning("Failed to override helpdeny!");

		this.getLogger().info("   - helpcancel");
		final PluginCommand helpCancelCommand = this.getServer().getPluginCommand("helpcancel");
		if(helpCancelCommand != null)
			helpCancelCommand.setExecutor(new HelpCancel(help));
		else this.getLogger().warning("Failed to override helpcancel!");

		this.getLogger().info("   - killshout");
		final PluginCommand killShout = this.getServer().getPluginCommand("killshout");
		if(killShout != null)
			killShout.setExecutor(new KillShout(this.shout));
		else this.getLogger().warning("Failed to override killshout!");
                
                /*this.getLogger().info("   - party");
                final PluginCommand party = this.getServer().getPluginCommand("party");
                if(party != null)
                        party.setExecutor(new NullParty());
                else this.getLogger().warning("Failed to override party (is mcMMO enabled?)");*/

		final EntityListener entity = new EntityListener(help, this);
		final PluginCommand antibear = this.getServer().getPluginCommand("antibear");
		if(antibear != null)
			antibear.setExecutor(new AntiBear(entity));

		this.getLogger().info(" - Registering EntityListener");
		this.getServer().getPluginManager().registerEvents(entity, this);
		this.getLogger().info(" - Registering Super items");
		this.getServer().getPluginManager().registerEvents(new SuperItems(), this);
		this.getLogger().info("[SCGeneral] SCGeneral enabled.");

		new BukkitRunnable()
		{
			private int counter = 0;
			private final String messageOne = (new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Donate for exclusive ranks www.sensationcraft.info").toString();
			private final String messageTwo = (new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("Don't forget to vote every day for free diamonds").toString();
			private final String messageThree = (new StringBuilder()).append(ChatColor.GREEN).append(ChatColor.BOLD).append("If you need help, submit a help request with '/helprequest'").toString();

			@Override
			public void run()
			{
				switch(this.counter){
				case 0:
					Bukkit.broadcastMessage(this.messageOne);
					this.counter++;
					break;
				case 1:
					Bukkit.broadcastMessage(this.messageTwo);
					this.counter++;
					break;
				case 2:
					Bukkit.broadcastMessage(this.messageThree);
					this.counter = 0;
					break;
				}
			}
		}.runTaskTimer(this, 20*30L, 20*30L);
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public Shout getShout() {
		return this.shout;
	}

	@SuppressWarnings("deprecation")
	public static void updateInvWithSuppressedWarning(final Player player){
		player.updateInventory();
	}
}