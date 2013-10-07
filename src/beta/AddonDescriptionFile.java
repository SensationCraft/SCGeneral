
package beta;

import beta.exceptions.InvalidAddonException;
import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author DarkSeraphim
 */
public class AddonDescriptionFile
{
    private final YamlConfiguration yml;
    
    protected AddonDescriptionFile(File file) throws IllegalArgumentException, InvalidAddonException
    {
        YamlConfiguration y = null;
        if(file == null)
            throw new IllegalArgumentException("Addon file cannot be null");
        if(!file.exists())
            throw new IllegalArgumentException("Addon file is nonexistant");
        if(!file.getName().endsWith(".jar"))
            throw new IllegalArgumentException("Addon file is not a jar");
        try
        {
            JarFile jf = new JarFile(file);
            JarEntry je = jf.getJarEntry("addon.yml");
            if(je == null)
                throw new InvalidAddonException("Invalid addon: missing addon.yml");
            InputStream in = jf.getInputStream(je);
            if(in == null)
                throw new InvalidAddonException("Invalid addon: missing addon.yml");
            y = YamlConfiguration.loadConfiguration(in);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        if(y != null)
        {
            this.yml = y;
        }
        else
        {
            this.yml = new YamlConfiguration();
        }
    }
    
    public String getMainClass()
    {
        return this.yml.getString("main", "");
    }
}
