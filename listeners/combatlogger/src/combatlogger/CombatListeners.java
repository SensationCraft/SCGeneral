package combatlogger;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

import com.massivecraft.factions.FPlayers;

public class CombatListeners extends Addon implements Listener
{

	public CombatListeners(final SCGeneral scg, final AddonDescriptionFile desc) {
		super(scg, desc);
	}

	private Map<String, Integer> fakeFlyExempts = new HashMap<String, Integer>();

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		if(!this.hasData(Map.class, "flyexempts"))
			this.setData("flyexempts", new HashMap<String, Integer>());
		this.fakeFlyExempts = this.getData(Map.class, "flyexmepts");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerAttack(final EntityDamageByEntityEvent e)
	{
		final Entity damager = e.getDamager();
		final Entity damaged = e.getEntity();
		if (!(damaged instanceof Player))
			return;
		final Player damagedPlayer = (Player) damaged;
		Player damagerPlayer = null;

		if (damager instanceof Player)
			damagerPlayer = (Player) damager;
		else if (damager instanceof Arrow)
		{
			final Arrow damagerArrow = (Arrow) damager;
			final LivingEntity shooter = damagerArrow.getShooter();
			if (shooter instanceof Player)
				damagerPlayer = (Player) shooter;
		}
		else if (damager instanceof ThrownPotion)
		{
			final ThrownPotion damagerPotion = (ThrownPotion) damager;
			final LivingEntity thrower = damagerPotion.getShooter();
			if (thrower instanceof Player)
				damagerPlayer = (Player) thrower;
		}

		if (damagerPlayer == null)
			return;

		SCGeneral.getUser(damagedPlayer.getName()).setInCombat(true);
		SCGeneral.getUser(damagerPlayer.getName()).setInCombat(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent e)
	{
		e.setDeathMessage(null);
		SCGeneral.getUser(e.getEntity().getName()).setInCombat(false);
		final ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		final SkullMeta meta = (SkullMeta) it.getItemMeta();
		meta.setOwner(e.getEntity().getName());
		it.setItemMeta(meta);
		e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), it);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(final PlayerQuitEvent e)
	{
		e.setQuitMessage(null);
		if (SCGeneral.getUser(e.getPlayer().getName()).isInCombat())
		{
			e.getPlayer().damage(32767);
			Bukkit.broadcastMessage(ChatColor.YELLOW + e.getPlayer().getName() + " " + ChatColor.DARK_PURPLE + "has been punished for logging out during combat!");
		}
		SCGeneral.getUser(e.getPlayer().getName()).setInCombat(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommandPre(final PlayerCommandPreprocessEvent e)
	{
		if (SCGeneral.getUser(e.getPlayer().getName()).isInCombat())
		{
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Command are disabled during PvP!");
			return;
		}
		/*String[] split = e.getMessage().split(" ");
         if(split.length<=1) return;
         if(split[0].equalsIgnoreCase("/heal") || split[0].equalsIgnoreCase("/eheal")){
         Player player = Bukkit.getPlayer(split[1]);
         if(player == null) return;
         if(this.inCombat.containsKey(player.getName())){
         e.setCancelled(true);
         e.getPlayer().sendMessage(ChatColor.AQUA+"You can't heal players that are in combat!");
         }
         }else if(split[0].equalsIgnoreCase("/kick") || split[0].equalsIgnoreCase("/ekick")){
         Player player = Bukkit.getPlayer(split[1]);
         if(player == null) return;
         if(this.inCombat.containsKey(player.getName())){
         e.setCancelled(true);
         e.getPlayer().sendMessage(ChatColor.AQUA+"You can't kick players that are in combat!");
         }
         }*/
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTeleport(final PlayerTeleportEvent event)
	{
		if (event.getCause() != TeleportCause.COMMAND && event.getCause() != TeleportCause.PLUGIN)
			return;
		if (SCGeneral.getUser(event.getPlayer().getName()).isInCombat())
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot teleport while in combat!");
			event.setTo(event.getFrom());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onMove(final PlayerMoveEvent event)
	{
		if (SCGeneral.getUser(event.getPlayer().getName()).isInCombat())
		{
			final Location fhome = FPlayers.i.get(event.getPlayer()).getFaction().getHome();
			if(this.sameLocation(event.getTo(), fhome))
				if(event.getFrom().distanceSquared(fhome) > 3)
				{
					final String name = event.getPlayer().getName();
					Integer count = this.fakeFlyExempts.get(name);
					if(count == null)
						this.fakeFlyExempts.put(name, 1);
					else
						this.fakeFlyExempts.put(name, ++count);
					event.setCancelled(true);
				}
		}
	}

	private boolean sameLocation(final Location a, final Location b)
	{
		if(a == null || b == null)
			return false;
		return a.getBlockX() == b.getBlockX()
				&& Math.abs(a.getBlockY() - b.getBlockY()) < 10
				&& a.getBlockZ() == b.getBlockZ();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onMove2(final PlayerMoveEvent event)
	{
		//System.out.println(event.getFrom());
		//System.out.println(event.getTo());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent e)
	{
		e.setJoinMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(final PlayerKickEvent e)
	{
		e.setLeaveMessage(null);
		System.out.println(e.getReason());
		if(e.getReason().toLowerCase().startsWith("kicked for flying"))
		{
			final String name = e.getPlayer().getName();
			Integer i = this.fakeFlyExempts.get(name);
			if(i == null || i.intValue() < 1)
				return;
			this.fakeFlyExempts.put(name, --i);
			if(SCGeneral.getUser(name).isInCombat())
				e.setCancelled(true);
		}
	}
}
