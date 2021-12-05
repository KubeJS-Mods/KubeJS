package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.kubejs.world.WorldEventJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DetectorBlockEventJS extends WorldEventJS {
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
	public WorldJS getWorld() {
		return levelOf(level);
	}

	public boolean isPowered() {
		return powered;
	}

	public BlockContainerJS getBlock() {
		return block;
	}
}
