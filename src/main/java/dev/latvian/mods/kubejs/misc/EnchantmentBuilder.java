package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.Wrapper;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EnchantmentBuilder extends BuilderBase<Enchantment> {
	public static Enchantment.Cost costOf(Object o) {
		o = Wrapper.unwrapped(o);

		if (o instanceof Number n) {
			return Enchantment.constantCost(n.intValue());
		}

		if (o instanceof Iterable<?> itr) {
			var it = itr.iterator();
			return Enchantment.dynamicCost(((Number) Wrapper.unwrapped(it.next())).intValue(), ((Number) Wrapper.unwrapped(it.next())).intValue());
		}

		KubeJS.LOGGER.warn("Failed to parse enchantment cost " + o);
		return Enchantment.constantCost(0);
	}

	@FunctionalInterface
	public interface DamageProtectionFunction {
		int getDamageProtection(int level, DamageSource source);
	}

	@FunctionalInterface
	public interface DamageBonusFunction {
		float getDamageBonus(int level, EntityType<?> entityType, ItemStack enchantedItem);
	}

	@FunctionalInterface
	public interface PostFunction {
		void apply(LivingEntity entity, Entity target, int level);
	}

	public transient TagKey<Item> supportedItems;
	public transient Optional<TagKey<Item>> primaryItems;
	public transient int weight;
	public transient int maxLevel;
	public transient Enchantment.Cost minCost;
	public transient Enchantment.Cost maxCost;
	public transient int anvilCost;
	public transient FeatureFlagSet requiredFeatures;
	public transient Set<EquipmentSlot> slots;

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
		supportedItems = null;
		primaryItems = Optional.empty();
		weight = 1;
		maxLevel = 1;
		minCost = null;
		maxCost = null;
		anvilCost = 1;
		requiredFeatures = FeatureFlagSet.of();
		slots = null;

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

	public EnchantmentBuilder supportedItems(ResourceLocation tag) {
		supportedItems = ItemTags.create(tag);
		return this;
	}

	public EnchantmentBuilder primaryItems(ResourceLocation tag) {
		primaryItems = Optional.of(ItemTags.create(tag));
		return this;
	}

	public EnchantmentBuilder weight(int i) {
		weight = i;
		return this;
	}

	public EnchantmentBuilder maxLevel(int i) {
		maxLevel = i;
		return this;
	}

	public EnchantmentBuilder minCost(Enchantment.Cost cost) {
		minCost = cost;
		return this;
	}

	public EnchantmentBuilder maxCost(Enchantment.Cost cost) {
		maxCost = cost;
		return this;
	}

	public EnchantmentBuilder anvilCost(int i) {
		anvilCost = i;
		return this;
	}

	public EnchantmentBuilder requiredFeatures(FeatureFlagSet f) {
		requiredFeatures = f;
		return this;
	}

	public EnchantmentBuilder slots(EquipmentSlot[] s) {
		if (slots == null) {
			slots = new HashSet<>();
		}

		slots.addAll(Arrays.asList(s));
		return this;
	}

	public EnchantmentBuilder armor() {
		return slots(new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	}

	public EnchantmentBuilder armorHead() {
		return slots(new EquipmentSlot[]{EquipmentSlot.HEAD});
	}

	public EnchantmentBuilder armorChest() {
		return slots(new EquipmentSlot[]{EquipmentSlot.CHEST});
	}

	public EnchantmentBuilder armorLegs() {
		return slots(new EquipmentSlot[]{EquipmentSlot.LEGS});
	}

	public EnchantmentBuilder armorFeet() {
		return slots(new EquipmentSlot[]{EquipmentSlot.FEET});
	}

	public EnchantmentBuilder body() {
		return slots(new EquipmentSlot[]{EquipmentSlot.BODY});
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
