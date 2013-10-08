package Duel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class DuelListeners implements Listener{

	private final Arena arena;

	public DuelListeners(){
		this.arena = SCGeneral.getInstance().getArena();
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent e)
	{
		if(e.getPlayer().hasPermission("duel.tp"))
			return;
		if(!this.arena.isInArena(e.getPlayer()) && this.arena.isInArena(e.getTo())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED+"You can't teleport into the arena!");
		}
		else if(!this.arena.isForceEnding() && this.arena.isInArena(e.getPlayer()) && !this.arena.isInArena(e.getTo())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED+"You can't teleport out of the arena!");
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(final PlayerQuitEvent e){
		if(this.arena.isRunning() && this.arena.isInArena(e.getPlayer()))
			this.arena.endMatch(this.arena.getOther(e.getPlayer()));
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(final PlayerKickEvent e){
		if(this.arena.isRunning() && this.arena.isInArena(e.getPlayer()))
			e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamageByPlayer(final EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player == false || e.getDamager() instanceof Player == false) return;
		if(this.arena.isInArena((Player)e.getEntity()) && !this.arena.isInArena((Player)e.getDamager())) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommandPre(final PlayerCommandPreprocessEvent e){
		if(this.arena.isInArena(e.getPlayer())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED+"Commands are blocked in the arena!");
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent e){
		if(this.arena.isInArena(e.getEntity()))
			this.arena.endMatch(this.arena.getOther(e.getEntity()));
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemPickup(final PlayerPickupItemEvent e){
		if(this.arena.isEnding())
			this.arena.pickedUp(e.getItem());
	}
}
