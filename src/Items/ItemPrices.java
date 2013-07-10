package Items;

import org.bukkit.Material;

public enum ItemPrices {

	SUGARCANE(0.25),
	COCOABEAN(0.5),
	CARROT(0.5),
	PUMPKIN(1.0),
	WHEAT(0.5),
	MELONBLOCK(1.0),
	EGG(0.5),
	POTATO(0.5),
	FISH(1.0);

	private double price;

	private ItemPrices(final double price){
		this.price = price;
	}

	public double getPrice(){
		return this.price;
	}

	public static ItemPrices translateMaterial(final Material material){
		if(material == Material.SUGAR_CANE)
			return ItemPrices.SUGARCANE;
		else if(material == Material.COCOA)
			return ItemPrices.COCOABEAN;
		else if(material == Material.CARROT_ITEM)
			return ItemPrices.CARROT;
		else if(material == Material.PUMPKIN)
			return ItemPrices.PUMPKIN;
		else if(material == Material.WHEAT)
			return ItemPrices.WHEAT;
		else if(material == Material.MELON_BLOCK)
			return ItemPrices.MELONBLOCK;
		else if(material == Material.EGG)
			return ItemPrices.EGG;
		else if(material == Material.POTATO_ITEM)
			return ItemPrices.POTATO;
		else if(material == Material.RAW_FISH)
			return ItemPrices.FISH;
		return null;
	}

}
