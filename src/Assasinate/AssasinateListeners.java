package Assasinate;

import org.bukkit.event.Listener;

public class AssasinateListeners implements Listener{

	
	public boolean checkBountyIsValid(long timeout){
		if(timeout < System.currentTimeMillis())
			return false;
		return true;
	}
	
}
