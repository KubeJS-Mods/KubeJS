package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DetectorBlockEventJS extends LevelEventJS {
	public static final EventHandler EVENT = EventHandler.server(DetectorBlockEventJS.class).name("detectorBlock").legacy("block.detector");
	public static final EventHandler POWERED_EVENT = EventHandler.server(DetectorBlockEventJS.class).name("detectorBlockPowered").legacy("block.detector.powered");
	public static final EventHandler UNPOWERED_EVENT = EventHandler.server(DetectorBlockEventJS.class).name("detectorBlockUnpowered").legacy("block.detector.unpowered");

	private final String detectorId;
	private final Level level;
	private final BlockPos pos;
	private final boolean powered;
	private final BlockContainerJS block;

	public DetectorBlockEventJS(String i, Level l, BlockPos p, boolean pow) {
		detectorId = i;
		level = l;
		pos = p;
		powered = pow;
		block = new BlockContainerJS(level, pos);
	}

	public String getDetectorId() {
		return detectorId;
	}

	@Override
	public LevelJS getLevel() {
		return levelOf(level);
	}

	public boolean isPowered() {
		return powered;
	}

	public BlockContainerJS getBlock() {
		return block;
	}
}
