package Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpRequest implements CommandExecutor{

	private final List<String> requests = new ArrayList<String>(0);

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2,
			final String[] arg3) {
		if(arg3.length == 0){
			arg0.sendMessage(ChatColor.RED+"You must enter a message!");
			return false;
		}
		if(this.hasRequest(arg0.getName())){
			arg0.sendMessage(ChatColor.RED+"Please wait for your previous help request to be accepted.");
			return false;
		}
		final String message = arg0.getName()+": "+this.translate(arg3);
		this.requests.add(this.requests.size(), message);
		for(final Player player:Bukkit.getOnlinePlayers()){
			if(!player.hasPermission("help.request")) continue;
			player.sendMessage(ChatColor.AQUA+"A help request has been received from "+arg0.getName()+" numbered "+(this.requests.size()));
		}
		arg0.sendMessage(ChatColor.GREEN+"\nYour request has been received. Please wait for a staff member to respond.");
		return true;
	}
	private String translate(final String[] args) {
		String message = "";
		for (final String arg : args)
			message += arg.concat(" ");
		message = message.trim();
		message = ChatColor.stripColor(message);
		return message;
	}

	public boolean hasRequest(final String name){
		for(final String string:this.requests)
			if(string != null && string.split("[:]")[0].equals(name)) return true;
		return false;
	}
	public List<String> getRequests(){
		return this.requests;
	}
	public String removeRequest(final String name){
		final ListIterator<String> it = this.requests.listIterator();
		while(it.hasNext()){
			final String string = it.next();
			if(string.split("[:]")[0].equals(name)){
				it.set(null);
				return string;
			}
		}
		return null;
	}

}
