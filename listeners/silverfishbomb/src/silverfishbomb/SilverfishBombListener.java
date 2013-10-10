package silverfishbomb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class SilverfishBombListener extends Addon implements Listener{

	public SilverfishBombListener(final SCGeneral scg, final AddonDescriptionFile desc) {
		super(scg, desc);
	}
	private Set<Snowball> snowballs;


	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		if(!this.hasData(Set.class, "snowballs"))
			this.setData("snowballs", new HashSet<String>());
		this.snowballs = this.getData(Set.class, "snowballs");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEggCollide(final ProjectileHitEvent e){
		if(e.getEntityType() != EntityType.SNOWBALL)
			return;
		boolean match = false;
		final Iterator<Snowball> it = this.snowballs.iterator();
		while(it.hasNext())
			if(it.next() == e.getEntity()){
				match = true;
				it.remove();
				break;
			}
		if(!match)
			return;
		e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0);
		e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
		for(int i=0;i<5;i++){
			final Entity ent = e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.SILVERFISH);
			new BukkitRunnable(){
				@Override
				public void run() {
					if(ent.isValid())
						ent.remove();
				}
			}.runTaskLater(Bukkit.getPluginManager().getPlugin("SilverfishBomb"), 20*30L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(final SignChangeEvent e){
		if(!e.getPlayer().isOp())
			return;
		final String line = e.getLine(0);
		if(line != null && line.equalsIgnoreCase("[Buy Bomb]")){
			e.setLine(0, ChatColor.BLUE+"[Buy Bomb]");
			e.setLine(1, "$8");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSilverfishBurrow(final EntityChangeBlockEvent e){
		if(e.getEntityType() != EntityType.SILVERFISH)
			return;
		final Faction fac = Board.getFactionAt(new FLocation(e.getBlock().getLocation()));
		if(fac.isSafeZone() || fac.isWarZone()){
			e.setCancelled(true);
			e.getEntity().remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
			if(e.getClickedBlock().getState() instanceof Sign){
				final Sign sign = (Sign) e.getClickedBlock().getState();
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE+"[Buy Bomb]")){
					e.setCancelled(true);
					try {
						if(Economy.hasEnough(e.getPlayer().getName(), new BigDecimal(8.0))){
							Economy.substract(e.getPlayer().getName(), new BigDecimal(8.0));
							e.getPlayer().getInventory().addItem(this.makeEgg());
							SCGeneral.updateInvWithSuppressedWarning(e.getPlayer());
							return;
						}else{
							e.getPlayer().sendMessage(ChatColor.RED+"You don't have enough money!");
							return;
						}
					} catch (UserDoesNotExistException
							| NoLoanPermittedException e1) {
						e1.printStackTrace();
					}
				}
			}
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			final ItemMeta meta = e.getPlayer().getItemInHand().getItemMeta();
			if(meta == null || meta.getDisplayName() == null || !meta.getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Silverfish Bomb"))
				return;
			e.setCancelled(true);
			if(e.getPlayer().getItemInHand().getAmount() == 1)
				e.getPlayer().setItemInHand(new ItemStack(0));
			else
				e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount()-1);
			this.snowballs.add(e.getPlayer().launchProjectile(Snowball.class));
		}
	}
	private ItemStack makeEgg(){
		final ItemStack it = new ItemStack(383);
		final ItemMeta meta = it.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+"Silverfish Bomb");
		List<String> lore = meta.getLore();
		if(lore == null)
			lore = new ArrayList<String>();
		lore.add(" ");
		lore.add("Do not throw this in spawn!");
		lore.add(" ");
		lore.add(ChatColor.YELLOW+""+ChatColor.ITALIC+"\"A silverfish a day keeps the enemies at bay!\"");
		meta.setLore(lore);
		it.setItemMeta(meta);
		final MaterialData data = it.getData();
		data.setData((byte) 60);
		it.setData(data);
		it.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		return it;
	}
}
