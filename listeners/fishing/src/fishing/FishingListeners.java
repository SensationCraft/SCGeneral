package fishing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class FishingListeners extends Addon implements Listener{

	public FishingListeners(SCGeneral scg, AddonDescriptionFile desc) {
		super(scg, desc);
	}

	//Monitor so it goes after mcMMO
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFish(final PlayerFishEvent e){
		if(e.getState() != PlayerFishEvent.State.FISHING)
			return;
		e.getHook().setBiteChance(e.getHook().getBiteChance()*FishingChance.getByPlayer(e.getPlayer()).getModifier());
	}

}
