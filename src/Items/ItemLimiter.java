package Items;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.yi.acru.bukkit.Lockette.Lockette;

public class ItemLimiter implements Listener
{
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		final Material block = event.getBlock().getType();
		final Player p = event.getPlayer();
		if(p.isOp())
			return;
		/*if(block == Material.FIRE)
		{
			if(!p.hasPermission("itemlimiter.special"))
			{
				event.setCancelled(true);
				p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You must be a donator to place fire!").toString());
			}
		}*/
		else if(block == Material.SKULL)
		{
			final byte data = event.getBlock().getData();
			if(!p.isOp() && data == 1)
			{
				event.setCancelled(true);
				p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You can't place Wither Skulls!").toString());
			}
		}
	}

	/*@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		final Player p = event.getPlayer();
		if(p.isOp())
			return;
		if(p.hasPermission("itemlimiter.special"))
			return;
		if(event.getBucket().equals(Material.WATER_BUCKET))
		{
			event.setCancelled(true);
			p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You must be a donator to do this!").toString());
			p.getItemInHand().setType(Material.WATER_BUCKET);
		}
		else if(event.getBucket().equals(Material.LAVA_BUCKET))
		{
			event.setCancelled(true);
			p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You must be a donator to do this!").toString());
			p.getItemInHand().setType(Material.LAVA_BUCKET);
		}
	}*/
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventoryMove(final InventoryMoveItemEvent event)
	{
		final Inventory src = event.getSource();
		final Inventory initiator = event.getInitiator();
		if((initiator.getHolder() instanceof Hopper || initiator.getHolder() instanceof HopperMinecart)
				&& src.getHolder() instanceof BlockState)
		{
			final Block b = ((BlockState)src.getHolder()).getBlock();
			if(Lockette.isProtected(b))
			{
				event.setCancelled(true);
                                // Note: please, don't. You will crash the server.
				//((BlockState)initiator.getHolder()).getBlock().breakNaturally();
			}
		}
	}
	//@EventHandler(priority = EventPriority.MONITOR)
	public void onItemPickup(final PlayerPickupItemEvent e){
		final ItemPrices mat = ItemPrices.translateMaterial(e.getItem().getItemStack().getType());
		if(mat == null)
			return;
		final ItemMeta meta = e.getItem().getItemStack().getItemMeta();
		final List<String> lore = meta.getLore();
		lore.clear();
		lore.add(ChatColor.GREEN+""+ChatColor.BOLD+"This item can be sold for $"+mat.getPrice()+" at the shop.");
		meta.setLore(lore);
		e.getItem().getItemStack().setItemMeta(meta);
	}
}