package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntitySpawnedEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level level;

	public EntitySpawnedEventJS(Entity entity, Level level) {
		this.entity = entity;
		this.level = level;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}
}