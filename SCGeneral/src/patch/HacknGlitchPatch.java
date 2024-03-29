package patch;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;

/**
 *
 * @author S129977
 */
public class HacknGlitchPatch implements Listener
{

	private final Set<String> pickup = new HashSet<String>();
	private final String pickupMsg = ChatColor.GOLD + "Picking up items has been %s" + ChatColor.GOLD + ".";
	private final String ena = ChatColor.GREEN + "enabled";
	private final String dis = ChatColor.RED + "disabled";

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if ((e.getMaterial() == Material.ENDER_PEARL && this.checkBlock(e.getPlayer(), null)))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Glitching is bad :(");
			e.setCancelled(true);
			return;
		}
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.isCancelled())
			return;
		if (e.getClickedBlock().getState() instanceof Chest == false)
			return;
		final User user = SCGeneral.getEssentials().getUser(e.getPlayer().getName());
		if (user == null)
			return;
		if (user.isVanished())
		{
			e.setCancelled(true);
			e.getPlayer().openInventory(((Chest) e.getClickedBlock().getState()).getInventory());
			e.getPlayer().sendMessage(ChatColor.AQUA + "Silent chest editting brought to you by the wonderful developers of SC. ;D");
		}
	}

	/*
	 * Anti door glitching...
	 * Walking through a closed door, uh
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onDoorGlitchAttempt(final PlayerMoveEvent event)
	{
		if (this.isDoorGlitchAttempt(event.getFrom(), event.getTo()))
		{
			event.setCancelled(true);
			event.setTo(event.getFrom());
			final Player player = event.getPlayer();
			final Location loc = event.getFrom();
			new BukkitRunnable()
			{

				@Override
				public void run()
				{
					player.teleport(loc, PlayerTeleportEvent.TeleportCause.UNKNOWN);
				}
			}.runTaskLater(SCGeneral.getInstance(), 2L);
		}
	}

	/**
	 * Anti-forcefield
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent e)
	{
		if (e.getDamage() == 0.0)
			return;
		if (e.getEntity() instanceof Player == false)
			return;
		if (e.getDamager() instanceof Player == false)
			return;

		final Player attacker = (Player) e.getDamager();
		final Player attacked = (Player) e.getEntity();
		if (!attacked.canSee(attacker) && !attacker.hasPermission("essentials.vanish.pvp"))
		{
			e.setCancelled(true);
			return;
		}

		final Vector entloc = attacked.getLocation().toVector();
		final Vector damloc = attacker.getLocation().toVector();
		final Vector attackdir = entloc.subtract(damloc).setY(0).normalize();
		final Vector hitdir = attacker.getLocation().getDirection().setY(0).normalize();

		final double angle = (attackdir.angle(hitdir) / (Math.PI * 2) * 360);

		if (angle > 40)
		{
			e.setCancelled(true);
			System.out.println("Blocked aimbot for " + ((Player) e.getDamager()).getName() + ": " + angle);
			return;
		}
		if (this.checkBlock(attacker, attacked))
		{
			attacker.sendMessage(ChatColor.RED + "Glitching is bad :(");
			e.setCancelled(true);
		}

	}

	private boolean checkBlock(final Player attacker, final Player attacked)
	{
		boolean flag = false;
		Block b;
		int len = 2;
		if (attacked != null)
			len = (int) Math.floor(attacker.getLocation().distance(attacked.getLocation()));
		for (final Block block : attacker.getLineOfSight(null, len))
			if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK)
			{
				b = block;
				if ((block.getData() & 8) != 0)
					b = b.getRelative(BlockFace.DOWN);
				if ((b.getData() & 4) == 0)
				{
					// 1 == BlockFace.NORTH
					if (attacked != null)
					{
						if (attacked.getLocation().getBlock().equals(b))
						{
							final BlockFace facing = this.getDoorFace(b.getData());
							final Vector face = new Vector(facing.getModX(), facing.getModY(), facing.getModZ());
							flag = face.angle(attacked.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize()) > 75.5;
						}
					} else
						flag = true;
					if (flag)
						break;
				}
			}

		//Door.isOpen is deprecated, using openable to supress the warning.
		if(flag)
			return true;
		else if(len > 0)
			for (final Block block : attacker.getLineOfSight(null, len))
				if (block.getType().isSolid())
					return true;
		return false;
	}

	private BlockFace getDoorFace(final byte data)
	{
		switch (data)
		{
		case 0:
			return BlockFace.WEST;
		case 1:
			return BlockFace.NORTH;
		case 2:
			return BlockFace.EAST;
		case 3:
			return BlockFace.SOUTH;
		default:
			return BlockFace.SELF;
		}
	}

	public boolean isDoorGlitchAttempt(final Location from, final Location to)
	{
		final Block bf = from.getBlock();
		if (bf.getType() != Material.WOODEN_DOOR && bf.getType() != Material.IRON_DOOR_BLOCK)
			return false;
		final Block bt = bf.getRelative(this.getDoorFace(bf.getData()));
		if (!to.getBlock().equals(bt))
			return false;

		Block door = bf;
		if ((door.getData() & 8) != 0)
			door = door.getRelative(BlockFace.DOWN);
		if ((door.getData() & 4) == 0)
			return true;
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent e)
	{
		if (e.getMessage().toLowerCase().startsWith("/op ") || e.getMessage().equalsIgnoreCase("/op"))
		{
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "Op can only be given from the console!");
		} /*else if (SCGeneral.getInstance().getShout().isDead() && (e.getMessage().startsWith("/me ") || e.getMessage().startsWith("/eme ")))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Shout is currently disabled! Try again later.");
			e.setCancelled(true);
			e.setMessage("/cockblocked");
		}*/ else if (e.getMessage().startsWith("/?"))
			e.setMessage(e.getMessage().replace("/?", "/help"));
		else if (e.getMessage().startsWith("/togglepickup"))
		{
			final Player player = e.getPlayer();
			final User u = SCGeneral.getEssentials().getUser(player);
			if (u.isVanished())
			{
				String val;
				if (this.pickup.contains(player.getName()))
				{
					this.pickup.remove(player.getName());
					val = this.ena;
				}
				else
				{
					this.pickup.add(player.getName());
					val = this.dis;
				}
				player.sendMessage(String.format(this.pickupMsg, val));
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPickup(final PlayerPickupItemEvent event)
	{
		final User user = SCGeneral.getEssentials().getUser(event.getPlayer());
		if (user.isVanished() && this.pickup.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
}
