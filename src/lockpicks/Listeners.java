package lockpicks;

import java.util.HashMap;
import java.util.Map;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.yi.acru.bukkit.PluginCore;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class Listeners implements Listener
{

	private final SCGeneral main;

	public Listeners(final SCGeneral main)
	{
		this.main = main;
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamageEvent(final EntityDamageEvent e)
	{
		final Entity ent = e.getEntity();
		if((ent instanceof Player) && this.picking.containsKey(((Player)ent).getName()))
		{
			final int id = this.picking.get(((Player)ent).getName()).intValue();
			Bukkit.getScheduler().cancelTask(id);
			this.picking.remove(((Player)ent).getName());
			final Player player = ((Player)ent).getPlayer();
			player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Picking cancelled! (You got attacked)").toString());
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getItemInHand().getTypeId() == 383 && e.getClickedBlock().getTypeId() == 68)
		{
			final Player player = e.getPlayer();
			final Block block = PluginCore.getSignAttachedBlock(e.getClickedBlock());
			final Sign sign = (Sign)e.getClickedBlock().getState();
			if(sign.getLine(0).equalsIgnoreCase("[private]"))
				if(this.picking.containsKey(e.getPlayer().getName()))
					e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("You are already picking a lock!").toString());
				else
				{
					final boolean vip = player.hasPermission("lockpicks.vip");
					final boolean premium = player.hasPermission("lockpicks.premium");
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
						if(premium)
						{
							final int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, new Premium(this, e), 100L);
							this.picking.put(e.getPlayer().getName(), Integer.valueOf(id));
						}
						else if(vip)
						{
							final int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, new Vip(this, e), 100L);
							this.picking.put(e.getPlayer().getName(), Integer.valueOf(id));
						}
						else
						{
							final int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, new Default(this, e), 100L);
							this.picking.put(e.getPlayer().getName(), Integer.valueOf(id));
						}
					} else
						e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("You can only pick locks in the Wilderness!").toString());
				}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerMoveEvent(final PlayerMoveEvent e)
	{
		final String name = e.getPlayer().getName();
		if(this.picking.containsKey(name))
		{
			final int id = this.picking.get(name).intValue();
			Bukkit.getScheduler().cancelTask(id);
			this.picking.remove(name);
			final Player player = e.getPlayer();
			player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Picking cancelled! (You moved)").toString());
		}
	}

	public Map<String, Integer> picking = new HashMap<String, Integer>();

}