package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockModificationEventJS extends EventJS {
	public static final EventHandler EVENT = EventHandler.startup(BlockModificationEventJS.class).legacy("block.modification");

	public void modify(BlockStatePredicate predicate, Consumer<BlockModificationProperties> c) {
		for (var block : predicate.getBlocks()) {
			c.accept(new BlockModificationProperties(block));
		}
	}
}
