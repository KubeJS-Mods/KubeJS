package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockModificationEventJS extends EventJS {
	public void modify(BlockStatePredicate predicate, Consumer<Block> c) {
		for (var block : predicate.getBlocks()) {
			c.accept(block);
		}
	}
}
