package mcMMOFix;

import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyTeleportEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.player.UserManager;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class FactionParty implements Listener
{
    
    Plugin plugin;
    
    public FactionParty(Plugin plugin)
    {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPartyChange(McMMOPartyChangeEvent event)
    {
        FPlayer fme = FPlayers.i.get(event.getPlayer());
        switch(event.getReason())
        {
            case CHANGED_PARTIES:
            case JOINED_PARTY:
                FPlayer newLeader = FPlayers.i.get(PartyAPI.getPartyLeader(event.getNewParty()));
                if(newLeader.getRelationTo(fme).isAtMost(Relation.NEUTRAL))
                {
                    fme.sendMessage(ChatColor.RED+"You are not allowed to party with non-members/non-allies.");
                    event.setCancelled(true);
                }
                break;
            case LEFT_PARTY:
            case KICKED_FROM_PARTY:
                // Let them leave/get kicked
                break;
            case CUSTOM:
                // No clue what to do with this. mcMMO does not seem to use it :3
                break;
        }
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPartyTeleport(McMMOPartyTeleportEvent event)
    {
        // Do nothing unless Svesken decides otherwise
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPartyCommand(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        String cmd = event.getMessage();
        if(!cmd.startsWith("/party"))
            return; // Its not what we are looking for, please move along.
        
        String[] split = cmd.split(" ");
        if(split.length < 3)
            return; // Its not even complete lol.
        if(PartySubcommandType.getSubcommand(split[1]) == PartySubcommandType.OWNER)
        {
            String party = PartyAPI.getPartyName(player);
            String leader;
            if(party != null)
            {
                leader = PartyAPI.getPartyLeader(party);
                if(player.getName().equals(leader))
                {
                    McMMOPlayer mp = UserManager.getPlayer(player);
                    if(mp == null)
                        return;
                    Party p = mp.getParty();
                    if(p != null && p.getMembers().contains(split[2]))
                    {
                        new CheckPartyMembersTask(p, split[2]).runTaskLater(this.plugin, 1L);
                    }
                }
            }
        }
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFacLeave(FPlayerLeaveEvent event)
    {
        PartyAPI.removeFromParty(event.getFPlayer().getPlayer());
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFacRelChange(FactionRelationEvent event)
    {
        switch(event.getRelation())
        {
            case MEMBER:
            case ALLY:
                return;
            default:
            	break;
        }
        cleanseParties(event.getFaction());
        cleanseParties(event.getTargetFaction());
    }
    
    private void cleanseParties(Faction fme)
    {
        FPlayer pleader;
        Party p;
        for(FPlayer fplayer : fme.getFPlayers())
        {
            p = UserManager.getPlayer(fplayer.getName()).getParty();
            pleader = FPlayers.i.get(p.getLeader());
            if(pleader.getRelationTo(fplayer).isAtMost(Relation.NEUTRAL))
                PartyManager.removeFromParty(Bukkit.getOfflinePlayer(fplayer.getName()), p);
        }
    }
}
