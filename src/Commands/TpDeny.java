package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDeny implements CommandExecutor{

	private final Tpa tpa;
	private final TpaHere tpaHere;

	public TpDeny(final Tpa tpa, final TpaHere tpaHere){
		this.tpa = tpa;
		this.tpaHere = tpaHere;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] arg3) {
		final String tpaSender = this.tpa.removeOriginByDestination(sender.getName());
		if(tpaSender != null){
			sender.sendMessage(ChatColor.RED+"Tpa request from "+tpaSender+" has been denied");
			final Player player = Bukkit.getPlayer(tpaSender);
			if(player != null)
				player.sendMessage(ChatColor.RED+sender.getName()+" has denied your tpa request.");
			return true;
		}else{
			final String tpaHereSender = this.tpaHere.removeDestinationByOrigin(sender.getName());
			if(tpaHereSender != null){
				sender.sendMessage(ChatColor.RED+"Tpahere request from "+tpaHereSender+" has been denied");
				final Player player = Bukkit.getPlayer(tpaHereSender);
				if(player != null)
					player.sendMessage(ChatColor.RED+sender.getName()+" has denied your tpahere request.");
				return true;
			} else
				sender.sendMessage(ChatColor.RED+"You have not received a teleport request.");
		}
		return false;
	}

}
