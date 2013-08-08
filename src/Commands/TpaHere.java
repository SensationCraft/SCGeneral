package Commands;

import Commands.TpSuite.TpRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import me.superckl.combatlogger.CombatLogger;

public class TpaHere
{

    private final CombatLogger combatLogger;
    private final Essentials ess;

    public TpaHere()
    {
        this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
        this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    public void execute(Player player, TpSuite suite, TpRequest req, String[] args)
    {
        if (args.length == 0)
        {
            player.sendMessage(ChatColor.RED + "You need to enter a player's name!");
            return;
        }

        Player other = Bukkit.getPlayerExact(args[0]);
        if (other == null)
        {
            player.sendMessage(ChatColor.DARK_RED + "Player offline.");
            return;
        }

        if (!ess.getUser(player.getName()).isTeleportEnabled())
        {
            player.sendMessage(ChatColor.RED + "That player has teleportation disabled!");
            return;
        }

        if (req != null)
        {
            final Player third = Bukkit.getPlayerExact(req.getRequester());
            if (third != null)
            {
                third.sendMessage(ChatColor.RED + String.format("%s has cancelled your teleport request.", player.getName()));
            }
        }

        suite.request(player.getName(), other.getName(), true);
        player.sendMessage(ChatColor.GOLD + "Request sent to " + other.getName() + ".");
        other.sendMessage(ChatColor.GOLD + player.getName() + " would like to teleport to you " + ChatColor.RED + "to them" + ChatColor.GOLD + ":");
        other.sendMessage(ChatColor.GOLD + "'" + ChatColor.GREEN + "/tpaccept" + ChatColor.GOLD + "' to accept.");
        other.sendMessage(ChatColor.GOLD + "'" + ChatColor.RED + "/tpdeny" + ChatColor.GOLD + "' to deny.");
    }
}
