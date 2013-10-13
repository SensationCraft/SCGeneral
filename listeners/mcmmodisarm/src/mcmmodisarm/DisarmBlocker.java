package mcmmodisarm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;

/**
 *
 * @author DarkSeraphim
 */
public class DisarmBlocker extends Addon implements Listener
{

	public DisarmBlocker(final SCGeneral scg, final AddonDescriptionFile desc) {
		super(scg, desc);
	}

	public static final String tag = "&r&5Disarm protect".replace('&', ChatColor.COLOR_CHAR);

	@EventHandler
	public void onDisarmed(final McMMOPlayerDisarmEvent event)
	{
		final Player defender = event.getDefender();
		if (this.isProtected(defender.getItemInHand()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onSignChange(final SignChangeEvent event)
	{
		if (event.getBlock().getType() != Material.WALL_SIGN)
			return;
		if (!event.getLine(0).equalsIgnoreCase("[purchase]"))
			return;
		if (!event.getPlayer().isOp())
		{
			event.setCancelled(true);
			event.setLine(0, "");
			event.setLine(1, "");
			event.setLine(2, "");
			event.setLine(3, "");
		}
		if (this.check(event))
		{
			event.setLine(0, ChatColor.BLUE + "[Purchase]");
			event.setLine(1, ChatColor.RED + event.getLine(1));
			// Basically leave line 3 alone
			//event.setLine(2, event.getLine(2));
			event.setLine(3, "");
		}
	}

	private boolean check(final SignChangeEvent event)
	{
		if(event.getLine(1).equalsIgnoreCase("protect") && event.getLine(2).length() > 1)
			try
		{
				final double d = Double.parseDouble(event.getLine(2).substring(1));
				return d > 0;
		}
		catch(final NumberFormatException ex)
		{
			event.setLine(3, "INVALID CHARGE");
			return false;
		}
		event.setLine(3, "UNKNOWN SIGN");
		return false;
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event)
	{
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(event.getClickedBlock().getType() != Material.WALL_SIGN)
			return;
		final Sign sign = (Sign) event.getClickedBlock().getState();

		String line = ChatColor.stripColor(sign.getLine(0)).toLowerCase();
		if(!line.equals("[purchase]"))
			return;
		line = ChatColor.stripColor(sign.getLine(1)).toLowerCase();
		if(!line.equals("protect"))
			return;

		final Player player = event.getPlayer();
		final ItemStack hand = player.getItemInHand();
		if(hand == null || hand.getType().isBlock() || hand.getType().getMaxDurability() < 1)
		{
			player.sendMessage(ChatColor.RED+"You cannot protect that ;)");
			return;
		}
		if(this.isProtected(hand))
		{
			player.sendMessage(ChatColor.RED+"That is already protected");
			return;
		}

		BigDecimal bd;

		try
		{
			bd = BigDecimal.valueOf(Double.valueOf(sign.getLine(2).substring(1)));
		}
		catch(final NumberFormatException ex)
		{
			player.sendMessage(ChatColor.RED+"Price seems to be invalid. Warn a member of staff.");
			return;
		}

		try
		{
			if(!Economy.hasEnough(player.getName(), bd))
			{
				player.sendMessage(ChatColor.RED+"You don't have enough money to protect your item");
				return;
			}
			Economy.substract(player.getName(), bd);
		}
		catch(final UserDoesNotExistException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Player {0} does not exist?", player.getName());
			player.sendMessage(ChatColor.RED+"Something unknown has occurred... Contact a member of staff.");
			return;
		}
		catch(final NoLoanPermittedException ex)
		{
			player.sendMessage(ChatColor.RED+"You don't have enough money to protect your item");
			return;
		}
		this.tagit(hand);
		player.sendMessage(ChatColor.GREEN+"Protected the item");
	}

	public boolean isProtected(final ItemStack i)
	{
		if (i == null)
			return false;
		final ItemMeta meta = i.getItemMeta();
		if (meta == null)
			return false;
		final List<String> lore = meta.getLore();
		if (lore == null)
			return false;
		return lore.contains(DisarmBlocker.tag);
	}

	private void tagit(final ItemStack i)
	{
		if(i == null)
			return;
		final ItemMeta meta = i.getItemMeta();
		if(meta == null)
			return;
		List<String> lore = meta.getLore();
		if(lore == null)
			lore = new ArrayList<String>();
		lore.add(DisarmBlocker.tag);
		meta.setLore(lore);
		i.setItemMeta(meta);
	}

}
