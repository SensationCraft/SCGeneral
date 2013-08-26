package Commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Delhomes implements CommandExecutor
{

    public Essentials ess;
    
    private final String msg = "&6Deleted homes:&c %s".replace('&', ChatColor.COLOR_CHAR);
    
    public Delhomes()
    {
        Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");
        if(p == null)
        {
            return;
        }
        this.ess = (Essentials) p;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage(ChatColor.RED+"Only ingame players can use this command");
            return true;
        }
        if(args.length < 1 || !args[0].matches("[0-9]*"))
        {
            sender.sendMessage(ChatColor.RED+"Usage: /delhomes <radius>: -1 for all homes");
            return true;
        }
        int r = Integer.parseInt(args[0]);
        if(r >= 0)
            r = (int) Math.pow(r, 2);
        else
           r = Integer.MAX_VALUE;
        
        User u = this.ess.getUser((Player)sender);
        
        Location loc = u.getLocation();
        
        List<String> deleted = new ArrayList<String>();
        
        for(String home : u.getHomes())
        {
            try
            {
                Location homeloc = u.getHome(home);
                if(homeloc == null)
                    throw new Exception("Home not found");
                if(loc.distanceSquared(homeloc) < r)
                {
                    u.delHome(home);
                    deleted.add(home);
                }
            }
            catch (Exception ex)
            {
                // Silence, my dear
            }
        }
        if(!deleted.isEmpty())
            sender.sendMessage(String.format(msg, Joiner.on(", ").join(deleted)));
        else
            sender.sendMessage(ChatColor.RED+"No homes found within the radius");
        return true;
    }

}
