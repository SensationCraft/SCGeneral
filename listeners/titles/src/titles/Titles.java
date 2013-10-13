package titles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.sensationcraft.scgeneral.SCGeneral;

import addon.Addon;
import addon.AddonDescriptionFile;
import addon.storage.Persistant;

import com.google.common.base.Joiner;

/**
 *
 * @author DarkSeraphim
 */
public class Titles extends Addon implements Listener, CommandExecutor
{

	File saveFile;

	YamlConfiguration yc;

	List<String> rankKeys;

	private final Map<String, String> lastKill = new HashMap<String, String>();

	@Persistant(key = "pvptitles", instantiationType = ConcurrentHashMap.class)
	private Map<String, String> pvptitles;

	Comparator<String> comparator = new Comparator<String>()
			{

		@Override
		public int compare(final String o1, final String o2)
		{
			final int i1 = Titles.strToInt(o1);
			final int i2 = Titles.strToInt(o2);
			return i1 - i2;
		}

			};

			private final String HELP = ChatColor.DARK_BLUE+"-- "+ChatColor.YELLOW+"SCPvPTitle"+ChatColor.DARK_BLUE+" --\n"+ChatColor.WHITE
					+ "/rank reset <playername>  - resets ones title\n"
					+ "/rank set <rank> <title>  - sets the title for that level\n"
					+ "/rank rem <rank>          - removes the rank for that level\n"
					+ "/rank list                - lists the ranks";

			private final String RANKING = "\n"
					+ChatColor.YELLOW+""+ChatColor.BOLD+"You have "+ChatColor.RED+""+ChatColor.BOLD+""+ChatColor.UNDERLINE+"%d kills\n"
					+ChatColor.YELLOW+""+ChatColor.BOLD+"Next rank title: "+ChatColor.RED+""+ChatColor.BOLD+""+ChatColor.UNDERLINE+"%s\n"
					+"\n";
			private final String RANKUP = ChatColor.translateAlternateColorCodes('&', "\n&a&lYou have recieved a new title: %s\n");

			public Titles(final SCGeneral scg, final AddonDescriptionFile desc)
			{
				super(scg, desc);
			}

			/*
			 * Internals
			 */
			public void onEnable()
			{
				this.saveFile = new File(this.getPlugin().getDataFolder(), "titles.sav");
				if(!this.saveFile.exists())
					try
				{
						if(!this.saveFile.getParentFile().exists() && !this.saveFile.getParentFile().mkdirs())
							throw new IOException("Failed to create savefile @ "+this.saveFile.getAbsolutePath());
						this.saveFile.createNewFile();
				}
				catch(final IOException ex)
				{
					ex.printStackTrace();
					this.saveFile = null;
				}

				if(this.canSave())
					this.yc = YamlConfiguration.loadConfiguration(this.saveFile);
				else
					this.yc = new YamlConfiguration();

				this.refreshRankList();

				Bukkit.getPluginCommand("rank").setExecutor(this);
				Bukkit.getPluginCommand("ranking").setExecutor(this);
			}

			protected void disable()
			{
				// Unregister the commands
				Bukkit.getPluginCommand("rank").setExecutor(null);
				Bukkit.getPluginCommand("ranking").setExecutor(null);
			}

			private boolean canSave()
			{
				return this.saveFile != null && this.saveFile.exists();
			}

			private static int strToInt(final String s)
			{
				try
				{
					return Integer.parseInt(s);
				}
				catch(final NumberFormatException ex)
				{
					return Integer.MAX_VALUE;
				}
			}

			private void saveRanks()
			{
				if(this.canSave())
					try
				{
						this.yc.save(this.saveFile);
				}
				catch(final IOException ex)
				{
					this.getPlugin().getLogger().log(Level.WARNING, "Failed to save ranks");
					ex.printStackTrace();
				}
			}

			private void refreshRankList()
			{
				ConfigurationSection section = this.getPlugin().getConfig().getConfigurationSection("ranks");
				if(section == null)
				{
					section = this.getPlugin().getConfig().createSection("ranks");
					this.getPlugin().saveConfig();
				}
				this.rankKeys = new ArrayList<String>(section.getKeys(false));
				Collections.sort(this.rankKeys, this.comparator);
			}



			public void resetKills(final String name)
			{
				this.yc.set(name, null);

			}

			public void incrementKills(final String name)
			{
				final int newrank = this.yc.getInt(name, 0) + 1;
				this.yc.set(name, newrank);
				final String title = this.getPlugin().getConfig().getString("ranks."+newrank, "");
				if(!title.isEmpty())
					Bukkit.getPluginManager().callEvent(new RankEvent(name, title));
				this.saveRanks();
			}

			public String getTitle(final String name)
			{
				String rank = "";
				final ConfigurationSection rankSection = this.getPlugin().getConfig().getConfigurationSection("ranks");
				if(rankSection == null)
					return rank;
				final int kills = this.yc.getInt(name, 0);
				int rankKills;
				for(final String key : this.rankKeys)
				{
					rankKills = Titles.strToInt(key);
					if(kills >= rankKills)
						rank = rankSection.getString(key, "");
					else
						return ChatColor.translateAlternateColorCodes('&', rank);
				}
				return ChatColor.translateAlternateColorCodes('&', rank);
			}

			public String getNextTitle(final String name)
			{
				final String rank = "";
				final ConfigurationSection rankSection = this.getPlugin().getConfig().getConfigurationSection("ranks");
				if(rankSection == null)
					return rank;
				final int kills = this.yc.getInt(name, 0);
				int rankKills;
				String str;
				final Iterator<String> it = this.rankKeys.iterator();
				while(it.hasNext())
				{
					str = it.next();
					rankKills = Titles.strToInt(str);
					if(kills < rankKills)
						return ChatColor.translateAlternateColorCodes('&', rankSection.getString(str, ""));
				}
				return "none";
			}

			/*
			 * Listeners
			 */

			@EventHandler
			public void onDeath(final PlayerDeathEvent event)
			{
				final Player player = event.getEntity();
				final String name = player.getName();
				if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent)
				{
					final Entity damager = ((EntityDamageByEntityEvent)player.getLastDamageCause()).getDamager();
					Player other = null;
					if(damager instanceof Player)
						other = (Player) damager;
					else if(damager instanceof Projectile)
						if(((Projectile)damager).getShooter() instanceof Player)
							other = (Player) ((Projectile)damager).getShooter();

					if(other == null) return;
					if(other.getAddress().getAddress().getHostAddress().equals(player.getAddress().getAddress().getHostAddress())) return;
					final String oname = other.getName();
					if(!name.equals(this.lastKill.get(oname)))
					{
						this.incrementKills(oname);
						this.lastKill.put(oname, name);
					}
				}
			}

			@EventHandler
			public void onJoin(final PlayerJoinEvent event)
			{
				final String name = event.getPlayer().getName();
				this.pvptitles.put(name, this.getTitle(name));
			}

			@EventHandler
			public void onRank(final RankEvent event)
			{
				final Player player = Bukkit.getPlayerExact(event.getName());
				if(player != null)
				{
					player.getWorld().playSound(player.getLocation(), Sound.WITHER_SPAWN, 2F, 2F);
					player.sendMessage(this.RANKUP);
					this.pvptitles.put(player.getName(), this.getTitle(player.getName()));
				}
			}

			/*
			 * CommandExecutor
			 */

			 @Override
			 public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
			 {
				 if(cmd.getName().equals("ranking"))
				 {
					 final int kills = this.yc.getInt(sender.getName(), 0);
					 sender.sendMessage(String.format(this.RANKING, kills, this.getNextTitle(sender.getName())));
					 return true;
				 }
				 if(!sender.hasPermission("titles.edit"))
				 {
					 sender.sendMessage("Unknown command. Type \"help\" for help");
					 return true;
				 }

				 if(cmd.getName().equals("rank"))
				 {
					 if(args.length == 0)
						 sender.sendMessage(this.HELP);
					 else
					 {
						 if(args[0].equals("debug"))
						 {
							 sender.sendMessage("kills: "+this.yc.getInt(sender.getName(), -1));
							 sender.sendMessage(this.getTitle(sender.getName()));
						 }
						 final boolean valid = args[0].equalsIgnoreCase("set") && args.length > 2 || args.length == 2 || args[0].equalsIgnoreCase("list");
						 if(valid)
						 {
							 if(args[0].equalsIgnoreCase("reset"))
							 {
								 if(!this.yc.contains(args[1]))
								 {
									 sender.sendMessage(ChatColor.DARK_RED+"Player not found");
									 return true;
								 }
								 else
								 {
									 this.resetKills(args[1]);
									 this.saveRanks();
									 sender.sendMessage(ChatColor.GREEN+String.format("Rank of %s reset", args[1]));
									 final Player player = Bukkit.getPlayerExact(args[1]);
									 if(player != null)
										 player.sendMessage(ChatColor.GOLD+"Your rank has been reset.");
								 }
							 }
							 else if(args[0].equalsIgnoreCase("set"))
							 {
								 final int rank = Titles.strToInt(args[1]);
								 if(rank == Integer.MAX_VALUE)
								 {
									 sender.sendMessage(ChatColor.RED+"Please give a number for the rank.");
									 return true;
								 }

								 final String title = Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length));

								 this.getPlugin().getConfig().set("ranks."+rank, title);
								 this.getPlugin().saveConfig();
								 sender.sendMessage(String.format("Title %s added for rank %d", title, rank));
								 this.refreshRankList();
								 return true;
							 }
							 else if(args[0].equalsIgnoreCase("rem"))
							 {
								 final String path = String.format("ranks.%s", args[1]);
								 if(!this.getPlugin().getConfig().contains(path))
								 {
									 sender.sendMessage(ChatColor.DARK_RED+"Rank not found.");
									 return true;
								 }
								 this.getPlugin().getConfig().set(path, null);
								 this.getPlugin().saveConfig();
								 sender.sendMessage(String.format("Rank %s cleared", args[1]));
								 this.refreshRankList();
								 return true;
							 }
							 else if(args[0].equalsIgnoreCase("list"))
							 {
								 ConfigurationSection section  = this.getPlugin().getConfig().getConfigurationSection("ranks");
								 if(section == null)
								 {
									 section = this.getPlugin().getConfig().createSection("ranks");
									 this.getPlugin().saveConfig();
								 }
								 final StringBuilder rankList = new StringBuilder(ChatColor.DARK_BLUE.toString())
								 .append("-- ").append(ChatColor.YELLOW).append("SCPvPTitles list")
								 .append(ChatColor.DARK_BLUE).append(" --");
								 for(final String rank : section.getKeys(false))
									 rankList.append("\n- ").append(rank).append(": ").append(section.getString(rank, ""));
								 sender.sendMessage(rankList.toString());
								 return true;
							 }
						 } else
							 sender.sendMessage(this.HELP);
					 }
					 return true;
				 }

				 return false;
			 }
}
