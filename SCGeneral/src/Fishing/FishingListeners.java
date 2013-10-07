package Fishing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListeners implements Listener{

	//Monitor so it goes after mcMMO
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFish(final PlayerFishEvent e){
		if(e.getState() != PlayerFishEvent.State.FISHING)
			return;
		e.getHook().setBiteChance(e.getHook().getBiteChance()*FishingChance.getByPlayer(e.getPlayer()).getModifier());
	}

}
