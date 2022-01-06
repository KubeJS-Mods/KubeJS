package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.world.LevelJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * @author LatvianModder
 */
public class EntitySpawnedEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level level;

	public EntitySpawnedEventJS(Entity entity, Level level) {
		this.entity = entity;
		this.level = level;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public LevelJS getLevel() {
		return levelOf(level);
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}
}