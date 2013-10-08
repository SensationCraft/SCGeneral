package Bounties;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.sensationcraft.scgeneral.SCGeneral;

import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public class BountiesListeners implements Listener{

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent e){
		final User user = SCGeneral.getEssentials().getOfflineUser(e.getPlayer().getName());
		if(user != null)
			if(user.getConfigMap().containsKey("bounties")){
				final List<String> bounties = (List<String>)user.getConfigMap().get("bounties");
				final Iterator<String> it = bounties.iterator();
				while(it.hasNext()){
					final String[] split = it.next().split("[:]");
					if(split.length == 2)
						if(!this.checkBountyIsValid(Long.parseLong(split[1])))
							it.remove();
				}
				user.setConfigProperty("bounties", bounties);
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent e){
		if(e.getEntity().getKiller() == null)
			return;
		final User user = SCGeneral.getEssentials().getUser(e.getEntity().getName());
		if(user != null)
			if(user.getConfigMap().containsKey("bounties")){
				@SuppressWarnings("unchecked")
				final
				List<String> bounties = (List<String>) user.getConfigMap().get("bounties");
				if(!bounties.isEmpty()){
					final Iterator<String> it = bounties.iterator();
					final String name = e.getEntity().getKiller().getName();
					while(it.hasNext())
						try {
							final String[] split = it.next().split("[:]");
							if(!this.checkBountyIsValid(Long.parseLong(split[1]))){
								it.remove();
								continue;
							}
							final double money = Double.parseDouble(split[0]);
							Economy.add(name, new BigDecimal(money));
							e.getEntity().getKiller().sendMessage(new StringBuilder().append(ChatColor.GREEN).append("You have been awarded $").append(money).append(" for killing ").append(e.getEntity().getName()).toString());
						} catch (NumberFormatException | NoLoanPermittedException | ArithmeticException | UserDoesNotExistException error) {
							error.printStackTrace();
						}
					user.setConfigProperty("bounties", bounties);
				}
			}
	}

	public boolean checkBountyIsValid(final long timeout){
		if(timeout < System.currentTimeMillis())
			return false;
		return true;
	}
}
