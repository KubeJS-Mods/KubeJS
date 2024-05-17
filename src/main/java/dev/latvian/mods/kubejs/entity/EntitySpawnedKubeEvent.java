package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Info("""
	Invoked when an entity is about to be added to the world.
			
	This event also fires for existing entities when they are loaded from a save.
	""")
public class EntitySpawnedKubeEvent implements KubeEntityEvent {
	private final Entity entity;
	private final Level level;

	public EntitySpawnedKubeEvent(Entity entity, Level level) {
		this.entity = entity;
		this.level = level;
	}

	@Override
	@Info("The level the entity is being added to.")
	public Level getLevel() {
		return level;
	}

	@Override
	@Info("The entity being added to the world.")
	public Entity getEntity() {
		return entity;
	}
}