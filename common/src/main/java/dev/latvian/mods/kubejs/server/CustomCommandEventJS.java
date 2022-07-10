package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class CustomCommandEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.server(CustomCommandEventJS.class).cancelable().legacy("server.custom_command");

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
	public LevelJS getLevel() {
		return levelOf(level);
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return this.getLevel().getBlock(blockPos);
	}
}