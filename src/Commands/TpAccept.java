package Commands;

import Commands.TpSuite.TpRequest;
import me.superckl.combatlogger.CombatLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpAccept {

    private final CombatLogger combatLogger;

    public TpAccept() 
    {
        this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
    }

    public void execute(Player player, TpRequest req)
    {
        Player other = Bukkit.getPlayerExact(req.getRequester());
        if(other == null)
        {
            player.sendMessage(ChatColor.RED+"Oops, seems the player was offline.");
            return;
        }
        
        if(this.combatLogger.getCombatListeners().isInCombat(other.getName()))
        {
            player.sendMessage(ChatColor.RED+"You cannot teleport to people while they are in combat!");
            return;
        }
        else if(this.combatLogger.getCombatListeners().isInCombat(player.getName()))
        {
            player.sendMessage(ChatColor.RED+"You cannot teleport to people while in combat!");
            return;
        }
        
        Location to;
        Player teleporter;
        if(req.isTpaHere())
        {
            teleporter = player;
            to = other.getLocation();
        }
        else
        {
            teleporter = other;
            to = player.getLocation();
        }
        
        teleporter.sendMessage(ChatColor.GOLD+"Teleporting...");
        teleporter.teleport(to);
    }
}
