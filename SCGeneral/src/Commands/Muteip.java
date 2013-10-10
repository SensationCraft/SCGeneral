package Commands;

import com.earth2me.essentials.utils.DateUtil;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public class Muteip implements CommandExecutor
{
    private final SCGeneral plugin;
    
    public Muteip(SCGeneral plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        _onCommand(sender, cmd, label, args);
        return true;
    }
    
    public void _onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!sender.hasPermission("essentials.mute"))
        {
            sender.sendMessage(ChatColor.DARK_RED+"You do not have sufficient permissions.");
            return;
        }
        
        if(args.length < 1)
        {
            sender.sendMessage("/mute <player> [time]");
            return;
        }
        
        Player muted = Bukkit.getPlayer(args[0]);
        if(muted == null)
        {
            sender.sendMessage(ChatColor.DARK_RED+"Player not found.");
            return;
        }
        
        long expire = Long.MAX_VALUE;
        
        if(args.length > 1)
        {
            String date = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
            try
            {
                long diff = DateUtil.parseDateDiff(date, true);
                expire = System.currentTimeMillis()+diff;
            }
            catch(Exception ex)
            {
                sender.sendMessage(ChatColor.DARK_RED+"Invalid date format.");
                return;
            }
        }
        if(this.plugin.getData().hasKey(null, "ipmutes"))
        {
            @SuppressWarnings("unchecked")
			Map<String, Long> ipmutes = this.plugin.getData().get(Map.class, "ipmutes");
            String ip = getIp(muted);
            if(ipmutes.containsKey(ip))
                ipmutes.remove(ip);
            else
                ipmutes.put(ip, expire);
        }
    }
    
    public String getIp(Player player)
    {
        return player.getAddress().getAddress().getHostAddress();
    }
}
