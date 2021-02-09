package dev.latvian.kubejs.block;

import dev.latvian.kubejs.core.BlockKJS;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockModificationEventJS extends EventJS
{
	public void modify(BlockStatePredicate predicate, Consumer<BlockModificationProperties> c)
	{
		for (Block block : predicate.getBlocks())
		{
			if (block instanceof BlockKJS)
			{
				c.accept(new BlockModificationProperties((BlockKJS) block));
			}
		}
	}
}
