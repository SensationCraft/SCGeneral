package Bounties;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class BountiesListeners implements Listener{

	private Essentials ess;
	
	public BountiesListeners(){
		this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		User user = this.ess.getOfflineUser(e.getPlayer().getName());
		if(user != null)
			if(user.getConfigMap().containsKey("bounties")){
				List<String> bounties = (List<String>)user.getConfigMap().get("bounties");
				Iterator<String> it = bounties.iterator();
				while(it.hasNext()){
					String[] split = it.next().split("[:]");
					if(split.length == 2)
						if(!this.checkBountyIsValid(Long.parseLong(split[1])))
							it.remove();
				}
				user.setConfigProperty("bounties", bounties);
			}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		if(e.getEntity().getKiller() == null)
			return;
		User user = this.ess.getUser(e.getEntity().getName());
		if(user != null){
			if(user.getConfigMap().containsKey("bounties")){
				@SuppressWarnings("unchecked")
				List<String> bounties = (List<String>) user.getConfigMap().get("bounties");
				if(!bounties.isEmpty()){
					String name = e.getEntity().getKiller().getName();
					for(String bounty:bounties)
						try {
							String[] split = bounty.split("[:]");
							if(!this.checkBountyIsValid(Long.parseLong(split[1])))
								continue;
							//TODO too lazy to remove it
							double money = Double.parseDouble(split[0]);
							Economy.add(name, new BigDecimal(money));
							e.getEntity().getKiller().sendMessage(new StringBuilder().append(ChatColor.GREEN).append("You have been awarded $").append(money).append(" for killing ").append(e.getEntity().getName()).toString());
						} catch (NumberFormatException | NoLoanPermittedException | ArithmeticException | UserDoesNotExistException error) {
							error.printStackTrace();
						}
				}
			}
		}
	}
	
	public boolean checkBountyIsValid(long timeout){
		if(timeout < System.currentTimeMillis())
			return false;
		return true;
	}
	
}
