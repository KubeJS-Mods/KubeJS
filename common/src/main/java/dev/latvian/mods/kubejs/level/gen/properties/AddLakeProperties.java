package dev.latvian.mods.kubejs.level.gen.properties;

import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author LatvianModder
 */
public class AddLakeProperties {
	public GenerationStep.Decoration _worldgenLayer = GenerationStep.Decoration.LAKES;
	public BlockState _block = Blocks.AIR.defaultBlockState();
	public int chance = 20;
	public int retrogen = 0;
	// FIXME!
	// public final WorldgenEntryList biomes = new WorldgenEntryList();

	public void setBlock(String id) {
		_block = UtilsJS.parseBlockState(id);
	}

	public void setWorldgenLayer(String id) {
		_worldgenLayer = GenerationStep.Decoration.valueOf(id.toUpperCase());
	}
}
