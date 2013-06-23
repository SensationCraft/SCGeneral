package Commands;

import me.superckl.scgeneral.SCGeneral;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ClearInvis implements CommandExecutor{

	private final SCGeneral instance;

	public ClearInvis(final SCGeneral instance){
		this.instance = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2,
			final String[] args) {
		if(sender.hasPermission("scgeneral.clearinvis")){
			for(final Player player:this.instance.getServer().getOnlinePlayers()) {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			sender.sendMessage(ChatColor.GREEN+"Invisibility cleared.");
			return true;
		}
		return false;
	}

}
