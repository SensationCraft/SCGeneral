package fishing;

import org.bukkit.permissions.Permissible;

/**
 *
 * @author superckl - Have a taste of your own medicine
 */
public enum FishingChance {

	DEFAULT(1.0),
	PREMIUM(1.75),
	PREMIUM_PLUS(2.0),
	VIP(1.25),
	VIP_PLUS(1.5);

	public static FishingChance getByPlayer(final Permissible player){
		if(player.hasPermission("fishing.premiumplus"))
			return PREMIUM_PLUS;
		else if(player.hasPermission("fishing.premium"))
			return PREMIUM;
		else if(player.hasPermission("fishing.vipplus"))
			return VIP_PLUS;
		else if(player.hasPermission("fishing.vip"))
			return VIP;
		return DEFAULT;
	}

	private double modifer;

	private FishingChance(final double modifier){
		this.modifer = modifier;
	}

	public double getModifier(){
		return this.modifer;
	}
}
