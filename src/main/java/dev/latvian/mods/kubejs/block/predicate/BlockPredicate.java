package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.LevelBlock;

public interface BlockPredicate {
	boolean check(LevelBlock block);
}