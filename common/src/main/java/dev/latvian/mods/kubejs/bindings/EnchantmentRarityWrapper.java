package dev.latvian.mods.kubejs.bindings;

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
		return switch (s.toLowerCase(Locale.ROOT)) {
			case "common" -> COMMON;
			case "uncommon" -> UNCOMMON;
			case "rare" -> RARE;
			case "epic", "very_rare" -> VERY_RARE;
			default -> new EnchantmentRarityWrapper(Enchantment.Rarity.valueOf(s));
		};
	}

	public static EnchantmentRarityWrapper of(Object o) {
		if(o instanceof EnchantmentRarityWrapper) {
			return (EnchantmentRarityWrapper) o;
		} else if(o instanceof Enchantment.Rarity) {
			return new EnchantmentRarityWrapper((Enchantment.Rarity) o);
		} else if(o instanceof String) {
			return fromString((String) o);
		} else {
			return null;
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
