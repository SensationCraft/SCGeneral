package lockpicks;

import java.util.Random;

import org.bukkit.entity.Player;

public enum LockPickRank {

	DEFAULT,
	VIP,
	PREMIUM;

	private final Random random = new Random();

	public boolean tryPick(){
		if(this == DEFAULT)
			return this.random.nextBoolean();
		else if(this == VIP)
			return this.random.nextInt(4) != 3;
		else
			return true;
	}

	public static LockPickRank getByPlayer(final Player player){
		if(player.hasPermission("lockpicks.premium"))
			return LockPickRank.PREMIUM;
		else if(player.hasPermission("lockpicks.vip"))
			return LockPickRank.VIP;
		else
			return LockPickRank.DEFAULT;
	}
}
