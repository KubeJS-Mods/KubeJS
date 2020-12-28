package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
public class AddLakeProperties
{
	public BlockState _block = Blocks.AIR.defaultBlockState();
	public int chance = 20;
	public int retrogen = 0;
	public final WorldgenEntryList<String> biomes = new WorldgenEntryList<>();

	public void setBlock(String id)
	{
		_block = UtilsJS.parseBlockState(id);
	}
}
