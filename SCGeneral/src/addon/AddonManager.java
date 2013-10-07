package addon;

import addon.exceptions.InvalidAddonException;
import addon.exceptions.UnknownAddonException;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    
    private final static Logger log;
    
    Map<String, AbstractReloadable> addons = new HashMap<String, AbstractReloadable>();
    
    protected static ClassLoader parentLoader;
    
    static
    {
        log = Logger.getLogger("AddonManager");
    }
    
    public AddonManager(SCGeneral plugin)
    {
        this.plugin = plugin;
        File pluginFolder = plugin.getDataFolder();
        if(!pluginFolder.exists())
            pluginFolder.mkdirs();
        File lFolder = new File(pluginFolder, "listeners");
        if(!lFolder.exists() || !lFolder.isDirectory())
            lFolder.mkdirs();
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
            log.log(Level.SEVERE, "Failed to obtain the parent ClassLoader");
            ex.printStackTrace();
            Bukkit.shutdown();
            // Break it off, likely it wouldn't work
        }
        AddonManager.parentLoader = cl;
        
        Set<String> ex = new HashSet<String>(this.plugin.getConfig().getStringList("excludes"));
        
        loadAll(ex);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args) 
    {
        if(sender instanceof Player)
        {
            sender.sendMessage(ChatColor.RED+"Cockblocked. Stay away from the addons!");
            return true;
        }
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
                    
                    this.plugin.getData().register(rl.getAddon());
                    
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
    
    public final void loadAll(Set<String> excludes)
    {
        File lisDir = new File(this.plugin.getDataFolder(), "listeners");
        File[] files = lisDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().endsWith(".jar");
            }
        });
        for(File file : files)
        {
            String name = file.getName();
            name = name.substring(0, name.length() - 4);
            if(excludes.contains(name))
            {
                continue;
            }
            ReloadableListener rl = new ReloadableListener(name);
            try
            {
                rl.load(this.plugin);
                // Maybe call an onEnable or smth

                this.plugin.getData().register(rl.getAddon());

                this.addons.put(name, rl);
                
                rl.enable(plugin);

                log.log(Level.INFO, ChatColor.GREEN+"Loaded addon {0}.", name);
            }
            catch(UnknownAddonException ex)
            {
                log.log(Level.WARNING, "{0}Unknown addon.", ChatColor.RED);
            }
            catch(InvalidAddonException ex)
            {
                log.log(Level.SEVERE, "{0}Failed to load the addon: ", ChatColor.RED);
                ex.printStackTrace();
            }
        }
    }
    
    public void destroy()
    {
    }
    
}
