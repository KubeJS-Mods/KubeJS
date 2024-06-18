package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BasicCommandKubeEvent implements KubeEntityEvent {
	private final Level level;
	private final Entity entity;
	private final BlockPos pos;
	public final String id;
	public final String input;

	public BasicCommandKubeEvent(Level level, @Nullable Entity entity, BlockPos pos, String id, String input) {
		this.level = level;
		this.entity = entity;
		this.pos = pos;
		this.id = id;
		this.input = input;
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
		return this.getLevel().kjs$getBlock(pos);
	}
}