package Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author DarkSeraphim
 */
public class SuperItems implements Listener
{
    
    public static final String tag = "Super item";
    
    private final ItemStack supersword = new ItemStack(Material.DIAMOND_SWORD);
    private final ItemStack superhelm = new ItemStack(Material.DIAMOND_HELMET);
    private final ItemStack superchestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    private final ItemStack superleggings = new ItemStack(Material.DIAMOND_LEGGINGS);
    private final ItemStack superboots = new ItemStack(Material.DIAMOND_BOOTS);
    private final ItemStack superbow = new ItemStack(Material.BOW);
    private final ItemStack superstick = new ItemStack(Material.STICK);
    
    Map<Enchantment, Integer> armor = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> sword = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> bow = new HashMap<Enchantment, Integer>();
    Map<Enchantment, Integer> stick = new HashMap<Enchantment, Integer>();
    
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
        
        superarmor(this.superboots);
        superarmor(this.superleggings);
        superarmor(this.superchestplate);
        superarmor(this.superhelm);
        supersword(this.supersword);
        superbow(this.superbow);
        superstick(this.superstick);
        
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
    
    private void tagit(ItemStack i)
    {
        ItemMeta meta = i.getItemMeta();
        List<String> lore = meta.getLore();
        if(lore == null)
            lore = new ArrayList<String>();
        lore.add(0, tag);
        i.setItemMeta(meta);
    }
    
    private void enchantit(ItemStack i, Map<Enchantment, Integer> enchants)
    {
        for(Map.Entry<Enchantment, Integer> enchant : enchants.entrySet())
        {
            if(enchant.getKey().canEnchantItem(i))
            {
                i.addUnsafeEnchantment(enchant.getKey(), enchant.getValue());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract()
    {
        
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
        HOE
    }
}
