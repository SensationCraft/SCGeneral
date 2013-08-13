package mcMMOFix;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckPartyMembersTask extends BukkitRunnable
{
    
    Party p;
    String nl;
    
    public CheckPartyMembersTask(Party party, String newLeader)
    {
        this.p = party;
        this.nl = newLeader;
    }

    @Override
    public void run()
    {
        if(this.p.getLeader().equals(this.nl))
        {
            FPlayer fme = FPlayers.i.get(this.nl);
            FPlayer fother;
            for(String member : this.p.getMembers())
            {
                if(this.nl.equals(member))
                    continue;
                fother = FPlayers.i.get(member);
                if(fother.getRelationTo(fme).isAtMost(Relation.NEUTRAL))
                    PartyManager.removeFromParty(Bukkit.getOfflinePlayer(member), this.p);
            }
        }
    }
}
