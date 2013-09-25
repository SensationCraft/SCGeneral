package org.sensationcraft.scgeneral;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lockpicks.LockpickListeners;
import mcMMO.DisarmBlocker;
import mcMMO.FactionParty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import patch.DupeFix;
import patch.ExpFarmFix;
import patch.HacknGlitchPatch;
import patch.PotionPatch;
import protocol.VanishFix;
import Bounties.BountiesListeners;
import CombatLogger.CombatListeners;
import Commands.Bounty;
import Commands.CheckBounties;
import Commands.ClearInvis;
import Commands.Delhomes;
import Commands.Expel;
import Commands.FactionCheck;
import Commands.Head;
import Commands.Heal;
import Commands.Kick;
import Commands.KillShout;
import Commands.Repair;
import Commands.SeamlessReload;
import Commands.Shout;
import Commands.ShoutMute;
import Commands.StopCommand;
import Commands.Bans.Ban;
import Commands.Bans.GetBans;
import Commands.Bans.OverrideBan;
import Commands.Bans.ResetBans;
import Commands.Bans.Unban;
import Commands.Duel.AcceptCommand;
import Commands.Duel.CancelCommand;
import Commands.Duel.ChallengeCommand;
import Commands.Duel.DenyCommand;
import Commands.Duel.EndCommand;
import Commands.Duel.SpectateCommand;
import Commands.help.HelpAccept;
import Commands.help.HelpCancel;
import Commands.help.HelpDeny;
import Commands.help.HelpList;
import Commands.help.HelpRead;
import Commands.help.HelpRequest;
import Commands.tp.Home;
import Commands.tp.Top;
import Commands.tp.TpSuite;
import Duel.Arena;
import Duel.DuelListeners;
import Entity.EntityListener;
import Factions.HomeAlert;
import Items.ItemLimiter;
import Items.SuperItems;
import SilverfishBomb.SilverfishBombListener;

import com.comphenix.protocol.ProtocolLibrary;
import com.earth2me.essentials.Essentials;


public class SCGeneral extends JavaPlugin implements Listener
{
	private Scoreboard scoreboard;
	private Shout shout;
	private HelpRequest help;
	private Arena arena;
	private CombatListeners combatListeners;
	private static SCGeneral instance;
	private static Essentials essentials;
	private final Map<String, SCUser> scUsers = new HashMap<String, SCUser>();
	private final Map<String, ReloadableListener> listeners = new HashMap<>();

	@Override
	public void onEnable()
	{
		SCGeneral.instance = this;
		this.getServer().getPluginManager().registerEvents(this, this);
		SCGeneral.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if(SCGeneral.essentials == null || !SCGeneral.essentials.isEnabled()){
			this.getLogger().info("Essentials not found! Stopping server.");
			this.getServer().shutdown();
		}
		this.getLogger().info("[SCGeneral] Startup.");
		this.getLogger().info(" - Reading config and setting up arena");
		this.saveDefaultConfig();
		this.arena = this.makeArena();
		this.getLogger().info(" - Registering combat listeners");
		this.combatListeners = new CombatListeners();
		this.listeners.put(this.combatListeners.getClass().getSimpleName(), this.combatListeners);
		this.getServer().getPluginManager().registerEvents(this.combatListeners, this);
		this.getLogger().info("Registering Silverfish Bombs");
		SilverfishBombListener bombList = new SilverfishBombListener();
		this.listeners.put(bombList.getClass().getSimpleName(), bombList);
		this.getServer().getPluginManager().registerEvents(bombList, this);
		this.getLogger().info(" - Registering duel listeners");
		DuelListeners duelList = new DuelListeners();
		this.listeners.put(duelList.getClass().getSimpleName(), duelList);
		this.getServer().getPluginManager().registerEvents(duelList, this);
		this.getLogger().info(" - Registering Faction fixes");
		HomeAlert homealert = new HomeAlert();
		this.listeners.put(homealert.getClass().getSimpleName(), homealert);
		this.getServer().getPluginManager().registerEvents(homealert, this);
		this.getLogger().info(" - Registering mcMMO disarm protect");
		DisarmBlocker disarm = new DisarmBlocker();
		this.listeners.put(disarm.getClass().getSimpleName(), disarm);
		this.getServer().getPluginManager().registerEvents(disarm, this);
		this.getLogger().info(" - Registering mcMMO party control");
		FactionParty factionParty = new FactionParty();
		this.listeners.put(factionParty.getClass().getSimpleName(), factionParty);
		this.getServer().getPluginManager().registerEvents(factionParty, this);
		this.getLogger().info(" - Registering mcMMO fixes");
		DupeFix dupe = new DupeFix();
		this.listeners.put(dupe.getClass().getSimpleName(), dupe);
		this.getServer().getPluginManager().registerEvents(dupe, this);
		ExpFarmFix expfix = new ExpFarmFix();
		this.listeners.put(expfix.getClass().getSimpleName(), expfix);
		this.getServer().getPluginManager().registerEvents(expfix, this);
		this.getLogger().info(" - Registering ItemLimiter");
		ItemLimiter limiter = new ItemLimiter();
		this.listeners.put(limiter.getClass().getSimpleName(), limiter);
		this.getServer().getPluginManager().registerEvents(limiter, this);
		this.getLogger().info(" - Registering PotionPatch");
		PotionPatch potion = new PotionPatch();
		this.listeners.put(potion.getClass().getSimpleName(), potion);
		this.getServer().getPluginManager().registerEvents(potion, this);
		this.getLogger().info(" - Registering LockPicks");
		LockpickListeners lock = new LockpickListeners();
		this.listeners.put(lock.getClass().getSimpleName(), lock);
		this.getServer().getPluginManager().registerEvents(lock, this);
		this.getLogger().info(" - Registering Bounty");
		BountiesListeners bounty = new BountiesListeners();
		this.listeners.put(bounty.getClass().getSimpleName(), bounty);
		this.getServer().getPluginManager().registerEvents(bounty, this);
		this.getLogger().info(" - Registering Hack & Glitch patches");
		HacknGlitchPatch hacks = new HacknGlitchPatch();
		this.listeners.put(hacks.getClass().getSimpleName(), hacks);
		this.getServer().getPluginManager().registerEvents(hacks, this);
		this.getLogger().info(" - Registering Chest packet filter for vanish ;)");
		ProtocolLibrary.getProtocolManager().addPacketListener(new VanishFix(this));
		this.getLogger().info(" - Registering EntityListener");
		this.help = new HelpRequest();
		final EntityListener entity = new EntityListener();
		this.listeners.put(entity.getClass().getSimpleName(), entity);
		this.getServer().getPluginManager().registerEvents(entity, this);
		this.getLogger().info(" - Registering Super items");
		SuperItems superi = new SuperItems();
		this.listeners.put(superi.getClass().getSimpleName(), superi);
		this.getServer().getPluginManager().registerEvents(superi, this);
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

	@Override
	public void onDisable(){
		this.arena.forceEnd();
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

	private Arena makeArena(){
		final String world = this.getConfig().getString("World", "");
		final int x1 = this.getConfig().getInt("Point 1.x");
		final int y1 = this.getConfig().getInt("Point 1.y");
		final int z1 = this.getConfig().getInt("Point 1.z");
		final int x2 = this.getConfig().getInt("Point 2.x");
		final int y2 = this.getConfig().getInt("Point 2.y");
		final int z2 = this.getConfig().getInt("Point 2.z");
		final int cont1x = this.getConfig().getInt("Contestant 1.x");
		final int cont1y = this.getConfig().getInt("Contestant 1.y");
		final int cont1z = this.getConfig().getInt("Contestant 1.z");
		final int cont2x = this.getConfig().getInt("Contestant 2.x");
		final int cont2y = this.getConfig().getInt("Contestant 2.y");
		final int cont2z = this.getConfig().getInt("Contestant 2.z");
		final int specx = this.getConfig().getInt("Spectate.x");
		final int specy = this.getConfig().getInt("Spectate.y");
		final int specz = this.getConfig().getInt("Spectate.z");
		final World bWorld = this.getServer().getWorld(world);
		final Location point1loc = new Location(bWorld, x1, y1, z1);
		final Location point2loc = new Location(bWorld, x2, y2, z2);
		final Location specloc = new Location(bWorld, specx, specy, specz);
		final Location cont1loc = new Location(bWorld, cont1x, cont1y, cont1z);
		final Location cont2loc = new Location(bWorld, cont2x, cont2y, cont2z);
		return new Arena(this, point1loc.toVector(), point2loc.toVector(), specloc, cont1loc, cont2loc);
	}

	private final Map<String, CommandExecutor> commandMap = new HashMap<>();

	private void initializeCommandMap(final HelpRequest help){
		this.commandMap.clear();
		// No its not ignored, it just handles the shizzle in the constructor
		new StopCommand(this);
		this.shout = new Shout();
		this.commandMap.put("shout", this.shout);
		this.commandMap.put("shoutmute", new ShoutMute());
		final PluginCommand repairCommand = this.getServer().getPluginCommand("repair");
		if(repairCommand != null)
			this.commandMap.put("repair", new Repair(repairCommand.getExecutor()));
		this.commandMap.put("factioncheck", new FactionCheck());
		this.commandMap.put("clearinvis", new ClearInvis());
		this.commandMap.put("delhomes", new Delhomes());
		this.commandMap.put("kick", new Kick());
		final OverrideBan overBan = new OverrideBan();
		this.commandMap.put("overrideban", overBan);
		this.commandMap.put("ban", new Ban(overBan));
		this.commandMap.put("resetBans", new ResetBans());
		this.commandMap.put("getbans", new GetBans());
		this.commandMap.put("unban", new Unban());
		this.commandMap.put("heal", new Heal());
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
		this.commandMap.put("top", new Top());
		this.commandMap.put("home", new Home());
		this.commandMap.put("bounty", new Bounty());
		this.commandMap.put("checkbounties", new CheckBounties());
		this.commandMap.put("accept", new AcceptCommand());
		this.commandMap.put("cancel", new CancelCommand());
		this.commandMap.put("deny", new DenyCommand());
		final ChallengeCommand chal = new ChallengeCommand();
		this.commandMap.put("duel", chal);
		this.commandMap.put("challenge", chal);
		this.commandMap.put("end", new EndCommand(this));
		this.commandMap.put("spectate", new SpectateCommand());
		this.commandMap.put("seamlessreload", new SeamlessReload());
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		this.scUsers.put(e.getPlayer().getName(), new SCUser(e.getPlayer()));
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent e){
		this.scUsers.remove(e.getPlayer().getName());
	}

	public Arena getArena(){
		return this.arena;
	}
	public CombatListeners getCombatListeners() {
		return this.combatListeners;
	}
	public HelpRequest getHelp(){
		return this.help;
	}
	public Map<String, ReloadableListener> getListeners(){
		return this.listeners;
	}
	public static SCUser getUser(final String name){
		return SCGeneral.instance.scUsers.get(name);
	}
	public static Map<String, SCUser> getSCUsers(){
		return SCGeneral.instance.scUsers;
	}
	public static SCGeneral getInstance(){
		return SCGeneral.instance;
	}
	public static Essentials getEssentials(){
		return SCGeneral.essentials;
	}
	public void reloadListener(ReloadableListener listener){
		//TODO DOESN'T ACTUALLY RELOAD FROM FILE FIX DAT SHIT MARK
		listener.prepareForReload();
		HandlerList.unregisterAll(listener);
		this.listeners.remove(listener.getClass().getSimpleName());
		String name = listener.getClass().getName();
		try {
			Method method = JavaPluginLoader.class.getDeclaredMethod("removeClass0", String.class);
			method.setAccessible(true);
			method.invoke(this.getPluginLoader(), name);
			@SuppressWarnings("unchecked")
			Class<? extends ReloadableListener> clazz = (Class<? extends ReloadableListener>) Class.forName(name);
			ReloadableListener newListener = clazz.newInstance();
			this.getServer().getPluginManager().registerEvents(newListener, this);
			newListener.finishReload();
			this.listeners.put(newListener.getClass().getSimpleName(), newListener);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
			this.getLogger().severe("Failed to reload "+name);
		}
	}

	public void setCombatListeners(CombatListeners combatListeners) {
		this.combatListeners = combatListeners;
	}
}