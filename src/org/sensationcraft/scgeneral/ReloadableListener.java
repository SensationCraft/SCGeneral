package org.sensationcraft.scgeneral;

import org.bukkit.event.Listener;

public abstract class ReloadableListener implements Listener{

	public abstract void prepareForReload();
	public static void finishReload(){
		
	}
	
}
