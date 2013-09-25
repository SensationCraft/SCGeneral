package patch;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.sensationcraft.scgeneral.ReloadableListener;

import com.gmail.nossr50.mcMMO;

public class DupeFix extends ReloadableListener{

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

	@Override
	public void prepareForReload() {
	}

	@Override
	public void finishReload() {
	}

}
