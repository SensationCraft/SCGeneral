package mcMMOFix;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpFarmFix implements Listener
{

    private final Set<String> sameIp = new HashSet<String>();
    
    private final Plugin plugin;
    
    public ExpFarmFix(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if(event.getEntity() instanceof Player == false)
            return;
        if(!shouldMcMMOAllowIt(event))
            return;
        
        Entity damager = event.getDamager();
        
        Player player = (Player) event.getEntity();
        Player attacker = null;
        if(damager instanceof Arrow)
        {
            if(((Arrow)damager).getShooter() instanceof Player)
            {
                attacker = (Player) ((Arrow)damager).getShooter();
            }
        }
        else if(damager instanceof Tameable)
        {
            AnimalTamer at = ((Tameable)damager).getOwner();
            if(at != null && ((OfflinePlayer)at).isOnline())
            {
                attacker = (Player) at;
            }
        }
        else if(damager instanceof Player)
        {
            attacker = (Player) damager;
        }
        
        if(attacker == null)
            return;
        
        if(attacker.getAddress().getAddress().getHostAddress().equals(player.getAddress().getAddress().getHostAddress()))
        {
            if(!this.sameIp.contains(attacker.getName()))
            {
                this.sameIp.add(player.getName());
                this.sameIp.add(attacker.getName());
                final Player p = player;
                final Player a = attacker;
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        sameIp.remove(p.getName());
                        sameIp.remove(a.getName());
                    }
                }.runTaskLater(this.plugin, 1L);
            }
        }
    }

    private boolean shouldMcMMOAllowIt(EntityDamageByEntityEvent event)
    {
        if (event instanceof FakeEntityDamageByEntityEvent)
        {
            return false;
        }

        double damage = event.getDamage();

        if (damage <= 0)
        {
            return false;
        }

        Entity defender = event.getEntity();

        if (Misc.isNPCEntity(defender) || !defender.isValid() || !(defender instanceof LivingEntity))
        {
            return false;
        }

        LivingEntity target = (LivingEntity) defender;

        if (CombatUtils.isInvincible(target, (int) damage))
        {
            return false;
        }

        Entity attacker = event.getDamager();

        if (attacker instanceof Projectile)
        {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable)
        {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer != null && ((OfflinePlayer) animalTamer).isOnline())
            {
                attacker = (Entity) animalTamer;
            }
        }

        if (defender instanceof Player && attacker instanceof Player)
        {
            Player defendingPlayer = (Player) defender;
            Player attackingPlayer = (Player) attacker;

            // We want to make sure we're not gaining XP or applying abilities when we hit ourselves
            if (defendingPlayer.equals(attackingPlayer))
            {
                return false;
            }

            if (PartyManager.inSameParty(defendingPlayer, attackingPlayer) && !(Permissions.friendlyFire(attackingPlayer) && Permissions.friendlyFire(defendingPlayer)))
            {
                event.setCancelled(true);
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onExpEarn(McMMOPlayerXpGainEvent event)
    {
        SkillType skill = event.getSkill();
        switch(skill)
        {
            case TAMING:
            case ACROBATICS:
            case UNARMED:
            case AXES:
            case SWORDS:
            case ARCHERY:
                break;
            default:
                return;
        }
        
        if(this.sameIp.contains(event.getPlayer().getName()))
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        for(Entity e: event.getChunk().getEntities())
        {
            if(e.hasMetadata(mcMMO.entityMetadataKey) && e instanceof Monster)
            {
                e.remove();
            }
        }
    }
    
}
