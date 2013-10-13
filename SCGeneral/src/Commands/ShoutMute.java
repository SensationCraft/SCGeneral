package Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class ShoutMute implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		final Shout shout = SCGeneral.getInstance().getShout();
		/*if(shout.getDisabled().contains(sender.getName())){
			shout.getDisabled().remove(sender.getName());
			sender.sendMessage(ChatColor.GOLD+"Shout is now on.");
		}else{
			shout.getDisabled().add(sender.getName());
			sender.sendMessage(ChatColor.GOLD+"Shout is now off.");
		}*/
		return true;
	}

}
