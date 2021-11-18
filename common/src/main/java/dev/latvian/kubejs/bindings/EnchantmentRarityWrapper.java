package dev.latvian.kubejs.bindings;

import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Locale;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentRarityWrapper {

	public static final EnchantmentRarityWrapper COMMON = new EnchantmentRarityWrapper(Enchantment.Rarity.COMMON); // Weight: 10
	public static final EnchantmentRarityWrapper UNCOMMON = new EnchantmentRarityWrapper(Enchantment.Rarity.UNCOMMON); // Weight: 5
	public static final EnchantmentRarityWrapper RARE = new EnchantmentRarityWrapper(Enchantment.Rarity.RARE); // Weight: 2
	public static final EnchantmentRarityWrapper VERY_RARE = new EnchantmentRarityWrapper(Enchantment.Rarity.VERY_RARE); // Weight: 1

	public final Enchantment.Rarity rarity;

	public EnchantmentRarityWrapper(Enchantment.Rarity rarity) {
        this.rarity = rarity;
    }


	public static EnchantmentRarityWrapper fromString(String s) {
        switch (s.toLowerCase(Locale.ROOT)) {
            case "common":
                return COMMON;
            case "uncommon":
                return UNCOMMON;
            case "rare":
                return RARE;
			case "epic":
            case "very_rare":
                return VERY_RARE;
            default:
                return new EnchantmentRarityWrapper(Enchantment.Rarity.valueOf(s));
        }
    }

	@Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof EnchantmentRarityWrapper)
			return rarity.getWeight() == ((EnchantmentRarityWrapper) o).rarity.getWeight();
		if (o instanceof Enchantment.Rarity)
			return rarity.getWeight() == ((Enchantment.Rarity) o).getWeight();
		if (o instanceof String)
			return rarity.getWeight() == fromString((String) o).rarity.getWeight();
		return false;
    }
}
