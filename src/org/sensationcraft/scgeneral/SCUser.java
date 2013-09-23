package org.sensationcraft.scgeneral;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.earth2me.essentials.PlayerExtension;

public class SCUser extends PlayerExtension{

	private boolean inCombat;
	private long combatTime;
	private BukkitTask combatTask;
	
	private final long COMBAT_TIME = 8000;
	
	public SCUser(Player base) {
		super(base);
	}
	
	public void setInCombat(boolean inCombat){
		if(inCombat){
			this.combatTime = System.currentTimeMillis()+this.COMBAT_TIME;
			this.combatTask = new BukkitRunnable()
			{
				@Override
				public void run()
				{
					SCUser.this.setInCombat(false);
				}
			}.runTaskLater(SCGeneral.getInstance(), this.COMBAT_TIME/50);
			if(!this.inCombat)
				this.getBase().sendMessage(ChatColor.YELLOW+"You have entered combat!");
		}else if(this.inCombat && !inCombat){
			this.combatTask.cancel();
			this.combatTime = 0;
			this.getBase().sendMessage(ChatColor.YELLOW+"You have left combat.");
		}
		this.inCombat = inCombat;
	}
	public boolean isInCombat(){
		return this.inCombat && System.currentTimeMillis() < this.combatTime;
	}
}
