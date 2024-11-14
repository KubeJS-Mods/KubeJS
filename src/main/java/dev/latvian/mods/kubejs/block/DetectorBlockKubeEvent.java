package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Info(value = """
	Invoked when a detector block registered in KubeJS receives a block update.
	
	`Powered`/`Unpowered` event will be fired when the detector block is powered/unpowered.
	""")
public class DetectorBlockKubeEvent implements KubeLevelEvent {
	private final String detectorId;
	private final Level level;
	private final boolean powered;
	private final LevelBlock block;

	public DetectorBlockKubeEvent(String i, Level l, BlockPos p, boolean pow) {
		detectorId = i;
		level = l;
		powered = pow;
		block = level.kjs$getBlock(p);
	}

	@Info("The id of the detector block when it was registered.")
	public String getDetectorId() {
		return detectorId;
	}

	@Override
	@Info("The level where the detector block is located.")
	public Level getLevel() {
		return level;
	}

	@Info("If the detector block is powered.")
	public boolean isPowered() {
		return powered;
	}

	@Info("The detector block.")
	public LevelBlock getBlock() {
		return block;
	}
}
