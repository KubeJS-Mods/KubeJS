package dev.latvian.kubejs.enchantment;

public class MinimumCostCallbackJS {
	public final int level;
	public final EnchantmentJS enchantment;

	public MinimumCostCallbackJS(int level, EnchantmentJS enchantment) {
		this.level = level;
		this.enchantment = enchantment;
    }

	public int getLevel() {
        return level;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }
}
