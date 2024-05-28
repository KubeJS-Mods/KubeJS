package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;

@Info(value = """
	Invoked when an entity attempts to trample farmland.
	""")
public class FarmlandTrampledKubeEvent implements KubeEntityEvent {
	private final BlockEvent.FarmlandTrampleEvent event;
	private final BlockContainerJS block;

	public FarmlandTrampledKubeEvent(BlockEvent.FarmlandTrampleEvent event) {
		this.event = event;
		this.block = new BlockContainerJS((Level) event.getLevel(), event.getPos());
		this.block.cachedState = event.getState();
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
	public BlockContainerJS getBlock() {
		return block;
	}
}
