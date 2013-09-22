package mcMMO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

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

public class FactionParty implements Listener
{

	Plugin plugin;

	public FactionParty(final Plugin plugin)
	{
		this.plugin = plugin;
	}


	@EventHandler
	public void onPartyChange(final McMMOPartyChangeEvent event)
	{
		final FPlayer fme = FPlayers.i.get(event.getPlayer());
		switch(event.getReason())
		{
		case CHANGED_PARTIES:
		case JOINED_PARTY:
			final String leader = PartyAPI.getPartyLeader(event.getNewParty());
			if(leader == null)
				break;
			final Party p = UserManager.getPlayer(leader).getParty();
			if(p == null)
				break; // Weird party xD
			for(final String member : p.getMembers())
				if(FPlayers.i.get(member).getRelationTo(fme).isAtMost(Relation.NEUTRAL))
				{
					fme.sendMessage(ChatColor.RED+"You are not allowed to party with non-members/non-allies.");
					event.setCancelled(true);
					break;
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
	public void onPartyTeleport(final McMMOPartyTeleportEvent event)
	{
		// Do nothing unless Svesken decides otherwise
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPartyCommand(final PlayerCommandPreprocessEvent event)
	{
		final Player player = event.getPlayer();
		final String cmd = event.getMessage();
		if(!cmd.startsWith("/party"))
			return; // Its not what we are looking for, please move along.

		final String[] split = cmd.split(" ");
		if(split.length < 3)
			return; // Its not even complete lol.
		if(PartySubcommandType.getSubcommand(split[1]) == PartySubcommandType.OWNER)
		{
			final String party = PartyAPI.getPartyName(player);
			String leader;
			if(party != null)
			{
				leader = PartyAPI.getPartyLeader(party);
				if(player.getName().equals(leader))
				{
					final McMMOPlayer mp = UserManager.getPlayer(player);
					if(mp == null)
						return;
					final Party p = mp.getParty();
					if(p != null && p.getMembers().contains(split[2]))
						new CheckPartyMembersTask(p, split[2]).runTaskLater(this.plugin, 1L);
				}
			}
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFacLeave(final FPlayerLeaveEvent event)
	{
                if(PartyAPI.inParty(event.getFPlayer().getPlayer()))
                    PartyAPI.removeFromParty(event.getFPlayer().getPlayer());
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFacRelChange(final FactionRelationEvent event)
	{
		switch(event.getRelation())
		{
		case MEMBER:
		case ALLY:
			return;
		default:
			break;
		}
		this.cleanseParties(event.getFaction());
		this.cleanseParties(event.getTargetFaction());
	}

	private void cleanseParties(final Faction fme)
	{
		FPlayer pleader;
		Party p;
		for(final FPlayer fplayer : fme.getFPlayers())
		{
			p = PartyManager.getPlayerParty(fplayer.getName());
			if(p == null)
				continue;
			if(p.getLeader() == null)
				continue;
			pleader = FPlayers.i.get(p.getLeader());
			if(pleader.getRelationTo(fplayer).isAtMost(Relation.NEUTRAL))
				PartyManager.removeFromParty(Bukkit.getOfflinePlayer(fplayer.getName()), p);
		}
	}
}
