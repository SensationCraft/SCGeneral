package patch;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gmail.nossr50.mcMMO;

public class DupeFix implements Listener{

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPistonExtend(final BlockPistonExtendEvent e){
		for(final Block b:e.getBlocks())
			mcMMO.getPlaceStore().setTrue(b);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPistonRetract(final BlockPistonRetractEvent e){
		if(e.getBlock() != null)
			mcMMO.getPlaceStore().setTrue(e.getBlock());
	}

}
