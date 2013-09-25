package org.sensationcraft.scgeneral;

import org.bukkit.event.Listener;

public abstract class ReloadableListener implements Listener{

	public abstract void prepareForReload();
	public abstract void finishReload();
	
	private static Object[] dataStore;
	public static void setDataStore(Object ... data){
		dataStore = data;
	}
	
	public static Object[] getDataStore(){
		return dataStore;
	}
	
}
