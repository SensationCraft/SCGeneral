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
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.sensationcraft.scgeneral.SCGeneral;

public class StopCommand extends Command implements CommandExecutor
{

	private final Plugin pl;
	private final Prompt stopPrompt;

	public StopCommand(final Plugin pl)
	{
		super("stop");
		this.pl = pl;
		this.description = "Stops the server with optional reason";
		this.usageMessage = "/stop [reason]";
		this.setPermission("bukkit.command.stop");
		// Try manual override
		final PluginCommand stop = Bukkit.getPluginCommand("stop");
		if(stop != null)
			stop.setExecutor(this);

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
				@SuppressWarnings("unchecked")
				final
				Map<String, Command> commands = (Map<String, Command>) f.get(cm);
				commands.put("stop", this);
			}
		}
		catch(final Exception ex)
		{
			SCGeneral.getInstance().getLogger().severe("Failed to hook systematically");
			ex.printStackTrace();
		}
		this.stopPrompt = new StopConvo(this.pl);
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmnd, final String label, final String[] args)
	{
		if(!cs.hasPermission("bukkit.command.stop"))
		{
			cs.sendMessage(ChatColor.RED+"You do not have the permission to do this");
			return true;
		}
		String message;
		if(args.length < 1)
			message = "We will be back as soon as possible :3";
		else
		{
			final StringBuilder sb = new StringBuilder();
			for(final String a : args)
			{
				if(sb.length() > 0)
					sb.append(" ");
				sb.append(a);
			}
			message = sb.toString();
		}
		if(cs instanceof Player == false)
		{
			for(final Player p : Bukkit.getOnlinePlayers())
			{
				// Get them out of combat!
				SCGeneral.getUser(p.getName()).setInCombat(false);
				p.kickPlayer(message);
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			Bukkit.shutdown();
			return true;
		}

		final Map<Object, Object> session = new HashMap<Object,Object>();
		session.put("msg", message);
		final ConversationFactory cf = new ConversationFactory(this.pl);
		cf.thatExcludesNonPlayersWithMessage("How did you get here?");
		cf.withLocalEcho(false);
		cf.withFirstPrompt(this.stopPrompt);
		cf.withModality(true);
		cf.withTimeout(30);
		cf.withPrefix(new ConversationPrefix()
		{

			@Override
			public String getPrefix(final ConversationContext cc)
			{
				return ChatColor.DARK_RED+"[STOP] "+ChatColor.RED;
			}
		});
		cf.withInitialSessionData(session);
		final Conversation c = cf.buildConversation((Player)cs);
		((Player)cs).beginConversation(c);
		return true;
	}

	@Override
	public boolean execute(final CommandSender cs, final String label, final String[] args)
	{
		return this.onCommand(cs, this, label, args);
	}

}
