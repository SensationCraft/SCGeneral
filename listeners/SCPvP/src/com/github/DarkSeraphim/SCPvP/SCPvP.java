package com.github.DarkSeraphim.SCPvP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.sensationcraft.login.event.SCLoginRegisterEvent;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

/**
 *
 * @author DarkSeraphim
 */
public class SCPvP extends Addon implements Listener
{

	// Needs no saving, this is currently handled by yaml
	private final Map<String, Long> protection = new HashMap<String, Long>();
	private final Object lock = new Object();

	// 2 hours of protection
	private final long PROTECTION_TIME = 1000*3600*2;

	// Removed protection message
	private final String NO_LONGER_PROTECTED = ChatColor.AQUA+""+ChatColor.BOLD+"You are no longer immune to PvP combat.";

	// Now protected + terms of protection
	private final String NOW_PROTECTED = ChatColor.AQUA+""+ChatColor.BOLD+"You are protected from PvP combat for the next 2 hours. This effect will cancel if voluntarily engage in combat.";

	// Protected message for the attacker
	private final String PROTECTED = ChatColor.AQUA+""+ChatColor.BOLD+"This player is temporarily immune to PvP damage.";

	// No abusing the immunity
	private final String NO_INTERACT = "&b&lYou cannot interact with containers or signs that are not on your Faction land when protected".replace('&', ChatColor.COLOR_CHAR);
	private final String NO_HOMESET = "&b&lYou cannot set home when not on your/unclaimed land when protected".replace('&', ChatColor.COLOR_CHAR);

	private File save;

	public SCPvP(final SCGeneral scg, final AddonDescriptionFile desc)
	{
		super(scg, desc);
	}

	@Override
	public void onEnable()
	{
		if(!this.getPlugin().getConfig().isConfigurationSection("ranks"))
		{
			final ConfigurationSection sec = this.getPlugin().getConfig().createSection("ranks");
			sec.set("0", "");
			sec.set("10", "scout");
			this.getPlugin().saveConfig();
		}
		this.save = new File(this.getPlugin().getDataFolder(), "protection.dat");
		final YamlConfiguration yc = YamlConfiguration.loadConfiguration(this.save);
		synchronized(this.lock)
		{
			for(final String player : yc.getKeys(false))
				this.protection.put(player, yc.getLong(player, 0L));
		}
	}

	@Override
	public void onDisable()
	{
		final YamlConfiguration yc = new YamlConfiguration();
		synchronized(this.lock)
		{
			for(final Map.Entry<String, Long> protectionEntry : this.protection.entrySet())
				if(protectionEntry.getValue() != null)
					yc.set(protectionEntry.getKey(), protectionEntry.getValue());
		}

		if(!this.save.exists())
			try
		{
				this.save.getParentFile().mkdirs();
				if(!this.save.createNewFile())
					throw new IOException("Could not make a savefile");
		}
		catch(final IOException ex)
		{
			this.getPlugin().getLogger().log(Level.SEVERE, "Could not create savefile {0}", this.save.getAbsolutePath());
			return;
		}

		try
		{
			yc.save(this.save);
		}
		catch(final IOException ex)
		{
			this.getPlugin().getLogger().log(Level.SEVERE, "Could not save to savefile {0}", this.save.getAbsolutePath());
		}
	}



	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(final EntityDamageByEntityEvent event)
	{
		if(event.getEntity() instanceof Player == false) return;
		final Player attacked = (Player) event.getEntity();
		Player attacker = null;
		boolean isPvP = event.getDamager() instanceof Player;
		if(isPvP)
			attacker = (Player) event.getDamager();
		if(event.getDamager() instanceof Projectile)
			isPvP = ((Projectile)event.getDamager()).getShooter() instanceof Player;
		if(!isPvP) return;

		if(attacker == null)
			attacker = (Player) ((Projectile)event.getDamager()).getShooter();

		if(this.isProtected(attacked))
		{
			event.setDamage(0);
			event.setCancelled(true);
			attacker.sendMessage(this.PROTECTED);
			return;
		}

		if(this.isProtected(attacker))
			this.removeProtection(attacker);
	}

	@EventHandler
	public void onRegister(final SCLoginRegisterEvent event)
	{
		final String name = event.getName();
		synchronized(this.lock)
		{
			if(this.protection.containsKey(name.toLowerCase())) return;
			this.protection.put(name.toLowerCase(), System.currentTimeMillis() + this.PROTECTION_TIME);
		}
		final Player player = Bukkit.getPlayerExact(name);
		if(player != null)
			player.sendMessage(this.NOW_PROTECTED);
	}

	public boolean isProtected(final Player player)
	{
		final String name = player.getName().toLowerCase();
		Long l;
		synchronized(this.lock)
		{
			l = this.protection.get(name);
		}
		if(l != null && l.longValue() > System.currentTimeMillis())
			return true;
		else if(l != null)
			synchronized(this.lock)
			{
				this.removeProtection(player);
			}
		return false;
	}

	public void removeProtection(final Player player)
	{
		synchronized(this.lock)
		{
			this.protection.remove(player.getName().toLowerCase());
		}
		player.sendMessage(this.NO_LONGER_PROTECTED);
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
	public void onInteract(final PlayerInteractEvent event)
	{
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		final BlockState state = event.getClickedBlock().getState();
		if(!(state instanceof Sign || state instanceof InventoryHolder))
			return;

		if(!this.isProtected(event.getPlayer()))
			return;

		final FPlayer fme = FPlayers.i.get(event.getPlayer());
		if(fme.getFaction().isNone() || Board.getFactionAt(new FLocation(event.getClickedBlock().getLocation())) != fme.getFaction())
		{
			event.setCancelled(true);
			fme.getPlayer().sendMessage(this.NO_INTERACT);
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onCommand(final PlayerCommandPreprocessEvent event)
	{
		final String cmd = event.getMessage().toLowerCase();
		if(cmd.startsWith("/sethome") || cmd.startsWith("/esethome"))
		{
			if(!this.isProtected(event.getPlayer()))
				return;

			final Faction fac = Board.getFactionAt(new FLocation(event.getPlayer().getLocation()));

			if(fac.isNormal() && FPlayers.i.get(event.getPlayer()).getFaction() != fac)
			{
				event.setCancelled(true);
				event.setMessage("/nope");
				event.getPlayer().sendMessage(this.NO_HOMESET);
			}
		}
	}

}
