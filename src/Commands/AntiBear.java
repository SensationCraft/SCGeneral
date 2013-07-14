package Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import Entity.EntityListener;

public class AntiBear implements CommandExecutor{

	private EntityListener entity;
	
	public AntiBear(EntityListener entity){
		this.entity = entity;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if(arg0 instanceof ConsoleCommandSender){
			if(arg3.length>0)
				entity.setAntiBear(arg3[0]);
			else
				entity.setAntiBear(null);
			return true;
		}
		return false;
	}

}
