package patch;


import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.sensationcraft.scgeneral.SCGeneral;

public class PotionPatch implements Listener
{

	private final PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 320, 2);

	private final PotionEffect regenSplash = new PotionEffect(PotionEffectType.REGENERATION, 200, 2);

	private final PotionEffect str = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 0);

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDrink(final org.bukkit.event.player.PlayerItemConsumeEvent event)
	{
		if(event.getItem().getType() != Material.POTION) return;
		final Player player = event.getPlayer();
		final Potion pot = Potion.fromItemStack(event.getItem());
		boolean update = false;
		if(pot.getType() == PotionType.WATER)
			return;
		if(pot.getType() == PotionType.STRENGTH && pot.getLevel() == 2)
		{
			event.setCancelled(true);
			player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE));
			player.addPotionEffect(this.str);
			update = true;
		}
		else if(pot.getType() == PotionType.REGEN && pot.getLevel() == 2)
		{
			event.setCancelled(true);
			player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE));
			player.addPotionEffect(this.regen);
			update = true;
		}
		if(update)
			SCGeneral.updateInvWithSuppressedWarning(player);
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSplash(final org.bukkit.event.entity.PotionSplashEvent event)
	{
		boolean buff = false;
		for(final PotionEffect pe : event.getPotion().getEffects())
			if(pe.getType().equals(PotionEffectType.INCREASE_DAMAGE) && pe.getAmplifier() == 1)
			{
				event.setCancelled(true);
				return;
			}
			else if(pe.getType().equals(PotionEffectType.REGENERATION))
				buff = true;
		if(buff)
		{
			event.setCancelled(true);
			for(final LivingEntity le : event.getAffectedEntities())
				le.addPotionEffect(this.regenSplash);
		}
	}
}
