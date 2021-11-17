package dev.latvian.kubejs.bindings;

import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Locale;

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

	public final EnchantmentCategory category;

	public EnchantmentCategoryWrapper(EnchantmentCategory category) {
        this.category = category;
    }

	public static EnchantmentCategoryWrapper fromString(String s) {
        switch (s.toLowerCase(Locale.ROOT)) {
            case "ARMOR": return ARMOR;
            case "ARMOR_FEET": return ARMOR_FEET;
            case "ARMOR_HEAD": return ARMOR_HEAD;
            case "ARMOR_LEGS": return ARMOR_LEGS;
            case "ARMOR_CHEST": return ARMOR_CHEST;
            case "BOW": return BOW;
            case "WEAPON": return WEAPON;
            case "BREAKABLE": return BREAKABLE;
            case "WEARABLE": return WEARABLE;
            case "CROSSBOW": return CROSSBOW;
            case "VANISHABLE": return VANISHABLE;
            case "TRIDENT": return TRIDENT;
            case "FISHING_ROD": return FISHING_ROD;
            case "DIGGER": return DIGGER;
            default: return new EnchantmentCategoryWrapper(EnchantmentCategory.valueOf(s.toUpperCase(Locale.ROOT)));
        }
    }
}
