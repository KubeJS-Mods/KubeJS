package dev.latvian.kubejs.enchantment;

public class MaximumCostCallbackJS {
	public final int level;
	public final EnchantmentJS enchantment;

	public MaximumCostCallbackJS(int level, EnchantmentJS enchantmentJS)
	{
        this.level = level;
		this.enchantment = enchantmentJS;
    }

	public int getLevel() {
        return level;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }
}
