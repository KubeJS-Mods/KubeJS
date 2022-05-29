package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.ItemEntityJS;
import dev.latvian.mods.kubejs.entity.ItemFrameEntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.GameRulesJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.WithAttachedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public abstract class LevelJS implements WithAttachedData {
	public final Level minecraftLevel;

	private AttachedData data;

	public LevelJS(Level w) {
		minecraftLevel = w;
	}

	public abstract ScriptType getSide();

	@Override
	public AttachedData getData() {
		if (data == null) {
			data = new AttachedData(this);
		}

		return data;
	}

	public GameRulesJS getGameRules() {
		return new GameRulesJS(minecraftLevel.getGameRules());
	}

	@Nullable
	public ServerJS getServer() {
		return null;
	}

	public long getTime() {
		return minecraftLevel.getGameTime();
	}

	public long getLocalTime() {
		return minecraftLevel.getDayTime();
	}

	public ResourceLocation getDimension() {
		return minecraftLevel.dimension().location();
	}

	public boolean isOverworld() {
		return minecraftLevel.dimension() == Level.OVERWORLD;
	}

	public boolean isDaytime() {
		return minecraftLevel.isDay();
	}

	public boolean isRaining() {
		return minecraftLevel.isRaining();
	}

	public boolean isThundering() {
		return minecraftLevel.isThundering();
	}

	public void setRainStrength(float strength) {
		minecraftLevel.setRainLevel(strength);
	}

	public BlockContainerJS getBlock(int x, int y, int z) {
		return getBlock(new BlockPos(x, y, z));
	}

	public BlockContainerJS getBlock(BlockPos pos) {
		return new BlockContainerJS(minecraftLevel, pos);
	}

	public BlockContainerJS getBlock(BlockEntity blockEntity) {
		return getBlock(blockEntity.getBlockPos());
	}

	public abstract PlayerDataJS getPlayerData(Player player);

	@Nullable
	public EntityJS getEntity(@Nullable Entity e) {
		if (e == null) {
			return null;
		} else if (e instanceof Player player) {
			return getPlayerData(player).getPlayer();
		} else if (e instanceof LivingEntity living) {
			return new LivingEntityJS(this, living);
		} else if (e instanceof ItemEntity item) {
			return new ItemEntityJS(this, item);
		} else if (e instanceof ItemFrame frame) {
			return new ItemFrameEntityJS(this, frame);
		}

		return new EntityJS(this, e);
	}

	@Nullable
	public LivingEntityJS getLivingEntity(@Nullable Entity entity) {
		var e = getEntity(entity);
		return e instanceof LivingEntityJS living ? living : null;
	}

	@Nullable
	public PlayerJS getPlayer(@Nullable Entity entity) {
		if (!(entity instanceof Player)) {
			return null;
		}

		var e = getEntity(entity);
		return e instanceof PlayerJS player ? player : null;
	}

	public EntityArrayList createEntityList(Collection<? extends Entity> entities) {
		return new EntityArrayList(this, entities);
	}

	public EntityArrayList getPlayers() {
		return createEntityList(minecraftLevel.players());
	}

	public EntityArrayList getEntities() {
		return new EntityArrayList(this, 0);
	}

	public ExplosionJS createExplosion(double x, double y, double z) {
		return new ExplosionJS(minecraftLevel, x, y, z);
	}

	@Nullable
	public EntityJS createEntity(ResourceLocation id) {
		var type = Registry.ENTITY_TYPE.get(id);

		if (type == null) {
			return null;
		}

		return getEntity(type.create(minecraftLevel));
	}

	public void spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable EntityJS player) {
		if (minecraftLevel instanceof ServerLevel) {
			var e = EntityType.LIGHTNING_BOLT.create(minecraftLevel);
			e.moveTo(x, y, z);
			e.setCause(player instanceof ServerPlayerJS serverPlayer ? serverPlayer.minecraftPlayer : null);
			e.setVisualOnly(effectOnly);
			minecraftLevel.addFreshEntity(e);
		}
	}

	public void spawnLightning(double x, double y, double z, boolean effectOnly) {
		spawnLightning(x, y, z, effectOnly, null);
	}

	public void spawnFireworks(double x, double y, double z, FireworksJS f) {
		minecraftLevel.addFreshEntity(f.createFireworkRocket(minecraftLevel, x, y, z));
	}

	public EntityArrayList getEntitiesWithin(AABB aabb) {
		return new EntityArrayList(this, minecraftLevel.getEntities(null, aabb));
	}

	@ApiStatus.OverrideOnly
	public Level getMinecraftLevel() {
		return minecraftLevel;
	}
}