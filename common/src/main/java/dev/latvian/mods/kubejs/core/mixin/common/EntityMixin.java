package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
@RemapPrefixForJS("kjs$")
public abstract class EntityMixin implements EntityKJS {
	@Shadow
	public abstract boolean removeTag(String string);

	private CompoundTag kjs$persistentData;

	@Override
	public CompoundTag kjs$getPersistentData() {
		if (kjs$persistentData == null) {
			kjs$persistentData = new CompoundTag();
		}

		return kjs$persistentData;
	}

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
	@RemapForJS("stepHeight")
	public float maxUpStep;

	@Shadow
	@RemapForJS("age")
	public int tickCount;

	@Shadow
	@RemapForJS("getUuid")
	public abstract UUID getUUID();

	@Shadow
	@RemapForJS("getStringUuid")
	public abstract String getStringUUID();

	@Shadow
	@RemapForJS("getUsername")
	public abstract String getScoreboardName();

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
	@RemapForJS("setMotion")
	public abstract void setDeltaMovement(double x, double y, double z);

	@Shadow
	@RemapForJS("setPositionAndRotation")
	public abstract void moveTo(double x, double y, double z, float yaw, float pitch);

	@Shadow
	@RemapForJS("addMotion")
	public abstract void push(double x, double y, double z);

	@Shadow
	@HideFromJS
	public abstract List<Entity> getPassengers();

	@Shadow
	@RemapForJS("isOnSameTeam")
	public abstract boolean isAlliedTo(Entity e);

	@Shadow
	@RemapForJS("getHorizontalFacing")
	public abstract Direction getDirection();

	@Shadow
	@RemapForJS("extinguish")
	public abstract void clearFire();

	@Shadow
	@RemapForJS("attack")
	public abstract boolean hurt(DamageSource source, float hp);

	@Shadow
	@RemapForJS("getDistanceSq")
	public abstract double distanceToSqr(double x, double y, double z);

	@Shadow
	@RemapForJS("getEntityType")
	public abstract EntityType<?> getType();

	@Shadow
	@RemapForJS("distanceToEntitySqr")
	public abstract double distanceToSqr(Entity arg);

	@Shadow
	@RemapForJS("distanceToEntity")
	public abstract float distanceTo(Entity arg);

	@Shadow
	@HideFromJS
	public abstract Level level();
}
