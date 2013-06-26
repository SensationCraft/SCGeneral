package Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDeny implements CommandExecutor{

	private Tpa tpa;
	private TpaHere tpaHere;
	
	public TpDeny(Tpa tpa, TpaHere tpaHere){
		this.tpa = tpa;
		this.tpaHere = tpaHere;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		String tpaSender = this.tpa.removeOriginByDestination(sender.getName());
		if(tpaSender != null){
			sender.sendMessage(ChatColor.RED+"Tpa request from "+tpaSender+" has been denied");
			Player player = Bukkit.getPlayer(tpaSender);
			if(player != null)
				player.sendMessage(ChatColor.RED+sender.getName()+" has denied your tpa request.");
			return true;
		}else{
			String tpaHereSender = this.tpaHere.removeDestinationByOrigin(sender.getName());
			if(tpaHereSender != null){
				sender.sendMessage(ChatColor.RED+"Tpahere request from "+tpaHereSender+" has been denied");
				Player player = Bukkit.getPlayer(tpaHereSender);
				if(player != null)
					player.sendMessage(ChatColor.RED+sender.getName()+" has denied your tpahere request.");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"You have not received a teleport request.");
			}
		}
		return false;
	}

}
