package lockpicks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.sensationcraft.scgeneral.SCGeneral;
import org.yi.acru.bukkit.PluginCore;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Listener;

public class LockpickListeners implements Listener
{

	private Map<String, BukkitTask> picking = new HashMap<String, BukkitTask>();
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamageEvent(final EntityDamageEvent e)
	{
		final Entity ent = e.getEntity();
		if((ent instanceof Player))
		{
			final BukkitTask task = this.picking.remove(((Player)ent).getName());
			if(task != null){
				task.cancel();
				final Player player = ((Player)ent).getPlayer();
				player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Picking cancelled! (You got attacked)").toString());
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getItemInHand().getTypeId() == 383 && e.getClickedBlock().getTypeId() == 68)
		{
			final Block block = PluginCore.getSignAttachedBlock(e.getClickedBlock());
			final Sign sign = (Sign)e.getClickedBlock().getState();
			if(sign.getLine(0).equalsIgnoreCase("[private]"))
				if(this.picking.containsKey(e.getPlayer().getName()))
					e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("You are already picking a lock!").toString());
				else
				{
					final Location loc = block.getLocation();
					final FLocation floc = new FLocation(loc);
					final Faction f = Board.getFactionAt(floc);
					if(f.isNone())
					{
						final ItemStack it = e.getPlayer().getItemInHand();
						it.setAmount(it.getAmount() - 1);
						if(it.getAmount() < 1)
							it.setType(Material.AIR);
						e.getPlayer().setItemInHand(it);
						e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.DARK_AQUA).append("Picking Lock...").toString());
						this.picking.put(e.getPlayer().getName(), new BukkitRunnable(){

							@Override
							public void run() {
								if(LockPickRank.getByPlayer(e.getPlayer()).tryPick()){
									e.getClickedBlock().setTypeId(0);
									e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.FIRE_IGNITE, 2.0F, 1.0F);
									e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Lock Pick successful!").toString());
								} else
									e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Pick failed!").toString());
								LockpickListeners.this.picking.remove(e.getPlayer().getName());
							}

						}.runTaskLater(SCGeneral.getInstance(), 100L));
					} else
						e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("You can only pick locks in the Wilderness!").toString());
				}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerMoveEvent(final PlayerMoveEvent e)
	{
		final BukkitTask task = this.picking.remove(e.getPlayer().getName());
		if(task != null)
		{
			task.cancel();
			final Player player = e.getPlayer();
			player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Picking cancelled! (You moved)").toString());
		}
	}

}