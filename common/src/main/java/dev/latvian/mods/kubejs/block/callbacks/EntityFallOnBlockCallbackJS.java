package dev.latvian.mods.kubejs.block.callbacks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityFallOnBlockCallbackJS extends EntityStepOnBlockCallbackJS {

	private final float fallHeight;

	public EntityFallOnBlockCallbackJS(Level level, Entity entity, BlockPos pos, BlockState state, float fallHeight) {
		super(level, entity, pos, state);
		this.fallHeight = fallHeight;
	}

	public boolean isSuppressingBounce() {
		return entity.isSuppressingBounce();
	}

	public float getFallHeight() {
		return fallHeight;
	}

	public boolean applyFallDamage() {
		return applyFallDamage(1);
	}

	public boolean applyFallDamage(float multiplier) {
		return applyFallDamage(fallHeight, multiplier);
	}

	public boolean applyFallDamage(float fallHeight, float multiplier) {
		return applyFallDamage(fallHeight, multiplier, DamageSource.FALL);
	}

	public boolean applyFallDamage(float fallHeight, float multiplier, DamageSource damageSource) {
		return entity.causeFallDamage(fallHeight, multiplier, damageSource);
	}
}
