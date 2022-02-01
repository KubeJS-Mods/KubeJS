package dev.latvian.mods.kubejs.enchantment;

public class MinimumCostCallbackJS {
	public final int enchantLevel;
	public final EnchantmentJS enchantment;
	public int cost;

	public MinimumCostCallbackJS(int enchantLevel, EnchantmentJS enchantment) {
		this.enchantLevel = enchantLevel;
		this.enchantment = enchantment;
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
		return "MinimumCostCallbackJS{" +
				"level=" + enchantLevel +
				", enchantment=" + enchantment +
				", cost=" + cost +
				'}';
	}
}
