package lockpicks;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class Default implements Runnable
{

	public Default(final Listeners l, final PlayerInteractEvent e)
	{
		this.e = e;
		this.l = l;
	}

	@Override
	public void run()
	{
		final Random random = new Random();
		if(random.nextBoolean())
		{
			this.e.getClickedBlock().setTypeId(0);
			this.e.getPlayer().getWorld().playSound(this.e.getPlayer().getLocation(), Sound.FIRE_IGNITE, 2.0F, 1.0F);
			this.e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Lock Pick successful!").toString());
		}
		else
		{
			this.e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.RED).append("Lock Pick failed!").toString());
		}
		this.l.picking.remove(this.e.getPlayer().getName());
	}

	private final PlayerInteractEvent e;

	private final Listeners l;

}