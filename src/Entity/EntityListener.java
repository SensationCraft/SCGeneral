package Entity;

import Commands.help.HelpRequest;
import java.util.Random;

import me.superckl.combatlogger.CombatLogger;
import me.superckl.scgeneral.SCGeneral;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;

public class EntityListener implements Listener
{

	private final Random random = new Random();
	final CombatLogger combatLogger;
	private final SCGeneral plugin;
        private final HelpRequest help;

	public EntityListener(final HelpRequest help, final SCGeneral plugin){
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
		this.plugin = plugin;
                this.help = help;
	}
 
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(final PlayerQuitEvent e)
        {
            this.help.removeRequest(e.getPlayer().getName());
        }

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		final int chance = this.random.nextInt(2);
		final Entity ent = event.getEntity();
		final Location loc = ent.getLocation();
		if (ent instanceof Zombie && chance == 0)
			loc.getWorld().dropItem(loc, new ItemStack(372, 1));
		else if (ent instanceof Skeleton && chance == 0)
			loc.getWorld().dropItem(loc, new ItemStack(Material.GHAST_TEAR, 1));
		else if (ent instanceof Spider && chance == 0)
			loc.getWorld().dropItem(loc, new ItemStack(Material.MAGMA_CREAM, 1));
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
			if(chance == 0)
				loc.getWorld().dropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
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
         * @param e - the event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent e){
		if(e.getDamage() == 0.0) return;
                if(e.getEntity() instanceof Player == false) 
                    return;
		if(e.getDamager() instanceof Player == false) 
                    return;
                
                Player attacker = (Player) e.getDamager();
                Player attacked = (Player) e.getEntity();
                
                if(!attacked.canSee(attacker))
                {
                    e.setCancelled(true);
                    return;
                }
                
		final Vector entloc = attacked.getLocation().toVector();
		final Vector damloc = attacker.getLocation().toVector();
		Vector attackdir = entloc.subtract(damloc).setY(0).normalize();
		Vector hitdir = attacker.getLocation().getDirection().setY(0).normalize();
		
                double angle = (attackdir.angle(hitdir)/(Math.PI*2) * 360);
                
		if(angle > 40)
                {
			e.setCancelled(true);
			System.out.println("Blocked aimbot for "+((Player)e.getDamager()).getName()+": "+angle);
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
		}else if(this.plugin.getShout().isDead() && (e.getMessage().startsWith("/me ") || e.getMessage().startsWith("/eme "))){
			e.getPlayer().sendMessage(ChatColor.RED+"Shout is currently disabled! Try again later.");
			e.setCancelled(true);
			e.setMessage("/cockblocked");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(final PlayerTeleportEvent event)
	{
            System.out.println(event.getCause());
		if(event.getCause() != TeleportCause.COMMAND && event.getCause() != TeleportCause.PLUGIN)
			return;

		if(this.combatLogger.getCombatListeners().isInCombat(event.getPlayer().getName()))
			event.setCancelled(true);
	}
}