package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;

@Info(value = """
	Invoked when a block is placed.
	""")
public class BlockPlacedKubeEvent implements KubeEntityEvent {
	private final BlockEvent.EntityPlaceEvent event;

	public BlockPlacedKubeEvent(BlockEvent.EntityPlaceEvent event) {
		this.event = event;
	}

	@Override
	@Info("The level of the block that was placed.")
	public Level getLevel() {
		return (Level) event.getLevel();
	}

	@Override
	@Info("The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
	public Entity getEntity() {
		return event.getEntity();
	}

	@Info("The block that is placed.")
	public BlockContainerJS getBlock() {
		var block = new BlockContainerJS((Level) event.getLevel(), event.getPos());
		block.cachedState = event.getPlacedBlock();
		return block;
	}
}