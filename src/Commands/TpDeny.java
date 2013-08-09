package Commands;

import Commands.TpSuite.TpRequest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TpDeny
{

    public TpDeny()
    {
    }

    public void execute(Player player, TpRequest req)
    {
        final Player other = Bukkit.getPlayerExact(req.getRequester());
        if (other != null)
        {
            other.sendMessage(ChatColor.RED + String.format("%s has cancelled your teleport request.", player.getName()));
        }
        player.sendMessage(ChatColor.GOLD+String.format("You denied %s's request", req.getRequester()));
    }
}
