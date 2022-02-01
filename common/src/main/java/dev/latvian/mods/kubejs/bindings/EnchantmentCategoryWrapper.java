package dev.latvian.mods.kubejs.bindings;

import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Locale;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentCategoryWrapper {
	public static final EnchantmentCategoryWrapper ARMOR = new EnchantmentCategoryWrapper(EnchantmentCategory.ARMOR);
	public static final EnchantmentCategoryWrapper ARMOR_FEET = new EnchantmentCategoryWrapper(EnchantmentCategory.ARMOR_FEET);
	public static final EnchantmentCategoryWrapper ARMOR_HEAD = new EnchantmentCategoryWrapper(EnchantmentCategory.ARMOR_HEAD);
	public static final EnchantmentCategoryWrapper ARMOR_LEGS = new EnchantmentCategoryWrapper(EnchantmentCategory.ARMOR_LEGS);
	public static final EnchantmentCategoryWrapper ARMOR_CHEST = new EnchantmentCategoryWrapper(EnchantmentCategory.ARMOR_CHEST);
	public static final EnchantmentCategoryWrapper BOW = new EnchantmentCategoryWrapper(EnchantmentCategory.BOW);
	public static final EnchantmentCategoryWrapper WEAPON = new EnchantmentCategoryWrapper(EnchantmentCategory.WEAPON);
	public static final EnchantmentCategoryWrapper BREAKABLE = new EnchantmentCategoryWrapper(EnchantmentCategory.BREAKABLE);
	public static final EnchantmentCategoryWrapper WEARABLE = new EnchantmentCategoryWrapper(EnchantmentCategory.WEARABLE);
	public static final EnchantmentCategoryWrapper CROSSBOW = new EnchantmentCategoryWrapper(EnchantmentCategory.CROSSBOW);
	public static final EnchantmentCategoryWrapper VANISHABLE = new EnchantmentCategoryWrapper(EnchantmentCategory.VANISHABLE);
	public static final EnchantmentCategoryWrapper TRIDENT = new EnchantmentCategoryWrapper(EnchantmentCategory.TRIDENT);
	public static final EnchantmentCategoryWrapper FISHING_ROD = new EnchantmentCategoryWrapper(EnchantmentCategory.FISHING_ROD);
	public static final EnchantmentCategoryWrapper DIGGER = new EnchantmentCategoryWrapper(EnchantmentCategory.DIGGER);
	public static final EnchantmentCategoryWrapper CUSTOM = new EnchantmentCategoryWrapper(null);

	public final EnchantmentCategory category;

	public EnchantmentCategoryWrapper(EnchantmentCategory category) {
		this.category = category;
	}

	public static EnchantmentCategoryWrapper fromString(String s) {
		return switch (s.toLowerCase(Locale.ROOT)) {
			case "breakable" -> BREAKABLE;
			case "wearable" -> WEARABLE;
			case "armor" -> ARMOR;
			case "head", "helmet", "armor_head" -> ARMOR_HEAD;
			case "chest", "chestplate", "armor_chest" -> ARMOR_CHEST;
			case "legs", "leggings", "pants", "armor_legs" -> ARMOR_LEGS;
			case "feet", "boots", "armor_feet" -> ARMOR_FEET;
			case "weapon" -> WEAPON;
			case "trident" -> TRIDENT;
			case "bow" -> BOW;
			case "crossbow" -> CROSSBOW;
			case "vanishable" -> VANISHABLE;
			case "fishingrod", "rod", "fishing_rod" -> FISHING_ROD;
			case "digger" -> DIGGER;
			case "null", "none", "custom" -> CUSTOM;
			default -> new EnchantmentCategoryWrapper(EnchantmentCategory.valueOf(s.toUpperCase(Locale.ROOT)));
		};
	}

	public static EnchantmentCategoryWrapper of(Object category) {
		if(category instanceof EnchantmentCategoryWrapper) {
			return (EnchantmentCategoryWrapper) category;
		} else if(category instanceof EnchantmentCategory) {
			return new EnchantmentCategoryWrapper((EnchantmentCategory) category);
		} else if(category instanceof String) {
			return fromString((String) category);
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return this == CUSTOM;
		if (obj instanceof EnchantmentCategoryWrapper)
			return category == ((EnchantmentCategoryWrapper) obj).category;
		if (obj instanceof EnchantmentCategory)
			return category == obj;
		if (obj instanceof String)
			return category == EnchantmentCategory.valueOf(((String) obj).toUpperCase(Locale.ROOT));
		return false;
	}
}
