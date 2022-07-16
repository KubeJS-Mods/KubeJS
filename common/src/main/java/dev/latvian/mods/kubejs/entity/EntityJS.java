package dev.latvian.mods.kubejs.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.core.AsKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.MessageSender;
import dev.latvian.mods.kubejs.util.WrappedJS;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class EntityJS implements MessageSender, WrappedJS {
	private static Map<String, DamageSource> damageSourceMap;

	public transient Entity minecraftEntity;

	public EntityJS(Entity e) {
		minecraftEntity = e;
	}

	public Entity getMinecraftEntity() {
		return minecraftEntity;
	}

	public final LevelJS getLevel() {
		return minecraftEntity.level.asKJS();
	}

	@Nullable
	public ServerJS getServer() {
		return getLevel().getServer();
	}

	public UUID getId() {
		return minecraftEntity.getUUID();
	}

	public String getType() {
		return Registries.getId(minecraftEntity.getType(), Registry.ENTITY_TYPE_REGISTRY).toString();
	}

	@Override
	public Component getName() {
		return minecraftEntity.getName();
	}

	public GameProfile getProfile() {
		return new GameProfile(getId(), minecraftEntity.getEncodeId());
	}

	@Override
	public Component getDisplayName() {
		return minecraftEntity.getDisplayName();
	}

	@Override
	public void tell(Component message) {
		minecraftEntity.sendMessage(message, Util.NIL_UUID);
	}

	public String toString() {
		return minecraftEntity.getName().getString() + "-" + getId();
	}

	@Nullable
	public PlayerJS<?> getPlayer() {
		return isPlayer() ? (PlayerJS<?>) this : null;
	}

	@Nullable
	public ItemStackJS getItem() {
		return null;
	}

	public boolean isFrame() {
		return false;
	}

	public Set<String> getTags() {
		return minecraftEntity.getTags();
	}

	public boolean isAlive() {
		return minecraftEntity.isAlive();
	}

	public boolean isLiving() {
		return false;
	}

	public boolean isPlayer() {
		return false;
	}

	public boolean isCrouching() {
		return minecraftEntity.isCrouching();
	}

	public boolean isSprinting() {
		return minecraftEntity.isSprinting();
	}

	public boolean isSwimming() {
		return minecraftEntity.isSwimming();
	}

	public boolean isGlowing() {
		return minecraftEntity.isCurrentlyGlowing();
	}

	public void setGlowing(boolean glowing) {
		minecraftEntity.setGlowingTag(glowing);
	}

	public boolean isInvisible() {
		return minecraftEntity.isInvisible();
	}

	public void setInvisible(boolean invisible) {
		minecraftEntity.setInvisible(invisible);
	}

	public boolean isInvulnerable() {
		return minecraftEntity.isInvulnerable();
	}

	public void setInvulnerable(boolean invulnerable) {
		minecraftEntity.setInvulnerable(invulnerable);
	}

	public boolean isBoss() {
		return !minecraftEntity.canChangeDimensions();
	}

	public boolean isMonster() {
		return !minecraftEntity.getType().getCategory().isFriendly();
	}

	public boolean isAnimal() {
		return minecraftEntity.getType().getCategory().isPersistent();
	}

	public boolean isAmbientCreature() {
		return minecraftEntity.getType().getCategory() == MobCategory.AMBIENT;
	}

	public boolean isWaterCreature() {
		return minecraftEntity.getType().getCategory() == MobCategory.WATER_CREATURE;
	}

	public boolean isPeacefulCreature() {
		return minecraftEntity.getType().getCategory().isFriendly();
	}

	public boolean isOnGround() {
		return minecraftEntity.isOnGround();
	}

	public float getFallDistance() {
		return minecraftEntity.fallDistance;
	}

	public void setFallDistance(float fallDistance) {
		minecraftEntity.fallDistance = fallDistance;
	}

	public float getStepHeight() {
		return minecraftEntity.maxUpStep;
	}

	public void setStepHeight(float stepHeight) {
		minecraftEntity.maxUpStep = stepHeight;
	}

	public boolean getNoClip() {
		return minecraftEntity.noPhysics;
	}

	public void setNoClip(boolean noClip) {
		minecraftEntity.noPhysics = noClip;
	}

	public boolean isSilent() {
		return minecraftEntity.isSilent();
	}

	public void setSilent(boolean isSilent) {
		minecraftEntity.setSilent(isSilent);
	}

	public boolean getNoGravity() {
		return minecraftEntity.isNoGravity();
	}

	public void setNoGravity(boolean noGravity) {
		minecraftEntity.setNoGravity(noGravity);
	}

	public double getX() {
		return minecraftEntity.getX();
	}

	public void setX(double x) {
		minecraftEntity.setPos(x, getY(), getZ());
	}

	public double getY() {
		return minecraftEntity.getY();
	}

	public void setY(double y) {
		minecraftEntity.setPos(getX(), y, getZ());
	}

	public double getZ() {
		return minecraftEntity.getZ();
	}

	public void setZ(double z) {
		minecraftEntity.setPos(getX(), getY(), z);
	}

	public float getYaw() {
		return minecraftEntity.getYRot();
	}

	public void setYaw(float yaw) {
		minecraftEntity.setYRot(yaw);
	}

	public float getPitch() {
		return minecraftEntity.getXRot();
	}

	public void setPitch(float pitch) {
		minecraftEntity.setXRot(pitch);
	}

	public double getMotionX() {
		return minecraftEntity.getDeltaMovement().x;
	}

	public void setMotionX(double x) {
		var m = minecraftEntity.getDeltaMovement();
		minecraftEntity.setDeltaMovement(x, m.y, m.z);
	}

	public double getMotionY() {
		return minecraftEntity.getDeltaMovement().y;
	}

	public void setMotionY(double y) {
		var m = minecraftEntity.getDeltaMovement();
		minecraftEntity.setDeltaMovement(m.x, y, m.z);
	}

	public double getMotionZ() {
		return minecraftEntity.getDeltaMovement().z;
	}

	public void setMotionZ(double z) {
		var m = minecraftEntity.getDeltaMovement();
		minecraftEntity.setDeltaMovement(m.x, m.y, z);
	}

	public void setMotion(double x, double y, double z) {
		minecraftEntity.setDeltaMovement(x, y, z);
	}

	public int getTicksExisted() {
		return minecraftEntity.tickCount;
	}

	public void setPosition(BlockContainerJS block) {
		teleportTo(block.getDimension(), block.getX(), block.getY(), block.getZ(), getYaw(), getPitch());
	}

	public void setPosition(double x, double y, double z) {
		setPositionAndRotation(x, y, z, getYaw(), getPitch());
	}

	public void setRotation(float yaw, float pitch) {
		setPositionAndRotation(getX(), getY(), getZ(), yaw, pitch);
	}

	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		minecraftEntity.moveTo(x, y, z, yaw, pitch);
	}

	public void teleportTo(ResourceLocation dimension, double x, double y, double z, float yaw, float pitch) {
		var level = getServer().getLevel(dimension);
		if (level == null) {
			throw new IllegalArgumentException("Invalid dimension!");
		}

		if (!Level.isInSpawnableBounds(new BlockPos(x, y, z))) {
			throw new IllegalArgumentException("Invalid coordinates!");
		} else if (Float.isNaN(yaw) || Float.isNaN(pitch)) {
			throw new IllegalArgumentException("Invalid rotation!");
		}

		try {
			var previousLevel = getLevel();
			TeleportCommand.performTeleport(
					minecraftEntity.createCommandSourceStack(),
					minecraftEntity,
					level.getMinecraftLevel(),
					x, y, z,
					Set.of(),
					yaw, pitch,
					null
			);

			// ah yes, i sure do love when entities get cloned
			if (previousLevel != level) {
				minecraftEntity = level.getMinecraftLevel().getEntity(getId());
			}
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(e.getRawMessage().getString());
		}
	}

	public void addMotion(double x, double y, double z) {
		minecraftEntity.setDeltaMovement(minecraftEntity.getDeltaMovement().add(x, y, z));
	}

	@Override
	public int runCommand(String command) {
		if (getLevel() instanceof ServerLevelJS level) {
			return level.getServer().getMinecraftServer().getCommands().performCommand(minecraftEntity.createCommandSourceStack(), command);
		}

		return 0;
	}

	@Override
	public int runCommandSilent(String command) {
		if (getLevel() instanceof ServerLevelJS level) {
			return level.getServer().getMinecraftServer().getCommands().performCommand(minecraftEntity.createCommandSourceStack().withSuppressedOutput(), command);
		}

		return 0;
	}

	public void kill() {
		minecraftEntity.kill();
	}

	public void remove() {
		minecraftEntity.remove(Entity.RemovalReason.DISCARDED);
	}

	public boolean startRiding(EntityJS e, boolean force) {
		return minecraftEntity.startRiding(e.minecraftEntity, force);
	}

	public void removePassengers() {
		minecraftEntity.ejectPassengers();
	}

	public void dismountRidingEntity() {
		minecraftEntity.stopRiding();
	}

	public EntityArrayList getPassengers() {
		return new EntityArrayList(getLevel(), minecraftEntity.getPassengers());
	}

	public boolean isPassenger(EntityJS e) {
		return minecraftEntity.hasPassenger(e.minecraftEntity);
	}

	public EntityArrayList getRecursivePassengers() {
		return new EntityArrayList(getLevel(), minecraftEntity.getIndirectPassengers());
	}

	@Nullable
	public EntityJS getRidingEntity() {
		return AsKJS.wrapSafe(minecraftEntity.getVehicle());
	}

	public String getTeamId() {
		var team = minecraftEntity.getTeam();
		return team == null ? "" : team.getName();
	}

	public boolean isOnSameTeam(EntityJS e) {
		return minecraftEntity.isAlliedTo(e.minecraftEntity);
	}

	public boolean isOnScoreboardTeam(String teamID) {
		Team team = minecraftEntity.getCommandSenderWorld().getScoreboard().getPlayerTeam(teamID);
		return team != null && minecraftEntity.isAlliedTo(team);
	}

	public void setCustomName(Component name) {
		minecraftEntity.setCustomName(name);
	}

	public Component getCustomName() {
		return minecraftEntity.getCustomName();
	}

	public boolean getHasCustomName() {
		return minecraftEntity.hasCustomName();
	}

	public void setCustomNameAlwaysVisible(boolean b) {
		minecraftEntity.setCustomNameVisible(b);
	}

	public boolean getCustomNameAlwaysVisible() {
		return minecraftEntity.isCustomNameVisible();
	}

	public Direction getHorizontalFacing() {
		return minecraftEntity.getDirection();
	}

	public Direction getFacing() {
		if (getPitch() > 45F) {
			return Direction.DOWN;
		} else if (getPitch() < -45F) {
			return Direction.UP;
		}

		return getHorizontalFacing();
	}

	public float getEyeHeight() {
		return minecraftEntity.getEyeHeight();
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(minecraftEntity.level, minecraftEntity.blockPosition());
	}

	public void setOnFire(int seconds) {
		minecraftEntity.setSecondsOnFire(seconds);
	}

	public void extinguish() {
		minecraftEntity.clearFire();
	}

	public CompoundTag getFullNBT() {
		var nbt = new CompoundTag();
		minecraftEntity.saveWithoutId(nbt);
		return nbt;
	}

	public void setFullNBT(@Nullable CompoundTag nbt) {
		if (nbt != null) {
			minecraftEntity.load(nbt);
		}
	}

	public EntityJS mergeFullNBT(@Nullable CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			return this;
		}

		var nbt = getFullNBT();

		for (var k : tag.getAllKeys()) {
			var t = tag.get(k);

			if (t == null || t == EndTag.INSTANCE) {
				nbt.remove(k);
			} else {
				nbt.put(k, tag.get(k));
			}
		}

		setFullNBT(nbt);
		return this;
	}

	@Deprecated
	public CompoundTag getNbt() {
		return getPersistentData();
	}

	public CompoundTag getPersistentData() {
		return minecraftEntity.getPersistentDataKJS();
	}

	public void playSound(SoundEvent id, float volume, float pitch) {
		minecraftEntity.level.playSound(null, getX(), getY(), getZ(), id, minecraftEntity.getSoundSource(), volume, pitch);
	}

	public void playSound(SoundEvent id) {
		playSound(id, 1F, 1F);
	}

	public void spawn() {
		getLevel().minecraftLevel.addFreshEntity(minecraftEntity);
	}

	public void attack(String source, float hp) {
		if (damageSourceMap == null) {
			damageSourceMap = new HashMap<>();

			try {
				for (var field : DamageSource.class.getDeclaredFields()) {
					field.setAccessible(true);

					if (Modifier.isStatic(field.getModifiers()) && field.getType() == DamageSource.class) {
						var s = (DamageSource) field.get(null);
						damageSourceMap.put(s.getMsgId(), s);
					}
				}
			} catch (Exception ignored) {
			}
		}

		var s = damageSourceMap.getOrDefault(source, DamageSource.GENERIC);
		minecraftEntity.hurt(s, hp);
	}

	public void attack(float hp) {
		minecraftEntity.hurt(DamageSource.GENERIC, hp);
	}

	public RayTraceResultJS rayTrace(double distance) {
		double xRot = minecraftEntity.getXRot();
		double yRot = minecraftEntity.getYRot();
		var fromPos = minecraftEntity.getEyePosition(1);
		var x0 = Math.sin(-yRot * (Math.PI / 180D) - (float) Math.PI);
		var z0 = Math.cos(-yRot * (Math.PI / 180D) - (float) Math.PI);
		var y0 = -Math.cos(-xRot * (Math.PI / 180D));
		var y = Math.sin(-xRot * (Math.PI / 180D));
		var x = x0 * y0;
		var z = z0 * y0;
		var toPos = fromPos.add(x * distance, y * distance, z * distance);
		HitResult hitResult = minecraftEntity.level.clip(new ClipContext(fromPos, toPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, minecraftEntity));
		return new RayTraceResultJS(this, hitResult, distance);
	}

	public boolean isInWater() {
		return minecraftEntity.isInWater();
	}

	public boolean isUnderWater() {
		return minecraftEntity.isUnderWater();
	}

	public double getDistanceSq(double x, double y, double z) {
		return minecraftEntity.distanceToSqr(x, y, z);
	}

	public double getDistance(double x, double y, double z) {
		return Math.sqrt(getDistanceSq(x, y, z));
	}

	public double getDistanceSq(BlockPos pos) {
		return getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	public double getDistance(BlockPos pos) {
		return Math.sqrt(getDistanceSq(pos));
	}
}