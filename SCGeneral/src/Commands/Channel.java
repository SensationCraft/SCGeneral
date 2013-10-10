package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.ChannelChangeEvent;
import org.sensationcraft.scgeneral.ChatChannel;

/**
 *
 * @author DarkSeraphim
 */
public class Channel implements CommandExecutor
{
    
    private final String changed = ChatColor.GREEN+"You are talking in "+ChatColor.YELLOW+"%s"+ChatColor.GREEN+".";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(sender instanceof Player == false)
        {
            sender.sendMessage("Only ingame players can switch channels");
            return true;
        }
        
        if(args.length < 1)
        {
            sender.sendMessage("/channel global|local");
            return true;
        }
        
        ChatChannel channel;
        if(args[0].isEmpty())
            channel = ChatChannel.NONE;
        else
        {
            char c = args[0].charAt(0);
            switch(c)
            {
                case 'g':
                    channel = ChatChannel.GLOBAL;
                    break;
                case 'l':
                    channel = ChatChannel.LOCAL;
                    break;
                default:
                    channel = ChatChannel.NONE;
            }
        }
        if(channel == ChatChannel.NONE)
        {
            sender.sendMessage(ChatColor.DARK_RED+String.format("Unknown channel '%s'", args[0]));
            return true;
        }
        
        sender.sendMessage(String.format(changed, channel.name()));
        
        Bukkit.getPluginManager().callEvent(new ChannelChangeEvent(sender.getName(), channel));
        
        return true;
    }
    
}
