package dev.latvian.kubejs.bindings;

import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Locale;

public class EnchantmentRarityWrapper {

	public static final EnchantmentRarityWrapper COMMON = new EnchantmentRarityWrapper(Enchantment.Rarity.COMMON);
	public static final EnchantmentRarityWrapper UNCOMMON = new EnchantmentRarityWrapper(Enchantment.Rarity.UNCOMMON);
	public static final EnchantmentRarityWrapper RARE = new EnchantmentRarityWrapper(Enchantment.Rarity.RARE);
	public static final EnchantmentRarityWrapper VERY_RARE = new EnchantmentRarityWrapper(Enchantment.Rarity.VERY_RARE);


	public final Enchantment.Rarity rarity;

	public EnchantmentRarityWrapper(Enchantment.Rarity rarity) {
        this.rarity = rarity;
    }


	public static EnchantmentRarityWrapper fromString(String s) {
        switch (s.toLowerCase(Locale.ROOT)) {
            case "COMMON":
                return COMMON;
            case "UNCOMMON":
                return UNCOMMON;
            case "RARE":
                return RARE;
            case "VERY_RARE":
                return VERY_RARE;
            default:
                return new EnchantmentRarityWrapper(Enchantment.Rarity.valueOf(s));
        }
    }
}
