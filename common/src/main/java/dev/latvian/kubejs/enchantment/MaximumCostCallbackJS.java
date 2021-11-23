package dev.latvian.kubejs.enchantment;

public class MaximumCostCallbackJS {
	public final int level;
	public final EnchantmentJS enchantment;
	public int cost;

	public MaximumCostCallbackJS(int level, EnchantmentJS enchantmentJS)
	{
        this.level = level;
		this.enchantment = enchantmentJS;
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
		return "MaximumCostCallbackJS{" +
				"level=" + level +
				", enchantment=" + enchantment +
				", cost=" + cost +
				'}';
	}
}
