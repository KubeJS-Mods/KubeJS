package dev.latvian.kubejs.enchantment;

public class MinimumCostCallbackJS {
	public final int level;
	public final EnchantmentJS enchantment;
	public int cost;

	public MinimumCostCallbackJS(int level, EnchantmentJS enchantment) {
		this.level = level;
		this.enchantment = enchantment;
		this.cost = 0;
    }

	public int getLevel() {
        return level;
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
				"level=" + level +
				", enchantment=" + enchantment +
				", cost=" + cost +
				'}';
	}
}
