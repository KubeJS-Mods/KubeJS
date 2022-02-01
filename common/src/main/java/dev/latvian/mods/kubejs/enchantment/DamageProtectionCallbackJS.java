package dev.latvian.mods.kubejs.enchantment;


import dev.latvian.mods.kubejs.entity.DamageSourceJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;

public class DamageProtectionCallbackJS {
	public final int enchantLevel;
	public final LevelJS level;
	public final DamageSourceJS source;
	public final EnchantmentJS enchantment;
	public int bonus;

    public DamageProtectionCallbackJS(int enchantLevel, DamageSource source, EnchantmentJS enchantment) {
        this.enchantLevel = enchantLevel;
		if(source.getDirectEntity() != null) {
			this.level = UtilsJS.getLevel(source.getDirectEntity().level);
		}else if(source.getEntity() != null) {
			this.level = UtilsJS.getLevel(source.getEntity().level);
		} else {
			// Rare edge case where both entities are null.
			this.level = null;
		}
        this.source = new DamageSourceJS(level,source);
        this.enchantment = enchantment;
    }

	public int getEnchantLevel() {
        return enchantLevel;
    }

    public LevelJS getLevel() {
        return level;
    }

    public DamageSourceJS getSource() {
        return source;
    }

    public EnchantmentJS getEnchantment() {
        return enchantment;
    }

	public int getBonus() {
		return bonus;
	}

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

	@Override
	public String toString() {
		return "DamageProtectionCallbackJS{" +
				"level=" + enchantLevel +
				", world=" + level +
				", source=" + source +
				", enchantment=" + enchantment +
				", bonus=" + bonus +
				'}';
	}
}
