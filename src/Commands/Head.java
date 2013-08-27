package Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Head implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if(arg0 instanceof Player == false){
			arg0.sendMessage(ChatColor.RED+"Only players can use this command!");
			return false;
		}
		if(!arg0.hasPermission("scgeneral.head")){
			arg0.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		if(arg3.length != 1){
			arg0.sendMessage(ChatColor.RED+"Syntax Error. Proper usage: '/head {name}");
		}
		if(arg3[0].length() > 16 || !arg3[0].matches("[a-zA-Z0-9_\\-]*")){
			arg0.sendMessage(ChatColor.RED+"That can't be a username!");
			return false;
		}
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwner(arg3[0]);
		head.setItemMeta(meta);
		((Player)arg0).getInventory().addItem(head);
		return true;
	}

}
