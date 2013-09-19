package Items;

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
import org.bukkit.inventory.Inventory;
import org.yi.acru.bukkit.Lockette.Lockette;

public class ItemLimiter implements Listener
{
	public ItemLimiter()
	{
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		final Material block = event.getBlock().getType();
		final Player p = event.getPlayer();
		if(p.isOp())
			return;
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
				event.setCancelled(true);
		}
	}
}