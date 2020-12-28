package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
public class AddOreProperties
{
	public BlockState _block = Blocks.AIR.defaultBlockState();
	public boolean noSurface = false;
	public int clusterMinSize = 5;
	public int clusterMaxSize = 9;
	public int clusterMinCount = 20;
	public int clusterMaxCount = 20;
	public int chance = 0;
	public int minHeight = 0;
	public int maxHeight = 64;
	public int retrogen = 0;
	public boolean squared = true;
	public final WorldgenEntryList<String> biomes = new WorldgenEntryList<>();
	public final WorldgenEntryList<String> spawnsIn = new WorldgenEntryList<>();

	public void setBlock(String id)
	{
		_block = UtilsJS.parseBlockState(id);
	}

	public void setClusterCount(int c)
	{
		clusterMinCount = c;
		clusterMaxCount = c;
	}
}
