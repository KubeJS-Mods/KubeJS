package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;

@Info(value = """
	Invoked when an entity attempts to trample farmland.
	""")
public class FarmlandTrampledKubeEvent implements KubeEntityEvent {
	private final BlockEvent.FarmlandTrampleEvent event;
	private final LevelBlock block;

	public FarmlandTrampledKubeEvent(BlockEvent.FarmlandTrampleEvent event) {
		this.event = event;
		this.block = ((Level) event.getLevel()).kjs$getBlock(event.getPos()).cache(event.getState());
	}

	@Info("The distance of the entity from the block.")
	public float getDistance() {
		return event.getFallDistance();
	}

	@Override
	@Info("The entity that is attempting to trample the farmland.")
	public Entity getEntity() {
		return event.getEntity();
	}

	@Override
	@Info("The level that the farmland and the entity are in.")
	public Level getLevel() {
		return (Level) event.getLevel();
	}

	@Info("The farmland block.")
	public LevelBlock getBlock() {
		return block;
	}
}
