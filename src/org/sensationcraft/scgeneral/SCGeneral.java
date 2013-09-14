package org.sensationcraft.scgeneral;

import java.util.HashMap;
import java.util.Map;

import lockpicks.Listeners;
import mcMMOFix.DisarmBlocker;
import mcMMOFix.DupeFix;
import mcMMOFix.ExpFarmFix;
import mcMMOFix.FactionParty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import Commands.ClearInvis;
import Commands.Delhomes;
import Commands.Expel;
import Commands.FactionCheck;
import Commands.Head;
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
import com.comphenix.protocol.ProtocolLibrary;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.LocationUtil.Vector3D;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import protocol.VanishFix;


public class SCGeneral extends JavaPlugin
{
	private Scoreboard scoreboard;
	private Shout shout;
	private HelpRequest help;
	
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
		this.getServer().getPluginManager().registerEvents(new DupeFix(), this);
		this.getServer().getPluginManager().registerEvents(new ExpFarmFix(this), this);
		this.getLogger().info(" - Registering ItemLimiter");
		this.getServer().getPluginManager().registerEvents(new ItemLimiter(), this);
		this.getLogger().info(" - Registering PotionPatch");
		this.getServer().getPluginManager().registerEvents(new PotionListener(), this);
		this.getLogger().info(" - Registering LockPicks");
		this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
		this.getLogger().info(" - Registering EntityListener");
                this.help = new HelpRequest();
                final EntityListener entity = new EntityListener(this.help, this);
                this.getLogger().info(" - Registering Chest packet filter for vanish ;)");
                ProtocolLibrary.getProtocolManager().addPacketListener(new VanishFix(this));
		this.getServer().getPluginManager().registerEvents(entity, this);
		this.getLogger().info(" - Fixing some Essentials 'safe' (actually glitching) teleporting");
                fixEssentialsTp();
		this.getLogger().info(" - Registering Super items");
		this.getServer().getPluginManager().registerEvents(new SuperItems(), this);
		this.getLogger().info(" - Overriding commands");
		this.overrideCommands(this.help);
		this.getLogger().info(" - Starting save-all loop");
		new BukkitRunnable(){
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			}
		}.runTaskTimer(this, 6000L, 6000L);
		this.getLogger().info(" - Starting broadcast loop");
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
		this.getLogger().info("[SCGeneral] SCGeneral enabled.");
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

	private final Map<String, CommandExecutor> commandMap = new HashMap<>();

	private void initializeCommandMap(final HelpRequest help){
		this.commandMap.clear();
		// No its not ignored, it just handles the shizzle in the constructor
		new StopCommand(this);
		this.shout = new Shout(this);
		this.commandMap.put("shout", this.shout);
		this.commandMap.put("shoutmute", new ShoutMute(this));
		final PluginCommand repairCommand = this.getServer().getPluginCommand("repair");
		if(repairCommand != null)
			this.commandMap.put("repair", new Repair(repairCommand.getExecutor()));
		this.commandMap.put("factioncheck", new FactionCheck());
		this.commandMap.put("clearinvis", new ClearInvis());
		this.commandMap.put("delhomes", new Delhomes());
		this.commandMap.put("kick", new Kick(this));
		final OverrideBan overBan = new OverrideBan(this);
		this.commandMap.put("overrideban", overBan);
		this.commandMap.put("ban", new Ban(this, overBan));
		this.commandMap.put("resetBans", new ResetBans());
		this.commandMap.put("getbans", new GetBans());
		this.commandMap.put("unban", new Unban());
		this.commandMap.put("heal", new Heal(this));
		final TpSuite tpsuite = new TpSuite();
		this.commandMap.put("tpa", tpsuite);
		this.commandMap.put("tpahere", tpsuite);
		this.commandMap.put("tpaccept", tpsuite);
		this.commandMap.put("tpdeny", tpsuite);
		this.commandMap.put("tpcheck", tpsuite);
		this.commandMap.put("killshout", new KillShout(this.shout));
		this.commandMap.put("head", new Head());
		this.commandMap.put("expel", new Expel());
		this.commandMap.put("helprequest", help);
		this.commandMap.put("helpread", new HelpRead(help));
		this.commandMap.put("helplist", new HelpList(help));
		this.commandMap.put("helpaccept", new HelpAccept(help));
		this.commandMap.put("helpdeny", new HelpDeny(help));
		this.commandMap.put("helpcancel", new HelpCancel(help));
	}
	private void overrideCommands(final HelpRequest help){
		this.initializeCommandMap(help);
		for(final String name:this.commandMap.keySet()){
			this.getLogger().info("   - "+name);
			final PluginCommand pCommand = this.getServer().getPluginCommand(name);
			if(pCommand == null){
				this.getLogger().warning("Failed to override "+name);
				continue;
			}
			final CommandExecutor command = this.commandMap.get(name);
			pCommand.setExecutor(command);
			pCommand.setUsage("");
		}
		this.commandMap.clear();
	}
        private void fixEssentialsTp()
        {
            try
            {
                Field f = LocationUtil.class.getDeclaredField("VOLUME");
                if(!f.isAccessible())
                    f.setAccessible(true);
                int mods = f.getModifiers();
                if((mods & Modifier.FINAL) != 0)
                {
                    Field m = Field.class.getDeclaredField("modifiers");
                    if(!m.isAccessible())
                        m.setAccessible(true);
                    m.setInt(f, mods & ~Modifier.FINAL);
                }
                f.set(null, new Vector3D[]{});
            }
            catch(Exception ex)
            {
                getLogger().warning("Failed to fix Essentials safe teleportation");
                // We failed, too bad
                ex.printStackTrace();
            }
            
        }
}