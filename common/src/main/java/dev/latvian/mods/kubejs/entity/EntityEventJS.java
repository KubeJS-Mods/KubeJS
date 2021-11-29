package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.world.WorldEventJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import net.minecraft.world.entity.Entity;

/**
 * @author LatvianModder
 */
public abstract class EntityEventJS extends WorldEventJS {
	private EntityJS cachedEntity;

	public abstract EntityJS getEntity();

	@Override
	public WorldJS getWorld() {
		return getEntity().getWorld();
	}

	protected EntityJS entityOf(Entity entity) {
		if (cachedEntity == null) {
			cachedEntity = worldOf(entity).getEntity(entity);

			if (cachedEntity == null) {
				throw new IllegalStateException("Entity can't be null!");
			}
		}

		return cachedEntity;
	}
}