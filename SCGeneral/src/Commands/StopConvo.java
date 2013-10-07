package Commands;

import org.bukkit.Bukkit;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sensationcraft.scgeneral.SCGeneral;

public class StopConvo extends BooleanPrompt
{
	final Plugin pl;

	public StopConvo(final Plugin pl)
	{
		this.pl = pl;
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final String in)
	{
		return this.acceptValidatedInput(cc, in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y"));
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext cc, final boolean bln)
	{
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
						SCGeneral.getUser(player.getName()).setInCombat(false);
						player.kickPlayer(message);
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
					Bukkit.shutdown();
				}
			}.runTask(SCGeneral.getInstance());
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
