package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(Entity.class)
@RemapPrefixForJS("kjs$")
public abstract class EntityMixin implements EntityKJS {
	@Shadow
	public abstract void playerTouch(Player arg);

	@Unique
	private CompoundTag kjs$persistentData;

	@Override
	public CompoundTag kjs$getPersistentData() {
		if (kjs$persistentData == null) {
			kjs$persistentData = new CompoundTag();
		}

		return kjs$persistentData;
	}

	@Shadow(remap = false)
	@RemapForJS("getForgePersistentData")
	public abstract CompoundTag getPersistentData();

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
		if (kjs$persistentData != null && !kjs$persistentData.isEmpty()) {
			tag.put("KubeJSPersistentData", kjs$persistentData);
		}
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void loadKJS(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("KubeJSPersistentData")) {
			kjs$persistentData = tag.getCompound("KubeJSPersistentData");
		} else {
			kjs$persistentData = null;
		}
	}

	@Override
	@Nullable
	@HideFromJS
	public CompoundTag kjs$getRawPersistentData() {
		return kjs$persistentData;
	}

	@Override
	@HideFromJS
	public void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
		kjs$persistentData = tag;
	}

	@Shadow
	@RemapForJS("tickCount")
	public int tickCount;

	@Shadow
	@RemapForJS("getUuid")
	public abstract UUID getUUID();

	@Shadow
	@RemapForJS("getStringUuid")
	public abstract String getStringUUID();

	@Shadow
	@RemapForJS("isGlowing")
	public abstract boolean isCurrentlyGlowing();

	@Shadow
	@RemapForJS("setGlowing")
	public abstract void setGlowingTag(boolean glowing);

	@Shadow
	@RemapForJS("getYaw")
	public abstract float getYRot();

	@Shadow
	@RemapForJS("setYaw")
	public abstract void setYRot(float yaw);

	@Shadow
	@RemapForJS("getPitch")
	public abstract float getXRot();

	@Shadow
	@RemapForJS("setPitch")
	public abstract void setXRot(float pitch);

	@Shadow
	@RemapForJS("setBodyYaw")
	@Info("Sets the entity's body yaw.")
	public abstract void setYBodyRot(float yBodyRot);

	@Shadow
	@RemapForJS("getBodyYaw")
	@Info("Gets the entity's body yaw (if the entity is a `LivingEntity`), or the entity's visual rotation (if the entity is an item entity or an item frame).")
	public abstract float getVisualRotationYInDegrees();

	@Shadow
	@RemapForJS("setMotion")
	public abstract void setDeltaMovement(double x, double y, double z);

	@Shadow
	@RemapForJS("setPositionAndRotation")
	public abstract void moveTo(double x, double y, double z, float yaw, float pitch);

	@Shadow
	@RemapForJS("addMotion")
	public abstract void push(double x, double y, double z);

	/**
	 * Replaced in JS by {@link EntityKJS#kjs$getPassengers()}.
	 */
	@Shadow
	@HideFromJS
	public abstract List<Entity> getPassengers();

	@Shadow
	@RemapForJS("isOnSameTeam")
	public abstract boolean isAlliedTo(Entity e);

	/**
	 * KubeJS adds {@link EntityKJS#kjs$getFacing()} which also can specify whether the entity is looking down or up.
	 */
	@Shadow
	@RemapForJS("getHorizontalFacing")
	public abstract Direction getDirection();

	@Shadow
	@RemapForJS("extinguish")
	public abstract void extinguishFire();

	/**
	 * Replaced by
	 */
	@Shadow
	@HideFromJS
	public abstract boolean hurt(DamageSource source, float hp);

	// The remaps:
	// distanceToSqr(double x, double y, double z) - remains as is
	// distanceToSqr(Vec3 vector) - remains as is
	// distanceToSqr(Entity entity) - remapped to distanceToEntitySqr
	// distanceToBlockSqr(BlockPos block) is added by EntityKJS

	/**
	 * {@link EntityKJS#kjs$getType()} returns a string in JS.
	 */
	@Shadow
	@RemapForJS("getEntityType")
	public abstract EntityType<?> getType();

	@Shadow
	@Info("Measures the **square** of a distance of entity to the point at specified 3D position vector.")
	public abstract double distanceToSqr(Vec3 vec);

	@Shadow
	@Info("Measures the distance of entity to the point at specified `x`, `y` and `z`.")
	public abstract double distanceToSqr(double x, double y, double z);

	@Shadow
	@RemapForJS("distanceToEntitySqr")
	@Info("Measures the **square** of a distance of entity to another entity.")
	public abstract double distanceToSqr(Entity arg);

	@Shadow
	@RemapForJS("distanceToEntity")
	@Info("Measures the distance of entity to another entity.")
	public abstract float distanceTo(Entity arg);

	/**
	 * Replaced in JS by {@link EntityKJS#kjs$teleportTo(double, double, double)}.
	 */
	@Shadow
	@HideFromJS
	public abstract void teleportTo(double x, double y, double z);

	/**
	 * Hidden, most similar equivalent usable by JS is
	 * {@link EntityKJS#kjs$teleportToLevel(ServerLevel, double, double, double, float, float)}.
	 */
	@Shadow
	@HideFromJS
	public abstract boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yaw, float pitch);

	/**
	 * Replaced in JS by {@link EntityKJS#kjs$getLevel()}.
	 */
	@Shadow
	@HideFromJS
	public abstract Level level();

	/**
	 * Disambiguates {@link Entity#spawnAtLocation(ItemStack)} in JS.
	 */
	@Shadow
	@HideFromJS
	public abstract ItemEntity spawnAtLocation(ItemLike item);

	/**
	 * Disambiguates {@link Entity#spawnAtLocation(ItemStack, float)} in JS.
	 */
	@Shadow
	@HideFromJS
	public abstract ItemEntity spawnAtLocation(ItemLike item, int offsetY);


	/**
	 * Disambiguates {@link Entity#moveTo(Vec3, float, float)} in JS.
	 */
	@Shadow
	@RemapForJS("moveToBlockPos")
	public abstract void moveTo(BlockPos pos, float yRot, float xRot);
}
