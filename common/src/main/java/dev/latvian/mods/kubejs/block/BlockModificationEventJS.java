package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.event.EventJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockModificationEventJS extends EventJS {
	public void modify(BlockStatePredicate predicate, Consumer<BlockModificationProperties> c) {
		for (var block : predicate.getBlocks()) {
			if (block instanceof BlockKJS kjs) {
				c.accept(new BlockModificationProperties(kjs));
			}
		}
	}
}
