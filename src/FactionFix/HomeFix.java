package FactionFix;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

public class HomeFix implements Listener
{

	private final String homeTeleportAlert = new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("An enemy has teleported into your faction's land!").toString();

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCommand(final PlayerCommandPreprocessEvent event)
	{
		final String[] msg = event.getMessage().substring(1).split(" ");
		final String cmd = msg[0];
		if(cmd.equalsIgnoreCase("home") || cmd.equalsIgnoreCase("ehome") || cmd.equals("homes") || cmd.equals("ehomes"))
			if(msg.length > 1)
			{
				final User user = SCGeneral.getEssentials().getUser(event.getPlayer());
				Location home;
				try
				{
					home = user.getHome(msg[1]);
					if(home == null)
						home = user.getHome("home");
					if(home == null) return;
				}
				catch (final Exception ex)
				{
					return;
				}
				final Faction fme = FPlayers.i.get(event.getPlayer()).getFaction();
				final FLocation loc = new FLocation(home);
				final Faction faction = Board.getFactionAt(loc);
				if(faction.getRelationTo(fme) == Relation.ENEMY)
					if(SCGeneral.getEssentials().getCommand("home").execute(event.getPlayer(), msg[0], Arrays.copyOfRange(msg, 1, msg.length)))
						for(final Player player:faction.getOnlinePlayers())
							player.sendMessage(this.homeTeleportAlert);
			}

	}

}
