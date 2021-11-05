package dev.latvian.kubejs.world;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.ItemEntityJS;
import dev.latvian.kubejs.entity.ItemFrameEntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.GameRulesJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.WithAttachedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public abstract class WorldJS implements WithAttachedData {
	public final Level minecraftLevel;
	public final Level minecraftWorld; // compat

	private AttachedData data;

	public WorldJS(Level w) {
		minecraftLevel = w;
		minecraftWorld = w;
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

	public String getDimension() {
		return minecraftLevel.dimension().location().toString();
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
		} else if (e instanceof Player) {
			return getPlayerData((Player) e).getPlayer();
		} else if (e instanceof LivingEntity) {
			return new LivingEntityJS(this, (LivingEntity) e);
		} else if (e instanceof ItemEntity) {
			return new ItemEntityJS(this, (ItemEntity) e);
		} else if (e instanceof ItemFrame) {
			return new ItemFrameEntityJS(this, (ItemFrame) e);
		}

		return new EntityJS(this, e);
	}

	@Nullable
	public LivingEntityJS getLivingEntity(@Nullable Entity entity) {
		EntityJS e = getEntity(entity);
		return e instanceof LivingEntityJS ? (LivingEntityJS) e : null;
	}

	@Nullable
	public PlayerJS getPlayer(@Nullable Entity entity) {
		if (!(entity instanceof Player)) {
			return null;
		}

		EntityJS e = getEntity(entity);
		return e instanceof PlayerJS ? (PlayerJS) e : null;
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
		EntityType<?> type = Registry.ENTITY_TYPE.get(id);

		if (type == null) {
			return null;
		}

		return getEntity(type.create(minecraftLevel));
	}

	public void spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable EntityJS player) {
		if (minecraftLevel instanceof ServerLevel) {
			LightningBolt e = EntityType.LIGHTNING_BOLT.create(minecraftLevel);
			e.moveTo(x, y, z);
			e.setCause(player instanceof ServerPlayerJS ? ((ServerPlayerJS) player).minecraftPlayer : null);
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
}