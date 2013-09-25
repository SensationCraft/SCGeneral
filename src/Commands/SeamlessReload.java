package Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.sensationcraft.scgeneral.ReloadableListener;
import org.sensationcraft.scgeneral.SCGeneral;

public class SeamlessReload implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if(arg0 instanceof ConsoleCommandSender == false)
			return false;
		if(arg3.length != 1)
			return false;
		ReloadableListener listener = SCGeneral.getInstance().getListeners().get(arg3[0]);
		if(listener != null){
			SCGeneral.getInstance().reloadListener(listener);
			arg0.sendMessage(ChatColor.GOLD+"Reload successful!");
			return true;
		}else
			arg0.sendMessage(ChatColor.RED+"Listener not found!");
		return false;
	}

}
