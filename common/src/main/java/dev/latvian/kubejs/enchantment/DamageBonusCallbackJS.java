package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.bindings.MobTypeWrapper;
import net.minecraft.world.entity.MobType;

public class DamageBonusCallbackJS {
	public final int level;
	public final MobTypeWrapper mobType;
	public final EnchantmentJS enchantmentJS;
	public float bonus;

    public DamageBonusCallbackJS(int level, MobType mobType, EnchantmentJS enchantmentJS) {
        this.level = level;
        this.mobType = new MobTypeWrapper(mobType);
        this.enchantmentJS = enchantmentJS;
		this.bonus = 0f;
    }

	public int getLevel() {
        return level;
    }

    public MobTypeWrapper getMobType() {
        return mobType;
    }

    public EnchantmentJS getEnchantment() {
        return enchantmentJS;
    }

	public float getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
		this.bonus = (float) bonus;
    }

	@Override
	public String toString() {
		return "DamageBonusCallbackJS{" +
				"level=" + level +
				", mobType=" + mobType +
				", enchantmentJS=" + enchantmentJS +
				", bonus=" + bonus +
				'}';
	}
}
