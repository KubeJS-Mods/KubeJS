package dev.latvian.mods.kubejs.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS {
	public static final UUID PLAYER_CUSTOM_SPEED = UUID.fromString("6715D9C6-1DA0-4B78-971A-5C32F5709F66");
	public static final String PLAYER_CUSTOM_SPEED_NAME = "kubejs.player.speed.modifier";

	public final LivingEntity minecraftLivingEntity;

	public LivingEntityJS(LevelJS l, LivingEntity e) {
		super(l, e);
		minecraftLivingEntity = e;
	}

	@Override
	public boolean isLiving() {
		return true;
	}

	public boolean isChild() {
		return minecraftLivingEntity.isBaby();
	}

	public float getHealth() {
		return minecraftLivingEntity.getHealth();
	}

	public void setHealth(float hp) {
		minecraftLivingEntity.setHealth(hp);
	}

	public void heal(float hp) {
		minecraftLivingEntity.heal(hp);
	}

	public float getMaxHealth() {
		return minecraftLivingEntity.getMaxHealth();
	}

	public void setMaxHealth(float hp) {
		minecraftLivingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
	}

	public boolean isUndead() {
		return minecraftLivingEntity.isInvertedHealAndHarm();
	}

	public boolean isOnLadder() {
		return minecraftLivingEntity.onClimbable();
	}

	public boolean isSleeping() {
		return minecraftLivingEntity.isSleeping();
	}

	public boolean isElytraFlying() {
		return minecraftLivingEntity.isFallFlying();
	}

	@Nullable
	public LivingEntityJS getRevengeTarget() {
		return getLevel().getLivingEntity(minecraftLivingEntity.getLastHurtByMob());
	}

	public int getRevengeTimer() {
		return minecraftLivingEntity.getLastHurtByMobTimestamp();
	}

	public void setRevengeTarget(@Nullable LivingEntityJS target) {
		minecraftLivingEntity.setLastHurtByMob(target == null ? null : target.minecraftLivingEntity);
	}

	@Nullable
	public LivingEntityJS getLastAttackedEntity() {
		return getLevel().getLivingEntity(minecraftLivingEntity.getLastHurtMob());
	}

	public int getLastAttackedEntityTime() {
		return minecraftLivingEntity.getLastHurtMobTimestamp();
	}

	public int getIdleTime() {
		return minecraftLivingEntity.getNoActionTime();
	}

	public EntityPotionEffectsJS getPotionEffects() {
		return new EntityPotionEffectsJS(minecraftLivingEntity);
	}

	@Nullable
	public DamageSource getLastDamageSource() {
		return minecraftLivingEntity.getLastDamageSource();
	}

	@Nullable
	public LivingEntityJS getAttackingEntity() {
		return getLevel().getLivingEntity(minecraftLivingEntity.getKillCredit());
	}

	public void swingArm(InteractionHand hand) {
		minecraftLivingEntity.swing(hand, true);
	}

	public ItemStackJS getEquipment(EquipmentSlot slot) {
		return ItemStackJS.of(minecraftLivingEntity.getItemBySlot(slot));
	}

	public void setEquipment(EquipmentSlot slot, ItemStackJS item) {
		minecraftLivingEntity.setItemSlot(slot, item.getItemStack());
	}

	public ItemStackJS getHeldItem(InteractionHand hand) {
		return ItemStackJS.of(minecraftLivingEntity.getItemInHand(hand));
	}

	public void setHeldItem(InteractionHand hand, ItemStackJS item) {
		minecraftLivingEntity.setItemInHand(hand, item.getItemStack());
	}

	public ItemStackJS getMainHandItem() {
		return getEquipment(EquipmentSlot.MAINHAND);
	}

	public void setMainHandItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.MAINHAND, item);
	}

	public ItemStackJS getOffHandItem() {
		return getEquipment(EquipmentSlot.OFFHAND);
	}

	public void setOffHandItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.OFFHAND, item);
	}

	public ItemStackJS getHeadArmorItem() {
		return getEquipment(EquipmentSlot.HEAD);
	}

	public void setHeadArmorItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.HEAD, item);
	}

	public ItemStackJS getChestArmorItem() {
		return getEquipment(EquipmentSlot.CHEST);
	}

	public void setChestArmorItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.CHEST, item);
	}

	public ItemStackJS getLegsArmorItem() {
		return getEquipment(EquipmentSlot.LEGS);
	}

	public void setLegsArmorItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.LEGS, item);
	}

	public ItemStackJS getFeetArmorItem() {
		return getEquipment(EquipmentSlot.FEET);
	}

	public void setFeetArmorItem(ItemStackJS item) {
		setEquipment(EquipmentSlot.FEET, item);
	}

	public void damageEquipment(EquipmentSlot slot, int amount, Consumer<ItemStackJS> onBroken) {
		var stack = minecraftLivingEntity.getItemBySlot(slot);

		if (!stack.isEmpty()) {
			stack.hurtAndBreak(amount, minecraftLivingEntity, livingEntity -> onBroken.accept(ItemStackJS.of(stack)));

			if (stack.isEmpty()) {
				minecraftLivingEntity.setItemSlot(slot, ItemStack.EMPTY);
			}
		}
	}

	public void damageEquipment(EquipmentSlot slot, int amount) {
		damageEquipment(slot, amount, stack -> {
		});
	}

	public void damageEquipment(EquipmentSlot slot) {
		damageEquipment(slot, 1);
	}

	public void damageHeldItem(InteractionHand hand, int amount, Consumer<ItemStackJS> onBroken) {
		damageEquipment(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, amount, onBroken);
	}

	public void damageHeldItem(InteractionHand hand, int amount) {
		damageHeldItem(hand, amount, stack -> {
		});
	}

	public void damageHeldItem() {
		damageHeldItem(InteractionHand.MAIN_HAND, 1);
	}

	public boolean isHoldingInAnyHand(Object ingredient) {
		var i = IngredientJS.of(ingredient);
		return i.testVanilla(minecraftLivingEntity.getItemInHand(InteractionHand.MAIN_HAND)) || i.testVanilla(minecraftLivingEntity.getItemInHand(InteractionHand.OFF_HAND));
	}

	public float getMovementSpeed() {
		ConsoleJS.SERVER.warn("'getMovementSpeed' is deprecated. Use 'getDefaultMovementSpeed' or 'getTotalMovementSpeed'");
		return minecraftLivingEntity.getSpeed();
	}

	@Deprecated
	public void setMovementSpeed(float speed) {
		ConsoleJS.SERVER.warn("'setMovementSpeed' is deprecated. Use 'setDefaultMovementSpeed', 'setMovementSpeedAddition', 'setDefaultMovementSpeedMultiplier' or 'setTotalMovementSpeedMultiplier'.");
		minecraftLivingEntity.setSpeed(speed);
	}

	public double getTotalMovementSpeed() {
		return minecraftLivingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED);
	}

	public double getDefaultMovementSpeed() {
		return minecraftLivingEntity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
	}

	public void setDefaultMovementSpeed(double speed) {
		minecraftLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
	}

	public void setMovementSpeedAddition(double speed) {
		AttributeInstance instance = minecraftLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(createSpeedModifier(speed, AttributeModifier.Operation.ADDITION));
		}
	}

	public void setDefaultMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = minecraftLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

	public void setTotalMovementSpeedMultiplier(double speed) {
		AttributeInstance instance = minecraftLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (instance != null) {
			instance.removeModifier(PLAYER_CUSTOM_SPEED);
			instance.addTransientModifier(createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_TOTAL));
		}
	}

	public boolean canEntityBeSeen(LivingEntityJS entity) {
		return BehaviorUtils.canSee(minecraftLivingEntity, entity.minecraftLivingEntity);
	}

	public float getAbsorptionAmount() {
		return minecraftLivingEntity.getAbsorptionAmount();
	}

	public void setAbsorptionAmount(float amount) {
		minecraftLivingEntity.setAbsorptionAmount(amount);
	}

	public double getReachDistance() {
		return getReachDistance(minecraftLivingEntity);
	}

	public RayTraceResultJS rayTrace() {
		return rayTrace(getReachDistance());
	}

	@ExpectPlatform
	private static double getReachDistance(LivingEntity livingEntity) {
		throw new AssertionError();
	}

	private AttributeModifier createSpeedModifier(double speed, AttributeModifier.Operation operation) {
		return new AttributeModifier(
				PLAYER_CUSTOM_SPEED,
				PLAYER_CUSTOM_SPEED_NAME,
				speed,
				operation);
	}
}
