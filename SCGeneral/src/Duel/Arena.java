package Duel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.sensationcraft.scgeneral.SCGeneral;

/**
*
* @author superckl - Have a taste of your own medicine
*/
public class Arena {

	private Player player1;
	private Location player1Loc;
	private Player player2;
	private Location player2Loc;
	private final Vector corner1;
	private final Vector corner2;
	private final Vector midpoint;
	private final int length;
	private final int depth;
	private final int height;
	private final Location spectate;
	private final Location cont1;
	private final Location cont2;
	private boolean isRunning;
	private boolean isForceEnding;
	private boolean isEnding;
	private final List<Item> drops = new ArrayList<Item>();
	private BukkitTask timer;
	private final SCGeneral plugin;
	private final Map<String, String> duelRequests = new HashMap<String, String>();

	public Arena(final SCGeneral plugin, final Vector corner1, final Vector corner2, final Location spectate, final Location cont1, final Location cont2){
		this.corner1 = Vector.getMinimum(corner1, corner2);
		this.corner2 = Vector.getMaximum(corner1, corner2);
		this.midpoint = this.corner1.clone().midpoint(this.corner1);
		this.length = Math.abs(this.corner1.getBlockX()-this.corner2.getBlockX());
		this.height = Math.abs(this.corner1.getBlockY()-this.corner2.getBlockY());
		this.depth = Math.abs(this.corner1.getBlockZ()-this.corner2.getBlockZ());
		this.spectate = spectate;
		this.cont1 = cont1;
		this.cont2 = cont2;
		this.plugin = plugin;
		this.isForceEnding = false;
		this.isEnding = false;
	}
	protected boolean isInArena(final Location location){
		return location.toVector().isInAABB(this.corner1, this.corner2);
	}
	public boolean isInArena(final Player player){
		return this.player1 == player || this.player2 == player;
	}
	public void spectate(final Player player){
		player.teleport(this.spectate);
		player.sendMessage(ChatColor.YELLOW+"Enjoy the show!");
	}

	public void startMatch(final Player player1, final Player player2){
		this.isRunning = true;
		this.player1 = player1;
		this.player2 = player2;
		this.player1Loc = player1.getLocation();
		this.player2Loc = player2.getLocation();
		if(!player1.teleport(this.cont1, TeleportCause.PLUGIN)){
			this.forceEnd();
			return;
		}
		if(!player2.teleport(this.cont2, TeleportCause.PLUGIN)){
			this.forceEnd();
			return;
		}
		this.plugin.getServer().broadcastMessage(ChatColor.AQUA+player1.getName()+ChatColor.GOLD+" and "+ChatColor.AQUA+player2.getName()+ChatColor.GOLD+" are now dueling in the arena! '/spectate' to watch them!");
		this.timer = new BukkitRunnable(){
			@Override
			public void run() {
				Arena.this.forceEnd();
			}
		}.runTaskLater(this.plugin, 6000L);
	}
	protected void endMatch(final Player victor){
		this.isEnding = true;
		this.timer.cancel();
		this.timer = null;
		final Player other = this.getOther(victor);
		if(!other.isDead()) other.damage(32767);
		for(final Entity ent:this.getArenaEntities())
			if(ent instanceof Item)
				this.drops.add((Item) ent);
		if(other.getName().equals(this.player1.getName())){
			this.player1Loc = null;
			this.player1 = null;
		}else if(other.getName().equals(this.player2.getName())){
			this.player2Loc = null;
			this.player2 = null;
		}
		this.plugin.getServer().broadcastMessage(ChatColor.AQUA+victor.getName()+ChatColor.GOLD+" has beaten "+ChatColor.AQUA+other.getName()+ChatColor.GOLD+" in the arena!");
		victor.sendMessage(ChatColor.GOLD+"Please collect the drops, you will be teleported to your previous location in ten seconds...");
		new BukkitRunnable(){
			@Override
			public void run() {
				Arena.this.player1 = null;
				Arena.this.player2 = null;
				if(Arena.this.player1Loc != null){
					victor.teleport(Arena.this.player1Loc);
					Arena.this.player1Loc = null;
				}
				else{
					victor.teleport(Arena.this.player2Loc);
					Arena.this.player2Loc = null;
				}
				boolean once = true;
				if(other.isOnline() && !other.isDead())
					for(final Item item:Arena.this.drops){
						if(once){
							other.sendMessage(ChatColor.GREEN+"Some items have been returned to you from your duel.");
							once = false;
						}
						other.getInventory().addItem(item.getItemStack());
					}
				Arena.this.drops.clear();
				for(final Entity ent:Arena.this.getArenaEntities())
					if(ent instanceof Item)
						ent.remove();
				Arena.this.isRunning = false;
				Arena.this.isEnding = false;
			}
		}.runTaskLater(this.plugin, 200L);
	}
	public void forceEnd(){
		this.isForceEnding = true;
		this.isEnding = true;
		if(this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		if(this.player1 != null && this.player2 != null) this.plugin.getServer().broadcastMessage(ChatColor.GOLD+"The duel between "+ChatColor.AQUA+this.player1.getName()+ChatColor.GOLD+" and "+ChatColor.AQUA+this.player2.getName()+ChatColor.GOLD+" ended in a draw!");
		if(this.player1 != null){
			SCGeneral.getUser(this.player1.getName()).setInCombat(false);
			this.player1.teleport(this.player1Loc, TeleportCause.UNKNOWN);
		}
		if(this.player2 != null){
			SCGeneral.getUser(this.player2.getName()).setInCombat(false);
			this.player2.teleport(this.player2Loc, TeleportCause.UNKNOWN);
		}
		this.player1 = null;
		this.player2 = null;
		this.player1Loc = null;
		this.player2Loc = null;
		this.isRunning = false;
		this.isForceEnding = false;
		this.isEnding = false;
	}
	private List<Entity> getArenaEntities(){
		final Entity arrow = this.spectate.getWorld().spawnArrow(this.midpoint.toLocation(this.spectate.getWorld()), this.midpoint, 0, 0);
		final List<Entity> ents = arrow.getNearbyEntities(Arena.this.length/2, Arena.this.height/2, Arena.this.depth/2);
		arrow.remove();
		return ents;
	}
	public boolean isRunning(){
		return this.isRunning;
	}
	public Player getOther(final Player player){
		if(player.getName().equals(this.player1.getName())) return this.player2;
		else if(player.getName().equals(this.player2.getName())) return this.player1;
		return null;
	}
	public boolean isForceEnding(){
		return this.isForceEnding;
	}
	public boolean isEnding(){
		return this.isEnding || this.isForceEnding;
	}
	public Set<String> getKeysByValue(final String value){
		final Set<String> keys = new HashSet<String>();
		for(final Entry<String, String> entry:this.duelRequests.entrySet()){
			if(!value.equals(entry.getValue())) continue;
			keys.add(entry.getKey());
		}
		return keys;
	}
	public Map<String, String> getDuelRequests(){
		return this.duelRequests;
	}
	public boolean containsEntry(final Entry<String, String> entry){
		return this.duelRequests.entrySet().contains(entry);
	}
	public void pickedUp(final Item item){
		if(this.isEnding)
			this.drops.remove(item);
	}
}
