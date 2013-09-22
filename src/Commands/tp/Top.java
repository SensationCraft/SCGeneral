package Commands.tp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author DarkSeraphim
 */
public class Top implements CommandExecutor
{

    private final String msg = "&6Teleporting to top.".replace('&', ChatColor.COLOR_CHAR);
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage(ChatColor.RED+"This command can only be used by players");
            return true;
        }
        
        if(!sender.hasPermission("essentials.top"))
        {
            sender.sendMessage(ChatColor.RED+"You do not have the permissions to use his command");
            return true;
        }
        
        Player player = (Player) sender;
        Location loc = player.getLocation().clone();
        loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        player.teleport(loc);
        player.sendMessage(this.msg);
        return true;
    }
}
