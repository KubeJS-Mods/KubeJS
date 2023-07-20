package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@JsInfo("""
		Invoked when an entity is about to be added to the world.
				
		This event also fires for existing entities when they are loaded from a save.
		""")
public class EntitySpawnedEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level level;

	public EntitySpawnedEventJS(Entity entity, Level level) {
		this.entity = entity;
		this.level = level;
	}

	@Override
	@JsInfo("The level the entity is being added to.")
	public Level getLevel() {
		return level;
	}

	@Override
	@JsInfo("The entity being added to the world.")
	public Entity getEntity() {
		return entity;
	}
}