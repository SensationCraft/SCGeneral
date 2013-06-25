package Entity;

import java.util.Random;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;

public class EntityListener implements Listener
{

	private final Random random = new Random();

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		final int chance = this.random.nextInt(2);
		final Entity ent = event.getEntity();
		final Location loc = ent.getLocation();
		if (ent instanceof Zombie && chance == 0)
		{
			loc.getWorld().dropItem(loc, new ItemStack(372, 1));
		}
		else if (ent instanceof Skeleton && chance == 0)
		{
			loc.getWorld().dropItem(loc, new ItemStack(Material.GHAST_TEAR, 1));
		}
		else if (ent instanceof Spider && chance == 0)
		{
			loc.getWorld().dropItem(loc, new ItemStack(Material.MAGMA_CREAM, 1));
		}
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		final Entity ent = event.getEntity();
		final Location loc = event.getLocation();
		if (ent instanceof Creeper)
		{
			final int chance = this.random.nextInt(2);
			event.setCancelled(true);
			loc.getWorld().createExplosion(loc, 0.0F);
			if(chance == 0) {
				loc.getWorld().dropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		final SCGeneral main = (SCGeneral)Bukkit.getPluginManager().getPlugin("SCGeneral");
		e.getPlayer().setScoreboard(((SCGeneral)Bukkit.getPluginManager().getPlugin("SCGeneral")).getScoreboard());
		e.getPlayer().setScoreboard(main.getScoreboard());
		main.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(e.getPlayer()).setScore(e.getPlayer().getHealth());
		final FPlayer fPlayer = FPlayers.i.get(e.getPlayer());
		if(fPlayer.getChatMode() == ChatMode.FACTION || fPlayer.getChatMode() == ChatMode.ALLIANCE){
			fPlayer.setChatMode(ChatMode.PUBLIC);
			fPlayer.sendMessage(ChatColor.DARK_GREEN+"You have been automagically taken out of faction chat.");
		}
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE && !e.getPlayer().hasPermission("creativeblock.bypass")){
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			e.getPlayer().sendMessage(ChatColor.DARK_RED+"Creative cock-blocked.");
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent e){
		e.setDeathMessage("");
		final Player player = e.getEntity().getKiller();
		if(player == null) return;
		e.getEntity().sendMessage(ChatColor.YELLOW+"You have been killed by "+player.getName());
		e.getEntity().playSound(e.getEntity().getLocation(), Sound.NOTE_BASS_DRUM, 1, 1);
		player.sendMessage(ChatColor.YELLOW+"You killed "+e.getEntity().getName());
		player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
	}
	/**
	 * Anti-forcefield
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent e){
		if(e.getDamage() == 0) return;
		if(e.isCancelled()) return;
		if(e.getCause() != DamageCause.ENTITY_ATTACK) return;
		if(e.getDamager().getType() != EntityType.PLAYER) return;
		final Location entloc = e.getEntity().getLocation();
		final Location damloc = e.getDamager().getLocation();
		final double entlocx = entloc.getX();
		final double entlocz = entloc.getZ();
		final double damlocx = damloc.getX();
		final double damlocz = damloc.getZ();
		final double adjx = (entlocx-damlocx);
		final double adjz = entlocz-damlocz;
		final Vector adjvector = new Vector(adjx, 0, adjz);
		final Vector basevector = new Vector(0,0,1);
		float angle = adjvector.angle(basevector);
		angle = (float) (angle*(180/Math.PI));
		if(adjx<0) {
			angle=360-angle;
		}
		float yaw = damloc.getYaw();
		if(yaw>0) {
			yaw=Math.abs(yaw-360);
		} else {
			yaw = Math.abs(yaw);
		}
		final float finalAngle = Math.abs(angle-yaw);
		if(finalAngle>15){
			e.setCancelled(true);
			System.out.println("Blocked aimbot for "+((Player)e.getEntity()).getName()+": "+finalAngle);
			for(Player player:Bukkit.getOnlinePlayers())
				if(player.hasPermission("antif.broadcast"))
					player.sendMessage(ChatColor.AQUA+"Detected forcefield for "+((Player)e.getEntity()).getName()+": "+finalAngle);
		}
	}
	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(final AsyncPlayerChatEvent e) {
		if (e.isCancelled())
			return;
		if(e.getMessage().startsWith("!")){
			e.setCancelled(true);
			e.setMessage("I'm a herp");
			e.getPlayer().sendMessage(ChatColor.DARK_RED+"I will eat your soul if you chat like that. -superckl");
			return;
		}
		final PermissionUser user = PermissionsEx.getUser(e.getPlayer());
		e.setFormat(ChatColor.translateAlternateColorCodes('&', user.getPrefix() + "%1$s" + user.getSuffix() + ": %2$s"));
		final Location loc = e.getPlayer().getLocation();
		e.setMessage(e.getMessage().trim());
		final Iterator<Player> it = e.getRecipients().iterator();
		while (it.hasNext()) {
			final Player player = it.next();
			if (loc.distanceSquared(player.getLocation()) > 144 && !player.hasPermission("chat.seelocal")) {
				it.remove();
			}
		}
		if (e.getRecipients().size() == 1) {
			Bukkit.getScheduler()
			.runTaskLater(Bukkit.getPluginManager().getPlugin("SCGeneral"), new BukkitRunnable() {
				@Override
				public void run() {
					e.getPlayer().sendMessage(
							ChatColor.YELLOW + "No one hears you.");
				}
			}, 2L);
		}
	}*/
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(final AsyncPlayerChatEvent e) {
		if(e.getMessage().startsWith("!")){
			e.setCancelled(true);
			e.setMessage("I'm a herp");
			e.getPlayer().sendMessage(ChatColor.DARK_RED+"I will eat your soul if you chat like that. -superckl");
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent e){
		if(e.getMessage().toLowerCase().startsWith("/op ") || e.getMessage().equalsIgnoreCase("/op")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_RED+"Op can only be given from the console!");
		}
	}

}