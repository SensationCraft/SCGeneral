package Commands.tp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sensationcraft.scgeneral.SCGeneral;

public class TpSuite implements CommandExecutor
{

	enum TpCommand
	{
		TPA,
		TPACCEPT,
		TPAHERE,
		TPCHECK,
		TPDENY;

		public boolean needsRequest()
		{
			switch(this)
			{
			case TPACCEPT:
			case TPDENY:
			case TPCHECK:
				return true;
			default:
				return false;
			}
		}
	}

	protected class TpRequest
	{
		final boolean isHere;
		final String requested;
		final String requester;

		public TpRequest(final String requester, final String requested, final boolean isHere)
		{
			this.requester = requester;
			this.requested = requested;
			this.isHere = isHere;
		}

		public String getRequested()
		{
			return this.requested;
		}

		public String getRequester()
		{
			return this.requester;
		}

		public boolean isTpaHere()
		{
			return this.isHere;
		}
	}
	private final TpAccept accept;

	private final String check = "&6Your current request is from &b%s &6and he requested to teleport &b%s".replace('&', ChatColor.COLOR_CHAR);
	private final TpDeny deny;

	private final Map<String, TpRequest> requests = new HashMap<String, TpRequest>();


	private final Tpa tpa;

	private final TpaHere tpahere;

	public TpSuite()
	{
		this.accept = new TpAccept();
		this.deny = new TpDeny();
		this.tpa = new Tpa();
		this.tpahere = new TpaHere();
	}

	public void clear(final String requested)
	{
		this.requests.remove(requested);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if(sender instanceof Player == false)
		{
			sender.sendMessage("Only a ingame player can use teleportation commands");
			return true;
		}

		if (SCGeneral.getUser(sender.getName()).isInCombat())
		{
			sender.sendMessage(ChatColor.DARK_RED+"You cannot send tp requests while in combat!");
			return true;
		}

		TpCommand tpcmd;
		try
		{
			tpcmd = TpCommand.valueOf(cmd.getName().toUpperCase());
		}
		catch(final IllegalArgumentException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED+"Unknown command.");
			return true;
		}

		final Player player = (Player) sender;

		final TpRequest req = this.requests.get(player.getName());
		if(req == null && tpcmd.needsRequest())
		{
			player.sendMessage(ChatColor.DARK_RED+"You have no pending teleport request");
			return true;
		}

		switch(tpcmd)
		{
		case TPCHECK:
			player.sendMessage(String.format(this.check, req.getRequester(), req.isTpaHere() ? "to him" : "to you"));
			break;
		case TPA:
			this.tpa.execute(player, this, req, args);
			break;
		case TPAHERE:
			this.tpahere.execute(player, this, req, args);
			break;
		case TPACCEPT:
			this.accept.execute(player, req);
			this.clear(player.getName());
			break;
		case TPDENY:
			this.deny.execute(player, req);
			this.clear(player.getName());
			break;
		}
		return true;
	}

	public void request(final String requester, final String requested, final boolean isHere)
	{
		this.requests.put(requested, new TpRequest(requester, requested, isHere));
	}

}
