package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class BasicEnchantment extends Enchantment {
	public final EnchantmentBuilder enchantmentBuilder;

	public BasicEnchantment(EnchantmentBuilder b) {
		super(b.rarity, b.category, b.slots);
		enchantmentBuilder = b;
	}

	@Override
	public int getMinLevel() {
		return enchantmentBuilder.minLevel;
	}

	@Override
	public int getMaxLevel() {
		return enchantmentBuilder.maxLevel;
	}

	@Override
	public int getMinCost(int i) {
		if (enchantmentBuilder.minCost != null) {
			return enchantmentBuilder.minCost.get(i);
		}

		return super.getMinCost(i);
	}

	@Override
	public int getMaxCost(int i) {
		if (enchantmentBuilder.maxCost != null) {
			return enchantmentBuilder.maxCost.get(i);
		}

		return super.getMaxCost(i);
	}

	@Override
	public int getDamageProtection(int i, DamageSource damageSource) {
		if (enchantmentBuilder.damageProtection != null) {
			return enchantmentBuilder.damageProtection.getDamageProtection(i, damageSource);
		}

		return super.getDamageProtection(i, damageSource);
	}


	@Override
	public float getDamageBonus(int i, MobType mobType) {
		if (enchantmentBuilder.damageBonus != null) {
			return enchantmentBuilder.damageBonus.getDamageBonus(i, UtilsJS.getMobTypeId(mobType));
		}

		return super.getDamageBonus(i, mobType);
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		if (enchantment == this) {
			return false;
		} else if (enchantmentBuilder.checkCompatibility != null) {
			return enchantmentBuilder.checkCompatibility.apply(KubeJSRegistries.enchantments().getId(enchantment));
		}

		return true;
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		if (super.canEnchant(itemStack)) {
			return true;
		} else if (enchantmentBuilder.canEnchant != null) {
			return enchantmentBuilder.canEnchant.apply(ItemStackJS.of(itemStack));
		}

		return false;
	}

	@Override
	public void doPostAttack(LivingEntity entity, Entity target, int level) {
		if (enchantmentBuilder.postAttack != null) {
			enchantmentBuilder.postAttack.apply((LivingEntityJS) ((EntityKJS) entity).asKJS(), (EntityJS) ((EntityKJS) target).asKJS(), level);
		}
	}

	@Override
	public void doPostHurt(LivingEntity entity, Entity target, int level) {
		if (enchantmentBuilder.postHurt != null) {
			enchantmentBuilder.postHurt.apply((LivingEntityJS) ((EntityKJS) entity).asKJS(), (EntityJS) ((EntityKJS) target).asKJS(), level);
		}
	}

	@Override
	public boolean isTreasureOnly() {
		return enchantmentBuilder.treasureOnly;
	}

	@Override
	public boolean isCurse() {
		return enchantmentBuilder.curse;
	}

	@Override
	public boolean isTradeable() {
		return enchantmentBuilder.tradeable;
	}

	@Override
	public boolean isDiscoverable() {
		return enchantmentBuilder.discoverable;
	}
}
