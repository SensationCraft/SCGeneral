package FactionFix;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

public class HomeFix implements Listener
{

	private final Essentials ess;
	private final String homeTeleportAlert = new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("An enemy has teleported into your faction's land!").toString();

	public HomeFix()
	{
		final Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");
		if(p != null)
			this.ess = (Essentials) p;
		else
			this.ess = null;
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onCommand(final PlayerCommandPreprocessEvent event)
	{
		if(this.ess == null) return;
		final String[] msg = event.getMessage().substring(1).split(" ");
		final String cmd = msg[0];
		if(cmd.equalsIgnoreCase("home") || cmd.equalsIgnoreCase("ehome"))
			if(msg.length > 1)
			{
				final User user = this.ess.getUser(event.getPlayer());
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
					if(this.ess.getCommand("home").execute(event.getPlayer(), msg[0], Arrays.copyOfRange(msg, 1, msg.length)))
						for(final Player player:faction.getOnlinePlayers())
							player.sendMessage(this.homeTeleportAlert);
			}

	}

}
