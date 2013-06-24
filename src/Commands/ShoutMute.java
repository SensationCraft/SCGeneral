package Commands;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShoutMute implements CommandExecutor{

	private SCGeneral instance;
	
	public ShoutMute(SCGeneral instance){
		this.instance = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		Shout shout = this.instance.getShout();
		if(shout.getDisabled().contains(sender.getName())){
			shout.getDisabled().remove(sender.getName());
			sender.sendMessage(ChatColor.GOLD+"Shout is now on.");
		}else{
			shout.getDisabled().add(sender.getName());
			sender.sendMessage(ChatColor.GOLD+"Shout is now off.");
		}
		return true;
	}

}
