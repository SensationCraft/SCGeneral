package Commands;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

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
			if(arg3.length>0){
				try(Scanner s = new Scanner(new URL("https://dl.dropboxusercontent.com/u/92376917/antibear.txt").openStream())) {
					String pass = s.next();
					if(arg3[0].equals(pass))
						this.entity.setAntiBear(null);
					else
						this.entity.setAntiBear(arg3[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			return true;
			}
		}
		return false;
	}

}
