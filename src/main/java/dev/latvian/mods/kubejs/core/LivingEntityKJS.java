package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.entity.EntityPotionEffectsJS;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.item.FoodEatenKubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public interface LivingEntityKJS extends EntityKJS {
	ResourceLocation KJS_PLAYER_CUSTOM_SPEED = KubeJS.id("player.speed.modifier");

	@Override
	default LivingEntity kjs$self() {
		return (LivingEntity) this;
	}

	default void kjs$foodEaten(ItemStack is, FoodProperties food) {
		if (this instanceof LivingEntity entity) {
			var event = new FoodEatenKubeEvent(entity, is);
			var i = is.getItem();
			var b = i.kjs$getItemBuilder();

			if (b != null && b.foodBuilder != null && b.foodBuilder.eaten != null) {
				b.foodBuilder.eaten.accept(event);
			}

			var key = i.kjs$getRegistryKey();

			if (ItemEvents.FOOD_EATEN.hasListeners(key)) {
				ItemEvents.FOOD_EATEN.post(entity, key, event);
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
			stack.hurtAndBreak(amount, (ServerLevel) kjs$self().level(), kjs$self(), item -> onBroken.accept(stack));

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

	default boolean kjs$isHoldingInAnyHand(ItemPredicate i) {
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
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	default void kjs$setDefaultMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
	}

	default void kjs$setTotalMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = kjs$self().getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(KJS_PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(kjs$createSpeedModifier(speed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
	}

	default boolean kjs$canEntityBeSeen(LivingEntity entity) {
		return BehaviorUtils.canSee(kjs$self(), entity);
	}

	default double kjs$getReachDistance() {
		return kjs$self().getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
	}

	default RayTraceResultJS kjs$rayTrace() {
		return kjs$rayTrace(kjs$getReachDistance());
	}

	@Nullable
	default Entity kjs$rayTraceEntity(Predicate<Entity> filter) {
		return kjs$rayTraceEntity(kjs$getReachDistance(), filter);
	}

	default double kjs$getAttributeTotalValue(Holder<Attribute> attribute) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			return instance.getValue();
		}
		return 0.0;
	}

	default double kjs$getAttributeBaseValue(Holder<Attribute> attribute) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			return instance.getBaseValue();
		}
		return 0.0;
	}

	default void kjs$setAttributeBaseValue(Holder<Attribute> attribute, double value) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}

	default void kjs$modifyAttribute(Holder<Attribute> attribute, ResourceLocation identifier, double d, AttributeModifier.Operation operation) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			instance.removeModifier(identifier);
			instance.addTransientModifier(new AttributeModifier(identifier, d, operation));
		}
	}

	default void kjs$removeAttribute(Holder<Attribute> attribute, ResourceLocation identifier) {
		AttributeInstance instance = kjs$self().getAttribute(attribute);
		if (instance != null) {
			instance.removeModifier(identifier);
		}
	}

	private AttributeModifier kjs$createSpeedModifier(double speed, AttributeModifier.Operation operation) {
		return new AttributeModifier(
			KJS_PLAYER_CUSTOM_SPEED,
			speed,
			operation);
	}
}