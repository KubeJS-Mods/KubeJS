package dev.latvian.kubejs.world;

import dev.latvian.kubejs.docs.MinecraftClass;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public abstract class WorldJS implements WithAttachedData {
	@MinecraftClass
	public Level minecraftWorld;

	private AttachedData data;

	public WorldJS(Level w) {
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
		return new GameRulesJS(minecraftWorld.getGameRules());
	}

	@Nullable
	public ServerJS getServer() {
		return null;
	}

	public long getTime() {
		return minecraftWorld.getGameTime();
	}

	public long getLocalTime() {
		return minecraftWorld.getDayTime();
	}

	public String getDimension() {
		return minecraftWorld.dimension().location().toString();
	}

	public boolean isOverworld() {
		return minecraftWorld.dimension() == Level.OVERWORLD;
	}

	public boolean isDaytime() {
		return minecraftWorld.isDay();
	}

	public boolean isRaining() {
		return minecraftWorld.isRaining();
	}

	public boolean isThundering() {
		return minecraftWorld.isThundering();
	}

	public void setRainStrength(float strength) {
		minecraftWorld.setRainLevel(strength);
	}

	public BlockContainerJS getBlock(int x, int y, int z) {
		return getBlock(new BlockPos(x, y, z));
	}

	public BlockContainerJS getBlock(BlockPos pos) {
		return new BlockContainerJS(minecraftWorld, pos);
	}

	public BlockContainerJS getBlock(BlockEntity blockEntity) {
		return getBlock(blockEntity.getBlockPos());
	}

	public abstract PlayerDataJS getPlayerData(Player player);

	/**
     * get Entity
	 * @param e Entity
     * @return EntityJS
     */
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

	/**
     * get living Entity
     * @return LivingEntityJS
     */
	@Nullable
	public LivingEntityJS getLivingEntity(@Nullable Entity entity) {
		EntityJS e = getEntity(entity);
		return e instanceof LivingEntityJS ? (LivingEntityJS) e : null;
	}

	/**
     * get Player
     * @return PlayerJS
     */
	@Nullable
	public PlayerJS getPlayer(@Nullable Entity entity) {
		if (!(entity instanceof Player)) {
			return null;
		}

		EntityJS e = getEntity(entity);
		return e instanceof PlayerJS ? (PlayerJS) e : null;
	}

	//TODO
	public EntityArrayList createEntityList(Collection<? extends Entity> entities) {
		return new EntityArrayList(this, entities);
	}

	/**
     * get Players
     * @return EntityArrayList
     */
	public EntityArrayList getPlayers() {
		return createEntityList(minecraftWorld.players());
	}

	/**
     * get Entities
     * @return EntityArrayList
     */
	public EntityArrayList getEntities() {
		return new EntityArrayList(this, 0);
	}

	/**
     * create Explosion
     * @param x float
     * @param y float
     * @param z float
     * @return ExplosionJS
     */
	public ExplosionJS createExplosion(double x, double y, double z) {
		return new ExplosionJS(minecraftWorld, x, y, z);
	}

	/**
     * create Entity
     * @param id ResourceLocation
     * @return EntityJS
     */
	@Nullable
	public EntityJS createEntity(ResourceLocation id) {
		EntityType<?> type = Registry.ENTITY_TYPE.get(id);

		if (type == null) {
			return null;
		}

		return getEntity(type.create(minecraftWorld));
	}

	/**
     * spawn Lightning
     * @param x float
     * @param y float
     * @param z float
     * @param effectOnly bool
	 * @param player EntityJS (default = null)
     */
	public void spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable EntityJS player) {
		if (minecraftWorld instanceof ServerLevel) {
			LightningBolt e = EntityType.LIGHTNING_BOLT.create(minecraftWorld);
			e.moveTo(x, y, z);
			e.setCause(player instanceof ServerPlayerJS ? ((ServerPlayerJS) player).minecraftPlayer : null);
			minecraftWorld.addFreshEntity(e);
		}
	}

	/**
     * spawn Lightning
     * @param x float
     * @param y float
     * @param z float
     * @param effectOnly bool
     */
	public void spawnLightning(double x, double y, double z, boolean effectOnly) {
		spawnLightning(x, y, z, effectOnly, null);
	}

	/**
     * spawn Fireworks
     * @param x float
     * @param y float
     * @param z float
     * @param f Fireworks
     */
	public void spawnFireworks(double x, double y, double z, FireworksJS f) {
		minecraftWorld.addFreshEntity(f.createFireworkRocket(minecraftWorld, x, y, z));
	}
}