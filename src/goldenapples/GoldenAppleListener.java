package goldenapples;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author DarkSeraphim - because I am 1337 enough to set the author :3
 */
public class GoldenAppleListener implements Listener
{
    
    private final ItemStack godapple;
    
    private final Map<String, Integer> appleOwners = new HashMap<String, Integer>();
    
    public GoldenAppleListener()
    {
        this.godapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)2);
        ItemMeta meta = this.godapple.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE+"God Apple");
        meta.setLore(Arrays.asList(ChatColor.GOLD+"An apple blessed by the ancient gods"));
        this.godapple.setItemMeta(meta);
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event)
    {
        ItemStack result = event.getRecipe().getResult();
        if(result.getType() == Material.GOLDEN_APPLE && result.getDurability() > 0)
        {
            event.getInventory().setResult(null);
        }
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCraft(CraftItemEvent event)
    {
        ItemStack result = event.getRecipe().getResult();
        if(result.getType() == Material.GOLDEN_APPLE && result.getDurability() > 0)
        {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(final SignChangeEvent event)
    {
            if (event.getBlock().getType() != Material.WALL_SIGN)
                    return;
            if (!event.getLine(0).equalsIgnoreCase("[purchase]"))
                    return;
            if (!event.getPlayer().isOp())
            {
                    event.setCancelled(true);
                    event.setLine(0, "");
                    event.setLine(1, "");
                    event.setLine(2, "");
                    event.setLine(3, "");
            }
            if (this.check(event))
            {
                    event.setLine(0, ChatColor.BLUE + "[Purchase]");
                    event.setLine(1, ChatColor.GOLD + "God Apple");
                    // Basically leave line 3 alone
                    //event.setLine(2, event.getLine(2));
                    event.setLine(3, "");
            }
    }

    private boolean check(final SignChangeEvent event)
    {
            if(event.getLine(1).equalsIgnoreCase("God Apple") && event.getLine(2).length() > 1)
                    try
                    {
                                    final double d = Double.parseDouble(event.getLine(2).substring(1));
                                    return d > 0;
                    }
                    catch(final NumberFormatException ex)
                    {
                            event.setLine(3, "INVALID CHARGE");
                            return false;
                    }
            event.setLine(3, "UNKNOWN SIGN");
            return false;
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event)
    {
            if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
                    return;
            if(event.getClickedBlock().getType() != Material.WALL_SIGN)
                    return;
            final Sign sign = (Sign) event.getClickedBlock().getState();

            String line = ChatColor.stripColor(sign.getLine(0)).toLowerCase();
            if(!line.equals("[purchase]"))
                    return;
            line = ChatColor.stripColor(sign.getLine(1)).toLowerCase();
            if(!line.equals("God Apple"))
                    return;
            if(!sign.getLine(3).isEmpty())
                return;

            final Player player = event.getPlayer();

            BigDecimal bd;

            try
            {
                    bd = BigDecimal.valueOf(Double.valueOf(sign.getLine(2).substring(1)));
            }
            catch(final NumberFormatException ex)
            {
                    player.sendMessage(ChatColor.RED+"Price seems to be invalid. Warn a member of staff.");
                    return;
            }

            try
            {
                    if(!Economy.hasEnough(player.getName(), bd))
                    {
                            player.sendMessage(ChatColor.RED+"You don't have enough money to purchase a god apple");
                            return;
                    }
                    if(!player.getInventory().addItem(this.godapple).isEmpty())
                    {
                            player.sendMessage(ChatColor.RED+"You don't seem to have enough space.");
                            return;
                    }
                    Economy.substract(player.getName(), bd);
            }
            catch(final UserDoesNotExistException ex)
            {
                    Bukkit.getLogger().log(Level.SEVERE, "Player {0} does not exist?", player.getName());
                    player.sendMessage(ChatColor.RED+"Something unknown has occurred... Contact a member of staff.");
                    return;
            }
            catch(final NoLoanPermittedException ex)
            {
                    player.sendMessage(ChatColor.RED+"You don't have enough money to protect your item");
                    return;
            }
            player.sendMessage(ChatColor.GREEN+"Purchased a god apple");
    }
    
    public void save(File parent)
    {
        File file = new File(parent, "apples.yml");
        if(!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                if(!file.createNewFile())
                    throw new IOException("Failed to create a new file");
            }
            catch(IOException ex)
            {
                return;
            }
        }
        
        try
        {
            YamlConfiguration yc = new YamlConfiguration();
            for(Map.Entry<String, Integer> appleEntry : this.appleOwners.entrySet())
                yc.set(appleEntry.getKey(), appleEntry.getValue());
            yc.save(file);
        }
        catch(IOException ex)
        {
            
        }
    }
}
