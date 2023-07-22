package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import dev.latvian.mods.kubejs.typings.JsParam;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@JsInfo(value = """
		Invoked when a detector block registered in KubeJS receives a block update.
				
		`Powered`/`Unpowered` event will be fired when the detector block is powered/unpowered.
		""")
public class DetectorBlockEventJS extends LevelEventJS {
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

	@JsInfo("The id of the detector block when it was registered.")
	public String getDetectorId() {
		return detectorId;
	}

	@Override
	@JsInfo("The level where the detector block is located.")
	public Level getLevel() {
		return level;
	}

	@JsInfo("If the detector block is powered.")
	public boolean isPowered() {
		return powered;
	}

	@JsInfo("The detector block.")
	public BlockContainerJS getBlock() {
		return block;
	}
}
