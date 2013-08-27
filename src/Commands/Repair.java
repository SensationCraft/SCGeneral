package Commands;

import java.util.ArrayList;
import java.util.List;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.commands.skills.RepairCommand;
import com.google.common.base.Joiner;

public class Repair implements CommandExecutor
{

	private final RepairCommand mcMMOCommand;

	public Repair(final CommandExecutor ce)
	{
		if(ce instanceof RepairCommand)
			this.mcMMOCommand = (RepairCommand) ce;
		else
			this.mcMMOCommand = null;
	}

	private final String repairAllMsg = "&6You have successfully repaired your: &c%s".replace('&', ChatColor.COLOR_CHAR);

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if(sender instanceof Player == false)
		{
			sender.sendMessage(ChatColor.RED+"Console cannot repair items, you silly :3");
			return true;
		}
		if(label.equalsIgnoreCase("repair") && this.mcMMOCommand != null)
			return this.mcMMOCommand.onCommand(sender, cmd, label, args);

		if(!sender.hasPermission("essentials.repair"))
		{
			sender.sendMessage(ChatColor.DARK_RED+"You do not have the permission to do that!");
			return true;
		}
		if(args.length != 1)
		{
			sender.sendMessage(ChatColor.DARK_RED+"Correct usage: /repair all|hand");
			return true;
		}

		final Player player = (Player) sender;
		if(args[0].equalsIgnoreCase("hand"))
		{
			final ItemStack i = player.getItemInHand();

			if(i == null)
			{
				player.sendMessage(ChatColor.DARK_RED+"No item found");
				return true;
			}
			else if(i.getType().isBlock() || i.getType().getMaxDurability() < 1)
				player.sendMessage(ChatColor.DARK_RED+"Item cannot be repaired");
			else if(i.getDurability() == 0)
				player.sendMessage(ChatColor.DARK_RED+"Item did not need a repair");
			else
				i.setDurability((short)0);
		}
		else if(args[0].equalsIgnoreCase("all"))
		{
			final List<String> repaired = new ArrayList<String>();
			this.repairItems(player.getInventory().getContents(), repaired);
			this.repairItems(player.getInventory().getArmorContents(), repaired);
			if(repaired.isEmpty())
				player.sendMessage(ChatColor.DARK_RED+"There were no items that needed repair");
			else
			{
				player.sendMessage(String.format(this.repairAllMsg, Joiner.on(", ").join(repaired)));
				SCGeneral.updateInvWithSuppressedWarning(player);
			}
		} else
			sender.sendMessage(ChatColor.DARK_RED+"Correct usage: /repair all|hand");
		return true;
	}

	private void repairItems(final ItemStack[] iss, final List<String> repaired)
	{
		for(final ItemStack is : iss)
		{
			if(is == null || is.getItemMeta() == null)
				continue;
			if(is.getType().isBlock() || is.getType().getMaxDurability() < 1)
				continue;
			else if(is.getDurability() == 0)
				continue;
			else
			{
				is.setDurability((short)0);
				repaired.add(is.getType().name().replace('_', ' ').toLowerCase());
			}
		}
	}

}
