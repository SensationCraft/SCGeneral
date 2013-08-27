package Fishing;

import org.bukkit.permissions.Permissible;

public enum FishingChance {

	DEFAULT(1.0),
	VIP(1.0),
	VIP_PLUS(1.0),
	PREMIUM(1.0),
	PREMIUM_PLUS(1.0);

	private double modifer;

	private FishingChance(final double modifier){
		this.modifer = modifier;
	}

	public double getModifier(){
		return this.modifer;
	}

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
}
