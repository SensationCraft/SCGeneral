package lockpicks;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class Premium implements Runnable
{

	public Premium(final Listeners l, final PlayerInteractEvent e)
	{
		this.e = e;
		this.l = l;
	}

	@Override
	public void run()
	{
		this.e.getClickedBlock().setTypeId(0);
		this.e.getPlayer().getWorld().playSound(this.e.getPlayer().getLocation(), Sound.FIRE_IGNITE, 2.0F, 1.0F);
		this.e.getPlayer().sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Lock Pick successful!").toString());
		this.l.picking.remove(this.e.getPlayer().getName());
	}

	private final PlayerInteractEvent e;

	private final Listeners l;
}