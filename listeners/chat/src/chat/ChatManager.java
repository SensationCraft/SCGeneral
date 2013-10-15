package chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sensationcraft.scgeneral.ChannelChangeEvent;
import org.sensationcraft.scgeneral.ChatChannel;
import org.sensationcraft.scgeneral.SCGeneral;
import org.sensationcraft.scgeneral.SCUser;

import addon.Addon;
import addon.AddonDescriptionFile;
import addon.storage.Persistant;

import com.earth2me.essentials.User;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author DarkSeraphim
 */
public class ChatManager extends Addon implements Listener
{

	private Map<String, ChatChannel> users = new HashMap<String, ChatChannel>();

	@Persistant(key = "pm", instantiationType = HashMap.class)
	private Map<String, String> pm;

	@Persistant(key = "ipmutes", instantiationType = HashMap.class)
	private Map<String, Long> mute;

	@Persistant(key = "shoutcool", instantiationType = HashMap.class)
	private Map<String, Long> cooldown;

	@Persistant(key = "shoutkill", instantiationType = AtomicBoolean.class)
	private AtomicBoolean shoutkill;

	@Persistant(key = "pvptitles", instantiationType = ConcurrentHashMap.class)
	private Map<String, String> pvptitles;

	private final String to = "&6[me -> %s&6]&r %s".replace('&', ChatColor.COLOR_CHAR);
	private final String from = "&6[%s&6 -> me]&r %s".replace('&', ChatColor.COLOR_CHAR);
	private final String ss = "[Socialspy: %s -> %s] %s";

	private final String local = "&a- &r%s&7: %s".replace('&', ChatColor.COLOR_CHAR);
	private final String global = "&c[S] &r%s &r%s&r: &l%s".replace('&', ChatColor.COLOR_CHAR);
	private final String me = "&5* %s %s".replace('&', ChatColor.COLOR_CHAR);

	private final String at = "@%s";
	private final String t = "&4&l[&r%s&4&l]&r".replace('&', ChatColor.COLOR_CHAR);
    
    private final Set<String> cmds = Sets.newHashSet("m", 
                                                     "t",
                                                     "w",
                                                     "tell",
                                                     "whisper",
                                                     "msg",
                                                     "r",
                                                     "emsg",
                                                     "etell",
                                                     "ewhisper",
                                                     "er",
                                                     "ereply",
                                                     "reply");

	// 15 seconds cooldown
	private final long SHOUT_COOLDOWN = 15000;

	public ChatManager(final SCGeneral scg, final AddonDescriptionFile desc)
	{
		super(scg, desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable()
	{
		for(final SCUser user : SCGeneral.getSCUsers().values())
			this.users.put(user.getName(), user.getChannel());

		if(!this.hasData(Map.class, "shoutcool"))
			this.setData("shoutcool", new HashMap<String, Long>());
		this.cooldown = this.getData(Map.class, "shoutcool");

		if(!this.hasData(Map.class, "pm"))
			this.setData("pm", new HashMap<String, String>());
		this.pm = this.getData(Map.class, "pm");

		if(!this.hasData(AtomicBoolean.class, "shoutkill"))
			this.setData("shoutkill", new AtomicBoolean(false));
		this.shoutkill = this.getData(AtomicBoolean.class, "shoutkill");

		if(!this.hasData(Map.class, "ipmutes"))
		{
			final Map<String, Long> ipmutes = new ConcurrentHashMap<String, Long>();
			ConfigurationSection sec = this.getPlugin().getConfig().getConfigurationSection("ip-mutes");
			if(sec == null)
			{
				sec = this.getPlugin().getConfig().createSection("ip-mutes");
				this.getPlugin().saveConfig();
			}
			for(final Map.Entry<String, Object> e : sec.getValues(false).entrySet())
			{
				long v = 0;
				if(e.getValue() instanceof Long)
					v = (Long) e.getValue();
				ipmutes.put(e.getKey(), v);
			}
			this.setData("ipmutes", ipmutes);
		}
		this.mute = this.getData(Map.class, "ipmutes");
	}

	@Override
	public void onDisable()
	{
		this.users = null;
		ConfigurationSection sec = this.getPlugin().getConfig().getConfigurationSection("ip-mutes");
		if(sec == null)
		{
			sec = this.getPlugin().getConfig().createSection("ip-mutes");
			this.getPlugin().saveConfig();
		}
		for(final Map.Entry<String, Long> e : this.mute.entrySet())
			sec.set(e.getKey(), e.getValue());
		this.getPlugin().saveConfig();
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event)
	{
		final SCUser user = SCGeneral.getUser(event.getPlayer().getName());
		this.put(user.getName(), user.getChannel());
	}

	@EventHandler
	public void onChannel(final ChannelChangeEvent event)
	{
		this.put(event.getPlayerName(), event.getChannel());
	}

	@EventHandler
	public void onLeave(final PlayerQuitEvent event)
	{
		this.remove(event.getPlayer().getName());
		this.pm.remove(event.getPlayer().getName());
	}

	public synchronized void put(final String name, final ChatChannel c)
	{
		this.users.put(name, c);
	}

	public synchronized ChatChannel get(final String name)
	{
		return this.users.containsKey(name) ? this.users.get(name) : ChatChannel.NONE;
	}

	public synchronized void remove(final String name)
	{
		this.users.remove(name);
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onChat(final AsyncPlayerChatEvent event)
	{
		String message = event.getMessage();
		final Player player = event.getPlayer();
		event.setCancelled(true);

		final User euser = SCGeneral.getEssentials().getUser(player);
		if(euser.isMuted() || this.isIpMuted(player))
		{
			player.sendMessage(ChatColor.DARK_RED+"You have been muted.");
			return;
		}

		if(message.startsWith("@"))
		{
			if(message.indexOf(" ") < 0)
			{
				player.sendMessage("@<player> <message>");
				return;
			}
			Player other = null;
			if(message.startsWith("@r "))
			{
				// fetch other;
				final String r = this.pm.containsKey(player.getName()) ? this.pm.get(player.getName()) : "";
				other = Bukkit.getPlayerExact(r);
				if(other == null)
					player.sendMessage(ChatColor.DARK_RED+"You got no one to reply to.");
			}
			else
			{
				final String user = message.substring(1, message.indexOf(" "));
				final List<Player> matches = Bukkit.matchPlayer(user);
				if(matches.isEmpty())
					player.sendMessage(ChatColor.DARK_RED+"Player not found!");
				else if(matches.size() > 1)
					player.sendMessage(ChatColor.DARK_RED+"Multiple players found!");
				else
				{
					other = matches.get(0);
					if(!player.canSee(other))
					{
						other = null;
						player.sendMessage(ChatColor.DARK_RED+"Player not found!");
					}
				}
			}
			if(other != null)
			{
				final User eother = SCGeneral.getEssentials().getUser(other);
				if(eother.isIgnoredPlayer(euser))
					return;
				this.pm.put(player.getName(), other.getName());
				this.pm.put(other.getName(), player.getName());
				String mes = message.substring(message.indexOf(" ")+1);
				player.sendMessage(String.format(this.to, other.getDisplayName(), mes));
				other.sendMessage(String.format(this.from, player.getDisplayName(), mes));
				mes = String.format(this.ss, player.getName(), other.getName(), mes);
				User espy;
				for(final Player spy : Bukkit.getOnlinePlayers())
				{
					if(spy == player || spy == other)
						continue;
					espy = SCGeneral.getEssentials().getUser(spy);
					if(espy != null && espy.isOnline() && espy.isSocialSpyEnabled())
						spy.sendMessage(mes);
				}
			}
			return;
		}
		else if(message.startsWith("!") && player.hasPermission("essentials.me"))
		{
			message = String.format(this.me, player.getDisplayName(), message.substring(1));
			for(final Player other : event.getRecipients())
				other.sendMessage(message);
			return;
		}
		final ChatChannel c = this.get(player.getName());
		String m;
		switch(c)
		{
		case LOCAL:
			final Location loc = player.getLocation();
			m = String.format(this.local, this.getLocalTag(player), event.getMessage());
			for(final Player other : event.getRecipients())
			{
				if(other.getWorld() != player.getWorld())
					continue;
				if(other.getLocation().distanceSquared(loc) <= 900)
					other.sendMessage(m);
			}
			break;
		case GLOBAL:
			// Global chatkill
			if(this.shoutkill.get() && !player.hasPermission("shout.bypass.kill"))
			{
				player.sendMessage(ChatColor.DARK_RED+"The shout chat has been silenced");
				return;
			}

			if(this.hasCooldown(player))
			{
				player.sendMessage(ChatColor.DARK_RED+String.format("You must wait %d between shouts.", (int)(this.SHOUT_COOLDOWN/1000)));
				return;
			}

			String title = "";
			final String rank = this.pvptitles.containsKey(player.getName()) ? this.pvptitles.get(player.getName()) : "";
			if(!rank.isEmpty())
				title = String.format(this.t, rank);
			m = String.format(this.global, title, this.getGlobalTag(player), event.getMessage());
			for(final Player other : event.getRecipients())
				other.sendMessage(m);
			break;
		default:
			player.sendMessage(ChatColor.DARK_RED+"Oops something went wrong... try relogging.");
		}
	}

	@EventHandler
	public void onTab(final PlayerChatTabCompleteEvent event)
	{
		final String msg = event.getChatMessage();
        
		if(msg.startsWith("@") && msg.indexOf(" ") < 0)
		{
			final Player player = event.getPlayer();
			final String search = msg.substring(1);
			final List<Player> matches = Bukkit.matchPlayer(search);
			final List<String> hits = new ArrayList<String>();
			for (Player other : matches) {
				if(other != null && player.canSee(other))
					hits.add(String.format(this.at, other.getName()));
			}
			event.getTabCompletions().clear();
			event.getTabCompletions().addAll(hits);
		}
	}
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onOldCommand(final PlayerCommandPreprocessEvent event)
	{
        Player player = event.getPlayer();
		if(event.getMessage().startsWith("/me") || event.getMessage().startsWith("/eme"))
        {
            player.sendMessage(ChatColor.RED+"Use !<message>");
            event.setCancelled(true);
            return;
        }
        
        int space = event.getMessage().indexOf(" ");
        if(space > -1)
        {
            String command = event.getMessage().substring(1, space);
            if(this.cmds.contains(command))
            {
                player.sendMessage(ChatColor.RED+"Use @<playername> and @r (for quick reply).");
                event.setCancelled(true);
                return;
            }
        }
        
	}

	private boolean isIpMuted(final Player player)
	{
		final String ip = player.getAddress().getAddress().getHostAddress();
		final long expire = this.mute.containsKey(ip) ? this.mute.get(ip) : 0;
		return System.currentTimeMillis() < expire;
	}

	private boolean hasCooldown(final Player player)
	{
		if(player.hasPermission("Shout.Bypass"))
			return false;
		if(!this.cooldown.containsKey(player.getName()))
		{
			this.cooldown.put(player.getName(), System.currentTimeMillis()+this.SHOUT_COOLDOWN);
			return false;
		}
		final long expire = this.cooldown.get(player.getName());
		if(expire < System.currentTimeMillis())
		{
			this.cooldown.put(player.getName(), System.currentTimeMillis()+this.SHOUT_COOLDOWN);
			return false;
		}
		return true;
	}

	private Tag getTag(final Player player)
	{
		if(player.isOp())
		{
			if(player.hasPermission("Shout.Owner"))
				return Tag.OWNER;
			return Tag.DEV;
		}
		else if(player.hasPermission("Shout.HeadAdmin"))
			return Tag.HA;
		else if(player.hasPermission("Shout.AdminPlus"))
			return Tag.AP;
		else if(player.hasPermission("Shout.Admin"))
			return Tag.A;
		else if(player.hasPermission("Shout.Mod"))
			return Tag.M;
		else if(player.hasPermission("Shout.PremiumPlus"))
			return Tag.PREMIUMP;
		else if(player.hasPermission("Shout.Premium"))
			return Tag.PREMIUM;
		else if(player.hasPermission("Shout.VIPPlus"))
			return Tag.VIPP;
		else if(player.hasPermission("Shout.VIP"))
			return Tag.VIP;
		else
			return Tag.NONE;
	}

	private String getLocalTag(final Player player)
	{
		return this.getTag(player).getLocalTag(player);
	}

	private String getGlobalTag(final Player player)
	{
		return this.getTag(player).getGlobalTag(player);
	}

	enum Tag
	{
		OWNER("["+ChatColor.GOLD+"Owner"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s", ChatColor.GOLD+"%s"),
		DEV("["+ChatColor.GOLD+"Dev"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s", ChatColor.GOLD+"%s"),
		HA("["+ChatColor.BLACK+"H"+ChatColor.GOLD+"A"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s",
				"["+ChatColor.BLACK+"H"+ChatColor.GOLD+"A"+ChatColor.RESET+"] %s"),
				AP("["+ChatColor.DARK_RED+"A"+ChatColor.YELLOW+"+"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s",
						"["+ChatColor.DARK_RED+"A"+ChatColor.YELLOW+"+"+ChatColor.RESET+"] %s"),
						A("["+ChatColor.DARK_RED+"A"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s",
								"["+ChatColor.DARK_RED+"A"+ChatColor.RESET+"] %s"),
								M("["+ChatColor.BLUE+"M"+ChatColor.RESET+"] "+ChatColor.DARK_GRAY+"%s",
										"["+ChatColor.BLUE+"M"+ChatColor.RESET+"] %s"),
										PREMIUMP(ChatColor.BLUE+"%s"+ChatColor.YELLOW+"+"),
										PREMIUM(ChatColor.BLUE+"%s"),
										VIPP(ChatColor.GREEN+"%s"+ChatColor.YELLOW+"+"),
										VIP(ChatColor.GREEN+"%s"),
										NONE(ChatColor.DARK_GRAY+"%s", "%s")
										;

		private final String local;
		private final String global;

		Tag(final String local)
		{
			this(local, local);
		}

		Tag(final String local, final String global)
		{
			this.local = local;
			this.global = global;
		}

		public String getLocalTag(final Player player)
		{
			return String.format(this.local, player.getDisplayName());
		}

		public String getGlobalTag(final Player player)
		{
			return String.format(this.global, player.getDisplayName());
		}
	}
}
