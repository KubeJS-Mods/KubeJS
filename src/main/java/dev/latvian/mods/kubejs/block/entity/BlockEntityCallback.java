package dev.latvian.mods.kubejs.block.entity;

@FunctionalInterface
public interface BlockEntityCallback {
	void accept(BlockEntityJS entity);
}
