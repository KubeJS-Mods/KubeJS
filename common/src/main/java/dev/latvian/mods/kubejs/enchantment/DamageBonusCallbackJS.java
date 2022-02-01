package dev.latvian.mods.kubejs.enchantment;

import dev.latvian.mods.kubejs.bindings.MobTypeWrapper;
import net.minecraft.world.entity.MobType;

public class DamageBonusCallbackJS {
	public final int enchantLevel;
	public final MobTypeWrapper mobType;
	public final EnchantmentJS enchantmentJS;
	public float bonus;

    public DamageBonusCallbackJS(int enchantLevel, MobType mobType, EnchantmentJS enchantmentJS) {
        this.enchantLevel = enchantLevel;
        this.mobType = new MobTypeWrapper(mobType);
        this.enchantmentJS = enchantmentJS;
		this.bonus = 0f;
    }

	public int getEnchantLevel() {
        return enchantLevel;
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
				"level=" + enchantLevel +
				", mobType=" + mobType +
				", enchantmentJS=" + enchantmentJS +
				", bonus=" + bonus +
				'}';
	}
}
