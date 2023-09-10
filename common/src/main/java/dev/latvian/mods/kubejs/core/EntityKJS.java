package dev.latvian.mods.kubejs.core;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@RemapPrefixForJS("kjs$")
public interface EntityKJS extends WithPersistentData, MessageSenderKJS, ScriptTypeHolder {
	default Entity kjs$self() {
		return (Entity) this;
	}

	default Level kjs$getLevel() {
		return kjs$self().level();
	}

	@Nullable
	default MinecraftServer kjs$getServer() {
		return kjs$getLevel().getServer();
	}

	default String kjs$getType() {
		return String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(kjs$self().getType()));
	}

	default GameProfile kjs$getProfile() {
		return new GameProfile(kjs$self().getUUID(), kjs$self().getScoreboardName());
	}

	@Override
	default Component kjs$getName() {
		return kjs$self().getName();
	}

	@Override
	default Component kjs$getDisplayName() {
		return kjs$self().getDisplayName();
	}

	@Override
	default void kjs$tell(Component message) {
		kjs$self().sendSystemMessage(message);
	}

	@Override
	default int kjs$runCommand(String command) {
		if (kjs$getLevel() instanceof ServerLevel level) {
			return level.getServer().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStack(), command);
		}

		return 0;
	}

	@Override
	default int kjs$runCommandSilent(String command) {
		if (kjs$getLevel() instanceof ServerLevel level) {
			return level.getServer().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStack().withSuppressedOutput(), command);
		}

		return 0;
	}

	default boolean kjs$isPlayer() {
		return false;
	}

	@Nullable
	default ItemStack kjs$getItem() {
		return null;
	}

	default boolean kjs$isFrame() {
		return this instanceof ItemFrame;
	}

	default boolean kjs$isLiving() {
		return false;
	}

	default boolean kjs$isMonster() {
		return !kjs$self().getType().getCategory().isFriendly();
	}

	default boolean kjs$isAnimal() {
		return kjs$self().getType().getCategory().isPersistent();
	}

	default boolean kjs$isAmbientCreature() {
		return kjs$self().getType().getCategory() == MobCategory.AMBIENT;
	}

	default boolean kjs$isWaterCreature() {
		return kjs$self().getType().getCategory() == MobCategory.WATER_CREATURE;
	}

	default boolean kjs$isPeacefulCreature() {
		return kjs$self().getType().getCategory().isFriendly();
	}

	default void kjs$setX(double x) {
		kjs$setPosition(x, kjs$self().getY(), kjs$self().getZ());
	}

	default void kjs$setY(double y) {
		kjs$setPosition(kjs$self().getX(), y, kjs$self().getZ());
	}

	default void kjs$setZ(double z) {
		kjs$setPosition(kjs$self().getX(), kjs$self().getY(), z);
	}

	default double kjs$getMotionX() {
		return kjs$self().getDeltaMovement().x;
	}

	default void kjs$setMotionX(double x) {
		var m = kjs$self().getDeltaMovement();
		kjs$self().setDeltaMovement(x, m.y, m.z);
	}

	default double kjs$getMotionY() {
		return kjs$self().getDeltaMovement().y;
	}

	default void kjs$setMotionY(double y) {
		var m = kjs$self().getDeltaMovement();
		kjs$self().setDeltaMovement(m.x, y, m.z);
	}

	default double kjs$getMotionZ() {
		return kjs$self().getDeltaMovement().z;
	}

	default void kjs$setMotionZ(double z) {
		var m = kjs$self().getDeltaMovement();
		kjs$self().setDeltaMovement(m.x, m.y, z);
	}

	default void kjs$teleportTo(ResourceLocation dimension, double x, double y, double z, float yaw, float pitch) {
		var previousLevel = kjs$getLevel();
		var level = kjs$getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));

		if (level == null) {
			throw new IllegalArgumentException("Invalid dimension!");
		}

		if (!Level.isInSpawnableBounds(BlockPos.containing(x, y, z))) {
			throw new IllegalArgumentException("Invalid coordinates!");
		} else if (Float.isNaN(yaw) || Float.isNaN(pitch)) {
			throw new IllegalArgumentException("Invalid rotation!");
		}

		if (level == previousLevel) {
			kjs$setPositionAndRotation(x, y, z, yaw, pitch);
			return;
		}

		try {
			TeleportCommand.performTeleport(
				kjs$self().createCommandSourceStack(),
				kjs$self(),
				level,
				x, y, z,
				Set.of(),
				yaw, pitch,
				null
			);
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException(e.getRawMessage().getString());
		}
	}

	default void kjs$setPosition(BlockContainerJS block) {
		kjs$teleportTo(block.getDimension(), block.getX(), block.getY(), block.getZ(), kjs$self().getYRot(), kjs$self().getXRot());
	}

	default void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		kjs$self().moveTo(x, y, z, yaw, pitch);
	}

	default void kjs$setPosition(double x, double y, double z) {
		kjs$setPositionAndRotation(x, y, z, kjs$self().getYRot(), kjs$self().getXRot());
	}

	default void kjs$setRotation(float yaw, float pitch) {
		kjs$setPositionAndRotation(kjs$self().getX(), kjs$self().getY(), kjs$self().getZ(), yaw, pitch);
	}

	default EntityArrayList kjs$getPassengers() {
		return new EntityArrayList(kjs$getLevel(), kjs$self().getPassengers());
	}

	default String kjs$getTeamId() {
		var team = kjs$self().getTeam();
		return team == null ? "" : team.getName();
	}

	default boolean kjs$isOnScoreboardTeam(String teamId) {
		Team team = kjs$self().getCommandSenderWorld().getScoreboard().getPlayerTeam(teamId);
		return team != null && kjs$self().isAlliedTo(team);
	}

	default Direction kjs$getFacing() {
		if (kjs$self().getXRot() > 45F) {
			return Direction.DOWN;
		} else if (kjs$self().getXRot() < -45F) {
			return Direction.UP;
		}

		return kjs$self().getDirection();
	}

	default BlockContainerJS kjs$getBlock() {
		return new BlockContainerJS(kjs$getLevel(), kjs$self().blockPosition());
	}

	default CompoundTag kjs$getNbt() {
		var nbt = new CompoundTag();
		kjs$self().saveWithoutId(nbt);
		return nbt;
	}

	default void kjs$setNbt(@Nullable CompoundTag nbt) {
		if (nbt != null) {
			kjs$self().load(nbt);
		}
	}

	default Entity kjs$mergeNbt(@Nullable CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			return kjs$self();
		}

		var nbt = kjs$getNbt();

		for (var k : tag.getAllKeys()) {
			var t = tag.get(k);

			if (t == null || t == EndTag.INSTANCE) {
				nbt.remove(k);
			} else {
				nbt.put(k, tag.get(k));
			}
		}

		kjs$setNbt(nbt);
		return kjs$self();
	}

	default void kjs$playSound(SoundEvent id, float volume, float pitch) {
		kjs$getLevel().playSound(null, kjs$self().getX(), kjs$self().getY(), kjs$self().getZ(), id, kjs$self().getSoundSource(), volume, pitch);
	}

	default void kjs$playSound(SoundEvent id) {
		kjs$playSound(id, 1F, 1F);
	}

	default void kjs$spawn() {
		kjs$getLevel().addFreshEntity(kjs$self());
	}

	default void kjs$attack(float hp) {
		kjs$self().hurt(kjs$self().damageSources().generic(), hp);
	}

	default double kjs$getDistance(double x, double y, double z) {
		return Math.sqrt(kjs$self().distanceToSqr(x, y, z));
	}

	default double kjs$getDistanceSq(BlockPos pos) {
		return kjs$self().distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	default double kjs$getDistance(BlockPos pos) {
		return Math.sqrt(kjs$getDistanceSq(pos));
	}

	default RayTraceResultJS kjs$rayTrace(double distance, boolean fluids) {
		return new RayTraceResultJS(kjs$self(), kjs$self().pick(distance, 0F, fluids), distance);
	}

	default RayTraceResultJS kjs$rayTrace(double distance) {
		return kjs$rayTrace(distance, true);
	}

	@Nullable
	@HideFromJS
	default CompoundTag kjs$getRawPersistentData() {
		throw new NoMixinException();
	}

	@HideFromJS
	default void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
		throw new NoMixinException();
	}

	@Override
	default ScriptType kjs$getScriptType() {
		return kjs$getLevel().kjs$getScriptType();
	}
}
