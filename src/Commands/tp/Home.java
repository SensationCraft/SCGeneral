package Commands.tp;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
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
public class Home implements CommandExecutor
{

    private final IEssentials ess;
    
    public Home()
    {
        this.ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage(ChatColor.RED+"Only ingame players can have homes.");
            return true;
        }
        
        Player player = (Player) sender;
        
        User user = this.ess.getUser(player);
        
        String homename = "";
        
        User from = user;
        
        if(args.length == 0 && (from.getHomes().isEmpty() || (from.getHomes().size() == 1 && from.getHomes().get(0).equalsIgnoreCase("home"))))
        {
            homename = "home";
        }
        else if(args.length > 0)
        {
            if(args[0].contains(":"))
            {
                String[] split = new String[]{args[0].substring(0, args[0].indexOf(":")), args[0].substring(args[0].indexOf(":")+1)};
                if(split.length > 1 && sender.hasPermission("essentials.home.others"))
                {
                    User other = this.ess.getOfflineUser(split[0]);
                    if(other != null)
                    {
                        from = other;
                        homename = split[1];
                    }
                }
            }
            else
            {
                homename = args[0];
            }
        }
        
        if(from.getHomes().isEmpty())
        {
            if(user == from)
                player.sendMessage(ChatColor.GOLD+"You haven't set your home yet. Use /sethome to set it.");
            else
                player.sendMessage(ChatColor.GOLD+"Player has not set a home.");
            return true;
        }
        
        Location home;
        try
        {
            home = from.getHome(homename);
        }
        catch(Exception ex)
        {
            sender.sendMessage(ChatColor.GOLD+"Error: home location not found!");
            return true;
        }
        
        if(home == null)
        {
            player.sendMessage(ChatColor.GOLD+"Homes: "+ChatColor.RESET+Joiner.on(", ").join(from.getHomes()));
        }
        else
            player.teleport(home);
        return true;
    }
    
}
