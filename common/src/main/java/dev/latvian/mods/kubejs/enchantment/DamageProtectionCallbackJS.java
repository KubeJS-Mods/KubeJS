package dev.latvian.mods.kubejs.enchantment;


import dev.latvian.mods.kubejs.entity.DamageSourceJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;

public class DamageProtectionCallbackJS {
	public final int enchantLevel;
	public final LevelJS world;
	public final DamageSourceJS source;
	public final EnchantmentJS enchantment;
	public int bonus;

    public DamageProtectionCallbackJS(int enchantLevel, DamageSource source, EnchantmentJS enchantment) {
        this.enchantLevel = enchantLevel;
		if(source.getDirectEntity() != null) {
			this.world = UtilsJS.getLevel(source.getDirectEntity().level);
		}else if(source.getEntity() != null) {
			this.world = UtilsJS.getLevel(source.getEntity().level);
		} else {
			// Rare edge case where both entities are null.
			this.world = null;
		}
        this.source = new DamageSourceJS(world,source);
        this.enchantment = enchantment;
    }

	public int getEnchantLevel() {
        return enchantLevel;
    }

    public LevelJS getWorld() {
        return world;
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
				", world=" + world +
				", source=" + source +
				", enchantment=" + enchantment +
				", bonus=" + bonus +
				'}';
	}
}
