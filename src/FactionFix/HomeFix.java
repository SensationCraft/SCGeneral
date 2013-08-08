package FactionFix;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author DarkSeraphim
 */
public class HomeFix implements Listener
{
    
    private final Essentials ess;
    
    public HomeFix()
    {
        Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");
        if(p != null)
        {
            this.ess = (Essentials) p;
        }
        else
            this.ess = null;
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if(this.ess == null) return;
        String[] msg = event.getMessage().substring(1).split(" ");
        String cmd = msg[0];
        if(cmd.equalsIgnoreCase("sethome") || cmd.equalsIgnoreCase("esethome"))
        {
            FLocation loc = new FLocation(event.getPlayer().getLocation());
            Faction fac = Board.getFactionAt(loc);
            Faction fme = FPlayers.i.get(event.getPlayer()).getFaction();
            if(fme.getRelationTo(fac).isEnemy())
            {
                event.getPlayer().sendMessage(ChatColor.RED+"You cannot set your home inside enemy territory!");
                event.setMessage("/peniscraft");
                event.setCancelled(true);
            }
        }
        else if(cmd.equalsIgnoreCase("home") || cmd.equalsIgnoreCase("ehome"))
        {
            if(msg.length > 1)
            {
                User user = this.ess.getUser(event.getPlayer());
                Location home;
                try
                {
                    home = user.getHome(msg[1]);
                    if(home == null)
                    {
                        home = user.getHome();
                    }
                    if(home == null) return;
                }
                catch (Exception ex)
                {
                    return;
                }
                Faction fme = FPlayers.i.get(event.getPlayer()).getFaction();
                FLocation loc = new FLocation(home);
                if(Board.getFactionAt(loc).getRelationTo(fme) == Relation.ENEMY)
                {
                    event.getPlayer().sendMessage(ChatColor.RED+"You cannot teleport to your home inside enemy territory!");
                    event.setMessage("/peniscraft");
                    event.setCancelled(true);
                }
            }
        }
        
    }

}
