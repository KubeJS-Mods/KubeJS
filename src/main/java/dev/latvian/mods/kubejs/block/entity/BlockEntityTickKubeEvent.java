package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.world.level.Level;

public class BlockEntityTickKubeEvent implements KubeLevelEvent {
	private final KubeBlockEntity entity;

	public BlockEntityTickKubeEvent(KubeBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public Level getLevel() {
		return entity.getLevel();
	}

	public LevelBlock getBlock() {
		return entity.getBlock();
	}

	public int getTick() {
		return entity.tick;
	}

	public int getCycle() {
		return entity.cycle;
	}
}
