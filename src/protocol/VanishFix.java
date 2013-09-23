package protocol;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.sensationcraft.scgeneral.SCGeneral;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.User;

public class VanishFix extends PacketAdapter
{

	public VanishFix(final Plugin plugin)
	{
		super(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.PLAY_NOTE_BLOCK);
	}

	@Override
	public void onPacketSending(final PacketEvent event)
	{
		if (event.getPacketID() == Packets.Server.PLAY_NOTE_BLOCK)
		{
			final PacketContainer pc = event.getPacket();
			final int x = pc.getIntegers().read(0);
			final int y = pc.getIntegers().read(1);
			final int z = pc.getIntegers().read(2);
			if (pc.getIntegers().read(4) == 0)
				return;
			final Block b = event.getPlayer().getWorld().getBlockAt(x, y, z);
			if (b.getState() instanceof Chest)
			{
				final Chest chest = (Chest) b.getState();
				final List<HumanEntity> viewers = chest.getInventory().getViewers();
				if (viewers.isEmpty())
				{
					event.setCancelled(true);
					return;
				} else if (viewers.size() > 1)
					return;
				final HumanEntity he = viewers.get(0);
				if (he instanceof Player == false)
					return;
				final User u = SCGeneral.getEssentials().getUser(he);
				if (u.isVanished())
					event.setCancelled(true);
			}
		}
	}
}
