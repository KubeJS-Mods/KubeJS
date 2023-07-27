package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockExplodedCallbackJS {

	protected final Level level;
	protected final BlockContainerJS block;
	protected final BlockState state;
	protected final Explosion explosion;

	public BlockExplodedCallbackJS(Level level, BlockPos pos, Explosion explosion) {
		this.level = level;
		this.block = new BlockContainerJS(level, pos);
		this.state = level.getBlockState(pos);
		this.explosion = explosion;
	}

	public Level getLevel() {
		return level;
	}

	public BlockContainerJS getBlock() {
		return block;
	}

	public BlockState getBlockState() {
		return state;
	}

	public Explosion getExplosion() {
		return explosion;
	}

	public Entity getCause() {
		return explosion.source;
	}

	@Nullable
	public LivingEntity getIgniter() {
		return explosion.getSourceMob();
	}

	public float getRadius() {
		return explosion.radius;
	}

	public DamageSource getDamageSource() {
		return explosion.getDamageSource();
	}

	public List<Player> getAffectedPlayers() {
		return explosion.getHitPlayers().keySet().stream().toList();
	}
}
