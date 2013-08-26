package Commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ClearInvis implements CommandExecutor{

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(sender.hasPermission("scgeneral.clearinvis"))
                {
                        if(args.length < 1 || (args[0].equalsIgnoreCase("p") && args.length < 2))
                        {
                            sender.sendMessage("/clearinvis all|<radius>|p [name]");
                            return true;
                        }
                        
                        final Player[] players;
                        if(args[0].equalsIgnoreCase("all"))
                        {
                            players = Bukkit.getOnlinePlayers();
                        }
                        else if(args[0].equalsIgnoreCase("p"))
                        {
                            Player player = Bukkit.getPlayerExact(args[1]);
                            if(player == null)
                            {
                                sender.sendMessage(ChatColor.RED+"Player not online!");
                            }
                            players = new Player[]{player};
                        }
                        else
                        {
                            if(sender instanceof Player == false)
                            {
                                sender.sendMessage(ChatColor.RED+"You need to be ingame to use the radius function");
                                return true;
                            }
                            if(!args[0].matches("[0-9]*"))
                            {
                                sender.sendMessage(ChatColor.RED+"Please enter a valid radius");
                                
                            }
                            int r = Integer.parseInt(args[0]);
                            List<Player> ps = new ArrayList<Player>();
                            for(Entity e: ((Player)sender).getNearbyEntities(r, r, r))
                            {
                                if(e instanceof Player)
                                    ps.add((Player)e);
                            }
                            players = ps.toArray(new Player[0]);
                        }
                    
			for(final Player player : players)
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			sender.sendMessage(ChatColor.GREEN+"Invisibility cleared.");
			return true;
		}
		return false;
	}

}
