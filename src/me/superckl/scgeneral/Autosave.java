package me.superckl.scgeneral;

import org.bukkit.Bukkit;

public class Autosave implements Runnable
{

	@Override
	public void run()
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
	}
}