package Items;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author DarkSeraphim
 */
public class SuperItems implements Listener
{
    
    public static final String tag = "&r&5Super item".replace('&', ChatColor.COLOR_CHAR);
    
    private final String broadcast = "&e%s &5has bought a &e%s".replace('&', ChatColor.COLOR_CHAR);
    
    private final ItemStack supersword = new ItemStack(Material.DIAMOND_SWORD);
    private final ItemStack superhelmet = new ItemStack(Material.DIAMOND_HELMET);
    private final ItemStack superchestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    private final ItemStack superleggings = new ItemStack(Material.DIAMOND_LEGGINGS);
    private final ItemStack superboots = new ItemStack(Material.DIAMOND_BOOTS);
    private final ItemStack superbow = new ItemStack(Material.BOW);
    private final ItemStack superstick = new ItemStack(Material.STICK);
    private final ItemStack superpickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
    private final ItemStack superaxe = new ItemStack(Material.DIAMOND_AXE);
    private final ItemStack supershovel = new ItemStack(Material.DIAMOND_SPADE);
    private final ItemStack superhoe = new ItemStack(Material.DIAMOND_HOE);
    
    Map<Enchantment, Integer> armor = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> sword = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> bow = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> stick = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> tool = new HashMap<Enchantment, Integer>();
    
    public SuperItems()
    {
        armor.put(Enchantment.PROTECTION_FIRE, 6);
        armor.put(Enchantment.PROTECTION_ENVIRONMENTAL, 6);
        armor.put(Enchantment.PROTECTION_EXPLOSIONS, 6);
        armor.put(Enchantment.PROTECTION_PROJECTILE, 6);
        armor.put(Enchantment.PROTECTION_FALL,10);
        armor.put(Enchantment.THORNS, 3);
        armor.put(Enchantment.WATER_WORKER, 1);
        armor.put(Enchantment.OXYGEN, 10);
        
        bow.put(Enchantment.ARROW_DAMAGE, 7);
        bow.put(Enchantment.ARROW_KNOCKBACK, 5);
        bow.put(Enchantment.ARROW_FIRE, 3);
        bow.put(Enchantment.ARROW_INFINITE, 10);
        
        sword.put(Enchantment.DAMAGE_ALL, 7);
        sword.put(Enchantment.FIRE_ASPECT, 3);
        sword.put(Enchantment.KNOCKBACK, 2);
        
        stick.put(Enchantment.KNOCKBACK, 10);
        
        tool.put(Enchantment.DURABILITY, 5);
        tool.put(Enchantment.DIG_SPEED, 10);
        tool.put(Enchantment.LOOT_BONUS_BLOCKS, 3);
        
        superarmor(this.superboots);
        superarmor(this.superleggings);
        superarmor(this.superchestplate);
        superarmor(this.superhelmet);
        supersword(this.supersword);
        superbow(this.superbow);
        superstick(this.superstick);
        supertool(this.superpickaxe);
        supertool(this.superaxe);
        supertool(this.supershovel);
        supertool(this.superhoe);
    }
    
    private void superarmor(ItemStack i)
    {
        tagit(i);
        enchantit(i, this.armor);
    }
    
    private void supersword(ItemStack i)
    {
        tagit(i);
        enchantit(i, this.sword);
    }
    
    private void superbow(ItemStack i)
    {
        tagit(i);
        enchantit(i, this.bow);
    }
    
    private void superstick(ItemStack i)
    {
        tagit(i);
        enchantit(i, this.stick);
    }
    
    private void supertool(ItemStack i)
    {
        tagit(i);
        enchantit(i, this.tool);
    }
    
    private void tagit(ItemStack i)
    {
        ItemMeta meta = i.getItemMeta();
        List<String> lore = meta.getLore();
        if(lore == null)
            lore = new ArrayList<String>();
        lore.add(0, tag);
        meta.setLore(lore);
        String name = "&r&c%s".replace('&', ChatColor.COLOR_CHAR);
        switch(i.getType())
        {
            case DIAMOND_SPADE:
                name = String.format(name, "Super Shovel");
                break;
            case DIAMOND_AXE:
                name = String.format(name, "Super Axe");
                break;
            case DIAMOND_PICKAXE:
                name = String.format(name, "Super Pickaxe");
                break;
            case DIAMOND_HOE:
                name = String.format(name, "Super Hoe");
                break;
            case STICK:
                name = String.format(name, "Super Stick");
                break;
            case BOW:
                name = String.format(name, "Super Bow");
                break;
            case DIAMOND_SWORD:
                name = String.format(name, "Sveskmourne");
                break;
            case DIAMOND_HELMET:
                name = String.format(name, "Super Helmet");
                break;
            case DIAMOND_CHESTPLATE:
                name = String.format(name, "Super Chestplate");
                break;
            case DIAMOND_LEGGINGS:
                name = String.format(name, "Super Leggings");
                break;
            case DIAMOND_BOOTS:
                name = String.format(name, "Super Boots");
                break;
        }
        meta.setDisplayName(name);
        i.setItemMeta(meta);
    }
    
    private void enchantit(ItemStack i, Map<Enchantment, Integer> enchants)
    {
        for(Map.Entry<Enchantment, Integer> enchant : enchants.entrySet())
        {
            if(enchant.getKey().canEnchantItem(i) || i.getType() == Material.STICK)
            {
                i.addUnsafeEnchantment(enchant.getKey(), enchant.getValue());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event)
    {
        if(!event.getPlayer().isOp())
        {
            event.setCancelled(true);
            event.setLine(0, "");
            event.setLine(1, "");
            event.setLine(2, "");
            event.setLine(3, "");
        }
        if(event.getBlock().getType() != Material.WALL_SIGN)
            return;
        if(!event.getLine(0).equalsIgnoreCase("[purchase]"))
            return;
        
        if(check(event))
        {
            event.setLine(0, ChatColor.BLUE+"[Purchase]");
            event.setLine(1, ChatColor.RED+event.getLine(1));
            // Basically leave line 3 alone
            //event.setLine(2, event.getLine(2));
            event.setLine(3, "");
        }
        else
        {
            event.setLine(3, ChatColor.DARK_RED+"INVALID");
        }
    }
    
    private boolean check(SignChangeEvent event)
    {
        String item = event.getLine(1);
        if(!item.toLowerCase().startsWith("super"))
            return false;
        item = item.substring(5);
        try
        {
            Super.valueOf(item.trim().toUpperCase());
        }
        catch(IllegalArgumentException ex)
        {
            return false;
        }
        
        if(!event.getLine(2).startsWith("$"))
            return false;
                
        try
        {
            Double.parseDouble(event.getLine(2).substring(1));
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        return true;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final String name = player.getName();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(event.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        
        Sign sign = (Sign) event.getClickedBlock().getState();
        if(!sign.getLine(0).contains("[Purchase]"))
            return;
        String item = ChatColor.stripColor(sign.getLine(1));
        if(!item.toLowerCase().startsWith("super"))
            return;
        item = item.substring(5);
        Super s;
        try
        {
            s = Super.valueOf(item.trim().toUpperCase());
        }
        catch(IllegalArgumentException ex)
        {
            return;
        }
        
        double price;
        
        try
        {
            price = Double.parseDouble(sign.getLine(2).substring(1));
        }
        catch(NumberFormatException ex)
        {
            return;
        }
        
        ItemStack superitem;
        
        switch(s)
        {
            case SWORD:
                superitem = this.supersword.clone();
                break;
            case BOOTS:
                superitem = this.superboots.clone();
                break;
            case LEGGINGS:
                superitem = this.superleggings.clone();
                break;
            case PLATE:
                superitem = this.superchestplate.clone();
                break;
            case HELMET:
                superitem = this.superhelmet.clone();
                break;
            case STICK:
                superitem = this.superstick.clone();
                break;
            case PICKAXE:
                superitem = this.superpickaxe.clone();
                break;
            case AXE:
                superitem = this.superaxe.clone();
                break;
            case SHOVEL:
                superitem = this.supershovel.clone();
                break;
            case HOE:
                superitem = this.superhoe.clone();
                break;
            default:
                return;       
        }
        
        try
        {
            BigDecimal bdprice = BigDecimal.valueOf(price);
            if(!Economy.hasEnough(name, bdprice))
                return;
            if(InventoryWorkaround.addAllItems(player.getInventory(), superitem) != null)
                return;
            Economy.substract(name, bdprice);
            player.updateInventory();
            Bukkit.broadcastMessage(String.format(this.broadcast, name, superitem.getItemMeta().getDisplayName()));
            player.getWorld().playSound(player.getLocation(), Sound.AMBIENCE_CAVE, 100F , 1F);
        }
        catch(UserDoesNotExistException ex)
        {
            // Swallow it
        }
        catch(NoLoanPermittedException ex)
        {   
            // Swallow it you cumbucket
        }
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareRepair(PrepareItemCraftEvent event)
    {
        if(!event.isRepair())
            return;
        Recipe r = event.getRecipe();
        if(isSuper(r.getResult()))
            event.getInventory().setResult(null);
        else
            for(ItemStack i : event.getInventory().getMatrix())
                if(isSuper(i))
                {
                    event.getInventory().setResult(null);
                    break;
                }
        for(HumanEntity he : event.getViewers())
            if(he instanceof Player)
                ((Player)he).updateInventory();
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onRepair(CraftItemEvent event)
    {
        Recipe r = event.getRecipe();
        boolean cancel = false;
        for(ItemStack i : event.getInventory().getMatrix())
            if(isSuper(i))
            {
                cancel = true;
                break;
            }
        
        if(cancel)
        {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
        for(HumanEntity he : event.getViewers())
            if(he instanceof Player)
                ((Player)he).updateInventory();
    }
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event)
    {
        if(event.getWhoClicked() instanceof Player == false)
            return;
        if(event.getInventory().getType() != InventoryType.ANVIL)
            return;
        Player player = (Player) event.getWhoClicked();
        if(event.getRawSlot() < 0 || event.getRawSlot() > 2)
            return;
        if(isSuper(event.getCurrentItem()) || isSuper(event.getCursor()))
        {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }
    
    public boolean isSuper(ItemStack i)
    {
        if(i == null)
            return false;
        ItemMeta meta = i.getItemMeta();
        if(meta == null)
            return false;
        List<String> lore = meta.getLore();
        if(lore == null)
            return false;
        return lore.contains(SuperItems.tag);
    }
    
    private enum Super
    {
        SWORD,
        BOOTS,
        LEGGINGS,
        PLATE,
        HELMET,
        STICK,
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        BOW
    }
}
