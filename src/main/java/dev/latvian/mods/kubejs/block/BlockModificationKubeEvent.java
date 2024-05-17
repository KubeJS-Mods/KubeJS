package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class BlockModificationKubeEvent implements KubeEvent {

	@Info("""
		Modifies blocks that match the given predicate.
					
		**NOTE**: tag predicates are not supported at this time.
		""")
	public void modify(BlockStatePredicate predicate, Consumer<Block> c) {
		for (var block : predicate.getBlocks()) {
			c.accept(block);
		}
	}
}
