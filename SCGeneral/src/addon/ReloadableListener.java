package addon;

import addon.exceptions.InvalidAddonException;
import addon.exceptions.UnknownAddonException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.sensationcraft.scgeneral.SCGeneral;

/**
 *
 * @author DarkSeraphim
 */
public class ReloadableListener extends AbstractReloadable
{    
    
    private final String name;
        
    private boolean isEnabled = false;
    
    ReloadableListener(String name)
    {
        this.name = name;
    }
    
    @Override
    public Addon load(SCGeneral plugin) throws UnknownAddonException, InvalidAddonException
    {
        File file = new File(plugin.getDataFolder(), String.format("listeners/%s.jar", name));
        if(!file.exists())
            throw new UnknownAddonException(name);
        URL[] urls = new URL[0];
        try
        {
            urls = new URL[]{file.toURI().toURL()};
        }
        catch(MalformedURLException ex)
        {
            return null;
            // Swallow it
        }
        ClassLoader cloader = new java.net.URLClassLoader(urls, AddonManager.parentLoader);
        AddonDescriptionFile desc = new AddonDescriptionFile(file);
        String mainClass = desc.getMainClass();
        try
        {
            Class<?> c = Class.forName(mainClass, true, cloader);
            Class<? extends Addon> addonClass = c.asSubclass(Addon.class);
            Addon a = addonClass.getConstructor(SCGeneral.class, AddonDescriptionFile.class).newInstance(plugin, desc);
            if(a instanceof Listener == false)
                throw new InvalidAddonException(String.format("Addon is not a listener"));
            
            this.addon = a;
        }
        catch(InvocationTargetException ex)
        {
            throw new InvalidAddonException(String.format("Invalid addon found: %s", ex.getCause().getMessage()));
        }
        catch(Throwable ex)
        {
            throw new InvalidAddonException(String.format("Invalid addon found: %s", ex.getMessage()));
        }
        return this.addon;
    }
    
    @Override
    public void validate(Addon addon) throws InvalidAddonException
    {
        if(addon instanceof Listener == false)
            throw new InvalidAddonException(String.format("Addon is not a listener"));
    }
    
    @Override
    public void load(Addon addon)
    {
        if(this.addon != null)
        this.addon = addon;
    }
    
    @Override
    public void unload()
    {
        if(this.addon != null)
        {
            disable();
        }
    }
    
    @Override
    public void enable(Plugin plugin) throws IllegalStateException
    {
        if(this.addon == null)
        {
            throw new IllegalStateException("Addon not loaded");
        }
        Bukkit.getPluginManager().registerEvents((Listener)this.addon, plugin);
        this.isEnabled = true;
    }
    
    public boolean isEnabled()
    {
        return this.isEnabled;
    }
    
    @Override
    public void disable()
    {
        if(this.addon != null)
        {
            HandlerList.unregisterAll((Listener)this.addon);
        }
        this.isEnabled = false;
    }
    
}
