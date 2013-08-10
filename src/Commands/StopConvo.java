package Commands;

import me.superckl.combatlogger.CombatLogger;

import org.bukkit.Bukkit;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author DarkSeraphim
 */
public class StopConvo extends BooleanPrompt
{

	final Plugin pl;
	final CombatLogger combatLogger;

	public StopConvo(final Plugin pl, final CombatLogger cl)
	{
		this.pl = pl;
		this.combatLogger = cl;
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final String in)
	{
		return this.acceptValidatedInput(cc, in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y"));
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final boolean bln)
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
					for(final Player player : Bukkit.getOnlinePlayers())
					{
						StopConvo.this.combatLogger.getCombatListeners().destroy(player.getName());
						player.kickPlayer(message);
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
					Bukkit.shutdown();
				}
			}.runTask(this.pl);
			return Prompt.END_OF_CONVERSATION;
		} else
			cc.getForWhom().sendRawMessage("Stopping server cancelled.");
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public String getPromptText(final ConversationContext cc)
	{
		return "Are you sure you want to stop the server? [y/n]";
	}

	@Override
	protected boolean isInputValid(final ConversationContext context, final String input)
	{
		return true;
	}

}
