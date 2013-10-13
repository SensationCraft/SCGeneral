package vote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

import com.vexsoftware.votifier.model.VotifierEvent;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class VoteListener extends Addon implements Listener
{


	public VoteListener(final SCGeneral scg, final AddonDescriptionFile desc) {
		super(scg, desc);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onVote(final VotifierEvent e)
	{
		final String name = e.getVote().getUsername();
		Bukkit.broadcastMessage((new StringBuilder()).append(ChatColor.BLUE).append(name).append(" has voted for free diamonds. Get your own free diamonds at www.sensationcraft.info/votenow").toString());
		final Player player = Bukkit.getPlayer(name);
		if(player != null)
			player.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));
	}
}