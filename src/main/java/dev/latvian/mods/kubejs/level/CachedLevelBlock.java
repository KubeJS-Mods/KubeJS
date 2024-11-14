package dev.latvian.mods.kubejs.level;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CachedLevelBlock implements LevelBlock {
	public final Level minecraftLevel;
	private final BlockPos pos;

	public transient BlockState cachedState;
	public transient BlockEntity cachedEntity;

	public CachedLevelBlock(Level w, BlockPos p) {
		minecraftLevel = w;
		pos = p;
	}

	@Override
	public Level getLevel() {
		return minecraftLevel;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public LevelBlock cache(BlockState state) {
		cachedState = state;
		return this;
	}

	@Override
	public LevelBlock cache(BlockEntity entity) {
		cachedEntity = entity;
		return this;
	}

	public void clearCache() {
		cachedState = null;
		cachedEntity = null;
	}

	@Override
	public BlockState getBlockState() {
		if (cachedState == null) {
			cachedState = minecraftLevel.getBlockState(getPos());
		}

		return cachedState;
	}

	@Override
	public void setBlockState(BlockState state, int flags) {
		minecraftLevel.setBlock(getPos(), state, flags);
		clearCache();
		cachedState = state;
	}

	@Override
	@Nullable
	public BlockEntity getEntity() {
		if (cachedEntity == null || cachedEntity.isRemoved()) {
			cachedEntity = minecraftLevel.getBlockEntity(pos);
		}

		return cachedEntity;
	}

	@Override
	public String toString() {
		return toBlockStateString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof CharSequence) {
			return kjs$getId().equals(obj.toString());
		} else if (obj instanceof ResourceLocation) {
			return kjs$getIdLocation().equals(obj);
		}

		return super.equals(obj);
	}
}