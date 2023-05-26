package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.entity.EntityPotionEffectsJS;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.UUID;
import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
public interface LivingEntityKJS extends EntityKJS {
	UUID KJS_PLAYER_CUSTOM_SPEED = UUID.fromString("6715D9C6-1DA0-4B78-971A-5C32F5709F66");
	String KJS_PLAYER_CUSTOM_SPEED_NAME = "kubejs.player.speed.modifier";

	@Override
	default LivingEntity kjs$self() {
		return (LivingEntity) this;
	}

	default void kjs$foodEaten(ItemStack is) {
		if (this instanceof LivingEntity entity) {
			var event = new FoodEatenEventJS(entity, is);
			var i = is.getItem();
			var b = i.kjs$getItemBuilder();

			if (b != null && b.foodBuilder != null && b.foodBuilder.eaten != null) {
				b.foodBuilder.eaten.accept(event);
			}

			if (ItemEvents.FOOD_EATEN.hasListeners()) {
				ItemEvents.FOOD_EATEN.post(ScriptType.of(entity), ItemWrapper.getId(i), event);
			}
		}
	}

	@Override
	default boolean kjs$isLiving() {
		return true;
	}

	default void kjs$setMaxHealth(float hp) {
		kjs$self().getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
	}

	default boolean kjs$isUndead() {
		return kjs$self().isInvertedHealAndHarm();
	}

	default EntityPotionEffectsJS kjs$getPotionEffects() {
		return new EntityPotionEffectsJS(kjs$self());
	}

	default void kjs$swing(InteractionHand hand) {
		kjs$self().swing(hand, true);
	}

	default void kjs$swing() {
		kjs$self().swing(InteractionHand.MAIN_HAND, true);
	}

	default ItemStack kjs$getEquipment(EquipmentSlot slot) {
		return kjs$self().getItemBySlot(slot);
	}

	default void kjs$setEquipment(EquipmentSlot slot, ItemStack item) {
		kjs$self().setItemSlot(slot, item);
	}

	default ItemStack kjs$getHeldItem(InteractionHand hand) {
		return kjs$self().getItemInHand(hand);
	}

	default void kjs$setHeldItem(InteractionHand hand, ItemStack item) {
		kjs$self().setItemInHand(hand, item);
	}

	default ItemStack kjs$getMainHandItem() {
		return kjs$getEquipment(EquipmentSlot.MAINHAND);
	}

	default void kjs$setMainHandItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.MAINHAND, item);
	}

	default ItemStack kjs$getOffHandItem() {
		return kjs$getEquipment(EquipmentSlot.OFFHAND);
	}

	default void kjs$setOffHandItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.OFFHAND, item);
	}

	default ItemStack kjs$getHeadArmorItem() {
		return kjs$getEquipment(EquipmentSlot.HEAD);
	}

	default void kjs$setHeadArmorItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.HEAD, item);
	}

	default ItemStack kjs$getChestArmorItem() {
		return kjs$getEquipment(EquipmentSlot.CHEST);
	}

	default void kjs$setChestArmorItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.CHEST, item);
	}

	default ItemStack kjs$getLegsArmorItem() {
		return kjs$getEquipment(EquipmentSlot.LEGS);
	}

	default void kjs$setLegsArmorItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.LEGS, item);
	}

	default ItemStack kjs$getFeetArmorItem() {
		return kjs$getEquipment(EquipmentSlot.FEET);
	}

	default void kjs$setFeetArmorItem(ItemStack item) {
		kjs$setEquipment(EquipmentSlot.FEET, item);
	}

	default void kjs$damageEquipment(EquipmentSlot slot, int amount, Consumer<ItemStack> onBroken) {
		var stack = kjs$self().getItemBySlot(slot);

		if (!stack.isEmpty()) {
			stack.hurtAndBreak(amount, kjs$self(), livingEntity -> onBroken.accept(stack));

			if (stack.isEmpty()) {
				kjs$self().setItemSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	default void kjs$damageEquipment(EquipmentSlot slot, int amount) {
		kjs$damageEquipment(slot, amount, stack -> {
		});
	}

	default void kjs$damageEquipment(EquipmentSlot slot) {
		kjs$damageEquipment(slot, 1);
	}

	default void kjs$damageHeldItem(InteractionHand hand, int amount, Consumer<ItemStack> onBroken) {
		kjs$damageEquipment(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, amount, onBroken);
	}

	default void kjs$damageHeldItem(InteractionHand hand, int amount) {
		kjs$damageHeldItem(hand, amount, stack -> {
		});
	}

	default void kjs$damageHeldItem() {
		kjs$damageHeldItem(InteractionHand.MAIN_HAND, 1);
	}

	default boolean kjs$isHoldingInAnyHand(Ingredient i) {
		return i.test(kjs$self().getItemInHand(InteractionHand.MAIN_HAND)) || i.test(kjs$self().getItemInHand(InteractionHand.OFF_HAND));
	}

	default double kjs$getTotalMovementSpeed() {
		return kjs$self().getAttributeValue(Attributes.MOVEMENT_SPEED);
	}

	default double kjs$getDefaultMovementSpeed() {
		return kjs$self().getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
	}

	default void kjs$setDefaultMovementSpeed(double speed) {
		kjs$self().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
	}

	default void kjs$setMovementSpeedAddition(double speed) {
		AttributeInstance instance = kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.ADDITION));
		}
	}

	default void kjs$setDefaultMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

	default void kjs$setTotalMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_TOTAL));
		}
	}

	default boolean kjs$canEntityBeSeen(LivingEntity entity) {
		return BehaviorUtils.canSee(kjs$self(), entity);
	}

	default double kjs$getReachDistance() {
		return LevelPlatformHelper.get().getReachDistance(kjs$self());
	}

	default RayTraceResultJS kjs$rayTrace() {
		return kjs$rayTrace(kjs$getReachDistance());
	}

	default double kjs$getAttributeTotalValue(Attribute attribute) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			return instance.getValue();
		}
		return 0.0;
	}

	default double kjs$getAttributeBaseValue(Attribute attribute) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			return instance.getBaseValue();
		}
		return 0.0;
	}

	default void kjs$setAttributeBaseValue(Attribute attribute, double value) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}

	default void kjs$modifyAttribute(Attribute attribute, String identifier, double d, AttributeModifier.Operation operation) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			UUID uuid = new UUID(identifier.hashCode(), identifier.hashCode());
			instance.removeModifier(uuid);
			instance.addTransientModifier(new AttributeModifier(uuid, identifier, d, operation));
		}
	}

	default void kjs$removeAttribute(Attribute attribute, String identifier) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			instance.removeModifier(new UUID(identifier.hashCode(), identifier.hashCode()));
		}
	}

	private AttributeModifier kjs$createSpeedModifier(double speed, AttributeModifier.Operation operation) {
		return new AttributeModifier(
				KJS_PLAYER_CUSTOM_SPEED,
				KJS_PLAYER_CUSTOM_SPEED_NAME,
				speed,
				operation);
	}
}