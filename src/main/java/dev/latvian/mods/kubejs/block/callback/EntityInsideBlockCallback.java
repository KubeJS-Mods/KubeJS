package dev.latvian.mods.kubejs.block.callback;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityInsideBlockCallback extends EntityBlockCallback {

	public EntityInsideBlockCallback(Level level, Entity entity, BlockPos pos, BlockState state) {
		super(level, entity, pos, state);
	}
}
