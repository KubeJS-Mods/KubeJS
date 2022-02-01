package dev.latvian.mods.kubejs.enchantment;

public class MaximumCostCallbackJS {
	public final int enchantLevel;
	public final EnchantmentJS enchantment;
	public int cost;

	public MaximumCostCallbackJS(int enchantLevel, EnchantmentJS enchantmentJS)
	{
        this.enchantLevel = enchantLevel;
		this.enchantment = enchantmentJS;
		this.cost = 0;
    }

	public int getEnchantLevel() {
        return enchantLevel;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }

	public int getCost() {
        return cost;
    }

	public void setCost(int cost) {
        this.cost = cost;
    }

	@Override
	public String toString() {
		return "MaximumCostCallbackJS{" +
				"level=" + enchantLevel +
				", enchantment=" + enchantment +
				", cost=" + cost +
				'}';
	}
}
