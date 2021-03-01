package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * @author LatvianModder
 */
public class EntitySpawnedEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level world;

	public EntitySpawnedEventJS(Entity entity, Level world) {
		this.entity = entity;
		this.world = world;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf(world);
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}
}