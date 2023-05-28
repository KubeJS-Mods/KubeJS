package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentBuilder extends BuilderBase {
	@FunctionalInterface
	public interface DamageProtectionFunction {
		int getDamageProtection(int level, DamageSource source);
	}

	@FunctionalInterface
	public interface DamageBonusFunction {
		float getDamageBonus(int level, String mobType);
	}

	@FunctionalInterface
	public interface PostFunction {
		void apply(LivingEntity entity, Entity target, int level);
	}

	public transient Enchantment.Rarity rarity;
	public transient EnchantmentCategory category;
	public transient EquipmentSlot[] slots;
	public transient int minLevel;
	public transient int maxLevel;
	public transient Int2IntFunction minCost;
	public transient Int2IntFunction maxCost;
	public transient DamageProtectionFunction damageProtection;
	public transient DamageBonusFunction damageBonus;
	public transient Object2BooleanFunction<ResourceLocation> checkCompatibility;
	public transient Object2BooleanFunction<ItemStack> canEnchant;
	public transient PostFunction postAttack;
	public transient PostFunction postHurt;
	public transient boolean treasureOnly;
	public transient boolean curse;
	public transient boolean tradeable;
	public transient boolean discoverable;

	public EnchantmentBuilder(ResourceLocation i) {
		super(i);
		rarity = Enchantment.Rarity.COMMON;
		category = EnchantmentCategory.DIGGER;
		slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
		minLevel = 1;
		maxLevel = 1;
		minCost = null;
		maxCost = null;
		damageProtection = null;
		damageBonus = null;
		checkCompatibility = null;
		canEnchant = null;
		postAttack = null;
		postHurt = null;
		treasureOnly = false;
		curse = false;
		tradeable = true;
		discoverable = true;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.ENCHANTMENT;
	}

	@Override
	public Enchantment createObject() {
		return new BasicEnchantment(this);
	}

	public EnchantmentBuilder rarity(Enchantment.Rarity r) {
		rarity = r;
		return this;
	}

	public EnchantmentBuilder uncommon() {
		return rarity(Enchantment.Rarity.UNCOMMON);
	}

	public EnchantmentBuilder rare() {
		return rarity(Enchantment.Rarity.RARE);
	}

	public EnchantmentBuilder veryRare() {
		return rarity(Enchantment.Rarity.VERY_RARE);
	}

	public EnchantmentBuilder category(EnchantmentCategory c) {
		category = c;
		return this;
	}

	public EnchantmentBuilder slots(EquipmentSlot[] s) {
		slots = s;
		return this;
	}

	public EnchantmentBuilder armor() {
		return category(EnchantmentCategory.ARMOR).slots(new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	}

	public EnchantmentBuilder armorHead() {
		return category(EnchantmentCategory.ARMOR_HEAD).slots(new EquipmentSlot[]{EquipmentSlot.HEAD});
	}

	public EnchantmentBuilder armorChest() {
		return category(EnchantmentCategory.ARMOR_CHEST).slots(new EquipmentSlot[]{EquipmentSlot.CHEST});
	}

	public EnchantmentBuilder armorLegs() {
		return category(EnchantmentCategory.ARMOR_LEGS).slots(new EquipmentSlot[]{EquipmentSlot.LEGS});
	}

	public EnchantmentBuilder armorFeet() {
		return category(EnchantmentCategory.ARMOR_FEET).slots(new EquipmentSlot[]{EquipmentSlot.FEET});
	}

	public EnchantmentBuilder weapon() {
		return category(EnchantmentCategory.WEAPON);
	}

	public EnchantmentBuilder fishingRod() {
		return category(EnchantmentCategory.FISHING_ROD);
	}

	public EnchantmentBuilder trident() {
		return category(EnchantmentCategory.TRIDENT);
	}

	public EnchantmentBuilder breakable() {
		return category(EnchantmentCategory.BREAKABLE).slots(EquipmentSlot.values());
	}

	public EnchantmentBuilder bow() {
		return category(EnchantmentCategory.BOW);
	}

	public EnchantmentBuilder wearable() {
		return category(EnchantmentCategory.WEARABLE);
	}

	public EnchantmentBuilder crossbow() {
		return category(EnchantmentCategory.CROSSBOW);
	}

	public EnchantmentBuilder vanishable() {
		return category(EnchantmentCategory.VANISHABLE).slots(EquipmentSlot.values());
	}

	public EnchantmentBuilder minLevel(int i) {
		minLevel = i;
		return this;
	}

	public EnchantmentBuilder maxLevel(int i) {
		maxLevel = i;
		return this;
	}

	public EnchantmentBuilder minCost(Int2IntFunction i) {
		minCost = i;
		return this;
	}

	public EnchantmentBuilder maxCost(Int2IntFunction i) {
		maxCost = i;
		return this;
	}

	public EnchantmentBuilder damageProtection(DamageProtectionFunction i) {
		damageProtection = i;
		return this;
	}

	public EnchantmentBuilder damageBonus(DamageBonusFunction i) {
		damageBonus = i;
		return this;
	}

	public EnchantmentBuilder checkCompatibility(Object2BooleanFunction<ResourceLocation> i) {
		checkCompatibility = i;
		return this;
	}

	public EnchantmentBuilder canEnchant(Object2BooleanFunction<ItemStack> i) {
		canEnchant = i;
		return this;
	}

	public EnchantmentBuilder postAttack(PostFunction i) {
		postAttack = i;
		return this;
	}

	public EnchantmentBuilder postHurt(PostFunction i) {
		postHurt = i;
		return this;
	}

	public EnchantmentBuilder treasureOnly() {
		treasureOnly = true;
		return this;
	}

	public EnchantmentBuilder curse() {
		curse = true;
		return this;
	}

	public EnchantmentBuilder untradeable() {
		tradeable = false;
		return this;
	}

	public EnchantmentBuilder undiscoverable() {
		discoverable = false;
		return this;
	}
}
