package Commands;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author DarkSeraphim
 */
public class StopCommand extends Command implements CommandExecutor
{
    
    private final Plugin pl;
    
    public StopCommand(Plugin pl)
    {
        super("stop");
        this.description = "Stops the server with optional reason";
        this.usageMessage = "/stop [reason]";
        this.setPermission("bukkit.command.stop");
        this.pl = pl;
        // Try manual override
        PluginCommand stop = Bukkit.getPluginCommand("stop");
        if(stop != null)
        {
            stop.setExecutor(this);
        }
        
        CommandMap cm = null;
        try
        {
            Field f = Class.forName("org.bukkit.craftbukkit.v1_6_R2.CraftServer").getDeclaredField("commandMap");
            if(!f.isAccessible())
                f.setAccessible(true);
            cm = (CommandMap) f.get(Bukkit.getServer());
            if(cm != null)
            {
                f = SimpleCommandMap.class.getDeclaredField("knownCommands");
                if(!f.isAccessible())
                    f.setAccessible(true);
                Map<String, Command> commands = (Map<String, Command>) f.get(cm);
                commands.put("stop", this);
            }
        }
        catch(Exception ex)
        {
            pl.getLogger().severe("Failed to hook systematically");
            ex.printStackTrace();
        }
        
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args)
    {
        if(!cs.hasPermission("bukkit.command.stop"))
        {
            cs.sendMessage(ChatColor.RED+"You do not have the permission to do this");
            return true;
        }
        String message;
        if(args.length < 1)
        {
            message = "We will be back as soon as possible :3";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for(String a : args)
            {
                if(sb.length() > 0)
                    sb.append(" ");
                sb.append(a);
            }
            message = sb.toString();
        }
        if(cs instanceof Player == false)
        {
            for(Player p : Bukkit.getOnlinePlayers())
            {
                // Get them out of combat!
                p.kickPlayer(message);
            }
            Bukkit.shutdown();
            return true;
        }
        
        Map<Object, Object> session = new HashMap<Object,Object>();
        session.put("msg", message);
        
        Conversation c = new ConversationFactory(pl)
            .thatExcludesNonPlayersWithMessage("How did you get here?")
            .withLocalEcho(false)
            .withFirstPrompt(new StopConvo())
            .withModality(true)
            .withTimeout(30)
            .withPrefix(new ConversationPrefix() 
            {

                @Override
                public String getPrefix(ConversationContext cc)
                {
                    return ChatColor.DARK_RED+"[STOP] "+ChatColor.RED;
                }
            })
            .withInitialSessionData(session)
            .buildConversation((Player)cs);
        ((Player)cs).beginConversation(c);
        return true;
    }

    @Override
    public boolean execute(CommandSender cs, String label, String[] args)
    {
        return this.onCommand(cs, this, label, args);
    }
    
    private class StopConvo extends BooleanPrompt
    {

        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, String in)
        {
            return this.acceptValidatedInput(cc, in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y"));
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext cc, boolean bln)
        {
            System.out.println("Bln: "+bln);
            if(bln)
            {
                String mes = (String) cc.getSessionData("msg");
                if(mes == null || mes.isEmpty())
                    mes = "We will be back as soon as possible :3";
                final String message = mes;
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        for(Player player : Bukkit.getOnlinePlayers())
                        {
                            player.kickPlayer(message);
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
                        Bukkit.shutdown();
                    }
                }.runTask(pl);
                return Prompt.END_OF_CONVERSATION;
            }
            else
            {
                cc.getForWhom().sendRawMessage("Stopping server cancelled.");
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext cc)
        {
            return "Are you sure you want to stop the server? [y/n]";
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input)
        {
            return true;
        }
        
    }

}
