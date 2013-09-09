package protocol;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VanishFix extends PacketAdapter
{

    private final IEssentials ess;

    public VanishFix(Plugin plugin)
    {
        super(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.PLAY_NOTE_BLOCK);
        Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");
        if (p != null)
        {
            ess = (IEssentials) p;
        }
        else
        {
            ess = null;
        }
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        if (ess == null)
        {
            return;
        }
        if (event.getPacketID() == Packets.Server.PLAY_NOTE_BLOCK)
        {
            PacketContainer pc = event.getPacket();
            int x = pc.getIntegers().read(0);
            int y = pc.getIntegers().read(1);
            int z = pc.getIntegers().read(2);
            if (pc.getIntegers().read(4) == 0)
            {
                return;
            }
            Block b = event.getPlayer().getWorld().getBlockAt(x, y, z);
            if (b.getState() instanceof Chest)
            {
                Chest chest = (Chest) b.getState();
                List<HumanEntity> viewers = chest.getInventory().getViewers();
                if (viewers.isEmpty())
                {
                    event.setCancelled(true);
                    return;
                }
                else
                {
                    if (viewers.size() > 1)
                    {
                        return;
                    }
                }
                HumanEntity he = viewers.get(0);
                if (he instanceof Player == false)
                {
                    return;
                }
                User u = this.ess.getUser((Player) he);
                if (u.isVanished())
                {
                    event.setCancelled(true);
                }
            }
        }
    }
}
