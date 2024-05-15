package dev.latvian.mods.kubejs.block.drop;

public interface BlockDropSupplier {
	BlockDropSupplier NO_DROPS = () -> BlockDrops.EMPTY;

	BlockDrops get();
}
