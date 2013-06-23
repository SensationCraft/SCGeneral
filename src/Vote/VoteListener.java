package Vote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener implements Listener
{

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onVote(final VotifierEvent e)
	{
		final Vote vote = e.getVote();
		final String name = vote.getUsername();
		Bukkit.broadcastMessage((new StringBuilder()).append(ChatColor.BLUE).append(name).append(" has voted for free diamonds. Get your own free diamonds at www.sensationcraft.info/votenow").toString());
		final Player player = Bukkit.getPlayer(name);
		if(player != null)
		{
			player.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));
		}
	}
}