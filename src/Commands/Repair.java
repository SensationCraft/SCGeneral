package Commands;

import Items.SuperItems;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DarkSeraphim
 */
public class Repair implements CommandExecutor
{
    
    private final String repairAllMsg = "&6You have successfully repaired your: &c%s".replace('&', ChatColor.COLOR_CHAR);
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage(ChatColor.RED+"Console cannot repair items, you silly :3");
            return true;
        }
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
        
        Player player = (Player) sender;
        if(args[0].equalsIgnoreCase("hand"))
        {
            ItemStack i = player.getItemInHand();
            
            List<String> lore = null;
            if(i.getItemMeta() != null)
                lore = i.getItemMeta().getLore();
            if(i.getType().isBlock() || i.getType().getMaxDurability() < 1)
            {
                player.sendMessage(ChatColor.DARK_RED+"Item cannot be repaired");
            }
            else if(i.getDurability() == 0)
            {
                player.sendMessage(ChatColor.DARK_RED+"Item did not need a repair");
            }
            else if(lore != null && lore.contains(SuperItems.tag))
            {
                player.sendMessage(ChatColor.DARK_RED+"You cannot repair super items");
            }
            else
                i.setDurability((short)0);
        }
        else if(args[0].equalsIgnoreCase("all"))
        {
            List<String> repaired = new ArrayList<String>();
            repairItems(player.getInventory().getContents(), repaired);
            repairItems(player.getInventory().getArmorContents(), repaired);
            if(repaired.isEmpty())
            {
                player.sendMessage(ChatColor.DARK_RED+"There were no items that needed repair");
            }
            else
            {
                player.sendMessage(String.format(repairAllMsg, Joiner.on(", ").join(repaired)));
                player.updateInventory();
            }
        }
        else
        {
            sender.sendMessage(ChatColor.DARK_RED+"Correct usage: /repair all|hand");
        }        
        return true;
    }
    
    private void repairItems(ItemStack[] iss, List<String> repaired)
    {
        for(ItemStack is : iss)
        {
            if(is == null || is.getItemMeta() == null)
                continue;
            List<String> lore = is.getItemMeta().getLore();
            if(is.getType().isBlock() || is.getType().getMaxDurability() < 1)
            {
                continue;
            }
            else if(is.getDurability() == 0)
            {
                continue;
            }
            else if(lore != null && lore.contains(SuperItems.tag))
            {
                continue;
            }
            else
            {
                is.setDurability((short)0);
                repaired.add(is.getType().name().replace('_', ' ').toLowerCase());
            }
        }
    }

}
