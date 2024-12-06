package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockExplodedCallback {
	protected final LevelBlock block;
	protected final Explosion explosion;

	public BlockExplodedCallback(Level level, BlockPos pos, Explosion explosion) {
		this.block = level.kjs$getBlock(pos);
		this.explosion = explosion;
	}

	public Level getLevel() {
		return block.getLevel();
	}

	public LevelBlock getBlock() {
		return block;
	}

	public BlockState getBlockState() {
		return block.getBlockState();
	}

	public Explosion getExplosion() {
		return explosion;
	}

	public Entity getCause() {
		return explosion.getDirectSourceEntity();
	}

	@Nullable
	public LivingEntity getIgniter() {
		return explosion.getIndirectSourceEntity();
	}

	public float getRadius() {
		return explosion.radius();
	}

	public List<Player> getAffectedPlayers() {
		return explosion.getHitPlayers().keySet().stream().toList();
	}
}
