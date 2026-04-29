package dev.latvian.mods.kubejs.core;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.entity.KubeRayTraceResult;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public interface EntityKJS extends WithPersistentData, MessageSenderKJS, ScriptTypeHolder {
	@HideFromJS
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
		return kjs$self().getType().kjs$getId();
	}

	@ThisIs(LocalPlayer.class)
	@Info("Checks, whether the entity is a reference to yourself - that is - the client player you are controlling.")
	default boolean kjs$isSelf() {
		return false;
	}

	@Nullable
	@Info("If the entity is a player, gets the player's profile, otherwise returns `null`.")
	default GameProfile kjs$getProfile() {
		return null;
	}

	@Info("Gets the entity's custom name, or entity ID if entity has no custom name.")
	default String kjs$getUsername() {
		Component customName = kjs$self().getCustomName();
		return customName != null ? customName.getString() : kjs$getType();
	}

	@Override
	default Component kjs$getName() {
		return kjs$self().getName();
	}

	@Override
	default Component kjs$getDisplayName() {
		return kjs$self().getDisplayName();
	}

	@Info(value = "Sends a message in chat to the entity.", params = {
		@Param(name = "message", value = "A text component. It may be a string, which will be implicitly wrapped into a text component."),
	})
	// TODO: move to PlayerKJS? maybe split MessageSender?
	@Override
	default void kjs$tell(Component message) {
		if (kjs$self() instanceof Player player) {
			player.sendSystemMessage(message);
		}
	}

	@Override
	@Info(value = "Runs the specified console command with permission level of the entity.", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	default void kjs$runCommand(String command) {
		if (kjs$getLevel() instanceof ServerLevel level) {
			level.getServer().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStackForNameResolution(level), command);
		}
	}

	@Override
	@Info(value = "Runs the specified console command with permission level of the entity. The command won't output any logs in chat nor console.", params = {
		@Param(name = "command", value = "The console command. Slash at the beginning is optional."),
	})
	default void kjs$runCommandSilent(String command) {
		if (kjs$getLevel() instanceof ServerLevel level) {
			level.getServer().getCommands().performPrefixedCommand(kjs$self().createCommandSourceStackForNameResolution(level).withSuppressedOutput(), command);
		}
	}

	@ThisIs(Player.class)
	@Info("Checks if the entity is a player entity.")
	default boolean kjs$isPlayer() {
		return false;
	}

	@ThisIs(ServerPlayer.class)
	@Info("Checks if the entity is a server-side player.")
	default boolean kjs$isServerPlayer() {
		return false;
	}

	@ThisIs(AbstractClientPlayer.class)
	@Info("Checks if the entity is a client-side player.")
	default boolean kjs$isClientPlayer() {
		return false;
	}

	@Nullable
	@Info("""
		Gets the item stack corresponding to either:
		- the item contained in the item entity,
		- the item in the item frame.
		Will be `null` if the entity is neither an item entity nor an item frame.
		""")
	default ItemStack kjs$getItem() {
		return null;
	}

	@ThisIs(ItemFrame.class)
	@Info("Checks if the entity is an item frame entity.")
	default boolean kjs$isFrame() {
		return this instanceof ItemFrame;
	}

	@ThisIs(ItemEntity.class)
	@Info("Checks if the entity is an item entity.")
	default boolean kjs$isItem() {
		return this instanceof ItemEntity;
	}

	@ThisIs(LivingEntity.class)
	@Info("Checks if the entity is a `LivingEntity`.")
	default boolean kjs$isLiving() {
		return false;
	}

	@Info("Checks if the entity is a monster.")
	default boolean kjs$isMonster() {
		return !kjs$self().getType().getCategory().isFriendly();
	}

	@Info("Checks if the entity is an animal.")
	default boolean kjs$isAnimal() {
		return kjs$self().getType().getCategory().isPersistent();
	}

	@Info("Checks if the entity is an ambient creature.")
	default boolean kjs$isAmbientCreature() {
		return kjs$self().getType().getCategory() == MobCategory.AMBIENT;
	}

	@Info("Checks if the entity is a water creature.")
	default boolean kjs$isWaterCreature() {
		return kjs$self().getType().getCategory() == MobCategory.WATER_CREATURE;
	}

	@Info("Checks if the entity is a peaceful creature (not a monster).")
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

	private void checkDestinationValidity(BlockPos blockPos, float yaw, float pitch) throws IllegalArgumentException {
		if (!Level.isInSpawnableBounds(blockPos)) {
			throw new IllegalArgumentException("The provided coordinates are out of bounds.");
		}
		if (Float.isNaN(yaw)) {
			throw new IllegalArgumentException("Yaw is not a number.");
		}
		if (Float.isNaN(pitch)) {
			throw new IllegalArgumentException("Pitch is not a number.");
		}
	}

	@Info(value = "Teleports an entity to a specified `ServerLevel`, to specified coordinates and rotation.", params = {
		@Param(name = "level", value = "A `ServerLevel` to teleport the entity to."),
		@Param(name = "x", value = "The `x` target coordinate."),
		@Param(name = "y", value = "The `y` target coordinate."),
		@Param(name = "z", value = "The `z` target coordinate."),
		@Param(name = "yaw", value = "The entity's target yaw."),
		@Param(name = "pitch", value = "The entity's target pitch.")
	})
	default boolean kjs$teleportToLevel(ServerLevel level, double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
		Entity self = kjs$self();
		Level previousLevel = kjs$getLevel();

		checkDestinationValidity(BlockPos.containing(x, y, z), yaw, pitch);

		if (level == previousLevel) {
			kjs$setPositionAndRotation(x, y, z, yaw, pitch);
			return true;
		}

		float adjustedYaw = Mth.wrapDegrees(yaw);
		float adjustedPitch = Mth.wrapDegrees(pitch);

		boolean teleportSucceeded = self.teleportTo(level, x, y, z, Set.of(), adjustedYaw, adjustedPitch, false);
		if (!teleportSucceeded) {
			return false;
		}

		try {
			TeleportCommand.performTeleport(
				kjs$self().createCommandSourceStackForNameResolution(level),
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
		return true;
	}

	@Info(value = "Teleports an entity to a dimension of specified ID, to specified coordinates and rotation.", params = {
		@Param(name = "dimension", value = "A `ResourceLocation` of the target dimension. It can be a string representing the dimension ID."),
		@Param(name = "x", value = "The `x` target coordinate."),
		@Param(name = "y", value = "The `y` target coordinate."),
		@Param(name = "z", value = "The `z` target coordinate."),
		@Param(name = "yaw", value = "The entity's target yaw."),
		@Param(name = "pitch", value = "The entity's target pitch.")
	})
	default boolean kjs$teleportTo(Identifier dimension, double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
		ServerLevel level = kjs$getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));

		if (level == null) {
			throw new IllegalArgumentException("The provided dimension ID is invalid.");
		}

		return kjs$teleportToLevel(level, x, y, z, yaw, pitch);
	}

	@Info(value = "Teleports an entity to a dimension of specified ID, to specified coordinates and rotation.", params = {
		@Param(name = "x", value = "The `x` target coordinate."),
		@Param(name = "y", value = "The `y` target coordinate."),
		@Param(name = "z", value = "The `z` target coordinate."),
		@Param(name = "yaw", value = "The entity's target yaw."),
		@Param(name = "pitch", value = "The entity's target pitch.")
	})
	default void kjs$teleportTo(double x, double y, double z, float yaw, float pitch) throws IllegalArgumentException {
		checkDestinationValidity(BlockPos.containing(x, y, z), yaw, pitch);
		kjs$setPositionAndRotation(x, y, z, yaw, pitch);
	}

	default void kjs$setPosition(LevelBlock block) {
		kjs$teleportTo(block.getDimension(), block.getX(), block.getY(), block.getZ(), kjs$self().getYRot(), kjs$self().getXRot());
	}

	default void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		kjs$self().snapTo(x, y, z, yaw, pitch);
	}

	default void kjs$setPosition(double x, double y, double z) {
		kjs$setPositionAndRotation(x, y, z, kjs$self().getYRot(), kjs$self().getXRot());
	}

	default void kjs$setRotation(float yaw, float pitch) {
		kjs$setPositionAndRotation(kjs$self().getX(), kjs$self().getY(), kjs$self().getZ(), yaw, pitch);
	}

	@Info("Gets a list of all passengers of the entity.")
	default EntityArrayList kjs$getPassengers() {
		return new EntityArrayList(kjs$self().getPassengers());
	}

	@Deprecated
	@Info("Replaced by `entity.getTeamName()`")
	default String kjs$getTeamId() {
		return kjs$getTeamName();
	}

	@Info("Gets the name of the team entity is in, or `''` (empty string) if the entity is not part of any team")
	default String kjs$getTeamName() {
		var team = kjs$self().getTeam();
		return team == null ? "" : team.getName();
	}

	@Info("Checks, whether the entity is part of any team.")
	default boolean kjs$isOnScoreboardTeam() {
		return kjs$self().getTeam() != null;
	}

	@Info(value = "Checks, whether the entity is part of a team called `teamName`.", params = {
		@Param(name = "teamName", value = "The name of the team to check.")
	})
	default boolean kjs$isOnScoreboardTeam(String teamName) {
		Team team = kjs$self().level().getScoreboard().getPlayerTeam(teamName);
		return team != null && kjs$self().isAlliedTo(team);
	}

	@Info("""
		Gets the entity's facing direction.
		If the entity faces more than 45 degrees up or down, the resulting facing direction is respectively `up` or `down`.
		Otherwise, the resulting facing direction is determined by whichever cardinal direction is closer to entity's yaw.
		""")
	default Direction kjs$getFacing() {
		if (kjs$self().getXRot() > 45F) {
			return Direction.DOWN;
		} else if (kjs$self().getXRot() < -45F) {
			return Direction.UP;
		}

		return kjs$self().getDirection();
	}

	@Info("Gets a block at the position of the entity.")
	default LevelBlock kjs$getBlock() {
		return kjs$getLevel().kjs$getBlock(kjs$self().blockPosition());
	}

	default CompoundTag kjs$getNbt() {
		var registries = kjs$self().level().registryAccess();
		var problems = new ProblemReporter.Collector(() -> "kubejs");

		var out = TagValueOutput.createWithContext(problems, registries);
		kjs$self().saveWithoutId(out);
		return out.buildResult();
	}

	default void kjs$setNbt(@Nullable CompoundTag nbt) {
		if (nbt == null) {
			return;
		}
		var registries = kjs$self().level().registryAccess();
		var problems = new ProblemReporter.Collector(() -> "kubejs");

		var in = TagValueInput.create(problems, registries, nbt);
		kjs$self().load(in);
	}


	default Entity kjs$mergeNbt(@Nullable CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			return kjs$self();
		}

		var nbt = kjs$getNbt();

		for (var k : tag.keySet()) {
			var t = tag.get(k);

			if (t == null || t == EndTag.INSTANCE) {
				nbt.remove(k);
			} else {
				nbt.put(k, t);
			}
		}

		kjs$setNbt(nbt);
		return kjs$self();
	}

	default void kjs$spawn() {
		kjs$getLevel().addFreshEntity(kjs$self());
	}

	@Info(value = "Damages an entity by a given amount of HP dealing generic damage.", params = {
		@Param(name = "hp", value = "The amount of damage to deal."),
	})
	default boolean kjs$damage(float hp) {
		return kjs$damage(hp, kjs$self().damageSources().generic());
	}

	@Info(value = "Damages an entity by a given amount of HP dealing a specific type of damage.", params = {
		@Param(name = "hp", value = "The amount of damage to deal."),
		@Param(name = "source", value = "The damage source. It may be a string specifying a damage source, like `'minecraft:cramming'`.")
	})
	default boolean kjs$damage(float hp, DamageSource source) {
		if (kjs$getLevel() instanceof ServerLevel serverLevel) {
			return kjs$self().hurtServer(serverLevel, source, hp);
		}
		return false;
	}

	@Deprecated
	default void kjs$attack(float hp) {
		kjs$damage(hp);
	}

	@Info("Replaced by `entity.damage(hp, damageSource)`")
	@Deprecated
	default void kjs$attack(DamageSource source, float hp) {
		kjs$damage(hp, source);
	}

	@Info("Measures the distance of entity to block at specified `BlockPos`.")
	default double kjs$distanceToBlock(BlockPos pos) {
		return Math.sqrt(kjs$distanceToBlockSqr(pos));
	}

	@Info("Measures the **square** of a distance of entity to the block at specified `BlockPos`.")
	default double kjs$distanceToBlockSqr(BlockPos pos) {
		return kjs$self().distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Info("Measures the distance of entity to the point at specified `x`, `y` and `z`.")
	default double kjs$distanceTo(double x, double y, double z) {
		return Math.sqrt(kjs$self().distanceToSqr(x, y, z));
	}

	@Info("Measures the distance of entity to the point at specified 3D position vector.")
	default double kjs$distanceTo(Vec3 position) {
		return Math.sqrt(kjs$self().distanceToSqr(position));
	}

	@Deprecated
	@Info("Replaced by `entity.distanceToSqr(x, y, z)`.")
	default double kjs$getDistanceSq(double x, double y, double z) {
		return kjs$self().distanceToSqr(x, y, z);
	}

	@Deprecated
	@Info("Replaced by `entity.distanceTo(x, y, z)`.")
	default double kjs$getDistance(double x, double y, double z) {
		return kjs$distanceTo(x, y, z);
	}

	@Deprecated
	@Info("Replaced by `entity.distanceToBlockSqr(pos)`.")
	default double kjs$getDistanceSq(BlockPos pos) {
		return kjs$distanceToBlockSqr(pos);
	}

	default KubeRayTraceResult kjs$rayTrace(double distance, boolean fluids) {
		var entity = kjs$self();
		var hitResult = entity.pick(distance, 0F, fluids);
		var eyePosition = entity.getEyePosition();
		var lookVector = entity.getViewVector(1.0f);
		var traceEnd = eyePosition.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);
		var bound = entity.getBoundingBox().expandTowards(lookVector.scale(distance)).inflate(1.0, 1.0, 1.0);
		var distanceSquared = hitResult.getType() != HitResult.Type.MISS ? hitResult.getLocation().distanceToSqr(eyePosition) : distance * distance;
		var entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePosition, traceEnd, bound, ent -> !ent.isSpectator() && ent.isPickable(), distanceSquared);
		if (entityHitResult != null) {
			var entityDistanceSquared = eyePosition.distanceToSqr(entityHitResult.getLocation());
			if (entityDistanceSquared < distanceSquared || hitResult.getType() == HitResult.Type.MISS) {
				hitResult = entityHitResult;
			}
		}
		return new KubeRayTraceResult(entity, hitResult, distance);
	}

	default KubeRayTraceResult kjs$rayTrace(double distance) {
		return kjs$rayTrace(distance, true);
	}

	@Nullable
	default Entity kjs$rayTraceEntity(double distance, @Nullable Predicate<Entity> filter) {
		double d0 = Double.MAX_VALUE;
		Entity entity = null;

		var start = kjs$self().getEyePosition();
		var end = start.add(kjs$self().getLookAngle().scale(distance));

		for (Entity entity1 : kjs$self().level().getEntities(kjs$self(), new AABB(start, end), filter == null ? UtilsJS.ALWAYS_TRUE : filter)) {
			double d1;
			AABB aabb = entity1.getBoundingBox();
			Optional<Vec3> optional = aabb.clip(start, end);
			if (optional.isEmpty() || !((d1 = start.distanceToSqr(optional.get())) < d0)) {
				continue;
			}
			entity = entity1;
			d0 = d1;
		}

		return entity;
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

	@Nullable
	default ItemEntity kjs$spawnAtLocation(ItemStack itemStack, Vec3 offset) {
		if (kjs$getLevel() instanceof ServerLevel serverLevel) {
			return kjs$self().spawnAtLocation(serverLevel, itemStack, offset);
		}

		return null;
	}
}
