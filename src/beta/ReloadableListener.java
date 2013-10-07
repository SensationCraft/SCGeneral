package beta;

import beta.exceptions.InvalidAddonException;
import beta.exceptions.UnknownAddonException;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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
    
    protected static File rootFolder;
    
    private static final String nameFormat = "%s.jar";
    
    private final String name;
    
    private Addon addon;
    
    private boolean isEnabled = false;
    
    ReloadableListener(String name)
    {
        this.name = name;
    }
    
    @Override
    public Addon load(SCGeneral plugin) throws UnknownAddonException, InvalidAddonException
    {
        File file = new File(rootFolder, String.format(nameFormat, name));
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
            Class c = Class.forName(mainClass, true, cloader);
            Class<? extends Addon> addonClass = c.asSubclass(Addon.class);
            System.out.println(addonClass);
            Addon a = addonClass.getConstructor(SCGeneral.class).newInstance(plugin);
            if(a instanceof Listener == false)
                throw new InvalidAddonException(String.format("Addon is not a listener"));
            
            // Possibly define some tests
            
            if(false)
            {
                System.out.println("Failed to load the addon");
                a = null;
            }
            
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
