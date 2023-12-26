package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CustomCommandEventJS extends EntityEventJS {
	private final Level level;
	private final Entity entity;
	private final BlockPos blockPos;
	private final String id;

	public CustomCommandEventJS(Level l, @Nullable Entity e, BlockPos p, String i) {
		level = l;
		entity = e;
		blockPos = p;
		id = i;
	}

	public String getId() {
		return id;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	public BlockContainerJS getBlock() {
		return this.getLevel().kjs$getBlock(blockPos);
	}
}