package Entity;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.sensationcraft.scgeneral.SCGeneral;

import Commands.help.HelpRequest;

import com.earth2me.essentials.User;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;
import org.bukkit.event.Listener;

public class EntityListener implements Listener
{

	private final Random random = new Random();
	private final HelpRequest help;
	/*private final EnumSet<Material> hax = EnumSet.of(
            Material.THIN_GLASS,
            Material.IRON_FENCE,
            Material.FENCE,
            Material.FENCE_GATE,
            Material.COBBLE_WALL,
            Material.NETHER_FENCE,
<<<<<<< HEAD
            Material.TRAP_DOOR);

	public EntityListener(final HelpRequest help){
		this.combatLogger = (CombatLogger) Bukkit.getPluginManager().getPlugin("CombatLogger");
=======
            Material.TRAP_DOOR);*///Unused

	public EntityListener()
	{
		this.help = SCGeneral.getInstance().getHelp();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent e)
	{
		this.help.removeRequest(e.getPlayer().getName());
		for(final Player player:Bukkit.getOnlinePlayers()){
			final User user = SCGeneral.getEssentials().getUser(player.getName());
			if(user == null)
				continue;
			if(!user.isInvSee())
				return;
			if(user.getOpenInventory() == null)
				return;
			if(user.getOpenInventory().getTopInventory() == e.getPlayer().getInventory()){
				user.closeInventory();
				user.sendMessage(ChatColor.RED+e.getPlayer().getName()+" has logged off!");
			}
		}
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
		if(ent instanceof PigZombie)
		{
			final List<ItemStack> drops = event.getDrops();
			for(final ItemStack i : drops)
				if(i.getType() == Material.GOLD_NUGGET || i.getType() == Material.GOLD_INGOT)
					i.setType(Material.DIRT);
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
			if(chance == 0)
				loc.getWorld().dropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
		}
		if(ent != null && ent.getType() == EntityType.ENDER_DRAGON)
			event.setCancelled(true);
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(final AsyncPlayerChatEvent e) {
		if(e.getMessage().startsWith("!")){
			e.setCancelled(true);
			e.setMessage("I'm a herp");
			e.getPlayer().sendMessage(ChatColor.DARK_RED+"I will eat your soul if you chat like that. -superckl");
		}
	}
}