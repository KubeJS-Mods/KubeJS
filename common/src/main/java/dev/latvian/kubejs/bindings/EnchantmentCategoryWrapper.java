package dev.latvian.kubejs.bindings;

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
        switch (s.toLowerCase(Locale.ROOT)) {
			case "breakable":
				return BREAKABLE;
			case "wearable":
				return WEARABLE;
			case "armor":
				return ARMOR;
			case "head":
			case "helmet":
			case "armor_head":
				return ARMOR_HEAD;
			case "chest":
			case "chestplate":
			case "armor_chest":
				return ARMOR_CHEST;
			case "legs":
			case "leggings":
			case "pants":
            case "armor_legs":
				return ARMOR_LEGS;
			case "feet":
			case "boots":
			case "armor_feet":
				return ARMOR_FEET;

			case "weapon":
				return WEAPON;

			case "trident":
				return TRIDENT;

			case "bow":
				return BOW;
			case "crossbow":
				return CROSSBOW;

			case "vanishable":
				return VANISHABLE;

			case "fishingrod":
			case "rod":
            case "fishing_rod":
				return FISHING_ROD;

            case "digger":
				return DIGGER;

			case "null":
			case "none":
			case "custom":
				return CUSTOM;
            default: return new EnchantmentCategoryWrapper(EnchantmentCategory.valueOf(s.toUpperCase(Locale.ROOT)));
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
			return category == (EnchantmentCategory) obj;
		if (obj instanceof String)
			return category == EnchantmentCategory.valueOf(((String) obj).toUpperCase(Locale.ROOT));
        return false;
    }
}
