package Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class NullParty implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        cs.sendMessage(ChatColor.RED+"Parties have been disabled!");
        return true;
    }

}
