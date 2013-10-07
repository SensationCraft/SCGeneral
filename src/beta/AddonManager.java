package beta;

import beta.exceptions.InvalidAddonException;
import beta.exceptions.UnknownAddonException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public class AddonManager implements CommandExecutor
{
    
    private final SCGeneral plugin;
    
    Map<String, AbstractReloadable> addons = new HashMap<String, AbstractReloadable>();
    
    protected static ClassLoader parentLoader;
    
    public AddonManager(SCGeneral plugin)
    {
        this.plugin = plugin;
        File pluginFolder = plugin.getDataFolder();
        if(!pluginFolder.exists())
            pluginFolder.mkdirs();
        File lFolder = new File(pluginFolder, "listeners");
        if(!lFolder.exists() || !lFolder.isDirectory())
            lFolder.mkdirs();
        ReloadableListener.rootFolder = lFolder;
        ClassLoader cl = null;
        try
        {
            Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader", new Class[0]);
            if(!m.isAccessible())
                m.setAccessible(true);
            cl = (ClassLoader) m.invoke(plugin, new Object[0]);
        }
        catch(Exception ex)
        {
            
        }
        
        AddonManager.parentLoader = cl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args) 
    {
        if(args.length < 1)
        {
            sender.sendMessage("Addon Manager v0.1");
            return true;
        }
        if(args[0].equals("load"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
            }
            else
            {
                ReloadableListener rl = new ReloadableListener(args[1]);
                try
                {
                    rl.load(this.plugin);
                    // Maybe call an onEnable or smth
                    this.addons.put(args[1], rl);
                    
                    sender.sendMessage(ChatColor.GREEN+"Loaded the addon.");
                }
                catch(UnknownAddonException ex)
                {
                    sender.sendMessage(ChatColor.RED+"Unknown addon.");
                }
                catch(InvalidAddonException ex)
                {
                    sender.sendMessage(ChatColor.RED+"Failed to load the addon: ");
                    ex.printStackTrace();
                }
            }
        }
        else if(args[0].equalsIgnoreCase("enable"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
            }
            else
            {
                if(!this.addons.containsKey(args[1]))
                    sender.sendMessage(ChatColor.RED+"Addon not found.");
                else
                {
                    AbstractReloadable ar = this.addons.get(args[1]);
                    ar.enable(plugin);
                    sender.sendMessage(ChatColor.GREEN+"Addon enabled.");
                }
            }
        }
        else if(args[0].equalsIgnoreCase("disable"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
            }
            else
            {
                if(!this.addons.containsKey(args[1]))
                    sender.sendMessage(ChatColor.RED+"Addon not found.");
                else
                {
                    AbstractReloadable ar = this.addons.get(args[1]);
                    ar.disable();
                    sender.sendMessage(ChatColor.DARK_RED+"Addon disabled.");
                }
            }
        }
        else if(args[0].equalsIgnoreCase("unload"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(ChatColor.RED+"Please specify the addon you want to load");
            }
            else
            {
                if(!this.addons.containsKey(args[1]))
                    sender.sendMessage(ChatColor.RED+"Addon not found.");
                else
                {
                    AbstractReloadable ar = this.addons.remove(args[1]);
                    ar.unload();
                    sender.sendMessage(ChatColor.DARK_RED+"Addon unloaded.");
                }
            }
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(ChatColor.RED+"Please specify the addon you want to reload");
            }
            else
            {
                if(!this.addons.containsKey(args[1]))
                    sender.sendMessage(ChatColor.RED+"Addon not found.");
                else
                {
                    AbstractReloadable ar = this.addons.remove(args[1]);
                    Addon a = null;
                    try
                    {
                        a = ar.load(plugin);
                    }
                    catch(UnknownAddonException ex)
                    {
                        sender.sendMessage(ChatColor.RED+"Unknown addon.");
                    }
                    catch(InvalidAddonException ex)
                    {
                        sender.sendMessage(ChatColor.RED+"Failed to load the addon!");
                        ex.printStackTrace();
                    }
                    if(a == null)
                    {
                        sender.sendMessage(ChatColor.RED+"Failed to reload the addon!");
                        return true;
                    }
                    try
                    {
                        ar.validate(a);
                    }
                    catch(Exception ex)
                    {
                        sender.sendMessage(ChatColor.RED+"Failed to reload the addon!");
                        ex.printStackTrace();
                    }
                    ar.unload();
                    ar.load(a);
                    sender.sendMessage(ChatColor.DARK_RED+"Addon reloaded.");
                }
            }
        }
        return true;
    }
    
    public void destroy()
    {
        ReloadableListener.rootFolder = null;
    }
    
}
